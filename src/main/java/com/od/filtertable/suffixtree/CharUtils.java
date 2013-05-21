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
            result = new MutableSequence(append(s, new char[]{TERMINAL_CHAR}));        
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

    public static char[] getPrefix(MutableCharSequence s, int length) {
        char[] result = new char[length];
        for ( int c = 0; c < length; c++) {
            result[c] = s.charAt(c);
        }
        return result;
    }

    public static char[] getSuffix(MutableCharSequence s, int length) {
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

    public static boolean isLowerValue(char[] b, char[] c) {
        //do not compare terminal char
        int blength = b.length > 0 && b[b.length - 1] == TERMINAL_CHAR ? b.length - 1 : b.length;
        int clength = c.length > 0 && c[c.length - 1] == TERMINAL_CHAR ? c.length - 1 : c.length;
        int shared = Math.min(blength, clength);
        boolean result = false;
        for ( int i = 0; i < shared; i++) {
            if ( b[i] < c[i]) {
                result = true; 
                break;
            }
        }
        
        if ( ! result ) {
            result = blength <= clength;
        }
        return result;
    }
}
