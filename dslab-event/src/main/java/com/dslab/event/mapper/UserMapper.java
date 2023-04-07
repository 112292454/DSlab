package com.dslab.event.mapper;

import com.dslab.event.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: dslab-event
 * @description: 用户表的数据库操作
 * @author: 郭晨旭
 * @create: 2023-03-29 00:19
 * @version: 1.0
 **/

@Mapper
@Repository
public interface UserMapper {
    /**
     * 添加用户信息
     *
     * @param user 用户信息
     * @return 修改的行数
     */
    int add(User user);

    /**
     * 根据id删除用户
     *
     * @param userId 用户id
     * @return 改变行数
     */
    int deleteById(Integer userId);

    /**
     * 根据姓名删除用户
     *
     * @param username 用户名
     * @return 改变行数
     */
    int deleteByName(String username);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 改变行数
     */
    int update(User user);

    /**
     * 查询所有用户信息
     *
     * @return 所有用户组成的列表
     */
    List<User> getAll();

    /**
     * 按照用户id查询用户
     *
     * @param userId 用户id
     * @return 用户信息
     */
    User getByUserId(Integer userId);

    /**
     * 按照姓名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);

    /**
     * 按照组id查询学生用户
     *
     * @param groupId 用户的组id
     * @return 用户信息
     */
    List<User> getByGroupId(Integer groupId);
}
