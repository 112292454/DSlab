package com.dslab.commonapi.services;

import java.util.Date;

/**
 * 模拟用户时间推进的服务
 *
 * @Author Guo
 * @CreateTime 2023-05-09 03:26
 */
public interface SimulateService {

	/**
	 * 开始模拟线程
	 *
	 * @param user 用户id
	 * @param startTime 模拟开始的时间
	 * @param simulateSpeed 模拟的倍率，单位同set，为1s对应多少min
	 * @param isInverseSimulate 是否反向模拟：即是否时间倒退
	 * @Author Guo
	 * @CreateTime 2023-05-09 03:18
	 * @Return boolean
	 */
	boolean startSimulateThread(String user, Date startTime, int simulateSpeed, boolean isInverseSimulate);

	/**
	 * 开始模拟线程
	 *
	 * @param user 用户id
	 * @param startTime 模拟开始的时间
	 * @param simulateSpeed 模拟的倍率，单位同set，为1s对应多少min
	 * @Author Guo
	 * @CreateTime 2023-05-09 03:18
	 * @Return boolean
	 */
	boolean startSimulateThread(String user, Date startTime, int simulateSpeed);

	/**
	 * 开始模拟线程
	 *
	 * @param user 用户id
	 * @param startTime 模拟开始的时间
	 * @Author Guo
	 * @CreateTime 2023-05-09 03:18
	 * @Return boolean
	 */
	boolean startSimulateThread(String user, Date startTime);


	/**
	 * 重置某用户模拟的状态到输入的时间
	 *
	 * @param user 用户id
	 * @param resetTime 将要重置到的时间
	 * @Author Guo
	 * @CreateTime 2023-05-09 03:19
	 * @Return boolean
	 */
	boolean resetSimulate(String user, Date resetTime);

	/**
	 * 重置模拟状态
	 *
	 * @param user 用户id
	 * @Author Guo
	 * @CreateTime 2023-05-09 03:19
	 * @Return boolean
	 */
	boolean resetSimulate(String user);

	/**
	 * 暂停模拟，可调用start（user）恢复
	 *
	 * @param user 用户id
	 * @Author Guo
	 * @CreateTime 2023-05-09 03:19
	 * @Return boolean
	 */
	boolean stopSimulate(String user);

	/**
	 * 彻底中止该用户的模拟
	 *
	 * @param user 用户id
	 * @Author Guo
	 * @CreateTime 2023-05-09 03:19
	 * @Return boolean
	 */
	boolean finishSimulate(String user);

	/**
	 * 设置用户模拟的倍速
	 *
	 * @param sToMin 单位为一秒对应多少分钟
	 * @param user 用户id 
	 * @Author Guo
	 * @CreateTime 2023-05-09 03:20
	 * @Return void
	 */
	void setSimulateSpeed(int sToMin, String user);

	void setSimulateInv(String user, boolean isInv);

	/**
	 * 获取该用户当前模拟到的时间
	 *
	 * @param user 用户id 用户id
	 * @Author Guo
	 * @CreateTime 2023-05-09 03:20
	 * @Return java.util.Date
	 */
	Date getUserSimulateTime(String user);

	int getUserSimulateSpeed(String user);


}