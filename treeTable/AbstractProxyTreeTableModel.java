/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treeTable;

import java.awt.Color;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.TimerTask;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import tjava.Destroyable;
import tjava.Filter;
import tjava.SharedTimer;

/** This TreeTableModel wraps a delegate to provide filtering or sorting services to a model.
 *
 * @param <N> Type of nodes used by the TreeTableModel.
 * @param <D> Type of TreeTableModel delegate being wrapped.
 * @author twalker
 */
@SuppressWarnings("deprecation")
public abstract class AbstractProxyTreeTableModel<N, D extends TreeTableModel>
        implements TreeTableModel<N>, FilterableTreeTableModel, SortableTreeTableModel,
                   FilterableTreeTableModelDelegate<N>, SortableTreeTableModelDelegate<N>,
                   TreeModelListener, Destroyable, TreeTableLegacyDnDHandler, TreeTableDnDHandler,
                   PropertyChangeListener {

    protected static final boolean DEBUG = false;

    transient protected EventListenerList listenerList;

    transient protected PropertyChangeSupport propertyChangeSupport;

    private WeakHashMap<N, AbstractProxyTreeTableNode<N>> nodeToProxyMap = null;

    private AbstractProxyTreeTableNode<N> proxyRoot = null;

    protected D delegateTreeTableModel;

    transient private AbstractProxyTreeTableNode<N> lastProxyLookupResult = null;

    /** Indicates that we are holding all event until we are sure the model is stable.
     *
     * While the model is transitioning from proxied to non-proxied, it may not be safe
     * to send out events.  The events may refer to the model that will exist
     * after the transition is complete.  Until that transition is complete, we should hold
     * the events so that the tree doesn't try to query the model at an inopportune time.
     *
     * @param delegateTreeTableModel
     */
    private boolean updating = false;

    private boolean needsUpdate = false;

    private long lastUpdateTime = 0;

    private long millisecondsBetweenUpdates = 250;

    private TimerTask updateTask = null;

    /** List holding events the will be fired once the model is stable.
     *
     */
    private LinkedList<HeldTreeTableEvent> heldEventList = null;

    public AbstractProxyTreeTableModel() {
    }

    public AbstractProxyTreeTableModel(D delegateTreeTableModel) {
        setDelegateTreeTableModel(delegateTreeTableModel);
    }

    /** Rebuilds the tree from scratch.
     * 
     */
    protected abstract void rebuiltTree();

    public D getDelegateTreeTableModel() {
        return delegateTreeTableModel;
    }

    /**
     * @param delegateTreeTableModel the delegateTreeTableModel to set
     */
    public void setDelegateTreeTableModel(D delegateTreeTableModel) {
        if (this.delegateTreeTableModel != delegateTreeTableModel) {
            if (this.delegateTreeTableModel != null) {
                this.delegateTreeTableModel.removeTreeModelListener(this);
                this.delegateTreeTableModel.removePropertyChangeListener(this);
            }

            this.delegateTreeTableModel = delegateTreeTableModel;

            if (this.delegateTreeTableModel != null) {
                this.delegateTreeTableModel.addTreeModelListener(this);
                this.delegateTreeTableModel.addPropertyChangeListener(this);
                rebuiltTree();
            }
        }
    }

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        AbstractProxyTreeTableNode<N> proxyNode = getProxyForNode(e.getTreePath().getLastPathComponent());
        if (proxyNode != null) {
            proxyNode.delegateNodesChanged(e);
        }
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        AbstractProxyTreeTableNode<N> proxyNode = getProxyForNode(e.getTreePath().getLastPathComponent());
        if (proxyNode != null) {
            proxyNode.delegateNodesInserted(e);
        }
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
        AbstractProxyTreeTableNode<N> proxyNode = getProxyForNode(e.getTreePath().getLastPathComponent());
        if (proxyNode != null) {
            proxyNode.delegateNodesRemoved(e);
        }
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        AbstractProxyTreeTableNode<N> proxyNode = getProxyForNode(e.getTreePath().getLastPathComponent());
        if (proxyNode != null) {
            proxyNode.delegateStructureChanged(e);
        }
    }

    @Override
    public N getRoot() {
        return proxyRoot.delegateNode;
    }

    public AbstractProxyTreeTableNode<N> getProxyRoot() {
        return proxyRoot;
    }

    public void setProxyRoot(AbstractProxyTreeTableNode<N> proxyRoot) {
        if (this.proxyRoot != proxyRoot) {
            if (this.proxyRoot != null) {
                removeNodeToProxyMapping(this.proxyRoot);
            }

            this.proxyRoot = proxyRoot;

            if (this.proxyRoot != null) {
                addNodeToProxyMapping(this.proxyRoot);
                fireTreeStructureChanged(this, this.proxyRoot.getDelegatePath(), null, null);
            }
        }
    }

    public void addNodeToProxyMapping(AbstractProxyTreeTableNode<N> proxyNode) {
        if (nodeToProxyMap == null) {
            nodeToProxyMap = new WeakHashMap<N, AbstractProxyTreeTableNode<N>>();
        }
        nodeToProxyMap.put(proxyNode.delegateNode, proxyNode);
    }

    public void removeNodeToProxyMapping(AbstractProxyTreeTableNode<N> proxyNode) {
        if (nodeToProxyMap != null) {
            nodeToProxyMap.remove(proxyNode.delegateNode);

            if (lastProxyLookupResult == proxyNode) {
                lastProxyLookupResult = null;
            }
        }
    }

    public AbstractProxyTreeTableNode<N> getProxyForNode(Object node) {
        if (lastProxyLookupResult == null || lastProxyLookupResult.delegateNode != node) {
            lastProxyLookupResult = nodeToProxyMap.get((N) node);
        }

        return lastProxyLookupResult;
    }

    @Override
    public N getChild(Object parent, int index) {
        AbstractProxyTreeTableNode<N> proxyParent = getProxyForNode(parent);

        return proxyParent.getChildAt(index).delegateNode;
    }

    @Override
    public int getChildCount(Object parent) {
        AbstractProxyTreeTableNode<N> proxyParent = getProxyForNode(parent);
        if(proxyParent==null) {
        	return 0;
        }
        return proxyParent.getChildCount();
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        AbstractProxyTreeTableNode<N> proxyParent = getProxyForNode(parent);
        AbstractProxyTreeTableNode<N> proxyChild = getProxyForNode(child);

        if (proxyParent == null || proxyChild == null) {
            return -1;
        }

        return proxyParent.getIndex(proxyChild);
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     * @param source
     * @param path
     * @param childIndices
     * @param children
     */
    protected void fireTreeNodesChanged(Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children) {
        if (listenerList != null) {
            if (updating) {
                addHeldEvent(new HeldNodesChangedTreeTableEvent(source, path, childIndices, children));
            }
            else {
                fireTreeNodesChangedImmediately(source, path, childIndices, children);
            }
        }
    }

    protected void fireTreeNodesChangedImmediately(Object source, Object[] path,
                                                   int[] childIndices,
                                                   Object[] children) {
        if (listenerList != null) {
            if (DEBUG) {
                System.out.println("[" + this.toString() + "]:\n   firing treeNodesChanged(\n      " + source + ",\n      " + Arrays.toString(path) + "\n   ).");
            }

            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            TreeModelEvent e = null;
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == TreeModelListener.class) {
                    // Lazily create the event:
                    if (e == null) {
                        e = new TreeModelEvent(source, path,
                                childIndices, children);
                    }
                    ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
                }
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     *
     * @param source
     * @param path
     * @param childIndices
     * @param children
     */
    protected void fireTreeNodesInserted(Object source, Object[] path,
                                         int[] childIndices,
                                         Object[] children) {
        if (listenerList != null) {
            if (updating) {
                addHeldEvent(new HeldNodesInsertedTreeTableEvent(source, path, childIndices, children));
            }
            else {
                fireTreeNodesInsertedImmediately(source, path, childIndices, children);
            }
        }
    }

    protected void fireTreeNodesInsertedImmediately(Object source, Object[] path,
                                                    int[] childIndices,
                                                    Object[] children) {
        if (listenerList != null) {
            if (DEBUG) {
                System.out.println("[" + this.toString() + "]:\n   firing treeNodesInserted(\n      " + source + ",\n      " + Arrays.toString(path) + ",\n      " + Arrays.toString(childIndices) + ",\n      " + Arrays.toString(children) + "\n   ).");
            }
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            TreeModelEvent e = null;
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == TreeModelListener.class) {
                    // Lazily create the event:
                    if (e == null) {
                        e = new TreeModelEvent(source, path,
                                childIndices, children);
                    }
                    ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
                }
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     *
     * @param source
     * @param path
     * @param childIndices
     * @param children
     */
    protected void fireTreeNodesRemoved(Object source, Object[] path,
                                        int[] childIndices,
                                        Object[] children) {
        if (listenerList != null) {
            if (updating) {
                addHeldEvent(new HeldNodesRemovedTreeTableEvent(source, path, childIndices, children));
            }
            else {
                fireTreeNodesRemovedImmediately(source, path, childIndices, children);
            }
        }
    }

    protected void fireTreeNodesRemovedImmediately(Object source, Object[] path,
                                                   int[] childIndices,
                                                   Object[] children) {

        if (listenerList != null) {
            if (DEBUG) {
                System.out.println("[" + this.toString() + "]:\n   firing treeNodesRemoved(\n      " + source + ",\n      " + Arrays.toString(path) + ",\n      " + Arrays.toString(childIndices) + ",\n      " + Arrays.toString(children) + "\n   ).");
            }

            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            TreeModelEvent e = null;
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == TreeModelListener.class) {
                    // Lazily create the event:
                    if (e == null) {
                        e = new TreeModelEvent(source, path,
                                childIndices, children);
                    }
                    ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
                }
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     *
     * @param source
     * @param path
     * @param childIndices
     * @param children
     */
    protected void fireTreeStructureChanged(Object source, Object[] path,
                                            int[] childIndices,
                                            Object[] children) {

        if (listenerList != null) {
            if (updating) {
                addHeldEvent(new HeldNodeStructureChangedTreeTableEvent(source, path, childIndices, children));
            }
            else {
                fireTreeStructureChangedImmediately(source, path, childIndices, children);
            }
        }
    }

    protected void fireTreeStructureChangedImmediately(Object source, Object[] path,
                                                       int[] childIndices,
                                                       Object[] children) {
        if (listenerList != null) {
            if (DEBUG) {
                System.out.println("[" + this.toString() + "]:\n   firing treeStructureChanged(\n      " + source + ",\n      " + Arrays.toString(path) + "\n   ).");
            }

            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            TreeModelEvent e = null;
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == TreeModelListener.class) {
                    // Lazily create the event:
                    if (e == null) {
                        e = new TreeModelEvent(source, path,
                                childIndices, children);
                    }
                    ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
                }
            }
        }
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(TreeModelListener.class, l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        if (listenerList != null) {
            listenerList.remove(TreeModelListener.class, l);
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if (propertyChangeSupport != null) {
            propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
        }
    }

    /**
     * Support for reporting bound property changes for Object properties.
     * This method can be called when a bound property has changed and it will
     * send the appropriate PropertyChangeEvent to any registered
     * PropertyChangeListeners.
     *
     * @param propertyName the property whose value has changed
     * @param oldValue the property's previous value
     * @param newValue the property's new value
     */
    protected void firePropertyChange(String propertyName,
                                      Object oldValue, Object newValue) {
        if (propertyChangeSupport != null && (oldValue != newValue && (oldValue == null || oldValue.equals(newValue) == false))) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == getDelegateTreeTableModel()) {
            firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        }
    }

    @Override
    public boolean isFilterable() {
        if (getDelegateTreeTableModel() instanceof FilterableTreeTableModel) {
            FilterableTreeTableModel filterableTreeTableModel = (FilterableTreeTableModel) getDelegateTreeTableModel();
            return filterableTreeTableModel.isFilterable();
        }

        return false;
    }

    @Override
    public boolean isFilterableDelegate() {
        if (getDelegateTreeTableModel() instanceof FilterableTreeTableModelDelegate) {
            FilterableTreeTableModelDelegate filterableTreeTableModelDelegate = (FilterableTreeTableModelDelegate) getDelegateTreeTableModel();
            return filterableTreeTableModelDelegate.isFilterableDelegate();
        }

        return false;
    }

    @Override
    public boolean isFiltered() {
        if (getDelegateTreeTableModel() instanceof FilterableTreeTableModel) {
            FilterableTreeTableModel filterableTreeTableModel = (FilterableTreeTableModel) getDelegateTreeTableModel();
            return filterableTreeTableModel.isFiltered();
        }

        return false;
    }

    @Override
    public void setFilterObject(Object filterObject) {
        if (getDelegateTreeTableModel() instanceof FilterableTreeTableModel) {
            FilterableTreeTableModel filterableTreeTableModel = (FilterableTreeTableModel) getDelegateTreeTableModel();
            filterableTreeTableModel.setFilterObject(filterObject);
        }
    }

    @Override
    public Filter<N> setupFilter(Object filterObject) {
        if (getDelegateTreeTableModel() instanceof FilterableTreeTableModelDelegate) {
            FilterableTreeTableModelDelegate filterableTreeTableModelDelegate = (FilterableTreeTableModelDelegate) getDelegateTreeTableModel();
            return filterableTreeTableModelDelegate.setupFilter(filterObject);
        }
        return null;
    }

    @Override
    public boolean childrenShouldBeFiltered(N parentNode, Filter<N> filter) {
        if (getDelegateTreeTableModel() instanceof FilterableTreeTableModelDelegate) {
            FilterableTreeTableModelDelegate filterableTreeTableModelDelegate = (FilterableTreeTableModelDelegate) getDelegateTreeTableModel();
            return filterableTreeTableModelDelegate.childrenShouldBeFiltered(parentNode, filter);
        }
        return false;
    }

    @Override
    public boolean isNodeFiltered(N node, Filter<N> filter) {
        if (getDelegateTreeTableModel() instanceof FilterableTreeTableModelDelegate) {
            FilterableTreeTableModelDelegate filterableTreeTableModelDelegate = (FilterableTreeTableModelDelegate) getDelegateTreeTableModel();
            return filterableTreeTableModelDelegate.isNodeFiltered(node, filter);
        }
        return false;
    }

    @Override
    public boolean isNodePruned(N parentNode, Filter<N> filter) {
        if (getDelegateTreeTableModel() instanceof FilterableTreeTableModelDelegate) {
            FilterableTreeTableModelDelegate filterableTreeTableModelDelegate = (FilterableTreeTableModelDelegate) getDelegateTreeTableModel();
            return filterableTreeTableModelDelegate.isNodePruned(parentNode, filter);
        }
        return false;
    }

    @Override
    public void willFilterChildren(N parentNode, Filter<N> filter) {
        if (getDelegateTreeTableModel() instanceof FilterableTreeTableModelDelegate) {
            FilterableTreeTableModelDelegate filterableTreeTableModelDelegate = (FilterableTreeTableModelDelegate) getDelegateTreeTableModel();
            filterableTreeTableModelDelegate.willFilterChildren(parentNode, filter);
        }
    }

    @Override
    public boolean isSortable() {
        if (getDelegateTreeTableModel() instanceof SortableTreeTableModel) {
            SortableTreeTableModel sortableTreeTableModel = (SortableTreeTableModel) getDelegateTreeTableModel();
            return sortableTreeTableModel.isSortable();
        }
        return false;
    }

    @Override
    public boolean isSortableDelegate() {
        if (getDelegateTreeTableModel() instanceof SortableTreeTableModelDelegate) {
            SortableTreeTableModelDelegate sortableTreeTableModelDelegate = (SortableTreeTableModelDelegate) getDelegateTreeTableModel();
            sortableTreeTableModelDelegate.isSortableDelegate();
        }
        return false;
    }

    @Override
    public boolean isSorted() {
        if (getDelegateTreeTableModel() instanceof SortableTreeTableModel) {
            SortableTreeTableModel sortableTreeTableModel = (SortableTreeTableModel) getDelegateTreeTableModel();
            return sortableTreeTableModel.isSorted();
        }
        return false;
    }

    @Override
    public void setSortOrder(int columnIndex, boolean ascending) {
        if (getDelegateTreeTableModel() instanceof SortableTreeTableModel) {
            SortableTreeTableModel sortableTreeTableModel = (SortableTreeTableModel) getDelegateTreeTableModel();
            sortableTreeTableModel.setSortOrder(columnIndex, ascending);
        }
    }

    @Override
    public int getSortColumnIndex() {
        if (getDelegateTreeTableModel() instanceof SortableTreeTableModel) {
            SortableTreeTableModel sortableTreeTableModel = (SortableTreeTableModel) getDelegateTreeTableModel();
            return sortableTreeTableModel.getSortColumnIndex();
        }
        return -1;
    }

    @Override
    public boolean isSortAscending() {
        if (getDelegateTreeTableModel() instanceof SortableTreeTableModel) {
            SortableTreeTableModel sortableTreeTableModel = (SortableTreeTableModel) getDelegateTreeTableModel();
            return sortableTreeTableModel.isSortAscending();
        }
        return true;
    }

    @Override
    public Comparator<N> getSortComparator(N parentNode, int columnIndex, boolean ascending) {
        if (getDelegateTreeTableModel() instanceof SortableTreeTableModelDelegate) {
            SortableTreeTableModelDelegate proxySortableTreeTableModelDelegate = (SortableTreeTableModelDelegate) getDelegateTreeTableModel();

            Comparator<N> c = proxySortableTreeTableModelDelegate.getSortComparator(parentNode, columnIndex, ascending);

            return c;
        }
        return null;
    }

    @Override
    public void destroy() {
        if (getDelegateTreeTableModel() instanceof Destroyable) {
            Destroyable destroyable = (Destroyable) getDelegateTreeTableModel();
            destroyable.destroy();
        }

        setDelegateTreeTableModel(null);
    }

    @Override
    public void setValueAt(Object node, int column, Object aValue) {
        getDelegateTreeTableModel().setValueAt(node, column, aValue);
    }

    @Override
    public boolean isLeaf(Object node) {
        AbstractProxyTreeTableNode<N> proxyNode = getProxyForNode(node);
        if(proxyNode!=null) {
        	return proxyNode.isLeaf();
        }
        return false;
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        return getDelegateTreeTableModel().isCellEditable(node, column);
    }

    @Override
    public boolean invokeMenu(TreeTable treeTable, TreePath[] paths, JPopupMenu popup) {
        // The tree paths should probably be reorder to match the order in
        // the proxy node, if it differs...
        return getDelegateTreeTableModel().invokeMenu(treeTable, paths, popup);
    }

    @Override
    public Object getValueAt(Object node, int column) {
        return getDelegateTreeTableModel().getValueAt(node, column);
    }

    @Override
    public String getToolTipText(Object node, int column, MouseEvent evt) {
        return getDelegateTreeTableModel().getToolTipText(node, column, evt);
    }

    @Override
    public int getPrimaryColumn() {
        return getDelegateTreeTableModel().getPrimaryColumn();
    }

    @Override
    public Icon getOpenIcon() {
        return getDelegateTreeTableModel().getOpenIcon();
    }

    @Override
    public Icon getLeafIcon() {
        return getDelegateTreeTableModel().getLeafIcon();
    }

    @Override
    public Icon getIcon(TreeTable treeTable, Object node, boolean isSelected, boolean expanded, boolean leaf, int row) {
        return getDelegateTreeTableModel().getIcon(treeTable, node, isSelected, expanded, leaf, row);
    }

    @Override
    public int getColumnSpan(Object node, int column, TreeTableColumnModel columnModel) {
        return getDelegateTreeTableModel().getColumnSpan(node, column, columnModel);
    }

    @Override
    public TreeTableColumnModel getColumnModel() {
        return getDelegateTreeTableModel().getColumnModel();
    }

    @Override
    public int getColumnCount() {
        return getDelegateTreeTableModel().getColumnCount();
    }

    @Override
    public Color getColumnColor(Object node, int column) {
        return getDelegateTreeTableModel().getColumnColor(node, column);
    }

    @Override
    public Class getColumnClass(int column) {
        return getDelegateTreeTableModel().getColumnClass(column);
    }

    @Override
    public Icon getClosedIcon() {
        return getDelegateTreeTableModel().getClosedIcon();
    }

    @Override
    public TreeTableCellRenderer getCellRenderer(Object node, int column) {
        return getDelegateTreeTableModel().getCellRenderer(node, column);
    }

    @Override
    public TreeTableCellEditor getCellEditor(Object node, int column) {
        return getDelegateTreeTableModel().getCellEditor(node, column);
    }

//    @Override
//    public TreeTableColumnModel buildColumnModel() {
//        return getDelegateTreeTableModel().buildColumnModel();
//    }
    @Override
    @SuppressWarnings("deprecation")
    public boolean isLegacyDnDSupported() {
        if (getDelegateTreeTableModel() instanceof TreeTableLegacyDnDHandler) {
            TreeTableLegacyDnDHandler treeTableLegacyDnDHandler = (TreeTableLegacyDnDHandler) getDelegateTreeTableModel();
            return treeTableLegacyDnDHandler.isLegacyDnDSupported();
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void endDrag(TreeTable treeTable, DragSourceDropEvent dsde) {
        if (getDelegateTreeTableModel() instanceof TreeTableLegacyDnDHandler) {
            TreeTableLegacyDnDHandler treeTableLegacyDnDHandler = (TreeTableLegacyDnDHandler) getDelegateTreeTableModel();
            treeTableLegacyDnDHandler.endDrag(treeTable, dsde);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean expandDuringDrag(TreeTable treeTable, TreePath dropPath, DropTargetDragEvent event) {
        if (getDelegateTreeTableModel() instanceof TreeTableLegacyDnDHandler) {
            TreeTableLegacyDnDHandler treeTableLegacyDnDHandler = (TreeTableLegacyDnDHandler) getDelegateTreeTableModel();
            return treeTableLegacyDnDHandler.expandDuringDrag(treeTable, dropPath, event);
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean handleDrop(TreeTable treeTable, TreePath dropPath, DropTargetDropEvent event) {
        if (getDelegateTreeTableModel() instanceof TreeTableLegacyDnDHandler) {
            TreeTableLegacyDnDHandler treeTableLegacyDnDHandler = (TreeTableLegacyDnDHandler) getDelegateTreeTableModel();
            return treeTableLegacyDnDHandler.handleDrop(treeTable, dropPath, event);
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean startDrag(TreeTable treeTable, TreePath dragPath, DragSourceListener listener, DragGestureEvent dge) {
        if (getDelegateTreeTableModel() instanceof TreeTableLegacyDnDHandler) {
            TreeTableLegacyDnDHandler treeTableLegacyDnDHandler = (TreeTableLegacyDnDHandler) getDelegateTreeTableModel();
            return treeTableLegacyDnDHandler.startDrag(treeTable, dragPath, listener, dge);
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public TreePath willHandleDrop(TreeTable treeTable, TreePath path, DropTargetDragEvent event) {
        if (getDelegateTreeTableModel() instanceof TreeTableLegacyDnDHandler) {
            TreeTableLegacyDnDHandler treeTableLegacyDnDHandler = (TreeTableLegacyDnDHandler) getDelegateTreeTableModel();
            return treeTableLegacyDnDHandler.willHandleDrop(treeTable, path, event);
        }
        return null;
    }

    @Override
    public boolean isDnDSupported() {
        if (getDelegateTreeTableModel() instanceof TreeTableDnDHandler) {
            TreeTableDnDHandler treeTableDnDHandler = (TreeTableDnDHandler) getDelegateTreeTableModel();
            return treeTableDnDHandler.isDnDSupported();
        }
        return false;
    }

    @Override
    public boolean canImport(TreeTable destinationTree, TreeTableTransferSupport support) {
        if (getDelegateTreeTableModel() instanceof TreeTableDnDHandler) {
            TreeTableDnDHandler treeTableDnDHandler = (TreeTableDnDHandler) getDelegateTreeTableModel();

            if ( support.isDrop() == false || adjustDropLocation(support.getDropLocation()) == true ) {
                return treeTableDnDHandler.canImport(destinationTree, support);
            }
        }
        return false;
    }

    @Override
    public Transferable createTransferable(TreeTable sourceTree, TreePath[] transferredPaths) {
        if (getDelegateTreeTableModel() instanceof TreeTableDnDHandler) {
            TreeTableDnDHandler treeTableDnDHandler = (TreeTableDnDHandler) getDelegateTreeTableModel();
            return treeTableDnDHandler.createTransferable(sourceTree, transferredPaths);
        }
        return null;
    }

    @Override
    public void exportDone(TreeTable sourceTree, TreePath[] transferredPaths, Transferable data, int action) {
        if (getDelegateTreeTableModel() instanceof TreeTableDnDHandler) {
            TreeTableDnDHandler treeTableDnDHandler = (TreeTableDnDHandler) getDelegateTreeTableModel();
            treeTableDnDHandler.exportDone(sourceTree, transferredPaths, data, action);
        }
    }

    @Override
    public int getDragSourceActions(TreeTable sourceTree, TreePath[] transferredPaths) {
        if (getDelegateTreeTableModel() instanceof TreeTableDnDHandler) {
            TreeTableDnDHandler treeTableDnDHandler = (TreeTableDnDHandler) getDelegateTreeTableModel();
            return treeTableDnDHandler.getDragSourceActions(sourceTree, transferredPaths);
        }
        return DnDConstants.ACTION_NONE;
    }

    @Override
    public boolean importData(TreeTable destinationTree, TreeTableTransferSupport support) {
        if (getDelegateTreeTableModel() instanceof TreeTableDnDHandler) {
            TreeTableDnDHandler treeTableDnDHandler = (TreeTableDnDHandler) getDelegateTreeTableModel();

            if ( support.isDrop() == false || adjustDropLocation(support.getDropLocation()) == true ) {
                return treeTableDnDHandler.importData(destinationTree, support);
            }
            
        }
        return false;
    }

    /** Adjust the drop location to account for reordering/missing nodes in the proxy versus the underlying model.
     *
     * The drop location often needs to be adjusted due to sorting or filtering.
     * <p>
     * This method takes the indicating drop location, find the proxy node indicated
     * by the path and child index, finding the corresponding delegate child, and then
     * finding the index of that delegate child in the delegate model.
     * <p>
     * From a semantic point of view, the child index tells us the location of the
     * child we want to insert the drop data in front of.
     *
     *
     * @param dropLocation
     * @return True if this is a legal drop, false if something about the look-up process failed.
     */
    protected boolean adjustDropLocation(TreeTableDropLocation dropLocation) {
        boolean result = false;

        if (dropLocation.getChildIndex() != -1) {
            Object delegateParent = dropLocation.getPath().getLastPathComponent();
            AbstractProxyTreeTableNode<N> proxyParent = getProxyForNode(delegateParent);
            if (proxyParent != null) {
                N delegateChild = (N) getDelegateTreeTableModel().getChild(delegateParent, dropLocation.getChildIndex());
                AbstractProxyTreeTableNode<N> proxyChild = getProxyForNode(delegateChild);
                int newIndex = proxyParent.getIndex(proxyChild);

                dropLocation.setChildIndex(newIndex);
                result = true;
            }
        }
        else {
            result = true;
        }

        return result;
    }

    /**
     * @return the holdTreeModelEvents
     */
    public boolean isUpdating() {
        return updating;
    }

    /**
     * @param updating
     */
    public void setUpdating(boolean updating) {
        if (this.updating != updating) {
            this.updating = updating;

            if (this.updating == false) {
                fireHeldEvents();

                lastUpdateTime = System.currentTimeMillis();

                if (needsUpdate) {
                    scheduleUpdate();
                }
            }
        }
    }

    private void addHeldEvent(HeldTreeTableEvent event) {
        if (heldEventList == null) {
            heldEventList = new LinkedList<HeldTreeTableEvent>();
        }

        heldEventList.add(event);
    }

    private void fireHeldEvents() {
        if (heldEventList != null) {
            while (heldEventList.isEmpty() == false) {
                HeldTreeTableEvent event = heldEventList.pollFirst();
                if (event.isEventLegal(this)) {
                    event.fireEvent();
                }
            }
            heldEventList = null;
        }
    }

    /**
     * @return the dirty
     */
    public boolean needsUpdate() {
        return needsUpdate;
    }

    /**
     * @param needsUpdate the needsUpdate to set
     */
    public void setNeedsUpdate(boolean needsUpdate) {
        if (this.needsUpdate != needsUpdate) {

            if (this.needsUpdate == true) {
                setUpdateTask(null);
            }

            this.needsUpdate = needsUpdate;

            if (this.needsUpdate == true) {
                // Needs a new update...
                if (isUpdating() == false) {
                    scheduleUpdate();
                }
            }

        }
    }

    public void setUpdateTask(TimerTask updateTask) {
        if (this.updateTask != updateTask) {
            if (this.updateTask != null) {
                // Cancel the old one...
                this.updateTask.cancel();
            }

            this.updateTask = updateTask;

            if (this.updateTask != null) {
                // this.updateTask.addChangeListener(this);
            }
        }
    }

    protected void scheduleUpdate() {
        if (updateTask == null) {
            if (lastUpdateTime - System.currentTimeMillis() > millisecondsBetweenUpdates) {
                updateModel();
            }
            else {
                updateTask = new UpdateTask();
                long delay = Math.max(0, millisecondsBetweenUpdates - (lastUpdateTime - System.currentTimeMillis()));
                SharedTimer.getSharedDeamonTimer().schedule(updateTask, delay);
                setUpdateTask(updateTask);
            }
        }
    }

    protected void updateModel() {
        assert isUpdating() == false;
        setUpdating(true);
        setNeedsUpdate(false);


        setUpdating(false);
    }

    public void updateModelImmediately() {
        if (isUpdating()) {
            setNeedsUpdate(true);
        }
        else {
            updateModel();
        }
    }

    protected class UpdateTask extends TimerTask {

        Runnable runnable;

        @Override
        public void run() {
            if (runnable == null) {
                runnable = new Runnable() {

                    @Override
                    public void run() {
                        updateModel();
                    }
                };
            }
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (InterruptedException ex) {
            } catch (InvocationTargetException ex) {
                Logger.getLogger(AbstractProxyTreeTableModel.class.getName()).log(Level.SEVERE, null, ex.getTargetException());
            }
        }
    }

    protected static abstract class HeldTreeTableEvent {

        Object source;

        Object[] path;

        int[] childIndices;

        Object[] children;

        public HeldTreeTableEvent(Object source, Object[] path, int[] childIndices, Object[] children) {
            super();
            this.source = source;
            this.path = path;
            this.childIndices = childIndices;
            this.children = children;
        }

        public abstract void fireEvent();

        public boolean isEventLegal(TreeTableModel model) {
            return isPathInModel(path.length, model);
        }

        private boolean isPathInModel(int length, TreeTableModel model) {
            if (length == 1) {
                return path[0] == model.getRoot();
            }
            else {
                return isPathInModel(length - 1, model) && model.getIndexOfChild(path[length - 2], path[length - 1]) != -1;
            }
        }
    }

    private class HeldNodeStructureChangedTreeTableEvent extends HeldTreeTableEvent {

        public HeldNodeStructureChangedTreeTableEvent(Object source, Object[] path, int[] childIndices, Object[] children) {
            super(source, path, childIndices, children);
        }

        @Override
        public void fireEvent() {
            fireTreeStructureChangedImmediately(source, path, childIndices, children);
        }
    }

    private class HeldNodesRemovedTreeTableEvent extends HeldTreeTableEvent {

        public HeldNodesRemovedTreeTableEvent(Object source, Object[] path, int[] childIndices, Object[] children) {
            super(source, path, childIndices, children);
        }

        @Override
        public void fireEvent() {
            fireTreeNodesRemovedImmediately(source, path, childIndices, children);
        }
    }

    private class HeldNodesInsertedTreeTableEvent extends HeldTreeTableEvent {

        public HeldNodesInsertedTreeTableEvent(Object source, Object[] path, int[] childIndices, Object[] children) {
            super(source, path, childIndices, children);
        }

        @Override
        public void fireEvent() {
            fireTreeNodesInsertedImmediately(source, path, childIndices, children);
        }
    }

    private class HeldNodesChangedTreeTableEvent extends HeldTreeTableEvent {

        public HeldNodesChangedTreeTableEvent(Object source, Object[] path, int[] childIndices, Object[] children) {
            super(source, path, childIndices, children);
        }

        @Override
        public void fireEvent() {
            fireTreeNodesChangedImmediately(source, path, childIndices, children);
        }
    }
}
