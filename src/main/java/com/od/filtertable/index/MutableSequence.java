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

package com.od.filtertable.index;

/**
 * Avoids generating unnecessary String instances using String.substring(x,x)
 * Otherwise each indexing generates thousands of short lived String instances
 */
public class MutableSequence implements MutableCharSequence {

    public static final String EMPTY_SEGMENT = "";
    private CharSequence segment;
    private int start = 0;
    private int end = 0;

    public MutableSequence() {}

    public MutableSequence(CharSequence c) {
        this.segment = c;
        this.end = segment.length();
    }
    
    @Override
    public int length() {
        return end - start;
    }

    @Override
    public int totalSequenceLength() {
        return segment.length();
    }

    @Override
    public char charAt(int index) {
        return segment.charAt(start + index);
    }

    public void setSegment(CharSequence segment) {
        this.segment = segment == null ? EMPTY_SEGMENT : segment;
    }

    @Override
    public void setStart(int start) {
        this.start = start;
    }

    @Override
    public void incrementStart(int v) {
        start += v;
    }
    
    @Override
    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public char[] toArray(int start, int end) {
        int length = end - start;
        char[] result = new char[length];
        for (int c = 0; c < length; c++) {
            result[c] = charAt(c + start);
        }
        return result;
    }
    
    @Override
    public CharSequence subSequence(int start, int end) {
        throw new UnsupportedOperationException("MutableCharSequence does not support subSequence");
    }

}
