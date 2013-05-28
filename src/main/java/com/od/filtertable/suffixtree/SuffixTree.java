package com.od.filtertable.suffixtree;

import com.od.filtertable.index.MutableCharSequence;
import com.od.filtertable.index.MutableSequence;
import com.od.filtertable.suffixtree.visitor.CollectValuesVisitor;
import com.od.filtertable.suffixtree.visitor.SuffixTreeVisitor;

import java.util.Collection;

/**
 * User: nick
 * Date: 07/05/13
 * Time: 19:00
 */
public abstract class SuffixTree<V> {

    //the next node in the linked list of children for this node's parent
    SuffixTree<V> nextPeer;
    
    //use of this field is overloaded to save memory / 1 reference per node instance
    //this will either be the first child node of a linked list of children, or, for terminal nodes, a collection of values
    Object payload; 

    char[] label = CharUtils.EMPTY_CHAR_ARRAY;

    /**
     * Add value to the tree under the key s
     */
    public void add(CharSequence s, V value) {
        MutableCharSequence c = CharUtils.addTerminalCharAndCheck(s);
        addToTree(c, value, new ChildIteratorPool<V>());
    }

    private void addToTree(MutableCharSequence s, V value, ChildIteratorPool<V> iteratorPool) {
        if (s.length() == 0) {
            //since when adding initial s always ends with terminal char, this is a terminal node, add the value
            addValue(value);
        } else {
            //there are still chars to add, see if they match any existing children, if not insert a new child node
            ChildIterator<V> i = iteratorPool.getIterator(this);
            try {
                doAdd(s, value, i, iteratorPool);
            } finally {
                iteratorPool.returnIterator(i);
            }
        }      
    }

    private void doAdd(MutableCharSequence s, V value, ChildIterator<V> i, ChildIteratorPool<V> p) {
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

    private void addToChild(MutableCharSequence s, V value, ChildIterator<V> i, int matchingChars, ChildIteratorPool<V> iteratorPool) {
        s.incrementStart(matchingChars);        
        i.getCurrentNode().addToTree(s, value, iteratorPool);
        s.decrementStart(matchingChars);
    }

    private void split(ChildIterator<V> i, MutableCharSequence s, V value, int matchingChars) {
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
        replacementChild.payload = nodeToReplace.payload;

        SuffixTree<V> newChild = createNewSuffixTreeNode();
        newChild.label = labelForNewChild;
        newChild.addValue(value);

        boolean newChildFirst = CharUtils.compare(labelForNewChild, labelForReplacementChild) == -1;
        SuffixTree<V> firstChild = newChildFirst ? newChild : replacementChild;
        SuffixTree<V> secondChild = newChildFirst ? replacementChild : newChild;
        
        replacementNode.payload = firstChild;
        firstChild.nextPeer = secondChild;
    }

    private void insert(ChildIterator<V> i, MutableCharSequence s, V value) {
        SuffixTree<V> newNode = createNewSuffixTreeNode();
        newNode.label = CharUtils.createCharArray(s);
        newNode.addValue(value);
        i.insert(newNode);
    }

    private void addValue(V value) {
        if ( payload == null) {
            payload = getCollectionFactory().createTerminalNodeCollection();
        }
        ((Collection<V>)payload).add(value);
    }

    /**
     * Get into target collection values from all nodes prefixed with char sequence
     */
    public Collection<V> get(CharSequence c, Collection<V> targetCollection) {
        CollectValuesVisitor<V> collectValuesVisitor = new CollectValuesVisitor<V>(targetCollection);
        accept(new MutableSequence(c), collectValuesVisitor, new ChildIteratorPool<V>());
        return targetCollection;
    }
    
    /**
     * Get into target collection values from all nodes prefixed with char sequence, to a limit of maxResults values
     */
    public <R extends Collection<V>> R get(CharSequence c, R targetCollection, int maxResults) {
        CollectValuesVisitor<V> collectValuesVisitor = new CollectValuesVisitor<V>(targetCollection, maxResults);
        accept(new MutableSequence(c), collectValuesVisitor, new ChildIteratorPool<V>());
        return targetCollection;
    }

    /**
     * Visit all nodes which are prefixed with char sequence
     */
    public void accept(CharSequence c, SuffixTreeVisitor v) {
        accept(new MutableSequence(c), v, new ChildIteratorPool<V>());    
    }

    private void accept(MutableCharSequence s, SuffixTreeVisitor<V> visitor, ChildIteratorPool<V> iteratorPool) {
        ChildIterator<V> i = iteratorPool.getIterator(this);
        try {
            if ( s.length() == 0) {
                accept(visitor, iteratorPool);
            } else {
                boolean foundMatch = false;
                //get results from all nodes which share a prefix
                while(i.isValid()) {
                    int sharedCharCount = CharUtils.getSharedPrefixCount(s, i.getCurrentNode().label);
                    if ( sharedCharCount > 0 || s.length() == 0 /* all chars already matched, include */ ) {
                        s.incrementStart(sharedCharCount);
                        i.getCurrentNode().accept(s, visitor, iteratorPool);
                        s.decrementStart(sharedCharCount);
                        foundMatch = true;
                    } else if (foundMatch) {
                        break;
                        //since alphabetical, if we already found at least one match, and the next match fails
                        //we can assume all subsequent will fail
                    }
                    i.next();
                }
            }
        } finally {
            iteratorPool.returnIterator(i);
        }
    }

    /**
     * Visit all nodes
     */
    public boolean accept(SuffixTreeVisitor v) {
        return accept(v, new ChildIteratorPool<V>());
    }
    
    private boolean accept(SuffixTreeVisitor v, ChildIteratorPool<V> iteratorPool) {
        boolean shouldContinue = v.visit(this);
        ChildIterator<V> i = iteratorPool.getIterator(this);
        while(i.isValid() && shouldContinue) {
            shouldContinue = i.getCurrentNode().accept(v, iteratorPool);
            i.next();
        }
        v.visitComplete(this);
        return shouldContinue;
    }

    /**
     * Remove value v from key s, if s exists in the tree 
     */
    public void remove(CharSequence s, V value) {
        MutableCharSequence c = CharUtils.addTerminalCharAndCheck(s);
        removeFromTree(c, value, new ChildIteratorPool<V>());
    }

    private boolean removeFromTree(MutableCharSequence c, V value, ChildIteratorPool<V> childIteratorPool) {
        if ( c.length() == 0) {
            ((Collection<V>)payload).remove(value);
        } else {
            ChildIterator<V> i = childIteratorPool.getIterator(this);
            while(i.isValid()) {
                SuffixTree<V> current = i.getCurrentNode();
                int matchingChars = CharUtils.getSharedPrefixCount(c, current.label);
                if ( matchingChars == current.label.length) {
                    c.incrementStart(matchingChars);
                    boolean joinOrRemove = current.removeFromTree(c, value, childIteratorPool);
                    if ( joinOrRemove ) {
                        joinOrRemove(i, current);
                    }
                    break;
                }
                i.next();
            }
        }
        
        return (isTerminalNode() && ((Collection<V>)payload).size() == 0) || isOnlyOneChild();
    }

    private void joinOrRemove(ChildIterator<V> i, SuffixTree<V> current) {
        if ( current.isTerminalNode()) {
            i.removeCurrent();    
        } else {
            //the current node now has just a single child
            //we should replace it with a new node which combines the prefixes
            SuffixTree<V> joined = createNewSuffixTreeNode();
            char[] newLabel = CharUtils.join(current.label, ((SuffixTree)current.payload).label);
            joined.label = newLabel;
            joined.payload = ((SuffixTree)current.payload).payload;
            i.join(joined);        
        }
    }

    protected abstract SuffixTree<V> createNewSuffixTreeNode();

    protected abstract CollectionFactory<V> getCollectionFactory();

    public char[] getLabel() {
        return label;
    }

    public Collection<V> getValues() {
        return isTerminalNode() ? (Collection<V>)payload : null;
    }

    public boolean isTerminalNode() {
        return label.length > 0 /* root node */ && label[label.length - 1] == CharUtils.TERMINAL_CHAR;
    }

    public boolean isOnlyOneChild() {
        return ! isTerminalNode() && payload != null && ((SuffixTree<V>)payload).nextPeer == null;
    }
}
