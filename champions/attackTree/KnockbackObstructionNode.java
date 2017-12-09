/*
 * KnockbackObstructionNode.java
 *
 * Created on November 8, 2001, 12:07 PM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.BattleEngine;
import champions.DefenseList;
import champions.Target;
import champions.event.TargetSelectedEvent;
import champions.interfaces.TargetListener;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version 
 */
public class KnockbackObstructionNode extends DefaultAttackTreeNode 
implements TargetListener {

    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Creates new KnockbackObstructionNode */
    public KnockbackObstructionNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.selectTargetIcon");
        
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        if ( manualOverride || nodeRequiresInput() ) {
            Target source = battleEvent.getActivationInfo().getKnockbackSourceTarget( getTargetGroup() ) ;
            
            SelectTargetPanel stp = SelectTargetPanel.getSelectTargetPanel(getBattleEvent(), getTargetGroup(), SelectTargetPanel.KNOCKBACK_TARGET,source, null);
            stp.addTargetListener(this);
            
            attackTreePanel.showInputPanel(this,stp);
            attackTreePanel.setInstructions("Select Secondary Knockback Target...");
            
            acceptActivation = true;
        }
        
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        if ( getTarget() == null ) {
            requiresInput = true;
        }
        
        return requiresInput && getAutoBypassTarget().getBooleanProfileOption( getAutoBypassOption());
    }
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        // This node is completely strange in that it firsts asks for a target, and once one is set, it 
        // creates the ToHit and TargetOption nodes immediately.
        
        
     /*   AttackTreeNode nextNode = null;
        // Compare the activeChild to the possible children, to determine what comes next.
        if ( activeChild != null && activeChild.getName() == null ) {
            // This is probably an error
            int index = children.indexOf(activeChild);
            System.out.println("Null Node name for child " + Integer.toString(index) + " of Parent " + this);
            nextNode = null;
        }
        else if ( activeChild == null ) {
            // Build the very first node: Attack Param
         //   ToHitNode node = new ToHitNode("ToHit");
         //   nextNode = node;
        }
        else if ( activeChild.getName().equals("Single Target") ) {
            // Build second node: Single Attack
         //   EffectNode node = new EffectNode("Effect");
            //nextNode = node;
        }

        return nextNode; */
        return null;
    }

    public String toString() {
        if ( target == null ) {
            return "Click to Select Knockback Target";
        }
        else {
            return "Target: " + target.getName();
        }
    }
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        if ( this.target != target ) {
            ActivationInfo ai = getBattleEvent().getActivationInfo();
            if ( this.target != null ) {
                ai.removeTarget(targetReferenceNumber, getTargetGroup());
                
                if ( children != null ) {
                    int index;
                    AttackTreeNode atn;
                    for( index = children.size() - 1; index >= 0; index--) {
                        atn = (AttackTreeNode)children.get(index);
                        removeChild(atn);
                    }
                }
            }
            
            this.target = target;
            
            if ( this.target != null ) {
                // Do all the target initialization here
                int tindex = ai.addTarget(target, getTargetGroup(), targetReferenceNumber);
                ai.setKnockbackSourceTarget(tindex, false);
                
                String reason = "Hit secondary knockback target automatically.";
                ai.setTargetHitOverride(tindex, true, reason, reason);
                
             //   CVList cvList = ai.getCVList(tindex); // This will return a blank CVList in most cases
                
                DefenseList dl = ai.getDefenseList(tindex);
                if ( dl == null ) {
                    dl = new DefenseList();
                    ai.setDefenseList(tindex, dl);
                }
                BattleEngine.buildDefenseList(dl,target);
                
                buildChildren();
            }
            
            if ( model != null ) {
                model.nodeChanged(this);
            }
        }
    }
    
    public void targetSelected(TargetSelectedEvent e) {
        Target newTarget = e.getTarget();
        
        if ( getTarget() != newTarget ) {
            setTarget(newTarget);
        }
    }
    
    public void destroy() {
        setTarget(null);
        
        super.destroy();
    }
    
    public void buildChildren() {

        TargetOptionNode ton = new TargetOptionNode("Target Options");
        addChild(ton);
        ton.setTarget(getTarget());
        
        // Build a ToHit node and add it
     //   ToHitNode thn = new ToHitNode("ToHit");
     //   addChild(thn);
    //    thn.setTarget(getTarget()); // Do this afterwards, since the BE would be set before the addChild.
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
        return "SHOW_KNOCKBACK_SECONDARY_TARGET_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
    }
    
}
