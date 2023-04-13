package com.gzy.nfa2dfa.guide.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class DFA implements FA {
    //状态集
    private List<Status> Q;

    //输入字母表
    private List<Character> T;

    //起始状态
    private Status START;

    //终止状态
    private List<Status> F;

    private Map<Status, Map<Character, Status>> sigmas;

    public DFA(List<Status> q, List<Character> t, Status START, List<Status> f) {
        Q = q;
        T = t;
        this.START = START;
        F = f;
        this.sigmas = new HashMap<>();
    }

    public DFA(List<Status> q, List<Character> t, Status START, List<Status> f, Map<Status, Map<Character, Status>> sigmas) {
        Q = q;
        T = t;
        this.START = START;
        F = f;
        this.sigmas = sigmas;
    }

    public Map<Status, Map<Character, Status>> getSigmas() {
        return this.sigmas;
    }

    public void setSigmas(Map<Status, Map<Character, Status>> sigmas) {
        this.sigmas = sigmas;
    }

    @Override
    public Status sigma(Status p, Character input) {
        //转换表里已有p+input，就返回
        if (this.sigmas.containsKey(p) && this.sigmas.get(p).containsKey(input)) {
            return this.sigmas.get(p).getOrDefault(input, deniedStatus);
        } else if (p.getNames().size() == 1) {
            //为原生的状态（不是状态集合，size=1），但是转换表又没有它（所有原生状态的转换应该一开始就有），那么就是不接受这个输入
            return deniedStatus;
        }

        List<String> names = p.getNames();
        List<String> newNames = new ArrayList<>();
        for (String name : names) {
            //若没有，对于p的每一个可能状态，看他通过input会变成什么状态，然后添加状态名称
            Status tempS = new Status(name);
            Status transStatus = sigma(tempS, input);
            //如果是denied的状态，也就是转换函数里面没说明这个转换，那就是不接受
            if (canContinue(transStatus)) {
                newNames.addAll(transStatus.getNames());
            }
        }
        List<String> resNames = newNames.stream().distinct().sorted().collect(Collectors.toList());
        Status res = new Status(resNames);
        //记录到sigmas转换函数
        addOneSigma(p, input, res);
        return res;
    }

    @Override
    public boolean addOneSigma(Status from, Character input, Status result) {
        if (!canContinue(result)) {
            return false;
        }
        checkQ(result);
        Map<Character, Status> map = this.sigmas.getOrDefault(from, new HashMap<>());
        map.put(input, result);
        this.sigmas.put(from, map);
        return true;
    }

    @Override
    public boolean canContinue(Status now) {

        return !now.equals(deniedStatus);
    }

    @Override
    public void buildSigmas(Map<Status, Map<Character, List<String>>> input) {
        input.forEach((k, v) -> {
            Map<Character, Status> tempResult = new HashMap<>();
            v.forEach((tk, tv) -> tempResult.put(tk, new Status(tv)));
            this.sigmas.put(k, tempResult);
        });
    }

    private void checkQ(Status result) {
        if (!this.Q.contains(result)) {
            this.Q.add(result);
        }
    }

    @Override
    public void printFA() {

    }
}
