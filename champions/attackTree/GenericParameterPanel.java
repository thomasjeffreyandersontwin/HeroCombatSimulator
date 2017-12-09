/*
 * GenericParameterPanel.java
 *
 * Created on January 20, 2002, 10:56 PM
 */

package champions.attackTree;

import champions.DetailList;
import champions.PADAbstractEditor;
import champions.PADBooleanEditor;
import champions.PADComboEditor;
import champions.PADDiceEditor;
import champions.PADDiceValueEditor;
import champions.PADDoubleEditor;
import champions.PADIntegerEditor;
import champions.PADLayout;
import champions.PADStringEditor;
import champions.PADUnknownEditor;
import champions.event.PADValueEvent;
import champions.interfaces.PADValueListener;
import champions.parameters.BooleanParameter;
import champions.parameters.ComboParameter;
import champions.parameters.DiceParameter;
import champions.parameters.DiceValueParameter;
import champions.parameters.DoubleParameter;
import champions.parameters.IntegerParameter;
import champions.parameters.Parameter;
import champions.parameters.ParameterList;
import champions.parameters.StringParameter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JPanel;

/**
 *
 * @author  twalker
 */
public class GenericParameterPanel extends JPanel implements AttackTreeInputPanel, PADValueListener, PropertyChangeListener {

    static private GenericParameterPanel defaultPanel;
    
    private boolean isProcessing = false;
    
        /** Holds value of property parameterList. */
    private ParameterList parameterList;

    private Vector padVector = new Vector();
    
    /** Holds value of property destinationDetailList. */
    private DetailList destinationDetailList;    
    
    /** Creates new form GenericParameterPanel */
    public GenericParameterPanel() {
        initComponents();
        setLayout( new PADLayout() );
    }

    public void adjustPADs() {
        isProcessing = true;
        
        removeAll();
        
        Iterator it = padVector.iterator();
        while ( it.hasNext() ) {
            ((PADAbstractEditor)it.next() ).removePADValueListener(this);
        }
        padVector.clear();

        int i;
       // int count  = parameterList.getIndexedSize( "parameter" );
        String key, desc, enabledString,name;
        Object value;
        boolean enabled;
        PADAbstractEditor p;


        Iterator<Parameter> it2 = parameterList.getParameters();
        while(it2.hasNext()) {
            Parameter param = it2.next();
            //type = param.getType();
            // Create the appropriate configuration panels
            if ( param != null ) {
                name = param.getName();
                key = param.getKey();
                desc = param.getDescription();
                value = parameterList.getParameterValue(name);
                enabled = parameterList.isParameterEnabled(name);

                // Find the value from the Destination...
                Object existingValue = destinationDetailList.getValue(key);
                if ( existingValue != null ) {
                    value = existingValue;
                }
                else {
                    // Doesn't exist, so add the default
                    destinationDetailList.add ( key, value, true);
                }

                if ( param instanceof DiceValueParameter ) {
                    String size = ((DiceValueParameter)param).getSize();
                    // Create a dice configuration panel
                    if ( value != null )  {
                        p = new PADDiceValueEditor(key,desc,(String)value,this,(String)size);
                    }
                    else {
                        p = new PADDiceValueEditor(key,desc,this,(String)size);
                    }
                }
                else if ( param instanceof DiceParameter ) {
                    // Create a dice configuration panel
                    if ( value != null )  {
                        p = new PADDiceEditor(key,desc,(String)value,this);
                    }
                    else {
                        p = new PADDiceEditor(key,desc,this);
                    }
                }
                else if ( param instanceof IntegerParameter ) {
                    // Create a integer configuration panel
                    if ( value != null )  {
                        p = new PADIntegerEditor(key,desc,(Integer)value,this);
                    }
                    else {
                        p = new PADIntegerEditor(key,desc,this);
                    }
                }
                else if ( param instanceof DoubleParameter ) {
                    // Create a Double configuration panel
                    //Object increment = parameterList.getIndexedValue(i, "parameter","INCREMENT");
                    //if ( increment == null || increment.getClass() != Double.class ) continue;
                    Double increment = ((DoubleParameter)param).getIncrement();
                    
                    if ( value != null )  {
                        p = new PADDoubleEditor(key,desc,(Double)value, (Double)increment,this);
                    }
                    else {
                        p = new PADDoubleEditor(key,desc,(Double)increment,this);
                    }
                }
                else if ( param instanceof StringParameter ) {
                    // Create a string configuration panel
                    if ( value != null )  {
                        p = new PADStringEditor(key,desc,(String)value,this);
                    }
                    else {
                        p = new PADStringEditor(key,desc,this);
                    }
                }
                else if ( param instanceof BooleanParameter ) {
                    // Create a string configuration panel
                    if ( value != null )  {
                        p = new PADBooleanEditor(key,desc,(String)value,this);
                    }
                    else {
                        p = new PADBooleanEditor(key,desc,this);
                    }
                }
                else if ( param instanceof ComboParameter ) {
                    // Create a JComboBox configuration panel
                    // Requires VALUES parameter
                    Object[] options = ((ComboParameter)param).getOptions();
                    if ( value != null )  {
                        p = new PADComboEditor(key,desc,value,options,this);
                    }
                    else {
                        p = new PADComboEditor(key,desc,options,this);
                    }
                }
                else  {
                    // Create a Unknown configuration panel
                    throw new IllegalArgumentException("Unknown parameter not supported.");
                    //p = new PADUnknownEditor(key,desc,type,this);
                }
                
                padVector.add(p);
                p.setEnabled(enabled);
                p.setName(name);
                add ( p );
                p.addPADValueListener( this);
            }
        }

        isProcessing = false;

    }
    
    /** Getter for property parameterList.
     * @return Value of property parameterList.
     */
    public ParameterList getParameterList() {
        return parameterList;
    }
    /** Setter for property parameterList.
     * @param parameterList New value of property parameterList.
     */
    public void setParameterList(ParameterList parameterList) {
        if ( this.parameterList != null ) {
            this.parameterList.removePropertyChangeListener(this);
        }
        this.parameterList = parameterList;
        if ( this.parameterList != null ) {
            this.parameterList.addPropertyChangeListener(this);
            adjustPADs();
        }
    }
    
    /** This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ( isProcessing == false && parameterList != null ) {
            adjustPADs();
        }
    }
    
    public void PADValueChanged(PADValueEvent evt) {
        if ( destinationDetailList != null ) {
            destinationDetailList.add(evt.getKey(), evt.getValue(),true);
        }
    }

    public boolean PADValueChanging(PADValueEvent evt) {
        return true;
    }
    
    static public GenericParameterPanel getDefaultPanel(ParameterList pl, DetailList destination) {
        if ( defaultPanel == null ) defaultPanel = new GenericParameterPanel();
        
        defaultPanel.setDestinationDetailList(destination);
        defaultPanel.setParameterList(pl);
        

        return defaultPanel;
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        setLayout(new java.awt.BorderLayout());

    }//GEN-END:initComponents

    public void showPanel(AttackTreePanel atip) {
        
    }    

    public JPanel getPanel() {
        return this;
    }    

    public void hidePanel() {
        setParameterList(null);
    }
    
    /** Getter for property destinationDetailList.
     * @return Value of property destinationDetailList.
     */
    public DetailList getDestinationDetailList() {
        return destinationDetailList;
    }
    
    /** Setter for property destinationDetailList.
     * @param destinationDetailList New value of property destinationDetailList.
     */
    public void setDestinationDetailList(DetailList destinationDetailList) {
        this.destinationDetailList = destinationDetailList;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
