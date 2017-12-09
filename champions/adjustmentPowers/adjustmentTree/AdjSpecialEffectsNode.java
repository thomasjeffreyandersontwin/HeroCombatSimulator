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
public class AdjSpecialEffectsNode extends DefaultTreeTableNode {

    protected boolean initialized = false;
    /** Creates new PADAbilitiesNode */
    public AdjSpecialEffectsNode() {
        setUserObject( "Special Effects" );
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
        
        int index;
        
        Iterator i = PADRoster.getSpecialEffectIterator();
        while ( i.hasNext() ) {
            this.add( new AdjSpecialEffectNode((String)i.next()) );
        }
    }
    
    public int getColumnSpan(int column, TreeTableColumnModel columnModel) {
        if ( column == 0 ) return 2;
        return 1;
    }

}
