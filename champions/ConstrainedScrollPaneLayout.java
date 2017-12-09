/*
 * ConstrainedScrollPaneLayout.java
 *
 * Created on June 5, 2001, 7:59 PM
 */

package champions;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author  twalker
 * @version 
 */
public class ConstrainedScrollPaneLayout extends Object
implements LayoutManager, SwingConstants, ComponentListener {

    /** Holds value of property direction. */
    protected int direction;
    protected JScrollPane scrollPane;
    protected Container container;
    
    /** Creates new ConstrainedScrollPaneLayout */
    public ConstrainedScrollPaneLayout(JScrollPane pane, Container container, int direction) {
        setDirection(direction);
        scrollPane = pane;
        this.container = container;
        
        scrollPane.addComponentListener(this);  
        if ( scrollPane.getViewport() != null ) {
            scrollPane.getViewport().addComponentListener(this);  
        }
        
    }

    /**
     * Adds the specified component with the specified name to
     * the layout.
     * @param name the component name
     * @param comp the component to be added
 */
    public void addLayoutComponent(String name,Component comp) {
    }
    
    /**
     * Removes the specified component from the layout.
     * @param comp the component to be removed
 */
    public void removeLayoutComponent(Component comp) {
    }
    
    /**
     * Calculates the preferred size dimensions for the specified 
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     * 
     * @see #minimumLayoutSize
 */
    public Dimension preferredLayoutSize(Container parent) {
        Insets insets = parent.getInsets();
        Component[] c = parent.getComponents();
        int i;
        int height = 0;
        int width = 0;
        Dimension d;
        for(i=0;i<c.length;i++) {
            if ( c[i].isVisible() == false ) continue;
            d = c[i].getPreferredSize();
            if ( direction == HORIZONTAL ) {
                // Constrained Horizontally.  Add the verticals heights
                height += d.height;
                if (d.width > width ) width = d.width;
            }
            else {
                // Constrained vertically.  Add the Horizontal widths.
                width += d.width;
                if (d.height > height ) height = d.height;
            }
        }
        
        d = scrollPane.getViewport().getSize();
        if ( direction == HORIZONTAL ) {
            d.height = height + insets.top + insets.bottom;
        }
        else {
            d.width = width + insets.left + insets.right;
        }
        
       // System.out.println("ConstrainedSPL size: " + Integer.toString(d.width) + ", " + Integer.toString(d.height));
        return d;
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
     * Lays out the container in the specified panel.
     * @param parent the component which needs to be laid out 
 */
    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        Component[] c = parent.getComponents();
        int i;

        Dimension d,e;
        d = scrollPane.getViewport().getSize();
        int loc = (direction == HORIZONTAL) ? insets.top : insets.left;
        int usableSize = (direction == HORIZONTAL) ? ( d.width - insets.left - insets.right ) 
            : ( d.height - insets.top - insets.bottom );
        
        for(i=0;i<c.length;i++) {
            if ( c[i].isVisible() == false ) continue;
            e = c[i].getPreferredSize();
            if ( direction == HORIZONTAL ) {
                // Constrained Horizontally.  Add the verticals heights
                c[i].setBounds(0,loc,usableSize,e.height);
                loc+=e.height;
            }
            else {
                // Constrained vertically.  Add the Horizontal widths.
                c[i].setBounds(loc,0,e.width,usableSize);
                loc+=e.width;
            }
        }
            
          //  System.out.println("Layout ConstrainedSPL:" + Integer.toString(d.width) + ", " + Integer.toString(d.height));
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
    
    /**
     * Invoked when the component's size changes.
 */
    public void componentResized(ComponentEvent e) {
       // container.invalidate();
       // container.repaint();
      //  System.out.println(e.getSource() + " resized");
        layoutContainer(container);
    }
    
    /**
     * Invoked when the component's position changes.
 */
    public void componentMoved(ComponentEvent e) {
    }
    
    /**
     * Invoked when the component has been made visible.
 */
    public void componentShown(ComponentEvent e) {
    }
    
    /**
     * Invoked when the component has been made invisible.
 */
    public void componentHidden(ComponentEvent e) {
    }
    
}
