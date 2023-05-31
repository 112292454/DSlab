package com.dslab.event.controller;

import com.dslab.commonapi.entity.Event;
import com.dslab.commonapi.entity.RequestParams;
import com.dslab.commonapi.services.EventService;
import com.dslab.commonapi.services.SimulateService;
import com.dslab.commonapi.vo.Result;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: dslab-event
 * @description: 日程控制器
 * @author: 郭晨旭
 * @create: 2023-04-05 18:22
 * @version: 1.0
 **/

@RestController
@CrossOrigin
@RequestMapping("/events")
public class EventController {

    private static Logger logger = LoggerFactory.getLogger(EventController.class);
    @Resource
    EventService eventService;

    @DubboReference(group = "DSlab", interfaceClass = SimulateService.class, check = false)
    SimulateService simulateService;


    /**
     * 测试方法
     */
    @GetMapping("/test/{userId}&&{date}")
    public Result<?> testEvent(@PathVariable(value = "userId") Integer userId,
                               @PathVariable(value = "date", required = false)
                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date) {
        System.out.println("----------");
        System.out.println(userId);
        System.out.println(date);
        if (date == null) {
            date = new Date();
        }
        System.out.println(date);
        Map map = new HashMap();
        map.put(userId, date);
        return Result.success().data(map);
    }

    /**
     * 添加日程
     *
     * @param requestParams 请求参数, 包含 event 和 user
     * @return 添加成功返回成功信息, 失败则根据不同活动类型进行判断返回内容
     */
    @PostMapping("/addEvent")
    @ResponseBody
    public Result<?> addEvent(@RequestBody @Valid RequestParams requestParams) {
        logger.info(requestParams.getUser() + " 在 "
                + simulateService.getUserSimulateTime(String.valueOf(requestParams.getUser().getUserId()))
                + " 增加日程 " + requestParams.getEvent());
        return eventService.addEvent(requestParams.getEvent(), requestParams.getUser());
    }

    /**
     * 删除日程
     *
     * @param requestParams 请求参数, 包含 event 和 user
     * @return 修改成功返回成功信息, 失败则根据不同活动类型进行判断返回内容
     */
    @DeleteMapping("/deleteEvent")
    @ResponseBody
    public Result<?> deleteByEventId(@RequestBody @Valid RequestParams requestParams) {
        logger.info(requestParams.getUser() + " 在 "
                + simulateService.getUserSimulateTime(String.valueOf(requestParams.getUser().getUserId()))
                + " 删除课程 " + requestParams.getEvent());
        return eventService.deleteByEventId(requestParams.getEvent(), requestParams.getUser());
    }

    /**
     * 修改日程
     *
     * @param requestParams 请求参数, 包含 event 和 user
     * @return 修改成功返回成功信息, 失败则根据不同活动类型进行判断返回内容
     */
    @PutMapping("/updateEvent")
    @ResponseBody
    public Result<?> updateEvent(@RequestBody @Valid RequestParams requestParams) {
        logger.info(requestParams.getUser() + " 在 "
                + simulateService.getUserSimulateTime(String.valueOf(requestParams.getUser().getUserId()))
                + " 更新课程 " + requestParams.getEvent());
        return eventService.updateEvent(requestParams.getEvent(), requestParams.getUser());
    }

    /**
     * 根据id获取日程信息
     *
     * @param eventId 日程id
     * @return 日程信息
     */
    @GetMapping("/eventId/{eventId}")
    @ResponseBody
    public Result<Event> getByEventId(@PathVariable @Param("eventId") Integer eventId) {
        logger.info("用户获取id为 " + eventId + " 的日程");
        Event e = eventService.getByEventId(eventId);
        if (e == null) {
            return Result.error("查询失败");
        } else {
            return Result.<Event>success("查询成功").data(e);
        }
    }

    /**
     * 根据名字获取日程信息
     *
     * @param eventName 日程名称
     * @return 日程信息
     */
    @GetMapping("/eventName/{eventName}")
    @ResponseBody
    public Result<Event> getByEventName(@PathVariable @Param("eventName") String eventName) {
        logger.info("用户获取名字为 " + eventName + " 的日程");
        Event e = eventService.getByEventName(eventName);
        if (e == null) {
            return Result.error("查询失败");
        } else {
            return Result.<Event>success("查询成功").data(e);
        }
    }

    /**
     * 获取用户某一天的所有日程
     *
     * @param userId 用户id
     * @param date   时间
     * @return 日程列表
     */
    @GetMapping("/DayEvents/{userId}&&{date}")
    @ResponseBody
    public Result<?> getDayEvents(@PathVariable(value = "userId") Integer userId,
                                  @PathVariable(value = "date", required = false)
                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date) throws CloneNotSupportedException {
        if (date == null) {
            date = simulateService.getUserSimulateTime(String.valueOf(userId));
        }
        logger.info(userId + " 获取 " + date + " 日程");
        List<Event> res = eventService.getDayEvents(userId, date);
        return Result.<List<Event>>success("查询成功").data(res);
    }

    /**
     * 获取用户某一天的课程和考试
     *
     * @param userId 用户id
     * @param date   时间
     * @return 日程列表
     */
    @GetMapping("/LessonAndExam/{userId}&&{date}")
    @ResponseBody
    public Result<?> getLessonAndExam(@PathVariable(value = "userId") Integer userId,
                                      @PathVariable(value = "date", required = false)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date) throws CloneNotSupportedException {
        if (date == null) {
            date = simulateService.getUserSimulateTime(String.valueOf(userId));
        }
        logger.info(userId + " 获取 " + date + " 课程和考试");
        List<Event> res = eventService.getLessonAndExam(userId, date);
        return Result.<List<Event>>success("查询成功").data(res);
    }

    /**
     * 获取用户一周的课程和考试
     *
     * @param userId 用户id
     * @param date   指定周的第一天
     * @return 日程列表
     */
    @GetMapping("/WeekLessonAndExam/{userId}&&{date}")
    @ResponseBody
    public Result<?> getWeekLessonAndExam(@PathVariable(value = "userId") Integer userId,
                                          @PathVariable(value = "date")
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date) throws CloneNotSupportedException {
        if (date == null) {
            logger.info(userId + " 获取以 " + date + " 为起始的一周的课程和考试, 但date为空, 获取失败");
            return Result.<List<Event>>error("日期为空, 查询失败");
        }
        logger.info(userId + " 获取以 " + date + " 为起始的一周的课程和考试");
        List<Event> res = eventService.getWeekLessonAndExam(userId, date);
        return Result.<List<Event>>success("查询成功").data(res);
    }

    /**
     * 获取用户某一天的集体活动
     *
     * @param userId 用户id
     * @param date   时间
     * @return 日程列表
     */
    @GetMapping("/GroupActivities/{userId}&&{date}")
    @ResponseBody
    public Result<?> getGroupActivities(@PathVariable(value = "userId") Integer userId,
                                        @PathVariable(value = "date", required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date) throws CloneNotSupportedException {
        if (date == null) {
            date = simulateService.getUserSimulateTime(String.valueOf(userId));
        }
        logger.info(userId + " 获取 " + date + " 集体活动");
        List<Event> res = eventService.getGroupActivities(userId, date);
        return Result.<List<Event>>success("查询成功").data(res);
    }

    /**
     * 获取用户某一天的个人日程
     *
     * @param userId 用户id
     * @param date   时间
     * @return 日程列表
     */
    @GetMapping("/PersonalEvents/{userId}&&{date}")
    @ResponseBody
    public Result<?> getPersonalEvents(@PathVariable(value = "userId") Integer userId,
                                       @PathVariable(value = "date", required = false)
                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date) throws CloneNotSupportedException {
        if (date == null) {
            date = simulateService.getUserSimulateTime(String.valueOf(userId));
        }
        logger.info(userId + " 获取 " + date + " 个人日程");
        List<Event> res = eventService.getPersonalEvents(userId, date);
        return Result.<List<Event>>success("查询成功").data(res);
    }

    /**
     * 获取用户给定日期和类型的活动或者临时事务
     *
     * @param userId 用户id
     * @param date   时间
     * @param type   自定义的类型
     * @return 日程列表
     */
    @GetMapping("/TypeAndDate/{userId}&&{date}&&{type}")
    @ResponseBody
    public Result<?> getByTypeAndDate(@PathVariable(value = "userId") Integer userId,
                                      @PathVariable(value = "date", required = false)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date,
                                      @PathVariable(value = "type", required = false) String type) throws CloneNotSupportedException {
        if (date == null) {
            date = simulateService.getUserSimulateTime(String.valueOf(userId));
        }
        logger.info(userId + " 获取 " + date + " 的 " + type + " 活动");
        List<Event> res = eventService.getByTypeAndDate(userId, date, type);
        return Result.<List<Event>>success("查询成功").data(res);
    }
}
