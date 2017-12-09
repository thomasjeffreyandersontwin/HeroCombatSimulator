/*
 * SenseBonusModifier.java
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
public class SenseBonusModifier
implements Debuggable, Serializable {
    
    static final long serialVersionUID = 8057693536066525223L;
    
    /** Indicates name of modifier... */
    protected String name;
    
    /** Holds the name of the sense of sense group that is bonused.
     *
     * If this is the name of a sense group, the whole group will be penalized.
     * Otherwise the individual senses will be penalized.
     */
    protected String senseAffected;
    
    /** EnhancePerception: Positive values indicate a bonus. */
    protected int enhancedPerceptionBonus = 0;
    
    /** Microscopic: Positive values indicate a bonus. */
    protected int microscopicBonus = 0;
    
    /** Rapid: Positive values indicate a bonus. */
    protected int rapidBonus = 0;
    
    /** Telescopic: Positive values indicate a bonus. */
    protected int telescopicBonus = 0;
    
    /** Ranged: true will turn on ranged on sense. */
    protected boolean rangedBonus = false;
    
    /** Targetting: true will turn on targetting on sense. */
    protected boolean targettingBonus = false;
    
    /** Transmit: true will turn on transmit on sense. */
    protected boolean transmitBonus = false;
    
    /** Tracking: true will turn on tracking on sense. */
    protected boolean trackingBonus = false;
    
    /** Sense: true will turn sense on sense. */
    protected boolean senseBonus = false;
    
    /** Detect: true will turn detect on sense. */
    protected boolean detectBonus = false;
    
    /** Analyze: true will turn analyze on sense. */
    protected boolean analyzeBonus = false;
    
    /** Functions: true will turn the sense on complete. */
    protected boolean functioningBonus = false;
    
    
    /** Creates a new instance of SensebonusModifier */
    public SenseBonusModifier(String name, String senseAffected) {
        setName(name);
        setSenseAffected(senseAffected);
    }
    
    /**
     * Getter for property analyzeBonus.
     * @return Value of property analyzeBonus.
     */
    public boolean isAnalyzeBonus() {
        return analyzeBonus;
    }
    
    /**
     * Setter for property analyzeBonus.
     * @param analyzeBonus New value of property analyzeBonus.
     */
    public void setAnalyzeBonus(boolean analyzeBonus) {
        this.analyzeBonus = analyzeBonus;
    }
    
    /**
     * Getter for property detectBonus.
     * @return Value of property detectBonus.
     */
    public boolean isDetectBonus() {
        return detectBonus;
    }
    
    /**
     * Setter for property detectBonus.
     * @param detectBonus New value of property detectBonus.
     */
    public void setDetectBonus(boolean detectBonus) {
        this.detectBonus = detectBonus;
    }
    
    /**
     * Getter for property senseBonus.
     * @return Value of property senseBonus.
     */
    public boolean isSenseBonus() {
        return senseBonus;
    }
    
    /**
     * Setter for property senseBonus.
     * @param senseBonus New value of property senseBonus.
     */
    public void setSenseBonus(boolean senseBonus) {
        this.senseBonus = senseBonus;
    }
    
    /**
     * Getter for property trackingBonus.
     * @return Value of property trackingBonus.
     */
    public boolean isTrackingBonus() {
        return trackingBonus;
    }
    
    /**
     * Setter for property trackingBonus.
     * @param trackingBonus New value of property trackingBonus.
     */
    public void setTrackingBonus(boolean trackingBonus) {
        this.trackingBonus = trackingBonus;
    }
    
    /**
     * Getter for property transmitBonus.
     * @return Value of property transmitBonus.
     */
    public boolean isTransmitBonus() {
        return transmitBonus;
    }
    
    /**
     * Setter for property transmitBonus.
     * @param transmitBonus New value of property transmitBonus.
     */
    public void setTransmitBonus(boolean transmitBonus) {
        this.transmitBonus = transmitBonus;
    }
    
    /**
     * Getter for property targettingBonus.
     * @return Value of property targettingBonus.
     */
    public boolean isTargettingBonus() {
        return targettingBonus;
    }
    
    /**
     * Setter for property targettingBonus.
     * @param targettingBonus New value of property targettingBonus.
     */
    public void setTargettingBonus(boolean targettingBonus) {
        this.targettingBonus = targettingBonus;
    }
    
    /**
     * Getter for property rangedBonus.
     * @return Value of property rangedBonus.
     */
    public boolean isRangedBonus() {
        return rangedBonus;
    }
    
    /**
     * Setter for property rangedBonus.
     * @param rangedBonus New value of property rangedBonus.
     */
    public void setRangedBonus(boolean rangedBonus) {
        this.rangedBonus = rangedBonus;
    }
    
    /**
     * Getter for property rapidBonus.
     * @return Value of property rapidBonus.
     */
    public int getRapidBonus() {
        return rapidBonus;
    }
    
    /**
     * Setter for property rapidBonus.
     * @param rapidBonus New value of property rapidBonus.
     */
    public void setRapidBonus(int rapidBonus) {
        this.rapidBonus = rapidBonus;
    }
    
    /**
     * Getter for property microscopicBonus.
     * @return Value of property microscopicBonus.
     */
    public int getMicroscopicBonus() {
        return microscopicBonus;
    }
    
    /**
     * Setter for property microscopicBonus.
     * @param microscopicBonus New value of property microscopicBonus.
     */
    public void setMicroscopicBonus(int microscopicBonus) {
        this.microscopicBonus = microscopicBonus;
    }
    
    /**
     * Getter for property enhancedPerceptionBonus.
     * @return Value of property enhancedPerceptionBonus.
     */
    public int getEnhancedPerceptionBonus() {
        return enhancedPerceptionBonus;
    }
    
    /**
     * Setter for property enhancedPerceptionBonus.
     * @param enhancedPerceptionBonus New value of property enhancedPerceptionBonus.
     */
    public void setEnhancedPerceptionBonus(int enhancedPerceptionBonus) {
        this.enhancedPerceptionBonus = enhancedPerceptionBonus;
    }
    
    /**
     * Getter for property telescopicBonus.
     * @return Value of property telescopicBonus.
     */
    public int getTelescopicBonus() {
        return telescopicBonus;
    }
    
    /**
     * Setter for property telescopicBonus.
     * @param telescopicBonus New value of property telescopicBonus.
     */
    public void setTelescopicBonus(int telescopicBonus) {
        this.telescopicBonus = telescopicBonus;
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
     * Getter for property functioningBonus.
     * @return Value of property functioningBonus.
     */
    public boolean isFunctioningBonus() {
        return functioningBonus;
    }
    
    /**
     * Setter for property functioningBonus.
     * @param functioningBonus New value of property functioningBonus.
     */
    public void setFunctioningBonus(boolean functioningBonus) {
        this.functioningBonus = functioningBonus;
    }
    
    /**
     * Getter for property senseAffected.
     * @return Value of property senseAffected.
     */
    public java.lang.String getSenseAffected() {
        return senseAffected;
    }
    
    /**
     * Setter for property senseAffected.
     * @param senseAffected New value of property senseAffected.
     */
    public void setSenseAffected(java.lang.String senseAffected) {
        this.senseAffected = senseAffected;
    }
    
    public void displayDebugWindow() {
        String windowName;
        if ( senseAffected == null ) {
            windowName = "SenseBonus: " + getName() + " against All Senses"+ " @" + Integer.toHexString(hashCode());
        }
        else {
            windowName = "SenseBonus: " + getName() + " against " + getSenseAffected()+ " @" + Integer.toHexString(hashCode());
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
