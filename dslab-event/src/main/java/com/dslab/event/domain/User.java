package com.dslab.event.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: DSlab
 * @description: 用户对象类
 * @author: 郭晨旭
 * @create: 2023-03-26 18:30
 * @version: 1.0
 **/

@Data
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1324389877885L;
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 邮箱
     */
    private String mail;
    /**
     * 用户类别 (student/admin)
     */
    private String type;
    /**
     * 用户所属的组
     */
    private Integer groupId;
}
