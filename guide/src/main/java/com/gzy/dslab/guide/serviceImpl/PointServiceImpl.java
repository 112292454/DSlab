package com.gzy.dslab.guide.serviceImpl;

import com.gzy.dslab.guide.dao.PointMapper;
import com.gzy.dslab.guide.entity.Point;
import com.gzy.dslab.guide.service.PointService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class PointServiceImpl implements PointService {

    private static HashMap<Integer, Point> points= new HashMap<>();

    private static List<List<Point>> map= new ArrayList<>();

    @Resource
    PointMapper pointMapper;

    private int maxID = 0;

    public PointServiceImpl() {
        //refreshMap();
    }

    @Override
    public boolean addPoint(int x, int y, String name) {

        Point p= StringUtils.isBlank(name)?new Point(maxID,x,y):new Point(maxID,x,y,name);
        AtomicBoolean flag = new AtomicBoolean(true);
        points.forEach((k,v)->{if(v.equals(p)) flag.set(false);});
        if(!flag.get()) return false;
        points.put(maxID, p);
        map.add(new ArrayList<>());

        return refreshRedis(maxID++);
    }

    @Override
    public boolean addPath(int aid, int bid) {
        Point a = points.get(aid);
        Point b = points.get(bid);
        if(a.getNeighbors().contains(b.getId())||b.getNeighbors().contains(a.getId())) return false;
        map.get(aid).add(b);
        map.get(bid).add(a);
        a.getNeighbors().add(bid);
        b.getNeighbors().add(aid);
        return refreshRedis(aid,bid);
    }

    @Override
    public List<Point> listAll() {
        refreshMap();
        List<Point> res=new ArrayList<>();
        points.forEach((k,v)->{if(k>0) res.add(v);});
        return res;
    }

    @Override
    public List<List<Point>> showMap() {
        return map;
    }

    private boolean refreshRedis(int... ids){
        boolean flag=true;
        for (int id : ids) {
            System.out.println("更新" + id);
            pointMapper.setPoint(id,points.get(id));
        }
        return flag;
    }

    @PostConstruct
    private void init(){
        map.add(new ArrayList<>());
        points.put(0,new Point(0, -1, -1));
        refreshMap();
        refreshRedis(0);
        maxID++;
    }

    private void refreshMap() {
        List<Point> allPoints = pointMapper.getAllPoints();
        //对所有的点构造它和邻居的双向关系，完成地图构造
        allPoints.forEach(a->{
            map.add(new ArrayList<>());
            maxID=Math.max(a.getId(), maxID);
            points.put(a.getId(),a);

            //a的所有邻居的编号
            List<Integer> neighbors = a.getNeighbors();
            //a的所有邻居的list
            List<Point> mapLine = map.get(a.getId());
            neighbors.forEach(id ->{
                //给a对应的mapLine加入所有相邻的节点
                mapLine.add(allPoints.get(id));
                //给a的所有邻居对应的mapLine加入a，构造双向路径
                map.get(id).add(a);
            });
        });
    }
}
