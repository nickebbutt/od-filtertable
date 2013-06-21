package com.od.filtertable.radixtree;

import com.od.filtertable.index.CharSequenceWithIntTerminator;

/**
 * User: nick
 * Date: 23/05/13
 * Time: 09:41
 */
public class CharSequenceWithTerminalNode implements CharSequenceWithIntTerminator {

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
            Character.MAX_VALUE :
            s.charAt(index);
    }
    
    public int intAt(int index) {
        return index == length() - 1 ?
            Character.MAX_VALUE + 1 :
            s.charAt(index);    
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        throw new UnsupportedOperationException();
    }

}

