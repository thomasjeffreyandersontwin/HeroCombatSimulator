/*
 * AdjAttackSourceAbilitiesNode.java
 *
 * Created on March 6, 2002, 7:42 PM
 */

package champions.adjustmentPowers.adjustmentTree;

import treeTable.*;

import champions.*;
import champions.interfaces.*;

import java.util.ArrayList;
/**
 *
 * @author  Trevor Walker
 * @version 
 */
public class AdjAttackSourceAbilitiesNode extends DefaultTreeTableNode
implements ChampionsConstants {

    /** Holds value of property sourceAbility. */
    private Ability sourceAbility;
    
    /** Holds value of property target. */
    private Target target;

    /** Holds value of property adjustables. */
    private ArrayList adjustables;
    
    /** Creates new AdjAttackSourceAbilitiesNode */
    public AdjAttackSourceAbilitiesNode(Ability sourceAbility, Target target, ArrayList adjustables) {
        setUserObject( "Abilities" );
        
        setSourceAbility(sourceAbility);
        setTarget(target);
        setAdjustables(adjustables);
        
        buildNode();
    }
    
    private void buildNode() {
        if ( adjustables != null ) {
            int index, count;
            
            Object adjustable;
            
            count = adjustables.size();
            for( index = 0; index < count; index++ ) {
                adjustable = adjustables.get(index);
                if ( adjustable instanceof Ability ) {
                    add( new AdjAbilityNode( (Ability)adjustable) );
                }
            }
        }
    }

    
    public boolean isLeaf() {
        return false;
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
