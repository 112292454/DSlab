package com.dslab.event.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: dslab-event
 * @description: 字符串相关工具
 * @author: 郭晨旭
 * @create: 2023-04-08 18:45
 * @version: 1.0
 **/
public class StringUtils {
    public static Trie trie = new Trie();

    /**
     * 判断两个字符串是否完全相等
     *
     * @return 完全相等返回true, 否则返回false
     */
    public static boolean isEqual(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) {
            return false;
        }
        char[] aChars = a.toCharArray();
        char[] bChars = b.toCharArray();
        int len = a.length();
        for (int i = 0; i < len; ++i) {
            if (aChars[i] != bChars[i]) {
                return false;
            }
        }
        return true;
    }


}

class Trie {
    private int curIndex;
    private ArrayList<int[]> trie;
    private ArrayList<Boolean> isEnd;
    private char bias;

    public Trie(int charsetSize, int maxLength, char bias) {
        //trie=new int[maxLength][charsetSize];
        trie = new ArrayList<>();
        trie.add(new int[27]);
        isEnd = new ArrayList<>();
        curIndex = 1;
        this.bias = bias;
    }

    public Trie(int charsetSize, char bias) {
        this(charsetSize, (int) (1e6 + 7), bias);
    }

    public Trie() {
        this(26, 'a');
    }

    public void add(String word) {
        int p = 0;
        //p是当前循环中，与charAt i将要匹配的节点：trie[p]中，若[index]不为0，则存储了i这个位置的字母
        for (int i = 0; i < word.length(); i++) {
            int index = word.charAt(i) - bias;
            int[] temp = trie.get(p);
            if (temp[index] == 0) {
                trie.add(new int[27]);
                temp[index] = curIndex++;
            }
            p = temp[index];
        }
        isEnd.ensureCapacity(p);
        while (isEnd.size() <= p) {
            isEnd.add(false);
        }
        isEnd.set(p, true);
    }

    public boolean contains(String word) {
        int p = 0;
        //p是当前循环中，与charAt i将要匹配的节点：trie[p]中，若[index]不为0，则存储了i这个位置的字母
        for (int i = 0; i < word.length(); i++) {
            int index = word.charAt(i) - bias;
            if (trie.get(p)[index] == 0) {
                return false;
            }
            p = trie.get(p)[index];
        }
        return isEnd.get(p);
    }

    public boolean isPrefix(String word) {
        int p = 0;
        //p是当前循环中，与charAt i将要匹配的节点：trie[p]中，若[index]不为0，则存储了i这个位置的字母
        for (int i = 0; i < word.length(); i++) {
            int index = word.charAt(i) - bias;
            if (trie.get(p)[index] == 0) {
                return false;
            }
            p = trie.get(p)[index];
        }
        return true;
    }

    /**
     * 注意：这里返回的，比如有he、here，那么只会返回he，即返回前缀
     */
    public List<String> listAllWord() {
        int p = 0;
        return listDFS(new ArrayList<>(), new char[curIndex], 0, 0);
    }


    private List<String> listDFS(List<String> res, char[] now, int nowCur, int index) {
        if (isEnd.get(index)) {
            res.add(new String(now, 0, nowCur));
            return res;
        }
        int[] temp = trie.get(0);
        for (int i = 0; i < temp.length; i++) {
            temp = trie.get(index);
            if (temp[i] != 0) {
                now[nowCur] = (char) (i + bias);
                listDFS(res, now, nowCur + 1, temp[i]);
                now[nowCur] = 0;
            }
        }
        return res;
    }
}