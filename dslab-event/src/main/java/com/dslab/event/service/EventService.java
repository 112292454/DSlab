package com.dslab.event.service;

import com.dslab.event.domain.Event;
import com.dslab.event.domain.User;
import com.dslab.event.vo.Result;

/**
 * @program: dslab-event
 * @description: 日程相关服务的接口
 * @author: 郭晨旭
 * @create: 2023-04-05 18:23
 * @version: 1.0
 **/
public interface EventService {
    Result addEvent(Event event, User user);

}
