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
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.image.*;

import treeTable.*;
/**
 *
 * @author  Trevor Walker
 * @version
 */
public class AdjAttackDestinationStatNode extends DefaultTreeTableNode {
    
    static private IntegerParameterEditor integerParameterEditor;
    
    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property stat. */
    private Characteristic stat;
    
    /** Holds value of property adjustmentList. */
    private AdjustmentList adjustmentList;
    
    /** Holds value of property sourceAbility. */
    private Ability sourceAbility;
    
    private Icon statIcon;
    
    
    /** Creates new AdjAttackDestinationAbilityNode */
    public AdjAttackDestinationStatNode(Ability sourceAbility, Characteristic stat, Target target, int percentage, AdjustmentList adjustmentList) {
        setSourceAbility(sourceAbility);
        setStat(stat);
        setTarget(target);
        setAdjustmentList(adjustmentList);
        
        statIcon = UIManager.getIcon("Stat.DefaultIcon");
        if ( statIcon != null ) setIcon(statIcon);
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
    
    /** Getter for property stat.
     * @return Value of property stat.
     */
    public Characteristic getStat() {
        return stat;
    }
    
    /** Setter for property stat.
     * @param stat New value of property stat.
     */
    public void setStat(Characteristic stat) {
        this.stat = stat;
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
                return stat.getName();
            case 1:
                FadeTracker ft = target.getFadeTracker( stat );
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
                int aindex = adjustmentList.getAdjustableIndex( stat );
                return new Integer(adjustmentList.getAdjustablePercentage(aindex));
        }
        return null;
    }
    
   /* public TreeTableCellRenderer getTreeTableCellRenderer(int column) {
        if (column == 3) {
            if ( integerParameterEditor == null ) integerParameterEditor = new IntegerParameterEditor(null, null);
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
