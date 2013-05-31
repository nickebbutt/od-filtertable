package com.od.filtertable.radixtree;

import com.od.filtertable.index.AbstractSimpleIndex;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 24/05/13
 * Time: 15:54
 */
public class SimpleSuffixTreeIndex<V> extends AbstractSimpleIndex<V> {

    private RadixTree<V> radixTree;

    public SimpleSuffixTreeIndex(RadixTree<V> radixTree, boolean indexSubstrings) {
        super(indexSubstrings);
        this.radixTree = radixTree;
    }
    
    public void add(CharSequence s, V v) {
        CharSequence c = CharUtils.addTerminalCharAndCheck(s);
        super.add(c, v);
    }

    @Override
    protected <R extends Collection<V>> R doGetValuesWithPrefixes(CharSequence s, R targetCollection, int maxValues) {
        return radixTree.get(s, targetCollection, maxValues);
    }

    @Override
    protected Collection<V> doGetValues(CharSequence s) {
        LinkedHashSet<V> result = new LinkedHashSet<V>();
        return radixTree.getValues(result);
    }

    @Override
    protected void addToIndex(CharSequence s, V val) {
        if ( s.length() > 1 /* not just the terminal char */) {
            radixTree.add(s, val);
        }
    }

    @Override
    protected void removeFromIndex(CharSequence s, V val) {
        radixTree.remove(s, val);
    }
}
