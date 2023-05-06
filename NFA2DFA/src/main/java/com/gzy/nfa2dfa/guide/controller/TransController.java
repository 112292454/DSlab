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
import java.io.OutputStream;

@RestController
@RequestMapping("/trans")
public class TransController {

    @Autowired
    Nfa2dfa nfa2dfa;

    private static final Logger log = LoggerFactory.getLogger(TransController.class);

    /**
     * NFA到DFA的转化
     */
    @RequestMapping({"nfa2dfa"})
    public DFA NFA2DFA(@RequestBody inputNFA input, HttpServletResponse response) throws Exception {
        // 将接受到的数据解析成NFA
        NFA n = new NFA(input.getQ(), input.getT(), input.getSTART(), input.getF());
        n.buildSigmas(input.getSigmas());

        // NFA转化成DFA
        DFA res = nfa2dfa.NFA2DFA(n);
        OutputStream os = null;

        try {
            // 有Graphviz则可以直接返回图片
            File graph = Dfa2graphUtils.dfa2graph(res);
            BufferedImage image = ImageIO.read(new FileInputStream(graph));
            response.setContentType("image/png");
            os = response.getOutputStream();
            if (image != null) {
                ImageIO.write(image, "png", os);
            }
        } catch (Exception e) {
            log.error("获取图片异常{}", e.getMessage());
        } finally {
            if (os != null) {
                os.flush();
                os.close();
            }
        }
        return res;
    }

    /**
     * 将输入的NFA进行图形化展示
     */
    @RequestMapping({"show"})
    public void showNFA(@RequestBody inputNFA input, HttpServletResponse response) throws Exception {
        //解析数据
        NFA n = new NFA(input.getQ(), input.getT(), input.getSTART(), input.getF());
        n.buildSigmas(input.getSigmas());
        File graph = Dfa2graphUtils.nfa2graph(n);

        OutputStream os = null;
        try {
            BufferedImage image = ImageIO.read(new FileInputStream(graph));
            response.setContentType("image/png");
            os = response.getOutputStream();
            if (image != null) {
                ImageIO.write(image, "png", os);
            }
        } catch (Exception e) {
            log.error("获取图片异常{}", e.getMessage());
        } finally {
            if (os != null) {
                os.flush();
                os.close();
            }
        }

    }
}
