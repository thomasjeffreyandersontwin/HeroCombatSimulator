/*
 * genericTransferable.java
 *
 * Created on September 10, 2000, 11:41 PM
 */

package dockable;

import java.awt.datatransfer.*;
/**
 *
 * @author  unknown
 * @version 
 */
public class DockingPanelTransferable extends Object implements Transferable{

    private DockingPanelIndex dockingPanelIndex;
    /** Creates new genericTransferable */
    public DockingPanelTransferable(DockingPanel dockingPanel) {
        this.dockingPanelIndex = new DockingPanelIndex( dockingPanel.getDockingPanelIndex() );
    }

    public Object getTransferData(final java.awt.datatransfer.DataFlavor p1) 
    throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException {
        if ( isDataFlavorSupported(p1) ) {
            return (Object)dockingPanelIndex;
        }
        else {
            throw new java.awt.datatransfer.UnsupportedFlavorException(p1);
        }
            
    }
    public boolean isDataFlavorSupported(final java.awt.datatransfer.DataFlavor p1) {
        int i;
        DataFlavor df[] = getTransferDataFlavors();
        for (i=0;i<df.length;i++) {
            if ( p1.equals( df[i] ) ) {
                return true;
            }
        }
        return false;
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]  {new DataFlavor( DockingPanelIndex.class, "DockingPanelIndex" )};
    }
}