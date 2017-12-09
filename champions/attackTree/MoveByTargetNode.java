/*
 * MoveByTargetNode.java
 *
 * Created on January 21, 2002, 9:54 AM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.CVList;
import champions.Target;

/**
 *
 * @author  twalker
 * @version
 */
public class MoveByTargetNode extends DefaultAttackTreeNode {
    
    /** Holds value of property shot. */
    private int shot;
    
    private String previousTargetGroup;
    
    /** Creates new MoveByTargetNode */
    public MoveByTargetNode(String name) {
        this.name = name;
        setVisible(true);
    }
    
    public boolean activateNode(boolean manualOverride) {
        // Whenever this node is activated, it need to gaurantee that the battleEvent is prepared correct.
        prepareBattleEvent();
        
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
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Move-By Effect")) {
                    MoveByEffectNode node = new MoveByEffectNode(nextNodeName);
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
            nextNodeName = "Single Attack";
        }
        else if ( previousNodeName.equals("Single Attack")) {
            ActivationInfo ai = battleEvent.getActivationInfo();
            if ( ai.getTargetGroupHasHitTargets( getTargetGroup() ) ){
                nextNodeName = "Move-By Effect";
            }
        }
        return nextNodeName;
    }
    
    /** Getter for property shot.
     * @return Value of property shot.
     */
    public int getShot() {
        return shot;
    }
    
    /** Setter for property shot.
     * @param shot New value of property shot.
     */
    public void setShot(int shot) {
        this.shot = shot;
    }
    
    public void prepareBattleEvent() {
        // Create the group for this attack
        ActivationInfo ai = getBattleEvent().getActivationInfo();
        Target source = battleEvent.getSource();
        
        String targetGroup = getTargetGroup();
        // Create the Group for this attack, in case it doesn't exist yet or if we just removed it.
        int tgindex = ai.addTargetGroup(targetGroup);
        
        // Add the first shot target with the reference number of 0 (primary target).
        int tindex = ai.addTarget(null,targetGroup, 0);
        
        Integer movebyTargets = ai.getIntegerValue("Attack.MOVEBYTARGETS");
        if ( movebyTargets != null && movebyTargets.intValue() > 1 ) {
            CVList cvl = ai.getCVList(tindex);
            cvl.addSourceCVModifier("Multiple Move-By Penalty", -2 * movebyTargets.intValue() + 2);
        }
        
        if ( shot > 1 ) {
            int ptindex = ai.getTargetIndex(0, previousTargetGroup);
            if ( ai.getTargetHit(ptindex) == false ) {
                // The previous target was missed, so this target can not be hit either.
                String explination;
                //Target target = ai.getTarget(tindex);
                
                explination = "Target was missed automatically by " + source.getName() + ".\n\n"
                + "This was Target " + Integer.toString(shot) + " of a Multiple Move-By Attack.  A previous target " 
                + "of this Move-By attack was missed, forcing all additional targets to be missed.";
                
                String reason = "Multiple Move-By Target " + Integer.toString(shot) + " automatically missed due to previous miss.";
                
                ai.setTargetHitOverride(tindex, false, reason, explination);
            }
            else {
                ai.clearTargetHitOverride(tindex);
            }
        }
        
    }
    
    /** Getter for property firstShotTargetGroup.
     * @return Value of property firstShotTargetGroup.
     */
    public String getPreviousTargetGroup() {
        return previousTargetGroup;
    }
    
    /** Setter for property firstShotTargetGroup.
     * @param firstShotTargetGroup New value of property firstShotTargetGroup.
     */
    public void setPreviousTargetGroup(String previousTargetGroup) {
        this.previousTargetGroup = previousTargetGroup;
    }
}
