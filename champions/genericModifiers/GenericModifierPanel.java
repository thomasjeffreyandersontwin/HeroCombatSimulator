/*
 * GenericModifierPanel.java
 *
 * Created on November 9, 2006, 8:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.genericModifiers;

import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import tjava.Destroyable;

/**
 *
 * @author 1425
 */
public abstract class GenericModifierPanel extends JPanel
implements Destroyable {
    
    /** Creates a new instance of GenericModifierPanel */
    public GenericModifierPanel() {
    }
    
    public abstract void addPropertyChangeListener(PropertyChangeListener listener);
    public abstract void removePropertyChangeListener(PropertyChangeListener listener);
    
    public void destroy() {
        
    }
    
}
