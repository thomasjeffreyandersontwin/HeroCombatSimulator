/*
 * ATAbilityListNodeFactory.java
 *
 * Created on February 5, 2008, 11:35 PM
 *
 * To change this template, choose Tools | Template Manager
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
public class ATExplosionDistanceFactory extends ATNodeFactory {
    
    /** Creates a new instance of ATAbilityListNodeFactory */
    public ATExplosionDistanceFactory() {
    }
    
    public ATAbilityListNode createAbilityListNode(AbilityList abilityList, boolean flat, Filter<Object> nodeFilter, boolean pruned) {
        return null;
    }

    public ATTargetNode createTargetNode(Target target, boolean flat, Filter<Object> nodeFilter, boolean pruned) {
        ATTargetNode node = new ATTargetNode(target, flat, this, nodeFilter, pruned);
        node.setEffectEnabled(false);
        node.setHighlightActiveTarget(false);
        node.setIncludeAbilityNode(false);
        node.setExpandedByDefault(false);
        node.setExpanded(false);
        node.setEditEnabled(false);
        
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

    @Override
    public ATAbilityListNode createDefaultAbilitiesNode(boolean flat, Filter<Object> nodeFilter, boolean pruned) {
        return null;
    }
    
    
            
}
