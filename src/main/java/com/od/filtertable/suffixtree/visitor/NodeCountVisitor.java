package com.od.filtertable.suffixtree.visitor;

import com.od.filtertable.suffixtree.SuffixTree;

/**
 * User: nick
 * Date: 23/05/13
 * Time: 09:31
 * 
 * Returns a count of nodes
 * This will include the root node, which has no label, if searching from root level
 */
public class NodeCountVisitor implements SuffixTreeVisitor {
    
    private int nodeCount;
    
    @Override
    public boolean visit(SuffixTree suffixTree) {
        nodeCount++;
        return true;
    }

    @Override
    public void visitComplete(SuffixTree suffixTree) {
    }
    
    public int getNodeCount() {
        return nodeCount;
    }
}
