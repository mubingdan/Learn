package com.learn.tree;

import java.util.Comparator;

/**
 * 红黑树
 * 红黑树等价于4阶B树，B树中删除节点必然是最后一层的叶子节点
 *
 * @param <E>
 */
public class RBTree<E> extends BinarySearchTree<E> {

    static final boolean RED = true;

    static final boolean BLACK = false;

    public RBTree() {
        this(null);
    }

    public RBTree(Comparator<E> comparator) {
        super(comparator);
    }

    @Override
    public void afterAdd(TreeNode<E> node) {
        if (null == node) {
            return;
        }

        RBTreeNode<E> parent = (RBTreeNode<E>) node.parent;
        if (parent == null) {// 添加的是根节点，染黑即可
            black(node);
            return;
        }

        if (isBlack(parent)) {// 父节点是黑色只需染色即可,默认新添加的为红色，因此不需手动改色
            return;
        }

        RBTreeNode<E> grand = (RBTreeNode<E>) parent.parent;// 注意要把grand取出来，因为旋转过程可能导致grand的引用关系错乱（之前没有取出来发现旋转后没有效果）
        RBTreeNode<E> uncle = parent.sibling();
        if (isRed(uncle)) {// 叔父节点是red
            black(parent);
            black(uncle);
            red(grand);// 祖父节点修改为red当做新加入的节点进行循环操作
            afterAdd(grand);
            return;
        }

        // 叔父节点不是red
        if (parent.isLeft()) {// L
            if (node.isLeft()) {// LL 右旋转
                black(parent);
                red(grand);
                rotateRight(grand);
            } else {// LR parent 左旋转 grand 左旋转
                black(node);// 自己染成黑色，替换parent自己成为parent
                red(grand);// 原祖父节点是黑色的，现在通过旋转做为node的左子树，因此染成红色
                rotateLeft(parent);
                rotateRight(grand);
            }
        } else {// R
            if (node.isLeft()) {// RL
                black(node);// 自己染成黑色，替换parent自己成为parent
                red(grand);// 原祖父节点是黑色的，现在通过旋转做为node的左子树，因此染成红色
                rotateRight(parent);
                rotateLeft(grand);
            } else {// RR
                black(parent);
                red(grand);
                rotateLeft(grand);
            }
        }
    }

    @Override
    public void afterRemove(TreeNode<E> node) {
        if (null == node) return;

        // 删除的是黑色节点
        // 如果删除的黑色节点有两个子节点，不会直接删除，会使用其前驱节点或者后继节点替换值，真正被删除的是前驱或后继节点，
        // 前驱或后继节点只可能拥有一个子树或者没有子树，这里不在考虑这种情况
        if (isRed(node)) {// 删除红色叶子节点，直接删除（空节点也是黑色的）
            black(node);
            return;
        }

        RBTreeNode<E> parent = (RBTreeNode<E>) node.parent;
        if (parent == null) {// 删除根节点不需操作
            return;
        }

        // 删除黑色叶子节点
        boolean left = parent.left == null || node.isLeft();
        RBTreeNode<E> sibling = (RBTreeNode<E>) (left ? parent.right : parent.left);
        if (left) {// 删除的节点在左边
            if (isRed(sibling)) {// 兄弟节点是红色，进行改色并旋转
                black(sibling);
                red(parent);
                rotateLeft(parent);
                // 右旋转后parent的左子树变为兄弟节点
                sibling = (RBTreeNode<E>) parent.right;
            }

            // 兄弟节点是黑色
            if (isBlack(sibling.left) && isBlack(sibling.right)) {// 兄弟节点没有可以借的红色子节点, 父节点要向下合并
                boolean blackParent = isBlack(parent);
                black(parent);// 父节点染黑
                red(sibling);// 兄弟节点染红
                if (blackParent) {
                    afterRemove(parent);
                }
            } else {// 兄弟节点至少有一个红色节点
                if (isBlack(sibling.right)) {// 如果右边是黑的，代表左边是红的，兄弟要右旋转
                    rotateRight(sibling);
                    sibling = (RBTreeNode<E>) parent.right;
                }

                sibling.color = parent.color;
                black(sibling.right);
                black(parent);

                rotateLeft(parent);
            }
        } else {// 被删除节点在右边
            if (isRed(sibling)) {// 兄弟节点是红色，进行改色并旋转
                black(sibling);
                red(parent);
                rotateRight(parent);
                // 右旋转后parent的左子树变为兄弟节点
                sibling = (RBTreeNode<E>) parent.left;
            }

            // 兄弟节点是黑色
            if (isBlack(sibling.left) && isBlack(sibling.right)) {// 兄弟节点没有可以借的红色子节点, 父节点要向下合并
                boolean parentBlack =  parent.color == BLACK;
                black(parent);
                red(sibling);
                if (parentBlack) {
                    afterRemove(parent);
                }
            } else {// 兄弟节点至少有一个红色节点
                if (isBlack(sibling.left)) {// 如果左边是黑的，代表右边是红的，兄弟要左旋转
                    rotateLeft(sibling);
                    sibling = (RBTreeNode<E>) parent.left;
                }

                sibling.color = parent.color;
                black(sibling.left);
                black(parent);

                rotateRight(parent);
            }
        }

    }

    @Override
    public TreeNode<E> createTreeNode(E element, TreeNode<E> parent) {
        return new RBTreeNode<>(element, parent);
    }

    /**
     * 左旋转 （右子树做为新的父节点，右子树的左子树成为原父节点的右子树，原父节点成为其右子树的左子树
     *
     * @param node
     */
    private void rotateLeft(TreeNode<E> node) {
        if (null == node) return;

        TreeNode<E> parent = node.right;
        TreeNode<E> child = parent.left;
        node.right = child;
        parent.left = node;

        rotateAfter((RBTreeNode<E>) node, parent, child);
    }

    /**
     * 右旋转 （左子树做为新的父节点，左子树的右子树成为原父节点的左子树，原父节点成为其左子树的右子树）
     *
     * @param node
     */
    private void rotateRight(TreeNode<E> node) {
        if (null == node) return;

        TreeNode<E> parent = node.left;
        TreeNode<E> child = parent.right;
        node.left = child;
        parent.right = node;

        rotateAfter((RBTreeNode<E>) node, parent, child);
    }

    /**
     * 更新旋转后数据 （左右旋转之后都需要更新parent及更新高度，因此单独抽取出来一个方法）
     *
     * @param node   grand node
     * @param parent parent node
     * @param child  node
     */
    private void rotateAfter(RBTreeNode<E> node, TreeNode<E> parent, TreeNode<E> child) {
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
    }

    private boolean colorOf(TreeNode<E> node) {
        return null == node ? BLACK : ((RBTreeNode<E>) node).color;
    }

    private RBTreeNode<E> red(TreeNode<E> node) {
        if (node != null) {
            ((RBTreeNode<E>) node).color = RED;
        }
        return (RBTreeNode<E>) node;
    }

    private RBTreeNode<E> black(TreeNode<E> node) {
        if (null != node) {
            ((RBTreeNode<E>) node).color = BLACK;
        }

        return (RBTreeNode<E>) node;
    }

    private boolean isBlack(TreeNode<E> node) {
        return colorOf(node) == BLACK;
    }

    private boolean isRed(TreeNode<E> node) {
        return colorOf(node) == RED;
    }

    private static class RBTreeNode<E> extends TreeNode<E> {

        boolean color = RED;

        public RBTreeNode(E element, TreeNode<E> parent) {
            super(element, parent);
        }

        /**
         * 获取兄弟节点
         */
        RBTreeNode<E> sibling() {
            if (isLeft()) {
                return (RBTreeNode<E>) parent.right;
            }

            if (isRight()) {
                return (RBTreeNode<E>) parent.left;
            }

            return null;
        }

        @Override
        public String toString() {
            String str = "";
            if (color == RED) {
                str = "R_";
            }
            return str + element.toString();
        }
    }
}
