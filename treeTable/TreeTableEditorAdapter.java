/*
 * TreeTableEditorAdapter.java
 *
 * Created on February 17, 2002, 3:38 PM
 */
package treeTable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author twalker
 * @version
 */
public class TreeTableEditorAdapter extends JPanel
        implements ActionListener, TreeCellEditor, TreeSelectionListener {

    /**
     * Editor handling the editing.
     */
    protected TreeTableCellEditor realEditor;

    /**
     * Renderer, used to get border and offsets from.
     */
    protected TreeTableRendererAdapter renderer;

    /**
     * Editing container, will contain the editorComponent.
     */
    //  protected Container                    editingContainer;
    /**
     * Component used in editing, obtained from the editingContainer.
     */
    transient protected Component editingComponent;

    /**
     * Should isCellEditable return true? This is set in configure... based on
     * the path being edited and the selected selected path.
     */
    protected boolean successivePathAreEqual;

    /**
     * Used in editing. Indicates x position to place editingComponent.
     */
    protected transient int offset;

    /**
     * JTree instance listening too.
     */
    protected transient TreeTable treeTable;

    /**
     * last path that was selected.
     */
    protected transient TreePath lastPath;

    /**
     * Used before starting the editing session.
     */
    protected transient Timer timer;

    /**
     * Row that was last passed into getTreeCellEditorComponent.
     */
    protected transient int lastRow;

    /**
     * True if the border selection color should be drawn.
     */
    protected Color borderSelectionColor;

    /**
     * Icon to use when editing.
     */
    protected transient Icon editingIcon;

    /**
     * Font to paint with, null indicates font of renderer is to be used.
     */
    protected Font font;

    protected Object node;
    /**
     * Depth of the node in the tree.
     */
    private int depth;

    static private final int DEBUG = 0;

    /**
     * Listeners to proxy to realEditor
     */
    protected EventListenerList listenerList = new EventListenerList();

    protected boolean layoutValid;
    protected boolean selected;
    protected boolean expanded;
    protected boolean leaf;
    protected int row;
    protected boolean hasFocus;

    protected int editingColumn;

    //protected Icon icon;
    protected JLabel iconLabel;

    protected CellRendererPane rendererPane;

    protected Dimension cachedPreferredSize;

    /**
     * Constructs a DefaultTreeCellEditor object for a JTree using the specified
     * renderer and a default editor. (Use this constructor for normal editing.)
     *
     * @param tree a JTree object
     * @param renderer a DefaultTreeCellRenderer object
     */
    public TreeTableEditorAdapter(TreeTable tree,
            TreeTableRendererAdapter renderer) {
        this(tree, renderer, null);
    }

    /**
     * Constructs a DefaultTreeCellEditor object for a JTree using the specified
     * renderer and the specified editor. (Use this constructor for specialized
     * editing.)
     *
     * @param tree a JTree object
     * @param renderer a DefaultTreeCellRenderer object
     * @param editor a TreeCellEditor object
     */
    public TreeTableEditorAdapter(TreeTable tree, TreeTableRendererAdapter renderer,
            TreeCellEditor editor) {
        this.renderer = renderer;
        //  realEditor = editor;
        // if(realEditor == null)
        //    realEditor = createTreeCellEditor();
        // editingContainer = createContainer();
        setTree(tree);
        setBorderSelectionColor(UIManager.getColor("Tree.editorBorderSelectionColor"));
        iconLabel = new JLabel();
        rendererPane = new CellRendererPane();
        this.add(rendererPane);
        setLayout(null);
        setOpaque(false);
        setFocusable(false);

        // setBorder( new LineBorder( Color.red ) );
    }

    /**
     * Sets the color to use for the border.
     */
    public void setBorderSelectionColor(Color newColor) {
        borderSelectionColor = newColor;
    }

    /**
     * Returns the color the border is drawn.
     */
    public Color getBorderSelectionColor() {
        return borderSelectionColor;
    }

    /**
     * Setter for property node.
     *
     * @param node New value of property node.
     */
    public void setNode(Object node) {
        this.node = node;

        if (node instanceof TreeNode) {
            depth = getLevel((TreeNode) node);
        } else {
            depth = 0;
        }

    }

    /**
     * Returns the number of levels above this node -- the distance from the
     * root to this node. If this node is the root, returns 0.
     *
     * @see	#getDepth
     * @return	the number of levels above this node
     */
    public int getLevel(TreeNode node) {
        TreeNode ancestor;
        int levels = 0;

        ancestor = node;
        while ((ancestor = ancestor.getParent()) != null) {
            levels++;
        }

        return levels;
    }

    protected int getFirstColumnIndent() {
        return (depth - 1) * 20; // 20 is based upon totalChildIndent from BasicTreeUI.  Might not work for all systems.
    }

    /**
     * Sets the font to edit with. null indicates the renderers font should be
     * used. This will NOT override any font you have set in the editor the
     * receiver was instantied with. If null for an editor was passed in a
     * default editor will be created that will pick up this font.
     *
     * @param font the editing Font
     * @see #getFont
     */
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * Gets the font used for editing.
     *
     * @return the editing Font
     * @see #setFont
     */
    public Font getFont() {
        return font;
    }

    public Dimension getPreferredSize() {
        if (cachedPreferredSize == null) {
            updatePreferredSize();
        }

        return cachedPreferredSize;
    }

    public void updatePreferredSize() {
        if (DEBUG > 0) {
            System.out.println("TreeTableEditingAdapter.updatePreferredSize()");
        }

        Dimension d = new Dimension();

        TreeTableColumnModel tcm = treeTable.getColumnModel();
        TreeTableModel ttm = treeTable.getProxyTreeTableModel();

        int columnStart;
        int columnWidth;
        int columnEnd;
        int realStart;
        int span, modelIndex;

        int maxHeight = 0;
        int width = 0;

        columnStart = 0;

        modelIndex = tcm.getColumn(0).getModelIndex();
        span = Math.max(1, ttm.getColumnSpan(node, modelIndex, tcm));

        columnWidth = getColumnWidth(0, span);
        columnEnd = columnStart + columnWidth - tcm.getColumnMargin();

        realStart = getFirstColumnIndent();

        // First Paint the Icon
        int iconOffset = 0;

        Icon icon = getIcon();

        if (icon != null) {
            // Center the Icon...
            //  realStart += icon.getIconWidth() + 3;
            int height = icon.getIconHeight();
            if (height > maxHeight) {
                maxHeight = height;
            }
        }

        // Draw the Columns
        int count, index;

        count = tcm.getColumnCount();
        for (index = 0; index < count;) {
            int realWidth = columnEnd - realStart;
            width += realWidth + tcm.getColumnMargin();

            // Attempt to get the column component and get their preferred sizes.
            int compHeight = -1;
            // We aren't at the last column, so try to restrict the size...
            if (index == editingColumn) {
                TreeTableCellEditor editor = getEditorForColumn(index);
                if (editor instanceof TreeTablePreferredHeightProvider) {
                    if (editor != null) {
                        compHeight = ((TreeTablePreferredHeightProvider) editor).getPreferredHeight(realWidth, treeTable, node, false, expanded, leaf, row, modelIndex, false);
                    }
                }

                if (compHeight == -1) {
                    Component comp = getEditorComponentForColumn(index);
                    if (DEBUG > 0) {
                        System.out.println("TreeTableRendererAdapter.getPreferredSize component for column " + Integer.toString(index) + " was " + comp);
                    }

                    if (comp != null) {
                        d = comp.getPreferredSize();
                        compHeight = d.height;
                    }
                }
            } else {
                TreeTableCellRenderer renderer = getRendererForColumn(index);
                if (renderer instanceof TreeTablePreferredHeightProvider) {
                    if (renderer != null) {
                        compHeight = ((TreeTablePreferredHeightProvider) renderer).getPreferredHeight(realWidth, treeTable, node, false, expanded, leaf, row, modelIndex, false);
                    }
                }

                if (compHeight == -1) {
                    Component comp = getRendererComponentForColumn(index);
                    if (DEBUG > 0) {
                        System.out.println("TreeTableRendererAdapter.getPreferredSize component for column " + Integer.toString(index) + " was " + comp);
                    }

                    if (comp != null) {
                        d = comp.getPreferredSize();
                        compHeight = d.height;
                    }
                }
            }

            if (compHeight > maxHeight) {
                maxHeight = compHeight;
            }

            index += span;
            // Setup for the next column
            if (index < count) {
                modelIndex = tcm.getColumn(index).getModelIndex();
                span = Math.max(1, ttm.getColumnSpan(node, modelIndex, tcm));

                columnStart += columnWidth;
                columnWidth = getColumnWidth(index, span);
                columnEnd = columnStart + columnWidth - tcm.getColumnMargin();

                realStart = (columnStart < 0) ? 0 : columnStart;
            }
        }

//        Dimension d = new Dimension();
//        
//        TreeTableColumnModel tcm = treeTable.getColumnModel();
//        TreeTableModel ttm = treeTable.getProxyTreeTableModel();
//        Class c;
//        Component comp;
//        
//        int width = 0;
//        int maxHeight = 0;
//        int modelIndex;
//        
//        int count, cindex, height;
//        count = tcm.getColumnCount();
//        for(cindex = 0; cindex < count ; cindex++) {
//            modelIndex = tcm.getColumn(cindex).getModelIndex();
//            width += tcm.getColumn(cindex).getWidth();
//            
//            // Attempt to get the column component and get their preferred sizes.
//            if ( ttm.isCellEditable(node, modelIndex) ) {
//                comp = getEditorComponentForColumn(cindex);
//                if ( DEBUG > 0 ) System.out.println("TreeTableEditorAdapter.getPreferredSize component for column " + Integer.toString(cindex) + " was " + comp);
//                if ( comp != null ) {
//                    height = (int)comp.getPreferredSize().getHeight();
//                    if ( height > maxHeight ) maxHeight = height;
//                }
//            }
//        }
//        
//        Icon icon = getIcon();
//        if ( icon != null && icon.getIconHeight() > maxHeight ) maxHeight = icon.getIconHeight();
//        maxHeight += 2; //Add a small margin between cells
//
//        if ( maxHeight < 20 ) {
//            if ( DEBUG > 0 ) System.out.println("TreeTableEditorAdapter.getPreferredSize adjusted maxHeight to be = 20 from " + Integer.toString(maxHeight) + ".");
//            maxHeight = 20;
//        }
        d.width = width;
        d.height = maxHeight;

        if (DEBUG > 0) {
            System.out.println("Preferred Size of EditingAdapter:" + d);
        }

        cachedPreferredSize = d;
    }

    //
    // TreeCellEditor
    //
    /**
     * Configures the editor. Passed onto the realEditor.
     */
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        if (DEBUG > 0) {
            System.out.println("TreeTableEditingAdapter.getTreeCellEditorComponent for " + value + ".  Selected: " + (isSelected ? "T" : "F"));
        }

        setTree((TreeTable) tree);
        //lastRow = row;

        this.row = row;
        this.leaf = leaf;
        this.expanded = expanded;
        this.selected = isSelected;
        setNode(value);
        //this.hasFocus = hasFocus;

        if (editingComponent != null) {
            this.remove(editingComponent);
        }
        editingComponent = null;
        cachedPreferredSize = null;
        layoutValid = false;
        //determineOffset(tree, value, isSelected, expanded, leaf, row);

        /*editingComponent = realEditor.getTreeCellEditorComponent(tree, value,
         isSelected, expanded,leaf, row);
         
         */
        TreePath newPath = tree.getPathForRow(row);

        successivePathAreEqual = (lastPath != null && newPath != null
                && lastPath.equals(newPath));

        lastPath = newPath;

        // Assume we are going to be editing since this getTreeCellEditorComponent is called
        // under 1.4.1 unless we really are going to edit.
        prepareForEditing();

        return this;

        //return new JTextField("Hello");
    }

    public void paint(Graphics g) {
        if (DEBUG > 0) {
            System.out.println("TreeTableEditorAdapter.paint()");
        }

        
        
        TreeTableModel ttm = treeTable.getProxyTreeTableModel();

        Rectangle r = new Rectangle();

        Point adapterLocation = getLocation();
        Dimension adapterDimension = getSize();
        Rectangle originalClip = g.getClipBounds();
        Rectangle newClip = new Rectangle();

        // Draw the Editor Components
        setOpaque(false);

        // Paint the background
        if (treeTable.isHighlightEnabled() && treeTable.getHighlightRow() == row) {
            g.setColor(treeTable.getHighlightColor());
            g.fillRect(originalClip.x, originalClip.y, originalClip.width, originalClip.height);
        } else if (renderer != null && renderer.getBackgroundNonSelectionColor() != null) {
            g.setColor(renderer.getBackgroundNonSelectionColor());
            g.fillRect(originalClip.x, originalClip.y, originalClip.width, originalClip.height);
        }

        //g.drawRect(adapterLocation.x, adapterLocation.y, adapterDimension.width, adapterDimension.height);
       super.paint(g);
       
       if (DEBUG > 0) {
            System.out.println("TreeTableEditorAdapter.paint(): EditorComponenet: " + editingComponent);
        }

        int columnStart;
        int columnWidth;
        int columnEnd;
        int realStart;
        int modelIndex, span;

        TreeTableColumnModel tcm = treeTable.getColumnModel();
        modelIndex = tcm.getColumn(0).getModelIndex();
        span = ttm.getColumnSpan(node, modelIndex, tcm);

        columnStart = getColumnStart(0);
        columnWidth = getColumnWidth(0, span);
        columnEnd = columnStart + columnWidth - tcm.getColumnMargin();

        realStart = (columnStart < 0) ? 0 : columnStart;
        newClip.setBounds(realStart, 0, columnEnd - realStart, adapterDimension.height);
        g.setClip(newClip.createIntersection(originalClip));
        

        // First Paint the Icon
        Icon icon = getIcon();

        int iconOffset = 0;

        if (icon != null) {
            iconLabel.setIcon(icon);

            r.x = 0;
            r.y = 0;

            r.width = icon.getIconWidth();
            r.height = icon.getIconHeight();

            r.y += (adapterDimension.height - r.height) / 2;

            rendererPane.paintComponent(g, iconLabel, this, r);

            iconOffset = r.width + 3;
        }

        // Draw the Columns
        int count, index;
        Class columnClass;

        // Setup Clip for the first column
        realStart = (realStart + iconOffset < 0) ? 0 : realStart + iconOffset;
        newClip.setBounds(realStart, 0, columnEnd - realStart, adapterDimension.height);

        count = tcm.getColumnCount();
        for (index = 0; index < count;) {

            // Only draw the column if it isn't the one that is being edited!
            if (index != editingColumn) {
                // Set the Clip for this column
                g.setClip(newClip.createIntersection(originalClip));

                modelIndex = tcm.getColumn(index).getModelIndex();

                Component c = getRendererComponentForColumn(index);
                if (c != null) {
                    rendererPane.paintComponent(g, c, this, newClip.x, newClip.y, newClip.width, newClip.height, true);
                }
            }

            if (DEBUG > 0) {
                g.setColor(Color.magenta);
                g.drawRect(newClip.x, newClip.y, newClip.width, newClip.height);
                g.setColor(Color.orange);
                g.drawLine(columnStart, 0, columnStart, adapterDimension.height);
                g.setColor(Color.red);
                g.drawLine(columnEnd - 1, 0, columnEnd - 1, adapterDimension.height);
            }

            index += span;
            // Setup for the next column
            if (index < count) {
                modelIndex = tcm.getColumn(index).getModelIndex();
                span = ttm.getColumnSpan(node, modelIndex, tcm);

                columnStart += columnWidth;
                columnWidth = getColumnWidth(index, span);
                columnEnd = columnStart + columnWidth - tcm.getColumnMargin();

                realStart = (columnStart < 0) ? 0 : columnStart;

                newClip.setBounds(realStart, 0, columnEnd - realStart, adapterDimension.height);
            }
        }
         
        g.setClip(originalClip);

    }

    /**
     * Returns the TreeTable Renderer Component for column <code>column</column>.
     *
     * This method should only be called after getTreeCellRendererComponent has setup the
     * relevant node and row information.
     */
    public Component getRendererComponentForColumn(int columnIndex) {
        int modelIndex = treeTable.getColumnModel().getColumn(columnIndex).getModelIndex();

        TreeTableModel ttm = treeTable.getProxyTreeTableModel();

        int primaryColumn = ttm.getPrimaryColumn();
        // Grab the renderer for the node/column
        TreeTableCellRenderer ttcr = ttm.getCellRenderer(node, modelIndex);

        if (ttcr == null) {
            Class columnClass = ttm.getColumnClass(modelIndex);

            ttcr = treeTable.getCellRenderer(columnClass);
        }

        if (ttcr == null) {
            ttcr = treeTable.getDefaultTreeTableCellRenderer();
        }

        if (ttcr != null) {
            return ttcr.getTreeTableCellRendererComponent(treeTable, node, selected && (modelIndex == primaryColumn), expanded, leaf, row, modelIndex, hasFocus && (modelIndex == primaryColumn));
        }
        return null;
    }

    public TreeTableCellRenderer getRendererForColumn(int columnIndex) {
        int modelIndex = treeTable.getColumnModel().getColumn(columnIndex).getModelIndex();

        TreeTableModel ttm = treeTable.getProxyTreeTableModel();

        int primaryColumn = ttm.getPrimaryColumn();
        // Grab the renderer for the node/column
        TreeTableCellRenderer ttcr = ttm.getCellRenderer(node, modelIndex);

        if (ttcr == null) {
            Class columnClass = ttm.getColumnClass(modelIndex);

            ttcr = treeTable.getCellRenderer(columnClass);
        }

        if (ttcr == null) {
            ttcr = treeTable.getDefaultTreeTableCellRenderer();
        }

        return ttcr;
    }

    /**
     * Returns the TreeTable Renderer Component for column <code>column</column>.
     *
     * This method should only be called after getTreeCellRendererComponent has setup the
     * relevant node and row information.
     */
    public Component getEditorComponentForColumn(int columnIndex) {
        int modelIndex = treeTable.getColumnModel().getColumn(columnIndex).getModelIndex();
        TreeTableModel ttm = treeTable.getProxyTreeTableModel();
        // Grab the renderer for the node/column
        TreeTableCellEditor ttcr = ttm.getCellEditor(node, modelIndex);

        if (ttcr == null) {
            Class columnClass = ttm.getColumnClass(modelIndex);

            ttcr = treeTable.getCellEditor(columnClass);
        }

        if (ttcr == null) {
            ttcr = treeTable.getDefaultTreeTableCellEditor();
        }

        if (ttcr != null) {
            return ttcr.getTreeTableCellEditorComponent(treeTable, node, selected && (columnIndex == 0), expanded, leaf, row, modelIndex);
        }
        return null;
    }

    public TreeTableCellEditor getEditorForColumn(int columnIndex) {
        int modelIndex = treeTable.getColumnModel().getColumn(columnIndex).getModelIndex();
        TreeTableModel ttm = treeTable.getProxyTreeTableModel();
        // Grab the renderer for the node/column
        TreeTableCellEditor ttcr = ttm.getCellEditor(node, modelIndex);

        if (ttcr == null) {
            Class columnClass = ttm.getColumnClass(modelIndex);

            ttcr = treeTable.getCellEditor(columnClass);
        }

        if (ttcr == null) {
            ttcr = treeTable.getDefaultTreeTableCellEditor();
        }

        return ttcr;
    }

    /**
     * Returns the value currently being edited.
     */
    public Object getCellEditorValue() {
        return realEditor.getCellEditorValue();
    }

    /**
     * If the realEditor returns true to this message, prepareForEditing is
     * messaged and true is returned.
     */
    public boolean isCellEditable(EventObject event) {
        boolean retValue = false;
        EventObject newEvent;
        if (DEBUG > 0) {
            System.out.println("TreeTableEditorAdapter.isCellEditable: " + event);
        }

        setupNodeInfo(event);

        setupRealEditor(event);

        if (realEditor == null) {
            return false;
        }

        /*    if ( event instanceof MouseEvent ) {
         MouseEvent me = (MouseEvent)event;
         int x = me.getX() - getColumnStart(editingColumn);
         newEvent = new MouseEvent( me.getComponent(), me.getID(), me.getWhen(), me.getModifiers(), x, me.getY(), me.getClickCount(), me.isPopupTrigger());
         }
         else {
         newEvent = event;
         } */
        newEvent = event;

        if (!realEditor.isCellEditable(newEvent)) {
            return false;
        }

        if (DEBUG > 0) {
            System.out.println("RealEditor for canEditImmediately is: " + realEditor);
        }
        if (DEBUG > 0) {
            System.out.println("Row for canEditImmediately is " + Integer.toString(row) + ", Column is " + Integer.toString(editingColumn) + ".");
        }

        Dimension iconSize = getIconSize();
        int iconWidth = iconSize == null ? 0 : iconSize.width;
        int modelIndex = treeTable.getColumnModel().getColumn(editingColumn).getModelIndex();

        if (event == null) {
            retValue = true;
        } else if (realEditor.canEditImmediately(newEvent, treeTable, node, row, editingColumn, (editingColumn == 0) ? iconWidth : 0)) {
            retValue = true;
        } else if (successivePathAreEqual && canEditImmediately(event)) {
            retValue = true;
        } else if (shouldStartEditingTimer(event)) {
            startEditingTimer();
        } else if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        return retValue;
    }

    /**
     * Sets up the Node/Row information appropriately for the event passed in.
     */
    private void setupNodeInfo(EventObject event) {
        if (event instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) event;
            // If this is a mouse event, the relavent node/row information
            // might not be set since isCellEditable can be called prior to
            // getCellEditorComponent.  Just setup the appropriate
            // node/row whenever they aren't set.
            //
            // If they can't be found, bail out...
            Point p = me.getPoint();

            int closestRow = treeTable.getClosestRowForLocation(p.x, p.y);
            Rectangle r = treeTable.getRowBounds(closestRow);
            if (r.contains(p) == false) {
                return;
            }

            TreePath path = treeTable.getPathForRow(closestRow);
            Object pathNode = path.getLastPathComponent();

            if (pathNode == null) {
                return;
            } else {

                row = closestRow;
                node = pathNode;
                if (DEBUG > 0) {
                    System.out.println("TreeTableEditorAdapter.setupNodeInfo set node to " + node + " and row to " + Integer.toString(row) + ".");
                }
            }
        }
    }

    /**
     * Messages the realEditor for the return value.
     */
    public boolean shouldSelectCell(EventObject event) {
        if (DEBUG > 0) {
            System.out.println("EditorAdapter shouldSelectCell called for " + realEditor);
        }
        return realEditor.shouldSelectCell(event);
    }

    /**
     * If the realEditor will allow editing to stop, the realEditor is removed
     * and true is returned, otherwise false is returned.
     */
    public boolean stopCellEditing() {
        if (DEBUG > 0) {
            System.out.println("TreeTableEditorAdapter.stopCellEditing() called.");
        }
        if (realEditor.stopCellEditing()) {
            if (editingComponent != null) {
                remove(editingComponent);
            }
            editingComponent = null;
            return true;
        }
        return false;
    }

    /**
     * Messages cancelCellEditing to the realEditor and removes it from this
     * instance.
     */
    public void cancelCellEditing() {
        if (DEBUG > 0) {
            System.out.println("TreeTableEditorAdapter.cancelCellEditing() called.");
        }
        realEditor.cancelCellEditing();
        if (editingComponent != null) {
            remove(editingComponent);
        }
        editingComponent = null;
    }

    /**
     * Adds the CellEditorListener.
     */
    public void addCellEditorListener(CellEditorListener l) {
        // realEditor.addCellEditorListener(l);
        listenerList.add(CellEditorListener.class, l);
    }

    /**
     * Removes the previously added CellEditorListener l.
     */
    public void removeCellEditorListener(CellEditorListener l) {
        //realEditor.removeCellEditorListener(l);
        listenerList.remove(CellEditorListener.class, l);
    }

    /**
     * Sets up the Real Cell Editor, as determined by the TreeTableCellEditor,
     * for the global node/row/column.
     *
     * setupRealEditor assumes that the node and row are set appropriately for
     * the event passed in.
     *
     */
    protected void setupRealEditor(EventObject event) {
        // editingColumn = -1;
        if (event == null && node == null) {
            realEditor = null;
            return;
        }

        if (event instanceof MouseEvent) {
            Point p = ((MouseEvent) event).getPoint();

            editingColumn = getColumnFor(p);

        }

        if (editingColumn == -1) {
            editingColumn = 0;
        }

        TreeTableModel ttm = treeTable.getProxyTreeTableModel();

        int modelIndex = treeTable.getColumnModel().getColumn(editingColumn).getModelIndex();

        if (ttm.isCellEditable(node, modelIndex)) {
            realEditor = ttm.getCellEditor(node, modelIndex);

            if (realEditor == null) {
                Class columnClass = ttm.getColumnClass(modelIndex);
                realEditor = treeTable.getCellEditor(columnClass);
            }

            if (realEditor == null) {
                realEditor = treeTable.getDefaultTreeTableCellEditor();
            }

            if (realEditor != null) {
                realEditor.getTreeTableCellEditorComponent(treeTable, node, selected, expanded, leaf, row, modelIndex);
            }
        } else {
            realEditor = null;
        }
    }

    //
    // TreeSelectionListener
    //
    /**
     * Resets lastPath.
     */
    public void valueChanged(TreeSelectionEvent e) {
        if (treeTable != null) {
            if (treeTable.getSelectionCount() == 1) {
                lastPath = treeTable.getSelectionPath();

                if (lastPath != null && realEditor != null && lastPath.getLastPathComponent() == node) {
                    realEditor.selectionStateChanged(treeTable.isPathSelected(lastPath));
                }
            } else {
                lastPath = null;
            }
        }
        if (timer != null) {
            timer.stop();
        }

    }

    //
    // ActionListener (for Timer).
    //
    /**
     * Messaged when the timer fires, this will start the editing session.
     */
    public void actionPerformed(ActionEvent e) {
        if (treeTable != null && lastPath != null) {
            treeTable.startEditingAtPath(lastPath);
        }
    }

    //
    // Local methods
    //
    /**
     * Sets the tree currently editing for. This is needed to add a selection
     * listener.
     */
    protected void setTree(TreeTable newTree) {
        if (treeTable != newTree) {
            if (treeTable != null) {
                treeTable.removeTreeSelectionListener(this);
            }
            treeTable = newTree;
            if (treeTable != null) {
                treeTable.addTreeSelectionListener(this);
            }
            if (timer != null) {
                timer.stop();
            }
        }
    }

    /**
     * Returns true if <code>event</code> is a MouseEvent and the click count is
     * 1.
     */
    protected boolean shouldStartEditingTimer(EventObject event) {
        if ((event instanceof MouseEvent)
                && SwingUtilities.isLeftMouseButton((MouseEvent) event)) {
            MouseEvent me = (MouseEvent) event;

            return (me.getClickCount() == 1
                    && inHitRegion(me.getX(), me.getY()));
        }
        return false;
    }

    /**
     * Starts the editing timer.
     */
    protected void startEditingTimer() {
        if (timer == null) {
            timer = new Timer(1200, this);
            timer.setRepeats(false);
        }
        timer.start();
    }

    /**
     * Returns true if <code>event</code> is null, or it is a MouseEvent with a
     * click count > 2 and inHitRegion returns true.
     */
    protected boolean canEditImmediately(EventObject event) {
        if ((event instanceof MouseEvent)
                && SwingUtilities.isLeftMouseButton((MouseEvent) event)) {
            MouseEvent me = (MouseEvent) event;

            return ((me.getClickCount() > 2)
                    && inHitRegion(me.getX(), me.getY()));
        }
        return (event == null);
    }

    /**
     * Should return true if the passed in location is a valid mouse location to
     * start editing from. This is implemented to return false if <code>x</code>
     * is <= the width of the icon and icon gap displayed by the renderer. In
     * other words this returns true if the user clicks over the text part
     * displayed by the renderer, and false otherwise.
     */
    protected boolean inHitRegion(int x, int y) {
        if (lastRow != -1 && treeTable != null) {
            Rectangle bounds = treeTable.getRowBounds(lastRow);

            if (bounds != null && x <= (bounds.x + offset)
                    && offset < (bounds.width - 5)) {
                //System.out.println("Not in hit region!");
                return false;
            }
        }
        return true;
    }

    /*   protected void determineOffset(JTree tree, Object value,
     boolean isSelected, boolean expanded,
     boolean leaf, int row) {
     if(renderer != null) {
     if(leaf)
     editingIcon = renderer.getLeafIcon();
     else if(expanded)
     editingIcon = renderer.getOpenIcon();
     else
     editingIcon = renderer.getClosedIcon();
     if(editingIcon != null)
     offset = renderer.getIconTextGap() +
     editingIcon.getIconWidth();
     else
     offset = renderer.getIconTextGap();
     }
     else {
     editingIcon = null;
     offset = 0;
     }
     } */
    /**
     * Invoked just before editing is to start. Will add the
     * <code>editingComponent</code> to the <code>editingContainer</code>.
     */
    protected void prepareForEditing() {
        if (DEBUG > 0) {
            System.out.println("TreeTableEditorAdapter.prepareForEditing() called.");
        }

        layoutEditingComponent();
    }

    protected void layoutEditingComponent() {
        if (realEditor != null) {
            int modelIndex = treeTable.getColumnModel().getColumn(editingColumn).getModelIndex();
            editingComponent = realEditor.getTreeTableCellEditorComponent(treeTable, node, selected, expanded, leaf, row, modelIndex);

            if (editingComponent != null) {
                if (DEBUG > 0) {
                    System.out.println("TreeTableEditorAdapter.layoutEditingComponent "
                            + "added EditingComponent for " + node + " to TreeTableEditorAdapter layout.");
                }
                add(editingComponent);
                layoutValid = false;
                
               // doLayout();
            }
        }

    }

    public void doLayout() {
        if (DEBUG > 0) {
            System.out.println("TreeTableEditorAdapter.layoutEditingComponentHelper() "
                    + "called.  LayoutValid = " + layoutValid);
        }
        if (editingComponent != null && layoutValid == false) {
            TreeTableModel ttm = treeTable.getProxyTreeTableModel();

            Dimension adapterDimension = getSize();
            Rectangle newClip = new Rectangle();

            int columnStart;
            int columnWidth;
            int columnEnd;
            int realStart;
            int span, modelIndex;

            TreeTableColumnModel tcm = treeTable.getColumnModel();
            modelIndex = tcm.getColumn(editingColumn).getModelIndex();
            span = ttm.getColumnSpan(node, modelIndex, tcm);

            columnStart = getColumnStart(editingColumn);
            columnWidth = getColumnWidth(editingColumn, span);
            columnEnd = columnStart + columnWidth - tcm.getColumnMargin();

            int iconOffset = 0;
            if (editingColumn == 0) {
                // First Paint the Icon
                Icon icon = getIcon();

                if (icon != null) {
                    iconOffset = icon.getIconWidth() + 3;
                }
            }

            // Setup Clip for the editor column
            realStart = (columnStart + iconOffset < 0) ? iconOffset : columnStart + iconOffset;
            newClip.setBounds(realStart, 0, columnEnd - realStart, adapterDimension.height);

            editingComponent.setLocation(newClip.x, newClip.y);
            editingComponent.setBounds(newClip);

            if (DEBUG > 0) {
                System.out.println("TreeTableEditorAdapter :EditingComponent layout " + editingComponent);
            }

            layoutValid = true;

            cachedPreferredSize = null;
            
        }
    }

    private Icon getIcon() {
        TreeTableModel model = treeTable.getProxyTreeTableModel();
        Icon icon = model.getIcon(treeTable, node, selected, expanded, leaf, row);

        if (icon == null) {
            if (leaf) {
                icon = model.getLeafIcon();
            } else if (expanded) {
                icon = model.getOpenIcon();
            } else {
                icon = model.getClosedIcon();
            }
        }

        return icon;
    }

    private Dimension getIconSize() {
        Icon icon;
        if ((icon = getIcon()) != null) {
            Dimension d = new Dimension(icon.getIconWidth(), icon.getIconHeight());
            return d;
        }
        return null;
    }

    /*  public void addNotify() {
     if ( DEBUG > 0 ) System.out.println("Added EditorAdapter for column " + Integer.toString(editingColumn) + " of node " + node + ".");
     super.addNotify();
   
   
     }
   
     public void removeNotify() {
     System.out.println("Removed EditorAdapter for column " + Integer.toString(editingColumn) + " of node " + node + ".");
     super.removeNotify();
     } */
    /**
     * Creates the container to manage placement of editingComponent.
     */
    /*  protected Container createContainer() {
     return new EditorContainer();
     } */
    /**
     * This is invoked if a TreeCellEditor is not supplied in the constructor.
     * It returns a TextField editor.
     */
    /*   protected TreeCellEditor createTreeCellEditor() {
     Border              aBorder = UIManager.getBorder("Tree.editorBorder");
     DefaultCellEditor   editor = new DefaultCellEditor
     (new DefaultTextField(aBorder)) {
     public boolean shouldSelectCell(EventObject event) {
     boolean retValue = super.shouldSelectCell(event);
     getComponent().requestFocus();
     return retValue;
     }
     };
  
     // One click to edit.
     editor.setClickCountToStart(1);
     return editor;
     } */
    // Serialization support.
   /* private void writeObject(ObjectOutputStream s) throws IOException {
     Vector      values = new Vector();
    
     s.defaultWriteObject();
     // Save the realEditor, if its Serializable.
     if(realEditor != null && realEditor instanceof Serializable) {
     values.addElement("realEditor");
     values.addElement(realEditor);
     }
     s.writeObject(values);
     }
    
     private void readObject(ObjectInputStream s)
     throws IOException, ClassNotFoundException {
     s.defaultReadObject();
    
     Vector          values = (Vector)s.readObject();
     int             indexCounter = 0;
     int             maxCounter = values.size();
    
     if(indexCounter < maxCounter && values.elementAt(indexCounter).
     equals("realEditor")) {
     realEditor = (TreeTreeCellEditor)values.elementAt(++indexCounter);
     indexCounter++;
     }
     } */
    /**
     * TextField used when no editor is supplied. This textfield locks into the
     * border it is constructed with. It also prefers its parents font over its
     * font. And if the renderer is not null and no font has been specified the
     * preferred height is that of the renderer.
     */
    public class DefaultTextField extends JTextField {

        /**
         * Border to use.
         */
        protected Border border;

        /**
         * Constructs a DefaultTreeCellEditor$DefaultTextField object.
         *
         * @param border a Border object
         */
        public DefaultTextField(Border border) {
            this.border = border;
        }

        /**
         * Overrides <code>JComponent.getBorder</code> to returns the current
         * border.
         */
        public Border getBorder() {
            return border;
        }

        // implements java.awt.MenuContainer
        public Font getFont() {
            Font font = super.getFont();

            // Prefer the parent containers font if our font is a
            // FontUIResource
            if (font instanceof FontUIResource) {
                Container parent = getParent();

                if (parent != null && parent.getFont() != null) {
                    font = parent.getFont();
                }
            }
            return font;
        }

        /**
         * Overrides <code>JTextField.getPreferredSize</code> to return the
         * preferred size based on current font, if set, or else use renderer's
         * font.
         */
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();

            // If not font has been set, prefer the renderers height.
            if (renderer != null
                    && TreeTableEditorAdapter.this.getFont() == null) {
                Dimension rSize = renderer.getPreferredSize();

                size.height = rSize.height;
            }
            return size;
        }
    }

    public int getColumnWidth(int index) {
        return getColumnWidth(index, 1);
    }

    public int getColumnWidth(int index, int span) {
        TreeTableColumnModel tcm = treeTable.getColumnModel();
        int columnCount = tcm.getColumnCount();

        int width = 0;

        columnCount = (columnCount < index + span) ? columnCount : index + span;

        for (; index < columnCount; index++) {
            width += tcm.getColumn(index).getWidth();
        }

        return width;
    }

    public int getColumnEnd(int index) {
        return getColumnStart(index) + getColumnWidth(index) - treeTable.getColumnModel().getColumnMargin();
    }

    public int getColumnEnd(int index, int span) {
        return getColumnStart(index) + getColumnWidth(index, span) - treeTable.getColumnModel().getColumnMargin();
    }

    public int getColumnFor(Point p) {
        TreeTableColumnModel tcm = treeTable.getColumnModel();
        TreeTableModel ttm = treeTable.getProxyTreeTableModel();
        Class c;

        int start = 0;
        int end = 0;
        int modelIndex, span;

        int count, cindex;
        count = tcm.getColumnCount();
        for (cindex = 0; cindex < count;) {
            modelIndex = tcm.getColumn(cindex).getModelIndex();
            span = ttm.getColumnSpan(node, modelIndex, tcm);

            end = start + getColumnWidth(cindex, span);

            if (p.x >= start && p.x <= end) {
                return cindex;
            }

            start = end;

            cindex += span;
        }
        return -1;
    }

    /* Returns the X-coordinate of the start of column <code>index</code> in the current coordinate
     * system.
     */
    public int getColumnStart(int index) {
        TreeTableColumnModel tcm = treeTable.getColumnModel();
        TreeTableModel ttm = treeTable.getProxyTreeTableModel();
        Class c;

        int start = 0;

        int count, cindex;
        count = tcm.getColumnCount();
        for (cindex = 0; cindex < count && index != cindex; cindex++) {
            start += tcm.getColumn(cindex).getWidth();
        }

        Point p = getLocation();

        start -= p.getX();

        return start;
    }

    public int getEditingColumn() {
        return editingColumn;
    }

    public void setEditingColumn(int editingColumn) {
        this.editingColumn = editingColumn;
    }
}
