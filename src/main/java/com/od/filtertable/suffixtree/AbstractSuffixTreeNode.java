package com.od.filtertable.suffixtree;

import com.od.filtertable.index.MutableCharArraySequence;
import com.od.filtertable.index.MutableCharSequence;
import com.od.filtertable.index.MutableSequence;

/**
 * User: nick
 * Date: 09/05/13
 * Time: 18:00
 */
public abstract class AbstractSuffixTreeNode<V> implements SuffixTreeNode<V> {

    private static final char TERMINAL_CHAR = '$'; //TODO, revise choice

    protected final char[] label;

    private SuffixTreeNode<V> nextPeer;

    public AbstractSuffixTreeNode(CharSequence s) {
        label = createCharArray(s);
    }
    
    public char[] getLabel() {
        return label;
    }   
    
    protected char[] createCharArray(CharSequence s) {
        char[] ch = new char[s.length()];
        for ( int c = 0; c < s.length(); c++) {
            ch[c] = s.charAt(c);    
        }
        return ch;
    }

    protected int getSharedPrefixCount(CharSequence s, char[] label) {
        int shared = 0;
        int maxLength = Math.min(s.length(), label.length);
        for ( int c = 0; c < maxLength; c++) {
            if ( s.charAt(c) != label[c] ) {
                break;
            }
            shared++;
        }
        return shared;
    }
    
    @Override
    public void setNextPeer(SuffixTreeNode<V> node) {
        this.nextPeer = node;
    }
    
    @Override
    public SuffixTreeNode<V> getNextPeer() {
        return nextPeer;
    }

    protected MutableCharSequence append(CharSequence s, char[] chars) {
        char[] result = new char[s.length() + chars.length];
        for ( int c = 0; c < s.length(); c++) {
            result[c] = s.charAt(c);
        }
        
        for (int c = 0; c < chars.length; c++) {
            result[c + s.length()] = chars[c];
        }
        return new MutableCharArraySequence(result);
    }

    protected MutableCharSequence addTerminalCharAndCheck(CharSequence s) {
        MutableCharSequence result;
        if ( getLastChar(s) != TERMINAL_CHAR) {
            result = new MutableSequence(append(s, new char[] { TERMINAL_CHAR }));        
        } else {
            result = new MutableSequence(s);
        }
        checkTerminalCharsInBody(s);
        return result;
    }

    private void checkTerminalCharsInBody(CharSequence s) {
        for ( int c = 0; c < s.length() - 1 ; c++) {
            if ( s.charAt(c) == TERMINAL_CHAR) {
                throw new UnsupportedOperationException("Cannot add a char sequence in which the terminal character " 
                    + TERMINAL_CHAR + " is not the last character");
            }
        }
    }

    private char getLastChar(CharSequence s) {
        return s.charAt(s.length() - 1);
    }

    protected boolean isLowerValue(MutableCharSequence b, char[] c) {
        int shared = Math.min(b.length(), c.length);
        boolean result = false;
        for ( int i = 0; i < shared; i++) {
            if ( b.charAt(i) < c[i]) {
                result = true; 
                break;
            }
        }
        
        if ( ! result ) {
            result = b.length() <= c.length;
        }
        return result;
    }
}
