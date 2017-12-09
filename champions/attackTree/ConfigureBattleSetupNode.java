/*
 * GenericAbilitySelectNode.java
 *
 * Created on October 30, 2001, 12:14 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.Battle;
import champions.ConfigureBattleBattleEvent;
import champions.GenericAbilityBattleEvent;
import champions.ProfileManager;
import champions.Roster;
import champions.Target;
import champions.interfaces.AbilityIterator;
import java.util.Collection;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version 
 */
public class ConfigureBattleSetupNode extends DefaultAttackTreeNode {

    /** Creates new TestAttackTreeNode */
    public ConfigureBattleSetupNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.configurePowerIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        setupActivationList();
               
        if ( nodeRequiresInput() || manualOverride == true ) {
            
            Roster r = ((ConfigureBattleBattleEvent)getBattleEvent()).getRoster();
            
            ConfigureBattleSetupPanel app = ConfigureBattleSetupPanel.getGenericAbilityConfigurePanel(r, getActivationList(), isStartOfBattle() );
            
            attackTreePanel.showInputPanel(this,app);
            attackTreePanel.setInstructions("Configure abilities to start/stop...");
            
            acceptActivation = true;
        } 
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        requiresInput = ProfileManager.getDefaultProfile().getBooleanProfileOption(getAutoBypassOption());
            
        
        return requiresInput;
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
        
        if ( battleEvent instanceof GenericAbilityBattleEvent ) {
            ((GenericAbilityBattleEvent)battleEvent).setConfigured(true);
        }

        return true;
    }

    
    public String getAutoBypassOption() {
        return "SHOW_CONFIGURE_BATTLE_SETUP_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return null;
    }
    
    protected boolean isStartOfBattle() {
        return ((ConfigureBattleBattleEvent)getBattleEvent()).isStartOfBattle();
    }

    protected ConfigureBattleActivationList getActivationList() {
        return ((ConfigureBattleBattleEvent)getBattleEvent()).getActivationList();
    }

    protected void setActivationList(ConfigureBattleActivationList activationList) {
        ((ConfigureBattleBattleEvent)getBattleEvent()).setActivationList(activationList);
    }

    protected void setupActivationList() {
        if ( getActivationList() == null ) {
            ConfigureBattleActivationList al = new ConfigureBattleActivationList();
            setActivationList(al);
            
            boolean startOfBattle = ((ConfigureBattleBattleEvent)getBattleEvent()).isStartOfBattle();
            
            // The activation list only contains the entries that are actually being 
            // activated.  The tree determines the which ability can be activated 
            // (see ConfigureBattleSetupPanel).
            if ( startOfBattle ) {
                Collection<Target> targets;
                Roster r = ((ConfigureBattleBattleEvent)getBattleEvent()).getRoster();

                if ( r == null ) {
                    targets = Battle.currentBattle.getCombatants();
                }
                else {
                    targets = r.getCombatants();
                }

                for(Target target : targets) {
                    AbilityIterator ai = target.getAbilities();
                    while(ai.hasNext()) {
                        Ability a = ai.next();

                        if ( a.isNormallyOn() && a.isActivated(target) == false && a.isDelayActivating(target) == false  ) {
                            al.setAction(target, a, ConfigureBattleActivationList.ConfigureBattleActivationListAction.ACTIVATE);
                        }
                    }
                }
            }
        }
    }
}
