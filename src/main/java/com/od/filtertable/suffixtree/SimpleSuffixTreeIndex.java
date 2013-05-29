package com.od.filtertable.suffixtree;

import com.od.filtertable.index.AbstractSimpleIndex;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 24/05/13
 * Time: 15:54
 */
public class SimpleSuffixTreeIndex<V> extends AbstractSimpleIndex<V> {

    private SuffixTree<V> suffixTree;

    public SimpleSuffixTreeIndex(SuffixTree<V> suffixTree, boolean indexSubstrings) {
        super(indexSubstrings);
        this.suffixTree = suffixTree;
    }
    
    public void add(CharSequence s, V v) {
        CharSequence c = CharUtils.addTerminalCharAndCheck(s);
        super.add(c, v);
    }

    @Override
    protected <R extends Collection<V>> R doGetValuesWithPrefixes(CharSequence s, R targetCollection, int maxValues) {
        return suffixTree.get(s, targetCollection, maxValues);
    }

    @Override
    protected Collection<V> doGetValues(CharSequence s) {
        Collection<V> collection = suffixTree.getCollectionFactory().createTerminalNodeCollection();
        return suffixTree.get(s, collection);
    }

    @Override
    protected void addToIndex(CharSequence s, V val) {
        if ( s.length() > 1) {
            suffixTree.add(s, val);
        }
    }

    @Override
    protected void removeFromIndex(CharSequence s, V val) {
        suffixTree.remove(s, val);
    }
}
