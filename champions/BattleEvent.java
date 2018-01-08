/*
 * BattleEvent.java
 *
 * Created on September 16, 2000, 6:14 PM
 */

package champions;

import champions.battleMessage.BattleMessage;
import champions.battleMessage.BattleMessageGroup;
import champions.battleMessage.DefaultBattleMessageGroup;
import champions.battleMessage.EffectSummaryMessage;
import champions.battleMessage.EmbeddedBattleEventMessageGroup;
import champions.battleMessage.LegacyBattleMessage;
import champions.battleMessage.StatChangeBattleMessage;
import champions.powers.advantageAreaEffect;
import champions.powers.advantageBasedOnECV;
import champions.powers.limitationCanBeMissileDeflected;
import champions.powers.maneuverMoveThrough;
import champions.powers.powerEgoAttack;
import champions.undoableEvent.ActivateUndoable;
import champions.enums.KnockbackEffect;
import champions.exception.BattleEventException;
import champions.filters.AndFilter;
import champions.interfaces.IndexIterator;
import champions.interfaces.Undoable;
import champions.undoableEvent.EffectUndoable;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.CombatLevelProvider;
import tjava.Filter;
import champions.powers.advantageExplosion;
import champions.undoableEvent.AbilityUndoable;
import champions.undoableEvent.AbilityActivationInfoLinkUndoable;
import champions.undoableEvent.ActivationInfoStateUndoable;
import champions.undoableEvent.ActiveTargetUndoable;
import champions.undoableEvent.AddEventUndoable;
import champions.undoableEvent.AttackDelayEvent;
import champions.undoableEvent.CombatStateUndoable;
import champions.undoableEvent.EmbedEventUndoable;
import champions.undoableEvent.PostTurnUndoable;
import champions.undoableEvent.PrephaseDoneUndoable;
import champions.undoableEvent.RosterUndoable;
import champions.undoableEvent.TargetActivationInfoLinkUndoable;
import champions.undoableEvent.TimeUndoable;
import champions.undoableEvent.FinishedProcessingUndoable;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import tjava.Destroyable;
import java.util.Vector;
import champions.SkillChallenge;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedWriter;
/** The BattleEvent class is the parent class for all battle event.
 *
 * BattleEvent was originally a non-object oriented class.  Hence, the main
 * BattleEvent class can be used for many different purposes, depending on
 * the type parameter. <P>
 *
 * Several newer BattleEvent subclasses exists (such as LinkedActivationBattleEvent
 * and GenericAbilityBattleEvent).  In order to maintain compatibility with
 * the older battleEvent type system, these battleEvent must also have appropriate
 * types defined in the main BattleEvent class.  (I know...that is crap and not
 * particularly extensible, but learn to live with it.)<p>
 *
 * <B>Property Change Events</B><P>
 * The Ability class only fires Property Change Events for a limited selection of Properties.  Changes in the follow
 * properties will trigger Property Change Events to be fired:<P>
 *
 * <I>Property          Description</I><P>
 * MESSAGE              A Change has occurred in the Messages.<P>
 *
 * @author  unknown
 * @version
 */
public class BattleEvent extends DetailList {
    
    static final long serialVersionUID = -5436122436130715913L;
    
    private static final int DEBUG = 0;
    // Event Types
    public static final int ACTIVATE = 1;
    public static final int DEACTIVATE = 2;
    public static final int ADVANCE_TIME = 3;
    public static final int ADD_EFFECT = 4;
    public static final int REMOVE_EFFECT = 5;
    public static final int ACTIVE_TARGET = 6;
    public static final int HOLD_TARGET = 7;
    public static final int PASS_TARGET = 8;
    public static final int ADD_ABILITY = 9;
    public static final int REMOVE_ABILITY = 10;
    public static final int DELAYED_ACTIVATE = 11;
    public static final int MANEUVER = 12;
    public static final int TRIGGER = 13;
    public static final int KNOCKBACK = 14;
    public static final int ACTION = 15;
    public static final int CONTINUE = 16;
    public static final int CHARGE_END = 17;
    public static final int LINKED_ACTIVATE = 18;
    public static final int GENERIC_ABILITY_ACTIVATE = 19;
    public static final int REMOVE_DELAYED_EVENT = 20;
    public static final int ADVANCE_SEGMENT_MARKER = 21;
    public static final int ROSTER_CHANGE_MARGER = 22;
    public static final int GUI_EVENT_MARKER = 23;
    public static final int CONFIGURE_BATTLE = 24;
    public static final int REPROCESS_EVENT = 25;
    public static final int RUNNABLE_EVENT = 26; // The newer object-orientent runnable event.  Must be subclass of RunnableBattleEvent.
    
    // Message Types
    public static final int MSG_COMBAT = 1;
    public static final int MSG_DEBUG = 2;
    public static final int MSG_ABILITY = 3;
    public static final int MSG_UTILITY = 4;
    public static final int MSG_ROSTER = 5;
    public static final int MSG_SEGMENT = 6;
    public static final int MSG_NOTICE = 7;
    public static final int MSG_HIT=8;
    public static final int MSG_MISS=9;
    public static final int MSG_DICE=10;
    public static final int MSG_END=11;
    
    protected transient HashSet updateList = null;
    protected boolean updating = true;
    
    /** Holds value of property action. */
    protected Action action;
    
    /** Holds value of ActivationInfo for faster access. */
    protected ActivationInfo activationInfo = null;
    
    protected int messageCount = 0;
    
    /** Holds value of property showAttackTree. */
    protected boolean showAttackTree = false;
    
    /** Holds value of property finishedProcessingEvent. */
    protected boolean finishedProcessingEvent = false;
    
    /** Holds value of property attackTreeDescriptionShown. */
    protected boolean attackTreeDescriptionShown;
    
    protected Chronometer timeProcessed;
    protected Chronometer timeParameter;
    
    protected int dex = 0;
    
    protected List<MessageEntry> messages;
    protected List<Undoable> undoables;
    
    protected boolean rolledBack = false;
    
    protected Boolean abilityDelayed = null;
    protected Boolean maneuverDelayed = null;
    
    private boolean reprocessingEvent = false;
    
    transient protected LinkedList<BattleMessageGroup> messageGroupList = null;
    protected BattleMessageGroup primaryBattleMessageGroup = null;

    transient protected HashMap<Target,CombatLevelList> combatLevelMap = null;
    
    /** Amount of time necessary to activate the ability.
     *
     */
    protected  String activationTime = null;

    protected java.util.Vector<SkillChallenge> skillChallengeList = null;
    
    
    /** Type of battleEvent.
     */
    protected int type;

	private Boolean _killing;
    
    /** Creates new BattleEvent */
    private BattleEvent() {
        setFireChangeByDefault(false);
    }
    
    /**
     * @param type
     * @param force  Should the time be forced to advance.
     */
    public BattleEvent(int type, boolean force) {
        setFireChangeByDefault(false);
        setType(type);
        setForcedAdvance(force);
    }
    
    /** Creates an Ability Activation battleEvent.
     *
     */
    public BattleEvent(Ability ability) {
        this(ability, false);
    }
    
    /** Creates a Ability Activation battleEvent.
     *
     * If the parameter abilityConstant is set to true, no modifiable ability
     * will be create and it will be assumed that the ability will only be
     * read and not modified by this battleEvent.  This is generally used for
     * getNameWithDamage which needs to setup a battleEvent to generate the
     * name, but doesn't want a new ability instance created in the process.
     *
     * @param ability  */
    public BattleEvent(Ability ability, boolean abilityConstant) {
        setFireChangeByDefault(false);
        setActivationInfo( new ActivationInfo() );
        setType( ACTIVATE );
        
        setAbility( ability , abilityConstant);
    }
    
    
    
    /**
     * @param ability
     * @param type  */
    public BattleEvent(Ability ability, int type) {
        this(ability);
        setType(type);
    }
    
    public BattleEvent(Action action, Chronometer time) {
        setFireChangeByDefault(false);
        setType(ACTION);
        setAction(action);
        setTimeParameter(time);
    }
    
    public BattleEvent(Action action) {
        setFireChangeByDefault(false);
        setType(ACTION);
        setAction(action);
    }
    
    /**
     * @param type
     * @param ai  */
    public BattleEvent(int type, ActivationInfo ai) {
        setFireChangeByDefault(false);
        this.setActivationInfo(ai);
        
        if ( ai.getIndexedSize( "Ability" ) == 1 ) {
            setAbility( (Ability)ai.getIndexedValue( 0, "Ability","ABILITY" ) );
        }
        if ( ai.getIndexedSize( "Ability" ) == 2 ) {
            setManeuver( (Ability)ai.getIndexedValue( 1, "Ability","ABILITY" ) );
        }
        setType(type);
    }
    
    /**
     * @param type
     * @param effect
     * @param target  */
    public BattleEvent(int type, Effect effect, Target target) {
        setFireChangeByDefault(false);
        setEffect(effect);
        setTarget(target);
        setType(type);
    }
    
    /**
     * @param type
     * @param target  */
    public BattleEvent(int type, Target target) {
        setFireChangeByDefault(false);
        setType(type);
        setTarget(target);
    }
    
    /**
     * @param ability
     * @param time
     * @param dex
     * @param source  */
    public BattleEvent(Ability ability, Chronometer time, int dex, Target source) {
        setFireChangeByDefault(false);
        setActivationInfo( new ActivationInfo() );
        setType(ACTIVATE);
        setAbility(ability);
        setTimeParameter(time);
        setDex(dex);
        setSource(source);
    }
    
    /**
     * @param type
     * @param ai
     * @param time
     * @param dex  */
    public BattleEvent(int type, ActivationInfo ai, Chronometer time, int dex) {
        setFireChangeByDefault(false);
        setActivationInfo(ai);
        setType(type);
        setTimeParameter(time);
        setDex(dex);
    }
    
    
    /**
     * @param type
     * @param target
     * @param kb
     * @param distance  */
    public BattleEvent(Chronometer time) {
        setFireChangeByDefault(false);
        setType(ADVANCE_TIME);
        setTimeParameter(time);
        setForcedAdvance(true);
    }
    
    /** BattleEvent for removing previously posted battleEvent.
     *
     */
    public BattleEvent(BattleEvent removeEvent) {
        setFireChangeByDefault(false);
        setType(REMOVE_DELAYED_EVENT);
        setBattleEventToRemove(removeEvent);
    }
    
    protected BattleEvent(int type) {
        setFireChangeByDefault(false);
        setType(type);
    }
    
    
    /**
     * @param ai  */
    public void setActivationInfo(ActivationInfo ai) {
        add("BattleEvent.ACTIVATIONINFO",  ai, true );
        activationInfo = ai;
    }
    
    /**
     * @return  */
    public ActivationInfo getActivationInfo() {
        return activationInfo;
    }
    
    /**
     * @param ability  */
    public void setAbility(Ability ability) {
        ActivationInfo ai = this.getActivationInfo();
        ai.setAbility(ability);
    }
    
    public void setAbility(Ability ability, boolean abilityConstant) {
        ActivationInfo ai = this.getActivationInfo();
        ai.setAbility(ability, abilityConstant);
    }
    
    /**
     * @return  */
    public Ability getAbility() {
        ActivationInfo ai = this.getActivationInfo();
        return (ai != null) ? ai.getAbility() : null;
    }
    
    /**
     * @param maneuver  */
    public void setManeuver(Ability maneuver) {
        ActivationInfo ai = this.getActivationInfo();
        ai.setManeuver(maneuver);
    }
    
    public void setManeuver(Ability maneuver, boolean abilityConstant) {
        ActivationInfo ai = this.getActivationInfo();
        ai.setManeuver(maneuver, abilityConstant);
    }
    
    /**
     * @return  */
    public Ability getManeuver() {
        ActivationInfo ai = this.getActivationInfo();
        return (ai != null) ? ai.getManeuver() : null;
    }
    
    /**
     * @return  */
    public boolean hasManeuver() {
        return getManeuver() != null;
    }
    
    public void setKillingAttack(Boolean k) {
    	_killing = k;
    }
    /**
     * @return  */
    public boolean isKillingAttack() {
        Ability maneuver = getManeuver();
        Ability ability = getAbility();
        if(_killing==null) {
        	if ( maneuver != null ) {
        		return ( maneuver.isKillingAttack() || ability.isKillingAttack() );
        	}
        
        	return   ability.isKillingAttack();
        }
        else {
        	return _killing;
        			
        }
        
    }
    
    /**
     * @return  */
    public boolean isNormalAttack() {
        Ability maneuver = getManeuver();
        Ability ability = getAbility();
        
        if ( maneuver != null ) {
            return ( maneuver.isNormalAttack() || ability.isNormalAttack() );
        }
        return   ability.isNormalAttack();
    }
    
    /**
     * @return  */
    public boolean isGrab() {
        Ability maneuver = getManeuver();
        Ability ability = getAbility();
        
        if ( maneuver != null ) {
            return ( maneuver.isGrab()  );
        }
        
        return   ability.isGrab();
    }
    
    /**
     * @return  */
    public double getDC() {
        Double d = getDoubleValue("Total.DC" );
        return d == null ? 0 : d.doubleValue();
    }
    
    /**
     * @param dc  */
    public void setDC(double dc) {
        add("Total.DC", new Double(dc),true,false);
    }
    
    /** Sets the time parameter for the battle event.
     *
     * @param time  */
    public void setTimeParameter(Chronometer time) {
        //add("BattleEvent.TIME",  time, true, false );
        this.timeParameter = time;
    }
    
    /** returns the time parameter.
     *
     *  The time parameter is a parameter need by some battle
     *  event to specify information about thier excecution.
     *
     *  The time parameter is not the time the battle event
     *  was actually executed by the battle engine.
     *
     * @return  */
    public Chronometer getTimeParameter() {
//        Object o =  getValue("BattleEvent.TIME" );
//        if ( o != null ) return (Chronometer)o;
//        return null;
        // return ability;
        return timeParameter;
    }
    
    /** Sets the time at which the battle event was processed.
     *
     *  This will be set by the battle engine when the event is processed.
     */
    public void setTimeProcessed(Chronometer c) {
        timeProcessed = c;
    }
    
    /** Return the time at which the event was processed.
     *
     */
    public Chronometer getTimeProcessed() {
        return timeProcessed;
    }
    
    /** Sets the Dex for ordering this battle event.
     * @param dex  */
    public void setDex(int dex) {
        //add("BattleEvent.DEX",  new Integer(dex),true, false);
        this.dex = dex;
    }
    
    /** Returns the Dex for ordering this battle event.
     * @return  */
    public int getDex() {
//        Integer i;
//        if ( (i = getIntegerValue("BattleEvent.DEX")) == null ) return 0;
//        else return i.intValue();
        return dex;
    }
    
    
    /**
     * @param error  */
    public void setError(String error) {
        add("BattleEvent.ERROR",  error,  true );
    }
    
    
    /**
     * @return  */
    public String getError() {
        return getStringValue("BattleEvent.ERROR");
    }
    
    /**
     * @return  */
    public String toString() {
        String type = "";
        
        switch ( getType() ) {
            case BattleEvent.ACTIVATE:
                type = "ACTIVATE";
                break;
            case BattleEvent.DEACTIVATE:
                type = "DEACTIVATE";
                break;
            case BattleEvent.DELAYED_ACTIVATE:
                type = "DELAYED_ACTIVATE";
                break;
            case BattleEvent.TRIGGER:
                type = "TRIGGER";
                break;
            case BattleEvent.MANEUVER:
                type = "MANEUVER";
                break;
            case BattleEvent.ADVANCE_TIME:
                type = "ADVANCE_TIME";
                break;
            case BattleEvent.ADD_EFFECT:
                type = "ADD_EFFECT";
                break;
            case BattleEvent.REMOVE_EFFECT:
                type = "REMOVE_EFFECT";
                break;
            case BattleEvent.ACTIVE_TARGET:
                type = "ACTIVE_TARGET";
                break;
            case BattleEvent.HOLD_TARGET:
                type = "HOLD_TARGET";
                break;
            case BattleEvent.PASS_TARGET:
                type = "PASS_TARGET";
                break;
            case BattleEvent.ACTION:
                type = "ACTION";
                break;
            case BattleEvent.CONTINUE:
                type = "CONTINUE";
                break;
            default:
                type = "UNKNOWN TYPE = " + Integer.toString(getType());
                break;
        }
        
        String s = "BattleEvent [Type: " + type + "(ActivationInfo: " + getActivationInfo() + ")";
        //s = s + super.toString();
        return s;
    }
    
    /** Getter for property type.
     * @return Value of property type.
     */
    public int getType() {
        //Integer i;
        //if ( (i = getIntegerValue("BattleEvent.TYPE")) == null ) return 0;
        // else return i.intValue();
        return type;
    }
    /** Setter for property type.
     * @param type New value of property type.
     */
    public void setType(int type) {
        //add("BattleEvent.TYPE",  new Integer(type),true);
        this.type = type;
    }
    
    /**
     * @param reason
     * @param end  */
    public void addExtraEnd( String reason, int end) {
        //if ( endList == null ) endList = new DetailList();
        
        int index = createIndexed(   "End","REASON", reason) ;
        addIndexed( index,   "End","END", new Integer( end ) )  ;
    }
    
    /**
     * @return  */
    public DetailList getEndList() {
        return this;
    }
    
    /**
     * @param type
     * @return  */
//    public int addGenericEvent( String type) {
//        int index = createIndexed( "Event","TYPE", type, false);
//        return index;
//    }
    
    /**
     * @param ability
     * @param activated
     * @return  */
    public int addActivateEvent( Ability ability, boolean activated) {
        //int index = addGenericEvent( "ACTIVATE");
        //addIndexed( index, "Event", "ABILITY", ability, true, false );
        //addIndexed( index, "Event", "ACTIVATED", (activated ? "TRUE" : "FALSE") , true, false);
        return addUndoableEvent( new ActivateUndoable(ability, activated) );
    }
    
    /**
     * @param effect
     * @param target
     * @param added
     * @return  */
    public int addEffectEvent(Effect effect, Target target, boolean added) {
////        int index = addGenericEvent( "EFFECT");
////        addIndexed( index, "Event", "EFFECT", effect, true, false );
////        addIndexed( index, "Event", "TARGET", target, true, false );
////        addIndexed( index, "Event", "ADDED", (added ? "TRUE" :"FALSE" ), true, false );
////        return index;
        return addUndoableEvent( new EffectUndoable(effect, target, added));
    }
    
    /**
     * @param ability
     * @param target
     * @param added
     * @return  */
    public int addAbilityEvent(Ability ability, Target target, boolean added) {
//        int index = addGenericEvent( "ABILITY");
//        addIndexed( index, "Event", "ABILITY", ability, true, false );
//        addIndexed( index, "Event", "TARGET", target, true, false);
//        addIndexed( index, "Event", "ADDED", (added ? "TRUE" :"FALSE" ), true, false );
//        return index;
        return addUndoableEvent( new AbilityUndoable(ability,target,added) );
    }
    
    /**
     * @param source
     * @param oldState
     * @param newState
     * @return  */
    public int addCombatStateEvent(Target source, CombatState oldState, CombatState newState) {
//        int index = addGenericEvent( "COMBAT_STATE");
//        addIndexed( index, "Event", "TARGET", source , true, false);
//        addIndexed( index, "Event", "OLDSTATE", oldState, true, false );
//        addIndexed( index, "Event", "NEWSTATE", newState, true, false);
//        return index;
        return addUndoableEvent( new CombatStateUndoable(source, oldState, newState) );
    }
    
    /**
     * @param source
     * @param state
     * @return  */
    public int addPostTurnEvent(Target source, boolean state) {
//        int index = addGenericEvent( "POST_TURN");
//        addIndexed( index, "Event", "TARGET", source, true, false);
//        addIndexed( index, "Event", "STATE", (state ? "TRUE" : "FALSE") , true, false);
//        return index;
        return addUndoableEvent( new PostTurnUndoable(source, state) );
    }
    
    /**
     * @param source
     * @param state
     * @return  */
    public int addPrephaseDoneEvent(Target source, boolean state) {
//        int index = addGenericEvent( "PREPHASE_DONE");
//        addIndexed( index, "Event", "TARGET", source, true, false);
//        addIndexed( index, "Event", "STATE", (state ? "TRUE" : "FALSE") , true, false);
//        return index;
        return addUndoableEvent(new PrephaseDoneUndoable(source, state));
    }
    
    /**
     * @param oldTarget
     * @param newTarget
     * @return  */
    public int addActiveTargetEvent(Target oldTarget, Target newTarget) {
//        int index = addGenericEvent( "TARGET_ACTIVE");
//        addIndexed( index, "Event", "OLDTARGET", oldTarget, true, false);
//        addIndexed( index, "Event", "NEWTARGET", newTarget, true, false);
//        return index;
        return addUndoableEvent( new ActiveTargetUndoable(oldTarget, newTarget) );
    }
    
    /**
     * @param oldState
     * @param newState
     * @return  */
    public int addTimeEvent(Chronometer oldTime, Chronometer newTime) {
//        int index = addGenericEvent( "TIME");
//        addIndexed( index, "Event", "OLDTIME", oldState, true, false);
//        addIndexed( index, "Event", "NEWTIME", newState, true, false);
//        return index;
        return addUndoableEvent( new TimeUndoable(oldTime, newTime) );
    }
    
    /**
     * @param roster
     * @param target
     * @param added
     * @return  */
    public int addRosterEvent(Roster roster, Target target, boolean added) {
//        int index = addGenericEvent( "ROSTER");
//        addIndexed( index, "Event", "ROSTER", roster, true, false);
//        addIndexed( index, "Event", "TARGET", target, true, false);
//        addIndexed( index, "Event", "ADDED", (added ? "TRUE" : "FALSE"), true, false );
//        return index;
        return addUndoableEvent( new RosterUndoable(roster, target, added) );
    }
    
    /**
     * @param target
     * @param value
     * @return  */
    public int addAttackDelayEvent(Target target, boolean value) {
//        int index = addGenericEvent( "ATTACKDELAYED");
//        addIndexed( index, "Event", "TARGET", target, true, false);
//        addIndexed( index, "Event", "VALUE", (value ? "TRUE" : "FALSE") , true, false);
//        return index;
        return addUndoableEvent( new AttackDelayEvent(target, value) );
    }
    
    /**
     * @param be
     * @param added
     * @return  */
//   /* public int addDelayedEvent(BattleEvent be, boolean added) {
//        int index = addGenericEvent( "DELAYEDEVENT");
//        addIndexed( index, "Event", "EVENT", be, true, false);
//        addIndexed( index, "Event", "ADDED", (added ? "TRUE" : "FALSE") , true, false);
//        return index;
//    } */
    
    /**
     * @param be
     * @param added
     * @return  */
    public int addAddEvent(BattleEvent be, boolean added) {
//        int index = addGenericEvent( "ADDEVENT");
//        addIndexed( index, "Event", "EVENT", be, true, false);
//        addIndexed( index, "Event", "ADDED", (added ? "TRUE" : "FALSE") , true, false);
//        return index;
        return addUndoableEvent( new AddEventUndoable(be, added) );
    }
    
    /**
     * @param ability
     * @param activationInfo
     * @param added
     * @return  */
    public int addActivationInfoEvent(Ability ability, ActivationInfo activationInfo, boolean added) {
//        int index = addGenericEvent( "AACTIVATIONINFO");
//        //addIndexed( index, "Event", "EVENT", be) , true, false);
//        addIndexed( index, "Event", "ABILITY", ability, true, false);
//        addIndexed( index, "Event", "ACTIVATIONINFO", activationInfo, true, false);
//        addIndexed( index, "Event", "ADDED", (added ? "TRUE" : "FALSE") , true, false);
//        return index;
        return addUndoableEvent( new AbilityActivationInfoLinkUndoable(ability, activationInfo, added) );
    }
    
    /**
     * @param target
     * @param activationInfo
     * @param added
     * @return  */
    public int addActivationInfoEvent(Target target, ActivationInfo activationInfo, boolean added) {
//        int index = addGenericEvent( "TACTIVATIONINFO");
//        //addIndexed( index, "Event", "EVENT", be) , true, false);
//        addIndexed( index, "Event", "TARGET", target, true, false);
//        addIndexed( index, "Event", "ACTIVATIONINFO", activationInfo, true, false);
//        addIndexed( index, "Event", "ADDED", (added ? "TRUE" : "FALSE") , true, false);
//        return index;
        return addUndoableEvent( new TargetActivationInfoLinkUndoable(target, activationInfo, added));
    }
    
    /**
     * @param source
     * @param oldState
     * @param newState
     * @return  */
    public int addAIStateEvent(ActivationInfo source, String oldState, String newState) {
//        int index = addGenericEvent( "AISTATE");
//        addIndexed( index, "Event", "ACTIVATIONINFO", source, true, false);
//        addIndexed( index, "Event", "OLDSTATE", oldState, true, false );
//        addIndexed( index, "Event", "NEWSTATE", newState, true, false);
//        return index;
        return addUndoableEvent( new ActivationInfoStateUndoable(source, oldState, newState));
    }
    
  /*  public int addStatCPChangeEvent(Target target, String stat, int oldCP, int newCP, boolean affectFigured) {
//        int index = addGenericEvent( "STATCPCHANGE");
//        //addIndexed( index, "Event", "EVENT", be) , true, false);
//        addIndexed( index, "Event", "TARGET", target, true, false);
//        addIndexed( index, "Event", "STAT", stat, true, false);
//        addIndexed( index, "Event", "OLDCP", new Integer(oldCP), true, false);
//        addIndexed( index, "Event", "NEWCP", new Integer(newCP), true, false);
//        addIndexed( index, "Event", "AFFECTFIGURED", (affectFigured ? "TRUE" : "FALSE"), true, false);
//        return index;
    } */
    
    /**
     * @param undoable
     * @return  */
    public int addMessageEvent(Undoable undoable) {
//        int index = addGenericEvent( "MESSAGE");
//        //addIndexed( index, "Event", "EVENT", be) , true, false);
//        addIndexed( index, "Event", "UNDOABLE", undoable, true, false);
//        return index;
        return addUndoableEvent(undoable);
    }
    
    public int addEmbedEventUndoable(BattleEvent embeddedEvent) {
//        int index = addGenericEvent( "EMBED");
//        //addIndexed( index, "Event", "EVENT", be) , true, false);
//        addIndexed( index, "Event", "BATTLEEVENT", embeddedEvent, true, false);
//        return index;
        return addUndoableEvent( new EmbedEventUndoable(this, embeddedEvent) );
    }
    
    /**
     * @param undoable
     * @return  */
    public int addUndoableEvent(Undoable undoable) {
        if ( undoable != null ) {
            if ( undoables == null ) undoables = new ArrayList<Undoable>();
            
            undoables.add(undoable);
            
            return undoables.size() - 1;
        } else {
            return -1;
        }
    }
    
    /** Embeds a BattleEvent into another BattleEvent.
     *
     * This method should be used when an existing, already processed BattleEvent
     * needs to be embedded into another BattleEvent.  This method does not actual
     * process the Event.  In order to process the event inline, the event typically
     * needs to be inserted into the AttackTree as a subnode.  Embedded events should
     * not be queue using Battle.addEvent() after they have been embedded.
     */
    public void embedBattleEvent(BattleEvent be) {
        if ( be == null ) {
            throw new IllegalArgumentException("Null BattleEvent can not be embedded!");
        }
        
        if ( be.getParentEvent() != null ) {
            throw new IllegalArgumentException("BattleEvent can not be embedded if Parent is Non-Null!");
        }
        
        // To embed an event, we must add it to the Messages Array...
        //int index = createIndexed( "Message","BATTLEEVENT", be,false);
        MessageEntry me = new EmbeddedBEMessageEntry(be);
        addMessageEntry(me);
        
        // We have to increase the messageCount by the messageCount of the embeded event...
        setMessageCount( getMessageCount() + be.getMessageCount() );
        
        // Finally, set the parent of the embedded event
        be.setParentEvent(this);
        
        EmbeddedBattleEventMessageGroup mg = new EmbeddedBattleEventMessageGroup(be);
        addBattleMessage(mg);
        
        // We also must add an undoable event to undo what we have done...
        addEmbedEventUndoable(be);
        
        
    }
    
    public void removeEmbeddedEvent(BattleEvent be) {
        if ( be == null ) {
            throw new IllegalArgumentException("Null BattleEvent can not be removed!");
        }
        
        // To embed an event, we must remove it to the Messages Array...
        //int index = findExactIndexed( "Message", "BATTLEEVENT", be);
        int index = findMessageEntry( new EmbeddedBEMessageEntry(be) );
        if ( index != -1 ) {
            //removeAllIndexed(index, "Message");
            removeMessageEntry(index);
            
            // Clear the parent of the Embedded Event
            be.setParentEvent(null);
            
            // Fix the messageCount...
            // We have to increase the messageCount by the messageCount of the embeded event...
            setMessageCount( getMessageCount() - be.getMessageCount() );
        }
        
        // This will probably leave an EmbedBattleEvent event in the stack...that should be removed also...
    }
    
    /**
     * @return  */
    public int getUndoableEventCount() {
        //return getIndexedSize("Event");
        if ( undoables == null ) return 0;
        else return undoables.size();
    }
    
    public Undoable getUndoableEvent(int index) {
        return undoables.get(index);
    }
    
    /**
     * @param toIndex  */
    public void partialRollbackBattleEvent(int toIndex) throws BattleEventException {
        // This rollbacks a battleEvent just to (and including) index toIndex.
        // Unlike rollbackBattleEvent, partialRollback actually dismantles
        // the events so no rollforward is posible.
        int index, count;
        String type;
        
        if ( DEBUG > 0 ) System.out.println("PartialRolling back to " + Integer.toString(toIndex));
        suspendUpdates();
        
        count = getUndoableEventCount();
        
        for ( index = count - 1; index >= toIndex && index >= 0; index -- ) {
            Undoable undoable = getUndoableEvent(index);
            
            if ( undoable instanceof MessageUndoable ) {
                // When you are doing a partial rollback, remove the messages too.
                // Don't do that for full rolls.
                undoable.undo();
            } else if ( undoable instanceof EmbedEventUndoable ) {
                // The Embedded event partial undoable requires special handling, as follows:
                BattleEvent embeddedBattleEvent = ((EmbedEventUndoable)undoable).getEmbeddedBattleEvent();
                embeddedBattleEvent.partialRollbackBattleEvent(0);
                removeEmbeddedEvent(embeddedBattleEvent);
            } else {
                undoable.undo();
            }
            
            //removeAllIndexed(index, "Event");
            undoables.remove(index);
        }
        
        resumeUpdates();
    }
    
    public void rollbackBattleEvent()  throws BattleEventException {
        // if ( Battle.currentBattle.dle != null ) Battle.currentBattle.dle.setDetailList( this.eventList );
        //System.out.println( "---------------------\nEVENT ROLLBACK:\n" + this.toString() );
        
        int index, count;
        String type;
        
        count = getUndoableEventCount();
        
        for ( index = count - 1; index >= 0; index -- ) {
            //type = getIndexedStringValue( index, "Event", "TYPE" );
            Undoable undoable = getUndoableEvent(index);
            
            if ( undoable instanceof MessageUndoable ) {
                // When you are doing a full rollback/rollforward, don't actually change the messages...
            } else {
                undoable.undo();
            }
        }
        //add("Effect.ROLLEDBACK", "TRUE", true);
        setRolledBack(true);
    }
    
    public void rollforwardBattleEvent()  throws BattleEventException {
        // System.out.println( "---------------------\nEVENT ROLLFORWARD:\n" + this.toString() );
        //if (Battle.currentBattle != null ) Battle.currentBattle.dle.setDetailList( this.eventList );
        int index, count;
        
        count = getUndoableEventCount();
        
        for ( index = 0; index < count; index ++ ) {
            //type = getIndexedStringValue( index, "Event", "TYPE" );
            Undoable undoable = getUndoableEvent(index);
            
            if ( undoable instanceof MessageUndoable ) {
                // When you are doing a full rollback/rollforward, don't actually change the messages...
            } else {
                undoable.redo();
            }
        }
        
        //add("Effect.ROLLEDBACK", "FALSE", true);
        setRolledBack(false);
    }
    /** Getter for property forcedAdvance.
     * @return Value of property forcedAdvance.
     */
    public boolean isForcedAdvance() {
        return getBooleanValue("BattleEvent.FORCEDADVANCE" );
    }
    /** Setter for property forcedAdvance.
     * @param forcedAdvance New value of property forcedAdvance.
     */
    public void setForcedAdvance(boolean forcedAdvance) {
        add("BattleEvent.FORCEDADVANCE",  (forcedAdvance ? "TRUE" : "FALSE" ), true, false);
    }
    /** Getter for property effect.
     * @return Value of property effect.
     */
    public Effect getEffect() {
        Object o =  getValue("BattleEvent.EFFECT" );
        if ( o != null ) return (Effect)o;
        return null;
    }
    /** Setter for property effect.
     * @param effect New value of property effect.
     */
    public void setEffect(Effect effect) {
        add("BattleEvent.EFFECT",  effect , true, false);
    }
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getSource() {
        if ( getActivationInfo() == null ) return null;
        
        return getActivationInfo().getSource();
    }
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setSource(Target target) {
        ActivationInfo ai = this.getActivationInfo();
        ai.setSource(target);
    }
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        Object o =  getValue("BattleEvent.TARGET" );
        if ( o != null ) return (Target)o;
        return null;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        add("BattleEvent.TARGET",  target, true, false);
        
        ActivationInfo ai = this.getActivationInfo();
        if(ai!=null) {
        	ai.setTarget(0, target);
        }
    }
    
    /** Getter for property target.
     *
     * The Delay target is used in conjunction with delayed events which have
     * a Dex value set to SEQUENCE_BEFORE_TARGET.
     *
     * @return Value of property target.
     */
    public Target getDelayTarget() {
        Object o =  getValue("BattleEvent.DELAYTARGET" );
        if ( o != null ) return (Target)o;
        return null;
    }
    /** Setter for property target.
     *
     * The Delay target is used in conjunction with delayed events which have
     * a Dex value set to SEQUENCE_BEFORE_TARGET.
     *
     * @param target New value of property target.
     */
    public void setDelayTarget(Target target) {
        add("BattleEvent.DELAYTARGET",  target, true, false);
    }
    
    /**
     * @param msg
     * @param type  */
    private void addMessage(String msg, int type) {
        //int index = createIndexed( "Message","TEXT", msg,false);
        //addIndexed(index, "Message","TYPE", new Integer(type),true, false);
        int index = addMessageEntry( new TextMessageEntry(msg,type) );
        
        // Increate the message Count by 1...
        setMessageCount( getMessageCount() + 1 );
        
        // This should really be a propertyChange with Property of "MESSAGE" eventually...
        // fireIndexedChanged( "Message" );
        
        if ( DEBUG > 0 ) System.out.println(msg);
        
        MessageUndoable mu = new MessageUndoable(this, index, msg, type);
        addMessageEvent(mu);
        
        //addBattleMessage(new LegacyBattleMessage(msg,type));
        
        FileWriter writer;
		
		try {
			if(msg!=null) {
				writer = new FileWriter(VirtualDesktop.Controller.GLOBALS.EXPORT_PATH + "MessageLog.info",true);
				if(msg.contains("activates")) {
					writer.write("-----------------------------------------------------");	
					writer.write(System.lineSeparator());
					}
				writer.write(msg);
				writer.write(System.lineSeparator());
				writer.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
    private void addMessage(BattleMessage message, int type) {
    	String msg = message.getMessage();
   
    	if(message.getClass() ==StatChangeBattleMessage.class) {
    		if(msg.contains("took")) {
    			Target t = ((StatChangeBattleMessage)message).getTarget();
    			if(t.stunned==true || t.isDying()==true ||t.isDead()==true ||t.isUnconscious()==true) {
	    			msg+=" and is ";
	    			if(t.stunned==true){
	    				msg+= " is Stunned";
					}
	    			if(t.unconscious==true){
	    				msg+= ", Unconsious";
					}
	    			if(t.isDying()==true){
	    				msg+= ", Dying";
					}
	    			if(t.isDead()==true){
						msg+= ", Dead";
					}	
    			}
    		}
    	}
    	
    	addMessage(msg, type);
    }
    
    public void addBattleMessage(BattleMessage message) {
        // This will eventually replace the above...but for now, we are just going
        // to build this the message newGroup struture in parallel
    	
        BattleMessageGroup messageGroup = getCurrentMessageGroup();
        if ( messageGroup == null ) {
            messageGroup = setupInitialMessageGroup();
        }
        
      //  System.out.println("added message " + message + " to " + messageGroup);
            
        messageGroup.addMessage(message);
        
        BattleMessageUndoable mu = new BattleMessageUndoable(messageGroup, message);
        addMessageEvent(mu);
        
        if ( message instanceof EmbeddedBattleEventMessageGroup == false ) {
            if ( message instanceof LegacyBattleMessage ) {
                addMessage(message, ((LegacyBattleMessage)message).getMessageType() );
            }
            else {
                addMessage(message, MSG_NOTICE);
            }
	         //VirtualDesktop.Legacy.MessageExporter.exportMessage(message,messageGroup);
        }
    }
    
    
    
    
    public void openMessageGroup(BattleMessageGroup messageGroup) {
        //System.out.println("Opening Group " + messageGroup);
        if ( messageGroupList == null ) messageGroupList = new LinkedList<BattleMessageGroup>();
        
        BattleMessageGroup currentMessageGroup = getCurrentMessageGroup();
        if ( currentMessageGroup != null ) {
            currentMessageGroup.addMessage(messageGroup);
            
            BattleMessageUndoable mu = new BattleMessageUndoable(currentMessageGroup, messageGroup);
            addMessageEvent(mu);
        }
       
        
        if ( primaryBattleMessageGroup == null ) {
            setPrimaryBattleMessageGroup(messageGroup);
        }
        
        addUndoableEvent( new OpenBattleGroupUndoable(this, messageGroup));
        
        messageGroupList.addFirst(messageGroup);
    }
    
    /**
     * Returns whether the indicated newGroup is currently open.
     */
    public boolean isMessageGroupOpen(BattleMessageGroup messageGroup) {
        return messageGroupList != null && messageGroupList.contains(messageGroup);
    }
    
    //public static class SetPrim
    
    /**
     * Close all groups, upto and including the indicated message newGroup.
     * 
     * 
     * @param messageGroup Message newGroup to close, if null close only the last newGroup.
     */
    public void closeMessageGroup(BattleMessageGroup messageGroup) {
        
        if ( messageGroupList != null && messageGroupList.size() != 0 ) {
            if ( messageGroup != null && messageGroupList.contains(messageGroup) == false ) {
                throw new IllegalArgumentException("Attempt to close message group that is not currently open: " + messageGroup);
            }
            
            BattleMessageGroup firstGroup = messageGroupList.removeFirst(); 
            while(firstGroup != null) {
                
                firstGroup.closeGroup();
                
                addUndoableEvent( new CloseBattleGroupUndoable(this, firstGroup));
                
                //System.out.println("Closing Group " + firstGroup);
                
                if ( messageGroup == null || messageGroup == firstGroup ) break;
                
                if ( messageGroupList.size() > 0 ) {
                    firstGroup = messageGroupList.removeFirst();
                }
                else { 
                    firstGroup = null;
                }
            }
        }
    }
    
    public BattleMessageGroup getCurrentMessageGroup() {
        if ( messageGroupList == null || messageGroupList.size() == 0 ) {
            return null;
        }
        
        return messageGroupList.getFirst();
    }
    
    protected BattleMessageGroup setupInitialMessageGroup() {
        BattleMessageGroup group = new DefaultBattleMessageGroup("BattleEvent root battle message group");
        openMessageGroup(group);
        
        return group;
    }

    public BattleMessageGroup getPrimaryBattleMessageGroup() {
        return primaryBattleMessageGroup;
    }

    public void setPrimaryBattleMessageGroup(BattleMessageGroup primaryBattleMessageGroup) {
        if ( this.primaryBattleMessageGroup != primaryBattleMessageGroup ) {
            BattleMessageGroup oldGroup = this.primaryBattleMessageGroup;
            
            this.primaryBattleMessageGroup = primaryBattleMessageGroup;
            addUndoableEvent( new SetPrimaryBattleGroupUndoable(this, oldGroup, primaryBattleMessageGroup));
            
        }
    }
    
    
    
    public void suspendUpdates() {
        updating = false;
    }
    
    public void resumeUpdates() {
        updating = true;
        
        triggerUpdate();
    }
    
    private void triggerUpdate() {
        if ( updateList != null && updateList.size() != 0 ) {
            int index, count;
            String indexName;
            Iterator i = updateList.iterator();
            while ( i.hasNext() ) {
                indexName = (String)i.next();
                //System.out.println("Firing Update on " + indexName + ".");
                super.fireIndexedChanged(indexName);
                i.remove();
            }
        }
    }
    
    public boolean isUpdating() {
        return updating;
    }
    
    public boolean isEmbedded() {
        return (getParentEvent() != null);
    }
    
    public void fireIndexedChanged( String indexName ) {
        if ( updating == true ) {
            super.fireIndexedChanged(indexName);
        } else {
            if ( updateList == null ) updateList = new HashSet();
            updateList.add(indexName);
        }
    }
    
    /**
     * @param paid  */
    public void setENDPaid(boolean paid) {
        add("BattleEvent.ENDPAID",  (paid?"TRUE":"FALSE"),true,false);
    }
    
    /**
     * @return  */
    public boolean isENDPaid() {
        return getBooleanValue("BattleEvent.ENDPAID" );
    }
    
    /**
     * @param bee  */
    static public void displayBattleError(final BattleEventException bee ) {
        
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(null, bee.getMessage(), "Battle Event Message", JOptionPane.OK_OPTION );
            }
        });
    }
    
    /**
     * @param be  */
//    public void embedBattleEvent(BattleEvent be) {
//        int index, count;
//        int nindex;
//
//        int startCount = getIndexedSize( "Event" );
//        count = be.getIndexedSize( "Event" );
//        String eventType;
//        for ( index = 0; index < count; index ++ ) {
//            eventType = be.getIndexedStringValue( index, "Event","TYPE");
//
//            if ( eventType.equals("MESSAGE") ) {
//                // This is a message, so we have to handle it slightly different...
//                // The message should be extracted from the MessageUndoable and added to the
//                // the new BattleEvent with the normal addBattleMessage mechanism
//                MessageUndoable mu = (MessageUndoable)be.getIndexedValue(index, "Event", "UNDOABLE");
//                this.addBattleMessage( mu.getMessage(), mu.getMessageType());
//
//            }
//            else {
//                nindex = createIndexed( "Event", "TYPE", eventType, false);
//
//                // This section copies the additional Event information into the new BattleEvent
//                Iterator i = be.keySet().iterator();
//                String key, type, keyName;
//                int end;
//
//                keyName = "Event" + Integer.toString(index) + ".";
//
//                while ( i.hasNext() ) {
//                    key = (String)i.next();
//                    if ( key.startsWith( keyName ) && key.endsWith( ".TYPE" ) == false) {
//                        end = key.indexOf(".");
//                        try {
//                            nindex = Integer.parseInt( key.substring(5,end) );
//                            type = key.substring(end+1);
//                            addIndexed( nindex + startCount, "Event",type, be.getValue( key ), false);
//
//                        }
//                        catch (NumberFormatException nfe) {
//                            System.out.println( "Error duplicating Key: " + key);
//                        }
//                    }
//                }
//            }
//
//        }
//
//        fireIndexedChanged("Message");
//    }
    
    /**
     * @param desc
     * @param value
     * @return  */
  /*  public int addKBModifier( String desc, String targetGroup, int value ) {
        ActivationInfo ai = getActivationInfo();
        String kbGroup = ai.getKnockbackGroup(targetGroup);
        int kbindex = getKn
        KnockbackModifiersList kml = getKnockbackModifiersList(kbindex
   
        return index;
    }  */
    
    /**
     * @param value  */
  /*  public void setKBBase( int value ) {
        add("KnockbackRoll.BASE",  new Integer(value),true);
    } */
    
    /**
     * @return  */
    public int getKBBase() {
        Integer i;
        if ( (i = getIntegerValue("KnockbackRoll.BASE" ) ) == null ) {
            return 2;
        } else {
            return i.intValue();
        }
    }
    
    /**
     * @return  */
    public int getTotalKB() {
        int total = getKBBase();
        Integer value;
        
        int count,index;
        
        count = getIndexedSize( "KnockbackRoll" );
        // Find the ability knockback...
        for(index=0;index<count;index++) {
            value = getIndexedIntegerValue( index,"KnockbackRoll","VALUE" );
            if ( value != null ) {
                total += value.intValue();
            }
            
        }
        return total;
    }
    
 /*   public void setupKnockback() {
        int count, index,index2;
        String type,desc,special;
        Object value;
        boolean active;
        Ability ability;
  
        setKBBase(2);
  
        if ( ( ability = getAbility() ) != null ) {
            count = ability.getIndexedSize( "KnockbackRoll" );
            // Find the ability knockback...
            for(index=0;index<count;index++) {
                value = ability.getIndexedValue( index,"KnockbackRoll","VALUE" );
                desc = ability.getIndexedStringValue( index,"KnockbackRoll","DESCRIPTION");
                active = ability.getIndexedBooleanValue( index, "KnockbackRoll", "ACTIVE" );
  
                index2 = createIndexed( "KnockbackRoll","VALUE",value,false);
                addIndexed( index2, "KnockbackRoll","DESCRIPTION",desc, true,false);
                addIndexed( index2, "KnockbackRoll","ACTIVE", active?"TRUE":"FALSE", true, false);
            }
        }
  
        if ( ( ability = getManeuver() ) != null ) {
            count = ability.getIndexedSize( "KnockbackRoll" );
            // Find the ability knockback...
            for(index=0;index<count;index++) {
                value = ability.getIndexedValue( index,"KnockbackRoll","VALUE" );
                desc = ability.getIndexedStringValue( index,"KnockbackRoll","DESCRIPTION");
                active = ability.getIndexedBooleanValue( index, "KnockbackRoll", "ACTIVE" );
  
                index2 = createIndexed( "KnockbackRoll","VALUE",value,false);
                addIndexed( index2, "KnockbackRoll","DESCRIPTION",desc, true,false);
                addIndexed( index2, "KnockbackRoll","ACTIVE",(active?"TRUE":"FALSE"), true,false);
            }
        }
  
        if ( ( index = findIndexed("KnockbackRoll","DESCRIPTION","Generic") ) == -1 ) {
            index = createIndexed(0,"KnockbackRoll","DESCRIPTION","Generic",false);
            addIndexed( index, "KnockbackRoll","VALUE",new Integer(0), true, false);
            addIndexed( index, "KnockbackRoll","ACTIVE", "TRUE", true, false);
        }
    } */
    
    /**
     * @param what
     * @return  */
    public boolean is( String what ) {
        Ability ability = getAbility();
        Ability maneuver = getManeuver();
        if ( getBooleanValue("Ability.IS" + what ) == true ) return true;
        if ( ability != null && ability.is( what ) == true ) return true;
        if ( maneuver != null && maneuver.is( what ) == true ) return true;
        return false;
    }
    
    /**
     * @param what
     * @return  */
    public boolean can( String what ) {
        Ability ability = getAbility();
        Ability maneuver = getManeuver();
        if ( getBooleanValue("Ability.CAN" + what ) == true ) return true;
        if ( ability!= null && ability.can( what ) == true ) return true;
        if ( maneuver!= null && maneuver.can( what ) == true ) return true;
        return false;
    }
    
    /**
     * @param what
     * @param replace  */
    public void setIs(String what, boolean replace ) {
        add("Ability.IS" + what,"TRUE",  replace, false);
    }
    
    /**
     * @param what
     * @param replace  */
    public void setCan( String what, boolean replace ) {
        add("Ability.CAN" + what,"TRUE", replace, false);
    }
    
    /**
     * @param what
     * @param replace  */
    public void setIsnot(String what, boolean replace) {
        add("Ability.IS" + what,"FALSE", replace, false);
    }
    
    /**
     * @param what
     * @param replace  */
    public void setCannot( String what, boolean replace) {
        add("Ability.CAN" + what,"FALSE", replace, false);
    }
    
    /**
     * @return  */
    public boolean isMeleeAttack() {
        Ability ability = getAbility();
        return ability.isMeleeAttack();
    }
    
    public boolean isRangedAttack() {
        Ability ability = getAbility();
        return ability.isRangedAttack();
    }
    
    /** Returns whether the event can be potentially blocked.
     *
     */
    public boolean isBlockable() {
        Ability ability = getAbility();
        
        return ability.isMeleeAttack() && 
                ability.hasAdvantage( advantageAreaEffect.advantageName ) == false && 
                ability.hasAdvantage( advantageExplosion.advantageName ) == false;
    }
    
    /** Returns whether the event can be potentially deflected.
     *
     */
    public boolean isDeflectable() {
        Ability ability = getAbility();
        
        return ability.isRangedAttack() && 
                (
                    (ability.hasAdvantage( advantageAreaEffect.advantageName ) == false && 
                    ability.hasAdvantage( advantageExplosion.advantageName ) == false)
                ||
                    ability.hasLimitation( limitationCanBeMissileDeflected.limitationName )
                );
    }
    
    /**
     * @return  */
    public boolean strengthAddsToDC() {
        Ability ability = getAbility();
        Ability maneuver = getManeuver();
        
        if ((ability != null && ability.strengthAddsToDC() == false) || (maneuver != null && maneuver.strengthAddsToDC() == false))
            return false;
        else
            return true;
    }
    
    /**
     * @return  */
    public boolean doesBody() {
        Ability a = getAbility();
        Ability m = getManeuver();
        
        if ( ( a != null && a.contains("Ability.DOESBODY" ) && a.getBooleanValue("Ability.DOESBODY" ) == false )
        || ( m != null && m.contains("Ability.DOESBODY" ) && m.getBooleanValue("Ability.DOESBODY" ) == false ) ) {
            return false;
        }
        
        return true;
    }
    
    /** Indicates that this ability does ECV Based body (EBODY).
     *
     * ECV Based body is only generated by Ego Attack and powers with the
     * Based On ECV advantage.
     *
     * ECV Based body doesn't not cause damage to most targets.  Only
     * special ECV entities (such as mental paralysis entangle) care about
     * ECV Based body.
     */
    public boolean doesECVBody() {
        Ability a = getAbility();
        
        return ( a.getPower() instanceof powerEgoAttack || a.hasAdvantage( advantageBasedOnECV.advantageName));
    }
    
    /**
     * @return  */
    public boolean doesStun() {
        Ability a = getAbility();
        Ability m = getManeuver();
        
        if ( ( a != null && a.contains("Ability.DOESSTUN" ) && a.getBooleanValue("Ability.DOESSTUN" ) == false )
        || ( m != null && m.contains("Ability.DOESSTUN" ) && m.getBooleanValue("Ability.DOESSTUN" ) == false ) ) {
            return false;
        }
        
        return true;
    }
    
    /**
     * @return  */
    public boolean doesKnockback() {
        Ability a = getAbility();
        Ability m = getManeuver();
        
        if ( ( a != null && a.contains("Ability.DOESKNOCKBACK" ) && a.getBooleanValue("Ability.DOESKNOCKBACK" ) == false )
        || ( m != null && m.contains("Ability.DOESKNOCKBACK" ) && m.getBooleanValue("Ability.DOESKNOCKBACK" ) == false ) ) {
            return false;
        }
        
        return true;
    }
    
    /**
     * @return  */
    public boolean isNND() {
        Ability a = getAbility();
        Ability m = getManeuver();
        
        if ( ( a != null && a.isNND() )
        || ( m != null &&  m.isNND() ) ) {
            return true;
        }
        
        return false;
    }
    
    public boolean usesHitLocation() {
        Ability a = getAbility();
        Ability m = getManeuver();
        
        if ( ( a != null && a.getBooleanValue("Ability.USESHITLOCATION" ) )
        || ( m != null &&  m.getBooleanValue("Ability.USESHITLOCATION" ) ) ) {
            return true;
        }
        
        return false;
    }
    
    /**
     * @return  */
    public String getNNDAttackType() {
        
        Ability a = getAbility();
        Ability m = getManeuver();
        
        if ( a != null && a.isNND() ) return a.getStringValue("NND.ATTACKTYPE");
        if ( m != null && m.isNND() ) return m.getStringValue("NND.ATTACKTYPE");
        return "";
    }
    
    /**
     * @return  */
    public String getNNDDefense() {
        String nndDefense = null;
        
        Ability a = getAbility();
        Ability m = getManeuver();
        
        if ( a != null && a.isNND() ) nndDefense = a.getNNDDefense();
        if ( nndDefense == null && m != null && m.isNND() ) nndDefense = m.getNNDDefense();
        return nndDefense == null ? "" : nndDefense;
    }
    
    /**
     * @param l  */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        //System.out.println( l + " wants to listen to " + this );
        super.addPropertyChangeListener(l);
    }
    
    /**
     * @param name
     * @param targetGroup
     * @param description
     * @param size
     * @return  */
    public int addDiceInfo(String name, String targetGroup, String description, String size) {
        int dindex = getDiceIndex(name, targetGroup);
        if ( dindex == -1 ) {
            dindex = createIndexed("Die","NAME",name);
            addIndexed(dindex,"Die", "TARGETGROUP", targetGroup, true);
        }
        addIndexed(dindex,"Die", "DESCRIPTION", description, true);
        addIndexed(dindex,"Die", "SIZE", size, true);
        
        return dindex;
    }
    
    public int addDiceInfo(String name, String targetGroup,String description, String size, String diceType, String stunLabel, String bodyLabel) {
        int dindex = getDiceIndex(name, targetGroup);
        if ( dindex == -1 ) {
            dindex = createIndexed("Die","NAME",name);
            addIndexed(dindex,"Die", "TARGETGROUP", targetGroup, true);
        }
        addIndexed(dindex,"Die", "SIZE", size, true);
        addIndexed(dindex,"Die", "DESCRIPTION", description, true);
        addIndexed(dindex,"Die", "TYPE", diceType, true);
        addIndexed(dindex,"Die", "STUNLABEL", stunLabel, true);
        addIndexed(dindex,"Die", "BODYLABEL", bodyLabel, true);
        
        return dindex;
    }
    
    /**
     * @param name
     * @param targetGroup
     * @param dice
     * @return  */
    public int setDiceRoll(String name, String targetGroup, Dice dice) {
        int dindex = getDiceIndex(name, targetGroup);
        if ( dindex != -1 ) {
            addIndexed(dindex,"Die", "ROLL", dice, true);
        }
        
        return dindex;
    }
    
    /**
     * @param name
     * @param targetGroup
     * @return  */
    public Dice getDiceRoll(String name, String targetGroup) {
        int dindex = getDiceIndex(name, targetGroup);
        if ( dindex != -1 ) {
            return getIndexedDiceValue(dindex, "Die", "ROLL");
        }
        return null;
    }
    
    /**
     * @param dindex
     * @param dice
     * @return  */
    public int setDiceRoll(int dindex, Dice dice) {
        if ( dindex != -1 ) {
            addIndexed(dindex,"Die", "ROLL", dice, true);
        }
        
        return dindex;
    }
    
    /**
     * @param dindex
     * @return  */
    public Dice getDiceRoll(int dindex) {
        if ( dindex != -1 ) {
            return getIndexedDiceValue(dindex, "Die", "ROLL");
        }
        return null;
    }
    
    /**
     * @param dindex
     * @param autoroll
     * @return  */
    public void setDiceAutoRoll(int dindex, boolean autoroll) {
        addIndexed(dindex,"Die", "AUTOROLL", autoroll?"TRUE":"FALSE", true);
    }
    
    /**
     * @param dindex
     * @param targetGroup
     * @return  */
    public boolean getDiceAutoRoll(int dindex) {
        String auto = null;
        if ( dindex != -1 ) {
            auto = getIndexedStringValue(dindex, "Die", "AUTOROLL");
        }
        return auto != null ? ( auto.equals("TRUE") ) : true;
    }
    
    /**
     * @param name
     * @param targetGroup
     * @param autoroll
     * @return  */
    public int setDiceAutoRoll(String name, String targetGroup, boolean autoroll) {
        int dindex = getDiceIndex(name, targetGroup);
        if ( dindex != -1 ) {
            addIndexed(dindex,"Die", "AUTOROLL", autoroll?"TRUE":"FALSE", true);
        }
        
        return dindex;
    }
    
    /**
     * @param name
     * @param targetGroup
     * @return  */
    public boolean getDiceAutoRoll(String name, String targetGroup) {
        String auto = null;
        int dindex = getDiceIndex(name, targetGroup);
        if ( dindex != -1 ) {
            auto = getIndexedStringValue(dindex, "Die", "AUTOROLL");
        }
        return auto != null ? ( auto.equals("TRUE") ) : true;
    }
    
    
    /**
     * @param dindex
     * @param autoroll
     * @return  */
    public void setDiceDescription(int dindex, String desciption) {
        addIndexed(dindex,"Die", "DESCRIPTION", desciption, true);
    }
    
    /**
     * @param dindex
     * @param targetGroup
     * @return  */
    public String getDiceDescription(int dindex) {
        return getIndexedStringValue(dindex, "Die", "DESCRIPTION");
    }
    
    /**
     * @param dindex
     * @param autoroll
     * @return  */
    public void setDiceSize(int dindex, String size) {
        addIndexed(dindex,"Die", "SIZE", size, true);
    }
    
    /**
     * @param dindex
     * @param targetGroup
     * @return  */
    public String getDiceSize(int dindex) {
        return getIndexedStringValue(dindex, "Die", "SIZE");
    }
    
    /**
     * @param name
     * @param targetGroup
     * @return  */
    public int getDiceIndex(String name, String targetGroup) {
        String foundTargetGroup;
        int dindex = findIndexed("Die", "NAME", name);
        while ( dindex != -1 ) {
            foundTargetGroup = getIndexedStringValue(dindex, "Die", "TARGETGROUP");
            if ( foundTargetGroup != null && foundTargetGroup.equals(targetGroup) ) {
                break;
            }
            dindex = findIndexed(dindex+1,"Die", "NAME", name);
        }
        return dindex;
    }
    
    /**
     * @param name
     * @param targetGroup  */
    public void removeDice(String name, String targetGroup) {
        int dindex = getDiceIndex(name, targetGroup);
        if ( dindex != -1 ) {
            removeAllIndexed(dindex, "Die");
        }
    }
    
    /**
     * @param targetGroup
     * @return  */
    public IndexIterator getDiceIterator(String targetGroup) {
        return getIteratorForIndex("Die", "TARGETGROUP", targetGroup);
    }
    
    /**
     * Adds knockback information to the BattleEvent, creating an entry if necessary.
     * 
     * addTargetKnockback adds a knockback entry to the BattleEvent.  If a knockback
     * entry already exists, the body and count are adjusted appropriated.
     * 
     * 
     * @param target Target to add knockback information to.
     * @param knockbackGroup The knockback newGroup which is being processed.
     * @param bodyDone The amount of body caused by the attack which could cause knockback.
     * @return An Undoable which can be used to remove the added knockback.
     */
    public KnockbackUndoable addKnockbackBody(Target target, String knockbackGroup, int bodyDone) {
        KnockbackUndoable ku = new KnockbackUndoable(this, target, knockbackGroup);
        
        int kbindex = getKnockbackIndex(target, knockbackGroup);
        
        if ( kbindex == -1 ) {
            ku.setStartingBody(0);
            
            kbindex = createIndexed("Knockback", "TARGET", target);
            addIndexed(kbindex, "Knockback", "KNOCKBACKGROUP", knockbackGroup, true);
        } else {
            ku.setStartingBody( getKnockbackBody(kbindex) );
        }
        
        int currentBody = getKnockbackBody(kbindex);
        int currentCount = getKnockbackCount(kbindex);
        
        
        if ( bodyDone > currentBody ) {
            setKnockbackBody(kbindex, bodyDone);
            ku.setEndingBody(bodyDone);
        } else {
            ku.setEndingBody(currentBody);
        }
        
        setKnockbackCount(kbindex, currentCount + 1);
        
        return ku;
    }
    
    /**
     * Returns the Index of the Knockback entry in the BattleEvent with the indicated target in the indicated knockback newGroup.
     * 
     * 
     * @param target Target to be found.
     * @param knockbackGroup Knockback newGroup to be found.
     * @return The index of the Knockback entry in the BattleEvent.
     */
    public int getKnockbackIndex(Target target, String knockbackGroup) {
        int kbindex;
        String group;
        
        kbindex = findIndexed("Knockback", "TARGET", target);
        while ( kbindex != -1 ) {
            group = getIndexedStringValue(kbindex,"Knockback","KNOCKBACKGROUP");
            if ( group != null && group.equals(knockbackGroup) ) {
                break;
            }
            kbindex = findIndexed(kbindex + 1, "Knockback", "TARGET", target);
        }
        
        return kbindex;
    }
    
    public Target getKnockbackTarget(int tindex) {
        return (Target)getIndexedValue(tindex, "Knockback", "TARGET");
    }
    
    /**
     * Returns the total amount of knockback causing body which was caused by a series of attacks.
     *
     * The total knockback causing body is the body of the most damaging attack, plus one for each
     * additional attack (in the case of autofire).  This number is the basis for calculating the
     * actual knockback caused by the attack.
     *
     * @param target Target of knockback entry.
     * @param knockbackGroup KnockbackGroup of knockback entry.
     * @return Total Body for use in knockback calculation.
     */
    public int getTotalKnockback(int kbindex) {
        if ( kbindex == -1 ) {
            return 0;
        } else {
            int count = getKnockbackCount(kbindex);
            int body = getKnockbackBody(kbindex);
            
            return (count > 0 ) ? body + count - 1 : 0;
        }
    }
    
    /**
     * Returns an IndexIterator which contains all of the Target indexes for a particular knockbackGroup.
     *
     * @param knockbackGroup KnockbackGroup to be iterated through.
     * @return IndexIterator. */
    public IndexIterator getKnockbackTargets(String knockbackGroup) {
        return getIteratorForIndex("Knockback", "KNOCKBACKGROUP", knockbackGroup);
    }
    
    /**
     * Get the current maximum amount of body done by any single attack in the indicated knockback newGroup.
     * 
     * 
     * @param kbindex The index of the Knockback entry in the BattleEvent.
     * @return The body of the largest knockback eligible attack.
     */
    public int getKnockbackBody(int kbindex) {
        int body = 0;
        
        if ( kbindex != -1 ) {
            Integer i = getIndexedIntegerValue(kbindex, "Knockback", "BODY");
            if ( i != null ) body = i.intValue();
        }
        return body;
    }
    
    /**
     * Returns the number of knockback causing attacks which occurred to target.
     *
     * @param kbindex The index of the Knockback entry in the BattleEvent.
     * @return The number of knockback eligible attacks which occurred.
     */
    public int getKnockbackCount(int kbindex) {
        int count = 0;
        
        if ( kbindex != -1 ) {
            Integer i = getIndexedIntegerValue(kbindex, "Knockback", "COUNT");
            if ( i != null ) count = i.intValue();
        }
        return count;
    }
    
    /**
     * Sets the maximum body for the Knockback entry.
     * 
     * The method sets the body used in calculating the total knockback eligible
     * body.  It should always be set to the amount of body caused by the most
     * damaging attack in a particular knockback newGroup.
     * 
     * In general, the method should not be called directly.  Instead, addTargetKnockback
     * should be used.  However, there are situations in which it is necessary, such as
     * in knockback undo/redo situations.
     * 
     * 
     * @param kbindex kbindex The index of the Knockback entry in the BattleEvent.
     * @param body Body of best knockback eligible attack.
     */
    public void setKnockbackBody(int kbindex, int body) {
        addIndexed(kbindex, "Knockback", "BODY", new Integer(body), true);
    }
    
    /**
     * Sets the number of knockback eligible attacks which occurred.
     *
     * In general, this method should not be called directly.  Instead, addTaretKnockback
     * should be used.  However, there are situations in which it is necessary, such as
     * in knockback undo/redo situations.
     *
     * @param kbindex kbindex The index of the Knockback entry in the BattleEvent.
     * @param count Nubmer of knockback eligible attacks which occurred. */
    public void setKnockbackCount(int kbindex, int count) {
        addIndexed(kbindex, "Knockback", "COUNT", new Integer(count), true);
    }
    
    /**
     * Gets the KnockbackModifierList for the Target/KnockbackGroup.
     */
    public KnockbackModifiersList getKnockbackModifiersList(Target target, String knockbackGroup) {
        int kbindex = getKnockbackIndex(target, knockbackGroup);
        return getKnockbackModifiersList(kbindex);
    }
    
    /**
     * Gets the KnockbackModifierList for the Target/KnockbackGroup.
     */
    public KnockbackModifiersList getKnockbackModifiersList(int kbindex) {
        Object o;
        
        o = getIndexedValue(kbindex, "Knockback", "KNOCKBACKMODIFIERSLIST");
        return ( o == null ) ? null : (KnockbackModifiersList)o;
    }
    
    /**
     * Sets the KnockbackModifersList for the Target/KnockbackGroup.
     */
    public void setKnockbackModifiersList(Target target, String knockbackGroup, KnockbackModifiersList kml) {
        int kbindex = getKnockbackIndex(target, knockbackGroup);
        setKnockbackModifiersList(kbindex, kml);
    }
    
    /**
     * Sets the KnockbackModifersList for the Target/KnockbackGroup.
     */
    public void setKnockbackModifiersList(int kbindex, KnockbackModifiersList kml) {
        addIndexed(kbindex, "Knockback", "KNOCKBACKMODIFIERSLIST", kml, true);
    }
    
    
    /**
     * Gets the KnockbackRoll for the Target/KnockbackGroup.
     *
     * Returns null if the Roll hasn't been made yet.
     */
    public Dice getKnockbackAmountRoll(int kbindex) {
        Object o;
        
        o = getIndexedValue(kbindex, "Knockback", "AMOUNTROLL");
        return ( o == null ) ? null : (Dice)o;
    }
    
    
    /**
     * Sets the KnockbackRoll for the Target/KnockbackGroup.
     */
    public void setKnockbackAmountRoll(int kbindex, Dice dice) {
        addIndexed(kbindex, "Knockback", "AMOUNTROLL", dice, true);
    }
    
    /**
     * Gets the KnockbackAutoroll for the Target/KnockbackGroup.
     *
     * Returns null if the Roll hasn't been made yet.
     */
    public boolean getKnockbackAutoRollAmount(int kbindex) {
        String b = getIndexedStringValue(kbindex, "Knockback", "AUTOROLLAMOUNT");
        return ( b == null || b.equals("TRUE"));
    }
    
    
    /**
     * Sets the KnockbackRoll for the Target/KnockbackGroup.
     */
    public void setKnockbackAutoRollAmount(int kbindex, boolean autoroll) {
        addIndexed(kbindex, "Knockback", "AUTOROLLAMOUNT", autoroll ? "TRUE" : "FALSE", true);
    }
    
    /**
     * Gets the KnockbackAutoroll for the Target/KnockbackGroup.
     *
     * Returns null if the Roll hasn't been made yet.
     */
    public int getKnockbackDistance(int kbindex) {
        Integer i = getIndexedIntegerValue(kbindex, "Knockback", "DISTANCE");
        return ( i == null ) ? 0 : i.intValue();
    }
    
    /**
     * Sets the KnockbackRoll for the Target/KnockbackGroup.
     */
    public void setKnockbackDistance(int kbindex, int distance) {
        addIndexed(kbindex, "Knockback", "DISTANCE", new Integer(distance), true);
    }

    /** Returns whether the Target was possibly knocked down by the knockback.
     *
     * This is the pre-breakfall indication of possible knockdown.  According
     * to the 5E rules, this can occur when the amount of knockback is exactly
     * zero or higher.
     */
    public boolean isKnockedDownPossible(int kbindex) {
        return getIndexedBooleanValue(kbindex, "Knockback", "KNOCKDOWN");
    }

    /** Returns whether the Target was possibly knocked down by the knockback.
     *
     * This is the pre-breakfall indication of possible knockdown.  According
     * to the 5E rules, this can occur when the amount of knockback is exactly
     * zero or higher.
     */
    public void setKnockedDownPossible(int kbindex, boolean knockedDown) {
        addIndexed(kbindex, "Knockback", "KNOCKDOWN", knockedDown ? "TRUE" : "FALSE", true);
    }
    
    /**
     * Sets the KnockbackRoll for the Target/KnockbackGroup.
     */
    public void setKnockbackEffect(int kbindex, KnockbackEffect effect) {
        addIndexed(kbindex, "Knockback", "EFFECT", effect, true);
    }
    
    /**
     * Gets the KnockbackAutoroll for the Target/KnockbackGroup.
     *
     * Returns null if the Roll hasn't been made yet.
     */
    public KnockbackEffect getKnockbackEffect(int kbindex) {
        return (KnockbackEffect)getIndexedValue(kbindex, "Knockback", "EFFECT");
    }
    
    
    /**
     * Gets the KnockbackAutoroll for the Target/KnockbackGroup.
     *
     * Returns null if the Roll hasn't been made yet.
     */
    public int getKnockbackAmount(int kbindex) {
        Integer i = getIndexedIntegerValue(kbindex, "Knockback", "AMOUNT");
        return ( i == null ) ? 0 : i.intValue();
    }
    
    /**
     * Sets the KnockbackRoll for the Target/KnockbackGroup.
     */
    public void setKnockbackAmount(int kbindex, int distance) {
        addIndexed(kbindex, "Knockback", "AMOUNT", new Integer(distance), true);
    }
    
    /**
     * Sets the KnockbackRoll for the Target/KnockbackGroup.
     */
    public void setKnockbackAutorollDamage(int kbindex, boolean autoroll) {
        addIndexed(kbindex, "Knockback", "AUTOROLLDAMAGE", autoroll ? "TRUE" : "FALSE", true);
    }
    
    /**
     * Gets the KnockbackAutoroll for the Target/KnockbackGroup.
     *
     * Returns null if the Roll hasn't been made yet.
     */
    public boolean getKnockbackAutorollDamage(int kbindex) {
        Object i = getIndexedValue(kbindex, "Knockback", "AUTOROLLDAMAGE");
        return ( i == null ) ? true : i.equals("TRUE");
    }
    
    /**
     * Gets the KnockbackRoll for the Target/KnockbackGroup.
     *
     * Returns null if the Roll hasn't been made yet.
     *
     * @deprecated Don't use this any more...just use the normal dice mechanism
     */
    public Dice getKnockbackDamageRoll(int kbindex) {
        Object o;
        
        o = getIndexedValue(kbindex, "Knockback", "DAMAGEROLL");
        return ( o == null ) ? null : (Dice)o;
    }
    
    
    /**
     * Sets the KnockbackRoll for the Target/KnockbackGroup.
     */
    public void setKnockbackDamageRoll(int kbindex, Dice dice) {
        addIndexed(kbindex, "Knockback", "DAMAGEROLL", dice, true);
    }
    
    public void setMessageCount(int count) {
        if ( messageCount != count ) {
            int oldCount = messageCount;
            
            this.messageCount = count;
            
            firePropertyChange(this, "MESSAGE", new Integer(oldCount), new Integer(count));
            
            if ( getParentEvent() != null ) {
                getParentEvent().childMessageCountChanged(oldCount, count);
            }
        }
    }
    
    public int getMessageCount() {
        //return getIndexedSize("Message");
        return messageCount;
    }
    
    public void childMessageCountChanged(int oldCount, int newCount) {
        if ( oldCount != newCount ) {
            setMessageCount( getMessageCount() + newCount - oldCount);
        }
    }
    
    public String getMessageText(int mindex) {
        //return getIndexedStringValue(mindex, "Message", "TEXT");
        Iterator i = getMessageIterator();
        MessagePair mp = null;
        int index = -1;
        while ( index != mindex && i.hasNext() ) {
            mp = (MessagePair)i.next();
            index++;
        }
        
        return (mp != null) ?  mp.getMessage() : null;
    }
    
    public int getMessageType(int mindex) {
        //Integer i = getIndexedIntegerValue(mindex, "Message", "TYPE");
        //return (i==null) ? 0 : i.intValue();
        Iterator i = getMessageIterator();
        MessagePair mp = null;
        int index = -1;
        while ( index != mindex && i.hasNext() ) {
            mp = (MessagePair)i.next();
            index++;
        }
        
        return (mp != null) ?  mp.getType() : -1;
    }
    
    public Iterator getMessageIterator() {
        return new MyMessageIterator(this);
    }
    
    /** Getter for property action.
     * @return Value of property action.
     */
    public Action getAction() {
        return (Action) getValue("BattleEvent.ACTION");
    }
    
    /** Setter for property action.
     * @param action New value of property action.
     */
    public void setAction(Action action) {
        add("BattleEvent.ACTION", action, true);
    }
    
    public BattleEvent clone() {
        BattleEvent clone = new BattleEvent();
        
        // Set the ActivationInfo correctly...
        clone.activationInfo = this.activationInfo;
        clone.action = action;
        clone.activationInfo = activationInfo;
        clone.messageCount = messageCount;
        clone.showAttackTree = showAttackTree;
        clone.finishedProcessingEvent = finishedProcessingEvent;
        clone.attackTreeDescriptionShown = attackTreeDescriptionShown;
        clone.timeProcessed = timeProcessed;
        clone.timeParameter = timeParameter;
        clone.type = type;
        clone.dex = dex;
        if ( combatLevelMap != null ) {
            clone.combatLevelMap = (HashMap)combatLevelMap.clone();
        }
        
        Iterator i = detailList.keySet().iterator();
        while ( i.hasNext() ) {
            Object key = i.next();
            clone.detailList.put( key, detailList.get(key) );
        }
        return clone;
    }
    
    /** Getter for property parentEvent.
     * @return Value of property parentEvent.
     */
    public BattleEvent getParentEvent() {
        return (BattleEvent)getValue("BattleEvent.PARENTEVENT");
    }
    
    /** Setter for property parentEvent.
     * @param parentEvent New value of property parentEvent.
     */
    public void setParentEvent(BattleEvent parentEvent) {
        add("BattleEvent.PARENTEVENT", parentEvent, true);
    }
    
    
    public int getEmbeddedUndoIndex(BattleEvent embeddedEvent) {
        int index = 0;
        //int count = getIndexedSize("Event");
        int count = getUndoableEventCount();
        
        for(index = 0; index < count; index++) {
            //String type = getIndexedStringValue(index, "Event", "TYPE");
            Undoable undoable = getUndoableEvent(index);
            if ( undoable instanceof EmbedEventUndoable ) {
                //BattleEvent be = (BattleEvent) getIndexedValue(index, "Event", "BATTLEEVENT");
                if ( ((EmbedEventUndoable)undoable).getEmbeddedBattleEvent() == embeddedEvent ) return index;
            }
        }
        return -1;
    }
    /** Getter for property showAttackTree.
     *
     * Always show attack tree indicates that the attack tree should be
     * show for this event, in spite of what the combat profile currently indicates.
     *
     * This will force the attack tree description node to always stop when
     * processing.
     *
     * @return Value of property showAttackTree.
     */
    public boolean isAlwaysShowAttackTree() {
        return showAttackTree;
    }
    
    /** Setter for property showAttackTree.
     *
     * Always show attack tree indicates that the attack tree should be
     * show for this event, in spite of what the combat profile currently indicates.
     *
     * This will force the attack tree description node to always stop when
     * processing.
     *
     * @param showAttackTree New value of property showAttackTree.
     */
    public void setAlwaysShowAttackTree(boolean showAttackTree) {
        this.showAttackTree = showAttackTree;
    }
    
    /** Getter for property finishedProcessingEvent.
     * @return Value of property finishedProcessingEvent.
     */
    public boolean isFinishedProcessingEvent() {
        return finishedProcessingEvent;
    }
    
    /** Setter for property finishedProcessingEvent.
     * @param finishedProcessingEvent New value of property finishedProcessingEvent.
     */
    public Undoable setFinishedProcessingEvent(boolean finishedProcessingEvent) {
        if ( this.finishedProcessingEvent != finishedProcessingEvent ) {
            Undoable u = new FinishedProcessingUndoable(this, this.finishedProcessingEvent, finishedProcessingEvent);
            this.finishedProcessingEvent = finishedProcessingEvent;
            return u;
        }
        return null;
    }
    
    /** Getter for property attackTreeDescriptionShown.
     * @return Value of property attackTreeDescriptionShown.
     */
    public boolean isAttackTreeDescriptionShown() {
        return this.attackTreeDescriptionShown;
    }
    
    /** Setter for property attackTreeDescriptionShown.
     *
     * The attackTreeDescriptionShown determines if a description node is generate
     * when a power is triggered.  In some cases, especially embedded powers, a
     * description panel is not desirable.
     *
     * @param attackTreeDescriptionShown New value of property attackTreeDescriptionShown.
     */
    public void setAttackTreeDescriptionShown(boolean attackTreeDescriptionShown) {
        this.attackTreeDescriptionShown = attackTreeDescriptionShown;
    }
    
    public int getManeuverStrengthUsed() {
        Integer i = getIntegerValue("Maneuver.STR_USED");
        return i == null ? 0 : i.intValue();
    }
    
    public int getNormalStrengthUsed() {
        Integer i = getIntegerValue("Normal.STR_USED");
        return i == null ? 0 : i.intValue();
    }
    
    public int getPushedStrengthUsed() {
        Integer i = getIntegerValue("Pushed.STR_USED");
        return i == null ? 0 : i.intValue();
    }

    public void addSkillChallenge(SkillChallenge skillChallenge) {
        if (skillChallengeList == null)
            skillChallengeList = new Vector<SkillChallenge>();

        skillChallengeList.add(skillChallenge);
        // TODO:  Put all this skill challenge stuff into the SkillChallenge object.
        // Maybe SkillRollInfo is the same thing.
    }

    public int addSkillChallenge(String description, String skillName, Target challenger, Target challengee) {
        int index = findSkillChallenge(description, skillName, challenger, challengee);
        if ( index == -1 ) {
            index = createIndexed( "SkillChallenge", "DESCRIPTION", description);
            addIndexed(index, "SkillChallenge", "SKILLNAME", skillName, true);
            addIndexed(index, "SkillChallenge", "CHALLENGER", challenger, true);
            addIndexed(index, "SkillChallenge", "CHALLENGEE", challengee, true);
        }
        return index;
    }
    
    public int findSkillChallenge(String description, String skillName, Target challenger, Target challengee) {
        IndexIterator ii = getIteratorForIndex("SkillChallenge");
        while(ii.hasNext() ) {
            int index = ii.nextIndex();
            if ( description.equals( getSkillChallengeDescription(index) )
            && skillName.equals( getSkillChallengeSkillName(index) )
            && challenger == getSkillChallengeChallenger(index)
            && challengee == getSkillChallengeChallengee(index) ) {
                return index;
            }
        }
        
        return -1;
    }
    
    public String getSkillChallengeDescription(int sindex) {
        return getIndexedStringValue(sindex, "SkillChallenge", "DESCRIPTION");
    }
    
    public String getSkillChallengeSkillName(int sindex) {
        return getIndexedStringValue(sindex, "SkillChallenge", "SKILLNAME");
    }
    
    public Target getSkillChallengeChallenger(int sindex) {
        return (Target) getIndexedValue(sindex, "SkillChallenge", "CHALLENGER");
    }
    
    public Target getSkillChallengeChallengee(int sindex) {
        return (Target) getIndexedValue(sindex, "SkillChallenge", "CHALLENGEE");
    }
    
    public Dice getSkillChallengeChallengerDice(int sindex) {
        return (Dice) getIndexedValue(sindex, "SkillChallenge", "CHALLENGERDICE");
    }
    
    public Dice getSkillChallengeChallengeeDice(int sindex) {
        return (Dice) getIndexedValue(sindex, "SkillChallenge", "CHALLENGEEDICE");
    }
    
    public void setSkillChallengeChallengerDice(int sindex, Dice dice) {
        addIndexed(sindex, "SkillChallenge", "CHALLENGERDICE", dice, true);
    }
    
    public void setSkillChallengeChallengeeDice(int sindex, Dice dice) {
        addIndexed(sindex, "SkillChallenge", "CHALLENGEEDICE", dice, true);
    }
    
    public Target getSkillChallengeWinner(int sindex) {
        return (Target) getIndexedValue(sindex, "SkillChallenge", "WINNER");
    }
    
    public int getSkillChallengeMargin(int sindex) {
        Integer i = getIndexedIntegerValue(sindex, "SkillChallenge", "MARGIN");
        return i == null ? -1 : i.intValue();
    }
    
    public double getSkillChallengeBodyRatio(int sindex) {
        Double d = getIndexedDoubleValue(sindex, "SkillChallenge", "BODYRATIO");
        return d == null ? -1 : d.doubleValue();
    }

    public void setSkillChallengeWinner(int sindex, Target winner, int winnerBody, int loserBody) {
        addIndexed(sindex, "SkillChallenge", "WINNER", winner, true);
        addIndexed(sindex, "SkillChallenge", "MARGIN", new Integer(winnerBody - loserBody), true);
        addIndexed(sindex, "SkillChallenge", "BODYRATIO", new Double((double) winnerBody / (double) loserBody), true);
    }
    
    public boolean getSkillChallengeChallengerAutoroll(int sindex) {
        return getIndexedBooleanValue(sindex, "SkillChallenge", "CHALLENGERAUTOROLL");
    }
    
    public boolean getSkillChallengeChallengeeAutoroll(int sindex) {
        return getIndexedBooleanValue(sindex, "SkillChallenge", "CHALLENGEEAUTOROLL");
    }
    
    public boolean getSkillChallengeChallengerAutorollSet(int sindex) {
        return containsIndexed(sindex, "SkillChallenge", "CHALLENGERAUTOROLL");
    }
    
    public boolean getSkillChallengeChallengeeAutorollSet(int sindex) {
        return containsIndexed(sindex, "SkillChallenge", "CHALLENGEEAUTOROLL");
    }
    
    public void setSkillChallengeChallengerAutoroll(int sindex, boolean autoroll) {
        addIndexed(sindex, "SkillChallenge", "CHALLENGERAUTOROLL", autoroll ? "TRUE" : "FALSE", true);
    }
    
    public void setSkillChallengeChallengeeAutoroll(int sindex, boolean autoroll) {
        addIndexed(sindex, "SkillChallenge", "CHALLENGEEAUTOROLL", autoroll ? "TRUE" : "FALSE", true);
    }
    
    /** Stores the BattleEvent to be removed from the BattleEngine */
    public void setBattleEventToRemove(BattleEvent be) {
        add("BattleEvent.BATTLEEVENTTOREMOVE", be, true);
    }
    
    /** Stores the BattleEvent to be removed from the BattleEngine */
    public BattleEvent getBattleEventToRemove() {
        return (BattleEvent)getValue("BattleEvent.BATTLEEVENTTOREMOVE");
    }
    
    /**
     * Creates a skillRoll in the current activation info.
     * 
     *  A skill roll is actually indexed with a concatenation of the
     *  name and targetGroup.
     * 
     *  Adding a skill roll when one already exists with the same name
     *  and target newGroup will return  -1 without replacing the existing
     *  skillRollInfo.
     */
    public int addSkillRoll(String name, String targetGroup, SkillRollInfo skillRollInfo) {
        String fullName = targetGroup + "." + name;
        if ( findIndexed("SkillRoll", "NAME", fullName) != -1 ) return -1;
        
        int index = createIndexed("SkillRoll", "NAME", fullName);
        addIndexed(index, "SkillRoll", "INFO", skillRollInfo);
        
        return index;
    }
    
    public int getSkillRollIndex(String name, String targetGroup) {
        String fullName = targetGroup + "." + name;
        return findIndexed("SkillRoll", "NAME", fullName);
    }
    
    public SkillRollInfo getSkillRollInfo(String name, String targetGroup) {
        int index = getSkillRollIndex(name,targetGroup);
        SkillRollInfo skillRollInfo = null;
        if ( index != -1 ) {
            skillRollInfo = (SkillRollInfo)getIndexedValue(index, "SkillRoll", "INFO");
        }
        return skillRollInfo;
    }
    
    public void removeSkillRollInfo(String name, String targetGroup) {
        int index = getSkillRollIndex(name,targetGroup);
        if ( index != -1 ) {
            removeAllIndexed(index, "SkillRoll");
        }
    }
    
    public Filter<Target> getTargetFilter() {
        Filter<Target> filter = null;
        
        if ( getAbility() != null ) {
            filter = getAbility().getTargetFilter();
        }
        
        if ( getManeuver() != null ) {
            Filter<Target> filter2 = getManeuver().getTargetFilter();
            if ( filter == null ) {
                filter = filter2;
            } else if ( filter2 != null ) {
                filter = new AndFilter<Target>(filter, filter2);
            }
        }
        
        return filter;
    }
    
    public String toLongString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("type = ").append(type).append("\n");
        sb.append("messageCount = ").append(messageCount).append("\n");
        if ( action != null ) sb.append("action = ").append(action).append("\n");
        if ( timeProcessed != null ) sb.append("timeProcessed = ").append(timeProcessed).append("\n");
        if ( timeParameter != null ) sb.append("timeParameter = ").append(timeParameter).append("\n");
        
        sb.append( super.toLongString() );
        
        return sb.toString();
    }
    
    /** Destroys the propertyChangeSupport reference associated with the detail list.
     *
     * Subclasses can override this for special handling of the
     * detaillist object contained by the list.
     * The reference should be set to null if the list is completely done using it.
     */
    protected void destroyValues() {
        if ( detailList != null ) {
            // System.out.println("Destroying values for BattleEvent: " + this);
            Iterator i = detailList.keySet().iterator();
            
            // First, run through the undoable list, looking for ability deactivations.
            // If an ability was deactivated, then the activation info can also be destroyed.
            int index, count;
            String type;
            
            count = getUndoableEventCount();
            
            for ( index = 0; index < count; index ++ ) {
                Undoable undoable = getUndoableEvent(index);
                //type = getIndexedStringValue( index, "Event", "TYPE" );
                if ( undoable instanceof EffectUndoable ) {
                    EffectUndoable eu = (EffectUndoable)undoable;
//                    Effect c = (Effect) getIndexedValue( index,"Event","EFFECT");
//                    Target t = (Target) getIndexedValue( index,"Event","TARGET");
//                    boolean a = getIndexedBooleanValue( index, "Event","ADDED");
                    
                    // If the effect was removed, it can now be destroyed, since it no
                    // longer is attached to the target...
                    if ( eu.isAdded() == false ) {
                        //System.out.println("BattleEvent::Destroying " + c );
                        eu.getEffect().destroy();
                    }
                } else if ( undoable instanceof EmbedEventUndoable ) {
                    EmbedEventUndoable eeu = (EmbedEventUndoable)undoable;
                    
                    // If we are doing a full rollback/rollforward, just rollback/rollforward the embeded event
                    BattleEvent embeddedEvent = eeu.getEmbeddedBattleEvent();
                    
                    // Will need to destroy embeded events also.  Unlike delayed events, these should never be
                    // referenced at a later time...
                    // System.out.println("BattleEvent::Destroying " + embeddedEvent );
                    embeddedEvent.destroy();
                } else if ( undoable instanceof ActivationInfoStateUndoable ) {
                    ActivationInfoStateUndoable aisu = (ActivationInfoStateUndoable)undoable;
                    
                    ActivationInfo ai = aisu.getActivationInfo();
                    String state = aisu.getNewState();
                    
                    // Perhaps an activation state change to DEACTIVATED is the best way to detect that the
                    // ActivationInfo is finished being used...
                    if ( state.equals(ChampionsConstants.AI_STATE_DEACTIVATED) ) {
                        //  System.out.println("BattleEvent::Destroying " + ai );
                        ai.destroy();
                    }
                }
            }
            
            // Destroy/Remove all key/value pairs.
            while (i.hasNext() ) {
                Object key = i.next();
                Object value = detailList.get(key);
                
                if ( value instanceof Destroyable && value instanceof DetailList == false ) {
                    ((Destroyable)value).destroy();
                    // System.out.println("BattleEvent::Destroying " + value );
                }
            }
            detailList.clear();
            detailList = null;
        }
    }
    
    /**
     * Adds a message entry to the messages array.
     * 
     * This should not be used directly.  Use addBattleMessage instead.
     * 
     * 
     * @param me
     * @return 
     */
    protected int addMessageEntry(MessageEntry me) {
        if ( messages == null ) {
            messages = new ArrayList<MessageEntry>();
        }
        
        messages.add(me);
        
        return messages.size() - 1;
    }
    
    protected void removeMessageEntry(int index) {
        if ( messages != null ) {
            messages.remove(index);
        }
    }
    
    protected int findMessageEntry(MessageEntry me) {
        int result = -1;
        
        if ( messages != null ) {
            result = messages.indexOf(me);
        }
        
        return result;
    }
    
    public boolean isRolledBack() {
        return rolledBack;
    }
    
    public void setRolledBack(boolean rolledBack) {
        this.rolledBack = rolledBack;
    }
    
    public static class MessageEntry implements Serializable {
        
    }
    
    public static class TextMessageEntry extends MessageEntry  {
        protected String text;
        protected int type;
        
        public TextMessageEntry(String text, int type) {
            this.text = text;
            this.type = type;
        }
        
        public boolean equals(Object obj) {
            if ( obj instanceof TextMessageEntry == false ) return false;
            TextMessageEntry that = (TextMessageEntry)obj;
            return text.equals(that.text) && type == that.type;
        }
    }
    
    public static class EmbeddedBEMessageEntry extends MessageEntry {
        protected BattleEvent battleEvent;
        
        public EmbeddedBEMessageEntry(BattleEvent battleEvent) {
            this.battleEvent = battleEvent;
        }
        
        public boolean equals(Object obj) {
            if ( obj instanceof EmbeddedBEMessageEntry == false ) return false;
            return this.battleEvent == ((EmbeddedBEMessageEntry)obj).battleEvent;
        }
        
        
    }
    
    public static class MessageUndoable implements Undoable, Serializable {
        
        private int index;
        private String message;
        private BattleEvent be;
        private int type;
        
        public MessageUndoable(BattleEvent be, int index, String message, int type) {
            super();
            this.be = be;
            this.index = index;
            this.message = message;
            this.type = type;
        }
        
        public void redo() {
//            if (be == null || be.getMessageCount() != index) {
//                System.out.println("Error redoing message \'" + message + "\' from BattleEvent: \n" + be);
//            } else {
                be.addMessageEntry(new BattleEvent.TextMessageEntry(message, type));
                be.setMessageCount(be.getMessageCount() + 1);
//            }
        }
        
        public void undo() {
//            if (be == null || be.getMessageCount() - 1 != index) {
//                System.out.println("Error undoing message \'" + message + "\' from BattleEvent: \n" + be);
//            } else {
                be.removeMessageEntry(index);
                be.setMessageCount(be.getMessageCount() - 1);
//            }
        }
        
        public String toString() {
            return "MessageUndoable [Index: " + Integer.toString(index) + ", Message: " + message + "]";
        }
        
        public String getMessage() {
            return message;
        }
        
        public int getMessageType() {
            return type;
        }
    }
    
    public static class BattleMessageUndoable implements Undoable, Serializable {
        
        private BattleMessageGroup messageGroup;
        private BattleMessage message;
        
        public BattleMessageUndoable(BattleMessageGroup messageGroup, BattleMessage message) {
            super();
            this.message = message;
            this.messageGroup = messageGroup;
        }
        
        public void redo() {
            messageGroup.addMessage(message);
        }
        
        public void undo() {
            //System.out.println("removing message " + message + " from " + messageGroup);
            messageGroup.removeMessage(message);
        }
        
        public String toString() {
            return "BattleMessageUndoable";
        }
        
    }
    
    public static class OpenBattleGroupUndoable implements Undoable {
        protected BattleMessageGroup newGroup;
        protected BattleEvent be;
    
        OpenBattleGroupUndoable(BattleEvent be, BattleMessageGroup newGroup) {
            this.newGroup = newGroup;
            this.be = be;
        }
        
        public void undo() throws BattleEventException {
            //System.out.println("Undoing OpenBattleGroup of " + newGroup);
            if ( be.messageGroupList != null && be.messageGroupList.size() >0 && be.messageGroupList.getFirst() == newGroup) {
                be.messageGroupList.removeFirst();
            }
        }

        public void redo() throws BattleEventException {
           // System.out.println("Redoing OpenBattleGroup of " + newGroup);
            if ( be.messageGroupList != null ) {
                be.messageGroupList.addFirst(newGroup);
            }
        }
        
    }
    
    public static class CloseBattleGroupUndoable implements Undoable {
        protected BattleMessageGroup group;
        protected BattleEvent be;
    
        CloseBattleGroupUndoable(BattleEvent be, BattleMessageGroup group) {
            this.group = group;
            this.be = be;
        }
        
        public void undo() throws BattleEventException {
            //System.out.println("Undoing CloseBattleGroup of " + group);
            if ( be.messageGroupList != null ) {
                be.messageGroupList.addFirst(group);
            }
        }

        public void redo() throws BattleEventException {
            //System.out.println("Redoing CloseBattleGroup of " + group);
            if ( be.messageGroupList != null && be.messageGroupList.getFirst() == group) {
                be.messageGroupList.removeFirst();
            }
        }
        
    }
    
    public static class SetPrimaryBattleGroupUndoable implements Undoable {
        protected BattleMessageGroup newGroup;
        protected BattleMessageGroup oldGroup;
        protected BattleEvent be;
    
        SetPrimaryBattleGroupUndoable(BattleEvent be, BattleMessageGroup oldGroup, BattleMessageGroup newGroup) {
            this.newGroup = newGroup;
            this.oldGroup = oldGroup;
            this.be = be;
        }
        
        public void undo() throws BattleEventException {
            //System.out.println("Undoing CloseBattleGroup of " + newGroup);
            be.primaryBattleMessageGroup = oldGroup;
        }

        public void redo() throws BattleEventException {
            //System.out.println("Redoing CloseBattleGroup of " + newGroup);
            be.primaryBattleMessageGroup = newGroup;
        }
        
    }
    
    private static class MyMessageIterator implements Iterator {
        private BattleEvent be;
        
        private int messageSize = 0;
        
        // Variable holding the next index which will be returned by next()
        private int messageIndex;
        
        private Iterator embeddedIterator;
        
        private MessagePair messagePair;
        
        
        public MyMessageIterator(BattleEvent be) {
            this.be = be;
            
            initializeIterator();
            
            messagePair = new MessagePair();
        }
        
        private void initializeIterator() {
            if ( be.messages != null ) {
                messageSize = be.messages.size();
            }
            messageIndex = 0;
        }
        
        /**
         * Returns <tt>true</tt> if the iteration has more elements. (In other
         * words, returns <tt>true</tt> if <tt>next</tt> would return an element
         * rather than throwing an exception.)
         *
         * @return <tt>true</tt> if the iterator has more elements.
         */
        public boolean hasNext() {
            boolean more = false;
            
            while ( messageIndex < messageSize && more == false ) {
                if ( embeddedIterator != null ) {
                    // If embeddedIterator is non-null, use that to determine if there is more...
                    if ( embeddedIterator.hasNext() ) {
                        // There are more, so set more true and break;
                        more = true;
                        break;
                    } else {
                        // The currently set message iterator has been used up, so clear it
                        // and increase the messageIndexx...then loop looking at the next index.
                        embeddedIterator = null;
                        messageIndex++;
                    }
                } else {
                    // Loop, looking for either a normal message, or an iterator with messages...
                    //BattleEvent embeddedEvent = (BattleEvent) be.getIndexedValue(messageIndex, "Message", "BATTLEEVENT");
                	if(be.messages.size()> messageIndex) {
                		MessageEntry me = be.messages.get(messageIndex);
                		if ( me instanceof TextMessageEntry ) {
                			// This must be a normal message, so set more to true and break...
                			more = true;
                			break;
	                    } else {
	                        BattleEvent embeddedEvent = ((EmbeddedBEMessageEntry)me).battleEvent;
	                        if ( embeddedEvent.getMessageCount() > 0 ) {
	                            embeddedIterator = embeddedEvent.getMessageIterator();
	                            more = true;
	                            break;
	                        } else {
	                            messageIndex++;
	                        }
	                    }
                	}
                }
            }
            
            return more;
        }
        
        
        /**
         * Returns the next element in the interation.
         *
         * @return the next element in the iteration.
         * @exception NoSuchElementException iteration has no more elements.
         */
        public Object next() {
            MessagePair mp = null;
            String out="";
            while ( messageIndex < messageSize && mp == null ) {
                mp = null;
                if ( embeddedIterator != null ) {
                    // If embeddedIterator is non-null, use that to determine if there is more...
                    if ( embeddedIterator.hasNext() ) {
                        // There are more, so set more true and break;
                        mp = (MessagePair)embeddedIterator.next();
                        break;
                    } else {
                        // The currently set message iterator has been used up, so clear it
                        // and increase the messageIndexx...then loop looking at the next index.
                        embeddedIterator = null;
                        messageIndex++;
                    }
                } else {
                    // Loop, looking for either a normal message, or an iterator with messages...
                    //BattleEvent embeddedEvent = (BattleEvent)be.getIndexedValue(messageIndex, "Message", "BATTLEEVENT");
                    MessageEntry me = be.messages.get(messageIndex);
                    
                    if ( me instanceof TextMessageEntry ) {
                        // This must be a normal message, so set more to true and break...
                        //String message = be.getIndexedStringValue(messageIndex, "Message", "TEXT");
                        String message = ((TextMessageEntry)me).text;
                        if ( message == null ) message = "Holy Crap! This isn't working right!";
                        
                        //Integer i = be.getIndexedIntegerValue(messageIndex, "Message", "TYPE");
                        //if ( i == null ) i = new Integer(BattleEvent.MSG_DEBUG);
                        int type = ((TextMessageEntry)me).type;
                        
                        messagePair.setMessage( message );
                        
                        messagePair.setType( type );
                        
                        mp = messagePair;
                        
                        // Increase the message index to indicate this one has been used.
                        messageIndex++;
                        break;
                    } else {
                        BattleEvent embeddedEvent = ((EmbeddedBEMessageEntry)me).battleEvent;
                        // This is a battleEvent, so setup the embeddedIterator
                        embeddedIterator = embeddedEvent.getMessageIterator();
                        
                        // Grab whether the iterator has more...if it doesn't, the loop will clear this
                        // iterator and more to the next automatically.
                        if ( embeddedIterator.hasNext() ) {
                            mp = (MessagePair)embeddedIterator.next();
                        }
                    }
                }
            }
            
            
            
            if ( mp != null ) return mp;
            else throw new NoSuchElementException();
        }
        
        /**
         *
         * Removes from the underlying collection the last element returned by the
         * iterator (optional operation).  This method can be called only once per
         * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
         * the underlying collection is modified while the iteration is in
         * progress in any way other than by calling this method.
         *
         * @exception UnsupportedOperationException if the <tt>remove</tt>
         * 		  operation is not supported by this Iterator.
         *
         * @exception IllegalStateException if the <tt>next</tt> method has not
         * 		  yet been called, or the <tt>remove</tt> method has already
         * 		  been called after the last call to the <tt>next</tt>
         * 		  method.
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    /** Returns whether the ability activation is delayed.
     *
     * The battle event can override the delay.
     */
    public boolean isAbilityDelayed() {
        if ( abilityDelayed == null ) return getAbility().isDelayed();
        return abilityDelayed;
    }
    
    /** Sets whether the ability activation is delayed.
     *
     *  If this is non-null, it will override the abilities
     *  configured behaviour.  If it is null, the ability isDelayed
     *  will be returned.
     */
    public void setAbilityDelayed(Boolean abilityDelayed) {
        this.abilityDelayed = abilityDelayed;
    }
    
    /** Returns whether the maneuver activation is delayed.
     *
     * The battle event can override the delay.
     */
    public boolean isManeuverDelayed() {
        if ( maneuverDelayed == null ) return getManeuver().isDelayed();
        return maneuverDelayed;
    }
    
    /** Sets whether the maneuver activation is delayed.
     *
     *  If this is non-null, it will override the maneuvers
     *  configured behaviour.  If it is null, the maneuver isDelayed
     *  will be returned.
     */
    public void setManeuverDelayed(Boolean maneuverDelayed) {
        this.maneuverDelayed = maneuverDelayed;
    }

    public String getActivationTime() {
        if ( activationTime != null ) return activationTime;
        return getAbility().getActivationTime();
    }
    
    /** Sets the activationTime for the ability, but doesn't overwrite previous battleEvent activation time settings.
     *
     *  Use setActivationTime(String,boolean) to overwrite previous settings.
     */
    public void setActivationTime(String activationTime) {
        setActivationTime(activationTime,false);
    }

    /** Sets the activationTime for the ability.
     *
     *  If overridePreviousValue == true, then the previous activationTime setting
     *  for this battleevent will be overwritten.  If overridePreviousValue == false
     *  the activation time will only be change if the activation time hasn't been
     *  set for the battleEvent.
     */
    public void setActivationTime(String activationTime, boolean overridePreviousValue) {
        if ( overridePreviousValue == false || this.activationTime == null ) {
            this.activationTime = activationTime;
        }
    }
    
    public boolean isMoveThrough() {
        return ( getAbility() != null && getAbility().getPower() instanceof maneuverMoveThrough ) ||
                ( getManeuver() != null && getManeuver().getPower() instanceof maneuverMoveThrough );
    }

    public boolean isReprocessingEvent() {
        return reprocessingEvent;
    }

    public void setReprocessingEvent(boolean reprocessingEvent) {
        this.reprocessingEvent = reprocessingEvent;
    }
    
    /** Resets the battle event to the original state, so it can be reprocessed.
     *
     */
    public void resetBattleEvent() {
        undoables.clear();
        messages.clear();
        messageCount = 0;
        
        setMessageCount(0);
    }

    public CombatLevelList getCombatLevelList(Target target) {
        CombatLevelList combatLevelList = null;

        if ( combatLevelMap != null ) {
            combatLevelList = combatLevelMap.get(target);
        }

        if ( combatLevelList == null ) {
            combatLevelList = buildCombatLevelList(target);
            setCombatLevelList(target, combatLevelList);
        }

        return combatLevelList;
    }

    protected void setCombatLevelList(Target target, CombatLevelList list) {
        if ( combatLevelMap == null ) {
            combatLevelMap = new HashMap<Target,CombatLevelList>();
        }

        combatLevelMap.put(target,list);
    }

    public CombatLevelList buildCombatLevelList(Target target) {
        CombatLevelList combatLevelList;

        combatLevelList = new CombatLevelList(target);

        for (Effect effect : target.getEffects()) {
            if ( effect instanceof CombatLevelProvider && ((CombatLevelProvider)effect).isCombatLevelProvider()) {
                CombatLevelProvider clp = (CombatLevelProvider)effect;
                boolean applies = clp.applies(getAbility()) || clp.applies(getManeuver());
                combatLevelList.addCombatLevelProvider(clp, applies);
            }
        }

        return combatLevelList;
    }
    Map<Integer, Effect> _damageEffects = new HashMap<Integer, Effect>();
	public void addDamageEffect(Effect effect, int tindex) {
		_damageEffects.put(tindex, effect);
		
	}
	public Effect getDamageEffect(int tindex) {
		return _damageEffects.get(tindex);
	}
	
	public void removeDamageEffect( int tindex) {
		_damageEffects.remove(tindex);
		
	}
	public int getDamageEffectCount()
	{
		return _damageEffects.size();
	}

	Map<Integer, Effect> _knockbackDamageEffects = new HashMap<Integer, Effect>();
	public void addKnockbackDamageEffect(Effect effect, int tindex) {
		_knockbackDamageEffects.put(tindex, effect);
		
	}
	public Effect getKnockbackDamageEffect(int tindex) {
		return _knockbackDamageEffects.get(tindex);
	}
	
	public void removeKnockbackDamageEffect( int tindex) {
		_knockbackDamageEffects.remove(tindex);
		
	}
    
}