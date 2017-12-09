/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treeTable;

import java.awt.datatransfer.Transferable;
import javax.swing.tree.TreePath;

/**
 *
 * @author twalker
 */
public interface TreeTableDnDNode {


    /** Called to check if a node would handle a drop or paste for new Dnd support.
     *
     * Note: the support should be check to see if this is a drag or a copy/cut.
     *
     * @param destinationTree Indicates the TreeTable which is currently displaying the node.
     * @param support TransferSupport with information about the drag or paste.  This
     * is the target of the TransferSupport will be a JTree so getDropLocation() will
     * return a JTree.DropLocation object, although you will have to cast to use it.
     * @return True if the node can import the data.
     *
     */
    public boolean canImport(TreeTable destinationTree, TreeTableTransferSupport support);

    /** Imports the data into the target node.
     *
     * The DefaultTreeTableModel simply passes control off to the last node
     * to accept the data via canImport.  It is up to this node to adjust
     * the TransferSupport information if needed...ie setDropAction
     * should be called if the drop action changes (only if this is a drop).
     * <p>
     * This is for the new dnd support.
     *
     * @param destinationTree Tree receiving the drop (or paste).
     * @param support Transfer support information.
     * @return True if data was imported into the tree, false otherwise.
     */
    public boolean importData(TreeTable destinationTree, TreeTableTransferSupport support);

    /** Creates the transferable for the selected node.
     *
     * This method should create the transferable for the node for a paste/drag
     * operation.  This method is called for each of the currently selected nodes
     * in the tree when the drag starts.  Selected nodes are processed in display
     * order, so if both a parent and a child are selected, the parent will always
     * be processed prior to the child.
     * <p>
     * If multiple nodes are selected, the allSelectedPaths array will contain all of the
     * selected paths in display order.
     * <p>
     * This method should return the transferable for the node, if the node can be copied/moved.
     * It should return null if the isn't any data that can be transferred from this node.
     * If multiple paths are selected and multiple nodes return transferable, the model decides
     * how to combine them.  The DefaultTreeTableModel simply takes the first transferable that
     * isn't null.
     *
     * @param sourceTree Tree table the node resides in.
     * @param thisPath Tree path of this node.
     * @param allSelectedPaths Tree paths of all selected nodes, in display order.
     * @return Transferable for this node or null if there is no transferable data.
     */
    public Transferable createTransferable(TreeTable sourceTree, TreePath thisPath, TreePath[] allSelectedPaths);

    /** Returns the set of drag actions allowed for the current selection.
     *
     * These should come from the set of actions defined in DnDConstants.
     *
     * Like createTransferable, specially handling is needed when more than one path is selected.
     * By default DefaultTreeTableModel simply returns the first constant that isn't ACTION_NONE.
     * Additionally, it is assumed that an node which returns a value other than ACTION_NONE will
     * also return a non-null transferable from createTransferable.
     *
     * @param sourceTree Tree table the node resides in.
     * @param thisPath Tree path of this node.
     * @param allSelectedPaths Tree paths of all selected nodes, in display order.
     * @return DnDconstants for allowed source actions.
     */
    public int getDragSourceActions(TreeTable sourceTree, TreePath thisPath, TreePath[] allSelectedPaths);

    /** Allows a data source node a chance to perform some action based upon the result of a drag/copy/paste data transfer.
     *
     * This method allows a node to react to the completion of a data transfer.  The node doesn't need to do anything.
     * However, in drag/cut situations, it allows the data to be removed if necessary.
     * <p>
     * Note, this method is called even when the action is NONE, so that should be check and the data should
     * not be removed in that case.
     *
     * @param sourceTree treeTable where the data originated.
     * @param path Specific path from transferred paths completing export.  This node will be in the
     * path, but may not be the last component.
     * @param transferredPaths paths originally selected when data transfer originated.
     * @param data data that was transferred.
     * @param action Final action taken by the drop/paste target.
     * @return True if the node has handle all of the data transfer export for this node and all children
     * of this node in the transferred paths.  False otherwise.
     */
    public boolean exportDone(TreeTable sourceTree, TreePath path, TreePath[] transferredPaths, Transferable data, int action);

}
