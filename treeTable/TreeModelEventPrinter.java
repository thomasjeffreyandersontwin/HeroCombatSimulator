/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treeTable;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

/**
 *
 * @author twalker
 */
public class TreeModelEventPrinter implements TreeModelListener {

    public static final int NODES_CHANGED = 1;
    public static final int NODES_INSERTED = 2;
    public static final int NODES_REMOVED = 4;
    public static final int STRUCTURE_CHANGED = 8;

    private int mode;

    public TreeModelEventPrinter(int mode) {
        this.mode = mode;
    }

    public TreeModelEventPrinter() {
        this(15);
    }



    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        if ( (mode & NODES_CHANGED) != 0 ) {
            System.out.println("Nodes Changed: " + e);
        }
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        if ( (mode & NODES_INSERTED) != 0 ) {
            System.out.println("Nodes Inserted: " + e);
        }
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
        if ( (mode & NODES_REMOVED) != 0 ) {
            System.out.println("Nodes Removed: " + e);
        }
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        if ( (mode & STRUCTURE_CHANGED) != 0 ) {
            System.out.println("Structure Changed: " + e);
        }

    }

}
