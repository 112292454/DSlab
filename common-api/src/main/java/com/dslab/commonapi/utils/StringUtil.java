package com.dslab.commonapi.utils;

import com.dslab.commonapi.dataStruct.Trie;

/**
 * @program: dslab-event
 * @description: 字符串相关工具
 * @author: 郭晨旭
 * @create: 2023-04-08 18:45
 * @version: 1.0
 **/
public class StringUtil {
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

    /**
     * todo 字符串模糊搜索
     *
     * @param s 需要查找的字符串
     * @return  返回一个查找结果的列表
     */
//    public static List<String> fuzzyMatch(String s){
//
//    }
}

