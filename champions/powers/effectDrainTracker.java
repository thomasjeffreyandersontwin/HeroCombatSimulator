/*
 * effectDrainTracker.java
 *
 * Created on March 6, 2002, 10:33 PM
 */

package champions.powers;

import champions.Ability;
import champions.AdjustmentList;
import champions.Effect;
import champions.Target;
import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author  Trevor Walker
 * @version
 */
public class effectDrainTracker extends Effect {
    
    /** Creates new effectDrainTracker */
    public effectDrainTracker() {
        super("Drain Tracker", "PERSISTENT", false);
        setHidden(true);
    }
    
    /** Getter for property adjustmentList.
     * @return Value of property adjustmentList.
     */
    public AdjustmentList getAdjustmentList(Target target) {
        int index;
        
        if ( target == null ) {
            index = 0;
        }
        else {
            index = findIndexed("AdjustmentList", "TARGET", target);
        }
        
        if ( index != -1 ) {
            return ( AdjustmentList) getIndexedValue(index, "AdjustmentList", "ADJUSTMENTLIST");
        }
        return null;
    }
    
    /** Setter for property adjustmentList.
     * @param adjustmentList New value of property adjustmentList.
     */
    public DrainTrackerALUndoable setAdjustmentList(Target target, AdjustmentList adjustmentList) {
        AdjustmentList oldList = null;
        
         int index = findIndexed("AdjustmentList", "TARGET", target);
         if ( index != -1 ) {
            oldList = ( AdjustmentList) getIndexedValue(index, "AdjustmentList", "ADJUSTMENTLIST");
        }
        
        if ( adjustmentList != null ) {
            if ( index == -1 ) {
                index = createIndexed("AdjustmentList", "TARGET", target);
            }
            addIndexed(index,"AdjustmentList", "ADJUSTMENTLIST", adjustmentList, true);
        }
        else {
            // Remove the entry 
            if ( index != -1 ) {
                removeAllIndexed(index,"AdjustmentList");
            }
        }
        
        return new DrainTrackerALUndoable(this, target, oldList, adjustmentList);
    }
    
    public void removeAdjustmentList(Target target) {
        int index = findIndexed("AdjustmentList", "TARGET", target);
        if ( index != -1 ) {
           removeAllIndexed(index,"AdjustmentList");
        }
    }
    
    /** Getter for property ability.
     * @return Value of property ability.
     */
    public Ability getAbility() {
        return (Ability)getValue("DrainTracker.ABILITY");
    }
    
    /** Setter for property ability.
     * @param ability New value of property ability.
     */
    public void setAbility(Ability ability) {
        add("DrainTracker.ABILITY", ability, true);
    }
    
    public class DrainTrackerALUndoable implements Undoable, Serializable {
        
        /** Holds value of property tracker. */
        private effectDrainTracker tracker;
        
        /** Holds value of property target. */
        private Target target;
        
        /** Holds value of property oldList. */
        private AdjustmentList oldList;
        
        /** Holds value of property newList. */
        private AdjustmentList newList;
        
        /** Creates new DrainTrackerALUndoable */
        public DrainTrackerALUndoable(effectDrainTracker tracker, Target target, AdjustmentList oldList, AdjustmentList newList) {
            this.tracker = tracker;
            this.target = target;
            this.oldList = oldList;
            this.newList = newList;
        }
        
        public void redo() {
            tracker.setAdjustmentList(target, newList);
            
        }
        
        public void undo() {
            if ( oldList == null ) {
                removeAdjustmentList(target);
            }
            else {
                tracker.setAdjustmentList(target, oldList);
            }
        }
        
        /** Getter for property tracker.
         * @return Value of property tracker.
         */
        public effectDrainTracker getTracker() {
            return tracker;
        }
        
        /** Setter for property tracker.
         * @param tracker New value of property tracker.
         */
        public void setTracker(effectDrainTracker tracker) {
            this.tracker = tracker;
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
        
        /** Getter for property oldList.
         * @return Value of property oldList.
         */
        public AdjustmentList getOldList() {
            return oldList;
        }
        
        /** Setter for property oldList.
         * @param oldList New value of property oldList.
         */
        public void setOldList(AdjustmentList oldList) {
            this.oldList = oldList;
        }
        
        /** Getter for property newList.
         * @return Value of property newList.
         */
        public AdjustmentList getNewList() {
            return newList;
        }
        
        /** Setter for property newList.
         * @param newList New value of property newList.
         */
        public void setNewList(AdjustmentList newList) {
            this.newList = newList;
        }
    }
}
