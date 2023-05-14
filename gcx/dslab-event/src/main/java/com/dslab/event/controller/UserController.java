package com.dslab.event.controller;

import com.dslab.commonapi.entity.Point;
import com.dslab.commonapi.entity.User;
import com.dslab.commonapi.services.PointService;
import com.dslab.commonapi.vo.Result;
import com.dslab.event.mapper.UserMapper;
import jakarta.validation.Valid;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

    @DubboReference(group = "DSlab-guide", version = "1.0.0", check = false)
    PointService pointService;

    @GetMapping("/dubbo_test")
    public Result<List<Point>> testMethod() {
        return Result.<List<Point>>success("测试dubbo远程调用成功，获取地图：").data(pointService.listAll());
    }

    @GetMapping("/userId/{userId}")
    public Result<User> getByUserId(@PathVariable @RequestParam(defaultValue = "1", required = true) Integer userId) {
        System.out.println(userId);
        User user = userMapper.getByUserId(userId);
        System.out.println(user);
        System.out.println(user.getUserId());
        System.out.println(user.getGroupId());
        return Result.<User>success("登陆成功").data(user);
    }

    @GetMapping("/username/{username}")
    public Result<User> getByUsername(@PathVariable @Valid String username) throws UnsupportedEncodingException {
        System.out.println(username);
        User user = userMapper.getByUsername(username);
        System.out.println(user);
        System.out.println(user.getUserId());
        System.out.println(user.getGroupId());
        return Result.<User>success("登陆成功").data(user);
    }

    @GetMapping("/groupId/{groupId}")
    public Result<List> getByGroupId(@PathVariable Integer groupId) {
        System.out.println(groupId);
        List<User> users = userMapper.getByGroupId(groupId);
        System.out.println(users);
        return Result.<List>success("登陆成功").data(users);
    }

    @PutMapping
    public Result<User> update(@RequestBody @Valid User user) {
        System.out.println(user);
        userMapper.update(user);
        return Result.<User>success("登陆成功").data(user);
    }

    @PostMapping
    public Result<User> add(@RequestBody User user) {
        System.out.println(user);
        System.out.println(userMapper.add(user));
        return Result.<User>success("登陆成功").data(user);
    }

    @GetMapping
    public Result<List> getAll() {
        List<User> list = userMapper.getAllUsers();
        return Result.<List>success("登陆成功").data(list);
    }

    @DeleteMapping("/name/{username}")
    public Result<String> deleteByName(@PathVariable String username) {
        userMapper.deleteByName(username);
        return Result.<String>success("删除成功").data(username);
    }

    @DeleteMapping("/id/{userId}")
    public Result<Integer> deleteByUserId(@PathVariable Integer userId) {
        userMapper.deleteById(userId);
        return Result.<Integer>error("删除失败").data(userId);
    }
}
