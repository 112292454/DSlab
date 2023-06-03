package com.dslab.event.controller;

import com.dslab.commonapi.entity.User;
import com.dslab.commonapi.services.UserService;
import com.dslab.commonapi.vo.Result;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: dslab-event
 * @description: 用户控制器
 * @author: 郭晨旭
 * @create: 2023-03-29 20:43
 * @version: 1.0
 **/

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {
    @Resource
    UserService userService;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * 根据邮箱获取用户信息
     *
     * @param mail 邮箱
     * @return 用户信息
     */
    @GetMapping("/mail/{mail}")
    public Result<User> getByMail(@PathVariable(value = "mail") String mail) {
        logger.info(mail + " 获取用户信息");
        User user = userService.loadByMail(mail);
        if (user == null) {
            logger.info(mail + " 获取用户信息失败");
            return Result.success("获取失败");
        } else {
            logger.info(mail + " 获取用户信息成功 " + user);
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
        logger.info("获取组");
        return Result.<List<Integer>>success("查询成功").data(groups);
    }
}
