/*
 * GenericAbilitySelectNode.java
 *
 * Created on October 30, 2001, 12:14 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.GenericAbilityBattleEvent;
import champions.Target;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version 
 */
public class GenericAbilityConfigureNode extends DefaultAttackTreeNode {

    
    /** Holds the Ability to be edited.
     *
     * The ability to be edited could be either the Ability or the Maneuver from
     * a battleEvent, or a completely unrelated ability.
     */
    Ability ability;
    
    /** Determines whether Ability Modifiers are allowed to be modified.
     *
     * Ability Modifiers include Limitations, Advantages, Special Effects,
     * and Special Parameters.
     */
    boolean modifiersShown = false;
    
    /** Determines if the Ability should be updated from the BattleEvent each time.
     */
    boolean updateAbility = true;
    
    /** Determines if the Abilities current instance should be used.
     */
    boolean useCurrentInstance = false;
    
    /** Creates new TestAttackTreeNode */
    public GenericAbilityConfigureNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.configurePowerIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        if ( updateAbility ) {
            Ability a = battleEvent.getAbility();
            if ( useCurrentInstance ) {
                setAbility(a.getInstanceGroup().getCurrentInstance());
            }
            else {
                if ( a.isBaseInstance() == false  ) {
                    a = a.getInstanceGroup().getBaseInstance();
                }
                setAbility(a);
            }
        }
               
        if ( nodeRequiresInput() || manualOverride == true ) {
            GenericAbilityConfigurePanel app = GenericAbilityConfigurePanel.getGenericAbilityConfigurePanel( ability, modifiersShown );
            
            attackTreePanel.showInputPanel(this,app);
            attackTreePanel.setInstructions("Configure Ability...");
            
            acceptActivation = true;
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        if ( battleEvent instanceof GenericAbilityBattleEvent ) {
            requiresInput = (((GenericAbilityBattleEvent)battleEvent).isConfigured() == false);
        }
        else {
            requiresInput = true;
        }
        
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
        return "SHOW_CONFIGURE_ABILITY_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return ability.getSource();
    }
    
    /** Getter for property ability.
     * @return Value of property ability.
     *
     */
    public Ability getAbility() {
        return ability;
    }    

    /** Setter for property ability.
     * @param ability New value of property ability.
     *
     */
    public void setAbility(Ability ability) {
        this.ability = ability;
    }
    
    /** Getter for property modifiersShown.
     * @return Value of property modifiersShown.
     *
     */
    public boolean isModifiersShown() {
        return modifiersShown;
    }
    
    /** Setter for property modifiersShown.
     * @param modifiersShown New value of property modifiersShown.
     *
     */
    public void setModifiersShown(boolean modifiersShown) {
        this.modifiersShown = modifiersShown;
    }
    
}
