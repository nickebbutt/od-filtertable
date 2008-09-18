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

import javax.swing.table.TableModel;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12-Sep-2008
 * Time: 11:52:20
 *
 * Stores formatters set by column class, column name and column index
 * Calculates which formatter to use to index the values in each column, 
 * when the table structure changes
 */
class FilterColumnConfig {

    private TableModel tableModel;
    private FilterFormatter[] formattersByColumnIndex;
    
    private Map<Class, FilterFormatter> columnClassFormatterMap = new HashMap<Class, FilterFormatter>();
    private Map<Integer, FilterFormatter> columnIndexFormatterMap = new HashMap<Integer, FilterFormatter>();
    private Map<String, FilterFormatter> columnNameFormatterMap = new HashMap<String, FilterFormatter>();

    public FilterColumnConfig(TableModel tableModel) {
        this.tableModel = tableModel;
        addDefaultFormatters();
        recalcFiltersColumns();
    }

    public void tableChanged(TableModel tableModel) {
        this.tableModel = tableModel;
        columnIndexFormatterMap.clear();
        recalcFiltersColumns();
    }

    public void setFormatter(FilterFormatter filterFormat, Integer... columnIndexes) {
        if ( columnIndexes.length > 0) {
            for ( Integer index : columnIndexes ) {
                columnIndexFormatterMap.put(index, filterFormat);
            }
            recalcFiltersColumns();
        }
    }

    public void setFormatter(FilterFormatter filterFormat, Class... columnClasses) {
        if ( columnClasses.length > 0 ) {
            for ( Class clazz : columnClasses) {
                columnClassFormatterMap.put(clazz, filterFormat);
            }
            recalcFiltersColumns();
        }
    }

    public void setFormatter(FilterFormatter filterFormat, String... columnNames) {
        if ( columnNames.length > 0 ) {
            for ( String name : columnNames) {
                columnNameFormatterMap.put(name, filterFormat);
            }
            recalcFiltersColumns();
        }
    }

    public void clearFormatters() {
        columnClassFormatterMap.clear();
        columnIndexFormatterMap.clear();
        columnNameFormatterMap.clear();
        addDefaultFormatters();
        recalcFiltersColumns();
    }

    public void recalcFiltersColumns() {
        formattersByColumnIndex = new FilterFormatter[tableModel.getColumnCount()];
        for( int col=0; col < tableModel.getColumnCount(); col ++) {
            formattersByColumnIndex[col] = calculateFilterFormatter(col);
        }
    }

    public FilterFormatter getFormatter(int columnIndex) {
        return formattersByColumnIndex[columnIndex];
    }

    private void addDefaultFormatters() {
        columnClassFormatterMap.put(Object.class, new ToStringFilterFormat());
    }

    private FilterFormatter calculateFilterFormatter(int col) {
        FilterFormatter format = columnIndexFormatterMap.get(col);

        if ( format == null) {
            format = columnNameFormatterMap.get(tableModel.getColumnName(col));
        }
        if ( format == null ) {
            format = getFilterFormatter(tableModel.getColumnClass(col));
        }
        if ( format == null ) {
            format = FilterFormatter.EXCLUDE_FROM_FILTER_INDEX;
        }
        return format;
    }

    private FilterFormatter getFilterFormatter(Class columnClass) {
        FilterFormatter result = null;
        if ( columnClass != null) {
            result = columnClassFormatterMap.get(columnClass);
            if ( result == null ) {
                result = getFilterFormatter(columnClass.getSuperclass());
            }
        }
        return result;
    }

    public static class ToStringFilterFormat implements FilterFormatter {
        public CharSequence format(Object o) {
            return o == null ? null : o.toString();
        }
    }

}
