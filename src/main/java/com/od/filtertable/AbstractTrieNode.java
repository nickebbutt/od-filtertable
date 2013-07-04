/**
 *  Copyright (C) Nick Ebbutt September 2009
 *
 *  This file is part of ObjectDefinitions Ltd. FilterTable.
 *  nick@objectdefinitions.com
 *  http://www.objectdefinitions.com/filtertable
 *
 *  FilterTable is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ObjectDefinitions Ltd. FilterTable is distributed in the hope that it will
 *  be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with ObjectDefinitions Ltd. FilterTable.
 *  If not, see <http://www.gnu.org/licenses/>.
 */

package com.od.filtertable;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Sep-2008
 * Time: 15:18:32
 */
public abstract class AbstractTrieNode<V, C extends Collection<V>> implements CharTrie<V,C> {

    private boolean caseSensitive;
    private int depth;
    private char key;
    private LinkedListNode<V, C> headNodeInChildList;
    private C values;

    public AbstractTrieNode(boolean isCaseSensitive) {
        caseSensitive = isCaseSensitive;
        values = createValuesCollection();
    }

    public AbstractTrieNode(boolean isCaseSensitive, int depth, char key) {
        caseSensitive = isCaseSensitive;
        this.depth = depth;
        this.key = key;
        values = createValuesCollection();
    }

    public void addValue(CharSequence key, V value) {
       add(key, value, key.length());
    }

    public void removeValue(CharSequence key, V value) {
       remove(key, value, false);
    }
    
    public void addValueForAllPrefixes(CharSequence key, V value) {
        add(key, value, 0);
    }
    
    public void addValueForAllPrefixes(CharSequence key, V value, int minDepth) {
       add(key, value, minDepth);
    }

    public void removeValueForAllPrefixes(CharSequence key, V value) {
       remove(key, value, true);
    }

    public void trimToDepth(int maximumDepth) {
        if ( depth == maximumDepth ) {
            headNodeInChildList = null;
        } else {
            LinkedListNode<V, C> currentChild = headNodeInChildList;
            while( currentChild != null ) {
                currentChild.value.trimToDepth(maximumDepth);
                currentChild = currentChild.nextNode;
            }
        }
    }

    abstract protected C createValuesCollection();


    private void add(CharSequence key, V value, int minDepth) {
        if ( depth == key.length() ) {
            values.add(value);
        } else {
            if ( depth >= minDepth ) {
                values.add(value);
            }
            AbstractTrieNode<V, C> child = getOrCreateChild(key);
            child.add(key, value, minDepth);
        }
    }

    private void remove(CharSequence key, V value, boolean removeForAllPrefixes) {
        if ( depth == key.length()) {
            values.remove(value);
        } else {
            if ( removeForAllPrefixes ) {
                values.remove(value);
            }
            AbstractTrieNode<V, C> child = getOrCreateChild(key);
            child.remove(key, value, removeForAllPrefixes);
        }
    }

    public C getValues(CharSequence key) {
        if ( depth == key.length() ) {
            return values;
        } else {
            AbstractTrieNode<V, C> n = getChild(getNextChar(key), false);
            return n == null ? getEmptyCollection() : n.getValues(key);
        }
    }

    abstract protected C getEmptyCollection();    

    private AbstractTrieNode<V, C> getOrCreateChild(CharSequence key) {
        char c = getNextChar(key);
        return getChild(c, true);
    }

    private char getNextChar(CharSequence key) {
        return caseSensitive ? key.charAt(depth) : Character.toLowerCase(key.charAt(depth));
    }

    private AbstractTrieNode<V, C> getChild(char c, boolean addIfDoesNotExist) {
        AbstractTrieNode<V, C> result = null;

        LinkedListNode<V, C> lastChild = null;
        LinkedListNode<V, C> currentChild = headNodeInChildList;
        while(currentChild != null) {
            if ( currentChild.value.key == c) {
                result = currentChild.value;
                break;
            }
            lastChild = currentChild;
            currentChild = currentChild.nextNode;
        }

        if ( result == null && addIfDoesNotExist ) {
            result = createChildNode(caseSensitive, depth + 1, c);
            appendToChildList(lastChild, result);
        }
        return result;
    }

    private void appendToChildList(LinkedListNode<V, C> lastChild, AbstractTrieNode<V, C> result) {
        LinkedListNode<V, C> newListNode = new LinkedListNode<V, C>(result);
        if ( lastChild == null ) {
            headNodeInChildList = newListNode;
        } else {
            lastChild.nextNode = newListNode;
        }
    }

    protected abstract AbstractTrieNode<V,C> createChildNode(boolean caseSensitive, int depth, char c);

    public String toString() {
        return "trieNode: " + String.valueOf(key);
    }

    /**
     * Why use this and not java.util.LinkedList?
     * This is on the critical path and creating Iterator instance each time we need to iterate results in a lot of
     * gc and object cycling. LinkedList.Entry is private, so there is no alternative to using the iterator
     */
    private static class LinkedListNode<V, C extends Collection<V>> {
        AbstractTrieNode<V, C> value;
        LinkedListNode<V, C> nextNode;

        public LinkedListNode() {
        }

        public LinkedListNode(AbstractTrieNode<V, C> value) {
            this.value = value;
        }
    }
}
