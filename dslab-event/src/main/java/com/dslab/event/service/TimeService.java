package com.dslab.event.service;

import com.dslab.event.domain.Event;

/**
 * @program: dslab-event
 * @description: 和时间有关的服务接口
 * @author: 郭晨旭
 * @create: 2023-04-06 21:50
 * @version: 1.0
 **/
public interface TimeService {
    /**
     * 对比日程时间是否有冲突
     *
     * @return 没有冲突返回true, 否则返回false
     */
    boolean compareTime(Event a, Event b);

    /**
     * 将时间戳转换成日期 (天数)
     *
     * @param timestamp 时间戳
     * @return 日期
     */
    long TimestampToDate(String timestamp);

    /**
     * 判断两个日程是否在同一天
     *
     * @return 在同一天则返回true, 否则返回false
     */
    Boolean IsInOneDay(Event a, Event b);

    /**
     * 判断事务是否在当天
     *
     * @return 在当天则返回true, 否则返回false
     */
    Boolean IsInOneDay(Event e);

    /**
     * 将时间戳转换成小时数
     *
     * @param timestamp 时间戳
     * @return 小时数
     */
    int TimestampToHour(String timestamp);
}
