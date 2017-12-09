/*
 * BattleEventException.java
 *
 * Created on October 25, 2000, 12:03 PM
 */

package champions.exception;

/**
 *
 * @author  unknown
 * @version 
 */
public class BattleEventException extends Exception {

    
    /** Holds value of property displayError. */
    private boolean displayError = true;
    /**
     * Creates new <code>BattleEventException</code> without detail message.
     */
    public BattleEventException() {
    }


    /**
     * Constructs an <code>BattleEventException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public BattleEventException(String msg) {
        super(msg);
    }
    
    public BattleEventException(String msg, boolean displayError) {
        super(msg);
        setDisplayError( displayError);
    }
    /** Getter for property displayError.
     * @return Value of property displayError.
     */
    public boolean isDisplayError() {
        return displayError;
    }
    /** Setter for property displayError.
     * @param displayError New value of property displayError.
     */
    public void setDisplayError(boolean displayError) {
        this.displayError = displayError;
    }
}

