/*
 * SpeedCharacteristic.java
 *
 * Created on January 17, 2003, 12:54 PM
 */

package champions;

/** Represents a SPEED characteristic.
 *
 * The SpeedCharacteristic object tracks a single speed Characteristic value of
 * a target.  To accomodate the various different states that a characteristic
 * can be in, there are four different values for a single Characteristic:<P>
 * 
 * <ul>
 *  <li>base - This is the value of the Characteristic originally purchased for the Character.  It 
 *             does not the effects of any powers.</li>
 *  <li>adjusted - This is the value of the Characteristic after it has been changed by the effects
 *             of various powers.  Aids, Drains, Growth, etc. all can modify this value.  It is the
 *             the normalCharacteristicMaximum value that the current value can ever be (legally).</li>
 *  <li>current - This is the current value of the Characteristic, based on the base, adjusted by the
 *             effects of various powers, and modified by damage.  For most characteristics, the adjusted
 *             is usually equal to the adjusted.  Current will only be different for "damagable" characteristics
 *             such as Body or Stun.</li>
 *  <li>adjustedAF - (Adjusted Affects Figured) This is the value of the Characteristic that is used to
 *             calculate the base of any figured characteristics that are affected by this particular
 *             characteristic.  The adjustedAF is the base characteristic value after it has been changed by the effects
 *             of various powers that affects figured.  The adjustedAF is the always less then or equal to
 *             the adjusted value.  Any time the adjustedAF changes, figures stat need to be recalculated</li>
 * </ul> 
 *
 * All of the above values are in the metric specific to the Characteristic represented.  However, internally
 * all data is kept in Characteristic Points to eliminate rounding errors.  The internal characteristic point
 * representations ignore the normal characteristic maximum, although the getCost() method will correctly 
 * calculate the cost in Character Points for the Characteristic given the normal characteristic maximum.
 *
 * Speed characteristics are based on two different starting values.  The base characteristic value is based upon
 * the normal starting value (as returned via getStartingValue()).  All other speed characteristic values are based
 * upon the adjusted starting value (as returned via getAdjustedStartingValue()).
 *
 * @author  Trevor Walker
 */
public class CharacteristicSpeed extends CharacteristicFigured {
    static final long serialVersionUID = -9024597217407937933L;
/** Creates a new instance of FiguredCharacteristic */
    public CharacteristicSpeed() {
        super("SPD", 10);
    }
    
    /** Setter for property adjustedStartingValue.
     *
     * The adjusted starting value is the value for a characteristic, if zero character points have been
     * spent on it.  The adjusted, adjustedAF, and current values are all based upon the adjusted
     * starting value.
     *
     * The adjustedStartingValue is in the metric specific to the represented stat.  In other words,
     * it is not in Character Points.
     *
     * The value provide can be a non-integer.  Since Speed needs to know the exact value, the adjustedStartingValue
     * will not be rounded for Speed.
     */
    public void setAdjustedStartingValue(double adjustedStartingValue) {
        this.adjustedStartingValue = adjustedStartingValue;
    }
    
        /** Setter for property startingValue.
     *
     * The starting value is the value for a characteristic, if zero character points have been
     * spent on it.  This exists for Primary Stats only.  Figured Stats use a figured base
     * characteristic to determine the starting value of a stat.
     *
     * @param statBaseValue New value of property statBaseValue.
     *
     */
    public void setStartingValue(double startingValue) {
        this.startingValue = startingValue;
    }
    
    /** Setter for property baseStat.
     * @param baseStat New value of property baseStat.
     *
     */
    public void setBaseStat(int newValue) {
        int diff =(int)( (newValue * cpPerPoint) - (baseCP + startingValue * 10) );
        
        baseCP += diff;
    }
    
    /** Getter for property baseStat.
     * @return Value of property baseStat.
     *
     */
    public int getBaseStat() {
        return (int)Math.floor(baseCP / cpPerPoint + startingValue);
    }
    
    /** Getter for property adjustedStat.
     * @return Value of property adjustedStat.
     *
     */
    public int getAdjustedStat() {
        return (int)Math.floor( (baseCP + adjustedCPOffset) / cpPerPoint + adjustedStartingValue);
    }
    
    /** Getter for property currentStat.
     * @return Value of property currentStat.
     *
     */
    public int getCurrentStat() {
        return (int)Math.floor((baseCP + adjustedCPOffset + currentCPOffset) / cpPerPoint + adjustedStartingValue);
    }
    
        
    /** Getter for property adjustedAFStat.
     * @return Value of property adjustedAFStat.
     *
     */
    public int getAdjustedAFStat() {
        return (int)Math.floor( (baseCP + adjustedAFCPOffset) / cpPerPoint + adjustedStartingValue);
    }
    
}
