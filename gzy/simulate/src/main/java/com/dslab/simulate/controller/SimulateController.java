package com.dslab.simulate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller("/simulate")
public class SimulateController {


	@PostMapping("start")
	public void doSimulate(String userId,
	                       @RequestParam(required = false,defaultValue = "0") Date nowTime,
	                       @RequestParam(required = false,defaultValue = "6") int timeSpeed){
		//输入时间可能换成long的毫秒
//		Date now= StringUtils.isBlank(nowTime)?new Date():new Date(nowTime);


	}

	@PostMapping("finish")
	public void finishSimulate(String userId){

	}
}
