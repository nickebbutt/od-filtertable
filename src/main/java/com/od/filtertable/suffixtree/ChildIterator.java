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
        currentNode = parent.firstChild;
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
            parent.firstChild = newNode; 
        }
        newNode.nextPeer = currentNode;
    }

    public void replaceCurrent(SuffixTree<V> replacementNode) {
        if ( lastNode != null) {
            lastNode.nextPeer = replacementNode;
        } else {
            parent.firstChild = replacementNode;
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
            parent.firstChild = currentNode;
        }
    }

    public void removeCurrent() {
        if ( lastNode != null) {
            lastNode.nextPeer = currentNode.nextPeer;
        } else {
            parent.firstChild = currentNode.nextPeer;
        }
    }
}
