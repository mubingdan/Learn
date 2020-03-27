package com.learn.tree;

import java.util.Comparator;

public abstract class BinaryTree<E> implements Tree<E> {

    public abstract TreeNode<E> createTreeNode(E element, TreeNode<E> parent);

    public abstract void afterAdd(TreeNode<E> node);

    public abstract void afterRemove(TreeNode<E> node);

    public TreeNode<E> root;

    public int size;

    protected Comparator<E> comparator;

    public BinaryTree() {
        this(null);
    }

    public BinaryTree(Comparator<E> comparator) {
        this.comparator = comparator;
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
    public void clear() {
        root = null;
    }

    @Override
    public void add(E element) {
        this.checkElementNull(element);
    }

    @Override
    public void remove(E element) {
        this.checkElementNull(element);
    }

    /**
     * 根据Element获取节点
     *
     * @param element
     * @return
     */
    protected TreeNode<E> nodeOfElement(E element) {
        checkElementNull(element);

        TreeNode<E> node = root;
        while (node != null) {
            int compare = compare(element, node.element);
            if (compare > 0) {
                node = node.right;
            } else if (compare < 0) {
                node = node.left;
            } else {
                return node;
            }
        }

        return null;
    }

    /**
     * 前驱节点
     * @param node
     * @return
     */
    protected TreeNode<E> predecessor(TreeNode<E> node) {
        if (null == node) return null;

        TreeNode<E> preNode = node.left;
        if (null != preNode) {// 左子树不为null
            while (preNode.right != null) {
                preNode = preNode.right;
            }
            return preNode;
        }

        while (node.parent != null && node == node.parent.left) {
            node = node.parent;
        }

        return node;
    }

    /**
     * 后继节点
     * @param node
     * @return
     */
    protected TreeNode<E> successor(TreeNode<E> node) {
        if (null == node) return null;

        TreeNode<E> afterNode = node.right;
        if (null != afterNode) {
            while (afterNode.left != null) {
                afterNode = afterNode.left;
            }

            return afterNode;
        }

        if (node.parent != null && node == node.parent.right) {
            node = node.parent;
        }

        return node.parent;
    }


    protected int compare(E e1, E e2) {
        if (null != this.comparator) {
            return this.comparator.compare(e1, e2);
        }

        return ((Comparable) e1).compareTo(e2);
    }

    private void checkElementNull(E element) {
        if (null == element) {
            throw new IllegalArgumentException("Element must not be null");
        }
    }

    protected static class TreeNode<E> {

        public E element;
        public TreeNode<E> left;
        public TreeNode<E> right;
        public TreeNode<E> parent;

        public TreeNode(E element, TreeNode<E> parent) {
            this.element = element;
            this.parent = parent;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }

        public boolean hasTwoChildren() {
            return left != null && right != null;
        }

        public boolean hasChildren() {
            return left != null || right != null;
        }

        public boolean isLeft() {
            return null != parent && this == parent.left;
        }

        public boolean isRight() {
            return null != parent && this == parent.right;
        }
    }

}
