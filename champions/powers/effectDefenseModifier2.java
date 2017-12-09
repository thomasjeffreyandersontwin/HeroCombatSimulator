package champions.powers;

import champions.*;
import champions.battleMessage.StatChangeBattleMessage;
import champions.battleMessage.StatChangeType;
import champions.enums.DefenseType;
import champions.exception.*;


/** Implements a Generic Defense Modifier with Dynamic reconfigurability.
 *
 * In order to use this effect, the ability has to define the following vps:
 *  Ability.PDBONUS
 *  Ability.EDBONUS
 *  Ability.rPDBONUS
 *  Ability.rEDBONUS
 *  Ability.MDBONUS
 *
 * Any vps not defined will not be added as defense.
 */
public class effectDefenseModifier2 extends LinkedEffect{
    //static String defenseTypes[] = {"PD", "rPD", "ED", "rED", "MD", "POWERDEFENSE"};
    /** Creates new effectCombatModifier */
    public effectDefenseModifier2(String name, Ability ability) {
        super(name, "LINKED");
        setAbility(ability);
    }

    public boolean addEffect(BattleEvent be, Target target) throws BattleEventException {
        if (super.addEffect(be, target)) {

            StatChangeBattleMessage m = new StatChangeBattleMessage(target, StatChangeType.INCREASE);
            boolean change = buildBattleMessage(m);

            if (change) {
                be.addBattleMessage(m);
            }

            target.updateDefenses();

            return true;
        }

        return false;
    }

    public void removeEffect(BattleEvent be, Target target) throws BattleEventException {
        super.removeEffect(be, target);

        StatChangeBattleMessage m = new StatChangeBattleMessage(target, StatChangeType.DECREASE);
        boolean change = buildBattleMessage(m);

        if (change) {
            be.addBattleMessage(m);
        }

        target.updateDefenses();
    }

    protected boolean buildBattleMessage(StatChangeBattleMessage m) {
        Ability ability = getAbility();
        Integer pdbonus = ability.getIntegerValue("Ability.PDBONUS");
        Integer edbonus = ability.getIntegerValue("Ability.EDBONUS");
        Integer rpdbonus = ability.getIntegerValue("Ability.rPDBONUS");
        Integer redbonus = ability.getIntegerValue("Ability.rEDBONUS");
        //1 line added by PR
        Integer mdbonus = ability.getIntegerValue("Ability.MDBONUS");
        Integer powerbonus = ability.getIntegerValue("Ability.POWERDEFENSE");

        boolean change = false;

        if (pdbonus != null && pdbonus > 0) {
            m.setStatChangeAmount("PD", pdbonus);
            change = true;
        }

        if (edbonus != null && edbonus > 0) {
            m.setStatChangeAmount("ED", edbonus);
            change = true;
        }

        if (rpdbonus != null && rpdbonus > 0) {
            m.setStatChangeAmount("rPD", rpdbonus);
            change = true;
        }

        if (redbonus != null && redbonus > 0) {
            m.setStatChangeAmount("rED", redbonus);
            change = true;
        }

        if (mdbonus != null && mdbonus > 0) {
            m.setStatChangeAmount("MD", mdbonus);
            change = true;
        }

        if (powerbonus != null && powerbonus > 0) {
            m.setStatChangeAmount("POWERDEFENSE", powerbonus);
            change = true;
        }

        return change;
    }

    public String getDescription() {
        Ability ability = getAbility();
        Integer pdbonus = ability.getIntegerValue("Ability.PDBONUS");
        Integer edbonus = ability.getIntegerValue("Ability.EDBONUS");
        Integer rpdbonus = ability.getIntegerValue("Ability.rPDBONUS");
        Integer redbonus = ability.getIntegerValue("Ability.rEDBONUS");
        //1 line added by PR
        Integer mdbonus = ability.getIntegerValue("Ability.MDBONUS");
        Integer powerbonus = ability.getIntegerValue("Ability.POWERDEFENSE");

        if (pdbonus == null) {
            pdbonus = new Integer(0);
        }
        if (edbonus == null) {
            edbonus = new Integer(0);
        }
        if (rpdbonus == null) {
            rpdbonus = new Integer(0);
        }
        if (redbonus == null) {
            redbonus = new Integer(0);
        }
        if (mdbonus == null) {
            mdbonus = new Integer(0);
        }
        if (powerbonus == null) {
            powerbonus = new Integer(0);
        }
        StringBuffer sb = new StringBuffer();

        sb.append("Defense Modifier:\n");
        if (pdbonus.intValue() > 0) {
            sb.append("  ");
            sb.append(pdbonus.toString());
            sb.append(" PD\n");
        }

        if (rpdbonus.intValue() > 0) {
            sb.append("  ");
            sb.append(rpdbonus.toString());
            sb.append(" rPD\n");
        }
        if (edbonus.intValue() > 0) {
            sb.append("  ");
            sb.append(edbonus.toString());
            sb.append(" ED\n");
        }
        if (redbonus.intValue() > 0) {
            sb.append("  ");
            sb.append(redbonus.toString());
            sb.append(" rED\n");
        }
        if (mdbonus.intValue() > 0) {
            sb.append("  ");
            sb.append(mdbonus.toString());
            sb.append(" MD\n");
        }
        if (powerbonus.intValue() > 0) {
            sb.append("  ");
            sb.append(powerbonus.toString());
            sb.append(" Power Defense\n");
        }
        return sb.toString();
    }

    public void addDefenseModifiers(DefenseList dl, String defense) {
        Ability ability = getAbility();
        Integer bonus;
        Integer hardenedLevels;

        DefenseType dt = DefenseType.valueOf(defense);

        if (defense.equals("PD")) {
            bonus = ability.getIntegerValue("Ability.PDBONUS");
            if (bonus != null) {
                dl.addDefenseModifier(dt, getName(), bonus.intValue());
            }
        } else if (defense.equals("rPD")) {
            bonus = ability.getIntegerValue("Ability.rPDBONUS");
            if (bonus != null) {
                dl.addDefenseModifier(dt, getName(), bonus.intValue());
            }
        } else if (defense.equals("ED")) {
            bonus = ability.getIntegerValue("Ability.EDBONUS");
            if (bonus != null) {
                dl.addDefenseModifier(dt, getName(), bonus.intValue());
            }
        } else if (defense.equals("rED")) {
            bonus = ability.getIntegerValue("Ability.rEDBONUS");
            if (bonus != null) {
                dl.addDefenseModifier(dt, getName(), bonus.intValue());
            }
        } //4 lines added by PR
        else if (defense.equals("MD")) {
            bonus = ability.getIntegerValue("Ability.MDBONUS");
            if (bonus != null) {
                dl.addDefenseModifier(dt, getName(), bonus.intValue());
            }
        } else if (defense.equals("POWERDEFENSE")) {
            bonus = ability.getIntegerValue("Ability.POWERDEFENSE");
            if (bonus != null) {
                dl.addDefenseModifier(dt, getName(), bonus.intValue());
            }
        }

        // Set the Hardened Levels
        hardenedLevels = ability.getIntegerValue("Ability.HARDENEDLEVELS");
        if (hardenedLevels != null) {
            dl.setHardenedLevel(getName(), hardenedLevels.intValue());
        }
    }

    public void predefense(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget)
            throws BattleEventException {
        // Check for the ONLYUPTOAMOUNTABSORBED
        if (getAbility().getBooleanValue("Absorption.ONLYUPTOAMOUNTABSORBED") == true) {
            ActivationInfo ai = be.getActivationInfo();
            DefenseList dl = ai.getDefenseList(targetReferenceNumber, targetGroup);
            // Grab the absorption ability
            Ability absorptionAbility = (Ability) getAbility().getValue("Absorption.ABILITY");

            if (dl != null && absorptionAbility != null) {
                //int aindex = powerAbsorption.getAbsorptionAmountIndex(be, targetGroup, targetReferenceNumber, absorptionAbility);
                int amountAbsorbed = powerAbsorption.getAbsorptionAmountAbsorbed(be, targetGroup, targetReferenceNumber, absorptionAbility);

                for (DefenseType dt : DefenseType.values()) {
                    int modifier = dl.getDefenseModifier(dt, getName());
                    if (amountAbsorbed < modifier) {
                        dl.addDefenseModifier(dt, getName(), amountAbsorbed);
                    }
                }
            }
        }
    }

}