package com.od.filtertable;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Sep-2009
 * Time: 14:05:49
 */
public class TableCell {

    public static final TableCell NO_MATCH_TABLE_CELL = new TableCell(-1, -1);

    private final int row;
    private final int col;

    public TableCell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /*
     * @return the table row index
     */
    public int getRow() {
        return row;
    }

    /**
     * @return column index in the table column model
     */
    public int getCol() {
        return col;
    }

    public boolean isCellAt(int row, int col) {
        return this.row == row && this.col == col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableCell tableCell = (TableCell) o;

        if (col != tableCell.col) return false;
        if (row != tableCell.row) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + col;
        return result;
    }

    @Override
    public String toString() {
        return "TableCell{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}
