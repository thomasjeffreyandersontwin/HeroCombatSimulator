/*
 * AdjParameterEditor.java
 *
 * Created on March 5, 2002, 12:21 PM
 */

package champions.adjustmentPowers.adjustmentTree;

import champions.Ability;
import champions.parameterEditor.DefaultParameterEditor;
import champions.parameters.ParameterList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.EventObject;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import treeTable.TreeTable;



/**
 *
 *
 * @author  Trevor Walker
 * @version
 */
public class AdjParameterEditor extends DefaultParameterEditor 
{
    
    private boolean setup = false;
    
    private static javax.swing.JPanel rendererPanel;
    private static javax.swing.JLabel rendererLabel;
    private static javax.swing.JScrollPane rendererScroll;
    private static javax.swing.JList rendererList;
    private static javax.swing.JButton rendererButton;
    private static DefaultParameterEditor.RestrictedPanel rendererRestrictedPanel;
    
    private static javax.swing.JPanel editorPanel;
    private static javax.swing.JLabel editorLabel;
    private static javax.swing.JScrollPane editorScroll;
    private static javax.swing.JList editorList;
    private static javax.swing.JButton editorButton;
    private static DefaultParameterEditor.RestrictedPanel editorRestrictedPanel;
    
    private static AdjParameterEditor.ObjectListModel rendererListModel;
    private static AdjParameterEditor.ObjectListModel editorListModel;
    
    private static EditListAction editAction;
    
    /** Holds value of property ability. */
    private WeakReference<Ability> abilityRef;    
    
    /** Holds value of property adjustmentType. */
    private int adjustmentType;
    
    /** Creates new AdjParameterEditor */
    public AdjParameterEditor(Ability ability, ParameterList parameterList, String parameter, int adjustmentType) {
        super(parameterList, parameter);
        
        setAbility(ability);
        setAdjustmentType(adjustmentType);
        setupEditor();
        
        setEditorDelegate( editorPanel );
        setRendererDelegate( rendererPanel );
    }
    
    private void setupEditor() {
        if ( setup == true ) return;
        
        rendererPanel = new JPanel();
        rendererLabel = new JLabel();
        rendererScroll = new javax.swing.JScrollPane();
        rendererList = new javax.swing.JList();
        rendererButton = new javax.swing.JButton();
        
        rendererPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        rendererPanel.add(rendererLabel, gridBagConstraints1);
        rendererPanel.setOpaque(false);
        
        rendererList.setVisibleRowCount(4);
        rendererScroll.setViewportView(rendererList);
        
        rendererRestrictedPanel = new DefaultParameterEditor.RestrictedPanel(null, getMinimumWidth());
        rendererRestrictedPanel.setLayout( new BorderLayout() );
        rendererRestrictedPanel.add(rendererScroll, BorderLayout.CENTER);
        rendererRestrictedPanel.setDoubleBuffered(false);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 0.0;
        gridBagConstraints1.weighty = 1.0;
        rendererPanel.add(rendererRestrictedPanel, gridBagConstraints1);
        
        rendererButton.setText("...");
        rendererButton.setOpaque(false);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        rendererPanel.add(rendererButton, gridBagConstraints1);
        
        editorPanel = new JPanel();
        editorLabel = new JLabel();
        editorScroll = new javax.swing.JScrollPane();
        editorList = new javax.swing.JList();
        editorButton = new javax.swing.JButton();
        
        
        editorPanel.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        editorPanel.add(editorLabel, gridBagConstraints1);
        editorPanel.setOpaque(false);
        
        editorList.setVisibleRowCount(4);
        editorScroll.setViewportView(editorList);
        
        editorRestrictedPanel = new DefaultParameterEditor.RestrictedPanel(null, getMinimumWidth());
        editorRestrictedPanel.setLayout( new BorderLayout() );
        editorRestrictedPanel.add(editorScroll, BorderLayout.CENTER);
        editorRestrictedPanel.setDoubleBuffered(false);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 0.0;
        gridBagConstraints1.weighty = 1.0;
        editorPanel.add(editorRestrictedPanel, gridBagConstraints1);
        
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        editorPanel.add(editorButton, gridBagConstraints1);
        
        
        rendererListModel = new AdjParameterEditor.ObjectListModel();
        editorListModel = new AdjParameterEditor.ObjectListModel();
        
        editorList.setModel(editorListModel);
        rendererList.setModel(rendererListModel);
        
        editAction = new EditListAction("...");
        editorButton.setAction(editAction);
        editorButton.setText("...");
        editorButton.setOpaque(false);
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
        
        setupEditor(treeTable.getForeground(), treeTable.getBackground(), treeTable.getFont());
        
        return editorPanel;
        
    }
    
    
    protected void setupEditor(Color fg, Color bg, Font font) {
        editorLabel.setVisible( isParameterNameVisible() );
        
        if ( parameterList != null ) {
            editorLabel.setText(parameterList.getParameterDescription(parameter) + ": ");
        }
        
        editorLabel.setForeground( fg);
        editorPanel.setForeground( fg );
        editorScroll.setForeground( fg );
        editorButton.setForeground( fg );
        
        editorLabel.setBackground( bg);
        editorPanel.setBackground( bg );
        editorScroll.setBackground( bg );
        editorButton.setBackground( bg );
        
        editorLabel.setFont(font);
        editorButton.setFont(font);
        editorList.setFont(font);
        
        editorListModel.setupList(parameterList, parameter);
        
        editAction.setup( getAbility(), parameterList, parameter, adjustmentType);
        
        editorRestrictedPanel.setMinimumWidth( getMinimumWidth());
        
        //super.setupEditor(fg,bg,font);
    }
    
    protected void setupRenderer(Color fg, Color bg, Font font) {
        rendererLabel.setVisible( isParameterNameVisible() );
        
        if ( parameterList != null ) {
            rendererLabel.setText(parameterList.getParameterDescription(parameter) + ": ");
        }
        
        rendererLabel.setForeground( fg);
        rendererPanel.setForeground( fg );
        rendererScroll.setForeground( fg );
        rendererButton.setForeground( fg );
        
        rendererLabel.setBackground( bg );
        rendererPanel.setBackground( bg );
        rendererScroll.setBackground( bg );
        rendererButton.setBackground( bg );
        
        rendererLabel.setFont(font);
        rendererButton.setFont(font);
        rendererList.setFont(font);
        
        rendererListModel.setupList(parameterList, parameter);
        
        rendererRestrictedPanel.setMinimumWidth( getMinimumWidth());
        
        //super.setupRenderer(fg,bg,font);
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
        if((e instanceof MouseEvent) && SwingUtilities.isLeftMouseButton((MouseEvent)e)) {
            return true;
        }
        return false;
    }
    
    /** Sets up the editor components to display/edit the supplied value.
     * @param value Value which is going to be edited.
     */
    public void setupEditorValue(Object value) {
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
        
        if ( rv == true ) {
            fireEditingStopped();
        }
        
        return rv;
    }
    
    /** Getter for property ability.
     * @return Value of property ability.
     */
    public Ability getAbility() {
        if ( abilityRef == null ) {
            return null;
        }
        else if ( abilityRef.get() == null ) {
            return null;
        }
        else {
            return abilityRef.get();
        }
    }
    
    /** Setter for property ability.
     * @param ability New value of property ability.
     */
    public void setAbility(Ability ability) {
        abilityRef = new WeakReference<Ability>(ability);
    }
    
    /** Getter for property adjustmentType.
     * @return Value of property adjustmentType.
     */
    public int getAdjustmentType() {
        return adjustmentType;
    }
    
    /** Setter for property adjustmentType.
     * @param adjustmentType New value of property adjustmentType.
     */
    public void setAdjustmentType(int adjustmentType) {
        this.adjustmentType = adjustmentType;
    }
    
    public int getDefaultWidth() {
        return 150;
    }
    
    public class ObjectListModel extends DefaultListModel 
    implements ListModel, PropertyChangeListener {
        
        /** Holds value of property parameterList. */
        private ParameterList parameterList;
        
        /** Holds value of property parameter. */
        private String parameter;
        
        private String indexName;
        
        
        public void setupList(ParameterList parameterList, String parameter) {

            if ( this.parameterList != parameterList ) {
                if ( this.parameterList != null ) {
                    this.parameterList.removePropertyChangeListener(this);
                }
                this.parameterList = parameterList;
                if ( this.parameterList != null ) {
                    this.parameterList.addPropertyChangeListener(this);
                }
            }

            this.parameter = parameter;
//            if ( this.parameterList != null ) {
//                //indexName = this.parameterList.getIndexedParameterIndexName(this.parameter);
//            }
//            else {
//                System.out.println("WARNING: AdjParameterEditor.ObjectListModel().setupList() recieved null parameter List with parameter: " + parameter + ".");
//            }
            
             fireContentsChanged(this,-1,-1);
            
        }
        /**
         * Returns the length of the list.
         */
        public int getSize() {
            int size = 0;
            if ( parameterList != null && parameter != null ) {
                size = parameterList.getIndexedParameterSize(parameter);
            }
            return size;
        }
        
        /**
         * Returns the value at the specified index.
         */
        public Object getElementAt(int index) {
            Object o = null;
            if ( parameterList != null && parameter != null ) {
                o = parameterList.getIndexedParameterValue(parameter, index);
            }
            return o;
        }
        
        /**
         * This method gets called when a bound property is changed.
         * @param evt A PropertyChangeEvent object describing the event source
         *  	and the property that has changed.
         */
        public void propertyChange(PropertyChangeEvent evt) {
            if ( evt.getPropertyName().startsWith(parameter) ) {
                fireContentsChanged(this, -1, -1);
            }
        }
        
    }
    
    public static class EditListAction extends AbstractAction{
        
        /** Holds value of property ability. */
        private Ability ability;
        
        /** Holds value of property parameter. */
        private String parameter;
        
        /** Holds value of property parameterList. */
        private ParameterList parameterList;
        
        /** Holds value of property adjustmentType. */
        private int adjustmentType;
        
        /** Holds value of property adjustmentLevel. */
        private int adjustmentLevel;
        
        public EditListAction(String name) {
            
        }
        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            
            AdjConfigDialog acd = AdjConfigDialog.getDefaultDialog(ability,adjustmentType,parameterList,parameter);
            acd.setVisible(true);
        }     
        
        public void setup(Ability ability, ParameterList parameterList, String parameter, int adjustmentType) {
           this.ability = ability;
           this.parameterList = parameterList;
           this.parameter = parameter;
            this.adjustmentType = adjustmentType;
        }
    }
}
