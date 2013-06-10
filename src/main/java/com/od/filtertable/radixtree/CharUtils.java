package com.od.filtertable.radixtree;

//import com.od.filtertable.index.MutableCharArraySequence;
import com.od.filtertable.index.MutableCharSequence;
import com.od.filtertable.index.MutableSequence;

/**
 * User: nick
 * Date: 13/05/13
 * Time: 17:37
 */
public class CharUtils {
    
    public static final char DEFAULT_TERMINAL_CHAR = '\u1000';

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

    public static void checkTerminalCharsInBody(CharSequence s, TreeConfig t) {
        for ( int c = 0; c < s.length() - 1 ; c++) {
            if ( t.isTerminalChar(s.charAt(c))) {
                throw new UnsupportedOperationException("Cannot add a char sequence in which the terminal character " 
                    + DEFAULT_TERMINAL_CHAR + " is not the last character");
            }
        }
    }

    private static char getLastChar(CharSequence s) {
        return s.charAt(s.length() - 1);
    }

    public static char[] createCharArray(CharSequence s) {
        char[] ch = new char[s.length()];
        for ( int c = 0; c < s.length(); c++) {
            ch[c] = s.charAt(c);    
        }
        return ch;
    }

    /**
     * Compare two sequences, excluding any terminal characters
     */
    public static int compare(CharSequence b, CharSequence c, TreeConfig t) {
        //exclude terminal chars
        int blength = b.length() > 0 && t.isTerminalChar(b.charAt(b.length() - 1)) ? b.length() - 1 : b.length();
        int clength = c.length() > 0 && t.isTerminalChar(c.charAt(c.length() - 1)) ? c.length() - 1 : c.length();

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

    public static String getString(CharSequence immutableSequence, short start, short end) {
        StringBuilder sb = new StringBuilder();
        for ( int c = start; c < end; c ++) {
            sb.append(immutableSequence.charAt(c));    
        }
        return sb.toString();
    }
}
