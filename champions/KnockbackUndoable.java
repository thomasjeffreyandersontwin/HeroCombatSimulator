/*
 * KnockbackUndoable.java
 *
 * Created on November 23, 2001, 2:31 PM
 */

package champions;

import champions.interfaces.*;
import java.io.Serializable;
/**
 *
 * @author  twalker
 * @version 
 */
public class KnockbackUndoable implements Undoable, Serializable {

    protected BattleEvent be;
    protected Target target;
    protected String knockbackGroup;

    /** Holds value of property startingBody. */
    private int startingBody = 0;
    
    /** Holds value of property endingBody. */
    private int endingBody = 0;
    
    /** Creates new KnockbackUndoable */
    public KnockbackUndoable(BattleEvent be, Target target, String knockbackGroup) {
        this.be = be;
        this.target = target;
        this.knockbackGroup = knockbackGroup;
    }

    public void redo() {
        int kbindex = be.getKnockbackIndex(target, knockbackGroup);
        if ( kbindex == -1 ) {
            // If the entry is completely gone, put it back.  Generally this
            // shouldn't happen.
            be.addKnockbackBody(target, knockbackGroup, endingBody);
        }
        else {
            be.setKnockbackBody(kbindex, endingBody);
            be.setKnockbackCount(kbindex, be.getKnockbackCount(kbindex) + 1);
        }
    }
    
    public void undo() {
        int kbindex = be.getKnockbackIndex(target, knockbackGroup);

        if ( kbindex != -1 ) {
            be.setKnockbackBody(kbindex, startingBody);
            be.setKnockbackCount(kbindex, be.getKnockbackCount(kbindex) - 1);
        }
    }
    
    /** Getter for property startingBody.
     * @return Value of property startingBody.
     */
    public int getStartingBody() {
        return startingBody;
    }
    
    /** Setter for property startingBody.
     * @param startingBody New value of property startingBody.
     */
    public void setStartingBody(int startingBody) {
        this.startingBody = startingBody;
    }
    
    /** Getter for property endingBody.
     * @return Value of property endingBody.
     */
    public int getEndingBody() {
        return endingBody;
    }
    
    /** Setter for property endingBody.
     * @param endingBody New value of property endingBody.
     */
    public void setEndingBody(int endingBody) {
        this.endingBody = endingBody;
    }
    
    public String toString() {
        return "KnockbackUndoable[" + target + ", Group:" + knockbackGroup + ", " + Integer.toString(startingBody) + "->" + Integer.toString(endingBody) +"]";
    }
    
}
