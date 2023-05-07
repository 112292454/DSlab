package com.dslab.commonapi.services;

import java.util.Date;

public interface SimulateService {

	boolean startSimulateThread(String user, Date startTime,int simulateSpeed,boolean isInverseSimulate);

	boolean startSimulateThread(String user, Date startTime,int simulateSpeed);

	boolean startSimulateThread(String user, Date startTime);

	boolean finishSimulate(String user);

	boolean resetSimulate(String user,Date resetTime);

	boolean resetSimulate(String user);

	boolean stopSimulate(String user);

	void setSimulateSpeed(int sToMin,String user);

	Date getUserSimulateTime(String user);


}
