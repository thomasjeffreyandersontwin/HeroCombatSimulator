/*
 * effectAidTracker.java
 *
 * Created on March 6, 2002, 10:33 PM
 */

package champions.powers;


import champions.Ability;
import champions.AdjustmentList;
import champions.BattleEvent;
import champions.CompoundUndoable;
import champions.Effect;
import champions.LinkedEffect;
import champions.Target;
import champions.adjustmentPowers.Adjustable;
import champions.exception.BattleEventException;
import champions.interfaces.IndexIterator;

import champions.interfaces.Undoable;
import java.io.Serializable;



/**
 *
 * @author  Trevor Walker
 * @version
 */
public class effectAbsorption extends LinkedEffect {
    
    /** Creates new effectAidTracker */
    public effectAbsorption(Ability ability, AdjustmentList adjustmentList) {
        super(ability.getName(), "LINKED", false);
        setAbility(ability);
        setAdjustmentList(adjustmentList);
        setHidden(false);
    }
    
    public void predefense(BattleEvent be, Effect effect, Target source, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget)
    throws BattleEventException {
        String absorbType = getAbility().getStringValue("Power.DAMAGETYPE");
        
        
        // Find the Total Body
        int totalBody = 0;
        int effectAlreadyAbsorbedAmount = 0;
        int eindex = 0;
        
        IndexIterator ii = effect.getSubeffectIterator();
        while ( ii.hasNext() ) {
            eindex = ii.nextIndex();
            if ( effect.getSubeffectEffectType(eindex).equals(DAMAGE)  && effect.getSubeffectVersusObject(eindex).equals("BODY") ) {
                String defense = effect.getSubeffectDefenseType(eindex);
                if ( (absorbType.equals("Energy") && (defense.equals("ED") || defense.equals("rED")))
                || (absorbType.equals("Physical") && (defense.equals("PD") || defense.equals("rPD"))) ) {
                    // This is a Body effect, so try to absorb it...
                    int effectBody = (int)Math.round(effect.getSubeffectValue(eindex) );
                    effectAlreadyAbsorbedAmount = (int)Math.round(effect.getSubeffectAbsorbedAmount(eindex) );
                    totalBody += effectBody - effectAlreadyAbsorbedAmount;
                    break;
                }
            }
        }
        
        if ( totalBody >  0 ) {
            int maxAmountAbsorbed = Math.min( (getMaximumAbsorptionForPhase() - getAmountAbsorbedThisPhase()), totalBody );
            
            //powerAbsorption.setAbsorptionAbsorbedBody(be,targetGroup,targetReferenceNumber,getAbility(), totalBody, eindex);
            
            // Store the realAmountAbsorbed somewhere in the BattleEvent so defenses which are "only up to amount absorbed"
            // can use it...
            powerAbsorption.addAbsorptionAmountAbsorbed(be, targetGroup, targetReferenceNumber, getAbility(), maxAmountAbsorbed);
            
            // Store the Amount absorbed from this effect, in case there are multiple absorption operating.
            // eindex should still contain the appropriate index for the subeffect.
            effect.setSubeffectAbsorbedAmount(eindex, effect.getSubeffectAbsorbedAmount(eindex) + maxAmountAbsorbed);
        }
    }
    
    
    public void postdefense(BattleEvent be, Effect effect, Target source, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget) throws BattleEventException {
    
        int totalBody = powerAbsorption.getAbsorptionAmountAbsorbed(be,targetGroup,targetReferenceNumber,getAbility());
        
        if ( totalBody >  0 ) {
            // Figure out how much to absorb...
            int maxAmountAbsorbed = Math.min( (getMaximumAbsorptionForPhase() - getAmountAbsorbedThisPhase()), totalBody );
            int realAmountAbsorbed = 0;
            
            // Grab the relavent ability parameters...
            powerAbsorption power = (powerAbsorption)getAbility().getPower();
            
            int maximumAbsorb = power.getMaximumAdjustment( getAbility() );
            long decayInterval = power.getDecayInterval(getAbility());
            int decayRate = power.getDecayRate(getAbility());
            
            // Actually absorb to the appropriate abilities
            AdjustmentList al = getAdjustmentList();
            int count = al.getAdjustableCount();
            int index;
            for(index=0;index<count;index++) {
                Adjustable adjustable = al.getAdjustableObject(index);
                int percent = al.getAdjustablePercentage(index);
                
                double amount = Math.round(maxAmountAbsorbed * 100.0 / percent);
                
                // Create the Effect and apply it
                effectAid e = new effectAid(adjustable, source, getAbility(), amount, maximumAbsorb, decayInterval, decayRate);
                e.addEffect(be, getTarget() );
                
                int actualAdjustment = (int)Math.round( e.getAdjustmentAmount() );
                
                //AdjustmentPower power = (AdjustmentPower)getAbility().getPower();
                if ( power.getAdjustmentLevel(getAbility()) > ADJ_SINGLE_ADJUSTMENT ) {
                    realAmountAbsorbed = Math.max( realAmountAbsorbed, actualAdjustment);
                }
                else {
                    // This is a single/variable, so add up the amount adjusted
                    realAmountAbsorbed += actualAdjustment;
                }
            }
            

            
            // Store the amount absorbed for this phase
            Undoable u = setAmountAbsorbedThisPhase(getAmountAbsorbedThisPhase() + realAmountAbsorbed);
            be.addUndoableEvent(u);
        }


    }
    
    /** Getter for property adjustmentList.
     * @return Value of property adjustmentList.
     */
    public AdjustmentList getAdjustmentList() {
        return ( AdjustmentList) getValue("Effect.ADJUSTMENTLIST");
    }
    
    /** Setter for property adjustmentList.
     * @param adjustmentList New value of property adjustmentList.
     */
    public Undoable setAdjustmentList(AdjustmentList newList) {
        AdjustmentList oldList = getAdjustmentList();
        
        Undoable undoable = null;
        
        if ( oldList != newList ) {
            if ( oldList != null ) {
                // Need to deduct points from old adjusted abilities/stats if they are different from the newList,
                // creating undoable if necessary.
            }
            
            add("Effect.ADJUSTMENTLIST", newList, true, false);
            
            if ( newList != null ) {
                // Might need to do something here...maybe
            }
            
            // Create Undoable....
            if ( undoable == null ) {
                undoable = new effectAbsorption.AbsorptionALUndoable(this,  oldList, newList);
            }
            else {
                CompoundUndoable cu = new CompoundUndoable();
                cu.addUndoable(undoable);
                cu.addUndoable( new effectAbsorption.AbsorptionALUndoable(this,  oldList, newList) );
                undoable = cu;
            }
        }
        
        return undoable;
    }

    
    /** Getter for property maximumAbsorptionForPhase.
     * @return Value of property maximumAbsorptionForPhase.
     */
    public int getMaximumAbsorptionForPhase() {
        Integer i = getIntegerValue("Effect.MAXABSORBFORPHASE");
        return (i==null)?0:i.intValue();
    }
    
    /** Setter for property maximumAbsorptionForPhase.
     * @param maximumAbsorptionForPhase New value of property maximumAbsorptionForPhase.
     */
    public Undoable setMaximumAbsorptionForPhase(int maximumAbsorptionForPhase) {
        int oldAmount = getMaximumAbsorptionForPhase();
        
        add("Effect.MAXABSORBFORPHASE", new Integer(maximumAbsorptionForPhase), true);
        
        return new effectAbsorption.MaximumAbsorbedUndoable(this, oldAmount, maximumAbsorptionForPhase);
    }
    
    /** Getter for property amountAbsorbedThisPhase.
     * @return Value of property amountAbsorbedThisPhase.
     */
    public int getAmountAbsorbedThisPhase() {
        Integer i = getIntegerValue("Effect.AMOUNTABSORBEDFORPHASE");
        return (i==null)?0:i.intValue();
    }
    
    /** Setter for property amountAbsorbedThisPhase.
     * @param amountAbsorbedThisPhase New value of property amountAbsorbedThisPhase.
     */
    public Undoable setAmountAbsorbedThisPhase(int amountAbsorbedThisPhase) {
        int oldAmount = getAmountAbsorbedThisPhase();
        
        add("Effect.AMOUNTABSORBEDFORPHASE", new Integer(amountAbsorbedThisPhase), true);
        
        return new effectAbsorption.AmountAbsorbedUndoable(this, oldAmount, amountAbsorbedThisPhase);
    }
    
    public class AbsorptionALUndoable implements Undoable, Serializable {
        
        /** Holds value of property tracker. */
        private effectAbsorption effect;
        
        /** Holds value of property oldList. */
        private AdjustmentList oldList;
        
        /** Holds value of property newList. */
        private AdjustmentList newList;
        
        /** Creates new AidTrackerALUndoable */
        public AbsorptionALUndoable(effectAbsorption effect, AdjustmentList oldList, AdjustmentList newList) {
            this.effect = effect;
            this.oldList = oldList;
            this.newList = newList;
        }
        
        public void redo() {
            effect.add("Effect.ADJUSTMENTLIST", newList, true, false);
            
        }
        
        public void undo() {
            effect.add("Effect.ADJUSTMENTLIST", oldList, true, false);
        }
    }
    
    public class AmountAbsorbedUndoable implements Undoable, Serializable {
        
        /** Holds value of property effect. */
        private Effect effect;
        
        /** Holds value of property oldAmount. */
        private int oldAmount;
        
        /** Holds value of property newAmount. */
        private int newAmount;
        
        public AmountAbsorbedUndoable(effectAbsorption effect, int oldAmount, int newAmount) {
            setEffect(effect);
            setOldAmount(oldAmount);
            setNewAmount(newAmount);
        }
        
        public void redo() {
            effect.add("Effect.AMOUNTABSORBEDFORPHASE", new Integer(newAmount), true);
        }
        
        public void undo() {
            effect.add("Effect.AMOUNTABSORBEDFORPHASE", new Integer(oldAmount), true);
        }
        
        /** Getter for property effect.
         * @return Value of property effect.
         */
        public Effect getEffect() {
            return effect;
        }
        
        /** Setter for property effect.
         * @param effect New value of property effect.
         */
        public void setEffect(Effect effect) {
            this.effect = effect;
        }
        
        /** Getter for property oldAmount.
         * @return Value of property oldAmount.
         */
        public int getOldAmount() {
            return oldAmount;
        }
        
        /** Setter for property oldAmount.
         * @param oldAmount New value of property oldAmount.
         */
        public void setOldAmount(int oldAmount) {
            this.oldAmount = oldAmount;
        }
        
        /** Getter for property newAmount.
         * @return Value of property newAmount.
         */
        public int getNewAmount() {
            return newAmount;
        }
        
        /** Setter for property newAmount.
         * @param newAmount New value of property newAmount.
         */
        public void setNewAmount(int newAmount) {
            this.newAmount = newAmount;
        }
        
    }
    
    public class MaximumAbsorbedUndoable implements Undoable, Serializable {
        
        /** Holds value of property effect. */
        private Effect effect;
        
        /** Holds value of property oldAmount. */
        private int oldAmount;
        
        /** Holds value of property newAmount. */
        private int newAmount;
        
        public MaximumAbsorbedUndoable(effectAbsorption effect, int oldAmount, int newAmount) {
            setEffect(effect);
            setOldAmount(oldAmount);
            setNewAmount(newAmount);
        }
        
        public void redo() {
            effect.add("Effect.MAXABSORBFORPHASE", new Integer(newAmount), true);
        }
        
        public void undo() {
            effect.add("Effect.MAXABSORBFORPHASE", new Integer(oldAmount), true);
        }
        
        /** Getter for property effect.
         * @return Value of property effect.
         */
        public Effect getEffect() {
            return effect;
        }
        
        /** Setter for property effect.
         * @param effect New value of property effect.
         */
        public void setEffect(Effect effect) {
            this.effect = effect;
        }
        
        /** Getter for property oldAmount.
         * @return Value of property oldAmount.
         */
        public int getOldAmount() {
            return oldAmount;
        }
        
        /** Setter for property oldAmount.
         * @param oldAmount New value of property oldAmount.
         */
        public void setOldAmount(int oldAmount) {
            this.oldAmount = oldAmount;
        }
        
        /** Getter for property newAmount.
         * @return Value of property newAmount.
         */
        public int getNewAmount() {
            return newAmount;
        }
        
        /** Setter for property newAmount.
         * @param newAmount New value of property newAmount.
         */
        public void setNewAmount(int newAmount) {
            this.newAmount = newAmount;
        }
        
    }
}
