/*
 * effectDamageReduction.java
 *
 * Created on April 22, 2001, 7:40 PM
 */

package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.Effect;
import champions.LinkedEffect;
import champions.Target;
import champions.battleMessage.SimpleBattleMessage;
import champions.exception.BattleEventException;
import champions.interfaces.IndexIterator;



/**
 *
 * @author  twalker
 * @version
 */
public class effectTakesNoStun extends LinkedEffect{
    //private String type;
    //private boolean resistant;
    //private Integer percent;
    
    effectTakesNoStun( String name) {
        super(name,"LINKED");
        //add("Effect.DAMAGERESULT", result) ;
        setEffectPriority(5); // happen after everything else
    }
    
    public void postdefense(BattleEvent be, Effect effect, Target target,
            int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget)
            throws BattleEventException {
        int i, count;
        String type, defense,special;
        Double value, newValue;
        Object versus;
        
        // String damageResult = this.getStringValue("Effect.DAMAGERESULT");
        
        IndexIterator ii = effect.getSubeffectIterator();
        
        while( ii.hasNext() ) {
            int index = ii.nextIndex();
            String effectType = effect.getSubeffectEffectType(index);
            Object versusStat = effect.getSubeffectVersusObject(index);
            
            if ( (effectType != null) && "STUN".equals(versusStat) ) {
                // This is a stun, so set the amount of stun damage to zero!
                effect.setSubeffectValue(index,0);
                //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( this.getName() + " reduced STUN " + effectType.toLowerCase() + " to 0." , BattleEvent.MSG_ABILITY )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( this.getName() + " reduced STUN " + effectType.toLowerCase() + " to 0." , BattleEvent.MSG_ABILITY )); // .addMessage( this.getName() + " reduced STUN " + effectType.toLowerCase() + " to 0." , BattleEvent.MSG_ABILITY );
                 be.addBattleMessage( new SimpleBattleMessage(target, this.getName() + " reduced STUN " + effectType.toLowerCase() + " to 0."));
            }
        }
        
    }
    
    
    public String getDescription() {
        return "Takes No Stun";
    }

    public Ability getAbility() {
        return null;
    }
    
    
    
    /*
    public void addDefenseModifiers(DefenseList dl, String defense) {
        String damageType = this.getStringValue("Effect.DAMAGETYPE");
        Integer percent = this.getIntegerValue("Effect.PERCENT");
        boolean resistant = this.getBooleanValue("Effect.RESISTANT");
     
        if ( defense.equals(damageType) || (resistant && defense.equals("r" + damageType)) ){
            dl.addDefenseMultiplier( defense, getName(), (1 -(percent.intValue() / 100.0)) );
        }
    }
     **/
    
} // End of effectDamageReduction
