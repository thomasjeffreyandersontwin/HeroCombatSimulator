/*
 * ParameterLayout.java
 *
 * Created on March 22, 2002, 11:44 PM
 */

package champions.parameterEditor;

import java.awt.*;
import javax.swing.*;
/**
 *
 * @author  Trevor Walker
 * @version 
 */
public class ParameterEditorLayout implements LayoutManager {

    /** Holds value of property direction. */
    private int direction;
    
    /** Creates new ParameterLayout */
    public ParameterEditorLayout(int direction) {
        
    }

    /**
     * Lays out the container in the specified panel.
     * @param parent the component which needs to be laid out
     */
    public void layoutContainer(Container parent) {
        Insets i = parent.getInsets();
        
        int x = i.left;
        int y = i.top;
        
        Dimension d = parent.getSize();
        Dimension p;
        
        Component[] c = parent.getComponents();
        int index;
        for(index = 0; index<c.length; index++) {
            p = c[index].getPreferredSize();
            c[index].setLocation(x,y);
            
            if ( direction == SwingConstants.HORIZONTAL ) {
                c[index].setSize(p.width, Math.min(d.height, p.height));
                x += p.width;
            }
            else {
                c[index].setSize(Math.min(d.width, p.width), p.height);
                y += p.height;
            }
        }
    }
    
    /**
     * Calculates the preferred size dimensions for the specified
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     *
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(Container parent) {
        Dimension d = new Dimension();
        Dimension p;
        
        Component[] c = parent.getComponents();
        int index;
        for(index = 0; index<c.length; index++) {
            p = c[index].getPreferredSize();
            
            
            if ( direction == SwingConstants.HORIZONTAL ) {
                if ( d.height < p.height ) d.height = p.height;
                d.width += p.width;
            }
            else {
                if ( d.width < p.width ) d.width = p.width;
                d.height += p.height;
            }
        }
        
        Insets i = parent.getInsets();
        d.width += i.left + i.right;
        d.height += i.top + i.bottom;
        
        return d;
    }
    
    /**
     * Adds the specified component with the specified name to
     * the layout.
     * @param name the component name
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name, Component comp) {
    }
    
    /**
     * Calculates the minimum size dimensions for the specified
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }
    
    /**
     * Removes the specified component from the layout.
     * @param comp the component to be removed
     */
    public void removeLayoutComponent(Component comp) {
    }
    
    /** Getter for property direction.
     * @return Value of property direction.
     */
    public int getDirection() {
        return direction;
    }
    
    /** Setter for property direction.
     * @param direction New value of property direction.
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }
    
}
