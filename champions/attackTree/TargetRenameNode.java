/*
 * KnockbackEffectNode.java
 *
 * Created on November 8, 2001, 12:03 PM
 */

package champions.attackTree;

import champions.*;
import champions.enums.KnockbackEffect;
import champions.exception.BadDiceException;

import champions.interfaces.AbilityIterator;
import champions.interfaces.IndexIterator;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class TargetRenameNode extends DefaultAttackTreeNode {
    
    
    /** Holds value of property target. */
    private Target target;
    
    private boolean firstPass = true;
    
    /** Creates new KnockbackEffectNode */
    public TargetRenameNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.genericConfigIcon");
        setVisible(true);
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;

        if ( manualOverride || nodeRequiresInput() ) {
            TargetRenamePanel ep = TargetRenamePanel.getDefaultPanel(battleEvent, target);
            
            attackTreePanel.showInputPanel(this,ep);
            attackTreePanel.setInstructions( "Enter the target information...");
            
            acceptActivation = true;
        }
        
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        return firstPass;
    }
    
    public boolean processAdvance() {
        firstPass = false;
        
        
        
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " exited.");
        return true;
    }
  
    /**
     * Builds the next child based upon the last child which was constructed.
     *
     * If all the children necessary have been built, return null, otherwise
     * return the next node.
     *
     * BuildNextChild should add that child to the tree.
     */
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        
        AttackTreeNode nextNode = null;
        
        // AttackParameter has no children...
        
        return nextNode;
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
    
    
    public String getAutoBypassOption() {
        return "SHOW_TARGET_RENAME_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return getTarget();
    } 
}
