package com.dslab.event.serviceImpl;

import com.dslab.commonapi.dataStruct.MyHashMap;
import com.dslab.commonapi.dataStruct.SegTree;
import com.dslab.commonapi.dataStruct.SegTreeImpl;
import com.dslab.commonapi.entity.Event;
import com.dslab.commonapi.entity.User;
import com.dslab.commonapi.services.EventService;
import com.dslab.commonapi.services.UserService;
import com.dslab.commonapi.utils.MathUtil;
import com.dslab.commonapi.utils.TimeUtil;
import com.dslab.commonapi.vo.Result;
import com.dslab.event.mapper.EventMapper;
import com.dslab.event.mapper.UserEventRelationMapper;
import com.dslab.event.mapper.UserMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @program: dslab-event
 * @description: 日程相关服务的实现
 * @author: 郭晨旭
 * @create: 2023-04-05 18:35
 * @version: 1.0
 **/

@Service
@DubboService(group = "DSlab", interfaceClass = EventService.class)
public class EventServiceImpl implements EventService {
    private static Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    @Resource
    EventMapper eventMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    UserEventRelationMapper userEventRelationMapper;

    @Resource
    UserService userService;

    /**
     * 每个用户一棵树, 记录其所有日程的时间
     */
    private static Map<Integer, SegTree> timeMap = new MyHashMap<>();
    /**
     * 根据用户id获取对应日程
     * 一个用户有哪些日程
     */
    private static Map<Integer, List<Integer>> userEventRelationMap = new MyHashMap<>();
    /**
     * 以日程id为键
     */
    private static Map<Integer, Event> eventIdMap = new MyHashMap<>();
    /**
     * 以日程名字为键
     */
    private static Map<String, Event> eventNameMap = new MyHashMap<>();
    /**
     * 活动冲突时, 需要选取的可用时间的数量
     */
    private static final int FREE_TIME_CNT = 3;

    /**
     * 预加载函数
     */
    @PostConstruct
    public void init() {
        List<User> users = userMapper.getAllUsers();
        for (User u : users) {
            List<Event> eventList = new ArrayList<>();
            List<Integer> ids = new ArrayList<>();
            List<Integer> eventIds = userEventRelationMapper.getByUserId(u.getUserId());
            if (eventIds != null) {
                for (int id : eventIds) {
                    Event e = eventMapper.getByEventId(id);
                    if (e != null) {
                        eventList.add(e);
                        ids.add(e.getEventId());
                    }
                }
            }
            // 加载用户日程
            userEventRelationMap.put(u.getUserId(), ids);
            // 加载用户的线段树
            SegTree segmentTree = new SegTreeImpl(eventList);
            timeMap.put(u.getUserId(), segmentTree);
        }

        // 加载所有日程
        List<Event> events = eventMapper.getAllEvents();
        if (events != null) {
            for (Event e : events) {
                eventIdMap.put(e.getEventId(), e);
                eventNameMap.put(e.getName(), e);
            }
        }

        logger.info("-----event service init success!-----");
    }

//===============================================增加日程==============================================

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
        if (!TimeUtil.checkTimeValid(event)) {
            // 校验日程时间是否合法
            logger.warn("日程时间不合法");
            return Result.error("日程时间不合法");
        }
        if (!userService.identifyUser(user, event)) {
            //校验用户是否有操作权限
            logger.warn("用户没有添加权限");
            return Result.error("用户没有添加权限");
        }
        if (eventNameMap.get(event.getName()) != null) {
            // 不能添加同名日程
            logger.warn("不能添加同名日程");
            return Result.error("不能添加同名日程");
        }

        Result<?> result;
        try {
            // 判断用户类别, 如果是admin则和他同组的所有用户都将会有该日程
            if (user.isAdmin()) {
                result = this.addEventByAdmin(event, user);
            } else {
                result = this.addEventByStudent(event, user);
            }
        } catch (Exception e) {
            logger.warn("添加日程时出现错误");
            return Result.error("数据不合法, 出现未知错误");
        }
        return result;
    }


    /**
     * 管理员添加日程
     *
     * @return 成功返回success, 失败返回error
     */
    private Result<?> addEventByAdmin(Event event, User user) {
        boolean result = adminCheckConflict(event, user);
        if (result) {
            // 添加失败
            if (event.isActivity()) {
                // 如果是活动类日程则需要找出三个可添加的时间段
                return findTime(event, user);
            }
            logger.warn("时间冲突, 添加失败");
            return Result.error("时间冲突, 添加失败");
        } else {
            // 给组内每个学生添加该日程
            // 选出同一个组的用户
            List<User> users = userService.selectSameGroupUsers(user);

            // 添加课程
            event = saveEvent(event);
            // 添加课程与用户映射关系
            for (User u : users) {
                addSuccess(event, u);
            }
            logger.info("添加成功");
            return Result.success("添加成功").data(event);
        }
    }

    /**
     * 学生添加日程 (只能添加除了课程和考试以外的日程)
     *
     * @return 成功返回success, 失败返回error
     */
    private Result<?> addEventByStudent(Event event, User user) {
        // 闹钟不会和其他日程产生冲突, 可以直接添加
        if (event.isClock()) {
            event = saveEvent(event);
            return addSuccess(event, user);
        }

        // 判断是否有冲突
        boolean result = studentCheckConflict(event, user);
        if (result) {
            // 添加失败
            if (event.isActivity()) {
                // 如果是活动类日程则需要找出三个可添加的时间段
                logger.warn("时间冲突, 返回可用时间段");
                return findTime(event, user);
            }
            logger.warn("时间冲突, 添加失败");
            return Result.error("时间冲突, 添加失败");
        } else {
            // 没有冲突, 可以添加
            event = saveEvent(event);
            return addSuccess(event, user);
        }
    }

    /**
     * 把日程保存到相关的map和表中
     *
     * @param event 日程
     * @return 带有id的日程
     */
    private Event saveEvent(Event event) {
        eventMapper.add(event);
        event = eventMapper.getByEventName(event.getName());
        eventIdMap.put(event.getEventId(), event);
        eventNameMap.put(event.getName(), event);
        return event;
    }

    /**
     * 用户成功添加日程
     *
     * @return 返回信息
     */
    private Result<?> addSuccess(Event event, User user) {
        // 添加线段树
        addToTimeTree(user, event);
        // 添加日程和用户的映射关系
        addRelation(user, event);
        logger.info("添加成功");
        return Result.success("添加成功").data(event);
    }

    /**
     * 向用户的线段树里面添加日程
     *
     * @param user  用户
     * @param event 日程
     */
    private void addToTimeTree(User user, Event event) {
        SegTree tree = timeMap.getOrDefault(user.getUserId(), new SegTreeImpl(new ArrayList<>()));
        tree.addEvent(event);
        timeMap.put(user.getUserId(), tree);
    }

    /**
     * 添加用户和日程映射关系
     *
     * @param user  用户
     * @param event 日程
     */
    private void addRelation(User user, Event event) {
        userEventRelationMapper.add(user.getGroupId(), user.getUserId(), event.getEventId());
        List<Integer> list = userEventRelationMap.getOrDefault(user.getUserId(), new ArrayList<>());
        list.add(event.getEventId());
        userEventRelationMap.put(user.getUserId(), list);
        System.out.println(list);
    }

//===============================================修改日程==============================================

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
            logger.warn("日程时间不合法");
            return Result.error("日程时间不合法");
        }
        if (!userService.identifyUser(user, event)) {
            //校验用户是否有操作权限
            logger.warn("用户没有修改权限");
            return Result.error("用户没有修改权限");
        }
        if (eventNameMap.get(event.getName()) != null
                && !(eventNameMap.get(event.getName())
                .getEventId().equals(event.getEventId()))) {
            // 不能添加同名日程
            logger.warn("不能添加同名日程");
            return Result.error("不能添加同名日程");
        }

        // 旧日程
        Event oldEvent = eventIdMap.get(event.getEventId());

        Result<?> result;
        try {
            // 判断用户类别, 如果是admin则和他同组的所有用户都将会有该日程
            if (user.isAdmin()) {
                result = this.updateEventByAdmin(user, oldEvent, event);
            } else {
                result = this.updateEventByStudent(user, oldEvent, event);
            }
        } catch (Exception e) {
            logger.warn("修改日程时出现错误");
            return Result.error("数据不合法, 出现未知错误");
        }
        return result;
    }

    /**
     * 管理员修改日程
     *
     * @param user 用户信息
     * @param src  原日程
     * @param dest 目标日程
     * @return 修改信息
     */
    private Result<?> updateEventByAdmin(User user, Event src, Event dest) {
        boolean result = adminCheckConflict(dest, user);
        if (result) {
            // 修改
            if (dest.isActivity()) {
                // 如果是活动类日程则需要找出三个可添加的时间段
                return findTime(dest, user);
            }
            logger.warn("时间冲突, 修改失败");
            return Result.error("时间冲突, 修改失败");
        } else {
            // 给组内每个学生修改该日程
            // 选出同一个组的用户
            List<User> users = userService.selectSameGroupUsers(user);
            eventMapper.update(dest);
            // 修改课程与用户映射关系
            for (User u : users) {
                updateSuccess(u.getUserId(), src, dest);
            }
            logger.info("修改成功");
            return Result.success("修改成功").data(dest);
        }
    }

    /**
     * 学生修改日程
     *
     * @param user 用户信息
     * @param src  原日程
     * @param dest 目标日程
     * @return 修改信息
     */
    private Result<?> updateEventByStudent(User user, Event src, Event dest) {
        // 闹钟不会和其他日程产生冲突, 可以直接修改
        if (dest.isClock()) {
            eventMapper.update(dest);
            return updateSuccess(user.getUserId(), src, dest);
        }

        // 判断是否有冲突
        boolean result = studentCheckConflict(dest, user);
        if (result) {
            // 修改失败
            if (dest.isActivity()) {
                // 如果是活动类日程则需要找出三个可添加的时间段
                return findTime(dest, user);
            }
            logger.warn("修改失败");
            return Result.error("修改失败");
        } else {
            // 没有冲突, 可以修改
            eventMapper.update(dest);
            return updateSuccess(user.getUserId(), src, dest);
        }
    }

    /**
     * 执行修改日程的操作, 将旧日程改为新日程
     *
     * @param userId 用户id
     * @param dest   新日程
     * @param src    旧日程
     */
    private Result<?> updateSuccess(Integer userId, Event src, Event dest) {
        eventIdMap.put(src.getEventId(), dest);
        eventNameMap.remove(src.getName());
        eventNameMap.put(dest.getName(), dest);
        SegTree segTree = timeMap.get(userId);
        segTree.modifyEvent(src, dest);
        logger.info("修改成功");
        return Result.success("修改成功").data(dest);
    }

//============================================判断冲突和查找可用时间==============================================

    /**
     * 管理员查询冲突, 要查询和该管理员同组的所有用户是否有冲突
     *
     * @return 如果有冲突返回true, 否则返回false
     */
    private boolean adminCheckConflict(Event event, User user) {
        List<User> users = userService.selectSameGroupUsers(user);
        for (User u : users) {
            if (studentCheckConflict(event, u)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断该用户是否有冲突的日程
     *
     * @return 如果有冲突返回true, 否则返回false
     */
    private boolean studentCheckConflict(Event event, User user) {
        // 选取用户在这个时间段内的所有可能冲突的日程
        List<Event> events = checkConflictEvents(event, user);
        return events.size() != 0;
    }

    /**
     * 查询用户给定时间内可能冲突的日程
     *
     * @param event 日程
     * @param user  用户
     * @return 日程信息, 没有则返回空列表
     */
    private List<Event> checkConflictEvents(Event event, User user) {
        List<Event> res = new ArrayList<>();
        // 查询用户在时间段内的日程
        SegTree segTree = timeMap.get(user.getUserId());
        List<Integer> eventIds = segTree.queryEvent(event.getStartTime(), event.getEndTime());
        for (Integer id : eventIds) {
            Event e = eventIdMap.get(id);
            // 判断两个日程是否会在同一天发生
            if (e != null && !e.isClock()
                    && !(e.isTemporary() && event.isTemporary())
                    && TimeUtil.isInOneDay(e, event)) {
                res.add(e);
            }
        }
        return res;
    }

    /**
     * 活动类日程, 寻找没有冲突的时间段
     * 集体活动至少找出三个和集体内成员冲突最少的三个时间段
     *
     * @param event 待添加日程
     * @param user  用户
     * @return 添加结果
     */
    private Result<?> findTime(Event event, User user) {
        List<int[]> freeTime;
        if (user.isAdmin()) {
            freeTime = findTimeByAdmin(event, user);
        } else {
            freeTime = findTimeByStudent(event, user);
        }
        if (freeTime.size() == 0) {
            logger.warn("时间段冲突, 找不到可用时间");
            return Result.error("时间段冲突, 找不到可用时间");
        } else {
            logger.warn("时间段冲突, 找到可用时间: " + freeTime);
            return Result.error("时间段冲突, 找到可用时间").data(freeTime);
        }
    }

    /**
     * 管理员寻找活动和可替代时间
     *
     * @param event 日程信息
     * @param user  用户信息
     * @return 可用时间列表(以小时记录)
     */
    private List<int[]> findTimeByAdmin(Event event, User user) {
        List<User> users = userService.selectSameGroupUsers(user);
        // 最终的结果
        List<int[]> freeTime = new ArrayList<>();
        // 同组用户的可用时间段
        Map<int[], Integer> time = new MyHashMap<>();
        for (User u : users) {
            List<int[]> studentTime = findTimeByStudent(event, u);
            for (int[] t : studentTime) {
                time.put(t, time.getOrDefault(t, 0) + 1);
                if (freeTime.size() < FREE_TIME_CNT) {
                    freeTime.add(t);
                } else {
                    MathUtil.mySort(freeTime, Comparator.comparingInt(time::get));
                    if (time.get(freeTime.get(0)) < time.get(t)) {
                        freeTime.set(0, t);
                    }
                }
            }
        }
        return freeTime;
    }

    /**
     * 学生寻找活动和可替代时间
     *
     * @param event 日程信息
     * @param user  用户信息
     * @return 可用时间列表(以小时记录)
     */
    private List<int[]> findTimeByStudent(Event event, User user) {
        SegTree segTree = timeMap.get(user.getUserId());
        // 可用时间, 以小时计算
        List<int[]> freeTime = new ArrayList<>();
        for (int i = 6 * 60; i <= 22 * 60; i += 60) {
            // 遍历6-22小时, 查询可用时间
            List<Integer> ids = segTree.rangeQuery(i, i + 59);
            if (ids.size() == 0) {
                freeTime.add(new int[]{i / 60, i / 60 + 1});
            } else {
                // 当前时间段有日程, 查询这个日程是否会和待添加在同一天发生
                boolean flag = true;
                for (int id : ids) {
                    Event e;
                    if ((e = eventIdMap.get(id)) != null
                            && !e.isClock()
                            && TimeUtil.isInOneDay(e, event)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    freeTime.add(new int[]{i / 60, i / 60 + 1});
                }
            }
        }
        return freeTime;
    }

//================================================删除日程==============================================

    /**
     * 删除日程
     * todo 删除有问题, 数据库没有修改(两个数据库)
     *
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteByEventId(Event event, User user) {
        if (!userService.identifyUser(user, event)) {
            //校验用户是否有操作权限
            logger.warn("删除失败, 用户没有修改权限");
            return Result.error("删除失败, 用户没有修改权限");
        }

        int change = eventMapper.deleteByEventId(event.getEventId());
        if (change == 1) {
            eventIdMap.remove(event.getEventId());
            eventNameMap.remove(event.getName());
            if (user.isAdmin()) {
                List<User> users = userService.selectSameGroupUsers(user);
                for (User u : users) {
                    SegTree segTree = timeMap.get(u.getUserId());
                    segTree.deleteEvent(event);
                }
            } else {
                SegTree segTree = timeMap.get(user.getUserId());
                segTree.deleteEvent(event);
            }
            logger.info("删除成功");
            return Result.success("删除成功");
        } else {
            logger.warn("删除失败");
            return Result.error("删除失败");
        }
    }

//===============================================查询日程==============================================

    /**
     * 根据日程id获取日程
     *
     * @param eventId 日程id
     * @return 日程信息
     */
    @Override
    public Event getByEventId(Integer eventId) {
        Event event = eventIdMap.get(eventId);
        if (event != null) {
            logger.info("查询成功 " + event);
            return event;
        } else {
            logger.warn("查询失败 " + eventId);
            return null;
        }
    }

    /**
     * 根据日程名称获取日程
     *
     * @param eventName 日程名称
     * @return 日程信息
     */
    @Override
    public List<Event> getByEventName(String eventName) {
        Event event = eventNameMap.get(eventName);
        List<Event> res = new ArrayList<>();
        if (event != null) {
            logger.info("查询成功 " + event);
            res.add(event);
        } else {
            logger.warn("查询失败 " + eventName);
        }
        return res;
    }

    /**
     * 获取用户给定日期的所有日程
     *
     * @param userId 用户id
     * @param date   时间
     * @return 日程列表
     */
    @Override
    public List<Event> getDayEvents(Integer userId, Date date) throws CloneNotSupportedException {
        long nowDay = TimeUtil.dateToDay(date);
        List<Event> res = selectSameDayEvents(nowDay, userId);
        res = TimeUtil.adjustDate(res, date);
        logger.info(userId + "查询日程成功 " + date + " " + res);
        return res;
    }

    /**
     * 获取用户给定日期的所有课程和考试日程
     *
     * @param userId 用户id
     * @param date   时间
     * @return 日程列表
     */
    @Override
    public List<Event> getLessonAndExam(Integer userId, Date date) throws CloneNotSupportedException {
        long nowDay = TimeUtil.dateToDay(date);
        List<Event> events = selectSameDayEvents(nowDay, userId);
        List<Event> res = new ArrayList<>();
        for (Event e : events) {
            if (e.isLesson() || e.isExam()) {
                res.add(e);
            }
        }
        res = TimeUtil.adjustDate(res, date);
        MathUtil.mySort(res, Comparator.comparing(Event::getStartTime));
        logger.info(userId + "查询课程考试成功 " + date + " " + res);
        return res;
    }

    /**
     * 获取用户给定日期的所有课程和考试日程
     *
     * @param userId 用户id
     * @param date   时间
     * @return 日程列表
     */
    @Override
    public List<Event> getWeekLessonAndExam(Integer userId, Date date) throws CloneNotSupportedException {
        List<Event> res = new ArrayList<>();
        for (long i = 0; i < 6; ++i) {
            Date d = TimeUtil.addDate(date, i);
            List<Event> week = getLessonAndExam(userId, d);
            res.addAll(week);
        }
        return res;
    }

    /**
     * 获取用户给定日期的所有集体活动
     *
     * @param userId 用户id
     * @param date   时间
     * @return 日程列表
     */
    @Override
    public List<Event> getGroupActivities(Integer userId, Date date) throws CloneNotSupportedException {
        long nowDay = TimeUtil.dateToDay(date);
        List<Event> events = selectSameDayEvents(nowDay, userId);
        List<Event> res = new ArrayList<>();
        for (Event e : events) {
            if (e.isActivity() && e.getIsGroup()) {
                res.add(e);
            }
        }
        res = TimeUtil.adjustDate(res, date);
        MathUtil.mySort(res, Comparator.comparing(Event::getStartTime));
        logger.info(userId + "查询集体活动成功 " + date + " " + res);
        return res;
    }

    /**
     * 获取用户给定日期的所有个人日程
     *
     * @param userId 用户id
     * @param date   时间
     * @return 日程列表
     */
    @Override
    public List<Event> getPersonalEvents(Integer userId, Date date) throws CloneNotSupportedException {
        long nowDay = TimeUtil.dateToDay(date);
        List<Event> events = selectSameDayEvents(nowDay, userId);
        List<Event> res = new ArrayList<>();
        for (Event e : events) {
            if (!e.getIsGroup()) {
                res.add(e);
            }
        }
        res = TimeUtil.adjustDate(res, date);
        MathUtil.mySort(res, Comparator.comparing(Event::getStartTime));
        logger.info(userId + "查询个人活动成功 " + date + " " + res);
        return res;
    }

    /**
     * 获取用户给定日期和类型的活动或者临时事务
     *
     * @param userId 用户id
     * @param date   时间
     * @param type   类型
     * @return 日程列表
     */
    @Override
    public List<Event> getByTypeAndDate(Integer userId, Date date, String type) throws CloneNotSupportedException {
        long nowDay = TimeUtil.dateToDay(date);
        List<Event> events = selectSameDayEvents(nowDay, userId);
        List<Event> res = new ArrayList<>();
        for (Event e : events) {
            String t = e.getCustomType();
            if (e.isLesson() || e.isExam()) {
                continue;
            } else if ("".equals(type)) {
                res.add(e);
            } else if (type.equals(t)) {
                res.add(e);
            }
        }
        res = TimeUtil.adjustDate(res, date);
        MathUtil.mySort(res, Comparator.comparing(Event::getStartTime));
        logger.info(userId + "查询成功 " + date + " " + res);
        return res;
    }

    /**
     * 获取用户在某个时间的课程
     *
     * @param nowTime 传入的时间
     * @param userId  用户id
     * @return 用户满足要求的日程
     */
    @Override
    public List<Event> checkUserEventInTime(Date nowTime, String userId) throws CloneNotSupportedException {
        long nowDay = TimeUtil.dateToDay(nowTime);
        int nowHour = TimeUtil.dateToHour(nowTime);
        int nowMin = TimeUtil.dateToMin(nowTime);

        List<Event> res = new ArrayList<>();
        if (nowHour < 23) {
            // 如果当前时间小于23点, 则是判断查询下一个小时的日程
            res = checkPeriodTimeEvents(nowDay, nowMin, nowMin + 60, Integer.valueOf(userId));
        } else {
            // 否则是查询第二天的日程
            res = selectSameDayEvents(nowDay + 1, Integer.valueOf(userId));
        }
        res = TimeUtil.adjustDate(res, nowTime);
        MathUtil.mySort(res, Comparator.comparing(Event::getStartTime));
        logger.info(userId + "查询日程成功 " + nowTime + " " + res);
        return res;
    }

    /**
     * 检查用户一段时间内的日程
     * 只提醒 课程, 考试, 临时事务和闹钟类型
     *
     * @param day    日期
     * @param from   起始时间
     * @param to     终止时间
     * @param userId 用户id
     * @return 日程列表
     */
    private List<Event> checkPeriodTimeEvents(long day, int from, int to, Integer userId) {
        // 获取这段时间内的日程
        SegTree segTree = timeMap.get(userId);
        List<Integer> eventIds = segTree.rangeQuery(from, to);
        List<Integer> events = new ArrayList<>();
        for (Integer id : eventIds) {
            Event e = eventIdMap.get(id);
            if (!e.isActivity() && !e.isTemporary()) {
                // 活动和临时事务不需要提前一小时提醒
                events.add(e.getEventId());
            }
        }
        // 返回在同一天的日程
        return selectSameDayEvents(day, events);
    }

    /**
     * 检查用户给定日期的日程
     *
     * @param day    给定日期
     * @param userId 用户id
     * @return 日程列表
     */
    private List<Event> selectSameDayEvents(long day, Integer userId) {
        // 选出该用户的所有日程
        List<Integer> events = userEventRelationMap.getOrDefault(userId, new ArrayList<>());
        // 根据用户的日程id找到对应日程, 并判断其是否是在给定日期的课程
        return selectSameDayEvents(day, events);
    }

    /**
     * 给定日期和日程id列表, 选取和给定日期在同一天的日程
     *
     * @param day    给定日期
     * @param events 日程
     * @return 日程列表
     */
    private List<Event> selectSameDayEvents(long day, List<Integer> events) {
        List<Event> res = new ArrayList<>();
        for (Integer id : events) {
            Event e;
            if ((e = eventIdMap.get(id)) != null && TimeUtil.isInOneDay(day, e)) {
                res.add(e);
            }
        }
        MathUtil.mySort(res, Comparator.comparing(Event::getStartTime));
        return res;
    }
}