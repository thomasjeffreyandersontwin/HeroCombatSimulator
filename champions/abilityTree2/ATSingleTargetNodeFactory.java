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
public class ATSingleTargetNodeFactory extends ATNodeFactory {
    
    /** Creates a new instance of ATAbilityListNodeFactory */
    public ATSingleTargetNodeFactory() {
    }
    
    public ATAbilityListNode createAbilityListNode(AbilityList abilityList, boolean flat, Filter<Object> nodeFilter, boolean pruned) {
        ATAbilityListNode node = new ATAbilityListNode(abilityList, flat, this, nodeFilter, pruned);
        if ( abilityList.getSource() != null ) {
            node.setName(abilityList.getSource().getName() + "'s Abilities");
        }
        node.setDeleteEnabled(true);
        return node;
    }

    public ATTargetNode createTargetNode(Target target, boolean flat, Filter<Object> nodeFilter, boolean pruned) {
        ATTargetNode node = new ATTargetNode(target, flat, this, nodeFilter, pruned);
        node.setExpandedByDefault(true);
        node.setExpanded(true);
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
    
    
   
}
