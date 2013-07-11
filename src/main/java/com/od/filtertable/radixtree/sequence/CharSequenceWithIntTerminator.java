package com.od.filtertable.radixtree.sequence;

/**
 * User: nick
 * Date: 19/06/13
 * Time: 18:17
 * 
 * Extend CharSequence to return chars as int values
 * 
 * This allows us to use an integer as the terminator for a CharSequence, which is helpful since
 * Generalized Suffix Tree require a different terminator per sequence, and if limited to the char range
 * this would impose an arbitrary limit on the range of of non-terminal characters supported and the number
 * of values which could be added.
 * 
 * The expectation is that all terminal values will be an integer > Character.MAX_VALUE
 */
public interface CharSequenceWithIntTerminator extends CharSequence
{
    /**
     * the char representation for an int terminator node
     */
    char TERMINATOR_CHAR_REPRESENTATION = Character.MAX_VALUE;

    int intAt(int index);
}
