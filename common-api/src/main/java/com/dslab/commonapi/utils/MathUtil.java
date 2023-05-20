package com.dslab.commonapi.utils;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

/**
 * @program: dslab-event
 * @description: 用中国剩余定理算法求两个有循环周期的日程在同一天发生的最近日期
 * @author: 郭晨旭
 * @create: 2023-04-07 19:46
 * @version: 1.0
 **/

public class MathUtil {
    /**
     * 扩展欧几里得算法
     */
    private static long exGcd(long a, long b, long[] u) {
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
    private static long inv(long a, long p) {
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

    /**
     * 排序
     *
     * @param list 待排序的列表
     * @param c    自定义比较器
     */
    public static <T, E> void mySort(List<E> list, Comparator<? super E> c) {
        Object[] arr = list.toArray();
        quickSort(arr, 0, arr.length - 1, (Comparator) c);
        ListIterator<E> i = list.listIterator();
        for (Object e : arr) {
            i.next();
            i.set((E) e);
        }
    }

    /**
     * 快排
     *
     * @param arr  待排序的数组
     * @param low  左端点
     * @param high 右端点
     * @param c    自定义比较器
     */
    private static <T> void quickSort(T[] arr, int low, int high, Comparator<? super T> c) {
        int i, j;
        T pivot, t;
        if (low >= high) {
            return;
        }
        i = low;
        j = high;
        //随机选取元素作为枢轴
        pivot = arr[new Random().nextInt(high - low) + low];

        while (i < j) {
            //先看右边，依次往左递减
            while (c.compare(pivot, arr[j]) <= 0 && i < j) {
                j--;
            }
            //再看左边，依次往右递增
            while (c.compare(pivot, arr[i]) >= 0 && i < j) {
                i++;
            }
            //如果满足条件则交换
            if (i < j) {
                t = arr[j];
                arr[j] = arr[i];
                arr[i] = t;
            }
        }

        //最后将基准为与i和j相等位置的数字交换
        arr[low] = arr[i];
        arr[i] = pivot;

        //递归调用左半数组
        quickSort(arr, low, j - 1, c);
        //递归调用右半数组
        quickSort(arr, j + 1, high, c);
    }

    /**
     * 查找
     *
     * @param a   要查找的列表
     * @param target 目标对象
     * @param c      自定义比较器
     * @return 对升序数组二分查找, 大于等于target的第一个数下标，均大于时返回0，均小于时返回-1
     */
    public static <T> int lowerBound(List<T> a, T target, Comparator<? super T> c) {

        int l = 0, r = 0, mid, half, len = a.size() - 1;
        while (len > 0) {
            half = len / 2;
            mid = l + half;
            if (c.compare(a.get(mid), target) < 0) {
                l = mid + 1;
                len = len - half - 1;
            } else {
                len = half;
            }
        }
        if (len < 0 || c.compare(target, a.get(l)) > 0) {
            return -1;
        }
        return l;
    }
}
