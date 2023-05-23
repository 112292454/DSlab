package com.dslab.event;

import com.alibaba.fastjson2.JSON;
import com.dslab.commonapi.dataStruct.MyHashMap;
import com.dslab.commonapi.dataStruct.MyMap;
import com.dslab.commonapi.entity.Event;
import com.dslab.commonapi.entity.User;
import com.dslab.commonapi.entity.UserEventRelation;
import com.dslab.commonapi.utils.MathUtil;
import com.dslab.event.mapper.EventMapper;
import com.dslab.event.mapper.UserEventRelationMapper;
import com.dslab.event.mapper.UserMapper;
import com.dslab.event.serviceImpl.EventServiceImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//@SpringBootTest
class DslabEventApplicationTests {

    @Resource
    EventMapper eventMapper;

    @Resource
    UserEventRelationMapper userEventRelationMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    EventServiceImpl eventService;

    @Test
    void contextLoads() {
        List<Event> list = new ArrayList<>();
        list.add(new Event(11, "aa"));
        list.add(new Event(22, "bb"));
        list.add(new Event(33, "cc"));
        list.add(new Event(44, "dd"));
        String jsonStr = JSON.toJSONString(list);
        List<Event> ll = (List<Event>) JSON.parse(jsonStr);
        System.out.println(ll.toString());
        System.out.println(new Date().getTime() - System.currentTimeMillis());
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

    @Test
    void testEntity() {
        UserEventRelation userEventRelation = new UserEventRelation();
        User user = new User();
        user.setUserId(123);
        userEventRelation.setUserId(user.getUserId() + 1);
        System.out.println(userEventRelation.getUserId());
    }

    @Test
    void testMapper() {
        Event event = new Event();
        event.setName("123");
        event.setStartTime(new Date());
        event.setStatus(1);
        eventMapper.add(event);
        System.out.println(userMapper.getAllUsers());
    }

    @Test
    void testMySearch() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(1);
        list.add(2);
        list.add(2);
        list.add(2);
        list.add(2);
        list.add(3);
        list.add(3);
        list.add(3);
        list.add(3);
        System.out.println(MathUtil.lowerBound(list, 2, Comparator.comparingInt(o -> o)));
        System.out.println(MathUtil.lowerBound(list, 4, Comparator.comparingInt(o -> o)));
    }

    @Test
    void testDate() throws ParseException {
        String d = "2013-07-11 01:25:12";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(d);
        System.out.println(date);
    }

    @Test
    void testMap() {
        MyMap<Integer, String> map = new MyHashMap<>();
        map.put(1, "aa");
        System.out.println(map.get(1));
        map.put(1, "bb");
        System.out.println(map.get(1));
        map.remove(1);
        System.out.println(map.get(1) == null);
    }
}
