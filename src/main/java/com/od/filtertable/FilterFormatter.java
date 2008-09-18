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

import java.text.Format;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12-Sep-2008
 * Time: 11:26:40
 */
public interface FilterFormatter {

    public static final FilterFormatter EXCLUDE_FROM_FILTER_INDEX = new NullFormat();

    /**
     * @param o, value from table cell
     * @return CharSequence to use for filtering, or null if cell is not to be indexed
     */
    CharSequence format(Object o);


    static class NullFormat implements FilterFormatter {
        public CharSequence format(Object o) {
            return null;
        }
    }

    public static class FormatAdapterFormatter implements FilterFormatter {
        private Format f;

        public FormatAdapterFormatter(Format f) {
            this.f = f;
        }

        public CharSequence format(Object o) {
            return o == null ? null : f.format(o);
        }
    }
    
}
