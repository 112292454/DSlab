package com.gzy.nfa2dfa.guide.entity;

import java.util.List;
import java.util.Map;

public interface FA {

    Status deniedStatus = new Status("denied!");
    Status epsilonStatus = new Status("continue!");

    Status sigma(Status p, Character input);

    boolean addOneSigma(Status from, Character input, Status result);

    boolean canContinue(Status now);

    void buildSigmas(Map<Status, Map<Character, List<String>>> input);

    void printFA();
}

