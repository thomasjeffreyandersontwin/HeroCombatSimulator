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
public class KnockbackSecondaryTargetNode extends DefaultAttackTreeNode
implements ChampionsConstants {
    
    public static AttackTreeNode Node;

	/** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber = -1;
    
    private int knockbackIndex;
    
    /** Creates new MissTargetNode */
    public KnockbackSecondaryTargetNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.summaryIcon");
        
        setVisible(false);
        Node = this;
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
                if ( nextNodeName.equals("Select Knockback Target") ) {
                    // Build the very first node: Attack Param
                    SingleTargetNode node = new SingleTargetNode("Select Knockback Target");
                    node.setTargetReferenceNumber(targetReferenceNumber);
                    node.setMode(KNOCKBACK_TARGET);
                    node.setInstructions("Select Knockback target...");
                    
                    String notes = buildExplination();
                    
                    node.setAdditionalNotes(notes);
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        
        if ( previousNodeName == null ) {
            nextNodeName = "Select Knockback Target";
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
        return battleEvent.getKnockbackTarget(knockbackIndex);
    }
    
    public Target getTarget() {
        ActivationInfo ai = battleEvent.getActivationInfo();
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
        
        return (tindex != -1 ? ai.getTarget(tindex) : null);
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
        ai.setTargetKnockbackSecondary(tindex, true);
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
        
        int distance = battleEvent.getKnockbackDistance(knockbackIndex);
        Target target = battleEvent.getKnockbackTarget(knockbackIndex);
        
        explination = "<html>" + target.getName() + " was knockback back " + distance + "\".<br><br>" +
                "If " + target.getName() + " possibly collided any other characters or objects along " +
                "the knockback tragectory, select them below.<br><br>" +
                "If multiple targets were possibly encountered, additional knockback target selections will be available " +
                "after this one.</html>";
                
        
        return explination;
    }
    
    public void clearSecondaryTarget() {
        battleEvent.getActivationInfo().removeTarget(targetReferenceNumber, getTargetGroup());
    }

    public int getKnockbackIndex() {
        return knockbackIndex;
    }

    public void setKnockbackIndex(int knockbackIndex) {
        this.knockbackIndex = knockbackIndex;
    }
    
}
