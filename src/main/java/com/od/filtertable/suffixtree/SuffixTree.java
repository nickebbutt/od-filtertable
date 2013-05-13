package com.od.filtertable.suffixtree;

import com.od.filtertable.index.MutableCharArraySequence;
import com.od.filtertable.index.MutableCharSequence;
import com.od.filtertable.index.MutableSequence;

import java.util.Collection;
import java.util.Collections;

/**
 * User: nick
 * Date: 07/05/13
 * Time: 19:00
 */
public abstract class SuffixTree<V> extends AbstractSuffixTreeNode<V> {

    private SuffixTreeNode<V> firstChild;

    /**
     * Create a root node
     */
    public SuffixTree() {
        super(new MutableSequence());
    }
    
    public SuffixTree(CharSequence s) {
        super(s);
    }

    public void add(CharSequence s, V value) {
        MutableCharSequence c = addTerminalCharAndCheck(s);
        add(c, value);
    }

    public void add(MutableCharSequence s, V value) {
        //strip our label from the sequence leaving any remaining chars
        s.incrementStart(label.length);
        
        if ( firstChild == null ) {
            //there are no children, create a terminal path
            firstChild = createNewTerminalNode(s, value);
        } else {
            SuffixTreeNode<V> currentNode = firstChild;
            SuffixTreeNode<V> lastNode = null;
            boolean inserted = false;
            while (currentNode != null) {
                char[] currentNodeLabel = currentNode.getLabel();
                if ( getSharedPrefixCount(s, currentNodeLabel) > 0) {
                    //insert into current node
                    inserted = true;
                    break;
                } else if (s.charAt(0) < currentNodeLabel[0]) {
                    SuffixTreeTerminalNode n = createNewTerminalNode(s, value);
                    n.setNextPeer(currentNode);
                    if ( lastNode == null ) {
                        firstChild = n;    
                    } else {
                        lastNode.setNextPeer(n);
                    }
                    inserted = true;
                    break;
                }
                lastNode = currentNode;
                currentNode = currentNode.getNextPeer();
            }
            
            if ( ! inserted ) {
                lastNode.setNextPeer(createNewTerminalNode(s, value));   
            }
        }
    }

    private SuffixTreeTerminalNode<V> createNewTerminalNode(MutableCharSequence s, V value) {
        SuffixTreeTerminalNode<V> n = new SuffixTreeTerminalNode<V>(s);
        n.addValue(getCollectionFactory(), value);
        return n;
    }

    public Collection<V> get(CharSequence c, Collection<V> targetCollection) {
        return get(new MutableSequence(c), targetCollection);
    }
    
    public Collection<V> get(MutableCharSequence s, Collection<V> targetCollection) {
        //strip our label from the sequence leaving any remaining chars
        s.incrementStart(label.length); 
        
        SuffixTreeNode currentNode = firstChild;
        Collection<V> result = Collections.emptySet();
        boolean foundMatch = false;
        while ( currentNode != null) {
            int sharedCharCount = getSharedPrefixCount(s, currentNode.getLabel());
            if ( sharedCharCount > 0) {
                result = currentNode.get(s, targetCollection);
                foundMatch = true;
            } else if ( foundMatch ) {
                break; 
                //since alphabetical, if we already found at least one match, and the next match fails
                //we can assume all subsequent will fail
            }
            currentNode = currentNode.getNextPeer();
        }
        return result;
    }

    public char[] getLabel() {
        return label;
    }

    protected abstract CollectionFactory<V> getCollectionFactory();

}
