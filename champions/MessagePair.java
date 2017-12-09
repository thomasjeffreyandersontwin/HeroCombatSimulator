/*
 * MessagePair.java
 *
 * Created on April 23, 2002, 7:34 PM
 */

package champions;

/**
 *
 * @author  Trevor Walker
 * @version 
 */
public class MessagePair {

    /** Holds value of property message. */
    private String message;
    
    /** Holds value of property type. */
    private int type;
    
    /** Creates new MessagePair */
    public MessagePair() {
    }
    
    /** Creates new MessagePair */
    public MessagePair(String message, int type) {
        this.message = message;
        this.type = type;
    }

    /** Getter for property message.
     * @return Value of property message.
     */
    final public String getMessage() {
        return message;
    }
    
    /** Setter for property message.
     * @param message New value of property message.
     */
    final public void setMessage(String message) {
        this.message = message;
    }
    
    /** Getter for property type.
     * @return Value of property type.
     */
    final public int getType() {
        return type;
    }
    
    /** Setter for property type.
     * @param type New value of property type.
     */
    final public void setType(int type) {
        this.type = type;
    }
    
}
