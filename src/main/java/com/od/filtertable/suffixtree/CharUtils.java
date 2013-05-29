package com.od.filtertable.suffixtree;

import com.od.filtertable.index.MutableCharArraySequence;
import com.od.filtertable.index.MutableCharSequence;
import com.od.filtertable.index.MutableSequence;

/**
 * User: nick
 * Date: 13/05/13
 * Time: 17:37
 */
public class CharUtils {

    /**
     * Reuse a thread local instance since many tens of thousands of these are otherwise created when indexing a large dataset
     */
    private static ThreadLocal<CharSequenceWithTerminalNode> charSequenceWithTerminalNodeThreadLocal = 
        new ThreadLocal<CharSequenceWithTerminalNode>() {
            public CharSequenceWithTerminalNode initialValue() {
                return new CharSequenceWithTerminalNode();
            }
    };
    
    public static final char[] EMPTY_CHAR_ARRAY = new char[0];
    
    public static final char TERMINAL_CHAR = '$';

    public static int getSharedPrefixCount(CharSequence s, char[] label) {
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

    public static int getSharedPrefixCount(CharSequence s, CharSequence s2) {
        int shared = 0;
        int maxLength = Math.min(s.length(), s2.length());
        for ( int c = 0; c < maxLength; c++) {
            if ( s.charAt(c) != s2.charAt(c) ) {
                break;
            }
            shared++;
        }
        return shared;
    }

    public static MutableCharSequence append(CharSequence s, char[] chars) {
        char[] result = new char[s.length() + chars.length];
        for ( int c = 0; c < s.length(); c++) {
            result[c] = s.charAt(c);
        }
        for (int c = 0; c < chars.length; c++) {
            result[c + s.length()] = chars[c];
        }
        return new MutableCharArraySequence(result);
    }

    public static MutableCharSequence addTerminalCharAndCheck(CharSequence s) {
        MutableCharSequence result;
        if ( getLastChar(s) != TERMINAL_CHAR) {
            CharSequenceWithTerminalNode n = charSequenceWithTerminalNodeThreadLocal.get();
            n.setSequence(s);
            result = n;     
        } else {
            result = new MutableSequence(s);
        }
        checkTerminalCharsInBody(s);
        return result;
    }

    public static void checkTerminalCharsInBody(CharSequence s) {
        for ( int c = 0; c < s.length() - 1 ; c++) {
            if ( s.charAt(c) == TERMINAL_CHAR) {
                throw new UnsupportedOperationException("Cannot add a char sequence in which the terminal character " 
                    + TERMINAL_CHAR + " is not the last character");
            }
        }
    }

    private static char getLastChar(CharSequence s) {
        return s.charAt(s.length() - 1);
    }

    public static char[] getPrefix(CharSequence s, int length) {
        char[] result = new char[length];
        for ( int c = 0; c < length; c++) {
            result[c] = s.charAt(c);
        }
        return result;
    }

    public static char[] getSuffix(CharSequence s, int length) {
        char[] result = new char[length];
        int start = s.length() - length;
        for ( int c = 0; c < length; c++) {
            result[c] = s.charAt(start + c);
        }
        return result;
    }

    public static char[] getSuffix(char[] s, int length) {
        char[] result = new char[length];
        int start = s.length - length;
        for ( int c = 0; c < length; c++) {
            result[c] = s[start + c];
        }
        return result;
    }

    public static char[] createCharArray(CharSequence s) {
        char[] ch = new char[s.length()];
        for ( int c = 0; c < s.length(); c++) {
            ch[c] = s.charAt(c);    
        }
        return ch;
    }
    
    public static char[] join(char[] a, char[] b) {
        char[] result = new char[a.length + b.length];
        for ( int c = 0; c < a.length; c++) {
            result[c] = a[c];
        }

        for ( int c = 0; c < b.length; c++) {
            result[a.length + c] = b[c];
        }
        return result;
    }

    public static int compare(CharSequence b, CharSequence c) {
        //exclude terminal chars
        int blength = b.length() > 0 && b.charAt(b.length() - 1) == TERMINAL_CHAR ? b.length() - 1 : b.length();
        int clength = c.length() > 0 && c.charAt(c.length() - 1) == TERMINAL_CHAR ? c.length() - 1 : c.length();

        int shared = Math.min(blength, clength);
        int result = 0;
        for ( int i = 0; i < shared; i++) {
            char bchar = b.charAt(i);
            if ( bchar != c.charAt(i)) {
                result = bchar < c.charAt(i) ? -1 : 1;
                break;
            }
        }

        //if all shared chars are equal the shorter sorts first
        if ( result == 0 ) {
            result = blength < clength ? -1 :
                    blength == clength ? 0 : 1;
        }
        return result;
    }
    
    /**
     * Compare a CharSequence to a char[]
     * Duplicating the char[] comparison logic since aiming for near-zero object creation on critical path
     */
    public static int compare(CharSequence b, char[] c) {
        //exclude terminal chars
        int blength = b.length() > 0 && b.charAt(b.length() - 1) == TERMINAL_CHAR ? b.length() - 1 : b.length();
        int clength = c.length > 0 && c[c.length - 1] == TERMINAL_CHAR ? c.length - 1 : c.length;

        int shared = Math.min(blength, clength);
        int result = 0;
        for ( int i = 0; i < shared; i++) {
            char bchar = b.charAt(i);
            if ( bchar != c[i]) {
                result = bchar < c[i] ? -1 : 1;
                break;
            }
        }

        //if all shared chars are equal the shorter sorts first
        if ( result == 0 ) {
            result = blength < clength ? -1 :
                    blength == clength ? 0 : 1;
        }
        return result;    
    }

    /**
     * Compare a char[] to a char[]
     */
    public static int compare(char[] b, char[] c) {
        //exclude terminal chars
        int blength = b.length > 0 && b[b.length - 1] == TERMINAL_CHAR ? b.length - 1 : b.length;
        int clength = c.length > 0 && c[c.length - 1] == TERMINAL_CHAR ? c.length - 1 : c.length;
        
        int shared = Math.min(blength, clength);
        int result = 0;
        for ( int i = 0; i < shared; i++) {
            if ( b[i] != c[i]) {
                result = b[i] < c[i] ? -1 : 1; 
                break;
            }
        }
        
        //if all shared chars are equal the shorter sorts first
        if ( result == 0 ) {
            result = blength < clength ? -1 :
                blength == clength ? 0 : 1;
        }
        return result;
    }
}
