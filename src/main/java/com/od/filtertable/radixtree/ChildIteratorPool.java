package com.od.filtertable.radixtree;

import java.util.Stack;

/**
 * User: nick
 * Date: 22/05/13
 * Time: 09:27
 */
public class ChildIteratorPool<E> {
    
    private static ThreadLocal<ChildIteratorPool> iteratorPoolThreadLocal = new ThreadLocal<ChildIteratorPool>() {
        public ChildIteratorPool initialValue() {
            return new ChildIteratorPool();
        }
    };
    
    private Stack<ChildIterator<E>> iterators = new Stack<ChildIterator<E>>();
    
    
    public ChildIterator<E> getIterator(RadixTree<E> parentNode, boolean isTerminal) {
        ChildIterator<E> result;
        if ( iterators.size() == 0) {
            result = new ChildIterator<E>();
        } else {
            result = iterators.pop();
        }
        result.setParent(parentNode, isTerminal);
        return result;
    }

    public void returnIterator(ChildIterator<E> i) {
        iterators.push(i);
    }
    
    public static <V> ChildIteratorPool<V> getIteratorPool() {
        return iteratorPoolThreadLocal.get();     
    }
}
