package com.od.filtertable.radixtree;

import com.od.filtertable.index.MutableCharSequence;
import com.od.filtertable.index.MutableSequence;

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
public class SuffixTree<V> {
    
    private RadixTree<V> radixTree = new RadixTree<V>();
    
    //for terminators use the first unassigned unicode character
    //plane onwards
    private char startOfTerminalCharRange = (char)0x30000;
    private char lastTerminator = startOfTerminalCharRange;

    private SingleValueSupplier<V> singleValueSupplier = new SingleValueSupplier<V>();

    private MutableSequence mutableSequence = new MutableSequence();

    private Map<CharSequence, CharSequenceWithAssignableTerminalChar> terminalNodesBySequence = new HashMap<CharSequence, CharSequenceWithAssignableTerminalChar>();
    
    public void put(CharSequence s, V value) {
        char terminalChar = lastTerminator++;
        if ( terminalChar < startOfTerminalCharRange ) {
            throw new UnsupportedOperationException("Adding more than " + (Character.MAX_VALUE - startOfTerminalCharRange) + " items not supported");
        }
        CharSequenceWithAssignableTerminalChar n = new CharSequenceWithAssignableTerminalChar(s, terminalChar);
        terminalNodesBySequence.put(s, n);
        mutableSequence.setSegment(n);
        addAllSuffixes(mutableSequence, n, value);
    }

    private void addAllSuffixes(MutableCharSequence s, CharSequenceWithAssignableTerminalChar n, V value) {
        for ( int c = 0; c < s.length() - 1; c++) {
            s.setStart(c);
            radixTree.add(mutableSequence, value, singleValueSupplier);
        }
    }

    public void remove(CharSequence s) {
        CharSequenceWithAssignableTerminalChar t = terminalNodesBySequence.get(s);
        if ( t != null) {
            terminalNodesBySequence.remove(s);
            mutableSequence.setSegment(t);
            removeAllSuffixes(mutableSequence);
        }
    }

    private void removeAllSuffixes(MutableCharSequence s) {
        for ( int c = 0; c < s.length() - 1; c++) {
            s.setStart(c);
            radixTree.remove(mutableSequence, null, singleValueSupplier);
        }
    }

    public V get(CharSequence s) {
        CharSequenceWithAssignableTerminalChar c = terminalNodesBySequence.get(s);
        LinkedList<V> result = null;
        if ( c != null) {
            mutableSequence.setSegment(c);
            result = new LinkedList<V>();
            radixTree.get(mutableSequence, result, singleValueSupplier);
            assert(result.size() < 2);
        }
        return result.size() > 0 ? result.get(0) : null;
    }

    public <E extends Collection<V>> E addStartingWith(CharSequence s, E collection) {
        mutableSequence.setSegment(s); //do not add terminal node
        radixTree.get(mutableSequence, collection, singleValueSupplier);
        return collection;
    }

    public <E extends Collection<V>> E addStartingWith(CharSequence s, E collection, int maxItems) {
        mutableSequence.setSegment(s); //do not add terminal node
        radixTree.get(mutableSequence, collection, maxItems, singleValueSupplier);
        return collection;
    }
}
