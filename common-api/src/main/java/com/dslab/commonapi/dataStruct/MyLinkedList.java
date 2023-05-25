package com.dslab.commonapi.dataStruct;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @program: DSlab
 * @description: 自己实现的linkedlist类
 * @author: 郭晨旭
 * @create: 2023-05-25 14:49
 * @version: 1.0
 **/
public class MyLinkedList<V> implements Iterable<V> {
    /**
     * 头节点
     */
    private DataNode first;
    /**
     * 尾节点
     */
    private DataNode last;
    /**
     * 链表大小
     */
    private int size;

    /**
     * 链表节点结构
     */
    class DataNode {
        /**
         * 数据
         */
        V data;
        /**
         * 指向上一个节点指针
         */
        DataNode previous;
        /**
         * 指向下一个节点指针
         */
        DataNode next;
    }

    /**
     * 在链表头插入节点
     *
     * @param data 需要插入的节点数据
     */
    public void addFirst(V data) {
        // 链表大小为0
        if (size == 0) {
            first = new DataNode();
            last = new DataNode();
            first.data = data;
            first.previous = null;
            first.next = null;
            last = first;
            size++;
        } else {
            // 创建临时节点
            DataNode temp = new DataNode();
            temp.data = data;
            temp.previous = null;
            temp.next = first;
            first.previous = temp;
            // 更新头节点
            first = temp;
            // 避免临时对象游离
            temp = null;
            size++;
        }
    }

    /**
     * 在链表尾插入节点
     *
     * @param data 需要插入的节点数据
     */
    public void addLast(V data) {
        // 链表大小为0
        if (size == 0) {
            first = new DataNode();
            last = new DataNode();
            first.data = data;
            first.previous = null;
            first.next = null;
            last = first;
            size++;
        } else {
            // 创建临时节点
            DataNode temp = new DataNode();
            temp.data = data;
            temp.next = null;
            temp.previous = last;
            last.next = temp;
            // 更新尾节点
            last = temp;
            // 避免临时对象游离
            temp = null;
            size++;
        }
    }

    /**
     * 在链表中插入节点
     *
     * @param data 需要插入的节点数据
     */
    public void add(V data) {
        addLast(data);
    }

    /**
     * 删除节点
     *
     * @param data 需要删除的节点数据
     */
    public boolean remove(V data) {
        // 链表中只剩一个节点
        if (size == 1) {
            first = null;
            size--;
            return true;
        }
        // 若删除的节点为头节点
        if (data.equals(first.data)) {
            DataNode temp = first;
            first = first.next;
            first.previous = null;
            size--;
            temp = null;
            return true;
        }
        // 若删除尾节点
        if (data.equals(last.data)) {
            DataNode temp = last;
            last = last.previous;
            last.next = null;
            size--;
            temp = null;
            return true;
        }
        // 删除链表中的节点
        for (DataNode x = first; x != null; x = x.next) {
            if (data.equals(x.data)) {
                x.previous.next = x.next;
                x.next.previous = x.previous;
                size--;
                x = null;
                return true;
            }
        }
        return false;
    }

    /**
     * 返回链表大小
     */
    public int size() {
        return size;
    }

    /**
     * 返回链表是否为空
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * 链表迭代
     */
    @Override
    public Iterator<V> iterator() {
        return new ListIterator();
    }

    /**
     * 自己实现链表的迭代器
     */
    private class ListIterator implements Iterator<V> {
        private DataNode current = first;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public V next() {
            if (isEmpty()) {
                throw new NoSuchElementException("The List is Empty!");
            } else {
                V data = current.data;
                current = current.next;
                return data;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * 格式化输出链表
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        int index = size;
        // 遍历整个链表
        for (DataNode x = first; x != null; x = x.next) {
            sb.append(x.data.toString());
            index--;
            if (index > 0) {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }
}
