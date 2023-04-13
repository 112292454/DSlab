package com.gzy.nfa2dfa.guide.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class NFA extends DFA {

    public NFA(List<Status> q, List<Character> t, Status START, List<Status> f) {
        super(q, t, START, f);
    }

    public NFA(List<Status> q, List<Character> t, Status START, List<Status> f, Map<Status, Map<Character, Status>> sigmas) {
        super(q, t, START, f, sigmas);
    }
}
