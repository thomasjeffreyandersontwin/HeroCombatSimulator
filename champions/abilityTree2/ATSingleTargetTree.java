/*
 * ATSingleTargetTree.java
 *
 * Created on June 8, 2004, 10:34 PM
 */

package champions.abilityTree2;

import champions.Target;
import javax.swing.tree.TreePath;

/**
 *
 * @author  1425
 */
public class ATSingleTargetTree extends ATAbilityListTree {
    
    /** Holds the Target of this tree. */
    private Target target;
//    
//    /** Holds the Sublist name to show. */
//    private String sublistName;
    
    /**
     * Creates a new instance of ATSingleTargetTree
     */
    public ATSingleTargetTree() {
    	
    }
    
    protected void setupModel() {
        ATNode root = new ATSingleTargetNodeFactory().createTargetNode(target, false, getNodeFilter(), true);
        ATModel model = new ATSingleTargetModel(root, getTitle());
        setTreeTableModel(model);
        root.setTree(this);
    }
    
    /**
     * Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
     
    /**
     * Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        if ( this.target != target  ) {
            
            this.target = target;
        
            Object root = getBaseTreeTableModel().getRoot();
            if ( root instanceof ATTargetNode ) {
                ((ATTargetNode)root).setTarget(target);
            }
//            ATNode root = new ATSingleTargetNodeFactory().createTargetNode(target, false, getNodeFilter(), true);
//            ATModel model = new ATSingleTargetModel(root, getTitle());
//            setTreeTableModel(model);
//            root.setTree(this);
        }
    }
    
    public void updateActions() {
        Object root = getProxyTreeTableModel().getRoot();
        if ( root instanceof ATTargetNode ) {
            ATTargetNode r = (ATTargetNode)root;
            r.updateTargetActions();
            
            if ( r.getChildCount() != 0 && r.getChildAt(0) instanceof ATTargetActionsNode ) {
                expandAll( new TreePath( ((ATTargetActionsNode)r.getChildAt(0)).getPath()));
            }
        }
    }
//    
//    /**
//     * Getter for property sublistName.
//     * @return Value of property sublistName.
//     */
//    public java.lang.String getSublistName() {
//        return sublistName;
//    }
//    
//    /**
//     * Setter for property sublistName.
//     * @param sublistName New value of property sublistName.
//     */
//    public void setSublistName(java.lang.String sublistName) {
//        if (this.sublistName != sublistName ) {
//            this.sublistName = sublistName;
//            
//            Object root = getProxyTreeTableModel().getRoot();
//            if ( root instanceof ATTargetNode2 ) {
//                ((ATTargetNode2)root).setSublistName(sublistName);
//            }
//        }
//    }
    
    
    
}
