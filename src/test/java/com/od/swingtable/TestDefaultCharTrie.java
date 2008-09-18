/**
 *  Copyright (C) Nick Ebbutt September 2009
 *
 *  This file is part of ObjectDefinitions Ltd. FilterTable.
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

package com.od.swingtable;

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
