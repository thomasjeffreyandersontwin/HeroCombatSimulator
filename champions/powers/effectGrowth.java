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
import champions.battleMessage.KnockbackResistanceMessage;
import champions.battleMessage.StatChangeBattleMessage;
import champions.battleMessage.StatChangeType;
import champions.exception.BattleEventException;
import champions.interfaces.ChampionsConstants;


public class effectGrowth extends LinkedEffect
        implements ChampionsConstants, SizeModifierEffect {

 
    static private Object[][] parameterArray = {
        {"Level", "Power.LEVEL", Integer.class, new Integer(1)},
        {"Resistance", "Knockback.RESISTANCE", Integer.class, new Integer(1)},
        {"DCVBonus", "Power.DCVBONUS", Integer.class, new Integer(0)}
    };

    public effectGrowth(Ability ability) {
        super(ability.getName(), "LINKED");
        this.ability = ability;
        Integer level = (Integer) ability.parseParameter(parameterArray, "Level");
        Integer strlevel = new Integer(level.intValue() * 5);

        int i;

        //  i = this.addSubeffectInfo( "STR", "AID", "NONE", "NORMAL", "STAT", "STR", strlevel);
        //  i = this.addSubeffectInfo( "BODY", "AID", "NONE", "NORMAL", "STAT", "BODY", level);
        //   i = this.addSubeffectInfo( "STUN", "AID", "NONE", "NORMAL", "STAT", "STUN", level);
        i = this.addIncreaseSubeffect("STRIncrease", "STR", strlevel.intValue(), ADJUSTED_STAT);
        this.addIndexed(i, "Subeffect", "AFFECTSFIGURED", "FALSE", true);
        i = this.addIncreaseSubeffect("BODYIncrease", "BODY", level.intValue(), ADJUSTED_STAT);
        this.addIndexed(i, "Subeffect", "AFFECTSFIGURED", "FALSE", true);
        i = this.addIncreaseSubeffect("STUNIncrease", "STUN", level.intValue(), ADJUSTED_STAT);
        this.addIndexed(i, "Subeffect", "AFFECTSFIGURED", "FALSE", true);
    }

    public void predefense(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget)
            throws BattleEventException {
        if (getAbility() != null) {
            Integer level = (Integer) getAbility().parseParameter(parameterArray, "Level");

            Integer current;

            if ((current = effect.getIntegerValue("Knockback.RESISTANCE")) != null) {
                level = new Integer(level.intValue() + current.intValue());
            }

            effect.add("Knockback.RESISTANCE", level, true);
        }
    }

    /*public void addDefenseModifiers(DefenseList dl, String defense) {
    Integer level = (Integer)ability.parseParameter(parameterArray, "Level");
    
    if ( defense.equals("PD") ) {
    dl.addDefenseModifier( defense, getName(), level.intValue() );
    }
    else if ( defense.equals( "ED" ) ) {
    dl.addDefenseModifier( defense, getName(), level.intValue() );
    }
    
    }*/
    public void addDCVDefenseModifiers(CVList cvList, Ability attack) {
        Integer dcvbonus = (Integer) getAbility().parseParameter(parameterArray, "DCVBonus");


        if (dcvbonus.intValue() != 0) {
            cvList.addTargetCVModifier(this.getName(), dcvbonus.intValue());
        }
    }

    public boolean addEffect(BattleEvent be, Target target)
            throws BattleEventException {
        Integer level = (Integer) getAbility().parseParameter(parameterArray, "Level");
        Integer dcvbonus = (Integer) getAbility().parseParameter(parameterArray, "DCVBonus");
        if (super.addEffect(be, target)) {
            be.addBattleMessage(new KnockbackResistanceMessage(target, true, level));
            be.addBattleMessage(new CVModiferMessage(target, true, "DCV", dcvbonus));
            be.addBattleMessage( buildStatChangeMessage(target, StatChangeType.INCREASE));
            return true;
        }
        return false;
    }

    public void removeEffect(BattleEvent be, Target target)
            throws BattleEventException {
        Integer level = (Integer) getAbility().parseParameter(parameterArray, "Level");
        Integer dcvbonus = (Integer) getAbility().parseParameter(parameterArray, "DCVBonus");
        super.removeEffect(be, target);
        
        be.addBattleMessage(new KnockbackResistanceMessage(target, false, level));
        be.addBattleMessage(new CVModiferMessage(target, false, "DCV", dcvbonus));
        be.addBattleMessage( buildStatChangeMessage(target, StatChangeType.DECREASE));
    }
    
    protected StatChangeBattleMessage buildStatChangeMessage(Target target, StatChangeType changeType) {

        Integer level = (Integer)getAbility().parseParameter(parameterArray, "Level");
        Integer strlevel = new Integer(level.intValue() * 5);
        
        StatChangeBattleMessage m = new StatChangeBattleMessage(target, changeType, level, level);
        m.setStatChangeAmount("STR", strlevel);
        
        return m;
    }

    public String getDescription() {
        if (getAbility() != null) {
            Integer level = (Integer) getAbility().parseParameter(parameterArray, "Level");
            Integer dcvbonus = (Integer) getAbility().parseParameter(parameterArray, "DCVBonus");
            Integer strlevel = new Integer(level.intValue() * 5);

            //return level.toString() + "\" Knockback Resistance, +" + level.toString() + " PD & ED, and +" + strlevel.toString() + " STR";
            return level.toString() + "\" Knockback Resistance\n+" + level.toString() + " PD & ED\n+" + strlevel.toString() + " STR\n" + dcvbonus.toString() + " DCV";
        } else {
            return "Effect Error";
        }
    }


}