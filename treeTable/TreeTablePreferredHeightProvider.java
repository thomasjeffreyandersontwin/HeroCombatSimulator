/*
 * TreeTablePreferredHeightProvider.java
 *
 * Created on February 22, 2008, 4:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package treeTable;

/**
 *
 * @author twalker
 */
public interface TreeTablePreferredHeightProvider {
    /** Returns the preferred height of the component given <code>width</code>.
     *
     */
    public int getPreferredHeight(int preferredWidth, TreeTable treeTable, Object node,
                                  boolean isSelected, boolean expanded, 
                                  boolean leaf, int row, int column, boolean hasFocus);
}
