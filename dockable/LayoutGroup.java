package dockable;

import java.awt.*;
import javax.swing.*;

public class LayoutGroup extends Object implements LayoutObject {
    private LayoutObject sideA = null;
    private LayoutObject sideB = null;
    private int orientation;

    private static final int HORIZONTAL = JSplitPane.VERTICAL_SPLIT;
    private static final int VERTICAL = JSplitPane.HORIZONTAL_SPLIT;

    /** Holds value of property dividerSize. */
    private int dividerSize = 3;
    public LayoutGroup(LayoutObject a, LayoutObject b) {
        add(a);
        add(b);
    }

    public LayoutGroup(LayoutObject a){
        add(a);
    }

    public LayoutGroup() {

    }

    public Rectangle getLayoutBounds() {
        if ( sideA == null ) return new Rectangle(0,0,0,0);
        if ( sideB == null ) return sideA.getLayoutBounds();
        Rectangle r = new Rectangle();
        Rectangle a = sideA.getLayoutBounds();
        Rectangle b = sideB.getLayoutBounds();
        r.x = a.x < b.x ? a.x : b.x;
        r.y = a.y < b.y ? a.y : b.y;
        r.width = ( (a.x + a.width) > (b.x + b.width) ? (a.x + a.width) : (b.x + b.width) ) - r.x;
        r.height = ( a.y + a.height > b.y + b.height ? a.y + a.height : b.y + b.height ) - r.y;
        //Rectangle r = sideA.getBounds();
        //r.add( sideB.getBounds() );
        return r;
    }

    public int getLayoutWidth() {
        int min = 0;
        if ( sideA != null ) min += sideA.getLayoutWidth();
        if ( sideB != null ) min += sideB.getLayoutWidth();
        return min;
    }

    public int getLayoutHeight() {
        int min = 0;
        if ( sideA != null ) min += sideA.getLayoutHeight();
        if ( sideB != null ) min += sideB.getLayoutHeight();
        return min;
    }

    protected void add(LayoutObject o) {
        if ( sideA == null ) {
            sideA = o;
        }
        else {
            Rectangle boundsA = sideA.getLayoutBounds();
            Rectangle boundsB = o.getLayoutBounds();

            if ( boundsA.x + boundsA.width  <= boundsB.x ) {
                sideB = o;
                orientation = VERTICAL;
            }
            else if ( boundsB.x + boundsB.width  <= boundsA.x ) {
                sideB = sideA;
                sideA = o;
                orientation = VERTICAL;
            }
            else if ( boundsA.y + boundsA.height  <= boundsB.y ) {
                sideB = o;
                orientation = HORIZONTAL;
            }
            else {
                sideB = sideA;
                sideA = o;
                orientation = HORIZONTAL;
            }
        }
    }

    public boolean intersects(LayoutObject o) {
        if ( o == null ) return false;
        return  getLayoutBounds().intersects(o.getLayoutBounds() );
    }

    public String toString() {
        String s = ( orientation == VERTICAL ) ? "(|)" : "(-)";
        return "LO" + s + ":[" + sideA + "," + sideB + "]";
    }

    public LayoutObject getA() {
        return sideA;
    }

    public LayoutObject getB() {
        return sideB;
    }

    public Component getLayoutComponent() {
        if ( sideA == null ) {
            return null;
        }
        else if ( sideB == null ) {
            return sideA.getLayoutComponent();
        }
        else {
            JSplitPane jsp = new DockingSplitPane();
            
            jsp.setDividerSize( dividerSize );
            jsp.setBorder(null);
            jsp.setOrientation( orientation );

            if ( orientation == HORIZONTAL ) {
                jsp.setTopComponent( sideA.getLayoutComponent());
                jsp.setBottomComponent( sideB.getLayoutComponent());
                double ratio = (double)sideA.getLayoutHeight() / (double)( sideA.getLayoutHeight() + sideB.getLayoutHeight() );

                jsp.setDividerLocation( -1 );
                jsp.setResizeWeight( ratio );
                
             //                   System.out.println( "Divider Start " + Integer.toString( jsp.getDividerLocation() ) );
              //  System.out.println( "Splitting " + sideA + "/" + sideB + " with location of " + Double.toString( ratio ));

            }
            else {
                jsp.setLeftComponent( sideA.getLayoutComponent());
                jsp.setRightComponent( sideB.getLayoutComponent() );
                double ratio = (double)sideA.getLayoutWidth() / (double)( sideA.getLayoutWidth() + sideB.getLayoutWidth() ) ;

                jsp.setDividerLocation( -1 );
                jsp.setResizeWeight( ratio );
                
               //                 System.out.println( "Divider Start " + Integer.toString( jsp.getDividerLocation() ) );
               // System.out.println( "Splitting " + sideA + "/" + sideB + " with location of " + Double.toString( ratio ));

            }
            return jsp;
        }
    }
    /** Getter for property dividerSize.
     * @return Value of property dividerSize.
     */
    public int getDividerSize() {
        return dividerSize;
    }
    /** Setter for property dividerSize.
     * @param dividerSize New value of property dividerSize.
     */
    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize;
    }
    public LayoutObject getPreferredSibling() {
        return null;
    }
    
    public boolean isVisible() {
        return true;
    }
}