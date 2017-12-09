/*
 * CharacterNotFound.java
 *
 * Created on September 13, 2000, 8:57 AM
 */

package champions.exception;

/**
 *
 * @author  unknown
 * @version 
 */
public class DetailListNotFound extends Exception {

    /**
     * Creates new <code>CharacterNotFound</code> without detail message.
     */
    public DetailListNotFound() {
    }


    /**
     * Constructs an <code>CharacterNotFound</code> with the specified detail message.
     * @param msg the detail message.
     */
    public DetailListNotFound(String msg) {
        super(msg);
    }
}

