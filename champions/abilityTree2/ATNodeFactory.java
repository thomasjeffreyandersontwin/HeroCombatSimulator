/*
 * ATNodeFactory.java
 *
 * Created on February 5, 2008, 5:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.abilityTree2;

import champions.Ability;
import champions.Battle;
import champions.Effect;
import champions.Roster;
import champions.Target;
import champions.interfaces.AbilityList;
import tjava.Filter;
import java.util.List;
import javax.swing.Action;

/**
 *
 * @author twalker
 */
public class ATNodeFactory {
    
    /** Creates a new instance of ATNodeFactory */
    public ATNodeFactory() {
    }
    
    public ATAbilityListNode createAbilityListNode(AbilityList abilityList, boolean flat, Filter<Object> nodeFilter, boolean pruned) {
        ATAbilityListNode node = new ATAbilityListNode(abilityList, flat, this, nodeFilter, pruned);
        node.setDeleteEnabled(true);
        return node;
    }
    
    public ATAbilityListNode createDefaultAbilitiesNode(boolean flat, Filter<Object> nodeFilter, boolean pruned) {
        AbilityList al = Battle.getDefaultAbilitiesOld();
        ATAbilityListNode node = new ATAbilityListNode(al, flat, this, nodeFilter, pruned);
        node.setName("Default Abilities");
        node.setDeleteEnabled(false);
        return node;
    }
    
    public ATAbilityNode createAbilityInstanceGroupNode(Ability ability, AbilityList abilityList, Filter<Object> nodeFilter, boolean pruned) {
        return new ATAbilityInstanceGroupNode(ability, abilityList, this, nodeFilter, pruned);
    }
    
    public ATAbilityNode createAbilityNode(Ability ability, AbilityList abilityList, Filter<Object> nodeFilter, boolean pruned) {
        return new ATAbilityNode(ability, abilityList, this, nodeFilter, pruned);
    }
    
    public ATActionNode createActionNode(Ability ability, Action action, Filter<Object> nodeFilter, boolean pruned) {
        return new ATActionNode(ability, action, this, nodeFilter, pruned);
    }
    
    /** Creates a node representing the active target and related information.
     *
     */	
    public ATNode createActiveTargetNode(Filter<Object> nodeFilter, boolean pruned) {
        return new ATActiveTargetNode(this, nodeFilter, pruned);
        //return null;
    }
    
    public ATTargetNode createTargetNode(Target target, boolean flat, Filter<Object> nodeFilter, boolean pruned) {
        return new ATTargetNode(target, flat, this, nodeFilter, pruned);
    }
    
    public ATCreateCharacterNode createCreateCharacterNode(Roster roster, Filter<Object> nodeFilter, boolean pruned) {
        return new ATCreateCharacterNode(roster, this, nodeFilter, pruned);
        //return null;
    }
    
    public VirtualDesktopActionNode createVirtualDesktopActionNode(Roster roster, Filter<Object> nodeFilter, boolean pruned, String action) {
        return new VirtualDesktopActionNode(roster, this, nodeFilter, pruned, action);
        //return null;
    }
    
    public ATMessageNode createMessageNode(String message, Filter<Object> nodeFilter, boolean pruned) {
        return new ATMessageNode(message, this, nodeFilter, pruned);
    }
    
    public ATRosterNode createRosterNode(Roster roster, Filter<Object> nodeFilter, boolean pruned) {
        return new ATRosterNode(roster, this, nodeFilter, pruned);
    }
    
    public ATRostersNode createRostersNode(Filter<Object> nodeFilter, boolean pruned) {
        return new ATRostersNode(this, nodeFilter, pruned);
    }
    
    public ATStatsNode createStatsNode(Target target, Filter<Object> nodeFilter, boolean pruned) {
        return new ATStatsNode(target, this, nodeFilter, pruned);
        //return null;
    }
    
    public ATStatNode createStatNode(Target target, String stat, Filter<Object> nodeFilter, boolean pruned) {
        return new ATStatNode(target, stat, this, nodeFilter, pruned);
    }
    
    public ATTargetActionsNode createTargetActionsNode(Target target, Filter<Object> nodeFilter, boolean pruned) {
        return new ATTargetActionsNode(target, this, nodeFilter, pruned);
    }
    
    public ATTargetEffectNode createTargetEffectNode(List<Effect> effectList, Filter<Object> nodeFilter, boolean pruned) {
        return new ATTargetEffectNode(effectList, this, nodeFilter, pruned);
        //return null;
    }
}
