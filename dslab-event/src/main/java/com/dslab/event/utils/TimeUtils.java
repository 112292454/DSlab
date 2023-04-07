package com.dslab.event.utils;

import com.dslab.event.domain.Event;
import com.dslab.event.domain.Time;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @program: dslab-event
 * @description: 和时间有关的工具
 * @author: 郭晨旭
 * @create: 2023-04-06 21:50
 * @version: 1.0
 **/

@Component
public class TimeUtils {

    Time time = new Time();
    @Resource
    CRTUtils crtUtils;

    /**
     * 对比日程时间是否有冲突
     *
     * @return 有冲突返回true, 否则返回false
     */
    public boolean compareTime(Event a, Event b) {
        long aStartHour = this.TimestampToHour(a.getStartTime());
        long aEndHour = this.TimestampToHour(a.getEndTime());
        long bStartHour = this.TimestampToHour(b.getStartTime());
        long bEndHour = this.TimestampToHour(b.getEndTime());
        return !(aStartHour >= bEndHour || aEndHour <= bStartHour);
    }

    /**
     * 将时间戳转换成日期 (天数)
     *
     * @param timestamp 时间戳
     * @return 日期
     */
    public long TimestampToDate(String timestamp) {
        return (Long.parseLong(timestamp) + 8 * 3600 * 1000) / (3600 * 1000 * 24);
    }

    /**
     * 将时间戳转换成小时数
     *
     * @param timestamp 时间戳
     * @return 小时数
     */
    public int TimestampToHour(String timestamp) {
        long t = Long.parseLong(timestamp);
        String res = (t / (3600 * 1000) + 8) % 24 + "";
        return Integer.parseInt(res);
    }

    /**
     * 判断两个日程是否可能在同一天
     *
     * @return 在同一天则返回true, 否则返回false
     */
    public Boolean IsInOneDay(Event a, Event b) {
        long aDate = this.TimestampToDate(a.getStartTime());
        long bDate = this.TimestampToDate(b.getStartTime());
        // 根据周期是否为0分类讨论, 进行判断
        if (a.getCycle() == 0 && b.getCycle() == 0) {
            return aDate == bDate;
        } else if (a.getCycle() == 0) {
            return bDate <= aDate && (aDate - bDate) % b.getCycle() == 0;
        } else if (b.getCycle() == 0) {
            return aDate <= bDate && (bDate - aDate) % a.getCycle() == 0;
        } else {
            /* todo 此处可能需要调用时钟的api获取一下停止时间
                    有些复杂, 因为算出来的是满足要求的最小数字,
                    所以还需要个循环获取范围内的合理日期
                    所以甚至可能不如直接模拟,
                    看网上有一句代码的算法, 但是似乎需要模数互质才行
             */
            long endTime = this.TimestampToDate(time.getEndTime());
//            long conDate = crtUtils.CRT(new long[]{aDate, bDate}, new long[]{a.getCycle(), b.getCycle()}, 2);
//            long lcm = (long) a.getCycle() * b.getCycle() / crtUtils.gcd(a.getCycle(), b.getCycle());
//            for (long i = conDate; i <= endTime; i += lcm) {
//                if (i >= Math.max(aDate, bDate)) {
//                    return true;
//                }
//            }
            // 所以选择直接模拟的写法
            for (long i = Math.max(aDate, bDate); i <= endTime; ++i) {
                if ((i - aDate) % a.getCycle() == 0 && (i - bDate) % b.getCycle() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断事务是否在当天
     *
     * @return 在当天则返回true, 否则返回false
     */
    public Boolean IsInOneDay(Event e) {
        // todo 此处需要调用定时器的api获取当前时间 (微服务??)
        String nowTime = time.getNowTime();
        long nt = this.TimestampToDate(nowTime);
        long st = this.TimestampToDate(e.getStartTime());
        return nt == st;
    }
}
