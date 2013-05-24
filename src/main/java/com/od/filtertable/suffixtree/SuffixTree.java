package com.od.filtertable.suffixtree;

import com.od.filtertable.index.MutableCharSequence;
import com.od.filtertable.index.MutableSequence;

import java.io.PrintWriter;
import java.util.Collection;

/**
 * User: nick
 * Date: 07/05/13
 * Time: 19:00
 */
public abstract class SuffixTree<V> {

    SuffixTree<V> firstChild;
    SuffixTree<V> nextPeer;
    Collection<V> values;

    char[] label = CharUtils.EMPTY_CHAR_ARRAY;

    public void add(CharSequence s, V value) {
        MutableCharSequence c = CharUtils.addTerminalCharAndCheck(s);
        addToTree(c, value, new ChildNodeIteratorPool<V>());
    }

    private void addToTree(MutableCharSequence s, V value, IteratorPool<V> iteratorPool) {
        if (s.length() == 0) {
            //since when adding initial s always ends with terminal char, this is a terminal node, add the value
            addValue(value);
        } else {
            //there are still chars to add, see if they match any existing children, if not insert a new child node
            ChildNodeIterator<V> i = iteratorPool.getIterator(this);
            try {
                doAdd(s, value, i, iteratorPool);
            } finally {
                iteratorPool.returnIterator(i);
            }
        }      
    }

    private void doAdd(MutableCharSequence s, V value, ChildNodeIterator<V> i, IteratorPool<V> p) {
        boolean added = false;
        while (i.isValid()) {
            int matchingChars = CharUtils.getSharedPrefixCount(s, i.getCurrentNode().label);
            if (matchingChars == s.length() /* since s must end with terminal char, this  be a terminal node */ ) {
                addToChild(s, value, i, matchingChars, p);
                added = true;
                break;
            } else if ( matchingChars == i.getCurrentNode().label.length /*whole prefix matched */) {
                addToChild(s, value, i, matchingChars, p);
                added = true;
                break;
            } else if ( matchingChars > 0) {  //only part of the current node label matched
                split(i, s, value, matchingChars);
                added = true;
                break;              
            } else if ( CharUtils.compare(s, i.getCurrentNode().label) == -1) {
                insert(i, s, value);
                added = true;
                break;
            }
            i.next();
        }

        if (!added) {
            insert(i, s, value);
        }
    }

    private void addToChild(MutableCharSequence s, V value, ChildNodeIterator<V> i, int matchingChars, IteratorPool<V> iteratorPool) {
        s.incrementStart(matchingChars);        
        i.getCurrentNode().addToTree(s, value, iteratorPool);
        s.decrementStart(matchingChars);
    }

    private void split(ChildNodeIterator<V> i, MutableCharSequence s, V value, int matchingChars) {
        char[] labelForReplacement = CharUtils.getPrefix(s, matchingChars);

        SuffixTree<V> nodeToReplace = i.getCurrentNode();
        int labelLengthForReplacementChild = nodeToReplace.label.length - matchingChars;
        char[] labelForReplacementChild = CharUtils.getSuffix(nodeToReplace.label, labelLengthForReplacementChild);
        
        int labelLengthForNewChild = s.length() - matchingChars;
        char[] labelForNewChild = CharUtils.getSuffix(s, labelLengthForNewChild);
        
        SuffixTree<V> replacementNode = createNewSuffixTreeNode();
        replacementNode.label = labelForReplacement;
        i.replaceCurrent(replacementNode);
        
        SuffixTree<V> replacementChild = createNewSuffixTreeNode();
        replacementChild.label = labelForReplacementChild;
        replacementChild.firstChild = nodeToReplace.firstChild;
        replacementChild.values = nodeToReplace.values;

        SuffixTree<V> newChild = createNewSuffixTreeNode();
        newChild.label = labelForNewChild;
        newChild.addValue(value);

        boolean newChildFirst = CharUtils.compare(labelForNewChild, labelForReplacementChild) == -1;
        SuffixTree<V> firstChild = newChildFirst ? newChild : replacementChild;
        SuffixTree<V> secondChild = newChildFirst ? replacementChild : newChild;
        
        replacementNode.firstChild = firstChild;
        firstChild.nextPeer = secondChild;
    }

    private void insert(ChildNodeIterator<V> i, MutableCharSequence s, V value) {
        SuffixTree<V> newNode = createNewSuffixTreeNode();
        newNode.label = CharUtils.createCharArray(s);
        newNode.addValue(value);
        i.insert(newNode);
    }

    private void addValue(V value) {
        if ( values == null) {
            values = getCollectionFactory().createTerminalNodeCollection();
        }
        values.add(value);
    }

    public Collection<V> get(CharSequence c, Collection<V> targetCollection) {
        return getFromTree(new MutableSequence(c), targetCollection, new ChildNodeIteratorPool<V>());
    }

    private Collection<V> getFromTree(MutableCharSequence s, Collection<V> targetCollection, IteratorPool<V> iteratorPool) {
        if ( isTerminalNode() ) {
            targetCollection.addAll(values);
        } else {
            ChildNodeIterator<V> i = iteratorPool.getIterator(this);
            try {
                doGet(s, targetCollection, i);
            } finally {
                iteratorPool.returnIterator(i);
            }
        }
        return targetCollection;
    }

    private void doGet(MutableCharSequence s, Collection<V> targetCollection, ChildNodeIterator<V> i) {
        boolean foundMatch = false;
        //get results from all nodes which share a prefix
        while(i.isValid()) {
            int sharedCharCount = i.getSharedChars(s);
            if ( sharedCharCount > 0 || s.length() == 0 /* all chars already matched, include */ ) {
                getValues(s, targetCollection, i, sharedCharCount);
                foundMatch = true;
            } else if (foundMatch) {
                break;
                //since alphabetical, if we already found at least one match, and the next match fails
                //we can assume all subsequent will fail
            }
            i.next();
        }
    }

    private void getValues(MutableCharSequence s, Collection<V> targetCollection, ChildNodeIterator<V> i, int sharedCharCount) {
        s.incrementStart(sharedCharCount);
        i.getCurrentNode().get(s, targetCollection);
        s.decrementStart(sharedCharCount);
    }

    public boolean accept(SuffixTreeVisitor v) {
        return accept(v, new ChildNodeIteratorPool<V>());
    }
    
    public boolean accept(SuffixTreeVisitor v, IteratorPool<V> iteratorPool) {
        boolean shouldContinue = v.visit(this);
        ChildNodeIterator<V> i = iteratorPool.getIterator(this);
        while(i.isValid() && shouldContinue) {
            shouldContinue = i.getCurrentNode().accept(v, iteratorPool);
            i.next();
        }
        v.visitComplete(this);
        return shouldContinue;
    }
    
    public void remove(CharSequence s, V value) {
        MutableCharSequence c = CharUtils.addTerminalCharAndCheck(s);
        removeFromTree(c, value, new ChildNodeIteratorPool<V>());
    }

    private boolean removeFromTree(MutableCharSequence c, V value, ChildNodeIteratorPool<V> childNodeIteratorPool) {
        if ( c.length() == 0) {
            values.remove(value);
        } else {
            ChildNodeIterator<V> i = childNodeIteratorPool.getIterator(this);
            while(i.isValid()) {
                SuffixTree<V> current = i.getCurrentNode();
                int matchingChars = CharUtils.getSharedPrefixCount(c, current.label);
                if ( matchingChars == current.label.length) {
                    c.incrementStart(matchingChars);
                    boolean shouldJoin = current.removeFromTree(c, value, childNodeIteratorPool);
                    if ( shouldJoin ) {
                        doJoin(i, current);
                    }
                    break;
                }
                i.next();
            }
        }
        
        return (isTerminalNode() && values.size() == 0) || isOnlyOneChild();
    }

    private void doJoin(ChildNodeIterator<V> i, SuffixTree<V> current) {
        if ( current.isTerminalNode()) {
            i.removeCurrent();    
        } else {
            SuffixTree<V> joined = createNewSuffixTreeNode();
            char[] newLabel = CharUtils.join(current.label, current.firstChild.label);
            joined.label = newLabel;
            joined.firstChild = current.firstChild.firstChild;
            joined.values = current.firstChild.values;
            i.join(joined);        
        }
    }

    protected abstract SuffixTree<V> createNewSuffixTreeNode();

    protected abstract CollectionFactory<V> getCollectionFactory();

    public boolean isTerminalNode() {
        return label.length > 0 /* root node */ && label[label.length - 1] == CharUtils.TERMINAL_CHAR;
    }

    public boolean isOnlyOneChild() {
        return firstChild != null && firstChild.nextPeer == null;
    }
}
