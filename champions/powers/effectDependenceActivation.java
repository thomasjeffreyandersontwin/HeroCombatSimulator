/*
 * effectDying.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.interfaces.*;
import champions.*;
import champions.event.*;
import champions.exception.*;
import champions.attackTree.*;

import java.awt.Color;
/**
 *
 * @author  unknown
 * @version
 */
public class effectDependenceActivation extends Effect {
    
    private Effect originatingEffect;
    
    private Ability ability;
    
    static private String[] timeIntervalOptions = {"1 Segment", "1 Phase","1 Turn", "1 Minute", "5 Minutes", "20 Minutes", "1 Hour", "6 Hours", "1 Day",
    "1 Week", "1 Month", "1 Season", "1 Year", "5 Years", "25 Years", "1 Century" };
    
    
    /** Creates new effectUnconscious */
    public effectDependenceActivation(Ability ability, Effect effect) {
        super( "Dependence Activation", "PERSISTENT", true);
        //setUnique(true);
        setCritical(true);
        setEffectColor(Color.red);
        setAbility(ability);
        setOriginatingEffect(effect);
        this.add("Effect.ACTIVATIONTIMEPENALTY",new Integer(1), true);
        
    }
    
//    public boolean addEffect(BattleEvent be, Target target)
//    throws BattleEventException {
//        Ability ability = getAbility();
//        Battle cb = Battle.getCurrentBattle();
//        if ( super.addEffect(be,target ) ){
//            return true;
//        }
//        return false;
//    }
//    
//    public void removeEffect(BattleEvent be, Target target)
//    throws BattleEventException {
//        this.remove("Effect.ACTIVATIONTIMEPENALTY");
//        super.removeEffect(be,target );
//    }
    
    public boolean presegment(BattleEvent be, Target t)
    throws BattleEventException {
        String substanceavailable = getOriginatingEffect().getStringValue("Effect.SUBSTANCEAVAILABLE");
        Battle cb = Battle.getCurrentBattle();
        Chronometer time = (Chronometer)Battle.getCurrentBattle().getTime().clone();
        long currenttime = cb.getTime().getTime();
        String timeinterval = this.getAbility().getStringValue("Disadvantage.TIME");
        String dependenceeffecttype = this.getAbility().getStringValue("Disadvantage.EFFECT");
        long secondsdelay = this.getDelayInterval(this.getAbility(),1);
        Long starttime = getOriginatingEffect().getLongValue("Effect.STARTTIME" );
        if (substanceavailable != null && substanceavailable.equals("FALSE")) {
            if (time.isActivePhase(t.getEffectiveSpeed(time)) && timeinterval.equals("1 Phase" )) {
                applyActivationPenalty(be,t);
            }
            else if ((currenttime - starttime.intValue() >= secondsdelay) && !timeinterval.equals("1 Phase")) {
                applyActivationPenalty(be,t);
                
            }
        }
        
        return false;
    }
    
    
    public void applyActivationPenalty(BattleEvent be, Target t)
    throws BattleEventException {
        Integer activationtimepenalty = this.getIntegerValue("Effect.ACTIVATIONTIMEPENALTY");
        if (activationtimepenalty == null ) {
            activationtimepenalty = new Integer(0);
        }
        this.add("Effect.ACTIVATIONTIMEPENALTY",new Integer(activationtimepenalty.intValue()-1), true);
        
        
        String dependenceeffecttype = this.getAbility().getStringValue("Disadvantage.EFFECT");
        int amount = 0;
        if (dependenceeffecttype.equals("Powers Gain 11- Activation Roll")) {
            amount = 11;
        }
        else if (dependenceeffecttype.equals("Powers Gain 14- Activation Roll")) {
            amount = 14;
        }
        Integer currentActivationRoll = new Integer(amount + (activationtimepenalty.intValue()-1));
        if (currentActivationRoll.intValue() < 3 ) {
            currentActivationRoll = new Integer(3);
        }
        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " will have an Activation Roll of " + currentActivationRoll.intValue() + "- on Powers due to Dependence Activation.",BattleEvent.MSG_COMBAT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " will have an Activation Roll of " + currentActivationRoll.intValue() + "- on Powers due to Dependence Activation.",BattleEvent.MSG_COMBAT)); // .addMessage( t.getName() + " will have an Activation Roll of " + currentActivationRoll.intValue() + "- on Powers due to Dependence Activation.",BattleEvent.MSG_COMBAT);
        
        Battle cb = Battle.getCurrentBattle();
        getOriginatingEffect().add("Effect.STARTTIME",new Long(cb.getTime().getTime()), true);
        
    }
    
    public AttackTreeNode preactivate(BattleEvent be, Ability ability) {
        //Ability ability = be.getAbility();
        
        //setMode(SKILL_TARGET);
        int amount = -1;
        String dependenceeffecttype = this.getAbility().getStringValue("Disadvantage.EFFECT");
        
        if (dependenceeffecttype.equals("Powers Gain 11- Activation Roll")) {
            amount = 11;
        }
        else if (dependenceeffecttype.equals("Powers Gain 14- Activation Roll")) {
            amount = 14;
        }
        
        Integer activationtimepenalty = this.getIntegerValue("Effect.ACTIVATIONTIMEPENALTY");
        
        if (activationtimepenalty == null) {
            activationtimepenalty = new Integer(0);
        }
        //System.out.println("Time: " + Battle.getCurrentBattle().getTime().getTime() + "activationtimepenalty: " + activationtimepenalty);
        //this.add("Effect.ACTIVATIONTIMEPENALTY",new Integer(activationtimepenalty.intValue()+1), true);
        
        //String activationroll = this.getAbility().getStringValue("Disadvantage.LEVEL");
        
        String substanceavailable = getOriginatingEffect().getStringValue("Effect.SUBSTANCEAVAILABLE");
        Integer currentSkillRoll = new Integer(amount + activationtimepenalty.intValue());
        
        if (ability.isDisallowForcedActivation() == false && substanceavailable != null && activationtimepenalty.intValue() <= 0 &&
        substanceavailable.equals("FALSE") && dependenceeffecttype.endsWith("Activation Roll")) {
            
            if (currentSkillRoll.intValue() >= 3) {
                SkillRollNode node = new SkillRollNode("Effect Roll",currentSkillRoll.intValue(), this );
                node.setTargetGroupSuffix("SKILLROLL");
                node.setAbility(ability);
                return node;
            }
            else {
                SkillRollNode node = new SkillRollNode("Effect Roll",3, this );
                node.setTargetGroupSuffix("SKILLROLL");
                node.setAbility(ability);
                return node;
            }
        }
        return null;
    }
    
    private long getDelayInterval(Ability ability, int index) {
        String timeinterval = this.getAbility().getStringValue("Disadvantage.TIME");
        double cost = 0.0;
        
        int i;
        
        for (i = 0;i< timeIntervalOptions.length;i++) {
            if ( timeinterval.equals(timeIntervalOptions[i] )) break;
        }
        // After the above calculation, i should correspond to the time chart in the ChampionsConstants file.
        return ChampionsUtilities.calculateSeconds(i, 1,ability.getSource());
    }
    
    
    /** Getter for property sense.
     * @return Value of property sense.
     */
    public Ability getAbility() {
        return (Ability)getValue("Effect.ABILITY");
    }
    /** Setter for property sense.
     * @param sense New value of property sense.
     */
    public void setAbility(Ability ability) {
        add("Effect.ABILITY", ability, true);
    }
    
    
    /** Getter for property battleEvent.
     * @return Value of property battleEvent.
     */
    public Effect getOriginatingEffect() {
        return originatingEffect;
    }
    
    /** Setter for property battleEvent.
     * @param battleEvent New value of property battleEvent.
     */
    public void setOriginatingEffect(Effect effect) {
        this.originatingEffect = effect;
    }
    
    /** Returns whether this ability is currently enabled given the current state of the BattleEngine.
     * Only Power specific conditions should be checked.
     * @param ability Ability to be check.
     * @return True if ability is currently usable.
     */
    public boolean isEnabled(Ability a) {
        int count = getIndexedSize("Burnout");
        int index;
        Ability ability;
        for(index=0;index<count;index++) {
            ability = (Ability)this.getIndexedValue(index, "Burnout", "ABILITY");
            if (ability.getInstanceGroup().isInstance(a) && ability.isDisallowForcedActivation() == false ) {
                return false;
            }
        }
        return true;
    }
}