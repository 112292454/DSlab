package com.dslab.commonapi.dataStruct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @program: DSlab
 * @description: 自己实现的hashmap类
 * @author: 郭晨旭
 * @create: 2023-05-23 14:49
 * @version: 1.0
 **/

public class MyHashMap<K, V> implements MyMap<K, V> {
    /**
     * 默认容量为16
     */
    private static final int DEFAULT_CAPACITY = 1 << 4;
    /**
     * 内部存储结构
     */
    Node<K, V>[] table = new Node[DEFAULT_CAPACITY];
    /**
     * 长度
     */
    private int size;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Accessors(chain = true)
    static class Node<K, V> implements MyMap.Entry<K, V> {
        /**
         * hash值
         */
        int hash;
        /**
         * key
         */
        K key;
        /**
         * value
         */
        V value;
        /**
         * 指向下个节点（单链表）
         */
        Node<K, V> next;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public V get(Object key) {
        int hashValue = hash(key);
        int i = indexFor(hashValue, table.length);
        for (Node<K, V> node = table[i]; node != null; node = node.next) {
            if (node.key.equals(key) && hashValue == node.hash) {
                return node.value;
            }
        }
        return null;
    }

    /**
     * 获取对应键的节点
     *
     * @param key 键
     * @return 节点
     */
    private Node<K, V> getNode(Object key) {
        int hashValue = hash(key);
        int i = indexFor(hashValue, table.length);
        for (Node<K, V> node = table[i]; node != null; node = node.next) {
            if (node.key.equals(key) && hashValue == node.hash) {
                return node;
            }
        }
        return null;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        Node<K, V> e;
        return (e = getNode(key)) == null ? defaultValue : e.value;
    }

    @Override
    public V remove(Object key) {
        // 通过key获取hash值
        int hashValue = hash(key);
        // 通过hash找到这个key应该放的位置
        int i = indexFor(hashValue, table.length);
        Node<K, V> pre = table[i];
        K k = pre.key;
        if (pre.hash == hashValue && (k == key || key.equals(k))) {
            V oldValue = pre.value;
            table[i] = table[i].next;
            return oldValue;
        }
        for (Node<K, V> node = table[i].next; node != null; pre = pre.next, node = node.next) {
            if (node.hash == hashValue && ((k = node.key) == key || key.equals(k))) {
                pre.next = node.next;
                return node.value;
            }
        }
        return null;
    }

    /**
     * 获取hash值
     *
     * @param key 键
     * @return hash值
     */
    public int hash(Object key) {
        return key.hashCode();
    }

    /**
     * 获取插入的位置
     *
     * @param hashValue hash值
     * @param length    表长
     * @return 插入位置
     */
    private int indexFor(int hashValue, int length) {
        return hashValue % length;
    }

    @Override
    public V put(K key, V value) {
        // 通过key获取hash值
        int hashValue = hash(key);
        // 通过hash找到这个key应该放的位置
        int i = indexFor(hashValue, table.length);
        for (Node<K, V> node = table[i]; node != null; node = node.next) {
            K k;
            if (node.hash == hashValue && ((k = node.key) == key || key.equals(k))) {
                V oldValue = node.value;
                node.value = value;
                return oldValue;
            }
        }

        // 如果i位置没有数据，或者i位置有数据但是key是新的key，新增节点
        addEntry(key, value, hashValue, i);
        return null;
    }

    /**
     * 新增节点
     *
     * @param key       键
     * @param value     值
     * @param hashValue 键的hash值
     * @param i         要插入的位置
     */
    private void addEntry(K key, V value, int hashValue, int i) {
        // 如果超过了原数组大小，则扩大数组
        if (++size == table.length) {
            Node<K, V>[] newTable = new Node[table.length << 1];
            System.arraycopy(table, 0, newTable, 0, table.length);
            table = newTable;
        }
        Node<K, V> eNode = table[i];
        // 新增节点，将该节点的next指向前一个节点
        table[i] = new Node<K, V>(hashValue, key, value, eNode);
    }
}


