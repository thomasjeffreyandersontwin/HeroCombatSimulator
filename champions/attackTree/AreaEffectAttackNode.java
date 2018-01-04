/*
 * AreaEffectAttackNode.java
 *
 * Created on December 28, 2001, 1:52 PM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.BattleEngine;
import champions.BattleEvent;
import champions.Target;
import champions.battleMessage.AreaEffectAttackMessageGroup;
import champions.battleMessage.BattleMessageGroup;
import champions.exception.BattleEventException;

/**
 *
 * @author  twalker
 * @version
 */
public class AreaEffectAttackNode extends DefaultAttackTreeNode implements BattleMessageGroupProvider {
    
    /** Hold the primary Target number, if this is a primary target. */
    private int primaryTargetNumber = -1;
    
    private boolean explosion = false;
    
    private AreaEffectAttackMessageGroup areaEffectAttackMessageGroup;
    
    /** Creates new AreaEffectAttackNode */
    public AreaEffectAttackNode(String name) {
        this.name = name;
        setVisible(false);
    }
    
    public boolean activateNode(boolean manualOverride) {
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        
        // When this is activated, make sure the battleEvent is correctly prepared.
        prepareBattleEvent();
        
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
                if ( nextNodeName.equals("Center Target")) {
                    AECenterTargetNode node = new AECenterTargetNode(nextNodeName);
                  //  node.setTargetGroupSuffix("CENTER");
                    node.setTargetReferenceNumber(0); // Indicate that this is the primary target of the Group.
                    node.setPrimaryTargetNumber(primaryTargetNumber);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Affected Targets")) {
                    AEAffectedTargetsNode node = new AEAffectedTargetsNode(nextNodeName);
                    node.setTargetGroupSuffix("AE");
                 //   node.setTargetReferenceNumber(0); // Indicate that this is the primary target of the Group.
                    nextNode = node;
                }
                else if ( nextNodeName.equals("DistanceFromCollision From Center")) {
                    ExplosionDistanceNode node = new ExplosionDistanceNode(nextNodeName);
                    node.setTargetGroupSuffix("AE");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Effect")) {
                    EffectNode node = new EffectNode(nextNodeName);
                    node.setTargetGroupSuffix("AE");
                    nextNode = node;
                }
                else if ( nextNodeName.equals("AreaEffect Close Message Group")) {
                    CloseMessageGroupNode node = new CloseMessageGroupNode(nextNodeName, this);
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
            nextNodeName = "Center Target";
        }
        else if ( previousNodeName.equals("Center Target") ) {
            nextNodeName = "Affected Targets";
        }
        else if ( previousNodeName.equals("Affected Targets") ) {
            
            
            
            BattleEvent be = getBattleEvent();
            ActivationInfo ai = be.getActivationInfo();
            int tindex;
            
            if ( ai.getTargetGroupHasHitTargets( getTargetGroup() + ".AE" ) ) {
                if ( isExplosion() ) {
                    nextNodeName = "DistanceFromCollision From Center";
                }
                else {
                    nextNodeName = "Effect";
                }
            }
        }
        else if ( previousNodeName.equals("DistanceFromCollision From Center")) {
            nextNodeName = "Effect";
        }
        
        if ( nextNodeName == null && "AreaEffect Close Message Group".equals(previousNodeName) == false) {
            nextNodeName = "AreaEffect Close Message Group";
        }
        return nextNodeName;
    }
    
    public void prepareBattleEvent() {
        String newGroup = getTargetGroup();
        // Create the group for this attack
        ActivationInfo ai = getBattleEvent().getActivationInfo();
        int tgindex = ai.addTargetGroup(newGroup);
        ai.setKnockbackGroup(tgindex, "KB");
        
        Target source = getBattleEvent().getSource();
        try {
            // Add all attack information which depends on the AttackParameterPanel
            
            BattleEngine.calculateDamage(battleEvent, battleEvent.getSource(), newGroup);
        }
        catch (BattleEventException bee) {
            
        }
        
        areaEffectAttackMessageGroup = new AreaEffectAttackMessageGroup(source);
        battleEvent.openMessageGroup(areaEffectAttackMessageGroup);
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

    public BattleMessageGroup getBattleMessageGroup() {
        return areaEffectAttackMessageGroup;
    }

    public boolean isExplosion() {
        return explosion;
    }

    public void setExplosion(boolean explosion) {
        this.explosion = explosion;
    }
    
}