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

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Sep-2008
 * Time: 15:26:45
 *
 * A non-compact trie structure where each trie node represents a char from a CharSequence or String
 * e.g. if we add the strings 'fogs' and 'fox' the trie would look like this:
 *
 *         |-(g)-(s)
 * (f)-(o)-|
 *         |-(x)
 *
 * This stores values of type V at each node, in a Collection<V>
 *     
 * When adding to the index, we each value is added under a given key, or char sequence.
 * We can a)
 *   add the value into the collection for only the terminal/leaf node which represents the entire char sequence specified.
 *   or b)
 *   add the value for the node representing the entire sequence, plus for every prefix of the sequence (e.g. N, NO, NOD, NODE)
 *
 * b) is more expensive in memory, but does result in near-zero processing time when returning matches from prefix searches. 
 * 
 * If using a) the alternative way of supporting prefix searches would be to build the equivalent collection dynamically 
 * by iterating all sub-nodes of the node matching a prefix. This could be done by n = getTrieNode(prefix) and n.accept(visitor) 
 * This would be a lot less expensive in memory but more computationally expensive/slower when processing queries
 */
public interface CharTrie<V, C extends Collection<V>> {

    /**
     * Add the value to the trie node for the path matching key
     */
    void addValue(CharSequence key, V value);

    /**
     * Remove value from the trie node for the path matching key
     */
    void removeValue(CharSequence key, V value);

    /**
     * Add the value to the trie node for the path matching key, and to the nodes matching every prefix of key
     * e.g. for the string 'whazzup' we add the value to the nodes for:
     * 'w', 'wh', 'wha', 'whaz', 'whazz', 'whazzu', 'whazzup'
     */
    void addValueForAllPrefixes(CharSequence key, V value);

     /**
     * Remove the value from the trie node for the path matching key, and to the nodes for every prefix of key
     */
    void removeValueForAllPrefixes(CharSequence key, V value);

    /**
     * Add value to the trie nodes for path matching key, and to any parent nodes/prefixes which are at least minLength chars long
     */
    void addValueForPrefixesFromDepth(CharSequence key, V value, int minLength);

    /**
     * Remove value to the trie nodes for path matching key, and to any parent nodes/prefixes which are at least minLength chars long
     */
    void removeValueForPrefixesFromDepth(CharSequence key, V value, int minLength);
    
    /**
     * @return the values stored against the trie node for the path matching key
     * If values were added 'for prefixes' then this will include values stored for char sequences where key is a suffix
     */
    C getValues(CharSequence key);

    /**
     * This method adds all values stored for key and its substrings into targetCollection, by iterating the descendant 
     * nodes under the node matching key. This is only useful where values were not been added 'with prefixes', although 
     * performance will be worse due to the need to dynamically iterate and create the result set.
     * 
     * @targetCollection the collection into which result values will be collated 
     */
    <R extends Collection<V>> R getValuesWithPrefixes(CharSequence key, R targetCollection);


    /**
     * Similar to above method but returns only a maximum of maxMatches results, 
     **/
    <R extends Collection<V>> R getValuesWithPrefixes(CharSequence key, R targetCollection, int maxMatches);

    /**
     * @return the trie node representing the key, if it exists
     */
    CharTrie<V, C> getTrieNode(CharSequence key);

    /**
     * remove from the trie any child nodes at depth greater than maximumDepth
     */
    void trimToDepth(int maximumDepth);

    /**
     * Visit all nodes in the trie structure from this node
     */
    void accept(TrieVisitor<V, C> v);
    
    /**
     * Visit nodes in the trie structure which represent the char sequence or one of its prefixes, 
     * and are at least minLength nodes deep (or represent a prefix which is at least minLength chars long)
     */
    void accept(TrieVisitor<V, C> v, CharSequence key, int minDepth);

}
