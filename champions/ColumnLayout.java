/*
 * PADLayout.java
 *
 * Created on October 9, 2000, 6:46 PM
 */

package champions;


import champions.event.LayoutEvent;
import tjava.*;
import champions.interfaces.Columned;
import champions.interfaces.LayoutListener;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.beans.*;
/**
 *
 * @author  unknown
 * @version
 */
public class ColumnLayout extends Object 
implements LayoutManager, Columned, PropertyChangeListener {

    /** Holds value of property hgap. */
    private int hgap = 3;
    /** Holds value of property columns. */
    private int columns = 1;
    /** Holds value of property visibleCount. */
    private int visibleCount;
    /** Holds value of property columnWidth. */
    private int columnWidth = 150;
    /** Holds value of property vgap. */
    private int vgap = 3;
    /** Holds value of property rows. */
    private int rows;
    /** Holds value of property fixedWidth. */
    private boolean fixedWidth = true;
    /** Holds value of property fixedColumns. */
    private boolean fixedColumns = false;
    /** Holds value of property orientation. */
    private int orientation = 0;
    
    private ColumnList columnList = null;

    private EventListenerList listenerList;

    static public final int VERTICAL = 0;
    static public final int HORIZONTAL = 1;
    /** Creates new PADLayout */
    public ColumnLayout() {
        listenerList = new EventListenerList();
    }


    public ColumnLayout(int columns, int width, int hgap, int vgap) {
        if ( columns == -1 ) {
            fixedColumns = false;
        }
        else {
            fixedColumns = true;
            this.columns = columns;
        }

        if ( width == -1 ) {
            fixedWidth = false;
        }
        else {
            fixedWidth = true;
            this.columnWidth = width;
        }
        this.hgap = hgap;
        this.vgap = vgap;
        
        listenerList = new EventListenerList();
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

        int i,j;

        //Dimension currentSize;
        Dimension preferredSize = new Dimension();

        Insets insets = parent.getInsets();

        // Determine the Number of Columns and the preferred width of the layout
        //int availableWidth = currentSize.width - insets.left - insets.right - columnWidth;

        if ( fixedWidth == true && fixedColumns == true) {
            // Width is fixed.  Assume smallest size with only one column
            preferredSize.width = insets.left + insets.right + columnWidth * columns + hgap * ( columns - 1);
        }
        else if ( fixedWidth == true ) {
            // Only the width is fixed.  Assume one column.
            preferredSize.width = insets.left + insets.right + columnWidth;
        }
        else {
            // Make due with very small columns
            preferredSize.width = insets.left + insets.right;
        }

        // Count the Visible Components
        Component[] component = parent.getComponents();

        visibleCount = 0;
        for (i = 0; i < component.length; i++ ) {
            if ( component[i].isVisible() ) {
                visibleCount += 1;
            }
        }

        if ( fixedColumns ) {
            rows = visibleCount / columns;
            if ( rows * columns < visibleCount ) rows+=1;
        }
        else {
            rows = visibleCount;
        }

        // Layout Components so we know the Real Height
        preferredSize.height = insets.top + insets.bottom;

       // System.out.println( "PrfInsets: " + insets.toString());
        int columnHeight = insets.top + insets.bottom;
        int currentRow = 0;

        for (i=0;i<component.length;i++) {
            if ( currentRow >= rows ) {
                // Start a new column
                if ( columnHeight > preferredSize.height )  preferredSize.height = columnHeight;

                columnHeight = insets.top + insets.bottom;
                currentRow = 0;
            }

            if ( component[i].isVisible() ) {
                columnHeight += vgap + component[i].getPreferredSize().height;
                currentRow++;
               // System.out.println ( "PreComponent [" + Integer.toString(i) + "]: " + component[i].getPreferredSize().toString() );
            }
        }

        if ( columnHeight > preferredSize.height )  preferredSize.height = columnHeight;

      //  System.out.println ( "Preferred Size: " + preferredSize.toString() );
        
        return preferredSize;
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
            int i,j;
            int x,y;

            Dimension currentSize,vpSize;

            currentSize = parent.getSize();
            Insets insets = parent.getInsets();
                        
            if ( parent.getParent() instanceof JViewport ) {
                boolean changeSize = false;
                JViewport jv = (JViewport)parent.getParent();
                vpSize = jv.getSize();
                //System.out.println("I am in a viewport of size " + vpSize + " but I am only " + currentSize);
                if ( currentSize.height < vpSize.height ) {
                    currentSize.height = vpSize.height;
                    changeSize = true;
                }
                if ( currentSize.width < vpSize.width ) {
                    currentSize.width = vpSize.width;
                    changeSize = true;
                }
                if ( changeSize ) {
                    jv.setViewSize(currentSize);
                }  
            }

            // Determine the Number of Columns and the preferred width of the layout
            int availableWidth = currentSize.width - insets.left - insets.right;
            int availableHeight = currentSize.height - insets.top - insets.bottom;
            
            if ( fixedWidth == true && fixedColumns == true) {
                // Width and columns is fixed
                //preferredSize.width = insets.left + insets.right + columnWidth * columns + hgap * ( columns - 1);
                // No need to do anything
            }
            else if ( fixedWidth == true ) {
                // Only the width is fixed.  Figure out how many columns
                columns = 1;
                columns += ( availableWidth - columnWidth ) / ( hgap + columnWidth );
            }
            else if ( fixedColumns == true ) {
                // Columns is fixed
                availableWidth -= hgap * ( columns - 1 );
                columnWidth = availableWidth / columns;
            }
            else {
                // Neither is fixed.  Assume one column
                columns = 1;
                columnWidth = availableWidth;
            }

            // Count the Visible Components
            Component[] component = parent.getComponents();

            visibleCount = 0;
            for (i = 0; i < component.length; i++ ) {
                if ( component[i].isVisible() ) {
                    visibleCount += 1;
                }
            }

            if ( orientation == HORIZONTAL ) {
                rows = visibleCount / columns;

                if ( rows * columns < visibleCount ) rows+=1;


                // Layout Components
                int currentRow = 0;
                int componentHeight;

                x=insets.left;
                y=insets.top;

                for (i=0;i<component.length;i++) {
                    if ( currentRow > rows ) {
                        // Start a new column
                        currentRow = 0;

                        y = insets.top;
                        x += columnWidth + hgap;
                    }

                    componentHeight = component[i].getPreferredSize().height;

                    if ( component[i].isVisible() ) {
                        component[i].setBounds(x,y,columnWidth,componentHeight);
                        y += vgap + componentHeight;
                        currentRow++;
                    }
                }
            }
            else {
                // Layout Components vertical
                int currentRow = 0;
                int componentHeight;

                x=insets.left;
                y=insets.top;

                for (i=0;i<component.length;i++) {
                    if ( component[i].isVisible() == false ) continue;
                    componentHeight = component[i].getPreferredSize().height;
               //     System.out.println ( "LayComponent [" + Integer.toString(i) + "]: " + component[i].getPreferredSize().toString() );

                    if ( y + componentHeight - insets.top > availableHeight   ) {
                        // Start a new column                        
                        y = insets.top;
                        x += columnWidth + hgap;
                    }

                    component[i].setBounds(x,y,columnWidth,componentHeight);
                    y += vgap + componentHeight;
                }

            }
        }
        
  //      System.out.println( this.toString() );
        
        fireLayoutMessage(parent);
    }

    /** Getter for property hgap.
     * @return Value of property hgap.
     */
    public int getHgap() {
        return hgap;
    }
    /** Setter for property hgap.
     * @param hgap New value of property hgap.
     */
    public void setHgap(int hgap) {
        this.hgap = hgap;
    }
    /** Getter for property columnWidth.
     * @return Value of property columnWidth.
     */
    public int getColumnWidth() {
        return columnWidth;
    }
    /** Setter for property columnWidth.
     * @param columnWidth New value of property columnWidth.
     */
    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
        setFixedWidth( true);
    }
    /** Getter for property columns.
     * @return Value of property columns.
     */
    public int getColumns() {
        return columns;
    }

    /** Getter for property columns.
     * @return Value of property columns.
     */
    public void setColumns(int columns) {
        this.columns = columns;
        setFixedColumns(true);
    }
    /** Getter for property visibleCount.
     * @return Value of property visibleCount.
     */
    public int getVisibleCount() {
        return visibleCount;
    }
    /** Setter for property visibleCount.
     * @param visibleCount New value of property visibleCount.
     */
    public void setVisibleCount(int visibleCount) {
    }


    /** Getter for property vgap.
     * @return Value of property vgap.
     */
    public int getVgap() {
        return vgap;
    }
    /** Setter for property vgap.
     * @param vgap New value of property vgap.
     */
    public void setVgap(int vgap) {
        this.vgap = vgap;
    }
    /** Getter for property rows.
     * @return Value of property rows.
     */
    public int getRows() {
        return rows;
    }
    /** Getter for property fixedWidth.
     * @return Value of property fixedWidth.
     */
    public boolean isFixedWidth() {
        return fixedWidth;
    }
    /** Setter for property fixedWidth.
     * @param fixedWidth New value of property fixedWidth.
     */
    public void setFixedWidth(boolean fixedWidth) {
        this.fixedWidth = fixedWidth;
    }
    /** Getter for property fixedColumns.
     * @return Value of property fixedColumns.
     */
    public boolean isFixedColumns() {
        return fixedColumns;
    }
    /** Setter for property fixedColumns.
     * @param fixedColumns New value of property fixedColumns.
     */
    public void setFixedColumns(boolean fixedColumns) {
        this.fixedColumns = fixedColumns;
    }
    /** Getter for property orientation.
     * @return Value of property orientation.
     */
    public int getOrientation() {
        return orientation;
    }
    /** Setter for property orientation.
     * @param orientation New value of property orientation.
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    /**
     *  Adds a <code>Battle</code> listener.
     *
     *  @param l  the <code>BattleListener</code> to add
     */
    public void addLayoutListener(LayoutListener l) {
        listenerList.add(LayoutListener.class,l);
    }

    /**
     * Removes a <code>Battle</code> listener.
     *
     * @param l  the <code>BattleListener</code> to remove
     */
    public void removeLayoutListener(LayoutListener l) {
        listenerList.remove(LayoutListener.class,l);
    }
    
    public String toString() {
        String s = "[Champions.ColumnLayout, Columns:" + Integer.toString(columns) 
            + ",Width:" + Integer.toString(columnWidth) + "]";
        return s;
        
    }

    protected void fireLayoutMessage(final Container parent) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                LayoutEvent e = null;
                // Guaranteed to return a non-null array
                Object[] listeners = listenerList.getListenerList();
                // Process the listeners last to first, notifying
                // those that are interested in this event
                for (int i = listeners.length-2; i>=0; i-=2) {
                    if (listeners[i]==LayoutListener.class) {
                        // Lazily create the event:
                        if (e == null)
                        e = new LayoutEvent(this,parent);
                        ((LayoutListener)listeners[i+1]).layoutPerformed(e);
                    }
                }
            }
        });
    }
    
    public void setColumnList(ColumnList columnList) {
        if ( this.columnList != null ) {
            this.columnList.removePropertyChangeListener(this);
        }
        
        this.columnList = columnList;
        
        if ( columnList != null ) {
            setFixedWidth( true );
            setColumnWidth( columnList.getWidth() );
            this.columnList.addPropertyChangeListener(this);
        }
    }
    
    public ColumnList getColumnList() {
        return columnList;
    }
    /** This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ( columnList != null ) {
            setColumnWidth( columnList.getWidth() );
        }
    }
}