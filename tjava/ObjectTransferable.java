/*
 * ClassTransferable.java
 *
 * Created on June 15, 2001, 10:34 AM
 */

package tjava;

import java.awt.datatransfer.*;
/**
 *
 * @author  twalker
 * @version 
 */
public class ObjectTransferable extends Object implements Transferable{

    private Object transferObject;
    private Class representationClass; 
    /** Creates new ClassTransferable */
    public ObjectTransferable(Object transferObject, Class representationClass) {
        if ( representationClass instanceof java.io.Serializable == false ) {
            throw new IllegalArgumentException("Class " + representationClass + " not Serializable!");
        }
        
        this.transferObject = transferObject;
        this.representationClass = representationClass;
    }


    public Object getTransferData(final java.awt.datatransfer.DataFlavor p1) 
    throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException {
        if ( isDataFlavorSupported(p1) ) {
            return transferObject;
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
        DataFlavor flavor = null;
        try {
            flavor = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType + "; class=\"" + representationClass.getName() +"\"" );
           // System.out.println(flavor);
        }
        catch ( Exception e ) {
            System.out.println(e);
            e.printStackTrace();
        }
        return new DataFlavor[]  { flavor };
    }
}
