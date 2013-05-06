package com.od.filtertable.index;

import com.od.filtertable.trie.AbstractTrieNode;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 */
public class IndexTrieNode<V> extends AbstractTrieNode<IndexedValue<V>, Set<IndexedValue<V>>> {

    public IndexTrieNode(boolean isCaseSensitive) {
        super(isCaseSensitive);
    }

    public IndexTrieNode(boolean isCaseSensitive, int depth, char key) {
        super(isCaseSensitive, depth, key);
    }

    @Override
    protected Set<IndexedValue<V>> createValuesCollection() {
        return new HashSet<IndexedValue<V>>();
    }

    @Override
    protected Set<IndexedValue<V>> getEmptyCollection() {
        return Collections.emptySet();
    }

    @Override
    protected AbstractTrieNode<IndexedValue<V>, Set<IndexedValue<V>>> createChildNode(boolean caseSensitive, int depth, char c) {
        return new IndexTrieNode<V>(caseSensitive, depth, c);
    }
}