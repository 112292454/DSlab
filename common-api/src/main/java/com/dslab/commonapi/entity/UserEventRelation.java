package com.dslab.commonapi.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: DSlab
 * @description: 用户和日程相对应的关系类
 * @author: 郭晨旭
 * @create: 2023-05-02 12:02
 * @version: 1.0
 **/
@Data
public class UserEventRelation implements Serializable {
    private static final long serialVersionUID = 1324378987677898L;

    private Integer id;
    /**
     * 用户的组id
     */
    private Integer groupId;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 日程id
     */
    private Integer eventId;
    /**
     * 日程的实体类
     */
    private Event event;
}
