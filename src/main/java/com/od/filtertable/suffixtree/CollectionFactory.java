package com.od.filtertable.suffixtree;

import java.util.Collection;

/**
 * User: nick
 * Date: 08/05/13
 * Time: 18:09
 */
public interface CollectionFactory<V> {
    
    Collection<V> createTerminalNodeCollection();
}
