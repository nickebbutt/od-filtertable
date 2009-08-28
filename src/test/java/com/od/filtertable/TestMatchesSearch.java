package com.od.filtertable;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 28-Aug-2009
 * Time: 17:33:07
 */
public class TestMatchesSearch extends TestCase {

    private RowFilteringTableModel filteredModel;

    public void testMatchingIndexes() {
        FixtureTableModel f = new TableParser().readBoard("/testMatchesSearch1.csv");
        filteredModel = new RowFilteringTableModel(f);

        filteredModel.setSearchTerm("one");
        assertMatches(new Cell(0, 1), new Cell(1,1));
    }

    private void assertMatches(Cell... cells) {
        Set<Cell> expectedCells = new HashSet<Cell>(Arrays.asList(cells));

        for ( int row = 0; row < filteredModel.getRowCount(); row ++) {
            for ( int col = 0 ; col < filteredModel.getColumnCount(); col++) {
                if ( filteredModel.isCellMatchingSearch(row, col)) {
                    if ( ! expectedCells.remove(new Cell(row, col)) ) {
                        fail("According to model cell at row " + row + " col " + col + " contains search term, which we are not expecting");
                    }
                }
            }
        }

        if (expectedCells.size() > 0 ) {
            fail("According to model cells " + expectedCells + " do not contain search term, and we are expecting them to match");
        }
    }

    private class Cell {
        int row;
        int col;

        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }



        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Cell cell = (Cell) o;

            if (col != cell.col) return false;
            if (row != cell.row) return false;

            return true;
        }

        @Override
        public String toString() {
            return "Cell{" +
                    "row=" + row +
                    ", col=" + col +
                    '}';
        }

        @Override
        public int hashCode() {
            int result = row;
            result = 31 * result + col;
            return result;
        }
    }
}
