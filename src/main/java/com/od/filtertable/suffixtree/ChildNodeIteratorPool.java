package com.od.filtertable.suffixtree;

import java.util.Stack;

/**
 * User: nick
 * Date: 22/05/13
 * Time: 09:27
 */
public class ChildNodeIteratorPool<E> implements IteratorPool<E> {
    
    private Stack<ChildNodeIterator<E>> iterators = new Stack<ChildNodeIterator<E>>();
    
    @Override
    public ChildNodeIterator<E> getIterator(SuffixTree<E> parentNode) {
        ChildNodeIterator<E> result;
        if ( iterators.size() == 0) {
            result = new ChildNodeIterator<E>(parentNode);
        } else {
            result = iterators.pop();
        }
        return result;
    }

    @Override
    public void returnIterator(ChildNodeIterator<E> i) {
        iterators.push(i);
    }
}
