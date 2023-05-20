package com.dslab.event.serviceImpl;

import com.dslab.commonapi.dataStruct.AVLTree;
import com.dslab.commonapi.dataStruct.AVLTreeImpl;
import com.dslab.commonapi.dataStruct.SegTree;
import com.dslab.commonapi.dataStruct.SegTreeImpl;
import com.dslab.commonapi.entity.Event;
import com.dslab.commonapi.entity.EventType;
import com.dslab.commonapi.entity.User;
import com.dslab.commonapi.entity.UserEventRelation;
import com.dslab.commonapi.services.EventService;
import com.dslab.commonapi.utils.MathUtil;
import com.dslab.commonapi.utils.TimeUtil;
import com.dslab.commonapi.vo.Result;
import com.dslab.event.mapper.EventMapper;
import com.dslab.event.mapper.UserEventRelationMapper;
import com.dslab.event.mapper.UserMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: dslab-event
 * @description: 日程相关服务的实现
 * @author: 郭晨旭
 * @create: 2023-04-05 18:35
 * @version: 1.0
 **/

@Service
public class EventServiceImpl implements EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    @Resource
    EventMapper eventMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    UserEventRelationMapper userEventRelationMapper;

    /**
     * 根据用户id进行排序的树
     */
    private final AVLTree<User> userIdTree = new AVLTreeImpl<>(Comparator.comparingInt(User::getUserId));
    /**
     * 根据用户id和群组id排序的列表
     * 一个群组有哪些用户
     */
    private final List<User> userGroupIdList = new ArrayList<>();
    /**
     * 根据日程id进行排序的树
     */
    private final AVLTree<Event> eventIdTree = new AVLTreeImpl<>(Comparator.comparingInt(Event::getEventId));
    /**
     * 根据日程名称进行排序的树
     */
    private final AVLTree<Event> eventNameTree = new AVLTreeImpl<>(Comparator.comparing(Event::getName));
    /**
     * 根据用户id和日程id排序的列表
     * 一个用户有哪些日程
     */
    private final List<UserEventRelation> userEventIdList = new ArrayList<>();
    /**
     * 每个用户一棵树, 记录其所有日程的时间
     */
    private final Map<Integer, SegTree> timeTree = new ConcurrentHashMap<>();

    /**
     * 预加载函数
     */
    @PostConstruct
    public void init() {
        List<User> users = userMapper.getAllUsers();
        for (User u : users) {
            userIdTree.insert(u);
            userGroupIdList.add(u);
            List<Event> eventList = new ArrayList<>();
            List<Integer> eventIds = userEventRelationMapper.getByUserId(u.getUserId());
            if (eventIds != null) {
                for (int id : eventIds) {
                    Event e = eventMapper.getByEventId(id);
                    if (e != null) {
                        eventList.add(e);
                    }
                }
            }
            SegTree segmentTree = new SegTreeImpl(eventList);
            timeTree.put(u.getUserId(), segmentTree);
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
     * 删除日程
     *
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteEventById(Event event, User user) {
        if (this.isInvalidUser(user, event)) {
            //校验用户是否有操作权限
            return Result.error("删除失败, 用户没有修改权限");
        }

        int change = eventMapper.deleteByEventId(event.getEventId());
        eventIdTree.remove(new Event(event.getEventId()));
        eventNameTree.remove(new Event(event.getName()));

        return change == 1?
                Result.success("删除成功"):
                Result.error("删除失败");
    }


    /**
     * 根据日程id获取日程
     *
     * @param eventId 日程id
     * @return 日程信息
     */
    @Override
    public Result<Event> getEventById(Integer eventId) {
        Event event = eventIdTree.search(new Event(eventId)).getKey();
        return event==null?
                Result.error("查找失败"):
                Result.<Event>success("查找成功").data(event);
    }

    /**
     * 根据日程名称获取日程
     *
     * @param eventName 日程名称
     * @return 日程信息
     */
    @Override
    public Result<Event> getEventByName(String eventName) {
        Event event = eventNameTree.search(new Event(eventName)).getKey();
        //TODO:原本写的eventIdTree.search，感觉不太对，换成这个了
        return event==null?
                Result.error("查找失败"):
                Result.<Event>success("查找成功").data(event);
    }

    /**
     * 获取用户给定日期的所有日程
     *
     * @param userId 用户id
     * @param nowDay   时间
     * @return 日程列表
     */
    @Override
    public Result<List<Event>> getEventsByDay(Integer userId, long nowDay) {
        List<Event> events = getUserAllEvent(userId);

        // 根据用户的日程id找到对应日程, 并判断其是否是第二天的课程
        List<Event> res = new ArrayList<>();
        for (Event e : events) {
            if (e != null && TimeUtil.isInOneDay(nowDay, e)) {
                res.add(e);
            }
        }

        return Result.<List<Event>>success("查询成功").data(res);
    }
    /**
     * 验证用户身份是否可以添加/修改此日程
     * 集体类只能由管理员添加, 个人类只能由学生添加
     *
     * @param u 用户
     * @param e 日程
     * @return 符合条件返回true, 否则返回false
     */
    private boolean isInvalidUser(User u, Event e) {
        if (u.isAdmin()) {
            return e.getIsGroup();
        }
        return !e.getIsGroup();
    }

    /**========================================update=========================================================*/

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
        if (this.isInvalidUser(user, event)) {
            //校验用户是否有操作权限
            return Result.error("用户没有修改权限");
        }

        // 先把该日程删除, 以免检测冲突的时候出错
        eventMapper.deleteByEventId(event.getEventId());

        // 闹钟不会和其他日程产生冲突, 可以直接修改
        if (EventType.EVENT_CLOCK.getValue().equals(event.getEventType())) {
            updateSuccess(event);
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
            updateSuccess(event);
            return Result.success("修改成功");
        }
    }

    /**
     * 执行修改日程的操作
     *
     * @param event 日程
     */
    private void updateSuccess(Event event) {
        eventMapper.update(event);
        eventIdTree.remove(event);
        eventIdTree.insert(event);
        eventNameTree.remove(event);
        eventNameTree.insert(event);
    }

    /**========================================add=========================================================*/

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
            if (this.isInvalidUser(user, event)) {
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

        boolean addRes=false;
        try {
            // 判断用户类别, 如果是admin则和他同组的所有用户都将会有该日程
            if (user.isAdmin()) {
                addRes = this.addEventByAdmin(event, user);
            } else if (user.isStudent()) {
                addRes = this.addEventByStudent(event, user);
            }
        } catch (Exception e) {
            logger.warn("添加日程时出现错误");
            return Result.error("数据不合法, 出现未知错误");
        }

        return addRes?
                Result.success("添加成功"):
                Result.error("添加失败");
    }

    /**
     * 管理员添加日程
     *
     * @return 成功返回success, 失败返回error
     */
    private boolean addEventByAdmin(Event event, User user) {
        boolean result = checkConflict(event, user);
        if (result) {
            // 添加失败
            if (EventType.EVENT_ACTIVITY.getValue().equals(event.getEventType())) {
                // 如果是活动类日程则需要找出三个可添加的时间段
                //return findTime(event, user);
                //TODO
            }
            return false;
        } else {
            // 给组内每个学生添加该日程
            // 选出同一个组的用户
            User tempUser = new User();
            tempUser.setGroupId(user.getGroupId());
            int low = MathUtil.lowerBound(userGroupIdList, tempUser, Comparator.comparingInt(User::getGroupId));
            tempUser.setGroupId(user.getGroupId() + 1);
            int high = MathUtil.lowerBound(userGroupIdList, tempUser, Comparator.comparingInt(User::getGroupId));
            high = (high == -1 ? userGroupIdList.size() : high);
            List<User> users = new ArrayList<>();
            for (int i = low; i < high; ++i) {
                int id = userGroupIdList.get(i).getUserId();
                users.add(userIdTree.search(new User(id)).getKey());
            }

            // 添加课程
            event = saveEvent(event);

            // 添加课程与用户映射关系
            for (User u : users) {
                addRelation(u, event);
                addToTimeTree(u, event);
            }
            MathUtil.mySort(userEventIdList, Comparator.comparingInt(UserEventRelation::getUserId));

            return true;
        }
    }

    /**
     * 学生添加日程 (只能添加除了课程和考试以外的日程)
     *
     * @return 成功返回success, 失败返回error
     */
    private boolean addEventByStudent(Event event, User user) {
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
                //return findTime(event, user);
                //TODO:写完findtime再说
            }
            return false;
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
    private boolean addSuccess(Event event, User user) {
        event = saveEvent(event);
        addToTimeTree(user, event);

        // 添加日程和用户的映射关系
        addRelation(user, event);
        MathUtil.mySort(userEventIdList, Comparator.comparingInt(UserEventRelation::getUserId));
        return true;
    }

    /**
     * 把日程保存到相关的树和表中
     *
     * @param event 日程
     * @return 带有id的日程
     */
    private Event saveEvent(Event event) {
        eventMapper.add(event);
        event = eventMapper.getByEventName(event.getName());
        eventIdTree.insert(event);
        eventNameTree.insert(event);
        return event;
    }

    /**
     * 向用户的线段树里面添加日程
     *
     * @param user  用户
     * @param event 日程
     */
    private void addToTimeTree(User user, Event event) {
        SegTree tree = timeTree.getOrDefault(user.getUserId(), new SegTreeImpl(new ArrayList<>()));
        tree.addEvent(event);
        timeTree.put(user.getUserId(), tree);
    }

    /**
     * 添加用户和日程映射关系
     *
     * @param user  用户
     * @param event 日程
     */
    private void addRelation(User user, Event event) {
        //TODO:验收如果问为什么写一遍存数据库，还要写一遍存内存数据结构，就说是他要求的。如果允许用数据库查询的话直接入库，然后用的时候直接select就可以了
        userEventRelationMapper.add(user.getGroupId(), user.getUserId(), event.getEventId());
        userEventIdList.add(new UserEventRelation(user.getGroupId(), user.getUserId(), event.getEventId()));
    }

    /**========================================send notify func=========================================================*/

    /**
     * 获取用户在某个时间的课程
     *
     * @param nowTime 传入的时间
     * @param userId  用户id
     * @return 用户满足要求的日程
     */
    @Override
    public Result<List<Event>> checkUserEventInTime(Date nowTime, String userId) {
        long nowDay = TimeUtil.dateToDay(nowTime);
        int nowHour = TimeUtil.dateToHour(nowTime);

        List<Event> res;
        if (nowHour < 23) {
            // 如果当前时间小于23点, 则是判断查询下一个小时的日程
            res=timeTree.get(Integer.parseInt(userId)).rangeQuery(nowHour*60, (nowHour+1)*60).stream()
                    .map(a->eventIdTree.search(new Event(a)).getKey()).toList();
        } else {
            // 否则是查询第二天的日程
            res = getEventsByDay(Integer.valueOf(userId),nowDay + 1).getData();
        }

        //TODO:是所有的事件都要发提醒吗？那闹钟的区别在哪呢。还是说只需要给标了闹钟的事件和一整天晚上检测第二天的那些才提醒？或者说闹钟不是之前说的事件的一个属性，而就是单独的事务？

        return res.isEmpty()?
                Result.error("无需要提醒的事件"):
                Result.<List<Event>>success("需要提醒事件共"+res.size()+"个").data(res);
    }


    /**========================================other private func=========================================================*/


    /**
     * 判断该用户是否有冲突的日程
     *
     * @return 如果有冲突返回true, 否则返回false
     */
    private boolean checkConflict(Event event, User user) {
        // 选出该用户的所有日程id
        List<Event> events = getUserAllEvent(user.getUserId());

        // 根据用户的日程id找到对应日程, 并判断是否有冲突
        for (Event e : events) {
            if (e != null && TimeUtil.isInOneDay(e, event) && TimeUtil.compareTime(event, e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取某用户的所有日程
     *
     * @param userId 用户id
     * @return 日程id列表
     */
    //TODO：给前端提供的接口里面没有这样获取整个课程表的吗？应该有的吧，或者分开一下课程、活动、事务
    //或者刚看了一下，写的是提供get by day的接口，然后前端一天一天获取？好像也行。我要睡了，再说吧
    private List<Event> getUserAllEvent(Integer userId) {
        List<Event> events = new ArrayList<>();
        UserEventRelation userEventRelation = new UserEventRelation(userId);
        //TODO:我觉得这里这个lowerbound传入list和relation对象的方式很奇怪，应该有可以优化
        int low = MathUtil.lowerBound(userEventIdList, userEventRelation, Comparator.comparingInt(UserEventRelation::getUserId));
        userEventRelation.setUserId(userId + 1);
        int high = MathUtil.lowerBound(userEventIdList, userEventRelation, Comparator.comparingInt(UserEventRelation::getUserId));
        high = (high == -1 ? userEventIdList.size() : high);

        userEventIdList.subList(low,high).stream()
                .mapToInt(UserEventRelation::getEventId)
                .forEach(a->events.add(eventIdTree.search(new Event(a)).getKey()));

        return events;
    }

    /**
     * todo 未完成
     * 活动类日程, 寻找没有冲突的时间段
     * 集体活动至少找出三个和集体内成员冲突最少的三个时间段
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
