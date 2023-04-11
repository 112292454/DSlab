package com.dslab.commonapi.entity;

/**
 * @program: dslab-event
 * @description: 事件参与者的性质
 * @author: 郭晨旭
 * @create: 2023-03-26 21:18
 * @version: 1.0
 **/
public enum MemberType {
    MEMBER_PERSONAL("0"), MEMBER_GROUP("1");

    private String value;

    private MemberType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
