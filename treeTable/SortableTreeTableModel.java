/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treeTable;

/**
 *
 * @author twalker
 */
public interface SortableTreeTableModel {

    public static final String SORTABLE_PROPERTY = "SORTABLE_PROPERTY";

    /** Returns whether this treeTableModel is sortable.
     *
     * If the model has property change support, it should post a
     * SORTABLE_PROPERTY change event when this value changes.
     *
     * @return true if sortable, false otherwise.
     */
    public boolean isSortable();

    /** Return whether this treeTableModel is currently sorted.
     *
     * A treeTable is sorted if both isSortable() is true and
     * getSortColumnIndex() != -1.
     * 
     * @return True if the table is currently sorted.
     */
    public boolean isSorted();

    /** Sets the sort order for the TreeTableModel.
     *
     * @param columnIndex Column that should be sorted on.
     * @param ascending Direction of sort.
     */
    public void setSortOrder(int columnIndex, boolean ascending);

    /** Returns the current model index of column the TreeTable is being sorted by.
     *
     * @return Model index of column being sorted on, -1 if none.
     */
    public int getSortColumnIndex();

    /** Returns the current sort direction.
     *
     * This value has no meaning if getSortColumnIndex() returns -1.
     *
     * @return True if the sort is ascending, false if descending.
     */
    public boolean isSortAscending();
}
