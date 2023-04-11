package com.dslab.server.guide.dao;

import com.dslab.commonapi.entity.Point;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Mapper
@Repository("PointMapper")
public class PointMapper {

	@Resource
	private RedisTemplate<String, Point> redisTemplate;
	private RedisSerializer<String> keySerializer = new StringRedisSerializer();
	private Jackson2JsonRedisSerializer<Point> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Point.class);

	//TODO：若泛化，修改这里的key为set的值
	private static final String _KEY="DSlab_map_points";

	public PointMapper(RedisTemplate redisTemplate) {
		this.redisTemplate = (RedisTemplate<String, Point>) redisTemplate;
		this.redisTemplate.setKeySerializer(keySerializer);
		this.redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
	}


	public List<Point> getAllPoints() {
		ListOperations<String, Point> ops = redisTemplate.opsForList();
		List<Point> Points = ops.range(_KEY, 0, -1);
		//Points=Points.stream().filter(a->a.getId()>0).collect(Collectors.toList());
		return Points;
	}

	public boolean setPoint(int index,Point point){
		ListOperations<String, Point> ops = redisTemplate.opsForList();
		Long size = ops.size(_KEY);
		if(size!=null&&size!=0&&size>index) ops.set(_KEY,index,point);
		else ops.rightPush(_KEY, point);
		return true;
	}


	public Boolean deleteAll() {
		ValueOperations<String, Point> ops = redisTemplate.opsForValue();
		return redisTemplate.delete(_KEY);
	}
}
