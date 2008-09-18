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

import javax.swing.table.TableModel;
import java.util.Collection;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 03-Sep-2008
 * Time: 17:42:55
 */
public abstract class AbstractFilteredTableTest extends TestCase {

    protected FixtureTableModel testTableModel;
    private String tableModelPath;

    public AbstractFilteredTableTest(String tableModelPath) {
        this.tableModelPath = tableModelPath;
    }

    public final void setUp() {
        testTableModel = readTableModel(tableModelPath);
        doSetUp();
    }

    protected FixtureTableModel readTableModel(String path) {
        return new TableParser().readBoard(path);
    }

    protected void doSetUp() {
    }

    public final void tearDown() {
        testTableModel = null;
        doTearDown();
    }

    private void doTearDown() {
    }

    protected int getFirstRow(Collection<TableCell> cells) {
        TableCell[] sortedCells = getRowSortedTableCells(cells);
        return sortedCells[0].getRow();
    }

    protected int getLastRow(Collection<TableCell> cells) {
        TableCell[] sortedCells = getRowSortedTableCells(cells);
        return sortedCells[sortedCells.length-1].getRow();
    }

    private TableCell[] getRowSortedTableCells(Collection<TableCell> cells) {
        TableCell[] sortedCells = cells.toArray(new TableCell[cells.size()]);
        Arrays.sort(sortedCells, new Comparator<TableCell>() {
            public int compare(TableCell o1, TableCell o2) {
                return ((Integer)o1.getRow()).compareTo(o2.getRow());
            }
        });
        return sortedCells;
    }

    protected boolean tableModelsAreEqual(TableModel t1, TableModel t2) {
        boolean result = false;
        if ( t1.getColumnCount() == t2.getColumnCount() && t1.getRowCount() == t2.getRowCount()) {
            result = true;
            for ( int row = 0; row < t1.getRowCount(); row++ ) {
                for ( int col = 0; col < t1.getColumnCount(); col++) {
                    if (! t1.getValueAt(row, col).equals(t2.getValueAt(row, col))) {
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }
}
