/*
 * TreeTableRendererAdapter.java
 *
 * Created on February 17, 2002, 3:35 PM
 */

package treeTable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;


/**
 *
 * @author  twalker
 * @version
 */
public class TreeTableRendererAdapter extends JPanel
implements TreeCellRenderer {
    
    /** Is the value currently selected. */
    protected boolean selected;
    /** True if has focus. */
    protected boolean hasFocus;
    /** True if draws focus border around icon as well. */
    private boolean drawsFocusBorderAroundIcon;
    
    private JLabel iconLabel;
    
    // Icons
    /** Icon used to show non-leaf nodes that aren't expanded. */
    transient protected Icon closedIcon;
    
    /** Icon used to show leaf nodes. */
    transient protected Icon leafIcon;
    
    /** Icon used to show non-leaf nodes that are expanded. */
    transient protected Icon openIcon;
    
    // Colors
    /** Color to use for the foreground for selected nodes. */
    protected Color textSelectionColor;
    
    /** Color to use for the foreground for non-selected nodes. */
    protected Color textNonSelectionColor;
    
    /** Color to use for the background when a node is selected. */
    protected Color backgroundSelectionColor;
    
    /** Color to use for the background when the node isn't selected. */
    protected Color backgroundNonSelectionColor;
    
    /** Color to use for the background when the node isn't selected. */
    protected Color borderSelectionColor;
    
    protected CellRendererPane rendererPane;
    
    /** Holds value of property treeTable. */
    private TreeTable treeTable;
    
    /** Holds value of property node. */
    private Object node;
    
    /** Holds value of property leaf. */
    private boolean leaf;
    
    /** Holds value of property row. */
    private int row;
    
    /** Depth of the node in the tree. */
    private int depth;
    
    /** Holds value of property expanded. */
    private boolean expanded;
    
    static private final int DEBUG = 0;
    
    /** Holds value of property icon. */
    //  private Icon icon;
    
    /**
     * Returns a new instance of DefaultTreeCellRenderer.  Alignment is
     * set to left aligned. Icons and text color are determined from the
     * UIManager.
     */
    public TreeTableRendererAdapter() {
        
        // setTreeTable(treeTable);
        
        setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
        setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
        setOpenIcon(UIManager.getIcon("Tree.openIcon"));
        
        setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
        setTextNonSelectionColor(UIManager.getColor("Tree.textForeground"));
        setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
        //setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
        setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
        Object value = UIManager.get("Tree.drawsFocusBorderAroundIcon");
        drawsFocusBorderAroundIcon = (value != null && ((Boolean)value).booleanValue());
        
        iconLabel = new JLabel();
        
        rendererPane = new CellRendererPane();
        this.add(rendererPane);
        this.setLayout(null);
        
    }
    
    
    /**
     * Returns the default icon, for the current laf, that is used to
     * represent non-leaf nodes that are expanded.
     */
    public Icon getDefaultOpenIcon() {
        return UIManager.getIcon("Tree.openIcon");
    }
    
    /**
     * Returns the default icon, for the current laf, that is used to
     * represent non-leaf nodes that are not expanded.
     */
    public Icon getDefaultClosedIcon() {
        return UIManager.getIcon("Tree.closedIcon");
    }
    
    /**
     * Returns the default icon, for the current laf, that is used to
     * represent leaf nodes.
     */
    public Icon getDefaultLeafIcon() {
        return UIManager.getIcon("Tree.leafIcon");
    }
    
    /**
     * Sets the icon used to represent non-leaf nodes that are expanded.
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
    
    /**
     * Sets the color the text is drawn with when the node is selected.
     */
    public void setTextSelectionColor(Color newColor) {
        textSelectionColor = newColor;
    }
    
    /**
     * Returns the color the text is drawn with when the node is selected.
     */
    public Color getTextSelectionColor() {
        return textSelectionColor;
    }
    
    /**
     * Sets the color the text is drawn with when the node isn't selected.
     */
    public void setTextNonSelectionColor(Color newColor) {
        textNonSelectionColor = newColor;
    }
    
    /**
     * Returns the color the text is drawn with when the node isn't selected.
     */
    public Color getTextNonSelectionColor() {
        return textNonSelectionColor;
    }
    
    /**
     * Sets the color to use for the background if node is selected.
     */
    public void setBackgroundSelectionColor(Color newColor) {
        backgroundSelectionColor = newColor;
    }
    
    
    /**
     * Returns the color to use for the background if node is selected.
     */
    public Color getBackgroundSelectionColor() {
        return backgroundSelectionColor;
    }
    
    /**
     * Sets the background color to be used for non selected nodes.
     */
    public void setBackgroundNonSelectionColor(Color newColor) {
        backgroundNonSelectionColor = newColor;
    }
    
    /**
     * Returns the background color to be used for non selected nodes.
     */
    public Color getBackgroundNonSelectionColor() {
        return backgroundNonSelectionColor;
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
     * Subclassed to map <code>FontUIResource</code>s to null. If
     * <code>font</code> is null, or a <code>FontUIResource</code>, this
     * has the effect of letting the font of the JTree show
     * through. On the other hand, if <code>font</code> is non-null, and not
     * a <code>FontUIResource</code>, the font becomes <code>font</code>.
     */
    public void setFont(Font font) {
        if(font instanceof FontUIResource)
            font = null;
        super.setFont(font);
    }
    
    /**
     * Subclassed to map <code>ColorUIResource</code>s to null. If
     * <code>color</code> is null, or a <code>ColorUIResource</code>, this
     * has the effect of letting the background color of the JTree show
     * through. On the other hand, if <code>color</code> is non-null, and not
     * a <code>ColorUIResource</code>, the background becomes
     * <code>color</code>.
     */
    public void setBackground(Color color) {
        if(color instanceof ColorUIResource)
            color = null;
        super.setBackground(color);
    }
    
    /**
     * Configures the renderer based on the passed in components.
     * The value is set from messaging the tree with
     * <code>convertValueToText</code>, which ultimately invokes
     * <code>toString</code> on <code>value</code>.
     * The foreground color is set based on the selection and the icon
     * is set based on on leaf and expanded.
     */
    public Component getTreeCellRendererComponent(JTree tree, Object node,
    boolean sel,
    boolean expanded,
    boolean leaf, int row,
    boolean hasFocus) {
        setTreeTable( (TreeTable)tree );
        setNode( node );
        
        this.hasFocus = hasFocus;
        this.leaf = leaf;
        this.expanded = expanded;
        this.row = row;
        
        selected = sel;
        
        if ( DEBUG > 0 ) System.out.println("TreeTableRendererAdapter.getTreeCellRenderer called for " + node);
        return this;
    }
    
    /**
     * Returns the number of levels above this node -- the distance from
     * the root to this node.  If this node is the root, returns 0.
     *
     * @see	#getDepth
     * @return	the number of levels above this node
     */
    public int getLevel(TreeNode node) {
	TreeNode ancestor;
	int levels = 0;

	ancestor = node;
	while((ancestor = ancestor.getParent()) != null){
	    levels++;
	}

	return levels;
    }
    
    /**
     * Paints the value.  The background is filled based on selected.
     */
    public void paint(Graphics g) {
        TreeTableModel ttm = treeTable.getProxyTreeTableModel();
        
        Rectangle r = new Rectangle();
        
      //  Point adapterLocation = getLocation();
        Dimension adapterDimension = getSize();
        
        Rectangle componentClip = g.getClipBounds();
        if ( componentClip == null ) componentClip = new Rectangle(0, 0, adapterDimension.width, adapterDimension.height);
        Rectangle columnClip = new Rectangle();
        
        if ( treeTable.isHighlightEnabled() && treeTable.getHighlightRow() == row ) {
            g.setColor( treeTable.getHighlightColor());
            g.fillRect( componentClip.x, componentClip.y, componentClip.width, componentClip.height);
        }
        else if ( getBackgroundNonSelectionColor() != null ) {
            g.setColor( getBackgroundNonSelectionColor() );
            g.fillRect( componentClip.x, componentClip.y, componentClip.width, componentClip.height);
        }
        
        
        TreeTableColumnModel tcm = treeTable.getColumnModel();
        
        int columnStart;
        int columnWidth;
        int columnEnd;
        int realStart;
        int span, modelIndex;
        
        columnStart = getColumnStart(0);
        
        modelIndex = tcm.getColumn(0).getModelIndex();
        
        span = Math.max(1,ttm.getColumnSpan(node, modelIndex, tcm));
        
        columnWidth= getColumnWidth(0, span);
        columnEnd = columnStart + columnWidth - tcm.getColumnMargin();
        
        realStart = (columnStart < 0) ? 0 : columnStart;
        columnClip.setBounds(realStart, 0, columnEnd - realStart, adapterDimension.height);
        g.setClip( columnClip.createIntersection( componentClip ) );
        
        // First Paint the Icon
        int iconOffset = 0;
        
        Icon icon = getIcon();
        
        if ( icon != null ) {
            iconLabel.setIcon(icon);
            
            r.x = 0;
            r.y = 0;
            
            // Center the Icon...
            r.width = icon.getIconWidth();
            r.height = icon.getIconHeight();
            
            r.y += (adapterDimension.height - r.height) / 2; 
            
            //rendererPane.paintComponent(g, iconLabel, this, r);
            rendererPane.paintComponent(g, iconLabel, this, r.x, r.y, r.width, r.height, false);
            
            iconOffset = r.width + 3;
        }
        
        // Draw the Columns
        int count, index;
        
        // Setup Clip for the first column
        realStart = (realStart + iconOffset < 0) ? 0 : realStart + iconOffset;
        columnClip.setBounds(realStart , 0, columnEnd - realStart, adapterDimension.height);
        
        count = tcm.getColumnCount();
        for(index = 0; index < count; ) {
            // Set the Clip for this column
            Rectangle2D compositeClip = columnClip.createIntersection(componentClip);
            if ( componentClip.isEmpty() == false ) {
            g.setClip( compositeClip );
                // Grab the renderer for the node/column
                Component c = getRendererComponentForColumn(index);
                if ( c != null ) {
                    //rendererPane.paintComponent(g, c, this, newClip.x, newClip.y, newClip.width, newClip.height, true);
                    rendererPane.paintComponent(g, c, this, columnClip.x, columnClip.y, columnClip.width, columnClip.height, true);
                }

                if ( DEBUG > 0 ) {
                    g.setColor(Color.magenta);
                    g.drawRect(columnClip.x, columnClip.y, columnClip.width-1, columnClip.height-1);
                    g.setColor(Color.orange);
                    g.drawLine(columnStart, 0, columnStart, adapterDimension.height);
                    g.setColor(Color.red);
                    g.drawLine(columnEnd -1, 0, columnEnd - 1, adapterDimension.height);
                }
            }
            
            index += span;
            // Setup for the next column
            if ( index < count ) {
                modelIndex = tcm.getColumn(index).getModelIndex();
                span = Math.max(1,ttm.getColumnSpan(node, modelIndex, tcm));
                
                columnStart += columnWidth;
                columnWidth = getColumnWidth(index, span);
                columnEnd = columnStart + columnWidth - tcm.getColumnMargin();
                
                realStart = ( columnStart < 0 ) ? 0 : columnStart;
                
                columnClip.setBounds(realStart , 0, columnEnd - realStart, adapterDimension.height);
            }
        }

        g.setClip(componentClip);
        
//        g.setColor(Color.RED);
//        g.drawRect( componentClip.x, componentClip.y, componentClip.width-1, componentClip.height-1);
//        
        
        
    }
    
    private int getLabelStart() {
        /*Icon currentI = getIcon();
        if(currentI != null && getText() != null) {
            return currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
        } */
        return 0;
    }
    
    private int getFirstColumnStart() {
        return -1;
    }
    
    private Icon getIcon() {
        TreeTableModel model = treeTable.getProxyTreeTableModel();
        Icon icon = model.getIcon(treeTable, node, selected, expanded, leaf, row);
        
        if ( icon == null ) {
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
    
    protected int getFirstColumnIndent() {
        return (depth-1) * 20; // 20 is based upon totalChildIndent from BasicTreeUI.  Might not work for all systems.
    }
    
    /**
     * Overrides <code>JComponent.getPreferredSize</code> to
     * return slightly wider preferred size value.
     */
    public Dimension getPreferredSize() {
        if ( DEBUG > 0 ) System.out.println("TreeTableRendererAdapter.getPreferredSize()");
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
        span = Math.max(1,ttm.getColumnSpan(node, modelIndex, tcm));
        
        columnWidth= getColumnWidth(0, span);
        columnEnd = columnStart + columnWidth - tcm.getColumnMargin();
        
        realStart = getFirstColumnIndent();
        
        // First Paint the Icon
        int iconOffset = 0;
        
        Icon icon = getIcon();
        
        if ( icon != null ) {
            // Center the Icon...
            realStart += icon.getIconWidth() + 3;
            int height = icon.getIconHeight();
            width += icon.getIconWidth()+3;
            if ( height > maxHeight ) {
                maxHeight = height;
            }
        }
        
       
        
        // Draw the Columns
        int count, index;
        
        count = tcm.getColumnCount();
        for(index = 0; index < count; ) {
             int realWidth = columnEnd - realStart;
             width += realWidth + tcm.getColumnMargin();
             
            // Attempt to get the column component and get their preferred sizes.
            int compHeight = -1;
                 // We aren't at the last column, so try to restrict the size...
                TreeTableCellRenderer renderer = getRendererForColumn(index);
                if ( renderer instanceof TreeTablePreferredHeightProvider ) {
                    if ( renderer != null ) {
                        compHeight = ((TreeTablePreferredHeightProvider)renderer).getPreferredHeight(realWidth, treeTable, node, false , expanded, leaf, row, modelIndex, false);
                    }
                }
            
            if ( compHeight == -1 ) {
                Component comp = getRendererComponentForColumn(index);
                if ( DEBUG > 0 ) System.out.println("TreeTableRendererAdapter.getPreferredSize component for column " + Integer.toString(index) + " was " + comp);

                if ( comp != null ) {
                    d = comp.getPreferredSize();
                    compHeight = d.height;
                }
            }
                
            if ( compHeight > maxHeight ) maxHeight = compHeight;
            
            
            index += span;
            // Setup for the next column
            if ( index < count ) {
                modelIndex = tcm.getColumn(index).getModelIndex();
                span = Math.max(1,ttm.getColumnSpan(node, modelIndex, tcm));
                
                columnStart += columnWidth;
                columnWidth = getColumnWidth(index, span);
                columnEnd = columnStart + columnWidth - tcm.getColumnMargin();
                
                realStart = ( columnStart < 0 ) ? 0 : columnStart;
            }
        }
            
        
//        TreeTableColumnModel tcm = treeTable.getColumnModel();
//        TreeTableModel ttm = treeTable.getProxyTreeTableModel();
//        Class c;
//        Component comp;
//        
//       // int firstColumnIndex = BasicTreeUI.
//        
//        int width = 0;
//        
//        int count, cindex, height;
//        count = tcm.getColumnCount();
//        for(cindex = 0; cindex < count ; cindex++) {
//            int columnWidth = tcm.getColumn(cindex).getWidth();
//            width += columnWidth;
//            
//            // Attempt to get the column component and get their preferred sizes.
//            TreeTableCellRenderer renderer = getRendererForColumn(cindex);
//            if ( renderer instanceof TreeTablePreferredHeightProvider ) {
//                int modelIndex = treeTable.getColumnModel().getColumn(cindex).getModelIndex();
//                if ( renderer != null ) {
//                    int h = ((TreeTablePreferredHeightProvider)renderer).getPreferredHeight(columnWidth, treeTable, node, false , expanded, leaf, row, modelIndex, false);
//                }
//            }
//            
//            
//            comp = getRendererComponentForColumn(cindex);
//            if ( DEBUG > 0 ) System.out.println("TreeTableRendererAdapter.getPreferredSize component for column " + Integer.toString(cindex) + " was " + comp);
//            if ( comp != null ) {
//                height = (int)comp.getPreferredSize().getHeight();
//                if ( height > maxHeight ) maxHeight = height;
//            }
//        }
//        
//        Icon icon = getIcon();
//        if ( icon == null ) 
//            icon = getIcon();
//        if ( icon != null && icon.getIconHeight() > maxHeight ) {
//            maxHeight = icon.getIconHeight();
//        }
//        
//  //      maxHeight += 2; //Add a small margin between cells
//        
////        if ( maxHeight < 20 ) {
////            if ( DEBUG > 0 ) System.out.println("TreeTableRendererAdapter.getPreferredSize adjusted maxHeight to be = 20 from " + Integer.toString(maxHeight) + ".");
////            maxHeight = 20;
////        }
        
        d.width = width;
        d.height = maxHeight;
        
        if ( DEBUG > 0 ) System.out.println("Preferred Size of RendererAdapter:" + d);
        
        return d;
    }
    
    public int getColumnWidth(int index) {
        return getColumnWidth(index, 1);
    }
    
    public int getColumnWidth(int index, int span) {
        TreeTableColumnModel tcm = treeTable.getColumnModel();
        int columnCount = tcm.getColumnCount();
        
        int width = 0;
        
        columnCount = (columnCount < index + span) ? columnCount : index + span;
        
        for(; index < columnCount; index++) {
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
    
    
    
    /* Returns the X-coordinate of the start of column <code>index</code> in the  coordinate
     * system of the parent tree.
     */
    
    public int getColumnStart(int index) {
        TreeTableColumnModel tcm = treeTable.getColumnModel();
        TreeTableModel ttm = treeTable.getProxyTreeTableModel();
        Class c;
        
        int start = 0;
        
        int count, cindex;
        count = tcm.getColumnCount();
        for(cindex = 0; cindex < count && index != cindex; cindex++) {
            start += tcm.getColumn(cindex).getWidth();
            // start += tcm.getColumnMargin();
        }
        
        Point p = getLocation();
        
        start -= p.getX();
        
        return start;
    }
    
    /** Returns the TreeTable Renderer Component for column <code>column</column>.
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
        
        if ( ttcr == null ) {
            Class columnClass = ttm.getColumnClass(modelIndex);
            
            ttcr = treeTable.getCellRenderer(columnClass);
        }
        
        if ( ttcr == null ) {
            ttcr = treeTable.getDefaultTreeTableCellRenderer();
        }
        
        if ( ttcr != null ) {
            return ttcr.getTreeTableCellRendererComponent(treeTable, node, selected && (modelIndex == primaryColumn) , expanded, leaf, row, modelIndex, hasFocus && (modelIndex == primaryColumn));
        }
        return null;
    }
    
    /** Returns the TreeTable Renderer Component for column <code>column</column>.
     *
     * This method should only be called after getTreeCellRendererComponent has setup the
     * relevant node and row information.
     */
    public TreeTableCellRenderer getRendererForColumn(int columnIndex) {
        int modelIndex = treeTable.getColumnModel().getColumn(columnIndex).getModelIndex();
        TreeTableModel ttm = treeTable.getProxyTreeTableModel();
        int primaryColumn = ttm.getPrimaryColumn();
        // Grab the renderer for the node/column
        TreeTableCellRenderer ttcr = ttm.getCellRenderer(node, modelIndex);
        
        if ( ttcr == null ) {
            Class columnClass = ttm.getColumnClass(modelIndex);
            
            ttcr = treeTable.getCellRenderer(columnClass);
        }
        
        if ( ttcr == null ) {
            ttcr = treeTable.getDefaultTreeTableCellRenderer();
        }
        
        return ttcr;
    }
    
    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    public void validate() {}
    
    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    public void revalidate() {} 
    
    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    public void repaint(long tm, int x, int y, int width, int height) {}
    
    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    public void repaint(Rectangle r) {}
    
    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        // Strings get interned...
        if (propertyName=="text")
            super.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}
    
    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    public void firePropertyChange(String propertyName, char oldValue, char newValue) {}
    
    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    public void firePropertyChange(String propertyName, short oldValue, short newValue) {}
    
    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {}
    
    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    public void firePropertyChange(String propertyName, long oldValue, long newValue) {}
    
    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    public void firePropertyChange(String propertyName, float oldValue, float newValue) {}
    
    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    public void firePropertyChange(String propertyName, double oldValue, double newValue) {}
    
    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
    
    /** Getter for property treeTable.
     * @return Value of property treeTable.
     */
    public TreeTable getTreeTable() {
        return treeTable;
    }
    
    /** Setter for property treeTable.
     * @param treeTable New value of property treeTable.
     */
    public void setTreeTable(TreeTable treeTable) {
        this.treeTable = treeTable;
    }
    
    /** Getter for property node.
     * @return Value of property node.
     */
    public Object getNode() {
        return node;
    }
    
    /** Setter for property node.
     * @param node New value of property node.
     */
    public void setNode(Object node) {
        this.node = node;
        
        if ( node instanceof TreeNode ) {
            depth = getLevel( (TreeNode)node);
        }
        else {
            depth = 0;
        }
            
    }
    
    /** Getter for property leaf.
     * @return Value of property leaf.
     */
    public boolean isLeaf() {
        return leaf;
    }
    
    /** Setter for property leaf.
     * @param leaf New value of property leaf.
     */
    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }
    
    /** Getter for property expanded.
     * @return Value of property expanded.
     */
    public boolean isExpanded() {
        return expanded;
    }
    
    /** Setter for property expanded.
     * @param expanded New value of property expanded.
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
    
    
}
