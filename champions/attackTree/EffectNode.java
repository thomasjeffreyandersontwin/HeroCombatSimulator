/*
 * EffectNode.java
 *
 * Created on November 7, 2001, 10:45 PM
 */

package champions.attackTree;

import champions.*;
import champions.battleMessage.DiceRollMessage;
import champions.exception.BadDiceException;
import champions.interfaces.IndexIterator;
import java.util.Vector;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class EffectNode extends DefaultAttackTreeNode {
    
    public static EffectNode Node;

	/** Creates new EffectNode */
    public EffectNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.diceIcon");
        // setVisible(false);
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        
        if ( manualOverride || nodeRequiresInput() ) {
            EffectPanel ep = EffectPanel.getDefaultPanel(getBattleEvent(), getTargetGroup());
            
            Ability ability = battleEvent.getAbility();
            Target source = battleEvent.getSource();
            
            if(attackTreePanel==null) {
            	attackTreePanel = AttackTreePanel.defaultAttackTreePanel;
            }
            attackTreePanel.showInputPanel(this,ep);
            attackTreePanel.setInstructions("Enter the Dice rolls for " + source.getName() + "'s " + ability.getName() + "...");
            
            acceptActivation = true;
            Node = this;
        }
        
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        // Run through the dice and make sure the all have values;
        boolean requiresInput = false;
        Dice d;
        
        IndexIterator ii = getBattleEvent().getDiceIterator(getTargetGroup());
        int dindex;
        while ( ii.hasNext() ) {
            dindex = ii.nextIndex();
            d = getBattleEvent().getDiceRoll(dindex);
            if ( d == null || d.isRealized() == false ) {
                requiresInput = true;
                break;
            }
        }
        // Before the override can be used, the Dice must be properly setup in processAdvance
        return requiresInput && getBattleEvent().getSource().getBooleanProfileOption("SHOW_EFFECT_ROLL_PANEL");
    }
    
    /**
     * Builds the next child based upon the last child which was constructed.
     *
     * If all the children necessary have been built, return null, otherwise
     * return the next node.
     *
     * BuildNextChild should add that child to the tree.
     */
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        
        AttackTreeNode nextNode = null;
        
        // AttackParameter has no children...
        
        return nextNode;
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
        // Instead of just checking the node, always rebuilt the node structure.
        updateChildren();
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
    	checkDice();
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " exited.");
        return true;
    }
    
    public void updateChildren() {
        // This method must make sure that all the Targets hit in the TargetGroup
        // have an associated TargetEffect node.
        
        boolean fireChange = false;
        boolean found;
        // Make sure the abilities in the source match the abilityNodes that are childern
        Vector newChildren = new Vector();
        int nindex, ncount;
        int total = 0;
        ncount = ( children != null ) ? children.size() : 0;
        TargetEffectNode ten;
        int dindex, refNumber;
        Target target;
        boolean hit;
        
        
        ActivationInfo ai = getBattleEvent().getActivationInfo();
        
        // If this is an nnd attack, check for the nnd defense...
//        if ( battleEvent.getAbility().is)
//        found = false;
//        for(nindex=0;nindex<ncount;nindex++) {
//            ten = (TargetEffectNode) children.get(nindex);
//            if (ten != null && ten.getTarget() == target && ten.getTargetReferenceNumber() == refNumber ) {
//                found = true;
//                // Move the ability node from the childern list to the newChildern list
//                newChildren.add(ten);
//                if ( nindex != total ) fireChange = true;
//                children.set(nindex, null);
//                break;
//            }
//        }
//
//        if ( found == false ) {
//            ten = new TargetEffectNode("Target Effect");
//            ten.setParent(this);
//            ten.setModel(getModel());
//            ten.setBattleEvent(getBattleEvent());
//            ten.setAttackTreePanel(getAttackTreePanel());
//            ten.setTarget(target);
//            ten.setTargetReferenceNumber(refNumber);
//
//            newChildren.add(ten);
//            fireChange = true;
//        }
//        total++;
        
        
        IndexIterator ii = ai.getTargetGroupIterator(getTargetGroup());
        
        while ( ii.hasNext() ) {
            dindex = ii.nextIndex();
            target = ai.getTarget(dindex);
            refNumber = ai.getTargetReferenceNumber(dindex);
            hit = ai.getTargetHit(dindex);
            
            if ( target != null && hit) {
                // Try to find it in the children array.
                found = false;
                for(nindex=0;nindex<ncount;nindex++) {
                    ten = (TargetEffectNode) children.get(nindex);
                    if (ten != null && ten.getTarget() == target && ten.getTargetReferenceNumber() == refNumber ) {
                        found = true;
                        // Move the ability node from the childern list to the newChildern list
                        newChildren.add(ten);
                        if ( nindex != total ) fireChange = true;
                        children.set(nindex, null);
                        break;
                    }
                }
                
                if ( found == false ) {
                    ten = new TargetEffectNode("Target Effect");
                    ten.setParent(this);
                    ten.setModel(getModel());
                    ten.setBattleEvent(getBattleEvent());
                    ten.setAttackTreePanel(getAttackTreePanel());
                    ten.setTarget(target);
                    ten.setTargetReferenceNumber(refNumber);
                    
                    newChildren.add(ten);
                    fireChange = true;
                }
                total++;
            }
        }
        
        Vector oldChildren = children;
        children = newChildren;
        
        // Now that everything is done, anything level not-null in oldChildren should be destroyed
        // and references to it released.
        if ( oldChildren != null ) {
            for(nindex=0;nindex<oldChildren.size();nindex++) {
                if ( oldChildren.get(nindex) != null ) {
                    ((AttackTreeNode)oldChildren.get(nindex)).destroy();
                    oldChildren.set(nindex,null);
                    fireChange = true;
                }
            }
        }
        
        if ( fireChange && model != null ) model.nodeStructureChanged(this);
        
        
    }
    
    public void nodeChanged(AttackTreeNode changedNode) {
        if ( changedNode.getName().equals("ToHit") ) {
            updateChildren();
        }
    }
    
    public void setBattleEvent(BattleEvent be) {
        super.setBattleEvent(be);
        
        // updateChildren();
    }
    
    private void checkDice() {
        if ( battleEvent != null ) {
            IndexIterator ii = battleEvent.getDiceIterator(getTargetGroup());
            
            int vindex = 0;
            int dindex;
            
            Dice dice;
            boolean auto;
            
            PADDiceValueEditor editor;
            
            while ( ii.hasNext() ) {
                dindex = ii.nextIndex();
                
                dice = battleEvent.getDiceRoll(dindex);
                
                try {
                    if ( dice == null ) {
                        String size = battleEvent.getIndexedStringValue(dindex, "Die", "SIZE");
                        
                        dice = new Dice( size, true);
                        
                        battleEvent.setDiceRoll(dindex, dice);
                        battleEvent.setDiceAutoRoll(dindex, true);
                    }
                    else {
                        // Make sure the correctly sized dice were rolled!
                        String size = battleEvent.getIndexedStringValue(dindex, "Die", "SIZE");
                        boolean autoroll = battleEvent.getDiceAutoRoll(dindex);
                        
                        if ( autoroll && dice.checkSize( size ) == false) {
                            dice = new Dice( size, true);
                            
                            battleEvent.setDiceRoll(dindex, dice);
                        }
                        
                    }
                    
                    if ( Preferences.getBooleanValue("DieRollMessages") ) {
                        String msg = battleEvent.getSource().getName() + " rolled " + dice.toString() + " with " + dice.getSizeString() + " for " + battleEvent.getDiceDescription(dindex)+ ".";
                        //battleEvent.addBattleMessage( new champions.battleMessage.SimpleBattleMessage( msg, MSG_DICE));
                        battleEvent.addBattleMessage( new DiceRollMessage(battleEvent.getSource(), msg));
                    }
                }
                catch ( BadDiceException bde) {
                    
                }
                
                vindex ++;
            }
        }
    }
    
    public String getAutoBypassOption() {
        return "SHOW_EFFECT_ROLL_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return getBattleEvent().getSource();
    }
}
