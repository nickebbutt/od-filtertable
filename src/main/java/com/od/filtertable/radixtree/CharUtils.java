package com.od.filtertable.radixtree;

import com.od.filtertable.index.CharSequenceWithIntTerminator;
import com.od.filtertable.index.MutableCharSequence;

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

    /**
     * Compare two sequences, excluding any terminal characters
     */
//    public static int compare(CharSequence b, CharSequence c, TreeConfig t) {
//        //exclude terminal chars
//        int blength = b.length() > 0 && t.isTerminalChar(b.charAt(b.length() - 1)) ? b.length() - 1 : b.length();
//        int clength = c.length() > 0 && t.isTerminalChar(c.charAt(c.length() - 1)) ? c.length() - 1 : c.length();
//
//        int shared = Math.min(blength, clength);
//        int result = 0;
//        for ( int i = 0; i < shared; i++) {
//            char bchar = b.charAt(i);
//            if ( bchar != c.charAt(i)) {
//                result = bchar < c.charAt(i) ? -1 : 1;
//                break;
//            }
//        }
//
//        //if all shared chars are equal the shorter sorts first
//        if ( result == 0 ) {
//            result = blength < clength ? -1 :
//                    blength == clength ? 0 : 1;
//        }
//        return result;
//    }

    //the complexity here is that we want terminal chars to sort before other ascii chars
    //this is so that we retrieve results in the right order - 
    //so that values stored at AB$ are returned before values stored at ABA$ for example
    //since terminal char values are > the chosen ascii/unicode range we have to apply  *= -1 and sort as int
    /*
    public static int compare(CharSequence b, CharSequence c, TreeConfig treeConfig) {

        int result = 0;
        int shared = Math.min(b.length(), c.length());

        for ( int i = 0; i < shared; i++) {
            char bchar = b.charAt(i);
            char cchar = c.charAt(i);
            int bint = treeConfig.isTerminalChar(bchar) ? -bchar : bchar;
            int cint = treeConfig.isTerminalChar(cchar) ? -cchar : cchar;
            result = bint == cint ? 0 : bint < cint ? -1 : 1;
            if ( result != 0) {
                break;
            }
        }

        //since we'd split child nodes around the shared prefix this comparison is not necessary?
        //if all shared chars are equal the shorter sorts first
        //if ( result == 0 ) {
        //  result = b.length() == c.length() ? 0 :  b.length() < c.length() ? -1 : 1;
        //}

        return result;
    }
    */

    //the complexity here is that we want terminal chars to sort before other ascii chars
    //this is so that we retrieve results in the right order - 
    //so that values stored at AB$ are returned before values stored at ABA$ for example
    //since terminal char values are > the chosen ascii/unicode range we have to apply  *= -1 and sort as int
    public static int compareFirstChar(CharSequenceWithIntTerminator b, CharSequenceWithIntTerminator c, TreeConfig treeConfig) {
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
