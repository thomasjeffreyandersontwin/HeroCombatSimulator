/*
 * ATConfigureBattleNodeFactory.java
 *
 * Created on February 6, 2008, 8:07 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.abilityTree2;

import champions.Ability;
import champions.Roster;
import champions.Target;
import champions.interfaces.AbilityList;
import tjava.Filter;
import javax.swing.Action;

/**
 *
 * @author twalker
 */
public class ATConfigureBattleNodeFactory extends ATNodeFactory {
    
    /** Creates a new instance of ATConfigureBattleNodeFactory */
    public ATConfigureBattleNodeFactory() {
    }

    public ATAbilityListNode createAbilityListNode(AbilityList abilityList, boolean flat, Filter<Object> nodeFilter, boolean pruned) {
        ATAbilityListNode node = new ATAbilityListNode(abilityList, false, this, nodeFilter, pruned);
        node.setDeleteEnabled(false);
        return node;
    }

    public ATAbilityListNode createDefaultAbilitiesNode(boolean flat, Filter<Object> nodeFilter, boolean pruned) {
        return null;
    }

    public ATAbilityNode createAbilityInstanceGroupNode(Ability ability, AbilityList abilityList, Filter<Object> nodeFilter, boolean pruned) {
        ATAbilityNode node = new ATConfigureBattleAbilityNode(ability,abilityList,this,nodeFilter,pruned);
        return node;
    }
    


    public ATActionNode createActionNode(Ability ability, Action action, Filter<Object> nodeFilter, boolean pruned) {
        return null;
    }

    public ATNode createActiveTargetNode(Filter<Object> nodeFilter, boolean pruned) {
        return null;
    }

    public ATCreateCharacterNode createCreateCharacterNode(Roster roster, Filter<Object> nodeFilter, boolean pruned) {
        return null;
    }

    public ATStatsNode createStatsNode(Target target, Filter<Object> nodeFilter, boolean pruned) {
        return null;
    }

    public ATTargetNode createTargetNode(Target target, boolean flat, Filter<Object> nodeFilter, boolean pruned) {
        ATTargetNode node = new ATTargetNode(target, true, this, nodeFilter, pruned);
        node.setEffectEnabled(false);
        return node;
    }

    public ATTargetActionsNode createTargetActionsNode(Target target, Filter<Object> nodeFilter, boolean pruned) {
        return null;
    }
    


    

    
}
