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
import champions.exception.BattleEventException;
import champions.interfaces.ChampionsConstants;




public class effectPhysicalLimitation extends LinkedEffect implements ChampionsConstants {
    
    static private Object[][] parameterArray = {
        {"DCVLevel","Disadvantage.DCVLEVEL", Integer.class, new Integer(0)}
    };
    
    public effectPhysicalLimitation(Ability ability) {
        super(ability.getName(), "LINKED");
        this.ability = ability;
    }
    
    
    public void addDCVDefenseModifiers( CVList cvList , Ability attack) {
        Integer DCVLevel = (Integer)getAbility().parseParameter( parameterArray, "DCVLevel" );
        if ( DCVLevel.intValue() != 0 ) {
            cvList.addTargetCVModifier( this.getName(), DCVLevel.intValue());
        }
    }
    
    public boolean addEffect(BattleEvent be, Target target)
    throws BattleEventException {
        Integer DCVLevel = (Integer)getAbility().parseParameter( parameterArray, "DCVLevel" );
        if ( super.addEffect(be,target ) ){
            //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " now has a " + DCVLevel.toString() + " DCV penalty" , BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " now has a " + DCVLevel.toString() + " DCV penalty" , BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " now has a " + DCVLevel.toString() + " DCV penalty" , BattleEvent.MSG_NOTICE );
            be.addBattleMessage( new CVModiferMessage(target, true, "DCV", DCVLevel));
            return true;
        }
        return false;
    }
    
    public void removeEffect(BattleEvent be, Target target)
    throws BattleEventException {
        Integer DCVLevel = (Integer)getAbility().parseParameter( parameterArray, "DCVLevel" );
        super.removeEffect(be,target );
        //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " no longer has a " + DCVLevel.toString() + " DCV penalty", BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " no longer has a " + DCVLevel.toString() + " DCV penalty", BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " no longer has a " + DCVLevel.toString() + " DCV penalty", BattleEvent.MSG_NOTICE );
        be.addBattleMessage( new CVModiferMessage(target, false, "DCV", DCVLevel));
    }
    
    
    public String getDescription() {
        if ( getAbility() != null ) {
            Integer DCVLevel = (Integer)getAbility().parseParameter( parameterArray, "DCVLevel" );
            
            //return level.toString() + "\" Knockback Resistance, +" + level.toString() + " PD & ED, and +" + strlevel.toString() + " STR";
            return "";//level.toString() + "\" Knockback Resistance\n+" + level.toString() + " PD & ED\n+" + strlevel.toString() + " STR\n" + DCVLevel.toString() + " DCV";
        }
        else {
            return "Effect Error";
        }
    }
}