package com.od.filtertable.index;

/**
 * User: nick
 * Date: 10/05/13
 * Time: 09:18
 */
public interface MutableCharSequence extends CharSequence {
    
    int length();

    int totalSequenceLength();

    char charAt(int index);

    void setStart(int start);

    void incrementStart(int v);

    void setEnd(int end);

    CharSequence subSequence(int start, int end);
}
