package com.od.filtertable.suffixtree;

import com.od.filtertable.suffixtree.SuffixTree;
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
@Handler("Suffix Tree")
public class SuffixTreeHandler extends ChorusAssert {

    private SuffixTree<String> suffixTree;

    @Step("I create a suffix tree")
    public void createIndex() {
        suffixTree = new StringSuffixTree();
    }

    @Step("I add a value (.*) under key (.*)")
    public void addAValue(String value, String key) {
        suffixTree.add(key, value);
    }
    
    @Step("I show the tree structure")
    public void showTheTreeStucture() {
        LoggingVisitor v = new LoggingVisitor(new PrintWriter(System.out));
        suffixTree.accept(v);
    }
    
    @Step("the suffix tree contains keys (.*)")
    public void checkContainsKeys(String keys) {
        List<String> expected = getExpectedList(keys);
        
        KeySetVisitor<String> v = new KeySetVisitor<String>();
        suffixTree.accept(v);
        List<String> actual = v.getLabels();
        
        assertEquals("Expected " + keys, expected, actual);
    }
    
    @Step("the number of nodes is (\\d+)") 
    public void countNodes(int number) {
        NodeCountVisitor n = new NodeCountVisitor();
        suffixTree.accept(n);
        assertEquals("Expect " + number + " nodes", number, n.getNodeCount());
    }
    
    @Step("a search for (.*) returns (.*)")
    public void doSearch(String key, String values) {
        List<String> expected = getExpectedList(values);

        Collection<String> actual = suffixTree.get(key, new LinkedHashSet<String>());
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

    private static class StringSuffixTree extends SuffixTree<String> {

        @Override
        protected SuffixTree createNewSuffixTreeNode() {
            return new StringSuffixTree();
        }

        @Override
        protected CollectionFactory<String> getCollectionFactory() {
            return new CollectionFactory<String>() {
                @Override
                public Collection<String> createTerminalNodeCollection() {
                    return new HashSet<String>();
                }
            };
        }
    }
}