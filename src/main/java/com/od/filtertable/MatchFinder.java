package com.od.filtertable;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Sep-2009
 * Time: 11:46:56
 */
public class MatchFinder {

    private TableCell lastFindResult = TableCell.NO_MATCH_TABLE_CELL;
    private IndexedTableModel indexedTableModel;
    private ColumnSource columnSource;

    public MatchFinder(ColumnSource columnSource, IndexedTableModel indexedTableModel) {
        this.columnSource = columnSource;
        this.indexedTableModel = indexedTableModel;
    }

    public boolean isLastFindResult(int row, int col) {
        return lastFindResult.isCellAt(row, col);
    }

    public TableCell getLastFindResult() {
        return lastFindResult;
    }

    public void clearLastFind() {
        this.lastFindResult = TableCell.NO_MATCH_TABLE_CELL;
    }

    /**
     * @return the next matching TableCell instance starting at the cell provided, or TableCell.NO_MATCH_TABLE_CELL if no cells
     * match the current search. The returned cell may eqaul the lastMatch if there is only one matching cell
     */
    public TableCell findNextMatchingCell(TableCell cell) {
        cell = cell != null && cell != TableCell.NO_MATCH_TABLE_CELL ? cell : new TableCell(0,0);
        return getNextMatchingCell(0, cell.getRow(), cell.getCol() + 1, true);
    }

    public TableCell findPreviousMatchingCell(TableCell cell) {
        cell = cell != null && cell != TableCell.NO_MATCH_TABLE_CELL ? cell : new TableCell(0,0);
        return getNextMatchingCell(0, cell.getRow(), cell.getCol() - 1, false);
    }

    /**
     * @return the first matching TableCell instance starting from cell 0,0 - or TableCell.NO_MATCH_TABLE_CELL if no cells match the current search
     */
    public TableCell findFirstMatchingCell() {
        return getNextMatchingCell(0, 0, 0, true);
    }

    private TableCell getNextMatchingCell(int rowsSearched, int currentRow, int currentCol, boolean isForwards) {
        //in case we are finding the next cell from a cell location which is no longer valid in the table
        //currentRow may be >= rowCount, but we just carry on the find from the nearest valid row
        currentRow = Math.min(indexedTableModel.getRowCount() - 1, currentRow);

        TableCell result = TableCell.NO_MATCH_TABLE_CELL;
        if (currentRow >= 0 && rowsSearched <= indexedTableModel.getRowCount()) {
            result = getNextMatchInRow(currentRow, currentCol, isForwards);
            if (result == TableCell.NO_MATCH_TABLE_CELL) {
                int nextRow = isForwards ?
                        (currentRow + 1) % indexedTableModel.getRowCount() :
                        currentRow == 0 ? indexedTableModel.getRowCount() - 1 : currentRow - 1;
                int nextCol = isForwards ? 0 : indexedTableModel.getColumnCount() - 1;
                result = getNextMatchingCell(rowsSearched + 1, nextRow, nextCol, isForwards);
            }
        }
        lastFindResult = result;
        return result;
    }

    //returns the cell at currentRow / currentCol if it matches, or the next matching cell in the row
    //either forwards or backwards, or NO_MATCH_TABLE_CELL if no subsequent match can be found
    private TableCell getNextMatchInRow(final int currentRow, int currentCol, boolean isForwards) {
        TableCell result = TableCell.NO_MATCH_TABLE_CELL;
        while ( currentCol >= 0 && currentCol < columnSource.getColumnCount() ) {
            boolean matchesSearch = indexedTableModel.isCellMatchingSearch(
                currentRow, columnSource.getTableModelColumnIndex(currentCol)
            );

            if (matchesSearch){
                result = new TableCell(currentRow, currentCol);
                break;
            }
            currentCol = isForwards ? currentCol + 1 : currentCol - 1;
        }
        return result;
    }

    public static interface ColumnSource {

        public int getTableModelColumnIndex(int columnModelIndex);

        public int getColumnCount();
    }
}
