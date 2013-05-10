package com.od.filtertable.suffixtree;

import com.od.filtertable.index.MutableCharSequence;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * User: nick
 * Date: 08/05/13
 * Time: 18:04
 */
public interface SuffixTreeNode<V> {
    char[] getLabel();

    Collection<V> get(MutableCharSequence s, Collection<V> targetCollection);

    Collection<V> get(CharSequence c, Collection<V> targetCollection);

    void setNextPeer(SuffixTreeNode<V> node);

    SuffixTreeNode<V> getNextPeer();
}
