package com.dslab.guide.serviceImpl;

import com.dslab.commonapi.dataStruct.ShortestRoad;
import com.dslab.commonapi.entity.Point;
import com.dslab.commonapi.services.GuideService;
import com.dslab.commonapi.services.PointService;
import jakarta.annotation.PostConstruct;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@DubboService(group = "DSlab",interfaceClass = GuideService.class)
public class GuideServiceImpl implements GuideService {

    ShortestRoad sr;

    @Autowired
    PointService pointService;

    @Override
    public List<Point> directGuide(int from, int to) {
        List<Point> dijkstra = sr.dijkstra(from, to);
        dijkstra.add(0, sr.getPoint(from));
        return dijkstra;
    }

    @Override
    public List<Point> byManyPointsGuide(List<Integer> passedPoints) {
        if(passedPoints.isEmpty()) return new ArrayList<>();
        Point start=sr.getPoint(passedPoints.get(0));

        List<Point> res=new ArrayList<>();
        res.add(start);
        int passedSize = passedPoints.size();

        if(passedSize >18){
            //如果size大于20，就采取近似解，通过搜索剪枝来确定路径
            while (!passedPoints.isEmpty()){
                Point now=res.get(res.size()-1);
                 Point temp = new Point();
                int min = 1 << 30,rmi=-1;
                for (int i = 0; i < passedPoints.size(); i++) {
                    int a=passedPoints.get(i);
                    if(sr.floydAsk(now.getId(), a)< min){
                        min =sr.floydAsk(now.getId(), a);
                        temp =sr.getPoint(a);
                        rmi=i;
                    }
                }
                passedPoints.remove(rmi);
                res.add(temp);
            }
            res.add(start);
        }else{
            //如果size小于20，使用哈密顿回路寻找到精确解，状压dp
            int[][] dp=new int[1<<passedSize][passedSize];
            List<Point>[] atPointPaths=new List[passedSize];//不论走过了什么点，怎么走的，存储最后位于k点的中距离最短的走法
            int[] atPointDist=new int[passedSize];
            for (int i = 0; i < atPointDist.length; i++) {
                atPointPaths[i]=new ArrayList<>();
            }
            for (int i = 0; i < dp.length; i++) {
                Arrays.fill(dp[i], Integer.MAX_VALUE/2);
            }
            dp[1][0]=0;


            for(int i=0;i<(1<<passedSize);i++) {//i代表的是一个方案的集合，其中每个位置的0/1代表没有/有经过这个点
                for(int j=0;j<passedSize;j++) {//枚举当前在哪个点
                    if(((i>>j)&1)!=0) {//如果i代表的状态中有j，也就是可以表示“经过了i中bit为1的点，且当前处于j点”
                        for(int k=0;k<passedSize;k++) {//枚举所有可以走到到达j的点
                            if((i-(1<<j)>>k&1)!=0) {//在i状态中，走到j这个点之前，是否可以停在k点。如果是，才能从k转移到j
                                int dist = sr.floydAsk(sr.getPoint(passedPoints.get(k)).getId(), sr.getPoint(passedPoints.get(j)).getId());
                                if(dp[i-(1<<j)][k]+dist<dp[i][j]){//如果从k走到j比原先的更短
                                    dp[i][j]=dp[i-(1<<j)][k]+ dist;
                                    atPointPaths[j]=new ArrayList<>(atPointPaths[k]);//那么走到j点的路径就必然是走到k点，再到j的
                                    while(atPointPaths[j].remove(sr.getPoint(passedPoints.get(j))));
                                    atPointPaths[j].add(sr.getPoint(passedPoints.get(j)));//atPP存储了从起点到达每一个点的路径
                                    atPointDist[j]=atPointDist[k]+dist;
                                }
                            }
                        }
                    }
                }
            }
            //计算完成了遍历需要pass的所有点的距离，也就是得到了所有的哈密顿路径值，然后还需要走回到出发点（由于项目要求）
            int min=0;
            for (int i = 0; i < atPointPaths.length; i++) {
                if(atPointPaths[i].isEmpty()) continue;
                atPointDist[i]+=sr.floydAsk(atPointPaths[i].get(atPointPaths[i].size()-1).getId(),start.getId());//获得再回到start的距离
                atPointPaths[i].add(start);
                if(atPointDist[i]>atPointDist[min]) min=i;
            }
            res=atPointPaths[min];
        }
        res.add(0, start);

        List<Point> expandRes=new ArrayList<>();
        for (int i = 1; i < res.size(); i++) {
            List<Point> temp = directGuide(res.get(i - 1).getId(), res.get(i).getId());
            temp.remove(0);
            expandRes.addAll(temp);
        }
        expandRes.add(0, start);

        for (Integer passedPoint : passedPoints) {
            if(passedPoint!=0&&!expandRes.contains(sr.getPoint(passedPoint))) {
                expandRes=expandRes.subList(0, 1);
                break;
            }
        }

        return expandRes;
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

