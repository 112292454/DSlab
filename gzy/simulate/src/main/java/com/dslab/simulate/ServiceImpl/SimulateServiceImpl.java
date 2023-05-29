package com.dslab.simulate.ServiceImpl;

import com.dslab.commonapi.entity.Event;
import com.dslab.commonapi.services.EventService;
import com.dslab.commonapi.services.SimulateService;
import com.dslab.commonapi.vo.Result;
import com.dslab.simulate.util.WebSocketUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@DubboService(group = "DSlab",interfaceClass = SimulateService.class)
public class SimulateServiceImpl implements SimulateService {


	private Map<String,simulateThread> threadMap=new HashMap<>();

	@DubboReference(group = "DSlab",interfaceClass = EventService.class,check = false)
	EventService eventService;


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
		}else{
			simulateThread simulateThread = new simulateThread(user,startTime,simulateSpeed,isInverseSimulate);
			threadMap.put(user, simulateThread);
			simulateThread.start();

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
		return true;
	}

	@Override
	public boolean resetSimulate(String user, Date resetTime) {
		if(!containsSimulateThread(user)) return false;
		threadMap.get(user).setNow(resetTime);
		return true;
	}

	@Override
	public boolean resetSimulate(String user) {
		return resetSimulate(user, new Date());
	}

	@Override
	public boolean inverseSimulate(String user) {
		threadMap.get(user).inverseSimulate();
		return true;
	}


	@Override
	public boolean stopSimulate(String user) {
		if(!containsSimulateThread(user)) return false;
		threadMap.get(user).setStop();
		return true;
	}

	@Override
	public void setSimulateSpeed(double sToMin, String user) {
		if(!containsSimulateThread(user)) return;
		threadMap.get(user).setSpeed(sToMin*60*1000);
	}

	@Override
	public void setSimulateInv(String user, boolean isInv) {
		if(!containsSimulateThread(user)) return;
		threadMap.get(user).setInverseSimulate(isInv);
	}

	@Override
	public Date getUserSimulateTime(String user) {
//		if(!containsSimulateThread(user)) throw new RuntimeException("此用户未开始模拟！");
		return threadMap.get(user).getNow();
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

		double speed;

		boolean isInverseSimulate;

		public simulateThread(String user, Date now, double speed, boolean isInverseSimulate) {
			this.user = user;
			this.now = now;
			this.speed = speed;
			this.isInverseSimulate = isInverseSimulate;
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
			try {
				while (!finished) {
					while (!stopped) {
						//查询应该发送什么提醒
						//这个消息的内容格式应该是和前端约定一下：socket收到这种消息就给用户弹一个提示，
						// 比方说result的{code=201，data=[课程a、课程b]}之类
//						Result<List<Event>> result = eventService.checkUserEventInTime(now, user);
						Result<List<Event>> result = Result.<List<Event>>success().data(new ArrayList<>());
						if(new Random().nextInt(10)==8) result.getData().add(new Event("webSocket测试样例"));

						Result<Date> timeStamp = Result.<Date>success("当前时间").data(now);
						result.setStatusCode(200);
						timeStamp.setStatusCode(201);


						WebSocketUtil.send(user, timeStamp);
						if(!result.getData().isEmpty()) WebSocketUtil.send(user, result);

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
	}


}
