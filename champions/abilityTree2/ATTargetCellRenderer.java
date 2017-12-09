/*
 * AbilityTreeTableCellEditor.java
 *
 * Created on April 6, 2004, 12:24 AM
 */

package champions.abilityTree2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.ref.WeakReference;
import java.util.EventObject;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreePath;
import treeTable.TreeTable;
import treeTable.TreeTableCellEditor;
import treeTable.TreeTableCellRenderer;
import treeTable.TreeTableModel;


/**
 *
 * @author  1425
 */
public class ATTargetCellRenderer extends JPanel implements TreeTableCellRenderer, TreeTableCellEditor {
    
    private WeakReference<ATTargetNode> rendererNode;
    
    static private Color defaultPressedColor = new Color(85,26,139);
    static private Color defaultEnabledColor = Color.BLUE; //new Color(0,0,238);
    /** Holds value of property pressedColor. */
    private Color pressedColor = null;
    /** Holds value of property enabledColor. */
    private Color enabledColor = null;
    /** Holds the default font. */
    private Font defaultFont;
    
    /** Holds the tree table the editor current is in. */
    private TreeTable editorTree;
    
    private String text;
   // private Rectangle textBounds;

    private Rectangle editButtonBounds;

    private boolean pressed;
    
    private int verticalAlignment = SwingConstants.CENTER;
    
    // Override component enabled so we can handle it a bit faster...
    private boolean enabled;

    /** Creates a new instance of AbilityTreeTableCellEditor */
    public ATTargetCellRenderer() {
        setBorder( new EmptyBorder(0,0,0,0) );
        //setBorderPainted( false );
        //setContentAreaFilled( false ); 
        
        Font font = UIManager.getFont("AbilityTree.defaultFont");
        if ( font != null ) {
            setFont(font);
        }
        
        setOpaque(false);
        
        defaultFont = getFont();
        
        addMouseListener( getMouseListener() );
        
        setLayout(null);
        
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height); //To change body of generated methods, choose Tools | Templates.
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
        return getTreeTableCellRendererComponent(treeTable, node, isSelected, expanded, leaf, row, column, false);
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
    @Override
    public Component getTreeTableCellRendererComponent(TreeTable treeTable, Object node, boolean isSelected, boolean expanded, boolean leaf, int row, int column, boolean hasFocus) {
        
        editorTree = treeTable;
        Font font = null;
        Color enableColor = null;
        TreeTableModel proxyModel = treeTable.getProxyTreeTableModel();
        TreeTableModel baseModel = treeTable.getBaseTreeTableModel();
        if ( node instanceof ATTargetNode && baseModel instanceof ATModel ) {
            ATModel atModel = (ATModel)baseModel;
            ATTargetNode rn = (ATTargetNode)node;
            this.setRendererNode(rn);
            
            font = atModel.getFont(node, column);
            //color = model.getColor(node, column);
            enableColor = defaultEnabledColor;
            Object value = proxyModel.getValueAt(node, column);

            setEnabled( rn.isEditEnabled() );
            
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
        
        if ( enableColor != null ) {
            this.enabledColor = enableColor;
        }
        else {
            this.enabledColor = defaultEnabledColor;
        }

        pressedColor = defaultPressedColor;
        
        setPressed(false);
        
        return this;                                          
    }
    
    
    
    /** 
     * Returns whether the editor should start editing immediate.  
     * 
     * By default, the editor waits for 2 clicks or for the timer to time out
     * to start editing.  This can override that behaviour.
     */
    public boolean canEditImmediately(EventObject event, TreeTable treeTable, Object node, int row, int column, int offset){
        if ( node instanceof ATTargetNode && ((ATTargetNode)node).isEditEnabled() ) {
            return true;
        }

        return false;
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
        // Note:  This is called prior to the get component, so you can't actually
        return true;
    }
    
    public void removeCellEditorListener(CellEditorListener l) {
    }
    
    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isEnabled() {
        return enabled;
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
       int ascent = fm.getAscent();
       int descent = fm.getDescent();

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

        int startX = x;
        int startY = baseLine-ascent;
        int boundsHeight = ascent+descent;

        editButtonBounds = null;

        if ( text != null ) {
            int stringWidth = fm.stringWidth(text);

            g.setColor( getForeground() );
            g.drawString(text,x ,baseLine );
            
            x+= stringWidth;
            
            if ( getRendererNode() != null && getRendererNode().isEditEnabled() ) {
                stringWidth = fm.stringWidth(" (");

                g.setColor( getForeground() );
                g.drawString(" (",x ,baseLine );
                x+= stringWidth;

                if ( pressed ) {
                    g.setColor(pressedColor);
                }
                else {
                    g.setColor(enabledColor);
                }
                
                stringWidth = fm.stringWidth("Edit");
                g.drawString("Edit",x ,baseLine );
                editButtonBounds = new Rectangle(x, startY, stringWidth, height);
                
                if ( pressed ) {
                    g.drawLine(x,baseLine+1,x+stringWidth,baseLine+1);
                }

                x+= stringWidth;

                g.setColor( getForeground() );
                g.drawString(")", x, baseLine);
            }
        }
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


 /**   public boolean invokeMenu(JPopupMenu popup,Component inComponent,Point inPoint) {
        Action action = getAction();
        if ( action != null && action instanceof ContextMenuListener ) {
            return ((ContextMenuListener)action).invokeMenu(popup, inComponent, inPoint);
        }
        else {
            return false;
        }
    } */

    protected void setText(String text) {
        this.text = text;
    }

    public int getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(int verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    protected String getText() {
        return text;
    }

    public ATTargetNode getRendererNode() {
        if ( rendererNode == null ) return null;
        return rendererNode.get();
    }

    public void setRendererNode(ATTargetNode rendererNode) {
        if ( rendererNode == null ) this.rendererNode = null;
        this.rendererNode = new WeakReference<ATTargetNode>(rendererNode);
    }

    public boolean stopCellEditing() {
        return true;
    }

    private MouseListener getMouseListener() {
        return new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if ( e.getClickCount() == 2) {
                    if ( !enabled || editButtonBounds == null || editButtonBounds.contains(e.getPoint()) == false ) {
                        TreePath p = new TreePath( getRendererNode().getPath() );
                        if ( editorTree.isExpanded(p) ) {
                            editorTree.collapsePath(p);
                        }
                        else {
                            editorTree.expandPath(p);
                        }
                    }
                }
            }
            public void mouseEntered(MouseEvent e) {
                
            }
            public void mouseExited(MouseEvent e) {
                //setPressed(false);
            }
            public void mousePressed(MouseEvent e) {
                if ( enabled && editButtonBounds != null && editButtonBounds.contains(e.getPoint()) ) {
                    setPressed(true);
                }
            }
            public void mouseReleased(MouseEvent e) {
                if ( enabled && pressed && editButtonBounds != null && editButtonBounds.contains(e.getPoint()) ) {
                    fireActionEvent(e);
                }
                setPressed(false);
            }
        };
    }
    
    protected void fireActionEvent(MouseEvent e) {
        if ( rendererNode.get() != null ) {
            ATNode oldNode = rendererNode.get();
            
            Point p = e.getPoint();
            Component c = e.getComponent();
            while( c != null && c != editorTree ) {
                p.translate( c.getLocation().x, c.getLocation().y );
                c = c.getParent();
            }
            
            e = new MouseEvent(c, e.getID(), e.getWhen(), e.getModifiers(), p.x, p.y, e.getClickCount(), e.isPopupTrigger());
            
            if ( editorTree != null ) {
                if ( ! editorTree.stopEditing() ) {
                    editorTree.cancelEditing();
                }
            }
            if ( oldNode != null ) oldNode.triggerDefaultAction(e);
        }
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        if ( this.pressed != pressed ) {
            this.pressed = pressed;
            
            repaint();
        }
    }
    
}
