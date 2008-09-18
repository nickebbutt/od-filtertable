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

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 10-Sep-2008
 * Time: 10:51:11
 */
class TableCellSetTrieNode extends AbstractTrieNode<TableCell, TableCellSet> {

    private static final TableCellSet emptyCollection = new TableCellSet();

    public TableCellSetTrieNode(boolean isCaseSensitive) {
        super(isCaseSensitive);
    }

    public TableCellSetTrieNode(boolean isCaseSensitive, int depth, char key) {
        super(isCaseSensitive, depth, key);
    }

    protected TableCellSet createValuesCollection() {
        return new TableCellSet();
    }

    protected TableCellSet getEmptyCollection() {
        return emptyCollection;
    }

    protected AbstractTrieNode<TableCell, TableCellSet> createChildNode(boolean caseSensitive, int depth, char c) {
        return new TableCellSetTrieNode(caseSensitive, depth, c);
    }
}
