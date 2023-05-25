package com.dslab.commonapi.dataStruct;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

/**
 * @program: DSlab
 * @description: 自己实现的hashmap类
 * @author: 郭晨旭
 * @create: 2023-05-23 14:49
 * @version: 1.0
 **/

public class MyHashMap<K, V> extends AbstractMap<K, V> {
    /**
     * 默认的桶大小
     */
    private static final int INITIAL_CAPACITY = 1 << 4;
    /**
     * 最大初始桶大小容量
     */
    private static final int MAX_INITIAL_CAPACITY = 64;
    /**
     * 最大可以扩充的桶大小
     */
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    /**
     * 存储节点的桶
     */
    private MyLinkedList<Node<K, V>>[] buckets;
    /**
     * 数组扩容时的装载因子
     */
    private static final float loadFactor = 0.75f;
    /**
     * 阈值
     */
    private int threshold;
    /**
     * 节点个数
     */
    private int size = 0;

    /**
     * 节点结构
     */
    static class Node<K, V> implements Map.Entry<K, V> {
        /**
         * 键
         */
        private final K key;
        /**
         * 值
         */
        private V value;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public final K getKey() {
            return key;
        }

        @Override
        public final V getValue() {
            return value;
        }

        @Override
        public final String toString() {
            return key + " -> " + value;
        }

        @Override
        public final V setValue(V newValue) {
            V oldValue = value;
            this.value = newValue;
            return oldValue;
        }

        @Override
        public final int hashCode() {
            return key.hashCode() ^ value.hashCode();
        }

        @Override
        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
                if (key.equals(e.getKey()) &&
                        value.equals(e.getValue())) {
                    return true;
                }
            }
            return false;
        }
    }

    public MyHashMap() {
        this(INITIAL_CAPACITY);
    }

    public MyHashMap(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (initialCapacity > MAX_INITIAL_CAPACITY) {
            initialCapacity = MAX_INITIAL_CAPACITY;
        }
        buckets = new MyLinkedList[initialCapacity];
        threshold = tableSizeFor(loadFactor);
    }

    /**
     * hash函数
     *
     * @param key 键值
     */
    private static int hash(Object key) {
//        int h;
//        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
        return 100;
    }

    /**
     * 向Map中添加元素
     *
     * @param key   键值
     * @param value 值
     */
    @Override
    public V put(K key, V value) {
        V oldValue = null;
        if (size >= threshold) {
            resize(2 * buckets.length);
        }
        // 获取桶下标
        int index = indexFor(hash(key), buckets.length);
        // 若桶中为空，则创建链
        if (buckets[index] == null) {
            buckets[index] = new MyLinkedList<Node<K, V>>();
        }
        // 找到桶中的链
        MyLinkedList<Node<K, V>> bucket = buckets[index];
        // 新加入的键值对对象
        Node<K, V> pair = new Node<K, V>(key, value);
        // 判断是否找到相同key
        boolean found = false;
        // 遍历对应桶中的链
        for (Node<K, V> iPair : bucket) {
            if (iPair.getKey().equals(key)) {
                oldValue = iPair.getValue();
                iPair.setValue(value);
                found = true;
                break;
            }
        }
        if (!found) {
            buckets[index].addFirst(pair);
            size++;
        }
        return oldValue;
    }

    /**
     * 获得值
     *
     * @param key 键值
     */
    @Override
    public V get(Object key) {
        int index = indexFor(hash(key), buckets.length);
        // 若桶中无节点
        if (buckets[index] == null) {
            return null;
        }
        for (Node<K, V> iPair : buckets[index]) {
            if (iPair.getKey().equals(key)) {
                return iPair.getValue();
            }
        }
        return null;
    }


    /**
     * 获取对应键的值或者默认值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return value
     */
    @Override
    public V getOrDefault(Object key, V defaultValue) {
        V e;
        return (e = get(key)) == null ? defaultValue : e;
    }

    /**
     * 从hashmap中移除指定key的映射关系
     *
     * @param key 键值
     */
    @Override
    public V remove(Object key) {
        V oldValue = null;
        int index = indexFor(hash(key), buckets.length);
        // 若桶中无节点
        if (buckets[index] == null) {
            return null;
        }
        for (Node<K, V> iPair : buckets[index]) {
            if (iPair.getKey().equals(key)) {
                oldValue = iPair.getValue();
                buckets[index].remove(iPair);
                return oldValue;
            }
        }
        return null;
    }

    /**
     * 返回节点个数
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * 对桶进行扩容
     *
     * @param capacity 扩充后桶的大小
     */
    private void resize(int capacity) {
        MyLinkedList<Node<K, V>>[] oldBuckets = buckets;
        int oldCapacity = oldBuckets.length;
        // 已经达到最大容量
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }
        MyLinkedList<Node<K, V>>[] newBuckets = new MyLinkedList[capacity];
        // 将旧桶节点转入新的桶中
        transfer(newBuckets);
        buckets = newBuckets;
        // 修改阈值
        threshold = tableSizeFor(loadFactor);
    }

    /**
     * 将旧桶节点转入新的桶中
     *
     * @param newBuckets 新创建桶的引用
     */
    private void transfer(MyLinkedList<Node<K, V>>[] newBuckets) {
        MyLinkedList<Node<K, V>>[] temp = buckets;
        int oldCapacity = temp.length;
        int index = 0;
        boolean found = false;
        // 遍历旧桶中的值
        for (MyLinkedList<Node<K, V>> bucket : temp) {
            // 找到桶中的链
            // 若桶为空，则跳过
            if (bucket == null) {
                continue;
            }
            // 遍历对应桶中的链
            for (Node<K, V> iPair : bucket) {
                // 重新获得桶下标值，因为桶的大小发生变化
                index = indexFor(hash(iPair.getKey()), newBuckets.length);
                // 若桶中为空，则创建链
                if (newBuckets[index] == null) {
                    newBuckets[index] = new MyLinkedList<Node<K, V>>();
                }
                // 找到桶中的链
                MyLinkedList<Node<K, V>> bucketTemp = newBuckets[index];
                // 判断是否找到相同key
                found = false;
                // 遍历桶中的链
                for (Node<K, V> iTemp : bucketTemp) {
                    if (iTemp.getKey().equals(iPair.getKey())) {
                        iTemp.setValue(iPair.getValue());
                        found = true;
                        break;
                    }
                }
                // 未找到相同key值，则进行添加
                if (!found) {
                    newBuckets[index].addFirst(iPair);
                }
            }
        }
    }

    /**
     * 计算桶下标
     *
     * @param h      哈希方法值
     * @param length 当前桶大小
     */
    private int indexFor(int h, int length) {
        return h & (length - 1);
    }

    /**
     * 计算阈值，因为涉及float与int运算，只是对float进行了运算时扩大
     *
     * @param loadFactor 装载因子
     */
    private int tableSizeFor(float loadFactor) {
        int temp = (int) (loadFactor * 100);
        int result = (int) (buckets.length * temp) / 100;
        return Math.min(result, MAXIMUM_CAPACITY + 1);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
//        Set<Map.Entry<K, V>> set = new HashSet<>();
//        for (MyLinkedList<Node<K, V>> bucket : buckets) {
//            if (bucket == null) {
//                continue;
//            }
//            for (Node<K, V> mpair : bucket) {
//                set.add(mpair);
//            }
//        }
//        return set;
        return null;
    }
}

