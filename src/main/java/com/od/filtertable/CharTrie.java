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
 * The stores values of type V at each node, in a Collection<V>
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
     * Add a value to the trie node for the path matching key, and to the nodes 
     * matching every prefix of key which is at least minDepth chars long
     * 
     * This can be used as an optimisation where we know we have already added
     * the value to the trie for all prefixes which are < minDepth in length
     * 
     * @param key
     * @param value
     * @param minDepth
     */
    void addValueForAllPrefixes(CharSequence key, V value, int minDepth);

    /**
     * Remove the value from the trie node for the path matching key, and to the nodes for every prefix of key
     */
    void removeValueForAllPrefixes(CharSequence key, V value);

    /**
     * @return the values stored against the trie node for the path matching key
     */
    C getValues(CharSequence key);

    /**
     * remove from the trie any child nodes at depth greater than maximumDepth
     */
    void trimToDepth(int maximumDepth);

}
