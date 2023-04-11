package com.dslab.commonapi.entity;

/**
 * @program: dslab-event
 * @description: 日程的地点属性
 * @author: 郭晨旭
 * @create: 2023-03-26 21:17
 * @version: 1.0
 **/
public enum PositionType {
    POSITION_ONLINE("0"), POSITION_OFFLINE("1");

    private String value;

    private PositionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
