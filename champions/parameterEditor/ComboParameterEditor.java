/*
 * ComboParameterEditor.java
 *
 * Created on June 13, 2001, 5:10 PM
 */

package champions.parameterEditor;

import champions.parameters.ParameterList;
import champions.parameters.ComboParameter;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.border.EmptyBorder;


/**
 *
 * @author  twalker
 * @version
 */
public class ComboParameterEditor extends DefaultParameterEditor {
    
    private boolean setup = false;
    private JComboBox rendererControl;
    private JComboBox editorControl;
    private ComboItemListener comboItemListener;
    private DefaultComboBoxModel editorComboBoxModel;
    private DefaultComboBoxModel rendererComboBoxModel;
    
    /** Creates new BooleanParameterEditor */
    public ComboParameterEditor(ParameterList parameterList,String parameter) {
        super(parameterList,parameter);
        
        setupEditor();
        
        setRendererDelegate(rendererControl);
        setEditorDelegate(editorControl);
    }
    
    public void setupEditor() {
        if ( setup == true ) return;
        
        rendererControl = new JComboBox() {

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height = d.height - 4;
                return d;
            }
            
        };
        rendererControl.setOpaque(false);
        rendererControl.setBorder( new EmptyBorder(0,0,1,0));
        
        
        editorControl = new JComboBox() {

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height = d.height - 4;
                return d;
            }
            
        };
        editorControl.setOpaque(false);
        editorControl.setBorder( new EmptyBorder(0,0,1,0));
        
        comboItemListener = new ComboItemListener();
        editorControl.addItemListener(comboItemListener);
        
        editorComboBoxModel = new DefaultComboBoxModel();
        rendererComboBoxModel = new DefaultComboBoxModel();
        
        setup = true;
    }
    
        public void setupColors() {
            
            
//        Color bg = UIManager.getColor( "AbilityEditor.background");
//        Color fg = UIManager.getColor( "AbilityEditor.foreground");
//        
//        if ( bg != null ) {
//            rendererControl.setBackground(bg);
//            editorControl.setBackground(bg);
//        }
//        
//        if ( fg != null ) {
//            rendererControl.setBackground(bg);
//            editorControl.setBackground(bg);
//        }
    }
    
    
    public Object getCellEditorValue() {
        return editorControl.getSelectedItem();
    }
    
    public void highlightEditor() {
        // Do nothing
    }
    
    public void setupEditorValue(Object value) {
        if ( parameterList != null && parameter != null ) {
            Object realValue = parameterList.getParameterValue(parameter);
            editorControl.setSelectedItem( realValue );
        }
        else {
            if ( editorControl.getSelectedItem() != value ) editorControl.setSelectedItem( value );
        }
    }
    
    public void setupRendererValue(Object value) {
        
        if ( parameterList != null && parameter != null ) {
            Object realValue = parameterList.getParameterValue(parameter);
         //   System.out.println("Set Combo Renderer Value to: " + realValue);
            rendererControl.setSelectedItem( realValue );
        }
        else {
            rendererControl.setSelectedItem( value );
        }
    }
    
    protected void editorDelegateAdded() {
        comboItemListener.setParameterEditor(this);
        
        if ( parameterList != null ) {
              
            ComboParameter param = (ComboParameter)parameterList.getParameter(parameter);
            Object[] objects = param.getOptions();
            if ( objects != null ) {
                editorControl.setModel( editorComboBoxModel);
                editorControl.removeAllItems();
                int index;
                for(index=0;index<objects.length;index++) {

                    editorControl.addItem(objects[index]);
                }
            }
            else if ( param.getModel() != null ) {
                editorControl.setModel( (ComboBoxModel)param.getModel());
            }
            else {
                editorControl.setModel( editorComboBoxModel);
                editorControl.removeAllItems();
            }
        }
        
        // Setup the Editor Value correctly
        setupEditorValue(null);
        
        // Don't start listening until AFTER the change, otherwise you will catch a false
        // change.
        editorControl.addItemListener(comboItemListener);
    }
    
    public void setComboBoxModel(ComboBoxModel model) {
        editorControl.setModel(model);
        rendererControl.setModel(model);
    }
    
    protected void editorDelegateRemoved() {
        editorControl.removeItemListener(comboItemListener);
    }
    
    protected void rendererDelegateAdded() {
        
        if ( parameterList != null ) {
            ComboParameter param = (ComboParameter)parameterList.getParameter(parameter);
            Object[] objects = param.getOptions();
            if ( objects != null ) {
                rendererControl.setModel( rendererComboBoxModel);
                rendererControl.removeAllItems();
                int index;
                for(index=0;index<objects.length;index++) {

                    rendererControl.addItem(objects[index]);
                }
            }
            else if ( param.getModel() != null ) {
                rendererControl.setModel( param.getModel() );
            }
            else {
                rendererControl.setModel( rendererComboBoxModel);
                rendererControl.removeAllItems();
            }
        }
        
        setupRendererValue(null);
    }
    
    protected void rendererDelegateRemoved() {
        
    }
    
     /**
     * Tell the editor to cancel editing and not accept any partially
     * edited value.
     */
    public void cancelCellEditing() {
        // Don't actually cancel cell editing with a combo...it might have just lost it's selection...
        // and gotten cancelled by mistake.
        if ( stopCellEditing() == false ) {
            // Only cancel if we fail to stop...
            super.cancelCellEditing();
        }
    }
    
    public class ComboItemListener extends Object implements ItemListener {
        private DefaultParameterEditor parameterEditor = null;
        
        public ComboItemListener() {
            
        }
        
        public void setParameterEditor(DefaultParameterEditor pe) {
            parameterEditor = pe;
        }
        
        public void itemStateChanged(ItemEvent e) {
       //     System.out.println("Combo changed to: " + e.getItem() + ". ParameterEditor current value: " + parameterEditor.getCellEditorValue());
            if (parameterEditor != null ) parameterEditor.setCurrentValue( parameterEditor.getCellEditorValue() );
        }
    }
}