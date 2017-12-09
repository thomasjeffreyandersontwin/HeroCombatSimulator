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
public class DockingPanelDropDestination implements DockingDropDestination {
    
    DockingFrame dockingFrame;
    DockingPanel dockingDestination;
    Rectangle destinationBounds;
    int side;
    
    private static DockingDropPanel dropPanel = new DockingDropPanel();
    
    /** Creates a new instance of DockingPanelDropDestination */
    public DockingPanelDropDestination(DockingFrame dockingFrame, DockingPanel dockingDestination, Rectangle destinationBounds, int side) {
        this.dockingFrame = dockingFrame;
        this.dockingDestination = dockingDestination;
        this.destinationBounds = destinationBounds;
        this.side = side;
    }

    public DockingDropPanel getDropPanel() {
        dropPanel.setBounds(destinationBounds);
        return dropPanel;
    }

    public void handleDrop(java.awt.dnd.DropTargetDropEvent e) {
        try {
            dockingFrame.resetPanelLayouts();
            Transferable tr = e.getTransferable();
            Point p = e.getLocation();
            
            if(e.isDataFlavorSupported( DockingFrame.dockingPanelDataFlavor )) {
                DockingPanelIndex dpi = (DockingPanelIndex)tr.getTransferData( DockingFrame.dockingPanelDataFlavor );
                e.acceptDrop(DnDConstants.ACTION_MOVE);
                
                DockingPanel dp = DockingPanel.getDockingPanel( dpi.getIndex() );
                if ( dockingDestination == null || dockingDestination == dp ) {
                    e.dropComplete(false);
                    return;
                }
                
                
                Rectangle layoutBounds = dockingDestination.getLayoutBounds();
                
               // int side = dockingFrame.findSide(layoutBounds,p);
                Rectangle r1 = dockingFrame.splitRectangle(layoutBounds, side);
                Rectangle r2 = dockingFrame.splitRectangle(layoutBounds,dockingFrame.getOppositeSide(side));
                dockingDestination.setLayoutBounds( r2 );
                dp.setLayoutBounds( r1 );
                //  System.out.println( "Dropping " + dp + " into " + dd + " on side " + Integer.toString(side));
                // System.out.println ( "Spliting " + dd + " to " + splitRectangle(layoutBounds, getOppositeSide(side)).toString() );
                // System.out.println ( "Added " + dp + " at " + splitRectangle(layoutBounds, side).toString() );
                
                if ( dp.getDockingFrame() != dockingFrame ) {
                    dp.dockIntoFrame(dockingFrame);
                } 
                
                if ( dp.isMinimized() ) {
                    dockingFrame.maximizePanel(dp);
                }
                else {
                    dockingFrame.layoutPanels();
                }
                
                
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

    public boolean isInBounds(Point p) {
        return destinationBounds != null && destinationBounds.contains(p);
    }
    
}
