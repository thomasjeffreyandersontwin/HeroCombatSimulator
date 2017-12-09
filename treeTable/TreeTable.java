/*
 * TreeTable.java
 *
 * Created on February 17, 2002, 3:15 PM
 */
package treeTable;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.plaf.UIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import tjava.Destroyable;

/**
 *
 * @author  twalker
 * @version
 */
@SuppressWarnings("serial")
public class TreeTable extends JTree
        implements TreeTableColumnModelListener, Destroyable, Scrollable, PropertyChangeListener {

    private static final String uiClassID = "TreeTableUI";

    /** Do not adjust column widths automatically; use a scrollbar. */
    public static final int AUTO_RESIZE_OFF = 0;

    /** When a column is adjusted in the UI, adjust the next column the opposite way. */
    public static final int AUTO_RESIZE_NEXT_COLUMN = 1;

    /** During UI adjustment, change subsequent columns to preserve the total width;
     * this is the default behavior. */
    public static final int AUTO_RESIZE_SUBSEQUENT_COLUMNS = 2;

    /** During all resize operations, apply adjustments to the last column only. */
    public static final int AUTO_RESIZE_LAST_COLUMN = 3;

    /** During all resize operations, proportionately resize all columns. */
    public static final int AUTO_RESIZE_ALL_COLUMNS = 4;

    /** Holds value of property proxyTreeTableModel. */
    protected TreeTableModel proxyTreeTableModel;

    /**
     *
     */
    protected TreeTableModel baseTreeTableModel;

    /** Holds value of property tableColumnModel. */
    protected TreeTableColumnModel columnModel;

    /**
     *
     */
    protected TreeTableHeader treeTableHeader;

    /** Holds value of property defaultTreeTableCellRenderer. */
    protected TreeTableCellRenderer defaultTreeTableCellRenderer;

    /** Holds value of property defaultTreeTableCellEditor. */
    protected TreeTableCellEditor defaultTreeTableCellEditor;

    /**
     *
     */
    protected Map rendererMap;

    /**
     *
     */
    protected Map editorMap;

    /**
     *
     */
    protected TreeTableRendererAdapter treeTableRendererAdapater;

    /**
     *
     */
    protected TreeTableEditorAdapter treeTableEditorAdapter;

    /**
     *
     */
    protected boolean reentrantCall;

    /**
     *
     */
    protected Color highlightColor = new Color(210, 220, 243);

    /**
     *
     */
    protected boolean highlightEnabled = false;

    /**
     *
     */
    protected int highlightRow = -1;

    /**
     *
     */
    protected TreeTableHighlightListener highlightListener = null;

    /**
     *  Determines if the table automatically resizes the
     *  width of the table's columns to take up the entire width of the
     *  table, and how it does the resizing.
     */
    protected int autoResizeMode;

    /** Stores whether the TreeTable is initialized.
     * This is set to false initially to make the TreeTable accept non-TreeTableModelAdapter models.
     * After initialization, it is set to tree and only TreeTableModelAdapters will be accepted.
     */
    protected boolean initialized = false;

    private boolean forceScrollableTracksViewportHeight = false;

    private boolean forceScrollableTracksViewportWidth = false;

    transient private Comparator<TreePath> displayOrderComparator;

    private TreeTableExpansionTracker expansionTracker = new TreeTableExpansionTracker(this);

    static {
        if (UIManager.get("TreeTableUI") == null) {
            UIManager.put("TreeTableUI", "treeTable.BasicTreeTableUI");
        }

        if (UIManager.get("TreeTableHeaderUI") == null) {
            UIManager.put("TreeTableHeaderUI", "treeTable.BasicTreeTableHeaderUI");
        }
    }

    /** Creates new TreeTable */
    public TreeTable() {
        this(new DefaultTreeTableModel(new DefaultMutableTreeNode("Root")));
    }

    /** Creates new TreeTable
     * @param model
     */
    public TreeTable(TreeTableModel model) {
        setTreeTableModel(model);

        setEditable(true);

        treeTableRendererAdapater = new TreeTableRendererAdapter();
        treeTableEditorAdapter = new TreeTableEditorAdapter(this, treeTableRendererAdapater);

        setCellRenderer(treeTableRendererAdapater);
        setCellEditor(treeTableEditorAdapter);

        setDefaultTreeTableCellRenderer(new DefaultTreeTableCellRenderer());
        setDefaultTreeTableCellEditor(new DefaultTreeTableCellEditor());

        this.setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);

        setupHeaderUI();
        setupTreeTableUI();

        TreeTableHeader h = new TreeTableHeader(getColumnModel());
        setTreeTableHeader(h);

        this.setInvokesStopCellEditing(true);

        initialized = true;
    }

    /** Creates new TreeTable
     * @param root
     */
    public TreeTable(TreeNode root) {
        setTreeTableModel(new DefaultTreeTableModel(root));

        setEditable(true);

        treeTableRendererAdapater = new TreeTableRendererAdapter();
        treeTableEditorAdapter = new TreeTableEditorAdapter(this, treeTableRendererAdapater);

        setCellRenderer(treeTableRendererAdapater);
        setCellEditor(treeTableEditorAdapter);

        setDefaultTreeTableCellRenderer(new DefaultTreeTableCellRenderer());
        setDefaultTreeTableCellEditor(new DefaultTreeTableCellEditor());

        this.setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);

        setupHeaderUI();
        setupTreeTableUI();

        TreeTableHeader h = new TreeTableHeader(getColumnModel());
        setTreeTableHeader(h);

        this.setInvokesStopCellEditing(true);

        initialized = true;
    }

    /**
     * Returns the suffix used to construct the name of the look and feel
     * (L&F) class used to render this component.
     * @return the string "TreeTableUI"
     *
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     *
     */
    protected void setupHeaderUI() {
        if (UIManager.get("TreeTableHeaderUI") == null) {
            UIManager.put("TreeTableHeaderUI", "treeTable.BasicTreeTableHeaderUI");
        }
    }

    /**
     *
     */
    protected void setupTreeTableUI() {
        if (UIManager.get("TreeTableUI") == null) {
            UIManager.put("TreeTableUI", "treeTable.BasicTreeTableUI");
        }
    }

    /** Returns the proxy TreeTableModel.
     *
     * If a tree table model supports FilteredTreeTableModelDelegate
     * or SortedTreeTableModelDelegate interfaces, it will be wrapped
     * in the appropriate proxies to support those services.
     * <P>
     * If you wish to listen to tree events that occur to the
     * model, generally you will want to listen to the
     * proxy model, not the base tree table model events.
     * <P>
     * If you need the original model, use getBaseTreeTableModel().
     * <P>
     * The base model and the proxy model may be the same if
     * the base model does not support any delegate interfaces.
     *
     * @see #getProxyTreeTableModel()
     * @see #getBaseTreeTableModel()
     *
     * @return The proxy model wrapping the base model.
     */
    public TreeTableModel getProxyTreeTableModel() {
        return proxyTreeTableModel;
    }

    /** Returns the base TreeTableModel.
     *
     * If a tree table model supports FilteredTreeTableModelDelegate
     * or SortedTreeTableModelDelegate interfaces, it will be wrapped
     * in the appropriate proxies to support those services.
     * <P>
     * If you wish to listen to tree events that occur to the
     * model, generally you will want to listen to the
     * proxy model, not the base tree table model events.
     * <P>
     * If you need the original model, use getBaseTreeTableModel().
     * <P>
     * The base model and the proxy model may be the same if
     * the base model does not support any delegate interfaces.
     *
     * @see #getProxyTreeTableModel()
     * @see #getBaseTreeTableModel()
     *
     * @return The base tree table model.
     */
    public TreeTableModel getBaseTreeTableModel() {
        return baseTreeTableModel;
    }

    /** Setter for property proxyTreeTableModel.
     *
     * If the baseTreeTableModel supports Sortable or Filterable delegates,
     * the base model will be wrapped in proxies that support those
     * services.
     * <P>
     * If you need to get the actual base model back at a later point,
     * use getBaseTreeTableModel(), not getProxyTreeTableModel(), since the
     * later returns the wrapped model, not the original.
     * <P>
     * Subclasses of TreeTable should be careful of the switch that occurs.
     * If you want to added a TableListener, make sure you add it to the
     * proxied model, not the base model.
     *
     *
     * @param baseTreeTableModel New value of property proxyTreeTableModel.
     */
    public void setTreeTableModel(TreeTableModel baseTreeTableModel) {
        if (this.baseTreeTableModel != baseTreeTableModel) {
            if (this.proxyTreeTableModel != null) {
                this.proxyTreeTableModel.removePropertyChangeListener(this);
            }

            this.baseTreeTableModel = baseTreeTableModel;

            this.proxyTreeTableModel = wrapModelWithProxies(baseTreeTableModel);

            if ( this.proxyTreeTableModel != null ) {
                this.proxyTreeTableModel.addTreeModelListener( new TreeModelEventPrinter(14) );
            }
            
            setModel(new TreeTableModelAdapter(this, proxyTreeTableModel));

            TreeTableColumnModel newColumnModel = proxyTreeTableModel.getColumnModel();

//            if (columnModel == null) {
//                columnModel = buildColumnModel(proxyTreeTableModel);
//            }
            setColumnModel(newColumnModel);

            clearTreeExpansionState();



            if (this.proxyTreeTableModel != null) {
                this.proxyTreeTableModel.addPropertyChangeListener(this);

                
            }
        }
    }

    /** Returns a TreeTableModel wrapped with the appropriate proxies for sorting and filtering.
     * 
     * @param treeTableModel
     * @return
     */
    protected TreeTableModel wrapModelWithProxies(TreeTableModel treeTableModel) {

        TreeTableModel baseModel = treeTableModel;
        boolean wrappedInSortable = false;
        boolean wrappedInFilterable = false;

        boolean done = false;
        while (!done) {
            if (treeTableModel instanceof ProxySortableTreeTableModel) {
                ProxySortableTreeTableModel proxySortableTreeTableModel = (ProxySortableTreeTableModel) treeTableModel;
                wrappedInSortable = true;
                baseModel = proxySortableTreeTableModel.getDelegateTreeTableModel();
            }
            else if (treeTableModel instanceof ProxyFilterableTreeTableModel) {
                ProxyFilterableTreeTableModel proxyFilterableTreeTableModel = (ProxyFilterableTreeTableModel) treeTableModel;
                wrappedInFilterable = true;
                baseModel = proxyFilterableTreeTableModel.getDelegateTreeTableModel();
            }
            else {
                done = true;
            }
        }

        TreeTableModel wrappedModel = treeTableModel;

        if (wrappedInSortable == false && baseModel instanceof SortableTreeTableModelDelegate) {
            SortableTreeTableModelDelegate sortableTreeTableModelDelegate = (SortableTreeTableModelDelegate) wrappedModel;
            wrappedModel = new ProxySortableTreeTableModel(sortableTreeTableModelDelegate);
        }

        if (wrappedInFilterable == false && baseModel instanceof FilterableTreeTableModelDelegate) {
            FilterableTreeTableModelDelegate filterableTreeTableModelDelegate = (FilterableTreeTableModelDelegate) wrappedModel;
            wrappedModel = new ProxyFilterableTreeTableModel(filterableTreeTableModelDelegate);
        }

        return wrappedModel;
    }

    @Override
    public void setModel(TreeModel model) {
        if (model instanceof TreeTableModelAdapter || initialized == false) {
            super.setModel(model);
        }
        else {
            throw new IllegalArgumentException("TreeTable only accepts TreeTableModelAdapter for the TreeModel.\nUse setTreeTableModel instead.");
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getPropertyName().equals(SortableTreeTableModel.SORTABLE_PROPERTY)) {
            columnModel.setSortable((Boolean)evt.getNewValue());
        }
    }

    /**
     *
     * @param c
     * @param r
     */
    public void setCellRenderer(Class c, TreeTableCellRenderer r) {
        if (rendererMap == null) {
            rendererMap = new HashMap();
        }

        rendererMap.put(c, r);
    }

    /**
     *
     * @param c
     * @return
     */
    public TreeTableCellRenderer getCellRenderer(Class c) {
        if (rendererMap == null) {
            return null;
        }
        return (TreeTableCellRenderer) rendererMap.get(c);
    }

    /**
     *
     * @param c
     * @param r
     */
    public void setCellEditor(Class c, TreeTableCellEditor r) {
        if (editorMap == null) {
            editorMap = new HashMap();
        }

        editorMap.put(c, r);
    }

    /**
     *
     * @param c
     * @return
     */
    public TreeTableCellEditor getCellEditor(Class c) {
        if (editorMap == null) {
            return null;
        }
        return (TreeTableCellEditor) editorMap.get(c);
    }

    /** Getter for property tableColumnModel.
     * @return Value of property tableColumnModel.
     */
    public TreeTableColumnModel getColumnModel() {
        return columnModel;
    }

    /** Setter for property tableColumnModel.
     * @param columnModel
     */
    public void setColumnModel(TreeTableColumnModel columnModel) {
        if (columnModel == null) {
            throw new IllegalArgumentException("Cannot set a null ColumnModel");
        }
        TreeTableColumnModel old = this.columnModel;
        if (columnModel != old) {
            if (old != null) {
                old.removeColumnModelListener(this);
            }
            this.columnModel = columnModel;

            if ( columnModel != null ) {
                columnModel.setSortable(isSortable());

                columnModel.addColumnModelListener(this);

                // Set the column model of the header as well.
                if (treeTableHeader != null) {
                    treeTableHeader.setColumnModel(columnModel);
                }

                setSortOrder(-1, true);
            }

            firePropertyChange("columnModel", old, columnModel);
            resizeAndRepaint();
        //repaint();
        }
    }

//    protected TreeTableColumnModel buildColumnModel(TreeTableModel baseModel) {
////        TreeTableColumnModel tableColumnModel = new DefaultTreeTableColumnModel();
////
////        int count, index;
////
////        count = baseModel.getColumnCount();
////        for ( index = 0; index < count; index++ ) {
////            TreeTableColumn tc = new TreeTableColumn(index);
////            tc.setHeaderValue(baseModel.getColumnName(index));
////            int preferredWidth = baseModel.getColumnPreferredWidth(index);
////            if ( preferredWidth != -1 ) {
////                tc.setPreferredWidth(preferredWidth);
////                tc.setWidth(preferredWidth);
////            }
////            else {
////                tc.setWidth(85);
////            }
////
////            Icon icon = baseModel.getColumnHeaderIcon(index);
////            tc.setIcon(icon);
////            tableColumnModel.addColumn( tc );
////        }
//
//        //setTableColumnModel(tableColumnModel);
//        TreeTableColumnModel tableColumnModel = proxyTreeTableModel.buildColumnModel();
//        return tableColumnModel;
//    }

    /** Getter for property defaultTreeTableCellRenderer.
     * @return Value of property defaultTreeTableCellRenderer.
     */
    public TreeTableCellRenderer getDefaultTreeTableCellRenderer() {
        return defaultTreeTableCellRenderer;
    }

    /** Setter for property defaultTreeTableCellRenderer.
     * @param defaultTreeTableCellRenderer New value of property defaultTreeTableCellRenderer.
     */
    public void setDefaultTreeTableCellRenderer(TreeTableCellRenderer defaultTreeTableCellRenderer) {
        this.defaultTreeTableCellRenderer = defaultTreeTableCellRenderer;
    }

    /** Getter for property defaultTreeTableCellEditor.
     * @return Value of property defaultTreeTableCellEditor.
     */
    public TreeTableCellEditor getDefaultTreeTableCellEditor() {
        return defaultTreeTableCellEditor;
    }

    /** Setter for property defaultTreeTableCellEditor.
     * @param defaultTreeTableCellEditor New value of property defaultTreeTableCellEditor.
     */
    public void setDefaultTreeTableCellEditor(TreeTableCellEditor defaultTreeTableCellEditor) {
        this.defaultTreeTableCellEditor = defaultTreeTableCellEditor;
    }

    /**
     * Calls the <code>configureEnclosingScrollPane</code> method.
     *
     * @see #configureEnclosingScrollPane
     */
    @Override
    public void addNotify() {
        super.addNotify();
        configureEnclosingScrollPane();
    }

    /**
     * If this <code>JTable</code> is the <code>viewportView</code> of an enclosing <code>JScrollPane</code>
     * (the usual situation), configure this <code>ScrollPane</code> by, amongst other things,
     * installing the table's <code>tableHeader</code> as the <code>columnHeaderView</code> of the scroll pane.
     * When a <code>JTable</code> is added to a <code>JScrollPane</code> in the usual way,
     * using <code>new JScrollPane(myTable)</code>, <code>addNotify</code> is
     * called in the <code>JTable</code> (when the table is added to the viewport).
     * <code>JTable</code>'s <code>addNotify</code> method in turn calls this method,
     * which is protected so that this default installation procedure can
     * be overridden by a subclass.
     *
     * @see #addNotify
     */
    protected void configureEnclosingScrollPane() {
        Container p = getParent();
        if (p instanceof JViewport) {
            Container gp = p.getParent();
            if (gp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) gp;
                // Make certain we are the viewPort's view and not, for
                // example, the rowHeaderView of the scrollPane -
                // an implementor of fixed columns might do this.
                JViewport viewport = scrollPane.getViewport();
                if (viewport == null || viewport.getView() != this) {
                    return;
                }
                scrollPane.setColumnHeaderView(getTreeTableHeader());
                //  scrollPane.getViewport().setBackingStoreEnabled(true);
                Border border = scrollPane.getBorder();
                if (border == null || border instanceof UIResource) {
                    scrollPane.setBorder(UIManager.getBorder("Table.scrollPaneBorder"));
                }
            }
        }
    }

    /**
     * Calls the <code>unconfigureEnclosingScrollPane</code> method.
     *
     * @see #unconfigureEnclosingScrollPane
     */
    @Override
    public void removeNotify() {
        unconfigureEnclosingScrollPane();
        super.removeNotify();
    }

    /**
     * Reverses the effect of <code>configureEnclosingScrollPane</code>
     * by replacing the <code>columnHeaderView</code> of the enclosing scroll pane with
     * <code>null</code>. <code>JTable</code>'s <code>removeNotify</code> method calls
     * this method, which is protected so that this default uninstallation
     * procedure can be overridden by a subclass.
     *
     * @see #removeNotify
     * @see #configureEnclosingScrollPane
     */
    protected void unconfigureEnclosingScrollPane() {
        Container p = getParent();
        if (p instanceof JViewport) {
            Container gp = p.getParent();
            if (gp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) gp;
                // Make certain we are the viewPort's view and not, for
                // example, the rowHeaderView of the scrollPane -
                // an implementor of fixed columns might do this.
                JViewport viewport = scrollPane.getViewport();
                if (viewport == null || viewport.getView() != this) {
                    return;
                }
                scrollPane.setColumnHeaderView(null);
            }
        }
    }

    /**
     * Sets the <code>tableHeader</code> working with this <code>JTable</code> to <code>newHeader</code>.
     * It is legal to have a <code>null</code> <code>tableHeader</code>.
     *
     * @param treeTableHeader
     * @see     #getTableHeader
     * @beaninfo
     *  bound: true
     *  description: The JTableHeader instance which renders the column headers.
     */
    public void setTreeTableHeader(TreeTableHeader treeTableHeader) {
        if (this.treeTableHeader != treeTableHeader) {
            TreeTableHeader old = this.treeTableHeader;
            // Release the old header
            if (old != null) {
                old.setTreeTable(null);
            }
            this.treeTableHeader = treeTableHeader;
            if (treeTableHeader != null) {
                treeTableHeader.setTreeTable(this);
            }
            firePropertyChange("treeTableHeader", old, treeTableHeader);
        }
    }

    /**
     * Returns the <code>tableHeader</code> used by this <code>JTable</code>.
     *
     * @return  the <code>tableHeader</code> used by this table
     * @see     #setTableHeader
     */
    public TreeTableHeader getTreeTableHeader() {
        return treeTableHeader;
    }

    /**
     * Tells listeners that the selection model of the
     * TreeTableColumnModel changed.
     * @param e
     */
    @Override
    public void columnSelectionChanged(ListSelectionEvent e) {
        resizeAndRepaint();
    }

    /** Tells listeners that a column was removed from the model.
     * @param e
     */
    @Override
    public void columnRemoved(TreeTableColumnModelEvent e) {
        resizeAndRepaint();
    }

    /** Tells listeners that a column was moved due to a margin change.
     * @param e
     */
    @Override
    public void columnMarginChanged(ChangeEvent e) {
        if (reentrantCall) {
            return;
        }
        reentrantCall = true;
        if (isEditing()) {
            stopEditing();
        }
        TreeTableColumn resizingColumn = null;
        if (treeTableHeader != null) {
            resizingColumn = treeTableHeader.getResizingColumn();
        }
        if (resizingColumn != null) {
            if (autoResizeMode == AUTO_RESIZE_OFF) {
                resizingColumn.setPreferredWidth(resizingColumn.getWidth());
            }
//            else {
//                int columnIndex = viewIndexForColumn(resizingColumn);
//                int delta = getWidth() - getColumnModel().getTotalColumnWidth();
//                accommodateDelta(columnIndex, delta);
//                repaint();
//                reentrantCall = false;
//                return;
//            }
        }
        resizeAndRepaint();
        reentrantCall = false;
    }

    /** Tells listeners that a column was repositioned.
     * @param e
     */
    @Override
    public void columnMoved(TreeTableColumnModelEvent e) {
        resizeAndRepaint();
    }

    /** Tells listeners that a column was added to the model.
     * @param e
     */
    @Override
    public void columnAdded(TreeTableColumnModelEvent e) {
        resizeAndRepaint();
    }

    /**
     *
     */
    protected void resizeAndRepaint() {
        if (isEditing()) {
            cancelEditing();
        }

        // Complete Hack! necessary to trigger UI to update sizes of nodes...
        firePropertyChange("font", "", getFont());

        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        }

        int width = 0;
        if (columnModel != null) {
            int count = columnModel.getColumnCount();
            for (int index = 0; index < count; index++) {
                TreeTableColumn tc = columnModel.getColumn(index);
                width += tc.getPreferredWidth();
            }
        }

        Dimension d = super.getPreferredSize();

        if (width != 0 && d.width != width) {
            d = new Dimension(width, d.height);
        }
        return d;
    }

    /** Getter for property editingColumn.
     * @return Value of property editingColumn.
     */
    public int getEditingColumn() {
        return treeTableEditorAdapter.getEditingColumn();
    }

    /** Setter for property editingColumn.
     * @param editingColumn New value of property editingColumn.
     */
    public void setEditingColumn(int editingColumn) {
        treeTableEditorAdapter.setEditingColumn(editingColumn);
    }

    /** Returns the column bound in TreeTable coordinates.
     * @param row
     * @param column
     * @return
     */
    public Rectangle getColumnBounds(int row, int column) {
        Rectangle r = getRowBounds(row);

        Object node = getPathForRow(row).getLastPathComponent();
        if (node != null) {
            int modelIndex = columnModel.getColumn(column).getModelIndex();
            int span = proxyTreeTableModel.getColumnSpan(node, modelIndex, columnModel);

            int start = getColumnStart(column);

            int columnCount = columnModel.getColumnCount();

            int width = 0;
            columnCount = (columnCount < column + span) ? columnCount : column + span;

            for (; column < columnCount; column++) {
                width += columnModel.getColumn(column).getWidth();
            }

            int end = start + width - columnModel.getColumnMargin();

            start = (start > r.x) ? start : r.x;
            end = (end > r.x) ? end : r.x;

            end = (end < r.x + r.width) ? end : r.x + r.width;

            r.x = start;
            r.width = end - start;
        }

        return r;
    }

    /* Returns the X-coordinate of the start of column <code>index</code> in the TreeTable coordinate
     * system.
     */
    /**
     *
     * @param index
     * @return
     */
    public int getColumnStart(int index) {
        Class c;

        int start = 0;

        int count, cindex;
        count = columnModel.getColumnCount();
        for (cindex = 0; cindex < count && index != cindex; cindex++) {
            start += columnModel.getColumn(cindex).getWidth();
        }

        return start;
    }

    /**
     *
     * @param index
     * @return
     */
    public int getColumnWidth(int index) {
        return getColumnWidth(index, 1);
    }

    /**
     *
     * @param index
     * @param span
     * @return
     */
    public int getColumnWidth(int index, int span) {
        int columnCount = columnModel.getColumnCount();

        int width = 0;

        columnCount = (columnCount < index + span) ? columnCount : index + span;

        for (; index < columnCount; index++) {
            width += columnModel.getColumn(index).getWidth();
        }

        return width;
    }

    /**
     *
     * @param index
     * @return
     */
    public int getColumnEnd(int index) {
        return getColumnStart(index) + getColumnWidth(index) - columnModel.getColumnMargin();
    }

    /**
     *
     * @param index
     * @param span
     * @return
     */
    public int getColumnEnd(int index, int span) {
        return getColumnStart(index) + getColumnWidth(index, span) - columnModel.getColumnMargin();
    }

    /**
     * Returns the number of columns in the column model. Note that this may
     * be different from the number of columns in the table model.
     *
     * @return  the number of columns in the table
     * @see #getRowCount
     * @see #removeColumn
     */
    public int getColumnCount() {
        return getColumnModel().getColumnCount();
    }

//    /**
//     * Returns the name of the column appearing in the view at
//     * column position <code>column</code>.
//     *
//     * @param  column    the column in the view being queried
//     * @return the name of the column at position <code>column</code>
//     * in the view where the first column is column 0
//     */
//    public String getColumnName(int column) {
//        return getProxyTreeTableModel().getColumnName(convertColumnIndexToModel(column));
//    }

    /**
     * Returns the type of the column appearing in the view at
     * column position <code>column</code>.
     *
     * @param   column   the column in the view being queried
     * @return the type of the column at position <code>column</code>
     * 		in the view where the first column is column 0
     */
    public Class getColumnClass(int column) {
        return getProxyTreeTableModel().getColumnClass(convertColumnIndexToModel(column));
    }

    /** Finds which column the point is in the tree table.
     *
     *  This will walk through all columns and determine which column the point
     *  resides in.  The returned value is in terms of the proxyTreeTableModel's column
     *  index and does take into account the column span of the row
     *  of Point loc.
     *
     *  @param loc 
     * @return
     * @returns The model index of the column.
     */
    public int getColumnForLocation(Point loc) {
        Object node = null;

        TreePath tp = getPathForLocation(loc.x, loc.y);
        if (tp != null) {
            node = tp.getLastPathComponent();
        }

        TreeTableModel model = getProxyTreeTableModel();
        TreeTableColumnModel aColumnModel = getColumnModel();

        int count = aColumnModel.getColumnCount();
        for (int i = 0; i < count; i++) {
            int modelIndex = aColumnModel.getColumn(i).getModelIndex();
            if (loc.x > getColumnStart(i) && loc.x < getColumnEnd(i, model.getColumnSpan(node, modelIndex, aColumnModel))) {

                return modelIndex;
            }
        }

        return model.getPrimaryColumn();
    }

    /**
     * Maps the index of the column in the view at
     * <code>viewColumnIndex</code> to the index of the column
     * in the table model.  Returns the index of the corresponding
     * column in the model.  If <code>viewColumnIndex</code>
     * is less than zero, returns <code>viewColumnIndex</code>.
     *
     * @param   viewColumnIndex     the index of the column in the view
     * @return  the index of the corresponding column in the model
     *
     * @see #convertColumnIndexToView
     */
    public int convertColumnIndexToModel(int viewColumnIndex) {
        if (viewColumnIndex < 0) {
            return viewColumnIndex;
        }
        return getColumnModel().getColumn(viewColumnIndex).getModelIndex();
    }

    /**
     * Maps the index of the column in the table model at
     * <code>modelColumnIndex</code> to the index of the column
     * in the view.  Returns the index of the
     * corresponding column in the view; returns -1 if this column is not
     * being displayed.  If <code>modelColumnIndex</code> is less than zero,
     * returns <code>modelColumnIndex</code>.
     *
     * @param   modelColumnIndex     the index of the column in the model
     * @return   the index of the corresponding column in the view
     *
     * @see #convertColumnIndexToModel
     */
    public int convertColumnIndexToView(int modelColumnIndex) {
        if (modelColumnIndex < 0) {
            return modelColumnIndex;
        }
        TreeTableColumnModel cm = getColumnModel();
        for (int column = 0; column < getColumnCount(); column++) {
            if (cm.getColumn(column).getModelIndex() == modelColumnIndex) {
                return column;
            }
        }
        return -1;
    }

    /**
     * Sets the table's auto resize mode when the table is resized.
     *
     * @param   mode One of 5 legal values:
     *                   AUTO_RESIZE_OFF,
     *                   AUTO_RESIZE_NEXT_COLUMN,
     *                   AUTO_RESIZE_SUBSEQUENT_COLUMNS,
     *                   AUTO_RESIZE_LAST_COLUMN,
     *                   AUTO_RESIZE_ALL_COLUMNS
     *
     * @see     #getAutoResizeMode
     * @see     #sizeColumnsToFit(int)
     * @beaninfo
     *  bound: true
     *  description: Whether the columns should adjust themselves automatically.
     *        enum: AUTO_RESIZE_OFF                JTable.AUTO_RESIZE_OFF
     *              AUTO_RESIZE_NEXT_COLUMN        JTable.AUTO_RESIZE_NEXT_COLUMN
     *              AUTO_RESIZE_SUBSEQUENT_COLUMNS JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
     *              AUTO_RESIZE_LAST_COLUMN        JTable.AUTO_RESIZE_LAST_COLUMN
     *              AUTO_RESIZE_ALL_COLUMNS        JTable.AUTO_RESIZE_ALL_COLUMNS
     */
    public void setAutoResizeMode(int mode) {
        if ((mode == AUTO_RESIZE_OFF) ||
                (mode == AUTO_RESIZE_NEXT_COLUMN) ||
                (mode == AUTO_RESIZE_SUBSEQUENT_COLUMNS) ||
                (mode == AUTO_RESIZE_LAST_COLUMN) ||
                (mode == AUTO_RESIZE_ALL_COLUMNS)) {
            int old = autoResizeMode;
            autoResizeMode = mode;
            resizeAndRepaint();
            if (treeTableHeader != null) {
                treeTableHeader.resizeAndRepaint();
            }
            firePropertyChange("autoResizeMode", old, autoResizeMode);
        }
    }

    /**
     * Returns the auto resize mode of the table.  The default mode
     * is AUTO_RESIZE_SUBSEQUENT_COLUMNS.
     *
     * @return  the autoResizeMode of the table
     *
     * @see     #setAutoResizeMode
     * @see     #sizeColumnsToFit(int)
     */
    public int getAutoResizeMode() {
        return autoResizeMode;
    }

    private TreeTableColumn getResizingColumn() {
        return (treeTableHeader == null) ? null
                : treeTableHeader.getResizingColumn();
    }

    /**
     * Causes this table to lay out its rows and columns.  Overridden so
     * that columns can be resized to accomodate a change in the size of
     * a containing parent.
     */
    @Override
    public void doLayout() {


//            sizeColumnsToFit(-1);
//            super.doLayout();

        TreeTableColumn resizingColumn = getResizingColumn();
        if (resizingColumn == null) {
            setWidthsFromPreferredWidths(false);
        }
        else {
            // JTable behaves like a layout manger - but one in which the
            // user can come along and dictate how big one of the children
            // (columns) is supposed to be.

            // A column has been resized and JTable may need to distribute
            // any overall delta to other columns, according to the resize mode.
            int columnIndex = viewIndexForColumn(resizingColumn);
            int delta = getWidth() - getColumnModel().getTotalColumnWidth();
            accommodateDelta(columnIndex, delta);
            delta = getWidth() - getColumnModel().getTotalColumnWidth();

            // If the delta cannot be completely accomodated, then the
            // resizing column will have to take any remainder. This means
            // that the column is not being allowed to take the requested
            // width. This happens under many circumstances: For example,
            // AUTO_RESIZE_NEXT_COLUMN specifies that any delta be distributed
            // to the column after the resizing column. If one were to attempt
            // to resize the last column of the table, there would be no
            // columns after it, and hence nowhere to distribute the delta.
            // It would then be given entirely back to the resizing column,
            // preventing it from changing size.
            if (delta != 0) {
                resizingColumn.setWidth(resizingColumn.getWidth() + delta);
            }

            // At this point the JTable has to work out what preferred sizes
            // would have resulted in the layout the user has chosen.
            // Thereafter, during window resizing etc. it has to work off
            // the preferred sizes as usual - the idea being that, whatever
            // the user does, everything stays in synch and things don't jump
            // around.
            setWidthsFromPreferredWidths(true);
        }
        super.doLayout();


    }

//    /**
//     * Sizes the table columns to fit the available space.
//     * @deprecated As of Swing version 1.0.3,
//     * replaced by <code>sizeColumnsToFit(int)</code>.
//     * @see #sizeColumnsToFit(int)
//     */
//    public void sizeColumnsToFit(boolean lastColumnOnly) {
//        int oldAutoResizeMode = autoResizeMode;
//        setAutoResizeMode(lastColumnOnly ? AUTO_RESIZE_LAST_COLUMN
//        : AUTO_RESIZE_ALL_COLUMNS);
//        sizeColumnsToFit(-1);
//        setAutoResizeMode(oldAutoResizeMode);
//    }
    /**
     *       Resizes one or more of the columns in the table
     *       so that the total width of all of this <code>JTable</code>'s
     * 	     columns is equal to the width of the table.
     * <p>
     *       When <code>doLayout</code> is called on this <code>JTable</code>,
     *       often as a result of the resizing of an enclosing window,
     *       this method is called with <code>resizingColumn</code>
     *       set to -1. This means that resizing has taken place "outside"
     *       the <code>JTable</code> and the change - or "delta" - should
     *       be distributed to all of the columns regardless of this
     *       <code>JTable</code>'s automatic resize mode.
     * <p>
     *       If the <code>resizingColumn</code> is not -1, it is one of
     *       the columns in the table that has changed size rather than
     *       the table itself. In this case the auto-resize modes govern
     *       the way the extra (or deficit) space is distributed
     *       amongst the available columns.
     * <p>
     *       The modes are:
     * <ul>
     * <li>  AUTO_RESIZE_OFF: Don't automatically adjust the column's
     *       widths at all. Use a horizontal scrollbar to accomodate the
     *       columns when their sum exceeds the width of the
     *       <code>Viewport</code>.  If the <code>JTable</code> is not
     *       enclosed in a <code>JScrollPane</code> this may
     *       leave parts of the table invisible.
     * <li>  AUTO_RESIZE_NEXT_COLUMN: Use just the column after the
     *       resizing column. This results in the "boundary" or divider
     *       between adjacent cells being independently adjustable.
     * <li>  AUTO_RESIZE_SUBSEQUENT_COLUMNS: Use all columns after the
     *       one being adjusted to absorb the changes.  This is the
     *       default behavior.
     * <li>  AUTO_RESIZE_LAST_COLUMN: Automatically adjust the
     *       size of the last column only. If the bounds of the last column
     *       prevent the desired size from being allocated, set the
     *       width of the last column to the appropriate limit and make
     *       no further adjustments.
     * <li>  AUTO_RESIZE_ALL_COLUMNS: Spread the delta amongst all the columns
     *       in the <code>JTable</code>, including the one that is being
     *       adjusted.
     * </ul>
     * <p>
     * <bold>Note:</bold> When a <code>JTable</code> makes adjustments
     *   to the widths of the columns it respects their minimum and
     *   maximum values absolutely.  It is therefore possible that,
     *   even after this method is called, the total width of the columns
     *   is still not equal to the width of the table. When this happens
     *   the <code>JTable</code> does not put itself
     *   in AUTO_RESIZE_OFF mode to bring up a scroll bar, or break other
     *   commitments of its current auto-resize mode -- instead it
     *   allows its bounds to be set larger (or smaller) than the total of the
     *   column minimum or maximum, meaning, either that there
     *   will not be enough room to display all of the columns, or that the
     *   columns will not fill the <code>JTable</code>'s bounds.
     *   These respectively, result in the clipping of some columns
     *   or an area being painted in the <code>JTable</code>'s
     *   background color during painting.
     * <p>
     *   The mechanism for distributing the delta amongst the available
     *   columns is provided in a private method in the <code>JTable</code>
     *   class:
     * <pre>
     *   adjustSizes(long targetSize, final Resizable3 r, boolean inverse)
     * </pre>
     *   an explanation of which is provided in the following section.
     *   <code>Resizable3</code> is a private
     *   interface that allows any data structure containing a collection
     *   of elements with a size, preferred size, maximum size and minimum size
     *   to have its elements manipulated by the algorithm.
     * <p>
     * <H3> Distributing the delta </H3>
     * <p>
     * <H4> Overview </H4>
     * <P>
     * Call "DELTA" the difference between the target size and the
     * sum of the preferred sizes of the elements in r. The individual
     * sizes are calculated by taking the original preferred
     * sizes and adding a share of the DELTA - that share being based on
     * how far each preferred size is from its limiting bound (minimum or
     * maximum).
     * <p>
     * <H4>Definition</H4>
     * <P>
     * Call the individual constraints min[i], max[i], and pref[i].
     * <p>
     * Call their respective sums: MIN, MAX, and PREF.
     * <p>
     * Each new size will be calculated using:
     * <p>
     * <pre>
     *          size[i] = pref[i] + delta[i]
     * </pre>
     * where each individual delta[i] is calculated according to:
     * <p>
     * If (DELTA < 0) we are in shrink mode where:
     * <p>
     * <PRE>
     *                        DELTA
     *          delta[i] = ------------ * (pref[i] - min[i])
     *                     (PREF - MIN)
     * </PRE>
     * If (DELTA > 0) we are in expand mode where:
     * <p>
     * <PRE>
     *                        DELTA
     *          delta[i] = ------------ * (max[i] - pref[i])
     *                      (MAX - PREF)
     * </PRE>
     * <P>
     * The overall effect is that the total size moves that same percentage,
     * k, towards the total minimum or maximum and that percentage guarantees
     * accomodation of the required space, DELTA.
     *
     * <H4>Details</H4>
     * <P>
     * Naive evaluation of the formulae presented here would be subject to
     * the aggregated rounding errors caused by doing this operation in finite
     * precision (using ints). To deal with this, the multiplying factor above,
     * is constantly recalculated and this takes account of the rounding
     * errors in the previous iterations. The result is an algorithm that
     * produces a set of integers whose values exactly sum to the supplied
     * <code>targetSize</code>, and does so by spreading the rounding
     * errors evenly over the given elements.
     *
     * <H4>When the MAX and MIN bounds are hit</H4>
     * <P>
     * When <code>targetSize</code> is outside the [MIN, MAX] range,
     * the algorithm sets all sizes to their appropriate limiting value
     * (maximum or minimum).
     *
     * @param resizingColumn    the column whose resizing made this adjustment
     *                          necessary or -1 if there is no such column
     * @see TreeTableColumn#setWidth
     */
    public void sizeColumnsToFit(int resizingColumn) {
        if (resizingColumn == -1) {
            setWidthsFromPreferredWidths(false);
        }
        else {
            if (autoResizeMode == AUTO_RESIZE_OFF) {
                TreeTableColumn aColumn = getColumnModel().getColumn(resizingColumn);
                aColumn.setPreferredWidth(aColumn.getWidth());
            }
            else {
                int delta = getWidth() - getColumnModel().getTotalColumnWidth();
                accommodateDelta(resizingColumn, delta);
            }
        }
    }

    private void setWidthsFromPreferredWidths(final boolean inverse) {
        int totalWidth = getWidth();
        int totalPreferred = getPreferredSize().width;
        int target = !inverse ? totalWidth : totalPreferred;

        final TreeTableColumnModel cm = columnModel;
        Resizable3 r = new Resizable3() {

            @Override
            public int getElementCount() {
                return cm.getColumnCount();
            }

            @Override
            public int getLowerBoundAt(int i) {
                return cm.getColumn(i).getMinWidth();
            }

            @Override
            public int getUpperBoundAt(int i) {
                return cm.getColumn(i).getMaxWidth();
            }

            @Override
            public int getMidPointAt(int i) {
                if (!inverse) {
                    return cm.getColumn(i).getPreferredWidth();
                //return cm.getColumn(i).getWidth();
                }
                else {
                    return cm.getColumn(i).getWidth();
                }
            }

            @Override
            public void setSizeAt(int s, int i) {
                if (!inverse) {
                    cm.getColumn(i).setWidth(s);
                }
                else {
                    cm.getColumn(i).setPreferredWidth(s);
                }
            }
        };

        adjustSizes(target, r, inverse);
    }

    // Distribute delta over columns, as indicated by the autoresize mode.
    private void accommodateDelta(int resizingColumnIndex, int delta) {
        int columnCount = getColumnCount();
        int from = resizingColumnIndex;
        int to = columnCount;

        // Use the mode to determine how to absorb the changes.
        switch (autoResizeMode) {
            case AUTO_RESIZE_NEXT_COLUMN:
                from = from + 1;
                //to = from + 1;
                to = Math.min(from + 1, columnCount);
                break;
            case AUTO_RESIZE_SUBSEQUENT_COLUMNS:
                from = from + 1;
                to = columnCount;
                break;
            case AUTO_RESIZE_LAST_COLUMN:
                from = columnCount - 1;
                to = from + 1;
                break;
            case AUTO_RESIZE_ALL_COLUMNS:
                from = 0;
                to = columnCount;
                break;
            default:
                return;
        }

        // from = columnCount - 1;
        // to = from + 1;

        final int start = from;
        final int end = to;
        final TreeTableColumnModel cm = columnModel;
        Resizable3 r = new Resizable3() {

            @Override
            public int getElementCount() {
                return end - start;
            }

            @Override
            public int getLowerBoundAt(int i) {
                return cm.getColumn(i + start).getMinWidth();
            }

            @Override
            public int getUpperBoundAt(int i) {
                return cm.getColumn(i + start).getMaxWidth();
            }

            @Override
            public int getMidPointAt(int i) {
                return cm.getColumn(i + start).getWidth();
            }

            @Override
            public void setSizeAt(int s, int i) {
                cm.getColumn(i + start).setWidth(s);
            }
        };

        int totalWidth = 0;
        for (int i = from; i < to; i++) {
            TreeTableColumn aColumn = columnModel.getColumn(i);
            int input = aColumn.getWidth();
            totalWidth = totalWidth + input;
        }

        adjustSizes(totalWidth + delta, r, false);

        //setWidthsFromPreferredWidths(true);
        // setWidthsFromPreferredWidths(false);
        return;
    }

    /**
     * @return the scrollableTracksViewportHeight
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (forceScrollableTracksViewportHeight == true) {
            return true;
        }
        else {
            return super.getScrollableTracksViewportHeight();
        }
    }

    /**
     * @return the scrollableTracksViewportWidth
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {
        if (forceScrollableTracksViewportWidth == true) {
            return true;
        }
        else {
            return super.getScrollableTracksViewportWidth();
        }
    }

    /** Return the force setting for ScrollableTracksViewportHeight.
     *
     * If true, the treetable will track the viewports size.
     *
     * If false, this default to the JTree implementation, which tracks the viewport
     * only if the viewport is bigger than the preferred size of the tree.
     *
     * @return the forceScrollableTracksViewportHeight
     */
    public boolean isForceScrollableTracksViewportHeight() {
        return forceScrollableTracksViewportHeight;
    }

    /** Set the force setting for ScrollableTracksViewportHeight.
     *
     * If true, the treetable will track the viewports size.
     *
     * If false, this default to the JTree implementation, which tracks the viewport
     * only if the viewport is bigger than the preferred size of the tree.
     *
     * @param forceScrollableTracksViewportHeight the forceScrollableTracksViewportHeight to set
     */
    public void setForceScrollableTracksViewportHeight(boolean forceScrollableTracksViewportHeight) {
        if (this.forceScrollableTracksViewportHeight = forceScrollableTracksViewportHeight) {
            boolean superValue = super.getScrollableTracksViewportHeight();

            this.forceScrollableTracksViewportHeight = forceScrollableTracksViewportHeight;

            if (superValue == false && this.forceScrollableTracksViewportHeight == false) {
                // This is the only case where the value will change...
                revalidate();
            }
        }
    }

    /** Return the force setting for ScrollableTracksViewportWidth.
     *
     * If true, the treetable will track the viewports size.
     *
     * If false, this default to the JTree implementation, which tracks the viewport
     * only if the viewport is bigger than the preferred size of the tree.
     *
     * @return the forceScrollableTracksViewportWidth
     */
    public boolean isForceScrollableTracksViewportWidth() {
        return forceScrollableTracksViewportWidth;
    }

    /** Sets the force setting for ScrollableTracksViewportWidth.
     *
     * If true, the treetable will track the viewports size.
     *
     * If false, this default to the JTree implementation, which tracks the viewport
     * only if the viewport is bigger than the preferred size of the tree.
     *
     * @param forceScrollableTracksViewportWidth the forceScrollableTracksViewportWidth to set
     */
    public void setForceScrollableTracksViewportWidth(boolean forceScrollableTracksViewportWidth) {
        if (this.forceScrollableTracksViewportWidth = forceScrollableTracksViewportWidth) {
            boolean superValue = super.getScrollableTracksViewportWidth();

            this.forceScrollableTracksViewportWidth = forceScrollableTracksViewportWidth;

            if (superValue == false && this.forceScrollableTracksViewportWidth == false) {
                // This is the only case where the value will change...
                revalidate();
            }
        }
    }

    private interface Resizable2 {

        public int getElementCount();

        public int getLowerBoundAt(int i);

        public int getUpperBoundAt(int i);

        public void setSizeAt(int newSize, int i);
    }

    private interface Resizable3 extends Resizable2 {

        public int getMidPointAt(int i);
    }

    private void adjustSizes(long target, final Resizable3 r, boolean inverse) {
        int N = r.getElementCount();
        long totalPreferred = 0;
        for (int i = 0; i < N; i++) {
            totalPreferred += r.getMidPointAt(i);
        }
        Resizable2 s;
        if ((target < totalPreferred) == !inverse) {
            s = new Resizable2() {

                @Override
                public int getElementCount() {
                    return r.getElementCount();
                }

                @Override
                public int getLowerBoundAt(int i) {
                    return r.getLowerBoundAt(i);
                }

                @Override
                public int getUpperBoundAt(int i) {
                    return r.getMidPointAt(i);
                }

                @Override
                public void setSizeAt(int newSize, int i) {
                    r.setSizeAt(newSize, i);
                }
            };
        }
        else {
            s = new Resizable2() {

                @Override
                public int getElementCount() {
                    return r.getElementCount();
                }

                @Override
                public int getLowerBoundAt(int i) {
                    return r.getMidPointAt(i);
                }

                @Override
                public int getUpperBoundAt(int i) {
                    return r.getUpperBoundAt(i);
                }

                @Override
                public void setSizeAt(int newSize, int i) {
                    r.setSizeAt(newSize, i);
                }
            };
        }
        adjustSizes(target, s, !inverse);
    }

    private void adjustSizes(long target, Resizable2 r, boolean limitToRange) {
        long totalLowerBound = 0;
        long totalUpperBound = 0;
        for (int i = 0; i < r.getElementCount(); i++) {
            totalLowerBound += r.getLowerBoundAt(i);
            totalUpperBound += r.getUpperBoundAt(i);
        }

        if (limitToRange) {
            target = Math.min(Math.max(totalLowerBound, target), totalUpperBound);
        }

        for (int i = 0; i < r.getElementCount(); i++) {
            int lowerBound = r.getLowerBoundAt(i);
            int upperBound = r.getUpperBoundAt(i);
            // Check for zero. This happens when the distribution of the delta
            // finishes early due to a series of "fixed" entries at the end.
            // In this case, lowerBound == upperBound, for all subsequent terms.
            int newSize;
            if (totalLowerBound == totalUpperBound) {
                newSize = lowerBound;
            }
            else {
                double f = (double) (target - totalLowerBound) / (totalUpperBound - totalLowerBound);
                newSize = (int) Math.round(lowerBound + f * (upperBound - lowerBound));
            // We'd need to round manually in an all integer version.
            // size[i] = (int)(((totalUpperBound - target) * lowerBound +
            //     (target - totalLowerBound) * upperBound)/(totalUpperBound-totalLowerBound));
            }
            r.setSizeAt(newSize, i);
            target -= newSize;
            totalLowerBound -= lowerBound;
            totalUpperBound -= upperBound;
        }
    }

    private int viewIndexForColumn(TreeTableColumn aColumn) {
        TreeTableColumnModel cm = getColumnModel();
        for (int column = 0; column < cm.getColumnCount(); column++) {
            if (cm.getColumn(column) == aColumn) {
                return column;
            }
        }
        return -1;
    }

    /**
     *
     */
    @Override
    public void destroy() {
        /** Holds value of property proxyTreeTableModel. */
        if (proxyTreeTableModel instanceof Destroyable) {
            ((Destroyable) proxyTreeTableModel).destroy();
        }
        proxyTreeTableModel = null;

        /** Holds value of property tableColumnModel. */
        columnModel = null;

        treeTableHeader = null;

        /** Holds value of property defaultTreeTableCellRenderer. */
        defaultTreeTableCellRenderer = null;

        /** Holds value of property defaultTreeTableCellEditor. */
        defaultTreeTableCellEditor = null;

        treeTableRendererAdapater = null;
        treeTableEditorAdapter = null;


    }

    /**
     *
     * @return
     */
    public Color getHighlightColor() {
        return highlightColor;
    }

    /**
     *
     * @param highlightColor
     */
    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
    }

    /**
     *
     * @return
     */
    public boolean isHighlightEnabled() {
        return highlightEnabled;
    }

    /**
     *
     * @param highlightEnabled
     */
    public void setHighlightEnabled(boolean highlightEnabled) {
        if (this.highlightEnabled != highlightEnabled) {


            this.highlightEnabled = highlightEnabled;

            if (highlightEnabled) {
                if (highlightListener == null) {
                    highlightListener = new TreeTableHighlightListener(this);
                }
                addMouseListener(highlightListener);
                addMouseMotionListener(highlightListener);
            }
            else {
                removeMouseListener(highlightListener);
                removeMouseMotionListener(highlightListener);
            }
            setHighlightRow(-1);
        }

    }

    /**
     *
     * @return
     */
    public int getHighlightRow() {
        return highlightRow;
    }

    /**
     *
     * @param highlightRow
     */
    public void setHighlightRow(int highlightRow) {
        if (this.highlightRow != highlightRow) {
            if (this.highlightRow != -1) {
                Rectangle bounds = getRowBounds(this.highlightRow);
                if (bounds != null) {
                    repaint(bounds.x, bounds.y, bounds.width, bounds.height);
                }
            }
            this.highlightRow = highlightRow;
            if (this.highlightRow != -1) {
                Rectangle bounds = getRowBounds(this.highlightRow);
                if (bounds != null) {
                    repaint(bounds.x, bounds.y, bounds.width, bounds.height);
                }
            }
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void columnSorted(TreeTableColumnModelEvent e) {
        if (columnModel != null) {
            if (columnModel.getSortColumn() == null) {
                setSortOrder(-1, true);
            }
            else {
                setSortOrder(columnModel.getSortColumn().getModelIndex(), columnModel.isSortAscending());
            }
        }
    }

    /**
     *
     * @param modelIndex
     * @param ascending
     */
    public void setSortOrder(int modelIndex, boolean ascending) {
        if (proxyTreeTableModel instanceof SortableTreeTableModel) {
            SortableTreeTableModel sortableTreeTableModel = (SortableTreeTableModel) proxyTreeTableModel;

            if ( sortableTreeTableModel.isSortable() ) {

                boolean wasRecorded = recordTreeExpansionState();

                sortableTreeTableModel.setSortOrder(modelIndex, ascending);

                restoreTreeExpansionState(wasRecorded == false);

                if ((modelIndex == -1 && columnModel.getSortColumn() != null) || (columnModel.getSortColumn() != null && modelIndex != columnModel.getSortColumn().getModelIndex()) || columnModel.isSortAscending() != ascending) {
                    columnModel.setSort(modelIndex == -1 ? null : columnModel.getColumn(columnModel.getColumnIndex(modelIndex)), ascending);
                }
            }
        }
    }

    public boolean isSortable() {
        if (proxyTreeTableModel instanceof SortableTreeTableModel) {
            SortableTreeTableModel sortableTreeTableModel = (SortableTreeTableModel) proxyTreeTableModel;
            return sortableTreeTableModel.isSortable();
        }
        return false;
    }

    public boolean recordTreeExpansionState() {
        return expansionTracker.recordExpansionState();
    }

    public void restoreTreeExpansionState(boolean clearExpansionState) {
        expansionTracker.restoreExpansionState(clearExpansionState);
    }

    public void clearTreeExpansionState() {
        expansionTracker.clearExpansionState();
    }

    /**
     *
     * @param paths
     * @return
     */
    public TreePath[] getDisplayOrderPaths(TreePath[] paths) {
        // sort the paths to display order rather than selection order
        TreePath[] displayPaths = null;
        if (paths != null) {
            ArrayList selOrder = new ArrayList();
            for (int i = 0; i < paths.length; i++) {
                selOrder.add(paths[i]);
            }

            if (displayOrderComparator == null) {
                displayOrderComparator = new DisplayOrderComparator();
            }

            Collections.sort(selOrder, displayOrderComparator);
            int n = selOrder.size();
            displayPaths = new TreePath[n];
            for (int i = 0; i < n; i++) {
                displayPaths[i] = (TreePath) selOrder.get(i);
            }
        }
        return displayPaths;
    }

    private class DisplayOrderComparator implements Comparator<TreePath> {

        public DisplayOrderComparator() {
        }

        @Override
        public int compare(TreePath o1, TreePath o2) {
            int row1 = getRowForPath(o1);
            int row2 = getRowForPath(o2);
            return row1 - row2;
        }
    }
}
