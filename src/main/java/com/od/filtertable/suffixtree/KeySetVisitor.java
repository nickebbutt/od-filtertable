package com.od.filtertable.suffixtree;

import java.util.ArrayList;
import java.util.List;

/**
 * User: nick
 * Date: 22/05/13
 * Time: 17:53
 * 
 * Collect all the keys present in the suffix tree
 */
public class KeySetVisitor<V> implements SuffixTreeVisitor<V> {
    
    private StringBuilder sb = new StringBuilder();
    
    private List<String> allKeys = new ArrayList<String>();
    
    @Override
    public boolean visit(SuffixTree<V> suffixTree) {
        //add the label chars
        sb.append(suffixTree.label);
        if (suffixTree.isTerminalNode()) {
            removeLastCharacter();
            allKeys.add(sb.toString());
        }
        return true;
    }

    @Override
    public void visitComplete(SuffixTree<V> suffixTree) {
        //remove the label chars
        int chars = suffixTree.label.length;
        if ( suffixTree.isTerminalNode()) {
            chars--;
        }
        
        for (int c=0; c < chars; c++) {
            removeLastCharacter();
        }
    }

    public List<String> getLabels() {
        return allKeys;
    }

    private void removeLastCharacter() {
        sb.deleteCharAt(sb.length() - 1);
    }
}
