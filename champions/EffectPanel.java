/*
 * EffectPanel.java
 *
 * Created on October 24, 2000, 3:16 PM
 */

package champions;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.SwingConstants;


/**
 *
 * @author unknown
 * @version
 * @deprecated This method of displaying effects is deprecated.
 * The HTMLButtonPanel coupled with an EffectListModel should
 * be used in it's place.
 */
public class EffectPanel extends javax.swing.JPanel
implements PropertyChangeListener {


    /** Holds value of property target. */
    private Target target;
    private Vector buttonVector = new Vector();
    /** Creates new form EffectPanel */
    public EffectPanel() {
        initComponents ();
        setLayout( new FlowLayout(FlowLayout.LEFT, 3, 0) );
    }

    public void adjustEffects() {
        HTMLButton button;
        int i = 0;
        int count;

        if ( target != null ) {

            count = target.getIndexedSize( "Effect" );

            for (; i < count ; i++) {
                if ( buttonVector.size() <= i ) {
                    button =  new HTMLButton();
                    buttonVector.add(button);
                    button.setFont( getFont() );
                    button.setVerticalAlignment(SwingConstants.TOP);
                    button.setForeground( getForeground() );
                    this.add(button);
                }
                else {
                    button = (HTMLButton)buttonVector.get(i);
                }
                //String effectName = target.getEffectName(i);
                final Effect effect = target.getEffect(i);
                button.setAction( new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        EffectDetail ed = new EffectDetail(effect);
                        ed.showEffectDetail(null);
                    }
                });
                button.setText( effect.getName() );
                button.setVisible( true );
                button.repaint();
            }
        }
        for (; i < buttonVector.size(); i++ ) {
            button = (HTMLButton)buttonVector.get(i);
            button.setVisible(false);
        }

        this.revalidate();
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        setLayout(new java.awt.FlowLayout(0, 2, 2));
        setFont(new java.awt.Font ("Arial", 0, 11));
    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        if ( this.target != null ) {
            this.target.removePropertyChangeListener(this);
        }
        this.target = target;
        adjustEffects();
        if ( this.target != null ) {
            this.target.addPropertyChangeListener(this);
        }
    }
    /** This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        adjustEffects();
    }
}