/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treeTable;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import javax.swing.tree.TreePath;

/**
 *
 * @author twalker
 * @deprecated
 */
@Deprecated
public interface TreeTableLegacyDnDHandler {

    /** Indicates that Legacy DnD is supported.
     *
     * The TreeTable will call this method prior to any other
     * Legacy DnD calls.  If this method returns false, the Tree
     * will not initiate any Legacy DnD calls to the model.
     * <P>
     * Classes that provide a proxy for a TreeTableModel may support
     * the TreeTableLegacyDnDHandler interface and pass the actual
     * work to their delegates.  This method provide a way for
     * the Tree to actually check if the final underlying TreeTableModel
     * actually supports legacy DnD.
     *
     * @return True if Legacy DnD is supported, false otherwise.
     */
    public boolean isLegacyDnDSupported();

    /** Notifies the node that it should start a drag.
     *
     * startDrag is called when the TreeTable detects a gesture which would normally start the drag and drop
     * process.  If this node is capable of participating in drag and drop, it should create the appropriate
     * transferrable and call startDrag on the DragGestureEvent.
     * @param treeTable Indicates the TreeTable which is currently displaying the node.
     * @param dragPath Indicates the TreePath at which the drag gesture occurred.
     * @param listener The source listener which should be specified in the startDrag method.
     * @param dge The drag gesture event which occurred.  This can be used to start
     * the drag by calling the method <code>dge.startDrag</CODE>.
     * @return True a drag was started, false otherwise.
     * @deprecated This is for the legacy dnd support.  Use createTransferable if you are using new dnd support.
     */
    public boolean startDrag(TreeTable treeTable, TreePath dragPath, DragSourceListener listener, DragGestureEvent dge);

    /** Notifies the model that a drag the model started previously (vis startDrag) has finished.
     *
     * This method is invoked to signify that the Drag and Drop
     * operation is complete. The getDropSuccess() method of
     * the <code>DragSourceDropEvent</code> can be used to
     * determine the termination state. The getDropAction() method
     * returns the operation that the <code>DropTarget</code>
     * selected (via the DropTargetDropEvent acceptDrop() parameter)
     * to apply to the Drop operation. Once this method is complete, the
     * current <code>DragSourceContext</code> and
     * associated resources become invalid.
     *
     * @param treeTable
     * @param dsde
     *
     * @deprecated This is for the legacy dnd support.
     */
    public void endDrag(TreeTable treeTable, DragSourceDropEvent dsde);

    /** Getter for property expandDuringDrag.
     *
     * Indicates that the node should be expanded, after a suitable amount of time, if the cursor is
     * over this node during the indicated drag.
     * @return Value of property expandDuringDrag.
     * @param treeTable Indicates the TreeTable which is currently displaying the node.
     * @param dropPath Indicates the TreePath at which may be expanded.
     * @param event The DropTargetDragEvent event which is current being processed.
     *
     * @deprecated This is for the legacy dnd support.
     */
    public boolean expandDuringDrag(TreeTable treeTable, TreePath dropPath, DropTargetDragEvent event);

    /** Notifies Node of a drop event occuring in or below the node.
     * The Node can use the handleDrop method to perform actions based on the drop of a DetailList on
     * the Node Jtree.  If the node wishes to handles the event, it should call acceptDrop and dropComplete
     * on the event, then return true to indicate the event was handled.
     * @returns True if event was handled.  False if additional handling
     * should be done.
     * @return True if the event was handled, false if it wasn't.
     * @param treeTable Indicates the TreeTable which is currently displaying the node.
     * @param dropPath Indicates the location of the drop in the Tree hierarchy.
     * @param event The Drop event that is being handled.
     *
     * @deprecated This is for the legacy dnd support.
     */
    public boolean handleDrop(TreeTable treeTable, TreePath dropPath, DropTargetDropEvent event);

    /** Called to check if a node would handle a drop if it occurred.
     * @return Returns the TreePath after which a feedback line could be drawn indicating where the drop will be placed.
     * @param treeTable Indicates the TreeTable which is currently displaying the node.
     * @param path
     * @param event The Drop event that is being handled.
     * @deprecated This method is used by the legacy DefaultTreeTable implementation of drag-n-drop.  Use the
     * newer canImport method if you true is going to use the new Java 1.4 Swing DnD implementation.
     */
    public TreePath willHandleDrop(TreeTable treeTable, TreePath path, DropTargetDragEvent event);
}
