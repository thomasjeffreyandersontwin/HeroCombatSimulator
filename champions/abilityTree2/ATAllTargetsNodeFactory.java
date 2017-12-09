/*
 * ATAllTargetsNodeFactory.java
 *
 * Created on February 5, 2008, 11:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.abilityTree2;

import champions.Target;
import champions.interfaces.AbilityList;
import tjava.Filter;

/**
 *
 * @author twalker
 */
public class ATAllTargetsNodeFactory extends ATNodeFactory {
    
    /**
     * Creates a new instance of ATAllTargetsNodeFactory
     */
    public ATAllTargetsNodeFactory() {
        
    }

    public ATAbilityListNode createDefaultAbilitiesNode(boolean flat, Filter<Object> nodeFilter, boolean pruned) {
        return null;
    }

    public ATTargetNode createTargetNode(Target target, boolean flat, Filter<Object> nodeFilter, boolean pruned) {
        ATTargetNode node = new ATTargetNode(target, flat, this, nodeFilter, pruned);
        node.setEffectEnabled(true);
        node.setExpandedByDefault(false);
        return node;
    }
    

    
}
