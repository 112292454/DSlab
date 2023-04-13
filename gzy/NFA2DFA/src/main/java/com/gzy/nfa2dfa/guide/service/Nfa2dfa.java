package com.gzy.nfa2dfa.guide.service;

import com.gzy.nfa2dfa.guide.entity.DFA;
import com.gzy.nfa2dfa.guide.entity.NFA;
import com.gzy.nfa2dfa.guide.entity.Status;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Nfa2dfa {

	public DFA NFA2DFA(NFA input){
		DFA result=new DFA(new ArrayList<>(), input.getT(), input.getSTART(),new ArrayList<>());

		Deque<Status> processing=new LinkedList<>();
		processing.addFirst(input.getSTART());
		result.getQ().add(input.getSTART());
		List<Status> vised = new ArrayList<>();
		while (!processing.isEmpty()){
			Status doProcessStatus = processing.pollFirst();
			vised.add(doProcessStatus);
			input.getT().forEach(a->{

				Status nextStatus = input.sigma(doProcessStatus, a);
				result.addOneSigma(doProcessStatus, a, nextStatus);
				//仅当可继续，且这个状态没有出现过才进入队列，将来再推
				if(result.canContinue(nextStatus)&&!vised.contains(nextStatus)){
					processing.addLast(nextStatus);
				}

			});
		}
		result.getQ().forEach(a ->{
			for (Status status : input.getF()) {
				if(a.getNames().contains(status.getNames().get(0))) {
					result.getF().add(a);
					break;
				}
			}
		});
		return result;
	}
}
