/*
 * BadDiceFormat.java
 *
 * Created on September 22, 2000, 9:18 PM
 */

package champions.exception;


/**
 *
 * @author  unknown
 * @version 
 */
public class BadDiceException extends Exception {

    /**
     * Creates new <code>BadDiceFormat</code> without detail message.
     */
    public BadDiceException() {
    }


    /**
     * Constructs an <code>BadDiceFormat</code> with the specified detail message.
     * @param msg the detail message.
     */
    public BadDiceException(String msg) {
        super(msg);
    }
}

