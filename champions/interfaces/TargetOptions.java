/*
 * TargetOptionsInterface.java
 *
 * Created on December 31, 2003, 2:03 PM
 */

package champions.interfaces;

import champions.Target;
import javax.swing.JPanel;

/**
 *
 * @author  1425
 */
public interface TargetOptions {
    /** Returns the name of the options.
     *
     * This name will be the title in the tab of the overall options panel.
     */
    public String getOptionName();
    
    /** Sets the Target this TargetOption should affect.
     */
    public void setTarget(Target target);
    
    /** Return a JPanel which contains the GUI controls. */
    public JPanel getOptionsPanel();
    
    /** Signals the TargetOptions that the user cancelled the changes. */
    public void cancelled();
    
    /** Signals the TargetOptions that the user applied the changes. */
    public void apply();
    
    /** Signals the TargetOptions that the user closed the options. 
     * 
     * If the user selects Okay, apply() will be run just prior to this
     * method.  If cancelled is selected, cancelled() will be run just
     * prior to this method.
     */
    public void close();
}
