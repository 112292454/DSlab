package com.dslab.server.guide.controller;

import com.dslab.commonapi.entity.Point;
import com.dslab.commonapi.services.GuideService;
import com.dslab.commonapi.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ds/guide")
@CrossOrigin
public class GuideController {

	@Autowired
	GuideService guideService;

	//像素点转换到实际距离（米）的系数
	private static final double PIXEL_2_METER_ARG=0.25;

	@GetMapping({"/p2p"})
	public Result<List<Point>> P2Pguide(int from, int to){
		List<Point> guidePaths = guideService.directGuide(from, to);
		int len=0;
		for (int i = 0; i < guidePaths.size()-1; i++) {
			len+=guidePaths.get(i).getDistance(guidePaths.get(i+1));
		}
		len*=PIXEL_2_METER_ARG;

		Result<List<Point>> res=guidePaths.isEmpty()?
				Result.error("导航失败！"):Result.success("导航成功，路线长度约"+len+"米");
		res.data(guidePaths);
		return res;
	}

}
