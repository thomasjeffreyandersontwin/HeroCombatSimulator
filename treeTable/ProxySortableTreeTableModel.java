/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treeTable;

import java.beans.PropertyChangeEvent;
import java.util.Comparator;

/**
 *
 * @param <N> Type of node used by the model.
 * @author twalker
 */
public class ProxySortableTreeTableModel<N> extends AbstractProxyTreeTableModel<N, SortableTreeTableModelDelegate<N>> {

    protected int sortColumnIndex = -1;
    
    protected boolean sortAscending = true;

    public ProxySortableTreeTableModel() {
    }

    public ProxySortableTreeTableModel(SortableTreeTableModelDelegate<N> delegateTreeTableModel) {
        super(delegateTreeTableModel);
    }

    @Override
    protected void rebuiltTree() {
        setProxyRoot(new ProxySortableTreeTableNode<N>(delegateTreeTableModel.getRoot(), this));
    }

    @Override
    public ProxySortableTreeTableNode<N> getProxyRoot() {
        return (ProxySortableTreeTableNode<N>)super.getProxyRoot();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getSource() == getDelegateTreeTableModel() ) {
            if ( evt.getPropertyName().equals(SORTABLE_DELEGATE_PROPERTY) ) {
                firePropertyChange(SORTABLE_PROPERTY, evt.getOldValue(), evt.getNewValue());
            }
            else {
                super.propertyChange(evt);
            }
        }
    }

    @Override
    public boolean isSortable() {
        return getDelegateTreeTableModel().isSortableDelegate();
    }

    @Override
    public boolean isSortableDelegate() {
        // We are a sortable tree model, so don't pass the
        // delegate stuff through anymore...
        return false;
    }

    @Override
    public boolean isSorted() {
        return sortColumnIndex != -1;
    }
    
    @Override
    public Comparator<N> getSortComparator(N parentNode, int columnIndex, boolean ascending) {
        // We are a sortable tree model, so don't pass the
        // delegate stuff through anymore...
        return null;
    }

    @Override
    public int getSortColumnIndex() {
        return sortColumnIndex;
    }

    @Override
    public boolean isSortAscending() {
        return sortAscending;
    }

    @Override
    public void setSortOrder(int sortColumnIndex, boolean sortAscending) {
        if ( this.sortColumnIndex != sortColumnIndex || this.sortAscending != sortAscending ) {
            this.sortColumnIndex = sortColumnIndex;
            this.sortAscending = sortAscending;
            
            getProxyRoot().sortCriteriaChanged();
        }
    }

    protected Comparator<N> getDelegateSortComparator(ProxySortableTreeTableNode<N> parentNode) {
        return getDelegateTreeTableModel().getSortComparator(parentNode.delegateNode, sortColumnIndex, sortAscending);
    }

    @Override
    public String toString() {
        return "ProxySortableTreeTableModel [ " + getDelegateTreeTableModel() + "]";
    }

    


    
}
