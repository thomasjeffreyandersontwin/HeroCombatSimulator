/*
 * AttackTypeNode.java
 *
 * Created on November 7, 2001, 10:23 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.ActivationInfo;
import champions.BattleEvent;

/**
 *
 * @author  twalker
 * @version
 */
public class AttackTypeNode extends DefaultAttackTreeNode {
    
    /** Creates new TestAttackTreeNode */
    public AttackTypeNode(String name) {
        this.name = name;
        setVisible(false);
    }
    
    public boolean activateNode(boolean manualOverride) {
        // This should actually check to make sure that the next node is correct give the
        // parameters configured for the ability activation...
        
        // The root node should never accept active node status, since
        // It isn't really a real node.
        return false;
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
                    node.setPrimaryTargetNumber(1);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Movement Node")) {
                    MovementNode node = new MovementNode(nextNodeName);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Throw")) {
                    ThrowNode node = new ThrowNode(nextNodeName);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Move By Attack")) {
                    MoveByAttackNode node = new MoveByAttackNode(nextNodeName);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Autofire")) {
                    AutofireAttackNode node = new AutofireAttackNode(nextNodeName);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Autofire Spray")) {
                    AutofireSprayAttackNode node = new AutofireSprayAttackNode(nextNodeName);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("AreaEffect")) {
                    AreaEffectAttackNode node = new AreaEffectAttackNode(nextNodeName);
                    node.setPrimaryTargetNumber(1);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Explosion")) {
                    AreaEffectAttackNode node = new AreaEffectAttackNode(nextNodeName);
                    node.setExplosion(true);
                    node.setPrimaryTargetNumber(1);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("EBMultiSpread")) {
                    EBMSpreadAttackNode node = new EBMSpreadAttackNode(nextNodeName);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("EBSpread")) {
                    EBSpreadAttackNode node = new EBSpreadAttackNode(nextNodeName);
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        if ( previousNodeName == null ) {
            BattleEvent be = getBattleEvent();
            Ability ability = be.getAbility();
            ActivationInfo ai = be.getActivationInfo();
            
            if ( ability.isMovementPower() ) {
                nextNodeName = "Movement Node";
            }
            else if ( ability.isThrow() ) {
                nextNodeName = "Throw";
            }
            else if ( ability.getBooleanValue("Ability.ISAUTOFIRE" ) ) {
                // This is an autofire.  Determine if it is normal or spray...
                if ( ai.getBooleanValue("Attack.ISSPRAY" ) ) {
                    nextNodeName = "Autofire Spray";
                }
                else {
                    nextNodeName = "Autofire";
                }
            }
            else if ( be.is("AE" ) ) {
                nextNodeName = "AreaEffect";
            }
            else if ( be.is("EXPLOSION" ) ) {
                nextNodeName = "Explosion";
            }
            else if ( ai.getBooleanValue("Attack.ISSPREAD" ) ) {
                if ( ai.getBooleanValue( "Attack.ISSPREADM" ) ) {
                    nextNodeName = "EBMultiSpread";
                }
                else {
                    nextNodeName = "EBSpread";
                }
            }
            else if ( ai.getBooleanValue("Attack.ISMOVEBY" ) ) {
                nextNodeName = "Move By Attack";
            }
            else {
                nextNodeName = "Single Attack";
            }
        }
        return nextNodeName;
    }
    
    
}
