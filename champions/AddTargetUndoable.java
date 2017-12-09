/*
 * addTargetUndoable.java
 *
 * Created on January 21, 2002, 12:30 PM
 */

package champions;

import champions.interfaces.*;
import java.io.Serializable;
/**
 *
 * @author  twalker
 * @version 
 */
public class AddTargetUndoable implements Undoable, Serializable {

    /** Holds value of property battleEvent. */
    private BattleEvent battleEvent;
    
    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Holds value of property targetGroup. */
    private String targetGroup;
    
    /** Creates new addTargetUndoable */
    public AddTargetUndoable(BattleEvent be, Target target, int targetReferenceNumber, String targetGroup) {
        setBattleEvent(be);
        setTarget(target);
        setTargetReferenceNumber(targetReferenceNumber);
        setTargetGroup(targetGroup);
    }

    /** Getter for property battleEvent.
     * @return Value of property battleEvent.
     */
    public BattleEvent getBattleEvent() {
        return battleEvent;
    }
    
    /** Setter for property battleEvent.
     * @param battleEvent New value of property battleEvent.
     */
    public void setBattleEvent(BattleEvent battleEvent) {
        this.battleEvent = battleEvent;
    }
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        this.target = target;
    }
    
    /** Getter for property targetReferenceNumber.
     * @return Value of property targetReferenceNumber.
     */
    public int getTargetReferenceNumber() {
        return targetReferenceNumber;
    }
    
    /** Setter for property targetReferenceNumber.
     * @param targetReferenceNumber New value of property targetReferenceNumber.
     */
    public void setTargetReferenceNumber(int targetReferenceNumber) {
        this.targetReferenceNumber = targetReferenceNumber;
    }
    
    /** Getter for property targetGroup.
     * @return Value of property targetGroup.
     */
    public String getTargetGroup() {
        return targetGroup;
    }
    
    /** Setter for property targetGroup.
     * @param targetGroup New value of property targetGroup.
     */
    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }
    
    public void redo() {
        battleEvent.getActivationInfo().addTarget(target, targetGroup, targetReferenceNumber);
    }
    
    public void undo() {
        battleEvent.getActivationInfo().removeTarget(targetReferenceNumber, targetGroup);
    }
    
}
