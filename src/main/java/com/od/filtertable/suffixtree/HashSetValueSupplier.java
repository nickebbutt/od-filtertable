package com.od.filtertable.suffixtree;

import java.util.Collection;
import java.util.HashSet;

/**
 * User: nick
 * Date: 09/05/13
 * Time: 09:09
 */
public class HashSetValueSupplier<V> implements ValueSupplier<V> {
    
    @Override
    public Object addValue(V value, Object currentValue) {
        HashSet<V> result  = currentValue == null ? new HashSet<V>() : (HashSet<V>)currentValue;
        result.add(value);
        return result;
    }

    @Override
    public Object removeValue(V value, Object currentValue) {
        HashSet<V> s = (HashSet<V>)currentValue;
        s.remove(value);
        return s.size() == 0 ? null : s;
    }

    @Override
    public void addValuesToCollection(Collection<V> collection, Object currentValue) {
        collection.addAll((HashSet<V>)currentValue);
    }
}
