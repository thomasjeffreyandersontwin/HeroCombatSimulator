/*
 * IntegerParameter.java
 *
 * Created on September 24, 2007, 8:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.parameters;

/**
 *
 * @author twalker
 */
public class BooleanParameter extends Parameter<Boolean> {
    static final long serialVersionUID = 5123833789342707402L;
    
    /** Creates a new instance of IntegerParameter */
    public BooleanParameter(String name, String key, String desc, boolean visible, boolean enabled, boolean required, Object defaultValue) {
        super(name,key,desc,visible,enabled,required,defaultValue);
        
        if ( defaultValue instanceof String ) {
            defaultValue = new Boolean( "TRUE".equals(defaultValue));
        }
    }
    
    public Boolean getCanonicalValue(Object value) {
        if ( value instanceof String ) {
            return value.equals("TRUE");
        }
        else {
            return (Boolean)value;
        }
    }

}
