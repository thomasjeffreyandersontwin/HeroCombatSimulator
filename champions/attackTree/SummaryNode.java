/*
 * SummaryNode.java
 *
 * Created on November 8, 2001, 4:54 PM
 */

package champions.attackTree;

import javax.swing.UIManager;

import VirtualDesktop.Mob.MobEffect;
import champions.Ability;
import champions.Roster;
import champions.Target;

/**
 *
 * @author  twalker
 * @version
 */
public class SummaryNode extends DefaultAttackTreeNode {
    
    public static SummaryNode Node;

	/** Creates new SummaryNode */
    public SummaryNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.summaryIcon");
        Node = this;
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean activateNode = false;
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        
        if ( nodeRequiresInput() || manualOverride == true ) {
            
         //   MessagePanel mp = MessagePanel.getDefaultPanel(battleEvent, battleEvent.getSource(), 0, Integer.MAX_VALUE);
            if(attackTreePanel==null) {
            	attackTreePanel= AttackTreePanel.defaultAttackTreePanel;
            }
            attackTreePanel.showInputPanel(this,null);
            attackTreePanel.setInstructions("Hit Okay to finish...");
            
            activateNode = true;
        }
        
        if(getBattleEvent().getActivationInfo()!=null)
        {
        	Target attacker = getBattleEvent().getActivationInfo().getSource();
        	Roster r = attacker.getRoster();
        	Ability a=getBattleEvent().getActivationInfo().getAbility();
        	if(r!=null &&r.MobMode==true && attacker==r.MobLeader  && a!=null) {
        		MobEffect me = (MobEffect) attacker.getEffect("Mob Effect");
        		me.ActivationInfo = getBattleEvent().getActivationInfo();
			
	    	}
        }
        
        return activateNode;
    }
    
    private boolean nodeRequiresInput(){
        boolean requiresInput = false;
        
      //  return battleEvent.getSource().getBooleanProfileOption("SHOW_SUMMARY_PANEL");
        return (battleEvent != null && (battleEvent.isAlwaysShowAttackTree() || battleEvent.isReprocessingEvent())) || model.isAttackTreePanelVisible();
       // return true;
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
        //System.out.println("Node " + name + " exited.");
        return true;
    }
    
}
