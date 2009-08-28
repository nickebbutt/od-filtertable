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

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 03-Sep-2008
 * Time: 17:48:33
 */
public class FilterTestUI {

    public FilterTestUI() {
        FixtureTableModel testTableModel = new TableParser().readBoard("/test1.csv");
        final RowFilteringTableModel filteredModel = new RowFilteringTableModel(testTableModel);

        JTable table = new JTable(filteredModel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        final JTextField textField = new JTextField(20);
        Box b = Box.createHorizontalBox();
        b.add(textField);
        b.add(Box.createHorizontalGlue());
        panel.add(b, BorderLayout.SOUTH);

//        textField.addActionListener(
//            new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    filteredModel.setFilterValue(textField.getText());
//                }
//            }
//        );

        textField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                long start=System.currentTimeMillis();
                filteredModel.setSearchTerm(textField.getText());
                System.out.println("filter: " + (System.currentTimeMillis() - start) + " millis");
            }

            public void removeUpdate(DocumentEvent e) {
                filteredModel.setSearchTerm(textField.getText());
            }

            public void changedUpdate(DocumentEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        JFrame frame = new JFrame();
        frame.getContentPane().add(panel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(
            new Runnable() {
                public void run() {
                    new FilterTestUI();                      }
                }
        );
    }
}
