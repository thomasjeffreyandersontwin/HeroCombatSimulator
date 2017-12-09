/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treeTable;

import java.util.Comparator;

/**
 *
 * @param <N> Type of nodes being sorted.
 * @author twalker
 */
public interface SortableTreeTableNode<N> {
    /** Returns the Comparator to use while sorting the children of this node.
     *
     * This method should return a comparator that can be used to compare the
     * children of this node.
     *
     * @param columnIndex Current sort column.
     * @param ascending direction of sort.
     * @return Comparator for the nodes.  If null, the nodes will not be sorted
     * and will be displayed in their natural order.
     */
    public Comparator<N> getSortComparator(int columnIndex, boolean ascending);
}
