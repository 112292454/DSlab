package com.gzy.nfa2dfa.guide;

import com.gzy.nfa2dfa.guide.entity.DFA;
import com.gzy.nfa2dfa.guide.entity.Graphviz;
import com.gzy.nfa2dfa.guide.entity.Status;

import java.io.File;
import java.util.*;

public class Dfa2graphUtils {
    private static final String link = " -> ";

    /**
     * 将自动机进行图形化
     */
    private static File toGraph(DFA in, String type) {
        String[] nodes = new String[in.getQ().size()];
        String[] ends = new String[in.getF().size()];
        Set<String> hash = new HashSet<>();
        for (int i = 0; i < ends.length; i++) {
            ends[i] = "\"" + in.getF().get(i).toString() + "\"";
            hash.add(ends[i]);
        }
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = "\"" + in.getQ().get(i).toString() + "\"";
        }

        List<String> preline = new ArrayList<>();
        Map<Status, Map<Character, Status>> sigmas = in.getSigmas();
        if (type.equals("DFA")) {
            sigmas.forEach((from, trans) ->
                    trans.forEach((ch, to) ->
                            preline.add(
                                    "\"" + from.toString() + "\"" +
                                            link +
                                            "\"" + to.toString() + "\"" +
                                            " [label = \"" + ch + "\"]")));
        } else if (type.equals("NFA")) {
            sigmas.forEach((from, trans) ->
                    trans.forEach((ch, to) ->
                            to.getNames().forEach(a -> preline.add(
                                    "\"" + from.toString() + "\"" +
                                            link +
                                            "\"[" + a + "]\"" +
                                            " [label = \"" + ch + "\"]"))));
        }


        Graphviz gv = new Graphviz();
        //定义每个节点的style
        String nodesty = "[shape = oval, sides = 6, peripheries = 0, color = lightblue, style = filled]";
        String endNodesty = "[shape = oval, sides = 6, peripheries = 3, color = lightblue, style = filled]";
        String startNodesty = "[shape = oval, sides = 6, peripheries = 0, color = red, style = filled]";
        //String linesty = "[dir=\"none\"]";

        gv.addln(gv.start_graph());//SATRT
        gv.addln("edge[fontname=\"DFKai-SB\" fontsize=15 fontcolor=\"black\" color=\"red\" style=\"filled\"]");
        gv.addln("size =\"8,8\";");
        //设置节点的style
        for (String end : ends) {
            if (!end.equals(in.getSTART().toString())) gv.addln(end + " " + endNodesty);
        }
        for (String node : nodes) {
            if (!hash.contains(node) && !node.equals(in.getSTART().toString())) gv.addln(node + " " + nodesty);
        }
        gv.addln("\"" + in.getSTART() + "\" " + startNodesty);

        for (String s : preline) {
            gv.addln(s);
        }
        gv.addln("START[shape = none, image = \"resources\\start_wild_pic.jpg\"]");

        gv.addln("START" + link + "\"" + in.getSTART() + "\"");

        gv.addln(gv.end_graph());//END
        //节点之间的连接关系输出到控制台
        System.out.println(gv.getDotSource());
        //输出什么格式的图片(gif,dot,fig,pdf,ps,svg,png,plain)
        String fileType = "png";
        //输出到文件夹以及命名
        File out = new File(type + new Date().toString().replaceAll("[ :]", "_") + ".png");   // Linux
        //File out = new File("c:/eclipse.ws/graphviz-java-api/out." + type);    // Windows
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), fileType), out);
        return out;
    }

    public static File dfa2graph(DFA in) {
        return toGraph(in, "DFA");
    }

    public static File nfa2graph(DFA in) {
        return toGraph(in, "NFA");
    }
}
