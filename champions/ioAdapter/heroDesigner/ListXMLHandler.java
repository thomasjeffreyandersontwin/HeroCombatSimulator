/*
 * ListXMLAdapter.java
 *
 * Created on April 10, 2004, 10:09 AM
 */

package champions.ioAdapter.heroDesigner;

import champions.DefaultAbilityList;
import champions.Target;
import champions.interfaces.AbilityList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import xml.DefaultXMLHandler;
import xml.XMLParseErrorList;
import xml.XMLParser;
import xml.XMLParserException;


/**
 *
 * @author  1425
 */
public class ListXMLHandler extends DefaultXMLHandler {
    
    /** Creates a new instance of ListXMLAdapter */
    public ListXMLHandler() {
    }
    
        /** Creates a new instance of ListXMLAdapter */
    public ListXMLHandler(XMLParser parser) {
        super(parser);
    }
    
    public Object parseNode(Document doc, Node node, Object userData, XMLParseErrorList xmlParseErrorList) {
        if ( userData instanceof HDInfo == false ) {
            throw new XMLParserException("userData must be HDInfo object");
        }
        
        HDInfo hdInfo = (HDInfo)userData;
        Target target = hdInfo.target;
        
        String name = findAttribute(node, "NAME");
        if ( name == null || name.equals("") ) {
            name = findAttribute(node, "ALIAS");
        }
        String id = findAttribute(node, "ID");
        
        hdInfo.listToIDMap.put( id, name );
        
        AbilityList sublist = target.getAbilityList().findSublist(hdInfo.baseSublist);
        
        if ( sublist == null ) {
            // Probably will need to create the list eventually, but for now,
            // lets just put it at the root of the abilities.
            sublist = target.getAbilityList();
        }
        
        sublist.addSublist( new DefaultAbilityList(name) );
        
        return null;
    }
    
    
}
