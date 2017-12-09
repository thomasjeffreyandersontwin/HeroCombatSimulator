package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.Effect;
import champions.FrameworkAbility;
import champions.LinkedEffect;
import champions.Target;
import champions.battleMessage.CombatSkillLevelMessage;
import champions.exception.BattleEventException;
import champions.interfaces.CombatLevelProvider;
import champions.parameters.ParameterList;

/**
 *
 * @author  unknown
 * @version
 */
public class effectCombatLevel extends LinkedEffect implements CombatLevelProvider {

    static final long serialVersionUID = 5295848683348707403L;

    /** Creates new effectUnconscious */
    public effectCombatLevel(String name, Ability ability) {
        super(name, "LINKED", true);
        setAbility(ability);


        setName(name + " watcher");
        setHidden(true);

    }

//    /** Getter for property sense.
//     * @return Value of property sense.
//     */
//    public Integer getConfiguredDCModifier() {
//        /* if (this.getAbility().getSource().hasEffect("Enraged/Berserk")) {
//        Effect effect = this.getAbility().getSource().getEffect("Enraged/Berserk");
//        Ability ability = (Ability)effect.getValue("Effect.ABILITY");
//        boolean isberserk = ability.getBooleanValue("Disadvantage.BERSERK");
//        if (isberserk == true ) {
//        return new Integer(0);
//        }
//        } */
//        //return getAbility().getIntegerValue("CombatLevel.DCBONUS");
//        ParameterList pl = ability.getPowerParameterList();
//        return pl.getParameterIntValue("DCLevel");
//    }
//
//    public Integer getConfiguredOCVModifier() {
//        /* if (this.getAbility().getSource().hasEffect("Enraged/Berserk")) {
//        Effect effect = this.getAbility().getSource().getEffect("Enraged/Berserk");
//        Ability ability = (Ability)effect.getValue("Effect.ABILITY");
//        boolean isberserk = ability.getBooleanValue("Disadvantage.BERSERK");
//        if (isberserk == true ) {
//        Integer levels = getAbility().getIntegerValue("Power.LEVELS");
//        return levels;
//        }
//        }*/
//        //return getAbility().getIntegerValue("Ability.OCVBONUS");
//        //return getAbility().getOCVModifier();
//        ParameterList pl = ability.getPowerParameterList();
//        return pl.getParameterIntValue("OCVLevel");
//    }
//
//    public Integer getConfiguredDCVModifier() {
//        if (this.getAbility().getSource().hasEffect("Enraged/Berserk")) {
//            return new Integer(0);
//        } else {
//            //return getAbility().getIntegerValue("Ability.DCVBONUS");
//            //return this.getAbility().getDCVModifier();
//            ParameterList pl = ability.getPowerParameterList();
//            return pl.getParameterIntValue("DCVLevel");
//        }
//    }
    public int getLevels() {
        ParameterList pl = ability.getPowerParameterList();
        return pl.getParameterIntValue("Level");
    }

    @Override
    public String getDescription() {
        StringBuffer sb = new StringBuffer();

        sb.append("Assigned Levels:\n");
        sb.append(ChampionsUtilities.toSignedString(getConfiguredOCVModifier()) + " OCV Levels\n");
        sb.append(ChampionsUtilities.toSignedString(getConfiguredDCVModifier()) + " DCV Levels\n");
        sb.append(ChampionsUtilities.toSignedString(getConfiguredDCModifier()) + " DC Levels\n");

        return sb.toString();
    }

    @Override
    public boolean addEffect(BattleEvent be, Target target)
            throws BattleEventException {
        if (super.addEffect(be, target)) {
            be.addBattleMessage(new CombatSkillLevelMessage(target, this, true));

            if (isOverallLevel()) {
                addCombatModifierEffect(be);
            }

            return true;
        }
        return false;
    }

    @Override
    public void removeEffect(BattleEvent be, Target target)
            throws BattleEventException {

        removeCombatModifierEffect(be);

        super.removeEffect(be, target);

        be.addBattleMessage(new CombatSkillLevelMessage(target, this, false));
    }

    protected boolean isOverallLevel() {
        return ability.getPowerParameterList().getParameterStringValue("Overall").equals("Overall (Always Active)");
    }

//    public void adjustDice(BattleEvent be, String targetGroup) {
//        Ability beAbility = be.getAbility();
//        Ability beManeuver = be.getManeuver();
//
//        boolean activate = applies(beAbility) || applies(beManeuver);
//
//        if (activate == true) {
//            Double dc = be.getDoubleValue("Combat.DC");
//            if (dc == null) {
//                dc = new Double(getConfiguredDCModifier());
//            } else {
//                dc = new Double(dc + getConfiguredDCModifier());
//            }
//
//            if (getConfiguredDCModifier() > 0) {
//                be.addBattleMessage(new DCSummaryMessage(getTarget(), getConfiguredDCModifier()));
//            }
//            be.add("Combat.DC", dc, true);
//        }
//    }
//
//    @Override
//    public void abilityIsActivating(BattleEvent be, Ability activatedAbility) throws BattleEventException {
//        Ability maneuver = be.getManeuver();
//
//        boolean activate = applies(activatedAbility) || applies(maneuver);
//
//
//    }
    public void activateCombatLevels(BattleEvent be, boolean activate) throws BattleEventException {
        if (activate == true) {
            addCombatModifierEffect(be);
        } else {
            removeCombatModifierEffect(be);
        }
    }

    private void addCombatModifierEffect(BattleEvent be) throws BattleEventException {
        Ability effectAbility = getAbility();

        if (be.getSource().hasEffect(effectAbility.getName()) == false) {
            Effect e = new effectCombatModifier(effectAbility.getName(), this, isOverallLevel() == false);
            e.setHidden(false);
            e.addEffect(be, be.getSource());
        }
    }

    private void removeCombatModifierEffect(BattleEvent be) throws BattleEventException {
        be.getSource().removeEffect(be, getAbility().getName());
    }

//    public void addDCVDefenseModifiers(CVList cvList, Ability attack) {
//        Integer dcvbonus = getConfiguredDCVModifier();
//        String type = this.getAbility().getStringValue("Power.OVERALL");
//
//        String leveltype = this.getAbility().getStringValue("Power.LEVELTYPE");
//        //        Object mtype = attack.getStringValue("Ability.MTYPE");
//
//        if (attack != null && this.getAbility().getSource().hasEffect(this.getAbility().getName() + " Attack Type Watcher")) {
//            String attacktype = this.getAbility().getStringValue("Power.ATTACKTYPE");
//            if (leveltype.equals("3 Maneuvers") && attack.isRangedAttack() && attacktype.equals("Ranged")) {
//                cvList.addTargetCVModifier("Ranged CL Only", dcvbonus.intValue());
//            } else if (leveltype.equals("3 Maneuvers") && attack.isMeleeAttack() && attacktype.equals("Melee")) {
//                cvList.addTargetCVModifier("Melee CL Only", dcvbonus.intValue());
//            }
//        } else if (type.equals("Overall (Always Active)") && dcvbonus.intValue() != 0) {
//            cvList.addTargetCVModifier(this.getName(), dcvbonus.intValue());
//        }
//    }

//    @Override
//    public void addOCVAttackModifiers(CVList cvList, Ability attack) {
//        Integer ocvbonus = getConfiguredOCVModifier();
//        String type = this.getAbility().getStringValue("Power.OVERALL");
//
//        if (type.equals("Overall (Always Active)") && ocvbonus.intValue() != 0) {
//            cvList.addSourceCVModifier(this.getName(), ocvbonus.intValue());
//        }
//    }
    public boolean isCombatLevelProvider() {
        return true;
    }

    public String getCombatLevelName() {
        return ability.getName();
    }

    public boolean applies(Ability ability) {
        boolean applies = false;
        if (ability != null) {
            Ability effectAbility = getAbility();
            ParameterList pl = effectAbility.getPowerParameterList();
            String type = pl.getParameterStringValue("Overall");
            String levelType = pl.getParameterStringValue("LevelType");

            if (type.equals("Overall (Only when Doing Something)") || type.equals("Overall (Always Active)")) {
                applies = true;
            } else if (levelType.equals("All Combat")) {
                applies = true;
            } else if (levelType.equals("Ranged Combat")) {
                if (ability.isRangedAttack()) {
                    applies = true;
                }
            } else if (levelType.equals("HTH Combat")) {
                if (ability.isMeleeAttack()) {
                    applies = true;
                }
            } else if (levelType.equals("Martial Arts")) {
                if (ability.is("MARTIALMANEUVER")) {
                    applies = true;
                }
            } else {
                // Check for Abilities which may not be exactly equal
                if (effectAbility.getStringValue("Power.OVERALL").equals("Normal (According To Ability List)")) {
                    int count = effectAbility.getIndexedSize("CanUseCL");
                    for (int index = 0; index < count; index++) {
                        Ability a = (Ability) effectAbility.getIndexedValue(index, "CanUseCL", "ABILITY");
                        if (ability.equals(a)) {
                            applies = true;
                            break;
                        } else if (a instanceof FrameworkAbility && ability.getFramework() != null && ability.getFramework().getFrameworkAbility() == a) {
                            applies = true;
                            break;
                        }
                    }
                }
            }
        }

        return applies;
    }

    public int getConfiguredOCVModifier() {
        return powerCombatLevels.getConfiguredOCVModifier(ability);
    }

    public int getConfiguredDCVModifier() {
        return powerCombatLevels.getConfiguredDCVModifier(ability);
    }

    public int getConfiguredDCModifier() {
        return powerCombatLevels.getConfiguredDCModifier(ability);
    }

    public boolean isDCVEditable() {
        return powerCombatLevels.isDCVEditable(ability);
    }

    public boolean isDCEditable() {
        return powerCombatLevels.isDCEditable(ability);
    }

    public boolean isConfigurationLegal(int ocv, int dcv, int dc) {
        ParameterList pl = ability.getPowerParameterList();
        int levels = pl.getParameterIntValue("Level");

        return (ocv >= 0 && dcv >= 0 && dc >= 0 && ocv + dcv + dc * 2 <= levels);
    }

    public void setConfiguredModifiers(int ocv, int dcv, int dc) {
    	try{
        ParameterList pl = ability.getPowerParameterList();

        if (isConfigurationLegal(ocv, dcv, dc)) {
            pl.setParameterValue("OCVLevel", ocv);
            pl.setParameterValue("DCVLevel", dcv);
            pl.setParameterValue("DCLevel", dc);
        }
    	}
    	catch(Exception e){}
    }
}