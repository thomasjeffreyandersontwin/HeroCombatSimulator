package treeTable;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.ObjectOutputStream;
import java.io.IOException;
import javax.accessibility.Accessible;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;

/**
 * This is the object which manages the header of the <code>TreeTable</code>.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.55 04/06/00
 * @author Alan Chung
 * @author Philip Milne
 * @see javax.swing.TreeTable
 */
public class TreeTableHeader extends JComponent implements TreeTableColumnModelListener, Accessible {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "TreeTableHeaderUI";

//
// Instance Variables
//
    /** 
     * The treeTable for which this object is the header;
     * the default is <code>null</code>.
     */
    protected TreeTable treeTable;

    /** 
     * The <code>TreeTableColumnModel</code> of the treeTable header.
     */
    protected TreeTableColumnModel columnModel;

    /** 
     * If true, reordering of columns are allowed by the user;
     * the default is true. 
     */
    protected boolean reorderingAllowed;

    /** 
     * If true, resizing of columns are allowed by the user; 
     * the default is true. 
     */
    protected boolean resizingAllowed;

    /**
     * Obsolete as of Java 2 platform v1.3.  Real time repaints, in response
     * to column dragging or resizing, are now unconditional.
     */
    /*
     * If this flag is true, then the header will repaint the treeTable as
     * a column is dragged or resized; the default is true.
     */
    protected boolean updateTableInRealTime;

    /** The index of the column being resized. <code>null</code> if not resizing. */
    transient protected TreeTableColumn resizingColumn;

    /** The index of the column being dragged. <code>null</code> if not dragging. */
    transient protected TreeTableColumn draggedColumn;

    /** The distance from its original position the column has been dragged. */
    transient protected int draggedDistance;

    /**
     *  The default renderer to be used when a <code>TreeTableColumn</code>
     *  does not define a <code>headerRenderer</code>.
     */
    private TreeTableCellRenderer defaultRenderer;

//
// Constructors
//
    /**
     *  Constructs a <code>TreeTableHeader</code> with a default 
     *  <code>TreeTableColumnModel</code>.
     *
     * @see #createDefaultColumnModel
     */
    public TreeTableHeader() {
        this(null);
    }

    /**
     *  Constructs a <code>TreeTableHeader</code> which is initialized with
     *  <code>cm</code> as the column model.  If <code>cm</code> is
     *  <code>null</code> this method will initialize the treeTable header
     *  with a default <code>TreeTableColumnModel</code>.
     *
     * @param cm	the column model for the treeTable
     * @see #createDefaultColumnModel
     */
    public TreeTableHeader(TreeTableColumnModel cm) {
        super();

        if (cm == null) {
            cm = createDefaultColumnModel();
        }
        setColumnModel(cm);

        // Initalize local ivars
        initializeLocalVars();

        // Get UI going
        updateUI();
    }

//
// Local behavior attributes
//
    /** 
     *  Sets the treeTable associated with this header. 
     *  @param  treeTable   the new treeTable
     *  @beaninfo
     *   bound: true
     *   description: The treeTable associated with this header.   
     */
    public void setTreeTable(TreeTable treeTable) {
        TreeTable old = this.treeTable;
        this.treeTable = treeTable;
        firePropertyChange("treeTable", old, treeTable);
    }

    /** 
     *  Returns the treeTable associated with this header.
     *  @return  the <code>treeTable</code> property
     */
    public TreeTable getTreeTable() {
        return treeTable;
    }

    /**
     *  Sets whether the user can drag column headers to reorder columns.
     *
     * @param	reorderingAllowed	true if the treeTable view should allow
     *  				reordering; otherwise false
     * @see	#getReorderingAllowed
     * @beaninfo
     *  bound: true
     *  description: Whether the user can drag column headers to reorder columns.   
     */
    public void setReorderingAllowed(boolean reorderingAllowed) {
        boolean old = this.reorderingAllowed;
        this.reorderingAllowed = reorderingAllowed;
        firePropertyChange("reorderingAllowed", old, reorderingAllowed);
    }

    /**
     * Returns true if the user is allowed to rearrange columns by
     * dragging their headers, false otherwise. The default is true. You can
     * rearrange columns programmatically regardless of this setting.
     *
     * @return	the <code>reorderingAllowed</code> property
     * @see	#setReorderingAllowed
     */
    public boolean getReorderingAllowed() {
        return reorderingAllowed;
    }

    /**
     *  Sets whether the user can resize columns by dragging between headers.
     *
     * @param	resizingAllowed		true if treeTable view should allow
     * 					resizing
     * @see	#getResizingAllowed
     * @beaninfo
     *  bound: true
     *  description: Whether the user can resize columns by dragging between headers.   
     */
    public void setResizingAllowed(boolean resizingAllowed) {
        boolean old = this.resizingAllowed;
        this.resizingAllowed = resizingAllowed;
        firePropertyChange("resizingAllowed", old, resizingAllowed);
    }

    /**
     * Returns true if the user is allowed to resize columns by dragging
     * between their headers, false otherwise. The default is true. You can
     * resize columns programmatically regardless of this setting.
     *
     * @return	the <code>resizingAllowed</code> property
     * @see	#setResizingAllowed
     */
    public boolean getResizingAllowed() {
        return resizingAllowed;
    }

    /**
     * Returns the the dragged column, if and only if, a drag is in
     * process, otherwise returns <code>null</code>.
     *
     * @return	the dragged column, if a drag is in
     * 		process, otherwise returns <code>null</code>
     * @see	#getDraggedDistance
     */
    public TreeTableColumn getDraggedColumn() {
        return draggedColumn;
    }

    /**
     * Returns the column's horizontal distance from its original
     * position, if and only if, a drag is in process. Otherwise, the
     * the return value is meaningless.
     *
     * @return	the column's horizontal distance from its original
     *		position, if a drag is in process, otherwise the return
     *		value is meaningless
     * @see	#getDraggedColumn
     */
    public int getDraggedDistance() {
        return draggedDistance;
    }

    /**
     * Returns the resizing column.  If no column is being
     * resized this method returns <code>null</code>.
     *
     * @return	the resizing column
     */
    public TreeTableColumn getResizingColumn() {
        return resizingColumn;
    }

    /**
     * Obsolete as of Java 2 platform v1.3.  Real time repaints, in response to
     * column dragging or resizing, are now unconditional.
     */
    /*
     *  Sets whether the body of the treeTable updates in real time when
     *  a column is resized or dragged.
     *
     * @param	flag			true if tableView should update
     *					the body of the treeTable in real time
     * @see #getUpdateTableInRealTime
     */
    public void setUpdateTableInRealTime(boolean flag) {
        updateTableInRealTime = flag;
    }

    /**
     * Obsolete as of Java 2 platform v1.3.  Real time repaints, in response to
     * column dragging or resizing, are now unconditional.
     */
    /*
     * Returns true if the body of the treeTable view updates in real
     * time when a column is resized or dragged.  User can set this flag to
     * false to speed up the treeTable's response to user resize or drag actions.
     * The default is true.
     *
     * @return	true if the treeTable updates in real time
     * @see #setUpdateTableInRealTime
     */
    public boolean getUpdateTableInRealTime() {
        return updateTableInRealTime;
    }

    /**
     * Sets the default renderer to be used when no <code>headerRenderer</code>
     * is defined by a <code>TabelColumn</code>.
     * @param  defaultRenderer  the default renderer
     */
    public void setDefaultRenderer(TreeTableCellRenderer defaultRenderer) {
        this.defaultRenderer = defaultRenderer;
    }

    /**
     * Returns the default renderer used when no <code>headerRenderer</code>
     * is defined by a <code>TreeTableColumn</code>.  
     * @return the default renderer
     */
    public TreeTableCellRenderer getDefaultRenderer() {
        return defaultRenderer;
    }

    /**
     * Returns the index of the column that <code>point</code> lies in, or -1 if it
     * lies out of bounds.
     *
     * @return	the index of the column that <code>point</code> lies in, or -1 if it
     *		lies out of bounds
     */
    public int columnAtPoint(Point point) {
        return getColumnModel().getColumnIndexAtX(point.x);
    }

    /**
     * Returns the rectangle containing the header tile at <code>column</code>.
     * When the <code>column</code> parameter is out of bounds this method uses the 
     * same conventions as the <code>TreeTable</code> method <code>getCellRect</code>. 
     *
     * @return	the rectangle containing the header tile at <code>column</code>
     * @see TreeTable#getCellRect
     */
    public Rectangle getHeaderRect(int column) {
        Rectangle r = new Rectangle();
        TreeTableColumnModel cm = getColumnModel();

        r.height = getHeight();

        if (column < 0) {
            // x = width = 0;
        }
        else if (column >= cm.getColumnCount()) {
            r.x = getWidth();
        }
        else {
            for (int i = 0; i < column; i++) {
                r.x += cm.getColumn(i).getWidth();
            }
            r.width = cm.getColumn(column).getWidth();
        }
        return r;
    }

    /**
     * Allows the renderer's tips to be used if there is text set.
     * @param  event  the location of the event identifies the proper 
     *				renderer and, therefore, the proper tip
     * @return the tool tip for this component
     */
    public String getToolTipText(MouseEvent event) {
        String tip = null;

        if (tip == null) {
            tip = getToolTipText();
        }

        return tip;
    }

//
// Managing TreeTableHeaderUI
//
    /**
     * Returns the look and feel (L&F) object that renders this component.
     *
     * @return the <code>TreeTableHeaderUI</code> object that renders this component
     */
    public TreeTableHeaderUI getUI() {
        return (TreeTableHeaderUI) ui;
    }

    /**
     * Sets the look and feel (L&F) object that renders this component.
     *
     * @param ui  the <code>TreeTableHeaderUI</code> L&F object
     * @see UIDefaults#getUI
     */
    public void setUI(TreeTableHeaderUI ui) {
        if (this.ui != ui) {
            super.setUI(ui);
            repaint();
        }
    }

    /**
     * Notification from the <code>UIManager</code> that the look and feel
     * (L&F) has changed. 
     * Replaces the current UI object with the latest version from the 
     * <code>UIManager</code>.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        try {
            setUI((TreeTableHeaderUI) UIManager.getUI(this));
            resizeAndRepaint();
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns the suffix used to construct the name of the look and feel
     * (L&F) class used to render this component.
     * @return the string "TreeTableHeaderUI"
     *
     * @return "TreeTableHeaderUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


//
// Managing models
//
    /**
     *  Sets the column model for this treeTable to <code>newModel</code> and registers
     *  for listener notifications from the new column model.
     *
     * @param	columnModel	the new data source for this treeTable
     * @exception IllegalArgumentException   
     *				if <code>newModel</code> is <code>null</code>
     * @see	#getColumnModel
     * @beaninfo
     *  bound: true
     *  description: The object governing the way columns appear in the view.   
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
            columnModel.addColumnModelListener(this);

            firePropertyChange("columnModel", old, columnModel);
            resizeAndRepaint();
        }
    }

    /**
     * Returns the <code>TreeTableColumnModel</code> that contains all column information
     * of this treeTable header.
     *
     * @return	the <code>columnModel</code> property
     * @see	#setColumnModel
     */
    public TreeTableColumnModel getColumnModel() {
        return columnModel;
    }

//
// Implementing TableColumnModelListener interface
//
    /**
     * Invoked when a column is added to the treeTable column model.
     * <p>
     * Application code will not use these methods explicitly, they
     * are used internally by <code>Jtable</code>.
     *
     * @param e  the event received
     * @see TableColumnModelListener
     */
    public void columnAdded(TreeTableColumnModelEvent e) {
        resizeAndRepaint();
    }

    /**
     * Invoked when a column is removed from the treeTable column model.
     * <p>
     * Application code will not use these methods explicitly, they
     * are used internally by <code>TreeTable</code>.
     *
     * @param e  the event received
     * @see TableColumnModelListener
     */
    public void columnRemoved(TreeTableColumnModelEvent e) {
        resizeAndRepaint();
    }

    /**
     * Invoked when a column is repositioned.
     * <p>
     *
     * Application code will not use these methods explicitly, they
     * are used internally by <code>TreeTable</code>.
     *
     * @param e the event received
     * @see TableColumnModelListener
     */
    public void columnMoved(TreeTableColumnModelEvent e) {
        repaint();
    }

    /**
     * Invoked when a column is moved due to a margin change.
     * <p>
     *
     * Application code will not use these methods explicitly, they
     * are used internally by <code>TreeTable</code>.
     *
     * @param e the event received
     * @see TableColumnModelListener
     */
    public void columnMarginChanged(ChangeEvent e) {
        resizeAndRepaint();
    }


    // --Redrawing the header is slow in cell selection mode.
    // --Since header selection is ugly and it is always clear from the
    // --view which columns are selected, don't redraw the header.
    /**
     * Invoked when the selection model of the <code>TreeTableColumnModel</code>
     * is changed.  This method currently has no effect (the header is not
     * redrawn).
     * <p>
     *
     * Application code will not use these methods explicitly, they
     * are used internally by <code>TreeTable</code>.
     *
     * @param e the event received
     * @see TableColumnModelListener
     */
    public void columnSelectionChanged(ListSelectionEvent e) {
    } // repaint(); }

//
//  Package Methods
//
    /**
     *  Returns the default column model object which is
     *  a <code>DefaultTableColumnModel</code>.  A subclass can override this
     *  method to return a different column model object
     *
     * @return the default column model object
     */
    protected TreeTableColumnModel createDefaultColumnModel() {
        return new DefaultTreeTableColumnModel();
    }

    /**
     *  Returns a default renderer to be used when no header renderer 
     *  is defined by a <code>TreeTableColumn</code>. 
     *  @param  default renderer to be used when there is no header renderer
     */
    protected TreeTableCellRenderer createDefaultRenderer() {
        DefaultTreeTableCellRenderer label = new DefaultTreeTableCellRenderer() {

            private Icon sortIcon = null;

            public Component getTreeTableCellRendererComponent(TreeTable treeTable, Object value,
                    boolean isSelected, boolean expanded,
                    boolean leaf, int row, int column, boolean hasFocus) {
                if (treeTable != null) {
                    TreeTableHeader header = treeTable.getTreeTableHeader();
                    if (header != null) {
                        setForeground(header.getForeground());
                        setBackground(header.getBackground());
                        setFont(header.getFont());

                    }

                    if (value instanceof TreeTableColumn) {
                        TreeTableColumn ttc = (TreeTableColumn) value;
                        Object text = ttc.getHeaderValue();
                        setText((text == null) ? "" : text.toString());
                        setIcon(ttc.getIcon());

                        if (header.getColumnModel().getSortColumn() == ttc) {
                            if (header.getColumnModel().isSortAscending()) {
                                sortIcon = UIManager.getIcon("Tree.forwardSortIcon");
                            }
                            else {
                                sortIcon = UIManager.getIcon("Tree.backwardSortIcon");
                            }
                        }
                        else {
                            sortIcon = null;
                        }

                    }
                    else {
                        setText((value == null) ? "" : value.toString());
                    }
                }


                Border b = BorderFactory.createCompoundBorder(UIManager.getBorder("TableHeader.cellBorder"), BorderFactory.createEmptyBorder(0, 0, 0, 0));
                //Border b = UIManager.getBorder("TableHeader.cellBorder");
                setBorder(b);
                return this;
            }

            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (sortIcon != null) {
                    Rectangle r = getBounds();
                    Insets i = getInsets();

                    sortIcon.paintIcon(this, g, r.width - i.right - sortIcon.getIconWidth() - 2, (r.height - sortIcon.getIconHeight() - i.top - i.bottom) / 2);
                }
            }
        };
        label.setHorizontalAlignment(JLabel.LEFT);
        return label;
    }

    /**
     * Initializes the local variables and properties with default values.
     * Used by the constructor methods.
     */
    protected void initializeLocalVars() {
        setOpaque(true);
        treeTable = null;
        reorderingAllowed = true;
        resizingAllowed = true;
        draggedColumn = null;
        draggedDistance = 0;
        resizingColumn = null;
        updateTableInRealTime = true;

        // I'm registered to do tool tips so we can draw tips for the
        // renderers
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.registerComponent(this);
        setDefaultRenderer(createDefaultRenderer());
    }

    /**
     * Sizes the header and marks it as needing display.  Equivalent
     * to <code>revalidate</code> followed by <code>repaint</code>.
     */
    public void resizeAndRepaint() {
        revalidate();
        repaint();
    }

    /**  
     *  Sets the header's <code>draggedColumn</code> to <code>aColumn</code>
     *  @param  aColumn  the new value for draggedColumn
     */
    public void setDraggedColumn(TreeTableColumn aColumn) {
        draggedColumn = aColumn;
    }

    /**  
     *  Sets the header's <code>draggedDistance</code> to <code>distance</code>.
     *  @param distance  the distance dragged
     */
    public void setDraggedDistance(int distance) {
        draggedDistance = distance;
    }

    /**  
     *  Sets the header's <code>resizingColumn</code> to <code>aColumn</code>.
     *  @param  aColumn  the column being resized
     */
    public void setResizingColumn(TreeTableColumn aColumn) {
        resizingColumn = aColumn;
    }

    /** 
     * See <code>readObject</code> and <code>writeObject</code> in
     * <code>JComponent</code> for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if ((ui != null) && (getUIClassID().equals(uiClassID))) {
            ui.installUI(this);
        }
    }

    /**
     * Returns a string representation of this <code>TreeTableHeader</code>. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * <P>
     * Overriding <code>paramString</code> to provide information about the
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this <code>TreeTableHeader</code>
     */
    protected String paramString() {
        String reorderingAllowedString = (reorderingAllowed ? "true" : "false");
        String resizingAllowedString = (resizingAllowed ? "true" : "false");
        String updateTableInRealTimeString = (updateTableInRealTime ? "true" : "false");

        return super.paramString() +
                ",draggedDistance=" + draggedDistance +
                ",reorderingAllowed=" + reorderingAllowedString +
                ",resizingAllowed=" + resizingAllowedString +
                ",updateTableInRealTime=" + updateTableInRealTimeString;
    }

    public void columnSorted(TreeTableColumnModelEvent e) {
    }
}  // End of Class TreeTableHeader


