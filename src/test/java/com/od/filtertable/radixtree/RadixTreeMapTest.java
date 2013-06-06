package com.od.filtertable.radixtree;

import com.od.filtertable.TestStringGenerator;
import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * User: nick
 * Date: 05/06/13
 * Time: 09:27
 */
public class RadixTreeMapTest extends TestCase {

    RadixTreeMap<Object> m = new RadixTreeMap<Object>();
//    TreeMap<String, Object> m = new TreeMap<String,Object>();
    
    public static void main(String[] args) throws Exception {
        new RadixTreeMapTest();    
    }
    
    public RadixTreeMapTest() throws Exception {
        populateMap();
        m.compress();
        Thread.sleep(100000);
        System.out.println(System.identityHashCode(m));    
    }

    private void populateMap() throws IOException {
        String[] vals = readLines("src/test/resources/sowpods.txt");
        //        TreeMap<String, String> m = new TreeMap<String,String>();

        Object o = new Object();
        
        for ( String s : vals) {
            m.put(s, o);
        }
    }

    private String[] getStrings() {
        TestStringGenerator testStringGenerator = new TestStringGenerator(100000);
        return testStringGenerator.getTestStrings();
    }
    
    public String[] readLines(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines.toArray(new String[lines.size()]);
    }
}
