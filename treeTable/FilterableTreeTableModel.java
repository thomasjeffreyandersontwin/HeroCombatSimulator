/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treeTable;

/**
 *
 * @author twalker
 */
public interface FilterableTreeTableModel {

    public static final String FILTERABLE_PROPERTY = "FILTERABLE_PROPERTY";

    /** Returns whether this FilterableTreeTableModel is filterable.
     *
     * If a FilterableTreeTableModel does not currently want to be filtered,
     * it can return false from this method.  This is useful when models
     * are composed into a hierarchy.
     * <P>
     * If the model has property change support, it should post a
     * FILTERABLE_PROPERTY change event when this value changes.
     *
     * @return true if the model is filterable, false otherwise.
     */
    public boolean isFilterable();

    /** Returns whether this FilterableTreeTableModel is currently filtered.
     *
     * @return true if a filter is currently applied.
     */
    public boolean isFiltered();

    /** Sets the current filter object for the tree.
     *
     * The filter object will depend on the tree that is being filtered.
     * The DefaultTreeTable by default provides a String representation
     * for the filter object, but other implementations may provide
     * abitrary objects.  The Tree and the FilterableTreeTableModel must
     * cordinate to filter the nodes appropriately.
     * <p>
     * The Tree Model can use the filter information in any way it desires.
     * For example, it could use it to eliminate nodes, only showing those
     * that pass the filter criteria.  Or, it may only use the filter to
     * change the selection of objects.
     *
     * @param filterObject object representing the current filter for the tree.
     */
    public void setFilterObject(Object filterObject);
}
