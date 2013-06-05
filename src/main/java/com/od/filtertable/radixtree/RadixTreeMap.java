package com.od.filtertable.radixtree;

import com.od.filtertable.index.MutableSequence;
import com.od.filtertable.radixtree.visitor.TreeVisitor;

import java.util.Collection;
import java.util.LinkedList;

/**
 * User: nick
 * Date: 04/06/13
 * Time: 08:31
 */
public class RadixTreeMap<V> {
    
    private RadixTree<V> radixTree = new RadixTree<V>();
    
    private SingleValueSupplier<V> singleValueSupplier = new SingleValueSupplier<V>();
    
    private MutableSequence mutableSequence = new MutableSequence();
    
    public void put(CharSequence s, V value) {
        mutableSequence.setSegment(new CharSequenceWithTerminalNode(s));
        radixTree.add(mutableSequence, value, singleValueSupplier);
    }
    
    public void remove(CharSequence s) {
        mutableSequence.setSegment(new CharSequenceWithTerminalNode(s));
        radixTree.remove(mutableSequence, null, singleValueSupplier);
    }

    public V get(CharSequence s) {
        mutableSequence.setSegment(new CharSequenceWithTerminalNode(s));
        LinkedList<V> result = new LinkedList<V>();
        radixTree.get(mutableSequence, result, singleValueSupplier);
        assert(result.size() < 2);
        return result.size() > 0 ? result.get(0) : null;
    }
    
    public <E extends Collection<V>> E getStartingWith(CharSequence s, E collection) {
        mutableSequence.setSegment(s); //do not add terminal node
        radixTree.get(mutableSequence, collection, singleValueSupplier);
        return collection;
    }

    public <E extends Collection<V>> E getStartingWith(CharSequence s, E collection, int maxItems) {
        mutableSequence.setSegment(s); //do not add terminal node
        radixTree.get(mutableSequence, collection, maxItems, singleValueSupplier);
        return collection;
    }

    public void accept(TreeVisitor<V> v) {
        radixTree.accept(v);
    }

}
