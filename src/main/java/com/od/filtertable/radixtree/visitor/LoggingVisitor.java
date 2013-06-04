package com.od.filtertable.radixtree.visitor;

import com.od.filtertable.radixtree.RadixTree;
import com.od.filtertable.radixtree.ValueSupplier;

import java.io.PrintWriter;
import java.util.LinkedList;

/**
 * User: nick
 * Date: 22/05/13
 * Time: 17:33
 */
public class LoggingVisitor<V> implements TreeVisitor<V> {

    private int indentLevel = 0;
    private PrintWriter writer;
    private ValueSupplier<V> valueSupplier;
    private LinkedList<V> values = new LinkedList<V>();

    public LoggingVisitor(PrintWriter writer, ValueSupplier<V> valueSupplier) {
        this.writer = writer;
        this.valueSupplier = valueSupplier;
    }
    
    @Override
    public boolean visit(RadixTree<V> radixTree) {
        indentLevel++;
        StringBuilder sb = new StringBuilder();
        addIndent(indentLevel, sb);
        sb.append(radixTree.getLabel());
        values.clear();
        if ( radixTree.getValues(values, valueSupplier).size() > 0) {
            sb.append("\t vals: ");
            for (Object o : values) {
                sb.append(o.toString()).append(" ");
            }
        }
        sb.append("\n");
        writer.print(sb.toString());
        writer.flush();
        return true;
    }

    @Override
    public void visitComplete(RadixTree<V> radixTree) {
        indentLevel--;
    }

    private void addIndent(int level, StringBuilder sb) {
        for ( int loop=0; loop < level; loop++) {
            sb.append("  ");
        }
    }
}
