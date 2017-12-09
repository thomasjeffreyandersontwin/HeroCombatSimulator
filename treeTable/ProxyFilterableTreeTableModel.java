/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treeTable;

import java.beans.PropertyChangeEvent;
import javax.swing.event.TreeModelEvent;
import tjava.Filter;

/**
 *
 * @param <N>
 * @author twalker
 */
public class ProxyFilterableTreeTableModel<N> extends AbstractProxyTreeTableModel<N, FilterableTreeTableModelDelegate<N>> {

    protected Filter filter;

    protected Object filterObject;

    /** Indicates that the proxy node structure is currently in control.
     *
     * When there is a filter applied, the proxy node structure has to handle all
     * calls to determine the trees information.  However, when there is not filter,
     * those calls are passed through directly to the delegate model.
     *
     * The transition has to be handled carefully.
     */
    protected boolean proxyInCharge = true;

    public ProxyFilterableTreeTableModel(FilterableTreeTableModelDelegate<N> delegateTreeTableModel) {
        super(delegateTreeTableModel);

        updateFilter();
    }

    public ProxyFilterableTreeTableModel() {
    }

    @Override
    protected void rebuiltTree() {
        setProxyRoot(new ProxyFilterableTreeTableNode<N>(delegateTreeTableModel.getRoot(), this));
    }

    @Override
    public ProxyFilterableTreeTableNode<N> getProxyRoot() {
        return (ProxyFilterableTreeTableNode<N>) super.getProxyRoot();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getSource() == getDelegateTreeTableModel() ) {
            if ( evt.getPropertyName().equals(FILTERABLE_DELEGATE_PROPERTY) ) {
                firePropertyChange(FILTERABLE_PROPERTY, evt.getOldValue(), evt.getNewValue());
            }
            else {
                super.propertyChange(evt);
            }
        }
    }

    @Override
    public boolean isFilterable() {
        return getDelegateTreeTableModel().isFilterableDelegate();
    }

    @Override
    public boolean isFilterableDelegate() {
        return false;
    }

    @Override
    public boolean isFiltered() {
        return filter != null;
    }

    @Override
    public void setFilterObject(Object filterObject) {
        if (this.filterObject != filterObject && (this.filterObject == null || this.filterObject.equals(filterObject) == false)) {

            this.filterObject = filterObject;

            if ( DEBUG ) System.out.println("***************************************************************************************");
            if ( DEBUG ) System.out.println("");
            if ( DEBUG ) System.out.println("Setting Filter to: " + filterObject);
            if ( DEBUG ) System.out.println("");
            if ( DEBUG ) System.out.println("***************************************************************************************");
            if ( DEBUG ) System.out.println("");

            updateFilter();
        }
    }

    protected void updateFilter() {
        Filter newFilter = getDelegateTreeTableModel().setupFilter(filterObject);

        if (this.filter != newFilter && (this.filter == null || this.filter.equals(newFilter) == false)) {

            setUpdating(true);

            this.filter = newFilter;

            getProxyRoot().filterCriteriaChanged();

            //proxyInCharge = (newFilter != null);

            setUpdating(false);
        }
    }

//    @Override
//    public void treeNodesChanged(TreeModelEvent e) {
//
//        // If we are holding events, it is because we are rebuilding the filter.
//        // If we are rebuilding the filter, we can ignore all of the events coming
//        // from the delegate model, since they should all be caused by the filter building
//        // process.
//        if (isUpdating() == false) {
//            if (proxyInCharge) {
//                AbstractProxyTreeTableNode<N> proxyNode = getProxyForNode(e.getTreePath().getLastPathComponent());
//                if (proxyNode != null) {
//                    proxyNode.delegateNodesChanged(e);
//                }
//            }
//            else {
//                fireTreeNodesChanged(e.getSource(), e.getPath(), e.getChildIndices(), e.getChildren());
//            }
//        }
//    }
//
//    @Override
//    public void treeNodesInserted(TreeModelEvent e) {
//        // If we are holding events, it is because we are rebuilding the filter.
//        // If we are rebuilding the filter, we can ignore all of the events coming
//        // from the delegate model, since they should all be caused by the filter building
//        // process.
//        if (isUpdating() == false) {
//            if (proxyInCharge) {
//                AbstractProxyTreeTableNode<N> proxyNode = getProxyForNode(e.getTreePath().getLastPathComponent());
//                if (proxyNode != null) {
//                    proxyNode.delegateNodesInserted(e);
//                }
//            }
//            else {
//                fireTreeNodesInserted(e.getSource(), e.getPath(), e.getChildIndices(), e.getChildren());
//            }
//        }
//    }
//
//    @Override
//    public void treeNodesRemoved(TreeModelEvent e) {
//        // If we are holding events, it is because we are rebuilding the filter.
//        // If we are rebuilding the filter, we can ignore all of the events coming
//        // from the delegate model, since they should all be caused by the filter building
//        // process.
//        if (isUpdating() == false) {
//            if (proxyInCharge) {
//                AbstractProxyTreeTableNode<N> proxyNode = getProxyForNode(e.getTreePath().getLastPathComponent());
//                if (proxyNode != null) {
//                    proxyNode.delegateNodesRemoved(e);
//                }
//            }
//            else {
//                fireTreeNodesRemoved(e.getSource(), e.getPath(), e.getChildIndices(), e.getChildren());
//            }
//        }
//    }
//
//    @Override
//    public void treeStructureChanged(TreeModelEvent e) {
//        // If we are holding events, it is because we are rebuilding the filter.
//        // If we are rebuilding the filter, we can ignore all of the events coming
//        // from the delegate model, since they should all be caused by the filter building
//        // process.
//        if (isUpdating() == false) {
//            if (proxyInCharge) {
//                AbstractProxyTreeTableNode<N> proxyNode = getProxyForNode(e.getTreePath().getLastPathComponent());
//                if (proxyNode != null) {
//                    proxyNode.delegateStructureChanged(e);
//                }
//            }
//            else {
//                fireTreeStructureChanged(e.getSource(), e.getPath(), e.getChildIndices(), e.getChildren());
//            }
//        }
//    }
//
//    @Override
//    public N getChild(Object parent, int index) {
//        if (proxyInCharge) {
//            AbstractProxyTreeTableNode<N> proxyNode = getProxyForNode(parent);
//            if (proxyNode != null) {
//                return proxyNode.getChildAt(index).delegateNode;
//            }
//            else {
//                return null; // Hmm...this probably shouldn't be happening!
//            }
//        }
//        else {
//            return getDelegateTreeTableModel().getChild(parent, index);
//        }
//    }
//
//    @Override
//    public int getChildCount(Object parent) {
//        if (proxyInCharge) {
//            AbstractProxyTreeTableNode<N> proxyNode = getProxyForNode(parent);
//            if (proxyNode != null && proxyNode.built) {
//                return proxyNode.getChildCount();
//            }
//            else {
//                return 0; // Hmm...this probably shouldn't be happening!
//            }
//        }
//        else {
//            return getDelegateTreeTableModel().getChildCount(parent);
//        }
//    }
//
//    @Override
//    public int getIndexOfChild(Object parent, Object child) {
//        if (proxyInCharge) {
//            AbstractProxyTreeTableNode<N> proxyParentNode = getProxyForNode(parent);
//            if (proxyParentNode != null && proxyParentNode.built) {
//                AbstractProxyTreeTableNode<N> proxyChildNode = getProxyForNode(child);
//                return proxyParentNode.getIndex(proxyChildNode);
//            }
//            else {
//                return -1; // Hmm...this probably shouldn't be happening!
//            }
//        }
//        else {
//            return getDelegateTreeTableModel().getIndexOfChild(parent, child);
//        }
//    }
//
//    @Override
//    public boolean isLeaf(Object node) {
//        if (proxyInCharge) {
//            AbstractProxyTreeTableNode<N> proxyNode = getProxyForNode(node);
//            if (proxyNode != null && proxyNode.built) {
//                return proxyNode.isLeaf();
//            }
//            else {
//                return false; // Hmm...this probably shouldn't be happening!
//            }
//        }
//        else {
//            return getDelegateTreeTableModel().isLeaf(node);
//        }
//    }

    @Override
    public String toString() {
        return "ProxyFilterableTreeTableModel [ " + getDelegateTreeTableModel() + "]";
    }


}
