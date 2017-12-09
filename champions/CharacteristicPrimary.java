/*
 * Stat.java
 *
 * Created on January 16, 2003, 11:54 PM
 */

package champions;

/** Represents a primary characteristic.
 *
 * The PrimaryCharacteristic object tracks a single Primary Characteristic value of
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
 * @author  Trevor Walker
 */
public class CharacteristicPrimary extends Characteristic {
    
    /** Holds value of property baseCP. 
     *
     * The bastCP is the number of character points that have been
     * spent to increase the stat from the startingValue/figuredBaseValue.  
     * 
     * Typically the actual base characteristic value equals 
     * (baseCP / cpPerPoint) + startingValue.
     *
     * baseCP and stat calculations involving baseCP ignore any 
     * Normal Characteristic Maximum.  In other words, the cpPerPoint metric does
     * not change after the normalCharacteristicMaximum for the stat has been reached.  See the 
     * method getCost() to get the real CP cost of the stat, with regards to
     * normalCharacteristicMaximums.
     */
    protected double baseCP;
    
    /** Holds value of property adjustedStatCP. 
     *
     * The adjustedCPOffset is the number of character points, in addition
     * to the baseCP, which represent the adjusted characteristic value.
     *
     * Typically the adjusted characteristic value equals 
     * (baseCP + adjustedCPOffset) / cpPerPoint + startingValue.
     */
    protected double adjustedCPOffset;
    
    /** Holds value of property adjustedAFStatCP.
     *
     * The adjustedAFCPOffset is the number of character points, in addition
     * to the baseCP, which represent the adjustedAF characteristic value.
     *
     * Typically the adjusted characteristic value equals 
     * (baseCP + adjustedAFCPOffset) / cpPerPoint + startingValue.
     *
     * The adjustedAF value is used to calculate the base value of figured 
     * characteristics.
     */
     protected double adjustedAFCPOffset;
    
    /** Holds value of property currentStatCP.
     *
     * The currentCPOffset is the number of character points, in addition
     * to the baseCP and adjustedCPOffset, which represent the current 
     * characteristic value.
     *
     * Typically the current characteristic value equals 
     * (baseCP + adjustedAFCPOffset + currentCPOffset) / cpPerPoint + startingValue.
     *
     */
    protected double currentCPOffset;

    /** Holds value of property startingValue. 
     *
     * The starting value is the value for a characteristic, if zero character points have been
     * spent on it.
     *
     * The startingValue is in the metric specific to the represented stat.  In other words,
     * it is not in Character Points.
     */
    protected double startingValue;
    
    /** Holds value of property cpPerPoint. 
     * 
     * cpPerPoint is the cost of a single point of the represented characteristic.
     * The cpPerPoint represents the base cost, ignoring normal characteristic maximum.
     */
    protected double cpPerPoint;
    
    /** Holds value of property normalCharacteristicMaximum. 
     *
     * The normalCharacteristicMaximum represents the normal characteristic maximum for this characteristic,
     * given the character this characteristic is attached to.  The normalCharacteristicMaximum is determined
     * by the type of campaign (heroic vs. superheroic) and by powers the character possesses.
     */
    protected int normalCharacteristicMaximum;
    
    /** Use normal characteristic maximum */
    protected boolean useNormalCharacteristicMaximum = false;
    
    /** Creates a new instance of Stat */
    public CharacteristicPrimary(String name, double startingValue, double costPerPoint) {
        super(name);
        
        setStartingValue(startingValue);
        setCpPerPoint(costPerPoint);
        baseCP = 0;
        adjustedCPOffset = 0;
        adjustedAFCPOffset = 0;
        currentCPOffset = 0;
    }
    
    /** Getter for property baseStat.
     * @return Value of property baseStat.
     *
     */
    public int getBaseStat() {
        return (int)Math.round(baseCP / cpPerPoint + startingValue);
    }
    
    /** Setter for property baseStat.
     * @param baseStat New value of property baseStat.
     *
     */
    public void setBaseStat(int newValue) {
        int diff = newValue - getBaseStat();
        
        baseCP += diff * cpPerPoint;
    }
    
    /** Getter for property adjustedStat.
     * @return Value of property adjustedStat.
     *
     */
    public int getAdjustedStat() {
        return (int)Math.round( (baseCP + adjustedCPOffset) / cpPerPoint + startingValue);
    }
    
    /** Setter for property adjustedStat.
     * @param adjustedStat New value of property adjustedStat.
     *
     */
    public void setAdjustedStat(int newValue) {
        int diff = newValue - getAdjustedStat();
        
        adjustedCPOffset += diff * cpPerPoint;
    }
    
    
    
    /** Getter for property currentStat.
     * @return Value of property currentStat.
     *
     */
    public int getCurrentStat() {
        return (int)Math.round((baseCP + adjustedCPOffset + currentCPOffset) / cpPerPoint + startingValue);
    }
    
    /** Setter for property currentStat.
     * @param currentStat New value of property currentStat.
     *
     */
    public void setCurrentStat(int newValue) {
        int diff = newValue - getCurrentStat();
        
        currentCPOffset += diff * cpPerPoint;
    }
    
    /** Getter for property adjustedAFStat.
     * @return Value of property adjustedAFStat.
     *
     */
    public int getAdjustedAFStat() {
        return (int)Math.round( (baseCP + adjustedAFCPOffset) / cpPerPoint + startingValue);
    }
    
    /** Setter for property adjustedAFStat.
     * @param adjustedAFStat New value of property adjustedAFStat.
     *
     */
    public void setAdjustedAFStat(int newValue) {
        int diff = newValue - getAdjustedAFStat();
        
        adjustedAFCPOffset += diff * cpPerPoint;        
    }
    
    /** Getter for property baseCP.
     * @return Value of property baseCP.
     *
     */
    public double getBaseStatCP() {
        return this.baseCP;
    }
    
    /** Setter for property baseCP.
     * @param baseCP New value of property baseCP.
     *
     */
    public void setBaseStatCP(double newValue) {
        this.baseCP = newValue;
    }
    

    
    /** Getter for property adjustedStatCP.
     * @return Value of property adjustedStatCP.
     *
     */
    public double getAdjustedStatCP() {
        return baseCP + adjustedCPOffset;
    }
    
    /** Setter for property adjustedStatCP.
     * @param adjustedStatCP New value of property adjustedStatCP.
     *
     */
    public void setAdjustedStatCP(double newValue) {
        adjustedCPOffset = newValue - (baseCP);
    }
    
    /** Getter for property adjustedAFStatCP.
     * @return Value of property adjustedAFStatCP.
     *
     */
    public double getAdjustedAFStatCP() {
        return baseCP + adjustedAFCPOffset;
    }
    
    /** Setter for property adjustedAFStatCP.
     * @param adjustedAFStatCP New value of property adjustedAFStatCP.
     *
     */
    public void setAdjustedAFStatCP(double newValue) {
        adjustedAFCPOffset = newValue - (baseCP);
    }
    
    /** Getter for property currentStatCP.
     * @return Value of property currentStatCP.
     *
     */
    public double getCurrentStatCP() {
        return baseCP + adjustedCPOffset + currentCPOffset;
    }
    
    /** Setter for property currentStatCP.
     * @param currentStatCP New value of property currentStatCP.
     *
     */
    public void setCurrentStatCP(double newValue) {
        currentCPOffset = newValue - (baseCP + adjustedCPOffset);
    }
    
    /** Getter for property cost.
     * @return Value of property cost.
     *
     */
    public int getCost() {
        int cost = 0;
        
        int base = getBaseStat();
        
        if ( useNormalCharacteristicMaximum && base > normalCharacteristicMaximum ) {
            cost += (normalCharacteristicMaximum - startingValue) * cpPerPoint;
            cost += (base - normalCharacteristicMaximum) * 2 * cpPerPoint;
        }
        else {
            cost += (base - startingValue) * cpPerPoint;
        }
        
        return cost;
    }
    

    /** Getter for property startingValue.
     *
     * The starting value is the value for a characteristic, if zero character points have been
     * spent on it.  This exists for Primary Stats only.  Figured Stats use a figured base
     * characteristic to determine the starting value of a stat.
     *
     * @return Value of property startingValue.
     *
     */
    public double getStartingValue() {
        return this.startingValue;
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
        this.startingValue = Math.round(startingValue);
    }
    
    /** Getter for property statCPPerPoint.
     *
     * cpPerPoint is the cost of a single point of the represented characteristic.
     * The cpPerPoint represents the base cost, ignoring normal characteristic maximum.
     *
     * @return Value of property statCPPerPoint.
     */
    public double getCpPerPoint() {
        return this.cpPerPoint;
    }
    
    /** Setter for property statCPPerPoint.
     *
     * cpPerPoint is the cost of a single point of the represented characteristic.
     * The cpPerPoint represents the base cost, ignoring normal characteristic maximum.
     *
     * @param statCPPerPoint New value of property statCPPerPoint.
     */
    public void setCpPerPoint(double cpPerPoint) {
        this.cpPerPoint = cpPerPoint;
    }
    
    /** Getter for property normalCharacteristicMaximum.
     *
     * The normalCharacteristicMaximum represents the normal characteristic maximum for this characteristic,
     * given the character this characteristic is attached to.  The normalCharacteristicMaximum is determined
     * by the type of campaign (heroic vs. superheroic) and by powers the character possesses.
     *
     * @return Value of property normalCharacteristicMaximum.
     */
    public int getNormalCharacteristicMaximum() {
        return this.normalCharacteristicMaximum;
    }
    
    /** Setter for property normalCharacteristicMaximum.
     *
     * The normalCharacteristicMaximum represents the normal characteristic maximum for this characteristic,
     * given the character this characteristic is attached to.  The normalCharacteristicMaximum is determined
     * by the type of campaign (heroic vs. superheroic) and by powers the character possesses.
     *
     * @param normalCharacteristicMaximum New value of property normalCharacteristicMaximum.
     */
    public void setNormalCharacteristicMaximum(int normalCharacteristicMaximum) {
        this.normalCharacteristicMaximum = normalCharacteristicMaximum;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append(name);
//       sb.append("[B:");
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
    
    /**
     * Getter for property useNormalCharacteristicMaximum.
     * @return Value of property useNormalCharacteristicMaximum.
     */
    public boolean isUseNormalCharacteristicMaximum() {
        return useNormalCharacteristicMaximum;
    }
    
    /**
     * Setter for property useNormalCharacteristicMaximum.
     * @param useNormalCharacteristicMaximum New value of property useNormalCharacteristicMaximum.
     */
    public void setUseNormalCharacteristicMaximum(boolean useNormalCharacteristicMaximum) {
        this.useNormalCharacteristicMaximum = useNormalCharacteristicMaximum;
    }
    
}
