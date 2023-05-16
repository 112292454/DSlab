package com.dslab.commonapi.dataStruct;

import com.dslab.commonapi.entity.Event;

import java.util.List;

public interface SegTree {
	void rangeModify(int start, int end, int value);

	List<Integer> rangeQuery(int start, int end);

	void addEvent(Event e);

	void modifyEvent(Event source, Event dest);
}
