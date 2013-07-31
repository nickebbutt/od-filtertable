package com.od.filtertable.radixtree;

/**
 * User: nick
 * Date: 22/05/13
 * Time: 17:28
 */
public interface TreeVisitor<V> {

    /**
     * @return true if visitation should continue
     */
    boolean visit(RadixTree<V> radixTree);
    
    void visitComplete(RadixTree<V> radixTree);
}
