package com.dslab.event;

import com.dslab.event.mapper.EventMapper;
import com.dslab.event.utils.MathUtils;
import com.dslab.event.utils.TimeUtils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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
