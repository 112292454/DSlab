package com.gzy.nfa2dfa.guide.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class inputNFA {
    @JsonProperty("Q")
    private List<Status> Q;

    //输入字母表
    @JsonProperty("T")
    private List<Character> T;

    //起始状态
    @JsonProperty("START")
    private Status START;

    //终止状态
    @JsonProperty("F")
    private List<Status> F;

    @JsonProperty("sigmas")
    private Map<Status, Map<Character, List<String>>> sigmas;
}
