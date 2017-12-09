/*
 * IntegerParameter.java
 *
 * Created on September 24, 2007, 8:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.parameters;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ListModel;

/**
 *
 * @author twalker
 */
public class ListParameter extends Parameter {
    static final long serialVersionUID = 5123812222342707402L;
    
    transient private ListModel model;
    protected Object[] options;
    /** Creates a new instance of IntegerParameter */
    public ListParameter(String name, String key, String desc, boolean visible, boolean enabled, boolean required, Object defaultValue) {
        super(name,key,desc,visible,enabled,required,null);
        
        if ( defaultValue != null ) {
            if ( defaultValue instanceof List ) {
                setDefaultValue(defaultValue);
            }
            else {
                List list = new ArrayList();
                list.add(defaultValue);
                setDefaultValue(list);
            }
        }
    }
    
    public void setOption(String option, Object value) {
        if ( option.equals("MODEL") ) {
            setModel( (ListModel) value);
        } else if ( option.equals("OPTIONS") ) {
            setOptions( (Object[]) value);
        } else {
            super.setOption(option, value);
        }
    }
    public ListModel getModel() {
        return model;
    }
    
    public void setModel(ListModel model) {
        this.model = model;
    }
    
    public Object[] getOptions() {
        return options;
    }
    
    public void setOptions(Object[] options) {
        this.options = options;
    }

    @Override
    public ListParameter clone() {
        ListParameter p = (ListParameter) super.clone();
        p.model = null;
        return p;
    }


}
