/*
 * Sense.java
 *
 * Created on June 16, 2004, 12:49 PM
 */

package champions;

import champions.interfaces.Debuggable;
import champions.interfaces.SenseCapabilities;
import champions.parameters.ListParameter;
import champions.parameters.ParameterList;
import champions.powers.powerInvisibility;

import java.awt.BorderLayout;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.JFrame;

/**
 *
 * @author  1425
 */
public class Sense
        implements SenseCapabilities, Debuggable, Serializable {
    static final long serialVersionUID = 8057623536066525223L;
    /** Indicate the value is true. */
    protected static final int SENSE_TRUE = 1;
    
    /** Indicate the value is false. */
    protected static final int SENSE_FALSE = 0;
    
    /** Indicate the value is inherited. */
    protected static final int SENSE_INHERITED = Integer.MIN_VALUE;
    
    
    
    /** Holds the SenseGroup this sense belongs too.
     *
     * For normal senses, this is usually a sense group.  For sense groups,
     * this is typically null.
     */
    protected SenseGroup senseGroup = null;
    
    /** Holds the name of the sense group this sense belongs to.
     *
     * For normal senses, this is usually a sense group name.  For sense groups,
     * this is typically null.
     */
    protected String senseGroupName = null;
    
    /** Holds the name of this sense. */
    protected String senseName = null;
    
    /** Holds the Icon for the Sense. */
    protected Icon senseIcon = null;
    
    /** Holds the Bonus Modifiers for the Sense.
     *
     */
    protected Set bonusModifiers = null;
    
    /** Holds the Penalty Modifiers for the Sense.
     *
     */
    protected Set penaltyModifiers = null;
    
    /** Holds the arc of perception value. */
    protected int arcOfPerception = SENSE_INHERITED;
    
    /** Holds the conceal level value.
     *
     * This is the relative value to the parent sense.  0 indicates the parent
     * sense value should be used.
     */
    protected int concealLevel = 0;
    
    /** Holds the EnhancedPerceptionLevel value.
     *
     * This is the relative value to the parent sense.  0 indicates the parent
     * sense value should be used.
     */
    protected int enhancedPerceptionLevel = 0;
    
    /** Holds the microscopic level of the sense.
     *
     * This is the relative value to the parent sense.  0 indicates the parent
     * sense value should be used.
     */
    protected int microscopicLevel = 0;
    
    /** Holds the rapid level of the sense.
     *
     * This is the relative value to the parent sense.  0 indicates the parent
     * sense value should be used.
     */
    protected int rapidLevel = 0;
    
    /** Holds the telescopic level of the sense.
     *
     * This is the relative value to the parent sense.  0 indicates the parent
     * sense value should be used.
     */
    protected int telescopicLevel = 0;
    
    /** Holds the analyze value. */
    protected int analyze = SENSE_INHERITED;
    
    /** Holds the detect value. */
    protected int detect = SENSE_INHERITED;
    
    /** Holds the isSense value. */
    protected int sense = SENSE_INHERITED;
    
    /** Holds the targetting value. */
    protected int targetting = SENSE_INHERITED;
    
    /** Holds the ranged value. */
    protected int ranged = SENSE_INHERITED;
    
        /** Holds the ranged value. */
    protected int cost = SENSE_INHERITED;
    
    /** Holds the functioning value. */
    protected int functioning = SENSE_INHERITED;
    
    /** Holds the tracking value. */
    protected int tracking = SENSE_INHERITED;
    
    /** Holds the transmit value. */
    protected int transmit = SENSE_INHERITED;
    
    protected Set<TargetAlias> perceivedTargets;
    
    /** Creates a new instance of Sense */
    protected Sense(String senseName) {
        setSenseName(senseName);
    }
    
    /** Creates a new instance of Sense */
    public Sense(String senseName, String senseGroupName) {
        setSenseName(senseName);
        setSenseGroupName(senseGroupName);
    }
    
    public void setSenseName(String name) {
        this.senseName = name;
    }
    
    public String getSenseName() {
        return senseName;
    }
    
    public String getSenseGroupName() {
        return senseGroupName;
    }
    
    public int getArcOfPerception() {
        if ( arcOfPerception == SENSE_INHERITED && senseGroup != null ) {
            return senseGroup.getArcOfPerception();
        } else {
            return arcOfPerception;
        }
    }
    
    public int getConcealLevel() {
        int level = concealLevel;
        if ( senseGroup != null ) {
            level += senseGroup.getConcealLevel();
        }
        return level;
    }
    
    /** Returns the overall adjustment to the perception of this sense.
     *
     * The perception adjustment will include all the bonuses and penalties
     * currently applied to the sense.
     */
    public int getEnhancedPerceptionLevel() {
        int level = enhancedPerceptionLevel;
        if ( senseGroup != null ) {
            level += senseGroup.getEnhancedPerceptionLevel();
        }
        
        if ( bonusModifiers != null ) {
            Iterator it = bonusModifiers.iterator();
            while(it.hasNext()) {
                SenseBonusModifier sc = (SenseBonusModifier) it.next();
                level += sc.getEnhancedPerceptionBonus();
            }
        }
        
        if ( penaltyModifiers != null ) {
            Iterator it = penaltyModifiers.iterator();
            while(it.hasNext()) {
                SensePenaltyModifier sc = (SensePenaltyModifier) it.next();
                level += sc.getEnhancedPerceptionPenalty();
            }
        }
        return level;
    }
    
    public int getMicroscopicLevel() {
        int level = microscopicLevel;
        if ( senseGroup != null ) {
            level += senseGroup.getMicroscopicLevel();
        }
        
        if ( bonusModifiers != null ) {
            Iterator it = bonusModifiers.iterator();
            while(it.hasNext()) {
                SenseBonusModifier sc = (SenseBonusModifier) it.next();
                level += sc.getMicroscopicBonus();
            }
        }
        
        if ( penaltyModifiers != null ) {
            Iterator it = penaltyModifiers.iterator();
            while(it.hasNext()) {
                SensePenaltyModifier sc = (SensePenaltyModifier) it.next();
                level += sc.getMicroscopicPenalty();
            }
        }
        return level;
    }
    
    public int getRapidLevel() {
        int level = rapidLevel;
        if ( senseGroup != null ) {
            level += senseGroup.getRapidLevel();
        }
        
        if ( bonusModifiers != null ) {
            Iterator it = bonusModifiers.iterator();
            while(it.hasNext()) {
                SenseBonusModifier sc = (SenseBonusModifier) it.next();
                level += sc.getRapidBonus();
            }
        }
        
        if ( penaltyModifiers != null ) {
            Iterator it = penaltyModifiers.iterator();
            while(it.hasNext()) {
                SensePenaltyModifier sc = (SensePenaltyModifier) it.next();
                level += sc.getRapidPenalty();
            }
        }
        return level;
    }
    
    public int getTelescopicLevel() {
        int level = telescopicLevel;
        if ( senseGroup != null ) {
            level += senseGroup.getTelescopicLevel();
        }
        if ( bonusModifiers != null ) {
            Iterator it = bonusModifiers.iterator();
            while(it.hasNext()) {
                SenseBonusModifier sc = (SenseBonusModifier) it.next();
                level += sc.getTelescopicBonus();
            }
        }
        
        if ( penaltyModifiers != null ) {
            Iterator it = penaltyModifiers.iterator();
            while(it.hasNext()) {
                SensePenaltyModifier sc = (SensePenaltyModifier) it.next();
                level += sc.getTelescopicPenalty();
            }
        }
        return level;
    }
    
    public boolean isAnalyzeSense() {
        if ( bonusModifiers != null ) {
            Iterator it = bonusModifiers.iterator();
            while(it.hasNext()) {
                SenseBonusModifier sc = (SenseBonusModifier) it.next();
                if ( sc.isAnalyzeBonus() ) return true;
            }
        }
        
        if ( senseGroup != null && analyze == SENSE_INHERITED ) {
            return senseGroup.isAnalyzeSense();
        }
        
        if ( analyze == SENSE_FALSE || analyze == SENSE_INHERITED ) return false;
        return true;
    }
    
    public boolean isDetectSense() {
        if ( bonusModifiers != null ) {
            Iterator it = bonusModifiers.iterator();
            while(it.hasNext()) {
                SenseBonusModifier sc = (SenseBonusModifier) it.next();
                if ( sc.isDetectBonus() ) return true;
            }
        }
        
        if ( senseGroup != null && detect == SENSE_INHERITED ) {
            return senseGroup.isDetectSense();
        }
        
        if ( detect == SENSE_FALSE || detect == SENSE_INHERITED ) return false;
        return true;
    }
    
    public boolean isFunctioning() {
        if ( penaltyModifiers != null ) {
            Iterator it = penaltyModifiers.iterator();
            while(it.hasNext()) {
                SensePenaltyModifier sc = (SensePenaltyModifier) it.next();
                if ( sc.isFunctioningPenalty() ) return false;
            }
        }
        
        if ( senseGroup != null && senseGroup.isFunctioningOverridden() ) {
            return senseGroup.isFunctioning();
        }
        
        if ( bonusModifiers != null ) {
            Iterator it = bonusModifiers.iterator();
            while(it.hasNext()) {
                SenseBonusModifier sc = (SenseBonusModifier) it.next();
                if ( sc.isFunctioningBonus() ) return true;
            }
        }
        
        if ( senseGroup != null && functioning == SENSE_INHERITED  ) {
            return senseGroup.isFunctioning();
        }
        
        if ( functioning == SENSE_FALSE || functioning == SENSE_INHERITED ) return false;
        return true;
    }
    
    public boolean isRangedSense() {
        if ( penaltyModifiers != null ) {
            Iterator it = penaltyModifiers.iterator();
            while(it.hasNext()) {
                SensePenaltyModifier sc = (SensePenaltyModifier) it.next();
                if ( sc.isRangedPenalty() ) return false;
            }
        }
        
        if ( senseGroup != null && senseGroup.isRangedOverridden() ) {
            return senseGroup.isRangedSense();
        }
        
        if ( bonusModifiers != null ) {
            Iterator it = bonusModifiers.iterator();
            while(it.hasNext()) {
                SenseBonusModifier sc = (SenseBonusModifier) it.next();
                if ( sc.isRangedBonus() ) return true;
            }
        }
        
        if ( senseGroup != null && ranged == SENSE_INHERITED ) {
            return senseGroup.isRangedSense();
        }
        
        if ( ranged == SENSE_FALSE || ranged == SENSE_INHERITED ) return false;
        return true;
    }
    
    public boolean isSenseSense() {
        if ( bonusModifiers != null ) {
            Iterator it = bonusModifiers.iterator();
            while(it.hasNext()) {
                SenseBonusModifier sc = (SenseBonusModifier) it.next();
                if ( sc.isSenseBonus() ) return true;
            }
        }
        
        if ( senseGroup != null && sense == SENSE_INHERITED ) {
            return senseGroup.isSenseSense();
        }
        
        if ( sense == SENSE_FALSE || sense == SENSE_INHERITED ) return false;
        return true;
    }
    
    public boolean isTargettingSense() {
        if ( penaltyModifiers != null ) {
            Iterator it = penaltyModifiers.iterator();
            while(it.hasNext()) {
                SensePenaltyModifier sc = (SensePenaltyModifier) it.next();
                if ( sc.isTargettingPenalty() ) return false;
            }
        }
        
        if ( senseGroup != null && senseGroup.isTargettingOverridden() ) {
            return senseGroup.isTargettingSense();
        }
        
        if ( bonusModifiers != null ) {
            Iterator it = bonusModifiers.iterator();
            while(it.hasNext()) {
                SenseBonusModifier sc = (SenseBonusModifier) it.next();
                if ( sc.isTargettingBonus() ) return true;
            }
        }
        
        if ( senseGroup != null && targetting == SENSE_INHERITED ) {
            return senseGroup.isTargettingSense();
        }
        
        if ( targetting == SENSE_FALSE || targetting == SENSE_INHERITED ) return false;
        return true;
    }
    
    public boolean isTrackingSense() {
        if ( bonusModifiers != null ) {
            Iterator it = bonusModifiers.iterator();
            while(it.hasNext()) {
                SenseBonusModifier sc = (SenseBonusModifier) it.next();
                if ( sc.isTrackingBonus() ) return true;
            }
        }
        
        if ( senseGroup != null && tracking == SENSE_INHERITED ) {
            return senseGroup.isTrackingSense();
        }
        
        if ( tracking == SENSE_FALSE || tracking == SENSE_INHERITED ) return false;
        return true;
    }
    
    public boolean isTransmitSense() {
        if ( penaltyModifiers != null ) {
            Iterator it = penaltyModifiers.iterator();
            while(it.hasNext()) {
                SensePenaltyModifier sc = (SensePenaltyModifier) it.next();
                if ( sc.isTransmitPenalty() ) return false;
            }
        }
        
        if ( senseGroup != null && senseGroup.isTransmitOverridden() ) {
            return senseGroup.isTransmitSense();
        }
        
        if ( bonusModifiers != null ) {
            Iterator it = bonusModifiers.iterator();
            while(it.hasNext()) {
                SenseBonusModifier sc = (SenseBonusModifier) it.next();
                if ( sc.isTransmitBonus() ) return true;
            }
        }
        
        if ( senseGroup != null && transmit == SENSE_INHERITED ) {
            return senseGroup.isTransmitSense();
        }
        
        if ( transmit == SENSE_FALSE || transmit == SENSE_INHERITED ) return false;
        return true;
    }
    
    public String toString() {
        return senseName;
    }
    
    public void displayDebugWindow() {
        String windowName;
        if ( senseGroup != null ) {
            windowName = "Sense: " + getSenseName() + "(" + senseGroup.getSenseName() + ")" + "@" + Integer.toHexString(hashCode());
        } else {
            windowName = "Sense: " + getSenseName()+ "@" + Integer.toHexString(hashCode());
        }
        
        JFrame f = new JFrame(windowName);
        ObjectDebugger dle = new ObjectDebugger();
        dle.setDebugObject(this);
        
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(dle);
        f.pack();
        f.setVisible(true);
    }
    
    public boolean isSenseGroup() {
        return false;
    }
    
    public boolean isSense() {
        return true;
    }
    
    /**
     * Getter for property senseGroup.
     * @return Value of property senseGroup.
     */
    public champions.SenseGroup getSenseGroup() {
        
        
        return senseGroup;
    }
    
    /**
     * Setter for property senseGroup.
     * @param senseGroup New value of property senseGroup.
     */
    public void setSenseGroup(SenseGroup senseGroup) {
        this.senseGroup = senseGroup;
        
        if ( senseGroup != null ) {
            senseGroupName = senseGroup.getSenseName();
        } else {
            senseGroupName = null;
        }
    }
    
    /**
     * Setter for property senseGroupName.
     * @param senseGroupName New value of property senseGroupName.
     */
    public void setSenseGroupName(String senseGroupName) {
        this.senseGroupName = senseGroupName;
        
        if ( senseGroupName == null ) {
            senseGroup = null;
        }
        else if ( senseGroup == null || senseGroup.getSenseGroupName().equals(senseGroupName) == false ) {
            senseGroup = (SenseGroup) PADRoster.getNewSense(senseGroupName);
        }
    }
    
    /**
     * Setter for property arcOfPerception.
     * @param arcOfPerception New value of property arcOfPerception.
     */
    public void setArcOfPerception(int arcOfPerception) {
        this.arcOfPerception = arcOfPerception;
    }
    
    /**
     * Setter for property concealLevel.
     * @param concealLevel New value of property concealLevel.
     */
    public void setConcealLevel(int concealLevel) {
        this.concealLevel = concealLevel;
    }
    
    
    /**
     * Setter for property enhancedPerceptionLevel.
     * @param enhancedPerceptionLevel New value of property enhancedPerceptionLevel.
     */
    public void setEnhancedPerceptionLevel(int enhancedPerceptionLevel) {
        this.enhancedPerceptionLevel = enhancedPerceptionLevel;
    }
    
    /**
     * Setter for property microscopicLevel.
     * @param microscopicLevel New value of property microscopicLevel.
     */
    public void setMicroscopicLevel(int microscopicLevel) {
        this.microscopicLevel = microscopicLevel;
    }
    
    /**
     * Setter for property rapidLevel.
     * @param rapidLevel New value of property rapidLevel.
     */
    public void setRapidLevel(int rapidLevel) {
        this.rapidLevel = rapidLevel;
    }
    
    /**
     * Setter for property telescopicLevel.
     * @param telescopicLevel New value of property telescopicLevel.
     */
    public void setTelescopicLevel(int telescopicLevel) {
        this.telescopicLevel = telescopicLevel;
    }
    
    /**
     * Setter for property analyze.
     * @param analyze New value of property analyze.
     */
    public void setAnalyzeSense(boolean analyze) {
        this.analyze = analyze?SENSE_TRUE:SENSE_FALSE;
    }
    
    /**
     * Setter for property detect.
     * @param detect New value of property detect.
     */
    public void setDetectSense(boolean detect) {
        this.detect = detect?SENSE_TRUE:SENSE_FALSE;
    }
    
    /**
     * Setter for property targetting.
     * @param targetting New value of property targetting.
     */
    public void setTargettingSense(boolean targetting) {
        this.targetting = targetting?SENSE_TRUE:SENSE_FALSE;
    }
    
    /**
     * Setter for property ranged.
     * @param ranged New value of property ranged.
     */
    public void setRangedSense(boolean ranged) {
        this.ranged = ranged?SENSE_TRUE:SENSE_FALSE;
    }
    
    /**
     * Setter for property cost.
     * @param cost New value of property ranged.
     */
    public void setCost(int cost) {
        this.cost = cost;
    }
    
    /**
     * Getter for property cost.
     * @param cost New value of property ranged.
     */
    public int getCost() {
        return cost;
    }
    
    
    
    /**
     * Setter for property functioning.
     * @param functioning New value of property functioning.
     */
    public void setFunctioning(boolean functioning) {
        this.functioning = functioning?SENSE_TRUE:SENSE_FALSE;
    }
    
    /**
     * Setter for property tracking.
     * @param tracking New value of property tracking.
     */
    public void setTrackingSense(boolean tracking) {
        this.tracking = tracking?SENSE_TRUE:SENSE_FALSE;
    }
    
    /**
     * Setter for property transmit.
     * @param transmit New value of property transmit.
     */
    public void setTransmitSense(boolean transmit) {
        this.transmit = transmit?SENSE_TRUE:SENSE_FALSE;
    }
    
    /** Adds a Sense Bonus to a sense.
     *
     */
    public void addSenseBonus(SenseBonusModifier modifier){
        if ( bonusModifiers == null ) bonusModifiers = new HashSet();
        bonusModifiers.add( modifier );
    }
    
    /** Removes a Sense Bonus from a sense.
     *
     */
    public void removeSenseBonus(SenseBonusModifier modifier){
        if ( bonusModifiers != null ) bonusModifiers.remove(modifier);
    }
    
    /** Adds a Sense Penalty to a sense.
     *
     */
    public void addSensePenalty(SensePenaltyModifier modifier){
        if ( penaltyModifiers == null ) penaltyModifiers = new HashSet();
        penaltyModifiers.add( modifier );
    }
    
    /** Removes a Sense Penalty from a sense.
     *
     */
    public void removeSensePenalty(SensePenaltyModifier modifier){
        if ( penaltyModifiers != null ) penaltyModifiers.remove(modifier);
    }
    
    /** Adds a target that this sense can perceive.
     *
     */
    public void addPerceivedTarget(Target target) {
        if ( perceivedTargets == null ) {
            perceivedTargets = new HashSet<TargetAlias>();
        }
        
        if ( perceivedTargets.contains(target) == false ) {
            perceivedTargets.add(new TargetAlias(target));
        }
    }
    
    /** Remove a target that this sense can perceive.
     *
     */
    public void removePerceivedTarget(Target target) {
        if ( perceivedTargets == null ) return;
        
        if ( perceivedTargets.contains(target) == false ) {
            perceivedTargets.remove(target);
        }
    }
    
    /** Returns the list of targets this sense can currently perceive.
     *
     */
    public Set getPerceivedTargets() {
        return perceivedTargets;
    }
    
    /** Returns whether this sense can be used for targeting target.
     *
     *  Will return if the indicated target is currently targetable
     *  with this sense.  If the sense isFunctioning() is false,
     *  this will always return false.  If the sense is functioning,
     *  this will return true if the sense is a targeting sense
     *  or if the sense is a ranged sense for which a perception roll
     *  has been made (ie target is on perceived list).
     *
     */
    public boolean isTargetableWithSense(Target target) {
    	//jeff
    	if( target.getAbility("Invisibility")!=null) {
        	Ability inv = target.getAbility("Invisibility");
        	powerInvisibility pInv =  (powerInvisibility) inv.getPower();
        	ParameterList p = inv.getPowerParameterList();
        	ListParameter l = (ListParameter) p.getParameter("Senses");
        	ArrayList senses = (ArrayList) p.getParameterValue("Senses");
        	 
        	for (Object o: senses) {
        		SenseGroup group = (SenseGroup)o;
        		if(group.getSenseName().equals(this.getSenseGroupName()))
        		{
        			return false;
        		}
			}
        }
        if ( isFunctioning() == false ) return false;
        
        if ( isTargettingSense() == true ) return true;
        
        if ( isRangedSense() == true && isPerceivableWithSense(target) ) return true;
        
        return false;
    }
    
    /** Returns whether the target is perceviable with this sense.
     *
     *  This will return if the target is in the perceivedTarget list.  It
     *  does not take into account other possible factors, such as if the
     *  sense is functioning.
     *
     */
    public boolean isPerceivableWithSense(Target target) {
        
        if ( perceivedTargets != null && perceivedTargets.contains(target) ) return true;
        
        return false;
    }
    
    /**
     * Getter for property senseIcon.
     * @return Value of property senseIcon.
     */
    public Icon getSenseIcon() {
        return senseIcon;
    }
    
    /**
     * Setter for property senseIcon.
     * @param senseIcon New value of property senseIcon.
     */
    public void setSenseIcon(Icon senseIcon) {
        this.senseIcon = senseIcon;
    }
    
    /** Returns a description of the penalties applied to the sense.
     *
     */
    public String getPenaltyString() {
        StringBuffer sb = new StringBuffer();
        
        if ( senseGroup != null ) {
            sb.append( senseGroup.getPenaltyString() );
        }
        
        if ( penaltyModifiers != null ) {
            Iterator it = penaltyModifiers.iterator();
            while(it.hasNext()) {
                SensePenaltyModifier sc = (SensePenaltyModifier) it.next();
                String name = sc.getName();
                if ( sb.indexOf(name) == -1 ) {
                    if ( sb.length() > 0 ) sb.append(", ");
                    sb.append( name );
                }
            }
        }
        return sb.toString();
    }
    
    public Set getPenaltyModifiers() {
        Set set = null;
        
        if ( senseGroup != null ) {
            set = getPenaltyModifiers();
            set.addAll( penaltyModifiers );
        } else {
            set = new HashSet();
            set.addAll(penaltyModifiers);
        }
        return set;
    }
    
    public boolean equals(Object that) {
        return that != null &&
                that instanceof Sense &&
                ( senseName == ((Sense)that).senseName ||
                senseName.equals( ((Sense)that).senseName)) &&
                ( senseGroup == ((Sense)that).senseGroup ||
                senseGroup.equals( ((Sense)that).senseGroup));
    }
    
    public String toDebugString() {
        return toString();
    }
    
}
