/*
 * TestFrame2.java
 *
 * Created on January 1, 2001, 1:12 PM
 */

package dockable;

import java.awt.*;
/**
 *
 * @author  unknown
 * @version 
 */
public class TestFrame2 extends javax.swing.JFrame {

    /** Creates new form TestFrame2 */
    public TestFrame2() {
        initComponents ();
        
        
        getContentPane().setLayout( new BorderLayout() );
        getContentPane().add( new TestDocker(), BorderLayout.CENTER );
        
        pack ();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents () {//GEN-BEGIN:initComponents
        addWindowListener (new java.awt.event.WindowAdapter () {
            public void windowClosing (java.awt.event.WindowEvent evt) {
                exitForm (evt);
            }
        }
        );

    }//GEN-END:initComponents

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit (0);
    }//GEN-LAST:event_exitForm

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
        new TestFrame2 ().show ();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}