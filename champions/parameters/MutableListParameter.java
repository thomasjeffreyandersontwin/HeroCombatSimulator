/*
 * IntegerParameter.java
 *
 * Created on September 24, 2007, 8:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.parameters;

import champions.interfaces.MutableListModel;
import java.awt.event.ActionListener;

/**
 *
 * @author twalker
 */
public class MutableListParameter extends ListParameter {
    static final long serialVersionUID = 5123833332342707402L;
    
    transient private MutableListModel model;
    transient private ActionListener actionListener1;
    transient private ActionListener actionListener2;
    private String button1Name;
    private String button2Name;
    
  //  private Object[] options;
    
    /** Creates a new instance of IntegerParameter */
    public MutableListParameter(String name, String key, String desc, boolean visible, boolean enabled, boolean required, Object defaultValue) {
        super(name,key,desc,visible,enabled,required,null);
        
//        if ( defaultValue != null ) {
//            if ( defaultValue instanceof List ) {
//                setDefaultValue(defaultValue);
//            }
//            else {
//                List list = new ArrayList();
//                list.add(defaultValue);
//                setDefaultValue(list);
//            }
//        }
    }
    
    public void setOption(String option, Object value) {
        if ( option.equals("MUTABLEMODEL") ) {
            setModel( (MutableListModel) value);
        }
        else if ( option.equals("ACTIONLISTENER1") ) {
            setActionListener1( (ActionListener) value);
        }
        else if ( option.equals("ACTIONLISTENER2") ) {
            setActionListener2( (ActionListener) value);
        }
        else if ( option.equals("BUTTON1") ) {
            setButton1Name( (String) value);
        }
        else if ( option.equals("BUTTON2") ) {
            setButton2Name( (String) value);
        }
        else if ( option.equals("OPTIONS") ) {
            setOptions( (Object[]) value);
        }
        else {
            super.setOption(option, value);
        }
    }

    public ActionListener getActionListener1() {
        return actionListener1;
    }

    public void setActionListener1(ActionListener actionListener1) {
        this.actionListener1 = actionListener1;
    }

    @Override
    public MutableListModel getModel() {
        return model;
    }

    public void setModel(MutableListModel model) {
        this.model = model;
    }

//    public Object[] getOptions() {
//        return options;
//    }
//
//    public void setOptions(Object[] options) {
//        this.options = options;
//    }

    public ActionListener getActionListener2() {
        return actionListener2;
    }

    public void setActionListener2(ActionListener actionListener2) {
        this.actionListener2 = actionListener2;
    }

    public String getButton1Name() {
        return button1Name;
    }

    public void setButton1Name(String button1Name) {
        this.button1Name = button1Name;
    }

    public String getButton2Name() {
        return button2Name;
    }

    public void setButton2Name(String button2Name) {
        this.button2Name = button2Name;
    }

    @Override
    public MutableListParameter clone() {
        MutableListParameter p = (MutableListParameter) super.clone();
        p.model = null;
        p.actionListener1 = null;
        p.actionListener2 = null;
        return p;
    }
}
