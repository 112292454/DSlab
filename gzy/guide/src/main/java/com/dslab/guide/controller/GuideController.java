package com.dslab.guide.controller;

import com.dslab.commonapi.entity.Point;
import com.dslab.commonapi.entity.byManyPostBody;
import com.dslab.commonapi.services.GuideService;
import com.dslab.commonapi.services.PointService;
import com.dslab.commonapi.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/guide")
@CrossOrigin
public class GuideController {

	@Autowired
	GuideService guideService;

	@Autowired
	PointService pointService;

	//像素点转换到实际距离（米）的系数
	private static final double PIXEL_2_METER_ARG = 0.25;
	//TODO：根据前端图的像素大小确定比例尺

	@PostMapping({"/p2p"})
	public Result<List<Point>> P2Pguide(int x1, int y1,int x2,int y2) {
		Point from = pointService.getByPos(x1, y1);
		Point to = pointService.getByPos(x2, y2);

		List<Point> guidePaths = guideService.directGuide(from.getId(), to.getId());
		return getResult(guidePaths);
	}


	@PostMapping({"/by_many"})
	//注意，传入参数中，第一个点是起始点的id，后面的是需要经过的点的id，不用把最终回到原点的需求也写进来
	public Result<List<Point>> byManyguide(@RequestBody byManyPostBody pointdata) {
		List<Integer> pointIds = pointdata.getPointdata().stream()
				.filter(a->a.getX()>50&&a.getY()>50)
				.map(a -> pointService.getByPos(a.getX(), a.getY()).getId())
				.collect(Collectors.toList());
		List<Point> guidePaths = guideService.byManyPointsGuide(pointIds);
		return getResult(guidePaths);
	}

	private Result<List<Point>> getResult(List<Point> guidePaths) {
		int len = 0;
		for (int i = 0; i < guidePaths.size() - 1; i++) {
			len += guidePaths.get(i).getDistance(guidePaths.get(i + 1));
		}
		len *= PIXEL_2_METER_ARG;

		Result<List<Point>> res = guidePaths.size()<=1 ?
				Result.error("导航失败！") : Result.success("导航成功，路线长度约" + len + "米");
		res.data(guidePaths);
		return res;
	}
}