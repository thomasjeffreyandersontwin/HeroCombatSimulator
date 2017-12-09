/*
 * ATSingleTargetTree.java
 *
 * Created on June 8, 2004, 10:34 PM
 */

package champions.abilityTree2;

import champions.filters.NameAbilityFilter;
import champions.filters.NameTargetFilter;
import champions.filters.OrFilter;
import champions.filters.RosterNameTargetFilter;
import treeTable.TreeTableFilterEvent;

/**
 *
 * @author  1425
 */
public class ATAllTargetsTree extends ATTree {
    
    /**
     * Creates a new instance of ATSingleTargetTree
     */
    public ATAllTargetsTree() {
        setRowHeight(0);
        setLargeModel(false);
        
    }
    
    protected void setupModel() {
        ATNode root = new ATAllTargetsNodeFactory().createRostersNode(null, true);
        ATModel model = new ATAllTargetsModel(root, getTitle());
        setTreeTableModel(model);
        root.setTree(this);
    }

    public void filter(TreeTableFilterEvent event) {
        Object f = event.getFilterObject();
        
        if (f instanceof String) {
            if (((String) f).equals("")  || ((String) f).equals(" ") ) {
                setPopupFilter(null);
            }
            else {
                setPopupFilter( 
                        new OrFilter<Object>(
                            new ATTargetFilter( new NameTargetFilter((String) f) ),
                            new ATTargetFilter( new RosterNameTargetFilter((String) f))
                        )
               );
            }
        }
        else {
            setPopupFilter( null ) ;
        }
    }
    

    
    
}
