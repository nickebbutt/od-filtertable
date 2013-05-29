package com.od.filtertable.index;

/**
 * User: nick
 * Date: 10/05/13
 * Time: 09:18
 */
public interface MutableCharSequence extends CharSequence {
    
    int length();

    int getBaseSequenceLength();
    
    CharSequence getBaseSequence();

    char charAt(int index);

    void setStart(int start);
    
    int getStart();

    void incrementStart(int v);

    void decrementStart(int v);

    void setEnd(int end);
    
    int getEnd();

    CharSequence subSequence(int start, int end);
    
    char[] toArray(int start, int end);

}
