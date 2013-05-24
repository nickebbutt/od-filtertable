package com.od.filtertable.index;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 06/05/13
 * Time: 15:54
 * 
 * An index structure based on the CharTrie
 */
public interface TrieIndex<V> {

    /**
     * If V is already in the index first remove it
     * Then store v into the index under CharSequence s
     */
    void add(CharSequence s, V v);

    /**
     * If V is in the index remove it
     */
    void remove(CharSequence s, V v);

    /**
     * Find all the values associated with CharSequence s and any other CharSequence for which s is a prefix
     */
    <C extends Collection<V>> C getValuesForPrefix(CharSequence s, C targetCollection);

    
    <C extends Collection<V>> C getValuesForPrefix(CharSequence s, C targetCollection, int maxMatches);
}
