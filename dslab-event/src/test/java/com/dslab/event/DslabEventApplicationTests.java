package com.dslab.event;

import com.dslab.event.service.impl.TimeServiceImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;

@SpringBootTest
class DslabEventApplicationTests {

    @Resource
    TimeServiceImpl timeService;

    @Test
    void contextLoads() {
        long time = System.currentTimeMillis();
        long time1 = time - 3600 * 1000 * 17;
        time = time - 3600 * 1000 * 2;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(time);
        System.out.println(date);
        date = format.format(time1);
        System.out.println(date);
        System.out.println(timeService.TimestampToHour(String.valueOf(time)));
        System.out.println(timeService.TimestampToHour(String.valueOf(time1)));
        System.out.println(this.IsInOneDay(time, time1));
    }

    public Boolean IsInOneDay(long a, long b) {
        long nt = (a + 8 * 3600 * 1000) / (3600 * 1000 * 24);
        long st = (b + 8 * 3600 * 1000) / (3600 * 1000 * 24);
        return nt == st;
    }
}
