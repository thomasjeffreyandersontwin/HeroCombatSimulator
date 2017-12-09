/*
 * HDImportError.java
 *
 * Created on September 2, 2005, 3:17 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package champions.ioAdapter.heroDesigner;

import champions.Target;
import champions.TargetEditor;
import xml.DefaultXMLParseError;

/**
 *
 * @author 1425
 */
public class HDImportError extends DefaultXMLParseError {
    
    public static final int IMPORT_INFO = 0;
    public static final int IMPORT_WARNING = 1;
    public static final int IMPORT_ERROR = 2;
    public static final int IMPORT_FAILURE = 3;
    
    public HDImportError(String errorDescription, int severity) {
        super(errorDescription, severity);
    }
    
    /** Returns whether there is some method to handle the error.
     *
     */
    public boolean canHandleError() {
        return false;
    }
    
    /** Attempts to handle the error in some manner.
     *
     */
    public void handleError(Target target, TargetEditor targetEditor) {
        
    }
}
