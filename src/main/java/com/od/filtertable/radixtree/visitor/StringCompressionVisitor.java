package com.od.filtertable.radixtree.visitor;

import com.od.filtertable.radixtree.RadixTree;

import java.util.HashMap;

/**
 * User: nick
 * Date: 06/06/13
 * Time: 08:27
 * 
 * Reduce memory usage by using the same base sequence for each similar label
 */
public class StringCompressionVisitor implements TreeVisitor {
    
    private HashMap<CharSequence, RadixTree> instancePerLabel = new HashMap<CharSequence, RadixTree>();
    
    @Override
    public boolean visit(RadixTree radixTree) {
        String label = radixTree.getLabel();
        RadixTree existingNodeWithSameLabel = instancePerLabel.get(label);
        if ( existingNodeWithSameLabel == null ) {
            instancePerLabel.put(label, radixTree);
        } else {
            radixTree.setLabel(
                existingNodeWithSameLabel.getRootSequence(), 
                existingNodeWithSameLabel.getStart(), 
                existingNodeWithSameLabel.getEnd()
            );
        }
        return true;
    }

    @Override
    public void visitComplete(RadixTree radixTree) {
    }
}
