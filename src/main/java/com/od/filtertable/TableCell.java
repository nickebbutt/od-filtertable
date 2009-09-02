package com.od.filtertable;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Sep-2009
 * Time: 14:05:49
 */
public class TableCell {

    private final int row;
    private final int col;

    public TableCell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
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
