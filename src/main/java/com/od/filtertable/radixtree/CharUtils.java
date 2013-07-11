package com.od.filtertable.radixtree;

import com.od.filtertable.radixtree.sequence.CharSequenceWithIntTerminator;

/**
 * User: nick
 * Date: 13/05/13
 * Time: 17:37
 */
public class CharUtils {
    
    public static int getSharedPrefixCount(CharSequenceWithIntTerminator s, CharSequenceWithIntTerminator s2) {
        int shared = 0;
        int maxLength = Math.min(s.length(), s2.length());
        for ( int c = 0; c < maxLength; c++) {
            if ( s.intAt(c) != s2.intAt(c) ) {
                break;
            }
            shared++;
        }
        return shared;
    }

    public static char[] createCharArray(CharSequence s) {
        char[] ch = new char[s.length()];
        for ( int c = 0; c < s.length(); c++) {
            ch[c] = s.charAt(c);    
        }
        return ch;
    }

    //the complexity here is that we want terminal chars to sort before other ascii chars
    //this is so that we retrieve results in the right order - 
    //so that values stored at AB$ are returned before values stored at ABA$ for example
    //since terminal char values are > the chosen ascii/unicode range we have to apply  *= -1 and sort as int
    public static int compareFirstChar(CharSequenceWithIntTerminator b, CharSequenceWithIntTerminator c) {
        int bchar = b.intAt(0);
        int cchar = c.intAt(0);
        int bint = bchar > Character.MAX_VALUE ? -bchar : bchar;
        int cint = cchar > Character.MAX_VALUE ? -cchar : cchar;
        return bint == cint ? 0 : bint < cint ? -1 : 1;
    }

    public static String getString(CharSequence immutableSequence, short start, short end) {
        StringBuilder sb = new StringBuilder();
        for ( int c = start; c < end; c ++) {
            sb.append(immutableSequence.charAt(c));    
        }
        return sb.toString();
    }
}
