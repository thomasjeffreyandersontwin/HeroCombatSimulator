/*
 * effectFlash.java
 *
 * Created on April 22, 2001, 7:53 PM
 */

package champions.powers;

import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.CVList;
import champions.ChampionsUtilities;
import champions.Dice;
import champions.Effect;
import champions.MentalEffectInfo;
import champions.Target;
import champions.battleMessage.GenericSummaryMessage;
import champions.exception.BattleEventException;


/**
 *
 * @author  twalker
 * @version
 */


public class effectMentalIllusions extends Effect {
    
    
    /** Holds value of property targetReferenceNumber */
    private int targetReferenceNumber;
    
    public effectMentalIllusions(Integer effectroll, MentalEffectInfo mei) {
        
        super("Mental Illusions", "PERSISTENT" );
        
        //attach the MEI to the effect
        setMentalEffectInfo(mei);
        
        //seed the effect with the effect roll rolled as the "damage" part of powerMindControl
        this.add("Effect.effectroll", effectroll);
    }
    
    public boolean addEffect(BattleEvent be, Target t) throws BattleEventException {
        int index;
        Effect effect;
        Integer effectroll;
        Integer effectsuccessby;
        Integer effectlevelneeded;
        //grab  the effect roll value from effect
        effectroll = getIntegerValue("Effect.effectroll" );
        //grab the mei for processing
        MentalEffectInfo mei = getMentalEffectInfo();
        //grab the level desired by the attacker from the mei
        //grab the level desired by the attacker from the mei
        String leveldesc = mei.getMentalEffectLevelDesc();
        Integer leveldesired = new Integer(mei.convertMentalEffectLevelDesctoInt(leveldesc));
        //grab the penalty for attacking a class of mind not defined in the power from the mei
        Integer classofmindeffectpenalty = mei.getMentalEffectClassOfMindEffectPenalty();
        //grab the total modifier saved to the mei from the mentaleffectpanel (doesn't include the other class of mind effect penalty)
        Integer totaleffectmodifier = new Integer(mei.getMentalEffectTotalModifiers());
        //grab the descripton of the mental power's effect from the mei
        String mentaleffectdescription = mei.getMentalEffectDescription();
        
        //if the mei for this value isn't set set it to 0
        if (classofmindeffectpenalty == null) {
            classofmindeffectpenalty = new Integer(0);
        }
        
        //this if/else is  needed because at level 1, the needed level is > EGO while the rest
        //of the levels are >= equal to EGO + value.  This computes the final value needed to equal or beat to succeed with this power
        if (leveldesired.intValue() == 0) {
            effectlevelneeded = new Integer(1 + t.getAdjustedStat("EGO") + totaleffectmodifier.intValue()+ classofmindeffectpenalty.intValue());
        }
        else {
            effectlevelneeded = new Integer(t.getAdjustedStat("EGO") + totaleffectmodifier.intValue()+ classofmindeffectpenalty.intValue());
        }
        
        //grab the roll and remove the current MD and the needed value computed above.
        effectsuccessby = new Integer(effectroll.intValue() - t.getAdjustedStat("MD") - effectlevelneeded.intValue());
        if ( effectsuccessby.intValue() >= 0 ) {
            //if the effect roll is successful populate the effect with valuable information (used in getDescription as well)
            Integer effectsuccesspenalty = new Integer((int)Math.round(effectsuccessby.doubleValue()/5));
            Battle cb = Battle.getCurrentBattle();
            this.add("Effect.effectroll", effectroll, true );
            this.add("Effect.leveldesired",leveldesired,  true );
            this.add("Effect.totaleffectmodifier",totaleffectmodifier,  true );
            this.add("Effect.effectlevelneeded",effectlevelneeded,  true );
            this.add("Effect.effectsuccessby",effectsuccessby,  true );
            this.add("Effect.effectsuccesspenalty",effectsuccesspenalty,  true );
            this.add("Effect.mentaleffectdescription",mentaleffectdescription,  true );
            this.add("Effect.starttime",new Long(cb.getTime().getTime()), true);
            this.add("Effect.timebonus",new Long(0),  false );
            
            be.addBattleMessage( new GenericSummaryMessage(t, "was affected by Mental Illusions"));
            //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " was affected by Mental Illusions.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " was affected by Mental Illusions.", BattleEvent.MSG_NOTICE)); // .addMessage( t.getName() + " was affected by Mental Illusions.", BattleEvent.MSG_NOTICE);
        }
        else {
            be.addBattleMessage( new GenericSummaryMessage(t, "was NOT affected by Mental Illusions"));
            //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " was NOT affected by Mental Illusions.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " was NOT affected by Mental Illusions.", BattleEvent.MSG_NOTICE)); // .addMessage( t.getName() + " was NOT affected by Mental Illusions.", BattleEvent.MSG_NOTICE);
            return false;
        }
        
        
        return super.addEffect(be,t);
    }
    
    public void removeEffect(BattleEvent be, Target target)
    throws BattleEventException {
        super.removeEffect(be,target);
        be.addBattleMessage( new GenericSummaryMessage(target, "is no longer affected by Mental Illusions"));
    }
    
    
    public boolean prephase(BattleEvent be, Target t)
    throws BattleEventException {
        
        int effectroll = geteffectroll();
        //int modifier = 0;
        int chartdifference = 0;
        //effectroll -= 1;
        Dice roll = new Dice(3,true);
        Integer egoroll= new Integer(9 + (t.getAdjustedStat("EGO")/5));
        Integer effectsuccessby = getIntegerValue("Effect.effectsuccessby");
        Integer effectsuccesspenalty = getIntegerValue("Effect.effectsuccesspenalty");
        long starttime = getLongValue("Effect.starttime").longValue();
        Battle cb = Battle.getCurrentBattle();
        
        long currenttime = cb.getTime().getTime();
        
        chartdifference = ChampionsUtilities.calculateTimeChartDifference(starttime,currenttime)-1;
        if ((chartdifference) > 0 ) {
            this.add("Effect.timebonus",new Long(chartdifference),  true );
        }
        if ( roll.getStun().intValue() <= egoroll.intValue() + (chartdifference) - effectsuccesspenalty.intValue() ) {
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + "'s Mental Illusions breakout roll of " +  roll.getStun() + " succeeded.  Needed " + (egoroll.intValue() + chartdifference - effectsuccesspenalty.intValue()) + ". " + t.getName() + " is no longer Mental Illusionsled.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + "'s Mental Illusions breakout roll of " +  roll.getStun() + " succeeded.  Needed " + (egoroll.intValue() + chartdifference - effectsuccesspenalty.intValue()) + ". " + t.getName() + " is no longer Mental Illusionsled.", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + "'s Mental Illusions breakout roll of " +  roll.getStun() + " succeeded.  Needed " + (egoroll.intValue() + chartdifference - effectsuccesspenalty.intValue()) + ". " + t.getName() + " is no longer Mental Illusionsled.", BattleEvent.MSG_NOTICE);
            return true;
        }
        else {
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " rolled " + roll.getStun() + " failing Mental Illusionsl breakout roll.  Needed " + (egoroll.intValue() + chartdifference - effectsuccesspenalty.intValue()) + "." , BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " rolled " + roll.getStun() + " failing Mental Illusionsl breakout roll.  Needed " + (egoroll.intValue() + chartdifference - effectsuccesspenalty.intValue()) + "." , BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " rolled " + roll.getStun() + " failing Mental Illusionsl breakout roll.  Needed " + (egoroll.intValue() + chartdifference - effectsuccesspenalty.intValue()) + "." , BattleEvent.MSG_NOTICE);
            return false;
        }
    }
    
    public String getDescription() {
        
        //ActivationInfo ai = this.
        
        Integer effectroll = getIntegerValue("Effect.effectroll" );
        Integer leveldesired = getIntegerValue("Effect.leveldesired" );
        Integer totaleffectmodifier = getIntegerValue("Effect.totaleffectmodifier" );
        Integer effectlevelneeded = getIntegerValue("Effect.effectlevelneeded" );
        Integer effectsuccessby = getIntegerValue("Effect.effectsuccessby" );
        Integer effectsuccesspenalty = getIntegerValue("Effect.effectsuccesspenalty");
        String mentaleffectdescription = getStringValue("Effect.mentaleffectdescription" );
        Long starttime = getLongValue("Effect.starttime" );
        Long timebonus = getLongValue("Effect.timebonus" );
        StringBuffer sb = new StringBuffer();
        Battle cb = Battle.getCurrentBattle();
        
        sb.append("Mental Illusions: " + mentaleffectdescription + "\n");
        sb.append("Effect Roll: " + effectroll +"\n");
        sb.append( "Effect Level Desired: " + leveldesired +"\n");
        sb.append( "Total Effect Modifier: " + totaleffectmodifier + "\n");
        sb.append( "Effect Level Needed: " + effectlevelneeded +"\n");
        sb.append( "Effect Successful By: " + effectsuccessby +"\n");
        sb.append( "Breakout Roll Penalty: " + effectsuccesspenalty.intValue() + "\n");
        sb.append( "Start Time (Secs): " + starttime.longValue()+ "\n");
        sb.append( "Current Time (Secs): " +new Long(cb.getTime().getTime()) + "\n");
        sb.append( "Time Difference Bonus: " + timebonus.longValue() + "\n");
        String s = "Mental Illusions\n" + " effectroll: " + effectroll.toString() + "\n";
        return sb.toString();
    }
    /** Getter for property the MentalEffectInfo detaillist (keeps track of everything
     * from the Mental Effect panel that the user sets at attack time
     *
     * @return Value of property MentalEffectInfo.
     */
    public MentalEffectInfo getMentalEffectInfo() {
        return (MentalEffectInfo)getValue("Effect.MENTALEFFECTINFO");
    }
    /** Setter for property the MentalEffectInfo detaillist (keeps track of everything
     * from the Mental Effect panel that the user sets at attack time
     *
     * @return Value of property MentalEffectInfo
     */
    public void setMentalEffectInfo(MentalEffectInfo mei) {
        add("Effect.MENTALEFFECTINFO", mei, true);
    }
    
    /** Getter for property effectroll.
     * @return Value of property effectroll.
     */
    public int geteffectroll() {
        Integer i = getIntegerValue("Effect.effectroll");
        return ( i==null ) ? 0 : i.intValue();
    }
    
    /** Setter for property effectroll.
     * @param effectroll New value of property effectroll.
     */
    public void seteffectroll(int effectroll) {
        add("Effect.effectroll", new Integer(effectroll), true);
    }
    
    /** Getter for property targetReferenceNumber.
     * @return Value of property targetReferenceNumber.
     *
     */
    public int getTargetReferenceNumber() {
        return targetReferenceNumber;
    }
    
    /** Setter for property targetReferenceNumber.
     * @param targetReferenceNumber New value of property targetReferenceNumber.
     *
     */
    public void setTargetReferenceNumber(int targetReferenceNumber) {
        this.targetReferenceNumber = targetReferenceNumber;
    }
    
    public void addDCVDefenseModifiers( CVList cvList, Ability attack ) {
        Integer leveldesired = getIntegerValue("Effect.leveldesired" );
        if (leveldesired.intValue() >= 3) {
            cvList.addTargetCVMultiplier( "Mental Illusions", 0.5);
        }
    }
}
