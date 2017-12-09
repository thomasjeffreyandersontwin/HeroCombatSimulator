/*
 * UnknownStatException.java
 *
 * Created on September 12, 2000, 3:45 PM
 */

package champions.exception;

/**
 *
 * @author  unknown
 * @version 
 */
public class UnknownStatException extends Exception {

    /**
     * Creates new <code>UnknownStatException</code> without detail message.
     */
    public UnknownStatException() {
    }


    /**
     * Constructs an <code>UnknownStatException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UnknownStatException(String msg) {
        super(msg);
    }
}

