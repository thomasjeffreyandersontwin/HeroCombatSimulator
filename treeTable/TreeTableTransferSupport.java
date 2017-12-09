/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treeTable;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import javax.swing.JTree;
import javax.swing.TransferHandler.TransferSupport;

/** This is a wrapper for a TransferSupport which allows stuff to be modified.
 *
 * // Start rant.
 *
 * I don't know what the hell the DnD implementers were thinking, but the first
 * DnD Java implementation was crap becuase it was so hard to use and the second
 * one was crap because they locked everything down so that it was almost completely
 * non-extensible.  Arrgg!  A well designed library shouldn't make you fight every
 * step of the way to do something you want to do...and I ain't talking about
 * anything wierd.  Just normal DnD activities.  Hell, you can't even create
 * a custom JComponent and do any custom DnD cause everything is package-private.
 * So stupid!  I hope that the fix this all in Java 7, but until then I guess I
 * will just wrap the hell out of everything.
 *
 * // End rant.
 *
 * @author twalker
 */
public class TreeTableTransferSupport {

    protected TransferSupport transferSupport;

    protected TreeTableDropLocation dropLocation;

    public TreeTableTransferSupport(TransferSupport transferSupport) {
        this.transferSupport = transferSupport;
        this.dropLocation = new TreeTableDropLocation( (JTree.DropLocation)transferSupport.getDropLocation());
    }

    public TreeTableTransferSupport(TransferSupport transferSupport, TreeTableDropLocation dropLocation) {
        this.transferSupport = transferSupport;
        this.dropLocation = dropLocation;
    }

    public void setShowDropLocation(boolean showDropLocation) {
        transferSupport.setShowDropLocation(showDropLocation);
    }

    public void setDropAction(int dropAction) {
        transferSupport.setDropAction(dropAction);
    }

    public boolean isDrop() {
        return transferSupport.isDrop();
    }

    public boolean isDataFlavorSupported(DataFlavor df) {
        return transferSupport.isDataFlavorSupported(df);
    }

    public int getUserDropAction() {
        return transferSupport.getUserDropAction();
    }

    public Transferable getTransferable() {
        return transferSupport.getTransferable();
    }

    public int getSourceDropActions() {
        return transferSupport.getSourceDropActions();
    }

    public TreeTableDropLocation getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(TreeTableDropLocation dropLocation) {
        this.dropLocation = dropLocation;
    }

    public int getDropAction() {
        return transferSupport.getDropAction();
    }

    public DataFlavor[] getDataFlavors() {
        return transferSupport.getDataFlavors();
    }

    public Component getComponent() {
        return transferSupport.getComponent();
    }


}
