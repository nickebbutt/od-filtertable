package com.od.filtertable.radixtree;

import com.od.filtertable.radixtree.map.SuffixTreeMap;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 10/06/13
 * Time: 21:31
 * To change this template use File | Settings | File Templates.
 */
@Handler("Suffix Tree Map")
public class SuffixTreeHandler extends AbstractRadixTreeHandler {

    @Step("I create a suffix tree")
    public void createIndex() {
        tree = new SuffixTreeMap<String>();
    }
}
