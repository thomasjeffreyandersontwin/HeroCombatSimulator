/*
 * CHARACTERXMLHandler.java
 *
 * Created on March 27, 2004, 5:30 PM
 */

package champions.ioAdapter.heroDesigner;

import champions.Ability;
import champions.Character;
import champions.interfaces.AbilityIterator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import xml.DefaultXMLHandler;
import xml.XMLHandler;
import xml.XMLParseErrorList;
import xml.XMLParser;
import xml.XMLParserException;


/**
 *
 * @author  1425
 */
public class TEMPLATEXMLHandler extends DefaultXMLHandler implements XMLHandler {
    
    /** Creates a new instance of CHARACTERXMLHandler */
    public TEMPLATEXMLHandler() {
        
    }
    
    public TEMPLATEXMLHandler(XMLParser parser) {
        super(parser);
    }
    
    public Object parseNode(Document doc, Node node, Object userData, XMLParseErrorList xmlParseErrorList) throws XMLParserException {
        
        
        return null;
    }
    
}
