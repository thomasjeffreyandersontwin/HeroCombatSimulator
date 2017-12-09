/*
 * DocumentXMLHandler.java
 *
 * Created on March 27, 2004, 5:25 PM
 */

package xml;


/**
 *
 * @author  1425
 */
public class DocumentXMLHandler extends DefaultXMLHandler implements XMLHandler {
    
    /** Creates a new instance of DocumentXMLHandler */
    public DocumentXMLHandler() {
        
    }
    
    public DocumentXMLHandler(XMLParser parser) {
        super(parser);
    }
    
    public Object parseNode(org.w3c.dom.Document doc, org.w3c.dom.Node node, Object userData, XMLParseErrorList xmlParseErrorList) 
     throws XMLParserException{
        org.w3c.dom.Node child = node.getFirstChild();
        while ( child != null  ) {
            if ( child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE ) {
                Object o = parser.parseNode(doc, child, userData, xmlParseErrorList);
                return o;
            }
            child = child.getNextSibling();
        }
        
        return null;
    }
    
}
