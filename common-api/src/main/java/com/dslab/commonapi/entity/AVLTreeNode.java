package com.dslab.commonapi.entity;

import lombok.Data;

/**
 * @program: DSlab
 * @description: 平衡树节点对象
 * @author: 郭晨旭
 * @create: 2023-05-02 01:01
 * @version: 1.0
 **/
@Data
public class AVLTreeNode<T> {
    T key;                // 关键字(键值)
    int height;         // 高度
    AVLTreeNode<T> left;    // 左孩子
    AVLTreeNode<T> right;    // 右孩子

    public AVLTreeNode(T key, AVLTreeNode<T> left, AVLTreeNode<T> right) {
        this.key = key;
        this.left = left;
        this.right = right;
        this.height = 0;
    }
}
