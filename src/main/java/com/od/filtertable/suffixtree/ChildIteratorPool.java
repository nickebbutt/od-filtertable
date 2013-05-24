package com.od.filtertable.suffixtree;

import java.util.Stack;

/**
 * User: nick
 * Date: 22/05/13
 * Time: 09:27
 */
public class ChildIteratorPool<E> {
    
    private Stack<ChildIterator<E>> iterators = new Stack<ChildIterator<E>>();
    
    public ChildIterator<E> getIterator(SuffixTree<E> parentNode) {
        ChildIterator<E> result;
        if ( iterators.size() == 0) {
            result = new ChildIterator<E>(parentNode);
        } else {
            result = iterators.pop();
        }
        return result;
    }

    public void returnIterator(ChildIterator<E> i) {
        iterators.push(i);
    }
}
