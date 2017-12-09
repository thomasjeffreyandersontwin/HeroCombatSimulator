/*
 * XMLHandler.java
 *
 * Created on March 27, 2004, 5:03 PM
 */

package xml;


/**
 *
 * @author  1425
 */
public interface XMLHandler {
    /** Handles the Parsing for a single XML Node from a DOM tree.
     *
     */
    public Object parseNode(org.w3c.dom.Document doc, org.w3c.dom.Node node, Object userData, XMLParseErrorList xmlParseErrorList) throws XMLParserException;

    /** Sets the Parser which is parsing the document.
     *
     */
    public void setXMLParser(XMLParser parser);
}
