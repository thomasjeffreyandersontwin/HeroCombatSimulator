package treeTable;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class TreeTableHighlightListener implements MouseListener, MouseMotionListener {   
    
    protected TreeTable treeTable;
    
    public TreeTableHighlightListener(TreeTable treeTable) {
        this.treeTable = treeTable;
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component
     * but no buttons have been pushed.
     */
    public void mouseMoved(MouseEvent e) {
        Component c = e.getComponent();
        
        Point p = e.getPoint();
        if ( p != null ) {
            int row = treeTable.getRowForLocation(p.x,p.y);
            treeTable.setHighlightRow(row);
        }
    }
    
//    protected Point getLocalPoint(Component c, Point p) {
//        while( c != null ) {
//            if ( c == treeTable ) return p;
//            Point p2 = c.getLocation();
//            p.translate(p2.x, p2.y);
//            
//            c = c.getParent();
//        }
//        return null;
//        
//    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
//        Rectangle r = treeTable.getBounds();
//        r.x -= 5;
//        r.y -= 5;
//        r.width -=10;
//        r.height -=10;
//        if ( r.contains(e.getPoint() ) == false ) {
            treeTable.setHighlightRow(-1);
//        }
    }

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {
        treeTable.setHighlightRow(treeTable.getRowForLocation(e.getX(), e.getY()));
    }

    /**
     * Invoked when a mouse button is pressed on a component and then 
     * dragged.  <code>MOUSE_DRAGGED</code> events will continue to be 
     * delivered to the component where the drag originated until the 
     * mouse button is released (regardless of whether the mouse position 
     * is within the bounds of the component).
     * <p> 
     * Due to platform-dependent Drag&Drop implementations, 
     * <code>MOUSE_DRAGGED</code> events may not be delivered during a native 
     * Drag&Drop operation.
     */
    public void mouseDragged(MouseEvent e) {
    }

    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     */
    public void mouseClicked(MouseEvent e) {
    }
    
    
}
