package com.gzy.dslab.guide.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Point {

    public static final long serializationUID = 4462789275212454253L;

    private int id;

    private int x;

    private int y;

    private List<Integer> neighbors;

    private String name;

    public Point(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.neighbors = new ArrayList<>();
        name="道路节点"+id;
    }

    public Point(int id, int x, int y, String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.neighbors = new ArrayList<>();
        this.name = name;
    }

    public int getDistance(Point p) {
        return Math.max(Math.abs(p.x - x), Math.abs(p.y - y));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
