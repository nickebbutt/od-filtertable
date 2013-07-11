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

package com.od.filtertable.radixtree;

import com.od.filtertable.radixtree.sequence.CharSequenceWithIntTerminator;

/**
 * Avoids generating unnecessary String instances using String.substring(x,x)
 * Otherwise each indexing generates thousands of short lived String instances
 */
public class MutableSequence implements MutableCharSequence {

    public static final CharSequenceWithIntTerminator EMPTY_SEGMENT = new MutableSequence();
    protected CharSequenceWithIntTerminator segment;
    protected int start = 0;
    protected int end = 0;

    public MutableSequence() {}

    public MutableSequence(CharSequenceWithIntTerminator c) {
        this.segment = c;
        this.end = segment.length();
    }

    public MutableSequence(CharSequenceWithIntTerminator s, int start, int end) {
        this.segment = s;
        this.start = start;
        this.end = end;
    }

    public void setSegment(CharSequenceWithIntTerminator segment) {
        this.segment = segment == null ? EMPTY_SEGMENT : segment;
        this.start = 0;
        this.end = segment.length();
    }
    
    @Override
    public int length() {
        return end - start;
    }

    @Override
    public int getBaseSequenceLength() {
        return segment instanceof MutableSequence ? 
            ((MutableSequence)segment).getBaseSequenceLength() : 
            segment.length();
    }

    @Override
    public CharSequenceWithIntTerminator getImmutableBaseSequence() {
        return segment instanceof MutableSequence ?
            ((MutableSequence)segment).getImmutableBaseSequence() :
            segment;
    }

    @Override
    public char charAt(int index) {
        return segment.charAt(start + index);
    }

    @Override
    public int intAt(int index) {
        return segment.intAt(start + index);
    }

    @Override
    public void setStart(int start) {
        this.start = start;
    }

    @Override
    public int getBaseSequenceStart() {
        return segment instanceof MutableSequence ? 
            ((MutableSequence) segment).getBaseSequenceStart() + start :
            start;
    }

    @Override
    public void incrementStart(int v) {
        start += v;
    }

    @Override
    public void decrementStart(int v) {
        start -= v;
    }

    @Override
    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public int getBaseSequenceEnd() {
        return getBaseSequenceStart() + length();
    }
    
    @Override
    public CharSequence subSequence(int start, int end) {
        throw new UnsupportedOperationException("MutableCharSequence does not support subSequence");
    }
    
    public String toString() {
        return new String(CharUtils.createCharArray(this));
    }

}
