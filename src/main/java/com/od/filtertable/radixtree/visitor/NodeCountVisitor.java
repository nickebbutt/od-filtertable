package com.od.filtertable.radixtree.visitor;

import com.od.filtertable.radixtree.RadixTree;
import com.od.filtertable.radixtree.TreeVisitor;

/**
 * User: nick
 * Date: 23/05/13
 * Time: 09:31
 * 
 * Returns a count of nodes
 * This will include the root node, which has no label, if searching from root level
 */
public class NodeCountVisitor implements TreeVisitor {
    
    private int nodeCount;
    
    @Override
    public boolean visit(RadixTree radixTree) {
        nodeCount++;
        return true;
    }

    @Override
    public void visitComplete(RadixTree radixTree) {
    }
    
    public int getNodeCount() {
        return nodeCount;
    }
}
