package com.od.filtertable.trie;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 06/05/13
 * Time: 14:29
 * To change this template use File | Settings | File Templates.
 */
public interface TrieVisitor<V, C extends Collection<V>> {

    /**
     * @return false to continue visiting, false to stop visiting
     */
    boolean visit(CharTrie<V, C> trieNode);
}
