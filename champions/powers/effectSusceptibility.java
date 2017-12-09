/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */


package champions.powers;

import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.Chronometer;
import champions.Dice;
import champions.Effect;
import champions.LinkedEffect;
import champions.Target;
import champions.ValuePairUndoable;
import champions.battleMessage.GainedEffectSummaryMessage;
import champions.exception.BadDiceException;
import champions.exception.BattleEventException;


import champions.interfaces.Undoable;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;



/**
 *
 * @author  unknown
 * @version
 *
 * Effect Clinging is now Dynamic.
 */



public class effectSusceptibility extends LinkedEffect{
    
    /** Holds value of property battleEvent. */
    private BattleEvent battleEvent;
    
    private Effect effect;
    
    /** Hold the Ability which this effect is linked to */
    
    static private String[] timeIntervalOptions = {"Effect Is Instant","1 Segment", "1 Phase","1 Turn", "1 Minute", "5 Minutes", "20 Minutes", "1 Hour", "6 Hours", "1 Day",
    "1 Week", "1 Month", "1 Season", "1 Year", "5 Years", "25 Years", "1 Century" };
    
    /** Creates new effectUnconscious */
    public effectSusceptibility(Ability ability) {
        super(ability.getName(), "LINKED");
        this.add("Effect.CONDITIONPRESENT","FALSE",false);
        setAbility(ability);
        setEffect(this);
        
    }
    
    public boolean addEffect(BattleEvent be, Target target)
            throws BattleEventException {
        setBattleEvent(be);
        if (super.addEffect(be, target)) {
            be.addBattleMessage(new GainedEffectSummaryMessage(target, this, true));
            return true;
        }
        return false;
    }

    public void removeEffect(BattleEvent be, Target target)
            throws BattleEventException {
        super.removeEffect(be, target);
        be.addBattleMessage(new GainedEffectSummaryMessage(target, this, false));
    }
    
    public void predefense(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget)
    throws BattleEventException {
        String damagedie= this.getAbility().getStringValue("Disadvantage.DAMAGEDIE");
        try {
            Dice roll = new Dice( damagedie );
            String damagestat = this.getAbility().getStringValue("Disadvantage.DAMAGESTAT");
            if (be.getAbility() != this.getAbility()) {
                int abilitySFXIndexSize = be.getAbility().getIndexedSize("SpecialEffect");
                int targetSFXIndexSize = this.getAbility().getIndexedSize("SpecialEffect");
                for (int index = 0; index < abilitySFXIndexSize; index++) {
                    for (int index2 = 0; index2 < targetSFXIndexSize; index2++) {
                        //compare the classes the power affects to the class(es) of mind of the target...look for a match
                        if (be.getAbility().getIndexedValue(index,"SpecialEffect","SPECIALEFFECT" ).equals(this.getAbility().getIndexedValue(index2,"SpecialEffect","SPECIALEFFECT" ))) {
                            if (be.getAbility().isDisadvantage()) {
                                break;
                            }
                            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " is Susceptible to the SFX of this attack.", BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " is Susceptible to the SFX of this attack.", BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " is Susceptible to the SFX of this attack.", BattleEvent.MSG_NOTICE );
                            
                            //Dice roll = new Dice(3,true);
                            Effect damage = new Effect( "Susceptibility Damage", "INSTANT" );
                            if (target.isUnconscious()== false) {
                                if (damagestat.equals("STUN")) {
                                    damage.addDamageSubeffect("StunFromSusceptibility", damagestat, roll.getStun().intValue(), "NONE", "NONE");
                                    be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " takes STUN damage from this Susceptibility.", BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " takes STUN damage from this Susceptibility.", BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " takes STUN damage from this Susceptibility.", BattleEvent.MSG_NOTICE );
                                }
                                else {
                                    damage.addDamageSubeffect("BodyFromSusceptibility", damagestat, roll.getBody().intValue(), "NONE", "NONE");
                                    be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " takes BODY damage immediately from this Susceptibility.", BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " takes BODY damage immediately from this Susceptibility.", BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " takes BODY damage immediately from this Susceptibility.", BattleEvent.MSG_NOTICE );
                                }
                            }
                            else {
                                if ( damagestat.equals("STUN")) {
                                    damage.addDamageSubeffect("StunFromSusceptibility", damagestat, roll.getStun().intValue(), "NONE", "NONE");
                                    damage.addDamageSubeffect("BodyFromSusceptibility", "BODY", roll.getBody().intValue(), "NONE", "NONE");
                                }
                            }
                            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " rolled " + roll.toString(), BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " rolled " + roll.toString(), BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " rolled " + roll.toString(), BattleEvent.MSG_NOTICE );
                            damage.addEffect(be,target);
                        }
                    }
                }
            }
        }
        catch (BadDiceException bde) {
        }
        
    }
    
    public boolean presegment(BattleEvent be, Target t)
    throws BattleEventException {
        Battle cb = Battle.getCurrentBattle();
        Chronometer time = (Chronometer)Battle.getCurrentBattle().getTime().clone();
        long currenttime = cb.getTime().getTime();
        String timeinterval = this.getAbility().getStringValue("Disadvantage.TIME");
        if (timeinterval.equals("1 Phase") || timeinterval.equals("Effect Is Instant")) {
            return false;
        }
        long secondsdelay = this.getDelayInterval(this.getAbility(),1);
        Long starttime = getLongValue("Effect.STARTTIME" );
        if (starttime != null) {
            if ((currenttime - starttime.intValue() >= secondsdelay)) {
                applySusceptibilityDamage(be,t,cb);
            }
        }
        return false;
    }
    
    public boolean prephase(BattleEvent be, Target t)
    throws BattleEventException {
        Battle cb = Battle.getCurrentBattle();
        String timeinterval = this.getAbility().getStringValue("Disadvantage.TIME");
        applySusceptibilityDamage(be, t, cb);
        return false;
    }
    
    
    public String getDescription() {
        Ability ability = getAbility();
        if ( ability != null ) {
            return "";
        }
        else {
            return "Effect Error";
        }
    }
    
    
    private long getDelayInterval(Ability ability, int index) {
        String timeinterval = this.getAbility().getStringValue("Disadvantage.TIME");
        int i;
        
        for (i = 0;i< timeIntervalOptions.length;i++) {
            if ( timeinterval.equals(timeIntervalOptions[i] )) break;
            
        }
        
        
        // After the above calculation, i should correspond to the time chart in the ChampionsConstants file.
        return ChampionsUtilities.calculateSeconds(i-1, 1);
    }
    
    //    public void addActions(Vector v) {
    //        final Ability ability = getAbility();
    //        Action assignAction = new AbstractAction( "Adjust " + ability.getName() + " State") {
    //
    //            public void actionPerformed(ActionEvent e) {
    //                final Ability currentAbility = ability.getCurrentInstance();
    //                Effect effect = getEffect();
    //
    //                String conditionpresent = (String)effect.getValue("Effect.CONDITIONPRESENT");
    //
    //                ParameterList pl = new ParameterList();
    //                pl.addBooleanParameter( "ConditionPresent",  "Effect.CONDITIONPRESENT", "Condition Is Present (Only Mark when Constant)", conditionpresent);
    //                String timeinterval = currentAbility.getStringValue("Disadvantage.TIME");
    //                if (timeinterval != null && timeinterval.equals("Effect Is Instant")) {
    //                    pl.setEnabled("ConditionPresent",false);
    //                }
    //                else {
    //                    pl.setEnabled("ConditionPresent",true);
    //                }
    //                PADDialog pd = new PADDialog(null);
    //                PADValueListener pvl = new PADValueListener() {
    //                    public void PADValueChanged(PADValueEvent evt) {
    //                    }
    //
    //                    public boolean PADValueChanging(PADValueEvent evt) {
    //
    //                        return true;
    //                    }
    //                };
    //                int result = pd.showPADDialog( "Adjust " + ability.getName() + " State", pl, effect, pvl);
    //                if ( result == JOptionPane.CANCEL_OPTION ) {
    //                    effect.add("Effect.CONDITIONPRESENT",conditionpresent,true);
    //
    //                }
    //                else {
    //                    BattleEvent be = getBattleEvent();
    //                    Target t = currentAbility.getSource();
    //                    Battle cb = Battle.getCurrentBattle();
    //                    try {
    //                        applySusceptibilityDamage(be, t, cb);
    //                    }
    //                    catch (BattleEventException bee) {
    //                    }
    //
    //                }
    //
    //            }
    //        };
    //
    //        v.add(assignAction);
    //    }
    
    //addactions() is used to add items to the actionstab for abilities and more often effects
    //This follow addActions() actually has a toggling pair of actions that it adds
    public void addActions(Vector v) throws BattleEventException {
        final Ability ability = getAbility();
        
        //triggerRemove contains an actionPerformed that will change Effect.CONDITIONPRESENT to false which will
        //eventually case the activation of dependence child effects
        Action triggerRemove = new AbstractAction( ability.getName() + ": Remove Condition" ) {
            public void actionPerformed(ActionEvent e){
                Action assignAction = new AbstractAction( "Adjust " + ability.getName() + " State") {
                    
                    public void actionPerformed(ActionEvent e){
                        final Ability currentAbility = ability.getInstanceGroup().getCurrentInstance();
                        Effect effect = getEffect();
                        
                        String conditionpresent = effect.getStringValue("Effect.CONDITIONPRESENT");
                        String newconditionpresent = "FALSE";
                        
                        // try {
                        
                        BattleEvent be = (BattleEvent)e.getSource();
                        Undoable u = new ValuePairUndoable(effect, "Effect.CONDITIONPRESENT",
                        conditionpresent, newconditionpresent);
                        effect.add( "Effect.CONDITIONPRESENT", "FALSE", true);
                        be.addUndoableEvent(u);
                        effect.remove("Effect.CHANGEOCCURRED");
                    }
                };
                
                BattleEvent newbe = new BattleEvent( assignAction );
                Battle.currentBattle.addEvent(newbe);
            }
        };
        
        //triggerReplace contains an actionPerformed that will change Effect.CONDITIONPRESENT to TRUE which will
        //eventually cause the removal of dependence child effects
        Action triggerReplace = new AbstractAction( ability.getName() + ": Apply Condition" ) {
            public void actionPerformed(ActionEvent e){
                Action assignAction = new AbstractAction( "Adjust " + ability.getName() + " State") {
                    
                    public void actionPerformed(ActionEvent e){
                        final Ability currentAbility = ability.getInstanceGroup().getCurrentInstance();
                        Effect effect = getEffect();
                        
                        String conditionpresent = effect.getStringValue("Effect.CONDITIONPRESENT");
                        String newconditionpresent = "FALSE";
                        
                        BattleEvent be = (BattleEvent)e.getSource();
                        Undoable u = new ValuePairUndoable(effect, "Effect.CONDITIONPRESENT",
                        conditionpresent, newconditionpresent);
                        effect.add( "Effect.CONDITIONPRESENT", "TRUE", true);
                        be.addUndoableEvent(u);
                        Target t = currentAbility.getSource();
                        Battle cb = Battle.getCurrentBattle();
                        try {
                            applySusceptibilityDamage(be, t, cb);
                        }
                        catch (BattleEventException bee) {
                        }
                    }
                };
                BattleEvent newbe = new BattleEvent( assignAction );
                Battle.currentBattle.addEvent(newbe);
            }
        };
        
        String conditionpresent = effect.getStringValue("Effect.CONDITIONPRESENT");
        if (conditionpresent == null || conditionpresent.equals("TRUE")) {
            v.add(triggerRemove);
        }
        else {
            v.add(triggerReplace);
        }
    }
    
    /** Getter for property battleEvent.
     * @return Value of property battleEvent.
     */
    public BattleEvent getBattleEvent() {
        return battleEvent;
    }
    
    /** Setter for property battleEvent.
     * @param battleEvent New value of property battleEvent.
     */
    public void setBattleEvent(BattleEvent battleEvent) {
        this.battleEvent = battleEvent;
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
    
    public void applySusceptibilityDamage(BattleEvent be, Target t, Battle cb)
    throws BattleEventException {
        String conditionpresent = this.getStringValue("Effect.CONDITIONPRESENT");
        if (conditionpresent != null && conditionpresent.equals("TRUE")) {
            
            try {
                String damagestat = this.getAbility().getStringValue("Disadvantage.DAMAGESTAT");
                String damagedie= this.getAbility().getStringValue("Disadvantage.DAMAGEDIE");
                
                Dice roll = new Dice( damagedie );
                
                Effect damage = new Effect( "Susceptibility Damage", "INSTANT" );
                if (t.isUnconscious()== false) {
                    if (damagestat.equals("STUN")) {
                        damage.addDamageSubeffect("StunFromSusceptibility", damagestat, roll.getStun().intValue(), "NONE", "NONE");
                    }
                    else {
                        damage.addDamageSubeffect("StunFromSusceptibility", damagestat, roll.getBody().intValue(), "NONE", "NONE");
                        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " takes BODY damage immediately from this Susceptibility.", BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " takes BODY damage immediately from this Susceptibility.", BattleEvent.MSG_NOTICE )); // .addMessage(t.getName() + " takes BODY damage immediately from this Susceptibility.", BattleEvent.MSG_NOTICE );
                    }
                }
                else {
                    if ( damagestat.equals("STUN")) {
                        damage.addDamageSubeffect("StunFromSusceptibility", damagestat, roll.getStun().intValue(), "NONE", "NONE");
                        damage.addDamageSubeffect("BodyFromDependence", "BODY", roll.getBody().intValue(), "NONE", "NONE");
                    }
                }
                be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " rolled " + roll.toString(), BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " rolled " + roll.toString(), BattleEvent.MSG_NOTICE )); // .addMessage(t.getName() + " rolled " + roll.toString(), BattleEvent.MSG_NOTICE );
                damage.addEffect(be,t);
                
                
                
                this.add("Effect.STARTTIME",new Long(cb.getTime().getTime()), true);
            }
            catch (BadDiceException bde) {
            }
        }
    }
}

