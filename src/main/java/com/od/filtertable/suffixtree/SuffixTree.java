package com.od.filtertable.suffixtree;

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
        MutableCharSequence c = CharUtils.addTerminalCharAndCheck(s);
        add(c, value);
    }

    public void add(MutableCharSequence s, V value) {
        //strip our label from the sequence leaving any remaining chars
        s.incrementStart(label.length);
        
        ChildNodeIterator<V> i = new ChildNodeIterator<V>(this);
        boolean inserted = false;
        while (i.isValid()) {
            if ( i.getSharedChars(s) > 0) {
                //insert into current node
                inserted = true;
                break;
            } else if (i.shouldInsert(s) ) {
                SuffixTreeTerminalNode n = createNewTerminalNode(s, value);
                i.insert(n);
                inserted = true;
                break;
            }
            i.next();
        }
        
        if ( ! inserted ) {
            i.insert(createNewTerminalNode(s, value));   
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
        ChildNodeIterator<V> i = new ChildNodeIterator<V>(this);
        boolean foundMatch = false;
        
        //get results from all nodes which share a prefix
        while(i.isValid()) {
            int sharedCharCount = i.getSharedChars(s);
            if ( sharedCharCount > 0) {
                i.getCurrentNode().get(s, targetCollection);
                foundMatch = true;
            } else if (foundMatch) {
                break;
                //since alphabetical, if we already found at least one match, and the next match fails
                //we can assume all subsequent will fail
            }
            i.next();
        }
        return targetCollection;
    }

    public char[] getLabel() {
        return label;
    }

    protected abstract CollectionFactory<V> getCollectionFactory();
    
    
    public static class ChildNodeIterator<V> {

        private SuffixTree<V> suffixTree;
        private SuffixTreeNode<V> currentNode;
        private SuffixTreeNode<V> lastNode;
        
        public ChildNodeIterator(SuffixTree<V> suffixTree) {
            this.suffixTree = suffixTree;
            currentNode = suffixTree.firstChild;
        }
        
        public boolean isValid() {
            return currentNode != null;        
        }
        
        public void next() {
            lastNode = currentNode;
            currentNode = currentNode.getNextPeer();    
        }
        
        public SuffixTreeNode<V> getCurrentNode() {
            return currentNode;
        }
        
        public SuffixTreeNode<V> getLastNode() {
            return lastNode;
        }

        public int getSharedChars(MutableCharSequence s) {
            return CharUtils.getSharedPrefixCount(s, currentNode.getLabel());
        }

        public boolean shouldInsert(MutableCharSequence s) {
            return s.charAt(0) < currentNode.getLabel()[0];
        }

        public void insert(SuffixTreeNode<V> n) {
            n.setNextPeer(currentNode);
            if ( lastNode == null ) {
                suffixTree.firstChild = n;
            } else {
                lastNode.setNextPeer(n);
            }
        }
    }

}
