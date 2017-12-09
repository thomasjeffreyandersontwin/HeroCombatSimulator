/*
 * AbilityTreeModel.java
 *
 * Created on June 11, 2001, 5:31 PM
 */

package champions.abilityTree;

import champions.Preferences;
import champions.interfaces.ChampionsConstants;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EventListener;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import tjava.Destroyable;
import treeTable.DefaultTreeTableColumnModel;
import treeTable.DefaultTreeTableNode;
import treeTable.TreeTable;
import treeTable.TreeTableCellEditor;
import treeTable.TreeTableCellRenderer;
import treeTable.TreeTableColumn;
import treeTable.TreeTableColumnModel;
import treeTable.TreeTableModel;


/**
 *
 * @author  twalker
 * @version
 */
public class AbilityTreeTableModel //extends DefaultTreeTableModel
implements TreeTableModel, ChampionsConstants, Destroyable{
    
    /**
     *
     */
    protected String[] columnNames = { "Ability", "A","END", "AP", "Cost", "Real" };
    /**
     *
     */
    protected int[] preferredColumnWidths = { 550, 13, 30, 30, 50, 60 };
    
        // Column Name constants
    /**
     *
     */
    public static final int ABILITY_TREE_ABILITYCOLUMN = 0;
    /**
     *
     */
    public static final int ABILITY_TREE_AUTOCOLUMN = 1;
    /**
     * 
     */
    public static final int ABILITY_TREE_ENDCOLUMN = 2;
    /**
     *
     */
    public static final int ABILITY_TREE_APCOLUMN = 3;
    /**
     *
     */
    public static final int ABILITY_TREE_PTSCOLUMN = 4;
    /**
     *
     */
    public static final int ABILITY_TREE_REALCOLUMN = 5;
    /**
     *
     */
    public static final int ABILITY_TREE_WIDTH = 6; // This has to be the last...
    
    
    
    /**
     *
     */
    protected ImageIcon currentIcon = null;
    /**
     *
     */
    protected BufferedImage iconBuffer = null;
    /**
     *
     */
    /**
     *
     */
    /**
     *
     */
    protected Icon criticalIcon, errorIcon, questionIcon;
    /**
     *
     */
    /**
     *
     */
    /**
     *
     */
    protected Icon childCriticalIcon, childErrorIcon, childQuestionIcon;
    /**
     *
     */
    protected JPanel iconPanel;

    protected TreeTableColumnModel columnModel;
    
    /** Icon used to show non-leaf nodes that aren't expanded. */
    transient protected Icon closedIcon;
    
    /** Icon used to show leaf nodes. */
    transient protected Icon leafIcon;
    
    /** Icon used to show non-leaf nodes that are expanded. */
    transient protected Icon openIcon;
    
    /**
     *
     */
    transient protected TreeNode root = null;

    /** Listeners. */
    transient protected EventListenerList listenerList = new EventListenerList();

    transient protected PropertyChangeSupport propertyChangeSupport;
    
     /** 
      * Determines how the <code>isLeaf</code> method figures
      * out if a node is a leaf node. If true, a node is a leaf 
      * node if it does not allow children. (If it allows 
      * children, it is not a leaf node, even if no children
      * are present.) That lets you distinguish between <i>folder</i>
      * nodes and <i>file</i> nodes in a file system, for example.
      * <p>
      * If this value is false, then any node which has no 
      * children is a leaf node, and any node may acquire 
      * children.
      *
      * @see TreeNode#getAllowsChildren
      * @see TreeModel#isLeaf
      * @see #setAsksAllowsChildren
      */
    protected boolean asksAllowsChildren;
    
    /** Creates new AbilityTreeModel
     * @param root
     */
    public AbilityTreeTableModel(TreeNode root) {
        this.root = root;
        
        if ( ABILITY_TREE_WIDTH != columnNames.length ) throw new ArrayIndexOutOfBoundsException();
        
        currentIcon = new ImageIcon();
        iconBuffer = new BufferedImage(16,16, BufferedImage.TYPE_INT_ARGB);
        currentIcon.setImage(iconBuffer);
        iconPanel = new JPanel();
        // iconPanel.setOpaque(false);
        // iconPanel.setBackground( UIManager.getColor("AbilityEditor.background") );
        
        setupIcons();
    }
    
    /**
     *
     */
    public void setupIcons() {
        criticalIcon = UIManager.getIcon( "AbilityTree.criticalIcon" );
        errorIcon = UIManager.getIcon( "AbilityTree.errorIcon" );
        questionIcon = UIManager.getIcon( "AbilityTree.questionIcon" );
        childCriticalIcon = UIManager.getIcon( "AbilityTree.childCriticalIcon" );
        childErrorIcon = UIManager.getIcon( "AbilityTree.childErrorIcon" );
        childQuestionIcon = UIManager.getIcon( "AbilityTree.childQuestionIcon" );
        
        setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
        setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
        setOpenIcon(UIManager.getIcon("Tree.openIcon"));
    }
    
    /**
     *
     * @param currentNode
     */
    public void getNextEditableNode(AbilityTreeNode currentNode) {
        
    }
    
    /**
     * Returns the root of the tree.  Returns null only if the tree has
     * no nodes.
     *
     * @return  the root of the tree
     */
    @Override
    public Object getRoot() {
        return root;
    }
    
    /** 
     * Returns the Column Model to be used with this Model initially.
     *
     * The column model can be changed by the user.  If a model wants to
     * be notified of these changes, it must register a listener.
     *
     * If the model returns null, a column model will be built with the  
     * all columns.
     */
    @Override
    public TreeTableColumnModel getColumnModel() {
        if ( columnModel == null ) {
            Object o = Preferences.getPreferenceList().getColumnModel("CharacterEditor.ABILITYCOLUMNMODEL");
            if (  o instanceof TreeTableColumnModel  ) {
                TreeTableColumnModel tcm = (TreeTableColumnModel)o;
                if ( tcm != null ) {
                    columnModel = copyTreeTableColumnModel(tcm);
                }
            }
        }

        if ( columnModel == null ) {
            columnModel = buildColumnModel();
        }

        return columnModel;
    }

    static public TreeTableColumnModel copyTreeTableColumnModel(TreeTableColumnModel tcm) {

        TreeTableColumnModel newModel = new DefaultTreeTableColumnModel();

            int index,count;
            count = tcm.getColumnCount();
            for(index=0;index<count;index++) {
                TreeTableColumn tc = tcm.getColumn(index);
                TreeTableColumn newTC = new TreeTableColumn(tc.getModelIndex(), tc.getWidth());
                newModel.addColumn(newTC);
             //   newTC.setCellEditor(tc.getCellEditor());
             //   newTC.setCellRenderer(tc.getCellRenderer());
             //   newTC.setHeaderRenderer(tc.getHeaderRenderer());
                newTC.setHeaderValue(tc.getHeaderValue());
                newTC.setIdentifier(tc.getIdentifier());
                newTC.setMaxWidth(tc.getMaxWidth());
                newTC.setMinWidth(tc.getMinWidth());
                newTC.setPreferredWidth(tc.getPreferredWidth());
                newTC.setResizable(tc.getResizable());

            }

            newModel.setColumnMargin( tcm.getColumnMargin() );
            newModel.setColumnSelectionAllowed( tcm.getColumnSelectionAllowed() );
            newModel.setSelectionModel( new DefaultListSelectionModel() );

            return newModel;
    }
    
        /**
     * Returns the child of <I>parent</I> at index <I>index</I> in the parent's
     * child array.  <I>parent</I> must be a node previously obtained from
     * this data source. This should not return null if <i>index</i>
     * is a valid index for <i>parent</i> (that is <i>index</i> >= 0 &&
     * <i>index</i> < getChildCount(<i>parent</i>)).
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @return  the child of <I>parent</I> at index <I>index</I>
     */
    public Object getChild(Object parent, int index) {
        return ((TreeNode)parent).getChildAt(index);
    }
    
        /**
     * Returns the number of children of <I>parent</I>.  Returns 0 if the node
     * is a leaf or if it has no children.  <I>parent</I> must be a node
     * previously obtained from this data source.
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @return  the number of children of the node <I>parent</I>
     */
    public int getChildCount(Object parent) {
        return ((TreeNode)parent).getChildCount();
    }
    
    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     *
     * @see     #removeTreeModelListener
     * @param   l       the listener to add
     */
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }
    
        /**
     * Returns the index of child in parent.
     */
    public int getIndexOfChild(Object parent, Object child) {
        if(parent == null || child == null)
            return -1;
        return ((TreeNode)parent).getIndex((TreeNode)child);
    }
    
    /**
     * Returns true if <I>node</I> is a leaf.  It is possible for this method
     * to return false even if <I>node</I> has no children.  A directory in a
     * filesystem, for example, may contain no files; the node representing
     * the directory is not a leaf, but it also has no children.
     *
     * @param   node    a node in the tree, obtained from this data source
     * @return  true if <I>node</I> is a leaf
     */
    public boolean isLeaf(Object node) {
        if(asksAllowsChildren)
            return !((TreeNode)node).getAllowsChildren();
        return ((TreeNode)node).isLeaf();
    }
    
    /**
     * Removes a listener previously added with <B>addTreeModelListener()</B>.
     *
     * @see     #addTreeModelListener
     * @param   l       the listener to remove
     */
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }
    
        /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    /**
     *
     * @param source
     * @param path
     * @param childIndices
     * @param children
     */
    protected void fireTreeNodesChanged(Object source, Object[] path,
                                        int[] childIndices, 
                                        Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, 
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesChanged(e);
            }          
        }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    /**
     *
     * @param source
     * @param path
     * @param childIndices
     * @param children
     */
    protected void fireTreeNodesInserted(Object source, Object[] path,
                                        int[] childIndices, 
                                        Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, 
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesInserted(e);
            }          
        }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    /**
     *
     * @param source
     * @param path
     * @param childIndices
     * @param children
     */
    protected void fireTreeNodesRemoved(Object source, Object[] path,
                                        int[] childIndices, 
                                        Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, 
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesRemoved(e);
            }          
        }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    /**
     *
     * @param source
     * @param path
     * @param childIndices
     * @param children
     */
    protected void fireTreeStructureChanged(Object source, Object[] path,
                                        int[] childIndices, 
                                        Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, 
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
            }          
        }
    }

    /**
     * Return an array of all the listeners of the given type that 
     * were added to this model. 
     *
     * @param listenerType
     * @return
     * @returns all of the objects recieving <em>listenerType</em> notifications
     *          from this model
     * 
     * @since 1.3
     */
    public EventListener[] getListeners(Class listenerType) { 
	return listenerList.getListeners(listenerType); 
    }
    
        /**
      * Invoke this method after you've changed how node is to be
      * represented in the tree.
         * @param node
         */
    public void nodeChanged(TreeNode node) {
        if(listenerList != null && node != null) {
            TreeNode         parent = node.getParent();

            if(parent != null) {
                int        anIndex = parent.getIndex(node);
                if(anIndex != -1) {
                    int[]        cIndexs = new int[1];

                    cIndexs[0] = anIndex;
                    nodesChanged(parent, cIndexs);
                }
            }
	    else if (node == getRoot()) {
		nodesChanged(node, null);
	    }
        }
    }

    /**
     * Invoke this method if you've modified the TreeNodes upon which this
     * model depends.  The model will notify all of its listeners that the
     * model has changed below the node <code>node</code>.
     * @param node
     */
    public void reload(TreeNode node) {
        if(node != null) {
            fireTreeStructureChanged(this, getPathToRoot(node), null, null);
        }
    }

    /**
      * Invoke this method after you've inserted some TreeNodes into
      * node.  childIndices should be the index of the new elements and
      * must be sorted in ascending order.
     * @param node
     * @param childIndices
     */
    public void nodesWereInserted(TreeNode node, int[] childIndices) {
        if(listenerList != null && node != null && childIndices != null
           && childIndices.length > 0) {
            int               cCount = childIndices.length;
            Object[]          newChildren = new Object[cCount];

            for(int counter = 0; counter < cCount; counter++)
                newChildren[counter] = node.getChildAt(childIndices[counter]);
            fireTreeNodesInserted(this, getPathToRoot(node), childIndices, 
                                  newChildren);
        }
    }
    
    /**
      * Invoke this method after you've removed some TreeNodes from
      * node.  childIndices should be the index of the removed elements and
      * must be sorted in ascending order. And removedChildren should be
      * the array of the children objects that were removed.
     * @param node
     * @param childIndices
     * @param removedChildren
     */
    public void nodesWereRemoved(TreeNode node, int[] childIndices,
                                 Object[] removedChildren) {
        if(node != null && childIndices != null) {
            fireTreeNodesRemoved(this, getPathToRoot(node), childIndices, 
                                 removedChildren);
        }
    }

    /**
      * Invoke this method after you've changed how the children identified by
      * childIndicies are to be represented in the tree.
     * @param node 
     * @param childIndices
     */
    public void nodesChanged(TreeNode node, int[] childIndices) {
        if(node != null) {
	    if (childIndices != null) {
		int            cCount = childIndices.length;

		if(cCount > 0) {
		    Object[]       cChildren = new Object[cCount];

		    for(int counter = 0; counter < cCount; counter++)
			cChildren[counter] = node.getChildAt
			    (childIndices[counter]);
		    fireTreeNodesChanged(this, getPathToRoot(node),
					 childIndices, cChildren);
		}
	    }
	    else if (node == getRoot()) {
		fireTreeNodesChanged(this, getPathToRoot(node), null, null);
	    }
        }
    }

    /**
      * Invoke this method if you've totally changed the children of
      * node and its childrens children...  This will post a
      * treeStructureChanged event.
     * @param node
     */
    public void nodeStructureChanged(TreeNode node) {
        if(node != null) {
           fireTreeStructureChanged(this, getPathToRoot(node), null, null);
        }
    }

    /**
     * Builds the parents of node up to and including the root node,
     * where the original node is the last element in the returned array.
     * The length of the returned array gives the node's depth in the
     * tree.
     * 
     * @param aNode the TreeNode to get the path for
     * @return
     */
    public TreeNode[] getPathToRoot(TreeNode aNode) {
        return getPathToRoot(aNode, 0);
    }

    /**
     * Builds the parents of node up to and including the root node,
     * where the original node is the last element in the returned array.
     * The length of the returned array gives the node's depth in the
     * tree.
     * 
     * @param aNode  the TreeNode to get the path for
     * @param depth  an int giving the number of steps already taken towards
     *        the root (on recursive calls), used to size the returned array
     * @return an array of TreeNodes giving the path from the root to the
     *         specified node 
     */
    protected TreeNode[] getPathToRoot(TreeNode aNode, int depth) {
        TreeNode[]              retNodes;
	// This method recurses, traversing towards the root in order
	// size the array. On the way back, it fills in the nodes,
	// starting from the root and working back to the original node.

        /* Check for null, in case someone passed in a null node, or
           they passed in an element that isn't rooted at root. */
        if(aNode == null) {
            if(depth == 0)
                return null;
            else
                retNodes = new TreeNode[depth];
        }
        else {
            depth++;
            if(aNode == root)
                retNodes = new TreeNode[depth];
            else
                retNodes = getPathToRoot(aNode.getParent(), depth);
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }
    
    
    /**
     * Returns the number of columns the <code>column</code> should span.
     */
    public int getColumnSpan(Object node, int column, TreeTableColumnModel columnModel) {
        return ((AbilityTreeNode)node).getColumnSpan(column);
    }
    
    /** Returns the renderer to be used for this node and column.
     */
    public TreeTableCellRenderer getCellRenderer(Object node, int column) {
        if ( node instanceof AbilityTreeNode )
            return ((AbilityTreeNode)node).getTreeTableCellRenderer(column);
        return null;
    }
    
    /**
     * Returns the name for column number <code>column</code>.
     */
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    
    /**
     * Returns the value to be displayed for node <code>node</code>,
     * at column number <code>column</code>.
     */
    public Object getValueAt(Object node, int column) {
        Object v = ((AbilityTreeNode)node).getValue( column );
        if ( v == null && column == 0 ) {
            v = node.toString();
        }
        return v;
        
    }
    
    
    /** Returns the editor to be used for this node and column.
     */
    public TreeTableCellEditor getCellEditor(Object node, int column) {
        return ((AbilityTreeNode)node).getTreeTableCellEditor(column);
    }
    
    /**
     * Returns the type for column number <code>column</code>.
     */
    public Class getColumnClass(int column) {
        return String.class;
    }
    
    /**
     * Indicates whether the the value for node <code>node</code>,
     * at column number <code>column</code> is editable.
     */
    public boolean isCellEditable(Object node, int column) {
        return ((AbilityTreeNode)node).isEditable(column);
    }
    
    /**
     * Returns the Icon to be used when drawing this node.
     *
     * If the Icon is null, the standard open, closed, leaf icons will be used.
     */
    public Icon getIcon(TreeTable treeTable, Object node, boolean isSelected, boolean expanded, boolean leaf, int row) {
        Icon icon = ((AbilityTreeNode)node).getIcon(treeTable, isSelected, expanded, leaf, row, false);
        
        if ( icon == null ) {
            if (leaf) {
                icon = getLeafIcon();
            } else if (expanded) {
                icon = getOpenIcon();
            } else {
                icon = getClosedIcon();
            }
        }
        
        makeIcon(treeTable, icon, (AbilityTreeNode)node);
        return currentIcon;
    }
    
    /**
     * Sets the value for node <code>node</code>,
     * at column number <code>column</code>.
     */
    public void setValueAt(Object node, int column, Object aValue) {
        // This is mostly unused, however allow the node to decide...
        ((AbilityTreeNode)node).setValueAt(column, aValue);
    }
    
    /**
     * Returns the number ofs availible column.
     */
    public int getColumnCount() {
        return columnNames.length;
    }
    
    /**
     *
     * @param node
     * @return
     */
    public int getNodeStatus(AbilityTreeNode node) {
        
        if ( node == null ) return AbilityTreeNode.OKAY_STATUS;
        
        int status = node.getNodeStatus();
        
        int childStatus;
        int realStatus;
        
        realStatus = status;
        
        AbilityTreeNode child;
        
        int index,count;
        count = node.getChildCount();
        for(index=0;index<count && status != AbilityTreeNode.CRITICAL_STATUS ;index++) {
            child = (AbilityTreeNode)node.getChildAt(index);
            childStatus = getNodeStatus(child);
            if ( childStatus < status ) {
                status = childStatus;
                
                switch (childStatus) {
                    case AbilityTreeNode.CRITICAL_STATUS:
                        realStatus = AbilityTreeNode.CHILD_CRITICAL_STATUS;
                        break;
                    case AbilityTreeNode.ERROR_STATUS:
                        realStatus = AbilityTreeNode.CHILD_ERROR_STATUS;
                        break;
                    case AbilityTreeNode.QUESTION_STATUS:
                        realStatus = AbilityTreeNode.CHILD_QUESTION_STATUS;
                        break;
                    default:
                        realStatus = childStatus;
                }
            }
        }
        return realStatus;
    }
    
    /** Takes the input Icon and creates a new icon, storing it in currentIcon.
     * @param treeTable
     * @param nodeIcon
     * @param node
     */
    
    public void makeIcon(TreeTable treeTable, Icon nodeIcon, AbilityTreeNode node) {
        // currentIcon = new ImageIcon();
        //  iconBuffer = new BufferedImage(16,16, BufferedImage.TYPE_INT_RGB);
        //  currentIcon.setImage(iconBuffer);
        JPanel panel = iconPanel;
        
        Graphics2D g = null;
        try {
            g = (Graphics2D)iconBuffer.getGraphics();
            if ( g != null ) {
                if ( treeTable.isOpaque()) {
                    g.setColor(treeTable.getBackground());
                    g.fillRect(0,0,iconBuffer.getWidth(), iconBuffer.getHeight());
                }
                else {
                    g.setColor(new Color(0,0,0,0));
                    g.setComposite(AlphaComposite.Src);
                    g.fill( new Rectangle2D.Double(0,0,iconBuffer.getWidth(), iconBuffer.getHeight()));
                }
                
                g.setComposite(AlphaComposite.SrcOver);
                
                if (nodeIcon != null ) {
                    
                    nodeIcon.paintIcon(panel, g, 0, 0);
                }
                
                int status = getNodeStatus(node);
                switch( status ) {
                    case AbilityTreeNode.CRITICAL_STATUS:
                        if ( criticalIcon != null ) criticalIcon.paintIcon(panel, g, 0, 0);
                        break;
                    case AbilityTreeNode.ERROR_STATUS:
                        if ( errorIcon != null ) errorIcon.paintIcon(panel, g, 0, 0);
                        break;
                    case AbilityTreeNode.QUESTION_STATUS:
                        if ( questionIcon != null ) questionIcon.paintIcon(panel, g, 0, 0);
                        break;
                    case AbilityTreeNode.CHILD_CRITICAL_STATUS:
                        if ( childCriticalIcon != null ) childCriticalIcon.paintIcon(panel, g, 0, 0);
                        break;
                    case AbilityTreeNode.CHILD_ERROR_STATUS:
                        if ( childErrorIcon != null ) childErrorIcon.paintIcon(panel, g, 0, 0);
                        break;
                    case AbilityTreeNode.CHILD_QUESTION_STATUS:
                        if ( childQuestionIcon != null ) childQuestionIcon.paintIcon(panel, g, 0, 0);
                        break;
                        
                }
                
            }
        }
        finally {
            if ( g != null ) g.dispose();
        }
    }
    
    /**
     * Sets the icon used to represent non-leaf nodes that are expanded.
     * @param newIcon
     */
    public void setOpenIcon(Icon newIcon) {
        openIcon = newIcon;
    }
    
    /**
     * Returns the icon used to represent non-leaf nodes that are expanded.
     */
    public Icon getOpenIcon() {
        return openIcon;
    }
    
    /**
     * Sets the icon used to represent non-leaf nodes that are not expanded.
     * @param newIcon
     */
    public void setClosedIcon(Icon newIcon) {
        closedIcon = newIcon;
    }
    
    /**
     * Returns the icon used to represent non-leaf nodes that are not
     * expanded.
     */
    public Icon getClosedIcon() {
        return closedIcon;
    }
    
    /**
     * Sets the icon used to represent leaf nodes.
     * @param newIcon
     */
    public void setLeafIcon(Icon newIcon) {
        leafIcon = newIcon;
    }
    
    /**
     * Returns the icon used to represent leaf nodes.
     */
    public Icon getLeafIcon() {
        return leafIcon;
    }
    
    /** Returns the Primary Column of the Model.
     *
     * The primary column is the column that is highlighted when a row is selected.
     * The model can return an column of the model it wants or it can return -1 to cause
     * no highlighting to occur.
     */
    public int getPrimaryColumn() {
        return ABILITY_TREE_ABILITYCOLUMN;
    }
    
    /**
     *
     */
    public void destroy() {
        if ( root instanceof Destroyable ) {
            ((Destroyable)root).destroy();
        }
        
        root = null;
    }

    public int getColumnPreferredWidth(int column) {
        return preferredColumnWidths[column];
    }

    public Icon getColumnHeaderIcon(int column) {
        return null;
    }

    /** Create the ColumnModel based upon the column.
     *
     * @return
     */
    public TreeTableColumnModel buildColumnModel() {
        TreeTableColumnModel tableColumnModel = new DefaultTreeTableColumnModel();
        
        
        for(int modelIndex = 0; modelIndex < getColumnCount(); modelIndex++) {
            TreeTableColumn tc = buildColumn(modelIndex);
            
            tableColumnModel.addColumn( tc );
        }
        
        //setTableColumnModel(tableColumnModel);
        return tableColumnModel;
    }
    
    /** Builds the initial TreeTableColumn for column modelIndex.
     *
     *  TreeTable builds columns based only upon the name,
     *  width, and preferred width of a column.  Using buildColumn
     *  a model is allowed more control over the column building
     *  process without implementing the full buildColumnModel method.
     * @param modelIndex
     * @return
     */
    public TreeTableColumn buildColumn(int modelIndex) {
        TreeTableColumn tc = new TreeTableColumn(modelIndex);
        tc.setHeaderValue( getColumnName(modelIndex));
        int preferredWidth = getColumnPreferredWidth(modelIndex);
        if ( preferredWidth != -1 ) {
            tc.setPreferredWidth(preferredWidth);
            tc.setWidth(preferredWidth);
        }
        else {
            tc.setWidth(85);
        }

        Icon icon = getColumnHeaderIcon(modelIndex);
        tc.setIcon(icon);
        
        return tc;
    }

    /**
     *
     * @return
     */
    public boolean isSortable() {
        return false;
    }

    /**
     *
     * @return
     */
    public int getSortColumnIndex() {
        return -1;
    }

    /**
     *
     * @return
     */
    public boolean isSortAscending() {
        return true;
    }

    /**
     * Support for reporting bound property changes for Object properties.
     * This method can be called when a bound property has changed and it will
     * send the appropriate PropertyChangeEvent to any registered
     * PropertyChangeListeners.
     *
     * @param propertyName the property whose value has changed
     * @param oldValue the property's previous value
     * @param newValue the property's new value
     */
    protected void firePropertyChange(String propertyName,
                                      Object oldValue, Object newValue) {
        if (propertyChangeSupport != null &&  (oldValue != newValue && (oldValue == null || oldValue.equals(newValue) == false)) ) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if ( propertyChangeSupport == null ) {
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if ( propertyChangeSupport != null ) {
            propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
        }
    }

    /**
     *
     * @param columnIndex
     * @param forward
     */
    public void setSortOrder(int columnIndex, boolean forward) {
    }
    
    public Color getColumnColor(Object node, int column) {
        if ( node instanceof DefaultTreeTableNode ) {
            return ((DefaultTreeTableNode)node).getColumnColor(column);
        }
        return null;
    }

    public String getToolTipText(Object node, int column, MouseEvent evt) {
        return null;
    }

    public boolean invokeMenu(TreeTable treeTable, TreePath[] paths, JPopupMenu popup) {
        boolean rv = false;

        if (paths != null && paths.length == 1) {
            TreePath path = paths[0];
            int index;
            for (index = path.getPathCount() - 1; index >= 0; index--) {
                Object node = path.getPathComponent(index);

                if (node instanceof DefaultTreeTableNode) {
                    if (((DefaultTreeTableNode) node).invokeMenu(treeTable, path, popup)) {
                        rv = true;
                    }
                }
            }
        }

        return rv;
    }

}
