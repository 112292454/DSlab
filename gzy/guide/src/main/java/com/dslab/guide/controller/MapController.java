package com.dslab.guide.controller;

import com.dslab.commonapi.entity.Point;
import com.dslab.commonapi.services.PointService;
import com.dslab.commonapi.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/map")
@CrossOrigin
public class MapController {
	 @Autowired
//	@DubboReference(group = "DSlab",version = "1.0.0",interfaceClass = PointService.class,check = false)
	PointService pointService;

	@PostMapping({"/add_point"})
	public Result<List<Point>> addPoint(int x, int y, @RequestParam(required = false,defaultValue = "") String name) {
		assert x>50&&y>50;
		boolean r = pointService.addPoint(x, y, name);
		Result<List<Point>> res = r ?
				Result.success("成功加入坐标点") : Result.error();
		return res.data(listAll().getData());
	}

	@PostMapping({"/add_path"})
	public Result<List<Point>> addPath(int aid, int bid) {
		boolean r = pointService.addPath(aid, bid);
		Result<List<Point>> res = r ?
				Result.success("成功加入路径") : Result.error();
		return res.data(listAll().getData());
	}

	@PostMapping({"/show_points"})
	public Result<List<Point>> listAll() {
		return Result.<List<Point>>success("成功获取坐标点列表").data(
				pointService.listAll().stream().
						filter(point ->!point.getName().startsWith("道路节点"))
						.toList());
	}

	@PostMapping({"/show_map"})
	public Result<List<List<Point>>> showMap() {
		return Result.<List<List<Point>>>success("成功获取整个地图").data(pointService.showMap());
	}

//	@PostMapping({"/drop_all"})
	public Result<String> deleteAll() {
		return Boolean.TRUE.equals(pointService.deleteAll())?
				Result.success("成功清空所有数据"):Result.error("清空数据失败");
	}


}
