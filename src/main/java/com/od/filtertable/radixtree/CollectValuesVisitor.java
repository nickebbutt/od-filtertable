package com.od.filtertable.radixtree;

import com.od.filtertable.radixtree.RadixTree;
import com.od.filtertable.radixtree.TreeConfig;
import com.od.filtertable.radixtree.TreeVisitor;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 24/05/13
 * Time: 14:53
 */
public class CollectValuesVisitor<V> implements TreeVisitor<V> {

    private Collection<V> targetCollection;
    private int maxValueCount;
    private TreeConfig<V> treeConfig;

    public CollectValuesVisitor(Collection<V> targetCollection, TreeConfig<V> treeConfig) {
        this(targetCollection, Integer.MAX_VALUE, treeConfig);
    }

    public CollectValuesVisitor(Collection<V> targetCollection, int maxValueCount, TreeConfig<V> treeConfig) {
        this.treeConfig = treeConfig;
        this.targetCollection = new ValueLimitingCollectionWrapper<V>(targetCollection, maxValueCount);
        this.maxValueCount = maxValueCount;
    }

    public boolean visit(RadixTree<V> radixTree) {
        if ( radixTree.isTerminalNode()) {
            radixTree.getValues(targetCollection, treeConfig);
        }
        return targetCollection.size() < maxValueCount;
    }

    public void visitComplete(RadixTree<V> radixTree) {
    }

    /**
     * Wrap a collection to limit the number of values which can be added
     */
    private static class ValueLimitingCollectionWrapper<V> implements Collection<V> {
        
        private Collection<V> collection;
        private int maxValueCount;

        private ValueLimitingCollectionWrapper(Collection<V> collection, int maxValueCount) {
            this.collection = collection;
            this.maxValueCount = maxValueCount;
        }

        public int size() {
            return collection.size();
        }

        public boolean isEmpty() {
            return collection.isEmpty();
        }

        public boolean contains(Object o) {
            return collection.contains(o);
        }

        public Iterator<V> iterator() {
            return collection.iterator();
        }

        public Object[] toArray() {
            return collection.toArray();
        }

        public <T> T[] toArray(T[] a) {
            return collection.toArray(a);
        }

        public boolean add(V v) {
            boolean result = false;
            if ( collection.size() < maxValueCount ) {
                result = collection.add(v);
            }
            return result;
        }

        public boolean remove(Object o) {
            return collection.remove(o);
        }

        public boolean containsAll(Collection<?> c) {
            return collection.containsAll(c);
        }

        public boolean addAll(Collection<? extends V> c) {
            boolean result = false;
            for ( V value : c) {
                result |= add(value);
            }
            return result;
        }

        public boolean removeAll(Collection<?> c) {
            return collection.removeAll(c);
        }

        public boolean retainAll(Collection<?> c) {
            return collection.retainAll(c);
        }

        public void clear() {
            collection.clear();
        }

        public boolean equals(Object o) {
            return collection.equals(o);
        }

        public int hashCode() {
            return collection.hashCode();
        }
    } 
}
