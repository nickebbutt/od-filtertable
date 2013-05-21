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
    
    @Step("a search for (.*) returns (.*)")
    public void doSearch(String key, String values) {
        StringTokenizer st = new StringTokenizer(values, ",");
        List<String> expected = new ArrayList<String>();
        while(st.hasMoreTokens()) {
            expected.add(st.nextToken().trim());
        }

        Collection<String> actual = suffixTree.get(key, new LinkedHashSet<String>());
        ArrayList<String> actualOrdered = new ArrayList<String>(actual);

        //suffixTree.printStructure(0, new PrintWriter(System.out));
        assertEquals("expected values in search results", expected, actualOrdered);
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
