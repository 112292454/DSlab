package com.dslab.event.controller;

import com.dslab.commonapi.entity.User;
import com.dslab.commonapi.services.UserService;
import com.dslab.commonapi.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: dslab-event
 * @description: 用户控制器
 * @author: 郭晨旭
 * @create: 2023-03-29 20:43
 * @version: 1.0
 **/

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    /**
     * 根据邮箱获取用户信息
     *
     * @param mail 邮箱
     * @return 用户信息
     */
    @GetMapping("/mail/{mail}")
    public Result<User> getByMail(@PathVariable String mail) {
        User user = userService.loadByMail(mail);
        if (user == null) {
            return Result.success("获取失败");
        } else {
            return Result.<User>success("获取成功").data(user);
        }
    }

    /**
     * 获取所有的组id
     *
     * @return 组id列表
     */
    @GetMapping("/groups")
    public Result<List<Integer>> getGroups() {
        List<Integer> groups = userService.getGroups();
        return Result.<List<Integer>>success("查询成功").data(groups);
    }
}
