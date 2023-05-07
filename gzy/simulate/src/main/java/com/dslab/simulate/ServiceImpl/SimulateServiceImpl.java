package com.dslab.simulate.ServiceImpl;

import com.dslab.commonapi.services.SimulateService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@DubboService(group = "DSlab",interfaceClass = SimulateService.class)
public class SimulateServiceImpl implements SimulateService {
	

	private Map<String,simulateThread> threadMap=new HashMap<>();


	@Override
	public boolean startSimulateThread(String user, Date startTime, int simulateSpeed, boolean isInverseSimulate) {
		internStart(user, startTime, simulateSpeed*60*1000, isInverseSimulate);

		return true;
	}

	private synchronized void internStart(String user, Date startTime, int simulateSpeed, boolean isInverseSimulate){
		if(threadMap.containsKey(user)){
			//TODO:look down
			threadMap.get(user).notifyAll();
		}else{
			//TODO
			simulateThread simulateThread = new simulateThread(user,startTime,simulateSpeed,isInverseSimulate);
			threadMap.put(user, simulateThread);
			simulateThread.start();

		}

	}

	@Override
	public boolean startSimulateThread(String user, Date startTime, int simulateSpeed) {
		return startSimulateThread(user, startTime, simulateSpeed*60*1000, false);
	}

	@Override
	public boolean startSimulateThread(String user, Date startTime) {
		return startSimulateThread(user, startTime, 6*60*1000);
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
		try {
			//TODO:look down
			threadMap.get(user).wait();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public void setSimulateSpeed(int sToMin,String user) {
		threadMap.get(user).setSpeed(sToMin*1000);
	}

	@Override
	public Date getUserSimulateTime(String user) {
		return threadMap.get(user).getNow();
	}


	private class simulateThread extends Thread{
		boolean finished=false;
//		boolean stopped=false;

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

		public void setNow(Date now) {
			this.now = now;
		}

		public void setSpeed(int speed) {
			this.speed = speed;
		}

		public void finish() {
			this.finished = true;
		}

		@Override
		public void run() {
			while (!finished){
				//TODO:MAIN FUNC
				//TODO:use stopped flag and while loop instead of wait/notify?





				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				now=new Date(now.getTime()+speed);
			}

		}
	}


}
