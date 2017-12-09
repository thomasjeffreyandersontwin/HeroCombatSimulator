/*
 * TargetOptionNode.java
 *
 * Created on November 20, 2001, 4:04 PM
 */

package champions.attackTree;

import champions.*;
import champions.powers.effectBlock;
import champions.powers.effectDeflection;

/**
 *
 * @author  twalker
 * @version
 */
public class TargetOptionNode extends DefaultAttackTreeNode {
    
    /** Store Target Selected for this node */
    protected Target target;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Creates new TargetOptionNode */
    public TargetOptionNode(String name) {
        this.name = name;
        setVisible(false);
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    
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
        // Don't do anything when you check the nodes.  This nodes is built by
        // when the setTarget method is called.
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
    
    public String toString() {
        return getName() + " - " + getTarget();
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
    
    public void destroy() {
        setTarget(null);
        
        super.destroy();
    }
    
    public void buildChildren() {
        
        // if ( target != battleEvent.getSource() ) {
        // Build a ToHit node and add it
        
        Ability ability = battleEvent.getAbility();
        if ( !ability.isSkill() || !ability.isDisadvantage()) {
//            AttackCombatLevelsNode acln = new AttackCombatLevelsNode("Target Combat Levels", target, false);
//            addChild(acln);

            SourcePerceptionsNode spn = new SourcePerceptionsNode("Attacker Perception");
            addChild(spn);
            // pn.setTargetReferenceNumber(targetReferenceNumber);
            spn.setTargetReferenceNumber(targetReferenceNumber);
            spn.setTarget(getTarget()); // Do this afterwards, since the BE would be set before the addChild.
        
            TargetPerceptionsNode tpn = new TargetPerceptionsNode("Target Perception");
            addChild(tpn);
            // pn.setTargetReferenceNumber(targetReferenceNumber);
            tpn.setTargetReferenceNumber(targetReferenceNumber);
            tpn.setTarget(getTarget()); // Do this afterwards, since the BE would be set before the addChild.
        
        }
        if ( target.getBooleanValue( "Target.HASDEFENSES") && !ability.isSkill() || !ability.isDisadvantage()) {
            DefensesNode dn = new DefensesNode("Defenses");
            addChild(dn);
            dn.setTargetReferenceNumber(targetReferenceNumber);
            dn.setTarget(getTarget()); // Do this afterwards, since the BE would be set before the addChild.
        }
        
        
        if ( battleEvent.usesHitLocation() && target.getBooleanValue("Target.USESHITLOCATION") && ((Boolean)Preferences.getPreferenceList().getParameterValue("AllowHitLocation") || (Boolean)Preferences.getPreferenceList().getParameterValue("HitLocationRequired")) && !ability.isSkill() || !ability.isDisadvantage()  ) {
            HitLocationNode csn = new HitLocationNode("Hit Location");
            addChild(csn);
            csn.setTargetReferenceNumber(targetReferenceNumber); // Do this afterwards, since the BE would be set before the addChild.
        }
        
        // Add the Obstruction Node
        
        if ( !ability.isSkill() || !ability.isDisadvantage()  ) {
            ObstructionNode on = new ObstructionNode("Obstructions");
            addChild(on);
            on.setTargetReferenceNumber(targetReferenceNumber);
        }
        // Setup the Special Defense nodes, such as block, dive for cover, etc...
        if ( battleEvent.isMeleeAttack() ) {
            int index = target.getEffectCount() - 1;
            for(; index >= 0; index-- ) {
                Effect effect = target.getEffect(index);
                if ( effect instanceof effectBlock ) {
                    BlockNode bn = new BlockNode("Block");
                    bn.setTargetReferenceNumber(targetReferenceNumber);
                    bn.setTarget(target);
                    bn.setBlockEffect(  effect);
                    addChild(bn);
                    break;
                }
            }
        }
        
        // Setup the Special Defense nodes, such as block, dive for cover, etc...
        if ( battleEvent.isRangedAttack() ) {
           int index = target.getEffectCount() - 1;
            for(; index >= 0; index-- ) {
                Effect effect = target.getEffect(index);
                if ( effect instanceof effectDeflection ) {
                    BlockNode bn = new BlockNode("Deflection");
                    bn.setTargetReferenceNumber(targetReferenceNumber);
                    bn.setTarget(target);
                    bn.setBlockEffect(  effect);
                    addChild(bn);
                    break;
                }
            }
        }
        
        if ( ability.requiresMentalPanel() && target.isAlive()) {
             MentalEffectNode men = new MentalEffectNode("Mental Effect");
            addChild(men);
            men.setTargetReferenceNumber(targetReferenceNumber);
        }
        //   }
        
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
    
}
