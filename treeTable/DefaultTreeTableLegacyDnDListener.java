/*
 * AbilityTreeDropTargetListener.java
 *
 * Created on June 14, 2001, 10:11 PM
 */
package treeTable;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.TimerTask;
import javax.swing.tree.TreePath;
import tjava.SharedTimer;

/**
 *
 * @author  twalker
 * @version
 */
@SuppressWarnings("serial")
public class DefaultTreeTableLegacyDnDListener extends Object
        implements TreeTableLegacyDnDListener, Serializable {

    private DefaultTreeTable tree;

    private TreePath lastPath;

    private boolean lastWillDrop;

    private TimerTask timerTask;

    private Rectangle dragImageBounds;

    private Rectangle cueLineBounds;

    private Color cueLineColor;

    /** The TreePath of node which is just before the actual drop location. */
    private TreePath dropLocation;

    private static final int DEBUG = 0;

    /** Creates new AbilityTreeDropTargetListener
     * @param tree
     */
    public DefaultTreeTableLegacyDnDListener(DefaultTreeTable tree) {
        this.tree = tree;

        //    new DropTarget(tree, DnDConstants.ACTION_COPY_OR_MOVE, this);

        //   DragSource dragSource = DragSource.getDefaultDragSource();
        //   dragSource.createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_COPY_OR_MOVE,this);

        dragImageBounds = new Rectangle();
        cueLineBounds = new Rectangle();

        cueLineColor = Color.black;
    }

    @SuppressWarnings("deprecation")
    private boolean isLegacyDnDSupported() {
       return (tree != null && tree.getProxyTreeTableModel() instanceof TreeTableLegacyDnDHandler  && ((TreeTableLegacyDnDHandler)tree.getProxyTreeTableModel()).isLegacyDnDSupported() );
    }

    /**
     * Called when a drag operation has
     * encountered the <code>DropTarget</code>.
     * <P>
     * @param dtde the <code>DropTargetDragEvent</code>
     */
    public void dragEnter(DropTargetDragEvent dtde) {
        updateDropStatus(dtde);

        drawDragImage(dtde.getLocation());
    }

    /**
     * Called when a drag operation is ongoing
     * on the <code>DropTarget</code>.
     * <P>
     * @param dtde the <code>DropTargetDragEvent</code>
     */
    public void dragOver(DropTargetDragEvent dtde) {
        updateDropStatus(dtde);

        drawDragImage(dtde.getLocation());

    }

    /**
     * Called if the user has modified
     * the current drop gesture.
     * <P>
     * @param dtde the <code>DropTargetDragEvent</code>
     */
    public void dropActionChanged(DropTargetDragEvent dtde) {
        updateDropStatus(dtde);
    }

    /**
     * The drag operation has departed
     * the <code>DropTarget</code> without dropping.
     * <P>
     * @param dte the <code>DropTargetEvent</code>
     */
    public void dragExit(DropTargetEvent dte) {
        lastPath = null;
        tree.setSelectionPath(null);

        eraseDragImage();
        eraseCueLine();
    }

    /**
     * The drag operation has terminated
     * with a drop on this <code>DropTarget</code>.
     * This method is responsible for undertaking
     * the transfer of the data associated with the
     * gesture. The <code>DropTargetDropEvent</code>
     * provides a means to obtain a <code>Transferable</code>
     * object that represents the data object(s) to
     * be transferred.<P>
     * From this method, the <code>DropTargetListener</code>
     * shall accept or reject the drop via the
     * acceptDrop(int dropAction) or rejectDrop() methods of the
     * <code>DropTargetDropEvent</code> parameter.
     * <P>
     * Subsequent to acceptDrop(), but not before,
     * <code>DropTargetDropEvent</code>'s getTransferable()
     * method may be invoked, and data transfer may be
     * performed via the returned <code>Transferable</code>'s
     * getTransferData() method.
     * <P>
     * At the completion of a drop, an implementation
     * of this method is required to signal the success/failure
     * of the drop by passing an appropriate
     * <code>boolean</code> to the <code>DropTargetDropEvent</code>'s
     * dropComplete(boolean success) method.
     * <P>
     * Note: The actual processing of the data transfer is not
     * required to finish before this method returns. It may be
     * deferred until later.
     * <P>
     * @param dtde the <code>DropTargetDropEvent</code>
     */
    @SuppressWarnings("deprecation")
    public void drop(DropTargetDropEvent dtde) {


        if ( isLegacyDnDSupported() == false ) {
            dtde.rejectDrop();
        }
        else {
            Point p = dtde.getLocation();
            TreePath path = tree.getPathForLocation(p.x, p.y);

            if (path == null) {
                path = new TreePath(tree.getModel().getRoot());
            }

            boolean rv = false;

            TreeTableModel ttm = tree.getProxyTreeTableModel();

            if (ttm instanceof DefaultTreeTableModel) {
                rv = ((DefaultTreeTableModel) ttm).handleDrop(tree, path, dtde);
            }

            if (rv == false) {
                dtde.rejectDrop();
            }
        }

        lastPath = null;
        lastWillDrop = false;

        stopExpandNodeTimer();
    }


    /**
     * A <code>DragGestureRecognizer</code> has detected
     * a platform-dependent drag initiating gesture and
     * is notifying this listener
     * in order for it to initiate the action for the user.
     * <P>
     * @param dge the <code>DragGestureEvent</code> describing
     * the gesture that has just occurred
     */
    @SuppressWarnings("deprecation")
    public void dragGestureRecognized(DragGestureEvent dge) {

        if ( isLegacyDnDSupported() ) {
            Point p = dge.getDragOrigin();
            TreePath path = tree.getClosestPathForLocation(p.x, p.y);
            if (path != null) {
                //Object last = path.getLastPathComponent();

                TreeTableModel model = tree.getProxyTreeTableModel();

                    TreeTableLegacyDnDHandler treeTableLegacyDnDHandler = (TreeTableLegacyDnDHandler) model;

                    if (treeTableLegacyDnDHandler.startDrag(tree, path, this, dge)) {
                        tree.cancelEditing();
                    }

            }
        }
    }

    /**
     * Called as the hotspot enters a platform dependent drop site.
     * This method is invoked when the following conditions are true:
     * <UL>
     * <LI>The logical cursor's hotspot initially intersects
     * a GUI <code>Component</code>'s  visible geometry.
     * <LI>That <code>Component</code> has an active
     * <code>DropTarget</code> associated with it.
     * <LI>The <code>DropTarget</code>'s registered
     * <code>DropTargetListener</code> dragEnter() method is invoked and
     * returns successfully.
     * <LI>The registered <code>DropTargetListener</code> invokes
     * the <code>DropTargetDragEvent</code>'s acceptDrag() method to
     * accept the drag based upon interrogation of the source's
     * potential drop action(s) and available data types
     * (<code>DataFlavor</code>s).
     * </UL>
     * <P>
     * @param dsde the <code>DragSourceDragEvent</code>
     */
    public void dragEnter(DragSourceDragEvent dsde) {
    }

    /**
     * Called as the hotspot moves over a platform dependent drop site.
     * This method is invoked when the following conditions
     * are true:
     * <UL>
     * <LI>The cursor's logical hotspot has moved but still
     * intersects the visible geometry of the <code>Component</code>
     * associated with the previous dragEnter() invocation.
     * <LI>That <code>Component</code> still has a
     * <code>DropTarget</code> associated with it.
     * <LI>That <code>DropTarget</code> is still active.
     * <LI>The <code>DropTarget</code>'s registered
     * <code>DropTargetListener</code> dragOver() method
     * is invoked and returns successfully.
     * <LI>The <code>DropTarget</code> does not reject
     * the drag via rejectDrag()
     * </UL>
     * <P>
     * @param dsde the <code>DragSourceDragEvent</code>
     */
    public void dragOver(DragSourceDragEvent dsde) {
    }

    /**
     * Called when the user has modified the drop gesture.
     * This method is invoked when the state of the input
     * device(s) that the user is interacting with changes.
     * Such devices are typically the mouse buttons or keyboard
     * modifiers that the user is interacting with.
     * <P>
     * @param dsde the <code>DragSourceDragEvent</code>
     */
    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    /**
     * Called as the hotspot exits a platform dependent drop site.
     * This method is invoked when the following conditions
     * are true:
     * <UL>
     * <LI>The cursor's logical hotspot no longer
     * intersects the visible geometry of the <code>Component</code>
     * associated with the previous dragEnter() invocation.
     * </UL>
     * OR
     * <UL>
     * <LI>The <code>Component</code> that the logical cursor's hotspot
     * intersected that resulted in the previous dragEnter() invocation
     * no longer has an active <code>DropTarget</code> or
     * <code>DropTargetListener</code> associated with it.
     * </UL>
     * OR
     * <UL>
     * <LI> The current <code>DropTarget</code>'s
     * <code>DropTargetListener</code> has invoked rejectDrag()
     * since the last dragEnter() or dragOver() invocation.
     * </UL>
     * <P>
     * @param dse the <code>DragSourceEvent</code>
     */
    public void dragExit(DragSourceEvent dse) {
    }

    /**
     * This method is invoked to signify that the Drag and Drop
     * operation is complete. The getDropSuccess() method of
     * the <code>DragSourceDropEvent</code> can be used to
     * determine the termination state. The getDropAction() method
     * returns the operation that the <code>DropTarget</code>
     * selected (via the DropTargetDropEvent acceptDrop() parameter)
     * to apply to the Drop operation. Once this method is complete, the
     * current <code>DragSourceContext</code> and
     * associated resources become invalid.
     * <P>
     * @param dsde the <code>DragSourceDropEvent</code>
     */
    public void dragDropEnd(DragSourceDropEvent dsde) {
        eraseDragImage();
        eraseCueLine();
        DefaultTreeTable.stopDrag();
    }

    @SuppressWarnings("deprecation")
    private void updateDropStatus(DropTargetDragEvent dtde) {
        if (isLegacyDnDSupported() == false ) {
            dtde.rejectDrag();
        }
        else {
            Point p = dtde.getLocation();
            //TreePath path = tree.getPathForLocation(p.x,p.y);
            TreePath path;
            Rectangle treeBounds = tree.getRowBounds(tree.getRowCount() - 1);
            if (treeBounds != null && p.y <= treeBounds.y + treeBounds.height) {
                if (DEBUG > 0) {
                    System.out.println("Using Closest Location to " + p + " for drop.");
                }
                path = tree.getClosestPathForLocation(p.x, p.y);
            }
            else {
                if (DEBUG > 0) {
                    System.out.println("Using Root path for drop.");
                }
                path = new TreePath(tree.getModel().getRoot());
            }
            TreePath newDropLocation = null;

            if (path == null) {
                path = new TreePath(tree.getModel().getRoot());
            }

            boolean rv = false;
            Object node;

            if (path.equals(lastPath) == false) {

                TreeTableModel ttm = tree.getProxyTreeTableModel();
                if (ttm instanceof DefaultTreeTableModel) {
                    DefaultTreeTableModel dttm = (DefaultTreeTableModel) ttm;
                    newDropLocation = dttm.willHandleDrop(tree, path, dtde);

                    if (newDropLocation != null) {


                        rv = true;
                    }

                    updateDropLocation(newDropLocation);

                    lastPath = path;
                    lastWillDrop = rv;


                    if (dttm.expandDuringDrag(tree, path, dtde) && tree.isExpanded(path) == false) {
                        startExpandNodeTimer();
                    }

                }
            }
            else {
                // Still the same place as last time
                rv = lastWillDrop;
            }


            if (rv == false) {
                dtde.rejectDrag();
                tree.setSelectionPath(null);
            }
            else {
                dtde.acceptDrag(dtde.getDropAction());
            }
        }
    }

    protected void drawDragImage(Point location) {
        Graphics2D g2 = (Graphics2D) tree.getGraphics();

        BufferedImage image = DefaultTreeTable.getDragImage();

        if (DefaultTreeTable.isDragging() && image != null && !DragSource.isDragImageSupported()) {
            Point offset = DefaultTreeTable.getDragOffset();
            // Erase the last ghost image and cue line
            if (dragImageBounds.x != location.x - offset.x || dragImageBounds.y != location.y - offset.y) {

                tree.paintImmediately(dragImageBounds.getBounds());

                // Remember where you are about to draw the new ghost image
                dragImageBounds.setRect(location.x - offset.x, location.y - offset.y, image.getWidth(), image.getHeight());

                // Draw the ghost image
                g2.drawImage(image, AffineTransform.getTranslateInstance(dragImageBounds.getX(), dragImageBounds.getY()), null);

            }

            // Draw the drop location
            if (dropLocation != null) {
                updateCueLine(dropLocation);

                g2.setColor(cueLineColor); // The cue line color
                g2.fill(cueLineBounds);         // Draw the cue line
            }
        }
    }

    protected void eraseDragImage() {
        Graphics2D g2 = (Graphics2D) tree.getGraphics();

        BufferedImage image = DefaultTreeTable.getDragImage();

        if (!DragSource.isDragImageSupported()) {
            // Erase the last ghost image and cue line
            tree.paintImmediately(dragImageBounds.getBounds());

            // Remember where you are about to draw the new ghost image
            dragImageBounds.setRect(-1, -1, -1, -1);
        }
    }

    protected void updateDropLocation(TreePath newDropLocation) {

        if (newDropLocation != dropLocation) {
            
            eraseCueLine();

            if (DEBUG > 0) {
                System.out.println("dropLocation updated from " + dropLocation + " to " + newDropLocation + ".");
            }
            
            dropLocation = newDropLocation;

            if ( dropLocation != null ) {
                Graphics2D g2 = (Graphics2D) tree.getGraphics();
                paintCueLine(g2);
            }
        }
    }

    private void eraseCueLine() {
        if ( cueLineBounds != null && cueLineBounds.isEmpty() == false ) {
            tree.paintImmediately(cueLineBounds);
            cueLineBounds.setRect(-1, -1, -1, -1);
        }
    }

    private void paintCueLine(Graphics2D g2) {
        updateCueLine(dropLocation);
        if ( cueLineBounds != null && cueLineBounds.isEmpty() == false ) {
            g2.setColor(cueLineColor); // The cue line color
            g2.fill(cueLineBounds);         // Draw the cue line
        }
    }

    private void updateCueLine(TreePath dropLocation) {
        if (dropLocation == null) {
            // Hide the cueLine
            // Cue line bounds (2 pixels beneath the drop target)
            cueLineBounds.setRect(0, 0, -1, -1);
        }
        else if (dropLocation.getLastPathComponent() == tree.getModel().getRoot()) {
            // Cue line bounds (2 pixels beneath the drop target)
            cueLineBounds.setRect(0, 0, tree.getWidth(), 2);
        }
        else if (tree.isVisible(dropLocation)) {
            // Get the drop target's bounding rectangle
            Rectangle pathBounds = tree.getPathBounds(dropLocation);

            // Cue line bounds (2 pixels beneath the drop target)
            cueLineBounds.setRect(0, pathBounds.y + (int) pathBounds.getHeight(), tree.getWidth(), 2);
        }
        else {
            // Hide the cueLine
            // Cue line bounds (2 pixels beneath the drop target)
            cueLineBounds.setRect(0, 0, -1, -1);
        }
    }

    protected void startExpandNodeTimer() {
        if ( timerTask == null ) {
            timerTask = new ExpandPathTimerTask();
        }

        timerTask.cancel();

        SharedTimer.getSharedDeamonTimer().schedule(timerTask, 1200);

    }

    protected void stopExpandNodeTimer() {
        if ( timerTask != null ) {
            timerTask.cancel();
        }
    }

    private class ExpandPathTimerTask extends TimerTask {

        @Override
        public void run() {
            if (lastPath != null && tree != null) {
                tree.expandPath(lastPath);
            }
        }
    }
}
