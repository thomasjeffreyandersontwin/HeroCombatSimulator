/*
 * PADDialog.java
 *
 * Created on November 22, 2000, 8:10 PM
 */

package champions;

import champions.parameters.BooleanParameter;
import champions.parameters.ComboParameter;
import champions.parameters.DiceParameter;
import champions.parameters.DoubleParameter;
import champions.parameters.IntegerParameter;
import champions.parameters.Parameter;
import champions.parameters.ParameterList;
import champions.parameters.StringParameter;
import javax.swing.*;
import java.awt.*;
import java.beans.*;
import champions.interfaces.*;
import champions.event.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.util.Iterator;
/**
 *
 * @author  unknown
 * @version
 */
public class PADDialog extends javax.swing.JDialog
implements PropertyChangeListener, PADValueListener {

    static private PADDialog padDialog = new PADDialog(null);

    /** Holds value of property parameterList. */
    private ParameterList parameterList;
    /** Holds value of property detailList. */
    private DetailList detailList;
    /** Holds value of property value. */
    private Object value;

    private PADValueListener pADValueListener = null;

    private boolean isProcessing = false;
    /** Creates new form PADDialog */

    static {
        KeyStroke lEnterKey = KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0);
        JTextField lTextField = new JTextField();
        Keymap lMap = lTextField.getKeymap();
        lMap.removeKeyStrokeBinding( lEnterKey );
    }
    /** Holds value of property PADValueListener. */
    private PADValueListener PADValueListener;
    public PADDialog(java.awt.Frame parent) {
        super (parent, true);
        initComponents ();
        padGroup.setLayout( new PADLayout() );
        this.getRootPane().setDefaultButton( okayButton );



        pack ();
    }

    static public int showPADDialog(final String label, final ParameterList pl, final DetailList dl) {
        return showPADDialog(label,pl,dl,null);
    }

    static public int showPADDialog(final String label, final ParameterList pl, final DetailList dl, final PADValueListener l) {
        padDialog.value = JOptionPane.UNINITIALIZED_VALUE;

        if ( SwingUtilities.isEventDispatchThread() ) {
            padDialog.setDetailList(dl);
            padDialog.setPADValueListener(l);
            padDialog.setParameterList(pl);


            padDialog.titleLabel.setText( label );
            padDialog.pack();
            padDialog.centerDialog();
            padDialog.setVisible(true);
        }
        else {
            try {
                SwingUtilities.invokeAndWait( new Runnable() {
                    public void run() {
                        padDialog.setDetailList(dl);
                        padDialog.setPADValueListener(l);
                        padDialog.setParameterList(pl);


                        padDialog.titleLabel.setText( label );
                        padDialog.pack();
                        padDialog.centerDialog();
                        padDialog.setVisible(true);
                    }
                });
            }
            catch (Exception exc) {
                System.out.println("Exception " + exc.toString() + "caught while Invoking and Waiting for PADDialog!");
            }
        }

        return padDialog.getValue();
    }

    public void centerDialog() {
        Dimension d;
        Point loc;
        Container parent = getParent();

        d = Toolkit.getDefaultToolkit ().getScreenSize ();
        loc = new Point(0,0);

        Dimension m = getSize ();

        d.width -= m.width;
        d.height -= m.height;
        d.width /= 2;
        d.height /= 2;
        int x = (int)loc.getX() + d.width;
        if ( x < 0 ) x = 0;
        int y = (int)loc.getY() + d.height;
        if ( y < 0 ) y = 0;
        setLocation (x,y);
    }

    public void adjustPADs() {
        isProcessing = true;

        int i;
        padGroup.removeAll();
        //int count = parameterList.getParameterCount();
        //int count  = parameterList.getIndexedSize( "parameter" );
        String type, key, desc, enabledString,name;
        Object value;
        boolean enabled;
        PADAbstractEditor p;


        //for( i=0;i<count;i++) {
        Iterator<Parameter> it = parameterList.getParameters();
        while ( it.hasNext() ) {
            // Create the appropriate configuration panels
            //Parameter p = parameterList.getParameter(i);
            Parameter param = it.next();
            
            //type = param.getType();
            //if ( type != null ) {
                name = param.getName();
                key = param.getKey();
                desc = param.getDescription();
                value = parameterList.getParameterValue(name);
                enabled = parameterList.isParameterEnabled(name);

                detailList.add ( key, value, true );

                if ( param instanceof DiceParameter ) {
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
                else if ( param instanceof DoubleParameter) {
                    // Create a Double configuration panel
                    double increment = ((DoubleParameter)param).getIncrement();
                    if ( value != null )  {
                        p = new PADDoubleEditor(key,desc,(Double)value, increment,this);
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
                        if ( value instanceof String ) {
                            // I don't think this should ever be the case...
                            p = new PADBooleanEditor(key,desc,(String)value,this);
                        }
                        else {
                            p = new PADBooleanEditor(key,desc,(Boolean)value,this);
                        }
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
                    //p = new PADUnknownEditor(key,desc,type,this);
                    throw new IllegalArgumentException("Unknown parameter type found");
                }
                p.setEnabled(enabled);


                p.setName(name);
                padGroup.add ( p );
                p.addPADValueListener( this);
                if ( pADValueListener != null ) p.addPADValueListener( pADValueListener );
            //}
        }

        pack();
        isProcessing = false;

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        labelGroup = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        padGroup = new javax.swing.JPanel();
        buttonGroup = new javax.swing.JPanel();
        okayButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        
        labelGroup.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        
        labelGroup.add(titleLabel);
        
        getContentPane().add(labelGroup, java.awt.BorderLayout.NORTH);
        
        padGroup.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        getContentPane().add(padGroup, java.awt.BorderLayout.CENTER);
        
        buttonGroup.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        
        okayButton.setText("Okay");
        okayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okayButtonActionPerformed(evt);
            }
        });
        
        buttonGroup.add(okayButton);
        
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        
        buttonGroup.add(cancelButton);
        
        getContentPane().add(buttonGroup, java.awt.BorderLayout.SOUTH);
        
    }//GEN-END:initComponents

  private void cancelButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    // Add your handling code here:
    setValue(JOptionPane.CANCEL_OPTION);
    this.setVisible(false);
  }//GEN-LAST:event_cancelButtonActionPerformed

  private void okayButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okayButtonActionPerformed
    // Add your handling code here:
    setValue(JOptionPane.OK_OPTION);
    this.setVisible(false);
  }//GEN-LAST:event_okayButtonActionPerformed

/** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setValue( JOptionPane.CLOSED_OPTION);
        setVisible (false);
        //dispose ();
    }//GEN-LAST:event_closeDialog

    /**
     * @param args the command line arguments
     */
    public static void main (String args[]) {
        new PADDialog (new javax.swing.JFrame ()).show ();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel labelGroup;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel padGroup;
    private javax.swing.JPanel buttonGroup;
    private javax.swing.JButton okayButton;
    private javax.swing.JButton cancelButton;
    // End of variables declaration//GEN-END:variables

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
        adjustPADs();

        if ( this.parameterList != null ) {
            this.parameterList.addPropertyChangeListener(this);
        }
    }
    /** Getter for property detailList.
     * @return Value of property detailList.
     */
    public DetailList getDetailList() {
        return detailList;
    }
    /** Setter for property detailList.
     * @param detailList New value of property detailList.
     */
    public void setDetailList(DetailList detailList) {
        if ( detailList == null ) detailList = new DetailList();
        this.detailList = detailList;
    }
    /** Getter for property value.
     * @return Value of property value.
     */
    public int getValue() {
        if ( value == null || value.equals(JOptionPane.UNINITIALIZED_VALUE) ) {
            return 0;
        }

        return ((Integer)value).intValue();
    }
    /** Setter for property value.
     * @param value New value of property value.
     */
    public void setValue(int value) {
        this.value = new Integer(value);
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
        detailList.add(evt.getKey(), evt.getValue(),true);
    }

    public boolean PADValueChanging(PADValueEvent evt) {
        return true;
    }
    /** Getter for property PADValueListener.
     * @return Value of property PADValueListener.
     */
    public PADValueListener getPADValueListener() {
        return pADValueListener;
    }
    /** Setter for property PADValueListener.
     * @param PADValueListener New value of property PADValueListener.
     */
    public void setPADValueListener(PADValueListener pADValueListener) {
        this.pADValueListener = pADValueListener;
    }
}