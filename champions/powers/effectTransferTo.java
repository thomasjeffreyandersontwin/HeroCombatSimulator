/*
 * effectAdjustment.java
 *
 * Created on September 8, 2001, 9:45 PM
 */

package champions.powers;


import champions.*;
import champions.adjustmentPowers.Adjustable;
import champions.exception.BattleEventException;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.IndexIterator;
import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author  twalker
 * @version
 */
public class effectTransferTo extends Effect implements ChampionsConstants {
    /** Creates new effectAdjustment */
    public effectTransferTo(AdjustmentList adjustmentList, Target source, Ability sourceAbility, double totalAdjustmentAmount, int maxAmount, long decayInterval, int decayRate) {
        super( "AidEffect", "INSTANT", false);
        setAdjustmentList(adjustmentList);
        setSource(source);
        setSourceAbility(sourceAbility);
        setMaximumTransfer(maxAmount);
        setDecayInterval(decayInterval);
        setDecayRate(decayRate);
        setTotalAdjustmentAmount(totalAdjustmentAmount);
        
        
    }
    
    
    
    public boolean addEffect(BattleEvent be, Target target) throws BattleEventException {
        // In addition to applying the subeffect (via the super() ), the fade timers must be added.
        
        long decayInterval = getDecayInterval();
        int decayRate = getDecayRate();
        
        IndexIterator ii = getSubeffectIterator();
        while ( ii.hasNext() ) {
            int sindex = ii.nextIndex();
            
            Object adjustable = getSubeffectVersusObject(sindex);
            
            double adjustmentAmount = getSubeffectValue(sindex);
            
            String versusType = getSubeffectVersusType(sindex);
            
            Chronometer startTime = (Chronometer)Battle.getCurrentBattle().getTime().clone();
            
            if ( versusType.equals("STATCP")) {
                // Target Object is Stat, with Stat metric
                String stat = (String)adjustable;
                Characteristic characteristic = new Characteristic(stat);
                
                // Check to make sure that target has stat
                if ( target == null || target.hasStat(stat) == false ) return false;
                
                FadeTracker ft = target.getFadeTracker(characteristic);
                
                double currentAdjustedAmount = 0;
                
                if ( ft != null ) {
                    currentAdjustedAmount = ft.getAidAdjustment();
                }
                
                if ( stat.equals("PD") || stat.equals("ED") ) {
                    // Cut defense adjustments in half...
                    adjustmentAmount = Math.round( adjustmentAmount / 2 );
                }
                
                if ( adjustmentAmount + currentAdjustedAmount > getMaximumTransfer() ) {
                    adjustmentAmount = Math.max(getMaximumTransfer() - currentAdjustedAmount, 0);
                }
                
                if ( adjustmentAmount > 0 ) {
                    setAdjustmentAmount( adjustmentAmount ); 
                    
                    target.createTargetFade(be, characteristic, getSource(), getSourceAbility(), ChampionsConstants.AID, adjustmentAmount, startTime, decayInterval, decayRate);

                    
                    super.addEffect(be,target);
                    
                    if ( decayRate > 0 && Preferences.getBooleanValue("AdjustmentFadeMessage") == true ) {
                        FadeTracker tracker = target.getFadeTracker(characteristic);
                        int index = tracker.getSourceAbilityIndex(getSourceAbility());
                        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( tracker.getSourceTarget(index).getName() + " " + tracker.getAdjustmentType(index) + " to " + tracker.getTarget().getName() + " will begin to fade at " + tracker.getNextTime(index).toString() + ".", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( tracker.getSourceTarget(index).getName() + " " + tracker.getAdjustmentType(index) + " to " + tracker.getTarget().getName() + " will begin to fade at " + tracker.getNextTime(index).toString() + ".", BattleEvent.MSG_ABILITY)); // .addMessage( tracker.getSourceTarget(index).getName() + " " + tracker.getAdjustmentType(index) + " to " + tracker.getTarget().getName() + " will begin to fade at " + tracker.getNextTime(index).toString() + ".", BattleEvent.MSG_ABILITY);
                    }
                }
            }
            else {
                // Target Object is Ability
                Ability ability = (Ability)adjustable;
                
                // Check to make sure that target has stat
                if ( target == null || target.hasAbility(ability) == false ) return false;
                
                FadeTracker ft = target.getFadeTracker(adjustable);
                
                double currentAdjustedAmount = 0;
                
                if ( ft != null ) {
                    currentAdjustedAmount = ft.getAidAdjustment();
                }
                
                if ( ability.isDefense() ) {
                    // Cut defense adjustments in half...
                    adjustmentAmount = Math.round( adjustmentAmount / 2 );
                }
                
                if ( adjustmentAmount + currentAdjustedAmount > getMaximumTransfer() ) {
                    adjustmentAmount = Math.max(getMaximumTransfer() - currentAdjustedAmount, 0);
                }
                
                if ( adjustmentAmount > 0 ) {
                    setAdjustmentAmount( adjustmentAmount );
                    
                    target.createTargetFade(be, adjustable, getSource(), getSourceAbility(), ChampionsConstants.AID, adjustmentAmount, startTime, decayInterval, decayRate);
                    
                    
                    super.addEffect(be,target);
                    
                    if ( Preferences.getBooleanValue("AdjustmentFadeMessage") == true ) {
                        FadeTracker tracker = target.getFadeTracker(adjustable);
                        int index = tracker.getSourceAbilityIndex(getSourceAbility());
                        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( tracker.getSourceTarget(index).getName() + " " + tracker.getAdjustmentType(index) + " to " + tracker.getTarget().getName() + " will begin to fade at " + tracker.getNextTime(index).toString() + ".", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( tracker.getSourceTarget(index).getName() + " " + tracker.getAdjustmentType(index) + " to " + tracker.getTarget().getName() + " will begin to fade at " + tracker.getNextTime(index).toString() + ".", BattleEvent.MSG_ABILITY)); // .addMessage( tracker.getSourceTarget(index).getName() + " " + tracker.getAdjustmentType(index) + " to " + tracker.getTarget().getName() + " will begin to fade at " + tracker.getNextTime(index).toString() + ".", BattleEvent.MSG_ABILITY);
                    }
                }
                
            }
        }
        
        return true;
    }
    
    private void buildSubeffects() {
        removeAll("Subeffect");
        
        AdjustmentList al = getAdjustmentList();
        int count = al.getAdjustableCount();
        
        Target source = getSource();
        double total = getTotalAdjustmentAmount();
        double maximum = getMaximumTransfer();
        int[] assigned = new int[count];
        int[] remaining = new int[count];
        
        for ( int index = 0; index < count; index++ ){
            Adjustable adjustable = al.getAdjustableObject(index);
            FadeTracker ft = source.getFadeTracker(adjustable);
            
            double currentAdjustedAmount = 0;
            if ( ft != null ) currentAdjustedAmount = ft.getAidAdjustment();
            
            if ( currentAdjustedAmount >= maximum ) {
                remaining[index] = 0;
            }
            else {
                remaining[index] = (int)(maximum - currentAdjustedAmount);
            }
        }
        
        boolean done = false;
        int loops = 0;
        while ( ! done && loops < 100) {
            int startTotal = (int)total;
            for(int index=0; index < count; index++) {
                int cp = (int)Math.round(startTotal / count );
                if ( assigned[index] + cp > remaining[index] ) {
                    cp = remaining[index] - assigned[index];
                }
                
                // Make sure we aren't past total...
                if ( cp > total ) {
                    cp = (int)total;
                }
                
                assigned[index] += cp;
                total -= cp;
            }
            if ( total <= 0 ) {
                done = true;
            }
            loops++;
        }
        
        int totalRemaining = 0;
        for (int index=0; index<count; index++) {
            totalRemaining += remaining[index] - assigned[index];
            // This is the subeffect that does the actual work of making the adjustment.
            // The adjustmentAmount should be bounded by the maximum Aid amount based upon the Source/Target
            // condition.
            Adjustable adjustable = al.getAdjustableObject(index);
            if ( adjustable instanceof Ability ) {
                addAidSubeffect("Aid", "ABILITY", adjustable, assigned[index]);
            }
            else {
                addAidSubeffect("Aid", "STATCP", ((Characteristic)adjustable).getName(), assigned[index]);
            }
        }
        
        // Update the total character points value
        add("Effect.AVAILABLECP", new Integer(totalRemaining), true);
    }
    
    /** Getter for property decayRate.
     * @return Value of property decayRate.
     */
    public int getDecayRate() {
        Integer i = getIntegerValue("Effect.DECAYRATE");
        return ( i == null ) ? -1 : i.intValue();
    }
    
    /** Setter for property decayRate.
     * @param decayRate New value of property decayRate.
     */
    public void setDecayRate(int decayRate) {
        add("Effect.DECAYRATE", new Integer(decayRate), true);
    }
    
    /** Getter for property decayInterval.
     * @return Value of property decayInterval.
     */
    public long getDecayInterval() {
        Long i = getLongValue("Effect.DECAYINTERVAL");
        return ( i == null ) ? -1 : i.longValue();
    }
    
    /** Setter for property decayInterval.
     * @param decayInterval New value of property decayInterval.
     */
    public void setDecayInterval(long decayInterval) {
        add("Effect.DECAYINTERVAL", new Long(decayInterval), true);
    }
    
    /** Getter for property decayInterval.
     * @return Value of property decayInterval.
     */
    public double getTotalAdjustmentAmount() {
        Double i = getDoubleValue("Effect.TOTALADJUSTMENTAMOUNT");
        return ( i == null ) ? -1 : i.doubleValue();
    }
    
    /** Setter for property decayInterval.
     * @param decayInterval New value of property decayInterval.
     */
    public Undoable setTotalAdjustmentAmount(double totalAdjustmentAmount) {
        double oldValue = getTotalAdjustmentAmount();
        
        add("Effect.TOTALADJUSTMENTAMOUNT", new Double(totalAdjustmentAmount), true);
        
        buildSubeffects();
        
        return new effectTransferTo.TotalAdjustmentUndoable(this, oldValue, totalAdjustmentAmount);
    }
    
    /** Getter for property adjustmentAmount.
     * @return Value of property adjustmentAmount.
     */
    public double getAdjustmentAmount() {
        Double i = getIndexedDoubleValue(0, "Subeffect", "VALUE");
        return ( i == null ) ? 0 : i.doubleValue();
    }
    
    /** Setter for property adjustmentAmount.
     * @param adjustmentAmount New value of property adjustmentAmount.
     */
    public void setAdjustmentAmount(double adjustmentAmount) {
        addIndexed(0,"Subeffect","VALUE", new Double(adjustmentAmount), true);
    }
    
    /** Getter for property sourceAbility.
     * @return Value of property sourceAbility.
     */
    public Target getSource() {
        Object o = getValue("Effect.SOURCE");
        return ( o == null ) ? null : (Target)o;
    }
    
    /** Setter for property sourceAbility.
     * @param sourceAbility New value of property sourceAbility.
     */
    public void setSource(Target source) {
        add("Effect.SOURCE", source, true);
    }
    
    /** Getter for property sourceAbility.
     * @return Value of property sourceAbility.
     */
    public Ability getSourceAbility() {
        Object o = getValue("Effect.SOURCEABILITY");
        return ( o == null ) ? null : (Ability)o;
    }
    
    /** Setter for property sourceAbility.
     * @param sourceAbility New value of property sourceAbility.
     */
    public void setSourceAbility(Ability sourceAbility) {
        add("Effect.SOURCEABILITY", sourceAbility, true);
    }
    
    /** Getter for property adjustable.
     * @return Value of property adjustable.
     */
    public AdjustmentList getAdjustmentList() {
        return (AdjustmentList)getValue("Effect.ADJUSTMENTLIST");
    }
    
    /** Setter for property adjustable.
     * @param adjustable New value of property adjustable.
     */
    public void setAdjustmentList(AdjustmentList adjustmentList) {
        add("Effect.ADJUSTMENTLIST", adjustmentList, true);
    }
    
    /** Getter for property fadeBelowBase.
     * @return Value of property fadeBelowBase.
     */
    public boolean getFadeBelowBase() {
        return getBooleanValue("Effect.FADEBELOWBASE");
    }
    
    /** Setter for property fadeBelowBase.
     * @param fadeBelowBase New value of property fadeBelowBase.
     */
    public void setFadeBelowBase(boolean fadeBelowBase) {
        add("Effect.FADEBELOWBASE", fadeBelowBase?"TRUE":"FALSE", true);
    }
    
    public void setAidLimit(double limit) {
        setLimit(0, limit);
    }
    
    /** Getter for property maximumAid.
     * @return Value of property maximumAid.
     */
    public int getMaximumTransfer() {
        Integer i = getIntegerValue("Effect.MAXIMUMTRANSFER");
        return ( i == null ) ? -1 : i.intValue();
    }
    
    /** Setter for property maximumAid.
     * @param maximumAid New value of property maximumAid.
     */
    public void setMaximumTransfer(int maximumTransfer) {
        add("Effect.MAXIMUMTRANSFER", new Integer(maximumTransfer), true);
    }
    
    /** Returns the number of Character Points which could still be transferred before the maximum is reached.
     */
    public double getAvailableCharacterPoints() {
        // This needs to be much smarter...
        Integer i = getIntegerValue("Effect.AVAILABLECP");
        return ( i != null ) ? i.intValue() : 0;
    }
    
    public class TotalAdjustmentUndoable implements Undoable, Serializable {
        double oldValue, newValue;
        effectTransferTo ett;
        public TotalAdjustmentUndoable(effectTransferTo ett, double oldValue, double newValue) {
            this.ett = ett;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
        
        public void redo() {
            ett.add("Effect.TOTALADJUSTMENTAMOUNT", new Double(newValue), true);
            ett.buildSubeffects();
        }
        
        public void undo() {
            ett.add("Effect.TOTALADJUSTMENTAMOUNT", new Double(oldValue), true);
            ett.buildSubeffects();
        }
        
    }
}
