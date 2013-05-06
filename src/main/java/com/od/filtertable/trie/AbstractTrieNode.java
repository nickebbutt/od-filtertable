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

package com.od.filtertable.trie;

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
       remove(key, value, key.length());
    }

    public void addValueForAllPrefixes(CharSequence key, V value) {
       add(key, value, 0);
    }

    public void removeValueForAllPrefixes(CharSequence key, V value) {
       remove(key, value, 0);
    }

    public void addValueForPrefixesFromDepth(CharSequence key, V value, int minLength) {
        add(key, value, minLength);
    }

    public void removeValueForPrefixesFromDepth(CharSequence key, V value, int minLength) {
        remove(key, value, minLength);
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


    private void add(CharSequence key, V value, int addValueMinDepth) {
        if ( depth >= addValueMinDepth ) {
            values.add(value);
        }
        
        if ( depth < key.length()) {
            AbstractTrieNode<V, C> child = getOrCreateChild(key);
            child.add(key, value, addValueMinDepth);
        }
    }

    private void remove(CharSequence key, V value, int removeValueMinDepth) {
        if ( depth >= removeValueMinDepth ) {
            values.remove(value);
        }
        
        if ( depth < key.length()) {
            AbstractTrieNode<V, C> child = getOrCreateChild(key);
            child.remove(key, value, removeValueMinDepth);
        }
    }

    /**
     * If values have been added 'with prefixes' this node will contain both values stored under the key for this node
     * (e.g NO) plus the values stored against all substrings, NODE, NOOK, NOODLE etc. 
     * 
     * Otherwise values will just be those stored against the key for this node exactly (NO)
     * 
     * @return values stored for this key
     */
    public C getValues(CharSequence key) {
        if ( depth == key.length() ) {
            return values;
        } else {
            AbstractTrieNode<V, C> n = getChild(getNextChar(key), false);
            return n == null ? getEmptyCollection() : n.getValues(key);
        }
    }

    public Collection<V> getValuesWithPrefixes(CharSequence key) {
        Collection<V> collection = createValuesCollection();
        return getValuesWithPrefixes(key, collection);
    }
    
    /**
     * This method returns all values stored for key and its substrings, by iterating the descendant nodes under the node matching key.
     * This is only useful where values have not been added 'with prefixes', although performance will be worse due to the 
     * need to dynamically iterate and create the result set.
     */
    @Override
    public Collection<V> getValuesWithPrefixes(CharSequence key, final Collection<V> targetCollection) {
        return getValuesWithPrefixes(key, targetCollection, Integer.MAX_VALUE);
    }

    @Override
    public Collection<V> getValuesWithPrefixes(CharSequence key, final Collection<V> targetCollection, final int maxMatches) {
        CharTrie<V,C> node = getTrieNode(key);
        if ( node != null) {
            node.accept(new TrieVisitor<V, C>() {
                int matchCount = 0;
                
                public boolean visit(CharTrie<V, C> trieNode) {
                    Collection<V> values = ((AbstractTrieNode)trieNode).values;
                    for ( V value : values) {
                        if (  matchCount++ < maxMatches ) {
                            targetCollection.add(value);    
                        } else {
                            break;
                        }
                    }
                    return matchCount < maxMatches;
                }
            });
        }
        return targetCollection;
    }
    
    public void accept(TrieVisitor<V, C> v) {
        boolean continueVisit = v.visit(this);
        LinkedListNode<V, C> currentChild = headNodeInChildList;
        while(continueVisit && currentChild != null) {
            currentChild.value.accept(v);
            currentChild = currentChild.nextNode;
        }
    }

    public void accept(TrieVisitor<V, C> v, CharSequence key, int minDepth) {
        if ( depth >= minDepth) {
            v.visit(this);
        }
        
        if ( depth < key.length() ) {
            AbstractTrieNode<V, C> n = getChild(getNextChar(key), false);
            if ( n != null) {
                n.accept(v, key, minDepth);
            }
        }
    }

    /**
     * @return the trie node representing the key, if it exists
     */
    public CharTrie<V, C> getTrieNode(CharSequence key) {
        CharTrie<V,C> result = null;
        if ( depth == key.length()) {
            return this;
        } else {
            AbstractTrieNode<V, C> n = getChild(getNextChar(key), false);
            if ( n != null) {
                result = n.getTrieNode(key);
            }
        }
        return result;
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
        
        //iterate the current children and insert new node into correct position by char value (maintain alphabetical ordering)
        while(currentChild != null) {
            if ( currentChild.value.key == c) {
                result = currentChild.value;
                break;
            } else if ( currentChild.value.key > c) {
                if ( addIfDoesNotExist) {
                    result = createChildNode(caseSensitive, depth + 1, c);
                    insertNewNode(result, lastChild, currentChild);
                }
                break;
            }
            lastChild = currentChild;
            currentChild = currentChild.nextNode;
        }
        
        if ( addIfDoesNotExist && result == null ) {
            result = createChildNode(caseSensitive, depth + 1, c);
            LinkedListNode<V, C> newListNode = new LinkedListNode<V, C>(result);
            if (lastChild != null) {
                lastChild.nextNode = newListNode;
            } else {
                headNodeInChildList = newListNode; 
            }
        }
        return result;
    }

    private void insertNewNode(AbstractTrieNode<V, C> newNode, LinkedListNode<V, C> lastChild, LinkedListNode<V, C> currentChild) {
        LinkedListNode<V, C> newListNode = new LinkedListNode<V, C>(newNode);
        if ( lastChild != null) {
            lastChild.nextNode = newListNode;
            newListNode.nextNode = currentChild;
        } else {
            newListNode.nextNode = headNodeInChildList;
            headNodeInChildList = newListNode;
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
