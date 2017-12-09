/*
 * FrameChangeEvent.java
 *
 * Created on January 1, 2001, 7:50 PM
 */

package dockable;

import java.util.*;
/**
 *
 * @author  unknown
 * @version 
 */
public class FrameChangeEvent extends EventObject {

    /** Holds value of property panelCount. */
    private int panelCount;
    /** Creates new FrameChangeEvent */
    public FrameChangeEvent(Object s, int panelCount) {
        super(s);
        setPanelCount(panelCount);
    }

    /** Getter for property panelCount.
     * @return Value of property panelCount.
     */
    public int getPanelCount() {
        return panelCount;
    }
    /** Setter for property panelCount.
     * @param panelCount New value of property panelCount.
     */
    public void setPanelCount(int panelCount) {
        this.panelCount = panelCount;
    }
}