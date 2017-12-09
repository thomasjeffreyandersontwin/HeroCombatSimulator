/*
 * effectKnockbackResistance.java
 *
 * Created on April 22, 2001, 7:54 PM
 */

package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.Effect;
import champions.LinkedEffect;
import champions.Target;
import champions.battleMessage.KnockbackResistanceMessage;
import champions.exception.BattleEventException;



/**
 *
 * @author  twalker
 * @version 
 */
public class effectKnockbackResistance extends LinkedEffect {
        static private Object[][] parameterArray = {
    {"Resistance","Knockback.RESISTANCE", Integer.class, new Integer(1)},

    };
    
        private Ability ability;

        public effectKnockbackResistance(Ability ability) {
            super(ability.getName(), "LINKED");
            this.ability = ability;
        }

        public void predefense(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget)
        throws BattleEventException {
            if ( getAbility() != null ) {
                Integer resistance = (Integer)getAbility().parseParameter(parameterArray, "Resistance");
                Integer current;

                if ( (current = effect.getIntegerValue("Knockback.RESISTANCE" ) ) != null ) {
                    resistance = new Integer ( resistance.intValue() + current.intValue() );
                }

                effect.add("Knockback.RESISTANCE",  resistance,  true);
            }
        }

        
        //addEffect & removeEffect added by Pete Ruttman
        public boolean addEffect(BattleEvent be, Target target)
    throws BattleEventException {
            Integer resistance = (Integer)getAbility().parseParameter(parameterArray, "Resistance");
       
        if ( super.addEffect(be,target ) ){
            //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " now has " + resistance.toString() + "\" of Knockback Resistance" , BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " now has " + resistance.toString() + "\" of Knockback Resistance" , BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " now has " + resistance.toString() + "\" of Knockback Resistance" , BattleEvent.MSG_NOTICE );
            be.addBattleMessage( new KnockbackResistanceMessage(target, true, resistance));
            return true;
        }
        return false;
    }
    
    public void removeEffect(BattleEvent be, Target target)
    throws BattleEventException {
        Integer resistance = (Integer)getAbility().parseParameter(parameterArray, "Resistance");
       
        super.removeEffect(be,target );
        //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " no longer has " + resistance.toString() + "\" of Knockback Resistance" , BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " no longer has " + resistance.toString() + "\" of Knockback Resistance" , BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " no longer has " + resistance.toString() + "\" of Knockback Resistance" , BattleEvent.MSG_NOTICE );
       be.addBattleMessage( new KnockbackResistanceMessage(target, false, resistance));

    }
        public String getDescription() {
            if ( getAbility() != null ) {
                Integer resistance = (Integer)getAbility().parseParameter(parameterArray, "Resistance");

                return resistance.toString() + "\" knockback resistance.";
            }
            else {
                return "Effect Error";
            }
        }

    public

    Ability getAbility() {
        return ability;
    }

    public void setAbility(Ability ability) {
        this.ability = ability;
    }
    }
