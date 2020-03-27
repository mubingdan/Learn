package com.learn.tree;

import java.util.Comparator;

public class BinarySearchTree<E> extends BinaryTree<E> {

    public BinarySearchTree() {
        this(null);
    }

    public BinarySearchTree(Comparator<E> comparator) {
        super(comparator);
    }

    @Override
    public void add(E element) {
        super.add(element);

        if (null == root) {
            root = this.createTreeNode(element, null);
            size++;
            afterAdd(root);
            return;
        }

        TreeNode<E> parent = root;
        TreeNode<E> tmpNode = root;
        int compare = 0;
        while (tmpNode != null) {
            parent = tmpNode;
            compare = this.compare(element, tmpNode.element);
            if (compare > 0) {// 大
                tmpNode = tmpNode.right;
            } else if (compare < 0) {// 小
                tmpNode = tmpNode.left;
            } else {
                tmpNode.element = element;
                return;
            }
        }

        TreeNode<E> node = this.createTreeNode(element, parent);
        if (compare > 0) {
            parent.right = node;
        } else {
            parent.left = node;
        }
        size++;

        afterAdd(node);
    }

    @Override
    public void remove(E element) {
        super.remove(element);

        TreeNode<E> node = this.nodeOfElement(element);
        if (null == node) {
            return;
        }

        size--;

        if (node.hasTwoChildren()) {// 拥有两个子树
            TreeNode<E> afterNode = this.successor(node);
            if (afterNode == null) {
                root = null;
                return;
            }

            node.element = afterNode.element;
            node = afterNode;
        }

        if (node.hasChildren()) {// 此时如果拥有一个子树
            TreeNode<E> child = null;
            if (node.left != null) {
                child = node.left;
            }

            if (node.right != null) {
                child = node.right;
            }

            if (child != null) {
                child.parent = node.parent;
                if (node.isLeft()) {
                    node.parent.left = child;
                } else if (node.isRight()) {
                    node.parent.right = child;
                } else {
                    root = child;
                }
            }

            // AVL树删除后的平衡操作是判断父节点及祖父节点是否平衡并修复（child是替换node的，因此拥有相同的父节点，此处传node或者child都可以）
            // 红黑树 删除的节点（拥有一个红色节点）同样是被红色child的替换，child替换为原来节点的颜色即可，因此这里传child并改为黑色
            afterRemove(child);
        } else if (node.parent == null) {
            root = null;
        } else {
            if (node.isLeft()) {
                node.parent.left = null;
            }
            if (node.isRight()) {
                node.parent.right = null;
            }
            afterRemove(node);
        }
    }

    @Override
    public boolean contains(E element) {
        return null != nodeOfElement(element);
    }

    @Override
    public void afterAdd(TreeNode<E> node) {

    }

    @Override
    public void afterRemove(TreeNode<E> node) {

    }

    @Override
    public TreeNode<E> createTreeNode(E element, TreeNode<E> parent) {
        return new TreeNode<>(element, parent);
    }
}
