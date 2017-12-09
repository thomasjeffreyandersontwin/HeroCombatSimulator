/*
 * ActionPanel.java
 *
 * Created on July 7, 2001, 3:05 PM
 */

package champions.exceptionWizard;

import champions.*;
import champions.interfaces.*;
import champions.exception.*;
import javax.swing.*;
/**
 *
 * @author  twalker
 * @version 
 */
public class ActionPanel extends javax.swing.JPanel 
implements WizardPanel {
    
    private Wizard wizard;
    private WizardPanel nextPanel;

    /** Creates new form ActionPanel */
    public ActionPanel() {
        initComponents ();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        setLayout(new java.awt.BorderLayout());
    }//GEN-END:initComponents

    public JPanel getPanel() {
        return this;
    }    

    public void setWizard(Wizard wizard) {
        this.wizard = wizard;
    }
    
    public void enterPanel() throws WizardException {
       wizard.setIsLastPanel(true);
    }
    
    public void exitPanel() {
    }
    
    public void abortPanel() {
    }
    
    public void destroy() {
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
