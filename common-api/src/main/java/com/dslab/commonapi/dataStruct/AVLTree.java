package com.dslab.commonapi.dataStruct;

import com.dslab.commonapi.entity.AVLTreeNode;

/**
 * @program: DSlab
 * @description: 平衡树相关服务的接口
 * @author: 郭晨旭
 * @create: 2023-05-04 21:34
 * @version: 1.0
 **/
public interface AVLTree<T> {
    /**
     * 获取树的高度
     *
     * @return 树的高度
     */
    int height();

    /**
     * 查找树中值为key的节点
     *
     * @param key 目标值
     * @return 树的节点
     */
    AVLTreeNode<T> search(T key);

    /**
     * 查找最小节点
     *
     * @return 最小节点
     */
    T minimum();

    /**
     * 查找最大节点
     *
     * @return 最大节点
     */
    T maximum();

    /**
     * 插入节点
     *
     * @param key 待插入节点
     */
    void insert(T key);

    /**
     * 删除节点
     *
     * @param key 待删除节点
     */
    void remove(T key);

    /**
     * 销毁树
     */
    void destroy();

    /**
     * 打印树
     */
    void print();

    /**
     * 修改节点
     * @param src 原节点
     * @param dest 目标节点
     */
    void update(T src, T dest);
}
