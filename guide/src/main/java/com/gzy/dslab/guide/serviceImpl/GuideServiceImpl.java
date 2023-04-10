package com.gzy.dslab.guide.serviceImpl;

import com.gzy.dslab.guide.entity.Point;
import com.gzy.dslab.guide.service.GuideService;
import com.gzy.dslab.guide.service.PointService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

import static java.lang.Math.max;

@Service
public class GuideServiceImpl implements GuideService {

    ShortestRoad sr;

    @Resource
    PointService pointService;

    @Override
    public List<Point> directGuide(int from, int to) {

        return sr.dijkstra(from, to);
    }

    @Override
    public List<Point> byManyPointsGuide(List<Point> passedPoints) {
        // TODO:初步设想的算法是
        // 跑完全图的floyd/dij，然后看始末的路径上有哪个点在必经里，然后以这些必经点分段

        return null;
    }

    @PostConstruct
    private void init() {
        sr=new ShortestRoad(pointService.showPoints(), pointService.showMap());
        new Thread(() -> {
            ShortestRoad tempS=sr;
            int size = pointService.showPoints().size();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < i; j++) {
                    tempS.dijkstra(i, j);
                }
            }
        }).start();
    }
}

class ShortestRoad {
    class Node{
        //从某一点（以map中的下标起始）到达哪个城市，以及到达的距离
        int to,value;
        public Node(int to,int value) {this.to=to;this.value=value;}
    }

    private Map<Integer, Point> points;

    private List<List<Point>> map;
    private List<Point>[][] cachedPaths;


    public ShortestRoad(Map<Integer, Point> points,List<List<Point>> map) {
        this.points = points;
        this.map = map;
        this.cachedPaths = new List[points.size()][points.size()];

        for (int i = 0; i < cachedPaths.length; i++) {
            for (int j = 0; j < cachedPaths[i].length; j++) {
                cachedPaths[i][j]=new ArrayList<>();
                if(i==j) cachedPaths[i][j].add(points.get(i));
            }
        }
    }

    double[][] f;
    public void setFloyd(double[][] f) {
        this.f=f;
    }
    public List<Point> dijkstra(int start ,int end){
        //起点start到各个点的路径是否有缓存
        if(!cachedPaths[start][end].isEmpty()) {
            return cachedPaths[start][end];
        }

        int[] distance=new int[points.size()],used=new int[points.size()];
        PriorityQueue<Node> node=new PriorityQueue<>(Comparator.comparingInt(o -> o.value));

        Arrays.fill(distance,Integer.MAX_VALUE/2);
        node.add(new Node(start,0));
        distance[start]=0;
        while (!node.isEmpty()) {
            //要被用来开始松弛的城市N
            int city = node.poll().to;
            Point from=points.get(city);
            if (used[city] != 0) continue;
            used[city] = 1;
            List<Point> arr = map.get(city);
            //if (arr.isEmpty()&&city!=end) return new ArrayList<>();
            //遍历这个城市的邻边
            for (Point n : arr) {
                int toCity = n.getId();
                if(cachedPaths[city][toCity].isEmpty()) cachedPaths[city][toCity].add(n);
                //如果有哪个相邻的点，满足：从已知的起始点到达该点的方式的距离，大于从起始点到达N再从N到达这个点的距离，就替换到达方式为后者
                if (distance[toCity] > distance[city] + n.getDistance(from)) {
                    distance[toCity] = distance[city] + n.getDistance(from);
                    node.offer(new Node(toCity, distance[toCity]));

                    ArrayList<Point> temp = new ArrayList<>(cachedPaths[start][city]);
                    temp.addAll(cachedPaths[city][toCity]);
                    cachedPaths[start][toCity]=temp;
                }
            }
        }
        cachedPaths[end][start]=cachedPaths[start][end];
        if(distance[end]!=Integer.MAX_VALUE/2) return cachedPaths[start][end];
        else return new ArrayList<>();
        //若返回空表，则为不连通
    }

    public void floydRun(int max) {
        for (int k = 0; k < max; k++)
            for (int i = 0; i < max; i++)
                if (f[i][k] != Integer.MAX_VALUE / 2)
                    for (int j = 0; j < max; j++) {
                        if (f[k][j] != Integer.MAX_VALUE / 2) {
                            f[i][j] = max(f[i][j], f[i][k] * f[k][j]);
                            //f[j][i] = min(f[j][i], f[i][k] + f[k][j]);
                        }
                    }
    }
    public double floydAsk(int i,int j){
        return f[i][j];
    }
}