package com.dslab.event.controller;

import com.dslab.event.domain.User;
import com.dslab.event.mapper.UserMapper;
import com.dslab.event.vo.Result;
import com.gzy.dslab.guide.service.PointService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
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
    @Resource
    UserMapper userMapper;

    @DubboReference(group = "DS-guide", version = "1.0.0")
    PointService pointService;

    @GetMapping("/userId/{userId}")
    @ResponseBody
    public Result<User> getByUserId(@PathVariable @RequestParam(defaultValue = "1", required = true) Integer userId) {
        System.out.println(userId);
        User user = userMapper.getByUserId(userId);
        System.out.println(user);
        System.out.println(user.getUserId());
        System.out.println(user.getGroupId());
        return Result.<User>success("登陆成功").data(user);
    }

    @GetMapping("/username/{username}")
    @ResponseBody
    public Result<User> getByUsername(@PathVariable @Valid String username) throws UnsupportedEncodingException {
        System.out.println(username);
        User user = userMapper.getByUsername(username);
        System.out.println(user);
        System.out.println(user.getUserId());
        System.out.println(user.getGroupId());
        return Result.<User>success("登陆成功").data(user);
    }

    @GetMapping("/groupId/{groupId}")
    @ResponseBody
    public Result<List> getByGroupId(@PathVariable Integer groupId) {
        System.out.println(groupId);
        List<User> users = userMapper.getByGroupId(groupId);
        System.out.println(users);
        return Result.<List>success("登陆成功").data(users);
    }

    @PutMapping
    @ResponseBody
    public Result<User> update(@RequestBody @Valid User user) {
        System.out.println(user);
        userMapper.update(user);
        return Result.<User>success("登陆成功").data(user);
    }

    @PostMapping
    @ResponseBody
    public Result<User> add(@RequestBody User user) {
        System.out.println(user);
        System.out.println(userMapper.add(user));
        return Result.<User>success("登陆成功").data(user);
    }

    @GetMapping
    @ResponseBody
    public Result<List> getAll() {
        List<User> list = userMapper.getAll();
        return Result.<List>success("登陆成功").data(list);
    }

    @DeleteMapping("/name/{username}")
    @ResponseBody
    public Result<String> deleteByName(@PathVariable String username) {
        userMapper.deleteByName(username);
        return Result.<String>success("删除成功").data(username);
    }

    @DeleteMapping("/id/{userId}")
    @ResponseBody
    public Result<Integer> deleteByUserId(@PathVariable Integer userId) {
        userMapper.deleteById(userId);
        return Result.<Integer>error("删除失败").data(userId);
    }
}
