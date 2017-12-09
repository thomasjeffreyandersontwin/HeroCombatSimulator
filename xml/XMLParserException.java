/*
 * XMLParserException.java
 *
 * Created on March 28, 2004, 2:07 AM
 */

package xml;

/**
 *
 * @author  1425
 */
public class XMLParserException extends java.lang.RuntimeException {
    
    /**
     * Creates a new instance of <code>XMLParserException</code> without detail message.
     */
    public XMLParserException() {
    }
    
    
    /**
     * Constructs an instance of <code>XMLParserException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public XMLParserException(String msg) {
        super(msg);
    }
}
