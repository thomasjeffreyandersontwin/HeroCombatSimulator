/*
 * ObjectListModel.java
 *
 * Created on August 4, 2001, 6:20 PM
 */

package champions;

import java.util.List;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import champions.interfaces.MutableListModel;

/**
 *
 * @author  twalker
 * @version 
 */
public class ArrayListModel extends DefaultListModel implements MutableListModel {

    private List array;
    /** Creates new ObjectListModel */
    public ArrayListModel(Object[] array) {
        this.array = new ArrayList(array.length);
        for(int i = 0; i < array.length; i++) {
            this.array.add( array[i] );
        }
    }
    
    public ArrayListModel() {
       this.array = new ArrayList(); 
    }

    /**
     * Returns the length of the list.
 */
    public int getSize() {
        return ( array != null ) ? array.size() : 0;
    }
    
    /**
     * Returns the value at the specified index.  
 */
    public Object getElementAt(int index) {
        return array.get(index);
    }
    
    public void addElement(Object element) {
        addElement(element, -1);
    }
    
    public void addElement(Object element, int beforeIndex) {
        int realIndex = beforeIndex == -1 ? array.size() : beforeIndex;
        array.add( realIndex ,  element);
        fireIntervalAdded(this, realIndex, realIndex);
    }    
    
    public Object removeElement(int index) {
        Object o = array.remove(index);
        fireIntervalRemoved(this, index, index);
        return o;
    }
    
    public boolean contains(Object element) {
        return array.contains(element);
    }
    
}
