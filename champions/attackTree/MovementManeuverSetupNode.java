/*
 * MovementDistanceNode.java
 *
 * Created on May 1, 2002, 7:08 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.ActivationInfo;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.Target;
import champions.powers.maneuverMoveThrough;
import javax.swing.UIManager;

/**
 *
 * @author  Trevor Walker
 * @version 
 */
public class MovementManeuverSetupNode extends DefaultAttackTreeNode{

    boolean firstPass = true;
    /** Creates new MovementDistanceNode */
    public MovementManeuverSetupNode(String nodeName) {
        this.name = nodeName;
        icon = UIManager.getIcon("AttackTree.runningIcon");
    }

    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        if ( manualOverride || nodeRequiresInput() ) {
            acceptActivation = true;
            
            Target source = battleEvent.getSource();
            boolean fullPhase = source.hasFullPhase();
            
            attackTreePanel.setInstructions( "Set the Distance and Velocity of the Movement...");
            attackTreePanel.showInputPanel(this, MovementManeuverSetupPanel.getDefaultPanel(battleEvent, fullPhase));
        }
        
        firstPass = false;
        
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        requiresInput = (maneuverMoveThrough.getMovementAbility(battleEvent) == null || 
                        maneuverMoveThrough.isMovementDistanceSet(battleEvent) == false ||
                        maneuverMoveThrough.isVelocitySet(battleEvent) == false );
        
        return requiresInput || (firstPass && getAutoBypassTarget().getBooleanProfileOption( getAutoBypassOption()));
    }
    
    public boolean processAdvance() {
        boolean advance = true;
        
        advance = (maneuverMoveThrough.getMovementAbility(battleEvent) != null && 
                        maneuverMoveThrough.isMovementDistanceSet(battleEvent)  &&
                        maneuverMoveThrough.isVelocitySet(battleEvent) );
        
        return advance;
    }
    
    public String getAutoBypassOption() {
        return "SHOW_MOVEMENT_DISTANCE_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
    }
    
    
    
    public static int getMaximumMoveDistance(BattleEvent battleEvent) {
        Target source = battleEvent.getSource();
        
        Ability movementAbility = maneuverMoveThrough.getMovementAbility(battleEvent);
        if ( movementAbility != null ) {
            boolean fullPhase = source.hasFullPhase();
            if ( fullPhase ) {
                return movementAbility.getRange();
            } else {
                return ChampionsUtilities.roundValue((double)movementAbility.getRange()/2.0,true);
            }
        }
        else {
            return 0;
        }
    }
}
