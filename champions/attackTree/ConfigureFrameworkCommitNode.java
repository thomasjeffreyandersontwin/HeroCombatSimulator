/*
 * GenericAbilitySelectNode.java
 *
 * Created on October 30, 2001, 12:14 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.AbilityKey;
import champions.BattleEngine;
import champions.ConfigureFrameworkBattleEvent;
import champions.Target;
import champions.VariablePointPoolAbilityConfiguration.ConfigurationEntry;
import java.util.Map;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version 
 */
public class ConfigureFrameworkCommitNode extends DefaultAttackTreeNode {

    Map<AbilityKey, ConfigurationEntry> configEntries;

    /** Creates new TestAttackTreeNode */
    public ConfigureFrameworkCommitNode(String name, Map<AbilityKey, ConfigurationEntry> configEntries) {
        this.name = name;
        this.configEntries = configEntries;
        icon = UIManager.getIcon("Framework.DefaultIcon");

        setVisible(true);
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = manualOverride;

        String text = "Apply Configuration Changes\n\n" +
                    "This node contains deactivation information for any abilities that " +
                    "were enabled in the framework and were currently activated.\n\n" +
                    "This node may have no children if either no abilities were disabled or " +
                    "none of the disabled abilities were active.";

            InformationPanel app = InformationPanel.getDefaultPanel( text );

            attackTreePanel.showInputPanel(this,app);
            attackTreePanel.setInstructions("Hit Okay to Continue...");
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }

    @Override
    public void checkNodes() {

        int childIndex = 0;

        for (Map.Entry<AbilityKey, ConfigurationEntry> entry : configEntries.entrySet()) {
            if ( childIndex < getChildCount() ) {
                // There is a node here, so lets make sure it is the one we want...
                ConfigureFrameworkApplyNode node = (ConfigureFrameworkApplyNode)children.get(childIndex);
                if ( node.ability == entry.getKey().getAbility() && node.entry == entry.getValue() ) {
                    // This node is alright, so advance to the next node
                }
                else {
                    // This node is not the correct one, so see if there is one that is already good
                    int nodeIndex = findNodeFor(entry.getKey().getAbility(), entry.getValue(), childIndex+1);
                    if ( nodeIndex != -1 ) {
                        // We found an existing node, so just move it...
                        Object o = children.remove(nodeIndex);
                        children.add(childIndex, o);
                    }
                    else {
                        // We didn't find an existing one, so just create it and add it...
                        node = new ConfigureFrameworkApplyNode(name, entry.getKey().getAbility(), entry.getValue());
                        addChild(node, childIndex, true);
                    }
                }
                
            }
            else {
                // We are passed the end of the current children, so just create the nodes and add it...
                ConfigureFrameworkApplyNode node = new ConfigureFrameworkApplyNode(name, entry.getKey().getAbility(), entry.getValue());
                addChild(node);
            }
            childIndex++;
        }

        // Now remove any left over nodes, since they aren't supposed to be there
        for(int i = children.size() -1; i >= childIndex; i--) {
            removeChild((AttackTreeNode)children.get(i));
        }
    }

    protected int findNodeFor(Ability ability, ConfigurationEntry entry, int startIndex) {
        for (int i = startIndex; i < getChildCount(); i++) {
            ConfigureFrameworkApplyNode node = (ConfigureFrameworkApplyNode)children.get(i);

            if ( node.ability == ability && node.entry == entry ) {
                return i;
            }
        }

        return -1;
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
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " exited.");

//        // We want to set the pending configuration and skill roll if necessary...
//        Framework framework = getBattleEvent().getFramework();
//        VariablePointPoolAbilityConfiguration config = framework.getConfiguration();
//        Target source = battleEvent.getSource();
//
//        SkillBasedSkillRollInfo sri = (SkillBasedSkillRollInfo)battleEvent.getSkillRollInfo("FrameworkSkillRoll", getTargetGroup());
//
//        if ( sri == null || sri.isSuccessful()) {
//            config.applyActions(battleEvent);
//        }
//        else {
//            config.failActions(battleEvent);
//        }
//
        BattleEngine.adjustSourceCombatStatus(battleEvent, getBattleEvent().getSource());
        
        return true;
    }

    
    public String getAutoBypassOption() {
        return null;
    }
    
    public Target getAutoBypassTarget() {
        return null;
    }
    
    public ConfigureFrameworkBattleEvent getBattleEvent() {
        return (ConfigureFrameworkBattleEvent)battleEvent;
    }
}
