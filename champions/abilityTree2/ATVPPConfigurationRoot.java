/*
 * PADAbilityListNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree2;

import champions.Ability;
import champions.FrameworkAbility;
import champions.Target;
import champions.interfaces.AbilityIterator;
import tjava.Filter;
import champions.interfaces.Framework;
import treeTable.DefaultTreeTableModel;




/**
 *
 * @author  twalker
 * @version
 *
 * PADAbilityListNode's hold references to Abilities stored in an AbilityList list.
 * It should not be used for powers/templates which are cached by the PADRoster
 * shared instance mechanism.
 */
public class ATVPPConfigurationRoot extends ATNode  {

    private Framework framework;
    private Target target;
    
    /** Creates new PADAbilityListNode */
    public ATVPPConfigurationRoot(Framework framework, Target target, Filter<Object> nodeFilter) {
        super( new ATVPPConfigurationFactory(), nodeFilter, true);
        
        setFramework(framework);
        setTarget(target);
        
        setupIcons();
    }
    
    protected void setupIcons() {

    }
    
    protected void buildNode() {
        removeAndDestroyAllChildren();
        
        if ( framework != null && target != null ) {
            
            AbilityIterator ai = target.getAbilities();
            while(ai.hasNext()) {
                Ability a = ai.next();
                
                if ( a instanceof FrameworkAbility == false && a.getFramework() == framework ) {
                    
                    ATNode node = nodeFactory.createAbilityNode(a, null, nodeFilter, pruned);
                    add(node);
                    
                }
            }
            
        }
        
        if ( model instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)model).nodeStructureChanged(this);
        }
    }
   
    
 
    
    public boolean isLeaf() {
        return false;
    }
    
   public boolean getAllowsChildren() {
        return true;
    }

    
 
    
    public void destroy() {
        super.destroy();
        
        setTarget(target);
        setFramework(framework);
        
    }

    public Framework getFramework() {
        return framework;
    }

    public void setFramework(Framework framework) {
        this.framework = framework;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public Target getTarget() {
        return target;
    }


}
