package com.od.filtertable.index;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 */
public class VariableDepthIndex<V> {

    private final boolean indexSubstrings;
    private VariableDepthTrieNode<V> index;
    private int initialDepth;
    private IdentityHashMap<V, IndexedValue<V>> identityHashMap = new IdentityHashMap<V, IndexedValue<V>>();
    
    private MutableCharSequence mutableCharSequence = new MutableCharSequence();
    private LinkedList<IndexedValue<V>> cellsToReindex = new LinkedList<IndexedValue<V>>();    
    
    public VariableDepthIndex(Index index) {
        this.index = new VariableDepthTrieNode<V>(index.isCaseSensitive());
        initialDepth = index.getInitialDepth();
        indexSubstrings = index.isIndexSubstrings();
    }

    /**
     * If V is already in the index first remove it
     * Then store v into the index under CharSequence s
     */
    public synchronized void addOrUpdate(V v, CharSequence s) {
        IndexedValue<V> val = identityHashMap.get(v);
        if ( val != null ) {
            CharSequence indexKey = val.getIndexKey();
            boolean indexFieldChanged = ! indexKey.equals(s);
            if ( indexFieldChanged ) {
                remove(indexKey, val);
            }
        } else {
            val = new IndexedValue<V>(v);
            identityHashMap.put(v, val);
        }
        val.setIndexKey(s);
        add(s, val);
    }

    /**
     * Remove v from the index
     */
    public synchronized void remove(V v) {
        IndexedValue<V> val = identityHashMap.get(v);
        if ( val != null ) {
            CharSequence lastIndexedString = val.getIndexKey();
            remove(lastIndexedString, val);
        }    
    }

    /**
     * Find all the values assoicated with CharSequence s
     */
    public Collection<IndexedValue<V>> getValues(CharSequence s) {
        updateIndexForSequence(s);
        return index.getValues(s);
    }

    private void add(CharSequence s, IndexedValue<V> val) {
        add(s, val, initialDepth);
    }

    private void add(CharSequence s, IndexedValue<V> val, int depth) {
        mutableCharSequence.setSegment(s);
        int lastChar = indexSubstrings ? s.length() : 1;
        for ( int startChar = 0; startChar < lastChar; startChar ++) {
            int endChar = Math.min(startChar + depth, s.length());
            mutableCharSequence.setStart(startChar);
            mutableCharSequence.setEnd(endChar);
            index.addValueForAllPrefixes(mutableCharSequence, val);            
        }
        val.setIndexedDepth(depth);        
    }

    private void remove(CharSequence s, IndexedValue<V> val) {
        int depth = val.getIndexedDepth();
        mutableCharSequence.setSegment(s);
        int lastChar = indexSubstrings ? s.length() : 1;
        for ( int startChar = 0; startChar < lastChar; startChar ++) {
            int endChar = Math.min(startChar + depth, s.length());
            mutableCharSequence.setStart(startChar);
            mutableCharSequence.setEnd(endChar);
            index.removeValueForAllPrefixes(mutableCharSequence, val);
        }
    }

    /**
     * Make sure that the index is built out to the depth required by this char sequence
     * if s is > than the initial index depth, we create the remaining index nodes dynamically / lazily.
     */
    private void updateIndexForSequence(CharSequence s) {
        if ( s.length() > initialDepth) {
            CharSequence subString = s.subSequence(0, s.length() - 1);
            updateIndexForSequence(subString);

            int newIndexDepth = s.length();

            Collection<IndexedValue<V>> cellCollection = index.getValues(subString);
            //have to add tol list to avoid concurrent mod
            for ( IndexedValue<V> v : cellCollection ) {
                if ( v.getIndexedDepth() < newIndexDepth ) {
                    cellsToReindex.add(v);
                }
            }

            while(cellsToReindex.size() > 0) {
                IndexedValue<V> v = cellsToReindex.remove(0);
                add(v.getIndexKey(), v, newIndexDepth);
            }
        }
    }


}
