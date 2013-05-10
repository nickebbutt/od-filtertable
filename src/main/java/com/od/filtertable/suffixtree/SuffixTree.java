package com.od.filtertable.suffixtree;

import com.od.filtertable.index.MutableCharSequence;

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
        super(new MutableCharSequence());
    }
    
    public SuffixTree(CharSequence s) {
        super(s);
    }

    public void add(CharSequence s, V value) {
        add(new MutableCharSequence(s), value);
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
                if ( getSharedPrefixCount(s, currentNode.getLabel()) > 0) {
                    //insert into current node
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
        return get(new MutableCharSequence(c), targetCollection);
    }
    
    public Collection<V> get(MutableCharSequence s, Collection<V> targetCollection) {
        //strip our label from the sequence leaving any remaining chars
        s.incrementStart(label.length); 
        
        SuffixTreeNode currentNode = firstChild;
        Collection<V> result = Collections.emptySet();
        while ( currentNode != null) {
            int sharedCharCount = getSharedPrefixCount(s, currentNode.getLabel());
            if ( sharedCharCount > 0) {
                result = currentNode.get(s, targetCollection);
                break;
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
