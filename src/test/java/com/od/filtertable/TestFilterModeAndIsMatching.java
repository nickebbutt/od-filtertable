package com.od.filtertable;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 28-Aug-2009
 * Time: 17:33:07
 */
public class TestFilterModeAndIsMatching extends AbstractFilteredTableTest {

    public TestFilterModeAndIsMatching() {
        super("/testMatchesSearch1.csv");
    }

    public void doSetUp() {
        filteredModel = new RowFilteringTableModel(testTableModel);
    }

    public void testMatchingIndexesWithFiltering() {
        filteredModel.setSearchTerm("one");
        assertMatchesSearch(new TableCell(0, 1), new TableCell(1,1));

        filteredModel.setSearchTerm("three");
        assertMatchesSearch(new TableCell(0, 2), new TableCell(1, 2));
    }

    public void testMatchingIndexesWithoutFiltering() {
        filteredModel.setFilterRows(false);

        filteredModel.setSearchTerm("one");
        assertMatchesSearch(new TableCell(0, 1), new TableCell(2,1));

        filteredModel.setSearchTerm("three");
        assertMatchesSearch(new TableCell(1, 2), new TableCell(2, 2));
    }

    public void testSearchTermRetainedWhenFilterRowsModeChanged() {
        filteredModel.setFilterRows(false);
        filteredModel.setSearchTerm("three");
        assertMatchesSearch(new TableCell(1, 2), new TableCell(2, 2));
        filteredModel.setFilterRows(true);
        assertMatchesSearch(new TableCell(0, 2), new TableCell(1, 2));
    }


}
