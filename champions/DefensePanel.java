/*
 * DefensePanel.java
 *
 * Created on November 21, 2001, 9:56 PM
 */

package champions;

import champions.enums.DefenseType;
import javax.swing.table.*;

/**
 *
 * @author  twalker
 */
public class DefensePanel extends javax.swing.JPanel {
    
    /** Creates new form DefensePanel */
    public DefensePanel() {
        initComponents();
        
        
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        defenseScroll = new javax.swing.JScrollPane();
        defenseTable = new javax.swing.JTable();

        defenseTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        defenseScroll.setViewportView(defenseTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(defenseScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(defenseScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    /** Getter for property defenseList.
     * @return Value of property defenseList.
     */
    public DefenseList getDefenseList() {
        return defenseList;
    }
    
    /** Setter for property defenseList.
     * @param defenseList New value of property defenseList.
     */
    public void setDefenseList(DefenseList defenseList) {
        
        if ( this.defenseList != defenseList ) {
            this.defenseList = defenseList;
            defenseTable.setModel( new DefensePanel.DefenseTableModel(defenseList));
            setColumnWidths();
        }
        
    }
    
    private void setColumnWidths() {
        TableColumn column = null;
        
        for(int i = 0; i < 7; i++) {
            column = defenseTable.getColumnModel().getColumn(i);
            switch(i) {
                case 0:
                    column.setPreferredWidth(25);
                    break;
                case 1:
                    column.setPreferredWidth(300);
                    break;
                default:
                    column.setPreferredWidth(50);
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane defenseScroll;
    private javax.swing.JTable defenseTable;
    // End of variables declaration//GEN-END:variables
    
    /** Holds value of property defenseList. */
    private DefenseList defenseList;
    
    
    public class DefenseTableModel extends DefaultTableModel
            implements TableModel {
        
        private DefenseList defenseList = null;
        
        public DefenseTableModel(DefenseList dl) {
            setDefenseList(dl);
        }
        /**
         * Returns the value for the cell at <code>columnIndex</code> and
         * <code>rowIndex</code>.
         *
         * @param	rowIndex	the row whose value is to be queried
         * @param	columnIndex 	the column whose value is to be queried
         * @return	the value Object at the specified cell
         */
        public Object getValueAt(int rowIndex, int columnIndex) {
            Object value = null;
            
            
            if ( rowIndex != getRowCount() - 1) {
                
            DefenseListEntry dle = getDefenseList().getDefenseListEntry(rowIndex);
                switch (columnIndex) {
                    case 0:
                        value = new Boolean(dle.isActive());
                        break;
                    case 1:
                        value = dle.getDescription();
                        break;
                    case 2:
                        value = new Integer( dle.getModifier(DefenseType.PD));
                        break;
                    case 3:
                        value = new Integer( dle.getModifier(DefenseType.rPD));
                        break;
                    case 4:
                        value = new Integer( dle.getModifier(DefenseType.ED));
                        break;
                    case 5:
                        value = new Integer( dle.getModifier(DefenseType.rED));
                        break;
                    case 6:
                        value = new Integer( dle.getModifier(DefenseType.MD));
                        break;
                        case 7:
                        value = new Integer( dle.getModifier(DefenseType.POWERDEFENSE));
                        break;
                }
            } else {
                switch (columnIndex) {
                    case 0:
                        value = new Boolean(false);
                        break;
                    case 1:
                        value = "Total Active";
                        break;
                    case 2:
                        value = getDefenseList().getTotalDefenseModifier(DefenseType.PD);
                        if ( value == null ) value = new Integer(0);
                        break;
                    case 3:
                        value = getDefenseList().getTotalDefenseModifier(DefenseType.rPD);
                        if ( value == null ) value = new Integer(0);
                        break;
                    case 4:
                        value = getDefenseList().getTotalDefenseModifier(DefenseType.ED);
                        if ( value == null ) value = new Integer(0);
                        break;
                    case 5:
                        value = getDefenseList().getTotalDefenseModifier(DefenseType.rED);
                        if ( value == null ) value = new Integer(0);
                        break;
                    case 6:
                        value = getDefenseList().getTotalDefenseModifier(DefenseType.MD);
                        if ( value == null ) value = new Integer(0);
                        break;
                        case 7:
                        value = getDefenseList().getTotalDefenseModifier(DefenseType.POWERDEFENSE);
                        if ( value == null ) value = new Integer(0);
                        break;
                }
            }
            return value;
        }
        
        /**
         * Returns the name of the column at <code>columnIndex</code>.  This is used
         * to initialize the table's column header name.  Note: this name does
         * not need to be unique; two columns in a table can have the same name.
         *
         * @param	columnIndex	the index of the column
         * @return  the name of the column
         */
        public String getColumnName(int columnIndex) {
            String columnName = null;
            
            switch (columnIndex) {
                case 0:
                    columnName = "";
                    break;
                case 1:
                    columnName = "Defense";
                    break;
                case 2:
                    columnName = "PD";
                    break;
                case 3:
                    columnName = "rPD";
                    break;
                case 4:
                    columnName = "ED";
                    break;
                case 5:
                    columnName = "rED";
                    break;
                case 6:
                    columnName = "MD";
                    break;
                    case 7:
                    columnName = "Power";
                    break;
            }
            return columnName;
        }
        
        /**
         * Returns the number of rows in the model. A
         * <code>JTable</code> uses this method to determine how many rows it
         * should display.  This method should be quick, as it
         * is called frequently during rendering.
         *
         * @return the number of rows in the model
         * @see #getColumnCount
         */
        public int getRowCount() {
            return (getDefenseList() == null) ? 1 : getDefenseList().getDefenseCount() + 1;
        }
        
        /**
         * Returns true if the cell at <code>rowIndex</code> and
         * <code>columnIndex</code>
         * is editable.  Otherwise, <code>setValueAt</code> on the cell will not
         * change the value of that cell.
         *
         * @param	rowIndex	the row whose value to be queried
         * @param	columnIndex	the column whose value to be queried
         * @return	true if the cell is editable
         * @see #setValueAt
         */
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return  ( columnIndex == 0 || columnIndex >= 2 ) && rowIndex != getRowCount() - 1;
        }
        
        /**
         * Sets the value in the cell at <code>columnIndex</code> and
         * <code>rowIndex</code> to <code>aValue</code>.
         *
         * @param	aValue		 the new value
         * @param	rowIndex	 the row whose value is to be changed
         * @param	columnIndex 	 the column whose value is to be changed
         * @see #getValueAt
         * @see #isCellEditable
         */
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            
            DefenseListEntry dle = getDefenseList().getDefenseListEntry(rowIndex);
            
            switch (columnIndex) {
                case 0:
                    Boolean b = (Boolean)aValue;
                    dle.setActive( b );
                    break;
                case 1:
                    //   value = getIndexedValue(rowIndex, "Defense", "DESCRIPTION");
                    break;
                case 2:
                    dle.setModifier(DefenseType.PD, (Integer)aValue);
                    break;
                case 3:
                    dle.setModifier(DefenseType.rPD, (Integer)aValue);
                    break;
                case 4:
                    dle.setModifier(DefenseType.ED, (Integer)aValue);
                    break;
                case 5:
                    dle.setModifier(DefenseType.rED, (Integer)aValue);
                    break;
                case 6:
                    dle.setModifier(DefenseType.MD, (Integer)aValue);
                    break;
                    case 7:
                    dle.setModifier(DefenseType.POWERDEFENSE, (Integer)aValue);
                    break;
            }
            
            int rows = getRowCount();
            this.fireTableRowsUpdated(rows-1,rows-1);
        }
        
        /**
         * Returns the most specific superclass for all the cell values
         * in the column.  This is used by the <code>JTable</code> to set up a
         * default renderer and editor for the column.
         *
         * @param columnIndex  the index of the column
         * @return the common ancestor class of the object values in the model.
         */
        public Class getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Boolean.class;
                case 1:
                    return String.class;
                case 2:
                    return Integer.class;
                case 3:
                    return Integer.class;
                case 4:
                    return Integer.class;
                case 5:
                    return Integer.class;
                case 6:
                    return Integer.class;
                case 7:
                    return Integer.class;
            }
            return null;
        }
        
        /**
         * Returns the number of columns in the model. A
         * <code>JTable</code> uses this method to determine how many columns it
         * should create and display by default.
         *
         * @return the number of columns in the model
         * @see #getRowCount
         */
        public int getColumnCount() {
            return 8;
        }
        
//        public void propertyChange(PropertyChangeEvent evt) {
//            //int rows = getRowCount();
//            //this.fireTableRowsUpdated(rows-1,rows-1);
//        }
        
        public DefenseList getDefenseList() {
            return defenseList;
        }
        
        public void setDefenseList(DefenseList defenseList) {
            if ( defenseList != this.defenseList ) {
                if ( this.defenseList != null ) {
                    //this.defenseList.removePropertyChangeListener(this);
                }
                this.defenseList = defenseList;
                if ( this.defenseList != null ) {
                    //// this.defenseList.addPropertyChangeListener(this);
                }
            }
            
        }
    }
}
