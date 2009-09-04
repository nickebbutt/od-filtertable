package com.od.filtertable;

import javax.swing.table.DefaultTableModel;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Sep-2009
 * Time: 16:01:19
 */
public class TestFindNextMatchingCell extends AbstractFilteredTableTest {

    private MatchFinder matchFinder;

    public TestFindNextMatchingCell() {
        super("/testMatchesSearch1.csv");
    }

    public void doSetUp() {
        filteredModel = new RowFilteringTableModel(testTableModel);
        matchFinder = new MatchFinder(new DirectColumnSource(filteredModel), filteredModel);
    }

    public void testFindNextCellWithWrapAround() {
        filteredModel.setFilterRows(false);
        filteredModel.setSearchTerm("three");
        assertMatchesSearch(new TableCell(1, 2), new TableCell(2, 2));

        TableCell matchingCell = matchFinder.findFirstMatchingCell();
        assertEquals(matchingCell, new TableCell(1, 2));

        matchingCell = matchFinder.findNextMatchingCell(matchingCell);
        assertEquals(matchingCell, new TableCell(2, 2));

        matchingCell = matchFinder.findNextMatchingCell(matchingCell);
        assertEquals(matchingCell, new TableCell(1, 2));
    }

    public void testFindPreviousCellWithWrapAround() {
        filteredModel.setFilterRows(false);
        filteredModel.setSearchTerm("three");
        assertMatchesSearch(new TableCell(1, 2), new TableCell(2, 2));

        TableCell matchingCell = matchFinder.findPreviousMatchingCell(TableCell.NO_MATCH_TABLE_CELL);
        assertEquals(matchingCell, new TableCell(2, 2));

        matchingCell = matchFinder.findNextMatchingCell(matchingCell);
        assertEquals(matchingCell, new TableCell(1, 2));

        matchingCell = matchFinder.findPreviousMatchingCell(matchingCell);
        assertEquals(matchingCell, new TableCell(2, 2));
    }

    public void testFindNextAndPreviousCellWithOnlyOneMatch() {
        filteredModel.setFilterRows(false);
        filteredModel.setSearchTerm("seven");
        assertMatchesSearch(new TableCell(1, 1));

        TableCell matchingCell = matchFinder.findFirstMatchingCell();
        assertEquals(matchingCell, new TableCell(1, 1));

        //find next cell after matchingCell
        matchingCell = matchFinder.findNextMatchingCell(matchingCell);
        assertEquals(matchingCell, new TableCell(1, 1));

        //find previous cell after matchingCell
        matchingCell = matchFinder.findPreviousMatchingCell(matchingCell);
        assertEquals(matchingCell, new TableCell(1, 1));
    }

    public void testFindNextAndPreviousWithNoMatches() {
        filteredModel.setFilterRows(false);
        filteredModel.setSearchTerm("itsallgonehorriblywrong");
        assertMatchesSearch();

        TableCell matchingCell = matchFinder.findFirstMatchingCell();
        assertEquals(TableCell.NO_MATCH_TABLE_CELL, matchingCell);

        matchingCell = matchFinder.findNextMatchingCell(null);
        assertEquals(TableCell.NO_MATCH_TABLE_CELL, matchingCell);

        matchingCell = matchFinder.findPreviousMatchingCell(matchingCell);
        assertEquals(TableCell.NO_MATCH_TABLE_CELL, matchingCell);
    }

    public void testFindWithFilterRowsEnabled() {
        filteredModel.setSearchTerm("three");
        assertMatchesSearch(new TableCell(0, 2), new TableCell(1, 2));

        TableCell matchingCell = matchFinder.findFirstMatchingCell();
        assertEquals(matchingCell, new TableCell(0, 2));

        matchingCell = matchFinder.findNextMatchingCell(matchingCell);
        assertEquals(matchingCell, new TableCell(1, 2));

        matchingCell = matchFinder.findNextMatchingCell(matchingCell);
        assertEquals(matchingCell, new TableCell(0, 2));

        matchingCell = matchFinder.findPreviousMatchingCell(matchingCell);
        assertEquals(matchingCell, new TableCell(1, 2));

        matchingCell = matchFinder.findPreviousMatchingCell(matchingCell);
        assertEquals(matchingCell, new TableCell(0, 2));
    }

    public void testGetLastFindResult() {
        filteredModel.setSearchTerm("three");
        assertMatchesSearch(new TableCell(0, 2), new TableCell(1, 2));

        TableCell matchingCell = matchFinder.findFirstMatchingCell();
        assertEquals(matchingCell, new TableCell(0, 2));

        matchingCell = matchFinder.findNextMatchingCell(matchFinder.getLastFindResult());
        assertEquals(matchingCell, new TableCell(1, 2));

        matchingCell = matchFinder.findNextMatchingCell(matchFinder.getLastFindResult());
        assertEquals(matchingCell, new TableCell(0, 2));

        matchingCell = matchFinder.findPreviousMatchingCell(matchFinder.getLastFindResult());
        assertEquals(matchingCell, new TableCell(1, 2));

        matchingCell = matchFinder.findPreviousMatchingCell(matchFinder.getLastFindResult());
        assertEquals(matchingCell, new TableCell(0, 2));
    }

    //our memory for the last find result is retained despite changes in the underlying table data
    //it may refer to a cell which no longer exists in the table.
    //this should not matter since we can still get the next or previous cell using the find
    //functions by passing in the last find result, even if the last find result cell is no
    //longer valid in the table.
    //The reason for this is that from the users perspective it will be annoying if the find location
    //jumps every time a table event affects the search results - so far as possible we want to continue
    //the find from the last location
    public void testGetLastFindResultIsNotClearedOnModelChangesWhichAffectTheSearchResults() {
        filteredModel.setSearchTerm("three");
        filteredModel.setFilterRows(false);
        assertMatchesSearch(new TableCell(1, 2), new TableCell(2, 2));
        TableCell matchingCell = matchFinder.findFirstMatchingCell();
        assertEquals(matchingCell, matchFinder.getLastFindResult());

        testTableModel.fireTableDataChanged();
        assertEquals(matchingCell, matchFinder.getLastFindResult());

        testTableModel.fireTableStructureChanged();
        assertEquals(matchingCell, matchFinder.getLastFindResult());

        testTableModel.setValueAt("wibble", 1, 2);
        testTableModel.fireTableCellUpdated(1, 2);
        assertEquals(matchingCell, matchFinder.getLastFindResult());
    }

    public void testFindPerformance() {
        FixtureTableModel fixture = readTableModel("/test1.csv");
        filteredModel = new RowFilteringTableModel(fixture);

        filteredModel.setFilterRows(false);
        filteredModel.setSearchTerm("eu");

        MatchFinder matchFinder = new MatchFinder(new DirectColumnSource(filteredModel), filteredModel);

        long startTime = System.currentTimeMillis();
        TableCell matchingCell = null;
        Set<TableCell> matchingCells = new HashSet<TableCell>();
        for ( int loop=0; loop < 100000; loop++) {
            matchingCell = matchFinder.findNextMatchingCell(matchingCell);
            matchingCells.add(matchingCell);
        }

        System.out.println("100000 find next cell: " + (System.currentTimeMillis() - startTime) + " millis");
        assertEquals(1592, matchingCells.size());
        assertTrue(System.currentTimeMillis() - startTime < 200);
    }

    public void testEmptyTable() {
        DefaultTableModel d = new DefaultTableModel(new Object[] { "col1", "col2"}, 0);
        RowFilteringTableModel f = new RowFilteringTableModel(d);
        f.setSearchTerm("test");
        MatchFinder matchFinder = new MatchFinder(new DirectColumnSource(f), f);
        TableCell lastCell = matchFinder.findFirstMatchingCell();
        assertEquals(TableCell.NO_MATCH_TABLE_CELL, lastCell);

        lastCell = matchFinder.findPreviousMatchingCell(lastCell);
        assertEquals(TableCell.NO_MATCH_TABLE_CELL, lastCell);
    }

    public void testModulus() {
        System.out.println(-12 % 10);
    }

    private class DirectColumnSource implements MatchFinder.ColumnSource {

        private RowFilteringTableModel filteredModel;

        public DirectColumnSource(RowFilteringTableModel filteredModel) {
            this.filteredModel = filteredModel;
        }

        public int getTableModelColumnIndex(int columnModelIndex) {
            return columnModelIndex;
        }

        public int getColumnCount() {
            return filteredModel.getColumnCount();
        }
    }
}
