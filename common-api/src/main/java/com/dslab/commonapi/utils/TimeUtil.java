package com.dslab.commonapi.utils;

import com.dslab.commonapi.entity.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @program: dslab-event
 * @description: 和时间有关的工具
 * @author: 郭晨旭
 * @create: 2023-04-06 21:50
 * @version: 1.0
 **/


public class TimeUtil {

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
        int start = dateToHour(e.getStartTime());
        int end = dateToHour(e.getEndTime());
        if (start <= end) {
            if (e.getIsGroup()) {
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
        int aStartHour = dateToHour(a.getStartTime());
        int aEndHour = dateToHour(a.getEndTime());
        int bStartHour = dateToHour(b.getStartTime());
        int bEndHour = dateToHour(b.getEndTime());
        return !(aStartHour >= bEndHour || aEndHour <= bStartHour);
    }

    /**
     * 日期加上指定天数
     *
     * @param date 日期
     * @param day  天数
     * @return 加上天数后的日期
     */
    public static Date addDate(Date date, long day) {
        long time = date.getTime();
        day = day * 24 * 60 * 60 * 1000;
        time += day;
        return new Date(time);
    }


    /**
     * 将日期转换成天数
     *
     * @param date 日期
     * @return 天数
     */
    public static long dateToDay(Date date) {
        long timestamp = date.getTime();
        return (timestamp + 8 * 3600 * 1000) / (3600 * 1000 * 24);
    }

    /**
     * 将日期换成小时数
     *
     * @param date 时间
     * @return 小时数
     */
    public static int dateToHour(Date date) {
        return date.getHours();
    }

    /**
     * 将data转换成在当天的分钟数
     *
     * @param date 日期
     * @return 当天的分钟数，介于0~1440-1之间
     */
    public static int dateToMin(Date date) {
        return date.getHours() * 60 + date.getMinutes();
    }

    /**
     * 判断两个日程是否可能在同一天
     *
     * @return 在同一天则返回true, 否则返回false
     */
    public static Boolean isInOneDay(Event a, Event b) {
        if (a == null || b == null) {
            return false;
        }
        long aDate = dateToDay(a.getStartTime());
        long bDate = dateToDay(b.getStartTime());
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
                date = MathUtil.CRT(new long[]{0, bDate - aDate}, new long[]{a.getCycle(), b.getCycle()}, 2);
            } else {
                date = MathUtil.CRT(new long[]{aDate - bDate, 0}, new long[]{a.getCycle(), b.getCycle()}, 2);
            }
            if (date < Math.abs(aDate - bDate)) {
                date = date + (long) a.getCycle() * b.getCycle() / MathUtil.gcd(a.getCycle(), b.getCycle());
            }
            return date <= MAX_DATE;
        }
    }

    /**
     * 判断一个日程和是否和给定日期在同一天
     *
     * @return 在当天则返回true, 否则返回false
     */
    public static Boolean isInOneDay(Long nowDay, Event e) {
        if (e == null) {
            return false;
        }
        long eDate = dateToDay(e.getStartTime());
        // 根据周期是否为0分类讨论, 进行判断
        if (e.getCycle() == 0) {
            return nowDay == eDate;
        } else {
            return eDate <= nowDay && (nowDay - eDate) % e.getCycle() == 0;
        }
    }

    /**
     * 将日程日期调整到指定日期之后
     *
     * @param e 日程列表
     * @param date 指定日期
     * @return 调整后日程列表
     */
    public static List<Event> adjustDate(List<Event> events, Date date) {
        List<Event> res = new ArrayList<>();
        for (Event e : events) {
            while (e.getDate().before(date)) {
                e.addCycle();
            }
            res.add(e);
        }
        return res;
    }
}
