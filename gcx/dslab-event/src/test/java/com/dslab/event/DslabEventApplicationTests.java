package com.dslab.event;

import com.alibaba.fastjson.JSON;
import com.dslab.commonapi.entity.Event;
import com.dslab.event.mapper.EventMapper;
import com.dslab.event.mapper.UserEventRelationMapper;
import com.dslab.event.serviceImpl.EventServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
}
