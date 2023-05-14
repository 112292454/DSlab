package com.dslab.event.utils;

import com.dslab.commonapi.entity.Event;
import com.dslab.commonapi.entity.EventType;
import com.dslab.commonapi.services.SimulateService;

import javax.annotation.Resource;
import java.util.Date;


/**
 * @program: dslab-event
 * @description: 和时间有关的工具
 * @author: 郭晨旭
 * @create: 2023-04-06 21:50
 * @version: 1.0
 **/


public class TimeUtils {

    /**
     * 冲突的规定范围日期
     */
    public static final long MAX_DATE = 120;

    /**
     * 课程, 考试类的时间范围
     */
    private static final long ADMIN_START_TIME = 8;
    private static final long ADMIN_END_TIME = 20;
    /**
     * 课外活动, 临时事务类的时间范围
     */
    private static final long STU_START_TIME = 6;
    private static final long STU_END_TIME = 22;


    /**
     * 判断日程时间是否合法
     *
     * @return 合法返回true, 否则返回false
     */
    public static boolean checkTimeValid(Event e) {
        long start = TimestampToHour(e.getStartTime());
        long end = TimestampToHour(e.getEndTime());
        if (start < end) {
            if (!(EventType.EVENT_LESSON.getValue().equals(e.getEventType())
                    || EventType.EVENT_EXAM.getValue().equals(e.getEventType()))) {
                return start >= ADMIN_START_TIME && end <= ADMIN_END_TIME;
            } else {
                return start >= STU_START_TIME && end <= STU_END_TIME;
            }
        }
        return false;
    }

    /**
     * 对比日程时间是否有冲突
     *
     * @return 有冲突返回true, 否则返回false
     */
    public static boolean compareTime(Event a, Event b) {
        long aStartHour = TimestampToHour(a.getStartTime());
        long aEndHour = TimestampToHour(a.getEndTime());
        long bStartHour = TimestampToHour(b.getStartTime());
        long bEndHour = TimestampToHour(b.getEndTime());
        return !(aStartHour >= bEndHour || aEndHour <= bStartHour);
    }

    /**
     * 将时间戳转换成日期 (天数)
     *
     * @param timestamp 时间戳
     * @return 日期
     */
    public static long TimestampToDate(String timestamp) {
        return (Long.parseLong(timestamp) + 8 * 3600 * 1000) / (3600 * 1000 * 24);
    }

    /**
     * 将时间戳转换成小时数
     *
     * @param timestamp 时间戳
     * @return 小时数
     */
    public static int TimestampToHour(String timestamp) {
        long t = Long.parseLong(timestamp);
        String res = (t / (3600 * 1000) + 8) % 24 + "";
        return Integer.parseInt(res);
    }

    /**
     * 将data转换成在当天的分钟数
     *
     * @param date 日期
     * @return 当天的分钟数，介于0~1440-1之间
     */
    public static int dateToMin(Date date) {
        return date.getHours()*60+date.getMinutes();
    }

    /**
     * 判断两个日程是否可能在同一天
     *
     * @return 在同一天则返回true, 否则返回false
     */
    public static Boolean IsInOneDay(Event a, Event b) {
        long aDate = TimestampToDate(a.getStartTime());
        long bDate = TimestampToDate(b.getStartTime());
        // 根据周期是否为0分类讨论, 进行判断
        if (a.getCycle() == 0 && b.getCycle() == 0) {
            return aDate == bDate;
        } else if (a.getCycle() == 0) {
            return bDate <= aDate && (aDate - bDate) % b.getCycle() == 0;
        } else if (b.getCycle() == 0) {
            return aDate <= bDate && (bDate - aDate) % a.getCycle() == 0;
        } else {
            long date;
            if (aDate <= bDate) {
                date = MathUtils.CRT(new long[]{0, bDate - aDate}, new long[]{a.getCycle(), b.getCycle()}, 2);
            } else {
                date = MathUtils.CRT(new long[]{aDate - bDate, 0}, new long[]{a.getCycle(), b.getCycle()}, 2);
            }
            if (date < Math.abs(aDate - bDate)) {
                date = date + (long) a.getCycle() * b.getCycle() / MathUtils.gcd(a.getCycle(), b.getCycle());
            }
            return date <= MAX_DATE;
        }
    }

    /**
     * 判断事务是否在当天
     *
     * @return 在当天则返回true, 否则返回false
     */
    public static Boolean IsInOneDay(Long nowTime, Event e) {
        long nt = TimestampToDate(String.valueOf(nowTime));
        long st = TimestampToDate(e.getStartTime());
        return nt == st;
    }
}
