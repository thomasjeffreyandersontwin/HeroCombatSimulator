/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.interfaces.*;
import champions.*;
import champions.event.*;
import champions.exception.*;
import java.util.Vector;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import champions.attackTree.*;
/**
 *
 * @author  unknown
 * @version
 *
 * Effect Clinging is now Dynamic.
 */



public class effectDependence extends LinkedEffect{
    
    /** Holds value of property battleEvent. */
    
    private Effect effect;
    
    
    
    
    /** Hold the Ability which this effect is linked to */
    
    static private String[] timeIntervalOptions = {"1 Segment", "1 Phase","1 Turn", "1 Minute", "5 Minutes", "20 Minutes", "1 Hour", "6 Hours", "1 Day",
    "1 Week", "1 Month", "1 Season", "1 Year", "5 Years", "25 Years", "1 Century" };
    
    /** Creates new effectUnconscious */
    public effectDependence(Ability ability) {
        super(ability.getName(), "LINKED");
        //SUBSTANCEAVAILABLE stores the state of the substance for dependence.  If this var is true then the substance is available
        //and the dependence is not affecting the character.  Opposite if the value is false
        this.add("Effect.SUBSTANCEAVAILABLE","TRUE",false);
        setAbility(ability);
        setEffect(this);
        //this effect is hidden since it is on all the time but the  child dependence effects will NOT be hidden
        setHidden(true);
    }
    
    public boolean addEffect(BattleEvent be, Target target)
    throws BattleEventException {
        Ability ability = getAbility();
        Battle cb = Battle.getCurrentBattle();
        //STARTTIME stores the time that dependence is started and is updated by the child effects based on the time chart.  It is
        //set to current after an the child effect causes its effect to the character.  Think of it as a resetting timer.
        this.add("Effect.STARTTIME", new Long(cb.getTime().getTime()), false);
        if ( super.addEffect(be,target ) ){
            return true;
        }
        return false;
    }
    
    public void removeEffect(BattleEvent be, Target target)
    throws BattleEventException {
        Ability ability = getAbility();
        super.removeEffect(be,target );
    }
    
    public boolean presegment(BattleEvent be, Target t)
    throws BattleEventException {
        Battle cb = Battle.getCurrentBattle();
        Chronometer time = (Chronometer)Battle.getCurrentBattle().getTime().clone();
        //grab the current time from the clock
        long currenttime = cb.getTime().getTime();
        //grab the value of the SUBSTANCEAVAILABLE to determine if the substance is missing and the child effects should activate
        String substanceavailable = this.getStringValue("Effect.SUBSTANCEAVAILABLE");
        //grab the value of EFFECT to determine what effect will activate if the substance is not available
        String dependenceeffecttype = this.getAbility().getStringValue("Disadvantage.EFFECT");
        //grab the value of how many seconds before each application of the child effect
        long secondsdelay = this.getDelayInterval(this.getAbility(),1);
        //grab the time that is currently set as the start of the effect.  This is more of a timer and is reset by the child effects
        //after they apply their changes to the character's state
        Long starttime = getLongValue("Effect.STARTTIME" );
        
        //this if/then runs applyDependenceEffect() if the substance is unavailable, and the time is right to fire the child effect
        //it checks directly to see if the effect should fire every phase and if it should fire based on the timer matching the length 
        //that should be waited between time increments have elapsed
        if (substanceavailable != null && substanceavailable.equals("FALSE")) {
            if (time.isActivePhase(t.getEffectiveSpeed(time) ) ) {
                applyDependenceEffect(be,t);
            }
            else if (currenttime - starttime.intValue() >= secondsdelay) {
                applyDependenceEffect(be,t);
            }
        }
        else {
        }
        return false;
    }
    
    public String getDescription() {
        Ability ability = getAbility();
        if ( ability != null ) {
            Target target = this.getTarget();
            return "";
        }
        else {
            return "Effect Error";
        }
    }
    
    
    //addactions() is used to add items to the actionstab for abilities and more often effects
    //This follow addActions() actually has a toggling pair of actions that it adds
    public void addActions(Vector v) throws BattleEventException {
        final Ability ability = getAbility();
        
        //triggerRemove contains an actionPerformed that will change Effect.SUBSTANCEAVAILABLE to false which will 
        //eventually case the activation of dependence child effects
        Action triggerRemove = new AbstractAction( ability.getName() + ": Remove Substance" ) {
            public void actionPerformed(ActionEvent e){
                Action assignAction = new AbstractAction( "Adjust " + ability.getName() + " State") {
                    
                    public void actionPerformed(ActionEvent e){
                        final Ability currentAbility = ability.getInstanceGroup().getCurrentInstance();
                        Effect effect = getEffect();
                        
                        String substanceavailable = effect.getStringValue("Effect.SUBSTANCEAVAILABLE");
                        String newSubstanceavailable = "FALSE";
                        
                        try {
                            
                            BattleEvent be = (BattleEvent)e.getSource();
                            Undoable u = new ValuePairUndoable(effect, "Effect.SUBSTANCEAVAILABLE",
                            substanceavailable, newSubstanceavailable);
                            effect.add( "Effect.SUBSTANCEAVAILABLE", "FALSE", true);
                            be.addUndoableEvent(u);
                            
                            //run the applyDependenceEffect() method which will spawn the child effects
                            applyDependenceEffect( be, getAbility().getSource());
                        }
                        catch (BattleEventException bee) {
                        }
                        
                    }
                };
                
                BattleEvent newbe = new BattleEvent( assignAction );
                Battle.currentBattle.addEvent(newbe);
            }
        };

        //triggerReplace contains an actionPerformed that will change Effect.SUBSTANCEAVAILABLE to TRUE which will 
        //eventually cause the removal of dependence child effects        
        Action triggerReplace = new AbstractAction( ability.getName() + ": Replace Substance" ) {
            public void actionPerformed(ActionEvent e){
                Action assignAction = new AbstractAction( "Adjust " + ability.getName() + " State") {
                    
                    public void actionPerformed(ActionEvent e){
                        final Ability currentAbility = ability.getInstanceGroup().getCurrentInstance();
                        Effect effect = getEffect();
                        
                        String substanceavailable = effect.getStringValue("Effect.SUBSTANCEAVAILABLE");
                        String newSubstanceavailable = "FALSE";
                        
                        try {
                            
                            BattleEvent be = (BattleEvent)e.getSource();
                            Undoable u = new ValuePairUndoable(effect, "Effect.SUBSTANCEAVAILABLE",
                            substanceavailable, newSubstanceavailable);
                            effect.add( "Effect.SUBSTANCEAVAILABLE", "TRUE", true);
                            be.addUndoableEvent(u);
                            //run the reverseDependenceEffect() method which will kill the child effects
                            reverseDependenceEffect( be, getAbility().getSource());
                        }
                        catch (BattleEventException bee) {
                        }
                        
                    }
                };
                BattleEvent newbe = new BattleEvent( assignAction );
                Battle.currentBattle.addEvent(newbe);
            }
        };
        
        String substanceavailable = effect.getStringValue("Effect.SUBSTANCEAVAILABLE");
        if (substanceavailable == null || substanceavailable.equals("TRUE")) {
            v.add(triggerRemove);
        }
        else {
            v.add(triggerReplace);
        }
    }
    
    
    //This method grabs the time chart setting from the ability gets its sequential place in the time chart and uses that to compute
    //the number of seconds (segments really) that comprise this choice on the time chart.  This is used as a timer to count down to
    //applying the next application of the child effect that is processing
    private long getDelayInterval(Ability ability, int index) {
        String timeinterval = this.getAbility().getStringValue("Disadvantage.TIME");
        double cost = 0.0;
        
        int i;
        
        for (i = 0;i< timeIntervalOptions.length;i++) {
            if ( timeinterval.equals(timeIntervalOptions[i] )) break;
            
        }
        return ChampionsUtilities.calculateSeconds(i, 1,ability.getSource());
    }
    
    
    /** Getter for property battleEvent.
     * @return Value of property battleEvent.
     */
    public Effect getEffect() {
        return effect;
    }
    
    
    /** Setter for property battleEvent.
     * @param battleEvent New value of property battleEvent.
     */
    public void setEffect(Effect effect) {
        this.effect = effect;
    }
    
    //this method looks at the ability to determine what effect was purchased with the power.  Then if the substance isn't available and
    //the timing described above is correct it will execute the child effect that represents the purchased effect.  THere are numerous
    //choices for dependence so each is a child effect.  This method also records the effect that is launched so it can be removed with 
    //the reverseDependenceEffect() method.  This method also sets effect.STARTTIME which begins the count down for launching the child
    ///effect if it is dependent on the time chart to determine when the child effect affects aspects of the character
    public void applyDependenceEffect(BattleEvent be, Target t)
    throws BattleEventException {
        Battle cb = Battle.getCurrentBattle();
        String dependenceeffecttype = this.getAbility().getStringValue("Disadvantage.EFFECT");
        
        Effect secondary = (Effect)getValue("Effect.SECONDARY");
        if ( secondary != null ) return;
        
        if (dependenceeffecttype.endsWith("Active Points from Affected Power")) {
            Effect e = new effectDependenceAdjustment(this.getAbility(),this);
            Undoable u = new ValuePairUndoable(this, "Effect.SECONDARY", e);
            this.add( "Effect.SECONDARY", e, true);
            be.addUndoableEvent(u);
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + "'s "  + this.getName() + " is now affecting Powers.",BattleEvent.MSG_COMBAT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + "'s "  + this.getName() + " is now affecting Powers.",BattleEvent.MSG_COMBAT)); // .addMessage( t.getName() + "'s "  + this.getName() + " is now affecting Powers.",BattleEvent.MSG_COMBAT);
            e.addEffect(be, getTarget() );
        }
        else if (dependenceeffecttype.endsWith("d6 Damage")) {
            Effect e = new effectDependenceDamage(this.getAbility(),this);
            Undoable u = new ValuePairUndoable(this, "Effect.SECONDARY", e);
            this.add( "Effect.SECONDARY", e, true);
            be.addUndoableEvent(u);
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + "'s "  + this.getName() + " is now causing Damage.",BattleEvent.MSG_COMBAT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + "'s "  + this.getName() + " is now causing Damage.",BattleEvent.MSG_COMBAT)); // .addMessage( t.getName() + "'s "  + this.getName() + " is now causing Damage.",BattleEvent.MSG_COMBAT);
            e.addEffect(be, getTarget() );
        }
        else if (dependenceeffecttype.endsWith("Activation Roll")) {
            Effect e = new effectDependenceActivation(this.getAbility(),this);
            Undoable u = new ValuePairUndoable(this, "Effect.SECONDARY", e);
            this.add( "Effect.SECONDARY", e, true);
            be.addUndoableEvent(u);
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + "'s "  + this.getName() + " is now forcing Powers to have an Activation Roll.",BattleEvent.MSG_COMBAT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + "'s "  + this.getName() + " is now forcing Powers to have an Activation Roll.",BattleEvent.MSG_COMBAT)); // .addMessage( t.getName() + "'s "  + this.getName() + " is now forcing Powers to have an Activation Roll.",BattleEvent.MSG_COMBAT);
            e.addEffect(be, getTarget() );
        }
        else if (dependenceeffecttype.startsWith("Weakness")) {
            Effect e = new effectDependenceWeakness(this.getAbility(),this);
            Undoable u = new ValuePairUndoable(this, "Effect.SECONDARY", e);
            this.add( "Effect.SECONDARY", e, true);
            be.addUndoableEvent(u);
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + "'s "  + this.getName() + " is now causing Weakness in Characteristics.",BattleEvent.MSG_COMBAT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + "'s "  + this.getName() + " is now causing Weakness in Characteristics.",BattleEvent.MSG_COMBAT)); // .addMessage( t.getName() + "'s "  + this.getName() + " is now causing Weakness in Characteristics.",BattleEvent.MSG_COMBAT);
            
            e.addEffect(be, getTarget() );
        }
        else if (dependenceeffecttype.startsWith("Incompetence")) {
            Effect e = new effectDependenceIncompetence(this.getAbility(),this);
            Undoable u = new ValuePairUndoable(this, "Effect.SECONDARY", e);
            this.add( "Effect.SECONDARY", e, true);
            be.addUndoableEvent(u);
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + "'s "  + this.getName() + " is now causing Incompetence for Skill, Characteristic Rolls, etc.",BattleEvent.MSG_COMBAT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + "'s "  + this.getName() + " is now causing Incompetence for Skill, Characteristic Rolls, etc.",BattleEvent.MSG_COMBAT)); // .addMessage( t.getName() + "'s "  + this.getName() + " is now causing Incompetence for Skill, Characteristic Rolls, etc.",BattleEvent.MSG_COMBAT);
            e.addEffect(be, getTarget() );
        }
        this.add("Effect.STARTTIME", new Long(cb.getTime().getTime()), true);
    }
    
    //this code removes any child effects based on the stored reference to the child effect.  There can only be one child effect 
    //for each dependence disadvantage
    public void reverseDependenceEffect(BattleEvent be, Target t)
    throws BattleEventException {
        Effect secondary = (Effect)getValue("Effect.SECONDARY");
        if ( secondary != null ) {
            secondary.removeEffect(be, t);
            this.remove("Effect.SECONDARY");
        }
    }
}

