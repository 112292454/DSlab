package com.dslab.simulate.ServiceImpl;

import com.dslab.commonapi.entity.Event;
import com.dslab.commonapi.entity.Point;
import com.dslab.commonapi.services.EventService;
import com.dslab.commonapi.services.GuideService;
import com.dslab.commonapi.services.PointService;
import com.dslab.commonapi.services.SimulateService;
import com.dslab.commonapi.utils.TimeUtil;
import com.dslab.commonapi.vo.Result;
import com.dslab.simulate.util.WebSocketUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@DubboService(group = "DSlab",interfaceClass = SimulateService.class)
public class SimulateServiceImpl implements SimulateService {

	Logger log = LoggerFactory.getLogger(SimulateService.class);
	private Map<String,simulateThread> threadMap=new HashMap<>();

	@DubboReference(group = "DSlab",interfaceClass = EventService.class,check = false)
	EventService eventService;

	@DubboReference(group = "DSlab",interfaceClass = GuideService.class,check = false)
	GuideService guideService;

	@DubboReference(group = "DSlab",interfaceClass = PointService.class,check = false)
	PointService pointService;


	@Override
	public boolean startSimulateThread(String user, Date startTime, int simulateSpeed, boolean isInverseSimulate) {
		internStart(user, startTime, simulateSpeed*60*1000, isInverseSimulate);
		return true;
	}

	private synchronized void internStart(String user, Date startTime, int simulateSpeed, boolean isInverseSimulate){
		if(startTime==null) startTime=new Date();
		if(threadMap.containsKey(user)){
			threadMap.get(user).setInverseSimulate(isInverseSimulate);
			threadMap.get(user).restore();
			log.info("从暂停中恢复模拟，用户：{}，当前模拟时间：{}",user,startTime);
		}else{
			simulateThread simulateThread = new simulateThread(user,startTime,simulateSpeed,isInverseSimulate);
			threadMap.put(user, simulateThread);
			simulateThread.start();
			log.info("开始模拟，用户：{}，初始模拟时间：{}",user,startTime);
		}
	}

	@Override
	public boolean startSimulateThread(String user, Date startTime, int simulateSpeed) {
		return startSimulateThread(user, startTime, simulateSpeed, false);
	}

	@Override
	public boolean startSimulateThread(String user, Date startTime) {
		return startSimulateThread(user, startTime, 6);
	}

	@Override
	public boolean containsSimulateThread(String user) {
		return threadMap.containsKey(user);
	}

	@Override
	public boolean finishSimulate(String user) {
		if(!containsSimulateThread(user)) return false;
		threadMap.get(user).finish();
		threadMap.remove(user);
		log.info("中止用户{}的模拟",user);
		return true;
	}

	@Override
	public boolean resetSimulate(String user, Date resetTime) {
		if(!containsSimulateThread(user)) return false;
		threadMap.get(user).setNow(resetTime);
		log.info("设置用户{}的模拟时间为{}",user,resetTime);
		return true;
	}

	@Override
	public boolean resetSimulate(String user) {
		return resetSimulate(user, new Date());
	}

	@Override
	public boolean inverseSimulate(String user) {
		if(!containsSimulateThread(user)) return false;
		threadMap.get(user).inverseSimulate();
		log.info("调转用户{}模拟时间方向，当前为：{}",user,threadMap.get(user).isInverseSimulate?"正常":"反向");
		return true;
	}


	@Override
	public boolean stopSimulate(String user) {
		if(!containsSimulateThread(user)) return false;
		threadMap.get(user).setStop();
		log.info("暂停用户{}的模拟",user);
		return true;
	}

	@Override
	public void setSimulateSpeed(double sToMin, String user) {
		if(!containsSimulateThread(user)) return;
		threadMap.get(user).setSpeed(sToMin*60*1000);
		log.info("设置用户{}的模拟速度为1秒={}小时",user,threadMap.get(user).getSpeed()/1000/60/60);
	}

	@Override
	public void setSimulateInv(String user, boolean isInv) {
		if(!containsSimulateThread(user)) return;
		threadMap.get(user).setInverseSimulate(isInv);
		log.info("设置用户反向模拟");
	}

	@Override
	public Date getUserSimulateTime(String user) {
//		if(!containsSimulateThread(user)) throw new RuntimeException("此用户未开始模拟！");
		simulateThread thr = threadMap.getOrDefault(user, new simulateThread("-1", new Date(), -1, false));
		log.info("查询用户{}当前模拟时间，为：{}",user,thr.getNow());
		return thr.getNow();
	}

	@Override
	public double getUserSimulateSpeed(String user) {
		if(!containsSimulateThread(user)) throw new RuntimeException("此用户未开始模拟！");
		return threadMap.get(user).getSpeed()/1000/60;
	}


	private class simulateThread extends Thread{
		boolean finished=false;
		boolean stopped=false;

		String user;

		Date now;

		//1秒对应speed毫秒
		double speed;

		boolean isInverseSimulate;

		int nowPlace;

		public simulateThread(String user, Date now, double speed, boolean isInverseSimulate) {
			this.user = user;
			this.now = now;
			this.speed = speed;
			this.isInverseSimulate = isInverseSimulate;
			this.nowPlace = 13;//学五宿舍楼的id，即模拟的用户默认在学五，然后遇到课程了就去各个课/活动，再以去了的位置作为下一次的起始位置
		}

		public Date getNow() {
			return now;
		}

		public double getSpeed() {
			return speed;
		}

		public void setNow(Date now) {
			this.now = now;
		}

		public void setSpeed(double speed) {
			this.speed = speed;
		}

		public void setStop() {
			this.stopped = true;
		}

		public void setInverseSimulate(boolean inverseSimulate) {
			isInverseSimulate = inverseSimulate;
		}

		public void inverseSimulate() {
			isInverseSimulate = !isInverseSimulate;
		}

		public void restore() {
			this.stopped = false;
		}

		public void finish() {
			this.stopped=true;
			this.finished = true;
		}

		@Override
		public void run() {
			int nowHour=0;
			boolean checked = false;
			try {
//				WebSocketUtil.send(user, "用户"+user+"已成功连接课设ws后端！");
				while (!finished) {
					while (!stopped) {
						//查询应该发送什么提醒
						//这个消息的内容格式应该是和前端约定一下：socket收到这种消息就给用户弹一个提示，
						// 比方说result的{code=201，data=[课程a、课程b]}之类
						Result<List<Event>> result = Result.<List<Event>>success("成功获取接下来待提醒的课程信息").
								data(eventService.checkUserEventInTime(now, user));

						Result<Date> timeStamp = Result.<Date>success("当前时间").data(now).setStatusCode(201);

						boolean isOnline = WebSocketUtil.send(user, timeStamp);

						if(!isOnline){
							log.info("用户{}已断开ws连接，自动中止模拟",user);
							this.finish();
							threadMap.remove(user);
						}else if(!result.getData().isEmpty()) {
							if(nowHour!=TimeUtil.dateToHour(now)){
								log.info("用户{}有课程待提醒，推送课程信息和导航路径",user);
								WebSocketUtil.send(user, result);
								Result<List<Point>> pathResult = getPathResult(result.getData());
								WebSocketUtil.send(user,pathResult);
							}



						}
						nowHour= TimeUtil.dateToHour(now);
						//每过1s跳动一下时间
						Thread.sleep(1000);
						now = new Date((long) (now.getTime() + speed*(isInverseSimulate?-1:1)));
					}
					//若被暂停，每0.5s查询一次是否恢复
					Thread.sleep(500);
				}
			} catch (InterruptedException e) {
				WebSocketUtil.send(user, Result.error("模拟出现异常，现已中止！").data(e));
				e.printStackTrace();
			}
		}

		private Result<List<Point>> getPathResult(List<Event> result) {
			List<Integer> placeIds=new ArrayList<>();
			placeIds.add(nowPlace);
			result.stream().filter(a->!a.getIsOnline()).filter(a->{
				Point p = pointService.getByName(a.getBuildingName());
				return p!=null&&p.getId()>0&&p.getId()<250&&p.getX()>50&&p.getY()>50;
			}).forEach(a-> placeIds.add(pointService.getByName(a.getBuildingName()).getId()));
			if(placeIds.size()>2){
				placeIds.clear();
				placeIds.add(nowPlace);
				for (int i = 0; i < 4; i++) {
					placeIds.add(new Random().nextInt(50)+20);
				}
			}

			List<Point> guidePaths = placeIds.size()>2?
					guideService.byManyPointsGuide(placeIds)
					:guideService.directGuide(placeIds.get(0),placeIds.get(1));
			nowPlace=guidePaths.size()>2?
					guidePaths.get(guidePaths.size()-1).getId()
					:13;
			return Result.<List<Point>>success("已为当前提醒的课程导航成功").data(guidePaths).setStatusCode(202);
		}
	}

}
