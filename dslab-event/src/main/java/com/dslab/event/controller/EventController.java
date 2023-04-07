package com.dslab.event.controller;

import com.dslab.event.domain.RequestParam;
import com.dslab.event.service.EventService;
import com.dslab.event.vo.Result;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
    @Resource
    EventService eventService;

    /**
     * 添加日程
     *
     * @param requestParam 请求参数, 包含 event 和 user
     * @return result
     */
    @PostMapping
    @ResponseBody
    public Result addEvent(@RequestBody @Valid RequestParam requestParam) {
        return (Result) eventService.addEvent(requestParam.getEvent(), requestParam.getUser());
    }
}
