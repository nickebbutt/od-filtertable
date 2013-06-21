package com.od.filtertable.radixtree;

public class TreeConfig<V> {
    
    private final ChildIteratorPool<V> iteratorPool;
    private final ValueSupplier<V> valueSupplier;

    public TreeConfig(ChildIteratorPool<V> iteratorPool, ValueSupplier<V> valueSupplier) {
        this.iteratorPool = iteratorPool;
        this.valueSupplier = valueSupplier;
    }

    public ChildIteratorPool<V> getIteratorPool() {
        return iteratorPool;
    }

    public ValueSupplier<V> getValueSupplier() {
        return valueSupplier;
    }
}
