package com.od.filtertable.suffixtree;

/**
* User: nick
* Date: 16/05/13
* Time: 08:30
*/
public class ChildIterator<V> {

    private SuffixTree<V> parent;
    private SuffixTree<V> currentNode;
    private SuffixTree<V> lastNode;

    public ChildIterator(SuffixTree<V> parent) {
        this.parent = parent;
        currentNode = parent.isTerminalNode() ? null : (SuffixTree<V>)parent.payload;
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

    public void replaceCurrent(SuffixTree<V> replacementNode) {
        if ( lastNode != null) {
            lastNode.nextPeer = replacementNode;
        } else {
            parent.payload = replacementNode;
        }
        replacementNode.nextPeer = currentNode.nextPeer;
        currentNode = replacementNode;
    }

    public void join(SuffixTree<V> replacementChild) {
        replacementChild.nextPeer = currentNode.nextPeer;
        currentNode = replacementChild;
        if ( lastNode != null) {
            lastNode.nextPeer = currentNode;
        } else {
            parent.payload = currentNode;
        }
    }

    public void removeCurrent() {
        if ( lastNode != null) {
            lastNode.nextPeer = currentNode.nextPeer;
        } else {
            parent.payload = currentNode.nextPeer;
        }
    }
}
