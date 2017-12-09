/*
 * MutableListParameterEditor.java
 *
 * Created on November 11, 2003, 5:57 PM
 */

package champions.parameterEditor;

import champions.ArrayListModel;
import champions.parameters.MutableListParameter;
import champions.parameters.ParameterList;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import tjava.PopupList.ToggleSelectionModel;


/**
 *
 * @author  1425
 */
public class MutableListParameterEditor extends ListParameterEditor {
    
    protected ListModel baseModel;
    protected ListModel mutableModel;
    
    protected String buttonName1 = null;
    protected String buttonName2 = null;
    
    protected ActionListener actionListener1 = null;
    protected ActionListener actionListener2 = null;
    
    protected JPanel customPanel = null;
    
    /** Creates a new instance of MutableListParameterEditor */
    public MutableListParameterEditor(ParameterList parameterList,String parameter) {
        super(parameterList, parameter);
        
        setupButtons();
    }
    
    protected void setupListModel() {
        Object o;
        MutableListParameter param = (MutableListParameter)parameterList.getParameter(parameter);
//        if ( (o = parameterList.getParameterOption(parameter,"MODEL")) != null ) {
//            baseModel = (ListModel)o;
//        }
//        else 
        if ( param.getOptions() != null ) {
            baseModel = new ArrayListModel( param.getOptions() );
        }
        
        if ( param.getModel() != null ) {
            mutableModel = param.getModel();
        }
        
        setModel( new MutableListParameterEditor.CompositeModel(baseModel, mutableModel) );
        //setModel( mutableModel );
    }
    
    protected void setupSelectionModel() {
        editorSelectionModel = new ToggleSelectionModel();  
        rendererSelectionModel = new ToggleSelectionModel();  
    }
    
    protected void setupButtons() {
        Object o;
        MutableListParameter param = (MutableListParameter)parameterList.getParameter(parameter);
        if ( param.getButton1Name() != null ) {
            buttonName1 = param.getButton1Name();
        }
        
        if ( param.getActionListener1() != null ) {
            actionListener1 = param.getActionListener1();
        }
        
        if ( param.getButton2Name()!= null ) {
            buttonName2 = param.getButton2Name();
        }
        
        if ( param.getActionListener2() != null ) {
            actionListener2 = param.getActionListener2();
        }
        
        if ( buttonName1 != null || buttonName2 != null ) {
            customPanel = new JPanel();
            
            if ( buttonName1 != null ) {
                JButton b = new JButton(buttonName1);
                if ( actionListener1 != null ) {
                    b.addActionListener( actionListener1);
                }
                customPanel.add(b);
            }
            
            if ( buttonName2 != null ) {
                JButton b = new JButton(buttonName2);
                if ( actionListener2 != null ) {
                    b.addActionListener( actionListener2);
                }
                customPanel.add(b);
            }
        }
            
    }
        
    protected void setupRenderer(Color fg, Color bg, Font font) {
        super.setupRenderer(fg, bg, font);
        
        if ( customPanel != null ) {
            rendererPopupList.setCustomPopupControls(customPanel);
        }
    }
    
    protected void setupEditor(Color fg, Color bg, Font font) {
        super.setupEditor(fg, bg, font);
        
        if ( customPanel != null ) {
            editorPopupList.setCustomPopupControls(customPanel);
        }
    }
    
    public class CompositeModel extends DefaultListModel 
    implements ListModel, ListDataListener {
        
        private ListModel list1;
        private ListModel list2;
        
        private List elements;
        
        public CompositeModel(ListModel list1, ListModel list2) {
            setList1(list1);
            setList2(list2);
            
            elements = new ArrayList();
            
            updateList();
        }
        
        private void updateList() {
            elements.clear();
            
            int size;
            
            if ( list1 != null ) {
                size = list1.getSize();
                for(int i = 0; i < size; i++) {
                    elements.add( list1.getElementAt(i) );
                }
            }
            
            if ( list2 != null ) {
                size = list2.getSize();
                for(int i = 0; i < size; i++) {
                    if ( elements.contains(list2.getElementAt(i)) == false ) {
                        elements.add( list2.getElementAt(i) );
                    }
                }
            }
            
            fireContentsChanged(this, 0, elements.size());
        }
        
        public Object getElementAt(int index) {
            return elements.get(index);
        }
        
        public int getSize() {
            return elements.size();
        }
        
        public void contentsChanged(ListDataEvent e) {
            updateList();
        }
        
        public void intervalAdded(ListDataEvent e) {
            updateList();
        }
        
        public void intervalRemoved(ListDataEvent e) {
            updateList();
        }
        
        /** Getter for property list1.
         * @return Value of property list1.
         *
         */
        public ListModel getList1() {
            return list1;
        }
        
        /** Setter for property list1.
         * @param list1 New value of property list1.
         *
         */
        public void setList1(ListModel list1) {
            if ( this.list1 != list1 ) {
                if ( this.list1 != null ) {
                    this.list1.removeListDataListener(this);
                }
            
                this.list1 = list1;
                
                if ( this.list1 != null ) {
                    this.list1.addListDataListener(this);
                }
            }
        }
        
        /** Getter for property list2.
         * @return Value of property list2.
         *
         */
        public ListModel getList2() {
            return list2;
        }
        
        /** Setter for property list2.
         * @param list2 New value of property list2.
         *
         */
        public void setList2(ListModel list2) {
            if ( this.list2 != list2 ) {
                if ( this.list2 != null ) {
                    this.list2.removeListDataListener(this);
                }
            
                this.list2 = list2;
                
                if ( this.list2 != null ) {
                    this.list2.addListDataListener(this);
                }
            }
        }
        
    }
    
}
