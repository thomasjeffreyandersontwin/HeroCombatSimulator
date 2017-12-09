/*
 * XMLParseErrorList.java
 *
 * Created on September 2, 2005, 3:15 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package xml;

import java.util.ArrayList;

/**
 *
 * @author 1425
 */
public class XMLParseErrorList implements XMLParseError {
    
    ArrayList xmlParseErrors = new ArrayList();
    
    private String errorDescription;
    
    
    /**
     * Creates a new instance of XMLParseErrorList 
     */
    public XMLParseErrorList() {
    }
    
    public XMLParseErrorList(String errorDescription) {
        setErrorDescription(errorDescription);
    }
    
    public void addXMLParseError(XMLParseError ie) {
        xmlParseErrors.add(ie);
    }
    
    public int getXMLParseErrorCount() {
        return xmlParseErrors.size();
    }
    
    public XMLParseError getXMLParseError(int index) {
        return (XMLParseError)xmlParseErrors.get(index);
    }

    public java.lang.String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
    
    public int getErrorSeverity() {
        int count = xmlParseErrors.size();
        int error = 0;
        for(int i = 0; i < count; i++) {
            int error2 = getXMLParseError(i).getErrorSeverity();
            if ( error2 > error ) {
                error = error2;
            }
        }
        return error;
    }
    
    
}
