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
import java.util.StringTokenizer;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Sep-2008
 * Time: 16:06:16
 */
public class TableParser {

    public FixtureTableModel readBoard(String pathToBoardResource) {
        String board = FileUtil.readFile(pathToBoardResource);
        ArrayList<ArrayList<Object>> tokens = getRows(board);
        return new FixtureTableModel(tokens);
    }

    private ArrayList<ArrayList<Object>> getRows(String board)
    {
        BufferedReader r = new BufferedReader(new StringReader(board));
        ArrayList<ArrayList<Object>> boardData = new ArrayList<ArrayList<Object>>();
        try {
            String row = r.readLine();
            while ( row != null  && row.trim().length() > 0) {
                boardData.add(getRowData(row));
                row = r.readLine();
            }
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        } finally {
            try
            {
                r.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return boardData;
    }

    private ArrayList<Object> getRowData(String row) {
        ArrayList<Object> tokens = new ArrayList<Object>();
        StringTokenizer st = new StringTokenizer(row, ",");
        while ( st.hasMoreTokens() ) {
            tokens.add(
               st.nextToken().trim()
            );
        }
        return tokens;
    }



}
