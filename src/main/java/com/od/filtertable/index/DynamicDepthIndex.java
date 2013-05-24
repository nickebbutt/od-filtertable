package com.od.filtertable.index;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 *
 * An index which should be blindingly fast for prefix searches, since results for prefixes are pre-calculated
 *
 * This will return results for prefix-based searches in time proportional to the length of the search term, up to the initial index depth
 * - i.e. very fast indeed. 
 * 
 * The number of values added influences the amount of memory required, but not the search speed.
 * Memory requirement can be large, but can be limited by setting an initial depth and allowing dynamic indexing: 
 *
 * To satisfy searches using search terms which are longer than the initial depth, the index depth is dynamically increased on
 * the fly for those paths which match the searched term only. Since indexing speed is very fast, this generally performs well, 
 * and represents a good compromise between memory usage and search speed, especially where there is an even value distribution.
 * 
 * For quickest searching, set the initial depth as high as possible, based on available memory and performance goals
 */
public class DynamicDepthIndex<V> implements TrieIndex<V> {

    private final boolean indexSubstrings;
    private final IndexTrieNode<V> index;
    private final int initialDepth;
    
    private final MutableSequence mutableCharSequence = new MutableSequence();
    private final LinkedList<IndexedValue<V>> cellsToReindex = new LinkedList<IndexedValue<V>>();    
    
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * @param initialDepth       - initial depth to create index
     * @param isCaseSensitive    - support a case sensitive search
     * @param indexSubstrings    - e.g. if search term is AR, whether this matches BAR and ROAR, or just AR, ARK, etc
     */
    public DynamicDepthIndex(int initialDepth, boolean isCaseSensitive, boolean indexSubstrings) {
        this.index = new IndexTrieNode<V>(isCaseSensitive);
        this.initialDepth = initialDepth;
        this.indexSubstrings = indexSubstrings;
    }

    /**
     * If V is already in the index first remove it
     * Then store v into the index under CharSequence s
     */
    public void add(CharSequence s, V v) {
        lock.writeLock().lock();
        try {
            IndexedValue<V> val = new IndexedValue<V>(v);
            val.setIndexKey(s);
            doAdd(s, val);
        } finally {
            lock.writeLock().unlock();
        }

    }

    /**
     * Remove v from the index
     */
    public void remove(CharSequence s, V v) {
        lock.writeLock().lock();
        try {
            IndexedValue<V> val = new IndexedValue<V>(v);
            doRemove(s, val);
        } finally {
            lock.writeLock().unlock();
        }

    }

     /**
     * Find all the values associated with CharSequence s and any other CharSequence for which s is a prefix
     */
    public Collection<IndexedValue<V>> getIndexedValuesForPrefix(CharSequence s) {
        lock.readLock().lock();
        try {
            updateIndexForSequence(s);
            return index.getValues(s);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Find all the values associated with CharSequence s and any other CharSequence for which s is a prefix
     * 
     * If you only wish to find/iterate the matches consider using getIndexedValuesForPrefix which returns 
     * the values wrapped as an IndexValue but avoids the work required to populate targetCollection
     */
    public <R extends Collection<V>> R getValuesForPrefix(CharSequence s, R targetCollection) {
        Collection<IndexedValue<V>> c = getIndexedValuesForPrefix(s);
        for ( IndexedValue<V> i : c) {
            targetCollection.add(i.getValue());
        }
        return targetCollection;
    }

    public <R extends Collection<V>> R getValuesForPrefix(CharSequence s, R targetCollection, int maxMatches) {
        Collection<IndexedValue<V>> c = getIndexedValuesForPrefix(s);
        int matchCount = 0;
        for ( IndexedValue<V> i : c) {
            if(matchCount++ < maxMatches) {
                targetCollection.add(i.getValue());
            } else {
                break;
            }
        }
        return targetCollection;    
    }

    private void doAdd(CharSequence s, IndexedValue<V> val) {
        doAdd(s, val, initialDepth, 0);
    }

    /**
     * @param s              - the char sequence against which which we want to store val 
     * @param val            - value to add to the index
     * @param maxDepth       - maximum depth to index, i.e. index for the first n letters of the char sequence
     * @param startingDepth  - usually 0, to store val against all prefixes, but where we have already stored to depth n, may be n + 1 to save work 
     */
    private void doAdd(CharSequence s, IndexedValue<V> val, int maxDepth, int startingDepth) {
        mutableCharSequence.setSegment(s);
        int lastChar = indexSubstrings ? s.length() : 1;
        for ( int startChar = 0; startChar < lastChar; startChar ++) {
            int endChar = Math.min(startChar + maxDepth, s.length());
            mutableCharSequence.setStart(startChar);
            mutableCharSequence.setEnd(endChar);
            index.addValueForPrefixesFromDepth(mutableCharSequence, val, startingDepth);            
        }
        val.setIndexedDepth(maxDepth);        
    }

    private void doRemove(CharSequence s, IndexedValue<V> val) {
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
                doAdd(v.getIndexKey(), v, newIndexDepth, newIndexDepth);
            }
        }
    }


}
