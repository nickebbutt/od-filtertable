package com.od.filtertable.suffixtree.visitor;

import com.od.filtertable.suffixtree.SuffixTree;

/**
 * User: nick
 * Date: 23/05/13
 * Time: 09:31
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
