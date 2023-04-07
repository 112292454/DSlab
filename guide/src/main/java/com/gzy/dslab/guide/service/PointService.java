package com.gzy.dslab.guide.service;

import com.gzy.dslab.guide.entity.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public interface PointService {



    boolean addPoint(int x, int y, String name);

    boolean addPath(int aid, int bid);

    List<Point> listAll();

    List<List<Point>> showMap();



}
