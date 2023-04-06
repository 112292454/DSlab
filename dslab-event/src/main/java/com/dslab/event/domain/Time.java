package com.dslab.event.domain;

import lombok.Data;

/**
 * @program: dslab-event
 * @description: 时间类, 可以修改/获取当前时间
 * @author: 郭晨旭
 * @create: 2023-04-06 21:45
 * @version: 1.0
 **/

@Data
public class Time {
    /**
     * 当前时间
     * 时间戳的字符串格式
     */
    private String nowTime;
}
