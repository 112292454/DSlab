package com.dslab.event;

import com.dslab.event.mapper.EventMapper;
import com.dslab.event.mapper.UserEventRelationMapper;
import com.dslab.event.serviceImpl.EventServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class DslabEventApplicationTests {

    @Resource
    EventMapper eventMapper;

    @Resource
    UserEventRelationMapper userEventRelationMapper;

    @Resource
    EventServiceImpl eventService;

    @Test
    void contextLoads() {
        System.out.println(eventService.getIdTree());
        System.out.println(eventService.getNameTree());
        System.out.println(eventService.getUserEventIdList());
        System.out.println(eventService.getUserGroupIdList());
    }
}
