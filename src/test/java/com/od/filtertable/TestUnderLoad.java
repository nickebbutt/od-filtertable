package com.od.filtertable;

import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.lang.reflect.InvocationTargetException;

/**
 * User: nick
 * Date: 06/02/13
 * Time: 08:47
 */
public class TestUnderLoad {


    private static final int DIFFERENT_VALUES_COUNT = 10000;
    private static final int MAX_STRING_LENGTH = 20;
    private static final int ROW_COUNT = 500;

    private String[] testStrings = new String[DIFFERENT_VALUES_COUNT];
    private double[] testDoubles = new double[DIFFERENT_VALUES_COUNT];
    private JTable table;
    private DefaultTableModel tableModel;

    @Before
    public void prepare() {
        testStrings = new TestStringGenerator(DIFFERENT_VALUES_COUNT).getTestStrings();
        for ( int count = 0; count < DIFFERENT_VALUES_COUNT ; count++) {
            testDoubles[count] = generateTestDouble();
        }
    }

    @Test
    public void doTest() throws InvocationTargetException, InterruptedException {
        Runnable runnable = new Runnable() {
            public void run() {
                tableModel = new DefaultTableModel(ROW_COUNT, 2);
                for ( int row = 0; row < ROW_COUNT ; row++) {
                    tableModel.setValueAt(testStrings[row], row, 0);
                    tableModel.setValueAt(testDoubles[row], row, 1);
                }
                JFrame frame = new JFrame();
                table = new JTable(tableModel);
                frame.getContentPane().add(new JScrollPane(table));
                frame.setSize(800, 600);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        };
        SwingUtilities.invokeAndWait(runnable);
        runForModel(tableModel);

        Runnable runnable1 = new Runnable() {
            public void run() {
                prepare();
                RowFilteringTableModel rowFilteringTableModel = new RowFilteringTableModel(tableModel, false, 2);
                rowFilteringTableModel.setIncludeSubstringsInSearch(true);
                rowFilteringTableModel.setSearchTerm("AB");
                table.setModel(rowFilteringTableModel);
            }
        };
        SwingUtilities.invokeAndWait(runnable1);
        runForModel(tableModel);

    }

    private void runForModel(final DefaultTableModel model) throws InvocationTargetException, InterruptedException {
        long endTime = System.currentTimeMillis() + 5000;
        int iterations = 1;
        while(System.currentTimeMillis() < endTime) {
            final int currentIteration = iterations;
            Runnable updateTable = new Runnable() {
                public void run() {
                    model.setValueAt(testStrings[currentIteration % DIFFERENT_VALUES_COUNT ], currentIteration % ROW_COUNT, 0);
                    model.setValueAt(testDoubles[currentIteration % DIFFERENT_VALUES_COUNT ], currentIteration % ROW_COUNT, 1);
                }
            };
            SwingUtilities.invokeAndWait(updateTable);
            iterations++;
        }
        System.out.println("Iterations: " + iterations);
    }

    private double generateTestDouble() {
        return Math.random();
    }

}
