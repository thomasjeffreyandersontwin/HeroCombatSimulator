/*
 * PADAbilitiesNode.java
 *
 * Created on February 27, 2002, 11:36 PM
 */

package champions.adjustmentPowers.adjustmentTree;

import treeTable.*;
import champions.*;

import java.util.Iterator;
/**
 *
 * @author  twalker
 * @version 
 */
public class AdjPowersNode extends DefaultTreeTableNode {

    protected boolean initialized = false;
    /** Creates new PADAbilitiesNode */
    public AdjPowersNode() {
        setUserObject( "Power Types" );
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
        
        Iterator i = PADRoster.getAbilityIterator();
        while ( i.hasNext() ) {
            this.add( new AdjPowerNode((String)i.next()) );
        }
    }   
    
    public int getColumnSpan(int column, TreeTableColumnModel columnModel) {
        if ( column == 0 ) return 2;
        return 1;
    }

}
