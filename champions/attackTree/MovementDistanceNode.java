/*
 * MovementDistanceNode.java
 *
 * Created on May 1, 2002, 7:08 PM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.Target;
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

    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        if(battleEvent.getAbility().isMovementPower()) {
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
            
            attackTreePanel.setInstructions( "Set the Distance and Velocity of the Movement...");
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
