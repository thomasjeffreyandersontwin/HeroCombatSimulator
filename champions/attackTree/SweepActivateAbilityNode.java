/*
 * ProcessActivateAbilityNode.java
 *
 * Created on April 25, 2002, 4:05 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.BattleEngine;
import champions.BattleEvent;
import champions.CVList;
import champions.SweepBattleEvent;
import champions.Target;
import champions.exception.BattleEventException;

/**
 *
 * @author  Trevor Walker
 * @version
 */
public class SweepActivateAbilityNode extends DefaultAttackTreeNode {
    
    boolean firstPass = true;
    
    /** Holds the index of the Ability in the SweepBattleEvent */
    int linkedAbilityIndex = -1;
    
    private SweepBattleEvent sweepBattleEvent = null;
    
    /** Creates new ProcessActivateAbilityNode */
    public SweepActivateAbilityNode(String name) {
        this.name = name;
        
        setVisible(true);
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean activateNode = false;
        
        // Embed the BattleEvent 
        if ( linkedAbilityIndex != -1 ) {
            BattleEvent be = ((SweepBattleEvent)battleEvent).getLinkedBattleEvent(linkedAbilityIndex);
            if ( be == null ) {
                Ability a = ((SweepBattleEvent)battleEvent).getLinkedAbility(linkedAbilityIndex);
                Ability m = ((SweepBattleEvent)battleEvent).getLinkedManeuver(linkedAbilityIndex); 
                
                be = a.getActivateAbilityBattleEvent(a, m, a.getSource());
                
                if ( be == null ) {
                    be = new BattleEvent(a);
                    
                    if ( m != null ) {
                        be.setManeuver(m);
                    }
                }
                
                ((SweepBattleEvent)battleEvent).setLinkedBattleEvent(linkedAbilityIndex, be);
            }
            
            battleEvent.embedBattleEvent(be);
        }
        
        if ( firstPass ) {
            buildChildren();
            firstPass = false;
        }
        
        if ( nodeRequiresInput() || manualOverride == true ) {
            Target source = battleEvent.getSource();
            Ability ability = ((SweepBattleEvent)battleEvent).getLinkedAbility(linkedAbilityIndex);
            String reason = "This is the activation node for " + source.getName() + "'s " + ability.getName() +
                            " ability.  This is part of a Sweep ability activation." +
                            "\n\nEverything contained in this folder pertains only to this single ability.";
            InformationPanel ip = InformationPanel.getDefaultPanel( reason );
            attackTreePanel.showInputPanel(this,ip);
            attackTreePanel.setInstructions("Hit Okay to Continue...");
            
            activateNode = true;
        }
        
        return activateNode;
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
    
    private void buildChildren() {
        if ( linkedAbilityIndex != -1 ) {
            BattleEvent be = ((SweepBattleEvent)battleEvent).getLinkedBattleEvent(linkedAbilityIndex);
            
            
            AttackTreeNode apn = BattleEngine.getProcessAbilityRoot(be);
            apn.setVisible(false);
            addChild(apn, false);
        }
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
        
        
        return true;
    }
    
    private boolean nodeRequiresInput() {
        // Run through the dice and make sure the all have values;
        boolean requiresInput = false;
        
        return requiresInput;
    }
    
    /** Getter for property linkedAbilityIndex.
     * @return Value of property linkedAbilityIndex.
     *
     */
    public int getLinkedAbilityIndex() {
        return linkedAbilityIndex;
    }
    
    /** Setter for property linkedAbilityIndex.
     * @param linkedAbilityIndex New value of property linkedAbilityIndex.
     *
     */
    public void setSweepAbilityIndex(int linkedAbilityIndex) {
        this.linkedAbilityIndex = linkedAbilityIndex;
    }

    /** 
     * Allows node to adjust the CVList created for an attack.
     *
     * Every node in the tree path of a to-hit node has an oppertunity to
     * adjust the cvList for an attack via this method.  This method should
     * in most cases pass this call up to it's attack tree node parent (or
     * simply call the default implementation which does exactly that).
     */
    public void adjustCVList(BattleEvent be, Target source, Target target, CVList cvList) {
        
        
        int count = sweepBattleEvent.getLinkedAbilityCount();
        
        if ( sweepBattleEvent.hasTwoWeaponFighting() && linkedAbilityIndex <= 1 ) {
            // First two attacks are taken with no OCV penalty
            cvList.addSourceCVModifier("Sweep Penalty w/Two-weapon fighting", 0);
        }
        else {
            cvList.addSourceCVModifier("Sweep/Rapidfire Penalty", Math.min(0, (count - 1) * -2));
        }
    }

    public SweepBattleEvent getSweepBattleEvent() {
        return sweepBattleEvent;
    }

    public void setSweepBattleEvent(SweepBattleEvent sweepBattleEvent) {
        this.sweepBattleEvent = sweepBattleEvent;
    }
    
}
