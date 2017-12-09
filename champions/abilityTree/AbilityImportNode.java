/*
 * AbilityImportNode.java
 *
 * Created on June 11, 2001, 8:11 PM
 */

package champions.abilityTree;

import champions.*;
import champions.Power;
import champions.interfaces.*;

import treeTable.*;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.util.*;
import java.io.*;
import java.beans.*;
/**
 *
 * @author  twalker
 * @version
 */
public class AbilityImportNode extends AbilityTreeNode
implements PropertyChangeListener
{
    
    /** Holds value of property source. */
    private DetailList source;
    
    /** Holds value of property abilityImport. */
    private AbilityImport abilityImport;
    
    /** Holds value of property ability. */
    private Ability ability;
    
    private PADNode powerNode = null;
    
    private static DebugDetailListAction debugAction;
    
    private static DataFlavor abilityFlavor;
    
    /** Creates new AbilityNode */
    public AbilityImportNode(AbilityTreeTableModel model, DetailList source, MutableTreeNode parent, AbilityImport abilityImport) {
        setModel(model);
        setSource(source);
        setParent(parent);
        setAbilityImport(abilityImport);
        
        setupActions();
        
        updateChildren();
        
        try {
            abilityFlavor = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType + "; class=" + Ability.class.getName() );
        }
        catch ( Exception e) {
            System.out.println(e);
        }
    }
    
    public void setupActions() {
        if ( debugAction == null ) debugAction = new DebugDetailListAction("Debug Ability Import...");
    }
    
    
    /** Getter for property source.
     * @return Value of property source.
     */
    public DetailList getSource() {
        return source;
    }
    
    /** Setter for property source.
     * @param source New value of property source.
     */
    public void setSource(DetailList source) {
        this.source = source;
    }
    
    /** Getter for property abilityImport.
     * @return Value of property abilityImport.
     */
    public AbilityImport getAbilityImport() {
        return abilityImport;
    }
    
    /** Setter for property abilityImport.
     * @param abilityImport New value of property abilityImport.
     */
    public void setAbilityImport(AbilityImport abilityImport) {
        if ( this.abilityImport != abilityImport ) {
            if ( this.abilityImport != null ) {
                abilityImport.removePropertyChangeListener("AbilityImport.ABILITY", this);
            }
            this.abilityImport = abilityImport;
            if ( this.abilityImport != null ) {
                setAbility( abilityImport.getAbility() );
                updateName();
                abilityImport.addPropertyChangeListener("AbilityImport.ABILITY", this);
            }
        }
    }
    
    /** Getter for property ability.
     * @return Value of property ability.
     */
    public Ability getAbility() {
        return ability;
    }
    
    /** Setter for property ability.
     * @param ability New value of property ability.
     */
    public void setAbility(Ability ability) {
        this.ability = ability;
    }
    
    public Icon getIcon(JTree tree,boolean selected,boolean expanded,boolean leaf,int row,boolean hasFocus) {
        return (powerNode != null) ? powerNode.getIcon(tree, selected, expanded, leaf, row, hasFocus) : null;
    }
    
    public TreeCellEditor getTreeCellEditor(JTree tree) {
        return null;
    }
    
    public TreeCellRenderer getTreeCellRenderer(JTree tree) {
        return null;
    }
    
    public int getNodeStatus() {
   /*     if ( getAbilityImport() != null ) {
            if ( getAbilityImport().getType() == null || getAbilityImport().getType().equals("") ) {
                return CRITICAL_STATUS;
            }
            else if ( getAbilityImport().getType().equals("POWER") && getAbilityImport().getPower() == null ) {
                return CRITICAL_STATUS;
            }
            else if ( getAbilityImport().getType().equals("MATCH") && getAbilityImport().getMatch() == null ) {
                return CRITICAL_STATUS;
            }
        } */
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
            
            Transferable tr = event.getTransferable();
            Ability ability = null;
            
            if ( event.isDataFlavorSupported(abilityFlavor) ) {
                ability = (Ability)tr.getTransferData(abilityFlavor);
                
                event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                
                abilityImport.setTemplate(ability);
                
                updateName();
                
                event.dropComplete(true);
                return true;
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
    
    public TreePath willHandleDrop(JTree tree, TreePath dropPath,DropTargetDragEvent event) {
      //  if(event.isDataFlavorSupported(abilityFlavor) ){
     //       return true;
      //  }
        return null;
    }
    
    /** Called to destroy the object.  All references should be set to null.
     * Once destroy has been called, the object will not be used anymore.
     */
    public void destroy() {
    }
    
    public void updateChildren() {
        boolean fireChange = false;
        boolean found;
        // Make sure the abilities in the source match the abilityNodes that are childern
        Vector newChildren = new Vector();
        int aindex, acount;
        int nindex, ncount;
        int total = 0;
        PAD pad;
        PADNode pn;
        TextNode tn;
        ParameterNode pan;
        
        ncount = ( children != null ) ? children.size() : 0;
        
       /* // Add the Special Effect Node
        found = false;
        for(nindex=0;nindex<ncount;nindex++) {
            if ( children.get(nindex) instanceof ParameterNode ) {
                pan = (ParameterNode) children.get(nindex);
                if ( pan.getParameterList() == parameterList && pan.getParameter().equals("SpecialEffect") ) {
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
            pan = new ParameterNode(model, ability, parameterList, this, "SpecialEffect");
            newChildren.add(pan);
            fireChange = true;
        }
        total++; */
        
        Ability ability = getAbilityImport().getAbility();
        
        // Check for Power
        pad = ( ability != null ) ? (PAD)ability.getPower() : null;
        // Try to find it in the children array.
        if ( pad != null ) {
            found = false;
            for(nindex=0;nindex<ncount;nindex++) {
                if ( children.get(nindex) instanceof PADNode ) {
                    pn = (PADNode) children.get(nindex);
                    if ( pn.getPad() == pad ) {
                        found = true;
                        // Move the ability node from the childern list to the newChildern list
                        newChildren.add(pn);
                        if ( nindex != total ) fireChange = true;
                        children.set(nindex, null);
                        powerNode = pn;
                        break;
                    }
                }
            }
            
            if ( found == false ) {
                pn = new PADNode(model, ability, this,(Power) pad);
                newChildren.add(pn);
                powerNode = pn;
                fireChange = true;
            }
            total++;
        }
        else {
            // Add a Text Node instead of the PADNode
            String powerNotSet = "Not Able to Determine Power";
            found = false;
            for(nindex=0;nindex<ncount;nindex++) {
                if ( children.get(nindex) instanceof TextNode ) {
                    tn = (TextNode) children.get(nindex);
                    if ( powerNotSet.equals( tn.getText()) ) {
                        found = true;
                        // Move the ability node from the childern list to the newChildern list
                        newChildren.add(tn);
                        if ( nindex != total ) fireChange = true;
                        children.set(nindex, null);
                        break;
                    }
                }
            }
            
            if ( found == false ) {
                tn = new TextNode(model, this, powerNotSet, ERROR_STATUS, null );
                newChildren.add(tn);
                fireChange = true;
            }
            total++;
            
            powerNode = null;
        }
        
        if ( ability != null ) {
            acount = ability.getAdvantageCount();
            for( aindex = 0; aindex < acount; aindex++) {
                pad = ability.getAdvantage(aindex);
                // Try to find it in the children array.
                found = false;
                for(nindex=0;nindex<ncount;nindex++) {
                    if ( children.get(nindex) instanceof PADNode ) {
                        pn = (PADNode) children.get(nindex);
                        if ( pn.getPad() == pad ) {
                            found = true;
                            // Move the ability node from the childern list to the newChildern list
                            newChildren.add(pn);
                            if ( nindex != total ) fireChange = true;
                            children.set(nindex, null);
                            break;
                        }
                    }
                }
                
                if ( found == false ) {
                    pn = new PADNode(model, ability, this, pad, aindex);
                    newChildren.add(pn);
                    fireChange = true;
                }
                total++;
            }
            
            acount = ability.getLimitationCount();
            for( aindex = 0; aindex < acount; aindex++) {
                pad = ability.getLimitation(aindex);
                // Try to find it in the children array.
                found = false;
                for(nindex=0;nindex<ncount;nindex++) {
                    if ( children.get(nindex) instanceof PADNode ) {
                        pn = (PADNode) children.get(nindex);
                        if ( pn.getPad() == pad ) {
                            found = true;
                            // Move the ability node from the childern list to the newChildern list
                            newChildren.add(pn);
                            if ( nindex != total ) fireChange = true;
                            children.set(nindex, null);
                            break;
                        }
                    }
                }
                
                if ( found == false ) {
                    pn = new PADNode(model, ability, this, pad, aindex);
                    newChildren.add(pn);
                    fireChange = true;
                }
                total++;
            }
            
            
            
           /* // Check that the Special Parameter Node is there
            // Try to find it in the children array.
            SpecialParameterNode spn;
            found = false;
            for(nindex=0;nindex<ncount;nindex++) {
                if ( children.get(nindex) instanceof SpecialParameterNode ) {
                    spn = (SpecialParameterNode) children.get(nindex);
                    if ( spn.getAbility() == ability ) {
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
                spn = new SpecialParameterNode(model, ability, this);
                newChildren.add(spn);
                fireChange = true;
            }
            total++;*/
        }
        
        // Add Import LineGroupNode
        LineGroupNode lgn;
        found = false;
        for(nindex=0;nindex<ncount;nindex++) {
            if ( children.get(nindex) instanceof LineGroupNode ) {
                lgn = (LineGroupNode) children.get(nindex);
                if ( lgn.getAbilityImport() == abilityImport ) {
                    found = true;
                    // Move the ability node from the childern list to the newChildern list
                    newChildren.add(lgn);
                    if ( nindex != total ) fireChange = true;
                    children.set(nindex, null);
                    break;
                }
            }
        }
        
        if ( found == false ) {
            lgn = new LineGroupNode(model, this, abilityImport);
            newChildren.add(lgn);
            fireChange = true;
        }
        total++;
        
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
    
    public void updateName() {
        if ( ability != null && model != null ) model.nodeChanged(this);
    }
    
    public boolean invokeMenu(JPopupMenu popup, JTree tree, TreePath path) {
        debugAction.setDetailList(abilityImport);
        popup.add(debugAction);
        return true;
    }
    
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getSource() == abilityImport ) {
            setAbility( abilityImport.getAbility() );
            updateChildren();
        }
    }
    
    public String toString() {
        if ( abilityImport == null ) return "Error: AbilityImport Node not initialized.";
        
        Ability ability = abilityImport.getAbility();
        if (ability != null ) {
            return ability.getName() + " - " + ability.getDescription();
        }
        else if ( abilityImport.getType() != null && abilityImport.getType().equals("SUBLIST") ) {
            return abilityImport.getSublist();
        }
        else {
            return abilityImport.getImportLine(0);
        }
    }
    
}
