/*
 * TargetXMLParser.java
 *
 * Created on March 27, 2004, 4:49 PM
 */

package xml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author  1425
 */
public class XMLParser {
    
    private static final int DEBUG = 0;
    
    protected Map handlerMap;
    protected XMLHandler defaultXMLHandler;
    
    public XMLParser() {
        handlerMap = new HashMap();
        
        registerXMLHandler("#document", new DocumentXMLHandler() );
    }
    
    public void registerXMLHandler(String elementType, XMLHandler handler) {
        handlerMap.put(elementType, handler);
        if ( handler != null ) {
            handler.setXMLParser(this);
        }
    }
    
    public void clearXMLHandler(String elementType) {
        handlerMap.remove(elementType);
        
    }
    
    public Object parse(InputStream stream) {
        return parse(stream, new XMLParseErrorList());
    }
    
    public Object parse(InputStream stream, XMLParseErrorList parseErrorList)  throws XMLParserException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //factory.setValidating(true);
        //factory.setNamespaceAware(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(stream);
            
            Object o = parseNode(document, document, null, parseErrorList);
            return o;
        } catch (SAXException sxe) {
            // Error generated during parsing)
            Exception  x = sxe;
            if (sxe.getException() != null)
                x = sxe.getException();
            x.printStackTrace();
            
        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
            
        } catch (IOException ioe) {
            // I/O error
            ioe.printStackTrace();
        }
        
        return null;
    }
    
    public Object parseNode(Document doc, Node node, Object userData, XMLParseErrorList xmlParseErrorList)
    throws XMLParserException{
        XMLHandler handler = getXMLHandler(node);
        
        if ( handler != null ) {
            return handler.parseNode(doc, node, userData, xmlParseErrorList);
        }
        return null;
    }
    
    public XMLHandler getXMLHandler(Node node) {
        String name = node.getNodeName();
        if ( handlerMap.containsKey(name) ) {
            return (XMLHandler)handlerMap.get(name);
        }
       /* else {
            // Lookup the handler class based on the name and create an instance
            String className = "xml." + name + "XMLHandler";
            try {
            Class c = Class.forName(className);
                if ( c != null ) {

                        XMLHandler handler = (XMLHandler) c.newInstance();
                        handler.setXMLParser(this);
                        registerXMLHandler(name, handler);
                        return handler;
                }
            }
            catch ( ClassNotFoundException cnfe ) {
                if ( defaultXMLHandler != null ) return defaultXMLHandler;
                System.out.println("No XMLHandler found for XML Element type \'" + name + "\'\n");
                cnfe.printStackTrace();
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        } */
        if ( DEBUG >= 1 ) System.out.println("No XMLHandler found for XML Element type \'" + name + "\'\n");

        return defaultXMLHandler;
    }
    
    /** Getter for property defaultXMLHandler.
     * @return Value of property defaultXMLHandler.
     *
     */
    public xml.XMLHandler getDefaultXMLHandler() {
        return defaultXMLHandler;
    }
    
    /** Setter for property defaultXMLHandler.
     * @param defaultXMLHandler New value of property defaultXMLHandler.
     *
     */
    public void setDefaultXMLHandler(xml.XMLHandler defaultXMLHandler) {
        this.defaultXMLHandler = defaultXMLHandler;
        defaultXMLHandler.setXMLParser(this);
    }
    
    public void parseChildren(Document doc, Node node, Object userData , XMLParseErrorList xmlParseErrorList)  throws XMLParserException {
        Node child = node.getFirstChild();
        
        while ( child != null ) {
            parseNode(doc, child, userData, xmlParseErrorList);
            
            child = child.getNextSibling();
        }
    }
    
}
