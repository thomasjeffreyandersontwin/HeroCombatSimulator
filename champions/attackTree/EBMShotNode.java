/*
 * EBMShotNode.java
 *
 * Created on January 19, 2002, 6:14 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.ActivationInfo;
import champions.BattleEvent;
import champions.Target;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class EBMShotNode extends DefaultAttackTreeNode {
    /** Holds value of property shot. */
    private int shot;
    
    /** Holds value of property firstShotTargetGroup. */
    private String firstShotTargetGroup;
    
    /** Creates new AFShotNode */
    public EBMShotNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.hexIcon");
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
            BattleEvent be = getBattleEvent();
            Ability ability = be.getAbility();
            ActivationInfo ai = be.getActivationInfo();
            
            nextNodeName = "Single Attack";
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
        
        // Reset the DCCalculated Flag, so things get recalculated appropriately.
        ai.addIndexed(tgindex, "TargetGroup", "DCCALCULATED", "FALSE", true);
        
        Integer width = ai.getIntegerValue("Attack.SPREADWIDTH");
        ai.setTargetGroupDCModifier(targetGroup, -1 * width.intValue() );
        
        //   CVList cvl = ai.getCVList(tindex);
        //   cvl.addSourceCVModifier("Autofire Penalty", -1 * sprayWidth.intValue());
    }
}
