/*
 * IntegerParameter.java
 *
 * Created on September 24, 2007, 8:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.parameters;

import champions.Dice;
import champions.exception.BadDiceException;
import champions.exceptionWizard.ExceptionWizard;

/**
 *
 * @author twalker
 */
public class DiceValueParameter extends Parameter<String> {
    static final long serialVersionUID = 5123833225542707402L;
    
    private String size;
    /** Creates a new instance of IntegerParameter */
    public DiceValueParameter(String name, String key, String desc, boolean visible, boolean enabled, boolean required, Object defaultValue) {
        super(name,key,desc,visible,enabled,required,defaultValue);
    }
    
  

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
    
}
