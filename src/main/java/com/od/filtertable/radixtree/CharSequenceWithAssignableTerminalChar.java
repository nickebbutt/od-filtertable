package com.od.filtertable.radixtree;

/**
 * User: nick
 * Date: 23/05/13
 * Time: 09:41
 */
public class CharSequenceWithAssignableTerminalChar implements CharSequence {

    private CharSequence s;
    private char terminalChar;

    public CharSequenceWithAssignableTerminalChar(CharSequence s, char terminalChar) {
        this.s = s;
        this.terminalChar = terminalChar;
    }

    @Override
    public int length() {
        return s.length() + 1;
    }

    @Override
    public char charAt(int index) {
        return index == length() - 1 ?
                terminalChar :
                s.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        throw new UnsupportedOperationException();
    }

}

