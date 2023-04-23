package com.dslab.event.utils;

/**
 * @program: dslab-event
 * @description: 用中国剩余定理算法求两个有循环周期的日程在同一天发生的最近日期
 * @author: 郭晨旭
 * @create: 2023-04-07 19:46
 * @version: 1.0
 **/

public class MathUtils {
    /**
     * 扩展欧几里得算法
     */
    public static long exGcd(long a, long b, long[] u) {
        if (b == 0) {
            u[0] = 1;
            u[1] = 0;
            return a;
        }
        long g = exGcd(b, a % b, u);
        long t = u[0];
        u[0] = u[1];
        u[1] = t;
        u[1] -= a / b * u[0];
        return g;
    }

    /**
     * 乘法逆元
     */
    public static long inv(long a, long p) {
        long[] u = {0, 0};
        long x = 0, y = 0;
        exGcd(a, p, u);
        return (u[0] % p + p) % p;
    }

    /**
     * 中国剩余定理求最小冲突日期
     *
     * @param b 日期和周期的模结果
     * @param p 循环周期
     * @param n 一共有几个日程进行判断
     * @return 冲突的最近天数
     */
    public static long CRT(long[] b, long[] p, int n) {
        long mul = 1;
        for (int i = 0; i < n; i++) {
            mul *= p[i];
        }

        long res = 0;
        for (int i = 0; i < n; i++) {
            long t = mul / p[i];
            res = (res + t * b[i] * inv(t, p[i])) % mul;
        }
        return res;
    }

    public static long gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}
