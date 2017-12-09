/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treeTable;

import java.awt.datatransfer.Transferable;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

/**
 *
 * @author twalker
 */
public interface TreeTableDnDHandler {

    /** Indicates that DnD is supported.
     *
     * The TreeTable will call this method prior to any other
     * DnD calls.  If this method returns false, the Tree
     * will not initiate any DnD calls to the model.
     * <P>
     * Classes that provide a proxy for a TreeTableModel may support
     * the TreeTableDnDHandler interface and pass the actual
     * work to their delegates.  This method provide a way for
     * the Tree to actually check if the final underlying TreeTableModel
     * actually supports DnD.
     *
     * @return True if DnD is supported, false otherwise.
     */
    public boolean isDnDSupported();

    /** Determine if something can be dropped (or pasted) on a tree.
     *
     * During a drag, the DefaultTreeTableModel implementation runs through all of the nodes in
     * the drop location tree path, deepest first, until it find a node willing to accept the
     * import.
     * <P>
     * During a paste, the DefaultTreeTableModel implementation only accepts imports if
     * there is currently a single selected path.  In that case, it runs through that
     * path's node, deepest first, until it find a node willing to accept the import.
     * <p>
     * As a side effect, the DefaultTreeTableModel will set the lastCanImportNode to be the
     * node that responded true to the import or null otherwise.
     * <p>
     * This is for the new dnd support.
     *
     * @param destinationTree TreeTable the drop occurred in.
     * @param support
     * @return True if the node can be imported.
     */
    public boolean canImport(TreeTable destinationTree, TreeTableTransferSupport support);

    /**
     * Imports the data into the target tree.
     *
     * The DefaultTreeTableModel simply passes control off to the last node
     * to accept the data via canImport.  It is up to that node to adjust
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

    /** Creates transferable for default tree table support.
     *
     * The default implementation doesn't handle multiple select well.  It orders
     * the selected paths by display order and then for each selected node, calls
     * DefaultTreeTableNode.createTransferable.  The first transferable that is
     * returned is used as the result.  Subclasses should probably do something
     * smarter than this.
     * <p>
     * Don't forget to override getDragSourceActions in your subclass if you replace
     * createTransferable with something more intelligent.
     * <p>
     * This is for the new dnd support.
     * <p>
     * The selection path for the tree is allowed to be adjusted at this point to
     * represent more accurately which data is being transferred.  For example,
     * it is possible to eliminate the selection of children if desired.
     *
     * @param sourceTree
     * @param transferredPaths Transferred paths, in display order, possibly null.
     * @return Transferable to be transferred.
     */
    public Transferable createTransferable(TreeTable sourceTree, TreePath[] transferredPaths);

    /** Returns the set of drag actions allowed for the current selection.
     *
     * These should come from the set of actions defined in DnDConstants.
     * <p>
     * The selection path for the tree is allowed to be adjusted at this point to
     * represent more accurately which data is being transferred.  For example,
     * it is possible to eliminate the selection of children if desired.
     *
     * @param sourceTree Source of the drag or copy action.
     * @param transferredPaths Transferred paths, in display order, possibly null.
     * @return DnDconstants for allowed source actions.
     */
    public int getDragSourceActions(TreeTable sourceTree, TreePath[] transferredPaths);

    /** Indicates that a drag/paste operation completed with the indicated action.
     *
     * The DefaultTreeTableModel iterates through all paths that we selected when
     * the drag began.  For each path, starting with the last path node, it
     * call exportDone for all of the nodes in the path.  The first node in the
     * path to return true from exportDone will terminate further evaluations of
     * exportDone for that particular path.  Furthermore, if a node returns
     * true from exportDone, all other selection paths that are decended from
     * that nodes path will not have exportDone called for them.
     * <p>
     * In order words, as soon as a node returns true from exportDone, it is
     * assumed that the node has handle both the current path and all other
     * paths that are decended from it.
     * <p>
     * Note, if the drag wasn't accepted, the action with be ACTION_NONE.  Nodes should
     * be careful to check the actions.
     *
     * @param sourceTree TreeTable where the data originated.
     * @param transferredPaths Transferred paths, in display order, possibly null.
     * @param data Data that was transferred.
     * @param action Action that was ultimately accepted or NONE if the drag was not accepted.
     */
    public void exportDone(TreeTable sourceTree, TreePath[] transferredPaths, Transferable data, int action);
}
