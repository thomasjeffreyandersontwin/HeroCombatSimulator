/*
 * CombinedAbilityNode.java
 *
 * Created on May 26, 2005, 8:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package champions.abilityTree;

import champions.Ability;
import champions.CombinedAbility;
import champions.DetailList;
import champions.interfaces.PAD;
import champions.SpecialEffect;
import champions.interfaces.SpecialParameter;
import champions.parameters.Parameter;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author twalker
 */
public class CombinedAbilityNode extends AbilityNode
implements ChangeListener {
    
    static protected DataFlavor abilityFlavor;
    
    /** Creates a new instance of CombinedAbilityNode */
    public CombinedAbilityNode(AbilityTreeTableModel model, DetailList source, MutableTreeNode parent, Ability ability) {
        super(model, source, parent, ability);
        
        ((CombinedAbility)ability).addChangeListener(this);
    }
    
    public Icon getIcon(JTree tree,boolean selected,boolean expanded,boolean leaf,int row,boolean hasFocus) {
        return UIManager.getIcon("Power.DefaultIcon");
    }
    
    public CombinedAbility getCombinedAbility() {
        return (CombinedAbility)ability;
    }
    
    protected void setupFlavors() {
        super.setupFlavors();
        try {
            if (abilityFlavor == null ) abilityFlavor = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType + "; class=" + Ability.class.getName() );
      }
        catch ( Exception e) {
            System.out.println(e);
        }
    }
    
    public int getNodeStatus() {
        return OKAY_STATUS;
    }
    
    /** Notifies Node of a drop event occuring in or below the node.
     * The Node can use the handleDrop method to perform actions based on the drop of a DetailList on
     * the Node Jtree.  If the node wishes to handles the event, it should call acceptDrop and dropComplete
     * on the event, then return true to indicate the event was handled.
     * @returns
     */
    public boolean handleDrop(JTree tree, TreePath dropPath,DropTargetDropEvent event) {
        try {
            // System.out.println("Got drop with path: " + dropPath);
            
            Transferable tr = event.getTransferable();
            PAD pad = null;
            if ( event.isDataFlavorSupported( specialEffectFlavor ) ) {
                SpecialEffect se = (SpecialEffect)tr.getTransferData(specialEffectFlavor);
                if ( se != null && ability.hasSpecialEffect( se.getName() ) == false ) {
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    
                    ability.addSpecialEffect(se);
                    
                    event.dropComplete(true);
                    return true;
                }
            }
            else if ( event.isDataFlavorSupported(specialParameterFlavor) ) {
                SpecialParameter specialParameter = (SpecialParameter)tr.getTransferData(specialParameterFlavor);
                if ( ((SpecialParameter)specialParameter).isUnique() && ability.hasSpecialParameter((SpecialParameter)specialParameter) ) return false;
                
                if ( specialParameter != null ) {
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    
                    ability.addSpecialParameter(specialParameter);
                    updateName();
                    
                    event.dropComplete(true);
                    return true;
                }
            }
            else if( event.isDataFlavorSupported(abilityFlavor) ) {
                tr = event.getTransferable();
                Ability ability = (Ability)tr.getTransferData( abilityFlavor );
                if ( ability instanceof CombinedAbility ) return false;
                //  System.out.println("Got Ability: " + ability);
                event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                
                int action = event.getDropAction();
                
                if ( action == DnDConstants.ACTION_COPY ) {
                    ability = (Ability)ability.clone();
                    
                    if ( getCombinedAbility().getAbilityIndex(ability) == -1 ) {
                        getCombinedAbility().addAbility(ability);
                    
                        updateChildren();

                        // Select the dropped child and expand it.
                        AbilityTreeNode an = findChildAbilityNode(ability);
                        if ( an != null && tree != null ) {
                            TreePath path = new TreePath( an.getPath() );
                            tree.setSelectionPath(path);
                            tree.expandPath(path);
                        }
                    }
                    
                    event.dropComplete(true);
                    return true;
                }
                else if ( action == DnDConstants.ACTION_MOVE ) {
                    if ( getCombinedAbility().getAbilityIndex(ability) == -1 ) {
                        if ( ability.getAbilityList() != null ) {
                            // This is a move, so remove it from it's old location...
                            ability.getAbilityList().removeAbility(ability);
                        }
                        
                        getCombinedAbility().addAbility(ability);
                    
                        updateChildren();

                        // Select the dropped child and expand it.
                        AbilityTreeNode an = findChildAbilityNode(ability);
                        if ( an != null && tree != null ) {
                            TreePath path = new TreePath( an.getPath() );
                            tree.setSelectionPath(path);
                            tree.expandPath(path);
                        }
                    }
                    
                    event.dropComplete(true);
                    return true;
                }
                else {
                    event.rejectDrop();
                }
            }
            
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        catch(UnsupportedFlavorException ufe) {
            ufe.printStackTrace();
        }
        
        return false;
    }
    
    public TreePath willHandleDrop(JTree tree, TreePath dropPath, DropTargetDragEvent event) {
        if(event.isDataFlavorSupported(specialEffectFlavor)
        || event.isDataFlavorSupported(specialParameterFlavor) 
        || event.isDataFlavorSupported(abilityFlavor) ) {
            TreePath tp = new TreePath(this.getPath());
            tp = tp.pathByAddingChild( new DropAsChildPlaceHolder());
            return tp;
        }
        return null;
    }
    
    public void updateChildren() {
        // System.out.println("AbilityNode update Children");
        boolean fireChange = false;
        boolean found;
        // Make sure the abilities in the source match the abilityNodes that are childern
        Vector newChildren = new Vector();
        int aindex, acount;
        int nindex, ncount;
        int total = 0;
        PAD pad;
        PADNode pn;
        ParameterNode pan;
        //SpecialEffectNode sen;
        String parameterName;
        
        ncount = ( children != null ) ? children.size() : 0;
        
        if ( ability == null || parameterList == null ) {
            if ( children != null ) children.clear();
            return;
        }
        // Add the name/special effect parameter Nodes
        Iterator<Parameter> it = parameterList.getParameters();
            while( it.hasNext() ) {
                Parameter p = it.next();
                parameterName = p.getName();
            if ( parameterList.isVisible(parameterName) == false ) continue;
            found = false;
            for(nindex=0;nindex<ncount;nindex++) {
                if ( children.get(nindex) instanceof ParameterNode ) {
                    pan = (ParameterNode) children.get(nindex);
                    if ( pan.getParameterList() == parameterList && pan.getParameter().equals(parameterName) ) {
                        found = true;
                        // Move the ability node from the childern list to the newChildern list
                        newChildren.add(pan);
                        if ( nindex != total ) fireChange = true;
                        children.set(nindex, null);
                        break;
                    }
                }
            }
            
            
            if ( found == false ) {
                pan = new ParameterNode(model, ability, parameterList, this, parameterName);
                pan.getParameterEditor().addPADValueListener(this);
                newChildren.add(pan);
                fireChange = true;
            }
            total++;
        }
        
        // Check that the Special Parameter Node is there
        // Try to find it in the children array.
        SpecialEffectsNode sen;
        found = false;
        for(nindex=0;nindex<ncount;nindex++) {
            if ( children.get(nindex) instanceof SpecialEffectsNode ) {
                sen = (SpecialEffectsNode) children.get(nindex);
                if ( sen.getAbility() == ability ) {
                    found = true;
                    // Move the ability node from the childern list to the newChildern list
                    newChildren.add(sen);
                    if ( nindex != total ) fireChange = true;
                    children.set(nindex, null);
                    break;
                }
            }
        }
        
        if ( found == false ) {
            sen = new SpecialEffectsNode(model, this, ability);
            newChildren.add(sen);
            fireChange = true;
        }
        total++;
        
        Ability subability;
        AbilityNode an;
        acount = getCombinedAbility().getAbilityCount();
        for( aindex = 0; aindex < acount; aindex++) {
            subability = getCombinedAbility().getAbility(aindex);
            // Try to find it in the children array.
            found = false;
            for(nindex=0;nindex<ncount;nindex++) {
                if ( children.get(nindex) instanceof AbilityNode ) {
                    an = (AbilityNode) children.get(nindex);
                    if ( an.getAbility() == subability ) {
                        found = true;
                        // Move the ability node from the childern list to the newChildern list
                        newChildren.add(an);
                        if ( nindex != total ) fireChange = true;
                        children.set(nindex, null);
                        break;
                    }
                }
            }
            
            if ( found == false ) {
                an = new AbilityNode(model, ability, this, subability);
                newChildren.add(an);
                fireChange = true;
            }
            total++;
        }
       
        
      
        
        // Check for all the SpecialParameter
        SpecialParameter specialParameter;
        SpecialParameterNode spn;
        acount = ability.getIndexedSize("SpecialParameter");
        for( aindex = 0; aindex < acount; aindex++) {
            specialParameter = (SpecialParameter)ability.getIndexedValue(aindex,"SpecialParameter","SPECIALPARAMETER");
            // Try to find it in the children array.
            found = false;
            for(nindex=0;nindex<ncount;nindex++) {
                if ( children.get(nindex) instanceof SpecialParameterNode ) {
                    spn = (SpecialParameterNode) children.get(nindex);
                    if ( spn.getSpecialParameter() == specialParameter ) {
                        found = true;
                        // Move the ability node from the childern list to the newChildern list
                        newChildren.add(spn);
                        if ( nindex != total ) fireChange = true;
                        children.set(nindex, null);
                        break;
                    }
                }
            }
            
            if ( found == false ) {
                spn = new SpecialParameterNode(model, ability, this, specialParameter, aindex);
                newChildren.add(spn);
                fireChange = true;
            }
            total++;
        }
        
        Vector oldChildren = children;
        children = newChildren;
        
        // Now that everything is done, anything level not-null in oldChildren should be destroyed
        // and references to it released.
        if ( oldChildren != null ) {
            for(nindex=0;nindex<oldChildren.size();nindex++) {
                if ( oldChildren.get(nindex) != null ) {
                    ((AbilityTreeNode)oldChildren.get(nindex)).destroy();
                    oldChildren.set(nindex,null);
                    fireChange = true;
                }
            }
        }
        
        if ( fireChange && model != null ) model.nodeStructureChanged(this);
        
        
    }
    
        /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        
        String property = evt.getPropertyName();
        
        if ( evt.getSource() == ability ) {
            if ( property.equals( "Combined.INDEXSIZE" ) ) {
                updateChildren();
            }
        }
        else if ( evt.getSource() == powerParameterList && evt.getPropertyName().endsWith("VISIBLE") ) {
            updateChildren();
        } 
    }
    
    public AbilityTreeNode findChildAbilityNode(Ability ability) {
        int index;
        int count = children != null ? children.size() : 0;
        Object o;
        
        for ( index=0;index<count;index++) {
            o = children.get(index);
            if ( o instanceof AbilityNode && ((AbilityNode)o).getAbility() == ability ) {
                return (AbilityTreeNode)o;
            }
        }
        return null;
    }
    
    public void destroy() {
        getCombinedAbility().removeChangeListener(this);
        super.destroy();
    }

    public void stateChanged(javax.swing.event.ChangeEvent changeEvent) {
        updateChildren();
    }
    
}
