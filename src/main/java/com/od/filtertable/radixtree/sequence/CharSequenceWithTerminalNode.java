package com.od.filtertable.radixtree.sequence;

/**
 * User: nick
 * Date: 23/05/13
 * Time: 09:41
 * 
 * A CharSequenceWithIntTerminator we can use when adding values to a radix tree structure, where each sequence should 
 * be suffixed with a terminal value. 
 * 
 * Provided we are not adding to a generalized suffix tree structure (and we are not adding suffixes), then we can use 
 * the same terminal value for each sequence added, here Character.MAX_VALUE + 1;
 * 
 * Because of this we don't have to maintain a reference to a assignable terminal char as in CharSequenceWithAssignableTerminalChar,
 * and hence this is more memory efficient.
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
            TERMINATOR_CHAR_REPRESENTATION : //represent the int terminator value as a char
            s.charAt(index);
    }
    
    public int intAt(int index) {
        return index == length() - 1 ?
            Character.MAX_VALUE + 1 : //int value at the start of the terinator char range
            s.charAt(index);    
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        throw new UnsupportedOperationException();
    }

}

