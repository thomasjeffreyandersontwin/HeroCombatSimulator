/*
 * PADLayout.java
 *
 * Created on October 9, 2000, 6:46 PM
 */

package champions;

import java.awt.*;
/**
 *
 * @author  unknown
 * @version
 */
public class PADLayout extends Object
implements LayoutManager2 {

    /** Creates new PADLayout */
    public PADLayout() {
    }


    /** Adds the specified component with the specified name to
     * the layout.
     * @param name the component name
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name,Component comp) {
        // Do nothing

    }
    /** Removes the specified component from the layout.
     * @param comp the component to be removed
     */
    public void removeLayoutComponent(Component comp) {
        // Do nothing

    }
    /** Calculates the preferred size dimensions for the specified
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     *
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(Container parent) {

        int height, width;

        height = 0;
        width = 0;


        Insets insets = parent.getInsets();
        int ncomponents = parent.getComponentCount();

        // Total parent dimensions
        height += (insets.top + insets.bottom);
        width += (insets.left + insets.right);

        for ( int i = 0; i < ncomponents; i++ ) {
            Component c = parent.getComponent(i);
            Dimension d = c.getPreferredSize();

            if ( c.isVisible() ) {
                if ( d.width + 4 + insets.left + insets.right > width )
                width = d.width + 4 +  insets.left + insets.right;
                height += d.height; // + i.top + i.bottom;
            }
        }

        return new Dimension( width, height);
    }
    /** Calculates the minimum size dimensions for the specified
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container parent) {
        return this.preferredLayoutSize(parent);
    }
    /** Lays out the container in the specified panel.
     * @param parent the component which needs to be laid out
     */
    public void layoutContainer(Container parent) {


        synchronized (parent.getTreeLock()) {
            int x, y, width;

            x = 0;
            y = 0;
            width = 0;

            Insets insets = parent.getInsets();
            Dimension parentDimension = parent.getSize();
            
            int ncomponents = parent.getComponentCount();

            if (ncomponents == 0) {
                return;
            }

            // Total parent dimensions
            y += insets.top;
            x += insets.left;

            for ( int i = 0; i < ncomponents; i++ ) {
                Component c = parent.getComponent(i);
                Dimension d = c.getPreferredSize();
                if ( c.isVisible() ) {

                    if ( d.width > width )
                    width = d.width ;
                }
            }
            
            if ( width < parentDimension.width - insets.left - insets.right - 4 ) {
                width = parentDimension.width - insets.left - insets.right - 4;
            }

            for ( int i = 0; i < ncomponents; i++ ) {
                Component c = parent.getComponent(i);
                if ( c.isVisible() ) {
                    Dimension d = c.getPreferredSize();
                    //Insets i = c.getInsets();
                    c.setBounds(x + 2, y , width, d.height );

                    //x += d.width;
                    y += d.height;
                }
            }
        }
    }
    /** Adds the specified component to the layout, using the specified
     * constraint object.
     * @param comp the component to be added
     * @param constraints  where/how the component is added to the layout.
     */
    public void addLayoutComponent(Component comp,Object constraints) {
        
    }
    /** Returns the maximum size of this component.
     * @see java.awt.Component#getMinimumSize()
     * @see java.awt.Component#getPreferredSize()
     * @see LayoutManager

     */
    public Dimension maximumLayoutSize(Container target) {
        
        return this.preferredLayoutSize(target);
    }
    /** Returns the alignment along the x axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentX(Container target) {
       
        return 0;
    }
    /** Returns the alignment along the y axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentY(Container target) {
        
        return 0;
    }
    /** Invalidates the layout, indicating that if the layout manager
     * has cached information it should be discarded.
     */
    public void invalidateLayout(Container target) {
      
        //this.layoutContainer(target);
    }
}