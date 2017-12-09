/*
 * ENDSourceList.java
 *
 * Created on May 4, 2001, 3:29 PM
 */

package champions;

import javax.swing.*;
import java.beans.*;
/**
 *
 * @author  twalker
 * @version 
 */
public class ENDSourceComboBoxModel extends AbstractListModel implements ComboBoxModel, PropertyChangeListener {
    
    private Object selectedItem = null;
    
    private Target source = null;
    /** Creates new ENDSourceList */
    public ENDSourceComboBoxModel() {

    }

    public int getSize() {
        int size = 1;
        if ( source != null ) {
            size += source.getIndexedSize( "ENDSource" );
        }
        
        return size;
    }
    
    /** Returns the value at the specified index.
     */
    public Object getElementAt(int index) {
        
        if ( index == 0 ) return "Character";
        
        if ( source != null ) {
            return source.getIndexedValue( index-1, "ENDSource", "NAME" );
        }
        
        return null;
    }
    
    /** This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getPropertyName().startsWith("ENDSource") ) {
            fireContentsChanged(this,0,getSize());
        }
    }
    
    public void setSelectedItem(java.lang.Object item) {
        selectedItem = item;
    }
    
    public java.lang.Object getSelectedItem() {
        return selectedItem;
    }  
    
    public Target getSource() {
        return source;
    }
    
    public void setSource(Target source) {
        if ( this.source != null ) {
            this.source.removePropertyChangeListener(this);
        }
        
        this.source = source;
        fireContentsChanged(this,0,getSize());
        
        if ( source != null ) {
            source.addPropertyChangeListener(this);
        }
    }
}
