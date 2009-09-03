package com.od.filtertable;

import java.util.Set;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Sep-2009
 * Time: 16:01:19
 */
public class TestFindNextMatchingCell extends AbstractFilteredTableTest {

    public TestFindNextMatchingCell() {
        super("/testMatchesSearch1.csv");
    }

    public void doSetUp() {
        filteredModel = new RowFilteringTableModel(testTableModel);
    }

    public void testFindNextCellWithWrapAround() {
        filteredModel.setFilterRows(false);
        filteredModel.setSearchTerm("three");
        assertMatchesSearch(new TableCell(1, 2), new TableCell(2, 2));

        TableCell matchingCell = filteredModel.findFirstMatchingCell();
        assertEquals(matchingCell, new TableCell(1, 2));

        matchingCell = filteredModel.findNextMatchingCell(matchingCell);
        assertEquals(matchingCell, new TableCell(2, 2));

        matchingCell = filteredModel.findNextMatchingCell(matchingCell);
        assertEquals(matchingCell, new TableCell(1, 2));
    }

    public void testFindNextCellWithOnlyOneMatch() {
        filteredModel.setFilterRows(false);
        filteredModel.setSearchTerm("seven");
        assertMatchesSearch(new TableCell(1, 1));

        TableCell matchingCell = filteredModel.findFirstMatchingCell();
        assertEquals(matchingCell, new TableCell(1, 1));

        //find next cell after matchingCell
        matchingCell = filteredModel.findNextMatchingCell(matchingCell);
        assertEquals(matchingCell, new TableCell(1, 1));
    }

    public void testFindNextWithNoMatches() {
        filteredModel.setFilterRows(false);
        filteredModel.setSearchTerm("itsallgonehorriblywrong");
        assertMatchesSearch();

        TableCell matchingCell = filteredModel.findFirstMatchingCell();
        assertEquals(TableCell.NO_MATCH_TABLE_CELL, matchingCell);

        matchingCell = filteredModel.findNextMatchingCell(null);
        assertEquals(TableCell.NO_MATCH_TABLE_CELL, matchingCell);
    }

    public void testFindWithFilterRowsEnabled() {
        filteredModel.setSearchTerm("three");
        assertMatchesSearch(new TableCell(0, 2), new TableCell(1, 2));

        TableCell matchingCell = filteredModel.findFirstMatchingCell();
        assertEquals(matchingCell, new TableCell(0, 2));

        matchingCell = filteredModel.findNextMatchingCell(matchingCell);
        assertEquals(matchingCell, new TableCell(1, 2));

        matchingCell = filteredModel.findNextMatchingCell(matchingCell);
        assertEquals(matchingCell, new TableCell(0, 2));
    }

    public void testFindPerformance() {
        FixtureTableModel fixture = readTableModel("/test1.csv");
        filteredModel = new RowFilteringTableModel(fixture);

        filteredModel.setFilterRows(false);
        filteredModel.setSearchTerm("eu");

        long startTime = System.currentTimeMillis();
        TableCell matchingCell = null;
        Set<TableCell> matchingCells = new HashSet<TableCell>();
        for ( int loop=0; loop < 100000; loop++) {
            matchingCell = filteredModel.findNextMatchingCell(matchingCell);
            matchingCells.add(matchingCell);
        }

        System.out.println("100000 find next cell: " + (System.currentTimeMillis() - startTime) + " millis");
        assertEquals(1592, matchingCells.size());
        assertTrue(System.currentTimeMillis() - startTime < 200);
    }

}
