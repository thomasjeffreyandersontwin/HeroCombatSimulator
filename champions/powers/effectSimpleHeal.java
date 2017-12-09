/*
 * effectAdjustment.java
 *
 * Created on September 8, 2001, 9:45 PM
 */

package champions.powers;


import champions.*;
import champions.interfaces.*;
import champions.exception.*;

/**
 *
 * @author  twalker
 * @version
 */
public class effectSimpleHeal extends Effect implements ChampionsConstants {
    /** Creates new effectAdjustment */
    public effectSimpleHeal(Target source, Ability sourceAbility, double stunAmount, double bodyAmount, int stunMax, int bodyMax) {
        super( "HealEffect", "INSTANT", false);
        setSource(source);
        setSourceAbility(sourceAbility);
        setMaximumBodyHeal(bodyMax);
        setMaximumStunHeal(stunMax);
        
        addHealSubeffect("HealBody", "STAT", "BODY", bodyAmount);
        addHealSubeffect("HealStun", "STAT", "STUN", stunAmount);
    }
    
    
    
    public boolean addEffect(BattleEvent be, Target target) throws BattleEventException {
        
        double bodyAmount = getBodyHealAmount();
        
        if ( bodyAmount > getMaximumBodyHeal() ) {
            bodyAmount = getMaximumBodyHeal();
        }
        
        if ( bodyAmount > 0 ) {
            setBodyHealAmount( bodyAmount );
        }
        
        
        if ( bodyAmount > getMaximumBodyHeal() ) {
            bodyAmount = getMaximumBodyHeal();
        }
        
        setBodyHealAmount( bodyAmount );
        
        double stunAmount = getStunHealAmount();
        if ( stunAmount > getMaximumStunHeal() ) {
            stunAmount = getMaximumStunHeal();
        }
        
        setStunHealAmount( stunAmount );
        
        super.addEffect(be,target);
        
        return true;
    }
    
    /** Getter for property adjustmentAmount.
     * @return Value of property adjustmentAmount.
     */
    public double getStunHealAmount() {
        int index = getSubeffectIndex("HealStun");
        return index != -1 ? getSubeffectValue(index) : 0;
    }
    
    /** Setter for property adjustmentAmount.
     * @param adjustmentAmount New value of property adjustmentAmount.
     */
    public void setStunHealAmount(double adjustmentAmount) {
        int index = getSubeffectIndex("HealStun");
        if ( index != -1  ) {
            setSubeffectValue(index, adjustmentAmount);
        }
    }
    
    /** Getter for property adjustmentAmount.
     * @return Value of property adjustmentAmount.
     */
    public double getBodyHealAmount() {
        int index = getSubeffectIndex("HealBody");
        return index != -1 ? getSubeffectValue(index) : 0;
    }
    
    /** Setter for property adjustmentAmount.
     * @param adjustmentAmount New value of property adjustmentAmount.
     */
    public void setBodyHealAmount(double adjustmentAmount) {
        int index = getSubeffectIndex("HealBody");
        if ( index != -1  ) {
            setSubeffectValue(index, adjustmentAmount);
        }
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
    
    /** Getter for property maximumHeal.
     * @return Value of property maximumHeal.
     */
    public int getMaximumBodyHeal() {
        Integer i = getIntegerValue("Effect.MAXIMUMBODY");
        return ( i == null ) ? -1 : i.intValue();
    }
    
    /** Setter for property maximumHeal.
     * @param maximumHeal New value of property maximumHeal.
     */
    public void setMaximumBodyHeal(int maximumHeal) {
        add("Effect.MAXIMUMBODY", new Integer(maximumHeal), true);
    }
    
    /** Getter for property maximumHeal.
     * @return Value of property maximumHeal.
     */
    public int getMaximumStunHeal() {
        Integer i = getIntegerValue("Effect.MAXIMUMSTUN");
        return ( i == null ) ? -1 : i.intValue();
    }
    
    /** Setter for property maximumHeal.
     * @param maximumHeal New value of property maximumHeal.
     */
    public void setMaximumStunHeal(int maximumHeal) {
        add("Effect.MAXIMUMSTUN", new Integer(maximumHeal), true);
    }
    
}
