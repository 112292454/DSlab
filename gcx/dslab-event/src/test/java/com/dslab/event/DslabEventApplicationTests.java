package com.dslab.event;

import com.dslab.commonapi.entity.Event;
import com.dslab.commonapi.services.AVLTreeService;
import com.dslab.event.mapper.EventMapper;
import com.dslab.event.serviceImpl.AVLTreeServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

@SpringBootTest
class DslabEventApplicationTests {

    @Resource
    EventMapper eventMapper;

    @Test
    void contextLoads() {
        AVLTreeService<Event> avlTree = new AVLTreeServiceImpl<>(Comparator.comparing(Event::getName));
        List<Event> allEvents = eventMapper.getAllEvents();
        for (Event event : allEvents) {
            avlTree.insert(event);
        }
        System.out.println(avlTree.search(new Event("数据结构")).getKey());
    }
}
