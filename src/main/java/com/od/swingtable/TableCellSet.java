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

import java.util.HashSet;
import java.util.Collection;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 10-Sep-2008
 * Time: 11:10:35
 *
 * Each trie node is associated with a set of TableCell, with an instance in the set for each cell which contains the
 * char sequence the trie node represents. Frequently there are updates to the table model/trie index, but these may
 * not affect the trie node which matches the current filter value
 *
 * Nevertheless, following each update, the filtered table model has to interrogate the trie to find the set of rows
 * which match the current filter. Rather than calulating the set every time, this class lazily creates the required
 * set of rows on demand and caches it.
 */
class TableCellSet extends HashSet<TableCell> {

    Set<MutableRowIndex> rowsInSet;

    @Override
    public boolean add(TableCell o) {
        rowsInSet = null;               
        return super.add(o);
    }

    @Override
    public boolean remove(Object o) {
        rowsInSet = null;
        return super.remove(o);
    }

    @Override
    public boolean removeAll(Collection c) {
        rowsInSet = null;
        return super.removeAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends TableCell> c) {
        rowsInSet = null;
        return super.addAll(c);
    }

    @Override
    public boolean retainAll(Collection c) {
        rowsInSet = null;
        return super.retainAll(c);
    }

    @Override
    public void clear() {
        rowsInSet = null;
        super.clear();
    }

    public Set<MutableRowIndex> getRowsInSet() {
        if ( rowsInSet == null ) {
            rowsInSet = findRowsInSet();
        }
        return rowsInSet;
    }

    private Set<MutableRowIndex> findRowsInSet() {
        Set<MutableRowIndex> rows = new HashSet<MutableRowIndex>();
        for (TableCell c : this) {
            rows.add(c.getMutableRowIndex());
        }
        return rows;
    }
}
