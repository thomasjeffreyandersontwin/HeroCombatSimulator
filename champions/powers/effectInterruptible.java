/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.*;
import champions.battleMessage.CVModiferMessage;
import champions.battleMessage.CVMultiplerMessage;
import champions.battleMessage.EffectSummaryMessage;
import champions.exception.BattleEventException;
import champions.powers.effectEntangle;

/**
 *
 * @author  unknown
 * @version
 */
public class effectInterruptible extends Effect {
    static final long serialVersionUID = -3610675105250268944L;
    
    /** Creates new effectUnconscious */
    public effectInterruptible(String effectName, Ability ability, BattleEvent interruptibleEvent, int interruptLevel, double dcvMultiplier, int dcvModifier) {
        super( effectName, "PERSISTENT", true );
        
        setInterruptibleAbility(ability);
        setInterruptLevel(interruptLevel);
        setDCVMultiplier(dcvMultiplier);
        setDCVModifier(dcvModifier);
        setInterruptibleEvent(interruptibleEvent);
        
        setEffectPriority(5);
    }
    
    
//    public boolean prephase(BattleEvent be, Target t)
//    throws BattleEventException {
//        if (getInterruptibleAbility().isActivated(t)) {
//            return false;
//        }
//        else {
//            return true;
//        }
//    }
    
    public void addDCVDefenseModifiers( CVList cvList , Ability attack) {
        double dcvMul = getDCVMultiplier();
        if ( dcvMul != 1 ) {
            cvList.addTargetCVMultiplier(getName(), dcvMul );
        }
        
        int dcvMod = getDCVModifier();
        if ( dcvMod != 0 ) {
            cvList.addTargetCVModifier(getName(), dcvMod );
        }
    }
    
    public boolean posteffect(BattleEvent be, Effect otherEffect, Target target) throws BattleEventException {
        int interruptLevel = getInterruptLevel();
        int index;
        
        boolean interrupted = false;
        
        switch ( interruptLevel ) {
            case NOT_INTERRUPTIBLE:
                break;
            case INTERRUPTIBLE_BY_DAMAGE:
                index = otherEffect.getIndexedSize( "Subeffect" ) - 1;
                
                for (; index >= 0; index --) {
                    String type;
                    double adjustedAmount;
                    
                    type = otherEffect.getSubeffectEffectType(index);
                    adjustedAmount = otherEffect.getSubeffectAdjustedAmount(index);
                    
                    if ( (type.equals("DRAIN")  || type.equals("DAMAGE")) && adjustedAmount > 0) {
                        interrupted = true;
                        break;
                    }
                }
                break;
            case INTERRUPTIBLE_BY_DAMAGE_AND_KNOCKBACK:
                index = otherEffect.getIndexedSize( "Subeffect" ) - 1;
                
                for (; index >= 0; index --) {
                    String type;
                    double adjustedAmount;
                    
                    type = otherEffect.getSubeffectEffectType(index);
                    adjustedAmount = otherEffect.getSubeffectAdjustedAmount(index);
                    
                    if ( (type.equals("DRAIN")  || type.equals("DAMAGE")) && adjustedAmount > 0) {
                        interrupted = true;
                        break;
                    }
                }
                
                if ( otherEffect instanceof effectKnockback && ((effectKnockback)otherEffect).getDistance() > 0 ) {
                    interrupted = true;
                    break;
                }
                break;
            case INTERRUPTIBLE_BY_ANY_DAMAGING_ATTACK:
                index = otherEffect.getIndexedSize( "Subeffect" ) - 1;
                
                for (; index >= 0; index --) {
                    String type;
                    double adjustedAmount;
                    
                    type = otherEffect.getSubeffectEffectType(index);
                    adjustedAmount = otherEffect.getSubeffectAdjustedAmount(index);
                    
                    if ( (type.equals("DRAIN")  || type.equals("DAMAGE")) && adjustedAmount > 0) {
                        interrupted = true;
                        break;
                    }
                }
                
                if ( otherEffect instanceof effectKnockback && ((effectKnockback)otherEffect).getDistance() > 0 ) {
                    interrupted = true;
                    break;
                }
                
                // Other Attack Types also need to be checked.  I am not sure that there is a good
                // way of doing this...currently there is no way to tell an Attack Effect from a non-attack
                // effect.
                
                // Check for Flash
                if ( otherEffect instanceof effectFlash && ((effectFlash)otherEffect).getDuration() > 0 ) {
                    interrupted = true;
                    break;
                }
                
                // Check to see if the target was grabbed
                if ( otherEffect instanceof effectGrabbed ) {
                    interrupted = true;
                    break;
                }
                // Check Entangles
                if ( otherEffect instanceof effectEntangle ) {
                    interrupted = true;
                    break;
                }
                
                break;
        }
        
        if ( interrupted ) {
            BattleEvent interruptEvent = getInterruptibleEvent();
            if ( interruptEvent.getActivationInfo().isContinuing() ) {
                be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + "'s " + getInterruptibleAbility().getName() + " was interrupted!", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + "'s " + getInterruptibleAbility().getName() + " was interrupted!", BattleEvent.MSG_ABILITY)); // .addMessage( target.getName() + "'s " + getInterruptibleAbility().getName() + " was interrupted!", BattleEvent.MSG_ABILITY);
                
                BattleEvent newBE = new BattleEvent(BattleEvent.DEACTIVATE, interruptEvent.getActivationInfo() );
                // There is currently no way to insert this deactivation inline or
                // even attach it as to the same event. 
                // For now, I will just seperate them so they get done, but long term 
                // this should be fixed...
                Battle.getCurrentBattle().addEvent(newBE);
                
                
                //Battle.getCurrentBattle().getBattleEngine().processAndEmbed(newBE, be, false);
            }
            else {
                be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + "'s activation of " + getInterruptibleAbility().getName() + " was interrupted!", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + "'s activation of " + getInterruptibleAbility().getName() + " was interrupted!", BattleEvent.MSG_ABILITY)); // .addMessage( target.getName() + "'s activation of " + getInterruptibleAbility().getName() + " was interrupted!", BattleEvent.MSG_ABILITY);
                BattleEvent newBE = new BattleEvent(BattleEvent.DEACTIVATE, interruptEvent.getActivationInfo() );
                Battle.getCurrentBattle().addEvent(newBE);
                //Battle.getCurrentBattle().getBattleEngine().processAndEmbed(newBE, be, false);
                //be.embedBattleEvent( newBE );
            }
            
            return true ;
        }
        
        return false;
    }

    public boolean addEffect(BattleEvent be, Target target) throws BattleEventException {
        boolean result = super.addEffect(be,target);
        
        if ( result ) {
            String modString = "";

            int mod = getDCVModifier();

            if ( mod != 0  ) {
                //modString = " and has a " + ChampionsUtilities.toSignedString(mod) + " DCV modifier";
                be.addBattleMessage( new CVModiferMessage(target, true, "DCV", mod));
            }
            else {
                double mult = getDCVMultiplier();
                be.addBattleMessage( new CVMultiplerMessage(target, true, "DCV", mult));
            }

            //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " is " + getName() + modString + ".", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " is " + getName() + modString + ".", BattleEvent.MSG_NOTICE)); // .addMessage(target.getName() + " is " + getName() + modString + ".", BattleEvent.MSG_NOTICE);
            be.addBattleMessage( new EffectSummaryMessage(target, this, true));
            
        }
        return result;
    }
    

    
    public void removeEffect(BattleEvent be, Target target) throws BattleEventException {
        super.removeEffect(be,target);
        BattleEvent delayEvent = getInterruptibleEvent();
        
        if ( Battle.currentBattle != null ) {
            be.addUndoableEvent( Battle.currentBattle.removeDelayedEvent( delayEvent ) );
        }
        
        Ability ability = getInterruptibleAbility();
        
        if ( ability.isDelayExclusive() ) {
            be.addCombatStateEvent(target, target.getCombatState(), CombatState.STATE_FIN);
            target.setCombatState(CombatState.STATE_FIN);
        }
        
        be.addBattleMessage( new EffectSummaryMessage(target, this, false));
        //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " is no longer " + getName() + ".", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " is no longer " + getName() + ".", BattleEvent.MSG_NOTICE)); // .addMessage(target.getName() + " is no longer " + getName() + ".", BattleEvent.MSG_NOTICE);
    }
    
    public String getDescription() {
        StringBuffer sb = new StringBuffer();
        int interruptLevel = getInterruptLevel();
        BattleEvent delayEvent = getInterruptibleEvent();
        Target source = delayEvent.getSource();
        
        sb.append( "The Ability " );
        sb.append( getInterruptibleAbility().getName() );
        if ( delayEvent.getActivationInfo().isContinuing() ) {
            sb.append( " is currently activated and is interruptible.\n\n" );
            sb.append( "It can be interrupted by " );
        }
        else {
            sb.append( " is currently activating.\n\n" );
            
            sb.append("Activation will finish at ");
            sb.append(getInterruptibleEvent().getTimeParameter());
            sb.append(".\n\n");
            
            sb.append( "The activation can be interrupted by " );
        }
        
        switch ( interruptLevel ) {
            case NOT_INTERRUPTIBLE:
                sb.append( "nothing.");
                break;
            case INTERRUPTIBLE_BY_DAMAGE:
                sb.append( "any damage or drains done to ");
                sb.append( source.getName() ) ;
                sb.append( "." );
                break;
            case INTERRUPTIBLE_BY_DAMAGE_AND_KNOCKBACK:
                sb.append( "any damage, drains, or knockback done to ");
                sb.append( source.getName() ) ;
                sb.append( "." );
                break;
            case INTERRUPTIBLE_BY_ANY_DAMAGING_ATTACK:
                sb.append( "any successful attack against ");
                sb.append( source.getName() ) ;
                sb.append( ", including damage, drains, knockback, flashes, etc." );
                break;
        }
        
        double dcvMul = getDCVMultiplier();
        int dcvMod = getDCVModifier();
        if ( dcvMul != 1 ) {
            sb.append("\n\nWhile activating ");
            sb.append( source.getName());
            sb.append( "'s DCV is at ");
            sb.append( dcvMul );
            sb.append( "."); 
        }
        else if ( dcvMod != 0 ) {
            sb.append("\n\nWhile activating ");
            sb.append( source.getName());
            sb.append( "'s DCV is at ");
            sb.append( ChampionsUtilities.toSignedString(dcvMod) );
            sb.append( ".");
        }
        
        
        return sb.toString();
    }
    
    /** Getter for property interruptLevel.
     * @return Value of property interruptLevel.
     */
    public int getInterruptLevel() {
        Integer i = getIntegerValue("Effect.INTERRUPTLEVEL");
        return ( i==null ) ? 0 : i.intValue();
    }
    
    /** Setter for property interruptLevel.
     * @param interruptLevel New value of property interruptLevel.
     */
    public void setInterruptLevel(int interruptLevel) {
        add("Effect.INTERRUPTLEVEL", new Integer(interruptLevel), true);
    }
    
    /** Getter for property dcvMultiplier.
     * @return Value of property dcvMultiplier.
     */
    public double getDCVMultiplier() {
        Double i = getDoubleValue("Effect.DCVMULTIPLIER");
        return ( i==null ) ? 0 : i.doubleValue();
    }
    
    /** Setter for property dcvMultiplier.
     * @param dcvMultiplier New value of property dcvMultiplier.
     */
    public void setDCVMultiplier(double dcvMultiplier) {
        add("Effect.DCVMULTIPLIER", new Double(dcvMultiplier), true);
    }
    
    /** Getter for property dcvMultiplier.
     * @return Value of property dcvMultiplier.
     */
    public int getDCVModifier() {
        Integer i = getIntegerValue("Effect.DCVMODIFIER");
        return ( i==null ) ? 0 : i.intValue();
    }
    
    /** Setter for property dcvMultiplier.
     * @param dcvMultiplier New value of property dcvMultiplier.
     */
    public void setDCVModifier(int dcvModifier) {
        add("Effect.DCVMODIFIER", new Integer(dcvModifier), true);
    }
    
    /** Getter for property interruptibleEvent.
     * @return Value of property interruptibleEvent.
     */
    public BattleEvent getInterruptibleEvent() {
        return (BattleEvent) getValue("Effect.INTERRUPTIBLEEVENT");
    }
    
    /** Setter for property interruptibleEvent.
     * @param interruptibleEvent New value of property interruptibleEvent.
     */
    public void setInterruptibleEvent(BattleEvent interruptibleEvent) {
        add("Effect.INTERRUPTIBLEEVENT", interruptibleEvent, true);
    }
    
    /** Getter for property interruptibleAbility.
     * @return Value of property interruptibleAbility.
     */
    public Ability getInterruptibleAbility() {
        return (Ability) getValue("Effect.INTERRUPTIBLEABILITY");
    }
    
    /** Setter for property interruptibleAbility.
     * @param interruptibleAbility New value of property interruptibleAbility.
     */
    public void setInterruptibleAbility(Ability interruptibleAbility) {
        add("Effect.INTERRUPTIBLEABILITY", interruptibleAbility, true);
    }
    
    /** Triggers the Battle to remove the effect at the next possible oppertunity.
     *
     *  This will add a battleEngine event to remove the effect, with full undoability.
     */
    public void triggerRemoval() {
        if ( getInterruptibleEvent() != null && getInterruptibleEvent().getActivationInfo() != null ) {
            BattleEvent be = new BattleEvent(BattleEvent.DEACTIVATE, getInterruptibleEvent().getActivationInfo());
            Battle.currentBattle.addEvent( be );
        }
        else {
            super.triggerRemoval();
        }
        
    }
    
}