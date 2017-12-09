/*
 * effectAdjustment.java
 *
 * Created on September 8, 2001, 9:45 PM
 */

package champions.powers;

import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.Characteristic;
import champions.Chronometer;
import champions.Effect;
import champions.FadeTracker;
import champions.Target;
import champions.exception.BattleEventException;
import champions.interfaces.ChampionsConstants;
import champions.Preferences;
import champions.battleMessage.StatCPChangeBattleMessage;
import champions.battleMessage.StatChangeType;




/**
 *
 * @author  twalker
 * @version 
 */
public class effectAid extends Effect implements ChampionsConstants {  
    /** Creates new effectAdjustment */
    public effectAid(Object adjustable, Target source, Ability sourceAbility, double adjustmentAmount, int maxAmount, long decayInterval, int decayRate) {
        super( "AidEffect", "INSTANT", false);
        setAdjustable(adjustable);
        setSource(source);
        setSourceAbility(sourceAbility);
        setMaximumAid(maxAmount);
        setDecayInterval(decayInterval);
        setDecayRate(decayRate);
        
        // This is the subeffect that does the actual work of making the adjustment.
        // The adjustmentAmount should be bounded by the maximum Aid amount based upon the Source/Target
        // condition.
        if ( adjustable instanceof Ability ) {
            addAidSubeffect("Aid", "ABILITY", adjustable, adjustmentAmount);
        }
        else {
            addAidSubeffect("Aid", "STATCP", ((Characteristic)adjustable).getName(), adjustmentAmount);
        }
    }
    
    
    
    public boolean addEffect(BattleEvent be, Target target) throws BattleEventException {
        // In addition to applying the subeffect (via the super() ), the fade timers must be added.
        Object adjustable = getAdjustable();
        long decayInterval = getDecayInterval();
        int decayRate = getDecayRate();
        
        double adjustmentAmount = getAdjustmentAmount();
        
        Chronometer startTime = (Chronometer)Battle.getCurrentBattle().getTime().clone();
        
        if ( adjustable instanceof Characteristic ) {            
            // Target Object is Stat, with Stat metric
            String stat = ((Characteristic)adjustable).getName();
            
            // Check to make sure that target has stat
            if ( target == null || target.hasStat(stat) == false ) return false;
            
            FadeTracker ft = target.getFadeTracker(adjustable);
            
            double currentAdjustedAmount = 0;
            
            if ( ft != null ) {
                currentAdjustedAmount = ft.getAidAdjustment();
            }
            
            if ( stat.equals("PD") || stat.equals("ED") ) {
                // Cut defense adjustments in half...
                adjustmentAmount = Math.round( adjustmentAmount / 2 );
            }
            
            if ( adjustmentAmount + currentAdjustedAmount > getMaximumAid() ) {
                adjustmentAmount = Math.max(getMaximumAid() - currentAdjustedAmount, 0);
            }
            
            if ( adjustmentAmount > 0 ) {
                setAdjustmentAmount( adjustmentAmount );
                
                target.createTargetFade(be, adjustable, getSource(), getSourceAbility(), ChampionsConstants.AID, adjustmentAmount, startTime, decayInterval, decayRate);
                
                super.addEffect(be,target);
              
                
                if ( decayRate > 0 && Preferences.getBooleanValue("AdjustmentFadeMessage") == true ) {
                    FadeTracker tracker = target.getFadeTracker(adjustable);
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
            
            if ( adjustmentAmount + currentAdjustedAmount > getMaximumAid() ) {
                adjustmentAmount = Math.max(getMaximumAid() - currentAdjustedAmount, 0);
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
        
        return true;
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
    public Object getAdjustable() {
        return getValue("Effect.TARGETOBJECT");
    }
    
    /** Setter for property adjustable.
     * @param adjustable New value of property adjustable.
     */
    public void setAdjustable(Object adjustable) {
        add("Effect.TARGETOBJECT", adjustable, true);
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
    public int getMaximumAid() {
        Integer i = getIntegerValue("Effect.MAXIMUMAID");
        return ( i == null ) ? -1 : i.intValue();
    }
    
    /** Setter for property maximumAid.
     * @param maximumAid New value of property maximumAid.
     */
    public void setMaximumAid(int maximumAid) {
        add("Effect.MAXIMUMAID", new Integer(maximumAid), true);
    }
    
}
