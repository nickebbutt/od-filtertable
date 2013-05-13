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

    private static final char TERMINAL_CHAR = '$'; //TODO, revise choice

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

//    public static boolean isLowerValue(MutableCharSequence b, char[] c) {
//        int shared = Math.min(b.length(), c.length);
//        boolean result = false;
//        for ( int i = 0; i < shared; i++) {
//            if ( b.charAt(i) < c[i]) {
//                result = true; 
//                break;
//            }
//        }
//        
//        if ( ! result ) {
//            result = b.length() <= c.length;
//        }
//        return result;
//    }
}
