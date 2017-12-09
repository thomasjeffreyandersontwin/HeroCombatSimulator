/*
 * AttackParametersNode.java
 *
 * Created on October 30, 2001, 12:14 PM
 */

package champions.attackTree;

import champions.BattleEngine;
import champions.SweepBattleEvent;
import champions.Target;

import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author  twalker
 * @version 
 */
public class SweepSetupNode extends DefaultAttackTreeNode implements ChangeListener {

    /**
     * Stores Variable indicating this is the first time this node has been selected.
     * 
     * If it is the first time, it will always show the attack parameters
     */
    private boolean firstPass = true;
    
    /**
     * Indicates a change occurred in the attackParametersPanel while this node was 
     * activated.
     */
    private boolean changeOccurred = false;
    
    /** Creates new TestAttackTreeNode */
    public SweepSetupNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.linkedSetupIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        if ( nodeRequiresInput() || manualOverride == true ) {
            SweepBattleEvent sbe = (SweepBattleEvent)getBattleEvent();
            
            SweepSetupPanel app = SweepSetupPanel.getSweepSetupPanel( sbe );
            app.addChangeListener(this);
        
            attackTreePanel.showInputPanel(this,app);
            attackTreePanel.setInstructions("Configure which linked powers to fire...");
        
            changeOccurred = false;
            
            acceptActivation = true;
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        requiresInput = firstPass;// && getAutoBypassTarget().getBooleanProfileOption( getAutoBypassOption());
        
        return requiresInput;
    }
    
    /**
     * Builds the next child based upon the last child which was constru || node.isVisiblected.
     *
     * If all the children necessary have been built, return null, otherwise
     * return the next node.
     *
     * BuildNextChild should add that child to the tree.
     */
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        // This node build everything upfront, when it's target is first set.
        return null;
    }
    
    /**
     * Causes the node to verify the layout of it's children and fix any discrepencies.
     *
     * checkNodes should guarantee that the current child structure is complete and
     * all nodes which should exist do.
     *
     * The default implementation calls the nextNodeName method to check that the nodes
     * are arranged properly.  If they are not, it deletes all nodes that aren't properly
     * arranged.
     */
    public void checkNodes() {
        // Don't do anything when you check the nodes.  The children are built when this
        // node is first activated.
    }
    
    /** 
     * Causes node to process an advance and perform any work that it needs to do.
     *
     * This is a method introduced by DefaultAttackTreeNode.  DefaultAttackTreeNode
     * delegates to this method if advanceNode node is called and this is the active
     * node.  This method should do all the work of advanceNode whenever it can, since
     * the next node processing and buildNextChild methods depend on the existing 
     * DefaultAttackTreeNode advanceNode method.
     *
     * Returns true if it is okay to leave the node, false if the node should
     * be reactivated to gather more information.
     */
    public boolean processAdvance() {
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " exited.");
        firstPass = false;
       
        if ( changeOccurred == true ) {
            BattleEngine.battleParametersChanged(battleEvent);
        } 
        
        return true;
    }
    
    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e  a ChangeEvent object
     */
    public void stateChanged(ChangeEvent e) {
        changeOccurred = true;
    }
    
    public String getAutoBypassOption() {
        return "SHOW_LINKED_SETUP_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return getBattleEvent().getSource();
    }
    

}
