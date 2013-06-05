package com.od.filtertable.radixtree;

/**
 * User: nick
 * Date: 23/05/13
 * Time: 09:41
 */
public class CharSequenceWithTerminalNode implements CharSequence {

    private CharSequence s;

    public CharSequenceWithTerminalNode(CharSequence s) {
        this.s = s;
    }

    @Override
    public int length() {
        return s.length() + 1;
    }

    @Override
    public char charAt(int index) {
        return index == length() - 1 ?
                CharUtils.TERMINAL_CHAR :
                s.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        throw new UnsupportedOperationException();
    }

}

