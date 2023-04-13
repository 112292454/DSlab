package com.gzy.nfa2dfa.guide.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class ENFA extends NFA {

    public ENFA(List<Status> q, List<Character> t, Status START, List<Status> f) {
        super(q, t, START, f);
    }

    public ENFA(List<Status> q, List<Character> t, Status START, List<Status> f, Map<Status, Map<Character, Status>> sigmas) {
        super(q, t, START, f, sigmas);
    }
}
