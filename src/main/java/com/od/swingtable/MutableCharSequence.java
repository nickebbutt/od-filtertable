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

/**
 * Avoids generating unnecessary String instances using String.substring(x,x)
 * Otherwise each indexing generates thousands of short lived String instances
 */
class MutableCharSequence implements CharSequence {

    public static final String EMPTY_SEGMENT = "";
    private CharSequence segment;
    private int start = 0;
    private int end = 0;

    public MutableCharSequence() {}

    public int length() {
        return end - start;
    }

    public int totalSequenceLength() {
        return segment.length();
    }

    public char charAt(int index) {
        return segment.charAt(start + index);
    }

    public void setSegment(CharSequence segment) {
        this.segment = segment == null ? EMPTY_SEGMENT : segment;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public CharSequence subSequence(int start, int end) {
        throw new UnsupportedOperationException("MutableCharSequence does not support subSequence");
    }
}
