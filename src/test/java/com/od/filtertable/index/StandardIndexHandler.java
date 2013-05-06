package com.od.filtertable.index;

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 06/05/13
 * Time: 20:07
 * To change this template use File | Settings | File Templates.
 */
@Handler("Standard Index")
public class StandardIndexHandler extends ChorusAssert {

    protected TrieIndex<String> index;

    @ChorusResource("feature.token")
    private FeatureToken featureToken;      //provides extra metadata about running feature
    
    @Step("I create an index")
    public void createIndex() {
        String c = featureToken.getConfigurationName();
        if ( "Simple".equals(c)) {
            index = new SimpleIndex<String>(false, true);    
        } else if ( "Dynamic".equals(c)) {
            index = new SimpleIndex<String>(false, true);
        } else {
            fail("Unknown configuration " + c);
        }
    }
    
    @Step("I add value (.*) under key (.*)")
    public void addAValue(String value, String key) {
        index.addOrUpdate(value.trim(), key);     
    }
    
    @Step("a prefix search for (.*) returns (.*)")
    public void testPrefixSearch(String key, String vals) {
        StringTokenizer st = new StringTokenizer(vals, ",");
        Set<String> expected = new HashSet<String>();
        while(st.hasMoreTokens()) {
            expected.add(st.nextToken().trim());   
        }

        Collection<String> actual = index.getValuesForPrefix(key, new LinkedHashSet<String>());
        assertEquals("expected values in search results", expected, actual);
    }
}
