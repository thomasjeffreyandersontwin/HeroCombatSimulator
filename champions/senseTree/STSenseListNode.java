/*
 * STSenseListNode.java
 *
 * Created on August 13, 2006, 8:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.senseTree;

import champions.Sense;
import champions.Target;
import java.util.Iterator;
import java.util.List;
import treeTable.DefaultTreeTableModel;

/**
 *
 * @author 1425
 */
public class STSenseListNode extends STNode {
    
    private List<Sense> senseList;
    private Target target;
    
    /** Creates a new instance of STSenseListNode */
    public STSenseListNode(Target target, List<Sense> senseList) {
        setTarget(target);
        setSenseList(senseList);
        
        
    }

    public List<Sense> getSenseList() {
        return senseList;
    }

    public void setSenseList(List<Sense> senseList) {
        this.senseList = senseList;
        buildNode();
    }
    
    /** Builds the node based on the senseList.
     *
     * Note:  This is not the normal buildChildren() method which 
     * the treeModel typically invokes.  This method will only be set when the
     * sense list is for set.
     */
    public void buildNode() {
        removeAllChildren();
        
        if ( getTarget() != null && senseList != null ) {
            Iterator it;
            
            it = senseList.iterator();
            while ( it.hasNext() ) {
                Sense sg = (Sense)it.next();
                STNode node = new STSenseNode(getTarget(), sg);
                add(node);
            }
        }
        
        if ( getModel() instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)getModel()).nodeStructureChanged(this);
        }
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }
    
}
