package com.dslab.simulate.controller;

import com.dslab.commonapi.services.PointService;
import com.dslab.commonapi.services.SimulateService;
import com.dslab.commonapi.vo.Result;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController("/simulate")
@RequestMapping("/simulate")
public class SimulateController {
	@Autowired
	SimulateService simulateService;

	@DubboReference(group = "DSlab",version = "1.0.0",interfaceClass = PointService.class,check = false)
	PointService pointService;

	@PostMapping("/start")
	public Result<?> doSimulate(String userId,
	                       @RequestParam(required = false,defaultValue = "0") Date nowTime,
	                       @RequestParam(required = false,defaultValue = "6") int timeSpeed){
		//输入时间可能换成long的毫秒
		simulateService.startSimulateThread(userId, nowTime,timeSpeed);
		return Result.success(String.format("已开始用户%s的模拟",userId));
	}

	@PostMapping("/test")
	public Result<?> dubboTestTemp(){
		System.out.println("test");
		return Result.success("succ").data(pointService.listAll());
	}

	@PostMapping("/stop")
	public Result<?> stopSimulate(String userId){
		return simulateService.stopSimulate(userId) ? Result.success(String.format("已暂停用户%s的模拟",userId)) : Result.error();
	}

	@PostMapping("/restore")
	public Result<Date> restoreSimulate(String userId){
		return simulateService.startSimulateThread(userId,new Date()) ? Result.<Date>success(String.format("已恢复用户%s的模拟",userId)).data(new Date()) : Result.error();
	}

	@PostMapping("/faster")
	public Result<?> faster(String userId){
		simulateService.setSimulateSpeed((int) (simulateService.getUserSimulateSpeed(userId)*1.5),userId);
		return Result.success(String.format("已加速用户%s的模拟速度至“10秒=%.2f小时”",userId,simulateService.getUserSimulateSpeed(userId) / 360.0));
	}

	@PostMapping("slower")
	public Result<?> slower(String userId){
		simulateService.setSimulateSpeed((int) (simulateService.getUserSimulateSpeed(userId)*0.75),userId);
		return Result.success(String.format("已减速用户%s的模拟速度至“10秒=%.2f小时”",userId,simulateService.getUserSimulateSpeed(userId) / 360.0));
	}

	@PostMapping("/reset")
	public Result<?> resetSimulate(String userId){
		return simulateService.resetSimulate(userId) ? Result.success(String.format("已重置用户%s的模拟",userId)) : Result.error();
	}

	@PostMapping("/finish")
	public Result<?> finishSimulate(String userId){
		simulateService.finishSimulate(userId);
		return Result.success(String.format("已结束用户%s的模拟",userId));
	}
}
