/*
 * StringTreeTableCellRenderer.java
 *
 * Created on February 17, 2002, 9:50 PM
 */

package treeTable;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import java.awt.geom.*;
/**
 *
 * @author  twalker
 * @version
 */
public class IconTreeTableCellRenderer extends JLabel implements TreeTableCellRenderer {
    
    protected static IconTreeTableCellRenderer defaultRenderer;
    /** Is the value currently selected. */
    protected boolean selected;
    /** True if has focus. */
    protected boolean hasFocus;
    
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
    
    protected TreeTable treeTable;
    
    /** Holds value of property rendererFont. */
    private Font rendererFont;
    
    /**
     * Returns a new instance of DefaultTreeCellRenderer.  Alignment is
     * set to left aligned. Icons and text color are determined from the
     * UIManager.
     */
    public IconTreeTableCellRenderer() {
        setHorizontalAlignment(JLabel.LEFT);
        
        setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
        setTextNonSelectionColor(UIManager.getColor("Tree.textForeground"));
        setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
        setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
        setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
        
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
     * Sets an initial <code>value</code> for the editor.  This will cause
     * the editor to <code>stopEditing</code> and lose any partially
     * edited value if the editor is editing when this method is called. <p>
     *
     * Returns the component that should be added to the client's
     * <code>Component</code> hierarchy.  Once installed in the client's
     * hierarchy this component will then be able to draw and receive
     * user input.
     *
     * @param	table		the <code>JTable</code> that is asking the
     * 				editor to edit; can be <code>null</code>
     * @param	value		the value of the cell to be edited; it is
     * 				up to the specific editor to interpret
     * 				and draw the value.  For example, if value is
     * 				the string "true", it could be rendered as a
     * 				string or it could be rendered as a check
     * 				box that is checked.  <code>null</code>
     * 				is a valid value
     * @param	isSelected	true if the cell is to be rendered with
     * 				highlighting
     * @param	row     	the row of the cell being edited
     * @param	column  	the column of the cell being edited
     * @return	the component for editing
     */
    public Component getTreeTableCellRendererComponent(TreeTable treeTable, Object node, boolean isSelected, boolean expanded, boolean leaf, int row, int column, boolean hasFocus) {
        //String         stringValue = treeTable.convertValueToText(node, isSelected, expanded, leaf, row, hasFocus);
        this.treeTable = treeTable;
        
        Object valueAt = treeTable.getProxyTreeTableModel().getValueAt(node, column);
        if ( valueAt instanceof Icon ) {
            setIcon((Icon)valueAt);
        }
        else {
            setIcon(null);
        }
        
        this.hasFocus = hasFocus;
        
        
        if(isSelected)
            setForeground(getTextSelectionColor());
        else
            //setForeground(getTextNonSelectionColor());
            setForeground( treeTable.getForeground() );
        
        setComponentOrientation(treeTable.getComponentOrientation());
        
        selected = isSelected;
        
        if ( rendererFont != null ) {
            setFont(rendererFont);
        }
        else {
            setFont(treeTable.getFont());
        }
        
        return this;
        
    }
    
    public void paint(Graphics g) {
        Color bColor;
        
        if(selected) {
            bColor = getBackgroundSelectionColor();
        } else {
           // bColor = getBackgroundNonSelectionColor();
            bColor = treeTable.getBackground();
            if(bColor == null)
                bColor = getBackground();
        }
        
        FontMetrics fm = g.getFontMetrics();
        
        int textWidth = 0;
        if ( getText() != null ) {
            Rectangle2D r = fm.getStringBounds(getText(), g);
            textWidth = (int)r.getWidth();
        }
        textWidth = Math.min(textWidth+2, getWidth() -1);
        
        if(bColor != null) {
            g.setColor(bColor);
            //g.fillRect(0, 0, getWidth() - 1, getHeight());
            g.fillRect(0, 0, textWidth, getHeight());
        }
        
        if (hasFocus) {
            Color       bsColor = getBorderSelectionColor();
            
            if (bsColor != null) {
                g.setColor(bsColor);
                //g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g.drawRect(0, 0, textWidth, getHeight() - 1);
            }
        }
        
        super.paint(g);
        
    }
    
    /**
     * Subclassed to map <code>FontUIResource</code>s to null. If
     * <code>font</code> is null, or a <code>FontUIResource</code>, this
     * has the effect of letting the font of the JTree show
     * through. On the other hand, if <code>font</code> is non-null, and not
     * a <code>FontUIResource</code>, the font becomes <code>font</code>.
     */
  /*  public void setFont(Font font) {
        if(font instanceof FontUIResource)
            font = null;
        super.setFont(font);
    } */
    
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
    
    /** Getter for property rendererFont.
     * @return Value of property rendererFont.
     */
    public Font getRendererFont() {
        return rendererFont;
    }
    
    /** Setter for property rendererFont.
     * @param rendererFont New value of property rendererFont.
     */
    public void setRendererFont(Font rendererFont) {
        this.rendererFont = rendererFont;
    }
    
    public static IconTreeTableCellRenderer getDefaultRenderer() {
        if ( defaultRenderer == null ) defaultRenderer = new IconTreeTableCellRenderer();
        
        return defaultRenderer;
    }
    
}
