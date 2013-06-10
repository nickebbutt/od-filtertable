package com.od.filtertable.radixtree;

import java.util.Collection;
import java.util.HashSet;

/**
 * User: nick
 * Date: 09/05/13
 * Time: 09:09
 */
public class HashSetValueSupplier<V> implements ValueSupplier<V> {
    
    private ValueSupplierResult<V> r = new ValueSupplierResult<V>();
    
    @Override
    public ValueSupplierResult<V> addValue(V value, Object currentValue) {
        HashSet<V> result  = currentValue == null ? new HashSet<V>() : (HashSet<V>)currentValue;
        result.add(value);

        r.payload = result;
        return r;
    }

    @Override
    public ValueSupplierResult<V> removeValue(V value, Object currentValue) {
        HashSet<V> s = (HashSet<V>)currentValue;
        boolean removed = s.remove(value);
        
        r.payload = s.size() == 0 ? null : s;
        r.result = removed ? value : null;
        return r;
    }

    @Override
    public void addValuesToCollection(Collection<V> collection, Object currentValue) {
        collection.addAll((HashSet<V>)currentValue);
    }
}
