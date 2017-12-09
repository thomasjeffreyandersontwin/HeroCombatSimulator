/*
 * AdjAttackDestinationAbilityNode.java
 *
 * Created on March 6, 2002, 11:07 PM
 */

package champions.adjustmentPowers.adjustmentTree;

import champions.*;
import champions.Power;
import champions.interfaces.*;
import champions.powers.*;
import champions.parameterEditor.*;

import treeTable.*;
/**
 *
 * @author  Trevor Walker
 * @version 
 */
public class AdjAttackDestinationAbilityNode extends DefaultTreeTableNode {

    static private PADIntegerEditor integerEditor;
    static private PADIntegerEditor integerRenderer;
            /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property targetAbility. */
    private Ability targetAbility;
    
    /** Holds value of property adjustmentList. */
    private AdjustmentList adjustmentList;
    
    /** Holds value of property sourceAbility. */
    private Ability sourceAbility;
    
    /** Creates new AdjAttackDestinationAbilityNode */
    public AdjAttackDestinationAbilityNode(Ability sourceAbility, Ability targetAbility, Target target, int percentage, AdjustmentList adjustmentList) {
        setSourceAbility(sourceAbility);
        setTargetAbility(targetAbility);
        setTarget(target);
        setAdjustmentList(adjustmentList);
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
    
        /** Getter for property targetAbility.
     * @return Value of property targetAbility.
     */
    public Ability getTargetAbility() {
        return targetAbility;
    }
    
    /** Setter for property targetAbility.
     * @param targetAbility New value of property targetAbility.
     */
    public void setTargetAbility(Ability targetAbility) {
        this.targetAbility = targetAbility;
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
    
    public Object getValueAt(int column) {
        switch ( column ) {
            case 0:
                return targetAbility.getName();
            case 1:
                FadeTracker ft = target.getFadeTracker( targetAbility );
                if ( ft != null ) {
                    return Integer.toString( (int)ft.getAidAdjustment() );
                }
                return "0";
            case 2:
                int max;
                Power p = sourceAbility.getPower();
                if ( p instanceof powerAid ) {
                    max = ((powerAid)p).getMaximumAid(sourceAbility);
                    return Integer.toString(max);
                }
                if ( p instanceof powerAbsorption ) {
                    max = ((powerAbsorption)p).getMaximumAdjustment(sourceAbility);
                    return Integer.toString(max);
                }
                return null;
            case 3:
                int aindex = adjustmentList.getAdjustableIndex( targetAbility );
                return new Integer(adjustmentList.getAdjustablePercentage(aindex));
        }
        return null;
    }
    
  /*  public TreeTableCellRenderer getTreeTableCellRenderer(int column) {
        if (column == 3) {
            if ( integerParameterEditor == null ) integerParameterEditor = new PADIntegerEditor(;
            return integerParameterEditor;
        }
        return null;
    }
    
    public TreeTableCellEditor getTreeTableCellEditor(int column) {
        if (column == 3) {
            if ( integerParameterEditor == null ) integerParameterEditor = new IntegerParameterEditor(null, null);
            return integerParameterEditor;
        }
        return null;
    } */

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
    
}
