/*
 * PADConfigurationException.java
 *
 * Created on July 1, 2001, 6:06 PM
 */

package champions.exception;

import java.lang.*;

/**
 *
 * @author  twalker
 * @version 
 */
public class PADConfigurationException extends java.lang.RuntimeException {

    /**
 * Creates new <code>PADConfigurationException</code> without detail message.
     */
    public PADConfigurationException() {
    }


    /**
 * Constructs an <code>PADConfigurationException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PADConfigurationException(String msg) {
        super(msg);
    }
}


