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
 * Date: 10-Sep-2008
 * Time: 11:10:35
 *
 * Each trie node is associated with a set of TableCell, with an instance in the set for each cell
 * which contains the char sequence the trie node represents.
 *
 * Following each update to the source table model, the TableCellSet for the trie nodes are updated
 * according to the old/new contents of the cells being updated. The filtered table model then has
 * to interrogate the trie to find the set of rows which match the current filter - the rowsInSet.
 * Rather than calculating the set every time, this class lazily creates the required set of rows on
 * demand and caches it, until an update to the source table actually invalidates the contents.
 *
 * One additional point is that the actual row indexes (mutableRowIndex values) may change if there are
 * inserts/delete to rows in the underlying table, although the cell instances in the set remain unchanged
 */
class TableCellSet extends HashSet<TableCell> {

    private HashMap<MutableRowIndex, Set<Integer>> mutableRowsToCols;

    @Override
    public boolean add(TableCell o) {
        mutableRowsToCols = null;               
        return super.add(o);
    }

    @Override
    public boolean remove(Object o) {
        mutableRowsToCols = null;
        return super.remove(o);
    }

    @Override
    public boolean removeAll(Collection c) {
        mutableRowsToCols = null;
        return super.removeAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends TableCell> c) {
        mutableRowsToCols = null;
        return super.addAll(c);
    }

    @Override
    public boolean retainAll(Collection c) {
        mutableRowsToCols = null;
        return super.retainAll(c);
    }

    @Override
    public void clear() {
        mutableRowsToCols = null;
        super.clear();
    }

    public boolean containsCell(int rowIndex, int colIndex) {
        boolean result = false;
        for ( TableCell c : this) {
            if ( c.getCol() == colIndex && c.getRow() == rowIndex) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * @return the Map of MutableRowIndex to the set of cols which pass the filters for that row
     *
     * If the TableCellSet contents has not changed since last time this method was called (the same cells are matching),
     * the same map instance will be returned. However the actual values of the mutableRowIndex keys may have been adjusted due
     * to row inserts/deletes in the source model
     */
    public HashMap<MutableRowIndex, Set<Integer>> getRowColumnMap() {
        if ( mutableRowsToCols == null ) {
            mutableRowsToCols = findRowsInSet();
        }
        return mutableRowsToCols;
    }

    //remember, there is only one instance of MutableRowIndex per row which is shared by the cells in that row
    private HashMap<MutableRowIndex, Set<Integer>> findRowsInSet() {
        HashMap<MutableRowIndex, Set<Integer>> rows = new HashMap<MutableRowIndex, Set<Integer>>();
        for (TableCell c : this) {
            Set<Integer> colSet = rows.get(c.getMutableRowIndex());
            if ( colSet == null) {
                colSet = new HashSet<Integer>();
                rows.put(c.getMutableRowIndex(), colSet);
            }
            colSet.add(c.getCol());
        }
        return rows;
    }
}
