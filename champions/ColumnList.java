/*
 * ColumnList.java
 *
 * Created on January 21, 2001, 9:50 PM
 */

package champions;

import champions.*;

/**
 *
 * @author  unknown
 * @version 
 */
public class ColumnList extends DetailList {

    /** Creates new ColumnList */
    public ColumnList() {
        setFireChangeByDefault(true);
    }
    
    public int getWidth() {
        int total = 0;
        int index,count;
        Integer width;
        count = this.getIndexedSize("Column");
        for(index=0;index<count;index++) {
            total += this.getIndexedIntegerValue( index, "Column", "WIDTH" ).intValue();
        }
        return total;
    }
    
    public int addColumnInfo(String name, String type, int width, String special) {
        int index = this.createIndexed( "Column","NAME",name,false) ;
        this.addIndexed(index,"Column","TYPE", type ,true,false);
        this.addIndexed(index,"Column","SPECIAL", special , true,false);
        this.addIndexed(index,"Column","WIDTH", new Integer(width),true,false);
        this.fireIndexedChanged("Column");
        return index;
    }

}