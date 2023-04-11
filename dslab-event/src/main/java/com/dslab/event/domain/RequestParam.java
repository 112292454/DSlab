package com.dslab.event.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: dslab-event
 * @description: 接受的参数类
 * @author: 郭晨旭
 * @create: 2023-04-05 18:47
 * @version: 1.0
 **/

@Data
public class RequestParam implements Serializable {
    @Serial
    private static final long serialVersionUID = 1324389877898L;
    Event event;
    User user;
}
