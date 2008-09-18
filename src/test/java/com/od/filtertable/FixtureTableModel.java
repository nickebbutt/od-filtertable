/**
 *  Copyright (C) Nick Ebbutt September 2009
 *
 *  This file is part of ObjectDefinitions Ltd. FilterTable.
 *  nick@objectdefinitions.com
 *  http://www.objectdefinitions.com/filtertable
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
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 12-Jul-2008
 * Time: 10:46:58
 * To change this template use File | Settings | File Templates.
 */
public class FixtureTableModel extends AbstractTableModel implements Cloneable
{
    private ArrayList<ArrayList<Object>> tableData;
    private Class[] columnClasses;

    public FixtureTableModel(ArrayList<ArrayList<Object>> tableData) {
        setTableData(tableData);
    }

    public void setTableData(ArrayList<ArrayList<Object>> tableData) {
        this.tableData = tableData;
        columnClasses = new Class[getColumnCount()];
    }

    public void setColumnClass(int colIndex, Class columnClass) {
        this.columnClasses[colIndex] = columnClass;
    }

    public void insertRow(int index, ArrayList<Object> row) {
        tableData.add(index, row);
        fireTableRowsInserted(index, index);
    }

    public void insertRows(int index, ArrayList<Object>... rows ) {
        for ( int loop=rows.length-1 ; loop >= 0; loop--) {
            tableData.add(index, rows[loop]);
        }
        fireTableRowsInserted(index, (index + rows.length)- 1);
    }

    public void removeRow(int index) {
        tableData.remove(index);
        fireTableRowsDeleted(index, index);
    }

    public void removeRows(int firstAffectedIndex, int lastAffectedIndex) {
        int affected = (lastAffectedIndex - firstAffectedIndex) + 1;
        for ( int loop=0; loop < affected; loop ++) {
            tableData.remove(firstAffectedIndex);
        }
        fireTableRowsDeleted(firstAffectedIndex, lastAffectedIndex);
    }

    public ArrayList<Object> getRow(int index) {
        return tableData.get(index);
    }

    public String getString(int row, int col) {
        return (String)getValueAt(row, col);
    }

    public int getRowCount() {
        return tableData.size();
    }

    public int getColumnCount() {
        return tableData.size() == 0 ? 0 : tableData.get(0).size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return tableData.get(rowIndex).get(columnIndex);
    }

    public String getColumnName(int colIndex) {
        return String.valueOf(colIndex);
    }

    public void setValueAt(Object value, int rowIndex, int colIndex) {
        tableData.get(rowIndex).set(colIndex, value);
    }

    public Class getColumnClass(int col) {
        return columnClasses[col] == null ? String.class : columnClasses[col];
    }

}
