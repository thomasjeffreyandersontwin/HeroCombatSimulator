/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treeTable;

import java.util.Comparator;

/** This interface should be supported by TreeTableModels which wish to make use of the ProxySortableTreeTableModel.
 *
 * @param <N> Type of node used by the TreeTableModel
 * @author twalker
 */
public interface SortableTreeTableModelDelegate<N> extends TreeTableModel<N> {

    public static final String SORTABLE_DELEGATE_PROPERTY = "SORTABLE_DELEGATE_PROPERTY";
    
    /** Returns whether this TreeTableModel is sortable.
     *
     * A SORTABLE_DELEGATE_PROPERTY property change event should be fired
     * when the value of this changes.
     *
     * @return true if sortable, false otherwise.
     */
    public boolean isSortableDelegate();

    /** Returns the Comparator to use while sorting the children of parentNode.
     *
     * This method should return a comparator that can be used to compare the
     * children of parentNode.
     *
     * @param parentNode Parent of the nodes being sorted.
     * @param columnIndex
     * @param ascending 
     * @return Comparator for the nodes.  If null, the nodes will not be sorted
     * and will be displayed in their natural order.
     */
    public Comparator<N> getSortComparator(N parentNode, int columnIndex, boolean ascending);
}
