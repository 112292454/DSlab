package com.dslab.commonapi.services;

import com.dslab.commonapi.entity.Point;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GuideService {


    List<Point> directGuide(int from, int to);

    List<Point> byManyPointsGuide(List<Integer> passedPoints);

}
