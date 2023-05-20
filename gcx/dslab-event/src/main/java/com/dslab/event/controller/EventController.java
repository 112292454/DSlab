package com.dslab.event.controller;

import com.dslab.commonapi.entity.Event;
import com.dslab.commonapi.entity.RequestParams;
import com.dslab.commonapi.services.EventService;
import com.dslab.commonapi.services.SimulateService;
import com.dslab.commonapi.utils.TimeUtil;
import com.dslab.commonapi.vo.Result;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    @PostMapping("/test")
    public Result<?> testEvent(@RequestBody @Valid Event requestParams) {
        System.out.println(requestParams);
        return Result.success().data(requestParams);
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
        return eventService.addEvent(requestParams.getEvent(), requestParams.getUser());
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
        return eventService.getEventById(eventId);
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
        return eventService.getEventByName(eventName);
    }

    /**
     * 获取用户某一天的日程
     *
     * @param map 两个参数, 用户id和一个时间
     * @return 日程列表
     */
    @GetMapping("/DayEvents")
    @ResponseBody
    public Result<List<Event>> getDayEvents(@RequestParam Map<String, String> map) throws ParseException {
        Integer userId = Integer.valueOf(map.get("userId"));
        Date date = null;
        if (map.containsKey("date")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = sdf.parse(map.get("date"));
        } else {
            date = simulateService.getUserSimulateTime(String.valueOf(userId));
        }
        System.out.println("*************");
        System.out.println(userId);
        System.out.println(date);
        return eventService.getEventsByDay(userId, TimeUtil.dateToDay(date));
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
        return eventService.deleteEventById(requestParams.getEvent(), requestParams.getUser());
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
        return eventService.updateEvent(requestParams.getEvent(), requestParams.getUser());
    }
}
