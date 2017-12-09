/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treeTable;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.event.InputEvent;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

/**
 *
 * @author twalker
 */
@SuppressWarnings("serial")
public class DefaultTreeTableTransferHandler extends TransferHandler {

    private DefaultTreeTable tree;

    private TreePath[] transferredPaths = null;

    public DefaultTreeTableTransferHandler(DefaultTreeTable tree) {
        this.tree = tree;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        TreeTableModel ttm = tree.getProxyTreeTableModel();
        if (ttm instanceof TreeTableDnDHandler) {
            TreeTableDnDHandler handler = (TreeTableDnDHandler) ttm;
            return handler.canImport(tree, new TreeTableTransferSupport(support));
        }
        return false;
    }

    @Override
    public boolean importData(TransferSupport support) {
        TreeTableModel ttm = tree.getProxyTreeTableModel();
        if (ttm instanceof TreeTableDnDHandler) {
            TreeTableDnDHandler handler = (TreeTableDnDHandler) ttm;
            return handler.importData(tree, new TreeTableTransferSupport(support));
        }
        return false;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {

        Transferable transferable = null;

        updateTransferredPaths();

        TreeTableModel ttm = tree.getProxyTreeTableModel();
        if (ttm instanceof TreeTableDnDHandler) {
            TreeTableDnDHandler handler = (TreeTableDnDHandler) ttm;
            transferable = handler.createTransferable(tree, transferredPaths);
        }

        updateTransferredPaths();

        return transferable;
    }

    @Override
    public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        super.exportAsDrag(comp, e, action);
    }


    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        TreeTableModel ttm = tree.getProxyTreeTableModel();
        if (ttm instanceof TreeTableDnDHandler) {
            TreeTableDnDHandler handler = (TreeTableDnDHandler) ttm;
            handler.exportDone(tree, transferredPaths, data, action);
        }
    }

//    @Override
//    public void exportToClipboard(JComponent comp, Clipboard clip, int action) throws IllegalStateException {
//        super.exportToClipboard(comp, clip, action);
//    }

    @Override
    public int getSourceActions(JComponent c) {
        updateTransferredPaths();

        int action = DnDConstants.ACTION_NONE;

        TreeTableModel ttm = tree.getProxyTreeTableModel();
        if (ttm instanceof TreeTableDnDHandler) {
            TreeTableDnDHandler handler = (TreeTableDnDHandler) ttm;
            action = handler.getDragSourceActions(tree, transferredPaths);
        }

        updateTransferredPaths();

        return action;
    }

    @Override
    public Icon getVisualRepresentation(Transferable t) {
        return super.getVisualRepresentation(t);
    }

    protected void updateTransferredPaths() {
        TreePath[] paths = tree.getDisplayOrderPaths(tree.getSelectionPaths());
        setTransferredPaths(paths);
    }

    /**
     * @return the transferredPaths
     */
    public TreePath[] getTransferredPaths() {
        return transferredPaths;
    }

    /**
     * @param transferredPaths the transferredPaths to set
     */
    public void setTransferredPaths(TreePath[] transferredPaths) {
        this.transferredPaths = transferredPaths;
    }

}
