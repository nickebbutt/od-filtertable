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
public class SimpleIndex<V> extends AbstractSimpleIndex<V> {

    private final AbstractTrieNode<V, ? extends Collection<V>> index;

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
        super(indexSubstrings);
        this.index = trie;
    }

    @Override
    protected <C extends Collection<V>> C doGetValuesWithPrefixes(CharSequence s, C targetCollection, int maxValues) {
        return index.getValuesWithPrefixes(s, targetCollection, maxValues);
    }

    @Override
    protected Collection<V> doGetValues(CharSequence s) {
        return index.getValues(s);
    }

    @Override
    protected void addToIndex(CharSequence s, V val) {
        index.addValue(s, val);
    }

    @Override
    protected void removeFromIndex(CharSequence s, V val) {
        index.removeValue(s, val);
    }
}
