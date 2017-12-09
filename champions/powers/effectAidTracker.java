/*
 * effectAidTracker.java
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

/** Tracks the AdjustmentList that was last used by AID.
 *
 * The sole purpose of the effectAidTracker is to track the adjustment list last used by a particular AID power.
 * It does not track the amount of aid applied nor does it have anything to do with the fading of the aid.
 *
 * @author  Trevor Walker
 * @version
 */
public class effectAidTracker extends Effect {
    
    private Ability ability;
    /** Creates new effectAidTracker */
    public effectAidTracker() {
        super("Aid Tracker", "PERSISTENT", false);
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
    
    public int getAdjustmentListCount() {
        return getIndexedSize("AdjustmentList");
    }
    
    public Target getAdjustmentListTarget(int index) {
        return ( Target) getIndexedValue(index, "AdjustmentList", "TARGET");
    }
    
    /** Setter for property adjustmentList.
     * @param adjustmentList New value of property adjustmentList.
     */
    public AidTrackerALUndoable setAdjustmentList(Target target, AdjustmentList adjustmentList) {
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
        
        return new AidTrackerALUndoable(this, target, oldList, adjustmentList);
    }
    
    public void removeAdjustmentList(Target target) {
        int index = findIndexed("AdjustmentList", "TARGET", target);
        if ( index != -1 ) {
           removeAllIndexed(index,"AdjustmentList");
        }
    }

    public Ability getAbility() {
        return ability;
    }

    public void setAbility(Ability ability) {
        this.ability = ability;
    }
    

    
    public class AidTrackerALUndoable implements Undoable, Serializable {
        
        /** Holds value of property tracker. */
        private effectAidTracker tracker;
        
        /** Holds value of property target. */
        private Target target;
        
        /** Holds value of property oldList. */
        private AdjustmentList oldList;
        
        /** Holds value of property newList. */
        private AdjustmentList newList;
        
        /** Creates new AidTrackerALUndoable */
        public AidTrackerALUndoable(effectAidTracker tracker, Target target, AdjustmentList oldList, AdjustmentList newList) {
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
        public effectAidTracker getTracker() {
            return tracker;
        }
        
        /** Setter for property tracker.
         * @param tracker New value of property tracker.
         */
        public void setTracker(effectAidTracker tracker) {
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
