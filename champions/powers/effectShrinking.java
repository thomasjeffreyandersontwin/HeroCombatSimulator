/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 *Pete Ruttman
 */

package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.CVList;
import champions.Effect;
import champions.LinkedEffect;
import champions.Target;
import champions.battleMessage.CVModiferMessage;
import champions.battleMessage.KnockbackBodyMessage;
import champions.exception.BattleEventException;




public class effectShrinking extends LinkedEffect implements SizeModifierEffect  {
    
    /*static private Object[][] parameterArray = {
        {"Level","Power.LEVEL", Integer.class, new Integer(1)},
        {"Resistance","Knockback.RESISTANCE", Integer.class, new Integer(3)},
        {"DCVBonus","Power.DCVBONUS", Integer.class, new Integer(0)}
    };
    */
    public effectShrinking(Ability ability) {
        super(ability.getName(), "LINKED");
        setAbility(ability);
        //this.ability = ability;
        //Integer level = (Integer)ability.parseParameter(parameterArray, "Level");
        
        //int i;
        
        
    }
    
    
    public void predefense(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget)
    throws BattleEventException {
        //new direct method 
        Ability effectAbility = getAbility();
        Integer level = effectAbility.getIntegerValue("Power.LEVEL" );
        effect.add("Knockback.MASS",  new Integer( -3 * level.intValue() ),  true);
    }
    
    
    public void addDCVDefenseModifiers( CVList cvList , Ability attack) {
        //new direct method 
        Ability effectAbility = getAbility();
        
        //new direct method 
        Integer level = effectAbility.getIntegerValue("Power.LEVEL" );
        Integer dcvbonus = effectAbility.getIntegerValue("Power.DCVBONUS" );
        
        
        if ( dcvbonus.intValue() != 0 ) {
            cvList.addTargetCVModifier( this.getName(), dcvbonus.intValue());
        }
    }
    
    public boolean addEffect(BattleEvent be, Target target)
    throws BattleEventException {
        Ability effectAbility = getAbility();
        
        Integer level = effectAbility.getIntegerValue("Power.LEVEL" );
        Integer dcvbonus = effectAbility.getIntegerValue("Power.DCVBONUS" );
        
        if ( super.addEffect(be,target ) ){
            //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " will now have " + new Integer(3 * level.intValue()).toString() + " added to Body when determining knockback due to Shrinking" , BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " will now have " + new Integer(3 * level.intValue()).toString() + " added to Body when determining knockback due to Shrinking" , BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " will now have " + new Integer(3 * level.intValue()).toString() + " added to Body when determining knockback due to Shrinking" , BattleEvent.MSG_NOTICE );
            be.addBattleMessage( new KnockbackBodyMessage(target, true, 3 * level));
            //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " now has a " + dcvbonus.toString() + " DCV Bonus" , BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " now has a " + dcvbonus.toString() + " DCV Bonus" , BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " now has a " + dcvbonus.toString() + " DCV Bonus" , BattleEvent.MSG_NOTICE );
            be.addBattleMessage( new CVModiferMessage(target, true, "DCV", dcvbonus));
            return true;
        }
        return false;
    }
    
    public void removeEffect(BattleEvent be, Target target)
    throws BattleEventException {
        Ability effectAbility = getAbility();
        
        Integer level = effectAbility.getIntegerValue("Power.LEVEL" );
        Integer dcvbonus = effectAbility.getIntegerValue("Power.DCVBONUS" );
        super.removeEffect(be,target );
        //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " will now lose " + new Integer(3 * level.intValue()).toString() + " added to Body when determining knockback due to Shrinking" , BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " will now lose " + new Integer(3 * level.intValue()).toString() + " added to Body when determining knockback due to Shrinking" , BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " will now lose " + new Integer(3 * level.intValue()).toString() + " added to Body when determining knockback due to Shrinking" , BattleEvent.MSG_NOTICE );
        be.addBattleMessage( new KnockbackBodyMessage(target, false, 3 * level));
        //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " will now lose " + dcvbonus.toString() + " DCV Bonus" , BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " will now lose " + dcvbonus.toString() + " DCV Bonus" , BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " will now lose " + dcvbonus.toString() + " DCV Bonus" , BattleEvent.MSG_NOTICE );
        be.addBattleMessage( new CVModiferMessage(target, false, "DCV", dcvbonus));
    }

    public String getDescription() {
        Ability effectAbility = getAbility();
        if ( effectAbility != null ) {
            Integer level = effectAbility.getIntegerValue("Power.LEVEL" );
            Integer dcvbonus = effectAbility.getIntegerValue("Power.DCVBONUS" );
            
            //return level.toString() + "\" Knockback Resistance, +" + level.toString() + " PD & ED, and +" + strlevel.toString() + " STR";
            return dcvbonus.toString() + "+ DCV\n +" + new Integer(level.intValue() *3).toString() + " added to KB distance";
        }
        else {
            return "Effect Error";
        }
    }
}