package com.gzy.dslab.guide.service;

import com.gzy.dslab.guide.entity.Point;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GuideService {


    public List<Point> directGuide(int from, int to);

    public List<Point> byManyPointsGuide(List<Point> passedPoints);


}
