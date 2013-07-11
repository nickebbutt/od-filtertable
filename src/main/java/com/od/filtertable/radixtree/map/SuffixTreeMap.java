package com.od.filtertable.radixtree.map;

import com.od.filtertable.radixtree.*;
import com.od.filtertable.radixtree.sequence.CharSequenceWithAssignableTerminalChar;
import com.od.filtertable.radixtree.sequence.CharSequenceWithIntTerminatorAdapter;
import com.od.filtertable.radixtree.visitor.StringCompressionVisitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * User: nick
 * Date: 10/06/13
 * Time: 08:20
 * 
 * A Generalized suffix tree
 */
public class SuffixTreeMap<V> implements RestrictedMap<V> {
    
    private RadixTree<V> radixTree = new RadixTree<V>();
    
    //for terminators use the first unassigned unicode character
    //plane onwards
    private int lastTerminator = Character.MAX_VALUE + 1;

    private SingleValueSupplier<V> singleValueSupplier = new SingleValueSupplier<V>();

    private CharSequenceWithIntTerminatorAdapter intTerminatorAdapter = new CharSequenceWithIntTerminatorAdapter();
    private MutableSequence mutableSequence = new MutableSequence();

    private Map<CharSequence, CharSequenceWithAssignableTerminalChar> terminalNodesBySequence = new HashMap<CharSequence, CharSequenceWithAssignableTerminalChar>();
    
    private TreeConfig<V> treeConfig = new TreeConfig<V>(new ChildIteratorPool<V>(), singleValueSupplier);
    
    public void put(CharSequence s, V value) {
        int terminalChar = lastTerminator++;
        CharSequenceWithAssignableTerminalChar n = new CharSequenceWithAssignableTerminalChar(s, terminalChar);
        terminalNodesBySequence.put(s, n);
        mutableSequence.setSegment(n);
        addAllSuffixes(mutableSequence, n, value);
    }

    private void addAllSuffixes(MutableCharSequence s, CharSequenceWithAssignableTerminalChar n, V value) {
        for ( int c = 0; c < n.length() - 1; c++) {
            s.setStart(c);
            radixTree.add(mutableSequence, value, treeConfig);
        }
    }

    public V remove(CharSequence s) {
        V result = null;
        CharSequenceWithAssignableTerminalChar t = terminalNodesBySequence.get(s);
        if ( t != null) {
            terminalNodesBySequence.remove(s);
            mutableSequence.setSegment(t);
            result = removeAllSuffixes(mutableSequence, t);
        }
        return result;
    }

    private V removeAllSuffixes(MutableCharSequence s, CharSequenceWithAssignableTerminalChar t) {
        V value = null;
        for ( int c = 0; c < t.length() - 1; c++) {
            s.setStart(c);
            value = (V)radixTree.remove(mutableSequence, null, treeConfig);
        }
        return value;
    }

    public V get(CharSequence s) {
        LinkedList<V> result = null;
        CharSequenceWithAssignableTerminalChar c = terminalNodesBySequence.get(s);
        if ( c != null) {
            mutableSequence.setSegment(c);
            result = new LinkedList<V>();
            radixTree.get(mutableSequence, result, treeConfig);
            assert(result.size() < 2);
        }
        return result.size() > 0 ? result.get(0) : null;
    }

    public <E extends Collection<V>> E addStartingWith(CharSequence s, E collection) {
        intTerminatorAdapter.setCharSequence(s);
        mutableSequence.setSegment(intTerminatorAdapter); //do not add terminal node
        radixTree.get(mutableSequence, collection, treeConfig);
        return collection;
    }

    public <E extends Collection<V>> E addStartingWith(CharSequence s, E collection, int maxItems) {
        intTerminatorAdapter.setCharSequence(s);
        mutableSequence.setSegment(intTerminatorAdapter); //do not add terminal node
        radixTree.get(mutableSequence, collection, maxItems, treeConfig);
        return collection;
    }

    public void accept(TreeVisitor<V> v) {
        radixTree.accept(v, treeConfig);
    }

    @Override
    public void compress() {
        StringCompressionVisitor v = new StringCompressionVisitor();
        radixTree.accept(v, treeConfig);    
    }

    public TreeConfig<V> getTreeConfig() {
        return treeConfig;
    }

}
