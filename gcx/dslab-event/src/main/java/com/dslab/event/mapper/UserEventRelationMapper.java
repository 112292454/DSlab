package com.dslab.event.mapper;

import com.dslab.commonapi.entity.UserEventRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
    int add(@Param("groupId") Integer groupId, @Param("userId") Integer userId, @Param("eventId") Integer eventId);

    /**
     * 删除用户
     *
     * @param userId 用户id
     * @return 修改的行数
     */
    int deleteUser(Integer userId);

    /**
     * 删除一个课程
     *
     * @param eventId 课程id
     * @return 修改的行数
     */
    int deleteEvents(Integer eventId);

    /**
     * 删除一对映射关系
     *
     * @param userId  用户id
     * @param eventId 日程id
     * @return 修改的行数
     */
    int delete(@Param("userId") Integer userId, @Param("eventId") Integer eventId);

    /**
     * 获取某个组的所有日程
     *
     * @param groupId 组id
     * @return 列表
     */
    List<Integer> getByGroupId(Integer groupId);

    /**
     * 获取某个用户的所有日程
     *
     * @param userId 用户id
     * @return 列表
     */
    List<Integer> getByUserId(Integer userId);

    /**
     * 获取某个日程的所有用户
     *
     * @param eventId 日程id
     * @return 列表
     */
    List<Integer> getByEventId(Integer eventId);

    /**
     * 获取表中的所有信息
     *
     * @return 所有信息的列表
     */
    List<UserEventRelation> getAll();
}
