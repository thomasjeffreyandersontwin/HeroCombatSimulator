/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treeTable;

import tjava.Filter;

/** This interface should be supported by TreeTableModels which wish to make use of the ProxyFilterableTreeTableModel.
 *
 * @param <N> Type of node used by the model.
 * @author twalker
 */
public interface FilterableTreeTableNode<N>  {

    /** Returns true if the node should be filtered from the model.
     *
     * This allows the delegate to customize the handling of filtering, possibly
     * ignoring the filter or filterObject completely.  This method does
     * not need to consider the filtering of its children.  If this method
     * returns false, it is still possible that it will be included if
     * two conditions occur: childrenShouldBeFiltered for this node returns true
     * and one or more of this nodes children remains after filtering.
     * <p>
     * Note: This method should return true if the node should be filtered (ie not
     * included) in the model.  This is the opposite of what Filter.includeElement
     * typically returned.  I did this just so the naming of the method included
     * the word filter and more obviously belonged to this interface.
     *
     * @param filter Filter, as returned by setupFilter, gauranteed non-null.
     * @return true if the node should be included in the model, given the filter.
     */
    public boolean isNodeFiltered(Filter<N> filter);

    /** Returns if the node's children should be filtered.
     *
     * If true, the parentNodes children will be filtered prior calling isNodeFiltered.
     * If false, all children will be included in the model.  The children's children
     * will still be filtered recursively.  Returning true from this method effectively
     * forces the all calls to isNodeFiltered for all children of parentNode to return
     * true.
     *
     * @param filter Filter, as returned by setupFilter, gauranteed non-null.
     * @param isNodeFiltered Whether this node was removed by the filter, as determined
     * by a previous call to isNodeFiltered or by parentNode's parent returning false
     * from childrenShouldBeFiltered.
     * @return true if the children should evaluated for filtered, false if they should all be
     * included without filtering.
     */
    public boolean childrenShouldBeFiltered(Filter<N> filter);

    /** Tells the delegate that the children of the node is about to be filtered.
     *
     * This is called prior to the children of the node being examined for filtering.
     * This will only be called if isNodeFiltered is false for the node or
     * childrenShouldBeFiltered is true for the node.  In other words, this will only be
     * called if there is a reason to filter the children.
     *
     * @param filter Filter, as returned by setupFilter, gauranteed non-null.
     */
    public void willFilterChildren(Filter<N> filter);

    /** Returns whether this node wishes to be pruned if filtering removed all of it's children.
     *
     * This will only be called if parentNode has no children after filtering but
     * would otherwise be included in the model.
     *
     * @param filter Filter, as returned by setupFilter, gauranteed non-null.
     * @return True if this node should be pruned if it has no children.
     */
    public boolean isNodePruned(Filter<N> filter);
}
