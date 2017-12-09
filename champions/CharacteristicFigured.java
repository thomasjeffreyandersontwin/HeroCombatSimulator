/*
 * FiguredCharacteristic.java
 *
 * Created on January 17, 2003, 12:40 PM
 */

package champions;

/** Represents a figured characteristic.
 *
 * The FiguredCharacteristic object tracks a single figured Characteristic value of
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
 * Figured characteristics are based on two different starting values.  The base characteristic value is based upon
 * the normal starting value (as returned via getStartingValue()).  All other figured characteristic values are based
 * upon the adjusted starting value (as returned via getAdjustedStartingValue()).
 *
 * @author  Trevor Walker
 */
public class CharacteristicFigured extends CharacteristicPrimary {
    
    /** Holds value of property adjustedStartingValue.
     *
     * The adjusted starting value is the value for a characteristic, if zero character points have been
     * spent on it.  The adjusted, adjustedAF, and current values are all based upon the adjusted
     * starting value.
     *
     * The adjustedStartingValue is in the metric specific to the represented stat.  In other words,
     * it is not in Character Points.
     */
    protected double adjustedStartingValue;
    
    /** Creates a new instance of FiguredCharacteristic */
    public CharacteristicFigured(String name, double costPerPoint) {
        super(name, 0, costPerPoint);
        
        adjustedStartingValue = 0;
    }
    
    /** Getter for property adjustedStartingValue.
     *
     * The adjusted starting value is the value for a characteristic, if zero character points have been
     * spent on it.  The adjusted, adjustedAF, and current values are all based upon the adjusted
     * starting value.
     *
     * The adjustedStartingValue is in the metric specific to the represented stat.  In other words,
     * it is not in Character Point.
     *
     * @return Value of property adjustedStartingValue.
     */
    public double getAdjustedStartingValue() {
        return this.adjustedStartingValue;
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
     * The value provide can be a non-integer.  The value will be rounded appropriately for this characteristic,
     * is necessary.
     */
    public void setAdjustedStartingValue(double adjustedStartingValue) {
        this.adjustedStartingValue = Math.round(adjustedStartingValue);
    }
    
        
    /** Getter for property adjustedStat.
     * @return Value of property adjustedStat.
     *
     */
    public int getAdjustedStat() {
        return (int)Math.round( (baseCP + adjustedCPOffset) / cpPerPoint + adjustedStartingValue);
    }
    
    /** Getter for property currentStat.
     * @return Value of property currentStat.
     *
     */
    public int getCurrentStat() {
        return (int)Math.round((baseCP + adjustedCPOffset + currentCPOffset) / cpPerPoint + adjustedStartingValue);
    }
    
        
    /** Getter for property adjustedAFStat.
     * @return Value of property adjustedAFStat.
     *
     */
    public int getAdjustedAFStat() {
        return (int)Math.round( (baseCP + adjustedAFCPOffset) / cpPerPoint + adjustedStartingValue); 
    }
    
        public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append(name);
//        sb.append("[B:");
//        sb.append( getBaseStat() );
//        sb.append(", A:");
//        sb.append( getAdjustedStat() );
//        sb.append(", C:");
//        sb.append( getCurrentStat() );
//        sb.append(", AF:");
//        sb.append( getAdjustedAFStat() );
//        
//        sb.append(", SV:");
//        sb.append( startingValue );
//        sb.append(", ASV:");
//        sb.append( adjustedStartingValue );
//        sb.append(", BCP:");
//        sb.append( baseCP );
//        sb.append(", ACPO");
//        sb.append( adjustedCPOffset );
//        sb.append(", CCPO:");
//        sb.append( currentCPOffset );
//        sb.append(", AFCPO:");
//        sb.append( adjustedAFCPOffset );
//        sb.append("]"); 
        
        return sb.toString();
    }
}
