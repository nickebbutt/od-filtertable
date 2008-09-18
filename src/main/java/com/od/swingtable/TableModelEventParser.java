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

import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 01-May-2008
 * Time: 10:59:31
 *
 * This utility class makes it easier to implement a TableModelListener correctly. When implementing TableModelListener
 * it is easy to forget to handle some valid combinations of event parameters.
 * By using the TableModelEventParser to listed to the TableEvent, and implementing the more granular
 * TableModelEventParserListener instead, it is harder to forget.
 */
public class TableModelEventParser implements TableModelListener {
    private TableModelEventParserListener eventParserListener;

    public TableModelEventParser(TableModelEventParserListener eventParserListener) {
        this.eventParserListener = eventParserListener;
    }

    public void tableChanged(TableModelEvent e) {
        switch ( e.getType() ) {
            case ( TableModelEvent.UPDATE) :
                if ( e.getFirstRow() == TableModelEvent.HEADER_ROW) {
                    eventParserListener.tableStructureChanged(e);
                } else if ( e.getFirstRow() == 0 && e.getLastRow() == Integer.MAX_VALUE) {
                    eventParserListener.tableDataChanged(e);
                } else if ( e.getColumn() == TableModelEvent.ALL_COLUMNS) {
                    eventParserListener.tableRowsUpdated(e.getFirstRow(), e.getLastRow(), e);
                } else {
                    eventParserListener.tableCellsUpdated(e.getFirstRow(), e.getLastRow(), e.getColumn(), e);
                }
                break;
            case ( TableModelEvent.INSERT) :
                if ( e.getColumn() == TableModelEvent.ALL_COLUMNS) {
                    eventParserListener.tableRowsInserted(e.getFirstRow(), e.getLastRow(), e);
                } else {
                    //it is not clear what an insert affecting just 1 column
                    //which does not affect structure would mean, so treat it as all data changed
                    eventParserListener.tableDataChanged(e);
                }
                break;
            case ( TableModelEvent.DELETE) :
                if ( e.getColumn() == TableModelEvent.ALL_COLUMNS) {
                    eventParserListener.tableRowsDeleted(e.getFirstRow(), e.getLastRow(), e);
                } else {
                    //it is not clear what a delete affecting just 1 column
                    //which does not affect structure would mean, so treat it as all data changed
                    eventParserListener.tableDataChanged(e);
                }
                break;
            default :
                //this is an event of unknown type, treat as a structure changed
                eventParserListener.tableStructureChanged(e);
        }

    }

    public static interface TableModelEventParserListener {

        void tableStructureChanged(TableModelEvent e);

        void tableDataChanged(TableModelEvent e);

        void tableRowsUpdated(int firstRow, int lastRow, TableModelEvent e);

        void tableCellsUpdated(int firstRow, int lastRow, int column, TableModelEvent e);

        void tableRowsDeleted(int firstRow, int lastRow, TableModelEvent e);

        void tableRowsInserted(int firstRow, int lastRow, TableModelEvent e);
    }

}

