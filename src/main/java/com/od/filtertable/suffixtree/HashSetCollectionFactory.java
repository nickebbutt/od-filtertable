package com.od.filtertable.suffixtree;

import java.util.HashSet;

/**
 * User: nick
 * Date: 09/05/13
 * Time: 09:09
 */
public class HashSetCollectionFactory<V> implements CollectionFactory<V> {
    
    @Override
    public HashSet<V> createTerminalNodeCollection() {
        return new HashSet<V>();
    }
}
