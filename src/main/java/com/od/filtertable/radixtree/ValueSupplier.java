package com.od.filtertable.radixtree;

import java.util.Collection;

/**
 * User: nick
 * Date: 08/05/13
 * Time: 18:09
 * 
 * Some use cases for trie permit more than one value to be stored per node
 * 
 * Where only a single value per key is required, we don't want to force the nodes to maintain references to 
 * collection instances (containing single values), since this significantly increases memory requirements 
 * 
 * To avoid this, responsibility for determining the value type to be stored in terminal nodes is delegated to a 
 * ValueSupplier instance. The Object returned by addValue is stored in the node instances.
 * 
 * In the simple case, where we support only a single value per node, addValue will simply return 
 * the value V
 * 
 * In multiple value cases, the value returned will be a Collection<V> - where subsequent additions
 * take place, the existing collection instance will be supplied as the currentValue parameter, so that additional
 * values can be added.
 */
public interface ValueSupplier<V> {

    ValueSupplierResult addValue(V value, Object currentValue);

    /**
     * @return the value to be stored, which should be null if there are no remaining values stored for this node
     */
    ValueSupplierResult removeValue(V value, Object currentValue);

    
    void addValuesToCollection(Collection<V> collection, Object currentValue);
    
    
    public static class ValueSupplierResult<V> {
        
        Object payload;
        V result;
        
        public void clear() {
            payload = null;
            result = null;
        }
    }

}
