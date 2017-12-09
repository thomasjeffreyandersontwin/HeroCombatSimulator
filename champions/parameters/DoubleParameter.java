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
public class DoubleParameter extends Parameter<Double> {
    static final long serialVersionUID = 5123833222123707402L;
    
    private double increment = 1.0;
    private double minimum = Double.NEGATIVE_INFINITY;
    private double maximum = Double.POSITIVE_INFINITY;
    
    /** Creates a new instance of IntegerParameter */
    public DoubleParameter(String name, String key, String desc, boolean visible, boolean enabled, boolean required, Object defaultValue) {
        super(name,key,desc,visible,enabled,required,defaultValue);
    }
    
    /** Merges the values based upon the type of the parameter.
     *
     */
    public Double mergeValues(Double childValue, Double parentValue) {
        if ( parentValue != null ) {
            if ( childValue == null ) {
                childValue = parentValue;
            } else {
                parentValue = (Double) new Double( ((Double)childValue) + ((Double)parentValue) );
            } 
        }
        
        return (Double) childValue;
    }
    
    /** Returns the difference between the Parent value and the child value.
     *
     */
    public Difference<Double> getValueDifference(Double targetValue, Double parentValue) {
        Double differenceValue = null;
        boolean requiresOverride = false;
        
        Double rv = new Double( ((Double)targetValue).doubleValue() - ((Double)parentValue).doubleValue() );
        differenceValue = rv;
        
        return new Parameter.Difference<Double>(differenceValue, requiresOverride);
    }

    public Double getIncrement() {
        return increment;
    }

    public void setIncrement(Double increment) {
        this.increment = increment;
    }
    
    public void setOption(String option, Object value) {
        if ( option.equals("MINIMUM") ) {
            if ( value instanceof Integer ) {
                setMinimum( (Integer) value);
            }
            else {
                setMinimum( (Double) value);
            }
        }
        else if ( option.equals("MAXIMUM") ) {
            if ( value instanceof Integer ) {
                setMaximum( (Integer) value);
            }
            else {
                setMaximum( (Double) value);
            }
        }
        else if ( option.equals("INCREMENT") ) {
            if ( value instanceof Integer ) {
                setIncrement( (Integer) value);
            }
            else {
                setIncrement( (Double) value);
            }
        }
        else {
            super.setOption(option, value);
        }
    }

    public double getMinimum() {
        return minimum;
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }

    public double getMaximum() {
        return maximum;
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }

    public void setIncrement(double increment) {
        this.increment = increment;
    }
}
