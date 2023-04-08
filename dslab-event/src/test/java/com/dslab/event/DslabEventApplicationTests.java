package com.dslab.event;

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
        time = timeUtils.TimestampToDate(String.valueOf(time));
        time1 = timeUtils.TimestampToDate(String.valueOf(time1));
        System.out.println(time1 - time);
        System.out.println(crtUtils.CRT(new long[]{0, time1 - time}, new long[]{7, 3}, 2));
    }
}
