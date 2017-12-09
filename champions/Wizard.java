/*
 * Wizard.java
 *
 * Created on July 6, 2001, 6:40 PM
 */

package champions;

import champions.interfaces.*;

import javax.swing.*;
import java.awt.*;
import champions.exception.*;
import tjava.*;
/**
 *
 * @author  twalker
 * @version
 */
public class Wizard extends javax.swing.JFrame
implements Destroyable {
    
    /** Holds value of property parameters. */
    protected DetailList parameters;
    
    /** Creates new form Wizard */
    public Wizard(String title, WizardPanel startPanel) {
        initComponents ();
        
        setupWizard();
        
        setTitle(title);
        
        setNextPanel(startPanel);
        
        advancePanel();
        
        pack();
    }
    
    public Wizard(String title) {
        initComponents ();
                
        setupWizard();
        
        setTitle(title);
    }
    
    public void setupWizard() {
        setParameters( new DetailList() );
        setIsLastPanel(false);
    }
    
    public void setCurrentPanel(WizardPanel panel) {
        parameters.add( "Wizard.CURRENTPANEL", panel, true );
    }
    
    public WizardPanel getCurrentPanel() {
        Object o = parameters.getValue("Wizard.CURRENTPANEL" );
        return (o != null) ? (WizardPanel)o : null;
    }
    
    public void setNextPanel(WizardPanel panel) {
        parameters.add( "Wizard.NEXTPANEL", panel, true  );
        setCanAdvance(true);
        setIsLastPanel(false);
    }
    
    public WizardPanel getNextPanel() {
        Object o = parameters.getValue("Wizard.NEXTPANEL");
        return (o != null) ? (WizardPanel) o : null;
    }
    
    public void setIsLastPanel(boolean lastPanel) {
        parameters.add( "Wizard.ISLASTPANEL", lastPanel ? "TRUE" : "FALSE", true );
        if ( lastPanel ) {
            nextButton.setText( "Finish" );
        }
        else {
            nextButton.setText( "Next >");
        }
    }
    
    public boolean isLastPanel() {
        return parameters.getBooleanValue("Wizard.ISLASTPANEL");
    }
    
    public void setCanAdvance(boolean canAdvance) {
        parameters.add( "Wizard.CANADVANCE", canAdvance ? "TRUE" : "FALSE", true);
        setupButtons();
    }
    
    public boolean canAdvance() {
        return parameters.getBooleanValue("Wizard.CANADVANCE");
    }
    
    public void advancePanel() {
        WizardPanel wp;
        
        wp = getCurrentPanel();
        
        if ( wp != null ) {
            wp.exitPanel();
            addPreviousPanel(wp);
            
        }
        
        wizardPanelGroup.removeAll();
        
        if ( isLastPanel() == true ) {
            this.setVisible(false);
            destroy();
        }
        else {
            wp = getNextPanel();
            wp.setWizard(this);
            try {
            wp.enterPanel();
            setCurrentPanel(wp);
            JPanel jpanel = wp.getPanel();
            wizardPanelGroup.add( jpanel, BorderLayout.CENTER );
            }
            catch ( WizardException we ) {
                rewindPanel();
                return;
            }
            
            setupButtons();
        
     //   pack();
            wizardPanelGroup.revalidate();
            repaint();
            
        }
        
        
    }
    
    public void rewindPanel() {
        WizardPanel wp;
        
        wp = getCurrentPanel();
        
        if ( wp != null ) {
            wp.abortPanel();
        }
        
        wizardPanelGroup.removeAll();
        
        wp = getPreviousPanel();
        if ( wp != null ) {
            wp.setWizard(this);
            try { 
                removePreviousPanel();
                setCurrentPanel(wp);
                wp.enterPanel();
                JPanel jpanel = wp.getPanel();
                wizardPanelGroup.add( jpanel, BorderLayout.CENTER );
                
            }
            catch ( WizardException we ) {
                rewindPanel();
                return;
            }
            
        }
        
        setupButtons();
        
        invalidate();
        repaint();
    }
    
    public void cancel() {
        WizardPanel wp;
        
        wp = getCurrentPanel();
        
        if ( wp != null ) {
            wp.abortPanel();
        }
        
        this.setVisible(false);
        
        destroy();
    }
    
    public void setupButtons() {
        backButton.setEnabled( getPreviousPanel() != null );
        nextButton.setEnabled( (getNextPanel() != null || isLastPanel() ) && canAdvance() );
        cancelButton.setEnabled(true);
    }


public void addPreviousPanel(WizardPanel wp) {
    parameters.createIndexed( "PreviousPanel","WIZARDPANEL", wp);
}

public WizardPanel getPreviousPanel() {
    int count = parameters.getIndexedSize( "PreviousPanel");
    Object o = null;
    if ( count > 0 ) o = parameters.getIndexedValue(count-1, "PreviousPanel", "WIZARDPANEL" );
    return (o!=null) ? (WizardPanel)o : null;
}

public void removePreviousPanel() {
    int count = parameters.getIndexedSize( "PreviousPanel");
    if ( count > 0 ) parameters.removeAllIndexed(count-1, "PreviousPanel");
}

    public void centerWizard() {
        Dimension d;
        Point loc;

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
    
    public void setVisible(boolean visible) {
        centerWizard();
        super.setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        buttonGroup = new javax.swing.JPanel();
        debugButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        wizardPanelGroup = new javax.swing.JPanel();
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        }
        );
        
        buttonGroup.setLayout(new java.awt.FlowLayout(2, 5, 5));
        
        debugButton.setText("Debug...");
          debugButton.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
                  debugButtonActionPerformed(evt);
              }
          }
          );
          buttonGroup.add(debugButton);
          
          
        cancelButton.setText("Cancel");
          cancelButton.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
                  cancelButtonActionPerformed(evt);
              }
          }
          );
          buttonGroup.add(cancelButton);
          
          
        backButton.setText("< Back");
          backButton.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
                  backButtonActionPerformed(evt);
              }
          }
          );
          buttonGroup.add(backButton);
          
          
        nextButton.setText("Next >");
          nextButton.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
                  nextButtonActionPerformed(evt);
              }
          }
          );
          buttonGroup.add(nextButton);
          
          
        getContentPane().add(buttonGroup, java.awt.BorderLayout.SOUTH);
        
        
        wizardPanelGroup.setLayout(new java.awt.BorderLayout());
        
        getContentPane().add(wizardPanelGroup, java.awt.BorderLayout.CENTER);
        
    }//GEN-END:initComponents

  private void debugButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debugButtonActionPerformed
// Add your handling code here:
      if ( parameters != null ) parameters.debugDetailList("Import Wizard Parameters");
  }//GEN-LAST:event_debugButtonActionPerformed

  private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
// Add your handling code here:
      advancePanel();
  }//GEN-LAST:event_nextButtonActionPerformed

  private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
// Add your handling code here:
      rewindPanel();
  }//GEN-LAST:event_backButtonActionPerformed

  private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
// Add your handling code here:
      cancel();
  }//GEN-LAST:event_cancelButtonActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        cancel();
    }//GEN-LAST:event_exitForm
    
    /**
     * @param args the command line arguments
     */
    public static void main (String args[]) {
        //new Wizard ().show ();
        Character.importCharacter(null);
    }
    
    /** Getter for property parameters.
     * @return Value of property parameters.
     */
    public DetailList getParameters() {
        return parameters;
    }
    
    /** Setter for property parameters.
     * @param parameters New value of property parameters.
     */
    public void setParameters(DetailList parameters) {
        this.parameters = parameters;
    }
    
    public void destroy() {
        // Release everything that can be holding memory
        WizardPanel wp;
        int index, count;
        count = parameters.getIndexedSize("PreviousPanel");
        for ( index = 0; index < count; index++ ) {
            wp = (WizardPanel)parameters.getIndexedValue(index, "PreviousPanel", "WIZARDPANEL" );
            wp.destroy();
        }
        
        if ( ( wp = getNextPanel() ) != null ) wp.destroy();
        if ( ( wp = getCurrentPanel() ) != null ) wp.destroy();
        
        parameters.clear();
        parameters = null;
        
        dispose();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonGroup;
    private javax.swing.JButton debugButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton backButton;
    private javax.swing.JButton nextButton;
    private javax.swing.JPanel wizardPanelGroup;
    // End of variables declaration//GEN-END:variables
    
    
    
}
