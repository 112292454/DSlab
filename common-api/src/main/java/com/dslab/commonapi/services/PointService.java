package com.dslab.commonapi.services;

import com.dslab.commonapi.entity.Point;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * 开发期间为添加地图上点的暂时service
 *
 * @Author Guo
 * @CreateTime 2023-05-09 03:25
 */
@Service
public interface PointService {

/**
 * 添加点
 *
 * @Author Guo
 * @CreateTime 2023-05-09 03:11
 * @Return boolean
 * @param x
 * @param y
 * @param name
 */
    boolean addPoint(int x, int y, String name);

    /**
     * 添加点直接联通的路径
     *
     * @Author Guo
     * @CreateTime 2023-05-09 03:12
     * @Return boolean
     * @param aid
     * @param bid
     */
    boolean addPath(int aid, int bid);

    /**
     * 列出所有的点
     *
     * @Author Guo
     * @CreateTime 2023-05-09 03:12
     * @Return java.util.List<com.dslab.commonapi.entity.Point>
     * @param
     */
    List<Point> listAll();

    /**
     * 取得地图<p>
     * map[i]中有的点都是与第i个点相连的点
     *
     * @Author Guo
     * @CreateTime 2023-05-09 03:12
     * @Return java.util.List<java.util.List<com.dslab.commonapi.entity.Point>>
     * @param
     */
    List<List<Point>> showMap();

    /**
     * 获取所有的点
     *
     *
     * @Author Guo
     * @CreateTime 2023-05-09 03:16
     * @Return java.util.HashMap<java.lang.Integer, com.dslab.commonapi.entity.Point>
     * @param
     */
    HashMap<Integer, Point> showPoints();

    Point getPoint(Integer id);


    void dubboTestMethod();

    /**
     * 勿动，删除数据库中的地图信息，测试期之后删除此方法
     *
     * @Author Guo
     * @CreateTime 2023-05-09 03:17
     * @Return java.lang.Boolean
     * @param
     */
	Boolean deleteAll();

}

