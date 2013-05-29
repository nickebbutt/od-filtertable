package com.od.filtertable.suffixtree;

import com.od.filtertable.index.MutableCharSequence;

/**
 * User: nick
 * Date: 23/05/13
 * Time: 09:41
 */
public class CharSequenceWithTerminalNode implements MutableCharSequence{

    private CharSequence s;
    private int start;
    private int end;

    public CharSequenceWithTerminalNode() {
    }

    public CharSequenceWithTerminalNode(CharSequence s) {
        init(s);
    }

    public void setSequence(CharSequence c) {
        init(c);
    }

    private void init(CharSequence s) {
        this.s = s;
        this.start = 0;       
        this.end = s.length() + 1;
    }

    @Override
    public int length() {
        return end - start;
    }

    @Override
    public int getBaseSequenceLength() {
        return s.length() + 1;
    }

    @Override
    public CharSequence getBaseSequence() {
        StringBuilder sb = new StringBuilder(s);
        sb.append(CharUtils.TERMINAL_CHAR);
        return sb.toString();
    }

    @Override
    public char charAt(int index) {
        return index == length() - 1 ? 
            CharUtils.TERMINAL_CHAR :
            s.charAt(start + index);
    }

    @Override
    public void setStart(int start) {
        this.start = start;
    }

    @Override
    public int getStart() {
        return this.start;
    }

    @Override
    public void incrementStart(int v) {
        this.start += v;
    }

    @Override
    public void decrementStart(int v) {
        this.start -= v;
    }

    @Override
    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public int getEnd() {
        return this.end;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        throw new UnsupportedOperationException();
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
}
