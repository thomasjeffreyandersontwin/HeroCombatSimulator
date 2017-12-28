package treeTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreeNode;

@SuppressWarnings("serial")
public abstract class AbstractProxyTreeTableNode<N> implements TreeNode {

    protected N delegateNode;

    protected boolean built = false;

    private boolean updating = false;

    protected ArrayList<AbstractProxyTreeTableNode<N>> children;

    protected AbstractProxyTreeTableNode<N> parent;

    protected LinkedList<HeldNodeEvent> heldEvents;

    protected int firstHeldChildEventIndex;

    public AbstractProxyTreeTableNode(N node) {
        this.delegateNode = node;
    }

    protected abstract AbstractProxyTreeTableModel getModel();

    /** Builds the node.
     *
     * This is often done lazily when needed.  Unfortunately, that will often be rigth away
     * since the Tree is often interested in the number of children immediately and most
     * proxies will have to build in order to accurately report the number of children.
     *
     * @param fireChanges Indicates if change events should be fired.
     */
    protected abstract void buildNode(boolean fireChanges);

    /** Rebuilds the node based upon the current delegate state.
     *
     * If the node has not yet been built, this does nothing.  This should
     * not be called ever as a side effect of buildNode since it will lead
     * to an infinite loop!
     *
     * @param fireChanges Indicates if change events should be fired.
     */
    protected void rebuildNode(boolean fireChanges) {
        if (built) {
            built = false;
            buildNode(fireChanges);
        }
    }

    /** Informs the ProxyNode that it's delegate node changed.
     *
     * @param e The tree model event that occurred.  The path of the event will end
     * with the delegateNode.
     */
    public void delegateNodesChanged(TreeModelEvent e) {

        //assert SwingUtilities.isEventDispatchThread();

        // The nodes have been reordered, so we have to get the new indecies...
        int[] newIndecies = getIndeciesForDelegateChildren(e.getChildren());
        Object[] newObjects = getDelegateChildrenForIndecies(newIndecies);

        if ( newObjects != null ) {
            nodeChanged(this, (N[])e.getPath(), newIndecies, newObjects);
        }
    }

    protected int[] getIndeciesForDelegateChildren(Object[] delegateChildren) {

        if ( delegateChildren != null && delegateChildren.length > 0 ) {

            List<Integer> list = new ArrayList<Integer>();

            for (int i = 0; i < delegateChildren.length; i++) {
                AbstractProxyTreeTableNode proxyNode = getModel().getProxyForNode(delegateChildren[i]);

                if ( proxyNode != null ) {
                    int index = getIndex(  proxyNode );
                    if ( index != -1 ) {
                        list.add(index);
                    }
                }
            }

            if ( list.size() > 0 ) {
                int[] array = toArray(list);
                Arrays.sort(array);
                return array;
            }
            
        }

        return null;
    }

    protected Object[] getDelegateChildrenForIndecies(int[] indecies) {
        if ( indecies != null && indecies.length > 0) {
            Object[] o = new Object[indecies.length];
            for (int i = 0; i < indecies.length; i++) {
                o[i] = getChildAt(indecies[i]).delegateNode;
            }
            return o;
        }

        return null;
    }

    /** Informs the ProxyNode that it's delegate node had elements inserted.
     *
     * @param e The tree model event that occurred.  The path of the event will end
     * with the delegateNode.
     */
    public abstract void delegateNodesInserted(TreeModelEvent e);

    /** Informs the ProxyNode that it's delegate node had elements removed.
     *
     * @param e The tree model event that occurred.  The path of the event will end
     * with the delegateNode.
     */
    public abstract void delegateNodesRemoved(TreeModelEvent e);

    /** Informs the ProxyNode that it's delegate node had a complete structure change.
     *
     * @param e The tree model event that occurred.  The path of the event will end
     * with the delegateNode.
     */
    public abstract void delegateStructureChanged(TreeModelEvent e);

    @Override
    public AbstractProxyTreeTableNode<N> getChildAt(int index) {

        buildNode(false);

        if (children == null || index < 0 || index >= children.size()) {
            throw new IndexOutOfBoundsException();
        }

        return children.get(index);
    }

    @Override
    public AbstractProxyTreeTableNode<N> getParent() {
        return parent;
    }

    public void setParent(AbstractProxyTreeTableNode<N> parent) {
        this.parent = parent;
    }

    @Override
    public Enumeration children() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getAllowsChildren() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getChildCount() {
        buildNode(false);

        return children != null ? children.size() : 0;
    }

    @Override
    public int getIndex(TreeNode node) {
        buildNode(false);

        return children != null ? children.indexOf(node) : -1;
    }

    @Override
    public boolean isLeaf() {
        return getModel().getDelegateTreeTableModel().isLeaf(delegateNode);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[ " + (delegateNode != null ? delegateNode.toString() : null) + " ]";
    }

    protected AbstractProxyTreeTableNode<N>[] getPathToRoot(AbstractProxyTreeTableNode<N> aNode, int depth) {
        AbstractProxyTreeTableNode<N>[] retNodes;

        /* Check for null, in case someone passed in a null delegateNode, or
        they passed in an element that isn't rooted at root. */
        if (aNode == null) {
            if (depth == 0) {
                return null;
            }
            else {
                retNodes = new AbstractProxyTreeTableNode[depth];
            }
        }
        else {
            depth++;
            retNodes = getPathToRoot(aNode.getParent(), depth);
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }

//    public AbstractProxyTreeTableNode<N>[] getPath() {
//        return getPathToRoot(this, 0);
//    }
    protected N[] getDelegatePathToRoot(AbstractProxyTreeTableNode<N> aNode, int depth) {
        AbstractProxyTreeTableNode<N>[] path = getPathToRoot(aNode, depth);

        Object[] delegatePath = new Object[path.length];

        for (int i = 0; i < path.length; i++) {
            AbstractProxyTreeTableNode<N> proxyNode = path[i];
            delegatePath[i] = proxyNode.delegateNode;
        }

        return (N[]) delegatePath;
    }

    public N[] getDelegatePath() {
        return getDelegatePathToRoot(this, 0);
    }

    public void add(AbstractProxyTreeTableNode<N> newChild) {
        if (newChild != null && newChild.getParent() == this) {
            insert(newChild, getChildCount() - 1);
        }
        else {
            insert(newChild, getChildCount());
        }
    }

    public void insert(AbstractProxyTreeTableNode<N> newChild, int childIndex) {
        if (newChild == null) {
            throw new IllegalArgumentException("new child is null");
        }
        else if (isNodeAncestor(newChild)) {
            throw new IllegalArgumentException("new child is an ancestor");
        }

        AbstractProxyTreeTableNode<N> oldParent = newChild.getParent();

        if (oldParent != null) {
            oldParent.remove(newChild);
        }
        newChild.setParent(this);
        if (children == null) {
            children = new ArrayList<AbstractProxyTreeTableNode<N>>();
        }
        children.add(childIndex, newChild);

        getModel().addNodeToProxyMapping(newChild);
    }

    public void remove(AbstractProxyTreeTableNode<N> aChild) {
        if (aChild == null) {
            throw new IllegalArgumentException("argument is null");
        }

        if (!isNodeChild(aChild)) {
            throw new IllegalArgumentException("argument is not a child");
        }
        remove(getIndex(aChild));	// linear search
    }

    public AbstractProxyTreeTableNode<N> remove(int childIndex) {
        AbstractProxyTreeTableNode<N> child = getChildAt(childIndex);
        children.remove(childIndex);
        child.setParent(null);

        getModel().removeNodeToProxyMapping(child);

        return child;
    }

    public boolean isNodeAncestor(AbstractProxyTreeTableNode<N> anotherNode) {
        if (anotherNode == null) {
            return false;
        }

        AbstractProxyTreeTableNode<N> ancestor = this;

        do {
            if (ancestor == anotherNode) {
                return true;
            }
        } while ((ancestor = ancestor.getParent()) != null);

        return false;
    }

    public boolean isNodeChild(AbstractProxyTreeTableNode<N> aNode) {
        boolean retval;

        if (aNode == null) {
            retval = false;
        }
        else {
            if (getChildCount() == 0) {
                retval = false;
            }
            else {
                retval = (aNode.getParent() == this);
            }
        }

        return retval;
    }

    public void removeAllChildren() {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            remove(i);
        }
    }

    /** Determines the set of removes and inserts that occurred to the children and triggers the appropriate update events.
     *
     * @param oldChildren
     * @param newChildren
     */
    protected void nodeChildrenChanged(List<AbstractProxyTreeTableNode<N>> oldChildren, List<AbstractProxyTreeTableNode<N>> newChildren) {

        if (oldChildren != newChildren && (oldChildren == null || oldChildren.equals(newChildren) == false)) {

            List<Integer> insertedIndecies = new ArrayList<Integer>();
            List<Integer> removedIndecies = new ArrayList<Integer>();
            List<AbstractProxyTreeTableNode<N>> removedChildren = new ArrayList<AbstractProxyTreeTableNode<N>>();

            calculateListDifference(oldChildren, newChildren, removedIndecies, removedChildren, insertedIndecies, null);

            if (removedChildren.size() > 0) {
                int[] ri = new int[removedIndecies.size()];
                Object[] ro = new Object[removedChildren.size()];
                for (int i = 0; i < ri.length; i++) {
                    ri[i] = removedIndecies.get(i);
                    ro[i] = removedChildren.get(i).delegateNode;
                }

                nodesWereRemoved(this, ri, ro);
            }

            if (insertedIndecies.size() > 0) {
                int[] ii = new int[insertedIndecies.size()];
                Object[] io = new Object[insertedIndecies.size()];

                for (int i = 0; i < ii.length; i++) {
                    ii[i] = insertedIndecies.get(i);
                    io[i] = newChildren.get(ii[i]).delegateNode;
                }

                nodesWereInserted(this, ii, io);
            }
        }
    }

    /** Determines the set of removes and inserts that occurred to the children and triggers the appropriate update events.
     *
     * @param oldChildren
     * @param newChildren
     */
    protected void nodeDelegateChildrenChanged(List<N> oldChildren, List<N> newChildren) {

        if (oldChildren != newChildren && (oldChildren == null || oldChildren.equals(newChildren) == false)) {

            List<Integer> insertedIndecies = new ArrayList<Integer>();
            List<Integer> removedIndecies = new ArrayList<Integer>();
            List<N> removedChildren = new ArrayList<N>();
            List<N> insertedChildren = new ArrayList<N>();

            calculateListDifference(oldChildren, newChildren, removedIndecies, removedChildren, insertedIndecies, insertedChildren);

            if (removedChildren.size() > 0) {
                int[] ri = toArray(removedIndecies);
                Object[] ro = removedChildren.toArray();

                nodesWereRemoved(this, ri, ro);
            }

            if (insertedIndecies.size() > 0) {
                int[] ii = toArray(insertedIndecies);
                Object[] io = insertedChildren.toArray();

                nodesWereInserted(this, ii, io);
            }
        }
    }

    /** Calculates the set of remove and insert operations necessary to convert sourceList to targetList.
     *
     * This method will calculate the set of removes and adds to convert from sourceList
     * to targetList.  The set of operations is calculated such that all of the remove
     * operations occur first, followed by all of the add operations.
     * <P>
     * The standard equal(Object) method is used to determine if the two objects are
     * equals.  Objects can occur multiple times (I think).  Null entries are supported (I think).
     * <P>
     * The results are appended to the end of the results lists, removedIndecies, removedObjects,
     * insertedIndecies, & insertedObjects.  One or more of these lists can be null, in which
     * case the respective type of result will just be dropped.
     * <P>
     * The List.get(int) method is used to obtain the elements of the list, so this method
     * may be inefficient on linked list structures.
     *
     * @param <T> Type of objects in the lists.
     * @param sourceList Source list.  If null, an empty source list is used.
     * @param targetList Target list.  If null, an empty target list is used.
     * @param removedIndecies Indecies of objects in sourceList that need to be removed to create targetList.  Indecies are in terms of sourceList positions.
     * @param removedObjects  Object in sourceList that need to be removed to create targetList.
     * @param insertedIndecies Indecies of objects in targetList that need to be added to sourceList.  Indecies are in terms of targetList positions.
     * @param insertedObjects Objects in targetList that need to be added to sourceList.
     * @return true if changes are necessary to convert from sourceList to targetList.
     */
    protected <T> boolean calculateListDifference(List<T> sourceList, List<T> targetList, List<Integer> removedIndecies, List<T> removedObjects, List<Integer> insertedIndecies, List<T> insertedObjects) {
        boolean different = false;

        if (sourceList != targetList && (sourceList == null || sourceList.equals(targetList) == false)) {

            int sourceSize = sourceList == null ? 0 : sourceList.size();
            int targetSize = targetList == null ? 0 : targetList.size();

            int sourceIndex = 0;
            int targetIndex = 0;

            T sourceObject = null;
            T targetObject = null;

            while (sourceIndex < sourceSize && targetIndex < targetSize) {
                sourceObject = sourceList.get(sourceIndex);
                targetObject = targetList.get(targetIndex);

                if (sourceObject == targetObject || (sourceObject != null && sourceObject.equals(targetObject))) {
                    sourceIndex++;
                    targetIndex++;
                }
                else {
                    int foundNewIndex = -1;
                    for (int n = targetIndex + 1; n < targetSize; n++) {
                        targetObject = targetList.get(n);
                        if (sourceObject == targetObject || (sourceObject != null && sourceObject.equals(targetObject))) {
                            foundNewIndex = n;
                            break;
                        }
                    }

                    if (foundNewIndex == -1) {
                        // It was removed
                        if (removedIndecies != null) {
                            removedIndecies.add(sourceIndex);
                        }
                        if (removedObjects != null) {
                            removedObjects.add(sourceObject);
                        }
                        different = true;
                    }
                    else {
                        // Indecies newIndex to foundNewIndex - 1 are new nodes...
                        for (int n = targetIndex; n < foundNewIndex; n++) {
                            if (insertedIndecies != null) {
                                insertedIndecies.add(n);
                            }
                            if (insertedObjects != null) {
                                insertedObjects.add(targetList.get(n));
                            }
                            different = true;
                        }
                        targetIndex = foundNewIndex + 1;
                    }
                    sourceIndex++;
                }
            }

            // We came to the end of one of the lists...
            if (sourceIndex < sourceSize) {
                // everything at or beyond oldIndex must be a delete...
                for (int o = sourceIndex; o < sourceSize; o++) {
                    // These are deletes...
                    if (removedIndecies != null) {
                        removedIndecies.add(o);
                    }
                    if (removedObjects != null) {
                        removedObjects.add(sourceList.get(o));
                    }
                    different = true;
                }
            }
            else if (targetIndex < targetSize) {
                // everything at or beyond newIndex must be an add...
                for (int n = targetIndex; n < targetSize; n++) {
                    // These are all adds...
                    if (insertedIndecies != null) {
                        insertedIndecies.add(n);
                    }
                    if (insertedObjects != null) {
                        insertedObjects.add(targetList.get(n));
                    }
                    different = true;
                }
            }
        }

        return different;
    }

    protected void nodeStructureChanged(AbstractProxyTreeTableNode<N> changedNode) {
        nodeStructureChanged(changedNode, null);
    }

    protected void nodeStructureChanged(AbstractProxyTreeTableNode<N> changedNode, N[] delegatePath) {
        if (delegatePath == null) {
            delegatePath = (N[]) changedNode.getDelegatePath();
        }

        if (updating) {
            addHeldEvent(new HeldNodeStructureChangedEvent(changedNode, delegatePath));
        }
        else {
            if (getModel().getProxyRoot() == this) {
                getModel().fireTreeStructureChanged(changedNode, changedNode.getDelegatePath(), null, null);
            }
            else if (getParent() != null) {
                getParent().nodeStructureChanged(changedNode, delegatePath);
            }
        }
    }

    protected void nodesWereRemoved(AbstractProxyTreeTableNode<N> changedNode, int[] removeIndecies, Object[] removedDelegateObjects) {
        nodesWereRemoved(changedNode, null, removeIndecies, removedDelegateObjects);
    }

    protected void nodesWereRemoved(AbstractProxyTreeTableNode<N> changedNode, N[] delegatePath, int[] removeIndecies, Object[] removedDelegateObjects) {
        if (delegatePath == null) {
            delegatePath = (N[]) changedNode.getDelegatePath();
        }

        if (updating) {
            addHeldEvent(new HeldNodesWereRemovedEvent(changedNode, delegatePath, removeIndecies, removedDelegateObjects));
        }
        else {
            if (getModel().getProxyRoot() == this) {
                getModel().fireTreeNodesRemoved(changedNode, changedNode.getDelegatePath(), removeIndecies, removedDelegateObjects);
            }
            else if (getParent() != null) {
                getParent().nodesWereRemoved(changedNode, delegatePath, removeIndecies, removedDelegateObjects);
            }
        }
    }

    protected void nodesWereInserted(AbstractProxyTreeTableNode<N> changedNode, int[] insertedIndeces, Object[] insertedDelegateObjects) {
        nodesWereInserted(changedNode, null, insertedIndeces, insertedDelegateObjects);
    }

    protected void nodesWereInserted(AbstractProxyTreeTableNode<N> changedNode, N[] delegatePath, int[] insertedIndeces, Object[] insertedDelegateObjects) {
        if (delegatePath == null) {
            delegatePath = (N[]) changedNode.getDelegatePath();
        }

        if (updating) {
            addHeldEvent(new HeldNodesWereInsertedEvent(changedNode, delegatePath, insertedIndeces, insertedDelegateObjects));
        }
        else {
            if (getModel().getProxyRoot() == this) {
                getModel().fireTreeNodesInserted(changedNode, delegatePath, insertedIndeces, insertedDelegateObjects);
            }
            else if (getParent() != null) {
                getParent().nodesWereInserted(changedNode, delegatePath, insertedIndeces, insertedDelegateObjects);
            }
        }
    }

    protected void nodeChanged(AbstractProxyTreeTableNode<N> parentNode, int[] changedIndecies, Object[] changedObjects) {
        nodeChanged(parentNode, null, changedIndecies, changedObjects);
    }

    protected void nodeChanged(AbstractProxyTreeTableNode<N> parentNode, N[] delegatePath, int[] changedIndecies, Object[] changedObjects) {
        if (delegatePath == null) {
            delegatePath = (N[]) parentNode.getDelegatePath();
        }

        if (updating) {
            addHeldEvent(new HeldNodeChangedEvent(parentNode, delegatePath, changedIndecies, changedObjects));
        }
        else {
            if (getModel().getProxyRoot() == this) {
                getModel().fireTreeNodesChanged(this, delegatePath, changedIndecies, changedObjects);
            }
            else if (getParent() != null) {
                getParent().nodeChanged(parentNode, delegatePath, changedIndecies, changedObjects);
            }
        }
    }

    /**
     * @return the updating
     */
    public boolean isUpdating() {
        return updating;
    }

    /**
     * @param updating the updating to set
     */
    public void setUpdating(boolean updating) {
        if (this.updating != updating) {
            this.updating = updating;

            if (this.updating == false) {
                fireHeldEvents();
            }
        }
    }

    private void addHeldEvent(HeldNodeEvent event) {
        if (heldEvents == null) {
            heldEvents = new LinkedList<HeldNodeEvent>();
            heldEvents.add(event);

            firstHeldChildEventIndex = 0;
        }
        else if (event.node == this) {
            // One of my events, so insert it in front of children...
            heldEvents.add(firstHeldChildEventIndex++, event);
        }
        else {
            // Child event
            heldEvents.add(event);
        }
    }

    private void fireHeldEvents() {
        if (heldEvents != null) {
            while (heldEvents.isEmpty() == false) {
                HeldNodeEvent event = heldEvents.pollFirst();
                event.fireEvent();
            }
            heldEvents = null;
        }
    }

    protected int[] toArray(List<Integer> insertedProxyIndecies) {
        int[] indexArray = new int[insertedProxyIndecies.size()];
        for (int i = 0; i < insertedProxyIndecies.size(); i++) {
            Integer integer = insertedProxyIndecies.get(i);
            indexArray[i] = integer;
        }
        return indexArray;
    }

    protected abstract class HeldNodeEvent {

        AbstractProxyTreeTableNode<N> node;

        N[] path;

        public HeldNodeEvent(AbstractProxyTreeTableNode<N> node, N[] path) {
            this.node = node;
            this.path = path;
        }

        public abstract void fireEvent();
    }

    protected class HeldNodeStructureChangedEvent extends HeldNodeEvent {

        public HeldNodeStructureChangedEvent(AbstractProxyTreeTableNode node, N[] path) {
            super(node, path);
        }

        @Override
        public void fireEvent() {
            nodeStructureChanged(node, path);
        }
    }

    protected class HeldNodesWereRemovedEvent extends HeldNodeEvent {

        int[] insertedIndeces;

        Object[] insertedDelegateObjects;

        public HeldNodesWereRemovedEvent(AbstractProxyTreeTableNode node, N[] path, int[] insertedIndeces, Object[] insertedDelegateObjects) {
            super(node, path);
            this.insertedIndeces = insertedIndeces;
            this.insertedDelegateObjects = insertedDelegateObjects;
        }

        @Override
        public void fireEvent() {
            nodesWereRemoved(node, path, insertedIndeces, insertedDelegateObjects);
        }
    }

    protected class HeldNodesWereInsertedEvent extends HeldNodeEvent {

        int[] insertedIndeces;

        Object[] insertedDelegateObjects;

        public HeldNodesWereInsertedEvent(AbstractProxyTreeTableNode node, N[] path, int[] insertedIndeces, Object[] insertedDelegateObjects) {
            super(node, path);
            this.insertedIndeces = insertedIndeces;
            this.insertedDelegateObjects = insertedDelegateObjects;
        }

        @Override
        public void fireEvent() {
            nodesWereInserted(node, path, insertedIndeces, insertedDelegateObjects);
        }
    }

    protected class HeldNodeChangedEvent extends HeldNodeEvent {

        int[] changedIndecies;
        Object[] changedObjects;

        public HeldNodeChangedEvent(AbstractProxyTreeTableNode<N> node, N[] path, int[] changedIndecies, Object[] changedObjects) {
            super(node, path);
            this.changedIndecies = changedIndecies;
            this.changedObjects = changedObjects;
        }

        @Override
        public void fireEvent() {
            nodeChanged(parent, path, changedIndecies, changedObjects);
        }
    }
}
