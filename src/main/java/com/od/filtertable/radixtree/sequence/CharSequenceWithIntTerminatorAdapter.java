package com.od.filtertable.radixtree.sequence;

/**
* User: nick
* Date: 20/06/13
* Time: 09:11
* 
* Adapt a CharSequence as a CharSequenceWithIntTerminator
*/
public class CharSequenceWithIntTerminatorAdapter implements CharSequenceWithIntTerminator {
    
    private CharSequence charSequence;

    public CharSequenceWithIntTerminatorAdapter() {}
    
    public CharSequenceWithIntTerminatorAdapter(CharSequence charSequence) {
        this.charSequence = charSequence;
    }

    public void setCharSequence(CharSequence charSequence) {
        this.charSequence = charSequence;
    }

    public int intAt(int index) {
        return charSequence.charAt(index);
    }
    
    @Override
    public char charAt(int index) {
        return charSequence.charAt(index);
    }

    @Override
    public int length() {
        return charSequence.length();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return charSequence.subSequence(start, end);
    }

    @Override
    public String toString() {
        return charSequence.toString();
    }
}
