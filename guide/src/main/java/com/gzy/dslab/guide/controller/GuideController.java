package com.gzy.dslab.guide.controller;

import com.gzy.dslab.guide.entity.Point;
import com.gzy.dslab.guide.service.GuideService;
import com.gzy.dslab.guide.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/ds/guide")
public class GuideController {

	@Resource
	GuideService guideService;

	//像素点转换到实际距离（米）的系数
	private static final int PIXEL_2_METER_ARG=1;

	@GetMapping({"/p2p"})
	public Result<List<Point>> P2Pguide(int from, int to){
		List<Point> guidePaths = guideService.directGuide(from, to);
		int len=0;
		for (int i = 0; i < guidePaths.size()-1; i++) {
			len+=guidePaths.get(i).getDistance(guidePaths.get(i+1));
		}
		len*=PIXEL_2_METER_ARG;

		Result<List<Point>> res=guidePaths.isEmpty()?
				Result.error("导航失败！"):Result.success("导航成功，路径长度"+len);
		res.data(guidePaths);
		return res;
	}

}
