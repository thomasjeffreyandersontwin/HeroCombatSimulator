 /*
 * PADTreeModel.java
 *
 * Created on February 27, 2002, 11:11 PM
 */

package champions.abilityTree2;

import champions.Ability;
import champions.Target;
import tjava.Filter;
import java.awt.Color;
import java.awt.Font;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import tjava.Destroyable;
import treeTable.DefaultTreeTableModel;
import treeTable.FilterableTreeTableModel;
import treeTable.ProxySortableTreeTableModel;
import treeTable.SortableTreeTableModelDelegate;
import treeTable.TreeTableColumn;
import treeTable.TreeTableModel;


/**
 * TODO: ProxyTreeTable - Switch to filter delegate.
 * @author  twalker
 * @version 
 */
@SuppressWarnings("serial")
public class ATModel extends DefaultTreeTableModel<ATNode> implements FilterableTreeTableModel {
    

    public static Icon endColumnIcon;
    public static Icon dcvColumnIcon;
    public static Icon ocvColumnIcon;
    public static Icon autoActivateColumnIcon;
    public static Icon cpColumnIcon;

    /** Title of column */
    protected String title = ATColumn.NAME_COLUMN.getName();
     
    /** Determines the capabilities of the tree. */
    protected boolean simpleTree = false;
    
    private boolean includeTargetActions = true;
    
    private Filter<Target> targetFilter;
    private Filter<Ability> abilityFilter;
    
    /** Current Number of columns. */
//    protected int columnCount;
    
    protected boolean suppressChangeMessages = false;
    
    /** Creates new PADTreeModel
     * @param root
     * @param title
     */
    public ATModel(ATNode root, String title) {
        super(root);
        this.title = title;
        root.setModel(this);
        root.buildNode();
        
        setSortableDelegate(true);
    }

    
    public void setupIcons() {
        if ( endColumnIcon == null ) endColumnIcon = UIManager.getIcon("AbilityTree.endColumnIcon"); 
        if ( ocvColumnIcon == null ) ocvColumnIcon = UIManager.getIcon("AbilityTree.ocvColumnIcon"); 
        if ( dcvColumnIcon == null ) dcvColumnIcon = UIManager.getIcon("AbilityTree.dcvColumnIcon"); 
        if ( autoActivateColumnIcon == null ) autoActivateColumnIcon = UIManager.getIcon("AbilityTree.autoActivateColumnIcon"); 
        if ( cpColumnIcon == null ) cpColumnIcon = UIManager.getIcon("AbilityTree.cpColumnIcon"); 
    }
    
    /**
     * Returns the number ofs availible column.
     */
    public int getColumnCount() {
        return ATColumn.values().length;
    }
    
    /**
     * Returns the name for column number <code>column</code>.
     */
    public String getColumnName(int column) {
        if ( column == 0) {
            return title;
        }
        return ATColumn.values()[column].getName();
    }
    
    public Icon getColumnHeaderIcon(int column) {
        ATColumn atc = ATColumn.values()[column];
        switch( atc ) {
            case NAME_COLUMN:
                return null;
            case END_COLUMN:
                return endColumnIcon;
            case OCV_COLUMN:
                return ocvColumnIcon;
            case DCV_COLUMN:
                return dcvColumnIcon;
            case AUTO_ACTIVATE_COLUMN:
                return autoActivateColumnIcon;
            case CP_COLUMN:
                return cpColumnIcon;  
            case LAUNCH_COLUMN:
                return null;
        }
        return null;
    }
    
    public TreeTableColumn buildColumn(int modelIndex) {
        
        Icon icon = getColumnHeaderIcon(modelIndex);
        
        return ATColumn.values()[modelIndex].createColumn(icon);
    }
    
    /** Returns the preferred rendering color.
     * 
     * Return null to use the default color.
     * @param node
     * @param column
     * @return
     */
    public Color getColor(Object node, int column) {
        if ( node instanceof ATNode ) {
            return ((ATNode)node).getColumnColor(column);
        }
        return null;
    }
    
     /** Returns the preferred rendering font.
     * 
     * Return null to use the default font.
      * @param node
      * @param column
      * @return
      */
    public Font getFont(Object node, int column) {
        if ( node instanceof ATNode ) {
            return ((ATNode)node).getColumnFont(column);
        }
        return null;
    }
    
    /** Changes the visible status on the columns. 
     *
     */
//    public void setColumnVisible(int column, boolean visible) {
//        boolean update = false;
//        if ( visible  ) {
//            int mask = getColumnMask(column);
//            
//            int oldVisible = visibleColumns;
//            visibleColumns = visibleColumns | mask;
//            
//            if ( oldVisible != visibleColumns ) {
//                update = true;
//            }
//        }
//        else {
//             int mask = getColumnMask(column);
//            
//            int oldVisible = visibleColumns;
//            visibleColumns = visibleColumns & (~mask);
//            
//            if ( oldVisible != visibleColumns ) {
//                update = true;
//            }           
//        }
//        
//        if ( update ) {
//            
//        }
//    }

//    public ATNode getRoot() {
//        return super.getRoot();
//    }

    protected void setRoot(ATNode root) {
        TreeNode oldRoot = getRoot();
        
        if ( root != oldRoot) {
            super.setRoot(root);
            
            if ( oldRoot != null ) {
                if ( oldRoot instanceof Destroyable ) {
                    ((Destroyable)oldRoot).destroy();
                }
            }
        }
    }

    public Class getColumnClass(int column) {
        return ATColumn.values()[column].getColumnClass();
    }
    

    
//    protected void update() {
//        //columnCount = countVisibleColumns();
//        
//    }
    
//    public boolean isColumnVisible(int column) {
//        return ((visibleColumns & getColumnMask(column)) != 0);
//    }
    
//    private int getColumnMask(int column) {
//        return (int)Math.pow(2,column);
//    }
    
//    private int countVisibleColumns() {
//        int columnCount = 0;
//        for(int i = 0; i < ATColumn.MAX_COLUMNS.ordinal(); i++) {
//            if ( isColumnVisible(i) ) columnCount ++;
//        }
//        return columnCount;
//    }
    
    /**
     * Getter for property simpleTree.
     * @return Value of property simpleTree.
     */
    public boolean isSimpleTree() {
        return simpleTree;
    }
    
    /**
     * Setter for property simpleTree.
     * @param simpleTree New value of property simpleTree.
     */
    public void setSimpleTree(boolean simpleTree) {
        this.simpleTree = simpleTree;
    }

    /**
     *
     * @return
     */
    public boolean getIncludeTargetActions() {
        return includeTargetActions;
    }

    /**
     *
     * @param includeTargetActions
     */
    public void setIncludeTargetActions(boolean includeTargetActions) {
        if ( this.includeTargetActions != includeTargetActions ) {
            this.includeTargetActions = includeTargetActions;
            
            ATNode myRoot = getRoot();
            if ( myRoot != null ) {
                myRoot.buildNode();
            }
        }
    }
    
    /**
     *
     * @param suppressChangeMessages
     * @return
     */
    public boolean rebuildTree(boolean suppressChangeMessages) {
        boolean changeOccurred = false;
        
        this.suppressChangeMessages = suppressChangeMessages;
        
        ATNode myRoot = getRoot();
        if ( myRoot != null  ) {
            changeOccurred = myRoot.rebuildTree();
            
            if ( changeOccurred && suppressChangeMessages) {
                Object rootPath[] = new Object[] { myRoot };
                super.fireTreeStructureChanged(this, rootPath , null, null);
            }
        }
        this.suppressChangeMessages = false;
        
        return changeOccurred;
    }

    protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        if (suppressChangeMessages == false) {
            super.fireTreeNodesChanged(source,  path, childIndices, children);
        }
    }

    protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
        if (suppressChangeMessages == false) {
            super.fireTreeNodesRemoved(source,  path, childIndices, children);
        }
    }

    protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
        if (suppressChangeMessages == false) {
            super.fireTreeNodesInserted(source,  path, childIndices, children);
        }
    }

    protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        if (suppressChangeMessages == false) {
            super.fireTreeStructureChanged(source,  path, childIndices, children);
        }
    }

    /**
     *
     * @return
     */
    public Filter<Target> getTargetFilter() {
        return targetFilter;
    }

    /**
     *
     * @param targetFilter
     */
    public void setTargetFilter(Filter<Target> targetFilter) {
        this.targetFilter = targetFilter;
    }

    /**
     *
     * @return
     */
    public Filter<Ability> getAbilityFilter() {
        return abilityFilter;
    }

    /**
     *
     * @param abilityFilter
     */
    public void setAbilityFilter(Filter<Ability> abilityFilter) {
        this.abilityFilter = abilityFilter;
    }
    
    /**
     *
     * @param treePath
     */
    public void nodeWillExpand(TreePath treePath) {
        Object node = treePath.getLastPathComponent();
        if ( node instanceof ATNode) {
            ((ATNode)node).nodeWillExpand();
        }
    }

    @Override
    public boolean isFilterable() {
        return true;
    }

    @Override
    public boolean isFiltered() {
        return abilityFilter != null || targetFilter != null;
    }



    @Override
    public void setFilterObject(Object filterObject) {
        throw new UnsupportedOperationException("Look in ATTree for the ATModel filtering stuff.  I didn't want to move it.");
    }
   
}
