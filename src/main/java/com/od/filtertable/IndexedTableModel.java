package com.od.filtertable;

import javax.swing.table.TableModel;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Sep-2009
 * Time: 11:50:03
 */
public interface IndexedTableModel extends TableModel {
    
    boolean isCellMatchingSearch(int rowIndex, int colIndex);

}
