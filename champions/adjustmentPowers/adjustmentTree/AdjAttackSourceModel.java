/*
 * AdjAttackSourceModel.java
 *
 * Created on March 6, 2002, 7:36 PM
 */

package champions.adjustmentPowers.adjustmentTree;

import treeTable.*;

import champions.*;
import champions.adjustmentPowers.AdjustmentPower;

import java.util.ArrayList;
/**
 *
 * @author  Trevor Walker
 * @version 
 */
public class AdjAttackSourceModel extends DefaultTreeTableModel {

    /** Holds value of property modelType. */
    private int modelType;
    
    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property sourceAbility. */
    private Ability sourceAbility;
    
    /** Holds value of property adjustables. */
    private ArrayList adjustables;
    
    /** Creates new AdjAttackSourceModel */
    public AdjAttackSourceModel(Ability sourceAbility, Target target, int modelType, ArrayList adjustables) {
        super();
        
        setSourceAbility(sourceAbility);
        setTarget(target);
        setModelType(modelType);
        setAdjustables(adjustables);
        
        buildModel();
    }
    
    private void buildModel() {
        DefaultTreeTableNode newRoot = new DefaultTreeTableNode();
        
        newRoot.add( new AdjAttackSourceAbilitiesNode(sourceAbility, target, adjustables));
        newRoot.add( new AdjAttackSourceStatsNode(sourceAbility, target, adjustables));
        
        setRoot(newRoot);
    }
    
    /**
     * Returns the number ofs availible column.
     */
    public int getColumnCount() {
        return 2;
    }
    
    /**
     * Returns the name for column number <code>column</code>.
     */
    public String getColumnName(int column) {
        switch ( column ) {
            case 0:
                return "Adjustable";
            case 1:
                return "Dynamic";
        }
        return null;
    }
    
    /** Getter for property modelType.
     * @return Value of property modelType.
     */
    public int getModelType() {
        return modelType;
    }
    
    /** Setter for property modelType.
     * @param modelType New value of property modelType.
     */
    public void setModelType(int modelType) {
        this.modelType = modelType;
    }
    
    /** Getter for property level.
     * @return Value of property level.
     */
    public int getLevel() {
        AdjustmentPower power = (AdjustmentPower)sourceAbility.getPower();
        return power.getAdjustmentLevel(sourceAbility);
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
    
    /** Getter for property adjustables.
     * @return Value of property adjustables.
     */
    public ArrayList getAdjustables() {
        return adjustables;
    }
    
    /** Setter for property adjustables.
     * @param adjustables New value of property adjustables.
     */
    public void setAdjustables(ArrayList adjustables) {
        this.adjustables = adjustables;
    }
    
}
