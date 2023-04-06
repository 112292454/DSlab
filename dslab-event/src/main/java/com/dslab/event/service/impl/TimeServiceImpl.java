package com.dslab.event.service.impl;

import com.dslab.event.domain.Event;
import com.dslab.event.domain.Time;
import com.dslab.event.service.TimeService;
import org.springframework.stereotype.Service;

/**
 * @program: dslab-event
 * @description: 和时间有关的服务接口实现
 * @author: 郭晨旭
 * @create: 2023-04-06 21:50
 * @version: 1.0
 **/

@Service
public class TimeServiceImpl implements TimeService {

    Time time = new Time();

    @Override
    public boolean compareTime(Event a, Event b) {
        return a.getStartTime().compareTo(b.getEndTime()) >= 0 || a.getEndTime().compareTo(b.getStartTime()) <= 0;
    }

    @Override
    public long TimestampToDate(String timestamp) {
        return (Long.parseLong(timestamp) + 8 * 3600 * 1000) / (3600 * 1000 * 24);
    }

    @Override
    public Boolean IsInOneDay(Event a, Event b) {
        return a.getDate().equals(b.getDate());
    }

    @Override
    public Boolean IsInOneDay(Event e) {
        String nowTime = time.getNowTime();
        long nt = (Long.parseLong(nowTime) + 8 * 3600 * 1000) / (3600 * 1000 * 24);
        long st = (Long.parseLong(e.getStartTime()) + 8 * 3600 * 1000) / (3600 * 1000 * 24);
        return nt == st;
    }


    @Override
    public int TimestampToHour(String timestamp) {
        long t = Long.parseLong(timestamp);
        String res = (t / (3600 * 1000) + 8) % 24 + "";
        return Integer.parseInt(res);
    }
}
