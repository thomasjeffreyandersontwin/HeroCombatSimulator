/*
 * WizardException.java
 *
 * Created on July 7, 2001, 12:05 AM
 */

package champions.exception;

/**
 *
 * @author  twalker
 * @version 
 */
public class WizardException extends java.lang.Exception {

    /**
 * Creates new <code>WizardException</code> without detail message.
     */
    public WizardException() {
    }


    /**
 * Constructs an <code>WizardException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public WizardException(String msg) {
        super(msg);
    }
}


