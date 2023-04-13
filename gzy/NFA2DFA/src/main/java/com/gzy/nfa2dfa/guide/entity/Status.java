package com.gzy.nfa2dfa.guide.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public class Status implements Serializable {
    List<String> names;


    public Status(String onlyName) {
        names = new ArrayList<>();
        names.add(onlyName);
    }

    public Status(List<String> names) {
        this.names = names;
    }

    public Status(String[] names) {
        this.names = new ArrayList<>();
        this.names.addAll(Arrays.asList(names));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Status)) {
            return false;
        }
        Status status = (Status) o;
        HashSet<String> a = new HashSet<>(names);
        HashSet<String> b = new HashSet<>(status.names);
        return a.equals(b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(names);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        names.forEach(a -> sb.append(a).append(", "));
        sb.deleteCharAt(sb.length() - 2);
        sb.append(']');
        return sb.toString();
    }
}
