package com.od.filtertable.radixtree.map;

import com.od.filtertable.radixtree.*;
import com.od.filtertable.radixtree.sequence.CharSequenceWithIntTerminatorAdapter;
import com.od.filtertable.radixtree.sequence.CharSequenceWithTerminalNode;
import com.od.filtertable.radixtree.visitor.StringCompressionVisitor;

import java.util.Collection;
import java.util.LinkedList;

/**
 * User: nick
 * Date: 04/06/13
 * Time: 08:31
 */
public class RadixTreeMap<V> implements RestrictedMap<V> {
    
    private RadixTree<V> radixTree = new RadixTree<V>();
    
    private SingleValueSupplier<V> singleValueSupplier = new SingleValueSupplier<V>();
    
    private CharSequenceWithIntTerminatorAdapter intTerminatorAdapter = new CharSequenceWithIntTerminatorAdapter();
    private MutableSequence mutableSequence = new MutableSequence();

    private TreeConfig<V> treeConfig = new TreeConfig<V>(new ChildIteratorPool<V>(), singleValueSupplier);

    @Override
    public void put(CharSequence s, V value) {
        mutableSequence.setSegment(new CharSequenceWithTerminalNode(s));
        radixTree.add(mutableSequence, value, treeConfig);
    }

    /**
     * Remove value corresponding to key s
     * @return old value, if there was a value in the map for this key
     */
    @Override
    public V remove(CharSequence s) {
        mutableSequence.setSegment(new CharSequenceWithTerminalNode(s));
        return (V)radixTree.remove(mutableSequence, null, treeConfig);
    }

    @Override
    public V get(CharSequence s) {
        mutableSequence.setSegment(new CharSequenceWithTerminalNode(s));
        LinkedList<V> result = new LinkedList<V>();
        radixTree.get(mutableSequence, result, treeConfig);
        assert(result.size() < 2);
        return result.size() > 0 ? result.get(0) : null;
    }
    
    @Override
    public <E extends Collection<V>> E addStartingWith(CharSequence s, E collection) {
        intTerminatorAdapter.setCharSequence(s);
        mutableSequence.setSegment(intTerminatorAdapter); //do not add terminal node
        radixTree.get(mutableSequence, collection, treeConfig);
        return collection;
    }

    @Override
    public <E extends Collection<V>> E addStartingWith(CharSequence s, E collection, int maxItems) {
        intTerminatorAdapter.setCharSequence(s);
        mutableSequence.setSegment(intTerminatorAdapter); //do not add terminal node
        radixTree.get(mutableSequence, collection, maxItems, treeConfig);
        return collection;
    }

    @Override
    public void accept(TreeVisitor<V> v) {
        radixTree.accept(v, treeConfig);
    }
    
    public void compress() {
        StringCompressionVisitor v = new StringCompressionVisitor();
        radixTree.accept(v, treeConfig);
    }

    @Override
    public TreeConfig<V> getTreeConfig() {
        return treeConfig;
    }
}
