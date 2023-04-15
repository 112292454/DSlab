package com.dslab.event.controller;

import com.dslab.commonapi.entity.Event;
import com.dslab.commonapi.entity.RequestParams;
import com.dslab.commonapi.services.EventService;
import com.dslab.commonapi.vo.Result;
import com.dslab.event.mapper.EventMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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

    @Resource
    EventMapper eventMapper;

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
     * todo 后期要用自己实现的算法替换
     *
     * @param eventId 课程ID
     * @return 课程信息
     */
    @GetMapping("/eventId/{eventId}")
    @ResponseBody
    public Result<Event> getByEventId(@PathVariable Integer eventId) {
        Event event = eventMapper.getByEventId(eventId);
        return Result.<Event>success().data(event);
    }

    /**
     * 删除日程
     *
     * @param requestParams 请求参数, 包含 event 和 user
     * @return 修改成功返回成功信息, 失败则根据不同活动类型进行判断返回内容
     */
    @DeleteMapping("/deleteEvent")
    @ResponseBody
    public Result<String> deleteByEventId(@RequestBody @Valid RequestParams requestParams) {
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
        return eventService.updateEvent(requestParams.getEvent(), requestParams.getUser());
    }
}
