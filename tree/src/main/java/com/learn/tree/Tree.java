package com.learn.tree;

public interface Tree<E> {

    int size();

    boolean isEmpty();

    void add(E element);

    void remove(E element);

    void clear();

    boolean contains(E element);

}
