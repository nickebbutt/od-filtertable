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
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 17-Sep-2008
 * Time: 10:56:28
 *
 * If there are many filtered models and the user does a lot of searching, the size of the 
 * indexes could grow over time. This class provides a mechanism to trim the indexes back to their
 * initial size every so often.
 */
public class TimedIndexTrimmer {

    private final List<WeakReference<RowFilteringTableModel>> filteredModels =
            Collections.synchronizedList(
                    new ArrayList<WeakReference<RowFilteringTableModel>>()
            );

    private final int trimPeriodMillis;
    private Timer timer;

    public TimedIndexTrimmer() {
        this(1000 * 60 * 5);
    }

    public TimedIndexTrimmer(int trimPeriodMillis) {
        this.trimPeriodMillis = trimPeriodMillis;
        createTimer();
    }

    public void addModel(RowFilteringTableModel rowFilteringTableModel) {
        filteredModels.add(new WeakReference<RowFilteringTableModel>(rowFilteringTableModel));
    }

    public void startIndexTrimming() {
        timer.start();
    }

    public void stopIndexTrimming() {
        timer.stop();
    }

    private void createTimer() {
        timer = new Timer(trimPeriodMillis, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                trimEachModelOrRemoveFromList();
            }
        });
    }

    private void trimEachModelOrRemoveFromList() {
        synchronized (filteredModels) {
            Iterator<WeakReference<RowFilteringTableModel>> i = filteredModels.iterator();
            WeakReference<RowFilteringTableModel> currentReference;
            while(i.hasNext()) {
                currentReference = i.next();
                RowFilteringTableModel t = currentReference.get();
                if ( t != null ) {
                    //Although trimming is fast, do the models one at a time, so as not to bog down the event thread
                    //if there are many models to trim
                    SwingUtilities.invokeLater(new TrimModelRunnable(t));
                } else {
                    i.remove();
                }
            }
        }
    }

    private static class TrimModelRunnable implements Runnable {
        private RowFilteringTableModel rowFilteringTableModel;

        public TrimModelRunnable(RowFilteringTableModel rowFilteringTableModel) {
            this.rowFilteringTableModel = rowFilteringTableModel;
        }

        public void run() {
            rowFilteringTableModel.trimIndexToInitialDepth();
        }
    }
}
