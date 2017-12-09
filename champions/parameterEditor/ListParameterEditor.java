/*
 * BooleanParameterEditor.java
 *
 * Created on June 13, 2001, 4:48 PM
 */

package champions.parameterEditor;

import champions.ArrayListModel;
import champions.parameters.ListParameter;
import champions.parameters.ParameterList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import tjava.PopupList;
import treeTable.TreeTable;



/**
 *
 * @author  twalker
 * @version
 */
public class ListParameterEditor extends DefaultParameterEditor
implements ListSelectionListener {
    
    static private boolean setup = false;
    
    static protected javax.swing.JPanel editorPanel;
    static protected javax.swing.JLabel editorLabel;
    static protected RestrictedListPanel editorContainer;
    static protected tjava.PopupList editorPopupList;
    
    static protected javax.swing.JPanel rendererPanel;
    static protected javax.swing.JLabel rendererLabel;
    static protected RestrictedListPanel rendererContainer;
    static protected tjava.PopupList rendererPopupList;
    
    static protected PopupListListener popupListListener;
    
    /** Holds value of property model. */
    protected ListModel model;
    protected ListSelectionModel editorSelectionModel;
    protected ListSelectionModel rendererSelectionModel;
    
    /** Creates new BooleanParameterEditor */
    public ListParameterEditor(ParameterList parameterList,String parameter) {
        super(parameterList,parameter);
        
        setupListModel();
        setupSelectionModel();
        
        setupPanels();
        
        setRendererDelegate(null);
        setEditorDelegate(null);
    }
    
    protected void setupListModel() {
        Object o;
        ListParameter param = (ListParameter)parameterList.getParameter(parameter);
        if ( param.getModel() != null ) {
            ListModel model = param.getModel();
            setModel(model);
        }
        else if ( param.getOptions() != null ) {
            ListModel model = new ArrayListModel( param.getOptions() );
            setModel(model);
        }
    }
    
    protected void setupSelectionModel() {
        editorSelectionModel = new PopupList.ToggleSelectionModel();
        rendererSelectionModel = new PopupList.ToggleSelectionModel();
    }
    
    protected void setupPanels() {
        if ( setup == true ) return;
        
        GridBagConstraints gridBagConstraints;
        
        editorPanel = new javax.swing.JPanel();
        
        editorPanel.setLayout(new BorderLayout());
        editorPanel.setOpaque(false);
        
        editorLabel = new javax.swing.JLabel();
        editorLabel.setOpaque(false);
        editorPanel.add(editorLabel, java.awt.BorderLayout.NORTH);
        
        editorContainer = new RestrictedListPanel(getDefaultWidth(), 60);
        
        editorContainer.setLayout( new GridBagLayout() );
        editorContainer.setOpaque(false);
        editorPanel.add(editorContainer, java.awt.BorderLayout.CENTER);
        
        editorPopupList = new PopupList();
        editorPopupList.setOpaque(false);
        editorPopupList.setSelectionModel(editorSelectionModel);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        
        editorContainer.add(editorPopupList, gridBagConstraints);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        JPanel p = new JPanel();
        p.setDoubleBuffered(false);
        //  p.setBorder(new javax.swing.border.LineBorder(Color.red));
        editorContainer.add(p, gridBagConstraints);
        
      /*  rendererPanel = editorPanel;
        rendererLabel = editorLabel;
        rendererContainer = editorContainer;
        rendererPopupList = editorPopupList; */
        rendererPanel = new javax.swing.JPanel();
        
        rendererPanel.setLayout(new BorderLayout());
        rendererPanel.setOpaque(false);
        rendererPanel.setDoubleBuffered(false);
        
        rendererLabel = new javax.swing.JLabel();
        rendererLabel.setOpaque(false);
        rendererLabel.setDoubleBuffered(false);
        rendererPanel.add(rendererLabel, java.awt.BorderLayout.NORTH);
        
        rendererContainer = new RestrictedListPanel(getDefaultWidth(), 60);
        //  FlowLayout f2 = new java.awt.FlowLayout();
        //  f2.setAlignment(FlowLayout.LEFT);
        rendererContainer.setOpaque(false);
        rendererContainer.setLayout( new GridBagLayout() );
        rendererContainer.setDoubleBuffered(false);
        rendererPanel.add(rendererContainer, java.awt.BorderLayout.CENTER);
        
        rendererPopupList = new PopupList();
        rendererPopupList.setOpaque(false);
        rendererPopupList.setSelectionModel(rendererSelectionModel);
        rendererPopupList.setDoubleBuffered(false);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        
        rendererContainer.add(rendererPopupList, gridBagConstraints);
        
        // Add Place Holder
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        p = new JPanel();
        p.setOpaque(false);
        p.setDoubleBuffered(false);
        // p.setBorder(new javax.swing.border.LineBorder(Color.blue));
        rendererContainer.add(p, gridBagConstraints);
        
        //rendererPanel.add(rendererPopupList, java.awt.BorderLayout.CENTER);
        
        popupListListener = new PopupListListener();
        
        setup = true;
    }
    
    public Object getCellEditorValue() {
        return null;
    }
    
    public void highlightEditor() {
        // Do nothing
    }
    
    public void setupEditorValue(Object value) {
        if ( parameterList != null && parameter != null ) {
            
            ListSelectionModel lsm = editorPopupList.getSelectionModel();
            lsm.setValueIsAdjusting(true);
            lsm.clearSelection();
            ListModel lm = getModel();
            
            Object pvalue, mvalue;
            int index, count;
            int mindex, mcount;
            
            if ( value != null ) {
            List list = (List)value;
            
            
                //count = parameterList.getIndexedParameterSize(parameter);
                count = list.size();
                mcount = lm.getSize();

                for(index=0;index<count;index++) {
                    //pvalue = parameterList.getIndexedParameterValue(parameter,  index);
                    pvalue = list.get(index);
                    if ( pvalue != null ) {
                        for(mindex=0;mindex<mcount;mindex++) {
                            mvalue = lm.getElementAt(mindex);
                            if ( pvalue.equals(mvalue) ) {
                                lsm.addSelectionInterval(mindex,mindex);
                            }
                        }
                    }
                }
            }
            lsm.setValueIsAdjusting(false);
        }
    }
    
    public void setupRendererValue(Object value) {
        if ( parameterList != null && parameter != null ) {
            
            
            ListSelectionModel lsm = rendererPopupList.getSelectionModel();
            lsm.setValueIsAdjusting(true);
            lsm.clearSelection();
            ListModel lm = getModel();
            
            Object pvalue, mvalue;
            int index, count;
            int mindex, mcount;
            
            ListParameter param = (ListParameter)parameterList.getParameter(parameter);
            List list = parameterList.getIndexedParameterValues(parameter);
            count = list.size();
            mcount = lm.getSize();
            
            for(index=0;index<count;index++) {
                pvalue = list.get(index);
                if ( pvalue != null ) {
                    for(mindex=0;mindex<mcount;mindex++) {
                        mvalue = lm.getElementAt(mindex);
                        if ( pvalue.equals(mvalue) ) {
                            lsm.addSelectionInterval(mindex,mindex);
                        }
                    }
                }
            }
            lsm.setValueIsAdjusting(false);
        }
    }
    
    /**
     * Called whenever the value of the selection changes.
     * @param e the event that characterizes the change.
     */
    public void valueChanged(ListSelectionEvent e) {
        if ( e.getValueIsAdjusting() == false ) {
            copyValuesToParameterList();
        }
    }
    
    private void copyValuesToParameterList() {
        if ( parameterList != null && parameter != null ) {
            int originalListCount;
            //int listCount;
            
            originalListCount = parameterList.getIndexedParameterSize(parameter);
            
            int index, total, count;
            
            Object value;
            
            ListSelectionModel lsm = editorPopupList.getSelectionModel();
            ListModel lm = editorPopupList.getModel();
            
            int listSize = lm.getSize();
            
            total = 0;
            
            ArrayList addList = new ArrayList();
            ArrayList removeList = new ArrayList();
            
            if ( lsm.isSelectionEmpty() == false ) {
                for (index = 0;index < listSize;index++) {
                    value = lm.getElementAt(index);
                    if ( lsm.isSelectedIndex(index) ) {
                        addList.add(value);  //parameterList.addIndexedParameterValue(parameter,value);
                        total++;
                    }
                    else {
                        removeList.add(value);//parameterList.removeIndexedParameterValue(parameter,value);
                    }
                }
                
                Iterator it;
                it = addList.iterator();
                while(it.hasNext()) {
                    parameterList.addIndexedParameterValue(parameter, it.next());
                }
                
                it = removeList.iterator();
                while(it.hasNext()) {
                    parameterList.removeIndexedParameterValue(parameter, it.next());
                }
            }
            else {
                // Selection is empty, just remove them all...
                count = parameterList.getIndexedParameterSize(parameter);
                for(index = 0; index < count; count--) {
                    value = parameterList.getIndexedParameterValue(parameter, index);
                    parameterList.removeIndexedParameterValue(parameter,value);
                }
            }
            // Now that we have added all the selected ones, lets remove all
            // that are not selected...
//            count = parameterList.getIndexedParameterSize(parameter);
//            for(index = 0; index < count; index++) {
//                value = parameterList.getIndexedParameterValue(parameter, index);
//
//                if ( lsm.isSelected( ) ) {
//                else {
//                            parameterList.removeIndexedParameterValue(parameter,value);
//                        }
//            }
            
            //            for(index = total; index < listCount; ) {
            //                parameterList.removeAllIndexed(index, keyName, false);
            //                listCount--;
            //            }
            
            firePADValueChanged( new Integer(originalListCount), new Integer(total) );
            
            //parameterList.setParameterOption(parameter, "SET", (total > 0) ? "TRUE" : "FALSE" );
        }
    }
    
    /**
     * Tell the editor to stop editing and accept any partially edited
     * value as the value of the editor.  The editor returns false if
     * editing was not stopped, useful for editors which validates and
     * can not accept invalid entries.
     *
     * This version should call PADValueChanging to make sure it is okay to
     * change to the new value.  However, it does not do this right now,
     * mostly becuase it is impossible to encapsulate the new value in the
     * form necessary for PADValueChanging.
     *
     * @return	true if editing was stopped
     */
    public boolean stopCellEditing() {
        boolean rv = true;
        
        copyValuesToParameterList();
        
        if ( rv == true ) {
            fireEditingStopped();
        }
        
        return rv;
    }
    
    /** Getter for property model.
     * @return Value of property model.
     */
    public ListModel getModel() {
        return model;
    }
    
    /** Setter for property model.
     * @param model New value of property model.
     */
    public void setModel(ListModel model) {
        this.model = model;
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
        
        return editorPanel;
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
        
        return editorPanel;
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
        Object currentNode = getEditorNode();
        
        setEditorTree(treeTable);
        setEditorNode(node);
        setEditorLastRow(row);
        setEditorLastColumn(column);
        setEditorSelected(isSelected);
        setEditorHasFocus(false);
        
        if ( currentNode != node ) {
            setupEditor(treeTable.getForeground(), treeTable.getBackground(), treeTable.getFont());
        }
        
        return editorPanel;
        
    }
    
    protected void setupEditor(Color fg, Color bg, Font font) {
        editorLabel.setVisible( isParameterNameVisible() );
        
        if ( parameterList != null ) {
            editorLabel.setText(parameterList.getParameterDescription(parameter) + ": ");
            int width = parameterList.getParameterWidth(parameter);
            if ( width <= 0 ) width = getDefaultWidth();
            editorContainer.setWidth(width);
            editorPopupList.setMinimumListSize(new Dimension(width, 0) );
        }
        
        
        editorLabel.setForeground( fg );
        editorLabel.setBackground( bg );
        editorLabel.setFont(font);
        
        editorPopupList.setForeground( fg );
        editorPopupList.setBackground( bg );
        editorPopupList.setFont(font);
        
        editorPopupList.setCustomPopupControls(null);
        
        prepareEditorDelegate();
        
        setupEditorValue(getCurrentValue());
        
        startEditing();
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
        
        return rendererPanel;
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
        
        return rendererPanel;
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
        
        return rendererPanel;
    }
    
    protected void setupRenderer(Color fg, Color bg, Font font) {
        rendererLabel.setVisible( isParameterNameVisible() );
        
        if ( parameterList != null ) {
            rendererLabel.setText( parameterList.getParameterDescription(parameter) + ": ");
            int width = parameterList.getParameterWidth(parameter);
            if ( width <= 0 ) width = getDefaultWidth();
            rendererContainer.setWidth(width);
            rendererPopupList.setMinimumListSize(new Dimension(width, 0) );
        }
        
        rendererLabel.setForeground( fg );
        rendererLabel.setBackground( bg );
        rendererLabel.setFont(font);
        
        rendererPopupList.setForeground( fg );
        rendererPopupList.setBackground( bg );
        rendererPopupList.setFont(font);
        
        rendererPopupList.setCustomPopupControls(null);
        
        prepareRendererDelegate();
        
        setupRendererValue(getCurrentValue());
    }
    
    protected void editorDelegateAdded() {
        editorPopupList.setModel(model);
        editorPopupList.setSelectionModel(editorSelectionModel);
        popupListListener.setParameterEditor(this);
        editorPopupList.addListSelectionListener( popupListListener);
    }
    
    protected void editorDelegateRemoved() {
        editorPopupList.removeListSelectionListener( popupListListener);
    }
    
    protected void rendererDelegateAdded() {
        rendererPopupList.setModel(model);
        rendererPopupList.setSelectionModel(rendererSelectionModel);
    }
    
    protected void rendererDelegateRemoved() {
        
    }
    
    protected int getDefaultWidth() {
        return 200;
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
                // editorLabel.setText(parameterList.getParameterDescription(parameter) + ": ");
                
                Dimension a = editorLabel.getPreferredSize();
                Rectangle b = treeTable.getColumnBounds(row, column);
                
                //   Rectangle c = new Rectangle(0, 0, b.width, b.height);
                
                if ( DEBUG > 0 ) System.out.println("canEditImmediately Real Column Bounds are " + b);
                
                // Adjust for the label
                b.x += offset + 3; // x + iconOffset + iconGap
                b.width -= offset - 3;
                
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
                    }
                    finally {
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
    
    public boolean canEditImmediately(EventObject e, JTree tree, int row, int offset) {
        if((e instanceof MouseEvent) &&
        SwingUtilities.isLeftMouseButton((MouseEvent)e)) {
            MouseEvent       me = (MouseEvent)e;
            
            Rectangle bounds = tree.getRowBounds(row);
            
            if ( rendererContainer != null ) {
                Dimension a = rendererLabel.getPreferredSize();
                int totalWidth = a.width + offset + 3;
                
                // Rectangle c = new Rectangle(bounds.x + totalWidth, bounds.y, bounds.width - totalWidth, bounds.height);
                
                //   Rectangle c = rendererContainer.getBounds();
                //   c.translate( bounds.x + offset, bounds.y );
                //   Rectangle d = editorContainer.getBounds();
                //  d.translate( bounds.x + offset, bounds.y );
                
                
           /*    Graphics g = null;
                try {
                    g = tree.getGraphics();
            
                    g.setColor(Color.red);
                    g.drawRect( bounds.x, bounds.y, bounds.width, bounds.height);
                 //   g.setColor(Color.yellow);
                 //   g.drawRect( d.x+1, d.y+1, c.width-2, c.height-2);
                 //   g.setColor(Color.blue);
                 //   g.drawRect( bounds.x, bounds.y, bounds.width, bounds.height);
                  //  g.setColor(Color.magenta);
                 //   g.drawRect( b.x, b.y, b.width, b.height);
                }
                finally {
                    if ( g != null ) g.dispose();
                }  */
                
                if ( bounds.contains( me.getPoint() ) ) {
                    //  System.out.println("Starting listParameterEditor for " + parameter);
                    return true;
                }
            }
        }
        return false;
    }
    
    public class PopupListListener extends Object implements ListSelectionListener {
        private ListParameterEditor parameterEditor = null;
        
        public PopupListListener() {
            
        }
        
        public void setParameterEditor(ListParameterEditor pe) {
            parameterEditor = pe;
        }
        
        public void valueChanged(ListSelectionEvent e) {
            if (parameterEditor != null ) parameterEditor.valueChanged(e);
        }
    }
    
    public class RestrictedListPanel extends JPanel {
        private int width = 0;
        private int height = 0;
        
        public RestrictedListPanel(int width, int height) {
            this.width  = width;
            this.height = height;
        }
        
        
        public void setWidth(int width) {
            this.width = width;
        }
        
        public void setHeight(int height) {
            this.height = height;
        }
        
        public Dimension getPreferredSize() {
            return new Dimension(width, height);
        }
    }
    
}
