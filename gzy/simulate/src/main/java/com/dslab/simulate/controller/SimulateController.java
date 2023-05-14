package com.dslab.simulate.controller;

import com.dslab.commonapi.services.SimulateService;
import com.dslab.commonapi.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller("/simulate")
public class SimulateController {

	@Autowired
	SimulateService simulateService;

	@PostMapping("start")
	public void doSimulate(String userId,
	                       @RequestParam(required = false,defaultValue = "0") Date nowTime,
	                       @RequestParam(required = false,defaultValue = "6") int timeSpeed){
		//输入时间可能换成long的毫秒
//		Date now= StringUtils.isBlank(nowTime)?new Date():new Date(nowTime);
		simulateService.startSimulateThread(userId, nowTime,timeSpeed);

	}

	@PostMapping("stop")
	public Result<?> stopSimulate(String userId){
		Result<?> result = simulateService.stopSimulate(userId) ? Result.success() : Result.error();
		return result;
	}

	@PostMapping("restore")
	public Result<?> restoreSimulate(String userId){
		Result<?> result = simulateService.startSimulateThread(userId,new Date()) ? Result.success() : Result.error();
		return result;
	}

	@PostMapping("faster")
	public Result<?> faster(String userId){
		simulateService.setSimulateSpeed((int) (simulateService.getUserSimulateSpeed(userId)*1.5),userId);
		return Result.success();
	}

	@PostMapping("slower")
	public Result<?> slower(String userId){
		simulateService.setSimulateSpeed((int) (simulateService.getUserSimulateSpeed(userId)*0.75),userId);
		return Result.success();
	}


	@PostMapping("reset")
	public Result<?> resetSimulate(String userId){
		Result<?> result = simulateService.resetSimulate(userId) ? Result.success() : Result.error();
		return result;
	}


	@PostMapping("finish")
	public void finishSimulate(String userId){
		simulateService.finishSimulate(userId);
	}
}
