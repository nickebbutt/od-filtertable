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

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 15-Sep-2008
 * Time: 15:54:55
 */
public class FixtureTableModelWithColumnClass extends FixtureTableModel {

    public FixtureTableModelWithColumnClass() throws ParseException {
        super(new ArrayList<ArrayList<Object>>());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        ArrayList<Object> rowA = new ArrayList<Object>();
        rowA.add(simpleDateFormat.parse("2008-01-01"));
        rowA.add(1.23d);
        rowA.add("rowA");
        rowA.add(4.56f);

        ArrayList<Object> rowB = new ArrayList<Object>();
        rowB.add(simpleDateFormat.parse("2008-02-01"));
        rowB.add(7.8d);
        rowB.add("rowB");
        rowB.add(9.0f);
        
        ArrayList<ArrayList<Object>> rowData = new ArrayList<ArrayList<Object>>();
        rowData.add(rowA);
        rowData.add(rowB);
        setTableData(rowData);

        setColumnClass(0, Date.class);
        setColumnClass(1, Double.class);
        setColumnClass(2, String.class);
        setColumnClass(3, Float.class);
    }

}
