package com.od.filtertable.radixtree.map;

import com.od.filtertable.radixtree.TreeConfig;
import com.od.filtertable.radixtree.TreeVisitor;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 10/06/13
 * Time: 21:25
 * To change this template use File | Settings | File Templates.
 */
public interface RestrictedMap<V> {
    
    void put(CharSequence s, V value);

    V remove(CharSequence s);

    V get(CharSequence s);

    <E extends Collection<V>> E addStartingWith(CharSequence s, E collection);

    <E extends Collection<V>> E addStartingWith(CharSequence s, E collection, int maxItems);

    void accept(TreeVisitor<V> v);

    void compress();

    TreeConfig<V> getTreeConfig();
}
