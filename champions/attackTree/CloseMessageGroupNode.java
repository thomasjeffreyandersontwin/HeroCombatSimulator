/*
 * KnockbackTargetNode.java
 *
 * Created on November 8, 2001, 11:59 AM
 */

package champions.attackTree;

import champions.battleMessage.BattleMessageGroup;

/**
 *
 * @author  twalker
 * @version
 */
public class CloseMessageGroupNode extends DefaultAttackTreeNode {

    
    private BattleMessageGroupProvider messageGroupProvider;
    
    /** Creates new KnockbackTargetNode */
    public CloseMessageGroupNode(String name, BattleMessageGroupProvider messageGroupProvider) {
        this.name = name;
        this.messageGroupProvider = messageGroupProvider;
        
        setVisible(false);
    }
    
    public boolean activateNode(boolean manualOverride) {
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        
        boolean activateNode = false;
        
        if ( messageGroupProvider != null ) {
            BattleMessageGroup bmg = messageGroupProvider.getBattleMessageGroup();
            if ( bmg != null ) {
                if (battleEvent.isMessageGroupOpen(bmg)) {
                    battleEvent.closeMessageGroup(bmg);
                }
            }
        }
        
        // The KnockbackTargetNode node should never accept active node status, since
        // it isn't really a real node.
        return activateNode;
    }
    
    
    
}
