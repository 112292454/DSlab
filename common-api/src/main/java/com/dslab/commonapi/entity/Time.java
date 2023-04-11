package com.dslab.commonapi.entity;

import lombok.Data;

/**
 * @program: dslab-event
 * @description: 时间类, 可以修改/获取当前时间,
 * 实际使用时该类的相关功能由定时器提供接口实现, 所以会删除
 * @author: 郭晨旭
 * @create: 2023-04-06 21:45
 * @version: 1.0
 **/

@Data
@Deprecated
public class Time {
    /**
     * 当前时间
     * 时间戳的字符串格式
     */
    private String nowTime;
    /**
     * 学期开始时间
     */
    private String startTime;
    /**
     * 学期结束时间
     */
    private String endTime = String.valueOf(System.currentTimeMillis() + 3600L * 1000 * 24 * 120);
}
