/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package champions.abilityTree2;

import champions.Ability;
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
public class ATSweepSetupNodeFactory extends ATNodeFactory {

    public ATSweepSetupNodeFactory() {
    }

    public ATAbilityListNode createAbilityListNode(AbilityList abilityList, boolean flat, Filter<Object> nodeFilter, boolean pruned) {
        ATAbilityListNode node = new ATAbilityListNode(abilityList, flat, this, nodeFilter, pruned);
        if (abilityList != null && abilityList.getSource() != null) {
            node.setName(abilityList.getSource().getName() + "'s Abilities");
        }
        node.setDeleteEnabled(false);

        return node;
    }

    public ATTargetNode createTargetNode(Target target, boolean flat, Filter<Object> nodeFilter, boolean pruned) {
        ATTargetNode node = new ATTargetNode(target, flat, this, nodeFilter, pruned);
        node.setExpandedByDefault(true);
        node.setExpanded(true);
        return node;
    }

    public ATAbilityNode createAbilityNode(Ability ability, AbilityList abilityList, Filter<Object> nodeFilter, boolean pruned) {
        ATAbilityNode node = new ATAbilityNode(ability, abilityList, this, nodeFilter, pruned);
        node.setAbilityRendererEnabled(false);
        return node;
    }

    public ATAbilityNode createAbilityInstanceGroupNode(Ability ability, AbilityList abilityList, Filter<Object> nodeFilter, boolean pruned) {
        ATAbilityInstanceGroupNode node = new ATAbilityInstanceGroupNode(ability, abilityList, this, nodeFilter, pruned);
        node.setAbilityRendererEnabled(false);
        return node;
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

    public ATTargetEffectNode createTargetEffectNode(List<Effect> effectList, Filter<Object> nodeFilter, boolean pruned) {
        return null;
    }

    public ATTargetActionsNode createTargetActionsNode(Target target, Filter<Object> nodeFilter, boolean pruned) {
        return null;
    }

    public ATActionNode createActionNode(Ability ability, Action action, Filter<Object> nodeFilter, boolean pruned) {
        return null;
    }
}
