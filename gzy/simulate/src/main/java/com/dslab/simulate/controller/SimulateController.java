package com.dslab.simulate.controller;

import com.dslab.commonapi.services.PointService;
import com.dslab.commonapi.services.SimulateService;
import com.dslab.commonapi.vo.Result;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController("/simulate")
@RequestMapping("/simulate")
@CrossOrigin
public class SimulateController {
	@Autowired
	SimulateService simulateService;

	@DubboReference(group = "DSlab",version = "1.0.0",interfaceClass = PointService.class,check = false)
	PointService pointService;

	@PostMapping("/start")
	public Result<?> doSimulate(String uid,
	                       @RequestParam(required = false) Date nowTime,
	                       @RequestParam(required = false,defaultValue = "6") int timeSpeed){
		//输入时间可能换成long的毫秒
		simulateService.startSimulateThread(uid, nowTime,timeSpeed);
		return Result.success(String.format("已开始用户%s的模拟",uid));
	}

	@PostMapping("/test")
	public Result<?> dubboTestTemp(){
		System.out.println("test");
		return Result.success("succ").data(pointService.listAll());
	}

	@PostMapping("/stop")
	public Result<?> stopSimulate(String uid){
		return simulateService.stopSimulate(uid) ? Result.success(String.format("已暂停用户%s的模拟",uid)) : Result.error();
	}

	@PostMapping("/restore")
	public Result<Date> restoreSimulate(String uid){
		if(!simulateService.containsSimulateThread(uid)) return Result.error("用户尚未开始模拟，无法继续");
		return simulateService.startSimulateThread(uid,new Date()) ? Result.<Date>success(String.format("已恢复用户%s的模拟",uid)).data(new Date()) : Result.error();
	}

	@PostMapping("/faster")
	public Result<?> faster(String uid){
		simulateService.setSimulateSpeed(simulateService.getUserSimulateSpeed(uid)*1.5,uid);
		return Result.success(String.format("已加速用户%s的模拟速度至“1秒=%.3f小时”",uid,simulateService.getUserSimulateSpeed(uid) / 60.0));
	}

	@PostMapping("slower")
	public Result<?> slower(String uid){
		simulateService.setSimulateSpeed(simulateService.getUserSimulateSpeed(uid)*0.75,uid);
		return Result.success(String.format("已减速用户%s的模拟速度至“1秒=%.3f小时”",uid,simulateService.getUserSimulateSpeed(uid) / 60.0));
	}

	@PostMapping("/reset")
	public Result<?> resetSimulate(String uid,@RequestParam(required = false) Date time){
		if(time==null) time=new Date();
		return simulateService.resetSimulate(uid,time) ? Result.success(String.format("已重置用户%s的模拟",uid)) : Result.error();
	}

	@PostMapping("/inverse")
	public Result<?> inverseSimulate(String uid){
		return simulateService.inverseSimulate(uid) ? Result.success(String.format("已翻转用户%s的模拟时间流向",uid)) : Result.error();
	}

	@PostMapping("/finish")
	public Result<?> finishSimulate(String uid){
		simulateService.finishSimulate(uid);
		return Result.success(String.format("已结束用户%s的模拟",uid));
	}
}
