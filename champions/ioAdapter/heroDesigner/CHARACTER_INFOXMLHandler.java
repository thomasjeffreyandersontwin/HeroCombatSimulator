/*
 * CHARACTER_INFOXMLHandler.java
 *
 * Created on March 28, 2004, 2:06 AM
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
import java.awt.*;
import javax.swing.*;
import java.beans.*;
import java.util.*;
import java.io.*;

import champions.interfaces.*;
import champions.event.*;
import champions.interfaces.*;
import tjava.*;

/**
 *
 * @author  1425
 */
public class CHARACTER_INFOXMLHandler extends DefaultXMLHandler implements XMLHandler {
    
    /** Creates a new instance of CHARACTER_INFOXMLHandler */
    public CHARACTER_INFOXMLHandler() {
        
    }
    
    public CHARACTER_INFOXMLHandler(XMLParser parser) {
        super(parser);
    }
    
    public Object parseNode(Document doc, Node node, Object userData, XMLParseErrorList xmlParseErrorList) throws XMLParserException {
        if ( userData instanceof HDInfo == false ) {
            throw new XMLParserException("userData must be HDInfo object");
        }
        
        HDInfo hdInfo = (HDInfo)userData;
        
        Target target = hdInfo.target;
        
        // Extract the name and put it as the target name field.
        NamedNodeMap attrs = node.getAttributes();
        Node nameNode = attrs.getNamedItem( "CHARACTER_NAME" );
        if ( nameNode != null ) target.setName( nameNode.getNodeValue() );
        
        
        // Extract the HD path to the image file and add it to the character
        nameNode = attrs.getNamedItem( "IMAGE_PATH" );
        if (nameNode != null) {
            String filename = nameNode.getTextContent();
            if ( filename != null ) {
                File file = new File( filename );
                if ( file != null ) target.setImageFile(file);
            }
        }
        
        return null;
    }
    
}
