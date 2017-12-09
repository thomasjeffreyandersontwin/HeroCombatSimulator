/*
 * AdjAttackDestinationModel.java
 *
 * Created on March 6, 2002, 10:54 PM
 */

package champions.adjustmentPowers.adjustmentTree;

import champions.*;
import champions.interfaces.*;

import treeTable.*;
/**
 *
 * @author  Trevor Walker
 * @version 
 */
public class AdjAttackDestinationModel extends DefaultTreeTableModel
implements ChampionsConstants {

    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property sourceAbility. */
    private Ability sourceAbility;
    
    /** Holds value of property adjustmentList. */
    private AdjustmentList adjustmentList;
    
    /** Holds value of property adjustmentType. */
    private int adjustmentType;
    
    /** Creates new AdjAttackDestinationModel */
    public AdjAttackDestinationModel(Ability sourceAbility, Target target, int adjustmentType, AdjustmentList adjustmentList) {
        setSourceAbility(sourceAbility);
        setTarget(target);
        setAdjustmentType(adjustmentType);
        setAdjustmentList(adjustmentList);
    
        buildModel();
    }
    
    private void buildModel() {
        DefaultTreeTableNode newRoot = new AdjAttackDestinationsNode(sourceAbility, target, adjustmentType, adjustmentList);
        
        setRoot(newRoot);
        newRoot.setModel(this);
    }
    
        /**
     * Returns the number ofs availible column.
     */
    public int getColumnCount() {
        switch ( adjustmentType ) {
            case ADJ_CONFIG_ABSORB_TO:
                return 4;
            case ADJ_CONFIG_AID_TO:
                return 3;
            case ADJ_CONFIG_DRAIN_FROM:
                return 1;
            case ADJ_CONFIG_TRANSFER_FROM:
                return 1;
            case ADJ_CONFIG_TRANSFER_TO:
                return 2;
        }
        return -1;
    }
    
    /**
     * Returns the name for column number <code>column</code>.
     */
    public String getColumnName(int column) {
        switch ( adjustmentType ) {
            case ADJ_CONFIG_ABSORB_TO:
                return getAbsorbToColumnName(column);
            case ADJ_CONFIG_AID_TO:
                return getAidToColumnName(column);
            case ADJ_CONFIG_DRAIN_FROM:
                return getDrainFromColumnName(column);
            case ADJ_CONFIG_TRANSFER_FROM:
                return getTransferFromColumnName(column);
            case ADJ_CONFIG_TRANSFER_TO:
                return getTransferToColumnName(column);
        }
        return null;
    }
    
    private String getAbsorbToColumnName(int column) {
        switch ( column ) {
            case 0:
                return "Adjustable";
            case 1:
                return "AMT";
            case 2:
                return "MAX";
            case 3:
                return "%";
        }
        return null;
    }
    
    private String getAidToColumnName(int column) {
        switch ( column ) {
            case 0:
                return "Adjustable";
            case 1:
                return "AMT";
            case 2:
                return "MAX";
        }
        return null;
    }
    
    private String getDrainFromColumnName(int column) {
        switch ( column ) {
            case 0:
                return "Adjustable";
        }
        return null;
    }
    
    private String getTransferFromColumnName(int column) {
        switch ( column ) {
            case 0:
                return "Adjustable";
        }
        return null;
    }
    
    private String getTransferToColumnName(int column) {
        switch ( column ) {
            case 0:
                return "Adjustable";
            case 1:
                return "AMT";
            case 2:
                return "MAX";
        }
        return null;
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
    
    /** Getter for property sourceAbility.
     * @return Value of property sourceAbility.
     */
    public Ability getSourceAbility() {
        return sourceAbility;
    }
    
    /** Setter for property sourceAbility.
     * @param sourceAbility New value of property sourceAbility.
     */
    public void setSourceAbility(Ability sourceAbility) {
        this.sourceAbility = sourceAbility;
    }
    
    /** Getter for property adjustmentList.
     * @return Value of property adjustmentList.
     */
    public AdjustmentList getAdjustmentList() {
        return adjustmentList;
    }
    
    /** Setter for property adjustmentList.
     * @param adjustmentList New value of property adjustmentList.
     */
    public void setAdjustmentList(AdjustmentList adjustmentList) {
        this.adjustmentList = adjustmentList;
    }
    
    /** Getter for property adjustmentType.
     * @return Value of property adjustmentType.
     */
    public int getAdjustmentType() {
        return adjustmentType;
    }
    
    /** Setter for property adjustmentType.
     * @param adjustmentType New value of property adjustmentType.
     */
    public void setAdjustmentType(int adjustmentType) {
        this.adjustmentType = adjustmentType;
    }
    
}
