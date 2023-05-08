package com.dslab.commonapi.dataStruct;

import com.dslab.commonapi.entity.AVLTreeNode;

import java.util.Comparator;

/**
 * todo 待测试, 改注释
 *
 * @program: DSlab
 * @description: 二叉平衡树的工具类
 * @author: 郭晨旭
 * @create: 2023-05-02 00:34
 * @version: 1.0
 **/
public class AVLTreeServiceImpl<T> implements AVLTree<T> {
    /**
     * 根节点
     */
    private AVLTreeNode<T> root;
    /**
     * 自定义比较器
     */
    private Comparator<T> c;


    /**
     * 构造函数
     *
     * @param c 自定义比较器
     */
    public AVLTreeServiceImpl(Comparator<T> c) {
        root = null;
        this.c = c;
    }

    /**
     * 获取树的高度
     */
    private int height(AVLTreeNode<T> tree) {
        if (tree != null) {
            return tree.getHeight();
        }

        return 0;
    }

    /**
     * 获取树的高度
     *
     * @return 树的高度
     */
    @Override
    public int height() {
        return height(root);
    }

    /**
     * 比较两个值的大小
     */
    private int max(int a, int b) {
        return a > b ? a : b;
    }

    /**
     * 前序遍历"AVL树"
     */
    private void preOrder(AVLTreeNode<T> tree) {
        if (tree != null) {
            System.out.print(tree.getKey() + " ");
            preOrder(tree.getLeft());
            preOrder(tree.getRight());
        }
    }

    @Override
    public void preOrder() {
        preOrder(root);
    }

    /**
     * 中序遍历"AVL树"
     */
    private void inOrder(AVLTreeNode<T> tree) {
        if (tree != null) {
            inOrder(tree.getLeft());
            System.out.print(tree.getKey() + " ");
            inOrder(tree.getRight());
        }
    }

    @Override
    public void inOrder() {
        inOrder(root);
    }

    /**
     * 后序遍历"AVL树"
     */
    private void postOrder(AVLTreeNode<T> tree) {
        if (tree != null) {
            postOrder(tree.getLeft());
            postOrder(tree.getRight());
            System.out.print(tree.getKey() + " ");
        }
    }

    @Override
    public void postOrder() {
        postOrder(root);
    }

    /**
     * (递归实现)查找"AVL树x"中键值为key的节点
     */
    private AVLTreeNode<T> search(AVLTreeNode<T> x, T key) {
        if (x == null) {
            return x;
        }

        int cmp = c.compare(key, x.getKey());
        if (cmp < 0) {
            return search(x.getLeft(), key);
        } else if (cmp > 0) {
            return search(x.getRight(), key);
        } else {
            return x;
        }
    }

    /**
     * 查找树中值为key的节点
     *
     * @param key 目标值
     * @return 树的节点
     */
    @Override
    public AVLTreeNode<T> search(T key) {
        return search(root, key);
    }

    /**
     * (非递归实现)查找"AVL树x"中键值为key的节点
     */
    private AVLTreeNode<T> iterativeSearch(AVLTreeNode<T> x, T key) {
        while (x != null) {
            int cmp = c.compare(key, x.getKey());
            if (cmp < 0) {
                x = x.getLeft();
            } else if (cmp > 0) {
                x = x.getRight();
            } else {
                return x;
            }
        }

        return x;
    }

    /**
     * 查找键值为key的节点
     *
     * @param key 待查找节点
     * @return 树的节点
     */
    @Override
    public AVLTreeNode<T> iterativeSearch(T key) {
        return iterativeSearch(root, key);
    }

    /**
     * 查找最小结点：返回tree为根结点的AVL树的最小结点。
     */
    private AVLTreeNode<T> minimum(AVLTreeNode<T> tree) {
        if (tree == null) {
            return null;
        }

        while (tree.getLeft() != null) {
            tree = tree.getLeft();
        }
        return tree;
    }

    /**
     * 查找最小节点
     *
     * @return 最小节点
     */
    @Override
    public T minimum() {
        AVLTreeNode<T> p = minimum(root);
        if (p != null) {
            return p.getKey();
        }

        return null;
    }

    /**
     * 查找最大结点：返回tree为根结点的AVL树的最大结点。
     */
    private AVLTreeNode<T> maximum(AVLTreeNode<T> tree) {
        if (tree == null) {
            return null;
        }

        while (tree.getRight() != null) {
            tree = tree.getRight();
        }
        return tree;
    }

    /**
     * 查找最大节点
     *
     * @return 最大节点
     */
    @Override
    public T maximum() {
        AVLTreeNode<T> p = maximum(root);
        if (p != null) {
            return p.getKey();
        }

        return null;
    }

    /**
     * LL：左左对应的情况(左单旋转)。
     *
     * @return 旋转后的根节点
     */
    private AVLTreeNode<T> leftLeftRotation(AVLTreeNode<T> k2) {
        AVLTreeNode<T> k1;

        k1 = k2.getLeft();
        k2.setLeft(k1.getRight());
        k1.setRight(k2);

        k2.setHeight(max(height(k2.getLeft()), height(k2.getRight())) + 1);
        k1.setHeight(max(height(k1.getLeft()), k2.getHeight()) + 1);

        return k1;
    }

    /**
     * RR：右右对应的情况(右单旋转)。
     *
     * @return 旋转后的根节点
     */
    private AVLTreeNode<T> rightRightRotation(AVLTreeNode<T> k1) {
        AVLTreeNode<T> k2;

        k2 = k1.getRight();
        k1.setRight(k2.getLeft());
        k2.setLeft(k1);
        ;

        k1.setHeight(max(height(k1.getLeft()), height(k1.getRight())) + 1);
        k2.setHeight(max(height(k2.getRight()), k1.getHeight()) + 1);

        return k2;
    }

    /**
     * LR：左右对应的情况(左双旋转)。
     *
     * @return 旋转后的根节点
     */
    private AVLTreeNode<T> leftRightRotation(AVLTreeNode<T> k3) {
        k3.setLeft(rightRightRotation(k3.getLeft()));

        return leftLeftRotation(k3);
    }

    /**
     * RL：右左对应的情况(右双旋转)。
     *
     * @return 旋转后的根节点
     */
    private AVLTreeNode<T> rightLeftRotation(AVLTreeNode<T> k1) {
        k1.setRight(leftLeftRotation(k1.getRight()));

        return rightRightRotation(k1);
    }

    /**
     * 将结点插入到AVL树中，并返回根节点
     *
     * @param tree AVL树的根结点
     * @param key  插入的结点的键值
     * @return 根节点
     */
    private AVLTreeNode<T> insert(AVLTreeNode<T> tree, T key) {
        if (tree == null) {
            // 新建节点
            tree = new AVLTreeNode<T>(key, null, null);
            if (tree == null) {
                System.out.println("ERROR: insert avltree node failed!");
                return null;
            }
        } else {
            int cmp = c.compare(key, tree.getKey());

            if (cmp < 0) {
                // 应该将key插入到"tree的左子树"的情况
                tree.setLeft(insert(tree.getLeft(), key));

                // 插入节点后，若AVL树失去平衡，则进行相应的调节。
                if (height(tree.getLeft()) - height(tree.getRight()) == 2) {
                    if (c.compare(key, tree.getLeft().getKey()) < 0) {
                        tree = leftLeftRotation(tree);
                    } else {
                        tree = leftRightRotation(tree);
                    }
                }
            } else if (cmp > 0) {
                // 应该将key插入到"tree的右子树"的情况
                tree.setRight(insert(tree.getRight(), key));

                // 插入节点后，若AVL树失去平衡，则进行相应的调节。
                if (height(tree.getRight()) - height(tree.getLeft()) == 2) {
                    if (c.compare(key, tree.getRight().getKey()) > 0) {
                        tree = rightRightRotation(tree);
                    } else {
                        tree = rightLeftRotation(tree);
                    }
                }
            } else {
                // cmp==0
                System.out.println("添加失败：不允许添加相同的节点！");
            }
        }

        tree.setHeight(max(height(tree.getLeft()), height(tree.getRight())) + 1);

        return tree;
    }

    /**
     * 插入节点
     *
     * @param key 待插入节点
     */
    @Override
    public void insert(T key) {
        root = insert(root, key);
    }

    /**
     * 删除结点, 返回根节点
     *
     * @param z    待删除的结点
     * @param tree AVL树的根结点
     * @return 根节点
     */
    private AVLTreeNode<T> remove(AVLTreeNode<T> tree, AVLTreeNode<T> z) {
        // 根为空 或者 没有要删除的节点，直接返回null。
        if (tree == null || z == null) {
            return null;
        }

        int cmp = c.compare(z.getKey(), tree.getKey());
        if (cmp < 0) {
            // 待删除的节点在"tree的左子树"中
            tree.setLeft(remove(tree.getLeft(), z));

            // 删除节点后，若AVL树失去平衡，则进行相应的调节。
            if (height(tree.getRight()) - height(tree.getLeft()) == 2) {
                AVLTreeNode<T> r = tree.getRight();
                if (height(r.getLeft()) > height(r.getRight())) {
                    tree = rightLeftRotation(tree);
                } else {
                    tree = rightRightRotation(tree);
                }
            }
        } else if (cmp > 0) {
            // 待删除的节点在"tree的右子树"中
            tree.setRight(remove(tree.getRight(), z));

            // 删除节点后，若AVL树失去平衡，则进行相应的调节。
            if (height(tree.getLeft()) - height(tree.getRight()) == 2) {
                AVLTreeNode<T> l = tree.getLeft();
                if (height(l.getRight()) > height(l.getLeft())) {
                    tree = leftRightRotation(tree);
                } else {
                    tree = leftLeftRotation(tree);
                }
            }
        } else {
            // tree是对应要删除的节点。
            // tree的左右孩子都非空
            if ((tree.getLeft() != null) && (tree.getRight() != null)) {
                if (height(tree.getLeft()) > height(tree.getRight())) {
                    // 如果tree的左子树比右子树高；
                    // 则(01)找出tree的左子树中的最大节点
                    //   (02)将该最大节点的值赋值给tree。
                    //   (03)删除该最大节点。
                    // 这类似于用"tree的左子树中最大节点"做"tree"的替身；
                    // 采用这种方式的好处是：删除"tree的左子树中最大节点"之后，AVL树仍然是平衡的。
                    AVLTreeNode<T> max = maximum(tree.getLeft());
                    tree.setKey(max.getKey());
                    tree.setLeft(remove(tree.getLeft(), max));
                } else {
                    // 如果tree的左子树不比右子树高(即它们相等，或右子树比左子树高1)
                    // 则(01)找出tree的右子树中的最小节点
                    //   (02)将该最小节点的值赋值给tree。
                    //   (03)删除该最小节点。
                    // 这类似于用"tree的右子树中最小节点"做"tree"的替身；
                    // 采用这种方式的好处是：删除"tree的右子树中最小节点"之后，AVL树仍然是平衡的。
                    AVLTreeNode<T> min = maximum(tree.getRight());
                    tree.setKey(min.getKey());
                    tree.setRight(remove(tree.getRight(), min));
                }
            } else {
                AVLTreeNode<T> tmp = tree;
                tree = (tree.getLeft() != null) ? tree.getLeft() : tree.getRight();
                tmp = null;
            }
        }

        return tree;
    }

    /**
     * 删除节点
     *
     * @param key 待删除节点
     */
    @Override
    public void remove(T key) {
        AVLTreeNode<T> z;

        if ((z = search(root, key)) != null) {
            root = remove(root, z);
        }
    }

    /**
     * 销毁AVL树
     */
    private void destroy(AVLTreeNode<T> tree) {
        if (tree == null) {
            return;
        }

        if (tree.getLeft() != null) {
            destroy(tree.getLeft());
        }
        if (tree.getRight() != null) {
            destroy(tree.getRight());
        }

        tree = null;
    }

    /**
     * 销毁树
     */
    @Override
    public void destroy() {
        destroy(root);
    }

    /**
     * 打印
     *
     * @param key       节点的键值
     * @param direction 0，表示该节点是根节点;
     *                  -1，表示该节点是它的父结点的左孩子;
     *                  1，表示该节点是它的父结点的右孩子。
     */
    private void print(AVLTreeNode<T> tree, T key, int direction) {
        if (tree != null) {
            if (direction == 0) {
                // tree是根节点
                System.out.println(tree.getKey() + "  " + key);
            } else {
                // tree是分支节点
                System.out.println(tree.getKey() + " " + key);
            }

            print(tree.getLeft(), tree.getKey(), -1);
            print(tree.getRight(), tree.getKey(), 1);
        }
    }

    /**
     * 打印树
     */
    @Override
    public void print() {
        if (root != null) {
            print(root, root.getKey(), 0);
        }
    }
}
