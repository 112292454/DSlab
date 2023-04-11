package com.dslab.commonapi.entity;

/**
 * @program: DSlab
 * @description: 各种日程的属性
 * @author: 郭晨旭
 * @create: 2023-03-26 20:24
 * @version: 1.0
 **/
public enum EventType {
    EVENT_LESSON("0"),
    EVENT_EXAM("1"),
    EVENT_ACTIVITY("2"),
    EVENT_TEMPORARY("3"),
    EVENT_CLOCK("4");

    private String value;

    private EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
