package com.dslab.simulate.ServiceImpl;

import com.dslab.commonapi.entity.Event;
import com.dslab.commonapi.services.EventService;
import com.dslab.commonapi.services.SimulateService;
import com.dslab.commonapi.vo.Result;
import com.dslab.simulate.util.WebsocketUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public boolean finishSimulate(String user) {
		threadMap.get(user).finish();
		return true;
	}

	@Override
	public boolean resetSimulate(String user, Date resetTime) {
		threadMap.get(user).setNow(resetTime);
		return true;
	}

	@Override
	public boolean resetSimulate(String user) {
		return resetSimulate(user, new Date());
	}

	@Override
	public boolean stopSimulate(String user) {
		threadMap.get(user).setStop();
		return true;
	}

	@Override
	public void setSimulateSpeed(int sToMin,String user) {
		threadMap.get(user).setSpeed(sToMin*60*1000);
	}

	@Override
	public void setSimulateInv(String user, boolean isInv) {
		threadMap.get(user).setInverseSimulate(isInv);
	}

	@Override
	public Date getUserSimulateTime(String user) {
		simulateThread simulateThread = threadMap.get(user);
		if(simulateThread==null||simulateThread.getNow()==null){
			return new Date();
		}
		return simulateThread.getNow();
	}

	@Override
	public int getUserSimulateSpeed(String user) {
		return threadMap.get(user).getSpeed()/1000;
	}
	private class simulateThread extends Thread{
		boolean finished=false;
		boolean stopped=false;

		String user;

		Date now;

		int speed;

		boolean isInverseSimulate;

		public simulateThread(String user, Date now, int speed, boolean isInverseSimulate) {
			this.user = user;
			this.now = now;
			this.speed = speed;
			this.isInverseSimulate = isInverseSimulate;
		}

		public Date getNow() {
			return now;
		}

		public int getSpeed() {
			return speed;
		}

		public void setNow(Date now) {
			this.now = now;
		}

		public void setSpeed(int speed) {
			this.speed = speed;
		}

		public void setStop() {
			this.stopped = true;
		}

		public void setInverseSimulate(boolean inverseSimulate) {
			isInverseSimulate = inverseSimulate;
		}

		public void restore() {
			this.stopped = false;
		}

		public void finish() {
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
						Result<List<Event>> result = eventService.checkUserEventInTime(now, user);
						WebsocketUtil.sendMessage(user, result);

						//每过1s跳动一下时间
						Thread.sleep(1000);
						now = new Date(now.getTime() + speed*(isInverseSimulate?-1:1));
					}
					//若被暂停，每0.5s查询一次是否恢复
					Thread.sleep(500);
				}
			} catch (InterruptedException e) {
				WebsocketUtil.sendMessage(user, Result.error("模拟出现异常，现已中止！").data(e));
				e.printStackTrace();
			}
		}
	}


}
