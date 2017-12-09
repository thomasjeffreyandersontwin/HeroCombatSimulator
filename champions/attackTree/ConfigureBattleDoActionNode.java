/*
 * ProcessActivateRoot.java
 *
 * Created on April 25, 2002, 2:17 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.ActivationInfo;
import champions.BattleEvent;
import champions.ConfigureBattleBattleEvent;
import champions.Target;
import champions.attackTree.ConfigureBattleActivationList.ConfigureBattleActivationListAction;

/**
 *
 * @author  Trevor Walker
 * @version
 */
public class ConfigureBattleDoActionNode extends DefaultAttackTreeNode {
    protected Target target;
    protected Ability ability;
    protected ConfigureBattleActivationListAction action;
    /** Creates new ProcessActivateRoot */
    public ConfigureBattleDoActionNode(BattleEvent be, String name, Target target, Ability ability, ConfigureBattleActivationListAction action) {
        super();
        setBattleEvent(be);
        this.name = name;
        
        this.target = target;
        this.ability = ability;
        this.action = action;
        
        setVisible(false);
        
        buildChildren();
    }
    
    public boolean activateNode(boolean manualOverride) {
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        
        // The root node should never accept active node status, since
        // It isn't really a real node.
        return false;
    }
    
    public void checkNodes() {
        // Do nothing here...
    }
    
    private void buildChildren() {
        if ( target != null && ability != null && action != null) {
            ProcessEmbeddedEventNode embeddedNode;
            
            boolean startOfBattle = ((ConfigureBattleBattleEvent)getBattleEvent()).isStartOfBattle();
            
            if ( action == ConfigureBattleActivationListAction.ACTIVATE ) {
                String s = "Activate " + target.getName() + "'s " + ability.getName();
                BattleEvent newBe = ability.getActivateAbilityBattleEvent(ability, null, target);
                if ( startOfBattle ) newBe.setENDPaid(true);
                newBe.setAbilityDelayed(false);
                newBe.setActivationTime("INSTANT",true);
                embeddedNode = new ProcessEmbeddedEventNode(s, newBe);
            } else {
                String s = "Deactivate " + target.getName() + "'s " + ability.getName();
                ActivationInfo ai = ability.getActivations(target).next();
                BattleEvent newBe = new BattleEvent(BattleEvent.DEACTIVATE, ai);
                if ( startOfBattle ) newBe.setENDPaid(true);
                embeddedNode = new ProcessEmbeddedEventNode(s, newBe);
            }
            addChild(embeddedNode, false);
        }
    }
    
    
    
}
