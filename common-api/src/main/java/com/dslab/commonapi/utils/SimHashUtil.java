package com.dslab.commonapi.utils;


import com.qianxinyao.analysis.jieba.keyword.Keyword;
import com.qianxinyao.analysis.jieba.keyword.TFIDFAnalyzer;
import io.micrometer.core.instrument.util.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;


public class SimHashUtil {

    HashMap<String, Integer> dict;

//    private final HashMap<String,Integer> g;

//    boolean[] wordHash=new boolean[10000];






    public SimHashUtil() {
//        this.gradeMapper=gradeMapper;
//        dict = new HashMap<>();

//        HashMap<String,Integer> g=new HashMap<>();
//        List<Grade> gradeList = gradeMapper.loadAllTrueGrade();
//        gradeList.forEach(a->g.put(a.getStr(),a.getGrade()));
//        g.forEach((k,v) ->wordHash[k.hashCode()&8191] =true);
//        this.g=g;
    }
    //TODO:设置语料库路径，从文件获取，统计词频，并计算

    public long getSimHash(String text) {
        HashMap<String, Double> strs = getByJieba(text);
        int[] res = new int[64];
        strs.forEach((k, v) -> {
            String bi = Long.toBinaryString((long) k.hashCode() * (long) k.hashCode());
            int weight = (int) Math.round(v*5);
            weight=Math.max(weight, 1);
            weight=Math.min(weight, 5);
//            if(wordHash[k.hashCode()&8191] && g.containsKey(k)) weight=5;
            if (k.charAt(0) > 127) {
                for (int i = 0; i < bi.length(); i++) {
                    res[i] += bi.charAt(i) == '1' ? weight : -weight;
                }
            }
        });
        long hash = 0;
        for (int re : res) {
            hash = (hash << 1) + (re > 0 ? 1 : 0);
        }
        return hash;
    }

    public boolean isSimiliar(long a,long b) {
        return isSimiliar(a,b,3);
    }

    public boolean isSimiliar(String a,String b) {
        return isSimiliar(getSimHash(a),getSimHash(b));
    }

    public boolean isSimiliar(long a,long b,int maxDiff) {
        return Long.bitCount(a^b)<maxDiff;
    }

    public int hamming(long a,long b) {
        return Long.bitCount(a^b);
    }


    private HashMap<String, Integer> getStrs(String text) {
        HashMap<String,Integer> cnt = new HashMap<>();

        /**
        String content = "《开端》《镜双城》《淘金》三部热播剧均有她，你发现了吗？";
        List<String> stop_words = FileUtils.readLines(new File(basePath + "files\\stop_words.txt"));
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<String> result = segmenter.sentenceProcess(content);
        System.out.println("没有过滤停用词======" + result);
        result = result.stream().map(String::trim).filter(o -> dict.getOrDefault(o,0)>1).collect(Collectors.toList());
        System.out.println("过滤停用词=========" + result);
         */

//        for (int i = 1; i <=4; i++) {
//            String temp;
//            for (int j = 0; j < text.length()-i-1;j++ ) {
//                temp = text.substring(j, j + i);
//                if (wordHash[temp.hashCode()&8191] && g.containsKey(temp)) {
//                    cnt.put(temp, cnt.getOrDefault(temp, 0) + 1);
//                }
//            }
//        }

        /**
        for (int i = 1; i <=4; i++) {
            String temp;
            for (int j = 0; j < text.length() - i - 1; j++) {
                temp = text.substring(j, j + i);
                if(temp.charAt(0)>' '&&temp.charAt(0)<'~') continue;
                cnt.put(temp, cnt.getOrDefault(temp, 0) + 1);
            }
        }
         */
        return cnt;
    }
    private HashMap<String, Double> getByJieba(String text) {
        HashMap<String,Double> cnt = new HashMap<>();
        int topN=200;
        TFIDFAnalyzer tfidfAnalyzer=new TFIDFAnalyzer();
        List<Keyword> list=tfidfAnalyzer.analyze(text,topN);
        for(Keyword word:list) {
            //System.out.println(word.getName()+":"+word.getTfidfvalue()+",");
            double v = word.getTfidfvalue();
            v*=32;
            //v=1/(1+e^(-lg(v))

            v=-Math.log10(v);
            v=1+Math.exp(v);
            v=1/v;

            cnt.put(word.getName(),v);
        }
        // 防拐:0.1992,幼儿园:0.1434,做好:0.1065,教育:0.0946,安全:0.0924
        return cnt;
    }

    public long getSimHash(Path text) throws IOException {
        return getSimHash(
                IOUtils.toString(Files.newInputStream(text.toFile().toPath()),
                        StandardCharsets.UTF_8));
    }

    private double getWordWeight(String s,int times){
        int Ttime=dict.getOrDefault(s,0);
        if(Ttime==0||Ttime>250000) return 0;
        double idf=Math.log10(Ttime);
        idf=Math.atan(idf);
        idf=idf/(Math.PI/2);
        idf=Math.abs(idf);
        idf=Math.log10(idf);
        idf=Math.abs(idf);
        double res=times*idf;
        res=Math.abs(res);

        if(res<1) res=1;
        if(res>5) {
            if(res*idf>5) res=1;
            else res=Math.min(5, Math.log(res));
        }
        //根据grade的词频表获取权重：idf= log(arctan(log(Ttimes))/90): 0.04~0.2

        //TODO:改进：根据tf~idf+分词的词频方式获取
        return res;
    }
}
