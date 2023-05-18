package com.dslab.commonapi.dataStruct;

import com.dslab.commonapi.entity.Event;
import com.dslab.commonapi.utils.TimeUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class SegTreeImpl implements SegTree {
    /*
	疑似常数过大/写假了，之后需尝试用segment【】数组重写一遍，看看能不能降到可接受的时间
	现在貌似比ac时间多了几倍
	//已完成
	 */
    private class segment {
        int l, r, lazy = 0;
        Set<Integer> value;

        public segment(int a, int b) {
            l = a;
            r = b;
            value = new HashSet<>();
        }

        public segment(Set<Integer> value) {
            this.value = value;
        }

        public segment() {
            value = new HashSet<>();
        }

        public Set<Integer> merge(segment another) {
            Set<Integer> set = new HashSet<>(value);
            set.addAll(another.value);
            return set;
        }
    }

    private class node {
        //public int value,x;
        public int father, son, top;//可能变为node对象
        public int depth, treeSize, value, id;//数值
        public int x, y;//坐标

        @Override
        public node clone() {
            node t = new node();
            t.father = father;
            t.son = son;
            t.top = top;
            t.depth = depth;
            t.treeSize = treeSize;
            t.value = value;
            t.id = id;
            t.x = x;
            t.y = y;
            return t;
        }

        public node(int value, int x, int y) {
            this.value = value;
            this.x = x;
            this.y = y;
        }

        public node() {

        }
    }

    segment[] seg;
    node[] source;
    private static final int size = 24*60;

    public SegTreeImpl(List<Event> userEvents) {
        seg = new segment[size << 2];
        build(1, size, 1);
        userEvents.forEach(this::addEvent);
    }

    private void build(int l, int r, int index) {
        if (seg[index] == null) {
            seg[index] = new segment(l, r);
        }
        if (l == r) {
            segment s = new segment(l, r);
            //s.value=source[l-1].value;
            seg[index] = s;
        } else {
            int mid = (l + r) / 2;
            build(l, mid, index << 1);
            build(mid + 1, r, index << 1 | 1);
            pushUp(index);
        }
    }

    @Override
    public void rangeModify(int start, int end, int value) {
        modify(1, start, end, value, false);
    }

    @Override
    public List<Integer> rangeQuery(int start, int end) {
        segment query = query(1, start, end);
        return query.value.stream().sorted().collect(Collectors.toList());
    }

    @Override
    public void addEvent(Event e) {
        int sm = TimeUtil.dateToMin(e.getStartTime()), em = TimeUtil.dateToMin(e.getEndTime());

        rangeModify(sm, em, e.getEventId());
    }

    @Override
    public List<Integer> queryEvent(Date from, Date to) {
        return rangeQuery(TimeUtil.dateToMin(from), TimeUtil.dateToMin(to));
    }

    @Override
    public void modifyEvent(Event source, Event dest) {
        int sm = TimeUtil.dateToMin(source.getStartTime()), em = TimeUtil.dateToMin(source.getEndTime());
        int newSM = TimeUtil.dateToMin(dest.getStartTime()), newEM = TimeUtil.dateToMin(dest.getEndTime());

        deleteEvent(source);
        rangeModify(newSM, newEM, dest.getEventId());
    }

    @Override
    public void deleteEvent(Event e) {
        int sm = TimeUtil.dateToMin(e.getStartTime()), em = TimeUtil.dateToMin(e.getEndTime());
        modify(1, sm, em, e.getEventId(), true);
    }

    private void pushUp(int a) {
        seg[a].value = seg[a << 1].merge(seg[a << 1 | 1]);
        //if(seg[a<<1].value.equals(seg[a<<1|1].value))
        // seg[a<<1].value=seg[a<<1|1].value;

    }

    private void pushDown(int a) {
        if (seg[a].lazy != 0) {
            /*处理a的两个子节点的value变化，视实现而定*/
            seg[a << 1].value.addAll(seg[a].value);
            seg[a << 1 | 1].value.addAll(seg[a].value);
            //seg[a<<1].lazy=seg[a<<1|1].lazy=seg[a].lazy;
            seg[a].lazy = 0;
        }
    }

    private void modify(int i, int start, int end, int value, boolean isDelete) {
        int l = seg[i].l, r = seg[i].r, mid = (l + r) / 2;
        if (l >= start && r <= end) {
            //a.value += value * (r - l + 1);
            if (isDelete) {
                seg[i].value.remove(value);//TODO:修改时间时清除原有的课程id用，感觉很可能会有bug
            }
            if (!seg[i].value.contains(value)) {
                seg[i].value.add(value);
                seg[i].lazy = 1;
            }
            //视实现而定
            return;
        } else if (l > end || r < start || r == l) {
            return;
        }
        pushDown(i);
        if (mid >= start) {
            modify(i << 1, start, end, value, isDelete);
        }
        if (mid <= end) {
            modify(i << 1 | 1, start, end, value, isDelete);
        }
        pushUp(i);
    }

    private segment query(int i, int start, int end) {
        int l = seg[i].l, r = seg[i].r;
        if (l >= start && r <= end) {
            return seg[i];
        } else if (r < start || l > end) {
            return new segment();
        }
        pushDown(i);
        return new segment(query(i << 1, start, end).merge(query(i << 1 | 1, start, end)));
    }
}


