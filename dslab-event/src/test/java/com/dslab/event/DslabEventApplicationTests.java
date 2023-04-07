package com.dslab.event;

import com.dslab.event.domain.Event;
import com.dslab.event.utils.CRTUtils;
import com.dslab.event.utils.TimeUtils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DslabEventApplicationTests {

    @Resource
    TimeUtils timeUtils;

    @Resource
    CRTUtils crtUtils;

    @Test
    void contextLoads() {
        long time = System.currentTimeMillis();
        long time1 = time + 3600 * 24 * 3 * 1000;
//        System.out.println(time);
//        System.out.println(time1);
        Event a = new Event(String.valueOf(time), 3);
        Event b = new Event(String.valueOf(time1), 4);
        System.out.println(timeUtils.IsInOneDay(a, b));
    }
}
