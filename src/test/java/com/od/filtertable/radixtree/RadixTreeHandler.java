package com.od.filtertable.radixtree;

import com.od.filtertable.radixtree.map.RadixTreeMap;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 06/05/13
 * Time: 22:41
 * To change this template use File | Settings | File Templates.
 */
@Handler("Radix Tree Map")
public class RadixTreeHandler extends AbstractRadixTreeHandler {

    @Step("I create a radix tree")
    public void createIndex() {
        tree = new RadixTreeMap<String>();
    }

}
