package com.dslab.event.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: dslab-event
 * @description: 用户表和日程表的中间表, 辅助操作
 * @author: 郭晨旭
 * @create: 2023-03-29 15:19
 * @version: 1.0
 **/
@Mapper
@Repository
public interface UserEventRelationMapper {
    /**
     * 添加课程映射
     *
     * @param userId  用户id
     * @param eventId 日程id
     * @return 修改的行数
     */
    @Insert("insert into user_event_relation (group_id ,user_id, event_id) values (#{groupId},#{userId}, #{eventId})")
    int add(@Param("groupId") Integer groupId, @Param("userId") Integer userId, @Param("eventId") Integer eventId);

    /**
     * 删除用户
     *
     * @param userId 用户id
     * @return 修改的行数
     */
    @Delete("delete from user_event_relation where user_id = #{userId}")
    int deleteUser(Integer userId);

    /**
     * 删除一个课程
     *
     * @param eventId 课程id
     * @return 修改的行数
     */
    @Delete("delete from user_event_relation where event_id = #{eventId}")
    int deleteEvents(Integer eventId);

    /**
     * 删除一对映射关系
     *
     * @param userId  用户id
     * @param eventId 日程id
     * @return 修改的行数
     */
    @Delete("delete from user_event_relation where user_id = #{userId} and event_id = #{eventId}")
    int delete(@Param("userId") Integer userId,@Param("eventId") Integer eventId);

    /**
     * 选择某个组的所有日程
     *
     * @param groupId 组id
     * @return 用户id 的列表
     */
    @Select("select event_id from user_event_relation where group_id = #{groupId}")
    List<Integer> selectByGroupId(Integer groupId);

    /**
     * 选择某个用户的所有日程
     *
     * @param userId 用户id
     * @return 日程id的列表
     */
    @Select("select event_id from user_event_relation where user_id = #{userId}")
    List<Integer> selectByUserId(Integer userId);

    /**
     * 选择某个日程的所有用户
     *
     * @param eventId 日程id
     * @return 用户id 的列表
     */
    @Select("select user_id from user_event_relation where event_id = #{eventId}")
    List<Integer> selectByEventId(Integer eventId);
}
