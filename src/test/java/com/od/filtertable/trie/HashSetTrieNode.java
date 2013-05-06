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

import com.od.filtertable.trie.AbstractTrieNode;

import java.util.Set;
import java.util.Collections;
import java.util.HashSet;


/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 10-Sep-2008
 * Time: 10:51:11
 */
public class HashSetTrieNode<V> extends AbstractTrieNode<V, Set<V>> {

    private Set<V> emptyCollection = Collections.emptySet();

    public HashSetTrieNode(boolean isCaseSensitive) {
        super(isCaseSensitive);
    }

    public HashSetTrieNode(boolean isCaseSensitive, int depth, char key) {
        super(isCaseSensitive, depth, key);
    }

    protected Set<V> createValuesCollection() {
        return new HashSet<V>();
    }

    protected Set<V> getEmptyCollection() {
        return emptyCollection;
    }

    protected AbstractTrieNode<V, Set<V>> createChildNode(boolean caseSensitive, int depth, char c) {
        return new HashSetTrieNode<V>(caseSensitive, depth, c);
    }
}
