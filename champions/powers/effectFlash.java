/*
 * effectFlash.java
 *
 * Created on April 22, 2001, 7:53 PM
 */

package champions.powers;

import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.Chronometer;
import champions.Effect;
import champions.Sense;
import champions.SensePenaltyModifier;
import champions.Target;
import champions.battleMessage.FlashedSummaryMessage;
import champions.exception.BattleEventException;

/**
 *
 * @author  twalker
 * @version
 */
public class effectFlash extends Effect {
    
    protected int duration;
    protected  Sense sense;
    protected SensePenaltyModifier sensePenaltyModifier;
    
    public effectFlash( int duration, Sense sense ) {
        super("Flashed", "PERSISTENT" );
        setSense(sense);
        setDuration(duration);
    }
    
    public boolean addEffect(BattleEvent be, Target t) throws BattleEventException {
        int index,count;
        Effect effect;
        
        Chronometer time = Battle.currentBattle.getTime();
        setCurrenttime(time.getSegment());
        
        
        
        // This has to be redone!!!
//        count = t.getIndexedSize("Effect");
//        for(index=0;index<count;index++) {
//            effect = (Effect)t.getIndexedValue( index,"Effect","EFFECT" );
//            if ( effect instanceof effectFlashDefense ) {
//                if ( ((effectFlashDefense)effect).getSense().equals( getSense() ) ){
//                    duration = new Integer(duration.intValue() - ((effectFlashDefense)effect).getLevel().intValue());
//                }
//            }
//        }
        
        
        this.add("Effect","FIRSTPHASE.TRUE",  true );
        if ( duration <= 0 ) return false;

        be.addBattleMessage( new FlashedSummaryMessage(t, sense, duration) );
        
        //  t.flashSense(this,getSense());
        
        if ( sensePenaltyModifier == null ) {
            sensePenaltyModifier = new SensePenaltyModifier("Flashed", sense.getSenseName());
            sensePenaltyModifier.setFunctioningPenalty(true);
            be.addUndoableEvent(t.addSensePenalty(sensePenaltyModifier));
        }
        
        
        
        return super.addEffect(be,t);
    }
    
    public void removeEffect(BattleEvent be, Target target)
    throws BattleEventException {

            be.addUndoableEvent(target.removeSensePenalty(sensePenaltyModifier));
            be.addBattleMessage( new FlashedSummaryMessage(target, sense, 0) );
        
        super.removeEffect(be,target);
    }
    
//    public void addOCVAttackModifiers(CVList cvList, Ability ability) {
//        if ( ability != null && ability.isRangedAttack() ) {
//            cvList.addSourceCVSet("Flashed", 0);
//        }
//        else {
//            cvList.addSourceCVMultiplier("Flashed",0.5);
//        }
//    }
    
//    public void addDCVDefenseModifiers(CVList cvList, Ability ability) {
//        cvList.addTargetCVMultiplier( "Flashed", 0.5 );
//    }
    
    public boolean presegment(BattleEvent be, Target t)
    throws BattleEventException {
        Chronometer time = Battle.currentBattle.getTime();
        
       
        
        if (getCurrenttime() != time.getSegment()) {
            setCurrenttime(time.getSegment());
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " will remain flashed for " + duration + " segments(s).", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " will remain flashed for " + duration + " phase(s).", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " will remain flashed for " + duration + " phase(s).", BattleEvent.MSG_NOTICE);
            duration -= 1;
        }
        else {
            System.out.println("Warning:  EffectFlash error: saved time == current time");
        
        }
        
        if ( duration == 0 ) {
            //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is no longer flashed.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is no longer flashed.", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is no longer flashed.", BattleEvent.MSG_NOTICE);
            return true;
        } else {
            setDuration(duration);
            return false;
        }
    }
    
   
    
    public String getDescription() {
        Integer duration = new Integer(getDuration());
        boolean targeting = getBooleanValue("Effect.TARGETING");
        
        
        String s = "Flashed\n" + " Duration: " + duration.toString() + "\n";
        if ( targeting ) {
            s  = s + " Targeting:\n" + "  1/2 DCV\n" + "  1/2 OCV H-to-H\n" + "  0 OCV Ranged";
        }
        return s;
    }
    
    public void setAbility(Ability ability) {
        add("Effect.ABILITY", ability, true);
    }
    
    public Ability getAbility() {
        return (Ability)getValue("Effect.ABILITY");
    }
    
    /** Getter for property duration.
     * @return Value of property duration.
     */
    public int getDuration() {
        return duration;
    }
    
    /** Setter for property duration.
     * @param duration New value of property duration.
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    
    
    
    /** Getter for property duration.
     * @return Value of property duration.
     */
    public int getCurrenttime() {
        Integer i = getIntegerValue("Effect.CURRENTTIME");
        return ( i==null ) ? 0 : i.intValue();
    }
    
    /** Setter for property duration.
     * @param duration New value of property duration.
     */
    public void setCurrenttime(int currenttime) {
        add("Effect.CURRENTTIME", new Integer(currenttime), true);
    }

    public Sense getSense() {
        return sense;
    }

    public void setSense(Sense sense) {
        this.sense = sense;
    }
    
}
