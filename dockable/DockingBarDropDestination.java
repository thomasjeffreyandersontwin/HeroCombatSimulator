/*
 * DockingPanelDropDestination.java
 *
 * Created on September 9, 2005, 1:46 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package dockable;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.io.IOException;

/**
 *
 * @author 1425
 */
public class DockingBarDropDestination implements DockingDropDestination {
    
    DockingFrame dockingFrame;
    int side;
    
    private static DockingDropPanel dropPanel = new DockingDropPanel();
    
    private static final int BAR_HEIGHT = 26;
    
    /** Creates a new instance of DockingPanelDropDestination */
    public DockingBarDropDestination(DockingFrame dockingFrame, int side ) {
        this.dockingFrame = dockingFrame;
        
        this.side = side;
    }

    /** Returns a DockingDropPanel with the bounds set appropriate to display user feedback.
     *
     * The bounds of the box should be in coordinates where 0,0 is the upper left corner
     * just below the menu bar.
     */
    public DockingDropPanel getDropPanel() {
        Rectangle d = dockingFrame.getContentPane().getBounds();
        
        int barHeight = BAR_HEIGHT;
        
        DockingBar bar = null;
        
        if ( dockingFrame.dockingBars[side] != null && dockingFrame.dockingBars[side].isVisible() ) {
            bar = dockingFrame.dockingBars[side];
        }
        
        switch (side) {
            case DockingFrame.TOP_SIDE:
                if ( bar != null ) barHeight = bar.getHeight();
                dropPanel.setBounds(d.x,d.y, d.width, barHeight);
                break;
            case DockingFrame.BOTTOM_SIDE:
                if ( bar != null ) barHeight = bar.getHeight();
                dropPanel.setBounds(d.x,d.y+d.height - barHeight, d.width, barHeight);
                break;
            case DockingFrame.LEFT_SIDE:
                if ( bar != null ) barHeight = bar.getWidth();
                dropPanel.setBounds(d.x,d.y, barHeight, d.height);
                break;
            case DockingFrame.RIGHT_SIDE:
                if ( bar != null ) barHeight = bar.getWidth();
                dropPanel.setBounds(d.x+d.width - barHeight,d.y, barHeight, d.height);
                break;
        }
        return dropPanel;
    }

    public void handleDrop(java.awt.dnd.DropTargetDropEvent e) {
        try {
            Transferable tr = e.getTransferable();
            Point p = e.getLocation();
            
            if(e.isDataFlavorSupported( DockingFrame.dockingPanelDataFlavor )) {
                DockingPanelIndex dpi = (DockingPanelIndex)tr.getTransferData( DockingFrame.dockingPanelDataFlavor );
                e.acceptDrop(DnDConstants.ACTION_MOVE);
                
                DockingPanel dp = DockingPanel.getDockingPanel( dpi.getIndex() );
                
                if ( dp.getDockingFrame() != dockingFrame ) {
                    dp.dockIntoFrame(dockingFrame);
                } 
                
                dockingFrame.minimizePanel(dp,  side);
                
                e.dropComplete(true);
            } else {
                e.rejectDrop();
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch(UnsupportedFlavorException ufe) {
            ufe.printStackTrace();
        }
    }

    /** Return whether the indicated point is still within this drop destination.
     *
     * This method should determine whether the point is still within this drop destination
     * or if it has possibly moved into a different drop destination.
     *
     * The point will be in frame coordinates (LayerPane specifically).
     */
    public boolean isInBounds(Point p) {
        Rectangle d = dockingFrame.getContentPane().getBounds();
        
        Rectangle r = null;
        switch (side) {
            case DockingFrame.TOP_SIDE:
                r = new Rectangle(d.x,d.y, d.width, 33);
                break;
            case DockingFrame.BOTTOM_SIDE:
                r = new Rectangle(d.x,d.y+d.height - 33, d.width, 33);
                break;
            case DockingFrame.LEFT_SIDE:
                r = new Rectangle(d.x,d.y, 33, d.height);
                break;
            case DockingFrame.RIGHT_SIDE:
                r = new Rectangle(d.x+d.width - 33,d.y, 33, d.height);
                break;
        }
        return r.contains(p);
    }
    
}
