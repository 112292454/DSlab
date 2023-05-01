package com.dslab.event;

import com.dslab.event.mapper.EventMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class DslabEventApplicationTests {

    @Resource
    EventMapper eventMapper;

    @Test
    void contextLoads() {
        System.out.println(eventMapper.getByEventName("数据结构"));
    }
}
