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
}
