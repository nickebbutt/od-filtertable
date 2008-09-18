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

import org.jmock.Expectations;
import org.jmock.Mockery;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 03-Sep-2008
 * Time: 17:42:11
 */
public class TestRowFilteringTableModel extends AbstractFilteredTableTest {

    private static final boolean LOG_EVENTS = false;
    private RowFilteringTableModel filteredModel;
    private Mockery mockery = new Mockery();
    private TableModelListener mockListener;

    private static final String STRING1 = "string1";
    private static final String STRING2 = "string2";
    private static final String STRING3 = "string3";
    private static final int STRING1_MATCH_COUNT = 4;

    public TestRowFilteringTableModel() {
        super("/filteredTableTestData.csv");
    }

    public void doSetUp() {
        filteredModel = new RowFilteringTableModel(testTableModel);
        mockListener = mockery.mock(TableModelListener.class);
    }

    public void testInitialModel() {
        assertEquals(testTableModel.getRowCount(), filteredModel.getRowCount());
    }

    public void testCaseInsensitiveFiltering() {
        filteredModel.setFilterValue(STRING1);
        assertEquals(STRING1_MATCH_COUNT, filteredModel.getRowCount());

        filteredModel.setFilterValue("StRiNg1");
        assertEquals(STRING1_MATCH_COUNT, filteredModel.getRowCount());
    }

    public void testCaseSensitiveFiltering() {
        filteredModel = new RowFilteringTableModel(testTableModel, true, 1);
        filteredModel.setFilterValue(STRING1);
        assertEquals(STRING1_MATCH_COUNT, filteredModel.getRowCount());

        filteredModel.setFilterValue("StRiNg1");
        assertEquals(0, filteredModel.getRowCount());
    }

    public void testRemoveRangeWhereAllRowsWereIncluded() {
        filteredModel.setFilterValue(STRING2);

        filteredModel.addTableModelListener(mockListener);
        mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 1, 3, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE))));
        }});

        //All three of the rows in this range contain values which pass the filters, so removing the range
        //should affect three row in the filtered view
        testTableModel.removeRows(5, 7);

        assertTrue(tableModelsAreEqual(readTableModel("/testRemoveRangeWhereAllRowsWereIncluded.csv"), filteredModel));
        mockery.assertIsSatisfied();
    }

    public void testRemoveRangeWhereNoRowsWereIncluded() {
        filteredModel.setFilterValue(STRING2);

        filteredModel.addTableModelListener(mockListener);
        //no expectations-should not be called

        //None of the rows in this range contain values which pass the filters, so removing the range
        //should affect nothing
        testTableModel.removeRows(8, 11);
        assertTrue(tableModelsAreEqual(readTableModel("/testRemoveRangeWhereNoRowsWereIncluded.csv"), filteredModel));
        mockery.assertIsSatisfied();
    }

    public void testRemoveMixedRange() {
        filteredModel.setFilterValue(STRING3);

        filteredModel.addTableModelListener(mockListener);
        mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 1, 2, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE))));
        }});

        //The second and fourth rows of this range contain values which pass the filters, so removing the range
        //should affect two row in the filtered view
        testTableModel.removeRows(9, 12);
        assertTrue(tableModelsAreEqual(readTableModel("/testRemoveMixedRange.csv"), filteredModel));
        mockery.assertIsSatisfied();
    }

    public void testInsertRangeWhereAllRowsPassFilters() {
        filteredModel.setFilterValue(STRING1);

        //All three of the rows in this range contain values which pass the filters, so inserting the range
        //should add three row in the filtered view
        filteredModel.addTableModelListener(mockListener);
        mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 4, 6, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT))));
        }});
        testTableModel.insertRows(7,
            (ArrayList<Object>)testTableModel.getRow(1).clone(),
            (ArrayList<Object>)testTableModel.getRow(2).clone(),
            (ArrayList<Object>)testTableModel.getRow(3).clone()
        );

        assertTrue(tableModelsAreEqual(readTableModel("/testInsertRangeWhereAllRowsPassFilters.csv"), filteredModel));       
        mockery.assertIsSatisfied();
    }

    public void testInsertRangeWhereNoRowsPassFilters() {
        filteredModel.setFilterValue(STRING1);

        //No rows in this range pass the filters, so inserting the range
        //should add no rows in the filtered view
        filteredModel.addTableModelListener(mockListener);
        testTableModel.insertRows(7,
            (ArrayList<Object>)testTableModel.getRow(4).clone(),
            (ArrayList<Object>)testTableModel.getRow(5).clone(),
            (ArrayList<Object>)testTableModel.getRow(6).clone()
        );

        assertTrue(tableModelsAreEqual(readTableModel("/testInsertRangeWhereNoRowsPassFilters.csv"), filteredModel));
        mockery.assertIsSatisfied();
    }

    public void testInsertMixedRange() {
        filteredModel.setFilterValue(STRING3);

        filteredModel.addTableModelListener(mockListener);
         mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 0, 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT))));
        }});
        testTableModel.insertRows(0,
            (ArrayList<Object>)testTableModel.getRow(8).clone(),
            (ArrayList<Object>)testTableModel.getRow(9).clone(),
            (ArrayList<Object>)testTableModel.getRow(10).clone(),
            (ArrayList<Object>)testTableModel.getRow(11).clone()
        );

        assertTrue(tableModelsAreEqual(readTableModel("/testInsertMixedRange.csv"), filteredModel));        
        mockery.assertIsSatisfied();
    }

    public void testSimpleUpdateAffectingRowsInFilteredModel() {
        filteredModel.setFilterValue(STRING3);

        //update affect the first 2 of the three rows passing the filter
        filteredModel.addTableModelListener(mockListener);
         mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 0, 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE))));
        }});
        testTableModel.setValueAt("test1", 8, 0 );
        testTableModel.setValueAt("test2", 9, 0 );
        testTableModel.setValueAt("test3", 10, 0 );
        testTableModel.fireTableRowsUpdated(8, 10);

        assertTrue(tableModelsAreEqual(readTableModel("/testSimpleUpdateAffectingRowsInFilteredModel.csv"), filteredModel));
        mockery.assertIsSatisfied();
    }

    public void testSimpleUpdateAffectingNoRowsInFilteredModel() {
        filteredModel.setFilterValue(STRING3);

        filteredModel.addTableModelListener(mockListener);

        testTableModel.setValueAt("test1", 0, 0 );
        testTableModel.setValueAt("test2", 1, 0 );
        testTableModel.setValueAt("test3", 2, 0 );
        testTableModel.fireTableRowsUpdated(0, 2);

        assertTrue(tableModelsAreEqual(readTableModel("/testSimpleUpdateAffectingNoRowsInFilteredModel.csv"), filteredModel));
        mockery.assertIsSatisfied();
    }

    public void testUpdateCausingInsertsInFilteredModel() {
        filteredModel.setFilterValue(STRING3);

        filteredModel.addTableModelListener(mockListener);

        mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 0, 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT))));
        }});
        testTableModel.setValueAt(STRING3, 0, 0 );
        testTableModel.setValueAt("wibble", 1, 0 );
        testTableModel.setValueAt(STRING3, 2, 0 );
        testTableModel.fireTableRowsUpdated(0, 2);

        assertTrue(tableModelsAreEqual(readTableModel("/testUpdateCausingInsertsInFilteredModel.csv"), filteredModel));
        mockery.assertIsSatisfied();
    }

    public void testUpdateCausingDeletesInFilteredModel() {
        filteredModel.setFilterValue(STRING3);

        filteredModel.addTableModelListener(mockListener);

        mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 1, 2, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE))));
        }});
        testTableModel.setValueAt("wibble", 10, 1 );
        testTableModel.setValueAt("wibble", 11, 1 );
        testTableModel.setValueAt("wibble", 12, 1 );
        testTableModel.fireTableRowsUpdated(10, 12);

        assertTrue(tableModelsAreEqual(readTableModel("/testUpdateCausingDeletesInFilteredModel.csv"), filteredModel));
        mockery.assertIsSatisfied();
    }

    public void testUpdateCausingInsertAndDeleteCausesDataChangedEvent() {
        filteredModel.setFilterValue(STRING3);

        filteredModel.addTableModelListener(mockListener);

        mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel))));
        }});
        testTableModel.setValueAt("wibble", 10, 1 );
        testTableModel.setValueAt(STRING3, 11, 1 );
        testTableModel.setValueAt("wibble", 12, 1 );
        testTableModel.fireTableRowsUpdated(10, 12);

        assertTrue(tableModelsAreEqual(readTableModel("/testUpdateCausingInsertAndDeleteCausesDataChangedEvent.csv"), filteredModel));
        mockery.assertIsSatisfied();
    }

    //this is possible provided the range of rows to insert is contiguous,
    //but we should receive the insert event first followed by updates for the surrounding rows
    public void testUpdateCausingUpdateInsertUpdate() {
        filteredModel.setFilterValue(STRING3);

        filteredModel.addTableModelListener(mockListener);

        mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 2, 2, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT))));
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 1, 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE))));
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 3, 3, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE))));
        }});
        testTableModel.setValueAt("test1", 10, 0 );
        testTableModel.setValueAt(STRING3, 11, 1 );
        testTableModel.setValueAt("test2", 12, 0 );
        testTableModel.fireTableRowsUpdated(10, 12);

        assertTrue(tableModelsAreEqual(readTableModel("/testUpdateCausingUpdateInsertUpdate.csv"), filteredModel));
        mockery.assertIsSatisfied();
    }

    public void testUpdateCausingUpdateInsert() {
        filteredModel.setFilterValue(STRING3);

        filteredModel.addTableModelListener(mockListener);

        mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 2, 2, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT))));
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 1, 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE))));
        }});
        testTableModel.setValueAt("test1", 10, 0 );
        testTableModel.setValueAt(STRING3, 11, 1 );
        testTableModel.fireTableRowsUpdated(10, 11);

        assertTrue(tableModelsAreEqual(readTableModel("/testUpdateCausingUpdateInsert.csv"), filteredModel));
        mockery.assertIsSatisfied();
    }

    public void testUpdateRequiringDiscontiguousInsertsCausesDataChangedEvent() {
        filteredModel.setFilterValue(STRING3);

        filteredModel.addTableModelListener(mockListener);

        mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel))));
        }});
        testTableModel.setValueAt(STRING3, 9, 1 );
        testTableModel.setValueAt("test1", 10, 0 );
        testTableModel.setValueAt(STRING3, 11, 1 );

        testTableModel.fireTableRowsUpdated(9, 11);

        assertTrue(tableModelsAreEqual(readTableModel("/testUpdateRequiringDiscontiguousInsertsCausesDataChangedEvent.csv"), filteredModel));
        mockery.assertIsSatisfied();
    }

    public void testUpdateCausingUpdateDeleteUpdate() {
        filteredModel.setFilterValue(STRING2);

        filteredModel.addTableModelListener(mockListener);

        mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 1, 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE))));
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 0, 0, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE))));
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 1, 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE))));
        }});
        testTableModel.setValueAt("test1", 4, 0 );
        testTableModel.setValueAt("wibble", 5, 1 );
        testTableModel.setValueAt("test2", 6, 0 );
        testTableModel.fireTableRowsUpdated(4, 6);

        assertTrue(tableModelsAreEqual(readTableModel("/testUpdateCausingUpdateDeleteUpdate.csv"), filteredModel));
        mockery.assertIsSatisfied();
    }

    public void testUpdateCausingDeleteUpdate() {
        filteredModel.setFilterValue(STRING2);

        filteredModel.addTableModelListener(mockListener);

        mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 1, 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE))));
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 1, 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE))));
        }});
        testTableModel.setValueAt("wibble", 5, 1 );
        testTableModel.setValueAt("test2", 6, 0 );
        testTableModel.fireTableRowsUpdated(5, 6);

        assertTrue(tableModelsAreEqual(readTableModel("/testUpdateCausingDeleteUpdate.csv"), filteredModel));
        mockery.assertIsSatisfied();
    }

    public void testUpdateRequiringDiscontiguousDeletesCausesDataChangeEvent() {
        filteredModel.setFilterValue(STRING2);

        filteredModel.addTableModelListener(mockListener);

        mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel))));
        }});
        testTableModel.setValueAt("wibble", 5, 1 );
        testTableModel.setValueAt("test1", 6, 0 );
        testTableModel.setValueAt("wibble", 7, 1 );
        testTableModel.fireTableRowsUpdated(5, 7);

        assertTrue(tableModelsAreEqual(readTableModel("/testUpdateRequiringDiscontiguousDeletesCausesDataChangeEvent.csv"), filteredModel));
        mockery.assertIsSatisfied();
    }

    public void testStructureChangedEvent() {
        filteredModel.setFilterValue(STRING1);

        filteredModel.addTableModelListener(mockListener);
        mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, TableModelEvent.HEADER_ROW))));
        }});

        ArrayList<ArrayList<Object>> rows = new ArrayList<ArrayList<Object>>();
        ArrayList<Object> row1 = new ArrayList<Object>();
        row1.add("row1"); row1.add("row1"); row1.add("row1");

        ArrayList<Object> row2 = new ArrayList<Object>();
        row2.add("row2"); row2.add("row2"); row2.add("string1");

        ArrayList<Object> row3 = new ArrayList<Object>();
        row3.add("row3"); row3.add("string1"); row3.add("row3");

        ArrayList<Object> row4 = new ArrayList<Object>();
        row4.add("row4"); row4.add("row4"); row4.add("row4");

        ArrayList<Object> row5 = new ArrayList<Object>();
        row5.add("string1"); row5.add("row5"); row5.add("row5");

        rows.add(row1); rows.add(row2); rows.add(row3); rows.add(row4); rows.add(row5);
        testTableModel.setTableData(rows);

        testTableModel.fireTableStructureChanged();

        assertTrue(tableModelsAreEqual(readTableModel("/testStructureChangedEvent.csv"), filteredModel));
        mockery.assertIsSatisfied();    
    }

    public void testInsertFollowedByDelete() {
         filteredModel.setFilterValue(STRING1);

        //All three of the rows in this range contain values which pass the filters, so inserting the range
        //should add three row in the filtered view
        filteredModel.addTableModelListener(mockListener);
        mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 4, 4, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT))));
        }});
        testTableModel.insertRows(7,
            (ArrayList<Object>)testTableModel.getRow(1).clone()
        );

        assertTrue(tableModelsAreEqual(readTableModel("/testInsertFollowedByDelete.csv"), filteredModel));
        mockery.assertIsSatisfied();

        mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, 1, 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE))));
        }});
        testTableModel.removeRow(1);
        assertTrue(tableModelsAreEqual(readTableModel("/testInsertFollowedByDelete2.csv"), filteredModel));
        mockery.assertIsSatisfied(); 
    }

    public void testDataChangedEvent() {
        filteredModel.setFilterValue(STRING1);

        filteredModel.addTableModelListener(mockListener);
        mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel))));
        }});

        testTableModel.setValueAt("string1",5,1);
        testTableModel.setValueAt("wibble",0,1);
        testTableModel.fireTableDataChanged();

        assertTrue(tableModelsAreEqual(readTableModel("/testDataChangedEvent.csv"), filteredModel));
        mockery.assertIsSatisfied();
    }

    public void testDateFormatter() throws ParseException {
        FixtureTableModelWithColumnClass fixtureTable = new FixtureTableModelWithColumnClass();
        filteredModel = new RowFilteringTableModel(fixtureTable);
        filteredModel.setFilterValue("January");
        assertEquals(0, filteredModel.getRowCount());

        filteredModel.setFormatter(new SimpleDateFormat("yyyy-MMMM-dd"), Date.class);
        assertEquals(1, filteredModel.getRowCount());
    }

    public void testNumberFormatter() throws ParseException {
        FixtureTableModelWithColumnClass fixtureTable = new FixtureTableModelWithColumnClass();
        filteredModel = new RowFilteringTableModel(fixtureTable);

        filteredModel.setFormatter(new DecimalFormat("#.#"), Number.class);
        //now the index should contain only numbers formatted to one decimal point
        filteredModel.setFilterValue("1.2");
        assertEquals(1, filteredModel.getRowCount());
        filteredModel.setFilterValue("1.23");
        assertEquals(0, filteredModel.getRowCount());

        filteredModel.setFilterValue("4.6"); //round up from .56 by formatter
        assertEquals(1, filteredModel.getRowCount());
        filteredModel.setFilterValue("4.56");
        assertEquals(0, filteredModel.getRowCount());

        filteredModel.clearFormatters();
        assertEquals(1, filteredModel.getRowCount());
    }

    public void testStructureChangeWhenNewTableModelSet() throws ParseException {
        FixtureTableModelWithColumnClass fixtureTable = new FixtureTableModelWithColumnClass();

        filteredModel.addTableModelListener(mockListener);
        mockery.checking(new Expectations() {{
            one(mockListener).tableChanged(with(equal(new TableModelEventWithEquals(filteredModel, TableModelEvent.HEADER_ROW))));
        }});

        filteredModel.setTableModel(fixtureTable);
        mockery.assertIsSatisfied();
    }

    //it makes no sense to keep any formatters set by column index when the column structure changes
    public void testFormattersByColIndexLostOnStructureChangeEventOrNewTableModelSet() throws ParseException {
        FixtureTableModelWithColumnClass fixtureTable = new FixtureTableModelWithColumnClass();
        filteredModel = new RowFilteringTableModel(fixtureTable);
        filteredModel.setFilterValue("row");
        assertEquals(2, filteredModel.getRowCount());

        filteredModel.setFormatter(FilterFormatter.EXCLUDE_FROM_FILTER_INDEX, 2);
        assertEquals(0, filteredModel.getRowCount());

        FixtureTableModelWithColumnClass fixtureTable2 = new FixtureTableModelWithColumnClass();
        filteredModel.setTableModel(fixtureTable2);
        assertEquals(2, filteredModel.getRowCount());

        filteredModel.setFormatter(FilterFormatter.EXCLUDE_FROM_FILTER_INDEX, 2);
        assertEquals(0, filteredModel.getRowCount());

        fixtureTable2.fireTableStructureChanged();
        assertEquals(2, filteredModel.getRowCount());
    }

    public void testFormattersByColClassKeptOnStructureChangeEventOrNewTableModelSet() throws ParseException {
        FixtureTableModelWithColumnClass fixtureTable = new FixtureTableModelWithColumnClass();
        filteredModel = new RowFilteringTableModel(fixtureTable);

        filteredModel.setFormatter(new DecimalFormat("#.#"), Number.class);
        filteredModel.setFilterValue("1.2");
        assertEquals(1, filteredModel.getRowCount());

        FixtureTableModelWithColumnClass fixtureTable2 = new FixtureTableModelWithColumnClass();
        filteredModel.setTableModel(fixtureTable2);
        assertEquals(1, filteredModel.getRowCount());

        fixtureTable2.fireTableStructureChanged();
        assertEquals(1, filteredModel.getRowCount());
    }

    public void testFormattersByColNameKeptOnStructureChangeEventOrNewTableModelSet() throws ParseException {
        FixtureTableModelWithColumnClass fixtureTable = new FixtureTableModelWithColumnClass();
        filteredModel = new RowFilteringTableModel(fixtureTable);

        filteredModel.setFormatter(new DecimalFormat("#.#"), "1");
        filteredModel.setFilterValue("1.2");
        assertEquals(1, filteredModel.getRowCount());

        FixtureTableModelWithColumnClass fixtureTable2 = new FixtureTableModelWithColumnClass();
        filteredModel.setTableModel(fixtureTable2);
        assertEquals(1, filteredModel.getRowCount());

        fixtureTable2.fireTableStructureChanged();
        assertEquals(1, filteredModel.getRowCount());
    }

    public void testAddFormatterForMultipleIndexes() throws ParseException {
        FixtureTableModelWithColumnClass fixtureTable = new FixtureTableModelWithColumnClass();
        filteredModel = new RowFilteringTableModel(fixtureTable);

        filteredModel.setFormatter(FilterFormatter.EXCLUDE_FROM_FILTER_INDEX, "2", "3");
        //after those columns are excluded by the exlusion formatter, filters set on their contents will fail
        filteredModel.setFilterValue("row");
        assertEquals(0, filteredModel.getRowCount());
        filteredModel.setFilterValue("3.4");
        assertEquals(0, filteredModel.getRowCount());
    }

    public void testFormatterPriority() throws ParseException {
        FixtureTableModelWithColumnClass fixtureTable = new FixtureTableModelWithColumnClass();
        filteredModel = new RowFilteringTableModel(fixtureTable);

        filteredModel.setFilterValue("row");
        assertEquals(2, filteredModel.getRowCount());
        filteredModel.setFormatter(FilterFormatter.EXCLUDE_FROM_FILTER_INDEX, String.class);
        assertEquals(0, filteredModel.getRowCount());

        //col name filter takes priority over class filter
        filteredModel.setFormatter(new FilterColumnConfig.ToStringFilterFormat(), "2");
        assertEquals(2, filteredModel.getRowCount());

        //col index filter takes priority over col name filter
        filteredModel.setFormatter(FilterFormatter.EXCLUDE_FROM_FILTER_INDEX, 2);
        assertEquals(0, filteredModel.getRowCount());

        //col structure change removes the col index filter but leaves the others
        fixtureTable.fireTableStructureChanged();
        assertEquals(2, filteredModel.getRowCount());
    }

    public static class TableModelEventWithEquals extends TableModelEvent {

        public TableModelEventWithEquals(TableModel source) {
            super(source);
        }

        public TableModelEventWithEquals(TableModel source, int firstRow, int lastRow, int column, int type) {
            super(source, firstRow, lastRow, column, type);
        }

        public TableModelEventWithEquals(RowFilteringTableModel filteredModel, int headerRow) {
            super(filteredModel, headerRow);
        }

        public int hashCode() {
            int result = 17;
            result = 31 * result + getFirstRow();
            result = 31 * result + getLastRow();
            result = 31 * result + getType();
            result = 31 * result + getColumn();
            return result;
        }

        public boolean equals(Object o) {
            boolean result = false;
            if ( o==this ) {
                result = true;
            } else {
                if ( o instanceof TableModelEvent) {
                    TableModelEvent t = (TableModelEvent)o;
                    if ( LOG_EVENTS ) {
                        logEvent("Expected Event", this);
                        logEvent("Actual Event", t );
                    }
                    result = t.getFirstRow() == getFirstRow() &&
                            t.getLastRow() == getLastRow() &&
                            t.getColumn() == getColumn() &&
                            t.getType() == getType() &&
                            t.getSource() == getSource();
                }
            }
            return result;
        }

        private void logEvent(String title, TableModelEvent t) {
            StringBuilder sb = new StringBuilder(title);
            System.out.println(sb.append(":").append(
                    " firstrow: ").append(t.getFirstRow()).append(
                    " lastrow: ").append(t.getLastRow()).append(
                    " col: ").append(t.getColumn()).append(
                    " type: ").append(t.getType()).append(
                    " source ").append(t.getSource()));
        }
    }
}
