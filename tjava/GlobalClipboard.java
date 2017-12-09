/*
 * GlobalClipboard.java
 *
 * Created on August 7, 2001, 7:24 PM
 */

package tjava;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;


/**
 *
 * @author  twalker
 * @version 
 */
public class GlobalClipboard extends java.lang.Object {

    static private Clipboard globalClipboard;

    static public Transferable getContents(Object requestor) {
        initializeClipboard();
        return globalClipboard.getContents(requestor);
    }
    
    static public String getName() {
        initializeClipboard();
        return globalClipboard.getName();
    }
    
    static public void setContents(Transferable contents, ClipboardOwner owner) {
        initializeClipboard();
        globalClipboard.setContents(contents, owner);
    }
    
    static private void initializeClipboard() {
        if ( globalClipboard == null ) globalClipboard = new Clipboard("Global Clipboard");
    }

}
