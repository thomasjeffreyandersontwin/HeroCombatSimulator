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
public class StringParameter extends Parameter<String> {
    
    /** Creates a new instance of IntegerParameter */
    public StringParameter(String name, String key, String desc, boolean visible, boolean enabled, boolean required, Object defaultValue) {
        super(name,key,desc,visible,enabled,required,defaultValue);
    }

}
