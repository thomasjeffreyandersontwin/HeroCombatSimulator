/*
 * ToggleSelectionModel.java
 *
 * Created on November 12, 2003, 10:07 PM
 */

package tjava;

import javax.swing.DefaultListSelectionModel;

/**
 *
 * @author  1425
 */
public class ToggleSelectionModel extends DefaultListSelectionModel {
    boolean gestureStarted = false;
    
    public void setSelectionInterval(int index0, int index1) {
        if (isSelectedIndex(index0) && !gestureStarted) {
            super.removeSelectionInterval(index0, index1);
        }
        else {
            super.addSelectionInterval(index0, index1);
        }
        gestureStarted = true;
    }
    
    public void addSelectionInterval(int index0, int index1) {
        if (isSelectedIndex(index0) && !gestureStarted) {
            super.removeSelectionInterval(index0, index1);
        }
        else {
            super.addSelectionInterval(index0, index1);
        }
        gestureStarted = true;
    }
    
    public void removeSelectionInterval(int index0, int index1) {
        if (isSelectedIndex(index0) && !gestureStarted) {
            super.removeSelectionInterval(index0, index1);
        }
        else {
            super.addSelectionInterval(index0, index1);
        }
        gestureStarted = true;
    }
    
    public void setValueIsAdjusting(boolean isAdjusting) {
        if (isAdjusting == false) {
            gestureStarted = false;
        }
        super.setValueIsAdjusting(isAdjusting);
    }
    
    public void clearSelection() {
        super.removeSelectionInterval( getMinSelectionIndex(), getMaxSelectionIndex());
    }
}
