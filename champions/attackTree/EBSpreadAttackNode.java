/*
 * EBSpreadAttack.java
 *
 * Created on January 19, 2002, 4:58 PM
 */

package champions.attackTree;

import champions.*;
import champions.exception.BattleEventException;
import champions.interfaces.IndexIterator;

/**
 *
 * @author  twalker
 * @version 
 */
public class EBSpreadAttackNode extends DefaultAttackTreeNode {

        /** Creates new EBSpreadAttack */
    public EBSpreadAttackNode(String name) {
        this.name = name;
        setVisible(false);
    }
    
    public boolean activateNode(boolean manualOverride) {
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        
        // When this is activated, make sure the battleEvent is correctly prepared.
        prepareBattleEvent();
        
        // The root node should never accept active node status, since
        // It isn't really a real node.
        return true;
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
                if ( nextNodeName.equals("Single Target")) {
                    SingleTargetNode node = new SingleTargetNode(nextNodeName);
                    node.setTargetReferenceNumber(0); // Indicate that this is the primary target of the Group.
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Effect")) {
                    EffectNode node = new EffectNode(nextNodeName);
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }
    
  /*  public void checkNodes() {
        int count = (children == null) ? 0 : children.size();
        int index;
        String previousNodeName = null;
        String nextNodeName = null;
        AttackTreeNode atn;
   
        for(index=0;index<count;index++) {
            atn = (AttackTreeNode)children.get(index);
            nextNodeName = nextNodeName(previousNodeName);
            if ( atn.getName().equals(nextNodeName) == false ) {
                // The next node name isn't what it should be, so rebuild this tree from this child down.
                while ( index < children.size() ) {
                    atn = (AttackTreeNode)children.get(index);
                    removeChild(atn);
                }
                break;
            }
            previousNodeName = nextNodeName;
        }
    } */
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        if ( previousNodeName == null ) {
            nextNodeName = "Single Target";
        }
        else if ( previousNodeName.equals("Single Target") ) {
            BattleEvent be = getBattleEvent();
            ActivationInfo ai = be.getActivationInfo();
            int tindex;
            
            boolean somethingHit = false;
            IndexIterator i = ai.getTargetGroupIterator(getTargetGroup());
            while ( i.hasNext() ) {
                tindex = i.nextIndex();
                if ( ai.getIndexedBooleanValue(tindex, "Target", "HIT") == true ) {
                    somethingHit = true;
                    break;
                }
            }
            
            if ( somethingHit == true ) {
                nextNodeName = "Effect";
            }
        }
        return nextNodeName;
    }
    
 /*   public void nodeChanged(AttackTreeNode changedNode) {
        if ( changedNode.getName().equals("ToHit") ) {
            checkNodes();
            
            AttackTreeNode atn = findNode("Effect", false);
            if ( atn != null ) atn.nodeChanged(changedNode);
        }
        
        //super.nodeChanged(changedNode);
    } */
    
    public void prepareBattleEvent() {
        String newGroup = getTargetGroup();
        // Create the group for this attack
        ActivationInfo ai = getBattleEvent().getActivationInfo();
        
        int tgindex = ai.addTargetGroup(newGroup);
        ai.setKnockbackGroup(tgindex, "KB");
        
        // Reset the DCCalculated Flag, so things get recalculated appropriately.
        ai.addIndexed(tgindex, "TargetGroup", "DCCALCULATED", "FALSE", true);
        
        // Add the first shot target with the reference number of 0 (primary target).
            int tindex = ai.addTarget(null,newGroup, 0);
            
            //Integer sprayWidth = ai.getIntegerValue("Attack.SPRAYWIDTH");
            Integer width = ai.getIntegerValue("Attack.SPREADWIDTH");
            
            CVList cvl = ai.getCVList(tindex);
            cvl.addSourceCVModifier("EB Spread Bonus", width.intValue());
            
            ai.setTargetGroupDCModifier(newGroup, -1 * width.intValue() );
        
        try {
            // Add all attack information which depends on the AttackParameterPanel
            Target source = getBattleEvent().getSource();
            
            BattleEngine.calculateDamage(battleEvent, battleEvent.getSource(), newGroup);
        }
        catch (BattleEventException bee) {
            
        }
    }

}
