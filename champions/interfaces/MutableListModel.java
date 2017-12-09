/*
 * MutableListModel.java
 *
 * Created on November 11, 2003, 6:04 PM
 */

package champions.interfaces;

import javax.swing.ListModel;

/**
 *
 * @author  1425
 */
public interface MutableListModel extends ListModel {
    /** Indicates an elements should be inserted into the list at
     * position <code>index</code>.  
     *
     * An index == -1 indicates that the element should be added at the
     * end of the list.
     */
    public void addElement(Object element, int beforeIndex);
    
    /** Removes the element at index <code>index</code>.  
     *
     * The removed object is returned.
     */
    public Object removeElement(int index);
}
