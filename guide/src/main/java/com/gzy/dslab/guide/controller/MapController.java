package com.gzy.dslab.guide.controller;

import com.gzy.dslab.guide.entity.Point;
import com.gzy.dslab.guide.service.PointService;
import com.gzy.dslab.guide.vo.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/ds/guide/map")
public class MapController {

	@Resource
	PointService pointService;

	@GetMapping({"/add_point"})
	public Result<List<Point>> addPoint(int x, int y, @RequestParam(required = false,defaultValue = "") String name) {
		boolean r = pointService.addPoint(x, y, name);
		Result<List<Point>> res = r ?
				Result.success("成功加入坐标点") : Result.error();
		return res.data(listAll().getData());
	}

	@GetMapping({"/add_path"})
	public Result<List<Point>> addPath(int aid, int bid) {
		boolean r = pointService.addPath(aid, bid);
		Result<List<Point>> res = r ?
				Result.success("成功加入路径") : Result.error();
		return res.data(listAll().getData());
	}

	@GetMapping({"/show_points"})
	public Result<List<Point>> listAll() {
		return Result.<List<Point>>success("成功获取坐标点列表").data(pointService.listAll());
	}

	@GetMapping({"/show_map"})
	public Result<List<List<Point>>> showMap() {
		return Result.<List<List<Point>>>success("成功获取整个地图").data(pointService.showMap());
	}


}
