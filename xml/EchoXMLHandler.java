/*
 * EchoXMLHandler.java
 *
 * Created on March 28, 2004, 1:34 AM
 */

package xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author  1425
 */
public class EchoXMLHandler extends DefaultXMLHandler implements XMLHandler {
    
    /** Creates a new instance of EchoXMLHandler */
    public EchoXMLHandler() {
        
    }
    
    public EchoXMLHandler(XMLParser parser) {
        super(parser);
    }
    
    public Object parseNode(org.w3c.dom.Document doc, org.w3c.dom.Node node, Object userData, XMLParseErrorList xmlParseErrorList) 
     throws XMLParserException {
        for(int i = getDepth(node)-1; i >= 0; i--) {
            System.out.print("\t");
        }
        
        System.out.println( node.getNodeName() );
        
        parser.parseChildren(doc, node, userData, xmlParseErrorList);
        return null;
    }
   
    
}
