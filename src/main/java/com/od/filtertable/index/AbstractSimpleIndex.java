package com.od.filtertable.index;

import com.od.filtertable.radixtree.CharSequenceWithIntTerminatorAdapter;

import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 24/05/13
 * Time: 15:51
 */
public abstract class AbstractSimpleIndex<V> implements TrieIndex<V> {
    
    protected final boolean indexSubstrings;
    private final MutableSequence mutableCharSequence = new MutableSequence();
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public AbstractSimpleIndex(boolean indexSubstrings) {
        this.indexSubstrings = indexSubstrings;
    }

    /**
     * If V is already in the index first remove it
     * Then store v into the index under CharSequence s
     */
    public void add(CharSequence s, V v) {
        lock.writeLock().lock();
        try {
            doAdd(s, v);
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
            doRemove(s, v);
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
    public <C extends Collection<V>> C getValuesForPrefix(CharSequence s, C targetCollection) {
        lock.readLock().lock();
        try {
            return doGetValuesWithPrefixes(s, targetCollection, Integer.MAX_VALUE);
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
    public <C extends Collection<V>> C getValuesForPrefix(CharSequence s, C targetCollection, int maxMatches) {
        lock.readLock().lock();
        try {
            return doGetValuesWithPrefixes(s, targetCollection, maxMatches);
        } finally {
            lock.readLock().unlock();
        }
    }

    protected abstract <C extends Collection<V>> C doGetValuesWithPrefixes(CharSequence s, C targetCollection, int maxValues);

    /**
     * Find all the values associated with the exact CharSequence s
     */
    public Collection<V> getValues(CharSequence s) {
        lock.readLock().lock();
        try {
            return doGetValues(s);
        } finally {
            lock.readLock().unlock();
        }    
    }

    protected abstract Collection<V> doGetValues(CharSequence s);

    /**
     * @param s - the char sequence against which which we want to store val 
     * @param val - value to add to the index
     */
    private void doAdd(CharSequence s, V val) {
        mutableCharSequence.setSegment(new CharSequenceWithIntTerminatorAdapter(s));
        int lastChar = indexSubstrings ? s.length() : 1;
        for ( int startChar = 0; startChar < lastChar; startChar ++) {
            int endChar = s.length();
            mutableCharSequence.setStart(startChar);
            mutableCharSequence.setEnd(endChar);
            addToIndex(mutableCharSequence, val);
        }
    }

    protected abstract void addToIndex(CharSequence s, V val);

    private void doRemove(CharSequence s, V val) {
        mutableCharSequence.setSegment(new CharSequenceWithIntTerminatorAdapter(s));
        int lastChar = indexSubstrings ? s.length() : 1;
        for ( int startChar = 0; startChar < lastChar; startChar ++) {
            int endChar = s.length();
            mutableCharSequence.setStart(startChar);
            mutableCharSequence.setEnd(endChar);
            removeFromIndex(mutableCharSequence, val);
        }
    }

    protected abstract void removeFromIndex(CharSequence s, V val);
}
