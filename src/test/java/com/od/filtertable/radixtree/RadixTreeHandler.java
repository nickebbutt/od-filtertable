package com.od.filtertable.radixtree;

import com.od.filtertable.radixtree.visitor.KeySetVisitor;
import com.od.filtertable.radixtree.visitor.LoggingVisitor;
import com.od.filtertable.radixtree.visitor.NodeCountVisitor;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

import java.io.PrintWriter;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 06/05/13
 * Time: 22:41
 * To change this template use File | Settings | File Templates.
 */
@Handler("Radix Tree Map")
public class RadixTreeHandler extends ChorusAssert {

    private RadixTreeMap<String> radixTree;

    @Step("I create a radix tree")
    public void createIndex() {
        radixTree = new RadixTreeMap<String>();
    }

    @Step("I add a value (.*) under key (.*)")
    public void addAValue(String value, String key) {
        radixTree.put(key, value);
    }
    
    @Step("I remove the value under key (.*)")
    public void remove(String key) {
        radixTree.remove(key);
    }
    
    @Step("I show the tree structure")
    public void showTheTreeStucture() {
        LoggingVisitor v = new LoggingVisitor(new PrintWriter(System.out), new SingleValueSupplier());
        radixTree.accept(v);
    }
    
    @Step("the radix tree contains keys (.*)")
    public void checkContainsKeys(String keys) {
        List<String> expected = getExpectedList(keys);
        
        KeySetVisitor<String> v = new KeySetVisitor<String>();
        radixTree.accept(v);
        List<String> actual = v.getLabels();
        
        assertEquals("Expected " + keys, expected, actual);
    }
    
    @Step("the number of nodes is (\\d+)") 
    public void countNodes(int number) {
        NodeCountVisitor n = new NodeCountVisitor();
        radixTree.accept(n);
        
        //-1 since we don't include the root node in the count for this test
        assertEquals("Expect " + number + " nodes", number, n.getNodeCount() - 1);
    }
    
    @Step("a search for (.*) returns (.*)")
    public void doSearch(String key, String values) {
        search(key, values, Integer.MAX_VALUE);
    }

    @Step("a search with maxItems=(\\d+) for (.*) returns (.*)")
    public void doSearch(int maxItems, String key, String values) {
        search(key, values, maxItems);
    }
    
    private void search(String key, String values, int maxItems) {
        List<String> expected = getExpectedList(values);

        Collection<String> actual = maxItems == Integer.MAX_VALUE ? 
            radixTree.getStartingWith(key, new LinkedHashSet<String>()) :
            radixTree.getStartingWith(key, new LinkedHashSet<String>(), maxItems);
        ArrayList<String> actualOrdered = new ArrayList<String>(actual);

        assertEquals("expected values in search results", expected, actualOrdered);
    }

    private List<String> getExpectedList(String values) {
        StringTokenizer st = new StringTokenizer(values, ",");
        List<String> expected = new ArrayList<String>();
        while(st.hasMoreTokens()) {
            expected.add(st.nextToken().trim());
        }
        return expected;
    }

}
