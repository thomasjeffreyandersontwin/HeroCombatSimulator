/*
 * GenericModifierTest.java
 *
 * Created on November 9, 2006, 8:43 PM
 */

package champions.genericModifiers;

/**
 *
 * @author  1425
 */
public class GenericModifierTest extends javax.swing.JFrame {
    
    /** Creates new form GenericModifierTest */
    public GenericModifierTest(GenericModifierList gml) {
        initComponents();
        
        genericModifierListPanel1.setModifierList(gml);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        genericModifierListPanel1 = new champions.genericModifiers.GenericModifierListPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(genericModifierListPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(genericModifierListPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        final GenericModifierList gml = new GenericModifierList("Breakfall roll");
        
        gml.addGenericModifier( new BaseGenericModifier(10));
        gml.addGenericModifier(new IncrementGenericModifier("Some Adjustment", +2, false));
        gml.addGenericModifier(new IncrementGenericModifier("Generic Adjustment", 0, true));
        gml.addGenericModifier(new MultiplierGenericModifier("Crazy Penalty", 0.5, false));
        gml.addGenericModifier(new MultiplierGenericModifier("Other Penalty", 0.25, true));
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GenericModifierTest(gml).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private champions.genericModifiers.GenericModifierListPanel genericModifierListPanel1;
    // End of variables declaration//GEN-END:variables
    
}
