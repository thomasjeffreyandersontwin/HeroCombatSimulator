/*
 * SensePenaltyModifier.java
 *
 * Created on June 16, 2004, 8:49 PM
 */

package champions;

import champions.interfaces.Debuggable;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import java.io.Serializable;

/**
 *
 * @author  1425
 */
public class SensePenaltyModifier
implements Debuggable,Serializable {
    static final long serialVersionUID = 9012522536066525223L;
    
    /** Indicates name of modifier... */
    protected String name;
    
    /** Holds the name of the sense of sense group that is penalized.
     *
     * If this is the name of a sense group, the whole group will be penalized.
     * Otherwise the individual senses will be penalized.
     */
    protected String senseAffected;
    
    /** EnhancedPerception: Negative values indicate a penalty. */
    protected int enhancedPerceptionPenalty = 0;
    
    /** Microscopic: Negative values indicate a penalty. */
    protected int microscopicPenalty = 0;
    
    /** Rapid: Negative values indicate a penalty. */
    protected int rapidPenalty = 0;
    
    /** Telescopic: Negative values indicate a penalty. */
    protected int telescopicPenalty = 0;
    
    /** Ranged: true will turn off ranged on sense. */
    protected boolean rangedPenalty = false;
    
    /** Targetting: true will turn off targetting on sense. */
    protected boolean targettingPenalty = false;
    
    /** Transmit: true will turn off transmit on sense. */
    protected boolean transmitPenalty = false;
    
    /** Functioning: true will turn off sense completely. */
    protected boolean functioningPenalty = false;
    
    /** Creates a new instance of SensePenaltyModifier */
    public SensePenaltyModifier(String name, String senseAffected) {
        setName(name);
        setSenseAffected(senseAffected);
    }
    
    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public java.lang.String getName() {
        return name;
    }
    
    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }
    
    /**
     * Getter for property enhancedPerceptionPenalty.
     * @return Value of property enhancedPerceptionPenalty.
     */
    public int getEnhancedPerceptionPenalty() {
        return enhancedPerceptionPenalty;
    }
    
    /**
     * Setter for property enhancedPerceptionPenalty.
     * @param enhancedPerceptionPenalty New value of property enhancedPerceptionPenalty.
     */
    public void setEnhancedPerceptionPenalty(int enhancedPerceptionPenalty) {
        this.enhancedPerceptionPenalty = enhancedPerceptionPenalty;
    }
    
    /**
     * Getter for property microscopicPenalty.
     * @return Value of property microscopicPenalty.
     */
    public int getMicroscopicPenalty() {
        return microscopicPenalty;
    }
    
    /**
     * Setter for property microscopicPenalty.
     * @param microscopicPenalty New value of property microscopicPenalty.
     */
    public void setMicroscopicPenalty(int microscopicPenalty) {
        this.microscopicPenalty = microscopicPenalty;
    }
    
    /**
     * Getter for property rapidPenalty.
     * @return Value of property rapidPenalty.
     */
    public int getRapidPenalty() {
        return rapidPenalty;
    }
    
    /**
     * Setter for property rapidPenalty.
     * @param rapidPenalty New value of property rapidPenalty.
     */
    public void setRapidPenalty(int rapidPenalty) {
        this.rapidPenalty = rapidPenalty;
    }
    
    /**
     * Getter for property telescopicPenalty.
     * @return Value of property telescopicPenalty.
     */
    public int getTelescopicPenalty() {
        return telescopicPenalty;
    }
    
    /**
     * Setter for property telescopicPenalty.
     * @param telescopicPenalty New value of property telescopicPenalty.
     */
    public void setTelescopicPenalty(int telescopicPenalty) {
        this.telescopicPenalty = telescopicPenalty;
    }
    
    /**
     * Getter for property rangedPenalty.
     * @return Value of property rangedPenalty.
     */
    public boolean isRangedPenalty() {
        return rangedPenalty;
    }
    
    /**
     * Setter for property rangedPenalty.
     * @param rangedPenalty New value of property rangedPenalty.
     */
    public void setRangedPenalty(boolean rangedPenalty) {
        this.rangedPenalty = rangedPenalty;
    }
    
    /**
     * Getter for property targettingPenalty.
     * @return Value of property targettingPenalty.
     */
    public boolean isTargettingPenalty() {
        return targettingPenalty;
    }
    
    /**
     * Setter for property targettingPenalty.
     * @param targettingPenalty New value of property targettingPenalty.
     */
    public void setTargettingPenalty(boolean targettingPenalty) {
        this.targettingPenalty = targettingPenalty;
    }
    
    /**
     * Getter for property transmitPenalty.
     * @return Value of property transmitPenalty.
     */
    public boolean isTransmitPenalty() {
        return transmitPenalty;
    }
    
    /**
     * Setter for property transmitPenalty.
     * @param transmitPenalty New value of property transmitPenalty.
     */
    public void setTransmitPenalty(boolean transmitPenalty) {
        this.transmitPenalty = transmitPenalty;
    }
    
    /**
     * Getter for property functioningPenalty.
     * @return Value of property functioningPenalty.
     */
    public boolean isFunctioningPenalty() {
        return functioningPenalty;
    }
    
    /**
     * Setter for property functioningPenalty.
     * @param functioningPenalty New value of property functioningPenalty.
     */
    public void setFunctioningPenalty(boolean functioningPenalty) {
        this.functioningPenalty = functioningPenalty;
    }
    
    /**
     * Getter for property senseAffect.
     *
     * If this is the name of a sense group, the whole group will be penalized.
     * Otherwise the individual senses will be penalized.
     */
    public java.lang.String getSenseAffected() {
        return senseAffected;
    }
    
    /**
     * Setter for property senseAffect.
     *
     * If this is the name of a sense group, the whole group will be penalized.
     * Otherwise the individual senses will be penalized.
     */
    public void setSenseAffected(java.lang.String senseAffected) {
        this.senseAffected = senseAffected;
    }
    
    public void displayDebugWindow() {
        String windowName;
        if ( senseAffected == null ) {
            windowName = "SensePenalty: " + getName() + " against All Senses"+ " @" + Integer.toHexString(hashCode());
        }
        else {
            windowName = "SensePenalty: " + getName() + " against " + getSenseAffected()+ " @" + Integer.toHexString(hashCode());
        }
        
        JFrame f = new JFrame(windowName);
        ObjectDebugger dle = new ObjectDebugger();
        dle.setDebugObject(this);
        
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(dle);
        f.pack();
        f.setVisible(true);
    }
    
    public String toDebugString() {
        return toString();
    }
    
}
