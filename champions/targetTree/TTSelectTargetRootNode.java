/*
 * PADTargetListNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.targetTree;
import champions.Battle;
import champions.Target;
import champions.TargetList;
import tjava.Filter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import treeTable.DefaultTreeTableModel;
import treeTable.TreeTableModel;



/**
 *
 * @author  twalker
 * @version
 *
 * PADTargetListNode's hold references to Abilities stored in an TargetList list.
 * It should not be used for powers/templates which are cached by the PADRoster
 * shared instance mechanism.
 */
public class TTSelectTargetRootNode extends TTNode
        implements PropertyChangeListener, ChangeListener {
    
    /** Creates new PADTargetListNode */
    public TTSelectTargetRootNode(TreeTableModel model) {
        setModel(model);
        buildNode();
    }
    
    /** Creates new PADTargetListNode */
    public TTSelectTargetRootNode() {
    }
    
    protected void buildNode() {
        if ( model == null ) return;
        
        removeAndDestroyAllChildren();
        
        if ( getModel() instanceof TTSelectTargetModel ) {
            TTSelectTargetModel model = (TTSelectTargetModel)getModel();
            
            
            final Target source = model.getSource();
            
            if ( source != null && source.getRecentTargets() != null ) {
                TargetList rt = source.getRecentTargets();
                TTRecentTargetNode rtn = new TTRecentTargetNode(model, rt);
                if ( rtn.getChildCount() > 0 ) {
                    add(rtn);
                }
            }
            
            TTNode stn = new TTSpecialTargetsNode(model);
            if ( stn.getChildCount() > 0 ) {
                add(stn);
            }
            
            TargetList targetList = Battle.getCurrentBattle().getTargetList(false);
            for(int index = 0; index < targetList.getSublistCount(); index++) {
                TargetList sublist = targetList.getSublist(index);
                TTTargetListNode node = new TTTargetListNode(model, sublist);
                if ( node.getChildCount() > 0 ) {
                    add(node);
                }
            }
            
            Filter<Target> filter = model != null ? model.getFilter() : null;
            Iterator i = targetList.getTargets(false);
            while ( i.hasNext() ) {
                Target target = (Target)i.next();
                if ( filter == null || filter.includeElement(target) ) {
                    TTTargetNode node = null;
                
                    node = new TTTargetNode(model, target, targetList);
                
                    add(node);
                }
            }
            
            if ( model instanceof DefaultTreeTableModel ) {
                ((DefaultTreeTableModel)model).nodeStructureChanged(this);
            }
            
        }
    }
    
    public void setModel(TreeTableModel model) {
        super.setModel(model);
        
        buildNode();
    }
    
    
    public boolean isLeaf() {
        return false;
    }
    
    public boolean getAllowsChildren() {
        return true;
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        
    }
    
    public void stateChanged(ChangeEvent e ) {
        
    }
}
