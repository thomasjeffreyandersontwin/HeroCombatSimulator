/*
 * ColumnHeaderPanel.java
 *
 * Created on October 26, 2000, 5:29 PM
 */

package champions;

import champions.event.LayoutEvent;
import champions.ColumnList;
import champions.interfaces.LayoutListener;
import champions.ColumnLayout;
import javax.swing.*;
import java.util.*;
import java.awt.*;

import javax.swing.border.LineBorder;

import champions.event.*;
import champions.interfaces.*;
/**
 *
 * @author  unknown
 * @version
 */
public class ColumnHeaderPanel extends JPanel
implements ColumnLayoutHeader, LayoutListener {

    private ColumnList columnList = new ColumnList();
    private int columns;
    private int columnWidth;
    private int hgap;
    /** Holds value of property headerInsets. */
    private Insets headerInsets = new Insets(2,15,0,0);

    /** Holds value of property headerClass. */
    private Class headerClass = ColumnHeader.class;

    /** Holds value of property headerVector. */
    private Vector headerVector = new Vector();
    
    private Dimension preferredSize = new Dimension(0,16);

    /** Holds value of property resizable. */
    private boolean resizable = false;
    /** Holds value of property columnLayout. */
    private ColumnLayout columnLayout;
    /** Holds value of property dynamicAdjust. */
    private boolean dynamicAdjust = true;
    /** Creates new ColumnHeaderPanel */
    public ColumnHeaderPanel() {
        setLayout( null );
        
        Insets insets = getInsets();
        FontMetrics fm = getFontMetrics(getFont() );
        preferredSize.height = fm.getHeight() + insets.top + insets.bottom;
    }

    public void setColumnList(ColumnList detailList) {
        int index;
        for ( index=0; index < headerVector.size(); index ++ ) {
            ((ColumnHeader)headerVector.get(index)).setColumnList(detailList);
        }
        columnList = detailList;
    }

    public ColumnList getColumnList() {
        return columnList;
    }
    
   public Dimension getPreferredSize() {
        return preferredSize;
    } 

    public void setLayoutInfo(int columns,int columnWidth,int hgap) {
        if ( this.columns != columns || this.columnWidth != columnWidth ||  this.hgap != hgap ) {
            this.columns = columns;
            this.columnWidth = columnWidth;
            this.hgap = hgap;
            adjustColumnHeaders();
        }
    }

    public void adjustColumnHeaders() {
        ColumnHeader columnHeader;
       /* if ( columnLayout != null ) {
            columns = columnLayout.getColumns();
            columnWidth = columnLayout.getColumns();
        }*/
        
        int i=0;

        int x = 0;
        int y = headerInsets.top;
        //int height = getHeight() - headerInsets.top - headerInsets.bottom;
        int height = getFontMetrics( getFont() ).getHeight() + 5 - headerInsets.top - headerInsets.bottom;
        int width = columnWidth - headerInsets.left - headerInsets.right;
        


        setLayout(null);
        for (i=0; i < columns; i++) {
            if ( headerVector.size() <= i ) {
                try {
                    columnHeader = (ColumnHeader) headerClass.newInstance();
                }
                catch ( Exception except ) {
                    continue;
                }
                
                headerVector.add(columnHeader);
                
                this.add(columnHeader);
            }
            columnHeader = (ColumnHeader)headerVector.get(i);
          //  columnHeader.setBorder( BorderFactory.createLineBorder(Color.red));
            columnHeader.setColumnList(columnList);
            columnHeader.setBounds(x + headerInsets.left, y, width+1, height);
            columnHeader.setResizable(resizable);
            columnHeader.setColumnLayout(columnLayout);
            columnHeader.setDynamicAdjust(dynamicAdjust);
            columnHeader.setFont(getFont());
            columnHeader.setForeground( getForeground() );
            columnHeader.setBackground( getBackground() );
            columnHeader.setVisible( true );
            
            //columnHeader.setBorder( new LineBorder(Color.orange));
            columnHeader.repaint();

            x += columnWidth + hgap;
        }

        for (; i < headerVector.size(); i++ ) {
            columnHeader = (ColumnHeader)headerVector.get(i);
            columnHeader.setVisible(false);
        }

        this.setMinimumSize( new Dimension(0, height) );
        //this.revalidate();
    }

    /** Getter for property headerInsets.
     * @return Value of property headerInsets.
     */
    public Insets getHeaderInsets() {
        return headerInsets;
    }
    /** Setter for property headerInsets.
     * @param headerInsets New value of property headerInsets.
     */
    public void setHeaderInsets(Insets headerInsets) {
        this.headerInsets = headerInsets;
    }
    /** Getter for property headerClass.
     * @return Value of property headerClass.
     */
    public Class getHeaderClass() {
        return headerClass;
    }
    /** Setter for property headerClass.
     * @param headerClass New value of property headerClass.
     */
    public void setHeaderClass(Class headerClass) {
        this.headerClass = headerClass;
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
        Iterator i = headerVector.iterator();
        while ( i.hasNext()) {
            ((ColumnHeader)i.next()).setResizable(resizable);
        }
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
        if ( this.columnLayout != null ) {
            this.columnLayout.removeLayoutListener(this);
        }
        
        this.columnLayout = columnLayout;

        if ( this.columnLayout != null ) {
            adjustColumnHeaders();
            this.columnLayout.addLayoutListener(this);
        }
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
        this.columnLayout = columnLayout;
        Iterator i = headerVector.iterator();
        while ( i.hasNext()) {
            ((ColumnHeader)i.next()).setDynamicAdjust(dynamicAdjust);
        }
    }
    
    public void layoutPerformed(LayoutEvent e) {
        //System.out.println( "Layout performed by" + columnLayout.toString() );
        columns = columnLayout.getColumns();
        columnWidth = columnLayout.getColumnWidth();
        hgap = columnLayout.getHgap();
        adjustColumnHeaders();
        
    }
}