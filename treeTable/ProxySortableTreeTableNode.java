/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treeTable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreeNode;

/**
 *
 * @param <N>
 * @author twalker
 */
public class ProxySortableTreeTableNode<N> extends AbstractProxyTreeTableNode<N> {

    protected ProxySortableTreeTableModel<N> model;

    public ProxySortableTreeTableNode(N delegateNode, ProxySortableTreeTableModel<N> model) {
        super(delegateNode);
        this.model = model;
    }

    protected ProxySortableTreeTableModel<N> getModel() {
        return model;
    }


    @Override
    public void delegateNodesInserted(TreeModelEvent e) {
        if (built && isUpdating() == false) {
            setUpdating(true);

            List<Integer> insertedChildrenIndecies = new LinkedList<Integer>();
           // List<Object> insertedChildrenObjects = new LinkedList<Object>();

            Comparator<N> comparator = null;

            if (model.isSorted()) {
                comparator = model.getDelegateSortComparator(this);
            }

            int[] delegateIndecies = e.getChildIndices();
            Object[] delegateObjects = e.getChildren();

            for (int i = 0; i < delegateIndecies.length; i++) {
                int delegateChildIndex = delegateIndecies[i];
                //N delegateChild = (N) delegateNode.getChildAt(delegateChildIndex);
                N delegateChild = (N) delegateObjects[i];

                AbstractProxyTreeTableNode<N> child = model.getProxyForNode(delegateChild);
                if (child == null) {
                    child = new ProxySortableTreeTableNode<N>(delegateChild, model);
                }

                if (comparator != null) {
                    int index = insertNodeInSortLocation(child, comparator);
                    addInsertedIndexToList(insertedChildrenIndecies, index, delegateChild);
                }
                else {
                    insert(child, delegateChildIndex);
                    addInsertedIndexToList(insertedChildrenIndecies, delegateChildIndex, delegateChild);
                }
            }

            if ( insertedChildrenIndecies.size() > 0 ) {
                int[] indexArray = toArray(insertedChildrenIndecies);
                Object[] objectArray = getDelegateChildrenForIndecies(indexArray);

                nodesWereInserted(this, indexArray, objectArray);
            }

            setUpdating(false);
        }
        else {
            // Don't think we should ever hit this...
            // If we are building (or updating for some other reason), the
            // delegate model shouldn't be posting changes since it is
            // only supposed to post changes in the event thread.
        	//jeff
            //assert false;
        }
    }

    private void addInsertedIndexToList(List<Integer> indexList, int index, Object object) {

        if (indexList.size() == 0) {
            indexList.add(index);
        }
        else {
            int position;

            for (position = indexList.size() - 1; position >= 0; position--) {
                if (index > indexList.get(position)) {
                    break;
                }
            }

            position++;
            indexList.add(position, index);
           // objectList.add(position, object);

            position++;

            // Fix up all the indecies after me...
            for (; position < indexList.size(); position++) {
                indexList.set(position, indexList.get(position) + 1);
            }
        }
    }

    private int insertNodeInSortLocation(AbstractProxyTreeTableNode<N> newChild, Comparator<N> comparator) {


        int childCount = getChildCount();

        int lower = 0;
        int upper = getChildCount() + 1;

        int position = lower + (upper - lower) / 2;

        while (position < childCount && lower < upper) {
            int c = comparator.compare(newChild.delegateNode, getChildAt(position).delegateNode);

            if (c < 0) {
                upper = position;
            }
            else if (c >= 0) {
                lower = position + 1;
            }

            position = lower + (upper - lower) / 2;
        }

        insert(newChild, position);
        return position;

    }

    @Override
    public void delegateNodesRemoved(TreeModelEvent e) {
        if (built && isUpdating() == false) {
            setUpdating(true);
            Object[] removedDelegateNodes = e.getChildren();

            Object[] removedChildren = new TreeNode[removedDelegateNodes.length];
            int[] removedIndecies = new int[removedDelegateNodes.length];

            int removeArrayIndex = 0;
            for (int i = 0; i < removedDelegateNodes.length; i++) {
                Object child = removedDelegateNodes[i];
                AbstractProxyTreeTableNode<N> childProxy = model.getProxyForNode(child);
                if (childProxy != null) {
                    int index = getIndex(childProxy);
                    if (index != -1) {
                        remove(index);
                        removedChildren[removeArrayIndex] = child;
                        removedIndecies[removeArrayIndex] = index;
                        removeArrayIndex++;
                    }
                }
            }

            if (removeArrayIndex > 0) {
                if (removeArrayIndex < removedDelegateNodes.length) {
                    removedChildren = Arrays.copyOf(removedChildren, removeArrayIndex);
                    removedIndecies = Arrays.copyOf(removedIndecies, removeArrayIndex);
                }

                nodesWereRemoved(this, removedIndecies, removedDelegateNodes);
            }
            setUpdating(false);
        }
        else {
            // Don't think we should ever hit this...
            assert false;
        }
    }

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

    @Override
    protected void buildNode(boolean fireChanges) {

        if (built == false) {
            built = true;

            List<AbstractProxyTreeTableNode<N>> oldChildren = null;

            if (children != null) {
                oldChildren = (List) children.clone();
                removeAllChildren();
            }

            TreeTableModel delegateModel = getModel().getDelegateTreeTableModel();
            int delegateChildCount = delegateModel.getChildCount(delegateNode);

            for (int i = 0; i < delegateChildCount; i++) {
                AbstractProxyTreeTableNode<N> newChild = new ProxySortableTreeTableNode<N>((N) delegateModel.getChild(delegateNode, i), model);
                insert(newChild, i);
            }

            // Sort, but don't fire events
            if (model.isSorted()) {
                sortNode(false);
            }

            if (fireChanges) {
                nodeChildrenChanged(oldChildren, children);
            }
        }
    }

    @Override
    public ProxySortableTreeTableNode<N> getChildAt(int index) {
        return (ProxySortableTreeTableNode<N>) super.getChildAt(index);
    }

    public void sortCriteriaChanged() {

        sortNode(true);

        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).sortCriteriaChanged();
        }
    }

    /** Sorts the node according to the current sort order.
     *
     * This should only be called when the sort order changes.  It won't hurt anything to
     * call it at other times, but it will do extra work.
     * <p>
     * This should not be called from buildNode(boolean) if the Tree isn't currently
     * sorted, since it will lead to infinite recursion.
     *
     * @param fireChanges Indicates that change events should be fired if the list changes.
     */
    protected void sortNode(boolean fireChanges) {
        if (built) {
            if (children != null && children.size() > 1) {
                if (model.isSorted()) {
                    Comparator<N> comparator = model.getDelegateSortComparator(this);

                    if (comparator != null && children != null) {

                        Comparator<AbstractProxyTreeTableNode> c2 = new ProxyComparator(comparator);

                        List oldChildren = null;

                        if (fireChanges) {
                            oldChildren = (List) children.clone();
                        }

                        Collections.sort(children, c2);

                        if (fireChanges) {
                            nodeChildrenChanged(oldChildren, children);
                        }
                    }
                }
                else {
                    // Rebuild the node from scratch to get the natural ordering...
                    resortToNaturalOrder(fireChanges);
                }
            }
        }
    }

    private void resortToNaturalOrder(boolean fireChanges) {
        if (built && children != null && children.size() > 1) {
            List oldChildren = null;

            if (children != null) {
                oldChildren = (List) children.clone();
                children.clear();
            }

            TreeTableModel delegateModel = getModel().getDelegateTreeTableModel();
            int delegateChildCount = delegateModel.getChildCount(delegateNode);

            for(int i = 0; i < delegateChildCount; i++) {
                N delegateChild = (N) delegateModel.getChild(delegateNode, i);
                AbstractProxyTreeTableNode<N> proxyChild = getModel().getProxyForNode(delegateChild);

                children.add(proxyChild);
            }

            if ( fireChanges ) {
                nodeChildrenChanged(oldChildren, children);
            }
        }
    }

    private static class ProxyComparator implements Comparator<AbstractProxyTreeTableNode> {

        private final Comparator comparator;

        public ProxyComparator(Comparator comparator) {
            this.comparator = comparator;
        }

        @Override
        public int compare(AbstractProxyTreeTableNode o1, AbstractProxyTreeTableNode o2) {
            return comparator.compare(o1.delegateNode, o2.delegateNode);
        }
    }
}
