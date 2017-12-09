/*
 * DetailListIndex.java
 *
 * Created on September 21, 2000, 1:36 PM
 */

package dockable;



/** Helper class for transfering detailListIndex numbers as datatransfers
 *
 * @author  unknown
 * @version
 */

public class DockingPanelIndex extends Object implements java.io.Serializable {

    /** Holds value of property index. */
    private Integer index;
    /** Creates new DetailListIndex */
    public DockingPanelIndex(int index) {
        setIndex( new Integer(index) );
    }

    /** Getter for property index.
     * @return Value of property index.
     */
    public int getIndex() {
        return index.intValue();
    }
    /** Setter for property index.
     * @param index New value of property index.
     */
    public void setIndex(Integer index) {
        this.index = index;
    }
}