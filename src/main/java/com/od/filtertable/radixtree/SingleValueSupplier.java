package com.od.filtertable.radixtree;

import java.util.Collection;

/**
 * Support a single value per node
 */
public class SingleValueSupplier<V> implements ValueSupplier<V> {

    private ValueSupplierResult<V> result = new ValueSupplierResult<V>();
    
    public ValueSupplierResult<V> addValue(V value, Object currentValue) {
        result.payload = value;
        return result;
    }

    public ValueSupplierResult<V> removeValue(V value, Object currentValue) {
        result.payload = null;
        result.result = (V)currentValue;
        return result;
    }

    public void addValuesToCollection(Collection<V> collection, Object currentValue) {
        collection.add((V)currentValue);
    }
}
