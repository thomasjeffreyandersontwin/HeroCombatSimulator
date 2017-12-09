/*
 * Interface.java
 *
 * Created on June 16, 2004, 12:55 PM
 */

package champions.interfaces;

/**
 *
 * @author  1425
 */
public interface SenseCapabilities {
    
    /** Returns the Parent Sense of the sense this sense capability describes.
     *
     * This value describes the parent sense of the this sense capability.  For
     * sense groups, this will typically be null.  For a normal sense, this will
     * be the sense group they belong too, or possibly the sense they were 
     * based upon.
     */
//    public String getParentSenseName();
    
    /** Returns the specific Sense this sense capability describes.
     *
     * This value can describe an individual sense or a group of senses.  If 
     * it is null, it describes all senses in its senses the target has.  If 
     * it is a standard sense group name of Sight Group, Smell/Taste Group, 
     * Hearing Group, Mental Group, Radio Group, Unusual Group, No Sense Group, 
     * or Touch Group then it describes the indicated group of senses.
     * Otherwise, it describe a single sense by its standard name (if it has
     * one).
     */
    public String getSenseName();
    
    /** Returns the priority of this sense group.
     *
     * This value describes at what level this sense capability should be
     * applied to senses.  There are four levels: SENSE_CAPABILITY_BASE,
     * SENSE_CAPABILITY_BONUS, SENSE_CAPABILITY_PENALTY, and 
     * SENSE_CAPABILITY_FILTER.<P>
     *
     * SENSE_CAPABILITY_BASE indicates this modifies the base level of the 
     * sense.  SENSE_CAPABILITY_BONUS indicates this is a bonus to the sense and
     * is added after the base is established.  SENSE_CAPABILITY_PENALTY 
     * indicates that this is a penalty to the sense and should be applied after
     * both the base and the bonuses are applied.  SENSE_CAPABILITY_FILTER 
     * indicates that the capability is just used as a filter and shouldn't be
     * applied to the final values.
     */
//    public int getSenseCapabilityType();
//    
//    /** SENSE_CAPABILITY_BASE indicates this modifies the base level of the sense. */
//    public static final int SENSE_CAPABILITY_BASE = 0;
//    /** ENSE_CAPABILITY_BONUS indicates this is a bonus to the sense. 
//     *
//     * SENSE_CAPABILITY_BONUS indicates this is a bonus to the sense and
//     * is added after the base is established.
//     */
//    public static final int SENSE_CAPABILITY_BONUS = 1;
//    /** SENSE_CAPABILITY_PENALTY indicates that this is a penalty to the sense. 
//     *
//     * SENSE_CAPABILITY_PENALTY indicates that this is a penalty to the sense 
//     * and should be applied after both the base and the bonuses are applied.
//     */
//    public static final int SENSE_CAPABILITY_PENALTY = 2;
//    /** SENSE_CAPABILITY_FILTER indicates that the capability is just used as a filter. 
//     * SENSE_CAPABILITY_FILTER indicates that the capability is just used as a 
//     * filter and shouldn't be applied to the final values.
//     */
//    public static final int SENSE_CAPABILITY_FILTER = 3;
    
    /** Returns whether this sense can detect the indicated thing.
     *
     * This determines whether the sense can detect the indicated thing using
     * standard detect rules.
     */
    public boolean isDetectSense();
    
    /** Returns the amount of enhanced perception this sense provides.
     *
     * This could be negative in the case of a sense penalty.
     */
    public int getEnhancedPerceptionLevel();
    
    /** Returns whether this sense can be used to analyze things.
     *
     */
    public boolean isAnalyzeSense();
    
    /** Returns the concealed level of a active sense.
     *
     */
    public int getConcealLevel();
    
    /** Returns whether the sense is Discriminatory.
     *
     * This should return one of the values ARC_OF_PERCEPTION_120 (default), 
     * ARC_OF_PERCEPTION_240, or ARC_OF_PERCEPTION_360.
     */
    public int getArcOfPerception();
    
    /** ARC_OF_PERCEPTION_120 Value. */
    public static final int ARC_OF_PERCEPTION_120 = 0;
    /** ARC_OF_PERCEPTION_240 Value. */
    public static final int ARC_OF_PERCEPTION_240 = 1;
    /** ARC_OF_PERCEPTION_360 Value. */
    public static final int ARC_OF_PERCEPTION_360 = 2;
    
    /** Returns the microscopic level for this sense.
     *
     * The returned value is in 10x level of microscopic perception.  0 should
     * be returned if there is no microscopic capabilities.
     */
    public int getMicroscopicLevel();
    
    /** Returns whether the sense is ranged.
     *
     */
    public boolean isRangedSense();
    
    /** Returns level of Rapid-ness of the sense.
     *
     * The returned value is in 10x level of rapid perception.  0 should
     * be returned if there is no microscopic capabilities.
     */
    public int getRapidLevel();
    
    /** Returns whether this is a sense?!?
     *
     * Returns true if this is a true sense (not just a detect).  True senses
     * do not require time to activate/use.  Detects require half phase actions.
     */
    public boolean isSenseSense();
    
    /** Returns whether this sense can be used for targetting in combat.
     *
     */
    public boolean isTargettingSense();
    
    /** Returns the telescopic level of the sense.
     *
     * The telescopic level can be used to reduce the range penalty on a ranged
     * attack.
     */
    public int getTelescopicLevel();
    
    /** Returns whether this sense can be used for tracking.
     *
     */
    public boolean isTrackingSense();
    
    /** Returns whether this sense can be used to transmit.
     *
     */
    public boolean isTransmitSense();
    
    /** Returns whether this sense is currently functioning.
     *
     */
    public boolean isFunctioning();
    
    /** Returns whether this is a senseGroup.
     *
     */
    public boolean isSenseGroup();
    
    /** Returns whther this is a sense (as opposed to a sense group)
     *
     */
    public boolean isSense();
}
