/*
 * PADAbilitiesNode.java
 *
 * Created on February 27, 2002, 11:36 PM
 */

package champions.adjustmentPowers.adjustmentTree;

import treeTable.*;
import champions.*;
/**
 *
 * @author  twalker
 * @version 
 */
public class AdjStatsNode extends DefaultTreeTableNode {

    protected boolean initialized = false;
    /** Creates new PADAbilitiesNode */
    public AdjStatsNode() {
        setUserObject( "Characteristics" );
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
        
        String[] names = champions.Character.statNames;
        int index;
        
        for ( index = 0; index < names.length; index++) {
            this.add( new AdjStatNode((String)names[index]) );
        }
    }   
    
    public int getColumnSpan(int column, TreeTableColumnModel columnModel) {
        if ( column == 0 ) return 2;
        return 1;
    }

}
