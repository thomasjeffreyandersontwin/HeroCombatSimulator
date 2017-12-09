/*
 * AECenterTargetNode.java
 *
 * Created on December 28, 2001, 2:01 PM
 */

package champions.attackTree;

import champions.*;
import champions.event.TargetSelectedEvent;
import tjava.Filter;
import champions.interfaces.TargetListener;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version 
 */
public class AECenterTargetNode extends DefaultAttackTreeNode 
implements TargetListener {

    /** Store Target Selected for this node */
    protected Target target;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Hold the primary Target number, if this is a primary target. */
    private int primaryTargetNumber = -1;
    
    /** Creates new AECenterTargetNode */
    public AECenterTargetNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.selectTargetIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        boolean fixed = false;
        String reason = null;
        
        // Call the adjustTarget() method to allow parent nodes to
        // fix up the target if necessary.
        adjustTarget(battleEvent, battleEvent.getSource(), targetReferenceNumber, getTargetGroup(), primaryTargetNumber);
        
        // Try to find a target based on the battleEvent
        ActivationInfo ai = getBattleEvent().getActivationInfo();
        
        // Setup the primary target information for other node to use later.
        if ( isPrimaryTargetNode() ) {
            ai.setPrimaryTarget(primaryTargetNumber, getTargetGroup(), targetReferenceNumber);
        }
        
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup() );
        
        if ( tindex != -1 ) {
            // Grab the first target found
            setTarget( (Target)ai.getIndexedValue(tindex, "Target", "TARGET") );
            fixed = ai.getTargetFixed(tindex);
            reason = ai.getTargetFixedReason(tindex);
        }
        
        if ( target == null || manualOverride == true ) {
            if ( fixed ) {
                InformationPanel ip = InformationPanel.getDefaultPanel( reason );
                attackTreePanel.showInputPanel(this,ip);
                attackTreePanel.setInstructions("Hit Okay to Continue...");
            }
            else {
                Filter<Target> filter = battleEvent.getTargetFilter();
                SelectTargetPanel stp = SelectTargetPanel.getSelectTargetPanel(getBattleEvent(), getTargetGroup(), SelectTargetPanel.AE_CENTER, filter);
                stp.addTargetListener(this);
                
                attackTreePanel.showInputPanel(this,stp);
                attackTreePanel.setInstructions("Select Target for Center of Area Effect Target...");
            }
            acceptActivation = true;
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        // This node is completely strange in that it firsts asks for a target, and once one is set, it
        // creates the ToHit and TargetOption nodes immediately.
        
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
        return getTarget() != null;
    }
    
    public String toString() {
        if ( getTarget() == null ) {
            return "Select AE Center Target";
        }
        else {
            return "AE Center: " + getTarget().getName();
        }
    }
    
    public void setTarget(Target target) {
        if ( this.target != target ) {
            ActivationInfo ai = getBattleEvent().getActivationInfo();
            if ( this.target != null ) {
                ai.removeTarget(this.target, getTargetGroup());
                
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
                ai.addTarget(target, getTargetGroup(), targetReferenceNumber);
                
                // Do all the target initialization here
                int tindex = ai.getTargetIndex(targetReferenceNumber,getTargetGroup());
                
                CVList cvList = ai.getCVList(tindex); // This will return a blank CVList in most cases
                if ( cvList.isInitialized() == false ) {
                    BattleEngine.buildCVList(getBattleEvent(), getBattleEvent().getSource(), target, cvList);
                }
                
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
    
    public Target getTarget() {
        return this.target;
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
        ton.setTargetReferenceNumber(targetReferenceNumber);
        ton.setTarget(getTarget());
        
        // Build a ToHit node and add it
        ToHitNode thn = new ToHitNode("ToHit");
        addChild(thn);
        thn.setTargetReferenceNumber(targetReferenceNumber);
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
    
    /** Getter for property primaryTargetNumber.
     * @return Value of property primaryTargetNumber.
     *
     */
    public int getPrimaryTargetNumber() {
        return primaryTargetNumber;
    }    
    
    /** Setter for property primaryTargetNumber.
     * @param primaryTargetNumber New value of property primaryTargetNumber.
     *
     */
    public void setPrimaryTargetNumber(int primaryTargetNumber) {
        this.primaryTargetNumber = primaryTargetNumber;
    }
    
    public boolean isPrimaryTargetNode() {
        return this.primaryTargetNumber != -1;
    }

}
