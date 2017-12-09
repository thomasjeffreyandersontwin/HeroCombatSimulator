/*
 * PADTreeModel.java
 *
 * Created on February 27, 2002, 11:11 PM
 */

package champions.adjustmentPowers.adjustmentTree;

import treeTable.*;
import champions.*;
import champions.interfaces.*;
/**
 *
 * @author  twalker
 * @version
 */
public class AdjConfigSourceModel extends DefaultTreeTableModel implements ChampionsConstants {
    
    /** Holds value of property modelType. */
    private int modelType;
    
    /** Holds value of property level. */
    private int level;
    
    /** Holds value of property source. */
    private Target source;
    
    /** Creates new PADTreeModel */
    public AdjConfigSourceModel(int modelType, int level, Target source) {
        super();
        
        setModelType(modelType);
        setLevel(level);
        setSource(source);
        
        buildModel();
    }
    
    protected void buildModel() {
        DefaultTreeTableNode newRoot = new DefaultTreeTableNode();
        
        switch ( modelType ) {
            case ADJ_CONFIG_DRAIN_FROM:
                if ( level == ADJ_SINGLE_ADJUSTMENT ) newRoot.add( new AdjPowersNode() );
                if ( level != ADJ_SINGLE_ADJUSTMENT ) newRoot.add( new AdjSpecialEffectsNode() );
                newRoot.add( new AdjStatsNode() );
                break;
            case ADJ_CONFIG_AID_TO:
                if ( level == ADJ_SINGLE_ADJUSTMENT ) newRoot.add( new AdjPowersNode() );
                if ( level != ADJ_SINGLE_ADJUSTMENT ) newRoot.add( new AdjSpecialEffectsNode() );
                newRoot.add( new AdjStatsNode() );
                break;
            case ADJ_CONFIG_TRANSFER_FROM:
                if ( level == ADJ_SINGLE_ADJUSTMENT ) newRoot.add( new AdjPowersNode() );
                if ( level != ADJ_SINGLE_ADJUSTMENT ) newRoot.add( new AdjSpecialEffectsNode() );
                newRoot.add( new AdjStatsNode() );
                break;
            case ADJ_CONFIG_TRANSFER_TO:
                if ( level == ADJ_SINGLE_ADJUSTMENT ) newRoot.add( new AdjAbilitiesNode(source) );
                if ( level != ADJ_SINGLE_ADJUSTMENT ) newRoot.add( new AdjSpecialEffectsNode() );
                newRoot.add( new AdjStatsNode() );
                break;
            case ADJ_CONFIG_ABSORB_TO:
                if ( level == ADJ_SINGLE_ADJUSTMENT ) newRoot.add( new AdjAbilitiesNode(source) );
                if ( level != ADJ_SINGLE_ADJUSTMENT ) newRoot.add( new AdjSpecialEffectsNode() );
                newRoot.add( new AdjStatsNode() );
                break;
        }
        
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
        return level;
    }
    
    /** Setter for property level.
     * @param level New value of property level.
     */
    public void setLevel(int level) {
        this.level = level;
    }
    
    /** Getter for property source.
     * @return Value of property source.
     */
    public Target getSource() {
        return source;
    }
    
    /** Setter for property source.
     * @param source New value of property source.
     */
    public void setSource(Target source) {
        this.source = source;
    }
    
}
