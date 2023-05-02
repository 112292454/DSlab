package com.dslab.event;

import com.dslab.commonapi.entity.Event;
import com.dslab.event.mapper.EventMapper;
import com.dslab.event.utils.AVLTree;
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
        AVLTree<Event> avlTree = new AVLTree<>(Comparator.comparingInt(Event::getEventId));
        List<Event> allEvents = eventMapper.getAllEvents();
        for (Event event : allEvents) {
            avlTree.insert(event);
        }
        avlTree.print();
    }
}
