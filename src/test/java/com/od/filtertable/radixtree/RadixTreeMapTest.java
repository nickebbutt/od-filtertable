package com.od.filtertable.radixtree;

import com.od.filtertable.TestStringGenerator;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * User: nick
 * Date: 05/06/13
 * Time: 09:27
 */
public class RadixTreeMapTest extends TestCase {
    
    public void testPutPerformance() {

        TestStringGenerator testStringGenerator = new TestStringGenerator(100000);
        String[] vals = testStringGenerator.getTestStrings();
        
        long start = System.currentTimeMillis();
        
        RadixTreeMap<String> m = new RadixTreeMap<String>();
//        TreeMap<String, String> m = new TreeMap<String,String>();
        
        for ( String s : vals) {
            m.put(s, s);
        }
        
        System.out.println("time: " + (System.currentTimeMillis() - start));
        
        
        
    }
}
