package com.od.filtertable.index;

import com.od.filtertable.trie.AbstractTrieNode;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 */
public class SimpleTrieNode<V> extends AbstractTrieNode<V, Set<V>> {

    public SimpleTrieNode(boolean isCaseSensitive) {
        super(isCaseSensitive);
    }

    public SimpleTrieNode(boolean isCaseSensitive, int depth, char key) {
        super(isCaseSensitive, depth, key);
    }

    @Override
    protected Set<V> createValuesCollection() {
        return new HashSet<V>();
    }

    @Override
    protected Set<V> getEmptyCollection() {
        return Collections.emptySet();
    }

    @Override
    protected AbstractTrieNode<V, Set<V>> createChildNode(boolean caseSensitive, int depth, char c) {
        return new SimpleTrieNode<V>(caseSensitive, depth, c);
    }
}