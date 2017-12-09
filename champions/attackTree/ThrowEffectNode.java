/*
 * KnockbackEffectNode.java
 *
 * Created on November 8, 2001, 12:03 PM
 */

package champions.attackTree;

import champions.*;
import champions.interfaces.IndexIterator;
import javax.swing.UIManager;


/**
 *
 * @author  twalker
 * @version
 */
public class ThrowEffectNode extends DefaultAttackTreeNode {
    
    /** Holds value of property knockbackGroup. */
    private String knockbackGroup;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property ability. */
    private Ability ability;
    
    /** Holds value of property diceName. */
    private String diceName;
    
    
    private KnockbackObstructionNode secondaryTargetNode;
    
    /** Creates new KnockbackEffectNode */
    public ThrowEffectNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.knockbackEffectIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        ActivationInfo ai = battleEvent.getActivationInfo();
        String effect = ai.getStringValue("ActivationInfo.THROWEFFECT");
        
        if ( effect == null ) {
            if ( isCollision() ) {
                ai.add("ActivationInfo.THROWEFFECT", "COLLISION", true);
            }
            else {
               ai.add("ActivationInfo.THROWEFFECT", "NOCOLLISION", true);
            }
        }
        
        if ( manualOverride || nodeRequiresInput() ) {
            acceptActivation = true;
            
            attackTreePanel.setInstructions( "Choose the result of the throw...");
            attackTreePanel.showInputPanel(this, ThrowEffectPanel.getDefaultPanel(battleEvent));
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        
        return requiresInput && getAutoBypassTarget().getBooleanProfileOption( getAutoBypassOption());
    }
    
    public boolean processAdvance() {
        boolean advance = true;

        ActivationInfo ai = battleEvent.getActivationInfo();

        
        return advance;
    }
    
    public String getAutoBypassOption() {
        return "SHOW_THROW_EFFECT_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
    }

    
    /** This method will look to see if an collision likely occurred.
     *
     * Collision occurs if:
     *   .ATTACK group has a hit target that isn't a hex
     *   .ATTACK group has a hit target that was a hex, but had obstructions
     *   .ATTACK ground was an adjacent hex (in this case we assume throw was a
     *      slam into the ground next to grabber)
     *   .ATTACK.AE has any hit targets.
     *
     * If any of these conditions occur, the "COLLISION" throw effect will be
     * set by default.  If none of them occur, the "NOCOLLISION" will be set.
     *
     */
    protected boolean isCollision() {
        ActivationInfo ai = battleEvent.getActivationInfo();
        
        IndexIterator ii = ai.getTargetGroupIterator(".ATTACK");
        while(ii.hasNext()) {
            int tindex = ii.nextIndex();
            if ( ai.getTargetHit(tindex) ) {
                Target t = ai.getTarget(tindex);
                if ( t instanceof HexTarget ) {
                    // This was a hex...check if it was adjacent...
                    if ( t.getCalculatedDCV() == 0 ) return true;
                    
                    // Check if there are obstruction in the obstruction
                    // list that were hit
                    ObstructionList ol = ai.getObstructionList(tindex);
                    if ( ol.getObstructionCount() > 0 ) return true;
                }
                else {
                    // It was a target other then a hex, so assume there was
                    // a collision...
                    return true;
                }
            }
        }
        
        // There wasn't a collision yet, so see if there is an AE target group
        ii = ai.getTargetGroupIterator(".ATTACK.AE");
        while(ii.hasNext()) {
            int tindex = ii.nextIndex();
            if ( ai.getTargetHit(tindex) ) {
                // If there is a target here, we assume a collision
                return true;
            }
        }
        
        return false;
    }
    
        
    public void buildChildren() {
        
        BattleEvent be = getBattleEvent();
        Ability ability = be.getAbility();
        
        ActivationInfo ai = be.getActivationInfo();
        
        String effect = ai.getStringValue("ActivationInfo.THROWEFFECT");
        
        if ( "COLLISION".equals(effect) ) {
            
            if ( getChildCount() == 1 ) {
                
            }
        }
        else if ( "KILLCOLLISION".equals(effect) ){
            
        }
        else {
            
        }
    }
   
}
