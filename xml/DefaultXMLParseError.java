/*
 * DefaultXMLParseError.java
 *
 * Created on September 2, 2005, 3:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package xml;


/**
 *
 * @author 1425
 */
public class DefaultXMLParseError implements XMLParseError {
    
    protected int errorSeverity;
    protected String errorDescription;
    
    /**
     * Creates a new instance of DefaultXMLParseError 
     */
    public DefaultXMLParseError(String errorDescription, int errorSeverity) {
        setErrorDescription(errorDescription);
        setErrorSeverity(errorSeverity);
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public int getErrorSeverity() {
        return errorSeverity;
    }

    public void setErrorSeverity(int errorSeverity) {
        this.errorSeverity = errorSeverity;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
    
}
