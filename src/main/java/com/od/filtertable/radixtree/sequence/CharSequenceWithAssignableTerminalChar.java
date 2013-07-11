package com.od.filtertable.radixtree.sequence;

/**
 * User: nick
 * Date: 23/05/13
 * Time: 09:41
 * 
 * A CharSequenceWithIntTerminator in which an int is specified as the terminator value.
 * 
 * This should be used when adding sequences to a generalized suffix tree structure, since in this case each
 * sequence added (and its suffixes) must have a distinct terminator
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
                TERMINATOR_CHAR_REPRESENTATION :
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

