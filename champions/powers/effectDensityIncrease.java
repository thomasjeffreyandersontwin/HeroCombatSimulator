/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 *Pete Ruttman
 */

package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.DefenseList;
import champions.Effect;
import champions.LinkedEffect;
import champions.Target;
import champions.battleMessage.KnockbackResistanceMessage;
import champions.battleMessage.StatChangeBattleMessage;
import champions.battleMessage.StatChangeType;
import champions.enums.DefenseType;
import champions.exception.BattleEventException;
import champions.interfaces.ChampionsConstants;



public class effectDensityIncrease extends LinkedEffect
implements ChampionsConstants  {
    
    public effectDensityIncrease(Ability ability) {
        super(ability.getName(), "LINKED");
        setAbility(ability);
        Integer level = ability.getIntegerValue("Power.LEVEL");
        Integer strlevel = new Integer(level.intValue() * 5);
        
        int i = this.addIncreaseSubeffect("DensityIncreaseSTR", "STR", strlevel.doubleValue(), ADJUSTED_STAT);
        this.addIndexed(i,"Subeffect","AFFECTSFIGURED","FALSE", true);
    }
    
    public void predefense(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget)
    throws BattleEventException {
        Ability ability = getAbility();
        if ( ability != null ) {
            
            Integer level = ability.getIntegerValue("Power.LEVEL");
            
            Integer current;
            
            if ( ( current = effect.getIntegerValue("Knockback.RESISTANCE" ) ) != null ) {
                level = new Integer ( level.intValue() + current.intValue() );
            }
            
            effect.add("Knockback.RESISTANCE",  level,  true);
        }
    }
    
    public void addDefenseModifiers(DefenseList dl, String defense) {
        Ability ability = getAbility();
        Integer level = ability.getIntegerValue("Power.LEVEL");
        
        
        if ( defense.equals("PD") ) {
            dl.addDefenseModifier( DefenseType.PD, getName(), level.intValue() );
        }
        else if ( defense.equals( "ED" ) ) {
            dl.addDefenseModifier( DefenseType.ED, getName(), level.intValue() );
        }
        
    }
    
    
    public boolean addEffect(BattleEvent be, Target target)
    throws BattleEventException {
        Ability ability = getAbility();
        Integer level = ability.getIntegerValue("Power.LEVEL");
        
        if ( super.addEffect(be,target ) ){
            //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " now has " + level.toString() + "\" of Knockback Resistance" , BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " now has " + level.toString() + "\" of Knockback Resistance" , BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " now has " + level.toString() + "\" of Knockback Resistance" , BattleEvent.MSG_NOTICE );
            be.addBattleMessage( new KnockbackResistanceMessage(target, true, level));
            
            //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " now has +" + level.toString() + " PD" , BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " now has +" + level.toString() + " PD" , BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " now has +" + level.toString() + " PD" , BattleEvent.MSG_NOTICE );
            //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " now has +" + level.toString() + " ED" , BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " now has +" + level.toString() + " ED" , BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " now has +" + level.toString() + " ED" , BattleEvent.MSG_NOTICE );
            be.addBattleMessage( new StatChangeBattleMessage(target, StatChangeType.INCREASE, "PD", level));
            be.addBattleMessage( new StatChangeBattleMessage(target, StatChangeType.INCREASE, "ED", level));
            return true;
        }
        return false;
    }
    
    public void removeEffect(BattleEvent be, Target target)
    throws BattleEventException {
        Ability ability = getAbility();
        Integer level = ability.getIntegerValue("Power.LEVEL");
        
        super.removeEffect(be,target );
        //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " no longer has " + level.toString() + "\" of Knockback Resistance" , BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " no longer has " + level.toString() + "\" of Knockback Resistance" , BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " no longer has " + level.toString() + "\" of Knockback Resistance" , BattleEvent.MSG_NOTICE );
        be.addBattleMessage( new KnockbackResistanceMessage(target, false, level));
        //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " no longer has +" + level.toString() + " PD" , BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " no longer has +" + level.toString() + " PD" , BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " no longer has +" + level.toString() + " PD" , BattleEvent.MSG_NOTICE );
        //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " no longer has +" + level.toString() + " ED" , BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " no longer has +" + level.toString() + " ED" , BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " no longer has +" + level.toString() + " ED" , BattleEvent.MSG_NOTICE );
        be.addBattleMessage( new StatChangeBattleMessage(target, StatChangeType.DECREASE, "PD", level));
        be.addBattleMessage( new StatChangeBattleMessage(target, StatChangeType.DECREASE, "ED", level));
    }
    
    
    public String getDescription() {
        Ability ability = getAbility();
        Integer level = ability.getIntegerValue("Power.LEVEL");
        Integer strlevel = new Integer(level.intValue() * 5);
        
        //return level.toString() + "\" Knockback Resistance, +" + level.toString() + " PD & ED, and +" + strlevel.toString() + " STR";
        return level.toString() + "\" Knockback Resistance\n +" + level.toString() + " PD & ED\n +" + strlevel.toString() + " STR";
    }

}