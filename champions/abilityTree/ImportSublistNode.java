/*
 * SublistNode.java
 *
 * Created on June 11, 2001, 5:46 PM
 */

package champions.abilityTree;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;
import java.beans.*;

import champions.*;
import champions.interfaces.*;

import treeTable.*;
/**
 *
 * @author  twalker
 * @version
 */
public class ImportSublistNode extends AbilityTreeNode implements PropertyChangeListener {
        
    static private DataFlavor abilityFlavor;
    
    /** Holds value of property source. */
    private CharacterImport source;
    
    /** Holds value of property sublistName. */
    private String sublistName;
    
    /** Creates new SublistNode */
    public ImportSublistNode(CharacterImport source, String sublistName, MutableTreeNode parent) {
        setParent(parent);
        setAllowsChildren(true);
        setSource(source);
        setSublistName(sublistName);
        setupFlavors();
        
        updateChildren();
    }
    
    public ImportSublistNode(AbilityTreeTableModel model, CharacterImport source, String sublistName, MutableTreeNode parent) {
        setModel(model);
        setParent(parent);
        setSource(source);
        setSublistName(sublistName);
        
        setAllowsChildren(true);
        
        updateChildren();
        
        setupFlavors();
        
    }
    
    public void setupFlavors() {
        try {
            if ( abilityFlavor == null )  abilityFlavor = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType + "; class=" + Ability.class.getName() );
        }
        catch (ClassNotFoundException cnfe ) {
        }
    }
    
    protected void buildNode() {
        if (children == null) {
            children = new Vector();
        }
        
        MutableTreeNode mtn = null;
        if ( source != null ) {
            int index, count;
            String string;
            Ability ability;
            AbilityImport ai;
            
            count = source.getIndexedSize( "Sublist" );
            for(index = 0; index < count; index++) {
                string = source.getIndexedStringValue(index,"Sublist","PARENT");
                if ( ( sublistName == null && string == null ) || ( string != null && string.equals(sublistName)) ) {
                    // Found a sublist...create it and add it
                    string = source.getIndexedStringValue(index,"Sublist","NAME");
                    mtn = new ImportSublistNode(model, getSource(), string, this );
                    children.add(mtn);
                }
            }
            
                count = source.getIndexedSize( "AbilityImport" );
                for(index = 0; index < count; index++) {
                    ai = (AbilityImport)source.getIndexedValue(index,"AbilityImport","ABILITYIMPORT");
                    string = ai.getSublist();
                    if ( ( userObject == null && string == null ) || ( string != null && string.equals(sublistName)) ) {
                        // Found an abilityImport which belongs to this sublist
                        if ( ai.getType() == null || ai.getType().equals("SUBLIST") == false ) {
                            mtn = new AbilityImportNode(model, source, this, ai );
                            children.add(mtn);
                        }
                    }
                }
        }
    }
    


    
    public Icon getIcon(JTree tree,boolean selected,boolean expanded,boolean leaf,int row,boolean hasFocus) {
        return null;
    }
    
    public TreeCellEditor getTreeCellEditor(JTree tree) {
        return null;
    }
    
    public TreeCellRenderer getTreeCellRenderer(JTree tree) {
        return null;
    }
    
    public int getNodeStatus() {
        return OKAY_STATUS;
    }
    
    public boolean isLeaf() {
        return false;
    }
    
    /** Notifies Node of a drop event occuring in or below the node.
     * The Node can use the handleDrop method to perform actions based on the drop of a DetailList on
     * the Node Jtree.  If the node wishes to handles the event, it should call acceptDrop and dropComplete
     * on the event, then return true to indicate the event was handled.
     * @returns
     */
    public boolean handleDrop(JTree tree, TreePath dropPath,DropTargetDropEvent event) {
      /*  try {
            // System.out.println("Got drop with path: " + dropPath);
            DataFlavor flavor = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType + "; class=" + Ability.class.getName() );
            
            if(event.isDataFlavorSupported(flavor)) {
                Transferable tr = event.getTransferable();
                Ability ability = (Ability)tr.getTransferData( flavor );
                //  System.out.println("Got Ability: " + ability);
                event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                
                // Check if this is a new ability or an existing one
                int finalPosition = -1;
                //int index = source.findExactIndexed( "Ability","ABILITY",ability);
                
                int insertIndex;
                if ( dropPath.getLastPathComponent() instanceof AbilityNode ) {
                    // The Last node of the drop path was an ability node, so put this ability
                    // just before that abilityNode.
                    Ability beforeAbility = ((AbilityNode)dropPath.getLastPathComponent()).getAbility();
                    // Find the index to insert before.
                    // insertIndex = source.findExactIndexed("Ability","ABILITY", beforeAbility);
                    insertIndex = abilityList.getAbilityIndex(beforeAbility);
                    // If already at a position before, so the actual insert index needs to be decreased.
                    // if ( isSublistNameEqual(ability.getSublist(), getSublist()) == false && index < insertIndex ) insertIndex --;
                    // ability.add("Sublist.NAME", getSublist(), true, false);
                    // source.moveIndexed(index, insertIndex, "Ability", true);
                    abilityList.addAbility(ability, insertIndex);
                }
                else {
                    // ability.add("Sublist.NAME", getSublist(), true, false);
                    // source.moveIndexed(index, 0, "Ability", true);
                    abilityList.addAbility(ability);
                }
                
                updateChildren();
                
                // Select the dropped child and expand it.
                AbilityTreeNode an = findChildAbilityNode(ability);
                if ( an != null && tree != null ) {
                    TreePath path = new TreePath( an.getPath() );
                    tree.setSelectionPath(path);
                    tree.expandPath(path);
                }
                
                
                
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
        catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } */
        
        return false;
    }
    
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        // Sublists watch for two things.  First the watch the Sublist property.  If thier parent changes
        // they must move themselves and if they are deleted completely, they must remove and destroy themselves.
        // Second, the must watch the ability.INDEXSIZE.  If an ability is inserted with the appropriate sublist,
        // they must create an abilityNode for that ability.
        String property = evt.getPropertyName();
        if ( sublistName != null && property.startsWith( "Sublist" ) ) {
          //  handleSublistChange();
        }
        else if ( property.equals( "AbilityImport.INDEXSIZE" ) ) {
            updateChildren();
        }
    }
    
    
   /* protected void handleSublistChange() {
        int index = source.findIndexed( "Sublist", "NAME", getSublistName() );
        if ( index == -1 ) {
            // Oh No.  I have been deleted!
            this.removeFromParent();
            this.destroy();
        }
        else {
            String newParentName = source.getIndexedStringValue(index,"Sublist","PARENT");
            
            if ( isSublistNameEqual(newParentName, ((ImportSublistNode)getParent()).getSublistName()) == false ) {
                // I used to be a Top level sublist, now I am not.  Better move.
                SublistNode node = findSublistNode(getRoot(), newParentName);
                removeFromParent();
                node.insert(this, node.getChildCount());
            }
        }
    } */
    
/*    public SublistNode findSublistNode(TreeNode node, String newParentName) {
        if ( node instanceof SublistNode && isSublistNameEqual( newParentName, ((SublistNode)node).getSublist()) ){
            return (SublistNode)node;
        }
        else {
            int count, index;
            TreeNode newNode;
            count = node.getChildCount();
            for(index=0;index<count;index++) {
                newNode = findSublistNode( node.getChildAt(index), newParentName);
                if ( newNode != null ) return (SublistNode)newNode;
            }
        }
        return null;
    }  */
    
    
    
public void updateChildren() {
        boolean fireChange = false;
        boolean found;
        // Make sure the abilities in the source match the abilityNodes that are childern
        Vector newChildren = new Vector();
        int aindex, acount;
        int nindex, ncount;
        int total = 0;
        Ability ability;
        AbilityNode an;
        ncount = ( children != null ) ? children.size() : 0;
        String childSublistName, sublistParent;
        ImportSublistNode sn;
        
        // Copy the Existing Sublists over properly
        acount = source.getIndexedSize("Sublist");
        for( aindex = 0; aindex < acount; aindex++) {
            sublistParent = source.getIndexedStringValue(aindex,"Sublist","PARENT");
            if ( isSublistNameEqual( getSublistName(), sublistParent ) ) {
                // This sublist needs needs to be in the node.
                // Try to find it in the children array.
                found = false;
                childSublistName = source.getIndexedStringValue(aindex,"Sublist","NAME");
                for(nindex=0;nindex<ncount;nindex++) {
                    if ( children.get(nindex) instanceof ImportSublistNode ) {
                        sn = (ImportSublistNode) children.get(nindex);
                        if ( isSublistNameEqual( sn.getSublistName(), childSublistName)) {
                            found = true;
                            // Move the ability node from the childern list to the newChildern list
                            newChildren.add(sn);
                            if ( nindex != total ) fireChange = true;
                            children.set(nindex, null);
                            break;
                        }
                    }
                }
                
                if ( found == false ) { 
               //     System.out.println("Creating ImportSublistNode for '" + childSublistName + "'...");
                    sn = new ImportSublistNode(model, source, childSublistName, this);
                    newChildren.add(sn);
                    fireChange = true;
                }
                total++;
            }
        }     
        
        AbilityImport ai;
        AbilityImportNode ain;
        acount = source.getIndexedSize("AbilityImport");
        for( aindex = 0; aindex < acount; aindex++) {
            ai = (AbilityImport)source.getIndexedValue(aindex,"AbilityImport","ABILITYIMPORT");
            if ( ai.isSublist() ) continue;
            if ( isSublistNameEqual( getSublistName(), ai.getSublist() ) ) {
                // This ability needs to be in the node.
                // Try to find it in the children array.
                found = false;
                for(nindex=0;nindex<ncount;nindex++) {
                    if ( children.get(nindex) instanceof AbilityImportNode ) {
                        ain = (AbilityImportNode) children.get(nindex);
                        if ( ain.getAbilityImport() == ai ) {
                            found = true;
                            // Move the ability node from the childern list to the newChildern list
                            newChildren.add(ain);
                            if ( nindex != total ) fireChange = true;
                            children.set(nindex, null);
                            break;
                        }
                    }
                }
                
                if ( found == false ) {
                    ain = new AbilityImportNode(model, source, this, ai);
                    newChildren.add(ain);
                    fireChange = true;
                }
                total++;
            }
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
    
    /** Called to destroy the object.  All references should be set to null.
     * Once destroy has been called, the object will not be used anymore.
     */
    public void destroy() {
        super.destroy();
        setSource(null);
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
    
    /** Adds an Ability to the Target and selects/updates the tree appropriately
     * This is an access function which allows an ability to be added to a target
     * and also allows the appropriate selection and expansion of the tree to be
     * maintained.  If the ability is added directly to the target, the sublistNode
     * will refresh properly, but the selection information will be lost and all
     * expanded nodes will be unexpanded.
     *
     * This method overcomes that problem by directly adding and adjusting the tree.
     * @param ability Ability to be added.  This ability should be an independent
     * object, ie it should either be a brand new object or a clone
     * of an existing ability.
     */
  /*  public void addAbility(Ability ability) {
        int index = abilityList.getAbilityIndex(ability);
        if ( index == -1  ) {
            // This is a new ability.
            ability = (Ability)ability.clone();
            //ability.setSublist( getSublist() );
            
            abilityList.addAbility(ability);
            
            updateChildren();
            
            // Select the dropped child and expand it.
            AbilityTreeNode an = findChildAbilityNode(ability);
            if ( an != null && tree != null ) {
                TreePath path = new TreePath( an.getPath() );
                tree.setSelectionPath(path);
                tree.expandPath(path);
            }
        }
    } */
    
    // Eventually you should be able to do this...
    public boolean canCopyOrCutNode() {
        return false;
    }
    
    public void copyOrCutNode(boolean cut) {
        
    }
    
    // You should be able to paste AbilityLists and Abilities
    public boolean canPasteData(Transferable t) {
        return t != null && t.isDataFlavorSupported(abilityFlavor);
    }
    
  /*  public void pasteData(Transferable t) {
        if ( t.isDataFlavorSupported(abilityFlavor) ) {
            Ability ability;
            try {
                ability = (Ability)t.getTransferData(abilityFlavor);
               // ability = (Ability)ability.clone();
                //ability.setSublist( sublist );
                addAbility(ability); // This will clone it!
                
                if ( tree != null ) {
                    TreePath path = new TreePath( getPath() );
                    tree.expandPath(path);
                }
                
            }
            catch ( UnsupportedFlavorException ufe ) {
                ufe.printStackTrace();
            }
            catch ( IOException ioe ) {
                ioe.printStackTrace();
            }
        }
    } */
    
    /** Getter for property source.
     * @return Value of property source.
     */
    public CharacterImport getSource() {
        return source;
    }
    
    /** Setter for property source.
     * @param source New value of property source.
     */
    public void setSource(CharacterImport source) {
                if ( this.source != source ) {
            if ( this.source != null ) {
                this.source.removePropertyChangeListener(this);

            }
            this.source = source;
            if ( this.source != null ) {
                this.source.addPropertyChangeListener(this);
                
            }
        }
    }
    
    public String toString() {
        return sublistName;
    }
    
    /** Getter for property sublistName.
     * @return Value of property sublistName.
     */
    public String getSublistName() {
        return sublistName;
    }    
    
    /** Setter for property sublistName.
     * @param sublistName New value of property sublistName.
     */
    public void setSublistName(String sublistName) {
        this.sublistName = sublistName;
    }    
    
}
