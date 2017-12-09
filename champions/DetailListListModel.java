/*
 * DetailListListModel.java
 *
 * Created on December 17, 2000, 4:05 PM
 */

package champions;

import champions.interfaces.MutableListModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;

/**
 *
 * @author  unknown
 * @version 
 */
public class DetailListListModel extends AbstractListModel
implements ListModel, PropertyChangeListener {

    /** Holds value of property detailList. */
    private DetailList detailList;
    /** Holds value of property indexName. */
    private String indexName;
    /** Holds value of property indexType. */
    private String indexType;
    
    /** Indicates the List should be static and not automatically updated. */
    private boolean listStatic = false;
    
    /** Creates new DetailListListModel */
    public DetailListListModel(DetailList dl, String indexName, String indexType, boolean listStatic) {
        setDetailList(dl);
        setIndexName(indexName);
        setIndexType(indexType);
        setListStatic(listStatic);
    }
    
        /** Creates new DetailListListModel */
    public DetailListListModel(DetailList dl, String indexName, String indexType) {
        this(dl, indexName, indexType, false);
    }

    /** Returns the length of the list.
     */
    public int getSize() {
        if ( detailList == null ) return 0;
        
        return detailList.getIndexedSize( indexName );
    }
    /** Returns the value at the specified index.
     */
    public Object getElementAt(int index) {
        return detailList.getIndexedValue( index, indexName, indexType );
    }

    /** Getter for property detailList.
     * @return Value of property detailList.
     */
    public DetailList getDetailList() {
        return detailList;
    }
    /** Setter for property detailList.
     * @param detailList New value of property detailList.
     */
    public void setDetailList(DetailList detailList) {
        if ( this.detailList != null ) {
            this.detailList.removePropertyChangeListener(this);
        }
        this.detailList = detailList;
        
        if ( listStatic == false && this.detailList != null ) {
            this.detailList.addPropertyChangeListener(this);
        }
    }
    /** Getter for property indexName.
     * @return Value of property indexName.
     */
    public String getIndexName() {
        return indexName;
    }
    /** Setter for property indexName.
     * @param indexName New value of property indexName.
     */
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
    /** Getter for property indexType.
     * @return Value of property indexType.
     */
    public String getIndexType() {
        return indexType;
    }
    /** Setter for property indexType.
     * @param indexType New value of property indexType.
     */
    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }
    /** This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getPropertyName().startsWith(indexName) ) {
            fireContentsChanged(this,0,getSize());
        }
    }
    
    /** Getter for property listStatic.
     * @return Value of property listStatic.
     *
     */
    public boolean isListStatic() {
        return listStatic;
    }
    
    /** Setter for property listStatic.
     * @param listStatic New value of property listStatic.
     *
     */
    public void setListStatic(boolean listStatic) {
        this.listStatic = listStatic;
    }
    
}