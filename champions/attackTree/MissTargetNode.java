/*
 * MissTargetNode.java
 *
 * Created on January 21, 2002, 12:36 PM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.Target;
import champions.interfaces.ChampionsConstants;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class MissTargetNode extends DefaultAttackTreeNode
implements ChampionsConstants {
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber = -1;
    
    /** Creates new MissTargetNode */
    public MissTargetNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.summaryIcon");
        
        setVisible(false);
    }
    
    /**
     * Activates the node.
     *
     * The method is called when this node is being activated.  Based on
     * the information the node current has available, it can choose to
     * accept the activation or reject it.
     *
     * If the node rejects the activation, the model will call the appropriate
     * methods to advance out of the node.
     *
     * The manualOverride boolean indicates that the user click on this node
     * specifically.  Even if this node has all the information to continue processing
     * without user input, it should accept activation so the user can make changes.
     *
     * This method should be overriden by children.
     */
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        prepareBattleEvent();
        
        
     /*   if ( manualOverride || nodeRequiresInput() ) {
            String explination = buildExplination();
            
            InformationPanel ip = InformationPanel.getDefaultPanel( explination );
            inline.showInputPanel(this,ip);
            inline.setInstructions("Hit Okay to Continue...");
            
            acceptActivation = true;
        } */
        //  }
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
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
        // Compare the activeChild to the possible children, to determine what comes next.
        if ( activeChild != null && activeChild.getName() == null ) {
            // This is probably an error
            int index = children.indexOf(activeChild);
            System.out.println("Null Node name for child " + Integer.toString(index) + " of Parent " + this);
            nextNode = null;
        }
        else {
            String previousNodeName = (activeChild == null ) ? null : activeChild.getName();
            String nextNodeName = nextNodeName(previousNodeName);
            if ( nextNodeName != null ) {
                if ( nextNodeName.equals("Select Reflection Target") ) {
                    // Build the very first node: Attack Param
                    SingleTargetNode node = new SingleTargetNode("Select Reflection Target");
                    node.setTargetReferenceNumber(targetReferenceNumber);
                    node.setMode(SECONDARY_TARGET);
                    //node.setTargetGroupSuffix("");
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        
        if ( previousNodeName == null ) {
            nextNodeName = "Select Reflection Target";
        }
        
        return nextNodeName;
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
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println( "Node " + name + " exited.");
        return true;
    }
    
    /** Determines if this node needs to show an input panel.
     *
     */
    protected  boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        return requiresInput && getAutoBypassTarget().getBooleanProfileOption(getAutoBypassOption());
    }
    
    /** Getter for property targetReferenceNumber.
     * @return Value of property targetReferenceNumber.
     */
    public int getTargetReferenceNumber() {
        return targetReferenceNumber;
    }
    
    /** Setter for property targetReferenceNumber.
     * @param targetReferenceNumber New value of property targetReferenceNumber.
     */
    public void setTargetReferenceNumber(int targetReferenceNumber) {
        this.targetReferenceNumber = targetReferenceNumber;
    }
    
    public String getAutoBypassOption() {
        return "SHOW_MISSED_INFO_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
    }
    
    private void prepareBattleEvent() {
        ActivationInfo ai = battleEvent.getActivationInfo();
        String targetGroup = getTargetGroup();
        if ( targetReferenceNumber == -1 ) {
            // Initialize a Target here
            targetReferenceNumber = ai.getNextTargetReferenceNumber( targetGroup);
        }
        
        int tindex = ai.addTarget(null, targetGroup, targetReferenceNumber);
       // AddTargetUndoable u = new AddTargetUndoable(battleEvent, null, targetReferenceNumber, targetGroup);
       // battleEvent.addUndoableEvent(u);
        ai.setTargetSecondary(tindex, true);
             /*   String explination;
                //Target target = ai.getTarget(tindex);
                
                explination = "The target was hit automatically by " + battleEvent.getSource().getName() + ".\n\n"
                + "This was a secondary target of an attack.  Secondary targets are always automatically hit. "
                + "If you desired this target not to be hit, remove it from the Select Secondary Target node.";
                
                String reason = "Secondary Target hit automatically.";
                
                ai.setTargetHitOverride(tindex, true, reason, explination); */
    }
    
    private String buildExplination() {
        String explination;
        
        explination = "The previous attack failed to hit it's target.  Although there are no Hero System rules to handle "
        + "missed targets of this kind, it is possible that the attack hit a secondary target accidentally.\n\n"
        + "If it is determined that the attack did hit a different target, select the secondary target below. "
        + "Secondary targets are always hit automatically.";
        
        return explination;
    }
    
    public void clearSecondaryTarget() {
        battleEvent.getActivationInfo().removeTarget(targetReferenceNumber, getTargetGroup());
    }
    
}
