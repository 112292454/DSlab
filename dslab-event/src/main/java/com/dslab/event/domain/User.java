package com.dslab.event.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "用户名不能为空")
    private String username;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 邮箱
     */
    @Email(message = "邮箱不正确")
    private String mail;
    /**
     * 用户类别 (student/admin)
     */
    @NotBlank(message = "用户类型不能为空")
    private String type;
    /**
     * 用户所属的组
     */
    @NotNull(message = "用户所属组不正确")
    @Min(value = 0, message = "组id必须为非负数")
    private Integer groupId;
}
