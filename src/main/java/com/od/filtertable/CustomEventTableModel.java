/**
 *  Copyright (C) Nick Ebbutt September 2009
 *  nick@objectdefinitions.com
 *  http://www.objectdefinitions.com/filtertable
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

package com.od.filtertable;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19-Sep-2008
 * Time: 13:19:54
 *
 * A table model which allows the event creation to be overridden and re-implements the fire methods
 */
public abstract class CustomEventTableModel extends AbstractTableModel {

    /**
     *  Subclasses may override this method to return their own subclass of TableModelEvent if required
     */
    protected TableModelEvent createTableModelEvent(TableModel source, int startRow, int endRow, int col, int eventType) {
        return new TableModelEvent(source, startRow, endRow, col, eventType);
    }

    public void fireTableDataChanged() {
        fireTableChanged(createDataChangedEvent());
    }

    public void fireTableStructureChanged() {
        fireTableChanged(createTableStructureChangedEvent());
    }

    public void fireTableRowsInserted(int firstRow, int lastRow) {
        fireTableChanged(createTableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    public void fireTableRowsUpdated(int firstRow, int lastRow) {
        fireTableChanged(createTableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
    }

    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        fireTableChanged(createTableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
    }

    public void fireTableCellUpdated(int row, int column) {
        fireTableChanged(createTableModelEvent(this, row, row, column, TableModelEvent.UPDATE));
    }

    protected TableModelEvent createDataChangedEvent() {
        return createTableModelEvent(this, 0, Integer.MAX_VALUE, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
    }

    protected TableModelEvent createTableStructureChangedEvent() {
        return createTableModelEvent(this, TableModelEvent.HEADER_ROW, TableModelEvent.HEADER_ROW, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE );
    }

}
