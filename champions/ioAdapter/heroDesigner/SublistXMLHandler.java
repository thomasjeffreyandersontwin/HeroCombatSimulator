/*
 * CHARACTERISTICSXMLHandler.java
 *
 * Created on March 28, 2004, 11:25 AM
 */

package champions.ioAdapter.heroDesigner;

import xml.DefaultXMLHandler;
import xml.XMLHandler;
import xml.XMLParseErrorList;
import xml.XMLParser;
import xml.XMLParserException;

/**
 *
 * @author  1425
 */
public class SublistXMLHandler extends DefaultXMLHandler implements XMLHandler {
    
    private String baseSublist = null;
    
    public SublistXMLHandler() {
        
    }
    
    public SublistXMLHandler(XMLParser parser, String baseSublist) {
        super(parser);
        setBaseSublist(baseSublist);
    }
    
    public Object parseNode(org.w3c.dom.Document doc, org.w3c.dom.Node node, Object userData, XMLParseErrorList xmlParseErrorList) {
        if ( userData instanceof HDInfo == false ) {
            throw new XMLParserException("userData must be HDInfo object");
        }
        
        HDInfo hdInfo = (HDInfo)userData;
        
        hdInfo.baseSublist = baseSublist;
        
        super.parseNode(doc, node, userData, xmlParseErrorList);
        
        return null;
    }    
    
    /**
     * Getter for property baseSublist.
     * @return Value of property baseSublist.
     */
    public java.lang.String getBaseSublist() {
        return baseSublist;
    }    
    
    /**
     * Setter for property baseSublist.
     * @param baseSublist New value of property baseSublist.
     */
    public void setBaseSublist(java.lang.String baseSublist) {
        this.baseSublist = baseSublist;
    }
    
}
