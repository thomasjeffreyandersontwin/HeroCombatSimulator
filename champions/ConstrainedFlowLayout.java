/*
 * ConstrainedFlowLayout.java
 *
 * Created on June 5, 2001, 10:27 PM
 */

package champions;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author  twalker
 * @version 
 */
public class ConstrainedFlowLayout extends FlowLayout 
implements LayoutManager, SwingConstants {

  //  private int alignment;
 //   private int hgap;
 //   private int vgap;
    private int direction;
    
    /** Creates new ConstrainedFlowLayout */
    public ConstrainedFlowLayout(int alignment, int hgap, int vgap, int direction) {
        super(alignment,hgap,vgap);
        //this.alignment = alignment;
       // this.hgap = hgap;
        //this.vgap = vgap;
        this.direction = direction;
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
        Dimension d = parent.getSize();
        int i;
        if ( direction == VERTICAL || d.width <= 0 ) {
            // Add the widths of the components and keep track of the highest height
            int width = insets.left + insets.right;
            int height = insets.top + insets.bottom;
            Component[] c = parent.getComponents();
            for(i=0;i<c.length;i++) {
                if ( c[i].isVisible() == false ) continue;
                d = c[i].getPreferredSize();
                width += d.width;
                if ( i > 0 ) width += getHgap();
                if ( d.height + insets.top + insets.bottom > height ) height = d.height + insets.top + insets.bottom;
            }
            d.width = width;
            d.height = height;
            return d;
        }
        else {
            // Non-Trivial Case, since Flow is Horizontally Constrained and parent size > 0;
            // Arrange components such that no component flows past parent size
            
            int rowHeight = 0;
            int totalHeight = 0;
            int column = 0;
            int totalRows = 0;
            int maxWidth = 0; //d.width;
            int usableSize =  d.width - insets.left - insets.right;
            int rowWidth = usableSize + 1; // Start with bad value to force row change immediately
            
            Component[] c = parent.getComponents();
            for(i=0;i<c.length;i++) {
                if ( c[i].isVisible() == false ) continue;
                d = c[i].getPreferredSize();
                if ( rowWidth + getHgap() + d.width <= usableSize ) {
                    // Another element fits on this row
                    rowWidth += d.width + getHgap();
                    //System.out.println( Integer.toString(rowWidth));
                    if ( d.height > rowHeight ) rowHeight = d.height;
                    column++; // Don't really care about the number of columns after the first
                    if ( rowWidth + insets.left +  insets.right > maxWidth ) maxWidth = rowWidth + insets.left + insets.right;
                }
                else {
                    // Record the total height
                    totalHeight += rowHeight;
                    if ( totalRows > 1 ) totalHeight += getVgap();
                                        
                    // Ran out of room, have to start a new row
                    totalRows++;
                    
                    // Reset variables for next row and add next element
                    rowWidth = d.width;
                    //System.out.println("New Row: " + Integer.toString(rowWidth) );
                    rowHeight = d.height;
                    column++;
                    // If the row with only one element is greater then the parent width, return this elements
                    // width as the preferred.
                    if ( rowWidth + insets.left +  insets.right > maxWidth ) maxWidth = rowWidth + insets.left + insets.right;
                }
            }
            // Add Height for last row
            totalHeight += rowHeight;
            if ( totalRows > 1 ) totalHeight += getVgap();
            
         ///   System.out.println("Returning constrained pref size: " + Integer.toString(maxWidth) +"," + Integer.toString(totalHeight) +
         //   ". Rows: " + Integer.toString(totalRows) + " Usable: " + Integer.toString(usableSize));
            
            // Add the insets for the border
            totalHeight += insets.top + insets.bottom;
            

            d = new Dimension(maxWidth, totalHeight);
            return d;
        }
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
   
    
}
