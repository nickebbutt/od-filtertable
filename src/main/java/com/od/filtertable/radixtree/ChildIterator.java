package com.od.filtertable.radixtree;

/**
* User: nick
* Date: 16/05/13
* Time: 08:30
* 
* Iterate through the child nodes of a suffix tree node,
* also supporting insert/remove/replace operations
*/
public class ChildIterator<V> {

    private RadixTree<V> parent;
    private RadixTree<V> currentNode;
    private RadixTree<V> lastNode;

    public ChildIterator(RadixTree<V> parent) {
        setParent(parent);    
    }
    
    public ChildIterator() {}
    
    public void setParent(RadixTree<V> parent) {
        this.parent = parent;
        currentNode = parent.isTerminalNode() ? null : (RadixTree<V>)parent.payload;
        lastNode = null;
    }

    public boolean isValid() {
        return currentNode != null;
    }

    public void next() {
        lastNode = currentNode;
        currentNode = currentNode.nextPeer;
    }

    public RadixTree<V> getCurrentNode() {
        return currentNode;
    }

    public void insert(RadixTree<V> newNode) {
        if (lastNode != null)  {
            lastNode.nextPeer = newNode;
        } else {
            parent.payload = newNode; 
        }
        newNode.nextPeer = currentNode;
    }

    public void replace(RadixTree<V> replacementNode) {
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