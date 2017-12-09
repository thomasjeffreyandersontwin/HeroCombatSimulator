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
public class DiceParameter extends Parameter<String> {
    static final long serialVersionUID = 5123833222342707123L;
    
    /** Creates a new instance of IntegerParameter */
    public DiceParameter(String name, String key, String desc, boolean visible, boolean enabled, boolean required, Object defaultValue) {
        super(name,key,desc,visible,enabled,required,defaultValue);
    }
    
    /** Merges the values based upon the type of the parameter.
     *
     */
    public String mergeValues(String childValue, String parentValue) {
        if ( parentValue != null ) {
            //String type = getIndexedStringValue(index, "parameter", "TYPE");
            if ( childValue == null ) {
                childValue = parentValue;
            } else {
                boolean plus = true;
                if ( ((String)childValue).startsWith("-") ) {
                    plus=false;
                    childValue = (String) ((String)childValue).substring(1);
                }

                try {
                    Dice d1 = new Dice((String)childValue, false);
                    Dice d2 = new Dice((String)parentValue, false);
                    int thirds1 = d1.getSizeInThirds();
                    int thirds2 = d2.getSizeInThirds();

                    Dice d3 = new Dice(0, false);
                    d3.setSizeInThrids( thirds2 + thirds1 * (plus?1:-1) );
                    childValue = (String) d3.toString();
                } catch ( BadDiceException bde ) {
                    ExceptionWizard.postException(bde);
                }
            }
        }
        
        return (String) childValue;
    }
    
    /** Returns the difference between the Parent value and the child value.
     *
     */
    public Difference<String> getValueDifference(String targetValue, String parentValue) {
        String differenceValue = null;
        boolean requiresOverride = false;

        try {
            Dice vd = new Dice((String)targetValue, false);
            Dice d2 = new Dice((String)parentValue, false);
            int thirds1 = vd.getSizeInThirds();
            int thirds2 = d2.getSizeInThirds();

            Dice d3 = new Dice(0, false);
            d3.setSizeInThrids( Math.abs(thirds1 - thirds2) );
            String rv = d3.toString();
            if ( thirds1 - thirds2 < 0 ) {
                rv = "-" + rv;
            }
            differenceValue = rv;
            //valueChanged = true;
        } catch ( BadDiceException bde ) {
            ExceptionWizard.postException(bde);
        }
            
       return new Parameter.Difference<String>(differenceValue, requiresOverride);
    }
    
}
