/*
 * Target.java
 *
 * Created on September 19, 2000, 11:11 AM
 */

package champions;

import champions.battleMessage.ENDSummaryMessage;
import champions.battleMessage.StatChangeBattleMessage;
import champions.battleMessage.StatChangeType;
import champions.enums.DefenseType;
import champions.exception.BadDiceException;
import champions.exception.BattleEventException;
import champions.exceptionWizard.ExceptionWizard;
import champions.interfaces.AbilityIterator;
import champions.interfaces.AbilityList;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.Debuggable;
import champions.interfaces.ENDSource;
import champions.interfaces.SenseFilter;
import champions.interfaces.Undoable;
import champions.parameters.ListParameter;
import champions.parameters.Parameter;
import champions.parameters.ParameterList;
import champions.powers.effectAdjusted;
import champions.powers.effectUnconscious;
import champions.powers.powerInvisibility;
import champions.senseFilters.SensesOnlySenseFilter;
import champions.senseTree.STSensePanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;







/**
 *
 * <B>Property Change Events</B><P>
 * The Target class only fires Property Change Events for a limited selection of Properties.  Changes in the follow
 * properties will trigger Property Change Events to be fired:<P>
 *
 * <I>Property          Description</I><P>
 * <STAT_NAME>          Any changes in the vps that control stat values will cause a PCE to be fired.<P>
 * Target.NAME          Target's Name.<P>
 * Effect.INDEXSIZE     The Effect Count Changes.<P>
 * COMBATSTATE          Indicates a change has occurred to the Targets combat state. <B>NOT IMPLEMENTED</B><P>
 * SENSES               Indicates a change has occurred to the senses.
 * ActivationInfo.INDEXSIZE
 *
 * @author  unknown
 * @version
 */
public class Target extends DetailList implements Comparable, ChampionsConstants, ENDSource {
    
    static final long serialVersionUID = 8050829359056525528L;
    
    static private final int DEBUG = 0;
    
    public String fileExtension = "tgt";
    
    /** Holds value of property roster. */
    transient protected Roster roster;
    
    transient protected BufferedImage image;
    transient protected ImageIcon icon;
    
    private String name;
    
    protected CombatState combatState;
    protected CombatState tempState;
    
    protected boolean postTurn;
    protected boolean tempPostTurn;
    
    public boolean stunned = false;
    public boolean unconscious = false;
    
    protected boolean egoPhase = false;
    
    protected Map<Long,Float> randomSequenceHash = new HashMap<Long,Float>();
    
    private byte[] imageJPEGData;
    
    protected CharacteristicPrimary stats[] = new CharacteristicPrimary[Characteristic.characteristicNames.length];
    
    protected List<TargetEffectEntry> effects = new ArrayList<TargetEffectEntry>();
    
    protected DefenseList defenseList;

    protected int effectiveDexAdjustment = 0;

	private List<AbilityAction> Actions= new ArrayList<AbilityAction>();
    
    public Target() {
        setFireChangeByDefault(false);
        //add("Target.NAME", "UNNAMED") ;
        setName("UNNAMED");
        
        //add("Target.POSTTURN", "FALSE") ;
        
        //add("Target.TEMPPOSTTURN", "FALSE", true,false);
        
        add("Target.ISALIVE",  "TRUE",  true);
        
        add("Target.HASDEFENSES",  "TRUE",  true);
        add("Target.USESHITLOCATION",  "TRUE",  true);
        add("Target.HASSENSES",  "TRUE",  true);
        
        add("Target.CANBEKNOCKEDBACK",  "TRUE",  true);
        
        setCombatState(CombatState.STATE_INACTIVE);
        setTempState(CombatState.STATE_INACTIVE);
        setPostTurn(false);
        setTempPostTurn(false);
        
        setAbilityList( new DefaultAbilityList() );
        
    }
    
    public Target(String name) {
        this();
        setName(name);
    }
    
    public String getName() {
        //String s = getStringValue("Target.NAME");
        return (name==null)?"UNNAMED":name;
    }
    
    public void setName(String name) {
        if ( this.name != name ) {
            String oldName = getName();
            //add("Target.NAME", name, true, false);
            this.name = name;
            firePropertyChange(this, "Target.NAME", oldName, name);
            VirtualDesktop.Legacy.MessageExporter.exportRenameEvent(this, oldName, name);
        }
    }
    
    /** Getter for property combatState.
     * @return Value of property combatState.
     */
    public CombatState getCombatState() {
        //String s = getStringValue("Target.COMBATSTATE");
        return (combatState == null) ? CombatState.STATE_INACTIVE : combatState;
    }
    
    /** Setter for property combatState.
     * @param combatState New value of property combatState.
     */
    public void setCombatState(CombatState combatState) {
        //add("Target.COMBATSTATE",  combatState, true, false);
        this.combatState = combatState;
        Battle.fireCombatStateEvent( "Target Combat State Change" );
        //  firePropertyChange(this, "COMBATSTATE", "", "");
    }
    
    public boolean isPostTurn() {
        //return getBooleanValue("Target.POSTTURN");
        return postTurn;
    }
    
    public void setPostTurn(boolean postTurn) {
        //add("Target.POSTTURN", b ? "TRUE":"FALSE",  true, false);
        this.postTurn = postTurn;
        Battle.fireCombatStateEvent( "Combat State Change" );
    }
    
    /** Getter for property combatState.
     * @return Value of property combatState.
     */
    public CombatState getTempState() {
        return tempState != null ? tempState : CombatState.STATE_INACTIVE;
    }
    /** Setter for property combatState.
     * @param combatState New value of property combatState.
     */
    public void setTempState(CombatState combatState) {
        //add("Target.TEMPSTATE",  combatState,true,false);
        this.tempState = combatState;
    }
    
    public void setTempState() {
        //add("Target.TEMPSTATE", getCombatState(),true,false);
        setTempState(getCombatState());
        setTempPostTurn( isPostTurn() );
    }
    
    
    public boolean isTempPostTurn() {
        //return getBooleanValue("Target.TEMPPOSTTURN");
        return tempPostTurn;
    }
    
    public void setTempPostTurn(boolean tempPostTurn) {
        //add("Target.TEMPPOSTTURN",( b) ? "TRUE" : "FALSE", true, false);
        this.tempPostTurn = tempPostTurn;
    }
    
    /** Indexed getter for property statString.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public String getStatString(String stat) {
        return Integer.toString(getCurrentStat(stat)) + "/" + Integer.toString(getBaseStat(stat));
    }
    
    
    /** Getter for property combatState.
     *
     * A Target is considered CombatActive if it is in a non- STATE_INACTIVE
     * CombatState and if it has a speed characteristics.
     *
     * @return Value of property combatState.
     */
    public boolean isCombatActive() {
        //String s = getStringValue("Target.COMBATSTATE");
        if ( combatState != null && combatState == CombatState.STATE_INACTIVE == false && hasStat("SPD") ) {
            return true;
        }
        return false;
    }
    
    public boolean hasFullPhase() {
        CombatState combatState = getCombatState();
        return combatState == CombatState.STATE_ACTIVE || combatState == CombatState.STATE_HELD || combatState == CombatState.STATE_ABORTING;
    }
    
    public boolean hasHalfPhase() {
        CombatState combatState = getCombatState();
        return combatState == CombatState.STATE_HALFFIN || combatState == CombatState.STATE_HALFHELD || hasFullPhase();
    }
    
    public void setPrephaseDone(boolean prephase) {
        add("Target.PREPHASEDONE",  prephase ? "TRUE" : "FALSE",  true, false);
    }
    
    public void setStunned(boolean stunned) {
        //System.out.println("Target.setStunned() called for " + this);
        //add("Target.STUNNED",  stunned ? "TRUE" : "FALSE",  true, false);
        this.stunned = stunned;
    }
    
    public void setUnconscious(boolean unconscious) {
        //System.out.println("Target.setUnconscious() called for " + this);
        //add("Target.UNCONSCIOUS",  un ? "TRUE" : "FALSE",  true, false);
        this.unconscious = unconscious;
    }
    
    public boolean isStunned() {
        //return hasEffect( "Stunned");
        return stunned;
    }
    
    public boolean isUnconscious() {
        //return hasEffect("Unconscious");
        return unconscious;
    }
    
    /** Returns whether the target is dead, destroy, non-functional, etc.
     *
     * Dead targets are considered combat inactive and typically only exist
     * for reference.
     */
    public boolean isDead() {
        return hasEffect(getDeadEffectName());
    }
    
    /** Returned String describing "dead" effect.
     *
     * Every Target can be considered dead.  However, some targets do not call
     * it dead.  For example, a wall might call it "destroyed".  All references
     * to the "dead" effect for a target should use the getDeadEffectName()
     * method to find the correct name for being dead.
     */
    public String getDeadEffectName() {
        return "Dead";
    }
    
    public boolean isDying() {
        return hasEffect("Dying");
    }
    public boolean isEnragedBerserk() {
        return hasEffect("Enraged/Berserk");
    }
    
    public boolean isPrephaseDone() {
        return getBooleanValue("Target.PREPHASEDONE" );
    }
    
    public void removeEffect(BattleEvent be,  String effectName )  throws BattleEventException {
        //int index = findIndexed("Effect","NAME",effectName);
        //if ( index != -1 ) {
        //    Effect effect = (Effect)getIndexedValue( index, "Effect","EFFECT");
        //    effect.removeEffect(be, this);
        //}
        
        Effect effect = getEffect(effectName);
        if ( effect != null ) {
            effect.removeEffect(be,this);
        }
    }
    
    /** Remove the effect entry from this target.
     *
     *  This should only be called from Effect.removeEffect.  If you wish
     *  to remove an effect you should use removeEffect.
     */
    protected void removeEffectEntry(Effect effect) {
        for(int index = 0; index < effects.size(); index++) {
            TargetEffectEntry tee = effects.get(index);
            if ( tee.getEffect() == effect ) {
                effects.remove(index);
                fireIndexedChanged("Effect");
                break;
            }
        }
    }
    
    /** Remove the effect entry from this target.
     *
     *  This should only be called from Effect.removeEffect.  If you wish
     *  to remove an effect you should use removeEffect.
     */
    protected void addEffectEntry(Effect effect) {
        if ( hasEffect(effect) == false ) {
            effects.add( new TargetEffectEntry(effect) );
            fireIndexedChanged("Effect");
        }
    }
    
    /** Searches the character for any effect of the same class.
     *
     * This will search all effects applied to the character looking for once
     * that is the exact same class as the provided class.
     */
//    public boolean hasEffect( Class c ) {
//        
//        for(TargetEffectEntry tee : effects) {
//            if ( tee.getEffect().equals(c) ) {
//                return true;
//            }
//        }
//        return false;
//    }
    
    public boolean hasEffect( String effectName ) {
        for(TargetEffectEntry tee : effects) {
            if ( tee.getName().equals(effectName) ) {
                return true;
            }
        }
        return false;
    }
    
    /** Returns whether the Target has the @param effect exactly.
     *
     */
    public boolean hasEffect( Effect effect ) {
        for(TargetEffectEntry tee : effects) {
            if ( tee.getEffect() == effect ) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasEffect( Class<? extends Effect> effectClass) {
        for(TargetEffectEntry tee : effects) {
            if ( effectClass.isInstance(tee.getEffect()) ) {
                return true;
            }
        }
        return false;
    }
    
    
    public Effect getEffect( String effectName ) {
        return getEffect(effectName, 0 );
    }
    
    public Effect getEffect( String effectName, int start ) {
        for(int index = start; index < effects.size(); index++) {
            TargetEffectEntry tee = effects.get(index);
            if ( tee.getName().equals(effectName) ) {
                return tee.getEffect();
            }
        }
        return null;
    }
    
    public Effect getEffect(int index) {
        return effects.get(index).getEffect();
    }
    
    public String getEffectName(int index) {
        return effects.get(index).getName();
    }
    
    public List<Effect> getEffects() {
        List<Effect> e = new ArrayList<Effect>();
    
        for(TargetEffectEntry tee : effects) {
            e.add(tee.getEffect());
        }
        
        return e;
    }
    
    public int getEffectCount() {
        //return getIndexedSize("Effect");
        return effects.size();
    }
    
    public String toDescriptor() {
        String s = getName() + "(SPD:" + getCurrentStat("SPD")
        + ", STUN:" + getCurrentStat("STUN")
        + ", BODY:" + getCurrentStat("BODY");
        if ( isUnconscious() ) s = s.concat( ", Unconscious");
        if ( isStunned() ) s = s.concat(", Stunned");
        s = s.concat(")");
        return s;
    }
    
    
    
    public String toString() {
        return getName();
    }
    
    public String dumpDetailList() {
        return super.toString();
    }
    
    public void editTarget() {
        TargetEditor.editTarget(this);
    }
    
    public void createCharacteristic(String name) {
        CharacteristicPrimary cp = getCharacteristic(name);
        
        if ( cp == null ) {
            cp = Characteristic.createCharacteristic(name, false);
            setCharacteristic(cp);
        }
    }
    
    public CharacteristicPrimary getCharacteristic(String name) {
        //return (CharacteristicPrimary) getValue("Characteristic." + name);
        int index = Characteristic.getCharacteristicIndex(name);
        return ( index == -1 ? null : stats[index]);
    }
    
    public void setCharacteristic(CharacteristicPrimary c) {
        //add("Characteristic." + c.getName(), c, true, true);
        int index = Characteristic.getCharacteristicIndex(c.getName());
        if ( c != stats[index] ) {
            CharacteristicPrimary old = stats[index];
            stats[index] = c;
            firePropertyChange(this, "Characteristic." + c.getName(), old, c);
        }
    }
    
    // Real stat access functions
    public int getBaseStat(String stat) {
        CharacteristicPrimary cp = getCharacteristic(stat);
        return cp.getBaseStat();
        //        Double cps = getDoubleValue(stat + ".BASECP");
        //        if ( cps == null ) return 0;
        //
        //        double base = (double)getStatBase( stat );
        //        double cost = getCPperStat(stat);
        //        int statValue;
        //
        //        if ( base == -1 ) {
        //            // This must be a figured stat, since the base is -1
        //            // Grab the base from the FIGUREDBASE
        //            Double figuredBase = getDoubleValue(stat + ".FIGUREDBASE" );
        //            base = (figuredBase == null) ? 0 : figuredBase.doubleValue();
        //        }
        //
        //        if ( stat.equals("SPD") ) {
        //            return (int)  Math.floor( cps.doubleValue() / cost + base);
        //        }
        //        else {
        //            return (int) Math.round( cps.doubleValue() / cost + base);
        //        }
    }
    
    public int getAdjustedStat(String stat) {
        return getCharacteristic(stat).getAdjustedStat();
        //        Double offsetCP = getDoubleValue(stat + ".ADJUSTEDCPOFFSET");
        //        if ( offsetCP == null ) offsetCP = new Double(0);
        //
        //        // Determine the Starting Value, using FIGUREDBASE if necessary
        //        double startingValue = (double)getStatBase( stat );
        //        double cost = getCPperStat(stat);
        //        int statValue;
        //
        //        if ( startingValue == -1 ) {
        //            Double figuredBase = getDoubleValue(stat + ".FIGUREDADJBASE" );
        //            startingValue = (figuredBase == null) ? 0 : figuredBase.doubleValue();
        //        }
        //
        //        double baseCP = getBaseStatCP(stat);
        //
        //        if ( stat.equals("SPD") ) {
        //            return (int) Math.floor( (offsetCP.doubleValue() + baseCP) / cost + startingValue);
        //        }
        //        else {
        //            return (int) Math.round( (offsetCP.doubleValue() + baseCP) / cost + startingValue);
        //        }
    }
    
    public int getCurrentStat(String stat) {
        return getCharacteristic(stat).getCurrentStat();
        //        Double offset = getDoubleValue(stat + ".CURRENTOFFSET");
        //        if ( offset == null ) offset = new Double(0);
        //
        //       /* double base = (double)getStatBase(stat);
        //        double cost = getCPperStat(stat);
        //        int statValue;
        //
        //        if ( base == -1 ) {
        //            Double figuredBase = getDoubleValue(stat + ".FIGUREDBASE" );
        //            base = (figuredBase == null) ? 0 : figuredBase.doubleValue();
        //        }
        //
        //        if ( stat.equals("SPD") ) {
        //            return (int) Math.floor(cps.doubleValue() / cost + base);
        //        }
        //        else {
        //            return (int)Math.round(cps.doubleValue() / cost + base);
        //        }*/
        //        return (int) (getAdjustedStat(stat) + offset.doubleValue());
    }
    
    public double getCurrentStatCP(String stat) {
        return getCharacteristic(stat).getCurrentStatCP();
    }
    
    //    private double getCurrentOffset(String stat) {
    //        Double offset = getDoubleValue(stat + ".CURRENTOFFSET");
    //        return ( offset == null ) ? 0 : offset.doubleValue();
    //    }
    
    public void setBaseStat(String stat, int newValue) {

        CharacteristicPrimary c = getCharacteristic(stat);
        int statValue = getBaseStat(stat);
        
        if ( c != null ) { //&& (statValue = getBaseStat(stat) ) != newValue ) {
            c.setBaseStat(newValue);
            
            firePropertyChange(this, stat, new Integer(statValue), new Integer(newValue));
            
            calculateFiguredStat(stat);
            calculateFiguredAdjustedStat(stat);
        }
    }
    
    /** Starts the Starting Value for a Stat.  
     *
     * This is used in cases where non-typical values are used for the basis of 
     * a statistic.  For example, objects use different body and defense basis.
     *
     * For figured characteristics, the updateAdjustedStartingValue will update
     * the adjustedStartingValue 
     */
    public void setStartingStat(String stat, double startingValue) {

        CharacteristicPrimary c = getCharacteristic(stat);
        
        if ( c != null ) { //&& (statValue = getBaseStat(stat) ) != newValue ) {
            int oldValue = c.getBaseStat();
            c.setStartingValue(startingValue);
            int newValue = c.getBaseStat();
            
            if( oldValue != newValue ) {
                

                if ( c instanceof CharacteristicFigured ) {
                    CharacteristicFigured cf = (CharacteristicFigured)c;
                    cf.setAdjustedStartingValue( startingValue );
                }
                else {
                    calculateFiguredStat(stat);
                    calculateFiguredAdjustedStat(stat);
                }
                
                firePropertyChange(this, stat, new Integer(oldValue), new Integer(newValue));
            }
        }
    }
    
    public double getStartingStat(String stat) {
        CharacteristicPrimary c = getCharacteristic(stat);
        
        if(c != null) {
            return c.getStartingValue();
        }
        return 0;
    }
    
    public void setAdjustedStat(String stat, int newValue, boolean affectsFigured) {
        //        Double offsetCP = getDoubleValue(stat + ".ADJUSTEDCPOFFSET");
        //        if ( offsetCP == null ) offsetCP = new Double(0);
        //
        //        double base = getStatBase( stat );
        //        double cost = getCPperStat(stat);
        //        int oldValue = getAdjustedStat(stat);
        //        double newOffsetCP;
        //
        //        if ( base == -1 ) {
        //            Double figuredBase = getDoubleValue(stat + ".FIGUREDADJBASE" );
        //            base = (figuredBase == null) ? 0 : figuredBase.doubleValue();
        //        }
        //
        //        double baseCP = getBaseStatCP(stat);
        //
        //        newOffsetCP = ( newValue * cost - baseCP - base * cost) ;
        //
        //        this.add(stat + ".ADJUSTEDCPOFFSET", new Double(newOffsetCP) , true, false);
        //
        //        if ( affectsFigured ) {
        //            Double oldAF = getDoubleValue(stat + ".ADJUSTEDCPAFOFFSET");
        //            if ( oldAF == null ) oldAF = offsetCP;
        //
        //            double newAF = oldAF.doubleValue() + ( newOffsetCP - offsetCP.doubleValue() );
        //            this.add(stat + ".ADJUSTEDCPAFOFFSET", new Double(newAF) , true, false);
        //
        //            calculateFiguredAdjustedStat(stat);
        //        }
        //
        //        firePropertyChange(this, stat, new Integer(oldValue), new Integer(newValue));
        CharacteristicPrimary c = getCharacteristic(stat);
        int statValue = getAdjustedStat(stat);
        
        if ( c != null ) { //&& (statValue = getAdjustedStat(stat) ) != newValue ) {
            c.setAdjustedStat(newValue);
            
            if ( affectsFigured ) {
                int offset = newValue - statValue;
                int adjustedAFstat = c.getAdjustedAFStat();
                c.setAdjustedAFStat(adjustedAFstat + offset);
                calculateFiguredAdjustedStat(stat);
            }
            
            firePropertyChange(this, stat, new Integer(statValue), new Integer(newValue));
        }
    }
    
   /* public void setBaseStat(String stat, int newValue) {
        setBaseStat( stat,  newValue, true);
    } */
    
    public void setAdjustedStat(String stat, int newValue) {
        setAdjustedStat( stat,  newValue, true);
    }
    
    public void setCurrentStat(String stat, int newValue) {
       
        
        CharacteristicPrimary c = getCharacteristic(stat);
        int statValue = getCurrentStat(stat);
        
        if ( c != null ) { //&& (statValue = getCurrentStat(stat) ) != newValue ) {
            c.setCurrentStat(newValue);
            
            firePropertyChange(this, stat, new Integer(statValue), new Integer(newValue));
        }
    }
    
    public void setCurrentStatCP(String stat, double newValue) {
        CharacteristicPrimary c = getCharacteristic(stat);
        double statValue;
        
        if ( c != null && (statValue = getCurrentStatCP(stat) ) != newValue ) {
            c.setCurrentStatCP(newValue);
            
            firePropertyChange(this, stat, new Double(statValue), new Double(newValue));
        }
    }
    
    //    public void setCurrentStatCP(String stat, int newValue) {
    //        double oldValue = getCurrentStat(stat);
    //        if ( oldValue != newValue ) {
    //            double adjustedStat = getAdjustedStat(stat);
    //            double newOffset = newValue - adjustedStat;
    //
    //            this.add(stat + ".CURRENTOFFSET", new Double(newOffset) , true, false);
    //
    //            firePropertyChange(this, stat, new Double(oldValue), new Double(newValue));
    //        }
    //    }
    
    /*public void setCurrentStat(String stat, int newValue) {
        setCurrentStat(stat, newValue, false);
    } */
    
    // CharacterPoint stat access functions
    public double getBaseStatCP(String stat) {
     /*   Double cps;
        if ( ( cps = getDoubleValue(stat + ".BASECP") ) != null ) {
            return cps.doubleValue();
        }
        else {
            return 0;
        } */
        //        Double cps = getDoubleValue(stat + ".BASECP");
        //        return cps == null ? 0 : cps.doubleValue();
        return getCharacteristic(stat).getBaseStatCP();
    }
    
    public double getAdjustedStatCP(String stat) {
        //        Double offsetCP = getDoubleValue(stat + ".ADJUSTEDCPOFFSET");
        //        if ( offsetCP == null ) offsetCP = new Double(0);
        //        Double baseCP = getDoubleValue(stat + ".BASECP");
        //        if ( baseCP == null ) baseCP = new Double(0);
        //
        //        return offsetCP.doubleValue() + baseCP.doubleValue();
        return getCharacteristic(stat).getAdjustedStatCP();
    }
    
   /* public double getCurrentStatCP(String stat) {
        Double cps;
        if ( ( cps = getDoubleValue(stat + ".CURRENTSTATCP") ) != null ) {
            return cps.doubleValue();
        }
        else {
            return 0;
        }
    } */
    
    public void setBaseStatCP(String stat, double newValue) {
        //        double baseCP = getBaseStatCP(stat);
        //        if ( baseCP != newValue ) {
        //            int oldValue = getBaseStat(stat);
        //            add(stat + ".BASECP", new Double( newValue ), true, false);
        //
        //            firePropertyChange(this, stat,new Integer( oldValue), new Integer(getBaseStat(stat)));
        //
        //            calculateFiguredStat(stat);
        //            calculateFiguredAdjustedStat(stat);
        //
        //
        //        }
        CharacteristicPrimary c = getCharacteristic(stat);
        double statValue;
        
        if ( c != null && (statValue = getBaseStatCP(stat) ) != newValue ) {
            c.setBaseStatCP(newValue);
            
            firePropertyChange(this, stat, new Double(statValue), new Double(newValue));
            
            calculateFiguredStat(stat);
            calculateFiguredAdjustedStat(stat);
        }
    }
    
    public void setAdjustedStatCP(String stat, double newValue, boolean affectsFigured) {
        //        Double offsetCP = getDoubleValue(stat + ".ADJUSTEDCPOFFSET");
        //        if ( offsetCP == null ) offsetCP = new Double(0);
        //        Double baseCP = getDoubleValue(stat + ".BASECP");
        //        if ( baseCP == null ) baseCP = new Double(0);
        //
        //        int oldValue = getAdjustedStat(stat);
        //
        //        double oldCP = offsetCP.doubleValue() + baseCP.doubleValue();
        //        double newOffsetCP = newValue - baseCP.doubleValue();
        //
        //        add(stat + ".ADJUSTEDCPOFFSET", new Double( newOffsetCP ) , true, false);
        //
        //        if ( affectsFigured ) {
        //            Double oldAF = getDoubleValue(stat + ".ADJUSTEDCPAFOFFSET");
        //            if ( oldAF == null ) oldAF = offsetCP;
        //
        //            double newAF = oldAF.doubleValue() + ( newOffsetCP - offsetCP.doubleValue() );
        //            this.add(stat + ".ADJUSTEDCPAFOFFSET", new Double(newAF) ,  true, false);
        //
        //            calculateFiguredAdjustedStat(stat);
        //        }
        //
        //        firePropertyChange(this, stat, new Integer(oldValue), new Integer((int)newValue));
        //
        //        return;
        CharacteristicPrimary c = getCharacteristic(stat);
        double statValue;
        
        if ( c != null && (statValue = getAdjustedStatCP(stat) ) != newValue ) {
            c.setAdjustedStatCP(newValue);
            
            if ( affectsFigured ) {
                double offset = newValue - statValue;
                double adjustedAFstat = c.getAdjustedAFStatCP();
                getCharacteristic(stat).setAdjustedAFStatCP(adjustedAFstat + offset);
                calculateFiguredAdjustedStat(stat);
            }
            
            firePropertyChange(this, stat, new Double(statValue), new Double(newValue));
        }
    }
    
    //    public double getAdjustedCPOffset(String stat) {
    //        Double offsetCP = getDoubleValue(stat + ".ADJUSTEDCPOFFSET");
    //        return offsetCP != null ? offsetCP.doubleValue() : 0;
    //    }
    //
    //    public double getAdjustedCPAFOffset(String stat) {
    //        Double offsetCP = getDoubleValue(stat + ".ADJUSTEDCPAFOFFSET");
    //        return offsetCP != null ? offsetCP.doubleValue() : 0;
    //    }
    //
    //    public void setAdjustedCPOffset(String stat, double value) {
    //        add( stat + ".ADJUSTEDCPOFFSET", new Double(value), true, true);
    //    }
    //
    //    public void setAdjustedCPAFOffset(String stat, double value) {
    //        add( stat + ".ADJUSTEDCPOFFSET", new Double(value), true, true);
    //        calculateFiguredAdjustedStat(stat);
    //    }
    
    private int getAdjustedAFStat(String stat) {
        //        Double offsetCP = getDoubleValue(stat + ".ADJUSTEDCPAFOFFSET");
        //        if ( offsetCP == null ) offsetCP = new Double(0);
        //
        //        // Determine the Starting Value, using FIGUREDBASE if necessary
        //        double startingValue = (double)getStatBase( stat );
        //        double cost = getCPperStat(stat);
        //        int statValue;
        //
        //        // This should never happen!!!
        //        if ( startingValue == -1 ) {
        //            System.out.println("Target.getAdjustedAFStat() executed for Figured Stat!!!");
        //            Double figuredBase = getDoubleValue(stat + ".FIGUREDADJBASE" );
        //            startingValue = (figuredBase == null) ? 0 : figuredBase.doubleValue();
        //        }
        //
        //        double baseCP = getBaseStatCP(stat);
        //
        //        if ( stat.equals("SPD") ) {
        //            return (int) Math.floor( (offsetCP.doubleValue() + baseCP) / cost + startingValue);
        //        }
        //        else {
        //            return (int) Math.round( (offsetCP.doubleValue() + baseCP) / cost + startingValue);
        //        }
        return getCharacteristic(stat).getAdjustedAFStat();
    }
    
    protected void calculateFiguredStat(String stat) {
        CharacteristicFigured c;
        int initialValue;
        
        if ( stat.equals("STR") ) {
            c = (CharacteristicFigured)getCharacteristic("PD");
            if ( c != null ) {
                c.setStartingValue( ChampionsUtilities.roundValue( (double)getBaseStat("STR")/5.0, true) );
                firePropertyChange(this, "PD", new Integer(-1), new Integer(-1));
            }
            
            c = (CharacteristicFigured)getCharacteristic("REC");
            if ( c != null ) {
                c.setStartingValue( ChampionsUtilities.roundStat("REC",
                        ChampionsUtilities.roundValue( (double)getBaseStat("STR")/5.0, true)
                        + ChampionsUtilities.roundValue( (double)getBaseStat("CON")/5.0, true) ) );
                firePropertyChange(this, "REC", new Integer(-1), new Integer(-1));
            }
            
            c = (CharacteristicFigured)getCharacteristic("STUN");
            if ( c != null ) {
                c.setStartingValue( ChampionsUtilities.roundStat("STUN",
                        (double)getBaseStat("BODY")
                        + ChampionsUtilities.roundValue( (double)getBaseStat("STR")/2.0,true )
                        + ChampionsUtilities.roundValue( (double)getBaseStat("CON")/2.0,true ) ) );
                firePropertyChange(this, "STUN", new Integer(-1), new Integer(-1));
            }
        } else if ( stat.equals("CON" ) ){
            c = (CharacteristicFigured)getCharacteristic("ED");
            if ( c != null ) {
                c.setStartingValue( ChampionsUtilities.roundStat("ED",
                        ChampionsUtilities.roundValue((double)getBaseStat("CON")/5.0,true) ) );
                firePropertyChange(this, "ED", new Integer(-1), new Integer(-1));
            }
            
            c = (CharacteristicFigured)getCharacteristic("REC");
            if ( c != null ) {
                c.setStartingValue( ChampionsUtilities.roundStat("REC",
                        ChampionsUtilities.roundValue( (double)getBaseStat("STR")/5.0, true)
                        + ChampionsUtilities.roundValue( (double)getBaseStat("CON")/5.0, true) ) );
                firePropertyChange(this, "REC", new Integer(-1), new Integer(-1));
            }
            
            c = (CharacteristicFigured)getCharacteristic("STUN");
            if ( c != null ) {
                c.setStartingValue( ChampionsUtilities.roundStat("STUN",
                        (double)getBaseStat("BODY")
                        + ChampionsUtilities.roundValue( (double)getBaseStat("STR")/2.0,true )
                        + ChampionsUtilities.roundValue( (double)getBaseStat("CON")/2.0,true ) ) );
                firePropertyChange(this, "STUN", new Integer(-1), new Integer(-1));
            }
            
            c = (CharacteristicFigured)getCharacteristic("END");
            if ( c != null ) {
                c.setStartingValue( ChampionsUtilities.roundStat("END",
                        (double)getBaseStat("CON") * 2.0 ) );
                firePropertyChange(this, "END", new Integer(-1), new Integer(-1));
            }
        } else if ( stat.equals("DEX" ) ){
            c = (CharacteristicFigured)getCharacteristic("SPD");
            if ( c != null ) {
                c.setStartingValue( 1 + (double)getBaseStat("DEX") / 10.0 );
                firePropertyChange(this, "SPD", new Integer(-1), new Integer(-1));
            }
        } else if ( stat.equals("BODY" ) ){
            c = (CharacteristicFigured)getCharacteristic("STUN");
            if ( c != null ) {
                c.setStartingValue( ChampionsUtilities.roundStat("STUN",
                        (double)getBaseStat("BODY")
                        + ChampionsUtilities.roundValue( (double)getBaseStat("STR")/2.0,true )
                        + ChampionsUtilities.roundValue( (double)getBaseStat("CON")/2.0,true ) ) );
                firePropertyChange(this, "STUN", new Integer(-1), new Integer(-1));
            }
        }
        
    }
    
    private void calculateFiguredAdjustedStat(String stat) {
        CharacteristicFigured c;
        
        if ( stat.equals("STR") ) {
            c = (CharacteristicFigured)getCharacteristic("PD");
            if ( c != null ) {
                c.setAdjustedStartingValue( ChampionsUtilities.roundValue( (double)getAdjustedAFStat("STR")/5.0, true) );
                firePropertyChange(this, "PD", new Integer(-1), new Integer(-1));
            }
            
            c = (CharacteristicFigured)getCharacteristic("REC");
            if ( c != null ) {
                c.setAdjustedStartingValue( ChampionsUtilities.roundStat("REC",
                        ChampionsUtilities.roundValue( (double)getAdjustedAFStat("STR")/5.0, true)
                        + ChampionsUtilities.roundValue( (double)getAdjustedAFStat("CON")/5.0, true) ) );
                firePropertyChange(this, "REC", new Integer(-1), new Integer(-1));
            }
            
            c = (CharacteristicFigured)getCharacteristic("STUN");
            if ( c != null ) {
                c.setAdjustedStartingValue( ChampionsUtilities.roundStat("STUN",
                        (double)getAdjustedAFStat("BODY")
                        + ChampionsUtilities.roundValue( (double)getAdjustedAFStat("STR")/2.0,true )
                        + ChampionsUtilities.roundValue( (double)getAdjustedAFStat("CON")/2.0,true ) ) );
                firePropertyChange(this, "STUN", new Integer(-1), new Integer(-1));
            }
        } else if ( stat.equals("CON" ) ){
            c = (CharacteristicFigured)getCharacteristic("ED");
            if ( c != null ) {
                c.setAdjustedStartingValue( ChampionsUtilities.roundStat("ED",
                        ChampionsUtilities.roundValue((double)getAdjustedAFStat("CON")/5.0,true) ) );
                firePropertyChange(this, "CON", new Integer(-1), new Integer(-1));
            }
            
            c = (CharacteristicFigured)getCharacteristic("REC");
            if ( c != null ) {
                c.setAdjustedStartingValue( ChampionsUtilities.roundStat("REC",
                        ChampionsUtilities.roundValue( (double)getAdjustedAFStat("STR")/5.0, true)
                        + ChampionsUtilities.roundValue( (double)getAdjustedAFStat("CON")/5.0, true) ) );
                firePropertyChange(this, "REC", new Integer(-1), new Integer(-1));
            }
            
            c = (CharacteristicFigured)getCharacteristic("STUN");
            if ( c != null ) {
                c.setAdjustedStartingValue( ChampionsUtilities.roundStat("STUN",
                        (double)getAdjustedAFStat("BODY")
                        + ChampionsUtilities.roundValue( (double)getAdjustedAFStat("STR")/2.0,true )
                        + ChampionsUtilities.roundValue( (double)getAdjustedAFStat("CON")/2.0,true ) ) );
                firePropertyChange(this, "STUN", new Integer(-1), new Integer(-1));
            }
            
            c = (CharacteristicFigured)getCharacteristic("END");
            if ( c != null ) {
                c.setAdjustedStartingValue( ChampionsUtilities.roundStat("END",
                        (double)getAdjustedAFStat("CON") * 2.0 ) );
                firePropertyChange(this, "END", new Integer(-1), new Integer(-1));
            }
        } else if ( stat.equals("DEX" ) ){
            c = (CharacteristicFigured)getCharacteristic("SPD");
            if ( c != null ) {
                c.setAdjustedStartingValue( 1 + (double)getAdjustedAFStat("DEX") / 10.0 );
                firePropertyChange(this, "SPD", new Integer(-1), new Integer(-1));
            }
        } else if ( stat.equals("BODY" ) ){
            c = (CharacteristicFigured)getCharacteristic("STUN");
            if ( c != null ) {
                c.setAdjustedStartingValue( ChampionsUtilities.roundStat("STUN",
                        (double)getAdjustedAFStat("BODY")
                        + ChampionsUtilities.roundValue( (double)getAdjustedAFStat("STR")/2.0,true )
                        + ChampionsUtilities.roundValue( (double)getAdjustedAFStat("CON")/2.0,true ) ) );
                firePropertyChange(this, "STUN", new Integer(-1), new Integer(-1));
            }
        }
        
        //        if ( stat.equals("STR") ) {
        //            add( "PD.FIGUREDADJBASE", new Double(ChampionsUtilities.roundStat( "PD",
        //            ChampionsUtilities.roundValue( (double)getAdjustedAFStat("STR")/5.0, true)
        //            )),true, true);
        //
        //            add( "REC.FIGUREDADJBASE", new Double( ChampionsUtilities.roundStat("REC",
        //            ChampionsUtilities.roundValue( (double)getAdjustedAFStat("STR")/5.0, true)
        //            + ChampionsUtilities.roundValue( (double)getAdjustedAFStat("CON")/5.0, true)
        //            )),true, true);
        //
        //            add( "STUN.FIGUREDADJBASE", new Double( ChampionsUtilities.roundStat("STUN",
        //            (double)getAdjustedAFStat("BODY")
        //            + ChampionsUtilities.roundValue( (double)getAdjustedAFStat("STR")/2.0,true )
        //            + ChampionsUtilities.roundValue( (double)getAdjustedAFStat("CON")/2.0,true )
        //            ) ),true, true);
        //        }
        //        else if ( stat.equals("CON" ) ){
        //            add( "ED.FIGUREDADJBASE", new Double( ChampionsUtilities.roundStat("ED",
        //            ChampionsUtilities.roundValue((double)getAdjustedAFStat("CON")/5.0,true)
        //            ) ),true, true);
        //
        //            add( "REC.FIGUREDADJBASE", new Double( ChampionsUtilities.roundStat("REC",
        //            ChampionsUtilities.roundValue((double)getAdjustedAFStat("CON")/5.0,true)
        //            + ChampionsUtilities.roundValue((double)getAdjustedAFStat("STR")/5.0,true)
        //            ) ),true, true);
        //
        //            add( "STUN.FIGUREDADJBASE", new Double( ChampionsUtilities.roundStat("STUN",
        //            (double)getBaseStat("BODY")
        //            + ChampionsUtilities.roundValue((double)getAdjustedAFStat("CON")/2.0,true)
        //            + ChampionsUtilities.roundValue((double)getAdjustedAFStat("STR")/2.0,true)
        //            ) ),true, true);
        //
        //            add("END.FIGUREDADJBASE", new Double( ChampionsUtilities.roundStat("END",
        //            (double)getAdjustedAFStat("CON") * 2.0 )), true, true);
        //
        //        }
        //        else if ( stat.equals("DEX" ) ){
        //            add("SPD.FIGUREDADJBASE", new Double( ChampionsUtilities.roundStat("SPD",
        //            1 + (double)getAdjustedAFStat("DEX")/10.0) ), true, true);
        //
        //        }
        //        else if ( stat.equals("BODY" ) ){
        //            add( "STUN.FIGUREDADJBASE", new Double( ChampionsUtilities.roundStat("STUN",
        //            (double)getAdjustedAFStat("BODY")
        //            + ChampionsUtilities.roundValue( (double)getAdjustedAFStat("STR")/2.0,true )
        //            + ChampionsUtilities.roundValue( (double)getAdjustedAFStat("CON")/2.0,true )
        //            )),true, true);
        //        }
        
    }
    
    public boolean hasStat(String stat) {
        return ( getCharacteristic(stat) != null );
    }
    
    /** Returns the effective speed of the target at the time indicated.
     *
     * Due to the wierd speed rules, the effective speed of the character can
     * be different then the real speed of the character.  This returns the
     * effective speed of the character at the time indicated.
     */
    public int getEffectiveSpeed(Chronometer time) {
        CharacteristicPrimary speed =  getCharacteristic("SPD");
        return Math.max(1, speed == null ? 0 : speed.getCurrentStat());
    }

    /** Returns the effect dex of the target for the purposes of scheduling.
     *
     * The effective dex can be different than the actual dex for special situations
     * such as hipshot.
     *
     * The effective dex is used only for target sequencing and shouldn't be
     * used for other purposes.
     *
     * The effective dex of a target is always relative to the current dex of the
     * target.
     *
     * You can adjust the effective dex via adjustEffectiveDex.
     */
    public int getEffectiveDex() {
        return getCurrentStat("DEX") + effectiveDexAdjustment;
    }

    /** Applies an adjustment to the effective dex of the target.
     *
     * The effective dex is used only for target sequencing and shouldn't be
     * used for other purposes.
     *
     * @param amount amount to increase effective dex.
     */
    public void adjustEffectiveDex(int amount) {
        effectiveDexAdjustment += amount;
    }


    
    /** Returns whether the character should be sequenced on EGO instead of DEX.
     *
     */
    public boolean isSequenceOnEgo() {
        return getBooleanValue("Target.SEQUENCEONEGO");
    }
    
    /** Sets whether the character should be sequenced on EGO instead of DEX.
     *
     */
    public void setSequenceOnEgo(boolean sequenceOnEgo) {
        add("Target.SEQUENCEONEGO", sequenceOnEgo ? "TRUE" : "FALSE", true, false);
    }
    
    /** Returns whether the character is acting on an EGO only phase.
     *
     */
    public boolean isEgoPhase() {
        //return getBooleanValue("Target.ISEGOPHASE");
        return egoPhase;
    }
    
    /** Sets whether the character should be sequenced on EGO instead of DEX.
     *
     */
    public void setEgoPhase(boolean egoPhase) {
        //add("Target.ISEGOPHASE", egoPhase ? "TRUE" : "FALSE", true, false);
        this.egoPhase = egoPhase;
    }
    
    /** Returns whether the character should be sequenced on EGO instead of DEX.
     *
     */
    public boolean isHoldingForDex() {
        return getBooleanValue("Target.HOLDINGFORDEX");
    }
    
    /** Sets whether the character should be sequenced on EGO instead of DEX.
     *
     */
    public void setHoldingForDex(boolean holdingForDex) {
        add("Target.HOLDINGFORDEX", holdingForDex ? "TRUE" : "FALSE", true, false);
    }
    
    /** Returns the Next Segment in which the Target will be active, based upon the targets current combat state.
     *
     */
    public Chronometer getNextActiveTime() {
        CombatState combatState = getCombatState();
        
        int phaseCount;
        
        if ( combatState == CombatState.STATE_ABORTING || combatState == CombatState.STATE_ABORTED) {
            phaseCount = 2;
        } else {
            phaseCount = 1;
        }
        
        Chronometer time = (Chronometer)Battle.getCurrentBattle().getTime().clone();
        int segment = time.getSegment();
        int nextSegment;
        
        for(int index=0; index < phaseCount; index++) {
            nextSegment = Chronometer.nextActiveSegment( getEffectiveSpeed(time), time);
            
            // Move the time forward
            int delta = nextSegment - segment;
            if (delta < 0) delta += 12;
            segment += delta;
            time.setTime( time.getTime() + delta );
        }
        
        return time;
    }
    
    public boolean activateNormallyOn() {
        if ( Battle.currentBattle == null ) return false;
        
        BattleEvent be;
        Ability a;
        Object o;
        String type;
        
        boolean result = false;
        
        AbilityIterator iterator = getAbilities();
        while ( iterator.hasNext() ) {
            a = iterator.nextAbility();
            if ( a.isNormallyOn() && ! a.isActivated(this) ) {
                if ( a.isEnabled(this, false) ) {
                    // Trigger the NormallyOn abilities
                    be = new BattleEvent(a);
                    Battle.currentBattle.addEvent(be);
                    result = true;
                }
            }
        }
        return result;
    }
    
    public boolean hasAbility(Ability a) {
        boolean found = false;
        AbilityIterator ai = getAbilities();
        while ( ai.hasNext() ) {
            if ( ai.nextAbility() == a ) {
                found = true;
                break;
            }
        }
        return found;
    }
    
    public Ability getAbility(String name) {
        boolean found = false;
        AbilityIterator ai = getAbilities();
        while ( ai.hasNext() ) {
            Ability a = ai.nextAbility();
            String pot= a.getName();
            if ( pot.equalsIgnoreCase(name) ) {
                return a;
            }
        }
        return null;
    }
    
    public String getFileExtension() {
        return "tgt";
    }
    /** Getter for property roster.
     * @return Value of property roster.
     */
    public Roster getRoster() {
        return roster;
    }
    /** Setter for property roster.
     * @param roster New value of property roster.
     */
    public void setRoster(Roster roster) {
        this.roster = roster;
    }
    
    public String adjustDamageClass(int activePoints, String die, boolean unlimitedDC) {
        return adjustDamageClass(activePoints,die, unlimitedDC, -1);
    }
    
    public String adjustDamageClass(int activePoints, String die, boolean unlimitedDC, int str) {
        Dice strDice;
        Dice damageDice;
        
        try {
            damageDice = new Dice(die, false);
            if ( str == -1 ) {
                str = getCurrentStat("STR");
            }
            strDice = ChampionsUtilities.strToDamage(activePoints, damageDice, str, unlimitedDC);
            damageDice.add( strDice );
            
            die = damageDice.toString();
        } catch (BadDiceException bde) {
            die = "";
        }
        return die;
    }
    
    public int strToEnd(int activePoints, String die, boolean unlimitedDC, int str) {
        Dice damageDice;
        int end;
        
        try {
            damageDice = new Dice(die, false);
            
            if ( str == -1 ) {
                str = getCurrentStat("STR");
            }
            end = ChampionsUtilities.strToEnd(activePoints, damageDice, getCurrentStat("STR"), unlimitedDC);
            
        } catch (BadDiceException bde) {
            end = 0;
        }
        return end;
    }
    
    public void posteffect(BattleEvent be, Effect appliedEffect) throws BattleEventException {
        if ( isDead() && hasStat("BODY") ) {
            // Check if we should be alive again...
            int baseBody = getAdjustedStat("BODY");
            int currentBody = getCurrentStat("BODY");
            
            if ( currentBody > baseBody * -1 ) {
                removeEffect(be, getDeadEffectName());
            }
        }
        
        if ( isUnconscious() && hasStat("STUN") ) {
            int currentStun = getCurrentStat("STUN");
            
            if ( currentStun >= 0 ) {
                removeEffect(be, "Unconscious");
            }
        }
    }
    
    public boolean isAbortable() {
        if ( Battle.currentBattle == null || Battle.currentBattle.isStopped()) return false;
        if(getCombatState()== CombatState.STATE_ABORTED || getCombatState()== CombatState.STATE_ABORTING) {
        	return false;
        }
        if ( Battle.currentBattle.getTime().isActivePhase( getEffectiveSpeed( Battle.currentBattle.getTime() ) ) == false
                || getCombatState() != CombatState.STATE_FIN ) {
            return true;
        }
        
        return false;
        
    }
    
    public int getCalculatedOCV() {
        int count, index;
        CVList cvl = new CVList();
        
        cvl.setSourceCVBase( getBaseOCV() );
        
        // Run through Source effects...
        count = getEffectCount();
        for (index = 0; index<count; index++ ) {
            getEffect(index).addOCVAttackModifiers(cvl,null);
        }
        
        return cvl.getSourceCV();
    }
    
    /** Returns the DCV of the character based upon the current DEX value.
     *
     * This does not take into account any effect on the character.
     */
    public int getBaseDCV() {
        if ( hasStat("DEX") ) {
            return (int)Math.round( ((double)getCurrentStat("DEX")) / 3.0 ) ;
        } else {
            return 0;
        }
    }
    
    /** Returns the OCV of the character based upon the current DEX value.
     *
     * This does not take into account any effect on the character.
     */
    public int getBaseOCV() {
        if ( hasStat("DEX") ) {
            return (int)Math.round( ((double)getCurrentStat("DEX")) / 3.0 ) ;
        } else {
            return 0;
        }
    }
    
    /** Returns the ECV of the character based upon the current DEX value.
     *
     * This does not take into account any effect on the character.
     *
     * According to Hero System rules, a character does not get this ECV unless they
     * have the power MentalDefense.
     */
    public int getBaseECV() {
        if ( hasStat("EGO") ) {
            return (int)Math.round( ((double)getCurrentStat("EGO")) / 3.0 ) ;
        } else {
            return 0;
        }
    }
    
    public int getCalculatedDCV() {
        int count, index;
        CVList cvl = new CVList();
        
        cvl.setTargetCVBase( getBaseDCV() );
        
        // Run through Source effects...
        count = getEffectCount();
        for (index = 0; index<count; index++ ) {
            getEffect(index).addDCVDefenseModifiers(cvl,null);
        }
        
        return cvl.getTargetCV();
    }
    
    public int getCalculatedECV() {
        int count, index;
        CVList cvl = new CVList();
        
        cvl.setTargetCVBase( getBaseECV() );
        
        // Run through Source effects...
        count = getEffectCount();
        for (index = 0; index<count; index++ ) {
            getEffect(index).addECVDefenseModifiers(cvl,null);
        }
        
        return cvl.getTargetCV();
    }
    
    public int getDefense(DefenseType defense) {
        DefenseList dl;
        
        dl = getDefenseList();
        
        if ( dl == null ) {
            dl = updateDefenses();
        }
        
        return dl.getTotalDefenseModifier(defense);
    }
    
    public DefenseList getDefenseList() {
        //return (DefenseList) getValue("Defenses.DEFENSELIST");
        return defenseList;
    }
    
    public void setDefenseList(DefenseList dl) {
        //add("Defenses.DEFENSELIST", dl, true, false);
        this.defenseList = dl;
    }
    
    public DefenseList buildDefenseList(DefenseList dl, DefenseType defenseType) {
        int count, index;
        Effect effect;
        
        if ( dl == null ) dl = new DefenseList();
        
        CharacteristicPrimary cp = this.getCharacteristic( defenseType.toString() );
        if ( cp != null ) {
            dl.setDefenseBase( defenseType, Math.max(cp.getCurrentStat(),0) );
        } else {
            dl.setDefenseBase( defenseType, 0 );
        }
        
        // Run through Source effects...
        count = getEffectCount();
        for (index = 0; index<count; index++ ) {
            getEffect(index).addDefenseModifiers(dl,defenseType.toString());
        }
        
        
        return dl;
    }
    
    public DefenseList buildDefenses(DefenseList dl) {
        
        if ( dl == null ) {
            dl = new DefenseList();
        } else {
            dl.clear();
        }
        
        buildDefenseList(dl, DefenseType.PD);
        buildDefenseList(dl, DefenseType.rPD);
        buildDefenseList(dl, DefenseType.ED);
        buildDefenseList(dl, DefenseType.rED);
        buildDefenseList(dl, DefenseType.MD);
        buildDefenseList(dl, DefenseType.POWERDEFENSE);
        
        return dl;
    }
    
    public DefenseList updateDefenses() {
        DefenseList dl = getDefenseList();
        
        dl = buildDefenses(dl);
        
        setDefenseList(dl);
        
        return dl;
    }
    
    public boolean isObstruction() {
        return getBooleanValue("Target.ISOBSTRUCTION" );
    }
    
    public void reconfigureAbilities() {
        int index,count;
        AbilityIterator iterator = getAbilities();
        while ( iterator.hasNext() ) {
            Ability a = iterator.nextAbility();
            a.reconfigure();
        }
    }
    
    public void addENDSource(String name, ENDSource endSource) {
        int index;
        
        if ( (index = findIndexed("ENDSource","NAME", name) ) == -1 ) {
            index = createIndexed("ENDSource","NAME", name);
        }
        addIndexed(index, "ENDSource","ENDSOURCE", endSource, true);
    }
    
    public void addENDSource(String name) {
        addENDSource(name, null);
    }
    
    public void removeENDSource(String name) {
        int index;
        
        if ( (index = findIndexed("ENDSource","NAME", name) ) != -1 ) {
            removeAllIndexed(index, "ENDSource");
        }
    }
    
    public void renameENDSource(String originalName, String newName) {
        int index;
        
        if ( findIndexed("ENDSource", "NAME", newName) != -1 ) {
            System.out.println("Target.renameENDSource():ERROR: ENDSource '" + newName + "' already exists on Target " + this.getName() + ".");
        } else if ( (index = findIndexed("ENDSource", "NAME", originalName) ) != -1 ) {
            addIndexed(index, "ENDSource", "NAME", newName, true);
            
            int aindex;
            Ability a;
            // Actually all of the abilities should be check also to see if they use this end source
      /*      IndexIterator ii = this.getIteratorForIndex("Ability");
            while (ii.hasNext() ) {
                aindex = ii.nextIndex();
                a = getAbility(aindex);
       
                if ( a.getPrimaryENDSource() != null && a.getPrimaryENDSource().equals(originalName) ) {
                    a.setPrimaryENDSource(newName);
                }
       
                if ( a.getSecondaryENDSource() != null && a.getSecondaryENDSource().equals(originalName) ) {
                    a.setSecondaryENDSource(newName);
                }
            } */
        }
    }
    
    public ENDSource getENDSource(String name) {
        ENDSource endSource = null;
        Object o;
        
        if ( name == null || name.equals("Character") || name.equals("")) return this;
        
        int index = this.findIndexed( "ENDSource","NAME",name);
        if ( index != -1 ) {
            o = getIndexedValue( index,"ENDSource","ENDSOURCE");
            if ( o != null ) endSource = (ENDSource)o;
        }
        return endSource;
    }
    
    /** Returns the number of times an amount of END could be used.
     * Amount should be the amount of end needed by the ability/action.  checkEnd() will then calculate
     * the number of times which that ability/action can be used, given the current end available, and
     * return it.
     *
     * If burnStun is true, the number of times allowed is calculated with stun burn accounted for.
     * @param be BattleEvent which is being processed.
     * @param amount END Cost of Ability/Action being checked.
     * @param burnStun True if STUN should be burned as END, False if not.
     * @return Number of times ability/action could be performed given currently
     * available END.
     */
    public int checkEND(BattleEvent be, int amount, boolean burnStun) {
        return checkEND(amount, burnStun);
    }
    
    /** Returns the number of times an amount of END could be used.
     * Amount should be the amount of end needed by the ability/action.  checkEnd() will then calculate
     * the number of times which that ability/action can be used, given the current end available, and
     * return it.
     *
     * If burnStun is true, the number of times allowed is calculated with stun burn accounted for.
     * @param amount END Cost of Ability/Action being checked.
     * @param burnStun True if STUN should be burned as END, False if not.
     * @return Number of times ability/action could be performed given currently available END.
     */
    public int checkEND(int amount, boolean burnStun) {
        if ( amount == 0 ) return Integer.MAX_VALUE;
        
        int totalEnd = getCurrentStat("END");
        if ( burnStun == true && hasStat("STUN") ) totalEnd += getCurrentStat("STUN") / 10;
        return totalEnd / amount;
    }
    
    /** Charges for the use of END.
     * chargeEND will actually remove the end from the end source.  If the endSource doesn't have enough
     * end, a BattleEventException will be thrown.
     *
     * @param burnStun True if STUN should be burned as END, False if not.
     * @param be BattleEvent which is being processed.
     * @param amount END Cost of Ability/Action being used.
     * @param count Number of times ability/action is being used, due to autofire or
     * other reasons.
     * @param reason String describing use of END.  Added to BattleEvent messages.
     * @throws BattleEventException A BattleEventException will be thrown if insignificant END exists.
     */
    public void chargeEND(BattleEvent be, int amount, int count, boolean burnStun, String reason) throws BattleEventException {
        CharacteristicPrimary c = getCharacteristic("END");
        if ( c != null && amount > 0 && count > 0) {
            int totalUsed = amount * count;
            
            // double oldCP = getCurrentStatCP("END");
            int oldStat = c.getCurrentStat();
            
            int newStat;
            
            if ( oldStat >= totalUsed ) {
                CharacteristicChangeUndoable ccu = new CharacteristicChangeUndoable(this, c);
                // There is enough END, so burn it.
                newStat = oldStat - totalUsed;
                setCurrentStat("END", newStat);
                //  double newStat = getCurrentStat("END");
                
                ccu.setFinalValues();
                
                be.addUndoableEvent(ccu);
                
                be.addBattleMessage( new ENDSummaryMessage( this, this, totalUsed, newStat, reason));
            } else if ( burnStun ) {
                // There isn't enough END, so see if we are allowed to burn stun...
                int d6 = (totalUsed - oldStat) / 2;
                Dice d = new Dice(d6, false);
                if ( d6 < (double)totalUsed / 2 ) d.setOnehalf(true);
                
                d.realizeDice();
                
                CharacteristicPrimary stunCharacteristic = getCharacteristic("STUN");
                int stunBurn = d.getStun().intValue();
                int stun = stunCharacteristic.getCurrentStat();
              /*  if ( stun < stunBurn ) {
                    // There isn't enough END and STUN.  Throw an exception.
                     throw new BattleEventException( "Not Enough END or STUN available for " + reason );
                } */
                
                // Charge END
                CharacteristicChangeUndoable ccu = new CharacteristicChangeUndoable(this, c);
                setCurrentStat("END", 0);
                
                ccu.setFinalValues();
                be.addUndoableEvent(ccu);
                
                //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( this.getName() + " used " + Integer.toString(oldStat) + " END using " + reason + ". " + this.getName() + "'s END is currently at 0.",BattleEvent.MSG_END));
                be.addBattleMessage( new ENDSummaryMessage( this, this, oldStat, 0, reason));
                // Charge Stun
                CharacteristicChangeUndoable stunUndoable = new CharacteristicChangeUndoable(this, stunCharacteristic);
                
                newStat = stun - stunBurn;
                setCurrentStat("STUN", newStat);
                
                stunUndoable.setFinalValues();
                
                be.addUndoableEvent(stunUndoable);
                
                //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( this.getName() + " burned " + Integer.toString(stunBurn) + " STUN using " + reason + ". " + this.getName() + "'s STUN is currently at " + Integer.toString(stun - stunBurn) + ".", BattleEvent.MSG_END));
                be.addBattleMessage( new StatChangeBattleMessage(this,StatChangeType.BURNED, "STUN", stunBurn));
                
                if ( newStat <= 0 && hasEffect("Unconscious") == false) {
                    Effect e = new effectUnconscious();
                    e.addEffect(be, this);
                }
            } else {
                // The isn't enough END and we aren't allowed to burn stun, so throw an exception.
                throw new BattleEventException( "Not Enough END available for " + reason );
            }
        }
    }
    
    public ImageIcon getPortrait() {

        return icon;
    }
    
    public Image getImage() {
        return image;
    }
    
     public void setImage(BufferedImage image) {
        this.image = image;
        updateImageIcon();
    }
     
     private void updateImageIcon() {
         if ( image != null ) {
            Image i2 = image.getScaledInstance(80,100,Image.SCALE_SMOOTH);
     
            icon = new ImageIcon(i2 );
         }
         else {
             icon = null;
         }
     }
    
    public void setImageFile(File file) {
        //add( "Target.IMAGEFILE", file, true);
        
        loadImage(file);
    }
    
    public File getImageFile() {
        return ( File ) getValue("Target.IMAGEFILE");
    }
    
    private void loadImage(File file) {
        //File file = getImageFile();
        
        try {
        if ( file != null && file.exists() ) {
            image = ImageIO.read( file );
            updateImageIcon();
        }
        else {
            image = null;
            updateImageIcon();
        }
        } 
        catch (IOException ioe) {
            image = null;
            updateImageIcon();
            ExceptionWizard.postException(ioe);
        }
    }
    
    public String getStateDescription() {
        StringBuffer s = new StringBuffer();
        if ( isPostTurn() == true ) {
            s.append( "(Post Turn Recover)" );
        } else if ( getCombatState() == CombatState.STATE_ACTIVE) {
            s.append( "(Full Action)" );
        } else if ( getCombatState() == CombatState.STATE_HALFFIN) {
            s.append( "(Half Action)" );
        } else if ( getCombatState() == CombatState.STATE_FIN) {
            s.append( "(Finished)" );
        } else if ( getCombatState() == CombatState.STATE_HELD) {
            s.append( "(Holding)" );
        } else if ( getCombatState() == CombatState.STATE_HALFHELD) {
            s.append( "(Half Holding)" );
        } else if ( getCombatState() == CombatState.STATE_ABORTING) {
            s.append( "(Aborting)" );
        } else if ( getCombatState() == CombatState.STATE_ABORTED) {
            s.append( "(Aborted)" );
        } else if ( getCombatState() == CombatState.STATE_INACTIVE) {
            s.append( "(Inactive)" );
        } else if ( getCombatState() == CombatState.STATE_DELAYED) {
            s.append( "(Activating Ability)" );
        }
        
        if ( isEgoPhase() ) {
            s.append(" EGO Powers Only");
        }
        return s.toString();
    }
    
    /*
    public void addActivationInfo( ActivationInfo activationInfo) {
        if ( findIndexed( "Target", "ACTIVATIONINFO", activationInfo ) == -1 ) {
            createIndexed( "Target", "ACTIVATIONINFO", activationInfo);
        }
    }
     
    public void removeActivationInfo( ActivationInfo activationInfo ) {
        int index = findIndexed( "Target", "ACTIVATIONINFO", activationInfo );
        if ( index != -1 ) {
            removeIndexed( index, "Target", "ACTIVATIONINFO" );
        }
    } */
    
    public boolean autoPost12() {
        return (Boolean)Preferences.getPreferenceList().getParameterValue( "AutoPost12" );
    }
    
    public boolean autoStunRecovery() {
        return (Boolean)Preferences.getPreferenceList().getParameterValue( "AutoStunRecovery" );
    }
    
    public boolean autoUnconscious() {
        return (Boolean)Preferences.getPreferenceList().getParameterValue( "AutoUnconscious" );
    }
    
    public boolean getBooleanProfileOption(String option) {
        Profile p = getTargetProfile();
        if ( p != null && p.isProfileOptionSet(option) ) {
            return p.getBooleanProfileOption(option);
        }
        
        p = getRosterProfile();
        if ( p != null && p.isProfileOptionSet(option) ) {
            return p.getBooleanProfileOption(option);
        }
        
        p = ProfileManager.getDefaultProfile();
        if ( p != null && p.isProfileOptionSet(option) ) {
            return p.getBooleanProfileOption(option);
        }
        
        return false;
    }
    
    public void setBooleanProfileOption(String option, boolean value) {
        Profile p = getTargetProfile();
        if ( p == null ) {
            p = new Profile();
            setTargetProfile(p);
        }
        p.setBooleanProfileOption(option, value);
    }
    
    public void unsetBooleanProfileOption(String option) {
        Profile p = getTargetProfile();
        if ( p != null ) {
            p.unsetBooleanProfileOption(option);
        }
    }
    
    public boolean getProfileOptionIsSet(String option) {
        Profile p = getTargetProfile();
        if ( p != null ) {
            return p.isProfileOptionSet(option);
        }
        return false;
    }
    
    public Profile getRosterProfile() {
        // String value = getStringValue("Profile.NAME");
        if ( getRoster() != null ) {
            return getRoster().getRosterProfile();
        }
        
        return null;
    }
    
    public Profile getTargetProfile() {
        return (Profile) getValue("Profile.TARGET");
    }
    
    public void setTargetProfile(Profile profile) {
        add("Profile.TARGET", profile, true);
    }
    
    /** Add a sublist to the AbilityImport to track hierarchy
     *
     */
    public AbilityList createSublist(String sublist, String parent) {
        int index;
        String name = sublist;
        int i = 1;
        
        AbilityList parentList = null;
        AbilityList sublistList = null;
        
        parentList = findSublist(parent);
        if ( parentList != null ) {
            
            while ( findSublist(sublist) != null ) {
                i++;
                name = sublist + " (" + Integer.toString(i) + ")";
            }
            
            // index = createIndexed( "Sublist", "NAME", name );
            //  addIndexed( index, "Sublist", "IMPORTNAME", name, true);
            
            // if ( parent != null && findSublist(parent) != -1 ) {
            //      addIndexed( index, "Sublist", "PARENT", parent, true );
            // }
            sublistList = new DefaultAbilityList(name);
            
            parentList.addSublist(sublistList);
            
        }
        
        return sublistList;
        
    }
    
    /** Search for a specific sublist in AbilityImport
     */
    public AbilityList findSublist(String sublist) {
        return  getAbilityList().findSublist(sublist);
    }
    
    public String getUniqueAbilityName(Ability newAbility) {
        int i = 2;
        int index, count;
        boolean unique = false;
        Ability ability;
        String originalName = newAbility.getName();
        String newName = newAbility.getName();
        
        if ( ChampionsMatcher.matches( "(.*) \\(([0-9]*)\\)", originalName ) ) {
            originalName = ChampionsMatcher.getMatchedGroup(1);
            i = ChampionsMatcher.getIntMatchedGroup(2);
        }
        
        while ( unique == false ) {
            unique = true;
            AbilityIterator ai = getAbilities();
            while ( ai.hasNext() ) {
                ability = ai.nextAbility();
                if ( newAbility != ability && newName.equals( ability.getName() ) ) {
                    newName = originalName + " (" + Integer.toString(i++) + ")";
                    unique = false;
                    break;
                }
            }
        }
        return newName;
    }
    
    /** Returns the number of abilities a target is eligible to stack.
     * This will be controlled by a preference which will dictate how this is calculated.
     * @Returns number of abilities which can be stacked. -1 indicates no limit.
     */
    public int getMaximumStackedAbilities() {
        return -1;
    }
    
    /** Apply an adjustment (Aid/Drain/Heal) to a stat.
     *
     *  aidStat will apply an aid adjustment to a stat.  It will modify the <Stat>.AIDADJUSTMENT,
     *  <Stat>.DRAINADJUSTMENT, and <Stat>.TOTALADJUSTMENT appropriately for the adjustment.
     *
     * The method can be used for both applying an initial aid, for recording fade
     * results.  It can not be used to record changes in a stat's value caused by damage.
     * The damageStat method should be used to record damage to a stat.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
 /*  public StatChangeUndoable applyAidToCurrentStatCP(String stat, double amount, double limit, boolean fadeBelowBase, boolean affectsFigured) {
  
        double baseStatCP = getBaseStatCP(stat);
        double currentStatCP = getCurrentStatCP(stat);
        double totalAdjustmentCP = getTotalAdjustmentCP(stat);
        double aidAdjustmentCP = getAidAdjustmentCP(stat);
        double drainAdjustmentCP = getDrainAdjustmentCP(stat);
  
        StatChangeUndoable ai = new StatChangeUndoable(this, stat);
        ai.setInitialValues(currentStatCP, totalAdjustmentCP, aidAdjustmentCP, drainAdjustmentCP);
        ai.setAffectsFigured(affectsFigured);
  
        if ( amount > 0 ) {
            // This is an aid to the stat (not a fade of previous aid, or undo).
  
            // Apply limit for amount.
            if ( limit == UNLIMITED ) {
            }
            else if ( limit == TARGET_LIMITED ) {
                    double maximumStatValue = this.getMaximumStatCP(stat);
                    if ( currentStatCP + amount > maximumStatValue ) amount = maximumStatValue - currentStatCP;
                    if ( amount < 0 ) amount = 0;
            }
            else if ( limit == BASE_VALUE ) {
                    if ( currentStatCP + amount > baseStatCP ) amount = baseStatCP - currentStatCP;
                    if ( amount < 0 ) amount = 0;
            }
            else {
                    // The limit passed in is an actual value.  Limit damage by that value.
                    if ( currentStatCP + amount > limit ) amount = limit - currentStatCP;
                    if ( amount < 0 ) amount = 0;
            }
  
            if ( fadeBelowBase == true ) {
                // The aided amount will fade even if below the base amount
                totalAdjustmentCP += amount;
            }
            else {
                // The aided amount will only fade point above the base.
                // Adjust Total to represent amount that will actually fade.
                double amountBelow = currentStatCP + amount - baseStatCP;
                if ( amountBelow < 0 ) amountBelow = 0;
                totalAdjustmentCP += amountBelow;
            }
  
            // Adjust the actual aid amount, total amount, and current stat values.
            aidAdjustmentCP = aidAdjustmentCP + amount;
            totalAdjustmentCP = totalAdjustmentCP;
            currentStatCP = currentStatCP + amount;
  
            setAidAdjustmentCP(stat, aidAdjustmentCP);
            setTotalAdjustmentCP(stat, totalAdjustmentCP);
            setCurrentStatCP(stat, currentStatCP, affectsFigured);
  
            ai.setFinalValues(currentStatCP, totalAdjustmentCP, aidAdjustmentCP, drainAdjustmentCP);
            ai.setAmount(amount);
            return ai;
        }
        else {
            // This is an fade to the stat.
            amount = -1 * amount; // Make amount positive for easier handling
  
  
            if ( amount > aidAdjustmentCP ) amount = aidAdjustmentCP; // Contain fade to points added previously.
  
            double fadeAmount = amount; // Fade amount is the amount the stat will actually be adjusted by.
  
            if ( Preferences.getPreferenceList().getBooleanValue("Adjustment.CONCURRENTAIDANDDRAIN") == false ) {
                // Concurrent Fades are not enabled.  When concurrent Fades are not enabled,
                // the maximum fade adjustment = amount total is above 0.
                if ( amount > totalAdjustmentCP ) fadeAmount = totalAdjustmentCP;
                if ( fadeAmount < 0 ) fadeAmount = 0;
            }
            else {
                // Concurrent Fades are enabled.  When concurrent Fades are enabled,
                // the maximum fade amount = total - drain
                if ( amount > ( totalAdjustmentCP - drainAdjustmentCP ) ) fadeAmount = totalAdjustmentCP - drainAdjustmentCP;
                if ( fadeAmount < 0 ) fadeAmount = 0;
            }
  
            if ( fadeBelowBase == false ) {
                if ( currentStatCP - fadeAmount < baseStatCP ) fadeAmount = currentStatCP - baseStatCP;
                if ( fadeAmount < 0 ) fadeAmount = 0;
            }
  
            // Adjust the actual aid amount, total amount, and current stat values.
            aidAdjustmentCP = aidAdjustmentCP - amount;
            totalAdjustmentCP = totalAdjustmentCP - fadeAmount;
            currentStatCP = currentStatCP - fadeAmount;
  
            setAidAdjustmentCP(stat, aidAdjustmentCP);
            setTotalAdjustmentCP(stat, totalAdjustmentCP);
            setCurrentStatCP(stat, currentStatCP, affectsFigured);
  
            ai.setFinalValues(currentStatCP, totalAdjustmentCP, aidAdjustmentCP, drainAdjustmentCP);
            ai.setAmount(amount);
            return ai;
        }
    } */
    
    /** Apply an adjustment(Aid/Drain) to a stat.
     *
     *  aidStat will apply an aid adjustment to a stat.  It will modify the <Stat>.AIDADJUSTMENT,
     *  <Stat>.DRAINADJUSTMENT, and <Stat>.TOTALADJUSTMENT appropriately for the adjustment.
     *
     * The method can be used for both applying an initial drain, for recording fade
     * results.  It can not be used to record changes in a stat's value caused by damage.
     * The damageStat method should be used to record damage to a stat.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
  /*  public StatChangeUndoable applyDrainToCurrentStatCP(String stat, double amount, double limit, boolean affectsFigured) {
        double baseStatCP = getBaseStatCP(stat);
        double currentStatCP = getCurrentStatCP(stat);
        double totalAdjustmentCP = getTotalAdjustmentCP(stat);
        double aidAdjustmentCP = getAidAdjustmentCP(stat);
        double drainAdjustmentCP = getDrainAdjustmentCP(stat);
   
        StatChangeUndoable ai = new StatChangeUndoable(this, stat);
        ai.setInitialValues(currentStatCP, totalAdjustmentCP, aidAdjustmentCP, drainAdjustmentCP);
        ai.setAffectsFigured(affectsFigured);
   
        if ( amount > 0 ) {
            // Apply limits
            if ( limit == UNLIMITED ) {
            }
            else if ( limit == TARGET_LIMITED ) {
                    int minimumStatValue = this.getMinimumStatCP(stat);
                    if ( currentStatCP - amount < minimumStatValue ) amount = currentStatCP - minimumStatValue;
                    if ( amount < 0 ) amount = 0;
            }
            else if ( limit == BASE_VALUE ) {
                    double baseStat = getAdjustedBaseStatCP(stat);
                    if ( currentStatCP - amount < baseStat ) amount = currentStatCP - baseStat;
                    if ( amount < 0 ) amount = 0;
            }
            else {
                    // The limit passed in is an actual value.  Limit damage by that value.
                    if ( currentStatCP - amount < limit ) amount = currentStatCP - limit;
                    if ( amount < 0 ) amount = 0;
            }
   
            // Adjust the actual aid amount, total amount, and current stat values.
            drainAdjustmentCP = drainAdjustmentCP - amount;
            totalAdjustmentCP = totalAdjustmentCP - amount;
            currentStatCP = currentStatCP - amount;
   
            setDrainAdjustmentCP(stat, drainAdjustmentCP);
            setTotalAdjustmentCP(stat, totalAdjustmentCP);
            setCurrentStatCP(stat, currentStatCP, affectsFigured);
   
            ai.setFinalValues(currentStatCP, totalAdjustmentCP, aidAdjustmentCP, drainAdjustmentCP);
            ai.setAmount(amount);
            return ai;
        }
        else {
            amount = amount * -1; // Reverse, so we are working with positive amounts
   
            // Points are being faded...
            if ( amount > -1 * drainAdjustmentCP ) amount = -1 * drainAdjustmentCP;
   
            double fadeAmount = amount; // Amount actally removed from stat
   
            if ( Preferences.getPreferenceList().getBooleanValue("Adjustment.CONCURRENTAIDANDDRAIN") == false ) {
                // Concurrent drain/aid is not enabled.
                // Max fade amount = amount total < 0
                if ( amount > totalAdjustmentCP * -1 ) fadeAmount = totalAdjustmentCP * -1;
                if ( fadeAmount < 0 ) fadeAmount = 0;
            }
            else {
                // Concurrent drain/aid is enabled.
                // Max fade amount = difference between total and aid.
                if ( amount > aidAdjustmentCP - totalAdjustmentCP ) fadeAmount = aidAdjustmentCP - totalAdjustmentCP;
                if ( fadeAmount < 0 ) fadeAmount = 0;
            }
   
            // Adjust the actual aid amount, total amount, and current stat values.
            drainAdjustmentCP = drainAdjustmentCP + amount;
            totalAdjustmentCP = totalAdjustmentCP + fadeAmount;
            currentStatCP = currentStatCP + amount;
   
            setDrainAdjustmentCP(stat, drainAdjustmentCP + amount);
            setTotalAdjustmentCP(stat, totalAdjustmentCP + fadeAmount);
            setCurrentStatCP(stat, currentStatCP + amount, affectsFigured);
   
            ai.setFinalValues(currentStatCP, totalAdjustmentCP, aidAdjustmentCP, drainAdjustmentCP);
            ai.setAmount(amount);
            return ai;
        }
    } */
    
    /** Sets the current value of a stat.
     *
     * increaseStat() will apply an increase adjustment to a stat.
     *
     * This method should be used to apply all damage to a stat.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
  /*  public StatChangeUndoable applySetToCurrentStatCP(String stat, double amount, boolean affectsFigured) {
        double currentStatCP = getCurrentStatCP(stat);
        double totalAdjustmentCP = getTotalAdjustmentCP(stat);
        double aidAdjustmentCP = getAidAdjustmentCP(stat);
        double drainAdjustmentCP = getDrainAdjustmentCP(stat);
   
        StatChangeUndoable ai = new StatChangeUndoable(this, stat);
        ai.setInitialValues(currentStatCP, totalAdjustmentCP, aidAdjustmentCP, drainAdjustmentCP);
        ai.setAffectsFigured(affectsFigured);
   
        double change = amount - currentStatCP;
   
        // Set the stats value in the target.
        setCurrentStatCP(stat, amount, affectsFigured);
   
        // Read the changed CP values.
        currentStatCP = amount;
   
        ai.setFinalValues(currentStatCP, totalAdjustmentCP, aidAdjustmentCP, drainAdjustmentCP);
   
        // Set the actual amount which was applied.
        ai.setAmount(change);
   
        return ai;
    } */
    
    /** Sets the adjustedBase value of a stat.
     *
     * increaseStat() will apply an increase adjustment to a stat.
     *
     * This method should be used to apply all damage to a stat.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
    public CharacteristicChangeUndoable applySetToAdjustedStatCP(String stat, double amount, boolean affectsFigured) {
        CharacteristicPrimary c = getCharacteristic(stat);
        
        CharacteristicChangeUndoable ccu = new CharacteristicChangeUndoable(this, c);
        
        double totalCP = c.getAdjustedAFStatCP();
        
        double change = amount - totalCP;
        
        // Set the stats value in the target.
        setAdjustedStatCP(stat, amount, affectsFigured);
        
        ccu.setFinalValues();
        
        // Set the actual amount which was applied.
        ccu.setAmount(change);
        
        return ccu;
    }
    
    /** Sets the base value of a stat.
     *
     * increaseStat() will apply an increase adjustment to a stat.
     *
     * This method should be used to apply all damage to a stat.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
    public CharacteristicChangeUndoable applySetToBaseStatCP(String stat, double amount) {
        
    /*    double adjustedBaseStatCP = getAdjustedBaseStatCP(stat);
        double currentStatCP = getCurrentStatCP(stat);
        double totalAdjustmentCP = getTotalAdjustmentCP(stat);
        double aidAdjustmentCP = getAidAdjustmentCP(stat);
        double drainAdjustmentCP = getDrainAdjustmentCP(stat); */
        
        CharacteristicPrimary c = getCharacteristic(stat);
        
        CharacteristicChangeUndoable ccu = new CharacteristicChangeUndoable(this, c);
        
        double baseStatCP = c.getBaseStatCP();
        
        double change = amount - baseStatCP;
        
        // Set the stats value in the target.
        setBaseStatCP(stat, amount);
        
        ccu.setFinalValues();
        
        // Set the actual amount which was applied.
        ccu.setAmount(change);
        
        return ccu;
    }
    
    /** Apply damage to a stat.
     *
     * damageStat() will apply an damage adjustment to a stat.  It will modify the <Stat>.TOTALADJUSTMENT
     * appropriately for the adjustment.
     *
     * This method should be used to apply all damage to a stat.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
   /* public StatChangeUndoable applyDamageToCurrentStatCP(String stat, double amount, boolean affectsFigured) {
        double baseStatCP = getBaseStatCP(stat);
        double currentStatCP = getCurrentStatCP(stat);
        double totalAdjustmentCP = getTotalAdjustmentCP(stat);
        double aidAdjustmentCP = getAidAdjustmentCP(stat);
        double drainAdjustmentCP = getDrainAdjustmentCP(stat);
    
        StatChangeUndoable ai = new StatChangeUndoable(this, stat);
        ai.setInitialValues(currentStatCP, totalAdjustmentCP, aidAdjustmentCP, drainAdjustmentCP);
        ai.setAffectsFigured(affectsFigured);
    
        if ( amount > 0 ) {
            currentStatCP -= amount;
            if ( totalAdjustmentCP > 0 ) {
                totalAdjustmentCP -= amount;
                if ( totalAdjustmentCP < 0 ) totalAdjustmentCP = 0;
            }
        }
    
        ai.setFinalValues(currentStatCP, totalAdjustmentCP, aidAdjustmentCP, drainAdjustmentCP);
        return ai;
    } */
    
    /** Apply healing to a stat.
     *
     * healStat() will apply an positive, permanent adjustment to a stat.  It will modify the <Stat>.TOTALADJUSTMENT
     * appropriately to account for the healing.
     *
     * This method should be used to apply all healing to a stat caused by recoveries, natural healing,
     * etc.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
   /* public StatChangeUndoable applyHealToCurrentStatCP(String stat, double amount, boolean affectsFigured) {
        double baseStatCP = getBaseStatCP(stat);
        double currentStatCP = getCurrentStatCP(stat);
        double totalAdjustmentCP = getTotalAdjustmentCP(stat);
        double aidAdjustmentCP = getAidAdjustmentCP(stat);
        double drainAdjustmentCP = getDrainAdjustmentCP(stat);
    
        StatChangeUndoable ai = new StatChangeUndoable(this, stat);
        ai.setInitialValues(currentStatCP, totalAdjustmentCP, aidAdjustmentCP, drainAdjustmentCP);
        ai.setAffectsFigured(affectsFigured);
    
        if ( amount > 0 ) {
            if ( currentStatCP + amount > baseStatCP ) amount = currentStatCP + amount - baseStatCP;
            if ( amount < 0 ) amount = 0;
    
            currentStatCP += amount;
            if ( totalAdjustmentCP < 0 ) {
                totalAdjustmentCP += amount;
                if ( totalAdjustmentCP > 0 ) totalAdjustmentCP = 0;
            }
        }
    
        ai.setFinalValues(currentStatCP, totalAdjustmentCP, aidAdjustmentCP, drainAdjustmentCP);
        return ai;
    } */
    
    /** Returns the total amount stat has been adjusted by aids and drains.
     * The total amount adjusted is stored in <Stat>.TOTALADJUSTMENT.
     *
     * TOTALADJUSTMENT is always measured in CharacterPoints.
     */
    public double getTotalAdjustmentCP(String stat) {
        Double i = getDoubleValue( stat + ".TOTALADJUSTMENT" );
        return (i==null) ? 0 : i.doubleValue();
    }
    
    /** Returns the total amount stat has been adjusted by aids.
     * This value represents the total aid applied to a stat that hasn't faded yet.
     * The total amount adjusted is stored in <Stat>.AIDADJUSTMENT.
     *
     * AIDADJUSTMENT is always measured in CharacterPoints.
     */
    public double getAidAdjustmentCP(String stat) {
        Double i = getDoubleValue( stat + ".AIDADJUSTMENT" );
        return (i==null) ? 0 : i.doubleValue();
    }
    
    /** Returns the total amount stat has been adjusted by drains.
     * This value represents the total aid applied to a stat that hasn't faded yet.
     * The total amount adjusted is stored in <Stat>.DRAINADJUSTMENT.
     *
     * DRAINADJUSTMENT is always measured in CharacterPoints.
     */
    public double getDrainAdjustmentCP(String stat) {
        Double i = getDoubleValue( stat + ".DRAINADJUSTMENT" );
        return (i==null) ? 0 : i.doubleValue();
    }
    
    /** Sets the total amount stat has been adjusted by aids and drains.
     * The total amount adjusted is stored in <Stat>.TOTALADJUSTMENT.
     *
     * TOTALADJUSTMENT is always measured in CharacterPoints.
     */
    public void setTotalAdjustmentCP(String stat, double total) {
        add( stat + ".TOTALADJUSTMENT", new Double(total), true);
    }
    
    /** Returns the total amount stat has been adjusted by aids.
     * This value represents the total aid applied to a stat that hasn't faded yet.
     * The total amount adjusted is stored in <Stat>.AIDADJUSTMENT.
     *
     * AIDADJUSTMENT is always measured in CharacterPoints.
     */
    public void setAidAdjustmentCP(String stat, double total) {
        add( stat + ".AIDADJUSTMENT", new Double(total), true);
    }
    
    /** Returns the total amount stat has been adjusted by drains.
     * This value represents the total aid applied to a stat that hasn't faded yet.
     * The total amount adjusted is stored in <Stat>.DRAINADJUSTMENT.
     *
     * DRAINADJUSTMENT is always measured in CharacterPoints.
     */
    public void setDrainAdjustmentCP(String stat, double total) {
        add( stat + ".DRAINADJUSTMENT", new Double(total), true);
    }
    
    /** Apply an adjustment (Aid/Drain/Heal) to a stat.
     *
     *  aidStat will apply an aid adjustment to a stat.  It will modify the <Stat>.AIDADJUSTMENT,
     *  <Stat>.DRAINADJUSTMENT, and <Stat>.TOTALADJUSTMENT appropriately for the adjustment.
     *
     * The method can be used for both applying an initial aid, for recording fade
     * results.  It can not be used to record changes in a stat's value caused by damage.
     * The damageStat method should be used to record damage to a stat.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
  /*  public StatChangeUndoable applyAidToCurrentStat(String stat, double amount, double limit, boolean fadeBelowBase, boolean affectsFigured) {
   
        //double baseStatCP = getBaseStatCP(stat);
        double currentStatCP = getCurrentStatCP(stat);
        double totalAdjustmentCP = getTotalAdjustmentCP(stat);
        double aidAdjustmentCP = getAidAdjustmentCP(stat);
        double drainAdjustmentCP = getDrainAdjustmentCP(stat);
   
        StatChangeUndoable ai = new StatChangeUndoable(this, stat);
        ai.setInitialValues(currentStatCP, totalAdjustmentCP, aidAdjustmentCP, drainAdjustmentCP);
        ai.setAffectsFigured(affectsFigured);
   
        // Convert amount in stat metric to amount in CP metric.
        amount *= getCPperStat(stat);
   
        double baseStatCP = getAdjustedBaseStatCP(stat);
   
        if ( amount > 0 ) {
            // This is an aid to the stat (not a fade of previous aid, or undo).
   
            // Apply limit for amount.
            if ( limit == UNLIMITED ) {
            }
            else if ( limit == TARGET_LIMITED ) {
                    int maximumStatValue = this.getMaximumStatCP(stat);
                    if ( currentStatCP + amount > maximumStatValue ) amount = maximumStatValue - currentStatCP;
                    if ( amount < 0 ) amount = 0;
            }
            else if ( limit == BASE_VALUE ) {
                    if ( currentStatCP + amount > baseStatCP ) amount = baseStatCP - currentStatCP;
                    if ( amount < 0 ) amount = 0;
            }
            else {
                    // The limit passed in is an actual value.  Limit damage by that value.
                    if ( currentStatCP + amount > limit ) amount = limit - currentStatCP;
                    if ( amount < 0 ) amount = 0;
            }
   
            if ( fadeBelowBase == true ) {
                // The aided amount will fade even if below the base amount
                totalAdjustmentCP += amount;
            }
            else {
                // The aided amount will only fade point above the base.
                // Adjust Total to represent amount that will actually fade.
                double amountBelow = currentStatCP + amount - baseStatCP;
                if ( amountBelow < 0 ) amountBelow = 0;
                totalAdjustmentCP += amountBelow;
            }
   
            // Adjust the actual aid amount, total amount, and current stat values.
            aidAdjustmentCP = aidAdjustmentCP + amount;
            totalAdjustmentCP = totalAdjustmentCP;
            currentStatCP = currentStatCP + amount;
   
            setAidAdjustmentCP(stat, aidAdjustmentCP);
            setTotalAdjustmentCP(stat, totalAdjustmentCP);
            setCurrentStatCP(stat, currentStatCP, affectsFigured);
   
            ai.setFinalValues(currentStatCP, totalAdjustmentCP, aidAdjustmentCP, drainAdjustmentCP);
   
            // Convert amount back to stat metric
            amount /= getCPperStat(stat);
            ai.setAmount(amount);
   
            return ai;
        }
        else {
            // This is an fade to the stat.
            amount = -1 * amount; // Make amount positive for easier handling
   
   
            if ( amount > aidAdjustmentCP ) amount = aidAdjustmentCP; // Contain fade to points added previously.
   
            double fadeAmount = amount; // Fade amount is the amount the stat will actually be adjusted by.
   
            if ( Preferences.getPreferenceList().getBooleanValue("Adjustment.CONCURRENTAIDANDDRAIN") == false ) {
                // Concurrent Fades are not enabled.  When concurrent Fades are not enabled,
                // the maximum fade adjustment = amount total is above 0.
                if ( amount > totalAdjustmentCP ) fadeAmount = totalAdjustmentCP;
                if ( fadeAmount < 0 ) fadeAmount = 0;
            }
            else {
                // Concurrent Fades are enabled.  When concurrent Fades are enabled,
                // the maximum fade amount = total - drain
                if ( amount > ( totalAdjustmentCP - drainAdjustmentCP ) ) fadeAmount = totalAdjustmentCP - drainAdjustmentCP;
                if ( fadeAmount < 0 ) fadeAmount = 0;
            }
   
            if ( fadeBelowBase == false ) {
                if ( currentStatCP - fadeAmount < baseStatCP ) fadeAmount = currentStatCP - baseStatCP;
                if ( fadeAmount < 0 ) fadeAmount = 0;
            }
   
            // Adjust the actual aid amount, total amount, and current stat values.
            aidAdjustmentCP = aidAdjustmentCP - amount;
            totalAdjustmentCP = totalAdjustmentCP - fadeAmount;
            currentStatCP = currentStatCP - fadeAmount;
   
            setAidAdjustmentCP(stat, aidAdjustmentCP);
            setTotalAdjustmentCP(stat, totalAdjustmentCP);
            setCurrentStatCP(stat, currentStatCP, affectsFigured);
   
            ai.setFinalValues(currentStatCP, totalAdjustmentCP, aidAdjustmentCP, drainAdjustmentCP);
   
            // Convert amount back to stat metric
            amount /= getCPperStat(stat);
            ai.setAmount(amount);
   
            return ai;
        }
    } */
    
    /** Apply an adjustment(Aid/Drain) to a stat.
     *
     *  aidStat will apply an aid adjustment to a stat.  It will modify the <Stat>.AIDADJUSTMENT,
     *  <Stat>.DRAINADJUSTMENT, and <Stat>.TOTALADJUSTMENT appropriately for the adjustment.
     *
     * The method can be used for both applying an initial drain, for recording fade
     * results.  It can not be used to record changes in a stat's value caused by damage.
     * The damageStat method should be used to record damage to a stat.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
 /*   public StatChangeUndoable applyDrainToCurrentStat(String stat, double amount, double limit, boolean affectsFigured) {
  
        double currentStatCP = getCurrentStatCP(stat);
        double totalAdjustmentCP = getTotalAdjustmentCP(stat);
        double aidAdjustmentCP = getAidAdjustmentCP(stat);
        double drainAdjustmentCP = getDrainAdjustmentCP(stat);
  
        StatChangeUndoable ai = new StatChangeUndoable(this, stat);
        ai.setInitialValues(currentStatCP, totalAdjustmentCP, aidAdjustmentCP, drainAdjustmentCP);
        ai.setAffectsFigured(affectsFigured);
  
        // Translate amount into CP metric
        amount *= getCPperStat(stat);
  
        if ( amount > 0 ) {
            // Points are actually being drained.
  
            // Apply limits
            if ( limit == UNLIMITED ) {
            }
            else if ( limit == TARGET_LIMITED ) {
                    int minimumStatValue = this.getMinimumStatCP(stat);
                    if ( currentStatCP - amount < minimumStatValue ) amount = currentStatCP - minimumStatValue;
                    if ( amount < 0 ) amount = 0;
            }
            else if ( limit == BASE_VALUE ) {
                    double baseStat = getAdjustedBaseStatCP(stat);
                    if ( currentStatCP - amount < baseStat ) amount = currentStatCP - baseStat;
                    if ( amount < 0 ) amount = 0;
            }
            else {
                    // The limit passed in is an actual value.  Limit damage by that value.
                    if ( currentStatCP - amount < limit ) amount = currentStatCP - limit;
                    if ( amount < 0 ) amount = 0;
            }
  
            // Adjust the actual aid amount, total amount, and current stat values.
            drainAdjustmentCP = drainAdjustmentCP - amount;
            totalAdjustmentCP = totalAdjustmentCP - amount;
            currentStatCP = currentStatCP - amount;
  
            setDrainAdjustmentCP(stat, drainAdjustmentCP);
            setTotalAdjustmentCP(stat, totalAdjustmentCP);
            setCurrentStatCP(stat, currentStatCP, affectsFigured);
  
            // Translate amount back to stat metric
            amount /= getCPperStat(stat);
            ai.setAmount(amount);
  
            ai.setFinalValues(currentStatCP, totalAdjustmentCP, aidAdjustmentCP, drainAdjustmentCP);
            return ai;
        }
        else {
            amount = amount * -1; // Reverse, so we are working with positive amounts
  
            // Points are being faded...
            if ( amount > -1 * drainAdjustmentCP ) amount = -1 * drainAdjustmentCP;
  
            double fadeAmount = amount; // Amount actally removed from stat
  
            if ( Preferences.getPreferenceList().getBooleanValue("Adjustment.CONCURRENTAIDANDDRAIN") == false ) {
                // Concurrent drain/aid is not enabled.
                // Max fade amount = amount total < 0
                if ( amount > totalAdjustmentCP * -1 ) fadeAmount = totalAdjustmentCP * -1;
                if ( fadeAmount < 0 ) fadeAmount = 0;
            }
            else {
                // Concurrent drain/aid is enabled.
                // Max fade amount = difference between total and aid.
                if ( amount > aidAdjustmentCP - totalAdjustmentCP ) fadeAmount = aidAdjustmentCP - totalAdjustmentCP;
                if ( fadeAmount < 0 ) fadeAmount = 0;
            }
  
            // Adjust the actual aid amount, total amount, and current stat values.
            drainAdjustmentCP = drainAdjustmentCP + amount;
            totalAdjustmentCP = totalAdjustmentCP + fadeAmount;
            currentStatCP = currentStatCP + amount;
  
            setDrainAdjustmentCP(stat, drainAdjustmentCP + amount);
            setTotalAdjustmentCP(stat, totalAdjustmentCP + fadeAmount);
            setCurrentStatCP(stat, currentStatCP + amount, affectsFigured);
  
            ai.setFinalValues(currentStatCP, totalAdjustmentCP, aidAdjustmentCP, drainAdjustmentCP);
  
            // Convert amount back to stat metric
            amount /= getCPperStat(stat);
            ai.setAmount(amount);
  
            return ai;
        }
    } */
    
    /** Apply damage to a stat.
     *
     * damageStat() will apply an damage adjustment to a stat.  It will modify the <Stat>.TOTALADJUSTMENT
     * appropriately for the adjustment.
     *
     * This method should be used to apply all damage to a stat.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
    public CharacteristicChangeUndoable applyDamageToCurrentStat(String stat, double amount, double limit) {
        CharacteristicPrimary c = getCharacteristic(stat);
        
        CharacteristicChangeUndoable ccu = new CharacteristicChangeUndoable(this, c);
        
        int currentStat = c.getCurrentStat();
        
        if ( amount > 0 ) {
            // Adjust the amount according to the value of limit.
            if ( limit == UNLIMITED ) {
            } else if ( limit == TARGET_LIMITED ) {
                int minimumStatValue = this.getMinimumStat(stat);
                if ( currentStat - amount < minimumStatValue ) amount = currentStat - minimumStatValue;
                if ( amount < 0 ) amount = 0;
            } else if ( limit == BASE_VALUE ) {
                int baseStat = c.getAdjustedStat();
                if ( currentStat - amount < baseStat ) amount = currentStat - baseStat;
                if ( amount < 0 ) amount = 0;
            } else {
                // The limit passed in is an actual value.  Limit damage by that value.
                if ( currentStat - amount < limit ) amount = currentStat - limit;
                if ( amount < 0 ) amount = 0;
            }
            
            currentStat -= amount; // Apply the actual damage.
            
            // Set the stats value in the target.
            setCurrentStat(stat, currentStat);
            
            
          /*  // Calculate the totalAdjustmentChange
            double cpChange = currentStatCP - newStatCP;
            if ( cpChange < 0 ) cpChange = 0;
           
            if ( totalAdjustmentCP > 0 ) {
                totalAdjustmentCP -= cpChange ;
                if ( totalAdjustmentCP < 0 ) totalAdjustmentCP = 0;
            }
           
            setTotalAdjustmentCP(stat, totalAdjustmentCP); */
            
            // Update Current Stat CP to be the new value
            // currentStatCP = newStatCP;
        } else {
            // Can't have negative damage...
            amount = 0;
            
            if ( DEBUG == 1 ) System.out.println("Attempt made to apply negative damage amount to target.");
        }
        
        ccu.setFinalValues();
        
        // Set the actual amount which was applied.
        ccu.setAmount(amount);
        
        return ccu;
    }
    
    /** Apply healing to a stat.
     *
     * healStat() will apply an positive, permanent adjustment to a stat.  It will modify the <Stat>.TOTALADJUSTMENT
     * appropriately to account for the healing.
     *
     * This method should be used to apply all healing to a stat caused by recoveries, natural healing,
     * etc.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
    public CharacteristicChangeUndoable applyHealToCurrentStat(String stat, double amount, double limit) {
        
        CharacteristicPrimary c = getCharacteristic(stat);
        
        CharacteristicChangeUndoable ccu = new CharacteristicChangeUndoable(this, c);
        int currentStat = c.getCurrentStat();
        
        if ( amount > 0 ) {
            // Adjust the amount according to the value of limit.
            if ( limit == UNLIMITED ) {
            } else if ( limit == TARGET_LIMITED ) {
                int maximumStatValue = this.getMaximumStat(stat);
                if ( currentStat + amount > maximumStatValue ) amount = maximumStatValue - currentStat;
                if ( amount < 0 ) amount = 0;
            } else if ( limit == BASE_VALUE ) {
                int baseStat = c.getAdjustedStat();
                if ( currentStat + amount > baseStat ) amount = baseStat - currentStat;
                if ( amount < 0 ) amount = 0;
            } else {
                // The limit passed in is an actual value.  Limit heal by that value.
                if ( currentStat + amount > limit ) amount = limit - currentStat;
                if ( amount < 0 ) amount = 0;
            }
            
            currentStat += amount; // Apply the actual heal.
            
            // Set the stats value in the target.
            setCurrentStat(stat, currentStat);
            
            // Read the changed CP values.
            //  double newStatCP = getCurrentStatCP(stat);
            
            // Calculate the totalAdjustmentChange
     /*       double cpChange = newStatCP - currentStatCP;
            if ( cpChange < 0 ) cpChange = 0;
      
            if ( totalAdjustmentCP < 0 ) {
                totalAdjustmentCP += cpChange ;
                if ( totalAdjustmentCP > 0 ) totalAdjustmentCP = 0;
            }
      
            setTotalAdjustmentCP(stat, totalAdjustmentCP);
      
            // Update Current Stat CP to be the new value
            currentStatCP = newStatCP; */
        } else {
            // Can't have negative damage...
            amount = 0;
            
            if ( DEBUG == 1 ) System.out.println("Attempt made to apply negative healing amount to target.");
        }
        
        ccu.setFinalValues();
        
        // Set the actual amount which was applied.
        ccu.setAmount(amount);
        
        return ccu;
    }
    
    /** Apply healing to a stat.
     *
     * healStat() will apply an positive, permanent adjustment to a stat.  It will modify the <Stat>.TOTALADJUSTMENT
     * appropriately to account for the healing.
     *
     * This method should be used to apply all healing to a stat caused by recoveries, natural healing,
     * etc.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
    public CharacteristicChangeUndoable applyHealToCurrentStatCP(String stat, double amount, double limit) {
        
        CharacteristicPrimary c = getCharacteristic(stat);
        
        CharacteristicChangeUndoable ccu = new CharacteristicChangeUndoable(this, c);
        
        double currentStat = c.getCurrentStatCP();
        if ( amount > 0 ) {
            // Adjust the amount according to the value of limit.
            if ( limit == UNLIMITED ) {
            } else if ( limit == TARGET_LIMITED ) {
                double maximumStatValue = this.getMaximumStatCP(stat);
                if ( currentStat + amount > maximumStatValue ) amount = maximumStatValue - currentStat;
                if ( amount < 0 ) amount = 0;
            } else if ( limit == BASE_VALUE ) {
                double baseStat = c.getAdjustedStatCP();
                if ( currentStat + amount > baseStat ) amount = baseStat - currentStat;
                if ( amount < 0 ) amount = 0;
            } else {
                // The limit passed in is an actual value.  Limit heal by that value.
                if ( currentStat + amount > limit ) amount = limit - currentStat;
                if ( amount < 0 ) amount = 0;
            }
            
            currentStat += amount; // Apply the actual heal.
            
            // Set the stats value in the target.
            setCurrentStatCP(stat, currentStat);
            
            // Read the changed CP values.
            //  double newStatCP = getCurrentStatCP(stat);
            
            // Calculate the totalAdjustmentChange
     /*       double cpChange = newStatCP - currentStatCP;
            if ( cpChange < 0 ) cpChange = 0;
      
            if ( totalAdjustmentCP < 0 ) {
                totalAdjustmentCP += cpChange ;
                if ( totalAdjustmentCP > 0 ) totalAdjustmentCP = 0;
            }
      
            setTotalAdjustmentCP(stat, totalAdjustmentCP);
      
            // Update Current Stat CP to be the new value
            currentStatCP = newStatCP; */
        } else {
            // Can't have negative damage...
            amount = 0;
            
            if ( DEBUG == 1 ) System.out.println("Attempt made to apply negative healing amount to target.");
        }
        
        ccu.setFinalValues();
        
        // Set the actual amount which was applied.
        ccu.setAmount(amount);
        
        return ccu;
    }
    
    /** Apply an increase to the adjustedBase of a stat.
     *
     * increaseStat() will apply an increase adjustment to a stat.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
    public CharacteristicChangeUndoable applyIncreaseToAdjustedStat(String stat, double amount, double limit, boolean affectsFigured) {
        CharacteristicPrimary c = getCharacteristic(stat);
        
        CharacteristicChangeUndoable ccu = new CharacteristicChangeUndoable(this, c);
        
        int adjustedBaseStat = getAdjustedStat(stat);
        
        if ( amount > 0 ) {
            // Adjust the amount according to the value of limit.
            if ( limit == UNLIMITED ) {
            } else if ( limit == TARGET_LIMITED ) {
                int maximumStatValue = this.getMaximumStat(stat);
                if ( adjustedBaseStat + amount > maximumStatValue ) amount = maximumStatValue - adjustedBaseStat;
                if ( amount < 0 ) amount = 0;
            }
      /*              else if ( limit == BASE_VALUE ) {
                    int baseStat = getAdjustedBaseStat(stat);
                    if ( adjustedBaseStat + amount > baseStat ) amount = baseStat - adjustedBaseStat;
                    if ( amount < 0 ) amount = 0;
            } */
            else {
                // The limit passed in is an actual value.  Limit damage by that value.
                if ( adjustedBaseStat - amount < limit ) amount = limit - adjustedBaseStat;
                if ( amount < 0 ) amount = 0;
            }
            
            adjustedBaseStat += amount; // Apply the actual damage.
            
            // Set the stats value in the target.
            setAdjustedStat(stat, adjustedBaseStat, affectsFigured);
            
        } else {
            // Can't have negative damage...
            amount = 0;
            
            if ( DEBUG == 1 ) System.out.println("Attempt made to apply negative damage amount to target.");
        }
        
        ccu.setFinalValues();
        
        // Set the actual amount which was applied.
        ccu.setAmount(amount);
        
        return ccu;
    }
    
    /** Apply an increase to the current value of a stat.
     *
     * increaseStat() will apply an increase adjustment to a stat.
     *
     * This method should be used to apply all damage to a stat.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
    public CharacteristicChangeUndoable applyIncreaseToCurrentStat(String stat, double amount, double limit) {
      /*  double baseStatCP = getBaseStatCP(stat);
        double adjustedBaseStatCP = getAdjustedBaseStatCP(stat);
        double currentStatCP = getCurrentStatCP(stat);
        double totalAdjustmentCP = getTotalAdjustmentCP(stat);
        double aidAdjustmentCP = getAidAdjustmentCP(stat);
        double drainAdjustmentCP = getDrainAdjustmentCP(stat); */
        
        int statValue = getCurrentStat(stat);
        
        CharacteristicPrimary c = getCharacteristic(stat);
        
        CharacteristicChangeUndoable ccu = new CharacteristicChangeUndoable(this, c);
        
        if ( amount > 0 ) {
            // Adjust the amount according to the value of limit.
            if ( limit == UNLIMITED ) {
            } else if ( limit == TARGET_LIMITED ) {
                int maximumStatValue = this.getMaximumStat(stat);
                if ( statValue + amount > maximumStatValue ) amount = maximumStatValue - statValue;
                if ( amount < 0 ) amount = 0;
            } else if ( limit == BASE_VALUE ) {
                int baseStat = c.getAdjustedStat();
                if ( statValue + amount > baseStat ) amount = baseStat - statValue;
                if ( amount < 0 ) amount = 0;
            } else {
                // The limit passed in is an actual value.  Limit damage by that value.
                if ( statValue - amount < limit ) amount = limit - statValue;
                if ( amount < 0 ) amount = 0;
            }
            
            statValue += amount; // Apply the actual damage.
            
            // Set the stats value in the target.
            setCurrentStat(stat, statValue);
            
            // Read the changed CP values.
            //currentStatCP = getCurrentStatCP(stat);
        } else {
            // Can't have negative damage...
            amount = 0;
            
            if ( DEBUG == 1 ) System.out.println("Attempt made to apply negative damage amount to target.");
        }
        
        ccu.setFinalValues();
        
        // Set the actual amount which was applied.
        ccu.setAmount(amount);
        
        return ccu;
    }
    
    /** Apply an decrease to the adjusted base value of a stat.
     *
     * increaseStat() will apply an increase adjustment to a stat.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
    public CharacteristicChangeUndoable applyDecreaseToAdjustedStat(String stat, double amount, double limit, boolean affectsFigured) {
        CharacteristicPrimary c = getCharacteristic(stat);
        
        CharacteristicChangeUndoable ccu = new CharacteristicChangeUndoable(this, c);
        
        int statValue = c.getAdjustedStat();
        
        if ( amount > 0 ) {
            // Adjust the amount according to the value of limit.
            if ( limit == UNLIMITED ) {
            } else if ( limit == TARGET_LIMITED ) {
                int minimumStatValue = this.getMinimumStat(stat);
                if ( statValue - amount < minimumStatValue ) amount = statValue - minimumStatValue;
                if ( amount < 0 ) amount = 0;
            } else if ( limit == BASE_VALUE ) {
                int baseStat = c.getBaseStat();
                if ( statValue - amount < baseStat ) amount = statValue - baseStat;
                if ( amount < 0 ) amount = 0;
            } else {
                // The limit passed in is an actual value.  Limit damage by that value.
                if ( statValue - amount < limit ) amount = statValue - limit;
                if ( amount < 0 ) amount = 0;
            }
            
            statValue -= amount; // Apply the actual damage.
            
            // Set the stats value in the target.
            setAdjustedStat(stat, statValue, affectsFigured);
        } else {
            // Can't have negative damage...
            amount = 0;
            
            if ( DEBUG == 1 ) System.out.println("Attempt made to apply negative damage amount to target.");
        }
        
        ccu.setFinalValues();
        
        // Set the actual amount which was applied.
        ccu.setAmount(amount);
        
  //  targetAdjusted();
        
        return ccu;
    }
    
    /** Apply an decrease to the current value of a stat.
     *
     * increaseStat() will apply an increase adjustment to a stat.
     *
     * This method should be used to apply all damage to a stat.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
    public CharacteristicChangeUndoable applyDecreaseToCurrentStat(String stat, double amount, double limit) {
        CharacteristicPrimary c = getCharacteristic(stat);
        
        CharacteristicChangeUndoable ccu = new CharacteristicChangeUndoable(this, c);
        
        int statValue = getCurrentStat(stat);
        if ( amount > 0 ) {
            // Adjust the amount according to the value of limit.
            if ( limit == UNLIMITED ) {
            } else if ( limit == TARGET_LIMITED ) {
                int minimumStatValue = this.getMinimumStat(stat);
                if ( statValue - amount < minimumStatValue ) amount = statValue - minimumStatValue;
                if ( amount < 0 ) amount = 0;
            } else if ( limit == BASE_VALUE ) {
                int baseStat = c.getAdjustedStat();
                if ( statValue - amount < baseStat ) amount = statValue - baseStat;
                if ( amount < 0 ) amount = 0;
            } else {
                // The limit passed in is an actual value.  Limit damage by that value.
                if ( statValue - amount < limit ) amount = statValue - limit;
                if ( amount < 0 ) amount = 0;
            }
            
            statValue -= amount; // Apply the actual damage.
            
            // Set the stats value in the target.
            setCurrentStat(stat, statValue);
            
        } else {
            // Can't have negative damage...
            amount = 0;
            
            if ( DEBUG == 1 ) System.out.println("Attempt made to apply negative damage amount to target.");
        }
        
        ccu.setFinalValues();
        
        // Set the actual amount which was applied.
        ccu.setAmount(amount);
        
//        targetAdjusted();
        
        return ccu;
    }
    
    /** Sets the current value of a stat.
     *
     * increaseStat() will apply an increase adjustment to a stat.
     *
     * This method should be used to apply all damage to a stat.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
    public CharacteristicChangeUndoable applySetToCurrentStat(String stat, double amount) {
        CharacteristicPrimary c = getCharacteristic(stat);
        
        CharacteristicChangeUndoable ccu = new CharacteristicChangeUndoable(this, c);
        
        int statValue = c.getCurrentStat();
        double change = amount - statValue;
        
        // Set the stats value in the target.
        setCurrentStat(stat, (int)amount);
        
        ccu.setFinalValues();
        
        // Set the actual amount which was applied.
        ccu.setAmount(change);
        
//        targetAdjusted();
        
        return ccu;
    }
    
    /** Sets the adjustedBase value of a stat.
     *
     * increaseStat() will apply an increase adjustment to a stat.
     *
     * This method should be used to apply all damage to a stat.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
    public CharacteristicChangeUndoable applySetToAdjustedStat(String stat, double amount, boolean affectsFigured) {     
        CharacteristicPrimary c = getCharacteristic(stat);
        
        CharacteristicChangeUndoable ccu = new CharacteristicChangeUndoable(this, c);
        
        int statValue = c.getAdjustedStat();
        
        double change = amount - statValue;
        
        // Set the stats value in the target.
        setAdjustedStat(stat, (int)amount, affectsFigured);
        
        ccu.setFinalValues();
        
        // Set the actual amount which was applied.
        ccu.setAmount(change);
        
//        targetAdjusted();
        
        return ccu;
    }
    
    /** Sets the base value of a stat.
     *
     * increaseStat() will apply an increase adjustment to a stat.
     *
     * This method should be used to apply all damage to a stat.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
    public CharacteristicChangeUndoable applySetToBaseStat(String stat, double amount) {     
        CharacteristicPrimary c = getCharacteristic(stat);
        
        CharacteristicChangeUndoable ccu = new CharacteristicChangeUndoable(this, c);
        
        int statValue = c.getBaseStat();
        
        double change = amount - statValue;
        
        // Set the stats value in the target.
        setBaseStat(stat, (int)amount);
        
        
        ccu.setFinalValues();
        
        // Set the actual amount which was applied.
        ccu.setAmount(change);
        
//        targetAdjusted();
        
        return ccu;
    }
    
    /** Sets the adjustedBase value of a stat.
     *
     * increaseStat() will apply an increase adjustment to a stat.
     *
     * This method should be used to apply all damage to a stat.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
    public CharacteristicChangeUndoable applyAidToAdjustedStatCP(String stat, double amount, boolean affectsFigured) {      
        CharacteristicPrimary c = getCharacteristic(stat);
        
        CharacteristicChangeUndoable ccu = new CharacteristicChangeUndoable(this, c);
        
        double totalCP = c.getAdjustedStatCP();
        
        setAdjustedStatCP(stat, totalCP + amount, affectsFigured);
        
        ccu.setFinalValues();
        
        ccu.setAmount(amount);
        
//        targetAdjusted();
        return ccu;
    }
    
    /** Sets the adjustedBase value of a stat.
     *
     * increaseStat() will apply an increase adjustment to a stat.
     *
     * This method should be used to apply all damage to a stat.
     *
     * Returns StatChangeUndoable which can be used to undo the adjustment.
     */
    public CharacteristicChangeUndoable applyDrainToAdjustedStatCP(String stat, double amount, boolean affectsFigured) {
        CharacteristicPrimary c = getCharacteristic(stat);
        
        CharacteristicChangeUndoable ccu = new CharacteristicChangeUndoable(this, c);
        
        double totalCP = c.getAdjustedStatCP();

        setAdjustedStatCP(stat, totalCP - amount, affectsFigured);
        
        ccu.setFinalValues();
        
        ccu.setAmount(amount);
        
//        targetAdjusted();
        return ccu;
    }
    
    
    
    public int getMinimumStat(String stat) {
        return Integer.MIN_VALUE;
    }
    
    public int getMinimumStatCP(String stat) {
        return Integer.MIN_VALUE;
    }
    
    public int getMaximumStat(String stat) {
        return Integer.MAX_VALUE;
    }
    
    public int getMaximumStatCP(String stat) {
        return Integer.MAX_VALUE;
    }
    
    /* Creates a Fader for a Stat which has be adjusted.
     * TargetFades are created to track the fading of stats/abilities which have
     * been adjusted.  The TargetFade record will be checked every segment to determine
     * if fading should occur.
     */
    public void createTargetFade(BattleEvent be, Object adjustable, Target source, Ability sourceAbility, String adjustmentType, double amount, Chronometer startTime, long interval, int rate) throws BattleEventException {
        Undoable undoable;
        
        FadeTracker ft = getFadeTracker(adjustable);
        
        if ( ft == null ) {
            ft = new FadeTracker(adjustable, this);
            createIndexed("FadeTracker", "FADETRACKER", ft);
            
            be.addUndoableEvent( new AddFadeTrackerUndoable(this, ft) );
            
            ft.addFadeInfo(be, source, sourceAbility, adjustmentType, amount, startTime, interval, rate);
        } else {
            ft.addFadeInfo(be, source, sourceAbility, adjustmentType, amount, startTime, interval, rate);
        }
        
        effectAdjusted ea = effectAdjusted.getAdjustedEffect(be, this, adjustable, ft);
        ea.updateName();
    }
    
    public FadeTracker getFadeTracker(Object adjustable) {
        FadeTracker ft = null;
        
        int index = getIndexedSize( "FadeTracker" ) - 1;
        
        if ( adjustable instanceof Ability ) {
            for(; index >= 0; index -- ) {
                ft = (FadeTracker)getIndexedValue(index, "FadeTracker", "FADETRACKER");
                if ( ft.getAdjustable() == adjustable  ) {
                    return ft;
                }
            }
        } else {
            for(; index >= 0; index -- ) {
                ft = (FadeTracker)getIndexedValue(index, "FadeTracker", "FADETRACKER");
                if ( ft.getAdjustable().equals( adjustable  ) ) {
                    return ft;
                }
            }
        }
        
        return null;
    }
    
    /** Return a array of all the FadeTrackers that have entries related to Ability.
     *
     */
    public FadeTracker[] getFadeTrackers(Ability ability) {
        
        int count = 0;
        int ftcount = getIndexedSize( "FadeTracker" );
        
        for(int index = 0; index < ftcount; index++) {
            FadeTracker ft = (FadeTracker)getIndexedValue(index, "FadeTracker", "FADETRACKER");
            if ( ft.getSourceAbilityIndex(ability) != -1 ) {
                count++;
            }
        }
        
        FadeTracker[] fts = new FadeTracker[count];
        int ftindex = 0;
        for(int index = 0; index < ftcount; index++) {
            FadeTracker ft = (FadeTracker)getIndexedValue(index, "FadeTracker", "FADETRACKER");
            if ( ft.getSourceAbilityIndex(ability) != -1 ) {
                fts[ftindex++] = ft;
            }
        }
        
        return fts;
    }
    
    /** Stops all faders from fading.
     * 
     *  This stops all fading of adjustment on the target.  This is generally done
     *  to when the target dies.
     *
     *  Undoables are posted for all of the changes, so none need to be posted by the caller.
     */
    public void stopAdjustmentFading(BattleEvent be) {
        int ftcount = getIndexedSize( "FadeTracker" );
        
        for(int index = 0; index < ftcount; index++) {
            FadeTracker ft = (FadeTracker)getIndexedValue(index, "FadeTracker", "FADETRACKER");
            Undoable u = ft.stopFading();
            if ( be != null ) be.addUndoableEvent(u);
        }
        
    }
    
    /** Starts all faders fading (if the have been stopped).
     *
     */
    public void startAdjustmentFading(BattleEvent be) {
        int ftcount = getIndexedSize( "FadeTracker" );
        
        for(int index = 0; index < ftcount; index++) {
            FadeTracker ft = (FadeTracker)getIndexedValue(index, "FadeTracker", "FADETRACKER");
            Undoable u = ft.startFading();
            if ( be != null ) be.addUndoableEvent(u);
        }
    }
    
    /* Returns the amount of aid which is currently applied to a specific stat.
     * Used to determine if more aid can be applied to a stat.
     */
    /*public double getAidAdjustment(String stat) {
        int index,count;
        String adjustmenttype, targetstat;
        Double amount;
     
        double total = 0;
     
        count = getIndexedSize("FadeTimer");
     
        for(index = 0;index < count; index++) {
            adjustmenttype = getIndexedStringValue(index,"FadeTimer","ADJUSTMENTTYPE");
            if ( adjustmenttype != null && adjustmenttype.equals("AID") ) {
                targetstat = getIndexedStringValue(index, "FadeTimer", "TARGETSTAT");
                if ( targetstat != null & targetstat .equals(stat) ) {
                    amount = getIndexedDoubleValue(index, "FadeTimer", "AMOUNT");
                    if ( amount != null ) total += amount.doubleValue();
                }
            }
        }
        return total;
    } */
    
        /* Returns the amount of healing which is currently applied to a specific stat.
         * Used to determine if more aid can be applied to a stat.
         */
    public double getHealAdjustment(Characteristic adjustable) {
        double total = 0;
        
        FadeTracker ft = getFadeTracker(adjustable);
        if ( ft != null ) total = ft.getHealAdjustment();
        
        return total;
    }
   
    /** This method should be called whenever the target has been adjusted by adjustment powers.
     *
     * This stub method is used to trigger a property change event when the target
     * or any of it's abilities are adjusted.
     */
    public void targetAdjusted() {
        firePropertyChange(this, "Target.ADJUSTED", new Integer(-1), new Integer(1));
    }
    
    /* Creates a Fader for an adjustment ability which has faded something else.
     * SourceFades are created to track the amount of character points which
     * an adjustment ability had faded already.  SourceFades are only necessary for
     * aid, since drain adjustment powers do not have a maximum.
     */
 /*   public SourceFadeUndoable createSourceFadeForAbility() {
        return null;
    } */
    
    /* Process Segment Advance.
     * This is executed for every target every single segment advance.  This method
     * will be execute whether
     */
    public void processSegmentAdvance(BattleEvent be, Chronometer time) throws BattleEventException {
        processFades(be, time);
        
        if ( isSequenceOnEgo() && isHoldingForDex() ) {
            setHoldingForDex(false);
        }
    }
    
    /* Process all Fade times attached to the target.
     */
    private void processFades(BattleEvent be, Chronometer time) throws BattleEventException {
        int index, count;
        count = getIndexedSize("FadeTimer");
        Chronometer nextTime;
        String adjustmentType, targetType, targetStat;
        Integer decayInterval, decayRate;
        Ability targetAbility;
        Double amount;
        
        for (index = 0; index < count; index++ ) {
            nextTime = (Chronometer)getIndexedValue( index, "FadeTimer", "NEXTTIME");
            if ( nextTime.equals(time) ) {
                if ( DEBUG == 1 ) System.out.println("Processing FadeTimer " + Integer.toString(index) + " for " + this + ".");
                adjustmentType = getIndexedStringValue(index, "FadeTimer", "ADJUSTMENTTYPE");
                targetType = getIndexedStringValue(index, "FadeTimer", "TARGETTYPE");
                decayInterval = getIndexedIntegerValue(index, "FadeTimer", "DECAYINTERVAL");
                decayRate = getIndexedIntegerValue(index, "FadeTimer", "DECAYRATE");
                amount = getIndexedDoubleValue(index, "FadeTimer", "AMOUNT");
                
                if ( targetType.equals( "STATCP" ) ) {
                    targetStat = getIndexedStringValue(index, "FadeTimer", "TARGETSTAT");
                    if ( DEBUG == 1 ) System.out.println("Stat: " + targetStat);
                    if ( DEBUG == 1 ) System.out.println("AdjustmentType: " + adjustmentType);
                    if ( DEBUG == 1 ) System.out.println("Decay Interval: " + decayInterval);
                    if ( DEBUG == 1 ) System.out.println("Decay Rate: " + decayRate);
                    
                    // Update the next time.
                    nextTime.setTime( nextTime.getTime() + decayInterval.intValue() );
                } else if ( targetType.equals( "ABILITY" ) ) {
                    
                } else {
                    if ( DEBUG == 1 ) System.out.println("Unknown Target type: " + targetType );
                }
            }
        }
    }
    
    /** This can be used to add senses or sense groups to a target.
     *
     * The sense groups should always be added first (typically during character
     * construction) and then senses after that.  This is only for the base senses
     * and not sense modifiers.
     */
    public void addSense(Sense sense) {
        int index = findIndexed("Sense", "NAME", sense.getSenseName());
        if ( index == -1 ) {
            index = createIndexed("Sense", "NAME", sense.getSenseName(), false);
            addIndexed(index, "Sense", "SENSE", sense, false);
        } else {
            addIndexed(index, "Sense", "NAME", sense.getSenseName(), false);
            addIndexed(index, "Sense", "SENSE", sense, false);
        }
        
        // Setup the parent properly
        if ( sense.isSense() && sense.getSenseGroupName() != null ) {
            SenseGroup sg = (SenseGroup)getSense( sense.getSenseGroupName() );
            if ( sg == null ) {
                sg = (SenseGroup)PADRoster.getNewSense(sense.getSenseGroupName());
                if ( sg != null ) addSense( sg );
            }
            sense.setSenseGroup( sg );
        }
        
        // The list of both bonus and penalties should be applied...
        int count = getIndexedSize("SenseBonus");
        for(int sindex = 0; sindex < count; sindex++) {
            SenseBonusModifier sb = (SenseBonusModifier)getIndexedValue(sindex, "SenseBonus", "SENSEBONUSMODIFIER" );
            String senseAffected = sb.getSenseAffected();
            if ( ((senseAffected == null || senseAffected.equals("All Senses")) && sense.isSenseGroup())
            || sense.getSenseName().equals(senseAffected)) {
                sense.addSenseBonus(sb);
            }
        }
        
        count = getIndexedSize("SensePenalty");
        for(int sindex = 0; sindex < count; sindex++) {
            SensePenaltyModifier sb = (SensePenaltyModifier)getIndexedValue(sindex, "SensePenalty", "SENSEPENALTYMODIFIER" );
            String senseAffected = sb.getSenseAffected();
            if ( ((senseAffected == null || senseAffected.equals("All Senses")) && sense.isSenseGroup())
            || sense.getSenseName().equals(senseAffected)) {
                sense.addSensePenalty(sb);
            }
        }
        
        firePropertyChange(this, "SENSES", null, sense.getSenseName());
    }
    
    /** This can be used to remove a sense.
     *
     */
    public void removeSense(Sense sense) {
        int index = findIndexed("Sense", "NAME", sense.getSenseName());
        if ( index != -1 ) {
            removeAllIndexed(index, "Sense");
            firePropertyChange(this, "SENSES", null, sense.getSenseName());
        }
    }
    
    /** Returns a sense object with the indicated name.
     *
     * This will work for both senses and sense groups.
     */
    public Sense getSense(String senseName) {
        int index = findIndexed("Sense", "NAME", senseName);
        if ( index != -1 ) {
            return (Sense)getIndexedValue(index, "Sense", "SENSE");
        }
        return null;
    }
    
    /** Adds a Sense Bonus to a sense.
     *
     */
    public Undoable addSenseBonus(SenseBonusModifier modifier){
        return addSenseBonus(modifier,true);
    }
    
    /** Adds a Sense Bonus to a sense.
     *
     */
    public Undoable addSenseBonus(SenseBonusModifier modifier, boolean generateUndoable){
        int index = findIndexed("SenseBonus", "SENSEBONUSMODIFIER", modifier);
        
        if ( index == -1 ) {
            index = createIndexed("SenseBonus", "SENSEBONUSMODIFIER", modifier);
            
            String senseAffected = modifier.getSenseAffected();
            if ( senseAffected == null || senseAffected == "All Senses") {
                // This applies to all group...
                int count = getIndexedSize("Sense");
                for(int sindex = 0; sindex < count; sindex++) {
                    Sense s = (Sense)getIndexedValue(sindex, "Sense", "SENSE" );
                    if ( s.isSenseGroup() ) {
                        s.addSenseBonus(modifier);
                    }
                }
            } else {
                Sense s = getSense(senseAffected);
                if ( s != null ) {
                    s.addSenseBonus(modifier);
                }
            }
        }
        firePropertyChange(this, "SENSES", null, modifier);
        
        if ( generateUndoable ) return new addSenseBonusUndoable(this, modifier);
        return null;
    }
    
    /** Removes a Sense Bonus from a sense.
     *
     */
    public Undoable removeSenseBonus(SenseBonusModifier modifier){
        return removeSenseBonus(modifier, true);
    }
    
    /** Removes a Sense Bonus from a sense.
     *
     */
    public Undoable removeSenseBonus(SenseBonusModifier modifier, boolean generateUndoable){
        int index = findIndexed("SenseBonus", "SENSEBONUSMODIFIER", modifier);
        
        if ( index != -1 ) {
            removeAllIndexed(index, "SenseBonus");
            
            String senseAffected = modifier.getSenseAffected();
            if ( senseAffected == null || senseAffected == "All Senses") {
                // This applies to all group...
                int count = getIndexedSize("Sense");
                for(int sindex = 0; sindex < count; sindex++) {
                    Sense s = (Sense)getIndexedValue(sindex, "Sense", "SENSE" );
                    if ( s.isSenseGroup() ) {
                        s.removeSenseBonus(modifier);
                    }
                }
            } else {
                Sense s = getSense(senseAffected);
                if ( s != null ) {
                    s.removeSenseBonus(modifier);
                }
            }
        }
        firePropertyChange(this, "SENSES", null, modifier);
        
        if ( generateUndoable ) return new removeSenseBonusUndoable(this, modifier);
        return null;
    }
    
    /** Adds a Sense Penalty to a sense.
     *
     */
    public Undoable addSensePenalty(SensePenaltyModifier modifier){
        return addSensePenalty(modifier, true);
    }
    
    /** Adds a Sense Penalty to a sense.
     *
     */
    public Undoable addSensePenalty(SensePenaltyModifier modifier, boolean generateUndoable){
        int index = findIndexed("SensePenalty", "SENSEPENALTYMODIFIER", modifier);
        
        if ( index == -1 ) {
            index = createIndexed("SensePenalty", "SENSEPENALTYMODIFIER", modifier);
            
            String senseAffected = modifier.getSenseAffected();
            if ( senseAffected == null || senseAffected == "All Senses") {
                // This applies to all group...
                int count = getIndexedSize("Sense");
                for(int sindex = 0; sindex < count; sindex++) {
                    Sense s = (Sense)getIndexedValue(sindex, "Sense", "SENSE" );
                    if ( s.isSenseGroup() ) {
                        s.addSensePenalty(modifier);
                    }
                }
            } else {
                Sense s = getSense(senseAffected);
                if ( s != null ) {
                    s.addSensePenalty(modifier);
                }
            }
        }
        firePropertyChange(this, "SENSES", null, modifier);
        
        if ( generateUndoable ) return new addSensePenaltyUndoable(this, modifier);
        return null;
    }
    
    /** Removes a Sense Penalty from a sense.
     *
     */
    public Undoable removeSensePenalty(SensePenaltyModifier modifier){
        return removeSensePenalty(modifier, true);
    }
    
    /** Removes a Sense Penalty from a sense.
     *
     */
    public Undoable removeSensePenalty(SensePenaltyModifier modifier, boolean generateUndoable){
        int index = findIndexed("SensePenalty", "SENSEPENALTYMODIFIER", modifier);
        
        if ( index != -1 ) {
            removeAllIndexed(index, "SensePenalty");
            
            String senseAffected = modifier.getSenseAffected();
            if ( senseAffected == null || senseAffected == "All Senses") {
                // This applies to all group...
                int count = getIndexedSize("Sense");
                for(int sindex = 0; sindex < count; sindex++) {
                    Sense s = (Sense)getIndexedValue(sindex, "Sense", "SENSE" );
                    if ( s.isSenseGroup() ) {
                        s.removeSensePenalty(modifier);
                    }
                }
            } else {
                Sense s = getSense(senseAffected);
                if ( s != null ) {
                    s.removeSensePenalty(modifier);
                }
            }
        }
        firePropertyChange(this, "SENSES", null, modifier);
        
        if ( generateUndoable ) return new removeSensePenaltyUndoable(this, modifier);
        return null;
    }
    
    /** Returns an iterator over all the senses the target has that meet capabilities.
     *
     * This will return an iterator that fulfills all of the capabilities indicated
     * by the filter passed in.
     */
    public Iterator<Sense> getSenses(SenseFilter senseFilter) {
        return new SenseIterator(this, senseFilter);
    }
    
    /** Displays a sense table for the target.
     *
     */
    public void displaySenseWindow() {
        String windowName;
        windowName = getName() + "'s Senses";
        
        JFrame f = new JFrame(windowName);
        STSensePanel sp = new STSensePanel(this);
        
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(sp);
        f.pack();
        
        ChampionsUtilities.centerWindow(f);
        f.setVisible(true);
    }
    
    /** Returns the best sense to use against indicated target.
     *
     * This method will look through all functioning senses and return the
     * one that has the best perception roll against the indicated target.
     * This is a best effort search and does not take into account some
     * factors, such as the distance to target (which may affect things like
     * the perception roll to see through invisibility).
     *
     * This method only considers functioning (non-flashed, non-darknessed)
     * senses that are ranged.  Targeting senses will always be preferred over
     * non-targetting senses.
     *
     * @return Sense with be perception roll.  Null if no senses qualify.
     */
    public Sense getBestSense(Target target) {
        Sense bestTargettingSense = null;
        int bestTargettingMod = Integer.MIN_VALUE;
        
        Sense bestNonTargettingSense = null;
        int bestNonTargettingMod = Integer.MIN_VALUE;
        
        Iterator it=null;
        
        it = getSenses( new SensesOnlySenseFilter() );
        //jeff change
        while ( it.hasNext() ) {
        	boolean invisibleToSense= false;
            Sense s = (Sense)it.next();
            if(target!=this && target.getAbility("Invisibility")!=null) {
            	Ability inv = target.getAbility("Invisibility");
            	powerInvisibility pInv =  (powerInvisibility) inv.getPower();
            	ParameterList p = inv.getPowerParameterList();
            	ListParameter l = (ListParameter) p.getParameter("Senses");
            	ArrayList senses = (ArrayList) p.getParameterValue("Senses");
            	 
            	for (Object o: senses) {
            		SenseGroup group = (SenseGroup)o;
            		if(group.getSenseName().equals(s.getSenseGroupName()))
            		{
            			invisibleToSense= true;
            		}
				}
            }
            if ( s.isFunctioning() && s.isRangedSense() && invisibleToSense==false) {
                if ( s.isTargettingSense() && s.getEnhancedPerceptionLevel() > bestTargettingMod) {
                    bestTargettingMod = s.getEnhancedPerceptionLevel();
                    bestTargettingSense = s;
                } else if ( ! s.isTargettingSense() && s.getEnhancedPerceptionLevel() > bestNonTargettingMod) {
                    bestNonTargettingMod = s.getEnhancedPerceptionLevel();
                    bestNonTargettingSense = s;
                }
            }
        }
        
        if ( bestTargettingSense != null ) return bestTargettingSense;
        return bestNonTargettingSense;
    }
    
    /** Returns a SenseList with all available sense.  
     *
     *  The sense list will be ordered according to their desirability
     *  for attacker or defense.  Functioning senses will always be places 
     *  for non-functioning senses, targeting before non, and ranged before non.
     */
    public List<Sense> getOrderedSenseList(final Target againstTarget) {
        List<Sense> unorderedList = new ArrayList<Sense>();
        List<Sense> orderedList = new ArrayList<Sense>();
        
        Iterator<Sense> it;
        
        it = getSenses( new SensesOnlySenseFilter() );
        while (it.hasNext()) {
            Sense s = (Sense) it.next();
            unorderedList.add(s);
        }
        
        Sense[] senseArray = new Sense[unorderedList.size()];
        unorderedList.toArray(senseArray);
        
        Arrays.sort(senseArray, new Comparator<Sense>(){
            public int compare(Sense s1, Sense s2) {
                
                boolean b1, b2;
                
                // isTargetableWithSense automatically includes
                // checks for isFunctioning, isTargetting, and isRanged
                b1 = s1.isTargetableWithSense(againstTarget);
                b2 = s2.isTargetableWithSense(againstTarget);
                
                if ( b1 && ! b2 ) return -1;
                if ( !b1 && b2 ) return +1;
                
                b1 = s1.isFunctioning();
                b2 = s2.isFunctioning();
                
                if ( b1 && ! b2 ) return -1;
                if ( !b1 && b2 ) return +1;
                
                b1 = s1.isTargettingSense();
                b2 = s2.isTargettingSense();
                
                if ( b1 && ! b2 ) return -1;
                if ( !b1 && b2 ) return +1;
                
                b1 = s1.isRangedSense();
                b2 = s2.isRangedSense();
                
                if ( b1 && ! b2 ) return -1;
                if ( !b1 && b2 ) return +1;
                
                int per1 = s1.getEnhancedPerceptionLevel();
                int per2 = s2.getEnhancedPerceptionLevel();
                    
                return per2 - per1;
            }
            
            public boolean equals(Object that) {
                return false;
            }
            
        });
        
        for(Sense s : senseArray) {
            orderedList.add(s);
        }
        
        return orderedList;
    }
    
    /** Returns the perception roll necessary for the indicated sense and target.
     *
     * If the target is null, a standard perception roll for the indicated
     * sense will be used.  If the target is non-null, the effects applied to
     * the target will modify the perception roll.
     */
    public int getPerceptionRoll(Sense sense, Target target) {
        int base = getPerceptionRoll();
        
        base += sense.getEnhancedPerceptionLevel();
        
        return base;
    }
    
    /** Returns the base, unmodified, perception roll for this target.
     */
    public int getPerceptionRoll() {
        int base = 0;
        if ( hasStat("INT") ) {
            base = (int)Math.round( getCurrentStat("INT") / 5.0 + 9);
        }
        
        return base;
    }
    
    /** Returns the Base Weight of the target in kg.
     *
     */
    public double getBaseWeight() {
        Double d = getDoubleValue("Target.BASEWEIGHT");
        return (d == null ? 100 : d.doubleValue());
    }
    
    /** Sets the Base Weight of the target in kg.
     *
     */
    public void setBaseWeight(double weight) {
        add("Target.BASEWEIGHT", new Double(weight), true, false);
    }
    
    /** Returns the current weight of the target modified by abilities.
     *
     */
    public double getCurrentWeight() {
        double base = getBaseWeight();
        int count = getIndexedSize("WeightMultiplier");
        for(int i = 0; i < count; i++) {
            Double d = getIndexedDoubleValue(i, "WeightMultiplier", "MULTIPLIER");
            base *= d.doubleValue();
        }
        return base;
    }
    
    /** Adds a weight multiplier to target.
     *
     */
    public void addWeightMultiplier(String name, double multiplier) {
        int index = findIndexed("WeightMultiplier", "NAME", name);
        if ( index == -1 ) {
            index = createIndexed("WeightMultiplier", "NAME", name, false);
            addIndexed(index, "WeightMultiplier", "MULTIPLIER", new Double(multiplier), true, false);
        }
    }
    
    /** Removes a weight multiplier from target.
     *
     */
    public void removeWeightMultiplier(String name) {
        int index = findIndexed("WeightMultiplier", "NAME", name);
        if ( index != -1 ) {
            removeAllIndexed(index, "WeightMultiplier");
        }
    }
    
    /** Returns the Base Height of the target in meters.
     *
     */
    public double getBaseHeight() {
        Double d = getDoubleValue("Target.BASEHEIGHT");
        return (d == null ? 2 : d.doubleValue());
    }
    
    /** Sets the Base Height of the target in meters.
     *
     */
    public void setBaseHeight(double height) {
        add("Target.BASEHEIGHT", new Double(height), true, false);
    }
    
    /** Returns the current height of the target modified by abilities.
     *
     */
    public double getCurrentHeight() {
        double base = getBaseHeight();
        int count = getIndexedSize("HeightMultiplier");
        for(int i = 0; i < count; i++) {
            Double d = getIndexedDoubleValue(i, "HeightMultiplier", "MULTIPLIER");
            base *= d.doubleValue();
        }
        return base;
    }
    
    /** Adds a height multiplier to target.
     *
     */
    public void addHeightMultiplier(String name, double multiplier) {
        int index = findIndexed("HeightMultiplier", "NAME", name);
        if ( index == -1 ) {
            index = createIndexed("HeightMultiplier", "NAME", name, false);
            addIndexed(index, "HeightMultiplier", "MULTIPLIER", new Double(multiplier), true, false);
        }
    }
    
    /** Removes a height multiplier from target.
     *
     */
    public void removeHeightMultiplier(String name) {
        int index = findIndexed("HeightMultiplier", "NAME", name);
        if ( index != -1 ) {
            removeAllIndexed(index, "HeightMultiplier");
        }
    }
    
    public void addClassOfMind(String classofmind ) {
        int index;
        if ( ( index = findIndexed("ClassOfMind", "NAME", classofmind) ) == -1 ) {
            index = createIndexed("ClassOfMind", "NAME", classofmind);
        }
    }
    
    //    public void flashSense(Effect flashEffect, String sense) {
    //        int index, sindex;
    //        int count;
    //        boolean exists = false;
    //
    //        if ( ( sindex = findIndexed( "Sense", "NAME", sense) ) != -1 ) {
    //            index = 0;
    //            while ( ( index = findIndexed(index, "FlashedSense", "EFFECT", flashEffect) ) != -1 ) {
    //                if ( getIndexedStringValue(index, "FlashedSense", "SENSE").equals( sense ) ) {
    //                    // already registered, so set flag and brake out
    //                    exists = true;
    //                    break;
    //                }
    //            }
    //
    //            if ( ! exists ) {
    //                // The effect/sense pair doesn't exist.  Create it.
    //                index = createIndexed( "FlashedSense", "EFFECT", flashEffect);
    //                addIndexed(index, "FlashedSense", "SENSE", sense, true);
    //
    //                // Mark the sense as flashed in the sense list
    //                addIndexed(sindex, "Sense", "FLASHED", "TRUE", true);
    //            }
    //        }
    //    }
    
    //    public void unflashSense(Effect flashEffect, String sense) {
    //        int index, sindex;
    //        int count;
    //        boolean exists = false;
    //
    //        if ( ( sindex = findIndexed( "Sense", "NAME", sense) ) != -1 ) {
    //            index = 0;
    //            while ( ( index = findIndexed(index, "FlashedSense", "EFFECT", flashEffect) ) != -1 ) {
    //                if ( getIndexedStringValue(index, "FlashedSense", "SENSE").equals( sense ) ) {
    //                    // already registered, so set flag and brake out
    //                    exists = true;
    //                    break;
    //                }
    //            }
    //
    //            if ( exists ) {
    //                // The effect/sense pair doest exist.  Remove it.
    //                removeAllIndexed(index, "FlashedSense");
    //
    //                // Check if there still is a flash effect for this particular sense
    //                if ( ( index = findIndexed( "FlashedSense", "SENSE" , sense) ) == -1 ) {
    //                    // There is no remain flashed effect for this sense.  Set the sense as unflashed
    //                    addIndexed(sindex, "Sense", "FLASHED", "FALSE", true);
    //                }
    //            }
    //        }
    //    }
    
    //    public void flashSenseGroup(Effect flashEffect, String senseGroup) {
    //        int sgindex, sindex;
    //        String sense;
    //        if ( ( sgindex = findIndexed("SenseGroup", "NAME", senseGroup) ) != -1 ) {
    //            sindex = 0;
    //            while ( (sindex = findIndexed(sindex, "Sense", "SENSEGROUP", senseGroup)) != -1 ) {
    //                // Find all the senses that belong to this sense group and flash them with this effect
    //                sense = getIndexedStringValue(sindex, "Sense", "NAME");
    //                flashSense(flashEffect, sense);
    //            }
    //        }
    //    }
    
    //    public void unflashSenseGroup(Effect flashEffect, String senseGroup) {
    //        int sgindex, sindex;
    //        String sense;
    //        if ( ( sgindex = findIndexed("SenseGroup", "NAME", senseGroup) ) != -1 ) {
    //            sindex = 0;
    //            while ( (sindex = findIndexed(sindex, "Sense", "SENSEGROUP", senseGroup)) != -1 ) {
    //                // Find all the senses that belong to this sense group and flash them with this effect
    //                sense = getIndexedStringValue(sindex, "Sense", "NAME");
    //                unflashSense(flashEffect, sense);
    //            }
    //        }
    //    }
    
    public boolean hasTargetingSense() {
        int index = 0;
        boolean foundSense = false;
        
        while ( ( index = findIndexed(index, "Sense", "TARGETING", "TRUE")) != -1 ) {
            if ( getIndexedBooleanValue(index, "Sense", "FLASHED") == false ) {
                foundSense = true;
                break;
            }
        }
        
        return foundSense;
    }
    
    public boolean isAlive() {
        return getBooleanValue("Target.ISALIVE");
    }
    
    public boolean canBeKnockedback() {
        return getBooleanValue("Target.CANBEKNOCKEDBACK");
    }
    
    public void setCanBeKnockedback(boolean canBeKnockedBack) {
        add("Target.CANBEKNOCKEDBACK", canBeKnockedBack ? "TRUE" : "FALSE", true);
    }
    
    public void clearBattleStats() {
        removeAll("BattleStat");
        createIndexed("BattleStat","TARGETNAME","BATTLETOTALS");
        addIndexed(0, "BattleStat", "HITS", new Integer(0), true);
        addIndexed(0, "BattleStat", "MISSES", new Integer(0), true);
        addIndexed(0, "BattleStat", "BODY", new Integer(0), true);
        addIndexed(0, "BattleStat", "STUN", new Integer(0), true);
    }
    
    public void addRecentTarget(Target target) {
        int tindex;
        
        tindex = findIndexed("RecentTarget", "TARGET", target);
        if ( tindex != -1 && tindex != 0 ) {
            // It is in the list, but not first, so remove it.
            removeAllIndexed(tindex,"RecentTarget");
        }
        
        if ( tindex != 0 ) {
            // It either wasn't in the list, or it wasn't first and got removed.
            createIndexed(0, "RecentTarget", "TARGET", target);
        }
        
        int count = getIndexedSize("RecentTarget");
        if ( count > getRecentTargetMaximum() ) {
            removeAllIndexed(count-1, "RecentTarget");
        }
    }
    
    public Target[] getRecentTargetsArray() {
        int tindex;
        int count = getIndexedSize("RecentTarget");
        
        Target[] targets = new Target[count];
        for(tindex = 0;tindex < count; tindex++) {
            targets[tindex] = (Target)getIndexedValue(tindex, "RecentTarget", "TARGET");
        }
        return targets;
    }
    
    public TargetList getRecentTargets() {
        int tindex;
        int count = getIndexedSize("RecentTarget");

        TargetList targets = new TargetList("Recent Targets");
        for(tindex = 0;tindex < count; tindex++) {
            targets.addTarget((Target)getIndexedValue(tindex, "RecentTarget", "TARGET"));
        }
        return targets;
    }
    
    public void clearRecentTargets() {
        removeAll("RecentTarget");
    }
    
    public int getRecentTargetMaximum() {
        // Integer i = getIntegerValue("RecentTarget.MAXIMUM");
        
        Integer i = (Integer)Preferences.getPreferenceList().getParameterValue("RecentTargets");
        return ( i == null ) ? 10 : i.intValue();
    }
    
    public ObstructionList getObstructionList() {
        return (ObstructionList)getValue("Target.OBSTRUCTIONLIST");
    }
    
    public void setObstructionList(ObstructionList ol) {
        add("Target.OBSTRUCTIONLIST", ol, true);
    }
    
    public AddObstructionUndoable addObstruction(Target obstruction, int position) {
        ObstructionList ol = getObstructionList();
        
        if ( ol == null ) {
            ol = new ObstructionList();
            setObstructionList(ol);
        }
        
        position = ol.addObstruction(obstruction, position);
        
        AddObstructionUndoable ou = new AddObstructionUndoable(ol, obstruction, position);
        return ou;
    }
    
    public AddObstructionUndoable addObstruction(Target obstruction) {
        return addObstruction(obstruction, Integer.MAX_VALUE);
    }
    
    public RemoveObstructionUndoable removeObstruction(Target obstruction) {
        ObstructionList ol = getObstructionList();
        RemoveObstructionUndoable ou = null;
        
        if ( ol != null ) {
            int oindex = ol.findObstruction(obstruction);
            if (oindex != -1 ) {
                ol.removeObstruction(oindex);
                
                ou = new RemoveObstructionUndoable(ol, obstruction, oindex);
            }
        }
        return ou;
    }
    
    
    
    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     *
     * The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)<p>
     *
     * The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.<p>
     *
     * Finally, the implementer must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.<p>
     *
     * It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * @param   o the Object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     * 		is less than, equal to, or greater than the specified object.
     *
     * @throws ClassCastException if the specified object's type prevents it
     *        from being compared to this Object.
     */
    public int compareTo(Object o) {
        return this.getName().compareTo(((Target)o).getName());
    }
    
    /** Called Prior to writing of the detailList.
     *
     * An cleanup that needs to be done prior to writing, such as removing
     * non-serializable objects from the detaillist, should be done here.
     */
    protected void preWrite() {
        // Clear the Recent Targets before saving.
        clearRecentTargets();
        
        if ( image != null ) {
            try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
                ImageIO.write(image, "jpeg", output);
            
            imageJPEGData = output.toByteArray();
            }
            catch (IOException ioe) {
                
            }
        }
    }
    
    /** Called After to reading of a detailList.
     *
     * An fix-up of the detailList after it has been read should be done
     * here.
     */
    protected void postRead() {
        // Tell all the abilities in our ability list that they belong to us...
        AbilityIterator ai = getAbilities();
        while ( ai.hasNext() ) {
            Ability baseAbility = ai.nextAbility();
            AbilityInstanceGroup aig = baseAbility.getInstanceGroup();
            Iterator<Ability> it = aig.getInstances();
            while (it.hasNext()) {
                Ability instance = it.next();
                instance.setSource(this);
            }
            
        }
        
        if ( imageJPEGData != null ) {
            ByteArrayInputStream input = new ByteArrayInputStream(imageJPEGData);
            try {
                image = ImageIO.read(input);
                updateImageIcon();
            }
            catch(IOException ioe) {
                
            }
            
            imageJPEGData = null;
        }
    }
    
    /** Returns an AbilityIterator which iterates through all the abilities in the list.
     *
     * This version of getAbilities should recursively iterator through all of the abilities
     * contained in this list.  Calling this method is equivalent to calling
     * getAbilities(true);
     */
    public AbilityIterator getAbilities() {
        return getAbilities( null );
    }
    
    /** Returns an AbilityIterator which iterates through the abilities in the sublist.
     *
     * This version of getAbilities will iterate through the abilities of the indicated,
     * sublist. If the recursive flag is true, the sublist does not necessarily have to
     * be a direct child of the list.
     */
    public AbilityIterator getAbilities(String sublistName) {
        // Just return null for now!
        AbilityList al;
        if ( sublistName == null ) {
            al = getAbilityList();
        } else {
            al =  getAbilityList().findSublist(sublistName);
        }
        
        if (al == null ) {
            return new NullAbilityIterator();
        } else {
            return al.getAbilities();
        }
    }
    
    /** Returns an AbilityIterator which iterates through all the skills in the list.
     *
     * This version of getSkills should recursively iterator through all of the skills
     * contained in this list.  Calling this method is equivalent to calling
     * getAbilities(true);
     */
    public AbilityIterator getSkills() {
        return getSkills( null );
    }
    
    /** Returns an AbilityIterator which iterates through the skills in the sublist.
     *
     * This version of getSkills will iterate through the skills of the indicated,
     * sublist. If the recursive flag is true, the sublist does not necessarily have to
     * be a direct child of the list.
     */
    public AbilityIterator getSkills(String sublistName) {
        // Just return null for now!
        AbilityList al;
        if ( sublistName == null ) {
            al = getAbilityList();
        } else {
            al =  getAbilityList().findSublist(sublistName);
        }
        
        if (al == null ) {
            return new NullAbilityIterator();
        } else {
            return al.getSkills();
        }
    }
    
    /** Returns the child sublist indicated by specified index.
     */
    public AbilityList getAbilityList() {
        return (AbilityList)getValue("AbilityList.ABILITYLIST");
    }
    
    private void setAbilityList(AbilityList al) {
        AbilityList oldAbilityList = getAbilityList();
        if ( oldAbilityList != al ) {
            if ( oldAbilityList != null ) {
                oldAbilityList.setSource(null);
            }
            
            add("AbilityList.ABILITYLIST", al, true);
            
            if ( al != null ) {
                al.setSource(this);
            }
        }
    }
    
    /** Adds the Ability to the Target.
     *
     * This method sets up the ability properly to be used by the target.  However,
     * it does not add the ability to the Target's abilityList.  This method should
     * not be called except by abilityList's which are doing abilityList maintenance.
     *
     * In general, if an arbitrary ability needs to be added to a target, the following
     * code should be used:
     * <code>
     * getTarget().getAbilityList().addAbility(ability);
     * </code>
     */
    
    public void attachAbilityToTarget( Ability ability ) {
        // Need to be a clone
        
        // Why does this need to be a clone?  Which abilities could be applied to
        // multiple targets?
        // With ActivationInfo, this probably has absolutely no reason to be a clone
        // ...Actually, ability should be unique.  This should not be an existing ability
        // which belong with to this or another target.
        Ability newability = ability; //(Ability)ability.clone();
        BattleEvent be;
        
        if ( newability.isAutoSource() == false ) {
            newability.setSource(this);
        }
        
        String name = getUniqueAbilityName(newability);
        newability.setName(name);
        
        // Maintain this list for now.  Eventually, this will go away...
        // this.createIndexed("Ability","ABILITY", newability);
        
        if ( Battle.currentBattle != null && ! Battle.currentBattle.isStopped() ) {
            be = new BattleEvent(ability, BattleEvent.ADD_ABILITY);
            be.addAbilityEvent(ability, this, false);
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Ability " + ability.getName() + " added to " + getName(), BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Ability " + ability.getName() + " added to " + getName(), BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Ability " + ability.getName() + " added to " + getName(), BattleEvent.MSG_NOTICE);
            Battle.currentBattle.addEvent(be);
            
            // If the ability is normally on, activate it.
            if ( Battle.currentBattle.getActiveTarget() == this && ability.isNormallyOn() && ability.isActivated(this) == false) {
                be = new BattleEvent(ability);
                Battle.currentBattle.addEvent(be);
            }
        }
    }
    
    /** Removes the Ability from the Target.
     *
     * This method sets up the ability properly to be used by the target.  However,
     * it does not remove the ability to the Target's abilityList.  This method should
     * not be called except by abilityList's which are doing abilityList maintenance.
     *
     * In general, if an arbitrary ability needs to be removed from a target, the following
     * code should be used:
     * <code>
     * getTarget().getAbilityList().removeAbility(ability);
     * </code>
     */
    public void detachAbilityFromTarget( Ability ability ) {
        if ( hasAbility(ability) ) {
            if ( Battle.currentBattle != null && ! Battle.currentBattle.isStopped() ) {
                if (  ability.isActivated(this) ) {
                    int aindex, count;
                    Ability currentInstance = ability.getInstanceGroup().getCurrentInstance();
                    ActivationInfo ai;
                    count = currentInstance.getActivationCount();
                    for(aindex=0;aindex<count;aindex++) {
                       // ai = (ActivationInfo)currentInstance.getIndexedValue(aindex,"ActivationInfo","ACTIVATIONINFO");
                        ai = currentInstance.getActivationInfo(aindex);
                        // The ability is activated, so make sure you post a deactivate.
                        BattleEvent be = new BattleEvent( BattleEvent.DEACTIVATE, ai);
                        Battle.currentBattle.addEvent( be );
                    }
                }
                BattleEvent be = new BattleEvent(ability, BattleEvent.REMOVE_ABILITY );
                be.addAbilityEvent(ability, this, false);
                be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Ability " + ability.getName() + " removed from " + getName(), BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Ability " + ability.getName() + " removed from " + getName(), BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Ability " + ability.getName() + " removed from " + getName(), BattleEvent.MSG_NOTICE );
                Battle.currentBattle.addEvent(be);
            }
            
            // The BattleEvent doesn't actually remove the ability, so do it here.
            if ( ability.getSource() == this ) ability.setSource(null);
        }
    }
    
    /* The AbilityList.addAbility should be used instead of this.
     
    public AbilityList addAbility(Ability ability) {
        return addAbility(ability, null);
    }
    
    public AbilityList addAbility(Ability ability, String sublist) {
        AbilityList al = findSublist(sublist);
        
        if ( al == null ) al = getAbilityList();
        
        al.addAbility(ability);
        
        return al;
    }*/
    
    public void removeAbility(Ability ability) {
        if ( ability.getSource() == this && ability.getAbilityList() != null ) {
            if ( ability.isActivated(this) ) {
                Iterator<ActivationInfo> it = ability.getActivations();
                if ( it != null ) {
                    for(;it.hasNext();) {
                        ActivationInfo activationInfo = it.next();
                        BattleEvent be = new BattleEvent(BattleEvent.DEACTIVATE, activationInfo);
                        Battle.currentBattle.addEvent(be);
                    }
                }
            }
            
            ability.getAbilityList().removeAbility(ability);
        }
    }
    
    /** Getter for property targetEditorLocation.
     * @return Value of property targetEditorLocation.
     */
    public Point getTargetEditorLocation() {
        return (Point)getValue("TargetEditor.LOCATION");
    }
    
    /** Setter for property targetEditorLocation.
     * @param targetEditorLocation New value of property targetEditorLocation.
     */
    public void setTargetEditorLocation(Point targetEditorSizeditorLocation) {
        add("TargetEditor.LOCATION", targetEditorSizeditorLocation, true, false);
    }
    
    /** Getter for property targetEditorSize.
     * @return Value of property targetEditorSize.
     */
    public Dimension getTargetEditorSize() {
        return (Dimension)getValue("TargetEditor.SIZE");
    }
    
    /** Setter for property targetEditorSize.
     * @param targetEditorSize New value of property targetEditorSize.
     */
    public void setTargetEditorSize(Dimension targetEditorSize) {
        add("TargetEditor.SIZE", targetEditorSize, true, false);
    }
    
    //    public void healCompletely() {
    //        // Don't use a battleEvent to make the changes.  Just apply them directly.
    //        if ( getCurrentStat("BODY") < getAdjustedStat("BODY") ) {
    //            setCurrentStat("BODY", getAdjustedStat("BODY"));
    //        }
    //        if ( getCurrentStat("STUN") <getAdjustedStat("STUN") ) {
    //            setCurrentStat("STUN", getAdjustedStat("STUN"));
    //        }
    //        if ( getCurrentStat("END") < getAdjustedStat("END") ) {
    //            setCurrentStat("END", getAdjustedStat("END"));
    //        }
    //
    //Effect e;
    //            if ( isDead() ) {
    //                e = getEffect("Dead");
    //                e.removeEffect(
    //                e.removeEffect();
    //            }
    //
    //            if ( hasEffect("Knocked Down") ) {
    //                e = getEffect("Knocked Down");
    //                e.removeEffect(battleEvent, this);
    //            }
    //            if ( hasEffect("Stunned") ) {
    //                e = getEffect("Stunned");
    //                e.removeEffect(battleEvent, this);
    //            }
    //        }
    //    }
    
    
    
    public void healCompletely(BattleEvent battleEvent) {
        Effect e = new Effect("Heal", "INSTANT");
        if ( hasStat("BODY") && getCurrentStat("BODY") < getAdjustedStat("BODY") ) {
            e.addHealSubeffect("Body", "STAT",  "BODY", (double)getAdjustedStat("BODY") - getCurrentStat("BODY"));
        }
        if ( hasStat("STUN") && getCurrentStat("STUN") <getAdjustedStat("STUN") ) {
            e.addHealSubeffect("Stun", "STAT", "STUN", (double)getAdjustedStat("STUN") - getCurrentStat("STUN"));
        }
        if ( hasStat("END") && getCurrentStat("END") < getAdjustedStat("END") ) {
            e.addHealSubeffect("End", "STAT", "END", (double)getAdjustedStat("END") - getCurrentStat("END"));
        }
        
        try {
            e.addEffect(battleEvent, this);
            
            if ( isDead() ) {
                e = getEffect(getDeadEffectName());
                e.removeEffect(battleEvent, this);
            }
            if ( isDying() ) {
                e = getEffect("Dying");
                e.removeEffect(battleEvent, this);
            }
            if ( hasEffect("Knocked Down") ) {
                e = getEffect("Knocked Down");
                e.removeEffect(battleEvent, this);
            }
            if ( hasEffect("Stunned") ) {
                e = getEffect("Stunned");
                e.removeEffect(battleEvent, this);
            }
            
        } catch ( BattleEventException bee) {
            ExceptionWizard.postException(bee);
        }
    }
    
    public void healNoBODY(BattleEvent battleEvent) {
        Effect e = new Effect("Heal", "INSTANT");
        if ( hasStat("STUN") && getCurrentStat("STUN") <getAdjustedStat("STUN") ) {
            e.addHealSubeffect("Stun", "STAT", "STUN", (double)getAdjustedStat("STUN") - getCurrentStat("STUN"));
        }
        if ( hasStat("END") && getCurrentStat("END") < getAdjustedStat("END") ) {
            e.addHealSubeffect("End", "STAT", "END", (double)getAdjustedStat("END") - getCurrentStat("END"));
        }
        
        try {
            e.addEffect(battleEvent, this);
            
          /*  if ( isDead() ) {
                e = getEffect("Dead");
                e.removeEffect(battleEvent, this);
            } */
            if ( isDying() ) {
                e = getEffect("Dying");
                e.removeEffect(battleEvent, this);
            }
            if ( hasEffect("Knocked Down") ) {
                e = getEffect("Knocked Down");
                e.removeEffect(battleEvent, this);
            }
            if ( hasEffect("Stunned") ) {
                e = getEffect("Stunned");
                e.removeEffect(battleEvent, this);
            }
        } catch ( BattleEventException bee) {
            ExceptionWizard.postException(bee);
        }
    }
    
    
    /** Returns an array list of the Target Options.
     *
     * The Target Options are the miscellaneous options that apply to only
     * certain types of targets.  Generally, you should call the super method
     * to get the base array list and then add your own TargetOptions objects
     * to it.
     *
     * Only instances of classes supporting the TargetOptions interface should
     * be added to the returned array list.
     */
    public ArrayList getTargetOptions() {
        // The default version of TargetOptions has no options included.
        return new ArrayList();
    }
    
    public Object clone() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();
            
            byte[] data = baos.toByteArray();
            
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object o = ois.readObject();
            return o;
            
        } catch ( Exception e) {
            ExceptionWizard.postException(e);
            
        }
        return null;
    }
    
    public String getENDString(int endCost) {
        if ( endCost == 0 ) return "";
        
        int end = getCurrentStat("END");
        return Integer.toString(endCost);
    }
    
    /** Returns the text to place in a tooltip when the mouse is held over the discription.
     *
     *  This can include html text if desired.
     */
    public String getENDTooltip(int endCost) {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<HTML>");
        sb.append("<B>").append(endCost).append("</B> end to use.<BR>");
        int end = getCurrentStat("END");
        sb.append("<B>").append(end).append("</B> end remaining.<BR>");
        sb.append("<P>");
        sb.append("This power uses the character as an end source directly.");
        sb.append("</HTML>");
        
        return sb.toString();
    }
    
    public boolean canBurnStun() {
        return true;
    }
    
    /* Special Debug Versions of addPropertyChangeListener */
  /*  public void addPropertyChangeListener(String property, java.beans.PropertyChangeListener l) {
        super.addPropertyChangeListener(property, l);
        System.out.println("Target.addPropertyChangeListener: adding PCL for prop \"" + property + "\", listener: " + l.getClass() + ".");
    }
   
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        super.addPropertyChangeListener(l);
        System.out.println("Target.addPropertyChangeListener: adding PCL for all properties, listener: " + l.getClass() + ".");
    } */
    
    public static class SenseIterator implements Iterator<Sense> {
        
        protected Target target;
        protected SenseFilter filter;
        
        protected Sense nextSense;
        protected int index = -1;
        
        public SenseIterator(Target target, SenseFilter filter) {
            this.target = target;
            this.filter = filter;
        }
        
        public boolean hasNext() {
            if ( nextSense == null ) setupNextSense();
            return nextSense != null;
        }
        
        public Sense next() {
            if ( nextSense == null ) setupNextSense();
            if ( nextSense == null ) throw new NoSuchElementException();
            Sense rv = nextSense;
            setupNextSense();
            return rv;
        }
        
        private void setupNextSense() {
            int count = target.getIndexedSize("Sense");
            while( ++index < count) {
                Sense s = (Sense)target.getIndexedValue(index, "Sense", "SENSE");
                if ( filter == null || filter.accept(s) ) {
                    nextSense = s;
                    return;
                }
            }
            nextSense = null;
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    public static class addSenseBonusUndoable implements Undoable, Serializable {
        
        static final long serialVersionUID = 9012523536066525223L;
        protected Target target;
        protected SenseBonusModifier modifier;
        
        public addSenseBonusUndoable(Target target, SenseBonusModifier modifier) {
            this.target = target;
            this.modifier = modifier;
        }
        
        public void redo() {
            if ( target != null && modifier != null ) {
                target.addSenseBonus(modifier, false);
            }
        }
        
        public void undo() {
            if ( target != null && modifier != null ) {
                target.removeSenseBonus(modifier, false);
            }
        }
    }
    
    public static class removeSenseBonusUndoable implements Undoable, Serializable {
        protected Target target;
        protected SenseBonusModifier modifier;
        
        public removeSenseBonusUndoable(Target target, SenseBonusModifier modifier) {
            this.target = target;
            this.modifier = modifier;
        }
        
        public void redo() {
            if ( target != null && modifier != null ) {
                target.removeSenseBonus(modifier, false);
            }
        }
        
        public void undo() {
            if ( target != null && modifier != null ) {
                target.addSenseBonus(modifier, false);
            }
        }
    }
    
    public static class addSensePenaltyUndoable implements Undoable, Serializable {
        static final long serialVersionUID = 8059693536066525223L;
        protected Target target;
        protected SensePenaltyModifier modifier;
        
        public addSensePenaltyUndoable(Target target, SensePenaltyModifier modifier) {
            this.target = target;
            this.modifier = modifier;
        }
        
        public void redo() {
            if ( target != null && modifier != null ) {
                target.addSensePenalty(modifier, false);
            }
        }
        
        public void undo() {
            if ( target != null && modifier != null ) {
                target.removeSensePenalty(modifier, false);
            }
        }
    }
    
    public static class removeSensePenaltyUndoable implements Undoable, Serializable {
        protected Target target;
        protected SensePenaltyModifier modifier;
        
        public removeSensePenaltyUndoable(Target target, SensePenaltyModifier modifier) {
            this.target = target;
            this.modifier = modifier;
        }
        
        public void redo() {
            if ( target != null && modifier != null ) {
                target.removeSensePenalty(modifier, false);
            }
        }
        
        public void undo() {
            if ( target != null && modifier != null ) {
                target.addSensePenalty(modifier, false);
            }
        }
    }
    public boolean suffersDCVPenaltyDueToSenses(){
        return true;
    }
    public boolean suffersOCVPenaltyDueToSenses(){
        return true;
    }
    
    public void setKnockbackResistance(int resistance) {
        add("Target.KNOCKBACKRESISTANCE", new Integer(resistance), true, true);
    }
    
    public int getKnockbackResistance() {
        Integer kb = getIntegerValue("Target.KNOCKBACKRESISTANCE");
        return (kb == null ? 0 : kb);
    }
    
    public void setRandomSequenceNumber(long time, float random) {
        randomSequenceHash.put(time, random);
    }
    
    public float getRandomSequenceNumber(long time) {
        return randomSequenceHash.get(time);
    }
    
    public boolean hasRandomSequenceNumber(long time) {
        return randomSequenceHash.containsKey(time);
    }
    
    public void removeRandomSequenceNumber(long time) {
        randomSequenceHash.remove(time);
    }

    public Target getAliasReferent() {
        return this;
    }

    public String getAliasIdentifier() {
        return getName();
    }

    public void setAliasIdentifier(String identifier) throws IllegalArgumentException {
        if ( getName().equals(identifier) == false ) {
            throw new IllegalArgumentException();
        }
    }
    
    public static class TargetEffectEntry implements Serializable, Debuggable{
        protected Effect effect;
        
        public TargetEffectEntry(Effect effect) {
            this.effect = effect;
        }

        public Effect getEffect() {
            return effect;
        }

        public void setEffect(Effect effect) {
            this.effect = effect;
        }

        public String getName() {
            return effect.getName();
        }

        public void displayDebugWindow() {
            ObjectDebugger.displayDebugWindow("Effect: " + getName(), effect);
        }
        
        public String toString() {
            if ( effect == null ) {
                return null;
            }
            else {
                return getName() + " (" + effect.getClass() + ")";
            }
        }

        public String toDebugString() {
            return toString();
        }

        
    }
	
    
}