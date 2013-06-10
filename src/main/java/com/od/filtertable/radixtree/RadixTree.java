package com.od.filtertable.radixtree;

import com.od.filtertable.index.MutableCharSequence;
import com.od.filtertable.index.MutableSequence;
import com.od.filtertable.radixtree.visitor.CollectValuesVisitor;
import com.od.filtertable.radixtree.visitor.TreeVisitor;

import java.util.Collection;

/**
 * User: nick
 * Date: 07/05/13
 * Time: 19:00
 */
public class RadixTree<V> implements CharSequence {
    
    //the next node in the linked list of children for this node's parent
    RadixTree<V> nextPeer;
    
    //use of this field is overloaded to save memory / 1 reference per node instance
    //this will either be the first child node of a linked list of children, or, for terminal nodes, a collection of values
    Object payload; 

    private CharSequence immutableSequence;
    private short start;
    private short end;

    public RadixTree() {}
    
    /**
     * Add value to the tree under the key s
     */
    public void add(MutableCharSequence c, V value, ValueSupplier<V> valueSupplier) {
        addToTree(c, value, ChildIteratorPool.<V>getIteratorPool(), valueSupplier);
    }

    private void addToTree(MutableCharSequence s, V value, ChildIteratorPool<V> iteratorPool, ValueSupplier<V> valueSupplier) {
        if (s.length() == 0) {
            //since when adding initial s always ends with terminal char, this is a terminal node, add the value
            addValue(value, valueSupplier);
        } else {
            //there are still chars to add, see if they match any existing children, if not insert a new child node
            ChildIterator<V> i = iteratorPool.getIterator(this);
            try {
                doAdd(s, value, i, iteratorPool, valueSupplier);
            } finally {
                iteratorPool.returnIterator(i);
            }
        }      
    }

    private void doAdd(MutableCharSequence s, V value, ChildIterator<V> i, ChildIteratorPool<V> p, ValueSupplier<V> valueSupplier) {
        boolean added = false;
        while (i.isValid()) {
            RadixTree<V> currentNode = i.getCurrentNode();
            int matchingChars = CharUtils.getSharedPrefixCount(s, currentNode);
            if ( matchingChars == s.length() /* must be a terminal node */
                || matchingChars == currentNode.getLabelLength()) {
                addToChild(s, value, i, matchingChars, p, valueSupplier);
                added = true;
                break;
            } else if ( matchingChars > 0) {  //only part of the current node label matched
                split(i, s, value, matchingChars, valueSupplier);
                added = true;
                break;              
            } else if ( CharUtils.compare(s, currentNode) == -1) {
                insert(i, s, value, valueSupplier);
                added = true;
                break;
            }
            i.next();
        }

        if (!added) {
            insert(i, s, value, valueSupplier);
        }
    }

    private void addToChild(MutableCharSequence s, V value, ChildIterator<V> i, int matchingChars, ChildIteratorPool<V> iteratorPool, ValueSupplier valueSupplier) {
        s.incrementStart(matchingChars);        
        i.getCurrentNode().addToTree(s, value, iteratorPool, valueSupplier);
        s.decrementStart(matchingChars);
    }

    private void split(ChildIterator<V> i, MutableCharSequence s, V value, int matchingChars, ValueSupplier<V> valueSupplier) {
        RadixTree<V> nodeToReplace = i.getCurrentNode();
        int labelLengthForNewChild = s.length() - matchingChars;

        RadixTree<V> replacementNode = new RadixTree<V>();
        replacementNode.setLabelFromNodePrefix(nodeToReplace, matchingChars);
        i.replace(replacementNode);

        RadixTree<V> replacementChild = new RadixTree<V>();
        replacementChild.setLabelFromNodeSuffix(nodeToReplace, matchingChars);
        replacementChild.payload = nodeToReplace.payload;

        RadixTree<V> newChild = new RadixTree<V>();
        newChild.setLabel(s.getImmutableBaseSequence(), s.getBaseSequenceEnd() - labelLengthForNewChild, s.getBaseSequenceEnd());
        newChild.addValue(value, valueSupplier);

        boolean newChildFirst = CharUtils.compare(newChild, replacementChild) == -1;
        RadixTree<V> firstChild = newChildFirst ? newChild : replacementChild;
        RadixTree<V> secondChild = newChildFirst ? replacementChild : newChild;
        
        replacementNode.payload = firstChild;
        firstChild.nextPeer = secondChild;
    }

    public void setLabel(CharSequence baseSequence, int start, int end) {
        this.immutableSequence = baseSequence;
        this.start = (short)start;
        this.end = (short)end;
    }

    private void insert(ChildIterator<V> i, MutableCharSequence s, V value, ValueSupplier<V> valueSupplier) {
        RadixTree<V> newNode = new RadixTree<V>();
        newNode.setLabel(s.getImmutableBaseSequence(), s.getBaseSequenceStart(), s.getBaseSequenceEnd());
        newNode.addValue(value, valueSupplier);
        i.insert(newNode);
    }

    private void addValue(V value, ValueSupplier<V> valueSupplier) {
        ValueSupplier.ValueSupplierResult r = valueSupplier.addValue(value, payload);
        payload = r.payload;
        r.clear();
    }

    /**
     * Get into target collection values from all nodes prefixed with char sequence
     */
    public Collection<V> get(CharSequence c, Collection<V> targetCollection, ValueSupplier<V> valueSupplier) {
        CollectValuesVisitor<V> collectValuesVisitor = new CollectValuesVisitor<V>(targetCollection, valueSupplier);
        accept(new MutableSequence(c), collectValuesVisitor, ChildIteratorPool.<V>getIteratorPool());
        return targetCollection;
    }
    
    /**
     * Get into target collection values from all nodes prefixed with char sequence, to a limit of maxResults values
     */
    public <R extends Collection<V>> R get(CharSequence c, R targetCollection, int maxResults, ValueSupplier<V> valueSupplier) {
        CollectValuesVisitor<V> collectValuesVisitor = new CollectValuesVisitor<V>(targetCollection, maxResults, valueSupplier);
        accept(new MutableSequence(c), collectValuesVisitor, ChildIteratorPool.<V>getIteratorPool());
        return targetCollection;
    }

    /**
     * Visit all nodes which are prefixed with char sequence
     */
    public void accept(MutableCharSequence c, TreeVisitor v) {
        accept(c, v, ChildIteratorPool.<V>getIteratorPool());    
    }

    private void accept(MutableCharSequence s, TreeVisitor<V> visitor, ChildIteratorPool<V> iteratorPool) {
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
    public boolean accept(TreeVisitor v) {
        return accept(v, ChildIteratorPool.<V>getIteratorPool());
    }
    
    private boolean accept(TreeVisitor v, ChildIteratorPool<V> iteratorPool) {
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
    public Object remove(MutableCharSequence s, V value, ValueSupplier<V> valueSupplier) {
        return removeFromTree(s, value, ChildIteratorPool.<V>getIteratorPool(), valueSupplier);
    }

    private Object removeFromTree(MutableCharSequence c, V value, ChildIteratorPool<V> childIteratorPool, ValueSupplier<V> valueSupplier) {
        Object result = null;
        if ( c.length() == 0 && payload != null) {
            ValueSupplier.ValueSupplierResult<V> r = valueSupplier.removeValue(value, payload);
            payload = r.payload;
            result = r.result;
            r.clear();
        } else {
            ChildIterator<V> i = childIteratorPool.getIterator(this);
            while(i.isValid()) {
                RadixTree<V> current = i.getCurrentNode();
                int matchingChars = CharUtils.getSharedPrefixCount(c, current);
                if ( matchingChars == current.getLabelLength()) {
                    c.incrementStart(matchingChars);
                    result = current.removeFromTree(c, value, childIteratorPool, valueSupplier);
                    if ( current.shouldBeCollapsed() ) {
                        joinOrRemove(i, current);
                    }
                    break;
                }
                i.next();
            }
        }
        return result;
    }
    
    protected boolean shouldBeCollapsed() {
        return (isTerminalNode() && payload == null) || isOnlyOneChild();    
    }

    private void joinOrRemove(ChildIterator<V> i, RadixTree<V> current) {
        if ( current.isTerminalNode()) {
            i.removeCurrent();    
        } else {
            //the current node now has just a single child
            //we should replace it with a new node which combines the prefixes
            RadixTree<V> firstChild = (RadixTree<V>)current.payload;
            RadixTree<V> joined = new RadixTree<V>();
            int newLength = current.getLabelLength() + firstChild.getLabelLength();
            joined.setLabel(firstChild.getRootSequence(), firstChild.getEnd() - newLength, firstChild.getEnd());
            joined.payload = firstChild.payload;
            i.replace(joined);        
        }
    }

    public Collection<V> getValues(Collection<V> targetCollection, ValueSupplier<V> valueSupplier) {
        if ( isTerminalNode() && payload != null ) { //should only ever be adding to a terminal node 
            valueSupplier.addValuesToCollection(targetCollection, payload);
        } 
        return targetCollection;
    }

    public boolean isTerminalNode() {
        return 
          end != 0 /* root */ && 
          immutableSequence.charAt(end - 1) == CharUtils.TERMINAL_CHAR; /* root node */
    }

    public boolean isOnlyOneChild() {
        return ! isTerminalNode() && payload != null && ((RadixTree<V>)payload).nextPeer == null;
    }
    
    public String getLabel() {
        return CharUtils.getString(immutableSequence, start, end);
    }

    private void setLabelFromNodeSuffix(RadixTree base, int trimFromStart) {
        this.immutableSequence = base.getRootSequence();
        this.start = (short)(base.getStart() + trimFromStart);
        this.end = base.getEnd();
    }

    private void setLabelFromNodePrefix(RadixTree base, int length) {
        this.immutableSequence = base.getRootSequence();
        this.start = base.getStart();
        this.end = (short)(base.getStart() + length);
    }
    
    public int getLabelLength() {
        return end - start;
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
