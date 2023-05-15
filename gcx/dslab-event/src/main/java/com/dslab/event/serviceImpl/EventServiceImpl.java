package com.dslab.event.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.dslab.commonapi.dataStruct.AVLTree;
import com.dslab.commonapi.dataStruct.AVLTreeImpl;
import com.dslab.commonapi.entity.*;
import com.dslab.commonapi.services.EventService;
import com.dslab.commonapi.utils.MathUtil;
import com.dslab.commonapi.utils.TimeUtil;
import com.dslab.commonapi.vo.Result;
import com.dslab.event.mapper.EventMapper;
import com.dslab.event.mapper.UserEventRelationMapper;
import com.dslab.event.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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
     * 根据用户id进行排序的树
     */
    private AVLTree<User> userIdTree = new AVLTreeImpl<>(Comparator.comparingInt(User::getUserId));
    /**
     * 根据用户id和群组id排序的列表
     * 一个群组有哪些用户
     */
    private List<User> userGroupIdList = new ArrayList<>();
    /**
     * 根据日程id进行排序的树
     */
    private AVLTree<Event> eventIdTree = new AVLTreeImpl<>(Comparator.comparingInt(Event::getEventId));
    /**
     * 根据日程名称进行排序的树
     */
    private AVLTree<Event> eventNameTree = new AVLTreeImpl<>(Comparator.comparing(Event::getName));
    /**
     * 根据用户id和日程id排序的列表
     * 一个用户有哪些日程
     */
    private List<UserEventRelation> userEventIdList = new ArrayList<>();

    /**
     * 预加载函数
     */
    @PostConstruct
    public void init() {
        List<User> users = userMapper.getAllUsers();
        for (User u : users) {
            userIdTree.insert(u);
            userGroupIdList.add(u);
        }
        MathUtil.mySort(userGroupIdList, Comparator.comparingInt(User::getGroupId));

        List<Event> events = eventMapper.getAllEvents();
        for (Event e : events) {
            eventIdTree.insert(e);
            eventNameTree.insert(e);
        }
        eventIdTree.preOrder();

        List<UserEventRelation> userEventRelations = userEventRelationMapper.getAll();
        userEventIdList.addAll(userEventRelations);
        MathUtil.mySort(userEventIdList, Comparator.comparingInt(UserEventRelation::getUserId));
        logger.info("init success!");
    }

    /**
     * 验证用户身份是否可以添加/修改此日程
     * 课程考试类日程只能由管理员操作, 其他日程只能由学生操作
     *
     * @param userType  用户类型
     * @param eventType 日程类型
     * @return 符合条件返回true, 否则返回false
     */
    private boolean identifyUser(String userType, String eventType) {
        if (EventType.EVENT_LESSON.getValue().equals(eventType)
                || EventType.EVENT_EXAM.getValue().equals(eventType)) {
            return UserType.USER_ADMIN.getValue().equals(userType);
        } else {
            return UserType.USER_STUDENT.getValue().equals(userType);
        }
    }

    /**
     * 添加日程
     *
     * @param event 待添加的日程
     * @param user  用户信息
     * @return 返回添加信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addEvent(Event event, User user) {
        try {
            if (!TimeUtil.checkTimeValid(event)) {
                // 校验日程时间是否合法
                return Result.error("日程时间不合法");
            }
            if (!this.identifyUser(user.getType(), event.getEventType())) {
                //校验用户是否有操作权限
                return Result.error("用户没有添加权限");
            }
            if (eventMapper.getByEventName(event.getName()) != null) {
                // 不能添加同名日程
                return Result.error("不能添加同名日程");
            }
        } catch (Exception e) {
            // 不能添加同名日程
            return Result.error("数据不合法, 校验出错");
        }

        Result<?> result;
        try {
            // 判断用户类别, 如果是admin则和他同组的所有用户都将会有该日程
            if (UserType.USER_ADMIN.getValue().equals(user.getType())) {
                result = this.addEventByAdmin(event, user);
            } else if (UserType.USER_STUDENT.getValue().equals(user.getType())) {
                result = this.addEventByStudent(event, user);
            } else {
                result = Result.error("用户类型不明确");
            }
        } catch (Exception e) {
            logger.warn("添加日程时出现错误");
            return Result.error("数据不合法, 出现未知错误");
        }
        return result;
    }

    /**
     * 删除日程
     *
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteByEventId(Event event, User user) {
        if (!this.identifyUser(user.getType(), event.getEventType())) {
            //校验用户是否有操作权限
            return Result.error("删除失败, 用户没有修改权限");
        }

        int change = eventMapper.deleteByEventId(event.getEventId());
        eventIdTree.remove(new Event(event.getEventId()));
        eventNameTree.remove(new Event(event.getName()));
        if (change == 1) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    /**
     * 修改日程
     *
     * @param event 待添加的日程
     * @param user  用户信息
     * @return 返回修改信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateEvent(Event event, User user) {
        if (!TimeUtil.checkTimeValid(event)) {
            // 校验日程时间是否合法
            return Result.error("日程时间不合法");
        }
        if (!this.identifyUser(user.getType(), event.getEventType())) {
            //校验用户是否有操作权限
            return Result.error("用户没有修改权限");
        }

        // 先把该日程删除, 以免检测冲突的时候出错
        eventMapper.deleteByEventId(event.getEventId());

        // 闹钟不会和其他日程产生冲突, 可以直接修改
        if (EventType.EVENT_CLOCK.getValue().equals(event.getEventType())) {
            eventMapper.update(event);
            eventIdTree.remove(event);
            eventIdTree.insert(event);
            eventNameTree.remove(event);
            eventNameTree.insert(event);
            return Result.success("修改成功");
        }

        // 判断是否有冲突
        boolean result = checkConflict(event, user);
        if (result) {
            // 修改失败
            if (EventType.EVENT_ACTIVITY.getValue().equals(event.getEventType())) {
                // 如果是活动类日程则需要找出三个可添加的时间段
                return findTime(event, user);
            }
            eventMapper.restartByEventId(event.getEventId());
            return Result.error("添加失败");
        } else {
            // 没有冲突, 可以直接修改
            eventMapper.update(event);
            eventIdTree.remove(event);
            eventIdTree.insert(event);
            eventNameTree.remove(event);
            eventNameTree.insert(event);
            return Result.success("修改成功");
        }
    }

    /**
     * 根据日程id获取日程
     *
     * @param eventId 日程id
     * @return 日程信息
     */
    @Override
    public Result<Event> getByEventId(Integer eventId) {
        Event event = eventIdTree.search(new Event(eventId)).getKey();
        if (event != null) {
            return Result.<Event>success("查找成功").data(event);
        } else {
            return Result.<Event>error("查找失败");
        }
    }

    /**
     * 根据日程名称获取日程
     *
     * @param eventName 日程名称
     * @return 日程信息
     */
    @Override
    public Result<Event> getByEventName(String eventName) {
        Event event = eventIdTree.search(new Event(eventName)).getKey();
        if (event != null) {
            return Result.<Event>success("查找成功").data(event);
        } else {
            return Result.error("查找失败");
        }
    }

    /**
     * todo
     * 获取用户在某个时间的课程
     *
     * @param nowTime 传入的时间
     * @param userId  用户id
     * @return 用户满足要求的日程
     */
    @Override
    public Result<String> checkUserEventInTime(Date nowTime, String userId) {
        long nowDay = TimeUtil.dateToDay(nowTime);
        int nowHour = TimeUtil.dateToHour(nowTime);

        String res;
        if (nowHour < 23) {
            // 如果当前时间小于23点, 则是判断查询下一个小时的日程
            res = checkNextHourEvent(nowDay, nowHour, Integer.valueOf(userId));
        } else {
            // 否则是查询第二天的日程
            res = checkNextDayEvent(nowDay, Integer.valueOf(userId));
        }
        return Result.<String>success("查询成功").data(res);
    }

    /**
     * todo
     * 检查用户下一个小时的日程
     *
     * @param nowDay  当前天
     * @param nowHour 当前小时
     * @param userId  用户id
     * @return JSON化字符串
     */
    private String checkNextHourEvent(long nowDay, int nowHour, Integer userId) {
        return "";
    }

    /**
     * 检查用户第二天的日程
     *
     * @param nowDay 当前天
     * @param userId 用户id
     * @return JSON化字符串
     */
    private String checkNextDayEvent(long nowDay, Integer userId) {
        // 选出该用户的所有日程id
        List<Integer> eventIds = new ArrayList<>();
        int low = MathUtil.mySearch(userEventIdList, new User(userId), Comparator.comparingInt(User::getUserId));
        int high = MathUtil.mySearch(userEventIdList, new User(userId), Comparator.comparingInt(User::getUserId));
        for (int i = low; i < high; ++i) {
            eventIds.add(userEventIdList.get(i).getEventId());
        }

        // 根据用户的日程id找到对应日程, 并判断其是否是第二天的课程
        List<Event> events = new ArrayList<>();
        for (Integer id : eventIds) {
            Event e = eventIdTree.search(new Event(id)).getKey();
            if (e != null && TimeUtil.IsInOneDay(nowDay + 1, e)) {
                events.add(e);
            }
        }
        return JSON.toJSONString(events);
    }

    /**
     * 管理员添加日程
     *
     * @return 成功返回success, 失败返回error
     */
    private Result<?> addEventByAdmin(Event event, User user) {
        boolean result = checkConflict(event, user);
        if (result) {
            // 添加失败
            return Result.error("添加失败");
        } else {
            // 给组内每个学生添加该日程
            // 选出同一个组的用户
            int low = MathUtil.mySearch(userGroupIdList, new User(user.getGroupId()), Comparator.comparingInt(User::getGroupId));
            int high = MathUtil.mySearch(userGroupIdList, new User(user.getGroupId() + 1), Comparator.comparingInt(User::getGroupId));
            List<User> users = new ArrayList<>();
            for (int i = low; i < high; ++i) {
                int id = userGroupIdList.get(i).getUserId();
                users.add(userIdTree.search(new User(id)).getKey());
            }

            // 添加课程
            eventMapper.add(event);
            // 可以获取到日程的id
            event = eventMapper.getByEventName(event.getName());
            eventIdTree.insert(event);
            eventNameTree.insert(event);

            // 添加课程与用户映射关系
            for (User u : users) {
                userEventRelationMapper.add(u.getGroupId(), u.getUserId(), event.getEventId());
                userEventIdList.add(new UserEventRelation(u.getGroupId(), u.getUserId(), event.getEventId()));
            }
            MathUtil.mySort(userEventIdList, Comparator.comparingInt(UserEventRelation::getUserId));

            return Result.success("添加成功");
        }
    }

    /**
     * 学生添加日程 (只能添加除了课程和考试以外的日程)
     *
     * @return 成功返回success, 失败返回error
     */
    private Result<?> addEventByStudent(Event event, User user) {
        System.out.println(event);
        System.out.println(user);
        // 闹钟不会和其他日程产生冲突, 可以直接添加
        if (EventType.EVENT_CLOCK.getValue().equals(event.getEventType())) {
            return addSuccess(event, user);
        }

        // 判断是否有冲突
        boolean result = checkConflict(event, user);
        if (result) {
            // 添加失败
            if (EventType.EVENT_ACTIVITY.getValue().equals(event.getEventType())) {
                // 如果是活动类日程则需要找出三个可添加的时间段
                return findTime(event, user);
            }
            return Result.error("添加失败");
        } else {
            // 没有冲突, 可以添加
            return addSuccess(event, user);
        }
    }

    /**
     * 学生用户成功添加日程
     *
     * @return 返回信息
     */
    private Result<?> addSuccess(Event event, User user) {
        System.out.println(event);
        System.out.println(user);
        // 添加日程
        eventMapper.add(event);
        event = eventMapper.getByEventName(event.getName());
        eventIdTree.insert(event);
        eventNameTree.insert(event);

        // 添加日程和用户的映射关系
        userEventRelationMapper.add(user.getGroupId(), user.getUserId(), event.getEventId());
        userEventIdList.add(new UserEventRelation(user.getGroupId(), user.getUserId(), event.getEventId()));
        MathUtil.mySort(userEventIdList, Comparator.comparingInt(UserEventRelation::getUserId));
        return Result.success("添加成功");
    }

    /**
     * 判断该用户是否有冲突的日程
     *
     * @return 如果有冲突返回true, 否则返回false
     */
    private boolean checkConflict(Event event, User user) {
        // 选出该用户的所有日程id
        // todo 抽象出来
        List<Integer> eventIds = new ArrayList<>();
        UserEventRelation userEventRelation = new UserEventRelation();
        userEventRelation.setUserId(user.getUserId());
        int low = MathUtil.mySearch(userEventIdList, userEventRelation, Comparator.comparingInt(UserEventRelation::getUserId));
        System.out.println(low);
        userEventRelation.setUserId(user.getUserId() + 1);
        int high = MathUtil.mySearch(userEventIdList, userEventRelation, Comparator.comparingInt(UserEventRelation::getUserId));
        System.out.println(low + "  " + high);
        for (int i = low; i < high; ++i) {
            eventIds.add(userEventIdList.get(i).getEventId());
        }
        // 根据用户的日程id找到对应日程, 并判断是否有冲突
        List<Event> events = new ArrayList<>();
        for (Integer id : eventIds) {
            Event e = eventIdTree.search(new Event(id)).getKey();
            if (e != null && TimeUtil.IsInOneDay(e, event) && TimeUtil.compareTime(event, e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * todo 未完成
     * 活动类日程, 寻找没有冲突的时间段
     *
     * @param event 待添加日程
     * @param user  用户
     * @return 添加结果
     */
    private Result<?> findTime(Event event, User user) {
//        // time[i] = 0 表示 [i, i+1) 内时间空闲
//        int[] time = new int[24];
//
//        int start = TimeUtils.TimestampToHour(e.getStartTime());
//        int end = TimeUtils.TimestampToHour(e.getEndTime());
//        for (int i = start; i < end; ++i) {
//            if (time[i] == 0) {
//                // 标记当前时间已被占用
//                time[i] = time[i] + Integer.parseInt(e.getEventType()) + 1;
//            }
//        }
//        if (ok) {
//            // 检测发现没有时间冲突
//            return false;
//        } else {
//            // 查找出合理的替代时间
//            List<int[]> replace = new ArrayList<>();
//            for (int i = 6; i <= 22 && replace.size() < 3; ++i) {
//                if (time[i] == 0) {
//                    // 当前时间空闲
//                    replace.add(new int[]{i, i + 1});
//                }
//            }
//
//            Result<?> result = Result.error("时间冲突, 以下是可选时间").data(replace);
//
//            // 如果没有可以代替的时间
//            if (replace.size() == 0) {
//                if (MemberType.MEMBER_PERSONAL.getValue().equals(event.getMemberType())) {
//                    // 如果是个人活动则直接返回错误
//                    result = Result.error("时间冲突, 添加失败");
//                } else if (MemberType.MEMBER_GROUP.getValue().equals(event.getMemberType())) {
//                    // todo 活动持续时间固定为一小时的话, 所有日程均以整点开始结束, 那"冲突最少"似乎就没有意义
//                    // todo 待定 查找可用时间的时候，最小冲突时间可以检测每个已有的课程，选取冲突最远的三个
//                    // ?如果是集体活动则选择三个冲突最小的时间 (或许是可以和临时事务冲突)
//                    for (int i = 6; i <= 22 && replace.size() < 3; ++i) {
//                        if (time[i] == 4) {
//                            // 当前时间为临时事务类型
//                            replace.add(new int[]{i, i + 1});
//                        }
//                    }
//                    // todo 待修改，查找最远冲突的日程，可能需要再遍历一遍list，应该可以优化
//                    if (replace.size() == 0) {
//                        // todo 可代替时间小于三个
//                        replace.add(new int[]{6, 7});
//                        replace.add(new int[]{7, 8});
//                        replace.add(new int[]{8, 9});
//                    }
//                    result = Result.error("时间冲突, 以下是冲突最小的时间").data(replace);
//                } else {
//                    return Result.<String>error("时间冲突, 活动类型不明确, 无法给出可代替时间").data("请求失败");
//                }
//            }
//
//            // 有可代替时间则直接返回
//            return result;
        return null;
    }
}
