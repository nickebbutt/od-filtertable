package com.od.filtertable.radixtree;

import com.od.filtertable.radixtree.sequence.CharSequenceWithIntTerminator;

/**
 * User: nick
 * Date: 10/05/13
 * Time: 09:18
 */
public interface MutableCharSequence extends CharSequenceWithIntTerminator {
    
    int length();

    char charAt(int index);

    void setStart(int start);

    void incrementStart(int v);

    void decrementStart(int v);

    void setEnd(int end);

    CharSequenceWithIntTerminator getImmutableBaseSequence();   
    
    int getBaseSequenceLength();        
    
    int getBaseSequenceStart();    
    
    int getBaseSequenceEnd();

    CharSequence subSequence(int start, int end);
    
}
