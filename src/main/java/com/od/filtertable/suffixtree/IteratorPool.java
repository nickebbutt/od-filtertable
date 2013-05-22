package com.od.filtertable.suffixtree;

/**
 * User: nick
 * Date: 22/05/13
 * Time: 09:24
 * 
 * Avoid creating thousands of iterators, by reusing instances
 */
public interface IteratorPool<E> {
    
    ChildNodeIterator<E> getIterator(SuffixTree<E> parentNode);
    
    void returnIterator(ChildNodeIterator<E> i);
}
