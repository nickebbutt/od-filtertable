package com.od.filtertable.suffixtree.visitor;

import com.od.filtertable.suffixtree.SuffixTree;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 24/05/13
 * Time: 14:53
 */
public class CollectValuesVisitor<V> implements SuffixTreeVisitor<V> {

    private Collection<V> targetCollection;
    private int maxValueCount;
    private int valueCount;

    public CollectValuesVisitor(Collection<V> targetCollection) {
        this(targetCollection, Integer.MAX_VALUE);
    }

    public CollectValuesVisitor(Collection<V> targetCollection, int maxValueCount) {
        this.targetCollection = targetCollection;
        this.maxValueCount = maxValueCount;
    }

    public boolean visit(SuffixTree<V> suffixTree) {
        if ( suffixTree.isTerminalNode()) {
            Iterator<V> i = suffixTree.getValues().iterator();
            while(i.hasNext() && valueCount < maxValueCount) {
                targetCollection.add(i.next());
                valueCount++;
            }
        }
        return valueCount < maxValueCount;
    }

    public Collection<V> getTargetCollection() {
        return targetCollection;
    }

    public void visitComplete(SuffixTree<V> suffixTree) {
    }
}
