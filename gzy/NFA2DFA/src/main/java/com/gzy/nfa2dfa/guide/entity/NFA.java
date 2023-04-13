package com.gzy.nfa2dfa.guide.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class NFA extends DFA{

	public NFA(List<Status> q, List<Character> t, Status START, List<Status> f) {
		super(q, t, START, f);
	}

	public NFA(List<Status> q, List<Character> t, Status START, List<Status> f, Map<Status, Map<Character, Status>> sigmas) {
		super(q, t, START, f, sigmas);
	}

//	@Override
//	public Status sigma(Status p, Character input) {
//		//转换表里已有p+input，就返回
//		if(this.sigmas.containsKey(p)&&this.sigmas.get(p).containsKey(input)){
//			Status res = this.sigmas.get(p).getOrDefault(input, deniedStatus);
//			this.addOneSigma(p,input,res);
//			return res;
//		}
//		else {
//			//为原生的状态（不是状态集合，size=1），但是转换表又没有它（所有原生状态的转换应该一开始就有），那么就是不接受这个输入
//			return deniedStatus;
//		}
//	}

}
