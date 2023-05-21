package com.dslab.commonapi.services;

import com.dslab.commonapi.entity.Event;
import com.dslab.commonapi.entity.User;
import com.dslab.commonapi.vo.Result;

import java.util.Date;
import java.util.List;

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
    Result<?> deleteEventById(Event event, User user);

    /**
     * 修改日程
     *
     * @param event 待添加的日程
     * @param user  用户信息
     * @return 返回修改信息
     */
    Result<?> updateEvent(Event event, User user);

    /**
     * 根据日程id获取日程
     *
     * @param eventId 日程id
     * @return 日程信息
     */
    Result<Event> getEventById(Integer eventId);

    /**
     * 根据日程名称获取日程
     *
     * @param eventName 日程名称
     * @return 日程信息
     */
    Result<Event> getEventByName(String eventName);


    /**
     * 获取用户给定日期的所有日程
     *
     * @param userId 用户id
     * @param day   时间
     * @return 日程列表
     */

    Result<List<Event>> getEventsByDay(Integer userId, long day);

    /**
     * 根据给定时间获取用户日程
     * 时间点在23点后获取第二天日程, 否则获取下一个小时的日程
     *
     * @param nowTime 时间点
     * @param userId  用户id
     * @return 日程
     */
    Result<List<Event>> checkUserEventInTime(Date nowTime, String userId);
}
