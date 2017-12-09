/*
 * IntegerParameter.java
 *
 * Created on September 24, 2007, 8:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.parameters;

import javax.swing.ComboBoxModel;

/**
 *
 * @author twalker
 */
public class ComboParameter extends Parameter<Object> {
    static final long serialVersionUID = 5123833225542707402L;
    
    transient private ComboBoxModel model;
    private Object[] options;
    
    /** Creates a new instance of IntegerParameter */
    public ComboParameter(String name, String key, String desc, boolean visible, boolean enabled, boolean required, Object defaultValue) {
        super(name,key,desc,visible,enabled,required,defaultValue);
    }

    public ComboBoxModel getModel() {
        return model;
    }

    public void setModel(ComboBoxModel model) {
        this.model = model;
    }

    public Object[] getOptions() {
        return options;
    }

    public void setOptions(Object[] options) {
        this.options = options;
    }
    
    public void setOption(String option, Object value) {
        if ( option.equals("OPTIONS") ) {
            setOptions( (Object[]) value);
        }
        else if ( option.equals("MODEL") ) {
            setModel( (ComboBoxModel) value);
        }
        else {
            super.setOption(option, value);
        }
    }
    
    
}
