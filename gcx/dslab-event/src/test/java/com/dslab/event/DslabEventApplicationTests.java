package com.dslab.event;

import com.dslab.event.mapper.EventMapper;
import com.dslab.event.utils.MathUtils;
import com.dslab.event.utils.TimeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class DslabEventApplicationTests {

    @Resource
    TimeUtils timeUtils;

    @Resource
    MathUtils mathUtils;
    @Resource
    EventMapper eventMapper;

    @Test
    void contextLoads() {
        System.out.println(eventMapper.getByEventName("数据结构"));
    }
}
