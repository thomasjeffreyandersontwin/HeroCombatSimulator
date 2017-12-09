/*
 * BooleanParameterEditor.java
 *
 * Created on June 13, 2001, 4:48 PM
 */

package champions.parameterEditor;

import champions.parameters.ParameterList;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EventObject;
import javax.swing.JCheckBox;
import javax.swing.border.LineBorder;



/**
 *
 * @author  twalker 
 * @version
 */
public class BooleanParameterEditor extends DefaultParameterEditor {
    
    private boolean setup = false;
    private JCheckBox rendererCheck;
    private JCheckBox editorCheck;
    private CheckboxItemListener checkboxItemListener;
    
    /** Creates new BooleanParameterEditor */
    public BooleanParameterEditor(ParameterList parameterList,String parameter) {
        super(parameterList,parameter);
        
        setupEditor();
        
        setRendererDelegate(rendererCheck);
        setEditorDelegate(editorCheck);
    }

    public BooleanParameterEditor() {
        setupEditor();

        setRendererDelegate(rendererCheck);
        setEditorDelegate(editorCheck);
    }
    
    public void setupEditor() {
        if ( setup == true ) return;
        
        rendererCheck = new JCheckBox();
        editorCheck = new JCheckBox();
        
        checkboxItemListener = new CheckboxItemListener();
        
        rendererCheck.setOpaque(false);
        editorCheck.setOpaque(false);
        
        setup = true;
    }
    
    public Object getCellEditorValue() {
        return (editorCheck.isSelected() ? Boolean.TRUE : Boolean.FALSE );
    }
    
    public void highlightEditor() {
        // Do nothing
    }
    
    public void setupEditorValue(Object value) {
        Object realValue;
        if ( parameterList != null && parameter != null ) {
            realValue = parameterList.getParameterValue(parameter);
        }
        else {
            realValue = value;
        }

        boolean b = realValue != null && realValue.equals( Boolean.TRUE );
        if ( editorCheck.isSelected() != b ) {
            checkboxItemListener.setIgnoreChanges(true);
            editorCheck.setSelected( b );
            checkboxItemListener.setIgnoreChanges(false);
        }
    }
    
    public void setupRendererValue(Object value) {
        Object realValue;
        if ( parameterList != null && parameter != null ) {
            realValue = parameterList.getParameterValue(parameter);
        }
        else {
            realValue = value;
        }
            rendererCheck.setSelected( realValue != null && realValue.equals( Boolean.TRUE ) );
        
    }
    
    protected void editorDelegateAdded() {
        checkboxItemListener.setParameterEditor(this);
        editorCheck.addItemListener(checkboxItemListener);
    }
    
    protected void editorDelegateRemoved() {
        editorCheck.removeItemListener(checkboxItemListener);
    }
    
    /**
     * Returns whether the editor should start editing immediate.
     *
     * By default, the editor waits for 2 clicks or for the timer to time out
     * to start editing.  This can override that behaviour.
     */
    public boolean canEditImmediately(EventObject event) {
        return true;
    }
    
     /**
     * Tell the editor to cancel editing and not accept any partially
     * edited value.
     */
    public void cancelCellEditing() {
       // setCurrentValue(getStartValue());
       // fireEditingCanceled();
        // Don't actually cancel cell editing with a checkbox...it might have just lost it's selection...
        // and gotten cancelled by mistake.
        if ( stopCellEditing() == false ) {
            // Only cancel if we fail to stop...
            super.cancelCellEditing();
        }
    }
    
    
    public class CheckboxItemListener extends Object implements ItemListener {
        private BooleanParameterEditor parameterEditor = null;
        private boolean ignoreChanges = false;
        
        public CheckboxItemListener() {
            
        }
        
        public void setParameterEditor(BooleanParameterEditor pe) {
            parameterEditor = pe;
        }
        public void itemStateChanged(ItemEvent e) {
            if ( ignoreChanges == false && parameterEditor != null ) parameterEditor.setCurrentValue( e.getStateChange() == ItemEvent.SELECTED );

        }

        /**
         * @return the ignoreChanges
         */
        public boolean isIgnoreChanges() {
            return ignoreChanges;
        }

        /**
         * @param ignoreChanges the ignoreChanges to set
         */
        public void setIgnoreChanges(boolean ignoreChanges) {
            this.ignoreChanges = ignoreChanges;
        }
    }
}