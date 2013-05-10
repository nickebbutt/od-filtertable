package com.od.filtertable.suffixtree;

/**
 * User: nick
 * Date: 09/05/13
 * Time: 18:00
 */
public abstract class AbstractSuffixTreeNode<V> implements SuffixTreeNode<V> {
    
    protected final char[] label;

    private SuffixTreeNode<V> nextPeer;

    public AbstractSuffixTreeNode(CharSequence s) {
        label = createCharArray(s);
    }
    
    public char[] getLabel() {
        return label;
    }   
    
    protected char[] createCharArray(CharSequence s) {
        char[] ch = new char[s.length()];
        for ( int c = 0; c < s.length(); c++) {
            ch[c] = s.charAt(c);    
        }
        return ch;
    }

    protected int getSharedPrefixCount(CharSequence s, char[] label) {
        int shared = 0;
        int maxLength = Math.min(s.length(), label.length);
        for ( int c = 0; c < maxLength; c++) {
            if ( s.charAt(c) != label[c] ) {
                break;
            }
            shared++;
        }
        return shared;
    }
    
    @Override
    public void setNextPeer(SuffixTreeNode<V> node) {
        this.nextPeer = node;
    }
    
    @Override
    public SuffixTreeNode<V> getNextPeer() {
        return nextPeer;
    }
}
