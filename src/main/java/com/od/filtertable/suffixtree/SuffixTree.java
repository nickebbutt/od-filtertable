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
public abstract class SuffixTree<V> implements CharSequence {

    //the next node in the linked list of children for this node's parent
    SuffixTree<V> nextPeer;
    
    //use of this field is overloaded to save memory / 1 reference per node instance
    //this will either be the first child node of a linked list of children, or, for terminal nodes, a collection of values
    Object payload; 

    CharSequence immutableSequence;
    short start;
    short end;
    
    /**
     * Add value to the tree under the key s
     */
    public void add(CharSequence s, V value) {
        MutableCharSequence c = CharUtils.addTerminalCharAndCheck(s);
        addToTree(c, value, ChildIteratorPool.<V>getIteratorPool());
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
            int matchingChars = CharUtils.getSharedPrefixCount(s, i.getCurrentNode());
            if (matchingChars == s.length() /* since s must end with terminal char, this  be a terminal node */ ) {
                addToChild(s, value, i, matchingChars, p);
                added = true;
                break;
            } else if ( matchingChars == i.getCurrentNode().getLabelLength() /*whole prefix matched */) {
                addToChild(s, value, i, matchingChars, p);
                added = true;
                break;
            } else if ( matchingChars > 0) {  //only part of the current node label matched
                split(i, s, value, matchingChars);
                added = true;
                break;              
            } else if ( CharUtils.compare(s, i.getCurrentNode()) == -1) {
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
        SuffixTree<V> nodeToReplace = i.getCurrentNode();
        int labelLengthForNewChild = s.length() - matchingChars;
       
        SuffixTree<V> replacementNode = createNewSuffixTreeNode();
        replacementNode.setLabelFromNodePrefix(nodeToReplace, matchingChars);
        i.replace(replacementNode);
        
        SuffixTree<V> replacementChild = createNewSuffixTreeNode();
        replacementChild.setLabelFromNodeSuffix(nodeToReplace, matchingChars);
        replacementChild.payload = nodeToReplace.payload;

        SuffixTree<V> newChild = createNewSuffixTreeNode();
        newChild.setLabel(s.getImmutableBaseSequence(), s.getBaseSequenceEnd() - labelLengthForNewChild, s.getBaseSequenceEnd());
        newChild.addValue(value);

        boolean newChildFirst = CharUtils.compare(newChild, replacementChild) == -1;
        SuffixTree<V> firstChild = newChildFirst ? newChild : replacementChild;
        SuffixTree<V> secondChild = newChildFirst ? replacementChild : newChild;
        
        replacementNode.payload = firstChild;
        firstChild.nextPeer = secondChild;
    }

    private void setLabel(CharSequence baseSequence, int start, int end) {
        this.immutableSequence = baseSequence;
        this.start = (short)start;
        this.end = (short)end;
    }

    private void insert(ChildIterator<V> i, MutableCharSequence s, V value) {
        SuffixTree<V> newNode = createNewSuffixTreeNode();
        newNode.setLabel(s.getImmutableBaseSequence(), s.getBaseSequenceStart(), s.getBaseSequenceEnd());
        newNode.addValue(value);
        i.insert(newNode);
    }

    private void addValue(V value) {
        payload = getValueSupplier().addValue(value, payload);
    }

    /**
     * Get into target collection values from all nodes prefixed with char sequence
     */
    public Collection<V> get(CharSequence c, Collection<V> targetCollection) {
        CollectValuesVisitor<V> collectValuesVisitor = new CollectValuesVisitor<V>(targetCollection);
        accept(new MutableSequence(c), collectValuesVisitor, ChildIteratorPool.<V>getIteratorPool());
        return targetCollection;
    }
    
    /**
     * Get into target collection values from all nodes prefixed with char sequence, to a limit of maxResults values
     */
    public <R extends Collection<V>> R get(CharSequence c, R targetCollection, int maxResults) {
        CollectValuesVisitor<V> collectValuesVisitor = new CollectValuesVisitor<V>(targetCollection, maxResults);
        accept(new MutableSequence(c), collectValuesVisitor, ChildIteratorPool.<V>getIteratorPool());
        return targetCollection;
    }

    /**
     * Visit all nodes which are prefixed with char sequence
     */
    public void accept(CharSequence c, SuffixTreeVisitor v) {
        accept(new MutableSequence(c), v, ChildIteratorPool.<V>getIteratorPool());    
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
                    int sharedCharCount = CharUtils.getSharedPrefixCount(s, i.getCurrentNode());
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
        return accept(v, ChildIteratorPool.<V>getIteratorPool());
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
        removeFromTree(c, value, ChildIteratorPool.<V>getIteratorPool());
    }

    private boolean removeFromTree(MutableCharSequence c, V value, ChildIteratorPool<V> childIteratorPool) {
        if ( c.length() == 0 && payload != null) {
            payload = getValueSupplier().removeValue(value, payload);
        } else {
            ChildIterator<V> i = childIteratorPool.getIterator(this);
            while(i.isValid()) {
                SuffixTree<V> current = i.getCurrentNode();
                int matchingChars = CharUtils.getSharedPrefixCount(c, current);
                if ( matchingChars == current.getLabelLength()) {
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
        
        return (isTerminalNode() && payload == null) || isOnlyOneChild();
    }

    private void joinOrRemove(ChildIterator<V> i, SuffixTree<V> current) {
        if ( current.isTerminalNode()) {
            i.removeCurrent();    
        } else {
            //the current node now has just a single child
            //we should replace it with a new node which combines the prefixes
            SuffixTree<V> joined = createNewSuffixTreeNode();
            SuffixTree<V> firstChild = (SuffixTree<V>)current.payload;
            int newLength = current.getLabelLength() + firstChild.getLabelLength();
            joined.setLabel(firstChild.getRootSequence(), firstChild.getEnd() - newLength, firstChild.getEnd());
            joined.payload = ((SuffixTree)current.payload).payload;
            i.replace(joined);        
        }
    }

    protected abstract SuffixTree<V> createNewSuffixTreeNode();

    protected abstract ValueSupplier<V> getValueSupplier();

    public Collection<V> getValues(Collection<V> targetCollection) {
        if ( isTerminalNode() && payload != null ) { //should only ever be adding to a terminal node 
            getValueSupplier().addValuesToCollection(targetCollection, payload);
        } 
        return targetCollection;
    }

    public boolean isTerminalNode() {
        return getLabelLength() > 0 /* root node */ && getLastChar() == CharUtils.TERMINAL_CHAR;
    }

    public boolean isOnlyOneChild() {
        return ! isTerminalNode() && payload != null && ((SuffixTree<V>)payload).nextPeer == null;
    }
    
    public CharSequence getLabel() {
        return new MutableSequence(immutableSequence, start, end);
    }

    private void setLabelFromNodeSuffix(SuffixTree base, int trimFromStart) {
        this.immutableSequence = base.getRootSequence();
        this.start = (short)(base.getStart() + trimFromStart);
        this.end = base.getEnd();
    }

    private void setLabelFromNodePrefix(SuffixTree base, int length) {
        this.immutableSequence = base.getRootSequence();
        this.start = base.getStart();
        this.end = (short)(base.getStart() + length);
    }
    
    public int getLabelLength() {
        return end - start;
    }
    
    public char getLastChar() {
        return immutableSequence.charAt(end - 1);
    }
    
    public CharSequence getRootSequence() {
        return immutableSequence;
    }
    
    public short getStart() {
        return start;
    }
    
    public short getEnd() {
        return end;
    }

    public int length() {
        return getLabelLength();
    }

    @Override
    public char charAt(int index) {
        return immutableSequence.charAt(start + index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        int newLength = end - start;
        return new MutableSequence(immutableSequence, this.start + start, this.start + start + newLength);
    }
}
