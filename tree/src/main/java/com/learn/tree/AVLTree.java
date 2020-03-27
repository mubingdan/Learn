package com.learn.tree;

import java.util.Comparator;

/**
 * AVL树
 *
 * @param <E>
 */
public class AVLTree<E> extends BinarySearchTree<E> {

    public AVLTree() {
        this(null);
    }

    public AVLTree(Comparator<E> comparator) {
        super(comparator);
    }

    @Override
    public void afterAdd(TreeNode<E> node) {
        if (null == node) return;

        while ((node = node.parent) != null) {
            if (((AVLTreeNode<E>) node).isBalance()) {// 平衡
                ((AVLTreeNode<E>) node).updateHeight();
            } else {// 不平衡
                reBalance(node);
                break;
            }
        }
    }

    @Override
    public void afterRemove(TreeNode<E> node) {
        if (null == node) return;

        while ((node = node.parent) != null) {
            if (((AVLTreeNode<E>) node).isBalance()) {// 平衡
                ((AVLTreeNode<E>) node).updateHeight();
            } else {// 不平衡
                reBalance(node);
                break;
            }
        }
    }

    @Override
    public TreeNode<E> createTreeNode(E element, TreeNode<E> parent) {
        return new AVLTreeNode<>(element, parent);
    }

    /**
     * 恢复平衡
     *
     * @param grand
     */
    private void reBalance(TreeNode<E> grand) {
        AVLTreeNode<E> parent = ((AVLTreeNode<E>) grand).tallerChild();
        AVLTreeNode<E> node = parent.tallerChild();
        if (parent.isLeft()) {// L
            if (node.isLeft()) {// LL 右旋转
                rotateRight(grand);
            } else {// LR 左旋转、右旋转
                // 先左旋转parent
                rotateLeft(parent);
                // 再右旋转grand
                rotateRight(grand);
            }
        } else {// R
            if (node.isLeft()) { // RL 右旋转，左旋转
                // 先右旋转parent
                rotateRight(parent);
                // 再左旋转grand
                rotateLeft(grand);
            } else {// RR 右旋转
                rotateRight(grand);
            }
        }
    }

    /**
     * 左旋转
     *
     * @param node
     */
    private void rotateLeft(TreeNode<E> node) {
        if (null == node) return;

        TreeNode<E> parent = node.right;
        TreeNode<E> child = parent.left;
        node.right = child;
        parent.left = node;

        rotateAfter((AVLTreeNode<E>) node, parent, child);
    }

    /**
     * 右旋转
     *
     * @param node
     */
    private void rotateRight(TreeNode<E> node) {
        if (null == node) return;

        TreeNode<E> parent = node.left;
        TreeNode<E> child = parent.right;
        node.left = child;
        parent.right = node;

        rotateAfter((AVLTreeNode<E>) node, parent, child);
    }

    /**
     * 更新旋转后数据 （左右旋转之后都需要更新parent及更新高度，因此单独抽取出来一个方法）
     *
     * @param node   grand node
     * @param parent parent node
     * @param child  node
     */
    private void rotateAfter(AVLTreeNode<E> node, TreeNode<E> parent, TreeNode<E> child) {
        parent.parent = node.parent;
        // 更新node的父节点前先判断是父节点的左节点还是右节点,建立与新的parent的关系
        if (node.isLeft()) {
            parent.parent.left = parent;
        } else if (node.isRight()) {
            parent.parent.right = parent;
        } else {
            root = parent;
        }
        node.parent = parent;
        if (null != child) {
            child.parent = node;
        }
        // 先更新变矮的节点的高度
        node.updateHeight();
        // 再更新变高的节点高度
        ((AVLTreeNode<E>) parent).updateHeight();
    }

    private static class AVLTreeNode<E> extends TreeNode<E> {

        int height = 1;

        AVLTreeNode(E element, TreeNode<E> parent) {
            super(element, parent);
        }

        int getLeftHeight() {
            return null != left ? ((AVLTreeNode<E>) left).height : 0;
        }

        int getRightHeight() {
            return null != right ? ((AVLTreeNode<E>) right).height : 0;
        }

        void updateHeight() {
            height = Math.max(getLeftHeight(), getRightHeight()) + 1;
        }

        boolean isBalance() {
            return Math.abs(getLeftHeight() - getRightHeight()) <= 1;
        }

        AVLTreeNode<E> tallerChild() {
            int leftHeight = getLeftHeight();
            int rightHeight = getRightHeight();
            if (leftHeight > rightHeight) return (AVLTreeNode<E>) left;
            if (leftHeight < rightHeight) return (AVLTreeNode<E>) right;
            return (AVLTreeNode<E>) (isLeft() ? left : right);
        }
    }

}
