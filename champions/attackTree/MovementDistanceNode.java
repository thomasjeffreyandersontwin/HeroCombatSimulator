/*
 * MovementDistanceNode.java
 *
 * Created on May 1, 2002, 7:08 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.ActivationInfo;
import champions.Target;
import champions.powers.advantageUsableByOthers;

import javax.swing.UIManager;

/**
 *
 * @author  Trevor Walker
 * @version 
 */
public class MovementDistanceNode extends DefaultAttackTreeNode{

    /** Creates new MovementDistanceNode */
    public MovementDistanceNode(String nodeName) {
        this.name = nodeName;
        icon = UIManager.getIcon("AttackTree.runningIcon");
    }
    
    
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
                if ( nextNodeName.equals("Single Attack")) {
                    SingleAttackNode node = new SingleAttackNode(nextNodeName);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Movement DistanceFromCollision") ) {
                    MovementDistanceNode node = new MovementDistanceNode(nextNodeName);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Process Movement") ) {
                    ProcessMovementNode node = new ProcessMovementNode(nextNodeName);
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }
     
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        if(getBattleEvent().getAbility().findAdvantage("Usable On Others") > -1)
        if ( previousNodeName == null ) {
            nextNodeName = "Movement DistanceFromCollision";
        }
        else if ( previousNodeName.equals("Movement DistanceFromCollision") ) {
            Ability ability = battleEvent.getAbility();
            if ( ability.isRequiresTarget() ) {
                nextNodeName = "Single Attack";
            }
            else {
                nextNodeName = "Process Movement";
            }
        }
        else if ( previousNodeName.equals("Single Attack") ) {
            nextNodeName = "Process Movement";
        }
        
        return nextNodeName;
    }

    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        if(battleEvent.getAbility().isMovementPower() 
        		&& battleEvent.getAbility().findAdvantage(new advantageUsableByOthers().getName()) ==-1) {
        	manualOverride = true; 
        }
        
        if ( manualOverride || nodeRequiresInput() ) {
            acceptActivation = true;
            
            Target source = battleEvent.getSource();
            int maximumDistance = 0;
            boolean fullPhase = source.hasFullPhase();
            if ( fullPhase ) {
                maximumDistance = battleEvent.getAbility().getRange();
            }
            else {
                maximumDistance = (int)Math.ceil( (double)battleEvent.getAbility().getRange() / 2.0);
            }
            
            attackTreePanel.setInstructions( "Set the DistanceFromCollision and Velocity of the Movement...");
            attackTreePanel.showInputPanel(this, MovementDistancePanel.getDefaultPanel(battleEvent, maximumDistance, fullPhase));
        }
        
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        ActivationInfo ai = battleEvent.getActivationInfo();
        Integer i = ai.getDistanceMoved();
        
        
        if ( i == null ) {
            requiresInput = true;
        }
        
        return requiresInput && getAutoBypassTarget().getBooleanProfileOption( getAutoBypassOption());
    }
    
    public boolean processAdvance() {
        boolean advance = true;
        
        ActivationInfo ai = battleEvent.getActivationInfo();
        
        Integer i = ai.getDistanceMoved();
        if ( i == null ) {
            ai.setDistanceMoved(MOVEMENT_FULL_MOVE);
        }
        
        return advance;
    }
    
    public String getAutoBypassOption() {
        return "SHOW_MOVEMENT_DISTANCE_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
    }
}
