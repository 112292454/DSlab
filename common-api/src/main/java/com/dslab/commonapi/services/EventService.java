package com.dslab.commonapi.services;

import com.dslab.commonapi.entity.Event;
import com.dslab.commonapi.entity.User;
import com.dslab.commonapi.vo.Result;

/**
 * @program: dslab-event
 * @description: 日程相关服务的接口
 * @author: 郭晨旭
 * @create: 2023-04-05 18:23
 * @version: 1.0
 **/
public interface EventService {
    Result<?> addEvent(Event event, User user);

    Result<String> deleteByEventId(Event event, User user);

    Result<?> updateEvent(Event event, User user);
}
