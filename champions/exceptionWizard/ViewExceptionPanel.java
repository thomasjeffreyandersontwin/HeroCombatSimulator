/*
 * ViewExceptionPanel.java
 *
 * Created on July 7, 2001, 3:05 PM
 */

package champions.exceptionWizard;

import champions.*;
import champions.interfaces.*;
import champions.exception.*;
import javax.swing.*;
import java.io.*;
import java.beans.*;

/**
 *
 * @author  twalker
 * @version 
 */
public class ViewExceptionPanel extends javax.swing.JPanel
implements WizardPanel, PropertyChangeListener {

    private ExceptionWizard wizard;
    private WizardPanel nextPanel;
    
    /** Creates new form ViewExceptionPanel */
    public ViewExceptionPanel() {
        initComponents ();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        titleLabel = new javax.swing.JLabel();
        descriptionText = new javax.swing.JTextArea();
        exceptionLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        exceptionText = new javax.swing.JTextArea();
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        titleLabel.setText("An Exception Occurred");
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(titleLabel, gridBagConstraints1);
        
        
        descriptionText.setDisabledTextColor(java.awt.Color.black);
        descriptionText.setWrapStyleWord(true);
        descriptionText.setLineWrap(true);
        descriptionText.setEditable(false);
        descriptionText.setFont(new java.awt.Font (GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        descriptionText.setText("Exceptions are low-level errors indicating an internal problem with the Hero Combat Simulator.\n\nAfter an exception occurs, it is best to save your battle and restart the Simulator since exceptions often leave the Simulator in an undefined state.");
        descriptionText.setEnabled(false);
        descriptionText.setOpaque(false);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets(0, 15, 15, 0);
        gridBagConstraints1.weightx = 1.0;
        add(descriptionText, gridBagConstraints1);
        
        
        exceptionLabel.setText("Exception:");
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(exceptionLabel, gridBagConstraints1);
        
        
        
        exceptionText.setTabSize(4);
          jScrollPane1.setViewportView(exceptionText);
          
          
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets(0, 15, 0, 0);
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints1);
        
    }//GEN-END:initComponents

    public JPanel getPanel() {
        return this;
    }    

    public void setWizard(Wizard wizard) {
        this.wizard = (ExceptionWizard)wizard;
    }
    
    public void enterPanel() throws WizardException {
        wizard.currentExceptionWizard = wizard;
        try {
      //  if ( nextPanel == null ) nextPanel = new ActionPanel();
     //   wizard.setNextPanel( nextPanel );
            wizard.setIsLastPanel(true);
        
            wizard.getParameters().addPropertyChangeListener("Exception.INDEXSIZE",this);
            
            updateExceptionText();
        
        }
        catch ( Exception e ) {
            System.out.println("WARNING: AN EXCEPTION OCCURED WHILE USING THE EXCEPTION WIZARD!");
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    public void exitPanel() {
        if ( wizard.currentExceptionWizard == wizard ) wizard.currentExceptionWizard = null;
        wizard.getParameters().removePropertyChangeListener("Exception.INDEXSIZE",this);
    }
    
    public void abortPanel() {
        if ( wizard.currentExceptionWizard == wizard ) wizard.currentExceptionWizard = null;
        wizard.getParameters().removePropertyChangeListener("Exception.INDEXSIZE",this);
    }
    
    public void destroy() {
    }
    
    private void updateExceptionText() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw );
        
        int index, count;
        count = wizard.getParameters().getIndexedSize("Exception");
        for ( index = 0; index < count; index ++ ) {
            Throwable exception = (Throwable) wizard.getParameters().getIndexedValue( index,"Exception","THROWABLE" );

            exception.printStackTrace(pw);
            exceptionText.setText( sw.getBuffer().toString() );
        }
    }
    
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source 
     *  	and the property that has changed.
 */
    public void propertyChange(PropertyChangeEvent evt) {
        updateExceptionText();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextArea descriptionText;
    private javax.swing.JLabel exceptionLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea exceptionText;
    // End of variables declaration//GEN-END:variables

}
