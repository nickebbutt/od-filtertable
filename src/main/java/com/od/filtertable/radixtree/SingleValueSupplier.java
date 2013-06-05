package com.od.filtertable.radixtree;

import java.util.Collection;

/**
 * Support a single value per node
 */
class SingleValueSupplier<V> implements ValueSupplier<V> {

    public Object addValue(V value, Object currentValue) {
        return value;
    }

    public Object removeValue(V value, Object currentValue) {
        return null;
    }

    public void addValuesToCollection(Collection<V> collection, Object currentValue) {
        collection.add((V)currentValue);
    }
}
