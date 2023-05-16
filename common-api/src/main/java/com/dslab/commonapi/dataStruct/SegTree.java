package com.dslab.commonapi.dataStruct;

import com.dslab.commonapi.entity.Event;

import java.util.Date;
import java.util.List;

public interface SegTree {
	void rangeModify(int start, int end, int value);

	List<Integer> rangeQuery(int start, int end);

	void addEvent(Event e);

	List<Integer> queryEvent(Date from, Date to);

	void modifyEvent(Event source, Event dest);

	void deleteEvent(Event e);
}
