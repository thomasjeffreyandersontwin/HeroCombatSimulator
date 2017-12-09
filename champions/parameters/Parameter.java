/*
 * Parameter.java
 *
 * Created on September 21, 2007, 11:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.parameters;

import champions.Dice;
import champions.exception.BadDiceException;
import champions.exceptionWizard.ExceptionWizard;
import champions.parameterEditor.AbstractParameterEditor;
import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author twalker
 */
public class Parameter<T> implements Serializable, Cloneable {
    
    static final long serialVersionUID = -5436012316130721853L;
    /*
            String name = parent.getIndexedStringValue(pindex, "parameter", "NAME");
            String key = parent.getIndexedStringValue(pindex, "parameter", "KEY");
            String type = parent.getIndexedStringValue(pindex, "parameter", "TYPE");
            String desc = parent.getIndexedStringValue(pindex, "parameter", "DESCRIPTION");
            String visible = parent.getIndexedStringValue(pindex, "parameter", "VISIBLE");
            String enabled = parent.getIndexedStringValue(pindex, "parameter", "ENABLED");
            String required = parent.getIndexedStringValue(pindex, "parameter", "REQUIRED");
            Object defaultValue = parent.getIndexedValue(pindex, "parameter", "DEFAULT");
     */
    public  String name;
    public String key;
    public String description;
  //  private String type;
    public boolean visibleByDefault;
    public boolean enabledByDefault;
    public boolean required;
    public int minimumWidth = 0;
    public T defaultValue;
    public String classVariable;
    
    
   // protected HashMap<String,Object> options = null;
    
    protected Parameter() {
        
    }
    
    protected Parameter(String name, String key, String desc, boolean visible, boolean enabled, boolean required, Object defaultValue) {
        setName(name);
        setKey(key);
        setDescription(desc);
        setVisibleByDefault(visible);
        setEnabledByDefault(enabled);
        setRequired(required);
        setDefaultValue(defaultValue);
    }
    
    /** Creates a new instance of Parameter */
    public static Parameter createParameter(String name, String key, String desc, String type, boolean visible, boolean enabled, boolean required, Object defaultValue) {
        Parameter p;
        if ( type.equals("INTEGER")) {
            p = new IntegerParameter(name,key,desc,visible,enabled,required,defaultValue);
        }
        else if ( type.equals("DOUBLE")) {
            p = new DoubleParameter(name,key,desc,visible,enabled,required,defaultValue);
        }
        else if ( type.equals("DICE")) {
            p = new DiceParameter(name,key,desc,visible,enabled,required,defaultValue);
        }
        else if ( type.equals("STRING")) {
            p = new StringParameter(name,key,desc,visible,enabled,required,defaultValue);
        }
        else if ( type.equals("COMBO")) {
            p = new ComboParameter(name,key,desc,visible,enabled,required,defaultValue);
        }
        else if ( type.equals("DICEVALUE")) {
            p = new ComboParameter(name,key,desc,visible,enabled,required,defaultValue);
        }
        else if ( type.equals("BOOLEAN")) {
            p = new BooleanParameter(name,key,desc,visible,enabled,required,defaultValue);
        }
        else if ( type.equals("LIST")) {
            p = new ListParameter(name,key,desc,visible,enabled,required,defaultValue);
        }
        else if ( type.equals("MUTABLELIST")) {
            p = new MutableListParameter(name,key,desc,visible,enabled,required,defaultValue);
        }
        else {
            p = new Parameter();
            p.setName(name);
            p.setKey(key);
            p.setDescription(desc);
            p.setVisibleByDefault(visible);
            p.setEnabledByDefault(enabled);
            p.setRequired(required);
            p.setDefaultValue(defaultValue);
        }
        
        return p;
    }
    
    public static Parameter createParameter(String name, String key, String desc, String type, boolean visible, boolean enabled, boolean required, Object defaultValue, Object[] options) {
        Parameter p = createParameter(name,key,desc,type,visible,enabled,required,defaultValue);
        
        for(int j=0;j+1<options.length;j+=2) {
            String option = (String)options[j];
            Object optionValue = options[j+1];
            p.setOption(option, optionValue);
        }
        
        return p;
    }
    
    
    public static Parameter createParameter(Object[] args) {
         if ( args.length < 9 || args.length % 2 != 1) {
            throw new IllegalArgumentException( "Error creating parameter from " + args);
        }
         
         //{"Strength","Power.STRENGTH", Integer.class, new Integer(0), "Additional Strength", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(0)},
     
         //{"Levels","Advantage#.LEVELS", Integer.class, new Integer(1), "Levels", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}
         Parameter p = createParameter((String)args[0], (String)args[1], (String)args[4], (String)args[5], args[6].equals("TRUE"), args[7].equals("TRUE"), args[8].equals("TRUE"), args[3]);

        if ( args.length > 9 ) {
            for(int j=9;j+1<args.length;j+=2) {
                String option = (String)args[j];
                Object optionValue = args[j+1];
                p.setOption(option, optionValue);
            }
        }
        
        return p;
    }
    
    /** Merges the values based upon the type of the parameter.
     *
     */
    public T mergeValues(T childValue, T parentValue) {
        if ( parentValue != null ) {
            //String type = getIndexedStringValue(index, "parameter", "TYPE");
            if ( childValue == null ) {
                childValue = parentValue;
            }
        }
        
        return (T) childValue;
    }
    
    /** Returns the difference between the Parent value and the child value.
     *
     */
    public Difference<T> getValueDifference(T targetValue, T parentValue) {
        T differenceValue = null;
        boolean requiresOverride = false;
        //String type = getIndexedStringValue(index, "parameter", "TYPE");
                
        if ( parentValue == null || parentValue.equals(targetValue) == false ) {
            requiresOverride = true;
            differenceValue = (T) targetValue;
        }
        
        return new Difference<T>(differenceValue, requiresOverride);
    }
    
    public void setOption(String option, Object value) {
        if ( option.equals("FIELDWIDTH") ) {
            setMinimumWidth((Integer)value);
        }
        else if ( option.equals("ClassVariable") ) {
            setClassVariable((String)value);
        }
        else {
            //if ( options == null ) options = new HashMap<String,Object>();

            //options.put(option, value);
            throw new IllegalArgumentException("Option " + option + " unsupported!");
        }
    }
    
    public Object getOption(String option) {
        //if ( options == null ) return null;
       // if ( options == null ) throw new IndexOutOfBoundsException("Option " + option + " not found.");
        
       // Object o = options.get(option);
       // if ( o == null ) {
       //     throw new IndexOutOfBoundsException("Option " + option + " not found.");
       // }
        return null;
    }
    
    public void removeOption(String option) {
       // if ( options != null ) {
       //     options.remove(option);
       // }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = getCanonicalValue(defaultValue);
    }

    public boolean isVisibleByDefault() {
        return visibleByDefault;
    }

    public void setVisibleByDefault(boolean visibleByDefault) {
        this.visibleByDefault = visibleByDefault;
    }

    public boolean isEnabledByDefault() {
        return enabledByDefault;
    }

    public void setEnabledByDefault(boolean enabledByDefault) {
        this.enabledByDefault = enabledByDefault;
    }
    
    public static class Difference<T> {
        T newValue;
        boolean requiresOverride;
        
        public Difference(T newValue, boolean override) {
            this.newValue = newValue;
            requiresOverride = override;
        }
    }
    
    public boolean isIndexed() {
        return key.indexOf("*") != -1;
    }

    public int getMinimumWidth() {
        return minimumWidth;
    }

    public void setMinimumWidth(int width) {
        this.minimumWidth = width;
    }

    public String getClassVariable() {
        return classVariable;
    }

    public void setClassVariable(String classVariable) {
        this.classVariable = classVariable;
    }
    
    public T getCanonicalValue(Object value) {
        return (T)value;
    }

    @Override
    public Parameter<T> clone() {
        try {
            return (Parameter<T>) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Parameter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
}
