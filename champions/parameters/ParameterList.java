
/*
 * ParameterList.java
 *
 * Created on October 13, 2000, 7:44 PM
 */

package champions.parameters;

import champions.DetailList;
import champions.ObjectDebugger;
import champions.exceptionWizard.ExceptionWizard;
import champions.interfaces.Debuggable;
import champions.parameterEditor.AbstractParameterEditor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.ComboBoxModel;
import javax.swing.SwingUtilities;


/**
 *
 * @author  unknown
 * @version
 */
public class ParameterList implements PropertyChangeListener, Serializable, Debuggable, Cloneable {
    static final long serialVersionUID = 5123833222342707402L;

    
    public ParameterList parent;
    public Map<String, ParameterEntry> parameters = new LinkedHashMap<String, ParameterEntry>();
    
    transient protected PropertyChangeSupport propertyChangeSupport;
    
    /** Creates new ParameterList */
    public ParameterList() {
    }
    
    /** Creates new ParameterList */
    public ParameterList(ParameterList parent) {
        setParent(parent);
        
        int count = parent.getParameterCount();
        for(int pindex = 0; pindex < count; pindex++) {
            Parameter p = parent.getParameter(pindex);
            boolean v = parent.isVisible(pindex);
            boolean e = parent.isParameterEnabled(pindex);
            
            ParameterEntry pe = createParameterEntry(p,v,e);
            
            parameters.put(p.getName(), pe);
        }
    }
    
    /** Creates new parameter array based on a parameterArray and detailList
     * The parameterArray should be in the following format:
     *
     * Object[][] parameterArray = {
     * {"Name","Value.KEY", Class, Default, "Description", TYPE, VISIBLE, ENABLED, NOTREQUIRED}
     * };
     *
     * KEY can either be a normal "Name.TYPE" or an indexed key "Name#.TYPE".
     * TYPE should be one of the PARAMETER type values from ChampionsConstants
     * VISIBLE, ENABLED, NOTREQUIRED should be either "TRUE" or "FALSE".  The convienence variables from ChampionsConstants
     * can also be used.
     * Class should be the class of the value.
     * Default should be the default value, and should be of type Class.
     *
     * Additional Options can also be appended to the Array, in the form of "OPTIONNAME", optionValue.
     * @param parameterArray array which specifies parameter information.
     * @param sourceList DetailList from which to get values.
     * @param fromIndex Index to use with any parameters that use the # key format.
     */
    public ParameterList(Object[][] parameterArray) {
        
        if ( parameterArray != null ) {
            String name, key, description, type, realKey;
            String visible, enabled, required, option;
            Object defaultValue, optionValue;
            Object value = null;
            int indexOf;
            
            int i,j, index;
            for(i = 0; i < parameterArray.length;i++) {
                Parameter p = Parameter.createParameter(parameterArray[i]);
                addParameter(p);
            }
        }
    }
    
    /** Creates new parameter array based on a parameterArray and detailList
     * The parameterArray should be in the following format:
     *
     * Object[][] parameterArray = {
     * {"Name","Value.KEY", Class, Default, "Description", TYPE, VISIBLE, ENABLED, NOTREQUIRED}
     * };
     *
     * KEY can either be a normal "Name.TYPE" or an indexed key "Name#.TYPE".
     * TYPE should be one of the PARAMETER type values from ChampionsConstants
     * VISIBLE, ENABLED, NOTREQUIRED should be either "TRUE" or "FALSE".  The convienence variables from ChampionsConstants
     * can also be used.
     * Class should be the class of the value.
     * Default should be the default value, and should be of type Class.
     *
     * Additional Options can also be appended to the Array, in the form of "OPTIONNAME", optionValue.
     * @param parameterArray array which specifies parameter information.
     * @param sourceList DetailList from which to get values.
     * @param fromIndex Index to use with any parameters that use the # key format.
     */
    public ParameterList(Object[][] parameterArray, DetailList sourceList, int fromIndex) {
        this(parameterArray);
        
        extractParameterValuesFromDetailList(sourceList, fromIndex);
    }
    
    public ParameterList(Object[][] parameterArray, DetailList sourceList) {
        this(parameterArray, sourceList, -1);
    }
    
    public void addParameter(Parameter parameter) {
        if ( parameter != null ) {
            ParameterEntry pp = createParameterEntry(parameter, parameter.isVisibleByDefault(), parameter.isEnabledByDefault() );
            parameters.put(parameter.getName(), pp);
        }
    }
    
    
    
    protected void extractParameterValuesFromDetailList(DetailList sourceList, int fromIndex) {
        int indexOf;
        String key, realKey, realType;
        
        //throw new UnsupportedOperationException();
        for(ParameterEntry pp : parameters.values()) {
            Parameter p = pp.parameter;
            
            key = p.getKey();
            
            if ( ( indexOf = key.indexOf("*")) != -1 ) {
                // This is a list parameter.
                // Suck in to whole list.
                if ( sourceList != null ) {
                    int sourceIndex, sourceCount, destCount;
                    
                    realKey = key.substring(0,indexOf );
                    realType = key.substring(indexOf+2);
                    
                    sourceCount = sourceList.getIndexedSize(realKey);
                    List list = getIndexedParameterValues(p.getName());
                    list.clear();
                    //int destCount = this.getIndexedSize(realKey);
                    for(sourceIndex=0;sourceIndex<sourceCount;sourceIndex++) {
                        Object value = sourceList.getIndexedValue(sourceIndex, realKey, realType);
                        list.add(value);
                    }
                    
//                    while(sourceCount < destCount) {
//                        removeAllIndexed(sourceCount, realKey, false);
//                        destCount--;
//                    }
                    
//                    if ( sourceCount == 0 ) {
//                        addIndexed( index, "parameter", "SET", "FALSE" , true, false);
//                    } else {
//                        addIndexed( index, "parameter", "SET", "TRUE" , true, false);
//                    }
                }
            } else {
                // Handle the normal parameter
                Object value = null;
                if ( key.indexOf("#") != -1 ) {
                    // This is an indexed Value
                    if ( fromIndex == -1 || sourceList == null) {
                        value = null;
                    } else {
                        realKey = key.substring(0, key.indexOf("#") ) + Integer.toString(fromIndex) + "." + key.substring( key.indexOf(".")+1 );
                        value = sourceList.getValue(realKey);
                    }
                } else if ( sourceList != null ) {
                    value = sourceList.getValue( key );
                }
                
                if ( value == null ) {
                    //addIndexed( index, "parameter", "VALUE", defaultValue , true, false);
                    //addIndexed( index, "parameter", "SET", "FALSE" , true, false);
                    pp.value = p.getDefaultValue();
                } else {
                    //addIndexed( index, "parameter", "VALUE", value , true, false);
                    //addIndexed( index, "parameter", "SET", "TRUE" , true, false);
                    pp.value = value;
                }
            }
        }
    }
    
    public ComboParameter addComboParameter(String name, String key, String desc, Object value, ComboBoxModel model) {
        ComboParameter p = new ComboParameter(name, key, desc, true, true, true, value);
        p.setModel(model);
        addParameter(p);
        return p;
    }
    
    public ComboParameter addComboParameter(String name, String key, String desc, Object value, Object[] options) {
        ComboParameter p = new ComboParameter(name, key, desc, true, true, true, value);
        p.setOptions(options);
        addParameter(p);
        return p;
    }
    
    public IntegerParameter addIntegerParameter(String name, String key, String desc, Integer value) {
        IntegerParameter p = new IntegerParameter(name, key, desc, true, true, true, value);
        addParameter(p);
        return p;
    }
    
    public IntegerParameter addIntegerParameter(String name, String key, String desc, Integer value, Integer increment) {
        IntegerParameter p = new IntegerParameter(name, key, desc, true, true, true, value);
        p.setIncrement(increment);
        addParameter(p);
        return p;
    }
    
    
    
    public DoubleParameter addDoubleParameter(String name, String key, String desc, Double value, Double increment) {
        DoubleParameter p = new DoubleParameter(name, key, desc, true, true, true, value);
        p.setIncrement(increment);
        addParameter(p);
        return p;
    }
    
    public StringParameter addStringParameter(String name, String key, String desc, String value) {
        StringParameter p = new StringParameter(name, key, desc, true, true, true, value);
        addParameter(p);
        return p;
    }
    
    public FileParameter addFileParameter(String name, String key, String desc, String value, boolean file) {
        FileParameter p = new FileParameter(name, key, desc, true, true, true, value);
        p.setFile(file);
        addParameter(p);
        return p;
    }
    
    public DiceParameter addDiceParameter(String name, String key, String desc, String value) {
        DiceParameter p = new DiceParameter(name, key, desc, true, true, true, value);
        addParameter(p);
        return p;
    }
    
    public BooleanParameter addBooleanParameter(String name, String key, String desc, boolean value) {
        BooleanParameter p = new BooleanParameter(name, key, desc, true, true, true, value);
        addParameter(p);
        return p;
    }
    
    public Parameter addObjectParameter(String name, String key, String desc, String value) {
        Parameter p = new Parameter(name, key, desc, true, true, true, value);
        addParameter(p);
        return p;
    }
    
    public boolean contains(String parameterName) {
        return parameters.keySet().contains(parameterName);
    }
    
    public void setPanel(String parameter, String panel) {
        
        throw new UnsupportedOperationException();
//        int i;
//        if ( ( i = findIndexed( "parameter","NAME", parameter )) != -1 ) {
//            addIndexed( i,  "parameter","PANEL",panel,true,false);
//
//            if ( findIndexed( "Panel","NAME",panel ) == -1 ) {
//                createIndexed( "Panel","NAME",panel);
//            }
//        }
    }
    
    protected ParameterEntry getParameterEntry(String name) {
        ParameterEntry pp = parameters.get(name);
        if ( pp == null ) {
            throw new IndexOutOfBoundsException("Parameter " + name + " not found.");
        }
        return pp;
    }
    
    protected ParameterEntry getParameterEntry(int index) {
        // Uggg....
        int i = 0;
        for(ParameterEntry p : parameters.values()) {
            if ( i++ == index ) return p;
        }
        return null;
    }
    
    public Parameter getParameter(String name) {
        ParameterEntry pp = parameters.get(name);
        if ( pp != null ) return pp.parameter;
        else return null;
    }
    
//    public void setEnabled(String name, String enabled) {
//        //return getParameterEntry(name).enabled;
//    }
    
    public void setEnabled(String name, boolean enabled) {
        getParameterEntry(name).enabled = enabled;
    }
    
    public void setHelpText(String name, String helpText) {
        getParameter(name).setOption("HELPTEXT", helpText);
    }
    
    public boolean isParameterEnabled(String parameter) {
        return getParameterEntry(parameter).enabled;
    }
    
    public boolean isParameterEnabled(int index) {
        return getParameterEntry(index).enabled;
    }
    
    public Object getParameterValue(String parameter) {
        Object rv = null;
        Object pv = null;
        
        ParameterEntry pp = getParameterEntry(parameter);
        //int index = findIndexed( "parameter","NAME",parameter);
        //if ( index != -1 ) {
        if ( pp != null ) {
            rv = pp.value;
            //rv = getIndexedValue(index,"parameter","VALUE");
            if ( getParent() != null ) {
                if ( pp.overrideParent == false ) {
                    pv = getParent().getParameterValue(parameter);
                    
                    rv = pp.parameter.mergeValues(rv,pv);
                }
            }
            
            if ( rv == null ) {
                rv = pp.parameter.getDefaultValue();
            }
        }
        return rv;
    }
    
    public String getParameterStringValue(String parameter) {
        return (String) getParameterValue(parameter);
    }
    
    public boolean getParameterBooleanValue(String parameter) {
        return (Boolean)getParameterValue(parameter);
    }
    
    public int getParameterIntValue(String parameter) {
        Integer i = (Integer) getParameterValue(parameter);
        return (i==null)?0:i.intValue();
    }
    
    public boolean isOverRideParent(String parameter) {
        if ( getParent() == null ) return true;
        //int index = findIndexed( "parameter","NAME",parameter);
        //if ( index != -1 ) {
        //    return getIndexedBooleanValue(index, "parameter", "OVERRIDEPARENT");
        //}
        return getParameterEntry(parameter).overrideParent;
    }
    
    public boolean isOverRideParent(int index) {
        if ( getParent() == null ) return true;
        return getParameterEntry(index).overrideParent;
        //return getIndexedBooleanValue(index, "parameter", "OVERRIDEPARENT");
        
    }
    
    public void setOverRideParent(String parameter, boolean override) {
//        int index = findIndexed( "parameter","NAME",parameter);
//        if ( index != -1 ) {
//            setOverRideParent(index, override);
//        }
        getParameterEntry(parameter).overrideParent = override;
    }
    
    public void setOverRideParent(int index, boolean override) {
        //addIndexed(index,"parameter", "OVERRIDEPARENT", override ? "TRUE" : "FALSE", true);
        getParameterEntry(index).overrideParent = override;
    }
    /**
     *
     * @deprecated
     */
    public int getParameterIndex(String parameter) {
        //return findIndexed( "parameter","NAME",parameter);
        int i = 0;
        for(String name : parameters.keySet()) {
            if ( name.equals(parameter) ) return i;
            i++;
        }
        return -1;
    }
    
    /** Gets the parameter value, except when the parameter key == overrideKey, in which case overrideValue is returned.
     *
     */
    public Object getParameterValue(String parameter, String overrideKey, Object overrideValue) {
        Object rv = null;
        //int index = findIndexed( "parameter","NAME",parameter);
        ParameterEntry pp = getParameterEntry(parameter);
        //if ( index != -1 ) {
        if ( pp != null ) {
            //String key = getIndexedStringValue(index, "parameter", "KEY");
            String key = pp.parameter.getKey();
            
            if ( overrideKey != null && overrideKey.equals(key)) {
                rv = overrideValue;
            } else {
                rv = getParameterValue(parameter);
            }
            
            if ( rv == null ) {
                //rv = getIndexedValue( index, "parameter", "DEFAULT" );
                rv = pp.parameter.getDefaultValue();
            }
        }
        return rv;
    }
    
    public String getParameterDescription(String parameter) {
//        String rv = null;
//        int index = findIndexed( "parameter","NAME",parameter);
//        if ( index != -1 ) {
//            rv = getParameterDescription(index);
//        }
//        return rv;
        return getParameterEntry(parameter).parameter.getDescription();
    }
    
    public String getParameterDescription(int index) {
        //return getIndexedStringValue(index,"parameter","DESCRIPTION");
        return getParameterEntry(index).parameter.getDescription();
    }
    
    public String getParameterKey(String parameter) {
//        String rv = null;
//        int index = findIndexed( "parameter","NAME",parameter);
//        if ( index != -1 ) {
//            rv = getParameterKey(index);
//        }
//        return rv;
        return getParameterEntry(parameter).parameter.getKey();
    }
    
    public String getParameterKey(int index) {
        //return getIndexedStringValue(index,"parameter","KEY");
        return getParameterEntry(index).parameter.getKey();
    }
    
    public int getParameterCount() {
        return parameters.size();
    }
    
    public Parameter getParameter(int index) {
        return getParameterEntry(index).parameter;
    }
    
    public void setParameterValue(String parameter, Object value) {
        Object newValue = null;
        Object oldValue = getParameterValue(parameter);
        
        ParameterEntry pp = getParameterEntry(parameter);
        // Check to see that all values going in are sane...
        value = pp.parameter.getCanonicalValue(value);
        
        if ( oldValue != value && ( value == null || value.equals(oldValue) == false) ) {
            //int index = findIndexed( "parameter","NAME",parameterName);
            
            if ( pp.indexed == true && value instanceof List == false) throw new IllegalArgumentException("Parameter " + parameter + " is indexed and new value = '" + value + "' is not a list.");
            //if ( index != -1 ) {
            if ( pp != null ) {
                //ParameterList parent = getParent();
                if ( getParent() == null || pp.overrideParent ) {
                    newValue = value;
                } else if ( getParent().getParameterValue(parameter) == null) {
                    //setOverRideParent(index,true);
                    pp.overrideParent = true;
                    newValue = value;
                } else {
                    Object pv = parent.getParameterValue(parameter);
                    
                    Parameter.Difference d = pp.parameter.getValueDifference(value, pv);
                    
                    newValue = d.newValue;
                    if ( d.requiresOverride == true ) pp.overrideParent = true;
                    //  fireIndexedChanged("parameter");
                }
                
                pp.value = newValue;
                //pp.set = true;
                //addIndexed(index, "parameter", "VALUE", newValue, true, false);
                firePropertyChange(parameter + ".VALUE", oldValue, newValue);
                //setParameterSet(index, true);
                
            } else {
                throw new IllegalArgumentException("Parameter " + parameter + " not in parameter list.");
            }
        }
        
    }

    /** Clones the parameter list, seperating it from any parent parameter lists.
     * 
     * 
     * @return
     * @throws java.lang.CloneNotSupportedException
     */
    @Override
    public ParameterList clone()  {
        ParameterList that = new ParameterList();
        
        for(Entry<String,ParameterEntry> e : this.parameters.entrySet()) {
            ParameterEntry pe = e.getValue();
            String name = e.getKey();
            
            that.parameters.put(name, pe.clone());
            if ( isParameterIndexed(name) == false) {
                Object value = this.getParameterValue(name);
                that.setParameterValue(name, value);
            }
            else {
                int count = this.getIndexedParameterSize(name);
                for(int i = 0; i < count; i++ ) {
                    Object value = this.getIndexedParameterValue(name, i);
                    that.addIndexedParameterValue(name, value);
                }
            }
            
        }
        
        return that;
    }
    
    
    
//    public Object getParameterOption(String parameter, String option) {
////        Object rv = null;
////        int index = findIndexed( "parameter","NAME",parameter);
////        if ( index != -1 ) {
////            rv = getIndexedValue(index,"parameter",option);
////        }
////        return rv;
//        return getParameterEntry(parameter).parameter.getOption(option);
//    }
    
//    public String getParameterType(String parameter) {
//        int index = findIndexed( "parameter","NAME", parameter);
//        if ( index != -1 ) {
//            return getIndexedStringValue(index, "parameter", "TYPE");
//        }
//
//        return null;
//        return getParameterEntry(parameter).parameter.getType();
//    }
    
    
//    public void setParameterOption(String parameter, String option, Object value) {
////        int index = findIndexed( "parameter","NAME",parameter);
////        if ( index != -1 ) {
////            addIndexed(index,"parameter",option,value,true,false);
//////            if ( isValueChanging() == false) {
//////                fireIndexedChanged("parameter");
//////            }
//////            else {
//////                setValueChanged(true);
//////            }
////        }
//        getParameterEntry(parameter).parameter.setOption(option, value);
//    }
    
//    public void removeParameterOption(String parameter, String option) {
////        int index = findIndexed( "parameter","NAME",parameter);
////        if ( index != -1 ) {
////            removeIndexed(index, "parameter", option);
////        }
//        getParameterEntry(parameter).parameter.removeOption(option);
//    }
    
    public void setParameterEnabled(String parameter, boolean enabled) {
//        int index = findIndexed( "parameter","NAME",parameter);
//        if ( index != -1 ) {
//            addIndexed(index,"parameter","ENABLED",enabled?"TRUE":"FALSE",true,true);
//        }
        getParameterEntry(parameter).enabled = enabled;
    }
    
//    public void isParameterEnabled(String parameter) {
//        int index = findIndexed( "parameter","NAME",parameter);
//        if ( index != -1 ) {
//            return getIndexedBooleanValue(index,"parameter","ENABLED");
//        }
//        return false;
//    }
    
    public void setVisible(String parameter, String visible) {
        //setVisible(parameter, visible.equals("TRUE"));
        getParameterEntry(parameter).visible = "TRUE".equals(visible);
    }
    
    public void setVisible(String parameter, boolean visible) {
        //int index = findIndexed( "parameter","NAME",parameter);
        ParameterEntry pp = getParameterEntry(parameter);
        
        if ( pp != null ) {
            boolean oldVisible = pp.visible;
            
            if ( oldVisible != visible) {
                //addIndexed( index,  "parameter","VISIBLE", visible ? "TRUE" : "FALSE" , true, false);
                pp.visible = visible;
                firePropertyChange(parameter + ".VISIBLE", new Boolean(oldVisible), new Boolean(visible));
            }
        }
    }
    
    public boolean isVisible(String parameter) {
        return getParameterEntry(parameter).visible;
    }
    
    public boolean isVisible(int index) {
        //return getIndexedBooleanValue(index,"parameter","VISIBLE");
        return getParameterEntry(index).visible;
    }
    
    public boolean isRequired(String parameter) {
        return getParameterEntry(parameter).parameter.isRequired();
    }
    
    public boolean isRequired(int index) {
        return getParameterEntry(index).parameter.isRequired();
    }
    
    public boolean isParameterSet(String parameter) {
//        int index = findIndexed( "parameter","NAME",parameter);
//        boolean rv = false;
//        if ( index != -1 ) {
//            rv = getIndexedBooleanValue(index,"parameter","SET");
//            if ( rv == false && isOverRideParent(parameter) == false ) {
//                rv = getParent().isParameterSet(parameter);
//            }
//        }
//        return rv;
        ParameterEntry pp = getParameterEntry(parameter);
        Object v = pp.value;
        return v != null || (pp.overrideParent == false && getParent() != null && getParent().isParameterSet(parameter) );
        
        
    }
    
    public boolean isParameterSet(int index) {
//        boolean rv = false;
//        if ( index != -1 ) {
//            rv = getIndexedBooleanValue(index,"parameter","SET");
//            if ( rv == false && isOverRideParent(getIndexedStringValue(index, "parameter", "NAME")) == false ) {
//                rv = getParent().isParameterSet( getIndexedStringValue(index, "parameter", "NAME"));
//            }
//        }
//        return rv;
        ParameterEntry pp = getParameterEntry(index);
        Object v = pp.value;
        if ( pp.indexed ) {
            return (v != null && ((List)v).size() > 0) || (pp.overrideParent == false && getParent() != null && getParent().isParameterSet(index) );
        }
        else {
           return v != null || (pp.overrideParent == false && getParent() != null && getParent().isParameterSet(index) ); 
        }
        
    }
    
    public void setParameterSet(String parameter, boolean set) {
//        int index = findIndexed( "parameter", "NAME", parameter);
//        if ( index != -1 ) {
//            setParameterSet(index,isSet);
//        }
    }
    
    private void setParameterSet(int index, boolean set) {
//        boolean oldValue = isParameterSet(index);
//        if ( oldValue != isSet) {
//            addIndexed(index, "parameter", "SET", isSet ? "TRUE" : "FALSE",true,false);
//            if ( isValueChanging() == false) {
//                firePropertyChange(this, getParameterName(index) + ".SET", oldValue ? "TRUE" : "FALSE", isSet ? "TRUE" : "FALSE");
//            } else {
//                setValueChanged(true);
//            }
//        }
        
        throw new UnsupportedOperationException();
        
    }
    
    private String getParameterName(int index) {
        //return getIndexedStringValue(index, "parameter", "NAME");
        return getParameterEntry(index).parameter.getName();
    }
    
    
    public int getParameterWidth(String parameter) {
//        Integer rv = null;
//        int index = findIndexed( "parameter","NAME",parameter);
//        if ( index != -1 ) {
//            rv = getIndexedIntegerValue(index,"parameter","FIELDWIDTH");
//        }
        int width = getParameterEntry(parameter).parameter.getMinimumWidth();
        return width;
        
    }
    
    public void setCustomEditor(String parameter, AbstractParameterEditor editor ) {
        getParameterEntry(parameter).customEditor = editor;
    }
    
    public AbstractParameterEditor getCustomEditor(String parameter) {
        return getParameterEntry(parameter).customEditor;
    }
    
    public int getIndexedParameterSize(String parameter) {
        ParameterEntry pp = getParameterEntry(parameter);
        if ( pp.overrideParent == false && getParent() != null) {
            return parent.getIndexedParameterSize(parameter);
        } else {
            List list = (List) pp.value;
            if ( list != null ) {
                return list.size();
            } else {
                return 0;
            }
        }
    }
    
    public Object getIndexedParameterValue(String parameter, int index) {
        ParameterEntry pp = getParameterEntry(parameter);
        if ( pp.overrideParent == false && getParent() != null) {
            return parent.getIndexedParameterValue(parameter, index);
        } else {
            List list = (List) pp.value;
            if ( list != null ) {
                return list.get(index);
            } else {
                throw new IndexOutOfBoundsException();
            }
        }
    }
    
    public List getIndexedParameterValues(String parameter) {
        ParameterEntry pp = getParameterEntry(parameter);
        if ( pp.overrideParent == false && getParent() != null) {
            return parent.getIndexedParameterValues(parameter);
        } else {
            List list = (List) pp.value;
            if ( list == null ) list = new ArrayList();
            return list;
        }
    }
    
    public int addIndexedParameterValue(String parameter, Object value) {
        ParameterEntry pp = getParameterEntry(parameter);
        if ( pp.overrideParent == false && getParent() != null) {
            //int index = parent.findIndexedParameterValue(parameter,value);
            List list = parent.getIndexedParameterValues(parameter);
            int index = -1;
            if ( list == null ) {
                pp.overrideParent = true;
                
                List newList = new ArrayList(list);
                newList.add(value);
                index = 0;
                
                pp.value = newList;
                
                firePropertyChange(parameter + ".VALUE", null, new Integer(index));
            } else {
                index = list.indexOf(value);
                if ( index == -1 ) {
                    // The parent doesn't have this value, so now we have to clone
                    // the parent list and override it...
                    pp.overrideParent = true;
                    
                    List newList = new ArrayList(list);
                    newList.add(value);
                    index = newList.size() - 1;
                    
                    pp.value = newList;
                    
                    firePropertyChange(parameter + ".VALUE", null, new Integer(index));
                }
            }
            
            return index;
        } else {
            List list = (List)pp.value;
            int index = -1;
            
            if ( list == null ) {
                list = new ArrayList();
                list.add(value);
                index = 0;
                pp.value = list;
                
                firePropertyChange(parameter + ".VALUE", null, new Integer(index));
            } else {
                index = list.indexOf(value);
                if ( index == -1 ) {
                    list.add(value);
                    index = list.size() - 1;
                    firePropertyChange(parameter + ".VALUE", null, new Integer(index));
                }
            }
            
            return index;
        }
        
    }
    
    /** Checks that all parameters are valid.
     *
     */
    public boolean isValid() {
        for(ParameterEntry pp : parameters.values()) {
            if ( pp.visible && pp.parameter.isRequired() && isParameterSet(pp.parameter.getName()) == false) {
                return false;
            } 
        }
        return true;
    }
    
    public int findIndexedParameterValue(String parameter, Object value) {
        ParameterEntry pp = getParameterEntry(parameter);
        if ( pp.overrideParent == false && getParent() != null) {
            return parent.findIndexedParameterValue(parameter,value);
        } else {
            List list = (List)pp.value;
            if ( list == null ) return -1;
            else return list.indexOf(value);
        }
    }
    
    public void removeIndexedParameterValue(String parameter, Object value) {
        ParameterEntry pp = getParameterEntry(parameter);
        if ( pp.overrideParent == false && getParent() != null) {
            int index = parent.findIndexedParameterValue(parameter,value);
            if ( index != -1) {
                pp.overrideParent = true;
                List list = parent.getIndexedParameterValues(parameter);
                list = new ArrayList(list);
                list.remove(value);
                pp.value = list;
                
                firePropertyChange(parameter + ".VALUE", new Integer(list.size() + 1), new Integer(list.size()));
            }
        } else {
            List list = (List)pp.value;
            if ( list.remove(value)  ) {
                firePropertyChange(parameter + ".VALUE", new Integer(list.size() + 1), new Integer(list.size()));
            }
        }
    }
    
    public void removeAllIndexedParameterValues(String parameter) {
        ParameterEntry pp = getParameterEntry(parameter);
        if ( pp.overrideParent == false && getParent() != null) {
            List list = getParent().getIndexedParameterValues(parameter);
            if ( list != null && list.size() > 0 ) {
                pp.overrideParent = true;
                List newList = new ArrayList();
                pp.value = newList;
                
                firePropertyChange(parameter + ".VALUE", new Integer(list.size()), new Integer(0));
            }
        } else {
            List list = (List)pp.value;
            if ( list != null && list.size() > 0 ) {
                int oldSize = list.size();
                list.clear();
                
                firePropertyChange(parameter + ".VALUE", new Integer(oldSize), new Integer(0));
            }
        }
    }
    
//    public String getIndexedParameterIndexName(String parameter) {
//        String indexName = null;
//        int pindex = findIndexed( "parameter","NAME",parameter);
//        if ( pindex != -1 ) {
//            String key = getIndexedStringValue(pindex, "parameter", "KEY");
//            int offset = key.indexOf("*");
//            if ( offset != -1 ) {
//                indexName = key.substring(0, offset);
//            }
//        }
//        return indexName;
//    }
    
//    private void cloneParentList(String parameter) {
//        int index;
//        int pindex = findIndexed( "parameter","NAME",parameter);
//        if ( pindex != -1 ) {
//            String key = getIndexedStringValue(pindex, "parameter", "KEY");
//            int offset = key.indexOf("*");
//            if ( offset != -1 ) {
//                String indexName = key.substring(0, offset);
//                String indexType = key.substring(offset+2);
//
//                ParameterList parent = getParent();
//                int size = parent.getIndexedParameterSize(parameter);
//                for(int i = 0; i < size; i++) {
//                    createIndexed(indexName, indexType, parent.getIndexedParameterValue(parameter, i), false);
//                }
//            }
//        }
//
//    }
    
//    public void clearSetValues() {
//        int index,count;
//        count = getIndexedSize("parameter");
//        for(index=0;index<count;index++) {
//            addIndexed(index, "parameter", "SET", "FALSE", true, false);
//        }
//        if ( isValueChanging() == false) {
//            fireIndexedChanged("parameter");
//        } else {
//            setValueChanged(true);
//        }
//    }
    
    public void copyValues(DetailList destinationList, int toIndex) {
        int index,count;
        String key, realKey, indexName, name;
        Object value;
        int indexLocation;
        
        //count = this.getIndexedSize("parameter");
        //for(index=0;index<count;index++) {
        for(ParameterEntry pp : parameters.values()) {
            //key = getIndexedStringValue(index, "parameter", "KEY");
            //name = getIndexedStringValue(index, "parameter", "NAME");
            key = pp.parameter.getKey();
            name = pp.parameter.getName();
            
            if ( ( indexLocation = key.indexOf("*")) != -1 ) {
                // This is a list parameter.
                // Suck in to whole list.
                String realType;
                int sourceIndex, sourceCount, destCount;
                
                realKey = key.substring(0,indexLocation );
                realType = key.substring(indexLocation+2);
                
                sourceCount = getIndexedParameterSize(name);
                destCount = destinationList.getIndexedSize(realKey);
                for(sourceIndex = 0; sourceIndex<sourceCount; sourceIndex++) {
                    value = getIndexedParameterValue(name, sourceIndex);
                    if ( destCount <= sourceIndex ) {
                        destinationList.createIndexed(realKey, realType, value, false);
                        destCount++;
                    } else {
                        destinationList.addIndexed(sourceIndex, realKey, realType, value, true, false);
                    }
                }
                
                while ( sourceCount < destCount ) {
                    destinationList.removeAllIndexed(sourceCount, realKey, false);
                    destCount --;
                }
                destinationList.fireIndexedChanged(realKey);
            } else {
                value = getParameterValue(name);
                if ( ( indexLocation = key.indexOf("#")) != -1 ) {
                    // This is an indexed Value
                    indexName = key.substring(0, indexLocation );
                    if ( toIndex < 0 || toIndex >= destinationList.getIndexedSize( indexName ) ) {
                        throw new ArrayIndexOutOfBoundsException(toIndex);
                    } else {
                        realKey =  indexName + Integer.toString(toIndex) + "." + key.substring( key.indexOf(".")+1 );
                        destinationList.add( realKey, value, true);
                    }
                } else {
                    destinationList.add( key, value, true);
                }
            }
        }
    }
    
    public void copyValues(DetailList destinationList) {
        copyValues(destinationList, -1 );
    }
    
    /** Returns the name of the parameter for the given key. */
    public String findParameterKey(String key) {
        for(ParameterEntry pp : parameters.values()) {
            if ( pp.parameter.getKey().equals(key) ) return pp.parameter.getName();
        }
        return null;
    }
    
    /** Getter for property valueChanging.
     * @return Value of property valueChanging.
     */
//    public boolean isValueChanging() {
//        return valueChanging;
//    }
    
    /** Setter for property valueChanging.
     * @param valueChanging New value of property valueChanging.
     */
//    public void setValueChanging(boolean valueChanging) {
//        if ( valueChanging != this.valueChanging ) {
//            this.valueChanging = valueChanging;
//            if ( valueChanging == true ) {
//                setValueChanged(false);
//            } else {
//                if ( isValueChanged() ) {
//                    fireIndexedChanged("parameter");
//                    setValueChanged(false);
//                }
//            }
//        }
//    }
    
    /** Getter for property valueChanged.
     * @return Value of property valueChanged.
     */
//    public boolean isValueChanged() {
//        return valueChanged;
//    }
    
    /** Setter for property valueChanged.
     * @param valueChanged New value of property valueChanged.
     */
//    public void setValueChanged(boolean valueChanged) {
//        this.valueChanged = valueChanged;
//    }
    
//    public void startMassChanges() {
//        setValueChanging(true);
//    }
    
//    public void endMassChanges() {
//        setValueChanging(false);
//    }
    
    /** Compares the Configuration of two parameter lists.
     *
     * @return true if parameters are configured the same, false otherwise.
     */
    public boolean compareConfiguration(ParameterList that) {
        throw new UnsupportedOperationException();
//        int count = getIndexedSize( "parameter" );
//        for(int index = 0; index < count; index++) {
//            String name = getIndexedStringValue(index, "parameter", "NAME");
//            if ( compareParameter( name, that ) == false ) return false;
//        }
//        return true;
    }
    
    /** Returns whether two parameters are equal.
     *
     * This method assumes that we are comparing similar parameter.  If the
     * parameters are configured differently (different types, keys, indexed type)
     * then the results are undefined.
     *
     * Specifically, in order this method to return true, the parameters must
     * have the same enabled state, and if enabled, have the same set setting
     * and value.
     */
    private boolean compareParameter(String parameterName, ParameterList that) {
        throw new UnsupportedOperationException();
        
//        int thisIndex = this.findIndexed("parameter","NAME",parameterName);
//        int thatIndex = that.findIndexed("parameter","NAME",parameterName);
//
//        // Compare Enable status
//        if ( isParameterEnabled(thisIndex) != that.isParameterEnabled(thatIndex) ) return false;
//
//        // Compare value if enabled...
//        if ( isParameterEnabled(thisIndex) ) {
//            if ( isParameterSet(thisIndex) != that.isParameterSet(thatIndex) )  return false;
//
//            if ( isParameterSet(thisIndex) ) {
//                if ( isParameterIndexed(thisIndex) ) {
//                    // Run through the indexed value and make sure the are the same...
//                    String key = getIndexedStringValue(thisIndex, "parameter", "KEY");
//                    int offset = key.indexOf("*");
//                    String indexName = key.substring(0, offset);
//                    String indexType = key.substring(offset+2);
//
//                    int thisCount = this.getIndexedSize(indexName);
//                    int thatCount = that.getIndexedSize(indexName);
//                    if ( thisCount != thatCount ) return false;
//
//                    for(int index = 0; index < thisCount; index++) {
//                        Object thisValue = getIndexedValue(index, indexName, indexType);
//                        Object thatValue = getIndexedValue(index, indexName, indexType);
//
//                        if ( thisValue == thatValue ) continue;
//                        if ( thisValue != null && thisValue.equals(thatValue) == false ) return false;
//                    }
//                } else {
//                    Object thisValue = getIndexedValue(thisIndex, "parameter", "VALUE");
//                    Object thatValue = getIndexedValue(thatIndex, "parameter", "VALUE");
//
//                    if ( thisValue != thatValue &&
//                            thisValue != null &&
//                            thisValue.equals(thatValue) == false ) return false;
//                }
//            }
//        }
//
//        return true;
    }
    
    /** Returns whether the parameter is an indexed parameter.
     *
     */
    public boolean isParameterIndexed(String parameter) {
        ParameterEntry pp = getParameterEntry(parameter);
        return pp.parameter.isIndexed();
    }
    
    /** Returns whether the parameter is an indexed parameter.
     *
     */
//    private boolean isParameterIndexed(int index) {
//        String key = getIndexedStringValue(index,"parameter","KEY");
//        return key.indexOf("*") != -1;
//    }
    
 /*   public void setSourceList(DetailList dl) {
        add("ParameterList.SOURCELIST",dl,true);
    }
  
    public DetailList getSourceList() {
        Object o = getValue("ParameterList.SOURCELIST");
        return ( o == null ) ? null : (DetailList)o;
    }
  
    public void setSourceObject(Object o) {
        add("ParameterList.SOURCEOBJECT",o,true);
    }
  
    public Object getSourceObject() {
        Object o = getValue("ParameterList.SOURCEOBJECT");
        return o;
    } */
    
//    protected void preWrite() {
//        int index = getIndexedSize("parameter") - 1;
//        for(; index >= 0; index--) {
//            Object editor = getIndexedValue(index, "parameter", "CUSTOMEDITOR");
//            if ( editor != null ) {
//                removeIndexed(index, "parameter", "CUSTOMEDITOR", false);
//            }
//        }
//
//        super.preWrite();
//    }
    
    /**
     * Getter for property parent.
     * @return Value of property parent.
     */
    public ParameterList getParent() {
        //return (ParameterList)getValue("ParameterList.PARENT");
        return this.parent;
    }
    
    /**
     * Setter for property parent.
     * @param parent New value of property parent.
     *
     * @todo Add a mergeToParent parameter when setting to non-null parent...
     */
    public void setParent(ParameterList parent) {
        if ( parent == this ) {
            ExceptionWizard.postException(new Exception("Attempt to set parameter list as it's own parent..."));
            return;
        }
        
        ParameterList oldParent = getParent();
        if ( oldParent != parent ) {
            if ( oldParent != null ) {
                oldParent.removePropertyChangeListener(this);
                seperateListFromParent();
            }
            
            //add("ParameterList.PARENT", parent, true, false);
            this.parent = parent;
            
            if ( parent != null ) {
                parent.addPropertyChangeListener(this);
            }
        }
    }
    
    protected void seperateListFromParent() {
        // For each parameter, get the parameter value and set it...
        for(ParameterEntry pp : parameters.values()) {
            String parameter = pp.parameter.getName();
            if ( pp.overrideParent == false ) {
                if ( isParameterIndexed(parameter) ) {
                    List list = parent.getIndexedParameterValues(parameter);
                    
                    if ( list != null ) {
                        List newList = new ArrayList(list);
                        pp.value = newList;
                    }
                } else {
                    Object value = parent.getParameterValue(parameter);
                    pp.value = value;
                }
                
            } else {
                Object value = getParameterValue(parameter);
                pp.value = value;
            }
            pp.overrideParent = true;
        }
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        int index = evt.getPropertyName().indexOf(".VALUE");
        if ( index != -1  ) {
            String parameter = evt.getPropertyName().substring(0, index);
            if ( isOverRideParent(parameter) == false ) {
                firePropertyChange(evt.getPropertyName(), evt.getOldValue(), getParameterValue(parameter));
            }
        }
    }
    
    public void firePropertyChange(final String property, final Object oldValue, final Object newValue) {
        if ( propertyChangeSupport != null ) {
            if ( ! SwingUtilities.isEventDispatchThread() ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        firePropertyChange(property, oldValue, newValue);
                    }
                });
            } else {
                propertyChangeSupport.firePropertyChange(property, oldValue, newValue);
            }
        }
    }
    
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        if ( propertyChangeSupport != null ) {
            propertyChangeSupport.removePropertyChangeListener(pcl);
        }
    }
    
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        if ( propertyChangeSupport == null ) propertyChangeSupport = new PropertyChangeSupport(this);
        
        propertyChangeSupport.addPropertyChangeListener(pcl);
    }
    
    public void removePropertyChangeListener(String property, PropertyChangeListener pcl) {
        if ( propertyChangeSupport != null ) {
            propertyChangeSupport.removePropertyChangeListener(property, pcl);
        }
    }
    
    public void addPropertyChangeListener(String property, PropertyChangeListener pcl) {
        if ( propertyChangeSupport == null ) propertyChangeSupport = new PropertyChangeSupport(this);
        
        propertyChangeSupport.addPropertyChangeListener(property, pcl);
    }
    
    public Iterator<Parameter> getParameters() {
        return new ParameterIterator(parameters.values().iterator());
    }
    
    protected ParameterEntry createParameterEntry(Parameter parameter, boolean visible, boolean enabled) {
        return new ParameterEntry(parameter, visible, enabled);
    }

    public void displayDebugWindow() {
        ObjectDebugger.displayDebugWindow(null, this);
    }

    public String toDebugString() {
        return toString();
    }
    
    public boolean isRelated(ParameterList that) {
        return this == that || this.isDecendent(that) || this.isAncestor(that);
    }
    
    /**
     * Returns true if <CODE>that</CODE> is a decendent of <CODE>this</CODE>.
     * @param that 
     * @return 
     */
    public boolean isDecendent(ParameterList that) {
        return that != null && that.isAncestor(this);
    }
    
    /**
     * Return true if <CODE>that</CODE> is an ancestor of <CODE>this</CODE>.
     */
    public boolean isAncestor(ParameterList that) {
        return parent != null && ( that == parent || parent.isAncestor(that) );
    }
    
    
//    public String findParameterKey(String key) {
//        for(ParameterEntry pp : parameters ) {
//            if (pp.parameter.getKey().equals(key)  ) {
//                return pp.parameter.getName();
//            }
//        }
//        return null;
//    }
    
    /*public void firePropertyChange(DetailList source, String propertyName, Object oldValue, Object newValue) {
        System.out.println("ParameterList.firePropertyChange(" +  source + ", " + propertyName + ", " + oldValue + ", " + newValue + ")");
        super.firePropertyChange(source, propertyName, oldValue, newValue);
    }*/
    
    public static class ParameterEntry<T> implements Serializable, Cloneable {
        
        static final long serialVersionUID = -54360715161312353L;
        
        public Parameter parameter;
        public boolean visible;
        public boolean enabled;
        public boolean overrideParent;
        public boolean set;
        public boolean indexed = false;
        
        public transient AbstractParameterEditor customEditor;
        
        public T value = null;
        
        public <T> ParameterEntry(Parameter<T> parameter, boolean visible, boolean enabled) {
            this.parameter = parameter;
            this.visible = visible;
            this.enabled = enabled;
            
            //this.set = false;
            this.value = null;
            this.overrideParent = false;
            
            this.indexed = parameter.isIndexed();
        }

        @Override
        protected ParameterEntry<T> clone() {
            ParameterEntry<T> that = new ParameterEntry<T>(parameter.clone(), visible, enabled);
            
            return that;
        }
        
        
        
        
    }
    
    public static class ParameterIterator implements Iterator<Parameter> {
        
        Iterator<ParameterEntry> iterator;
        
        public ParameterIterator(Iterator<ParameterEntry> iterator) {
            this.iterator = iterator;
        }
        
        public boolean hasNext() {
            return iterator.hasNext();
        }
        
        public Parameter next() {
            return iterator.next().parameter;
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
    }
}


