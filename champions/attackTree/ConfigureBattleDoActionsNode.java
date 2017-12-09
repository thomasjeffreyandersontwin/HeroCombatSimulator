/*
 * ProcessActivateRoot.java
 *
 * Created on April 25, 2002, 2:17 PM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.BattleEvent;
import champions.attackTree.ConfigureBattleActivationList.ConfigureBattleActivationListAction;
import champions.attackTree.ConfigureBattleActivationList.ConfigureBattleActivationListTAAEntry;
import java.util.List;

/**
 *
 * @author  Trevor Walker
 * @version
 */
public class ConfigureBattleDoActionsNode extends DefaultAttackTreeNode {
    
    protected ConfigureBattleActivationList activationList;
    protected List<ConfigureBattleActivationListTAAEntry> taaList;
    
    /** Creates new ProcessActivateRoot */
    public ConfigureBattleDoActionsNode(String name, ConfigureBattleActivationList activationList) {
        super();
        this.name = name;
        
        this.activationList = activationList;
        this.taaList = activationList.getActionList();
        
        setVisible(false);
    }
    
    public boolean activateNode(boolean manualOverride) {
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        
        // Recreate the taaList, in case it changed
        this.taaList = activationList.getActionList();
        
        // We are hidden and should never accept activation...
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
                int index = Integer.parseInt(nextNodeName);
                
                ConfigureBattleActivationListTAAEntry taa = taaList.get(index);
                
                nextNode = new ConfigureBattleDoActionNode(getBattleEvent(), Integer.toString(index), taa.target, taa.ability, taa.action);
            }
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        int index = 0;
        
        if ( previousNodeName != null ) {
            index = Integer.parseInt(previousNodeName) + 1;
        }
        
        if ( index < taaList.size() ) {
            nextNodeName = Integer.toString( index );
        }
        
        return nextNodeName;
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
        // This is called prior to any advancement of the nodes.
        
        // Grab the Active Node, then use it to find which child it is a descendent of...
        if ( children != null ) {
            
            int cindex;
            boolean destroy = false;
            
            for(cindex = 0; cindex < children.size(); cindex++) {
                
                if ( cindex >= taaList.size() ) {
                    destroy = true;
                    break;
                }
                
                ConfigureBattleActivationListTAAEntry taa = taaList.get(cindex);
                ConfigureBattleDoActionNode node = (ConfigureBattleDoActionNode)children.get(cindex);
                
                if ( taa.target != node.target || taa.ability != node.ability || taa.action != node.action ) {
                    destroy = true;
                    break;
                }
            }
            
            
            if ( destroy ) {
                // There was a problem at cindex, so destory it and all
                // children after it...
                int index = children.size() - 1;
                for(; cindex <= index; index -- ) {
                    AttackTreeNode atn = (AttackTreeNode)children.get(index);
                    removeChild(atn);
                }
            }
        }
    }
    
}
