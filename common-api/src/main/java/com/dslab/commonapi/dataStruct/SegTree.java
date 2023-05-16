package com.dslab.commonapi.dataStruct;

import com.dslab.commonapi.entity.Event;

import java.util.Date;
import java.util.List;

public interface SegTree {
	/**
	 * 范围修改
	 *
	 * @Author Guo
	 * @CreateTime 2023-05-17 00:02
	 * @Return void
	 * @param start 起始时间，分钟
	 * @param end
	 * @param value 要设置拥有什么事件id
	 */
	void rangeModify(int start, int end, int value);

	/**
	 * 范围查询
	 *
	 * @Author Guo
	 * @CreateTime 2023-05-17 00:02
	 * @Return java.util.List<java.lang.Integer>
	 * @param start 起始时间，分钟
	 * @param end
	 */
	List<Integer> rangeQuery(int start, int end);

	/**
	 * 添加事件
	 *
	 * @Author Guo
	 * @CreateTime 2023-05-17 00:02
	 * @Return void
	 * @param e 待添加的事件
	 */
	void addEvent(Event e);

	/**
	 * 查询时间段内有什么事件
	 *
	 * @Author Guo
	 * @CreateTime 2023-05-17 00:03
	 * @Return java.util.List<java.lang.Integer>
	 * @param from 起始时间，date
	 * @param to
	 */
	List<Integer> queryEvent(Date from, Date to);

	/**
	 * 修改事件的时间
	 *
	 * @Author Guo
	 * @CreateTime 2023-05-17 00:03
	 * @Return void
	 * @param source 待删除的事件
	 * @param dest 待添加的事件
	 */
	void modifyEvent(Event source, Event dest);

	/**
	 * 删除事件
	 *
	 * @Author Guo
	 * @CreateTime 2023-05-17 00:03
	 * @Return void
	 * @param e 待删除的事件（id、时间
	 */
	void deleteEvent(Event e);
}
