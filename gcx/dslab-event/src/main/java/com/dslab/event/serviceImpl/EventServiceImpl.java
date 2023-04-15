package com.dslab.event.serviceImpl;

import com.dslab.commonapi.entity.*;
import com.dslab.commonapi.services.EventService;
import com.dslab.commonapi.vo.Result;
import com.dslab.event.mapper.EventMapper;
import com.dslab.event.mapper.UserEventRelationMapper;
import com.dslab.event.mapper.UserMapper;
import com.dslab.event.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: dslab-event
 * @description: 日程相关服务的实现
 * @author: 郭晨旭
 * @create: 2023-04-05 18:35
 * @version: 1.0
 **/

@Service
public class EventServiceImpl implements EventService {
    private static Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    @Resource
    EventMapper eventMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    UserEventRelationMapper userEventRelationMapper;

    /**
     * todo 待测试
     * 验证用户身份是否可以添加/修改此日程
     * 课程考试类日程只能由管理员操作, 其他日程只能由学生操作
     *
     * @param userType  用户类型
     * @param eventType 日程类型
     * @return 符合条件返回true, 否则返回false
     */
    public boolean identifyUser(String userType, String eventType) {
        if (UserType.USER_ADMIN.getValue().equals(userType)
                && !(EventType.EVENT_LESSON.getValue().equals(eventType)
                || EventType.EVENT_EXAM.getValue().equals(eventType))) {
            return false;
        } else if (UserType.USER_STUDENT.getValue().equals(userType)
                && (EventType.EVENT_LESSON.getValue().equals(eventType)
                || EventType.EVENT_EXAM.getValue().equals(eventType))) {
            return false;
        }
        return true;
    }

    /**
     * 添加日程
     *
     * @param event 待添加的日程
     * @param user  用户信息
     * @return 返回添加信息
     */
    @Override
    @Transactional
    public Result<?> addEvent(Event event, User user) {
        if (!TimeUtils.checkTimeValid(event)) {
            // 校验日程时间是否合法
            return Result.<String>error("日程时间不合法").data("日程添加失败");
        } else if (!this.identifyUser(user.getType(), event.getEventType())) {
            //校验用户是否有操作权限
            return Result.<String>error("用户没有添加权限").data("日程添加失败");
        }

        Result<?> result;
        try {
            // 判断用户类别, 如果是admin则和他同组的所有用户都将会有该日程
            if (UserType.USER_ADMIN.getValue().equals(user.getType())) {
                result = this.addEventByAdmin(event, user);
            } else if (UserType.USER_STUDENT.getValue().equals(user.getType())) {
                result = this.addEventByStudent(event, user);
            } else {
                result = Result.<String>error("用户类型不明确").data("请求失败");
            }
        } catch (Exception e) {
            logger.warn("添加日程时出现错误");
            return Result.<String>error("数据不合法, 出现未知错误").data("请求失败");
        }
        return result;
    }

    /**
     * todo 待测试
     * 删除日程
     *
     * @return 是否删除成功
     */
    @Override
    public Result<String> deleteByEventId(Event event, User user) {
        if (!this.identifyUser(user.getType(), event.getEventType())) {
            //校验用户是否有操作权限
            return Result.<String>error("用户没有修改权限").data("日程修改失败");
        }

        int change = eventMapper.deleteByEventId(event.getEventId());
        if (change == 1) {
            return Result.<String>success("删除成功").data("请求成功");
        } else {
            return Result.<String>error("删除失败").data("请求失败");
        }
    }

    /**
     * todo 待测试
     * 修改日程
     *
     * @param event 待添加的日程
     * @param user  用户信息
     * @return 返回修改信息
     */
    @Override
    @Transactional
    public Result<?> updateEvent(Event event, User user) {
        if (!TimeUtils.checkTimeValid(event)) {
            // 校验日程时间是否合法
            return Result.<String>error("日程时间不合法").data("日程修改失败");
        } else if (!this.identifyUser(user.getType(), event.getEventType())) {
            //校验用户是否有操作权限
            return Result.<String>error("用户没有修改权限").data("日程修改失败");
        }

        // 先把该日程删除, 以免检测冲突的时候出错
        eventMapper.deleteByEventId(event.getEventId());

        // 闹钟不会和其他日程产生冲突, 可以直接修改
        if (EventType.EVENT_CLOCK.getValue().equals(event.getEventType())) {
            eventMapper.update(event);
            return Result.<String>success("修改成功").data("请求成功");
        }

        // 判断是否有冲突
        Object result = checkConflict(event, user);
        if (result instanceof Result<?>) {
            // 添加失败, 直接返回失败的信息
            return (Result<?>) result;
        } else if (result instanceof Boolean && !(Boolean) result) {
            // 没有冲突, 可以添加
            eventMapper.update(event);
            return Result.<String>success("添加成功").data("请求成功");
        } else {
            return Result.<String>error("未知错误").data("请求失败");
        }
    }

    /**
     * 管理员添加日程
     *
     * @return 成功返回success, 失败返回error
     */
    public Result<?> addEventByAdmin(Event event, User user) {
        Object result = checkConflict(event, user);
        if (result instanceof Result<?>) {
            // 添加失败, 直接返回失败的信息
            return (Result<?>) result;
        } else if (result instanceof Boolean && !(Boolean) result) {
            // 给组内每个学生添加该日程
            List<User> students = userMapper.getByGroupId(user.getGroupId());
            eventMapper.add(event);
            // 可以获取到日程的id
            event = eventMapper.getByEventName(event.getName());
            for (User u : students) {
                userEventRelationMapper.add(u.getGroupId(), u.getUserId(), event.getEventId());
            }
            return Result.<String>success("添加成功").data("请求成功");
        } else {
            return Result.<String>error("未知错误").data("请求失败");
        }
    }

    /**
     * 学生添加日程 (只能添加除了课程和考试以外的日程)
     *
     * @return 成功返回success, 失败返回error
     */
    public Result<?> addEventByStudent(Event event, User user) {
        // 闹钟不会和其他日程产生冲突, 可以直接添加
        if (EventType.EVENT_CLOCK.getValue().equals(event.getEventType())) {
            eventMapper.add(event);
            event = eventMapper.getByEventName(event.getName());
            userEventRelationMapper.add(user.getGroupId(), user.getUserId(), event.getEventId());
            return Result.<String>success("添加成功").data("请求成功");
        }

        // 判断是否有冲突
        Object result = checkConflict(event, user);
        if (result instanceof Result<?>) {
            // 添加失败, 直接返回失败的信息
            return (Result<?>) result;
        } else if (result instanceof Boolean && !(Boolean) result) {
            // 没有冲突, 可以添加
            eventMapper.add(event);
            event = eventMapper.getByEventName(event.getName());
            userEventRelationMapper.add(user.getGroupId(), user.getUserId(), event.getEventId());
            return Result.<String>success("添加成功").data("请求成功");
        } else {
            return Result.<String>error("未知错误").data("请求失败");
        }
    }

    /**
     * 判断该用户是否有冲突的日程
     *
     * @return 如果无法添加则返回 result, 否则返回false表示没有冲突可以添加
     */
    public Object checkConflict(Event event, User user) {
        // todo 此处可以优化
        // 选出该用户的所有日程
        List<Integer> eventIds = userEventRelationMapper.selectByUserId(user.getUserId());
        List<Event> events = new ArrayList<>();
        for (Integer id : eventIds) {
            Event e = eventMapper.getByEventId(id);
            if (TimeUtils.IsInOneDay(e, event)) {
                events.add(e);
            }
        }

        // 根据日程类型不同进行不同的冲突判断
        if (EventType.EVENT_LESSON.getValue().equals(event.getEventType())
                || EventType.EVENT_EXAM.getValue().equals(event.getEventType())) {
            return checkLessonExamConflict(event, events);
        } else if (EventType.EVENT_ACTIVITY.getValue().equals(event.getEventType())) {
            return checkActivityConflict(event, events);
        } else if (EventType.EVENT_TEMPORARY.getValue().equals(event.getEventType())) {
            return checkTemporaryConflict(event, events);
        } else {
            logger.warn("日程类型出现错误");
            return Result.<String>error("日程类型出现错误").data("请求失败");
        }
    }

    /**
     * 判断该用户是否有冲突的日程 (课程考试类)
     *
     * @param event  待添加的日程
     * @param events 该用户当天的所有日程
     * @return 如果有冲突则直接返回添加失败;
     * 若没有则返回false
     */
    public Object checkLessonExamConflict(Event event, List<Event> events) {
        for (Event e : events) {
            if (TimeUtils.compareTime(event, e)) {
                return Result.<String>error("时间冲突, 无法添加").data("请求失败");
            }
        }
        return false;
    }

    /**
     * 判断该用户是否有冲突的日程 (活动类)
     *
     * @param event  待添加的日程
     * @param events 该用户当天的所有日程
     * @return 如果没有冲突则直接返回false;
     * 如果有冲突则给出三个可行性时间 (6 - 22);
     * 若没有则个人活动提示添加失败, 集体活动给出冲突最小的三个时间
     */
    public Object checkActivityConflict(Event event, List<Event> events) {
        // 查看是否有冲突
        boolean ok = true;
        // time[i] = 0 表示 [i, i+1) 内时间空闲
        int[] time = new int[24];
        for (Event e : events) {
            if (TimeUtils.compareTime(e, event)) {
                ok = false;
            }
            int start = TimeUtils.TimestampToHour(e.getStartTime());
            int end = TimeUtils.TimestampToHour(e.getEndTime());
            for (int i = start; i < end; ++i) {
                if (time[i] == 0) {
                    // 标记当前时间已被占用
                    time[i] = time[i] + Integer.parseInt(e.getEventType()) + 1;
                }
            }
        }

        if (ok) {
            // 检测发现没有时间冲突
            return false;
        } else {
            // 查找出合理的替代时间
            List<int[]> replace = new ArrayList<>();
            for (int i = 6; i <= 22 && replace.size() < 3; ++i) {
                if (time[i] == 0) {
                    // 当前时间空闲
                    replace.add(new int[]{i, i + 1});
                }
            }

            Result<?> result = Result.error("时间冲突, 以下是可选时间").data(replace);

            // 如果没有可以代替的时间
            if (replace.size() == 0) {
                if (MemberType.MEMBER_PERSONAL.getValue().equals(event.getMemberType())) {
                    // 如果是个人活动则直接返回错误
                    result = Result.error("时间冲突, 添加失败");
                } else if (MemberType.MEMBER_GROUP.getValue().equals(event.getMemberType())) {
                    // todo 活动持续时间固定为一小时的话, 所有日程均以整点开始结束, 那"冲突最少"似乎就没有意义
                    // todo 待定 查找可用时间的时候，最小冲突时间可以检测每个已有的课程，选取冲突最远的三个
                    // ?如果是集体活动则选择三个冲突最小的时间 (或许是可以和临时事务冲突)
                    for (int i = 6; i <= 22 && replace.size() < 3; ++i) {
                        if (time[i] == 4) {
                            // 当前时间为临时事务类型
                            replace.add(new int[]{i, i + 1});
                        }
                    }
                    // todo 待修改，查找最远冲突的日程，可能需要再遍历一遍list，应该可以优化
                    if (replace.size() == 0) {
                        // todo 可代替时间小于三个
                        replace.add(new int[]{6, 7});
                        replace.add(new int[]{7, 8});
                        replace.add(new int[]{8, 9});
                    }
                    result = Result.error("时间冲突, 以下是冲突最小的时间").data(replace);
                } else {
                    return Result.<String>error("时间冲突, 活动类型不明确, 无法给出可代替时间").data("请求失败");
                }
            }

            // 有可代替时间则直接返回
            return result;
        }
    }

    /**
     * 判断该用户是否有冲突的日程 (临时事务类)
     *
     * @param event  待添加的日程
     * @param events 该用户当天的所有日程
     * @return 如果有冲突则直接返回添加失败;
     * 若没有则返回false
     */
    public Object checkTemporaryConflict(Event event, List<Event> events) {
        for (Event e : events) {
            if (TimeUtils.compareTime(event, e)) {
                return Result.<String>error("时间冲突, 无法添加").data("请求失败");
            }
        }
        return false;
    }
}
