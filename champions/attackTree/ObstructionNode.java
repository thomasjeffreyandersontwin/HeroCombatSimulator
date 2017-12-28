/*
 * ObstructionNode.java
 *
 * Created on January 6, 2002, 12:05 PM
 */

package champions.attackTree;

import champions.*;
import javax.swing.UIManager;


/**
 *
 * @author  twalker
 * @version 
 */
public class ObstructionNode extends DefaultAttackTreeNode {

    
    public static ObstructionNode Node;

	/** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Store Target Selected for this node */
    protected Target target;
    
    /** Creates new DefensesNode */
    public ObstructionNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.obstructionIcon");
        Node = this;
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        ActivationInfo ai = getBattleEvent().getActivationInfo();
        
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
        Target target = ai.getTarget(tindex);
        
        setTarget(target);
        
        // Check if Obstruction List is already set
        ObstructionList ol = ai.getObstructionList(tindex);
        if ( ol != null ){
                for(int i = 0; i < ol.getObstructionCount(); i++) {
                    Target t = ol.getObstruction(i);
                    if ( Battle.currentBattle.getCombatants().contains( t ) == false ) {
                        Roster r = Battle.currentBattle.findRoster("Obstructions");
                        if ( r == null ) {
                            r = new Roster("Obstructions");
                            Battle.currentBattle.addRoster(r);
                            RosterDockingPanel.getDefaultRosterDockingPanel().addRoster(r);
                        }
                        r.add(t, battleEvent);
                    }
                }
            }
        
        if ( ol == null ) {
            // Create a new Obstruction List
            ol = new ObstructionList();
            
            // First add the Source Obstructions, then the target obstructions...
            ObstructionList newList = battleEvent.getSource().getObstructionList();
            
            if ( newList != null ) {
                // Clone the target list
                ol.mergeObstructions(newList);
            }
            
            // The list was null, so check to see if the target has one
            newList = target.getObstructionList();
            
            if ( newList != null ) {
                // Clone the target list
                ol.mergeObstructions(newList);
            }
        }
       
        // Add the ObstructionList to the ActivationInfo
        ai.setObstructionList(tindex, ol);
          
        if ( nodeRequiresInput() || manualOverride ) {
        
           ObstructionPanel op = ObstructionPanel.getDefaultPanel(battleEvent, targetReferenceNumber, getTargetGroup());
           if(attackTreePanel==null) {
           	attackTreePanel=AttackTreePanel.defaultAttackTreePanel;
           }
           attackTreePanel.showInputPanel(this,op);
           attackTreePanel.setInstructions("Configure Obstructions between " + battleEvent.getSource().getName() + " and " + target.getName() + ".");
          
           acceptActivation = true;
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    public boolean nodeRequiresInput() {
        return false;
    }
    
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        // This node build everything upfront, when it's target is first set.
        return null;
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

        return true;
    }
    
    public void setTarget(Target target) {
        if ( this.target != target ) {
            ActivationInfo ai = getBattleEvent().getActivationInfo();
            if ( this.target != null ) {
                // This is actually an error, since these nodes should be created for one specific target
            }
            
            this.target = target;
            
            if ( this.target != null ) {
                buildChildren();
            }
        }
    }
    
    public Target getTarget() {
        return this.target;
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
    
    public void destroy() {
        setTarget(null);
        
        super.destroy();
    }
    
    public void buildChildren() {
        // Build a ToHit node and add it
     //   ToHitNode thn = new ToHitNode("ToHit");
     //   addChild(thn);
     //   thn.setTarget(getTarget()); // Do this afterwards, since the BE would be set before the addChild.

    }
    
        
    public String getAutoBypassOption() {
        return "SHOW_OBSTRUCTION_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return getTarget();
    }

}
