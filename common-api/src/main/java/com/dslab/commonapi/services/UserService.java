package com.dslab.commonapi.services;

import com.dslab.commonapi.entity.Event;
import com.dslab.commonapi.entity.User;

import java.util.List;

/**
 * @program: DSlab
 * @description: 用户服务类
 * @author: 郭晨旭
 * @create: 2023-05-23 22:36
 * @version: 1.0
 **/

public interface UserService {
    /**
     * 选取同一组的用户
     *
     * @param user 管理员
     * @return 同组用户
     */
    List<User> selectSameGroupUsers(User user);


    /**
     * 验证用户身份是否可以添加/修改此日程
     * 集体类只能由管理员添加, 个人类只能由学生添加
     *
     * @param u 用户
     * @param e 日程
     * @return 符合条件返回true, 否则返回false
     */
    boolean identifyUser(User u, Event e);

    /**
     * 根据用户id加载用户
     *
     * @param userId 用户id
     * @return 用户
     */
    User load(Integer userId);

    /**
     * 根据用户邮箱加载用户
     *
     * @param mail 用户邮箱
     * @return 用户
     */
    User loadByMail(String mail);

    /**
     * 判断用户邮箱是否已存在
     *
     * @param mail 用户邮箱
     * @return 用户
     */
    boolean contains(String mail);

    /**
     * 用户注册 (只有学生才会注册)
     *
     * @param name     用户名
     * @param mail     用户邮箱
     * @param password 密码
     * @param groupId  组id
     */
    void register(String name, String mail, String password, Integer groupId);

    /**
     * 获取所有的组id
     *
     * @return 组id列表
     */
    List<Integer> getGroups();
}
