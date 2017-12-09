/*
 * ATSingleTargetTree.java
 *
 * Created on June 8, 2004, 10:34 PM
 */

package champions.abilityTree2;

import champions.battleMessage.BattleMessage;

/**
 *
 * @author  1425
 */
public class ATBattleMessageTree extends ATAbilityListTree {
    
    private BattleMessage primaryBattleMessage;
//    
//    /** Holds the Sublist name to show. */
//    private String sublistName;
    
    /**
     * Creates a new instance of ATSingleTargetTree
     */
    public ATBattleMessageTree() {
    }
    
    protected void setupModel() {
        ATNode root = new ATBattleMessageRootNode(primaryBattleMessage, null);
        ATModel model = new ATBattleMessageModel(root);
        setTreeTableModel(model);
        root.setTree(this);
    }

    public BattleMessage getPrimaryBattleMessage() {
        return primaryBattleMessage;
    }

    public void setPrimaryBattleMessage(BattleMessage primaryBattleMessage) {

            this.primaryBattleMessage = primaryBattleMessage;
            
            ATNode root = new ATBattleMessageRootNode(primaryBattleMessage, null);
            ATModel model = new ATBattleMessageModel(root);
            setTreeTableModel(model);
            root.setTree(this);
    }
    

}
