/*
 * effectSkillLevels.java
 *
 * Created on October 2, 2000, 4:06 PM
 */
package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.CVList;
import champions.ChampionsUtilities;
import champions.Effect;
import champions.LinkedEffect;
import champions.Target;
import champions.exception.BattleEventException;
import champions.parameters.ParameterList;
import java.awt.Color;

/**
 *
 * @author  unknown
 * @version
 */
public class effectSkillLevel extends LinkedEffect {

    static private Object[][] parameterArray = {
        {"Level", "Power.SKILLLEVEL", Integer.class, new Integer(1)},};

    public effectSkillLevel(String name, Ability ability, Target target) {
        super(name, "LINKED", true);
        this.ability = ability;
        setAbility(ability);
        setHidden(true);

        //setUnique(true);
        //setCritical(true);
        setEffectColor(new Color(153, 0, 153));

    }

//    public boolean prephase(BattleEvent be, Target t) {
//        Integer usedsl = (Integer) this.getValue("Effect.USEDSL");
//        //System.out.println("usedsl: " + usedsl);
//        add("Effect.USEDSL", new Integer(0), true);
//        return false;
//    }

    public boolean addEffect(BattleEvent be, Target t) throws BattleEventException {

        boolean added = super.addEffect(be, be.getSource());
        //        if ( added ) {
        //            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName(), BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName(), BattleEvent.MSG_NOTICE)); // .addMessage(t.getName(), BattleEvent.MSG_NOTICE);
        //            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage("and has discovered EXTENSIVE mannerisms to exploit.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage("and has discovered EXTENSIVE mannerisms to exploit.", BattleEvent.MSG_NOTICE)); // .addMessage("and has discovered EXTENSIVE mannerisms to exploit.", BattleEvent.MSG_NOTICE);
        //
        //        }
        return added;
    }

    public void removeEffect(BattleEvent be, Target t) throws BattleEventException {
        super.removeEffect(be, t);
    //        Target analyzetarget = (Target)getValue("Effect.ANALYZETARGET");
    //        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is no longer SkillLevels " + analyzetarget, BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is no longer SkillLevels " + analyzetarget, BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is no longer SkillLevels " + analyzetarget, BattleEvent.MSG_NOTICE);
    }

    @Override
    public void skillIsActivating(BattleEvent be, Ability activatedAbility) {
       // Ability ability = getAbility();
        Ability maneuver = be.getManeuver();
     //   String type = effectAbility.getStringValue("Power.OVERALL");
     //   Boolean useascl = this.getAbility().getBooleanValue("SkillLevel.USEASCL");

        int index, count;
        Ability a;
        boolean activate = false;

        ParameterList parameterList = ability.getPowerParameterList();
        String leveltype = (String) parameterList.getParameterValue("LevelType");

        if ( leveltype.equals("Overall Level") || leveltype.equals("All Non-Combat Skills")) {
            activate = true;
        }
        else {
            count = parameterList.getIndexedParameterSize("CanUseSL");
            for (index = 0; index < count; index++) {
                a = (Ability) parameterList.getIndexedParameterValue("CanUseSL", index);

                if (activatedAbility != null && activatedAbility.equals(a)) {
                    activate = true;
                    break;
                }
                else if (maneuver != null && maneuver.equals(a)) {
                    activate = true;
                    break;
                }
            }
        }

        
        if (activate == true) {
            Integer allocatedSL = (Integer) parameterList.getParameterValue("AllocatedSL");
            add("Effect.SKILLLEVEL", allocatedSL, true);
        } else {
            add("Effect.SKILLLEVEL", new Integer(0), true);
        }
    }

    public String getDescription() {
        if (ability != null) {
//            ParameterList parameterList = ability.getPowerParameterList();
//            int level = (Integer) parameterList.getParameterValue("Level");
//
            
            //return level.toString() + "\" Knockback Resistance, +" + level.toString() + " PD & ED, and +" + strlevel.toString() + " STR";
            return "Skill Levels";
        } else {
            return "Effect Error";
        }
    }

    /** Getter for property sense.
     * @return Value of property sense.
     */
//    public Integer getDCs() {
//        return getAbility().getIntegerValue("SkillLevel.DCBONUS");
//    }
//
//    public Integer getOCVs() {
//        return getAbility().getIntegerValue("SkillLevel.OCVBONUS");
//    }
//
//    public Integer getDCVs() {
//        return getAbility().getIntegerValue("SkillLevel.DCVBONUS");
//    }

    //    public String getDescription() {
    //        StringBuffer sb = new StringBuffer();
    //
    //        sb.append("Assigned Levels:\n");
    //        sb.append(ChampionsUtilities.toSignedString( getOCVs() ) + " OCV Levels\n");
    //        sb.append(ChampionsUtilities.toSignedString( getDCVs() ) + " DCV Levels\n");
    //        sb.append(ChampionsUtilities.toSignedString( getDCs() ) + " DC Levels\n");
    //
    //        return sb.toString();
    //    }
    public void adjustDice(BattleEvent be, String targetGroup) {


        ParameterList parameterList = ability.getPowerParameterList();
        int skillLevelDice = (Integer) parameterList.getParameterValue("DCLevel");


        if (skillLevelDice > 0) {
            Double dc = be.getDoubleValue("Combat.DC");
            if (dc == null) {
                dc = (double) skillLevelDice;
            } else {
                dc = dc + skillLevelDice;
            }

            be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(getName() + " added " + ChampionsUtilities.toSignedString(skillLevelDice) + " DC(s).", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( getName() + " added " + ChampionsUtilities.toSignedString( getDCs() ) + " DC(s).", BattleEvent.MSG_NOTICE)); // .addMessage( getName() + " added " + ChampionsUtilities.toSignedString( getDCs() ) + " DC(s).", BattleEvent.MSG_NOTICE);

            be.add("Combat.DC", dc, true);
        }

    }

//    public void abilityIsActivating(BattleEvent be, Ability ability) throws BattleEventException {
//        Ability effectAbility = getAbility();
//        Ability maneuver = be.getManeuver();
//
//        int index, count;
//        Ability a;
//        boolean activate = false;
//
//        if (effectAbility.getStringValue("Power.OVERALL").equals("Overall (Only when Doing Something)")) {
//            activate = true;
//        }
//
//        // Check for Abilities which may not be exactly equal
//        if (effectAbility.getStringValue("Power.OVERALL").equals("Normal (According To Ability List)")) {
//            count = effectAbility.getIndexedSize("CanUseCL");
//            for (index = 0; index < count; index++) {
//                a = (Ability) effectAbility.getIndexedValue(index, "CanUseCL", "ABILITY");
//                if (ability.equals(a)) {
//                    activate = true;
//                    break;
//                }
//                if (maneuver != null && maneuver.equals(a)) {
//                    activate = true;
//                    break;
//                }
//            }
//        }
//        String leveltype = (String) effectAbility.getStringValue("Power.LEVELTYPE");
//        String mtype = (String) ability.getStringValue("Ability.MTYPE");
//        String attacktype = (String) ability.getStringValue("Power.ATTACKTYPE");
//
//
//
//        if (activate == true) {
//            // Add OCV/DCV Effect
//            if (leveltype != null && leveltype.equals("3 Maneuvers")) {
//                Effect e = new effectAttackTypeWatcher(effectAbility.getName() + " Attack Type Watcher");
//                e.setHidden(true);
//                e.addEffect(be, be.getSource());
//            } else {
//                Effect e = new effectCombatModifier(effectAbility.getName(), getOCVs().intValue(), getDCVs().intValue());
//                e.setHidden(false);
//                e.addEffect(be, be.getSource());
//            }
//        }
//
//    }
    @Override
    public void addDCVDefenseModifiers(CVList cvList, Ability attack) {
        ParameterList parameterList = ability.getPowerParameterList();
        int dcv = (Integer) parameterList.getParameterValue("DCVLevel");


        if (dcv > 0) {
            cvList.addTargetCVModifier(this.getName(), dcv);
        }
    }

    @Override
    public void addOCVAttackModifiers(CVList cvList, Ability attack) {

        ParameterList parameterList = ability.getPowerParameterList();
        int ocv = (Integer) parameterList.getParameterValue("OCVLevel");

        if (ocv > 0) {
            cvList.addSourceCVModifier(this.getName(), ocv);
        }
    }

//    public boolean OverallsetasCL() {
//        String overall = this.getAbility().getStringValue("Power.OVERALL");
//        Boolean useascl = this.getAbility().getBooleanValue("SkillLevel.USEASCL");
//        ;
//
//        if (overall.equals("Overall (Always Active)") && useascl) {
//            return true;
//        }
//        return false;
//    }

//    private boolean isUseAsCombatLevel() {
//        ParameterList parameterList = ability.getPowerParameterList();
//        int ocv = (Integer) parameterList.getParameterValue("OCVLevel");
//        int dcv = (Integer) parameterList.getParameterValue("DCVLevel");
//        int dc = (Integer) parameterList.getParameterValue("DCLevel");
//
//        return ocv > 0 || dcv > 0 || dc > 0;
//    }
}