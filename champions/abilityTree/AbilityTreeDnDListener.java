/*
 * AbilityTreeDropTargetListener.java
 *
 * Created on June 14, 2001, 10:11 PM
 */

package champions.abilityTree;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.*;
import java.io.*;
/**
 *
 * @author  twalker
 * @version
 */
public class AbilityTreeDnDListener extends Object
implements DropTargetListener, DragGestureListener, DragSourceListener, ActionListener, Serializable {
    
    private AbilityTreeTable tree;
    private TreePath lastPath;
    private boolean lastWillDrop;
    private Timer timer, timer2;
    
    private Rectangle dragImageBounds;
    private Rectangle cueLineBounds;
    private Color cueLineColor;
    
    /** The TreePath of node which is just before the actual drop location. */
    private TreePath dropLocation;
    private boolean dropAsChild;
    
    private boolean scrolling = false;
    private boolean scrollDirection;
    
    private static final int DEBUG = 0;
    
    private Icon leftDragIcon;
    private Icon rightDragIcon;
    
    /** Holds value of property scrollPane. */
    private JScrollPane scrollPane;
    
    /** Creates new AbilityTreeDropTargetListener */
    public AbilityTreeDnDListener(AbilityTreeTable tree) {
        this.tree = tree;
        
        new DropTarget(tree, DnDConstants.ACTION_COPY_OR_MOVE, this);
        
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_COPY_OR_MOVE,this);
        //dragSource.createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_COPY,this);
        //dragSource.createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_MOVE,this);
        
        timer = new Timer(1200, this);
        timer.setRepeats(false);
        
        timer2 = new Timer(100, this); 
        timer.setRepeats(true);
        
        dragImageBounds = new Rectangle();
        cueLineBounds = new Rectangle();
        
        cueLineColor = Color.gray;
        
        setScrollPane(scrollPane);
        
        leftDragIcon = UIManager.getIcon("AbilityTree.leftDragIcon");
        rightDragIcon = UIManager.getIcon("AbilityTree.rightDragIcon");
    }
    
    /**
     * Called when a drag operation has
     * encountered the <code>DropTarget</code>.
     * <P>
     * @param dtde the <code>DropTargetDragEvent</code>
     */
    public void dragEnter(DropTargetDragEvent dtde) {
        scrollTree(dtde.getLocation());
        
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
        scrollTree(dtde.getLocation());
        
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
        stopScrolling();
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
    public void drop(DropTargetDropEvent dtde) {
        stopScrolling();
        
        if ( tree == null ) {
            dtde.rejectDrop();
        }
        else {
            Point p = dtde.getLocation();
            TreePath path = tree.getPathForLocation(p.x,p.y);
            
            if ( path == null ) path = new TreePath(tree.getModel().getRoot());
            
            boolean rv = false;
            Object node;
            
            int index;
            for(index = path.getPathCount() - 1;index >= 0;index--) {
                node = path.getPathComponent(index);
                if ( node instanceof AbilityTreeNode ) {
                    //System.out.println("dragOver: Check will handle for " + node );
                    rv = ((AbilityTreeNode)node).handleDrop(tree, path, dtde);
                    if ( rv == true ) {
                        break;
                    }
                }
            }
            
            if ( rv == false ) {
                dtde.rejectDrop();
                
                eraseDragImage();
            }
        }
        
        lastPath = null;
        lastWillDrop = false;
        timer.stop();
    }
    
    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        if ( e.getSource() == timer ) {
            if ( lastPath != null  && tree != null ) {
                AbilityTreeNode an = (AbilityTreeNode)lastPath.getLastPathComponent();
                
                if ( an != null && an.expandDuringDrag() ) {
                    tree.expandPath(lastPath);
                } 
            }
        }
        else if ( e.getSource() == timer2 ) {
            scroll();
        }
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
    public void dragGestureRecognized(DragGestureEvent dge) {
        Point p = dge.getDragOrigin();
        TreePath path = tree.getClosestPathForLocation(p.x,p.y);
        Object last = path.getLastPathComponent();
        
        if ( last instanceof AbilityTreeNode ) {

            ((AbilityTreeNode)last).startDrag(tree,path,this,dge); 
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
        stopScrolling();
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
        stopScrolling();
        eraseDragImage();
        AbilityTreeTable.stopDrag();
    }
    
    private void updateDropStatus(DropTargetDragEvent dtde) {
        if ( tree == null ) {
            dtde.rejectDrag();
        }
        else {
            Point p = dtde.getLocation();
            //TreePath path = tree.getPathForLocation(p.x,p.y);
            TreePath path;
            Rectangle treeBounds = tree.getRowBounds( tree.getRowCount() - 1 );
            if ( p.y <= treeBounds.y + treeBounds.height ) {
                if ( DEBUG > 0 ) System.out.println("Using Closest Location to " + p + " for drop.");
                path = tree.getClosestPathForLocation(p.x,p.y);
            }
            else {
                if ( DEBUG > 0 ) System.out.println("Using Root path for drop.");
                path = new TreePath( tree.getModel().getRoot() );
            }
            TreePath newDropLocation = null;
            boolean newDropAsChild = false;
            
            if ( path == null ) path = new TreePath(tree.getModel().getRoot());
            
            boolean rv = false;
            Object node;
            
            if ( path.equals(lastPath) == false ) {
                int index;
                for(index = path.getPathCount() - 1;index >= 0;index--) {
                    node = path.getPathComponent(index);
                    if ( node instanceof AbilityTreeNode ) {
                        //System.out.println("dragOver: Check will handle for " + node );
                        newDropLocation = ((AbilityTreeNode)node).willHandleDrop(tree, path, dtde);
                        if ( newDropLocation != null ) {
                            if ( newDropLocation.getLastPathComponent() instanceof DropAsChildPlaceHolder ) {
                                newDropLocation = newDropLocation.getParentPath();
                                newDropAsChild = true;
                            }
                            
                            // Select the Node that will actually handle the drop...
                            Object[] pathObjects = new Object[index+1];
                            for(int j =0; j<= index;j++) {
                                pathObjects[j] = path.getPathComponent(j);
                            }
                            tree.setSelectionPath( new TreePath(pathObjects));
                            
                            rv = true;
                            
                            break;
                        }
                    }
                }
                
                updateDropLocation(newDropLocation, newDropAsChild);
                
                lastPath = path;
                lastWillDrop = rv;
                if ( ((AbilityTreeNode)path.getLastPathComponent()).expandDuringDrag() && tree.isExpanded(path) == false ) {
                    timer.start();
                }
            }
            else {
                // Still the same place as last time
                rv = lastWillDrop;
            }
            
            
            if ( rv == false ) {
                dtde.rejectDrag();
                tree.setSelectionPath(null);
            }
            else {
                dtde.acceptDrag( dtde.getDropAction() );
            }
        }
    }
    
    protected void drawDragImage(Point location) {
        Graphics2D g2 = (Graphics2D) tree.getGraphics();
        
        BufferedImage image = AbilityTreeTable.getDragImage();
        
        if (AbilityTreeTable.isDragging() && image != null && !DragSource.isDragImageSupported()) {
            Point offset = AbilityTreeTable.getDragOffset();
            // Erase the last ghost image and cue line
            if ( dragImageBounds.x != location.x - offset.x || dragImageBounds.y != location.y - offset.y ) {
                
                tree.paintImmediately(dragImageBounds.getBounds());
                
                // Remember where you are about to draw the new ghost image
                dragImageBounds.setRect(location.x - offset.x, location.y - offset.y, image.getWidth(), image.getHeight());
                
                // Draw the ghost image
                g2.drawImage(image, AffineTransform.getTranslateInstance(dragImageBounds.getX(), dragImageBounds.getY()), null);
                
            }
            
            // Draw the drop location
            drawFeedbackLine(g2);
        }
    }
    
    protected void eraseDragImage() {
        Graphics2D g2 = (Graphics2D) tree.getGraphics();
        
        BufferedImage image = AbilityTreeTable.getDragImage();
        
        if (!DragSource.isDragImageSupported()) {
            // Erase the last ghost image and cue line
            tree.paintImmediately(dragImageBounds.getBounds());
            if ( cueLineBounds != null) {
                tree.paintImmediately(cueLineBounds);
            }
            
            // Remember where you are about to draw the new ghost image
            dragImageBounds.setRect(-1, -1, -1, -1);
        }
    }
    
    protected void updateDropLocation(TreePath newDropLocation, boolean dropAsChild) {
        
        if ( (dropLocation == null && newDropLocation != null ) 
            || (dropLocation != null && dropLocation.equals(newDropLocation) == false) 
            || this.dropAsChild != dropAsChild) {
            Graphics2D g2 = (Graphics2D) tree.getGraphics();
            if ( dropLocation != null ) {
                tree.paintImmediately(cueLineBounds);
                cueLineBounds.setRect(-1,-1,-1,-1);
            }
            
            if ( DEBUG > 0 ) System.out.println("dropLocation updated from " + dropLocation + " to " + newDropLocation + ".");
            dropLocation = newDropLocation;
            this.dropAsChild = dropAsChild;
            
            // Draw the drop location
            drawFeedbackLine(g2);
        }
    }
    
    private void drawFeedbackLine(Graphics2D g2) {
        if ( dropLocation != null ) {
            if ( DEBUG > 0 ) System.out.println("Drawing at Location After Path: " + dropLocation);
            TreePath drawLocation = null;
            if ( dropAsChild == false ) {
                drawLocation = dropLocation;
                while( tree.isExpanded(drawLocation) ) {
                    // The dropLocation is expanded.  To make it look like it is being inserted
                    // after the dropLocation, we have to skip all of the expanded crap somehow...
                    AbilityTreeNode an = (AbilityTreeNode)drawLocation.getLastPathComponent();
                    if ( an.getChildCount() == 0 ) {
                        break;
                    }
                    else {
                        drawLocation = drawLocation.pathByAddingChild( an.getLastChild() );
                    }
                    
                }
                
                // Draw the cue line
            }
            else {
                if ( tree.isExpanded(dropLocation) ) {
                    drawLocation = dropLocation;
                }
            }
            
            if ( drawLocation != null && tree.isVisible(drawLocation) ) {
                // Get the drop target's bounding rectangle
                if ( DEBUG > 0 ) System.out.println("Final DrawLocation: " + drawLocation);
                Rectangle pathBounds2 = tree.getPathBounds(dropLocation);
                Rectangle pathBounds = tree.getPathBounds(drawLocation);
                Rectangle treeBounds = tree.getVisibleRect();
                
                int left, right, y, top, bottom;
                int neededHeight;
                
                if ( dropAsChild ) {
                    left = pathBounds2.x + 19;
                }
                else {
                    left = pathBounds2.x;
                }
                right = (int)treeBounds.x + (int)treeBounds.getWidth();
                
                y = (int)pathBounds.y + (int)pathBounds.getHeight();
                
                neededHeight = Math.max(leftDragIcon.getIconHeight(), rightDragIcon.getIconHeight());
                
                top = y - neededHeight / 2;
                bottom = top + neededHeight;
                
                cueLineBounds.setRect(left, top, right - left, bottom - top);
                
                // Draw the left icon
                leftDragIcon.paintIcon(tree, g2, left, y - leftDragIcon.getIconHeight()/2);
                
                // Draw the right icon
                rightDragIcon.paintIcon(tree, g2, right - rightDragIcon.getIconWidth(), y - rightDragIcon.getIconHeight()/2);
                
                
                g2.setColor(cueLineColor); // The cue line color
                g2.drawLine(left + leftDragIcon.getIconWidth(), y, right - rightDragIcon.getIconWidth(), y);   
                
                if ( DEBUG >= 2 ) g2.setColor(Color.red); 
                if ( DEBUG >= 2 ) g2.drawRect((int)pathBounds.x, (int)pathBounds.y, (int)pathBounds.getWidth(), (int)pathBounds.getHeight());
            }
        }
    }
    
    private void scrollTree(Point p) {
        
        Rectangle bounds = tree.getVisibleRect();
        
        Graphics2D g2 = (Graphics2D) tree.getGraphics();
        
        Rectangle r1 = new Rectangle((int)bounds.x , (int)bounds.y, (int)bounds.getWidth(), 10);
        Rectangle r2 = new Rectangle((int)bounds.x  , (int)bounds.y + (int)bounds.getHeight() - 10, (int)bounds.getWidth(), 10);
        if ( DEBUG >= 2 ) g2.setColor(Color.green); 
        if ( DEBUG >= 2 ) g2.drawRect((int)r1.x , (int)r1.y, (int)r1.getWidth(), (int)r1.getHeight());
        if ( DEBUG >= 2 ) g2.setColor(Color.blue); 
        if ( DEBUG >= 2 ) g2.drawRect((int)r2.x , (int)r2.y, (int)r2.getWidth(), (int)r2.getHeight());
        if ( r1.contains(p) ) {
            startScrolling(true);
            scroll();
        }
        else if ( r2.contains(p) ) {
            startScrolling(false);
            scroll();
        }
        else {
            stopScrolling();
        }
        
    }
    
    private void scroll() {
        
        if ( scrolling ) {
            JScrollBar sb = scrollPane.getVerticalScrollBar();
            int increment = sb.getBlockIncrement();
            int value = sb.getValue();
            
            if ( scrollDirection ) {
               sb.setValue( Math.max( value - increment, 0));
            }
            else {
               sb.setValue( Math.min( value + increment, sb.getMaximum())); 
            }
            
        }
    }
    
    private void startScrolling(boolean direction) {
        scrolling = true;
        scrollDirection = direction;
        
        timer2.start();
    }
    
    private void stopScrolling() {
        scrolling = false;
        timer2.stop();
    }
    
    /** Getter for property scrollPane.
     * @return Value of property scrollPane.
     *
     */
    public JScrollPane getScrollPane() {
        return this.scrollPane;
    }
    
    /** Setter for property scrollPane.
     * @param scrollPane New value of property scrollPane.
     *
     */
    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }
    
}
