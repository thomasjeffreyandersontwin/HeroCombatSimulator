/*
 * TransferPostTriggerNode.java
 *
 * Created on November 20, 2001, 2:44 PM
 */

package champions.adjustmentPowers.adjustmentTree;

import champions.Target;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.DefaultAttackTreeNode;
import champions.attackTree.MessagePanel;
import champions.powers.powerTransfer;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class TransferPostTriggerNode extends DefaultAttackTreeNode {
    
    private int startMessageCount;
    private int endMessageCount;
    
    private boolean firstPass = true;
    
    
    /**
     * Creates new TransferPostTriggerNode
     */
    public TransferPostTriggerNode(String name) {
        this.name = name;
        //setVisible(true);
        icon = UIManager.getIcon("AttackTree.summaryIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean activateNode = false;
        
        // The root node should never accept active node status, since
        // It isn't really a real node.
        // Build a ToHit node and add it
        startMessageCount = battleEvent.getMessageCount();
        
        powerTransfer power = (powerTransfer)battleEvent.getAbility().getPower();
        power.generateTransferEffects(battleEvent);
        
        endMessageCount = battleEvent.getMessageCount();
        
      /*  if ( startMessageCount == endMessageCount ) {
            battleEvent.addBattleMessage( new champions.battleMessage.SimpleBattleMessage( target.getName() + " was not affected.", MSG_COMBAT)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage( target.getName() + " was not affected.", MSG_COMBAT)); // .addMessage( target.getName() + " was not affected.", MSG_COMBAT);
            endMessageCount = battleEvent.getMessageCount();
        } */
        
        if ( nodeRequiresInput() || manualOverride == true ) {
            MessagePanel mp = MessagePanel.getDefaultPanel(battleEvent, battleEvent.getSource(), startMessageCount, endMessageCount);
            attackTreePanel.showInputPanel(this,mp);
            attackTreePanel.setInstructions("Hit Okay to Continue...");
            
            activateNode = true;
        }
        
        return activateNode;
    }
    
    private boolean nodeRequiresInput(){
        boolean requiresInput = false;
        
        if ( startMessageCount < endMessageCount ) {
            requiresInput = true;
        }
        return requiresInput && firstPass && getAutoBypassValue();
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
        
        firstPass = false;
        
        return true;
    }
    
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        
        AttackTreeNode nextNode = null;
        
        return nextNode;
    }

    public String toString() {
        //return getName() + " - " + getTarget();
        return "Transfer Effects for " + battleEvent.getSource();
    }

    public String getAutoBypassOption() {
        return "SHOW_APPLY_EFFECTS_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
    }
}
