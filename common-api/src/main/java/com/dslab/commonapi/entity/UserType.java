package com.dslab.commonapi.entity;

/**
 * @program: DSlab
 * @description: 用户属性
 * @author: 郭晨旭
 * @create: 2023-03-26 20:40
 * @version: 1.0
 **/
public enum UserType {
    USER_STUDENT("0"), USER_ADMIN("1");

    private String value;

    private UserType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
