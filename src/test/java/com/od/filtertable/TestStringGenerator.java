package com.od.filtertable;

/**
 * User: nick
 * Date: 05/06/13
 * Time: 09:24
 */
public class TestStringGenerator {

    private static final String allChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private String[] testStrings;

    public TestStringGenerator(int differentValueCount) {
        testStrings = new String[differentValueCount];
        testStrings[0] = "AbCdEfGhIjKlMnOpQrStUvWxYz";
        for ( int count = 1; count <= (differentValueCount - 1) ; count++) {
            testStrings[count] = generateTestString(count - 1);
        }
    }

    private String generateTestString(int lastString) {
        String s = testStrings[lastString];
        char randomChar = getRandomChar();
        char randomChar2 = getRandomChar();
        return s.replaceFirst(new String(new char[] {randomChar}), new String(new char[] {randomChar2}));
    }

    private char getRandomChar() {
        return allChars.charAt((int)(Math.random() * 51));
    }
    
    public String[] getTestStrings() {
        return testStrings;
    }
}
