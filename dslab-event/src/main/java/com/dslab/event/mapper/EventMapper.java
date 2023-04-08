package com.dslab.event.mapper;

import com.dslab.event.domain.Event;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @program: dslab-event
 * @description: 和日程相关的数据库操作
 * @author: 郭晨旭
 * @create: 2023-03-29 10:20
 * @version: 1.0
 **/

@Mapper
@Repository
public interface EventMapper {

    /**
     * 新增日程
     *
     * @param event 日程
     * @return 新增日程的id
     */
    int add(Event event);

    /**
     * 根据日程id删除日程 (不会真的删除, 只是将该日程的状态设为禁用)
     *
     * @param eventId 日程id
     * @return 修改的行数
     */
    int deleteByEventId(Integer eventId);

    /**
     * 根据日程名称删除日程 (不会真的删除, 只是将该日程的状态设为禁用)
     *
     * @param name 日程名称
     * @return 改变的行数
     */
    int deleteByName(String name);

    /**
     * 更新日程
     *
     * @param event 日程信息
     * @return 修改的行数
     */
    int update(Event event);

    /**
     * 根据日程id查询
     *
     * @param eventId 日程id
     * @return 日程信息
     */
    Event getByEventId(Integer eventId);

    /**
     * 根据日程名称查询
     *
     * @param name 日程名称
     * @return 日程信息
     */
    Event getByEventName(String name);

    /**
     * 根据日程地点id查询
     *
     * @param buildingId 地点id
     * @return 日程信息
     */
    Event getByBuildingId(Integer buildingId);
}
