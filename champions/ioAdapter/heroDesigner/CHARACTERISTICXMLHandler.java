/*
 * CHARACTERISTICXMLHandler.java
 *
 * Created on March 28, 2004, 11:28 AM
 */

package champions.ioAdapter.heroDesigner;

import champions.Target;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
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
public class CHARACTERISTICXMLHandler extends DefaultXMLHandler implements XMLHandler {
    
    public static AbilityXMLHandler abilityHandler = null;
    
    /** Creates a new instance of CHARACTERISTICXMLHandler */
    public CHARACTERISTICXMLHandler() {
        
    }
    
    public CHARACTERISTICXMLHandler(XMLParser parser) {
        super(parser);
    }
    
    public Object parseNode(Document doc, Node node, Object userData, XMLParseErrorList xmlParseErrorList) throws XMLParserException {
        if ( userData instanceof HDInfo == false ) {
            throw new XMLParserException("userData must be HDInfo object");
        }
        
        HDInfo hdInfo = (HDInfo)userData;
        
        Target target = hdInfo.target;
        
        // Extract the Stat information here...
        String stat = node.getNodeName();
        
        NamedNodeMap attrs = node.getAttributes();
        
        Node isPower = attrs.getNamedItem("ADD_MODIFIERS_TO_BASE");
        
        Node levelsNode = attrs.getNamedItem( "LEVELS" );
        
        if ( isPower == null ) {
            if ( levelsNode != null ) {
                int levels = Integer.parseInt(levelsNode.getNodeValue());
                target.setBaseStat(stat, target.getBaseStat(stat) + levels);
            }
        }
        else {
            // Process it like a power...
            if ( abilityHandler == null ) {
                abilityHandler = new AbilityXMLHandler( getParser() );
            }
            
            abilityHandler.parseNode(doc, node, userData, xmlParseErrorList);
        }
        
        return null;
    }
}
