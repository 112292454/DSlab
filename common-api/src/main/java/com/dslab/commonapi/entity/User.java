package com.dslab.commonapi.entity;

import jakarta.validation.constraints.*;
import lombok.Data;

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
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱不正确")
    private String mail;
    /**
     * 用户类别 (student/admin)
     */
    @NotBlank(message = "用户类型不能为空")
    @Pattern(regexp = "^0|1$", message = "用户类型不正确")
    private String type;
    /**
     * 用户所属的组
     */
    @NotNull(message = "用户所属组不正确")
    @Min(value = 0, message = "组id必须为非负数")
    private Integer groupId;

    public User() {
    }

    public User(String username, String password, String mail, String type, Integer groupId) {
        this.username = username;
        this.password = password;
        this.mail = mail;
        this.type = type;
        this.groupId = groupId;
    }

    public User(Integer userId) {
        this.userId = userId;
    }

    /**
     * 判断是不是管理员
     *
     * @return 是返回true, 否则返回false
     */
    public boolean isAdmin() {
        return UserType.USER_ADMIN.getValue().equals(type);
    }
}
