/*
 * ATAbilityInstanceGroup.java
 *
 * Created on June 3, 2004, 11:09 PM
 */

package champions.abilityTree2;

import champions.Ability;
import champions.AbilityInstanceGroup;
import champions.event.AbilityAddedEvent;
import champions.event.AbilityRemovedEvent;
import champions.event.ActivationStateChangeEvent;
import champions.event.InstanceChangedEvent;
import champions.interfaces.AbilityInstanceGroupListener;
import champions.interfaces.AbilityList;
import champions.interfaces.ChampionsConstants;
import tjava.Filter;
import java.util.Iterator;
import treeTable.DefaultTreeTableModel;

/**
 *
 * @author  1425
 */
public class ATAbilityInstanceGroupNode extends ATAbilityNode
        implements AbilityInstanceGroupListener, ChampionsConstants {
    
    protected AbilityInstanceGroup abilityInstanceGroup;
    
    
    /** Creates a new instance of ATAbilityInstanceGroup.
     *
     * The baseAbility will be used to extract the AbilityInstanceGroup.  The
     * actual ability held in the Ability
     */
    public ATAbilityInstanceGroupNode(Ability baseAbility, AbilityList abilityList, ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned) {
        super(null, abilityList, nodeFactory, nodeFilter, pruned);
        setAbilityInstanceGroup(baseAbility.getInstanceGroup());
        setExpandedByDefault(true);
    }
    
    /**
     * Getter for property abilityInstanceGroup.
     * @return Value of property abilityInstanceGroup.
     */
    public AbilityInstanceGroup getAbilityInstanceGroup() {
        return abilityInstanceGroup;
    }
    
    /**
     * Setter for property abilityInstanceGroup.
     * @param abilityInstanceGroup New value of property abilityInstanceGroup.
     */
    public void setAbilityInstanceGroup(AbilityInstanceGroup abilityInstanceGroup) {
        if (this.abilityInstanceGroup != abilityInstanceGroup) {
            if ( this.abilityInstanceGroup != null ) {
                this.abilityInstanceGroup.removeAbilityInstanceGroupListener(this);
            }
            
            this.abilityInstanceGroup = abilityInstanceGroup;
            
            
            if ( this.abilityInstanceGroup != null ) {
                rebuildNode();
                this.abilityInstanceGroup.addAbilityInstanceGroupListener(this);
            }
            
        }
    }
    
    protected void buildNode() {
        removeAndDestroyAllChildren();
        
        if ( abilityInstanceGroup != null ) {
            
            // First look for an activated instance...
            Ability activeAbility = null;
            Iterator<Ability> it;
            it = abilityInstanceGroup.getInstances();
            while ( it.hasNext() ) {
                Ability a = it.next();
                if ( a.isModifiableInstance() && a.isActivated(null) ) {
                    activeAbility = a;
                    break;
                }
            }
            
            if ( activeAbility == null ) {
                Ability currentAbility = abilityInstanceGroup.getCurrentInstance();
                setAbility(currentAbility, false);
                
                addActions();
                
                // build the other templates...
                it = abilityInstanceGroup.getInstances();
                while ( it.hasNext() ) {
                    Ability a = it.next();
                    if ( a.isModifiableInstance() && a != currentAbility ) {
                        ATNode node = nodeFactory.createAbilityNode(a, abilityList, nodeFilter, pruned);
                        if ( node != null ) {
                            add(node);
                        }
                    }
                }
            } else {
                setAbility(activeAbility, false);
                
                addActions();
            }
            
        }
        if ( model instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)model).nodeStructureChanged(this);
        }
        
//        if ( tree instanceof AbilityTree2 ) {
//            ((AbilityTree2)tree).expandAll( new TreePath( getPath() ) );
//        }
    }
    
    public void abilityAdded(AbilityAddedEvent evt) {
        //if ( getTree() != null && getTree().isShowing() == false ) return;
        rebuildNode();
    }
    
    public void abilityRemove(AbilityRemovedEvent evt) {
        //if ( getTree() != null && getTree().isShowing() == false ) return;
        rebuildNode();
    }
    
    public void instanceChanged(InstanceChangedEvent evt) {
        //if ( getTree() != null && getTree().isShowing() == false ) return;
        
        if ( evt.getType() == InstanceChangedEvent.CURRENT_INSTANCE ) {
            rebuildNode();
        }
        
    }
    
    public void activationStateChanged(ActivationStateChangeEvent evt) {
//        if ( evt.getNewState() != null &&
//            (evt.getNewState().equals(AI_STATE_ACTIVATED)
//             || evt.getNewState().equals(AI_STATE_ACTIVATION_FAILED)
//             || evt.getNewState().equals(AI_STATE_DEACTIVATED))) {
//            //buildChildren();
//            rebuildNode();
//        }
        rebuildNode();
    }
    
    public void destroy() {
        super.destroy();
        
        setAbilityInstanceGroup(null);
        setAbility(null);
        setAbilityList(null);
    }
}
