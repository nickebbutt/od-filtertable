package com.od.filtertable.radixtree;

public class TreeConfig<V> {
    
    private final ChildIteratorPool<V> iteratorPool;
    private final ValueSupplier<V> valueSupplier;
    private final char terminalNodeRangeStart;

    public TreeConfig(ChildIteratorPool<V> iteratorPool, ValueSupplier<V> valueSupplier) {
        this(iteratorPool, valueSupplier, CharUtils.DEFAULT_TERMINAL_CHAR);
    }

    public TreeConfig(ChildIteratorPool<V> iteratorPool, ValueSupplier<V> valueSupplier, char terminalNodeRangeStart) {
        this.iteratorPool = iteratorPool;
        this.valueSupplier = valueSupplier;
        this.terminalNodeRangeStart = terminalNodeRangeStart;
    }

    public ChildIteratorPool<V> getIteratorPool() {
        return iteratorPool;
    }

    public ValueSupplier<V> getValueSupplier() {
        return valueSupplier;
    }

    public boolean isTerminalNode(RadixTree<V> t) {
        return t.getLastChar() >= terminalNodeRangeStart;
    }
}
