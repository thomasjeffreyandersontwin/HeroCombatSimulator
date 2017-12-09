/*
 * AdjustmentInfo.java
 *
 * Created on August 21, 2001, 7:58 PM
 */

package champions;

import champions.interfaces.Undoable;
import java.io.Serializable;


/**
 *
 * @author  twalker
 * @version
 */
public class CharacteristicChangeUndoable extends java.lang.Object implements Undoable, Serializable {
    
    /** Holds value of property initialBaseCP. */
    private double initialBaseCP;
    
    /** Holds value of property finalBaseCP. */
    private double finalBaseCP;
    
    /** Holds value of property initialAdjustedCPOffset. */
    private double initialAdjustedCPOffset;
    
    /** Holds value of property finalAdjustedCPOffset. */
    private double finalAdjustedCPOffset;
    
        /** Holds value of property initialAdjustedCPOffset. */
    private double initialAdjustedCPAFOffset;
    
    /** Holds value of property finalAdjustedCPOffset. */
    private double finalAdjustedCPAFOffset;
    
    /** Holds value of property initialCurrentCPOffset. */
    private double initialCurrentCPOffset;
    
    /** Holds value of property finalCurrentCPOffset. */
    private double finalCurrentCPOffset;
    
    /** Holds value of property amount.
     * The amount represents that actual adjustment which was made to the stat/ability.  The amount
     * is often modified by the limit.
     *
     * The amount metric depends upon the operation which generated the adjustmentInfo.  In Ability
     * and StatCP adjustments, the metric is in CPs.  For Stat adjustments, the metric is in stat podoubles.
     */
    private double amount;
    
    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property characteristic. */
    private CharacteristicPrimary characteristic;
    
    /** Creates new AdjustmentInfo */
    public CharacteristicChangeUndoable(Target target, CharacteristicPrimary characteristic ) {
        setTarget(target);
        setCharacteristic(characteristic);

        initialBaseCP = characteristic.baseCP;
        finalBaseCP = characteristic.baseCP;
        initialAdjustedCPOffset = characteristic.adjustedCPOffset;
        finalAdjustedCPOffset = characteristic.adjustedCPOffset;
        initialAdjustedCPAFOffset = characteristic.adjustedAFCPOffset;
        finalAdjustedCPAFOffset = characteristic.adjustedAFCPOffset;
        initialCurrentCPOffset = characteristic.currentCPOffset;
        finalCurrentCPOffset = characteristic.currentCPOffset;
    }

    public void setInitialValues() {
        initialBaseCP = characteristic.baseCP;
        initialAdjustedCPOffset = characteristic.adjustedCPOffset;
        initialAdjustedCPAFOffset = characteristic.adjustedAFCPOffset;
        initialCurrentCPOffset = characteristic.currentCPOffset;
    }
    
    public void setFinalValues() {
        finalBaseCP = characteristic.baseCP;
        finalAdjustedCPOffset = characteristic.adjustedCPOffset;
        finalAdjustedCPAFOffset = characteristic.adjustedAFCPOffset;
        finalCurrentCPOffset = characteristic.currentCPOffset;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("StatChangeUndoable: ");
        sb.append(target.getName());
        sb.append("'s ");
        sb.append(characteristic.getName());
        sb.append("[");
        if ( getInitialBaseCP() != getFinalBaseCP() ) {
            sb.append("Base:");
            sb.append(Double.toString(getInitialBaseCP()));
            sb.append("->");
            sb.append(Double.toString(getFinalBaseCP()));
        }
        if ( getInitialAdjustedCPOffset() != getFinalAdjustedCPOffset() ) {
            sb.append("AdjOffset:");
            sb.append(Double.toString(getInitialAdjustedCPOffset()));
            sb.append("->");
            sb.append(Double.toString(getFinalAdjustedCPOffset()));
        }
        if ( getInitialAdjustedCPAFOffset() != getFinalAdjustedCPAFOffset() ) {
            sb.append("AdjAFOffset:");
            sb.append(Double.toString(getInitialAdjustedCPAFOffset()));
            sb.append("->");
            sb.append(Double.toString(getFinalAdjustedCPAFOffset()));
        }
        if ( getInitialCurrentCPOffset() != getFinalCurrentCPOffset() ) {
            sb.append("CurrentCPOffset:");
            sb.append(Double.toString(getInitialCurrentCPOffset()));
            sb.append("->");
            sb.append(Double.toString(getFinalCurrentCPOffset()));
        }
        
        sb.append( " Amount: " );
        sb.append( Double.toString(amount));
        sb.append("]");
        return sb.toString();
    }
    
    /** Getter for property initialBaseCP.
     * @return Value of property initialBaseCP.
     */
    public double getInitialBaseCP() {
        return initialBaseCP;
    }
    
    /** Setter for property initialBaseCP.
     * @param initialBaseCP New value of property initialBaseCP.
     */
    public void setInitialBaseCP(double initialBaseCP) {
        this.initialBaseCP = initialBaseCP;
    }   
    
    /** Getter for property finalBaseCP.
     * @return Value of property finalBaseCP.
     */
    public double getFinalBaseCP() {
        return finalBaseCP;
    }
    
    /** Setter for property finalBaseCP.
     * @param finalBaseCP New value of property finalBaseCP.
     */
    public void setFinalBaseCP(double finalBaseCP) {
        this.finalBaseCP = finalBaseCP;
    }
    
    /** Getter for property initialAdjustedCPOffset.
     * @return Value of property initialAdjustedCPOffset.
     */
    public double getInitialAdjustedCPOffset() {
        return initialAdjustedCPOffset;
    }
    
    /** Setter for property initialAdjustedCPOffset.
     * @param initialAdjustedCPOffset New value of property initialAdjustedCPOffset.
     */
    public void setInitialAdjustedCPOffset(double value) {
        this.initialAdjustedCPOffset = value;
    }
    
    /** Getter for property initialAdjustedCPOffset.
     * @return Value of property initialAdjustedCPOffset.
     */
    public double getInitialAdjustedCPAFOffset() {
        return initialAdjustedCPAFOffset;
    }
    
    /** Setter for property initialAdjustedCPOffset.
     * @param initialAdjustedCPOffset New value of property initialAdjustedCPOffset.
     */
    public void setInitialAdjustedCPAFOffset(double value) {
        this.initialAdjustedCPAFOffset = value;
    }
    
        /** Getter for property initialAdjustedCPOffset.
     * @return Value of property initialAdjustedCPOffset.
     */
    public double getFinalAdjustedCPOffset() {
        return finalAdjustedCPOffset;
    }
    
    /** Setter for property finalAdjustedCPOffset.
     * @param finalAdjustedCPOffset New value of property finalAdjustedCPOffset.
     */
    public void setFinalAdjustedCPOffset(double value) {
        this.finalAdjustedCPOffset = value;
    }
    
    /** Getter for property finalAdjustedCPOffset.
     * @return Value of property finalAdjustedCPOffset.
     */
    public double getFinalAdjustedCPAFOffset() {
        return finalAdjustedCPAFOffset;
    }
    
    /** Setter for property finalAdjustedCPOffset.
     * @param finalAdjustedCPOffset New value of property finalAdjustedCPOffset.
     */
    public void setFinalAdjustedCPAFOffset(double value) {
        this.finalAdjustedCPAFOffset = value;
    }
    

    
    /** Getter for property initialCurrentCPOffset.
     * @return Value of property initialCurrentCPOffset.
     */
    public double getInitialCurrentCPOffset() {
        return initialCurrentCPOffset;
    }
    
    /** Setter for property initialCurrentCPOffset.
     * @param initialCurrentCPOffset New value of property initialCurrentCPOffset.
     */
    public void setInitialCurrentCPOffset(double initialCurrentCPOffset) {
        this.initialCurrentCPOffset = initialCurrentCPOffset;
    }
    
    /** Getter for property finalCurrentCPOffset.
     * @return Value of property finalCurrentCPOffset.
     */
    public double getFinalCurrentCPOffset() {
        return finalCurrentCPOffset;
    }
    
    /** Setter for property finalCurrentCPOffset.
     * @param finalCurrentCPOffset New value of property finalCurrentCPOffset.
     */
    public void setFinalCurrentCPOffset(double finalCurrentCPOffset) {
        this.finalCurrentCPOffset = finalCurrentCPOffset;
    }
    
    /** Getter for property amount.
     * @return Value of property amount.
     */
    public double getAmount() {
        return amount;
    }
    
    /** Setter for property amount.
     * @param amount New value of property amount.
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        this.target = target;
    }
    
    public void redo() {

        if ( getFinalBaseCP() != getInitialBaseCP() ) characteristic.baseCP = getFinalBaseCP();
        if ( getFinalAdjustedCPOffset() != getInitialAdjustedCPOffset() ) characteristic.adjustedCPOffset = getFinalAdjustedCPOffset();
        if ( getFinalAdjustedCPAFOffset() != getInitialAdjustedCPAFOffset() ) characteristic.adjustedAFCPOffset = getFinalAdjustedCPAFOffset();
        if ( getFinalCurrentCPOffset() != getInitialCurrentCPOffset() ) characteristic.currentCPOffset = getFinalCurrentCPOffset();
    }
    
    public void undo() {
        if ( getFinalBaseCP() != getInitialBaseCP() ) characteristic.baseCP = getInitialBaseCP();
        if ( getFinalAdjustedCPOffset() != getInitialAdjustedCPOffset() ) characteristic.adjustedCPOffset = getInitialAdjustedCPOffset();
        if ( getFinalAdjustedCPAFOffset() != getInitialAdjustedCPAFOffset() ) characteristic.adjustedAFCPOffset = getInitialAdjustedCPAFOffset();
        if ( getFinalCurrentCPOffset() != getInitialCurrentCPOffset() ) characteristic.currentCPOffset = getInitialCurrentCPOffset();
    }
    
    /** Getter for property characteristic.
     * @return Value of property characteristic.
     *
     */
    public CharacteristicPrimary getCharacteristic() {
        return this.characteristic;
    }
    
    /** Setter for property characteristic.
     * @param characteristic New value of property characteristic.
     *
     */
    public void setCharacteristic(CharacteristicPrimary characteristic) {
        this.characteristic = characteristic;
    }
    
}
