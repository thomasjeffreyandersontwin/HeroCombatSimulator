/*
 * DefaultParameterEditor.java
 *
 * Created on June 12, 2001, 3:29 PM
 */

package champions.parameterEditor;

import champions.parameters.BooleanParameter;
import champions.parameters.ComboParameter;
import champions.parameters.DiceParameter;
import champions.parameters.DoubleParameter;
import champions.parameters.IntegerParameter;
import champions.parameters.ListParameter;
import champions.parameters.MutableListParameter;
import champions.parameters.Parameter;
import champions.parameters.ParameterList;
import champions.parameters.StringParameter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTableNode;
import treeTable.TreeTable;



/** Creates a DefaultParameterEditor
 *
 * The DefaultParameterEditor provides a TextField with no validation for the editor.
 *
 * This class can be used in two ways, either as a simple editor, using the
 * the rendererDelegate/editorDelegate methods to set the actual render/editor
 * for the value, or by creating a whole new component layout and returning
 * that layout for the renderer and/or editor.
 *
 * To do the first method (just replacing the delegate):<P>
 * In constructor: <CODE>setRendererDelegate</CODE> to a component that can render value;<P>
 * In constructor: <CODE>setEditorDelegate</CODE> to a component that can edit value;<P>
 * Override <CODE>getCellEditorValue</CODE> to return the proper value from the editorDelegate;<P>
 * Override <CODE>highlightEditor</CODE> to highlight the delegate correctly;<P>
 * Override <CODE>setupEditorValue</CODE> to place the proper info into editor delegate;<P>
 * Override <CODE>setupRendererValue</CODE> to place the proper info into renderer delegate;<P>
 *
 *
 * To do the second method (more complex):<P>
 *
 * In constructor: construct the renderer layout. If you use the rendererLabel in
 * the layout, it will be correctly populated with the Description of the Parameter
 * when getXXXcomponent is executed.<P>
 *
 * In constructor: construct the editor layout. If you use the editorLabel in
 * the layout, it will be correctly populated with the Description of the Parameter
 * when getXXXcomponent is executed.<P>
 *
 * In constructor: add focusListener to appropriate components in layout.  focusListener
 * will stop editing when focus is lost, and call <CODE>highlightEditor</CODE> when
 * focus is gained.  In some cases, you may need to create a new focus listener which
 * behaves appropriately for the editor.
 *
 * In constructor: add listeners which call requestEditingStop() whenever an action would
 * terminate editing.  The variable actionListener contains such a listener.<P>
 *
 * Override <CODE>getXXXRendererComponent</CODE>: call super.getXXXRendererComponent to setup default
 * information, then make sure you initialize your component layout, setup colors and
 * selection highlighting, and return the renderer layout.<P>
 *
 * Override <CODE>getXXXEditorComponent</CODE>: call super.getXXXEditorComponent to setup default
 * information, then make sure you initialize your component layout, setup colors and
 * selection highlighting, and return the editor layout.<P>
 *
 * Override <CODE>canEditImmediately</CODE>: Check events to see if they would trigger immediate
 * editing.  The default canEditImmediately checks returns TRUE if the mouse was clicked in the
 * rendererContainer.<P>
 *
 * Override <CODE>getCellEditorValue</CODE> to return the proper value from the editorDelegate;<P>
 *
 * Override <CODE>highlightEditor</CODE> to highlight the delegate correctly;<P>
 *
 * Override <CODE>setupEditorValue</CODE> to place the proper info into editor delegate;<P>
 *
 * Override <CODE>setupRendererValue</CODE> to place the proper info into renderer delegate;<P>
 *
 *
 * @author twalker
 * @version
 */
public class DefaultParameterEditor extends AbstractParameterEditor implements TableCellEditor, TreeCellEditor, TreeCellRenderer, TableCellRenderer {
    
    /** Indicates that the setup of the static variables has taken place.
     */
    static private boolean defaultsSetup = false;
    
    
    /** The default rendererComponent returned by getXXXRendererComponent.
     * The layout and contents of this component can be modified as necessary.
     */
    static private JPanel rendererComponent;
    
    
    /** The default editorComponent returned by getXXXEditorComponent.
     * The layout and contents of this component can be modified as necessary.
     */
    static private JPanel editorComponent;
    
    
    /** The default rendererLabel.
     * This label will be populated with the parameter description.
     */
    static private SelectableLabel rendererLabel;
    
    
    /** The default editor Label.
     * This label will be populated with the parameter description.
     */
    static private SelectableLabel editorLabel;
    
    
    /** The default renderer container for the delegate.
     * If a renderer delegate is used, it is added to this container.  If no delegate
     * is used, additional controls/layout can be performed on this container as
     * necessary.
     */
    static private RestrictedPanel rendererContainer;
    
    
    /** The default editor container for the delegate.
     * If a editor delegate is used, it is added to this container.  If no delegate
     * is used, additional controls/layout can be performed on this container as
     * necessary.
     */
    static private RestrictedPanel editorContainer;
    
    /** Hold the most recently used JTree.
     * If a renderer for a tree was requested, tree will be the requester and
     * table will be null.
     */
    static protected JTree editorTree;
    static protected JTree rendererTree;
    
    /** Hold the most recently used JTable.
     * If a renderer for a table was requested, table will be the requester and
     * tree will be null.
     */
    static protected JTable editorTable;
    static protected JTable rendererTable;
    
    /** Holds value of property node. */
    static protected Object editorNode;
    static protected Object rendererNode;
    
    /** Holds value of property lastRow. */
    static protected int editorLastRow;
    static protected int rendererLastRow;
    
    /** Holds value of property lastColumn. */
    static protected int editorLastColumn;
    static protected int rendererLastColumn;
    
    /** Holds value of property selected. */
    static private boolean editorSelected;
    static private boolean rendererSelected;
    
    /** Holds value of property hasFocus. */
    static private boolean editorHasFocus;
    static private boolean rendererHasFocus;
    
    /** Holds the default editor delegate.
     * The default editor delegate is a JTextField and is the shared delegate for all DefaultParameterEditors
     */
    static protected JComponent defaultEditorDelegate;
    
    
    /** Holds the default renderer delegate.
     * The default renderer delegate is a JLabel and is the shared delegate for all DefaultParameterEditors
     */
    static protected JComponent defaultRendererDelegate;
    
    
    /** Holds the default Focus Listener
     * The default Focus Listener is a FocusListener which can be used by any of the ParameterEditors.
     * It's default behavior is to call highlightEditor when focus is gained and requestEditingStop
     * when focus is lost.
     */
    static protected DefaultFocusListener defaultFocusListener;
    
    
    /** Holds the default Action Listener
     * The default Action Listener is a ActionListener which can be used by any of the ParameterEditors.
     * It's default behavior is to call  requestEditingStop
     * when an action occurs.
     */
    static protected DefaultActionListener defaultActionListener;
    
    
    /** Holds the current renderer delegate.
     * The Current renderer delegate is the delegate that is currently positioned in the rendererContainer.
     */
    static protected JComponent currentRendererDelegate;
    
    
    
    /** Holds the current editor delegate.
     * The Current editor delegate is the delegate that is currently positioned in the editorContainer.
     * The currentEditorDelegate will be listened to for action event and editing event...
     */
    static protected JComponent currentEditorDelegate;
    
    /** Holds the renderer delegate.
     */
    protected JComponent rendererDelegate;
    /** Holds the editor delegate.
     */
    protected JComponent editorDelegate;
    
    /** Holds value of property startValue. */
    protected Object startValue;
    
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
    
    /** Current Value.
     * 
     * The Current value field is only used if the parameterList and parameter
     * are currently null.  In this case, the editor falls back to the current
     * value field.
     */
    protected Object currentValue;
    
    /** Holds value of property parameterNameVisible. */
    private boolean parameterNameVisible = true;
    
    static protected final int DEBUG = 0;

    public DefaultParameterEditor() {

        setupStatics();

        setupColors();

        setRendererDelegate( defaultRendererDelegate );
        setEditorDelegate( defaultEditorDelegate );
    }
    
    /** Creates new DefaultParameterEditor.
     * @param parameterList ParameterList containing the parameter to be edited.
     * @param parameter Name of parameter to be edited.
     */
    public DefaultParameterEditor(ParameterList parameterList,String parameter) {
        super(parameterList,parameter);
        
        setupStatics();
        
        setupColors();
        
        setRendererDelegate( defaultRendererDelegate );
        setEditorDelegate( defaultEditorDelegate );
    }
    
    public void setupStatics() {
        if ( defaultsSetup == true ) return;
        
        rendererComponent = new JPanel();
        //rendererComponent = new TestPanel();
        // rendererComponent.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        rendererComponent.setLayout(new ParameterEditorLayout(SwingConstants.HORIZONTAL));
        rendererComponent.setDoubleBuffered(false);
        rendererComponent.setOpaque(false);
        
        rendererLabel = new SelectableLabel();
        rendererLabel.setOpaque(false);
        rendererLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        rendererLabel.setDoubleBuffered(false);
        
        rendererContainer = new RestrictedPanel(rendererLabel,0);
        rendererContainer.setBorder( new EmptyBorder(0,3,0,0) );
        rendererContainer.setLayout(new BorderLayout());
        rendererContainer.setDoubleBuffered(false);
        rendererContainer.setOpaque(false);
        
        rendererComponent.add(rendererLabel, BorderLayout.WEST);
        rendererComponent.add(rendererContainer, BorderLayout.CENTER);
        
        
        defaultRendererDelegate = new JLabel();
        defaultRendererDelegate.setOpaque(false);
        defaultRendererDelegate.setBorder( new EtchedBorder(Color.white, new Color(102,102,102)) );
        
        editorComponent = new FocusablePanel();
        //editorComponent.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        editorComponent.setLayout(new ParameterEditorLayout(SwingConstants.HORIZONTAL));
        editorComponent.setOpaque(false);
        
        editorLabel = new SelectableLabel();
        editorLabel.setOpaque(false);
        editorLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        editorContainer = new RestrictedPanel(editorLabel,0);
        editorContainer.setBorder( new EmptyBorder(0,3,0,0) );
        editorContainer.setLayout(new BorderLayout());
        editorContainer.setOpaque(false);
        //editorComponent.add(editorLabel);
        //editorComponent.add(editorContainer);
        editorComponent.add(editorLabel, BorderLayout.WEST);
        editorComponent.add(editorContainer, BorderLayout.CENTER);
        
        defaultEditorDelegate = new JTextField();
        defaultEditorDelegate.setOpaque(false);
        
        defaultFocusListener = new DefaultFocusListener();
        defaultActionListener = new DefaultActionListener();
        
        defaultsSetup = true;
    }
    
    public void setupColors() {
        // Setup Colors
        setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
        setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
        setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
        
        Color bg = UIManager.getColor( "AbilityEditor.background");
        Color fg = UIManager.getColor( "AbilityEditor.foreground");
        
        if ( bg != null ) {
            rendererComponent.setBackground(bg);
            rendererLabel.setBackground(bg);
            rendererContainer.setBackground(bg);
            rendererComponent.setBackground(bg);
            
            
            setBackgroundNonSelectionColor(UIManager.getColor("AbilityEditor.background"));
            
            editorComponent.setBackground(bg);
            editorLabel.setBackground(bg);
            editorContainer.setBackground(bg);
            editorComponent.setBackground(bg);
        }
        
        if ( fg != null ) {
            editorLabel.setForeground(fg);
            
            rendererLabel.setForeground(fg);
            
            setTextNonSelectionColor(UIManager.getColor("AbilityEditor.foreground"));
        }
    }
    
    
    /** Sets the delegate for rendering.
     * Defaults to a JLabel if not specified.
     * @param delegate Delegate to handle custom rendering.
     */
    public void setRendererDelegate(JComponent delegate) {
        if ( this.rendererDelegate != delegate ) {
            this.rendererDelegate = delegate;
        }
    }
    
    /** Sets the delegate for editing.
     * Defaults to a JTextField if not specified.
     * @param delegate Delegate to handle custom editing.
     */
    public void setEditorDelegate(JComponent delegate) {
        if ( this.editorDelegate != delegate ) {
            this.editorDelegate = delegate;
        }
    }
    
    /** Sets the delegate for rendering.
     * Defaults to a JLabel if not specified.
     * @param delegate Delegate to handle custom rendering.
     */
    public void prepareRendererDelegate() {
        
        rendererContainer.removeAll();
        rendererDelegateRemoved();
        
        currentRendererDelegate = rendererDelegate;
        
        if ( currentRendererDelegate != null ) {
            rendererContainer.add(currentRendererDelegate);
        }
        
        rendererDelegateAdded();
    }
    
    /** Sets the delegate for editing.
     * Defaults to a JTextField if not specified.
     * @param delegate Delegate to handle custom editing.
     */
    public void prepareEditorDelegate() {
        if ( DEBUG > 0 ) System.out.println("EditorDelegate Remove: " + currentEditorDelegate);
        editorContainer.removeAll();
        editorDelegateRemoved();
        //clearDelegateFocus(currentEditorDelegate);
        
        currentEditorDelegate = editorDelegate;
        
        if ( currentEditorDelegate != null ) {
            editorContainer.add(editorDelegate);
        }
        
        if ( DEBUG > 0 ) System.out.println("EditorDelegate Added: " + currentEditorDelegate);
        editorDelegateAdded();
    }
    
    /** Clears the Focus if the editorDelegate of any child owns focus.
     */
    protected void clearDelegateFocus(Component c) {
        if ( c != null && c.hasFocus() == true ) {
            if ( DEBUG > 0 ) System.out.println("EditorDelegate still had focus: " + c);
            
            if ( c instanceof Container ) {
                int index = ((Container)c).getComponentCount() - 1;
                for( ; index >= 0; index-- ) {
                    Component child = ((Container)c).getComponent(index);
                    clearDelegateFocus(child);
                }
            }
        }
    }
    
    
    /** Returns the value contained in the editor.
     * Classes should override this method to provide custom handling of edit data.
     * getCellEditorValue() is responsible for validating input to make sure it is
     * the right type and within exceptable ranges.
     * {@link setCurrentValue} takes care of actually checking to see if values are
     * acceptable.
     * @return Value currently represented by editor.  Null if value in
     * editor is incorrect type or out of range.
     */
    public Object getCellEditorValue() {
        Object value = null;
        if ( editorDelegate instanceof JTextField ) {
            value = ((JTextField)editorDelegate).getText();
        }
        return value;
    }
    
    /**
     * Ask the editor if it can start editing using <I>anEvent</I>.
     * <I>anEvent</I> is in the invoking component coordinate system.
     * The editor can not assume the Component returned by
     * getCellEditorComponent() is installed.  This method is intended
     * for the use of client to avoid the cost of setting up and installing
     * the editor component if editing is not possible.
     * If editing can be started this method returns true.
     *
     * @param	anEvent		the event the editor should use to consider
     * 				whether to begin editing or not.
     * @return	true if editing can be started.
     * @see #shouldSelectCell
     */
    public boolean isCellEditable(EventObject anEvent) {
        int index;
        if ( (parameterList != null && parameter != null && parameterList.isParameterEnabled(parameter)) || currentValue != null ) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * The return value of shouldSelectCell() is a boolean indicating whether
     * the editing cell should be selected or not.  Typically, the return
     * value is true, because is most cases the editing cell should be
     * selected.  However, it is useful to return false to keep the selection
     * from changing for some types of edits.  eg. A table that contains
     * a column of check boxes, the user might want to be able to change
     * those checkboxes without altering the selection.  (See Netscape
     * Communicator for just such an example)  Of course, it is up to
     * the client of the editor to use the return value, but it doesn't
     * need to if it doesn't want to.
     *
     * @param	anEvent		the event the editor should use to start
     * 				editing.
     * @return	true if the editor would like the editing cell to be selected
     * @see #isCellEditable
     */
    public boolean shouldSelectCell(EventObject anEvent) {
        //focusEditor();
        if ( ! (anEvent instanceof MouseEvent) ) {
            focusEditor();
        }
        highlightEditor();
        return true;
    }
    
    /**
     * Tell the editor to stop editing and accept any partially edited
     * value as the value of the editor.  The editor returns false if
     * editing was not stopped, useful for editors which validates and
     * can not accept invalid entries.
     *
     * @return	true if editing was stopped
     */
    public boolean stopCellEditing() {
        boolean rv = true;
        Object newValue = getCellEditorValue();
        Object current = getCurrentValue();
        if ( newValue == null ) {
            rv = false;
        } else if ( current == null || !current.equals(newValue) ) {
             
            //Object oldValue = parameterList.getParameterValue(parameter);
            boolean valueOK = firePADValueChanging( current, newValue);
            if ( valueOK ) {
                if ( parameterList != null && parameter != null ) parameterList.setParameterValue(parameter,newValue);
                firePADValueChanged(current, newValue);
                this.currentValue = newValue;
            } else {
                rv = false;
            }
            
        }
        
        if ( rv == true ) {
            // Reset the initial start value if the stop was successful.  This is done
            // because on a close of the window contain the tree, the tree will call
            // both stopEditing and cancelEditing, in that order.  If the start value isn't
            // updated here, the cancel will actually overwrite the correct changes, in this
            // on case.  Since stopEditing is only called when the editing is finished, it
            // doesn't matter that we are losing the original starting value at this point...
            setStartValue(newValue);
            
            fireEditingStopped();
        }
        
        return rv;
    }
    
    /**
     * Tell the editor to cancel editing and not accept any partially
     * edited value.
     */
    public void cancelCellEditing() {
        setCurrentValue(getStartValue());
        fireEditingCanceled();
    }
    
    
    /**
     * Sets an initial <I>value</I> for the editor.  This will cause
     * the editor to stopEditing and lose any partially edited value
     * if the editor is editing when this method is called. <p>
     *
     * Returns the component that should be added to the client's
     * Component hierarchy.  Once installed in the client's hierarchy
     * this component will then be able to draw and receive user input.
     *
     * @return the component for editing
     * @param value the value of the cell to be edited.
     * @param isSelected true is the cell is to be renderer with
     * 				selection highlighting
     * @param expanded true if the node is expanded
     * @param leaf true if the node is a leaf node
     * @param row the row index of the node being edited
     * @param tree Tree which is requesting component.
     */
    public Component getTreeCellEditorComponent(JTree tree,Object value,boolean isSelected,boolean expanded,boolean leaf,int row) {
        setEditorTree(tree);
        setEditorNode(value);
        setEditorLastRow(row);
        setEditorSelected(isSelected);
        setEditorHasFocus(false);
        
        setupEditor(tree.getForeground(), tree.getBackground(), tree.getFont());
        
        return editorComponent;
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
    public Component getTableCellEditorComponent(JTable table,Object value,boolean isSelected,int row,int column) {
        setEditorTable(table);
        setEditorLastRow(row);
        setEditorLastColumn(column);
        setEditorSelected(isSelected);
        setEditorHasFocus(false);
        
        setupEditor(table.getForeground(), table.getBackground(), table.getFont());
        
        return editorComponent;
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
    public Component getTreeTableCellEditorComponent(TreeTable treeTable, Object node, boolean isSelected, boolean expanded, boolean leaf, int row, int column) {
        setEditorTree(treeTable);
        setEditorNode(node);
        setEditorLastRow(row);
        setEditorLastColumn(column);
        setEditorSelected(isSelected);
        setEditorHasFocus(false);


        if ( parameterList == null || parameter == null ) {
            // The parameterList and Parameter are null, so grab the value from the node
            if ( node instanceof DefaultTreeTableNode ) {
                Object v = ((DefaultTreeTableNode)node).getValueAt(column);
                setCurrentValue(v);
            }
        }
        
        if ( DEBUG > 0 ) System.out.println("DefaultParameterEditor.getTreeTableCellEditor for " + node);
        setupEditor(treeTable.getForeground(), treeTable.getBackground(), treeTable.getFont());
        
        return editorComponent;
        
    }
    
    protected void setupEditor(Color fg, Color bg, Font font) {
        editorLabel.setVisible( isParameterNameVisible() );
        
        if ( parameterList != null ) {
            editorLabel.setText(parameterList.getParameterDescription(parameter) + ": ");
            int width = parameterList.getParameterWidth(parameter);
            if ( width <= 0 ) width = getDefaultWidth();
            editorContainer.setMinimumWidth(width);
        }
        
        editorComponent.setForeground( fg );
        editorContainer.setForeground( fg );
        editorLabel.setForeground( fg );
        
        editorComponent.setBackground( bg );
        editorContainer.setBackground( bg );
        editorLabel.setBackground( bg );
        
        editorComponent.setFont(font);
        editorContainer.setFont(font);
        editorLabel.setFont(font);
        
        prepareEditorDelegate();
        if ( editorDelegate != null ) {
            editorDelegate.setForeground( fg );
            editorDelegate.setBackground( bg );
            editorDelegate.setFont(font);
        }
        setupEditorValue(getCurrentValue());
        
        // startEditing();
    }
    
    /**
     * Sets the value of the current tree cell to <code>value</code>.
     * If <code>selected</code> is true, the cell will be drawn as if
     * selected. If <code>expanded</code> is true the node is currently
     * expanded and if <code>leaf</code> is true the node represets a
     * leaf anf if <code>hasFocus</code> is true the node currently has
     * focus. <code>tree</code> is the JTree the receiver is being
     * configured for.
     * Returns the Component that the renderer uses to draw the value.
     *
     * @return Component that the renderer uses to draw the value.
     * @param tree
     * @param value
     * @param selected
     * @param expanded
     * @param leaf
     * @param row
     * @param hasFocus
     */
    public Component getTreeCellRendererComponent(JTree tree,Object value,boolean selected,boolean expanded,boolean leaf,int row,boolean hasFocus) {
        setRendererTree(tree);
        setRendererNode(value);
        setRendererLastRow(row);
        setRendererSelected(selected);
        setRendererHasFocus(hasFocus);
        
        setupRenderer(tree.getForeground(), tree.getBackground(), tree.getFont());
        
        
        return rendererComponent;
    }
    
    /**
     * Returns the component used for drawing the cell.  This method is
     * used to configure the renderer appropriately before drawing.
     *
     * @param table the <code>JTable</code> that is asking the
     * 				renderer to draw; can be <code>null</code>
     * @param value the value of the cell to be rendered.  It is
     * 				up to the specific renderer to interpret
     * 				and draw the value.  For example, if
     * 				<code>value</code>
     * 				is the string "true", it could be rendered as a
     * 				string or it could be rendered as a check
     * 				box that is checked.  <code>null</code> is a
     * 				valid value
     * @param isSelected true if the cell is to be rendered with the
     * 				selection highlighted; otherwise false
     * @param hasFocus if true, render cell appropriately.  For
     * 				example, put a special border on the cell, if
     * 				the cell can be edited, render in the color used
     * 				to indicate editing
     * @param row the row index of the cell being drawn.  When
     * 				drawing the header, the value of
     * 				<code>row</code> is -1
     * @param column the column index of the cell being drawn
     * @return
     */
    public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column) {
        setRendererTable(table);
        setRendererLastRow(row);
        setRendererLastColumn(column);
        setRendererSelected(isSelected);
        setRendererHasFocus(hasFocus);
        
        setupRenderer(table.getForeground(), table.getBackground(), table.getFont());
        
        return rendererComponent;
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
        setRendererTree(treeTable);
        setRendererNode(node);
        setRendererLastRow(row);
        setRendererLastColumn(column);
        setRendererSelected(isSelected);
        setRendererHasFocus(hasFocus);
        
        setupRenderer(treeTable.getForeground(), treeTable.getBackground(), treeTable.getFont());

        if ( parameterList == null || parameter == null ) {
            // The parameterList and Parameter are null, so grab the value from the node
            if ( node instanceof DefaultTreeTableNode ) {
                Object v = ((DefaultTreeTableNode)node).getValueAt(column);
                setCurrentValue(v);
            }
        }
        
        
        if ( DEBUG > 0 ) System.out.println("DefaultParameterEditor.getTreeTableCellRenderer for " + node);
        
        return rendererComponent;
    }
    
    protected void setupRenderer(Color fg, Color bg, Font font) {
        rendererLabel.setVisible( parameter != null && isParameterNameVisible() );
        
        if ( parameterList != null ) {
            rendererLabel.setText(parameterList.getParameterDescription(parameter) + ": ");
            int width = parameterList.getParameterWidth(parameter);
            if ( width <= 0 ) width = getDefaultWidth();
            rendererContainer.setMinimumWidth(width);
        }
        else {
            rendererLabel.setText("");
            rendererContainer.setMinimumWidth(getDefaultWidth());
        }
        
        rendererComponent.setForeground( fg );
        rendererContainer.setForeground( fg );
        rendererLabel.setForeground( fg );
        
        rendererComponent.setBackground( bg );
        rendererContainer.setBackground( bg );
        rendererLabel.setBackground( bg );
        
        rendererComponent.setFont(font);
        rendererContainer.setFont(font);
        rendererLabel.setFont(font);
        
        prepareRendererDelegate();
        
        if ( rendererDelegate != null ) {
            rendererDelegate.setForeground( fg );
            rendererDelegate.setBackground( bg );
            rendererDelegate.setFont(font);
        }

        
        setupRendererValue(getCurrentValue());
    }
    
    
    /** Indicates editing should start immediately.
     * This method should be overriden to check and see if the event would
     * trigger editing to start.  By default, it checks to see if the event
     * is a mouseEvent and occurred inside of the renderer delegate.
     * @param e Event which might trigger edit.
     * @param tree Tree where event occurred.
     * @param row Row in which event occurred.
     * @return True to trigger edit, False to ignore event.
     */
    public boolean canEditImmediately(EventObject e, JTree tree, int row, int offset) {
        if((e instanceof MouseEvent) &&
                SwingUtilities.isLeftMouseButton((MouseEvent)e)) {
            MouseEvent       me = (MouseEvent)e;
            
            Rectangle bounds = tree.getRowBounds(row);
            
            if ( rendererContainer != null ) {
                Dimension a = rendererLabel.getPreferredSize();
                int totalWidth = a.width + offset + 3;
                
                Rectangle c = new Rectangle(bounds.x + totalWidth, bounds.y, bounds.width - totalWidth, bounds.height);
                
                //   Rectangle c = rendererContainer.getBounds();
                //   c.translate( bounds.x + offset, bounds.y );
                //   Rectangle d = editorContainer.getBounds();
                //  d.translate( bounds.x + offset, bounds.y );
                
                
       /*        Graphics g = null;
                try {
                    g = tree.getGraphics();
        
                    g.setColor(Color.red);
                    g.drawRect( c.x, c.y, c.width, c.height);
                 //   g.setColor(Color.yellow);
                 //   g.drawRect( d.x+1, d.y+1, c.width-2, c.height-2);
                 //   g.setColor(Color.blue);
                 //   g.drawRect( bounds.x, bounds.y, bounds.width, bounds.height);
                  //  g.setColor(Color.magenta);
                 //   g.drawRect( b.x, b.y, b.width, b.height);
                }
                finally {
                    if ( g != null ) g.dispose();
                } */
                
                if ( c.contains( me.getPoint() ) ) {
                    //System.out.println("Starting editor for " + parameter);
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Returns whether the editor should start editing immediate.
     *
     * By default, the editor waits for 2 clicks or for the timer to time out
     * to start editing.  This can override that behaviour.
     *
     * This mouse event should be in column local coordinates...
     */
    public boolean canEditImmediately(EventObject e, TreeTable treeTable, Object node, int row, int column, int offset) {
        if((e instanceof MouseEvent) &&
                SwingUtilities.isLeftMouseButton((MouseEvent)e)) {
            MouseEvent       me = (MouseEvent)e;
            
            //  Rectangle bounds = tree.getRowBounds(row);
            
            if ( editorContainer != null ) {
                if ( parameterList != null && parameter != null ) {
                    editorLabel.setText(parameterList.getParameterDescription(parameter) + ": ");
                }
                else {
                    editorLabel.setText("");
                }
                
                Dimension a = editorLabel.getPreferredSize();
                Rectangle b = treeTable.getColumnBounds(row, column);
                
                //   Rectangle c = new Rectangle(0, 0, b.width, b.height);
                
                if ( DEBUG > 0 ) System.out.println("canEditImmediately Real Column Bounds are " + b);
                
                // Adjust for the label
                b.x += a.width + offset + 3; // x + labelWidth + iconOffset + iconGap
                b.width -= a.width - offset - 3;
                
                if ( DEBUG > 0 ) System.out.println("checkin for canEditImmediately - Bounds: " + b + ", Point: " + me.getPoint() + ". Bounds of rednererContainer are " + rendererContainer.getBounds());
                if ( DEBUG > 0 ) System.out.println("canEditImmediately editorLabel text: " + editorLabel.getText());
                //   Rectangle c = rendererContainer.getBounds();
                //   c.translate( bounds.x + offset, bounds.y );
                //   Rectangle d = editorContainer.getBounds();
                //  d.translate( bounds.x + offset, bounds.y );
                
                
                
                if ( DEBUG > 0 &&  treeTable != null ) {
                    Graphics g = null;
                    try {
                        g = treeTable.getGraphics();
                        
                        g.setColor(Color.red);
                        g.drawRect(  b.x, b.y, b.width, b.height );
                        //   g.setColor(Color.yellow);
                        //   g.drawRect( d.x+1, d.y+1, c.width-2, c.height-2);
                        //   g.setColor(Color.blue);
                        //   g.drawRect( bounds.x, bounds.y, bounds.width, bounds.height);
                        //  g.setColor(Color.magenta);
                        //   g.drawRect( b.x, b.y, b.width, b.height);
                    } finally {
                        if ( g != null ) g.dispose();
                    }
                }
                
                if ( b.contains( me.getPoint() ) ) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void setEditorTree(JTree tree) {
        this.editorTable = null;
        this.editorTree = tree;
    }
    
    public void setRendererTree(JTree tree) {
        this.rendererTable = null;
        this.rendererTree = tree;
    }
    
    public void setEditorTable(JTable table) {
        this.editorTable = table;
        this.editorTree = null;
    }
    
    public void setRendererTable(JTable table) {
        this.rendererTable = table;
        this.rendererTree = null;
    }
    
    /** Getter for property node.
     * @return Value of property node.
     */
    public Object getEditorNode() {
        return editorNode;
    }
    
    public Object getRendererNode() {
        return rendererNode;
    }
    
    /** Setter for property node.
     * @param node New value of property node.
     */
    public void setEditorNode(Object node) {
        this.editorNode = node;
    }
    
    /** Setter for property node.
     * @param node New value of property node.
     */
    public void setRendererNode(Object node) {
        this.rendererNode = node;
    }
    
    /** Getter for property lastRow.
     * @return Value of property lastRow.
     */
    public int getEditorLastRow() {
        return editorLastRow;
    }
    
    /** Getter for property lastRow.
     * @return Value of property lastRow.
     */
    public int getRendererLastRow() {
        return rendererLastRow;
    }
    
    /** Setter for property lastRow.
     * @param lastRow New value of property lastRow.
     */
    public void setEditorLastRow(int lastRow) {
        this.editorLastRow = lastRow;
    }
    
    /** Setter for property lastRow.
     * @param lastRow New value of property lastRow.
     */
    public void setRendererLastRow(int lastRow) {
        this.rendererLastRow = lastRow;
    }
    
    public JTree getEditorTree() {
        return editorTree;
    }
    
    public JTree getRendererTree() {
        return rendererTree;
    }
    
    public JTable getEditorTable() {
        return editorTable;
    }
    
    public JTable getRendererTable() {
        return rendererTable;
    }
    
    /** Getter for property lastColumn.
     * @return Value of property lastColumn.
     */
    public int getRendererLastColumn() {
        return rendererLastColumn;
    }
    
    /** Getter for property lastColumn.
     * @return Value of property lastColumn.
     */
    public int getEditorLastColumn() {
        return editorLastColumn;
    }
    
    /** Setter for property lastColumn.
     * @param lastColumn New value of property lastColumn.
     */
    public void setEditorLastColumn(int lastColumn) {
        this.editorLastColumn = lastColumn;
    }
    
    /** Setter for property lastColumn.
     * @param lastColumn New value of property lastColumn.
     */
    public void setRendererLastColumn(int lastColumn) {
        this.rendererLastColumn = lastColumn;
    }
    
    /** Getter for property startValue.
     * @return Value of property startValue.
     */
    public Object getStartValue() {
        return startValue;
    }
    
    /** Setter for property startValue.
     * @param startValue New value of property startValue.
     */
    public void setStartValue(Object startValue) {
        this.startValue = startValue;
    }
    
    /** Getter for property currentValue.
     * @return Value of property currentValue.
     */
    public Object getCurrentValue() {
        if ( parameterList != null && parameter != null ) {
            return parameterList.getParameterValue(parameter);
        }
        else {
            return currentValue;
        }
    }
    
    /** Setter for parameter in the parameter list.
     * Checks to validity of the new valid via calls to PADValueChanging for
     * all PADListeners, then if valid, set the parameter in the parameterList
     * according to the value.
     *
     * @param newValue New Value for the parameter.
     * @return True if value was valid and set, false if it was invalid and not set.
     */
    public boolean setCurrentValue(Object newValue) {
        boolean rv = true;
        
        Object curValue = getCurrentValue();
        if ( curValue == null || !curValue.equals(newValue) ) {
            if ( newValue != null ) {
                boolean valueOK = firePADValueChanging( curValue, newValue);
                if ( valueOK ) {
                    if ( parameterList != null && parameter != null) parameterList.setParameterValue(parameter,newValue);
                    firePADValueChanged(curValue, newValue);
                    setupEditorValue(newValue);
                    setupRendererValue(newValue);
                    this.currentValue = newValue;
                    //  System.out.println("Value set: " + key + " = " + newValue);
                } else {
                    setupEditorValue(curValue);
                    setupRendererValue(curValue);
                    rv = false;
                }
            }
        }
        return rv;
    }
    
    /** Prepares editor for editing.
     * This is called prior to providing the JTree or JTable the editing component.
     */
    public void startEditing() {
        setStartValue(getCurrentValue());
        // focusEditor();
        //  highlightEditor();
    }
    
    /** Highlights the editing components.
     * The method should be override when appropriate to select text or perform other
     * selection preparation as necessary.
     */
    public void highlightEditor() {
        if ( editorDelegate instanceof JTextField ) {
            ((JTextField)editorDelegate).selectAll();
            //    ((JTextField)editorDelegate).requestFocus();
        }
    }
    
    /** Causes the Editor to request focus for the appropriate Component to start editing.
     * The method should be override when appropriate to focus on the primary editing component.
     */
    public void focusEditor() {
        if ( editorDelegate instanceof JComponent ) {
            ((JComponent)editorDelegate).grabFocus();
            //    ((JTextField)editorDelegate).requestFocus();
        }
    }
    
    /** Sets up the editor components to display/edit the supplied value.
     * @param value Value which is going to be edited.
     */
    public void setupEditorValue(Object value) {
        if ( editorDelegate instanceof JTextField ) {
            ((JTextField)editorDelegate).setText( value.toString() );
        }
    }
    
    /** Sets up the renderer components to display the supplied value.
     * @param value Value which should be displayed.
     */
    public void setupRendererValue(Object value) {
        if ( rendererDelegate instanceof JLabel ) {
            ((JLabel)rendererDelegate).setText( value.toString() );
        }
    }
    
    protected void editorDelegateAdded() {
        if ( editorDelegate instanceof JTextField ) {
            defaultActionListener.setParameterEditor(this);
            ((JTextField)editorDelegate).addActionListener(defaultActionListener);
        }
        
        defaultFocusListener.setParameterEditor(this);
        editorDelegate.addFocusListener( defaultFocusListener );
    }
    
    protected void editorDelegateRemoved() {
        if ( editorDelegate instanceof JTextField ) {
            ((JTextField)editorDelegate).removeActionListener(defaultActionListener);
        }
        editorDelegate.removeFocusListener( defaultFocusListener );
    }
    
    protected void rendererDelegateAdded() {
        
    }
    
    protected void rendererDelegateRemoved() {
        
    }
    
    protected int getDefaultWidth() {
        return 60;
    }
    
    protected int getMinimumWidth() {
        int width = getDefaultWidth();
        if ( parameterList != null ) {
            int pWidth = parameterList.getParameterWidth(parameter);
            width = Math.max(width, pWidth);
        }
        return width;
    }
    
    /** Causes the Tree/Table to stop editing.
     */
    public void requestEditingStop() {
        if ( getEditorTree() != null ) {
            getEditorTree().stopEditing();
        } else if ( getEditorTable() != null ) {
            // getTable(). // Tell table to stop editing
        }
    }
    
    /** Causes the next editable node to start editing
     */
    public void editNextNode() {
        if ( getEditorTree() != null ) {
            TreePath path = getEditorTree().getEditingPath();
            getEditorTree().stopEditing();
        }
    }
    
    public int getDelegateContainerXOffset() {
        int xoffset = 0;
        
        FontMetrics fm = rendererLabel.getFontMetrics( rendererLabel.getFont() );
        xoffset = fm.stringWidth( rendererLabel.getText() );
        
        return xoffset;
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
    static public AbstractParameterEditor getRendererFor(ParameterList parameterList,String parameter) {
        AbstractParameterEditor r = null;
        int index;
        if ( parameterList != null && parameter != null ) {
            Parameter p = parameterList.getParameter(parameter);
            //String type = p.getType();
            
            if ( p instanceof ComboParameter ) {
                r = new ComboParameterEditor(parameterList, parameter);
            } else if ( p instanceof IntegerParameter) {
                r = new IntegerParameterEditor(parameterList, parameter);
            } else if ( p instanceof DoubleParameter ) {
                r = new DoubleParameterEditor(parameterList, parameter);
            } else if ( p instanceof StringParameter ) {
                r = new DefaultParameterEditor(parameterList, parameter);
            } else if ( p instanceof BooleanParameter ) {
                r = new BooleanParameterEditor(parameterList, parameter);
            } else if ( p instanceof DiceParameter ) {
                r = new DiceParameterEditor(parameterList, parameter);
            } else if ( p instanceof MutableListParameter ) {
                r = new MutableListParameterEditor(parameterList, parameter);
            } else if ( p instanceof ListParameter ) {
                r = new ListParameterEditor(parameterList, parameter);
            } else {
                r = new DefaultParameterEditor(parameterList, parameter);
            }
        }
        return r;
    }
    
    /** Getter for property selected.
     * @return Value of property selected.
     */
    public boolean isRendererSelected() {
        return rendererSelected;
    }
    
    /** Getter for property selected.
     * @return Value of property selected.
     */
    public boolean isEditorSelected() {
        return editorSelected;
    }
    
    /** Setter for property selected.
     * @param selected New value of property selected.
     */
    public void setRendererSelected(boolean selected) {
        this.rendererSelected = selected;
        rendererLabel.setSelected(selected);
    }
    
    /** Setter for property selected.
     * @param selected New value of property selected.
     */
    public void setEditorSelected(boolean selected) {
        this.editorSelected = selected;
        editorLabel.setSelected(selected);
    }
    
    /** Getter for property hasFocus.
     * @return Value of property hasFocus.
     */
    public boolean isRendererHasFocus() {
        return rendererHasFocus;
    }
    
    /** Getter for property hasFocus.
     * @return Value of property hasFocus.
     */
    public boolean isEditorHasFocus() {
        return editorHasFocus;
    }
    
    /** Setter for property hasFocus.
     * @param hasFocus New value of property hasFocus.
     */
    public void setEditorHasFocus(boolean hasFocus) {
        this.editorHasFocus = hasFocus;
        editorLabel.setHasFocus(hasFocus);
    }
    
    /** Setter for property hasFocus.
     * @param hasFocus New value of property hasFocus.
     */
    public void setRendererHasFocus(boolean hasFocus) {
        this.rendererHasFocus = hasFocus;
        rendererLabel.setHasFocus(hasFocus);
    }
    
    /** Getter for property parameterNameVisible.
     * @return Value of property parameterNameVisible.
     */
    public boolean isParameterNameVisible() {
        return parameterNameVisible;
    }
    
    /** Setter for property parameterNameVisible.
     * @param parameterNameVisible New value of property parameterNameVisible.
     */
    public void setParameterNameVisible(boolean parameterNameVisible) {
        this.parameterNameVisible = parameterNameVisible;
    }
    
    public String toString() {
        String s = getClass().getName() + " [Parameter:" + parameter + "]";
        return s;
    }
    
    
    /**
     * Informs the editor that it's current selection status may have changed to <code>isSelected</code>.
     */
    public void selectionStateChanged(boolean isSelected) {
        if ( this.editorSelected != isSelected ) {
            setEditorSelected(isSelected);
            //editorLabel.setSelected(isSelected);
            //System.out.println("Selection of ParameterEditor changed to " + (( isSelected ) ? "T" : "F"));
            editorLabel.repaint();
        }
    }
    
    public class RestrictedPanel extends JPanel {
        private JComponent heightRestricter;
        private int minWidth = 0;
        
        public RestrictedPanel(JComponent heightRestricter, int minWidth) {
            super();
            this.heightRestricter = heightRestricter;
            this.minWidth = minWidth;
        }
        
        public void setHeightRestricter(JComponent heightRestricter) {
            this.heightRestricter = heightRestricter;
        }
        
        public void setMinimumWidth(int minWidth) {
            this.minWidth = minWidth;
        }
        
        public Dimension getPreferredSize() {
            Dimension e = super.getPreferredSize();
            
            if ( heightRestricter != null ) {
                Dimension r = heightRestricter.getPreferredSize();
                
                if ( r.height != 0 ) {
                    e.height = Math.min(r.height+2,e.height);
                }
            }
            
            if ( minWidth > 0 && e.width < minWidth ) {
                e.width = minWidth;
            }
            
            return e;
        }
        
        public Dimension getMinimumSize() {
            Dimension e = super.getMinimumSize();
            
            if ( heightRestricter != null ) {
                Dimension r = heightRestricter.getMinimumSize();
                
                //     if ( e.height == 0 || e.height > r.height ) {
                e.height = r.height;
                //     }
            }
            
            if ( minWidth > 0 && e.width < minWidth ) {
                e.width = minWidth;
            }
            
            return e;
        }
        
      /* public void paint(Graphics g) {
            System.out.println("RestrictedPanel painted at :" + getLocation());
            super.paint(g);
            int index2;
            for(index2 = 0; index2 <= 300; ) {
                if ( index2 % 100 == 0 ) {
                    g.setColor( Color.red );
                }
                else {
                    g.setColor( Color.blue );
                }
       
                g.drawLine(index2, 0, index2, 20);
                index2 += 10;
            }
        } */
    }
    
    public class TestPanel extends JPanel {
        public TestPanel() {
            //  setDoubleBuffered(false);
        }
        
        public void paint(Graphics g) {
            System.out.println("TestPanel painted at :" + getLocation());
            
            super.paint(g);
            int index2;
            for(index2 = 0; index2 <= 300; ) {
                if ( index2 % 100 == 0 ) {
                    g.setColor( Color.red );
                } else {
                    g.setColor( Color.blue );
                }
                
                g.drawLine(index2, 0, index2, 20);
                index2 += 10;
            }
        }
    }
  /*  public class RestrictedBorderLayout extends BorderLayout {
        private JComponent heightRestricter;
        private int minWidth = 0;
   
        public RestrictedBorderLayout(JComponent heightRestricter, int minWidth) {
            private JComponent heightRestricter;
            private int minWidth = 0;
        }
   
        public Dimension preferredLayoutSize(Container parent) {
            Dimension e = super.preferredLayoutSize(parent);
   
            Dimension r = heightRestricter.getPreferredSize();
   
            if ( e.height > r.height ) {
                e.height = r.height;
            }
   
            if ( minWidth > 0 && e.width < minWidth ) {
                e.width = minWidth;
            }
        }
   
        public Dimension minimumLayoutSize(Container parent) {
            Dimension e = super.minimumLayoutSize(parent);
   
            Dimension r = heightRestricter.getPreferredSize();
   
            if ( e.height > r.height ) {
                e.height = r.height;
            }
   
            if ( minWidth > 0 && e.width < minWidth ) {
                e.width = minWidth;
            }
        }
    } */
    
    public class SelectableLabel extends JLabel {
        protected boolean selected;
        protected boolean hasFocus;
        /**
         * Paints the value.  The background is filled based on selected.
         */
        public SelectableLabel() {
            
        }
        
        public void paint(Graphics g) {
            Color bColor;
            
            if(selected) {
                bColor = getBackgroundSelectionColor();
            } else {
                bColor = getBackgroundNonSelectionColor();
                if(bColor == null)
                    bColor = getBackground();
            }
            
            int imageOffset = -1;
            if ( selected || isOpaque() ) {
                
                if(bColor != null) {
                    Icon currentI = getIcon();
                    
                    imageOffset = getLabelStart();
                    g.setColor(bColor);
                    if(getComponentOrientation().isLeftToRight()) {
                        g.fillRect(imageOffset, 0, getWidth() - 1 - imageOffset,
                                getHeight());
                    } else {
                        g.fillRect(0, 0, getWidth() - 1 - imageOffset,
                                getHeight());
                    }
                }
            }
            
            if (hasFocus) {
                imageOffset = getLabelStart();
                Color       bsColor = getBorderSelectionColor();
                
                if (bsColor != null) {
                    g.setColor(bsColor);
                    if(getComponentOrientation().isLeftToRight()) {
                        g.drawRect(imageOffset, 0, getWidth() - 1 - imageOffset,
                                getHeight() - 1);
                    } else {
                        g.drawRect(0, 0, getWidth() - 1 - imageOffset,
                                getHeight() - 1);
                    }
                }
            }
            super.paint(g);
        }
        
        private int getLabelStart() {
        /*Icon currentI = getIcon();
        if(currentI != null && getText() != null) {
            return currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
        } */
            return 0;
        }
        
        public void setSelected(boolean selected){
            this.selected = selected;
        }
        
        public void setHasFocus(boolean hasFocus) {
            this.hasFocus = hasFocus;
        }
    }
    
    public class FocusablePanel extends JPanel {
        public FocusablePanel() {
            
        }
        
        public boolean isFocusTraversable() {
            return true;
        }
        
    }
    
    public class DefaultFocusListener extends FocusAdapter {
        private DefaultParameterEditor parameterEditor = null;
        
        public DefaultFocusListener() {
            
        }
        
        public void setParameterEditor(DefaultParameterEditor pe) {
            parameterEditor = pe;
        }
        
        public void focusGained(FocusEvent e) {
            if ( parameterEditor != null ) parameterEditor.highlightEditor();
        }
        
      /*  public void focusLost(FocusEvent e) {
            // if ( parameterEditor != null ) parameterEditor.requestEditingStop();
            if ( parameterEditor != null ) parameterEditor.editNextNode();
        } */
    }
    
    public class DefaultActionListener extends Object implements ActionListener {
        private DefaultParameterEditor parameterEditor = null;
        
        public DefaultActionListener() {
            
        }
        
        public void setParameterEditor(DefaultParameterEditor pe) {
            parameterEditor = pe;
        }
        
        public void actionPerformed(ActionEvent e) {
            if ( parameterEditor != null ) parameterEditor.requestEditingStop();
        }
    }
}
