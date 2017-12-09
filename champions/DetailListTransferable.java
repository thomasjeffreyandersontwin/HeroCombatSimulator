/*
 * genericTransferable.java
 *
 * Created on September 10, 2000, 11:41 PM
 */

package champions;

import java.awt.datatransfer.*;
import champions.exception.*;
//import champions.DetailList.*;
//import java.lang.ref.*;
/**
 *
 * @author  unknown
 * @version 
 */
public class DetailListTransferable extends Object implements Transferable{

    private DetailListIndex detailListIndex;
    private Class realClass;
    /** Creates new genericTransferable */
    public DetailListTransferable(DetailList detailList) {
        this.detailListIndex = new DetailListIndex( detailList.getDetailListIndex() );
        this.realClass = detailList.getClass();
    }

    public Object getTransferData(final java.awt.datatransfer.DataFlavor flavor) 
    throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException {
        if ( isDataFlavorSupported(flavor) ) {
            if ( flavor.getRepresentationClass() == DetailListIndex.class ) {
                return (Object)detailListIndex;
            }
            else {
                try {
                    Integer dli = detailListIndex.getIndex();
                    return (Object)DetailList.getDetailListReference(dli);
                }
                catch ( DetailListNotFound dnf) {
                    System.out.println("I lost the DetailList: " + dnf);
                    dnf.printStackTrace();
                }
            }
        }
            
        throw new java.awt.datatransfer.UnsupportedFlavorException(flavor);
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
        return new DataFlavor[]  { 
            new DataFlavor( DetailListIndex.class, "DetailListIndex" ),
            new DataFlavor( realClass, realClass.getName() ) };
    }
}