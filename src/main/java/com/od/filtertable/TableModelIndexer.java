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

import javax.swing.table.TableModel;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Sep-2008
 * Time: 16:16:25
 *
 * This class performs the indexing which allows us to search for string values within
 * the RowFilteringTableModel's source model.
 */
public class TableModelIndexer {

    private TableModel tableModel;
    private FilterColumnConfig filterColumnConfig;
    private int initialIndexDepth;
    private boolean caseSensitive;
    private CharTrie<TableCell, TableCellSet> index = new TableCellSetTrieNode(false);
    private MutableCharSequence mutableCharRange = new MutableCharSequence();
    protected TableCell[][] tableCells;
    protected int size;

    public TableModelIndexer(TableModel tableModel, int initialIndexDepth) {
        this(tableModel, false, initialIndexDepth);
    }

    public TableModelIndexer(TableModel tableModel, boolean caseSensitive, int initialIndexDepth) {
        this.tableModel = tableModel;
        this.filterColumnConfig = new FilterColumnConfig(tableModel);
        this.caseSensitive = caseSensitive;
        buildIndex(initialIndexDepth);
    }

    public void setTableModel(TableModel t) {
        this.tableModel = t;
        tableStructureChanged();
    }

    public void tableStructureChanged() {
        filterColumnConfig.tableChanged(tableModel);
        buildIndex();
    }

    //re-build the index to the specified initialDepth
    public void buildIndex(int initialDepth) {
        this.initialIndexDepth = initialDepth;
        buildIndex();
    }

    //re-build the index to the current initial depth
    public void buildIndex() {
        index = new TableCellSetTrieNode(caseSensitive);
        tableCells = new TableCell[((tableModel.getRowCount() * 3) / 2) + 1][tableModel.getColumnCount()];
        size = tableModel.getRowCount();
        populateWithTableCellsAndAddToIndex(0, size - 1);
    }

    public Collection<TableCell> getCellsContaining(CharSequence s) {
        updateIndexForSequence(s);
        return index.getValues(s);
    }

    public Collection<MutableRowIndex> getRowsContaining(CharSequence s) {
        updateIndexForSequence(s);
        return index.getValues(s).getRowsInSet();
    }

    //rows have been inserted to the source model
    public void insertRows(int startIndex, int endIndex) {
        int insertedRowCount = (endIndex - startIndex) + 1;
        ensureCapacity(size + insertedRowCount);
        increaseMutableRowIndexes(startIndex, insertedRowCount);
        insertCellsAndAddToIndex(startIndex, endIndex, insertedRowCount);
        size+=insertedRowCount;
    }

    //rows have been removed from the source model
    public void removeRows(int startIndex, int endIndex) {
        int removedRowCount = (endIndex - startIndex) + 1;
        decreaseMutableRowIndexes(endIndex + 1, removedRowCount);
        removeCellsAndRemoveFromIndex(startIndex, endIndex);
        size-=removedRowCount;
    }

    //the value in a cell has been updated, we need to re-index it
    public void reIndexCell(int row, int col) {
        removeFromIndex(tableCells[row][col]);
        refreshTableCellValue(row, col);
        addToIndex(tableCells[row][col], initialIndexDepth);
    }

    public void setFormatter(FilterFormatter filterFormat, Integer... columnIndexes) {
        filterColumnConfig.setFormatter(filterFormat, columnIndexes);
        buildIndex();
    }

    public void setFormatter(FilterFormatter filterFormat, Class... columnClasses) {
        filterColumnConfig.setFormatter(filterFormat, columnClasses);
        buildIndex();
    }

    public void setFormatter(FilterFormatter filterFormat, String... columnNames) {
        filterColumnConfig.setFormatter(filterFormat, columnNames);
        buildIndex();
    }

    public void clearFormatters() {
        filterColumnConfig.clearFormatters();
        buildIndex();
    }

    public void trimIndexToInitialDepth() {
        index.trimToDepth(initialIndexDepth);
        setCellIndexDepth(initialIndexDepth);
    }

    private void setCellIndexDepth(int indexDepth) {
        for ( int row=0; row < size; row++) {
            for ( int col=0; col < tableModel.getColumnCount(); col++ ) {
                tableCells[row][col].setIndexedDepth(indexDepth);
            }
        }
    }

    private void insertCellsAndAddToIndex(int startIndex, int endIndex, int insertedRowCount) {
        System.arraycopy(tableCells, startIndex, tableCells, startIndex + insertedRowCount, size - startIndex);
        createNewTableCellRows(startIndex, endIndex);
        populateWithTableCellsAndAddToIndex(startIndex, endIndex);
    }

    private void removeCellsAndRemoveFromIndex(int startIndex, int endIndex) {
        removeRowsFromIndex(startIndex, endIndex);
        System.arraycopy(tableCells, endIndex + 1, tableCells, startIndex, size - (endIndex + 1) );
    }

    //start and end row are inclusive
    private void populateWithTableCellsAndAddToIndex(int startRow, int endRow) {
        for ( int row = startRow; row <= endRow; row ++) {
            MutableRowIndex rowIndex = new MutableRowIndex(row);
            for ( int col = 0; col < tableModel.getColumnCount(); col ++) {
                tableCells[row][col] = new TableCell(rowIndex, col);
                refreshTableCellValue(row, col);
                addToIndex(tableCells[row][col], initialIndexDepth);
            }
        }
    }

    private void removeRowsFromIndex(int startIndex, int endIndex) {
        for ( int row = startIndex;  row <= endIndex; row ++ ) {
            for ( int col=0; col < tableModel.getColumnCount(); col++ ) {
                removeFromIndex(tableCells[row][col]);
            }
        }
    }

    private void createNewTableCellRows(int startIndex, int endIndex) {
        for ( int row=startIndex; row <= endIndex; row ++) {
            tableCells[row] = new TableCell[tableModel.getColumnCount()];
        }
    }

    private void increaseMutableRowIndexes(int startIndex, int insertedRowCount) {
        for (int loop=startIndex; loop < size; loop ++) {
            tableCells[loop][0].adjustRowIndex(insertedRowCount);
        }
    }

    private void decreaseMutableRowIndexes(int startIndex, int removedRowCount) {
        for (int loop=startIndex; loop <  size; loop ++) {
            tableCells[loop][0].adjustRowIndex(-removedRowCount);
        }
    }

    private void addToIndex(TableCell cell, int maxDepth) {
        cell.setIndexedDepth(maxDepth);
        if ( ! cell.isNullValue()) {
            readFormattedCellValueIntoCharRange(cell);
            for ( int start = 0; start < mutableCharRange.totalSequenceLength(); start ++ ) {
                mutableCharRange.setStart(start);
                mutableCharRange.setEnd(Math.min(start + maxDepth, mutableCharRange.totalSequenceLength()));
                index.addValueForAllPrefixes(mutableCharRange, cell);
            }
        }
    }

    private void removeFromIndex(TableCell cell) {
        int depthToRemove = cell.getIndexedDepth();
        if ( ! cell.isNullValue()) {
            readFormattedCellValueIntoCharRange(cell);
            for ( int start = 0; start < mutableCharRange.totalSequenceLength(); start ++ ) {
                mutableCharRange.setStart(start);
                mutableCharRange.setEnd(Math.min(start + depthToRemove, mutableCharRange.totalSequenceLength()));
                index.removeValueForAllPrefixes(mutableCharRange, cell);
            }
        }
    }

    private void readFormattedCellValueIntoCharRange(TableCell cell) {
        FilterFormatter formatter = filterColumnConfig.getFormatter(cell.getCol());
        CharSequence chars = formatter.format(cell.getValue());
        mutableCharRange.setSegment(chars);
    }

    private void refreshTableCellValue(int row, int col) {
        tableCells[row][col].setValue(tableModel.getValueAt(row, col));
    }

    //n.b. see ArrayList.ensureCapacity for the derivation
    private void ensureCapacity(int newSize) {
        if ( newSize > tableCells.length ) {
            int newCapacity = Math.max((tableCells.length * 3)/2 + 1, newSize);
            TableCell[][] newTableCells = new TableCell[newCapacity][tableModel.getRowCount()];
            System.arraycopy(tableCells, 0, newTableCells, 0, size);
            this.tableCells = newTableCells;
        }
    }

    private void updateIndexForSequence(CharSequence s) {
        if ( s.length() > initialIndexDepth) {
            CharSequence subString = s.subSequence(0, s.length() - 1);
            updateIndexForSequence(subString);

            int newIndexDepth = s.length();

            //iterate array since concurrent modification exeception is possible
            Collection<TableCell> cellCollection = index.getValues(subString);
            TableCell[] cells = cellCollection.toArray(new TableCell[cellCollection.size()]);
            for ( TableCell cell : cells ) {
                if ( cell.getIndexedDepth() < newIndexDepth ) {
                    addToIndex(cell, newIndexDepth);
                }
            }
        }
    }

}