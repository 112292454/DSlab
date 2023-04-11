package com.dslab.commonapi.services;

import com.dslab.commonapi.entity.Point;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public interface PointService {



    boolean addPoint(int x, int y, String name);

    boolean addPath(int aid, int bid);

    List<Point> listAll();

    List<List<Point>> showMap();

    HashMap<Integer, Point> showPoints();

    void dubboTestMethod();

	Boolean deleteAll();


}

