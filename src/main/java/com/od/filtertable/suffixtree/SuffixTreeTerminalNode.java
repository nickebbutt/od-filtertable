package com.od.filtertable.suffixtree;

import com.od.filtertable.index.MutableCharSequence;

import java.util.Collection;

/**
 * User: nick
 * Date: 07/05/13
 * Time: 19:00
 */
public class SuffixTreeTerminalNode<V> extends AbstractSuffixTreeNode<V> {
    
    private SuffixTreeTerminalNode nextPeer;

    private Collection<V> values;

    public SuffixTreeTerminalNode(CharSequence s) {
        super(s);
    }

    public void addValue(CollectionFactory<V> collectionFactory, V value) {
        if ( values == null) {
            values = collectionFactory.createTerminalNodeCollection();
        }
        values.add(value);
    }

    public Collection<V> get(MutableCharSequence s, Collection<V> targetCollection) {
        if ( s.length() <= CharUtils.getSharedPrefixCount(s, label) ) {
            targetCollection.addAll(values);        
        } 
        return targetCollection;
    }

    public Collection<V> get(CharSequence c, Collection<V> targetCollection) {
        throw new UnsupportedOperationException("Should never call this get on a terminal node");
    }
}
