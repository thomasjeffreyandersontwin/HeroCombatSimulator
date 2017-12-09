/*
 * DefaultXMLHandler.java
 *
 * Created on March 28, 2004, 2:12 AM
 */

package xml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author  1425
 */
public class DefaultXMLHandler implements XMLHandler {
    
    protected XMLParser parser;
    
    public DefaultXMLHandler() {
        
    }
    
    public DefaultXMLHandler(XMLParser parser) {
        setXMLParser(parser);
    }
    
    /** Sets the Parser which is parsing the document.
     *
     */
    public void setXMLParser(XMLParser parser) {
        this.parser = parser;
    }
    
    /** Getter for property parser.
     * @return Value of property parser.
     *
     */
    public xml.XMLParser getParser() {
        return parser;
    }
    
    public Object parseNode(Document doc, Node node, Object userData, XMLParseErrorList xmlParseErrorList) throws XMLParserException {
        
        parser.parseChildren(doc, node, userData, xmlParseErrorList);
        
        return null;
    }
    
    protected int getDepth(Node node) {
        if ( node == null ) return -1;
        return getDepth(node.getParentNode()) + 1;
    }
    
    /** Finds a Node based upon an node description of the Node being searched for.
     *
     * The Node description should follow this pattern:<ul>
     * <li>attrDesc -> [nodeDesc ":"][attrName]
     * <li>nodeDesc -> NodeName[nodeSpec]["." nodeDesc]
     * <li>nodeSpec -> "(" attrName "=" ["'"] attrValue ["'"] ")"</ul>
     *
     * For example:<ul>
     * <li>"ADDER" - describes any node of with the name ADDER.
     * <li>"MODIFIER(XMLID='BOB')" - describes any node with name ADDER and attribute
     *     XMLID of BOB.
     * <li>"ADDER(XMLID=BOB).MODIFIER(XMLID='JIM')" - describes a node with the name
     *     MODIFIER and XMLID of JIM that is the child of a ADDER node with XMLID of Bob.
     * <li>"ADDER(XMLID='BOB'):INPUT" - describes the attribute INPUT of a node ADDER with
     *     XMLID of Bob.
     * <li>"" - describes the root node of the search.</ul>
     *
     * This method can take any of the formats and will look for a matching node.  If
     * there is a colon, it will assume the parameter is an attrDesc, otherwise
     * it will assume that it is a nodeDesc. (This is important, since attrDesc
     * with of the root node and nodeDesc of a child node appear the same.)<P>
     */
    public static Node findNode(Node topNode, String attrDesc) {
        return findNode(topNode, null, attrDesc);
    }

    /** Finds a Node based upon an node description of the Node being searched for.
     *
     * The Node description should follow this pattern:<ul>
     * <li>attrDesc -> [nodeDesc "."][attrName]
     * <li>nodeDesc -> NodeName "(" [nodeSpec] ")" ["." nodeDesc]
     * <li>nodeSpec -> "(" attrName "=" ["'"] attrValue ["'"] ")"</ul>
     *
     * For example:<ul>
     * <li>"ADDER" - describes any node of with the name ADDER.
     * <li>"MODIFIER(XMLID='BOB')" - describes any node with name ADDER and attribute
     *     XMLID of BOB.
     * <li>"ADDER(XMLID=BOB).MODIFIER(XMLID='JIM')" - describes a node with the name
     *     MODIFIER and XMLID of JIM that is the child of a ADDER node with XMLID of Bob.
     * <li>"ADDER(XMLID='BOB').INPUT" - describes the attribute INPUT of a node ADDER with
     *     XMLID of Bob.
     * <li>"" - describes the root node of the search.</ul>
     *
     * This method can take any of the formats and will look for a matching node.  
     * findNode will only return nodes which match the node description and have
     * the attrName attribute value.<P>
     *
     * The topNode parameter is the root of the search.  Only the topNode and 
     * nodes below it will be searched.<P>
     *
     * The startNode parameter is the start of the search amoung the descendents
     * of topNode.  If startNode is null, the search will start at the first
     * child of topNode.  If startNode is non-null, the search will start at the
     * node immediately following startNode (if there is one).
     */
    public static Node findNode(Node topNode, Node startNode, String attrDesc) {    
        int index;
        String currentNode = null;
        String currentAttr = null;
        String currentAttrValue = null;
        String remainder = null;
        String finalAttr = null;

        Pattern p1 = Pattern.compile("\\.?([A-Za-z_]*)\\((?:([A-Za-z_]+)=\'([^\']*)\')?\\)((\\..*\\(.*\\))?(?:\\.([A-Za-z_]*))?)");
        Pattern p2 = Pattern.compile("\\.?([A-Za-z_]*)\\((?:([A-Za-z_]+)=([^\\)]*))?\\)((\\..*\\(.*\\))?(?:\\.([A-Za-z_]*))?)");
        Pattern p3 = Pattern.compile("\\.?([A-Za-z_]+)");

        Matcher m;
        
        if ( attrDesc == null || attrDesc.equals("") ) {
            return topNode;
        }
        else if ( (m=p1.matcher(attrDesc)).matches() || (m=p2.matcher(attrDesc)).matches() ) {
            currentNode = m.group(1);
            currentAttr = m.group(2);
            currentAttrValue = m.group(3);
            remainder = m.group(4);
            String s5 = m.group(5);
            finalAttr = m.group(6);
            
            if ( currentNode == null || currentNode.equals("")) {
                // We are looking for something like:
                // ().ATTR or (), so it must be the current node we want
                if ( currentAttr != null ) {
                    Node attrNode = topNode.getAttributes().getNamedItem(currentAttr);
                    if ( attrNode != null && currentAttrValue.equals( attrNode.getNodeValue() ) ) {
                        // This one matches, so find keep going on this node...
                        Node newNode = findNode(topNode, startNode, remainder);
                        // This node matches the node specification.  If
                        // there is an attrName check that the node has 
                        // it...
                        if ( newNode != null ) return newNode;
                    }
                }
                else {
                    // This one matches, so find keep going on this node...
                    Node newNode = findNode(topNode, startNode, remainder);
                    // Only return if succeeds, otherwise keep looking...
                    if ( newNode != null ) return newNode;
                }
            }
            else {
                // Search through the childred of top node to find the
                // one with name currentNode...
                Node child;
                if ( startNode != null ) {
                    if ( startNode.getParentNode() == topNode ) {
                        child = startNode.getNextSibling();
                    }
                    else {
                        // This won't work right here...searching multiple
                        // layers is going to fail...
                        child = startNode.getParentNode();
                        while ( child != null && child.getParentNode() != topNode ) {
                            child = child.getParentNode();
                        }
                    }
                }
                else {
                    child = topNode.getFirstChild();
                }
                
                while ( child != null ) {
                    if ( currentNode.equals( child.getNodeName() ) ) {
                        if ( currentAttr != null ) {
                            Node attrNode = child.getAttributes().getNamedItem(currentAttr);
                            if ( attrNode != null && currentAttrValue.equals( attrNode.getNodeValue() ) ) {
                                // This one matches, so find keep going on this node...
                                Node newNode = findNode(child, null, remainder);
                                // This node matches the node specification.  If
                                // there is an attrName check that the node has 
                                // it...
                                if ( newNode != null ) return newNode;
                            }
                        }
                        else {
                            // This one matches, so find keep going on this node...
                            Node newNode = findNode(child, remainder);
                            // Only return if succeeds, otherwise keep looking...
                            if ( newNode != null ) return newNode;
                        }
                    }
                    child = child.getNextSibling();
                }
            }
        }
        else if ( (m=p3.matcher(attrDesc)).matches() ) {
            // This is just an attribute now, so return the startNode.
            finalAttr = m.group(1);
            if ( finalAttr == null || finalAttr.equals("") ) {
                return startNode == null ? topNode : startNode;
            }
            else {
                Node node = (startNode == null ? topNode : startNode);
                String finalValue = getAttributeValue(node, finalAttr);
                if ( finalValue != null) return node;
            }
            return null;
        }
        return null;
    }
    
    /** Finds an Attribute value based upon a AttrDesc.
     *
     */
    public static String findAttribute(Node startNode, String attrDesc) {
        Node node = findNode(startNode, attrDesc);
        if ( node != null ){
            return getAttributeValue(node, getAttributeName(attrDesc));
        }
        return null;
    }
    
    /** Returns the final attribute name from an attrDesc string.
     *
     * This method just seperates the attribute name from the rest of the 
     * attrDesc, return just the name.
     */
    public static String getAttributeName(String attrDesc) {

        Pattern p = Pattern.compile("(?:\\.?[A-Za-z_]+\\(.*\\))*\\.?([A-Za-z_]+)");
        Matcher m = p.matcher(attrDesc);

        if ( m.matches() ) {
            // This is just an attribute now, so return the startNode.
            String finalAttr = m.group(1);
            return finalAttr;
        }
        return null;
    }
    
    /** Gets the value of the specified attribute of the node.
     *
     * @return Attribute value, null if attribute doesn't exist.
     */
    public static String getAttributeValue(Node node, String attributeName) {
        Node attrNode = node.getAttributes().getNamedItem(attributeName);
        if ( attrNode == null) {
            return null;
        }
        else {
            return attrNode.getNodeValue();
        }
    }
}
