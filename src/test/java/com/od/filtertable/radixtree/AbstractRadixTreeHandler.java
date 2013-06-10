package com.od.filtertable.radixtree;

import com.od.filtertable.radixtree.visitor.KeySetVisitor;
import com.od.filtertable.radixtree.visitor.LoggingVisitor;
import com.od.filtertable.radixtree.visitor.NodeCountVisitor;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

import java.io.PrintWriter;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 10/06/13
 * Time: 21:27
 * To change this template use File | Settings | File Templates.
 */
public class AbstractRadixTreeHandler extends ChorusAssert {
    
    protected RestrictedMap<String> tree;

    private String removedValue;

    @Step("I add a value (.*) under key (.*)")
    public void addAValue(String value, String key) {
        tree.put(key, value);
    }

    @Step("I remove the value under key (.*)")
    public void remove(String key) {
        removedValue = tree.remove(key);
    }

    @Step("the removed value was (.*)")
    public void checkRemovedValue(String key) {
        assertTrue((key.equals("null") && removedValue == null) || removedValue.equals(key));
    }

    @Step("I show the tree structure")
    public void showTheTreeStucture() {
        LoggingVisitor v = new LoggingVisitor(new PrintWriter(System.out), tree.getTreeConfig());
        tree.accept(v);
    }

    @Step("the radix tree contains keys (.*)")
    public void checkContainsKeys(String keys) {
        tree.compress();
        List<String> expected = getExpectedList(keys);

        KeySetVisitor<String> v = new KeySetVisitor<String>(tree.getTreeConfig());
        tree.accept(v);
        List<String> actual = v.getLabels();

        assertEquals("Expected " + keys, expected, actual);
    }

    @Step("the number of nodes is (\\d+)")
    public void countNodes(int number) {
        NodeCountVisitor n = new NodeCountVisitor();
        tree.accept(n);

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
                tree.addStartingWith(key, new LinkedHashSet<String>()) :
                tree.addStartingWith(key, new LinkedHashSet<String>(), maxItems);
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
