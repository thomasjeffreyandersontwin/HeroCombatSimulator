/*
 * PADAbilitiesNode.java
 *
 * Created on February 27, 2002, 11:36 PM
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
public class AdjAbilitiesNode extends DefaultTreeTableNode {

    protected boolean initialized = false;
    
    /** Holds value of property source. */
    private Target source;
    
    /** Creates new PADAbilitiesNode */
    public AdjAbilitiesNode(Target source) {
        setSource(source);
        setUserObject( "Abilities" );
        setAllowsChildren(true);
    }
    
    public boolean isLeaf() {
        return false;
    }
    
    public int getChildCount() {
        if ( initialized == false ) initializeNodes();
        return super.getChildCount();
    }
    
    protected void initializeNodes() {
        initialized = true;
        
        AbilityIterator i = source.getAbilities();
        while ( i.hasNext() ) {
            Ability a = i.nextAbility();
            this.add( new AdjAbilityNode(a) );
        }
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
    
    public void destroy() {
        super.destroy();
        setSource(null);
    }
    
    public int getColumnSpan(int column, TreeTableColumnModel columnModel) {
        if ( column == 0 ) return 2;
        return 1;
    }
}
