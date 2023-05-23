package com.dslab.commonapi.dataStruct;

/**
 * @program: DSlab
 * @description: 自己实现的map接口
 * @author: 郭晨旭
 * @create: 2023-05-23 14:48
 * @version: 1.0
 **/

public interface MyMap<K, V> {
    /**
     * 返回map集合大小
     *
     * @return map集合大小
     */
    int size();

    /**
     * 判断map是否为空
     *
     * @return true/false
     */
    boolean isEmpty();

    /**
     * 根据key获取value
     *
     * @param key 键
     * @return 根据键返回的value, 不存在则返回null
     */
    V get(Object key);

    /**
     * 添加元素
     *
     * @param key   键
     * @param value 值
     * @return 返回旧值或者null
     */
    V put(K key, V value);

    /**
     * 删除对应键的键值对
     *
     * @param key 键
     * @return 返回旧值或者null
     */
    V remove(Object key);

    interface Entry<k, v> {
        /**
         * 获取key
         *
         * @return 键
         */
        k getKey();


        /**
         * 获取值
         *
         * @return 值
         */
        v getValue();
    }
}

