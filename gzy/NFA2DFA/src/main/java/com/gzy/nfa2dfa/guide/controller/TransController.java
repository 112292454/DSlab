package com.gzy.nfa2dfa.guide.controller;

import com.gzy.nfa2dfa.guide.Dfa2graphUtils;
import com.gzy.nfa2dfa.guide.entity.DFA;
import com.gzy.nfa2dfa.guide.entity.NFA;
import com.gzy.nfa2dfa.guide.entity.inputNFA;
import com.gzy.nfa2dfa.guide.service.Nfa2dfa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequestMapping("/trans")
public class TransController {

	@Autowired
	Nfa2dfa nfa2dfa;

	private static final Logger log= LoggerFactory.getLogger(TransController.class);

	@RequestMapping({"nfa2dfa"})
	public void NFA2DFA(@RequestBody inputNFA input, HttpServletResponse response) throws IOException {

		NFA n=new NFA(input.getQ(), input.getT(), input.getSTART(), input.getF());
		n.buildSigmas(input.getSigmas());
		//		LinkedHashMap<Status,LinkedHashMap<Character,Status>> tempProcess=
//				((LinkedHashMap<Status,LinkedHashMap<Character,Status>>)nfa).get("sigmas");
		DFA res = nfa2dfa.NFA2DFA(n);
		File graph = Dfa2graphUtils.dfa2graph(res);

		OutputStream os = null;
		try {
			BufferedImage image = ImageIO.read(new FileInputStream(graph));
			response.setContentType("image/png");
			os = response.getOutputStream();
			if (image != null) {
				ImageIO.write(image, "png", os);
			}
		} catch (IOException e) {
			log.error("获取图片异常{}",e.getMessage());
		} finally {
			if (os != null) {
				os.flush();
				os.close();
			}
		}

		//return res;
	}


}
