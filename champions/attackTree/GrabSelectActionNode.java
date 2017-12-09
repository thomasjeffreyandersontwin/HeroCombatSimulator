/*
 * GrabTriggerNode.java
 *
 * Created on June 5, 2002, 3:24 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.BattleEvent;
import champions.Target;
import champions.exception.BattleEventException;
import champions.powers.maneuverSqueezeFromGrab;
import champions.powers.maneuverThrowFromGrab;


/**
 *
 * @author  Trevor Walker
 */
public class GrabSelectActionNode extends DefaultAttackTreeNode {
    
        
    private boolean childrenBuilt = false;
    
    /** Holds value of property targetReferenceNumber. */
    private int grabbedEffectIndex;
    
    private Target target;
    
    /** Creates new GrabTriggerNode */
    public GrabSelectActionNode(String name) {
        this.name = name;
        
         setVisible(true);
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        String action = battleEvent.getIndexedStringValue(grabbedEffectIndex, "GrabbedEffect", "ACTION");
        if ( action == null ) {
            battleEvent.addIndexed(grabbedEffectIndex, "GrabbedEffect", "ACTION", "NONE", true);
        }
        
        if ( nodeRequiresInput() || manualOverride ) {
            
            GrabSelectActionPanel op = GrabSelectActionPanel.getDefaultPanel(battleEvent, getTarget(), getGrabbedEffectIndex());
            attackTreePanel.showInputPanel(this,op);
            attackTreePanel.setInstructions("Select Follow-up Action for grab of " + getTarget().getName());
            
            acceptActivation = true;
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    protected boolean nodeRequiresInput() {
        String action = battleEvent.getIndexedStringValue(grabbedEffectIndex, "GrabbedEffect", "ACTION");
        
        return action != null;
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
    public boolean processAdvance() throws BattleEventException {
        // We need to make sure we embed the right type of attack into 
        // this event...
        String action = battleEvent.getIndexedStringValue(grabbedEffectIndex, "GrabbedEffect", "ACTION");
        
        AttackTreeNode node = (AttackTreeNode)getRealChildAt(0);
        
        if ( action == null || action.equals("NONE")) {
            if ( node != null ) removeChild( node );
        }
        else if ( action.equals("SQUEEZE")) {
            if ( node != null && ((ProcessEmbeddedEventNode)node).getEmbeddedEvent().getAbility().getPower() instanceof maneuverSqueezeFromGrab == false ) {
                removeChild( node );
                node = null;
            }
            
            if ( node == null ) {
                Ability a = new Ability("Squeeze From Grab");
                a.addPAD( new maneuverSqueezeFromGrab(), null);
                BattleEvent newBE = new BattleEvent(a);

                node = new ProcessEmbeddedEventNode("Squeeze From Grab Root", newBE);
                node.setVisible(false);
                addChild(node);
            }
        }
        else if ( action.equals("THROW")) {
            if ( node != null && ((ProcessEmbeddedEventNode)node).getEmbeddedEvent().getAbility().getPower() instanceof maneuverThrowFromGrab == false ) {
                removeChild( node );
                node = null;
            }
            
            if ( node == null ) {
                Ability a = new Ability("Squeeze From Grab");
                a.addPAD( new maneuverThrowFromGrab(), null);
                BattleEvent newBE = new BattleEvent(a);
                newBE.add("BattleEvent.THROWNOBJECT", getTarget(), true );

                node = new ProcessEmbeddedEventNode("Throw From Grab Root", newBE);
                node.setVisible(false);
                addChild(node);
            }
        }
        
        return true;
    }
    
    public void checkNodes() {
        // Don't check the children ever.
    }
    
    /**
     * Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    
    /**
     * Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        this.target = target;
    }
    
    /**
     * Getter for property grabbedEffectIndex.
     * @return Value of property grabbedEffectIndex.
     */
    public int getGrabbedEffectIndex() {
        return grabbedEffectIndex;
    }
    
    /**
     * Setter for property grabbedEffectIndex.
     * @param grabbedEffectIndex New value of property grabbedEffectIndex.
     */
    public void setGrabbedEffectIndex(int grabbedEffectIndex) {
        this.grabbedEffectIndex = grabbedEffectIndex;
    }
    
    /** 
     * Allows node to adjust the target set for an attack.
     *
     * Every node in the tree path of a target node has an oppertunity to
     * adjust the target for an attack via this method.  This method should
     * in most cases pass this call up to it's attack tree node parent (or
     * simply call the default implementation which does exactly that).
     *
     * PrimaryTargetNumber indicates if this particular target is considered 
     * a primary target for this attack.  If it is, primaryTargetNumber should
     * be one or greater.
     */
    public void adjustTarget(BattleEvent be, Target source, int referenceNumber, String targetGroup, int primaryTargetNumber) {
        // Don't do anything if this isn't a primary target...
        String action = battleEvent.getIndexedStringValue(grabbedEffectIndex, "GrabbedEffect", "ACTION");
        
        if ( "SQUEEZE".equals(action) && primaryTargetNumber > 0 && target != null ) {
            int index = be.getActivationInfo().addTarget(target, targetGroup, referenceNumber);
            be.getActivationInfo().setTargetFixed(index, true, target.getName() + " is being squeezed by " + source.getName());
            be.getActivationInfo().setTargetHitOverride(index, true, "Automatic hit: Follow-up to grab", source.getName() + " is squeezing " + target.getName() + " as a follow-up action to a grab maneuver.  Hit is automatic.");
        }
    }
    
}
