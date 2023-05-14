package com.dslab.event;

import com.alibaba.fastjson.JSON;
import com.dslab.commonapi.entity.Event;
import com.dslab.event.mapper.EventMapper;
import com.dslab.event.mapper.UserEventRelationMapper;
import com.dslab.event.serviceImpl.EventServiceImpl;
import com.dslab.commonapi.utils.MathUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        List<Event> list = new ArrayList<>();
        list.add(new Event(11, "aa"));
        list.add(new Event(22, "bb"));
        list.add(new Event(33, "cc"));
        list.add(new Event(44, "dd"));
        JSON json = (JSON) JSON.toJSON(list);
        System.out.println(json.toString());
        String jsonStr = JSON.toJSONString(list);
        List<Event> ll = JSON.parseArray(jsonStr, Event.class);
        System.out.println(ll.toString());
    }

    @Test
    void testSort() {
        List<Integer> list = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        List<Integer> list3 = new ArrayList<>();
        List<Integer> list4 = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 1000000; ++j) {
                list.add(j);
                list2.add(-j);
                list3.add(new Random().nextInt(10000000));
                list4.add(new Random().nextInt(10000000));
            }
            long t1 = System.currentTimeMillis();
            MathUtil.mySort(list, (o1, o2) -> o1 - o2);
            long t2 = System.currentTimeMillis();
            System.out.print(t2 - t1 + "    ");
            long t3 = System.currentTimeMillis();
            MathUtil.mySort(list2, (o1, o2) -> o1 - o2);
            long t4 = System.currentTimeMillis();
            System.out.print(t4 - t3 + "       ");
            long t5 = System.currentTimeMillis();
            MathUtil.mySort(list3, (o1, o2) -> o1 - o2);
            long t6 = System.currentTimeMillis();
            System.out.println(t6 - t5);
            long t7 = System.currentTimeMillis();
            list4.sort((o1, o2) -> o1 - o2);
            long t8 = System.currentTimeMillis();
            System.out.println(t8 - t7);
        }
    }
}
