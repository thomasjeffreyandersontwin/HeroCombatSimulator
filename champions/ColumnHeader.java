/*
 * TargetButtonStats.java
 *
 * Created on October 22, 2000, 3:28 PM
 */

package champions;

import champions.ColumnList;
import champions.ColumnLayout;
import tjava.ContextMenu;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.beans.*;
import javax.swing.border.*;

import champions.interfaces.*;
/**
 *
 * @author  unknown
 * @version
 */
public class ColumnHeader extends JPanel 
implements PropertyChangeListener, MouseListener, MouseMotionListener {

    /** Holds value of property columnList. */
    protected ColumnList columnList;

    protected Vector columnName;
    protected Vector columnStart;
    protected Vector columnWidth;
    protected Vector columnStat;
    protected Vector columnType;

    protected Vector labelVector;

    protected int dragStart = 0;
    protected int dragColumn = -1;
    protected int dragWidth = 0;

    protected Dimension preferredSize = new Dimension(0,16);

    /** Holds value of property resizable. */
    protected boolean resizable = false;
    /** Holds value of property columnLayout. */
    private ColumnLayout columnLayout;
    /** Holds value of property dynamicAdjust. */
    private boolean dynamicAdjust;
    public ColumnHeader() {
        columnName = new Vector();
        columnStart = new Vector();
        columnWidth = new Vector();
        columnStat = new Vector();
        columnType = new Vector();

        labelVector = new Vector();
        
      //  setName("Header");

        setLayout( null );

        Insets insets = getInsets();
        FontMetrics fm = getFontMetrics(getFont() );
        preferredSize.height = fm.getHeight() + insets.top + insets.bottom + 2;

        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        ContextMenu.addContextMenu(this);
        
       // this.setBorder( new LineBorder( Color.red) );
    }

    public void layoutColumns() {
        int index,count;
        String name;
        JLabel label;

        if ( columnList == null ) return;

        Insets insets = getInsets();
        int x,y,width, height;

        height = getHeight() - insets.top - insets.bottom;

        FontMetrics fm = getFontMetrics(getFont() );

        if ( height == 0 ) {
            height = fm.getHeight() + insets.top + insets.bottom;
        }

        y = insets.top;
        x = insets.left;

        count = columnList.getIndexedSize( "Column" );
        for ( index = 0; index < count; index ++ ) {
            name = columnList.getIndexedStringValue( index, "Column","NAME");
            width = columnList.getIndexedIntegerValue(index, "Column","WIDTH").intValue();
            columnList.addIndexed( index,"Column","START", new Integer(x), true, false);

            if ( index < labelVector.size() ) {
                label = (JLabel)labelVector.get(index);
            }
            else {
                label = new JLabel();
                this.add(label);
                labelVector.add(label);

            }

            label.setBackground( getBackground() );
            label.setForeground( getForeground() );
            label.setFont( getFont() );
            label.setText( name );
            label.setBorder( BorderFactory.createEtchedBorder() );
            label.setBounds(x,y, width, height-1);
            label.setVisible(true);

            x+= width;
        }

        for (;index < labelVector.size(); index++) {
            label = (JLabel)labelVector.get(index);
            label.setVisible(false);
        }

        preferredSize.width = x+1;
        preferredSize.height = fm.getHeight() + insets.top + insets.bottom;

        if ( columnLayout != null ) {
            columnLayout.setColumnWidth(x+1);
        }

        revalidate();
    }

    /** This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        layoutColumns();
    }

    public void setColumnList(ColumnList columnList) {
        if ( this.columnList != null ) {
            this.columnList.removePropertyChangeListener(this );
        }

        this.columnList = columnList;

        if ( this.columnList != null ) {
            this.columnList.addPropertyChangeListener(this);
        }

        layoutColumns();
    }

    public ColumnList getColumnList() {
        return columnList;
    }

    public Dimension getPreferredSize() {
        return preferredSize;
    }
    /** Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(MouseEvent e) {
    }
    /** Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
        if ( ! e.isPopupTrigger() && resizable) {

            int count, index, columnEnd;
            int x = e.getPoint().x;

            count = columnList.getIndexedSize( "Column" );

            for ( index = 0; index < count; index ++ ) {

                columnEnd = columnList.getIndexedIntegerValue(index,"Column","START").intValue() +
                columnList.getIndexedIntegerValue(index,"Column","WIDTH").intValue();

                if ( x > columnEnd - 3 && x < columnEnd + 3 ) {
                    dragColumn = index;
                    dragStart = x;
                    if ( !dynamicAdjust ) columnList.add("Columns.ADJUSTING", "TRUE", true);
                    dragWidth = columnList.getIndexedIntegerValue(index,"Column","WIDTH").intValue();
                    break;
                }
            }


        }
    }
    /** Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
        if ( !dynamicAdjust ) columnList.add("Columns.ADJUSTING", "FALSE", true);
        dragColumn = -1;

    }
    /** Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {
    }
    /** Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
    }
    /** Invoked when a mouse button is pressed on a component and then
     * dragged.  Mouse drag events will continue to be delivered to
     * the component where the first originated until the mouse button is
     * released (regardless of whether the mouse position is within the
     * bounds of the component).
     */
    public void mouseDragged(MouseEvent e) {
        if ( dragColumn != -1  ) {
            int newWidth = dragWidth + e.getPoint().x - dragStart;
            if ( newWidth > 5 ) {
                columnList.addIndexed( dragColumn,  "Column","WIDTH",new Integer(newWidth), true, false);
                layoutColumns();
                columnList.fireIndexedChanged("Column");
            }
        }
    }
    /** Invoked when the mouse button has been moved on a component
     * (with no buttons no down).
     */
    public void mouseMoved(MouseEvent e) {
        int count, index, columnEnd;
        int x = e.getPoint().x;
        Integer i;

        if (  resizable ) {
            count = columnList.getIndexedSize( "Column" );

            for ( index = 0; index < count; index ++ ) {
                columnEnd = columnList.getIndexedIntegerValue(index,"Column","START").intValue() +
                columnList.getIndexedIntegerValue(index,"Column","WIDTH").intValue();

                if ( x > columnEnd - 3 && x < columnEnd + 3 ) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                    return;
                }
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /* public JPopupMenu InvokeMenu(Component inComponent,final Point inPoint) {
    JPopupMenu popup = new JPopupMenu();
    final int column = findColumnAt(inPoint);
    String name = "Column";

    if ( column != -1 ) {
    name = columnList.getIndexedStringValue(column, "Column" , "NAME");
    name = name.concat( " Column");
    }

    popup.add( new AbstractAction( "Remove " + name ) {
    public void actionPerformed(ActionEvent e) {
    removeColumn(column);
    }

    public boolean isEnabled() {
    return (column != -1 );
    }
    });

    JMenu menu = new JMenu( "Insert Column" );

    // Add Stats
    int index;

    for(index = 0; index < Character.statNames.length; index++ ) {
    final String stat = Character.statNames[index];
    menu.add( new AbstractAction( Character.statNames[index] ) {
    public void actionPerformed(ActionEvent e) {
    addColumn("STAT", stat, stat, column);
    }

    public boolean isEnabled() {
    return true;
    }
    });
    }

    popup.add( menu);

    return popup;
    } */

    public int findColumnAt(Point p) {
        int count, index, columnEnd, columnStart;
        int x = p.x;
        Integer i;

        count = columnList.getIndexedSize( "Column" );

        for ( index = 0; index < count; index ++ ) {
            columnStart = columnList.getIndexedIntegerValue(index,"Column","START").intValue();
            columnEnd = columnStart + columnList.getIndexedIntegerValue(index,"Column","WIDTH").intValue();

            if ( x > columnStart && x < columnEnd ) {
                return index;
            }
        }
        return -1;
    }

    public void removeColumn(int column) {

        columnList.removeAllIndexed(column, "Column", false);
        layoutColumns();
        columnList.fireIndexedChanged("Column");
    }

    public void addColumn( String type, String name, String key, int column) {
        int index;

        if ( column != -1 ) {
            index = columnList.createIndexed(column+1,"Column","NAME",name, false);
        }
        else {
            index = columnList.createIndexed("Column","NAME",name, false)  ;
        }

        columnList.addIndexed(index,"Column","TYPE", type ,  true, false);
        columnList.addIndexed(index,"Column","STAT", key,  true, false);
        columnList.addIndexed(index,"Column","WIDTH", new Integer(80) ,  true, false);

        layoutColumns();
        
        columnList.fireIndexedChanged("Column");

    }
    /** Getter for property resizable.
     * @return Value of property resizable.
     */
    public boolean isResizable() {
        return resizable;
    }
    /** Setter for property resizable.
     * @param resizable New value of property resizable.
     */
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }
    /** Getter for property columnLayout.
     * @return Value of property columnLayout.
     */
    public ColumnLayout getColumnLayout() {
        return columnLayout;
    }
    /** Setter for property columnLayout.
     * @param columnLayout New value of property columnLayout.
     */
    public void setColumnLayout(ColumnLayout columnLayout) {
        this.columnLayout = columnLayout;
    }
    /** Getter for property dynamicAdjust.
     * @return Value of property dynamicAdjust.
     */
    public boolean isDynamicAdjust() {
        return dynamicAdjust;
    }
    /** Setter for property dynamicAdjust.
     * @param dynamicAdjust New value of property dynamicAdjust.
     */
    public void setDynamicAdjust(boolean dynamicAdjust) {
        this.dynamicAdjust = dynamicAdjust;
    }

    public void setFont(Font font) {
        if ( font != getFont() ) {
            super.setFont(font);
            layoutColumns();
        }
    }

    public void setBackground(Color c) {
        if ( c != getBackground() ) {
            super.setBackground(c);
            layoutColumns();
        }
    }

    public void setForeground(Color c) {
        if ( c != getForeground() ) {
            super.setForeground(c);
            layoutColumns();
        }
    }
}