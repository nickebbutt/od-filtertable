package com.od.filtertable.suffixtree;

import java.io.PrintWriter;

/**
 * User: nick
 * Date: 22/05/13
 * Time: 17:33
 */
public class LoggingVisitor<V> implements SuffixTreeVisitor<V> {

    private int indentLevel = 0;
    private PrintWriter writer;

    public LoggingVisitor(PrintWriter writer) {
        this.writer = writer;
    }
    
    @Override
    public boolean visit(SuffixTree<V> suffixTree) {
        indentLevel++;
        StringBuilder sb = new StringBuilder();
        addIndent(indentLevel, sb);
        sb.append(suffixTree.label);
        if ( suffixTree.values != null) {
            //sb.append("\n");
            //addIndent(indentLevel, sb);
            sb.append("\t vals: ");
            for (Object o : suffixTree.values) {
                sb.append(o.toString()).append(" ");
            }
        }
        sb.append("\n");
        writer.print(sb.toString());
        writer.flush();
        return true;
    }

    @Override
    public void visitComplete(SuffixTree<V> suffixTree) {
        indentLevel--;
    }


//    public void printStructure(int level, PrintWriter w) {
//        StringBuilder sb = new StringBuilder();
//        addIndent(level, sb);
//        sb.append(label);
//        if ( values != null) {
//            sb.append("\n");
//            addIndent(level, sb);
//            sb.append("val: ");
//            for (Object o : values) {
//                sb.append(o.toString()).append(" ");
//            }
//        }
//        sb.append(" -->\n");
//        w.print(sb.toString());
//        w.flush();
//        ChildNodeIterator<V> i = new ChildNodeIterator<V>(this);
//        while (i.isValid()) {
//            i.getCurrentNode().printStructure(level + 1, w);
//            i.next();
//        }
//    }
//
    private void addIndent(int level, StringBuilder sb) {
        for ( int loop=0; loop < level; loop++) {
            sb.append("  ");
        }
    }
}
