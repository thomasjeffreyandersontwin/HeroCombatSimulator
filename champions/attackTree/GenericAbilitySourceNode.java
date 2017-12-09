/*
 * GenericAbilitySourceNode.java
 *
 * Created on October 30, 2001, 12:14 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.ActivationInfo;
import champions.BattleEngine;
import champions.Target;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author  twalker
 * @version 
 */
public class GenericAbilitySourceNode extends DefaultAttackTreeNode {

    
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
    
    /** Creates new TestAttackTreeNode */
    public GenericAbilitySourceNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.abilitySourceIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        if ( updateAbility ) {
            Ability a = battleEvent.getAbility();
            if ( getAbility() != a ) {
                setAbility(a);
                if ( getModel() != null ) getModel().nodeChanged(this);
            }
        }
               
        if ( nodeRequiresInput() || manualOverride == true ) {
            GenericAbilitySourcePanel app = GenericAbilitySourcePanel.getGenericAbilitySourcePanel( ability );
            
            attackTreePanel.showInputPanel(this,app);
            attackTreePanel.setInstructions("Select Ability Source...");
            
            acceptActivation = true;
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        if ( ability == null || ability.getSource() == null ) {
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
        
        
        if ( getModel() != null ) getModel().nodeChanged(this);

        return true;
    }
    
    public String toString() {
        if ( ability == null ) {
            return "Source: ";
        }
        else {
            return "Source: " + ability.getSource();
        }
    }

    
    public String getAutoBypassOption() {
        return "SHOW_ABILITY_SOURCE_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return ability.getSource();
    }
    
    /** Getter for property ability.
     * @return Value of property ability.
     *
     */
    public champions.Ability getAbility() {
        return ability;
    }    

    /** Setter for property ability.
     * @param ability New value of property ability.
     *
     */
    public void setAbility(champions.Ability ability) {
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
