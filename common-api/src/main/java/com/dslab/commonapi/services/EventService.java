package com.dslab.commonapi.services;

import com.dslab.commonapi.entity.Event;
import com.dslab.commonapi.entity.User;
import com.dslab.commonapi.vo.Result;

import java.util.Date;

/**
 * @program: dslab-event
 * @description: 日程相关服务的接口
 * @author: 郭晨旭
 * @create: 2023-04-05 18:23
 * @version: 1.0
 **/
public interface EventService {

    /**
     * 添加日程
     *
     * @param event 待添加的日程
     * @param user  用户信息
     * @return 返回添加信息
     */
    Result<?> addEvent(Event event, User user);

    /**
     * 删除日程
     *
     * @return 是否删除成功
     */
    Result<String> deleteByEventId(Event event, User user);

    /**
     * 修改日程
     *
     * @param event 待添加的日程
     * @param user  用户信息
     * @return 返回修改信息
     */
    Result<?> updateEvent(Event event, User user);

    Result<String> checkUserEventInTime(Date nowTime,String userId);
}
