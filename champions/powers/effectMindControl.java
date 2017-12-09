
//set pioruty to 3
/*
 * effectFlash.java
 *
 * Created on April 22, 2001, 7:53 PM
 */

package champions.powers;

import champions.Battle;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.Dice;
import champions.Effect;
import champions.MentalEffectInfo;
import champions.Target;
import champions.battleMessage.GenericSummaryMessage;
import champions.exception.BattleEventException;
import java.awt.Color;

/**
 *
 * @author  twalker
 * @version
 */

public class effectMindControl extends Effect {
    
    
    /** Holds value of property targetReferenceNumber */
    private int targetReferenceNumber;
    
    public effectMindControl(Integer effectroll, MentalEffectInfo mei) {
        
        super("Mind Controlled", "PERSISTENT" );
        
        //attach the MEI to the effect
        setMentalEffectInfo(mei);
        
        //seed the effect with the effect roll rolled as the "damage" part of powerMindControl
        this.addDamageSubeffect("Mind Control", "EFFECT", effectroll.intValue(), "MD", "NORMAL");
        setEffectPriority(3);
        setCritical(true);
        setEffectColor(Color.red);
    }
    
    public boolean addEffect(BattleEvent be, Target t) throws BattleEventException {
        int index;
        Effect effect;
        Integer effectroll;
        Integer effectsuccessby;
        Integer effectlevelneeded;
        //grab  the effect roll value from effect (its seeded as it comes in from effectMindControl)
        //effectroll = getIntegerValue("Effect.effectroll" );
        
        int mentalpowerindex = this.getSubeffectIndex("Mind Control");
        effectroll = new Integer((int)Math.round(this.getSubeffectValue(mentalpowerindex)));
        System.out.println("effectroll: " + effectroll);
        // int effectBody = (int)Math.round(effect.getSubeffectValue(eindex) );
        
        //grab the mei for processing
        MentalEffectInfo mei = getMentalEffectInfo();
        //grab the level desired by the attacker from the mei
        String leveldesc = mei.getMentalEffectLevelDesc();
        Integer leveldesired = new Integer(mei.convertMentalEffectLevelDesctoInt(leveldesc));
        
        //grab the penalty for attacking a class of mind not defined in the power from the mei
        Integer classofmindeffectpenalty = mei.getMentalEffectClassOfMindEffectPenalty();
        //grab the total modifier saved to the mei from the mentaleffectpanel (doesn't include the other class of mind effect penalty)
        Integer totaleffectmodifier = new Integer(mei.getMentalEffectTotalModifiers());
        //grab the descripton of the mental power's effect from the mei
        String mentaleffectdescription = mei.getMentalEffectDescription();
        Target source = mei.getMentalEffectSource();
        //if the mei for this value isn't set set it to 0
        //do this in prepower of power
        if (classofmindeffectpenalty == null) {
            classofmindeffectpenalty = new Integer(0);
        }
        //grab the stat CON, EGO or INT based on the source's ability and the target's STAT existence
        String stat = getEffectRollStat(be, t);
        
        if ( t.hasStat(stat) ) {
            //get the most recent vlaue of the stat obtained above
            Integer statvalue = new Integer(t.getAdjustedStat(stat));
            
            //compute the level desired based on the source's gui selections and any penalty from class of mind restrictions
            if (leveldesired.intValue() == 0) {
                //the 1 + thing here is to deal with fact that level 0 is "greater than" not "equal to" as with the other levels
                effectlevelneeded = new Integer(1 + statvalue.intValue() + totaleffectmodifier.intValue()+ classofmindeffectpenalty.intValue());
                
            } else {
                effectlevelneeded = new Integer(statvalue.intValue() + totaleffectmodifier.intValue()+ classofmindeffectpenalty.intValue());
            }
            
            //check for vulnerability to attacking power
//        Double multiplier = champions.powers.disadvantageVulnerability.checkForEnabledVulnerabilitySFXMatch(be, t ) ;
//        if (multiplier.intValue() > 0 ) {
//            Double effectrolldouble;
//            effectrolldouble = new Double(effectroll.doubleValue() * multiplier.doubleValue());
//            effectroll = new Integer(effectrolldouble.intValue());
//            this.add("Effect.effectroll", effectroll );
//            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " is vulnerable to this attack.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " is vulnerable to this attack.", BattleEvent.MSG_NOTICE)); // .addMessage( t.getName() + " is vulnerable to this attack.", BattleEvent.MSG_NOTICE);
//        }
            
            //grab the roll and remove the neededlevel value computed above.
            effectsuccessby = new Integer(effectroll.intValue() - effectlevelneeded.intValue());
            //if effectsuccesby is positive then the effect worked and the effect needs to be populated with info
            //so it can be processed for breakout rolls
            if ( effectsuccessby.intValue() >= 0 ) {
                //if the effect roll is successful populate the effect with valuable information (used in getDescription as well)
                Integer effectsuccesspenalty = new Integer((int)Math.round(effectsuccessby.doubleValue()/5));
                Battle cb = Battle.getCurrentBattle();
                this.add("Effect.effectroll", effectroll, true );
                this.add("Effect.stat", stat, true );
                this.add("Effect.leveldesired",leveldesired,  true );
                this.add("Effect.totaleffectmodifier",totaleffectmodifier,  true );
                this.add("Effect.effectlevelneeded",effectlevelneeded,  true );
                this.add("Effect.effectsuccessby",effectsuccessby,  true );
                this.add("Effect.effectsuccesspenalty",effectsuccesspenalty,  true );
                this.add("Effect.mentaleffectdescription",mentaleffectdescription,  true );
                this.add("Effect.starttime",new Long(cb.getTime().getTime()), true);
                this.add("Effect.timebonus",new Long(0),  false );
                this.add("Effect.source",source,false);
                be.addBattleMessage( new GenericSummaryMessage(t, "was Mind Controlled"));
                //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " was Mind Controlled.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " was Mind Controlled.", BattleEvent.MSG_NOTICE)); // .addMessage( t.getName() + " was Mind Controlled.", BattleEvent.MSG_NOTICE);
            } else {
                //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " was NOT Mind Controlled.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " was NOT Mind Controlled.", BattleEvent.MSG_NOTICE)); // .addMessage( t.getName() + " was NOT Mind Controlled.", BattleEvent.MSG_NOTICE);
                be.addBattleMessage( new GenericSummaryMessage(t, "was NOT Mind Controlled"));
                return false;
            }
        } else {
            be.addBattleMessage( new GenericSummaryMessage(t, "can not be affected by Mind Control"));
            //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " can not be affected by Mind Control.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " can not be affected by Mind Control.", BattleEvent.MSG_NOTICE)); // .addMessage( t.getName() + " can not be affected by Mind Control.", BattleEvent.MSG_NOTICE);
            return false;
        }
        
        return super.addEffect(be,t);
    }
    
    public void removeEffect(BattleEvent be, Target target)
    throws BattleEventException {
        super.removeEffect(be,target);
        be.addBattleMessage( new GenericSummaryMessage(target, "is no longer Mind Control"));
    }
    
    public boolean prephase(BattleEvent be, Target t)
    throws BattleEventException {
        
        //grab the effect roll value rolled at ability execution time
        int effectroll = geteffectroll();
        //grab the stat determined as the breakout roll stat during execution time
        String stat = getStringValue("Effect.stat");
        //compute the value of the stat roll needed for the breakout roll
        Integer statroll= new Integer(9 + (t.getAdjustedStat(stat)/5));
        //grab the value computed for the penalty to the breakout roll based on how successful the effect roll was
        Integer effectsuccesspenalty = getIntegerValue("Effect.effectsuccesspenalty");
        //grab the time that the ability was executed
        long starttime = getLongValue("Effect.starttime").longValue();
        
        //these two lines grab the current time
        Battle cb = Battle.getCurrentBattle();
        long currenttime = cb.getTime().getTime();
        
        //Target source = (Target)this.getValue("Effect.source");
        
        //these lines compute the time difference from ability start to current to determine what the bonus
        //with be for the target to break out of the mental effect
        int chartdifference = 0;
        chartdifference = ChampionsUtilities.calculateTimeChartDifference(starttime,currenttime)-1;
        if ((chartdifference) > 0 ) {
            this.add("Effect.timebonus",new Long(chartdifference),  true );
        }
        
        //toss the bones and then compute success or failure of breakout based on all the values recovered above
        Dice roll = new Dice(3,true);
        if ( roll.getStun().intValue() <= statroll.intValue() + (chartdifference) - effectsuccesspenalty.intValue() ) {
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + "'s Mind Control breakout roll of " +  roll.getStun() + " succeeded.  Needed " + (statroll.intValue() + chartdifference - effectsuccesspenalty.intValue()) + ". " + t.getName() + " is no longer Mind Controlled.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + "'s Mind Control breakout roll of " +  roll.getStun() + " succeeded.  Needed " + (statroll.intValue() + chartdifference - effectsuccesspenalty.intValue()) + ". " + t.getName() + " is no longer Mind Controlled.", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + "'s Mind Control breakout roll of " +  roll.getStun() + " succeeded.  Needed " + (statroll.intValue() + chartdifference - effectsuccesspenalty.intValue()) + ". " + t.getName() + " is no longer Mind Controlled.", BattleEvent.MSG_NOTICE);
            return true;
        } else {
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " rolled " + roll.getStun() + " failing Mind Control breakout roll.  Needed " + (statroll.intValue() + chartdifference - effectsuccesspenalty.intValue()) + " or less." , BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " rolled " + roll.getStun() + " failing Mind Control breakout roll.  Needed " + (statroll.intValue() + chartdifference - effectsuccesspenalty.intValue()) + " or less." , BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " rolled " + roll.getStun() + " failing Mind Control breakout roll.  Needed " + (statroll.intValue() + chartdifference - effectsuccesspenalty.intValue()) + " or less." , BattleEvent.MSG_NOTICE);
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
        
        //seed the effect popup box with various goodies about this effect
        sb.append("Mind Control: " + mentaleffectdescription + "\n");
        sb.append("Effect Roll: " + effectroll +"\n");
        sb.append( "Effect Level Desired: " + leveldesired +"\n");
        sb.append( "Total Effect Modifier: " + totaleffectmodifier + "\n");
        sb.append( "Effect Level Needed: " + effectlevelneeded +"\n");
        sb.append( "Effect Successful By: " + effectsuccessby +"\n");
        sb.append( "Breakout Roll Penalty: " + effectsuccesspenalty.intValue() + "\n");
        sb.append( "Start Time (Secs): " + starttime+ "\n");
        sb.append( "Current Time (Secs): " +new Long(cb.getTime().getTime()) + "\n");
        sb.append( "Time Difference Bonus: " + timebonus + "\n");
        String s = "Mind Controlled\n" + " effectroll: " + effectroll.toString() + "\n";
        return sb.toString();
    }
    /** Getter for property the MentalEffectInfo detaillist (keeps track of everything
     * from the Mental Effect panel that the user sets at attack time
     *
     * @return Value of property MentalEffectInfo.
     */
    //grab the mei which carries all sorts of values passed during mental power execution time into the effect
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
    
    
    //this method grabs the proper stat during power execution time to determine
    public String getEffectRollStat(BattleEvent be, Target t) {
        String stat;
        
        
        //Limitation basedonCON = new limitationBasedOnCON();
        //check to see if mental power is based on CON
        if (be.getActivationInfo().getAbility().hasLimitation(limitationBasedOnCON.limitationName)) {
            stat = "CON";
        } else {
            stat = "EGO";
            
            if ( t.hasStat("EGO") == false ) {
                stat = "INT";
                
            }
        }
        //if the target has no EGO value then use INT
        
        return stat;
    }
}
