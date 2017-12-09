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
public class ATVPPConfigurationFactory extends ATNodeFactory {
    
    /** Creates a new instance of ATAbilityListNodeFactory */
    public ATVPPConfigurationFactory() {
    }
    
    public ATAbilityListNode createAbilityListNode(AbilityList abilityList, boolean flat, Filter<Object> nodeFilter, boolean pruned) {
        return new ATAbilityListNode(abilityList, flat, this, nodeFilter, pruned);
    }

    @Override
    public ATAbilityNode createAbilityNode(Ability ability, AbilityList abilityList, Filter<Object> nodeFilter, boolean pruned) {
        ATAbilityNode n = new ATAbilityNode(ability, abilityList, this, nodeFilter, pruned);
        n.setAbilityRendererEnabled(false);
        return n;
    }
    
    

    public ATTargetNode createTargetNode(Target target, boolean flat, Filter<Object> nodeFilter, boolean pruned) {
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
