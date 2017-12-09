/*
 * PADAbilityListNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree2;

import champions.ActivationInfo;
import champions.Target;
import tjava.Filter;
import champions.interfaces.IndexIterator;
import java.util.Iterator;
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
public class ATExplosionDistanceRoot extends ATNode  {
    private ActivationInfo activationInfo;
    private String targetGroup;

   // private static RemoveAbilityListAction deleteAction = null;
   // private static DebugAction debugAction = null;
    
//    /** Creates new PADAbilityListNode */
//    public ATAbilityListNode(ATNodeFactory nodeFactory, AbilityList abilityList) {
//        this(nodeFactory, abilityList, null, false, false);
//    }
//    
//    /** Creates new PADAbilityListNode */
//    public ATAbilityListNode(ATNodeFactory nodeFactory, AbilityList abilityList, Filter<Object> nodeFilter) {
//        this(nodeFactory, abilityList, nodeFilter, false, false);
//    } 
    
    /** Creates new PADAbilityListNode */
    public ATExplosionDistanceRoot(ActivationInfo ai, String targetGroup, Filter<Object> nodeFilter) {
        super( new ATExplosionDistanceFactory(), nodeFilter, true);
        
        setActivationInfo(ai);
        setTargetGroup(targetGroup);
        
        setupIcons();
    }
    
    protected void setupIcons() {

    }
    
    protected void buildNode() {
        removeAndDestroyAllChildren();
        
        Iterator i;
        
        if ( getActivationInfo() != null && getTargetGroup() != null) {
            IndexIterator ii = getActivationInfo().getTargetGroupIterator(getTargetGroup());
            while(ii.hasNext()) {
                int index = ii.nextIndex();
                
                Target t = getActivationInfo().getTarget(index);
                
                if ( t != null && (nodeFilter == null || nodeFilter.includeElement(t)) ) {
                    ATNode n = nodeFactory.createTargetNode(t, true, nodeFilter, true);
                    add(n);
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
        
        setActivationInfo(null);
        setTargetGroup(null);
    }

    public ActivationInfo getActivationInfo() {
        return activationInfo;
    }

    public void setActivationInfo(ActivationInfo activationInfo) {
        this.activationInfo = activationInfo;
    }

    public String getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }


}
