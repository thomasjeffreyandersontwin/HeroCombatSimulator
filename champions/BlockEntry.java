/*
 * BlockEntry.java
 *
 * Created on April 12, 2002, 2:12 PM
 */

package champions;

import java.io.Serializable;
/**
 *
 * @author  Trevor Walker
 * @version 
 */
public class BlockEntry implements Serializable {

    /** Holds value of property time. */
    private Chronometer time;
    
    /** Holds value of property blockedTarget. */
    private Target blockedTarget;
    
    /** Holds value of property blockingTarget. */
    private Target blockingTarget;
    
    /** Creates new BlockEntry */
    public BlockEntry(Chronometer time, Target blockingTarget, Target blockedTarget) {
        setTime(time);
        setBlockingTarget(blockingTarget);
        setBlockedTarget(blockedTarget);
    }

    /** Getter for property time.
     * @return Value of property time.
     */
    public Chronometer getTime() {
        return time;
    }
    
    /** Setter for property time.
     * @param time New value of property time.
     */
    public void setTime(Chronometer time) {
        this.time = time;
    }
    
    /** Getter for property blockedTarget.
     * @return Value of property blockedTarget.
     */
    public Target getBlockedTarget() {
        return blockedTarget;
    }
    
    /** Setter for property blockedTarget.
     * @param blockedTarget New value of property blockedTarget.
     */
    public void setBlockedTarget(Target blockedTarget) {
        this.blockedTarget = blockedTarget;
    }
    
    /** Getter for property blockingTarget.
     * @return Value of property blockingTarget.
     */
    public Target getBlockingTarget() {
        return blockingTarget;
    }
    
    /** Setter for property blockingTarget.
     * @param blockingTarget New value of property blockingTarget.
     */
    public void setBlockingTarget(Target blockingTarget) {
        this.blockingTarget = blockingTarget;
    }
    
}
