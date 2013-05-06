package com.od.filtertable.index;

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.results.FeatureToken;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 06/05/13
 * Time: 22:41
 * To change this template use File | Settings | File Templates.
 */
@Handler("Simple Index")
public class SimpleIndexHandler extends StandardIndexHandler {

    @Step("I create a simple index")
    public void createIndex() {
        index = new SimpleIndex<String>(false, true);
    }

    @Step("a prefix search for (.*) returns in order (.*)")
    public void testPrefixSearch(String key, String vals) {
        StringTokenizer st = new StringTokenizer(vals, ",");
        List<String> expected = new ArrayList<String>();
        while(st.hasMoreTokens()) {
            expected.add(st.nextToken().trim());
        }

        Collection<String> actual = index.getValuesForPrefix(key, new LinkedHashSet<String>());
        ArrayList<String> actualOrdered = new ArrayList<String>(actual);
        
        assertEquals("expected values in search results", expected, actualOrdered);
    }

}
