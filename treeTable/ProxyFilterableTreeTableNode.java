/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treeTable;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;
import tjava.Filter;

/**
 *
 * @param <N> 
 * @author twalker
 */
public class ProxyFilterableTreeTableNode<N> extends AbstractProxyTreeTableNode<N> {

    protected ProxyFilterableTreeTableModel<N> model;

    protected Filter<N> filter = null; // Currently applied filter...

    public ProxyFilterableTreeTableNode(N node, ProxyFilterableTreeTableModel<N> model) {
        super(node);
        this.model = model;
    }

    @Override
    protected ProxyFilterableTreeTableModel<N> getModel() {
        return model;
    }

    @Override
    protected void buildNode(boolean fireChanges) {
        if (built == false) {
            built = true;

            boolean wasUpdating = isUpdating();
            setUpdating(true);

            boolean wasFiltered = (filter != null);

            filter = getModel().filter;

            boolean isFiltered = (filter != null);

            FilterableTreeTableModelDelegate<N> delegateModel = getModel().getDelegateTreeTableModel();

            boolean childrenShouldBeFiltered = false;

            List<N> oldDelegateChildren = getCurrentDelegateChildren(true, delegateModel);

            if (isFiltered) {
                childrenShouldBeFiltered = delegateModel.childrenShouldBeFiltered(delegateNode, filter);

                if (childrenShouldBeFiltered == true) {
                    delegateModel.willFilterChildren(delegateNode, filter);
                }
            }

            int delegateChildCount = delegateModel.getChildCount(delegateNode);
            int childCount = children == null ? 0 : children.size();

            int childIndex = 0;

            for (int i = 0; i < delegateChildCount; i++) {
                N delegateChild = delegateModel.getChild(delegateNode, i);

                boolean childFiltered = false;

                ProxyFilterableTreeTableNode<N> proxyChild = null;

                if (childIndex < childCount && children.get(childIndex).delegateNode == delegateChild) {
                    proxyChild = (ProxyFilterableTreeTableNode<N>) children.get(childIndex);
                }
                else {
                    proxyChild = new ProxyFilterableTreeTableNode<N>(delegateChild, model);
                    insert(proxyChild, childIndex);
                    childCount++;
                }
                childIndex++;

                proxyChild.filterNode(fireChanges);

                if (isFiltered && childrenShouldBeFiltered) {
                    // We can only filter empty children...
                    if (proxyChild.getChildCount() == 0) {
                        // The node has no children, so it is eligible to be removed.

                        // A node with no children can be removed if:
                        // 1) the filter doesn't included it (isNodeFilter == true)
                        // 2) the node is pruned (isNodePruned == true)
                        childFiltered = delegateModel.isNodeFiltered(delegateChild, filter) && delegateModel.isNodePruned(delegateChild, filter);
                    }
                }

                if (childFiltered) {
                    remove(proxyChild);
                    childIndex--;
                    childCount--;
                }
            }

            List<N> newDelegateChildren = getCurrentDelegateChildren(true, delegateModel);

            if (fireChanges) {
                nodeDelegateChildrenChanged(oldDelegateChildren, newDelegateChildren);
            }

            setUpdating(wasUpdating);
        }
    }

    @Override
    public void delegateNodesChanged(TreeModelEvent e) {
        if (filter != null) {
            if (isUpdating() == false) {
                setUpdating(true);

                TreePath parentPath = e.getTreePath();

                if (parentPath.getPathCount() != 1 || e.getChildIndices() != null) {
                    // The children of parentPath changed...
                    FilterableTreeTableModelDelegate<N> delegateModel = null;
                    boolean childrenShouldBeFiltered = false;

                    delegateModel = getModel().getDelegateTreeTableModel();

                    List<N> oldDelegateChildren = getCurrentDelegateChildren(true, delegateModel);

                    delegateModel.willFilterChildren(delegateNode, filter);

                    childrenShouldBeFiltered = delegateModel.childrenShouldBeFiltered(delegateNode, filter);

                    if (childrenShouldBeFiltered == true) {
                        delegateModel.willFilterChildren(delegateNode, filter);
                    }

                    List<N> changedDelegateChildren = new ArrayList<N>();

                    int[] delegateIndecies = e.getChildIndices();
                    Object[] delegateObjects = e.getChildren();
                    int lowestPossibleIndexInProxy = 0;

                    for (int i = 0; i < delegateObjects.length; i++) {
                        N delegateChild = (N) delegateObjects[i];
                        int delegateChildIndex = delegateIndecies[i];

                        boolean childFiltered = false;

                        // First check if it is in the model...
                        boolean isAlreadyInModel = true;
                        ProxyFilterableTreeTableNode<N> proxyChild = (ProxyFilterableTreeTableNode<N>) getModel().getProxyForNode(delegateChild);

                        int proxyIndex = -1;

                        if (proxyChild != null) {
                            proxyIndex = getIndex(proxyChild);
                        }
                        else {
                            // It was previously filtered...so create one so we can check for filtering...
                            proxyChild = new ProxyFilterableTreeTableNode<N>(delegateChild, model);

                            proxyIndex = delegateChildIndex;
                            proxyIndex = getInsertIndex(delegateChildIndex, lowestPossibleIndexInProxy);

                            insert(proxyChild, proxyIndex);
                            lowestPossibleIndexInProxy = proxyIndex;

                            isAlreadyInModel = false;
                        }

                        if (childrenShouldBeFiltered) {
                            childFiltered = delegateModel.isNodeFiltered(delegateChild, filter);
                        }

                        proxyChild.filterNode(true);

                        // We can only filter children if:
                        // 1) The parent wants us to filter the children
                        // 2) The node we are considering has no children itself
                        if (childrenShouldBeFiltered && proxyChild.getChildCount() == 0) {
                            // The node has no children, so it is eligible to be removed.

                            // A node with no children can be removed if:
                            // 1) the filter doesn't included it (isNodeFilter == true)
                            // 2) the node is pruned (isNodePruned == true)
                            childFiltered = delegateModel.isNodeFiltered(delegateChild, filter) && delegateModel.isNodePruned(delegateChild, filter);
                        }

                        if (childFiltered) {
                            remove(proxyIndex);
                        }
                        else if (isAlreadyInModel) {
                            changedDelegateChildren.add(delegateChild);
                        }
                    }

                    List<N> newDelegateChildren = getCurrentDelegateChildren(true, delegateModel);

                    // First fire off the node changed messages for anything that was in the model
                    // already, changed, and wasn't removed...
                    if (changedDelegateChildren.size() > 0) {
                        int[] changeIndecies = getIndeciesForDelegateChildren(changedDelegateChildren.toArray());
                        Object[] changedDelegateArray = getDelegateChildrenForIndecies(changeIndecies);

                        if (changedDelegateArray != null) {
                            nodeChanged(this, (N[]) e.getPath(), changeIndecies, changedDelegateArray);
                        }
                    }

                    nodeDelegateChildrenChanged(oldDelegateChildren, newDelegateChildren);

                    setUpdating(false);
                }

            }
        }
        else {
            super.delegateNodesChanged(e);
        }
    }

    @Override
    public void delegateNodesInserted(TreeModelEvent e) {
        if (isUpdating() == false) {
            FilterableTreeTableModelDelegate<N> delegateModel = null;

            boolean childrenShouldBeFiltered = false;

            delegateModel = getModel().getDelegateTreeTableModel();

            boolean isFiltered = (filter != null);

            if (isFiltered) {
                delegateModel.willFilterChildren(delegateNode, filter);

                childrenShouldBeFiltered = delegateModel.childrenShouldBeFiltered(delegateNode, filter);

                if (childrenShouldBeFiltered == true) {
                    delegateModel.willFilterChildren(delegateNode, filter);
                }
            }

            int[] delegateIndecies = e.getChildIndices();
            Object[] delegateObjects = e.getChildren();
            int lowestPossibleIndexInProxy = 0;

            List<Object> insertedProxyObjects = new ArrayList<Object>();
            List<Integer> insertedProxyIndecies = new ArrayList<Integer>();

            for (int i = 0; i < delegateObjects.length; i++) {
                N delegateChild = (N) delegateObjects[i];
                int delegateChildIndex = delegateIndecies[i];

                boolean childFiltered = false;

                ProxyFilterableTreeTableNode<N> proxyChild = new ProxyFilterableTreeTableNode<N>(delegateChild, model);

                int proxyIndex = delegateChildIndex;

                proxyIndex = getInsertIndex(delegateChildIndex, lowestPossibleIndexInProxy);

                insert(proxyChild, proxyIndex);
                lowestPossibleIndexInProxy = proxyIndex;

                proxyChild.filterNode(true);

                if (isFiltered) {
                    // We can only filter children if:
                    // 1) The parent wants us to filter the children
                    // 2) The node we are considering has no children itself
                    if (childrenShouldBeFiltered && proxyChild.getChildCount() == 0) {
                        // The node has no children, so it is eligible to be removed.

                        // A node with no children can be removed if:
                        // 1) the filter doesn't included it (isNodeFilter == true)
                        // 2) the node is pruned (isNodePruned == true)
                        childFiltered = delegateModel.isNodeFiltered(delegateChild, filter) && delegateModel.isNodePruned(delegateChild, filter);
                    }
                }

                if (childFiltered) {
                    remove(proxyIndex);
                }
                else {
                    insertedProxyObjects.add(delegateChild);
                    insertedProxyIndecies.add(proxyIndex);
                    lowestPossibleIndexInProxy++;
                }
            }

            if (insertedProxyIndecies.size() > 0) {
                int[] indexArray = toArray(insertedProxyIndecies);
                Object[] objectArray = insertedProxyObjects.toArray();

                nodesWereInserted(this, indexArray, objectArray);
            }
        }
        else {
            // Filter nodes are always built (or in the process of being built)
            // so we never want to propogate unhandled events up...
            //nodesWereInserted(this, e.getChildIndices(), e.getChildren());
            }
    }

    private List<N> getCurrentDelegateChildren(boolean obtainFromProxyChildren, FilterableTreeTableModelDelegate<N> delegateModel) {
        List<N> oldDelegateChildren = null;
        if (obtainFromProxyChildren) {
            // We were previously filtered, so we are just updating the
            // filter that we are using.  Use our current set of children
            // to determine the delta for change events.
            int count = children == null ? 0 : children.size();
            if (count > 0) {
                oldDelegateChildren = new ArrayList<N>();
                for (int i = 0; i < count; i++) {
                    oldDelegateChildren.add(children.get(i).delegateNode);
                }
            }
        }
        else {
            // We weren't previously filtered, so we need to use our delegate's
            // children to determine the delta for change events.
            int count = delegateModel.getChildCount(delegateNode);
            if (count > 0) {
                oldDelegateChildren = new ArrayList<N>();
                for (int i = 0; i < count; i++) {
                    oldDelegateChildren.add(delegateModel.getChild(delegateNode, i));
                }
            }
        }
        return oldDelegateChildren;
    }

    private int getInsertIndex(int indexInDelegate, int lowestPossibleIndexInProxy) {

        int childCount = children == null ? 0 : children.size();

        FilterableTreeTableModelDelegate<N> delegateModel = getModel().getDelegateTreeTableModel();

        int delegateChildCount = delegateModel.getChildCount(delegateNode);

        int proxyIndex = lowestPossibleIndexInProxy;
        int delegateIndex = indexInDelegate + 1;

        for (; delegateIndex < delegateChildCount; delegateIndex++) {

            N delegateChild = delegateModel.getChild(delegateNode, delegateIndex);

            for (int i = proxyIndex; i < childCount; i++) {
                if (children.get(i).delegateNode == delegateChild) {
                    return i;
                }
            }
        }

        // Didn't find anything after us, so we must belong at the end...
        return childCount;
    }

    @Override
    public void delegateNodesRemoved(TreeModelEvent e) {

        int childCount = children == null ? 0 : children.size();

        Object[] delegateObjects = e.getChildren();

        if (isUpdating() == false && childCount > 0) {

            List<Object> removedObjects = new ArrayList<Object>();
            List<Integer> removedIndecies = new ArrayList<Integer>();

            for (int i = 0; i < delegateObjects.length; i++) {
                Object delegateChild = delegateObjects[i];

                AbstractProxyTreeTableNode<N> proxyChild = getModel().getProxyForNode(delegateChild);

                if (proxyChild != null) {
                    for (int j = 0; j < children.size(); j++) {
                        if (children.get(j) == proxyChild) {
                            remove(j);
                            removedObjects.add(delegateChild);
                            removedIndecies.add(j);
                            break;
                        }
                    }
                }
            }

            if (removedIndecies.size() > 0) {
                int[] indexArray = toArray(removedIndecies);
                Object[] objectArray = removedObjects.toArray();

                nodesWereRemoved(this, indexArray, objectArray);
            }
        }
        else {
            // Filter nodes are always built (or in the process of being built)
            // so we never want to propogate unhandled events up...
            //nodesWereRemoved(this, delegateIndecies, delegateObjects);
        }
    }

    @Override
    protected void nodesWereRemoved(AbstractProxyTreeTableNode<N> changedNode, N[] delegatePath, int[] removeIndecies, Object[] removedDelegateObjects) {
        // If one of our children lost all of it's nodes, we might have to prune...
        if (changedNode.getParent() == this && isUpdating() == false) {
            if (built && changedNode.getChildCount() == 0 && getModel().getDelegateTreeTableModel().isNodePruned(changedNode.delegateNode, filter) == true) {
                // We need to remove that child from our list...
                int index = children.indexOf(changedNode);
                if (index != -1) {
                    remove(index);
                    nodesWereRemoved(this, this.getDelegatePath(), new int[]{index}, new Object[]{changedNode.delegateNode});
                }
            }
            else {
                super.nodesWereRemoved(changedNode, delegatePath, removeIndecies, removedDelegateObjects);
            }
        }
        else {
            super.nodesWereRemoved(changedNode, delegatePath, removeIndecies, removedDelegateObjects);
        }
    }

    @Override
    protected void nodesWereInserted(AbstractProxyTreeTableNode<N> changedNode, N[] delegatePath, int[] insertedIndeces, Object[] insertedDelegateObjects) {
        // We really need to do a check here to see if we need to unprune something...ugh
        // ToDo: Fix this
        super.nodesWereInserted(changedNode, delegatePath, insertedIndeces, insertedDelegateObjects);

    }

//    @Override
//    public void delegateNodesChanged(TreeModelEvent e) {
//
////        if ( isUpdating() == false ) {
////            built = false;
////           // buildNode(true);
////        }
////        else {
////            super.delegateNodesChanged(e);
////        }
//    }
    @Override
    public void delegateStructureChanged(TreeModelEvent e) {
        if ( isUpdating() == false ) {
            if (built == false) {
                nodeStructureChanged(this);
            }
            else {
                rebuildNode(true);
            }
        }
    }

    public void filterCriteriaChanged() {
        filterNode(true);
    }

    private void filterNode(boolean fireUpdates) {
        if (getModel().isFiltered()) {
            built = false;
            buildNode(fireUpdates);
        }
        else {
            if ( built ) {
                built = false;
                buildNode(fireUpdates);
            }
            //unfilterNode();
        }
    }

    private void unfilterNode() {
        if (built == true) {

            boolean wasUpdating = isUpdating();
            setUpdating(true);

            FilterableTreeTableModelDelegate<N> delegateModel = getModel().getDelegateTreeTableModel();

            int childCount = children == null ? 0 : children.size();
            int delegateChildCount = delegateModel.getChildCount(delegateNode);

            // Not a full rebuild.
            // First let our existing children do their unfiltering...
            List<N> oldDelegateChildren = new ArrayList<N>();
            List<N> newDelegateChildren = new ArrayList<N>();

            for (int i = 0; i < childCount; i++) {
                ProxyFilterableTreeTableNode<N> child = (ProxyFilterableTreeTableNode<N>) children.get(i);
                child.unfilterNode();
                oldDelegateChildren.add(child.delegateNode);
            }

            for (int i = 0; i < delegateChildCount; i++) {
                newDelegateChildren.add(delegateModel.getChild(delegateNode, i));
            }

            List<N> insertedObjects = new ArrayList<N>();
            List<Integer> insertedIndecies = new ArrayList<Integer>();

            calculateListDifference(oldDelegateChildren, newDelegateChildren, null, null, insertedIndecies, insertedObjects);

            if (insertedIndecies.size() > 0) {
                int[] indexArray = toArray(insertedIndecies);
                Object[] objectArray = insertedObjects.toArray();

                nodesWereInserted(this, indexArray, objectArray);
            }

            removeAllChildren();

            filter = null;
            built = false;

            setUpdating(wasUpdating);
        }
    }
}
