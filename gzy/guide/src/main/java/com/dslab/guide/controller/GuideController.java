package com.dslab.guide.controller;

import com.dslab.commonapi.entity.Point;
import com.dslab.commonapi.services.GuideService;
import com.dslab.commonapi.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ds/guide")
@CrossOrigin
public class GuideController {

	@Autowired
	GuideService guideService;

	//像素点转换到实际距离（米）的系数
	private static final double PIXEL_2_METER_ARG = 0.25;

	@GetMapping({"/p2p"})
	public Result<List<Point>> P2Pguide(int from, int to) {
		List<Point> guidePaths = guideService.directGuide(from, to);
		return getResult(guidePaths);
	}


	@GetMapping({"/by_many"})
	//注意，传入参数中，第一个点是起始点的id，后面的是需要经过的点的id，不用把最终回到原点的需求也写进来
	public Result<List<Point>> byManyguide(@RequestBody List<Integer> points) {
		List<Point> guidePaths = guideService.byManyPointsGuide(points);
		return getResult(guidePaths);
	}

	private Result<List<Point>> getResult(List<Point> guidePaths) {
		int len = 0;
		for (int i = 0; i < guidePaths.size() - 1; i++) {
			len += guidePaths.get(i).getDistance(guidePaths.get(i + 1));
		}
		len *= PIXEL_2_METER_ARG;

		Result<List<Point>> res = guidePaths.isEmpty() ?
				Result.error("导航失败！") : Result.success("导航成功，路线长度约" + len + "米");
		res.data(guidePaths);
		return res;
	}
}