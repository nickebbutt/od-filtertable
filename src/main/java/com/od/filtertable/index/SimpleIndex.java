package com.od.filtertable.index;

import com.od.filtertable.trie.AbstractTrieNode;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * 
 * An index which should be memory efficient but slower for prefix searches
 * 
 * This will return all the values for an exact match in time proportional to the length of the search term
 * i.e. very fast indeed
 * 
 * It will take longer to return values for prefix-based searches since calculation of matches for prefix
 * searches is done on the fly by iterating the tree structure, and is not pre-calculated.
 * 
 * This saves a lot of memory, but will be much slower, especially for short search strings.
 * Consider enforcing a minimum search term length, or switching to DynamicDepthIndex if this is a problem
 */
public class SimpleIndex<V> implements TrieIndex<V> {

    private final boolean indexSubstrings;
    private final AbstractTrieNode<V, ? extends Collection<V>> index;
    private final IdentityHashMap<V, IndexedValue<V>> identityHashMap = new IdentityHashMap<V, IndexedValue<V>>();
    
    private final MutableCharSequence mutableCharSequence = new MutableCharSequence();
    
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    /*
    * @param isCaseSensitive    - support a case sensitive search
    * @param indexSubstrings    - e.g. if search term is AR, whether this matches BAR and ROAR, or just AR, ARK, etc
    */
    public SimpleIndex(boolean isCaseSensitive, boolean indexSubstrings) {
        this(new HashSetTrieNode<V>(isCaseSensitive), indexSubstrings);
    }
    
   /*
   * @param trie               - the trie structure which will be used to maintain the index
   * @param indexSubstrings    - e.g. if search term is AR, whether this matches BAR and ROAR, or just AR, ARK, etc
   */
    public SimpleIndex(AbstractTrieNode<V, ? extends Collection<V>> trie, boolean indexSubstrings) {
        this.index = trie;
        this.indexSubstrings = indexSubstrings;
    }

    /**
     * If V is already in the index first remove it
     * Then store v into the index under CharSequence s
     */
    @Override
    public void addOrUpdate(V v, CharSequence s) {
        lock.writeLock().lock();
        try {
            IndexedValue<V> val = identityHashMap.get(v);
            if (val != null) {
                CharSequence indexKey = val.getIndexKey();
                boolean indexFieldChanged = !indexKey.equals(s);
                if (indexFieldChanged) {
                    remove(indexKey, v);
                }
            } else {
                val = new IndexedValue<V>(v);
                identityHashMap.put(v, val);
            }
            val.setIndexKey(s);
            add(s, v);
        } finally {
            lock.writeLock().unlock();
        }

    }

    /**
     * Remove v from the index
     */
    @Override
    public void remove(V v) {
        lock.writeLock().lock();
        try {
            IndexedValue<V> val = identityHashMap.get(v);
            if (val != null) {
                CharSequence lastIndexedString = val.getIndexKey();
                remove(lastIndexedString, v);
            }
        } finally {
            lock.writeLock().unlock();
        }

    }

    /**
     * Find all the values associated with CharSequence s and any other CharSequence for which s is a prefix
     * 
     * The SimpleIndex makes an additional guarantee that target collection will be populated with values in 
     * alphabetical ordering from the internal char structure, 
     * e.g. for the search term 'A' values stored against node 'A' will be added to targetCollection first, followed by 
     * 'AB' and 'AC'
     */
    public Collection<V> getValuesForPrefix(CharSequence s, Collection<V> targetCollection) {
        lock.readLock().lock();
        try {
            return index.getValuesWithPrefixes(s, targetCollection);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Find all the values associated with CharSequence s and any other CharSequence for which s is a prefix, up to 
     * a certain number of values
     *
     * The SimpleIndex makes an additional guarantee that target collection will be populated with values in 
     * alphabetical ordering from the internal char structure, 
     * e.g. for the search term 'A' values stored against node 'A' will be added to targetCollection first, followed by 
     * 'AB' and 'AC'
     */
    @Override
    public Collection<V> getValuesForPrefix(CharSequence s, Collection<V> targetCollection, int maxMatches) {
        lock.readLock().lock();
        try {
            return index.getValuesWithPrefixes(s, targetCollection, maxMatches);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Find all the values associated with the exact CharSequence s
     */
    public Collection<V> getValues(CharSequence s) {
        lock.readLock().lock();
        try {
            return index.getValues(s);
        } finally {
            lock.readLock().unlock();
        }    
    }

    /**
     * @param s - the char sequence against which which we want to store val 
     * @param val - value to add to the index
     */
    private void add(CharSequence s, V val) {
        mutableCharSequence.setSegment(s);
        int lastChar = indexSubstrings ? s.length() : 1;
        for ( int startChar = 0; startChar < lastChar; startChar ++) {
            int endChar = s.length();
            mutableCharSequence.setStart(startChar);
            mutableCharSequence.setEnd(endChar);
            index.addValue(mutableCharSequence, val);            
        }
    }

    private void remove(CharSequence s, V val) {
        mutableCharSequence.setSegment(s);
        int lastChar = indexSubstrings ? s.length() : 1;
        for ( int startChar = 0; startChar < lastChar; startChar ++) {
            int endChar = s.length();
            mutableCharSequence.setStart(startChar);
            mutableCharSequence.setEnd(endChar);
            index.removeValue(mutableCharSequence, val);
        }
    }
}
