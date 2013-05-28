package com.od.filtertable.suffixtree;

/**
* User: nick
* Date: 16/05/13
* Time: 08:30
* 
* Iterate through the child nodes of a suffix tree node,
* also supporting insert/remove/replace operations
*/
public class ChildIterator<V> {

    private SuffixTree<V> parent;
    private SuffixTree<V> currentNode;
    private SuffixTree<V> lastNode;

    public ChildIterator(SuffixTree<V> parent) {
        setParent(parent);    
    }
    
    public ChildIterator() {}
    
    public void setParent(SuffixTree<V> parent) {
        this.parent = parent;
        currentNode = parent.isTerminalNode() ? null : (SuffixTree<V>)parent.payload;
        lastNode = null;
    }

    public boolean isValid() {
        return currentNode != null;
    }

    public void next() {
        lastNode = currentNode;
        currentNode = currentNode.nextPeer;
    }

    public SuffixTree<V> getCurrentNode() {
        return currentNode;
    }

    public void insert(SuffixTree<V> newNode) {
        if (lastNode != null)  {
            lastNode.nextPeer = newNode;
        } else {
            parent.payload = newNode; 
        }
        newNode.nextPeer = currentNode;
    }

    public void replace(SuffixTree<V> replacementNode) {
        if ( lastNode != null) {
            lastNode.nextPeer = replacementNode;
        } else {
            parent.payload = replacementNode;
        }
        replacementNode.nextPeer = currentNode.nextPeer;
        currentNode = replacementNode;
    }

    public void removeCurrent() {
        if ( lastNode != null) {
            lastNode.nextPeer = currentNode.nextPeer;
        } else {
            parent.payload = currentNode.nextPeer;
        }
    }
}
