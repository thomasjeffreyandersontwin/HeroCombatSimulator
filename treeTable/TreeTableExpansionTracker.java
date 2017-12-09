/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treeTable;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.tree.TreePath;

/**
 *
 * @author twalker
 */
public class TreeTableExpansionTracker {

    protected Set<TreePath> expandedNodeSet = new LinkedHashSet<TreePath>();

    protected boolean recorded = false;

    protected TreeTable treeTable;

    public TreeTableExpansionTracker(TreeTable treeTable) {
        this.treeTable = treeTable;
    }

    public boolean recordExpansionState() {

        boolean wasRecorded = recorded;

        if (recorded == false) {
            recorded = true;

            TreeTableModel model = treeTable.getProxyTreeTableModel();

            recordNodeExpansionState(model, new TreePath(model.getRoot()));
        }

        return wasRecorded;
    }

    private void recordNodeExpansionState(TreeTableModel model, TreePath path) {
        if (treeTable.isExpanded(path)) {
            expandedNodeSet.add(path);

            for (int i = 0; i < model.getChildCount(path.getLastPathComponent()); i++) {
                recordNodeExpansionState(model, path.pathByAddingChild(model.getChild(path.getLastPathComponent(), i)));
            }
        }
    }

    public void restoreExpansionState(boolean clearExpansionState) {
        if (recorded == true) {

            System.out.println("");
            System.out.println("restoreExpansionState(" + clearExpansionState + ")");

            TreeTableModel model = treeTable.getProxyTreeTableModel();

            for (TreePath path : expandedNodeSet) {

                if ( isPathInModel(model, path) ) {
                    treeTable.expandPath(path);
                 //   System.out.println("Expanding Path " + path);
                }
                else {
                 //   System.out.println("NOT Expanding Path " + path);
                }
            }

            if (clearExpansionState) {
                clearExpansionState();
            }
        }
    }

    private boolean isPathInModel(TreeTableModel model, TreePath path) {
        //TreePath parent = path.getParentPath();

        if (path.getPathCount() == 1) {
            return path.getLastPathComponent() == model.getRoot();
        }
        else {
            if (isPathInModel(model, path.getParentPath()) == false) {
                return false;
            }
            else {
                return model.getIndexOfChild(path.getPath()[path.getPathCount() - 2], path.getLastPathComponent()) != -1;
            }
        }
    }

    private boolean isChild(TreePath parent, TreePath child) {
        if ( child.getPathCount() <= parent.getPathCount() ) return false;
        else {
            for (int i = 0; i < parent.getPathCount(); i++) {
                if ( parent.getPath()[i] != child.getPath()[i] ) {
                    return false;
                }
            }
        }
        return true;
    }

    public void clearExpansionState() {
        if (recorded == true) {
            expandedNodeSet.clear();

            recorded = false;
        }
    }
}
