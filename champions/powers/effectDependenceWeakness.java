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

import java.awt.Color;
/**
 *
 * @author  unknown
 * @version
 */
public class effectDependenceWeakness extends Effect {
    
    
    /** Holds value of property battleEvent. */
    
    private Effect originatingEffect;
    
    private Ability ability;
    
    static private String[] timeIntervalOptions = {"1 Segment", "1 Phase","1 Turn", "1 Minute", "5 Minutes", "20 Minutes", "1 Hour", "6 Hours", "1 Day",
    "1 Week", "1 Month", "1 Season", "1 Year", "5 Years", "25 Years", "1 Century" };
    
    static public String[] statOptions = {"STR","CON","DEX","BODY","INT","EGO","PRE","COM","PD",
    "ED","SPD","REC","END","STUN"};
    
    
    /** Creates new effectUnconscious */
    public effectDependenceWeakness(Ability ability, Effect effect) {
        super( "Dependence Weakness", "PERSISTENT", true);
        //setUnique(true);
        setCritical(true);
        setEffectColor(Color.red);
        setAbility(ability);
        setOriginatingEffect(effect);
    }
    
    public boolean addEffect(BattleEvent be, Target target)
    throws BattleEventException {
        Ability ability = getAbility();
        Battle cb = Battle.getCurrentBattle();
        if ( super.addEffect(be ,target ) ){
            return true;
        }
        return false;
        
    }
    
    public void removeEffect(BattleEvent be, Target target)
    throws BattleEventException {
        Ability ability = getAbility();
        String stat;
        int index,index2, count;
        Integer charpenalty = this.getIntegerValue("Effect.CHARPENALTY");
        
        Effect penalty = new Effect("Dependance Penalty");
        count = this.getAbility().getIndexedSize( "StatList" );
        int length = statOptions.length;
        for ( index=0;index<count;index++) {
            stat = (String)this.getAbility().getIndexedValue( index, "StatList", "STAT" );
            for(index2=0;index2<length;index2++) {
                if (stat.equals(statOptions[index2]) && charpenalty != null ) {
                    int dindex = penalty.addIncreaseSubeffect("Dependence Penalty", stat, charpenalty.intValue(), ADJUSTED_STAT);
                    //penalty.addIndexed(dindex,"Subeffect","AFFECTSFIGURED","FALSE", true);
                    break;
                }
            }
            penalty.addEffect(be,target);
            this.add("Effect.CHARPENALTY",new Integer(0), true);
        }
        
        super.removeEffect(be,target );
    }
    
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
                applyWeaknessPenalty(be,t);
            }
            else if ((currenttime - starttime.intValue() >= secondsdelay) && !timeinterval.equals("1 Phase")) {
                applyWeaknessPenalty(be,t);
                
            }
        }
        
        return false;
    }
    
    
    public void applyWeaknessPenalty(BattleEvent be, Target t)
    throws BattleEventException {
        Battle cb = Battle.getCurrentBattle();
        String dependenceeffecttype = this.getAbility().getStringValue("Disadvantage.EFFECT");
        
        String stat;
        int index,index2;
        Integer charpenalty = this.getIntegerValue("Effect.CHARPENALTY");
        Effect penalty = new Effect("Dependance Penalty");
        int count = this.getAbility().getIndexedSize( "StatList" );
        int length = statOptions.length;
        
        for ( index=0;index<count;index++) {
            stat = (String)this.getAbility().getIndexedValue( index, "StatList", "STAT" );
            if ( t.hasStat(stat) ) {
                int dindex = penalty.addDecreaseSubeffect("Dependence Penalty", stat, 3, ADJUSTED_STAT);
                penalty.addIndexed(dindex,"Subeffect","AFFECTSFIGURED","FALSE", true);
            }
            penalty.addEffect(be,t);
            if (charpenalty != null && charpenalty.intValue() != 0) {
                this.add("Effect.CHARPENALTY",new Integer(charpenalty.intValue() + 3), true);
            }
            else {
                this.add("Effect.CHARPENALTY",new Integer(3), true);
            }
        }
        getOriginatingEffect().add("Effect.STARTTIME",new Long(cb.getTime().getTime()), true);
        
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
    
    
}