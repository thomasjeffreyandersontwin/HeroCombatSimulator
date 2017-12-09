/*
 * PADTreeModel.java
 *
 * Created on February 27, 2002, 11:11 PM
 */

package champions.adjustmentPowers.adjustmentTree;

import champions.parameters.ParameterList;
import treeTable.*;
import champions.*;
import champions.adjustmentPowers.AdjustmentPower;
import champions.interfaces.*;
/**
 *
 * @author  twalker
 * @version
 */
public class AdjConfigDestinationModel extends DefaultTreeTableModel implements ChampionsConstants {
    
    /** Holds value of property modelType. */
    private int modelType;
    
    /** Holds value of property ability. */
    private Ability ability;
    
    /** Holds value of property parameterList. */
    private ParameterList parameterList;
    
    /** Holds value of property parameter. */
    private String parameter;
    
    /** Creates new PADTreeModel */
    public AdjConfigDestinationModel(Ability ability, int modelType, ParameterList parameterList, String parameter) {
        super();
        
        setModelType(modelType);
        setAbility(ability);
        setParameterList(parameterList);
        setParameter(parameter);
        
        buildModel();
    }
    
    protected void buildModel() {
        AdjConfigDestinationRoot newRoot = new AdjConfigDestinationRoot(parameterList, parameter, modelType, getAdjustmentLevel());
        
        setRoot(newRoot);
        newRoot.setModel(this);
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
    public int getAdjustmentLevel() {
        AdjustmentPower power = (AdjustmentPower)ability.getPower();
        return power.getAdjustmentLevel(ability);
    }

    
    /** Getter for property ability.
     * @return Value of property ability.
     */
    public Ability getAbility() {
        return ability;
    }
    
    /** Setter for property ability.
     * @param ability New value of property ability.
     */
    public void setAbility(Ability ability) {
        this.ability = ability;
    }
    
    /** Getter for property parameterList.
     * @return Value of property parameterList.
     */
    public ParameterList getParameterList() {
        return parameterList;
    }
    
    /** Setter for property parameterList.
     * @param parameterList New value of property parameterList.
     */
    public void setParameterList(ParameterList parameterList) {
        this.parameterList = parameterList;
    }
    
    /** Getter for property parameter.
     * @return Value of property parameter.
     */
    public String getParameter() {
        return parameter;
    }
    
    /** Setter for property parameter.
     * @param parameter New value of property parameter.
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
    
}
