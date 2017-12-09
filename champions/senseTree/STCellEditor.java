/*
 * AbilityTreeTableCellEditor.java
 *
 * Created on April 6, 2004, 12:24 AM
 */

package champions.senseTree;

import champions.Ability;
import tjava.ContextMenu;
import tjava.ContextMenuListener;
import tjava.HTMLColor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CellEditorListener;
import treeTable.TreeTable;
import treeTable.TreeTableCellEditor;
import treeTable.TreeTableCellRenderer;


/**
 *
 * @author  1425
 */
public class STCellEditor extends JButton implements TreeTableCellEditor, TreeTableCellRenderer {
    
    protected STNode editorNode;
    protected STNode rendererNode;
    
    static private Color defaultPressedColor = new Color(85,26,139);
    static private Color defaultEnabledColor = Color.BLACK; //new Color(0,0,238);
    /** Holds value of property pressedColor. */
    private Color pressedColor = null;
    /** Holds value of property shadowed. */
    private boolean shadowed = true;
    /** Holds value of property enabledColor. */
    private Color enabledColor = null;
    /** Holds the default font. */
    private Font defaultFont;
    
    /** Holds the tree table the editor current is in. */
    private TreeTable editorTree;

    /** Creates a new instance of AbilityTreeTableCellEditor */
    public STCellEditor() {
        setBorder( new EmptyBorder(0,0,0,0) );
        setBorderPainted( false );
        setContentAreaFilled( false ); 
        
        Font font = UIManager.getFont("AbilityTree.defaultFont");
        if ( font != null ) {
            setFont(font);
        }
        
        defaultFont = getFont();
        
        this.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                STNode oldNode = editorNode;
                if ( editorTree != null ) {
                    if ( ! editorTree.stopEditing() ) {
                        editorTree.cancelEditing();
                    }
                }
                if ( oldNode != null ) oldNode.triggerDefaultAction();
            }
        });
        
        ContextMenu.addContextMenu(this);
    }
    
    /**
     *  Sets an initial <code>value</code> for the editor.  This will cause
     *  the editor to <code>stopEditing</code> and lose any partially
     *  edited value if the editor is editing when this method is called. <p>
     *
     *  Returns the component that should be added to the client's
     *  <code>Component</code> hierarchy.  Once installed in the client's
     *  hierarchy this component will then be able to draw and receive
     *  user input.
     *
     * @param	table		the <code>JTable</code> that is asking the
     *				editor to edit{ } can be <code>null</code>
     * @param	value		the value of the cell to be edited{ } it is
     *				up to the specific editor to interpret
     *				and draw the value.  For example, if value is
     *				the string "true", it could be rendered as a
     *				string or it could be rendered as a check
     *				box that is checked.  <code>null</code>
     *				is a valid value
     * @param	isSelected	true if the cell is to be rendered with
     *				highlighting
     * @param	row     	the row of the cell being edited
     * @param	column  	the column of the cell being edited
     * @return	the component for editing
     */
    public Component getTreeTableCellEditorComponent(TreeTable treeTable, Object node, boolean isSelected, boolean expanded, boolean leaf, int row, int column){ 
        editorTree = treeTable;
        
        Font font = null;
        Color color = null;
        if ( node instanceof STNode ) {
            this.editorNode = (STNode)node;
            font = this.editorNode.getColumnFont(column);
            color = this.editorNode.getColumnColor(column);
            Object value = this.editorNode.getValueAt(column);
            if ( value == null ) {
                setText("");
            }
            else {
                setText(value.toString());
            }
            
            if ( column == STModel.NAME_COLUMN && node instanceof STNode ) {
                setEnabled( ((STNode)node).isEnabled());
            }
            else { 
                setEnabled(true);
            }
        }
        else {
            setText(node.toString());
        }
        
        if ( font != null ) {
            setFont(font);
        }
        else {
            setFont(defaultFont);
        }
        
        if ( color != null ) {
            enabledColor = color;
        }
        else {
            enabledColor = defaultEnabledColor;
        }
        
        return this;
    }
    
        /**
     *  Sets an initial <code>value</code> for the editor.  This will cause
     *  the editor to <code>stopEditing</code> and lose any partially
     *  edited value if the editor is editing when this method is called. <p>
     *
     *  Returns the component that should be added to the client's
     *  <code>Component</code> hierarchy.  Once installed in the client's
     *  hierarchy this component will then be able to draw and receive
     *  user input.
     *
     * @param	table		the <code>JTable</code> that is asking the
     *				editor to edit; can be <code>null</code>
     * @param	value		the value of the cell to be edited; it is
     *				up to the specific editor to interpret
     *				and draw the value.  For example, if value is
     *				the string "true", it could be rendered as a
     *				string or it could be rendered as a check
     *				box that is checked.  <code>null</code>
     *				is a valid value
     * @param	isSelected	true if the cell is to be rendered with
     *				highlighting
     * @param	row     	the row of the cell being edited
     * @param	column  	the column of the cell being edited
     * @return	the component for editing
     */
    public Component getTreeTableCellRendererComponent(TreeTable treeTable, Object node, boolean isSelected, boolean expanded, boolean leaf, int row, int column, boolean hasFocus) {
        Font font = null;
        Color color = null;
        if ( node instanceof STNode ) {
            this.rendererNode = (STNode)node;
            font = this.rendererNode.getColumnFont(column);
            color = ((STNode)node).getColumnColor(column);
            Object value = this.rendererNode.getValueAt(column);
            
            if ( column == STModel.NAME_COLUMN && node instanceof STNode ) {
                setEnabled( ((STNode)node).isEnabled());
            }
            else { 
                setEnabled(true);
            }
            
            if ( value == null ) {
                setText("");
            }
            else {
                setText(value.toString());
            }
        }
        else {
            setText(node.toString());
        }
        
        if ( font != null ) {
            setFont(font);
        }
        else {
            setFont(defaultFont);
        }
        
        if ( color != null ) {
            enabledColor = color;
        }
        else {
            enabledColor = defaultEnabledColor;
        }
        
        return this;                                          
    }
    
    /** 
     * Returns whether the editor should start editing immediate.  
     * 
     * By default, the editor waits for 2 clicks or for the timer to time out
     * to start editing.  This can override that behaviour.
     */
    public boolean canEditImmediately(EventObject event, TreeTable treeTable, Object node, int row, int column, int offset){
        return true;
    }
    
    /** 
     * Informs the editor that it's current selection status may have changed to <code>isSelected</code>.
     */
    public void selectionStateChanged(boolean isSelected){ 
    
    }
    
    public void addCellEditorListener(CellEditorListener l) {
    }    
    
    public void cancelCellEditing() {
    }    
    
    public Object getCellEditorValue() {
        return null;
    }    
    
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }
    
    public void removeCellEditorListener(CellEditorListener l) {
    }
    
    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }
    
    public boolean stopCellEditing() {
        editorTree = null;
        editorNode = null;
        return true;
    }
    
    public void cancelEditing() {
        editorTree = null;
        editorNode = null;
    }
    
    protected void paintComponent(Graphics g) {

        g.setFont( getFont() );
        
        FontMetrics fm = g.getFontMetrics();
        Insets insets = getInsets();
        int x = insets.left;
        int y = insets.top;
        int width = getWidth();
        int height = getHeight();

        int baseLine;
        
// Bounds box for testing... 
//        g.setColor(Color.RED);
//        g.drawRect(x, y, width - insets.left - insets.right, height - insets.top - insets.bottom);
       

        if ( getVerticalAlignment() == SwingConstants.CENTER ) {
            baseLine = y + ( height - insets.top - insets.bottom - fm.getHeight() ) / 2 + fm.getAscent();
        }
        else if ( getVerticalAlignment() == SwingConstants.TOP ) {
            baseLine = y + fm.getAscent();
        }
        else  {
            baseLine = y + fm.getDescent() + insets.bottom;
        }

        String text = getText();

        
        
        if ( this.isOpaque() ) {
            g.setColor(getBackground());
            g.fillRect(x, y, width - insets.left - insets.right, height - insets.top - insets.bottom);
        }

        if ( text != null ) {
            /* Draw the Text */
            if( model.isEnabled() ) {
                /*** paint the text normally */
                if ( model.isPressed() ) {
                    g.setColor( getPressedColor() );
                }
                else {
                    g.setColor( getEnabledColor());
                }
                g.drawString(text,x ,baseLine );
                
                if ( model.isPressed() ) {
                    g.drawLine( x, baseLine+ 1,
                    x + (int)fm.getStringBounds(getText(),g).getWidth() - 1, baseLine + 1);
                }

            }
            else {
                if ( isShadowed() ) {
                    /*** paint the text disabled ***/
                    g.setColor(getBackground().brighter());
                    g.drawString(text,x + 1, baseLine + 1);
                    g.setColor(getBackground().darker());
                    g.drawString(text,x, baseLine);
                }
                else {
                    g.setColor( getForeground() );
                    g.drawString(text,x, baseLine);
                }
            }
        }
    }

    /** Getter for property pressedColor.
     * @return Value of property pressedColor.
     */
    public Color getPressedColor() {
        Action action = getAction();
        if ( action instanceof HTMLColor ) {
            Color c = ((HTMLColor)action).getPressedColor();
            if ( c != null ) return c;
        }
        if ( pressedColor == null ) return defaultPressedColor;
        return pressedColor;
    }
    /** Setter for property pressedColor.
     * @param pressedColor New value of property pressedColor.
     */
    public void setPressedColor(Color pressedColor) {
        this.pressedColor = pressedColor;
    }

    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        FontMetrics fm = getFontMetrics( getFont() );
        d.height = fm.getHeight();
        return d;
    }

    public Dimension getMinimumSize() {
        Dimension d = super.getMinimumSize();
        FontMetrics fm = getFontMetrics( getFont() );
        d.height = fm.getHeight();
        return d;
    }
    /** Getter for property shadowed.
     * @return Value of property shadowed.
     */
    public boolean isShadowed() {
        return shadowed;
    }
    /** Setter for property shadowed.
     * @param shadowed New value of property shadowed.
     */
    public void setShadowed(boolean shadowed) {
        this.shadowed = shadowed;
    }
    /** Getter for property enabledColor.
     * @return Value of property enabledColor.
     */
    public Color getEnabledColor() {
        Action action = getAction();
        if ( action instanceof HTMLColor ) {
            Color c = ((HTMLColor)action).getEnabledColor();
            if ( c != null ) return c;
        }
        if ( enabledColor == null ) return defaultEnabledColor;
        return enabledColor;
    }
    /** Setter for property enabledColor.
     * @param enabledColor New value of property enabledColor.
     */
    public void setEnabledColor(Color enabledColor) {
         this.enabledColor = enabledColor;
    }

 /**   public boolean invokeMenu(JPopupMenu popup,Component inComponent,Point inPoint) {
        Action action = getAction();
        if ( action != null && action instanceof ContextMenuListener ) {
            return ((ContextMenuListener)action).invokeMenu(popup, inComponent, inPoint);
        }
        else {
            return false;
        }
    } */
    
}
