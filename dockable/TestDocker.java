/*
 * TestDocker.java
 *
 * Created on January 1, 2001, 2:56 PM
 */

package dockable;

import java.awt.event.*;
/**
 *
 * @author  unknown
 */
public class TestDocker extends dockable.DockingPanel {

    /** Creates new form TestDocker */
    public TestDocker() {
        initComponents();
        
        setJMenuBar(jMenuBar1);
        
        setName("Test Docker");
    }
    
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents () {//GEN-BEGIN:initComponents
        jMenuBar1 = new javax.swing.JMenuBar ();
        jMenu1 = new javax.swing.JMenu ();
        jMenuItem1 = new javax.swing.JMenuItem ();
        jMenu2 = new javax.swing.JMenu ();
        jMenuItem2 = new javax.swing.JMenuItem ();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem ();
        jSeparator1 = new javax.swing.JSeparator ();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem ();
        jPanel1 = new javax.swing.JPanel ();
        jButton1 = new javax.swing.JButton ();
        jCheckBox1 = new javax.swing.JCheckBox ();

          jMenu1.setText ("Menu");
  
            jMenuItem1.setText ("Item");
    
            jMenu1.add (jMenuItem1);
          jMenuBar1.add (jMenu1);
          jMenu2.setText ("Menu");
  
            jMenuItem2.setText ("Item");
    
            jMenu2.add (jMenuItem2);
            jRadioButtonMenuItem1.setText ("RadioButton");
    
            jMenu2.add (jRadioButtonMenuItem1);
    
            jMenu2.add (jSeparator1);
            jCheckBoxMenuItem1.setText ("CheckBox");
    
            jMenu2.add (jCheckBoxMenuItem1);
          jMenuBar1.add (jMenu2);
        getContentPane().setLayout (new java.awt.BorderLayout ());


          jButton1.setText ("jButton1");
  
          jPanel1.add (jButton1);
  
          jCheckBox1.setText ("jCheckBox1");
  
          jPanel1.add (jCheckBox1);
  

        getContentPane().add (jPanel1, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    // End of variables declaration//GEN-END:variables

}
