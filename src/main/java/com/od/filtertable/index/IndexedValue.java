package com.od.filtertable.index;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * 
 * A value stored in the index, along with the string last used to store it in the index,
 * and the depth to which the index was populated
 */
public class IndexedValue<V> {
    
    private int indexedDepth;
    private V value;
    private CharSequence indexKey;

    public IndexedValue(V value) {
        this.value = value;
    }

    public int getIndexedDepth() {
        return indexedDepth;
    }

    public void setIndexedDepth(int indexedDepth) {
        this.indexedDepth = indexedDepth;
    }

    public CharSequence getIndexKey() {
        return indexKey;
    }

    public void setIndexKey(CharSequence indexKey) {
        this.indexKey = indexKey;
    }

    public V getValue() {
        return value;
    }

}
