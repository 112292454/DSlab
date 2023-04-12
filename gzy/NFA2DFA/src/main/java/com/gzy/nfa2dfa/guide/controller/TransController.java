package com.gzy.nfa2dfa.guide.controller;

import com.gzy.nfa2dfa.guide.entity.DFA;
import com.gzy.nfa2dfa.guide.entity.NFA;
import com.gzy.nfa2dfa.guide.entity.inputNFA;
import com.gzy.nfa2dfa.guide.service.Nfa2dfa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trans")
public class TransController {

	@Autowired
	Nfa2dfa nfa2dfa;

	@RequestMapping({"nfa2dfa"})
	public DFA NFA2DFA(@RequestBody inputNFA input){

		NFA n=new NFA(input.getQ(), input.getT(), input.getSTART(), input.getF());
		n.buildSigmas(input.getSigmas());
		//		LinkedHashMap<Status,LinkedHashMap<Character,Status>> tempProcess=
//				((LinkedHashMap<Status,LinkedHashMap<Character,Status>>)nfa).get("sigmas");

		return nfa2dfa.NFA2DFA(n);
	}


}
