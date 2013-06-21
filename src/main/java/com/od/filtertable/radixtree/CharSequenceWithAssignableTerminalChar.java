package com.od.filtertable.radixtree;

import com.od.filtertable.index.CharSequenceWithIntTerminator;

/**
 * User: nick
 * Date: 23/05/13
 * Time: 09:41
 */
public class CharSequenceWithAssignableTerminalChar implements CharSequenceWithIntTerminator {

    private CharSequence s;
    private int terminalInt;

    public CharSequenceWithAssignableTerminalChar(CharSequence s, int terminalInt) {
        this.s = s;
        this.terminalInt = terminalInt;
    }

    @Override
    public int length() {
        return s.length() + 1;
    }

    @Override
    public char charAt(int index) {
        return index == length() - 1 ?
                (char) terminalInt :
                s.charAt(index);
    }
    
    public int intAt(int index) {
        return index == length() - 1 ?
                terminalInt :
                s.charAt(index); 
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        throw new UnsupportedOperationException();
    }

}

