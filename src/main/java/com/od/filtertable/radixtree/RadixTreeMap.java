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
    
    public void put(CharSequence s, V value) {
        radixTree.add(CharUtils.addTerminalCharAndCheck(s), value, singleValueSupplier);
    }
    
    public void remove(CharSequence s) {
        radixTree.remove(CharUtils.addTerminalCharAndCheck(s), null, singleValueSupplier);
    }

    public V get(CharSequence s) {
        CharSequence c = CharUtils.addTerminalCharAndCheck(s);
        LinkedList<V> result = new LinkedList<V>();
        radixTree.get(c, result, singleValueSupplier);
        assert(result.size() < 2);
        return result.size() > 0 ? result.get(0) : null;
    }
    
    public <E extends Collection<V>> E getStartingWith(CharSequence s, E collection) {
        CharSequence c = new MutableSequence(s); //do not add terminal node
        radixTree.get(c, collection, singleValueSupplier);
        return collection;
    }

    public <E extends Collection<V>> E getStartingWith(CharSequence s, E collection, int maxItems) {
        CharSequence c = new MutableSequence(s); //do not add terminal node
        radixTree.get(c, collection, maxItems, singleValueSupplier);
        return collection;
    }

    public void accept(TreeVisitor<V> v) {
        radixTree.accept(v);
    }

    /**
     * Support a single value per node
     */
    public static class SingleValueSupplier<V> implements ValueSupplier<V> {

        public Object addValue(V value, Object currentValue) {
            return value;
        }

        public Object removeValue(V value, Object currentValue) {
            return null;
        }

        public void addValuesToCollection(Collection<V> collection, Object currentValue) {
            collection.add((V)currentValue);
        }
    }
}
