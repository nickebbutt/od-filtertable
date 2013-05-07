package com.od.filtertable.index;

import com.od.filtertable.trie.AbstractTrieNode;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 */
public class HashSetTrieNode<V> extends AbstractTrieNode<V, HashSet<V>> {

    public HashSetTrieNode(boolean isCaseSensitive) {
        super(isCaseSensitive);
    }

    public HashSetTrieNode(boolean isCaseSensitive, int depth, char key) {
        super(isCaseSensitive, depth, key);
    }

    @Override
    protected HashSet<V> createValuesCollection() {
        return new HashSet<V>();
    }

    @Override
    protected HashSet<V> getEmptyCollection() {
        return (HashSet<V>)Collections.emptySet();
    }

    @Override
    protected AbstractTrieNode<V, HashSet<V>> createChildNode(boolean caseSensitive, int depth, char c) {
        return new HashSetTrieNode<V>(caseSensitive, depth, c);
    }
}