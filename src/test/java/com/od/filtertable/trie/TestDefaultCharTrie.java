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

import com.od.filtertable.trie.HashSetTrieNode;
import com.od.filtertable.trie.AbstractTrieNode;
import junit.framework.TestCase;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Sep-2008
 * Time: 15:45:31
 */
public class TestDefaultCharTrie extends TestCase {

    private AbstractTrieNode<String, Set<String>> defaultTrie;
    private String value1 = "value1";
    private String value2 = "value2";
    private String key1 = "key1";
    private String key2 = "key2";

    public void setUp() {
        defaultTrie = new HashSetTrieNode<String>(true);
    }

    public void testAddValue() {
        defaultTrie.addValue(key1, value1);
        defaultTrie.addValue(key1, value2);

        assertEquals(2, defaultTrie.getValues(key1).size());
        assertEquals(0, defaultTrie.getValues(key2).size());
    }

    public void testAddValueForAllPrefixes() {
        defaultTrie.addValueForAllPrefixes(key1, value1);
        defaultTrie.addValueForAllPrefixes(key1, value2);

        assertEquals(0, defaultTrie.getValues("key11").size());
        assertEquals(2, defaultTrie.getValues(key1).size());
        assertEquals(2, defaultTrie.getValues("key").size());
        assertEquals(2, defaultTrie.getValues("ke").size());
        assertEquals(2, defaultTrie.getValues("k").size());
    }

    public void testGetValuesWithPrefixes() {
        //here we don't add with prefixes, but can get the equivalent results by calling getValuesWithPrefixes
        //although in this case the results computation is lazy although memory is reduced
        defaultTrie.addValue(key1, value1);
        defaultTrie.addValue(key1, value2);
        assertEquals(0, defaultTrie.getValues("key11").size());
        assertEquals(0, defaultTrie.getValuesWithPrefixes("key11").size());
        assertEquals(2, defaultTrie.getValues(key1).size());
        assertEquals(2, defaultTrie.getValuesWithPrefixes(key1).size());
        assertEquals(0, defaultTrie.getValues("key").size());
        assertEquals(2, defaultTrie.getValuesWithPrefixes("key").size());
        assertEquals(0, defaultTrie.getValues("ke").size());
        assertEquals(2, defaultTrie.getValuesWithPrefixes("ke").size());
        assertEquals(0, defaultTrie.getValues("k").size());
        assertEquals(2, defaultTrie.getValuesWithPrefixes("k").size());
    }

    public void testAddWithMinDepth() {
        //here we add with prefixes, but not to trie nodes with <= 3 or 2 depth 
        //equvi to prefixes of length 3 or 2
        //can be used to save work where we have already added such nodes to the index and are dynamically adding at a deeper level
        //or to avoid storing large collections for short prefixes, making it mandatory to search using at least 3+ characters, for example
        defaultTrie.addValueForPrefixesFromDepth(key1, value1, 3);
        defaultTrie.addValueForPrefixesFromDepth(key1, value2, 2);
        assertEquals(2, defaultTrie.getValues(key1).size());
        assertEquals(2, defaultTrie.getValues("key").size());
        assertEquals(1, defaultTrie.getValues("ke").size());
        assertEquals(0, defaultTrie.getValues("k").size());
    }

    public void testRemoveWithMinDepth() {
        //here we add with prefixes, but not for trie nodes with 3 depth or greater
        //could be used to selectively reduce the indexing depth against which a value is stored (probably not a common requirement)
        defaultTrie.addValueForPrefixesFromDepth(key1, value1, 2);
        defaultTrie.removeValueForPrefixesFromDepth(key1, value1, 3);
        assertEquals(0, defaultTrie.getValues("key").size());
        assertEquals(1, defaultTrie.getValues("ke").size());
    }


    public void testRemoveValue() {
        testAddValue();
        defaultTrie.removeValue(key1, value1);
        assertEquals(1, defaultTrie.getValues(key1).size());
        assertEquals(value2, defaultTrie.getValues(key1).toArray()[0]);

        defaultTrie.removeValue(key1, "wibble");
        assertEquals(1, defaultTrie.getValues(key1).size());

        defaultTrie.removeValue(key1, value2);
        assertEquals(0, defaultTrie.getValues(key1).size());
    }

    public void testRemoveValueForAllPrefixes() {
        testAddValueForAllPrefixes();
        defaultTrie.removeValueForAllPrefixes(key1, value1);
        assertEquals(1, defaultTrie.getValues(key1).size());
        assertEquals(1, defaultTrie.getValues("key").size());
        assertEquals(1, defaultTrie.getValues("ke").size());
        assertEquals(1, defaultTrie.getValues("k").size());

        defaultTrie.removeValueForAllPrefixes("key", value2);
        assertEquals(1, defaultTrie.getValues(key1).size());
        assertEquals(0, defaultTrie.getValues("key").size());
        assertEquals(0, defaultTrie.getValues("ke").size());
        assertEquals(0, defaultTrie.getValues("k").size());
    }

    public void testCaseInsensitiveAdd() {
        defaultTrie.addValueForAllPrefixes(key1, value1);

        assertEquals(1, defaultTrie.getValues(key1).size());
        assertEquals(0, defaultTrie.getValues("KEY1").size());
        assertEquals(1, defaultTrie.getValues("key").size());
        assertEquals(0, defaultTrie.getValues("KEY").size());

        defaultTrie = new HashSetTrieNode<String>(false);
        defaultTrie.addValueForAllPrefixes(key1, value1);
        assertEquals(1, defaultTrie.getValues(key1).size());
        assertEquals(1, defaultTrie.getValues("KEY1").size());
        assertEquals(1, defaultTrie.getValues("key").size());
        assertEquals(1, defaultTrie.getValues("KEY").size());
    }

    public void testCaseInsensitiveRemove() {
        defaultTrie = new HashSetTrieNode<String>(false);
        defaultTrie.addValueForAllPrefixes(key1, value1);
        assertEquals(1, defaultTrie.getValues(key1).size());
        assertEquals(1, defaultTrie.getValues("key").size());

        defaultTrie.removeValueForAllPrefixes("KEY1", value1);
        assertEquals(0, defaultTrie.getValues(key1).size());
        assertEquals(0, defaultTrie.getValues("key").size());
    }
    
    public void tearDown() {
        defaultTrie = null;
    }


}
