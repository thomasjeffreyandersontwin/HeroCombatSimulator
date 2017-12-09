/*
 * FadeTracker.java
 *
 * Created on March 7, 2002, 9:59 AM
 */

package champions;

import champions.exception.BattleEventException;
import champions.exceptionWizard.ExceptionWizard;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.Undoable;
import champions.powers.effectAdjusted;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import javax.swing.AbstractAction;

/**
 *
 * @author  Trevor Walker
 * @version
 */
public class FadeTracker extends DetailList implements ChampionsConstants {
    
    private static final long serialVersionUID = -3219399499188185647L;
    
    int nextReferenceNumber = 0;
    
    /** Creates new FadeTracker */
    public FadeTracker(Object adjustable, Target target) {
        setAdjustable(adjustable);
        setTarget(target);
    }
    
    protected int getNextReferenceNumber() {
        return nextReferenceNumber++;
    }
    
    public void addFadeInfo(BattleEvent be, Target source, Ability sourceAbility, String adjustmentType, double amount, Chronometer startTime, long interval, int rate) {
        Undoable undoable;
        
        int index = findIndexed("Fader", "SOURCEABILITY", sourceAbility);
        if ( index == -1 ) {
            
            // We have to create a new one...
            int referenceNumber = getNextReferenceNumber();
            index = createIndexed("Fader", "SOURCEABILITY", sourceAbility);
            setReferenceNumber(index, referenceNumber);
            setSourceTarget(index, source);
            setAdjustmentType(index, adjustmentType);
            setAmount(index, amount);
            setDecayInterval(index, interval);
            setDecayRate(index, rate);
            setStartTime(index, startTime);
            
            be.addUndoableEvent( new FadeTracker.AddFadeUndoable(this, referenceNumber, sourceAbility, adjustmentType, amount, startTime, interval, rate));
            
            if ( rate > 0 ) {
                Undoable u = setupNextFadeEvent(index, startTime);
                be.addUndoableEvent(u);
            }
        }
        else {
            double currentAmount = getAmount(index);
            
            setAmount(index, currentAmount + amount);
            undoable = new FadeTracker.SetFadeAmountUndoable(this, index, currentAmount, currentAmount + amount);
            be.addUndoableEvent(undoable);
        }
        
        try {
            effectAdjusted ea = effectAdjusted.getAdjustedEffect(be, getTarget(), getAdjustable(), this);
            if ( ea != null ) ea.updateName();
        }
        catch( BattleEventException bee) {
            ExceptionWizard.postException(bee);
        }
        
    }
    
    protected void removeFadeInfo(BattleEvent be, int referenceNumber)  {
        int index = getReferenceNumberIndex(referenceNumber);
        Undoable undoable = new FadeTracker.RemoveFadeUndoable(this, referenceNumber, getSourceTarget(index), getSourceAbility(index), getAdjustmentType(index), getAmount(index), getStartTime(index), getDecayInterval(index), getDecayRate(index));
        removeAllIndexed(index, "Fader");
        be.addUndoableEvent(undoable);
        
        effectAdjusted ea = effectAdjusted.findAdjustedEffect(getTarget(), getAdjustable());
        if ( ea != null ) ea.updateName();
        
    }
    
    public int getSourceAbilityIndex(Ability sourceAbility) {
        int index = findIndexed("Fader", "SOURCEABILITY", sourceAbility);
        return index;   
    }
    
    public int getReferenceNumberIndex(int referenceNumber) {
        int index = findIndexed("Fader", "REFERENCENUMBER", new Integer(referenceNumber));
        return index;   
    }
    
    public int fadeAdjustment(BattleEvent be, Ability sourceAbility, boolean fadeCompletely) {
        // Fade the appropriate adjustable
            Object adjustable = getAdjustable();
            Target target = getTarget();
            String versustype;
            
            int index = findIndexed("Fader", "SOURCEABILITY", sourceAbility);
            if ( index == -1 ) return 0;
            
            long rate = getDecayRate(index);
            double adjustedAmount = getAmount(index);
            String type = getAdjustmentType(index);
            
            double fadeAmount; 
            
            if ( fadeCompletely ) {
                fadeAmount = -1 * adjustedAmount;
            }
            else {
                fadeAmount = -1 * Math.min(rate, adjustedAmount);
            }
            
            if ( type.equals( AID ) ) {
                Effect e = new Effect("AdjustmentFader", "INSTANT");
                if ( adjustable instanceof Ability ) {
                    versustype = "ABILITY";
                    e.addAidSubeffect( "AdjustmentFader", versustype, adjustable, fadeAmount );
                }
                else {
                    versustype = "STATCP";
                    e.addAidSubeffect( "AdjustmentFader", versustype, ((Characteristic)adjustable).getName(), fadeAmount );
                }


                try {
                    e.addEffect(be,target);
                }
                catch (BattleEventException bee ) {

                }
            }
            else if ( type.equals( DRAIN ) ) {
                Effect e = new Effect("AdjustmentFader", "INSTANT");
                if ( adjustable instanceof Ability ) {
                    versustype = "ABILITY";
                    e.addDrainSubeffect( "AdjustmentFader", versustype, adjustable, fadeAmount );
                }
                else {
                    versustype = "STATCP";
                    e.addDrainSubeffect( "AdjustmentFader", versustype, ((Characteristic)adjustable).getName(), fadeAmount );
                }


                try {
                    e.addEffect(be,target);
                }
                catch (BattleEventException bee ) {

                }
            }
            
            // Reduce the adjusted Amount
            setAmount(index, adjustedAmount + fadeAmount);
            
            // Create Undoable!!!
            be.addUndoableEvent( new FadeTracker.SetFadeAmountUndoable(this, index, adjustedAmount, adjustedAmount + fadeAmount));
            
            if ( adjustedAmount + fadeAmount <= 0 ) {
                // If the adjustable has been completely faded, fade tracker
                    removeFadeInfo(be, index);
            }
                
            
            if ( getFaderCount() == 0 ) {
                // We must have removed the last one, so unhook the tracker...
                int tindex = getTarget().findIndexed("FadeTracker", "FADETRACKER", this);
                if ( tindex != -1 ) {
                    getTarget().removeAllIndexed(tindex, "FadeTracker");
                    be.addUndoableEvent( new RemoveFadeTrackerUndoable(getTarget(), this));
                }
                
                try {
                
                    effectAdjusted ea = effectAdjusted.findAdjustedEffect(getTarget(), getAdjustable());
                    if ( ea != null ) {
                        ea.removeEffect(be, getTarget());
                    }
                }
                catch ( BattleEventException bee ) {
                    ExceptionWizard.postException(bee);
                }
            }
            
            return (int)(adjustedAmount + fadeAmount);
        
    }
    

    
    /** Gets to Total Adjustment applied to the Adjustable.
     *
     * This method will return the amount of adjustment that has been done by
     * any adjustment power to the adjustable.  Aid/Absorb/TransferTo will be counted
     * as a positive adjustment, while drains/TransferFroms will be counted as negative.
     */
    public double getAidAdjustment() {
        double total = 0;
        double amount;
        String type;
        int index = getFaderCount() - 1;
        for(; index >= 0; index--) {
            type = getAdjustmentType(index);
            amount = getAmount(index);
            if ( type.equals( AID ) ) {
                total += amount;
            }
//            else 
//                if ( type.equals( DRAIN ) ) {
//                total -= amount;
//            }
        }
        return total;
    }
    
    /** Gets to Total Adjustment applied to the Adjustable.
     *
     * This method will return the amount of adjustment that has been done by
     * any adjustment power to the adjustable.  Aid/Absorb/TransferTo will be counted
     * as a positive adjustment, while drains/TransferFroms will be counted as negative.
     */
    public double getDrainAdjustment() {
        double total = 0;
        double amount;
        String type;
        int index = getFaderCount() - 1;
        for(; index >= 0; index--) {
            type = getAdjustmentType(index);
            amount = getAmount(index);
//            if ( type.equals( AID ) ) {
//                total += amount;
//            }
//            else 
                if ( type.equals( DRAIN ) ) {
                total -= amount;
            }
        }
        return total;
    }
    
    /** Gets to Total Adjustment applied to the Adjustable.
     *
     * This method will return the amount of adjustment that has been done by
     * any adjustment power to the adjustable.  Aid/Absorb/TransferTo will be counted
     * as a positive adjustment, while drains/TransferFroms will be counted as negative.
     */
    public double getHealAdjustment() {
        double total = 0;
        double amount;
        String type;
        int index = getFaderCount() - 1;
        for(; index >= 0; index--) {
            type = getAdjustmentType(index);
            amount = getAmount(index);
            if ( type.equals( HEAL ) ) {
                total += amount;
            }
//            else {
//                total -= amount;
//            }
        }
        return total;
    }
    
    protected Undoable setupNextFadeEvent(int index, Chronometer currentTime) {
        Chronometer nextTime = currentTime.getLeastTurnEndSegment( getDecayInterval(index) );
        setNextTime(index, nextTime);
        
        int referenceNumber = getReferenceNumber(index);
        BattleEvent be = new BattleEvent( new FadeAction(this, referenceNumber), nextTime);
        Undoable u = Battle.currentBattle.addDelayedEvent(be);
        addIndexed(index, "Fader", "FADEEVENT", be, true);
        return u;
    }
    
    protected Undoable removeNextFadeEvent(int index) {
        Undoable u = null;
        BattleEvent be = (BattleEvent)getIndexedValue(index, "Fader", "FADEEVENT");
        if ( be != null ) {
            Battle.currentBattle.removeDelayedEvent(be);
            removeIndexed(index, "Fader", "FADEEVENT");
            u = new RemoveFadeEventUndoable(index, be);
        }
        return u;
    }
    
    /** Stop the fader from further fading the adjustable.
     *
     */
    public Undoable stopFading() {
        CompoundUndoable u = new CompoundUndoable();
        for(int i = 0; i < getIndexedSize("Fader"); i++) {
            u.addUndoable( removeNextFadeEvent(i) );
        }
        return u;
    }
    
    /** Start all fader adjusting.
     *
     *  This can be done after a fader has been stopped.
     *
     */
    public Undoable startFading() {
        CompoundUndoable u = new CompoundUndoable();
        for(int i = 0; i < getIndexedSize("Fader"); i++) {
            BattleEvent be = (BattleEvent)getIndexedValue(i, "Fader", "FADEEVENT");
            if ( be == null ) {
                u.addUndoable( setupNextFadeEvent(i, Battle.currentBattle.getTime()));
            }
        }
        return u;
    }
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return (Target)getValue("Adjustable.TARGET");
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        add("Adjustable.TARGET", target, true);
    }
    
        /** Getter for property adjustable.
     * @return Value of property adjustable.
     */
    public Object getAdjustable() {
        return (Object)getValue("Adjustable.OBJECT");
    }
    
    /** Setter for property adjustable.
     * @param adjustable New value of property adjustable.
     */
    public void setAdjustable(Object adjustable) {
        add("Adjustable.OBJECT", adjustable, true);
    }
    
    /** Indexed getter for property sourceAbility.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public Ability getSourceAbility(int index) {
        return (Ability)getIndexedValue(index, "Fader", "SOURCEABILITY");
    }
    
    /** Indexed setter for property sourceAbility.
     * @param index Index of the property.
     * @param sourceAbility New value of the property at <CODE>index</CODE>.
     */
    public void setSourceAbility(int index, Ability sourceAbility) {
        addIndexed(index, "Fader", "SOURCEABILITY", sourceAbility, true);
    }
    
    /** Getter for property fadeCount.
     * @return Value of property fadeCount.
     */
    public int getFaderCount() {
        return getIndexedSize("Fader");
    }
    
    /** Indexed getter for property adjustmentType.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public String getAdjustmentType(int index) {
        return (String)getIndexedValue(index, "Fader", "ADJUSTMENTTYPE");
    }
    
    /** Indexed setter for property sourceAbility.
     * @param index Index of the property.
     * @param sourceAbility New value of the property at <CODE>index</CODE>.
     */
    public void setAdjustmentType(int index, String adjustmentType) {
        addIndexed(index, "Fader", "ADJUSTMENTTYPE", adjustmentType, true);
    }
    
    /** Indexed getter for property source.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public Target getSourceTarget(int index) {
        return (Target)getIndexedValue(index, "Fader", "SOURCETARGET");
    }
    
    /** Indexed setter for property sourceAbility.
     * @param index Index of the property.
     * @param sourceAbility New value of the property at <CODE>index</CODE>.
     */
    public void setSourceTarget(int index, Target sourceTarget) {
        addIndexed(index, "Fader", "SOURCETARGET", sourceTarget, true);
    }
    
    /** Indexed getter for property amount.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public double getAmount(int index) {
        Double d = getIndexedDoubleValue(index, "Fader", "AMOUNT");
        return ( d == null ) ? 0 : d.doubleValue();
    }
    
    /** Indexed setter for property amount.
     * @param index Index of the property.
     * @param amount New value of the property at <CODE>index</CODE>.
     */
    public void setAmount(int index, double amount) {
        addIndexed(index, "Fader", "AMOUNT", new Double(amount), true);
    }
    
    /** Indexed getter for property amount.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public int getReferenceNumber(int index) {
        Integer d = getIndexedIntegerValue(index, "Fader", "REFERENCENUMBER");
        return ( d == null ) ? 0 : d.intValue();
    }
    
    /** Indexed setter for property amount.
     * @param index Index of the property.
     * @param amount New value of the property at <CODE>index</CODE>.
     */
    public void setReferenceNumber(int index, int referenceNumber) {
        addIndexed(index, "Fader", "REFERENCENUMBER", new Integer(referenceNumber), true);
    }
    
    /** Indexed getter for property startTime.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public Chronometer getStartTime(int index) {
        return (Chronometer)getIndexedValue(index, "Fader", "STARTTIME");
    }
    
    /** Indexed setter for property startTime.
     * @param index Index of the property.
     * @param startTime New value of the property at <CODE>index</CODE>.
     */
    public void setStartTime(int index, Chronometer startTime) {
        addIndexed(index, "Fader", "STARTTIME", startTime, true);
    }
    
    /** Indexed getter for property nextTime.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public Chronometer getNextTime(int index) {
        return (Chronometer)getIndexedValue(index, "Fader", "NEXTTIME");
    }
    
    /** Indexed setter for property nextTime.
     * @param index Index of the property.
     * @param nextTime New value of the property at <CODE>index</CODE>.
     */
    public void setNextTime(int index, Chronometer nextTime) {
        addIndexed(index, "Fader", "NEXTTIME", nextTime, true);
    }
    
    /** Indexed getter for property decayInterval.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public long getDecayInterval(int index) {
        Long d = getIndexedLongValue(index, "Fader", "DECAYINTERVAL");
        return ( d == null ) ? 0 : d.longValue();
    }
    
    /** Indexed setter for property decayInterval.
     * @param index Index of the property.
     * @param decayInterval New value of the property at <CODE>index</CODE>.
     */
    public void setDecayInterval(int index, long decayInterval) {
        addIndexed(index, "Fader", "DECAYINTERVAL", new Long(decayInterval), true);
    }
    
    /** Indexed getter for property decayRate.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public int getDecayRate(int index) {
        Integer d = getIndexedIntegerValue(index, "Fader", "DECAYRATE");
        return ( d == null ) ? 0 : d.intValue();
    }
    
    /** Indexed setter for property decayRate.
     * @param index Index of the property.
     * @param decayRate New value of the property at <CODE>index</CODE>.
     */
    public void setDecayRate(int index, int decayRate) {
        addIndexed(index, "Fader", "DECAYRATE", new Integer(decayRate), true);
    }
    
    public static  class FadeAction extends AbstractAction {
        private FadeTracker tracker;
        private int referenceNumber;
        
        public FadeAction(FadeTracker tracker, int referenceNumber) {
            this.putValue( "NAME", "Adjustment Fade");
            this.tracker = tracker;
            this.referenceNumber = referenceNumber;
        }
        
        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent event) {
            BattleEvent be = (BattleEvent) event.getSource();
            
            // Fade the appropriate adjustable
            int index = tracker.getReferenceNumberIndex(referenceNumber);
            if ( tracker.fadeAdjustment(be, tracker.getSourceAbility(index), false) > 0) {
            
                // Schedule the next fade action
                be.addUndoableEvent( tracker.setupNextFadeEvent(index, Battle.currentBattle.getTime()));
                if ( Preferences.getBooleanValue("AdjustmentFadeMessage") == true ) {
                    be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( tracker.getSourceTarget(index).getName() + " " + tracker.getAdjustmentType(index) + " to " + tracker.getTarget().getName() + " will fade again at " + tracker.getNextTime(index).toString() + ".", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( tracker.getSourceTarget(index).getName() + " " + tracker.getAdjustmentType(index) + " to " + tracker.getTarget().getName() + " will fade again at " + tracker.getNextTime(index).toString() + ".", BattleEvent.MSG_ABILITY)); // .addMessage( tracker.getSourceTarget(index).getName() + " " + tracker.getAdjustmentType(index) + " to " + tracker.getTarget().getName() + " will fade again at " + tracker.getNextTime(index).toString() + ".", BattleEvent.MSG_ABILITY);
                }
            }
        }
    }
    
    public class AddFadeUndoable implements Undoable, Serializable {
        private FadeTracker tracker;
        private  Ability sourceAbility;
        private String adjustmentType;
        private double amount;
        private Chronometer startTime;
        private long interval;
        private int rate;
        private int referenceNumber;
        
        public AddFadeUndoable(FadeTracker tracker, int referenceNumber, Ability sourceAbility, String adjustmentType, double amount, Chronometer startTime, long interval, int rate ) {
            this.tracker = tracker;
            this.sourceAbility = sourceAbility;
            this.adjustmentType = adjustmentType;
            this.amount = amount;
            this.startTime = startTime;
            this.interval = interval;
            this.rate = rate;
            this.referenceNumber = referenceNumber;
        }
        
        public void undo() {
            int index = tracker.findIndexed("Fader", "SOURCEABILITY", sourceAbility);
            if ( index != -1 ) {
                tracker.removeAllIndexed(index, "Fader");
            }
        }
        
        public void redo() {
            int index = tracker.findIndexed("Fader", "SOURCEABILITY", sourceAbility);
            if ( index == -1 ) {
                // We have to create a new one...
                index = tracker.createIndexed("Fader", "SOURCEABILITY", sourceAbility);
            }
            tracker.setAdjustmentType(index, adjustmentType);
            tracker.setAmount(index, amount);
            tracker.setDecayInterval(index, interval);
            tracker.setDecayRate(index, rate);
            
            tracker.setStartTime(index, startTime);
            
            tracker.setReferenceNumber(index, referenceNumber);
        }
    }
    
    public class RemoveFadeUndoable implements Undoable, Serializable {
        private FadeTracker tracker;
        private  Ability sourceAbility;
        private String adjustmentType;
        private double amount;
        private Chronometer startTime;
        private long interval;
        private int rate;
        private Target sourceTarget;
        private int referenceNumber;
        
        public RemoveFadeUndoable(FadeTracker tracker, int referenceNumber, Target sourceTarget, Ability sourceAbility, String adjustmentType, double amount, Chronometer startTime, long interval, int rate ) {
            this.tracker = tracker;
            this.sourceTarget = sourceTarget;
            this.sourceAbility = sourceAbility;
            this.adjustmentType = adjustmentType;
            this.amount = amount;
            this.startTime = startTime;
            this.interval = interval;
            this.rate = rate;
            this.referenceNumber = referenceNumber;
        }
        
        public void redo() {
            int index = tracker.findIndexed("Fader", "SOURCEABILITY", sourceAbility);
            if ( index != -1 ) {
                tracker.removeAllIndexed(index, "Fader");
            }
        }
        
        public void undo() {
            int index = tracker.findIndexed("Fader", "SOURCEABILITY", sourceAbility);
            if ( index == -1 ) {
                // We have to create a new one...
                index = tracker.createIndexed("Fader", "SOURCEABILITY", sourceAbility);
            }
            tracker.setAdjustmentType(index, adjustmentType);
            tracker.setAmount(index, amount);
            tracker.setDecayInterval(index, interval);
            tracker.setDecayRate(index, rate);
            tracker.setSourceTarget(index, sourceTarget);
            
            tracker.setStartTime(index, startTime);
            
            tracker.setReferenceNumber(index, referenceNumber);
        }
    }
    
    public class SetFadeAmountUndoable implements Undoable, Serializable {
        private FadeTracker tracker;
        private int index;
        private double oldAmount, newAmount;
        
        public SetFadeAmountUndoable(FadeTracker tracker, int index, double oldAmount, double newAmount) {
            this.tracker = tracker;
            this.index = index;
            this.oldAmount = oldAmount;
            this.newAmount = newAmount;
        }
        
        public void undo() {
            if ( index != -1 ) {
                // We have to create a new one...
                tracker.setAmount(index, oldAmount);
            }
        }
        
        public void redo() {
            if ( index != -1 ) {
                // We have to create a new one...
                tracker.setAmount(index, newAmount);
            }
        }
    }
    
    public class RemoveFadeEventUndoable implements Undoable, Serializable {
        int index;
        BattleEvent be;
        
        public RemoveFadeEventUndoable(int index, BattleEvent be) {
            this.be = be;
            this.index = index;
        }

        public void undo() {
            Battle.currentBattle.addDelayedEvent(be);
            addIndexed(index, "Fader", "FADEEVENT", be, true);
        }

        public void redo() {
            Battle.currentBattle.removeDelayedEvent(be);
            removeIndexed(index, "Fader", "FADEEVENT");
        }
    }
}
