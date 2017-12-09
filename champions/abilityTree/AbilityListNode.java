/*
 * SublistNode.java
 *
 * Created on June 11, 2001, 5:46 PM
 */

package champions.abilityTree;

import champions.Ability;
import champions.CombinedAbility;
import champions.DefaultAbilityList;
import champions.DetailList;
import tjava.ObjectTransferable;
import champions.Target;
import champions.VPPConfigurationEditor;
import champions.interfaces.AbilityIterator;
import champions.interfaces.AbilityList;
import champions.interfaces.Framework;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import tjava.SharedPopupAction;
import treeTable.DefaultTreeTableCellRenderer;
import treeTable.TreeTableCellRenderer;



/**
 *
 * @author  twalker
 * @version
 */
public class AbilityListNode extends AbilityTreeNode
implements ChangeListener {
    
    /** Holds value of property detailList. */
    private AbilityList abilityList;
    
    static private DataFlavor abilityFlavor;
    static private DataFlavor abilityListFlavor;
    
    static private AbilityListNode.RemoveAbilityListAction removeAbilityListAction;
    static private AbilityListNode.AddAbilityListAction addAbilityListAction;
    static private AbilityListNode.LoadAbilityAction loadAbilityAction;
    static private AbilityListNode.ConfigureFrameworkAction configureFrameworkAction;
    
    private static JMenuItem removeMenuItem, addMenuItem, loadMenuItem;
    
    private static InputMap inputMap;
    private static ActionMap actionMap;
    
    /** Holds value of property source. */
    private Target source;
    
    static private DefaultTreeTableCellRenderer renderer;
    
    static private final int DEBUG = 0;
    
    /** Creates new SublistNode */
    public AbilityListNode(Target source, AbilityList abilityList, MutableTreeNode parent) {
        setParent(parent);
        setAbilityList(abilityList);
        setAllowsChildren(true);
        setSource(source);
        
        updateChildren();
        
        setupFlavors();
        
        setupActions();
        
        setupKeyBindings();
        
        setupRenderer();
        
        setExpandDuringDrag(true);
    }
    
    public AbilityListNode(AbilityTreeTableModel model, Target source, AbilityList abilityList, MutableTreeNode parent) {
        setModel(model);
        setParent(parent);
        setAbilityList(abilityList);
        setSource(source);
        setAllowsChildren(true);
        
        updateChildren();
        
        
        setupFlavors();
        
        setupActions();
        
        setupKeyBindings();
        
        setupRenderer();
        
        setExpandDuringDrag(true);
    }
    
    public void setupFlavors() {
        try {
            if ( abilityFlavor == null )  abilityFlavor = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType + "; class=" + Ability.class.getName() );
            if ( abilityListFlavor == null ) abilityListFlavor = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType + "; class=" + AbilityList.class.getName() );
            // System.out.println(abilityListFlavor);
        }
        catch (ClassNotFoundException cnfe ) {
            cnfe.printStackTrace();
        }
    }
    
    public void setupActions() {
        if ( removeAbilityListAction == null ) {
            removeAbilityListAction = new RemoveAbilityListAction();
            removeMenuItem = new JMenuItem(removeAbilityListAction);
            removeMenuItem.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_DELETE ,0) );
        }
        
        if ( addAbilityListAction == null ) {
            addAbilityListAction = new AddAbilityListAction();
            addMenuItem = new JMenuItem(addAbilityListAction);
            //addMenuItem.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_DELETE ,0) );
        }
        
        if ( loadAbilityAction == null ) {
            loadAbilityAction = new LoadAbilityAction();
            loadMenuItem = new JMenuItem(loadAbilityAction);
        }
        
        if ( configureFrameworkAction == null ) {
            configureFrameworkAction = new ConfigureFrameworkAction();
        }
    }
    
    
    public void setupKeyBindings() {
        if ( inputMap == null ) {
            inputMap = new InputMap();
            inputMap.put( KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "RemoveAbilityListAbility" );
            //inputMap.put( KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "RemoveAbilityListAbility" );
            
        }
        
        if ( actionMap == null ) {
            actionMap = new ActionMap();
            actionMap.put( "RemoveAbilityListAbility", removeAbilityListAction );
        }
        
    }
    
    public void setupRenderer() {
        if ( renderer == null ) {
            renderer = new DefaultTreeTableCellRenderer();
            renderer.setRendererFont( UIManager.getFont("AbilityTree.sublistCPFont"));
            renderer.setHorizontalAlignment( SwingConstants.TRAILING );
        }
    }
    
    protected void buildNode() {
        if (children == null) {
            children = new Vector();
        }
        
        MutableTreeNode mtn = null;
        if ( abilityList != null ) {
            int index, count;
            //  String string = "Oh Fuck";
            Ability ability;
            //   AbilityImport ai;
            AbilityList sublist;
            
            count = abilityList.getSublistCount();
            for(index = 0; index < count; index++) {
                //string = source.getIndexedStringValue(index,"Sublist","PARENT");
                // if ( ( userObject == null && string == null ) || ( string != null && string.equals(userObject)) ) {
                // Found a sublist...create it and add it
                //string = source.getIndexedStringValue(index,"Sublist","NAME");
                sublist = abilityList.getSublist(index);
                mtn = new AbilityListNode(model, source, sublist, this );
                children.add(mtn);
                //}
            }
            
            // if ( source instanceof Target ) {
            AbilityIterator iterator = abilityList.getAbilities();
            while ( iterator.hasNext() ) {
                ability = iterator.nextAbility();
                //abilityList = ability.getAbilityList();
                //if ( ( userObject == null && string == null ) || ( string != null && string.equals(userObject)) ) {
                // Found an ability which belongs to this sublist
                if ( ability instanceof CombinedAbility ) {
                    mtn = new CombinedAbilityNode(model, source, this, ability);
                }
                else {
                    mtn = new AbilityNode(model, source, this, ability);
                }
                children.add(mtn);
                //}
            }
            // }
            
           /* if ( source instanceof CharacterImport ) {
                count = source.getIndexedSize( "AbilityImport" );
                for(index = 0; index < count; index++) {
                    ai = (AbilityImport)source.getIndexedValue(index,"AbilityImport","ABILITYIMPORT");
                    string = ai.getSublist();
                    if ( ( userObject == null && string == null ) || ( string != null && string.equals(userObject)) ) {
                        // Found an abilityImport which belongs to this sublist
                        if ( ai.getType() == null || ai.getType().equals("SUBLIST") == false ) {
                            mtn = new AbilityImportNode(model, source, this, ai );
                            children.add(mtn);
                        }
                    }
                }
            } */
        }
    }
    
    /** Getter for property source.
     * @return Value of property source.
     */
    public AbilityList getAbilityList() {
        return abilityList;
    }
    
    /** Setter for property source.
     * @param source New value of property source.
     */
    public void setAbilityList(AbilityList abilityList) {
        if ( this.abilityList != abilityList ) {
            if ( this.abilityList != null ) {
                //  this.abilityList.removePropertyChangeListener(this);
                this.abilityList.removeChangeListener(this);
            }
            this.abilityList = abilityList;
            if ( this.abilityList != null ) {
                //this.abilityList.addPropertyChangeListener(this);
                this.abilityList.addChangeListener(this);
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
        boolean handled = false;
        try {
            
            if( event.isDataFlavorSupported(abilityFlavor ) ) {
                Transferable tr = event.getTransferable();
                Ability ability = (Ability)tr.getTransferData( abilityFlavor );
                //  System.out.println("Got Ability: " + ability);
                event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                
                int action = event.getDropAction();
                
                if ( action == DnDConstants.ACTION_COPY ) {
                    ability = (Ability)ability.clone();
                    
                    int insertIndex = Integer.MAX_VALUE;
                    if ( dropPath.getLastPathComponent() instanceof AbilityNode ) {
                        // The Last node of the drop path was an ability node, so put this ability
                        // just before that abilityNode.
                        Ability beforeAbility = ((AbilityNode)dropPath.getLastPathComponent()).getAbility();
                        insertIndex = abilityList.getAbilityIndex(beforeAbility);
                    }
                    
                    abilityList.addAbility(ability, insertIndex);
                    
                    updateChildren();
                    
                    // Select the dropped child and expand it.
                    AbilityTreeNode an = findChildAbilityNode(ability);
                    if ( an != null && tree != null ) {
                        TreePath path = new TreePath( an.getPath() );
                        tree.setSelectionPath(path);
                        tree.expandPath(path);
                    }
                    
                    event.dropComplete(true);
                    handled = true;
                }
                else if ( action == DnDConstants.ACTION_MOVE ) {
                    int insertIndex = abilityList.getAbilityCount();
                    boolean alreadyExist = abilityList.hasAbility(ability, true);
                    
                    if ( dropPath.getLastPathComponent() instanceof AbilityNode ) {
                        // The Last node of the drop path was an ability node, so put this ability
                        // just before that abilityNode.
                        Ability beforeAbility = ((AbilityNode)dropPath.getLastPathComponent()).getAbility();
                        insertIndex = abilityList.getAbilityIndex(beforeAbility);
                    }
                    
                    abilityList.addAbility(ability, insertIndex);
                    
                    updateChildren();
                    
                    // Select the dropped child and expand it.
                    AbilityTreeNode an = findChildAbilityNode(ability);
                    if ( an != null && tree != null ) {
                        TreePath path = new TreePath( an.getPath() );
                        tree.setSelectionPath(path);
                        if ( alreadyExist == false ) tree.expandPath(path);
                    }
                    
                    event.dropComplete(true);
                    handled = true;
                }
                else {
                    event.rejectDrop();
                }
            }
            else if ( event.isDataFlavorSupported(abilityListFlavor ) ) {
                Transferable tr = event.getTransferable();
                AbilityList newList = (AbilityList)tr.getTransferData( abilityListFlavor );
                
                int action = event.getDropAction();
                
                if ( ( action == DnDConstants.ACTION_MOVE && newList != getAbilityList() ) ||
                action == DnDConstants.ACTION_COPY) {
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    
                    if ( action == DnDConstants.ACTION_COPY ) {
                        newList =  newList.cloneList();
                    }
                    
                    getAbilityList().addSublist(newList);
                    
                    updateChildren();
                    
                    // Select the dropped child and expand it.
                    AbilityTreeNode an = findChildSublistNode(abilityList);
                    if ( an != null && tree != null ) {
                        TreePath path = new TreePath( an.getPath() );
                        tree.setSelectionPath(path);
                        tree.expandPath(path);
                    }
                    
                    event.dropComplete(true);
                    handled = true;
                }
                else {
                    event.rejectDrop();
                    handled = true;
                }
            }
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        catch(UnsupportedFlavorException ufe) {
            ufe.printStackTrace();
        }
        catch(CloneNotSupportedException cnse) {
            cnse.printStackTrace();
        }
        
        return handled;
    }
    
    public TreePath willHandleDrop(JTree tree, TreePath dropPath, DropTargetDragEvent event) {
        TreePath dropLocation = null;
        
        AbilityTreeNode lastImportantNode = null;
        
        int count = dropPath.getPathCount();
        for(int index = 0; index < count; index++) {
            if ( dropPath.getPathComponent(index) == this ) {
                if ( index+1 < count ) {
                    lastImportantNode = (AbilityTreeNode)dropPath.getPathComponent(index+1);
                }
                else {
                    lastImportantNode = (AbilityTreeNode)dropPath.getPathComponent(index);
                }
            }
        }
        
        if ( event.isDataFlavorSupported(abilityFlavor) ) {
            TreeNode node;
            
            if ( lastImportantNode instanceof AbilityNode ) {
                
                // Always insert above the current node...I know...it is ackward...maybe I will fix this later.
                node = getChildBefore(lastImportantNode);
                if ( node == null ) {
                    // It was over the first child, so return the current path to this node, plus a DropAsChildPlaceHolder
                    dropLocation = new TreePath(this.getPath()); 
                    dropLocation = dropLocation.pathByAddingChild( new DropAsChildPlaceHolder() );
                }
                else {
                    // It was in the middle, so build a path ending with the node above the current...
                    dropLocation = new TreePath(this.getPath());
                    dropLocation = dropLocation.pathByAddingChild(node);
                }
            }
            else {
                // It must be over this sublistNode, so insert it at the end of the children...
                if ( getChildCount() == 0 ) {
                    // There are no children, so just send back this node's path + DropAsChildPlaceHolder.
                    dropLocation = new TreePath(this.getPath());
                    dropLocation = dropLocation.pathByAddingChild( new DropAsChildPlaceHolder() );
                }
                else {
                    // There is a last child, so build a path with it...
                    node = getLastChild();
                    dropLocation = new TreePath(this.getPath());
                    dropLocation = dropLocation.pathByAddingChild(node);
                }
            }
        }
        else if (event.isDataFlavorSupported(abilityListFlavor) && lastImportantNode == this ) {
            // Here we are dropping an ability list of some sort...
            // If there are other abilityLists in this node,
            dropLocation = new TreePath(this.getPath());
            dropLocation = dropLocation.pathByAddingChild( new DropAsChildPlaceHolder() );
        }
        
        if ( DEBUG >= 1 ) System.out.println("Returning Drop Path of: " + dropLocation);
        
        return dropLocation;
    }
    
    public boolean startDrag(AbilityTreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {
        if ( getAbilityList() != null ) {
            Point p = dge.getDragOrigin();
            Rectangle bounds = tree.getPathBounds(path);
            Point offset = new Point(p.x - bounds.x, p.y - bounds.y);
            
            BufferedImage i = tree.buildDragImage(path);
            AbilityTreeTable.startDrag(i, offset);
            
            dge.startDrag(null, i, offset,new ObjectTransferable( getAbilityList(), AbilityList.class) , listener);
            return true;
        }
        return false;
    }
    
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
  /*  public void propertyChange(PropertyChangeEvent evt) {
        // Sublists watch for two things.  First the watch the Sublist property.  If thier parent changes
        // they must move themselves and if they are deleted completely, they must remove and destroy themselves.
        // Second, the must watch the ability.INDEXSIZE.  If an ability is inserted with the appropriate sublist,
        // they must create an abilityNode for that ability.
        String property = evt.getPropertyName();
        if ( sublist != null && property.startsWith( "Sublist" ) ) {
            handleSublistChange();
        }
        else if ( property.equals( "Ability.INDEXSIZE" ) ) {
            updateChildren();
        }
    } */
    
    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e  a ChangeEvent object
     */
    public void stateChanged(ChangeEvent e) {
        updateChildren();
        
    }
    
  /*  protected void handleSublistChange() {
        int index = source.findIndexed( "Sublist", "NAME", getSublist() );
        if ( index == -1 ) {
            // Oh No.  I have been deleted!
            this.removeFromParent();
            this.destroy();
        }
        else {
            String newParentName = source.getIndexedStringValue(index,"Sublist","PARENT");
   
            if ( isSublistNameEqual(newParentName, ((SublistNode)getParent()).getSublist()) == false ) {
                // I used to be a Top level sublist, now I am not.  Better move.
                SublistNode node = findSublistNode(getRoot(), newParentName);
                removeFromParent();
                node.insert(this, node.getChildCount());
            }
        }
    } */
    
 /*   public SublistNode findSublistNode(TreeNode node, String newParentName) {
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
    } */
    
    
    
    public void updateChildren() {
        boolean fireChange = false;
        boolean found;
        // Make sure the abilities in the source match the abilityNodes that are childern
        int nindex, ncount;
        Ability ability;
        AbilityNode an;
        AbilityListNode sn;
        AbilityList sublist;
        int aindex, acount;
        
        int total = 0;
        Vector newChildren = new Vector();
        
        ncount = ( children != null ) ? children.size() : 0;
        
        if ( abilityList != null ) {
            // Copy the Existing Sublists over properly
            acount = abilityList.getSublistCount();
            for( aindex = 0; aindex < acount; aindex++) {
                sublist = abilityList.getSublist(aindex);
                // This sublist needs needs to be in the node.
                // Try to find it in the children array.
                found = false;
                for(nindex=0;nindex<ncount;nindex++) {
                    if ( children.get(nindex) instanceof AbilityListNode ) {
                        sn = (AbilityListNode) children.get(nindex);
                        if ( sn.getAbilityList() == sublist ) {
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
                    sn = new AbilityListNode(model, source, sublist, this);
                    newChildren.add(sn);
                    fireChange = true;
                }
                total++;
            }
            
            AbilityIterator ai = abilityList.getAbilities(false);
            while ( ai.hasNext() ) {
                ability = ai.nextAbility();
                // This ability needs to be in the node.
                // Try to find it in the children array.
                found = false;
                for(nindex=0;nindex<ncount;nindex++) {
                    if ( children.get(nindex) instanceof AbilityNode ) {
                        an = (AbilityNode) children.get(nindex);
                        if ( an.getAbility() == ability ) {
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
                    if ( ability instanceof CombinedAbility ) {
                        an = new CombinedAbilityNode(model, source, this, ability);
                    }
                    else {
                        an = new AbilityNode(model, source, this, ability);
                    }
                    an.setTree(tree);
                    newChildren.add(an);
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
        setAbilityList(null);
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
    
    public AbilityTreeNode findChildSublistNode(AbilityList abilityList) {
        int index;
        int count = children != null ? children.size() : 0;
        Object o;
        
        for ( index=0;index<count;index++) {
            o = children.get(index);
            if ( o instanceof AbilityListNode && ((AbilityListNode)o).getAbilityList() == abilityList ) {
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
    public void addAbility(Ability ability) {
        //int index = abilityList.getAbilityIndex(ability);
        //if ( index == -1  ) {
            if ( tree != null && tree.isEditing() ) {
                tree.stopEditing();
            }
            
            String newName = ability.getName();
            
            
            
            // This is a new ability.
            ability = (Ability)ability.clone();
            
            if ( abilityList.getSource() != null ) {
                newName = abilityList.getSource().getUniqueAbilityName(ability);
            }
            
            ability.setName(newName);
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
        //}
    }
    
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
    
    public void pasteData(Transferable t) {
        if ( t.isDataFlavorSupported(abilityFlavor) ) {
            Ability ability;
            try {
                ability = (Ability)t.getTransferData(abilityFlavor);
                
                addAbility(ability); // This will NOT clone it!
                
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
        else if ( t.isDataFlavorSupported(abilityListFlavor) ) {
            AbilityList al;
            try {
                al = (AbilityList)t.getTransferData(abilityListFlavor);
                
                abilityList.addSublist(al); // This will clone it!
                
                updateChildren();
                
                // Select the dropped child and expand it.
                AbilityTreeNode an = findChildSublistNode(abilityList);
                if ( an != null && tree != null ) {
                    TreePath path = new TreePath( an.getPath() );
                    tree.setSelectionPath(path);
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
    }
    
    /** Getter for property source.
     * @return Value of property source.
     */
    public Target getSource() {
        return source;
    }
    
    /** Setter for property source.
     * @param source New value of property source.
     */
    public void setSource(Target source) {
        this.source = source;
    }
    
    public String toString() {
        return abilityList == null ? "" : abilityList.getName();
    }
    
    /** Returns the Nodes custom input map.
     * This method should return a custom input map of action the Node can perform and has key bindings for.
     * When this node is selected, the AbilityTree will load this input map and bind the keys appropriately.
     *
     * MenuItems returned by invokeMenu which have key bindings should populate an input map and return it.
     *
     * This is also the point that Actions should be updated to respond to this particular node.
     */
    
    public InputMap getInputMap() {
        
        // Setup the Static Action to refer to this particulars nodes configuration
        //removeAction.setTarget(source);
        removeAbilityListAction.setAbilityList(abilityList);
        //debugAction.setAbility(ability);
        
        return inputMap;
    }
    
    /** Returns the Nodes custom action map.
     * This method should return a custom action map of action the Node can perform and has key bindings for.
     * When this node is selected, the AbilityTree will load this action map and bind the keys appropriately.
     *
     * MenuItems returned by invokeMenu which have key bindings should populate an action map and return it.
     */
    
    public ActionMap getActionMap() {
        return actionMap;
    }
    
    public boolean invokeMenu(JPopupMenu popup, JTree tree, TreePath path) {
        boolean added = false;
        
        if ( path.getLastPathComponent() == this ) {
            removeAbilityListAction.setAbilityList(abilityList);
            removeMenuItem.setEnabled(removeAbilityListAction.isEnabled());
            popup.add(removeMenuItem);
            
            addAbilityListAction.setAbilityList(abilityList);
            addMenuItem.setEnabled(true);
            popup.add(addMenuItem);
            
            loadAbilityAction.setTarget(source);
            loadAbilityAction.setSublist(abilityList);
            popup.add(loadMenuItem);
            
            if ( abilityList.getFramework() != null ) {
                configureFrameworkAction.setTarget(source);
                configureFrameworkAction.setFramework(abilityList.getFramework());
                popup.add(configureFrameworkAction);
            }
            
            added = true;
        }
        
        return added;
    }
    
    public Object getValue(int columnIndex) {
        Object v = null;
        
        if ( abilityList != null ) {
            switch (columnIndex) {
                case AbilityTreeTableModel.ABILITY_TREE_REALCOLUMN:
                    v =  Integer.toString( abilityList.getRealCost() );
                    break;
            }
        }
        
        return v;
        
    }
    
    public boolean isEditable(int column) {
        boolean rv = false;
        
        switch (column) {
            case AbilityTreeTableModel.ABILITY_TREE_ABILITYCOLUMN:
                rv = true;
                break;
        }
        
        return rv;
    }
    
    /**
     * Sets the value for node <code>node</code>,
     * at column number <code>column</code>.
     */
    public void setValueAt(int column, Object aValue) {
        // This is mostly unused, however allow the node to decide...
        switch (column) {
            case AbilityTreeTableModel.ABILITY_TREE_ABILITYCOLUMN:
                abilityList.setName( (String) aValue);
        }
    }
    
    
    
    
    /* The following set of methods are for the support of the TreeTable abilityTrees
     *
     * Nodes should implement them, as appropriate.
     */
    
    public TreeTableCellRenderer getTreeTableCellRenderer(int columnIndex) {
        if ( columnIndex == AbilityTreeTableModel.ABILITY_TREE_REALCOLUMN ) {
            return renderer;
        }
        else {
            return null;
        }
    }
    
    public static class RemoveAbilityListAction extends SharedPopupAction {
        private AbilityList abilityList;
        public RemoveAbilityListAction() {
            super("Delete Sublist");
        }
        
        public void actionPerformed(ActionEvent e ) {
            
            if ( abilityList != null && abilityList.getAbilityList() != null ) {
                int result = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to remove the Sublist " + abilityList.getName() + " and all contained abilities?",
                "Delete Sublist?",
                JOptionPane.OK_CANCEL_OPTION);
                
                if ( result == JOptionPane.OK_OPTION ) {
                    abilityList.getAbilityList().removeSublist(abilityList);
                }
            }
        }
        
        public boolean isEnabled() {
            return ( abilityList != null && abilityList.getAbilityList() != null );
        }
        
        public void setAbilityList(AbilityList abilityList) {
            this.abilityList = abilityList;
        }
    }
    public static class AddAbilityListAction extends SharedPopupAction {
        private AbilityList abilityList;
        public AddAbilityListAction() {
            super("Add Sublist");
        }
        
        public void actionPerformed(ActionEvent e ) {
            if ( abilityList != null ) {
                String result = JOptionPane.showInputDialog(
                "Sublist Name",
                "New Sublist");
                
                if ( result != null && result.equals("") == false ) {
                    abilityList.addSublist( new DefaultAbilityList(result) );
                }
            }
        }
        
        public boolean isEnabled() {
            return ( abilityList != null );
        }
        
        public void setAbilityList(AbilityList abilityList) {
            this.abilityList = abilityList;
        }
    }
    
    private static class ConfigureFrameworkAction extends SharedPopupAction {
        private Target target;
        private Framework framework;

        public ConfigureFrameworkAction() {
            super("Configure Framework... (Unimplemented)");
        }

        public void actionPerformed(ActionEvent e) {
            if ( this.target != null && this.framework != null ) {
                
                VPPConfigurationEditor editor = new VPPConfigurationEditor(target, framework);
                editor.setVisible(true);
                
            }
        }

        public Target getTarget() {
            return target;
        }

        public void setTarget(Target target) {
            this.target = target;
        }

        public Framework getFramework() {
            return framework;
        }

        public void setFramework(Framework framework) {
            this.framework = framework;
        }
        
        
        
        
    }
    
    private static class LoadAbilityAction extends SharedPopupAction {
        private Target target;
        private AbilityList sublist;
        public LoadAbilityAction() {
            super("Load Ability...");
        }
        
        public void actionPerformed(ActionEvent e) {
            if (target != null && sublist != null) {
                try {
//                    Ability ability = (Ability)Ability.open( new String[] {"abt"}, "Ability to Load", Ability.class);
//                    
//                    if ( ability != null ) {
//                        sublist.addAbility(ability);
//                    }
                    
                    DetailList[] abilities = DetailList.openMultiple( new String[] {"abt"}, "Abilities", Ability.class );
                    for(DetailList dl : abilities) {
                        if ( dl instanceof Ability ) {
                            Ability ability = (Ability)dl;
                            sublist.addAbility(ability);
                        }
                    }
                }
                catch (Exception exc) {
                    JOptionPane.showMessageDialog(null,
                    "An Error Occurred while saving:\n" +
                    exc.toString(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        public void setTarget(Target target) {
            this.target = target;
        }
        
        public void setSublist(AbilityList  sublist) {
            this.sublist = sublist;
        }
    }
}
