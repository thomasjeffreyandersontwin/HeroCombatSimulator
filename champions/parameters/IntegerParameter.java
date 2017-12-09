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
public class IntegerParameter extends Parameter<Integer> {
    static final long serialVersionUID = 5193833222342707402L;
    
    private int minimum = Integer.MIN_VALUE;
    private int maximum = Integer.MAX_VALUE;
    private int increment = 1;
    
    /** Creates a new instance of IntegerParameter */
    public IntegerParameter(String name, String key, String desc, boolean visible, boolean enabled, boolean required, Object defaultValue) {
        super(name,key,desc,visible,enabled,required,defaultValue);
    }
    
    /** Merges the values based upon the type of the parameter.
     *
     */
    public Integer mergeValues(Integer childValue, Integer parentValue) {
        if ( parentValue != null ) {
            if ( childValue == null ) {
                childValue = parentValue;
            } else {
                childValue = (Integer)new Integer( ((Integer)childValue) + ((Integer)parentValue) );
            } 
        }
        
        return (Integer) childValue;
    }
    
        /** Returns the difference between the Parent value and the child value.
     *
     */
    public Difference<Integer> getValueDifference(Integer targetValue, Integer parentValue) {
        Integer differenceValue = null;
        boolean requiresOverride = false;

        Integer rv = new Integer( targetValue.intValue() - parentValue.intValue() );
        differenceValue = rv;
            
       return new Parameter.Difference<Integer>(differenceValue, requiresOverride);
    }
    
    public void setOption(String option, Object value) {
        if ( option.equals("MINIMUM") ) {
            setMinimum( (Integer) value);
        }
        else if ( option.equals("MAXIMUM") ) {
            setMaximum( (Integer) value);
        }
        else if ( option.equals("INCREMENT") ) {
            setIncrement( (Integer) value);
        }
        else {
            super.setOption(option, value);
        }
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }
}
