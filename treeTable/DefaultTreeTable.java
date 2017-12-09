/*
 * DefaultTreeTable.java
 *
 * Created on February 28, 2002, 9:18 AM
 */
package treeTable;

import tjava.ContextMenu;
import tjava.ContextMenuListener;
import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.DropMode;
import javax.swing.FocusManager;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import tjava.Destroyable;

/**
 *
 * @author  twalker
 * @version
 */
@SuppressWarnings("serial")
public class DefaultTreeTable extends TreeTable
        implements ContextMenuListener, Destroyable, TreeTableFilterListener, FocusListener {

    static private BufferedImage dragImage;

    static private Point dragOffset;

    static private boolean dragging;

    /** Holds value of property dndListener. */
    private boolean dragEnabled = false;

    private boolean legacyDnDEnabled = false;

    private boolean legacyDnDSetup = false;

    private TreeTableLegacyDnDListener legacyDnDListener;

    private DragGestureRecognizer legacyDragGestureRecognizer;

    private DragSource legacyDragSource;

    private DropTarget legacyDropTarget;

    private TreeTableFilterComponentProvider filterComponentProvider;

    private TreeTableKeyMapHandler keyMapHandler = null;

    //private boolean filterable = false;

    /** Creates new DefaultTreeTable */
    public DefaultTreeTable() {
        // setLegacyDnDEnabled(true);
        setDragEnabled(true);

        setupListeners();
        setDropMode(DropMode.ON_OR_INSERT);

        setForceScrollableTracksViewportWidth(true);

    }

    public DefaultTreeTable(boolean enableDnD) {
        if (enableDnD) {
            // setLegacyDnDEnabled(true);
            setDragEnabled(true);
        }

        setupListeners();
        setDropMode(DropMode.ON_OR_INSERT);

        setForceScrollableTracksViewportWidth(true);
    }

    public DefaultTreeTable(TreeNode root) {
        super(root);

        //setLegacyDnDEnabled(true);
        setDragEnabled(true);

        setupListeners();
        setDropMode(DropMode.ON_OR_INSERT);

        setForceScrollableTracksViewportWidth(true);
    }

    public DefaultTreeTable(TreeTableModel model) {
        super(model);

        //setLegacyDnDEnabled(true);
        setDragEnabled(true);

        setupListeners();
        setDropMode(DropMode.ON_OR_INSERT);

        setForceScrollableTracksViewportWidth(true);
    }

    protected void setupListeners() {
        ContextMenu.addContextMenu(this);

        addMouseListener(getMouseListener());
        addKeyListener(getKeyListener());
        addFocusListener(this);

        ToolTipManager.sharedInstance().registerComponent(this);
    }

    protected void setupLegacyDnD() {
        if (legacyDnDSetup == false) {
            legacyDnDListener = createLegacyDnDListener();
            legacyDropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, legacyDnDListener);
            legacyDragSource = DragSource.getDefaultDragSource();
            legacyDragGestureRecognizer = legacyDragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, legacyDnDListener);
            legacyDnDSetup = true;
        }
        else {
            // Already initializes, so make sure we turn everything on...
            legacyDropTarget.setActive(true);
            legacyDragGestureRecognizer.setComponent(this);
            legacyDragGestureRecognizer.setSourceActions(DnDConstants.ACTION_COPY_OR_MOVE);
        }
    }

    protected void disableLegacyDnD() {
        if (legacyDnDSetup) {
            legacyDropTarget.setActive(false);
            legacyDragGestureRecognizer.setComponent(null);
        }
    }

    protected void setupNewDnD() {
        //if ( getTransferHandler() == null ) {
        setTransferHandler(createDnDTransferHandler());
        //}
        super.setDragEnabled(true);
    }

    protected void disableNewDnD() {
        if (getTransferHandler() != null) {
            setTransferHandler(null);
        }
        super.setDragEnabled(false);
    }

    @Override
    public void setDragEnabled(boolean dragEnabled) {
        if (this.dragEnabled != dragEnabled) {
            this.dragEnabled = dragEnabled;

            updateDnDConfiguration();
        }
    }

//    public int getDragSourceActions() {
//        return dragSourceActions;
//    }
//
//    public void setDragSourceActions(int dragSourceActions) {
//        if (this.dragSourceActions != dragSourceActions) {
//            this.dragSourceActions = dragSourceActions;
//            updateDnDConfiguration();
//        }
//    }
    protected TransferHandler createDnDTransferHandler() {
        return new DefaultTreeTableTransferHandler(this);
    }

    /** Indicates that drag-and-drop support is enable on the tree table.
     *
     * Since Java 1.4 there is built-in dnd support on all swing components.
     * However, DefaultTreeTable code supported pre-1.4 dnd.  To use newer
     * Java 1.4 support, legacyDnDEnabled should be set to false.  To use the
     * older legacy support (which you shouldn't unless you are too lazy to
     * port to the newer stuff), set legacyDnDEnabled = true.
     * @return
     */
    public boolean isDragEnabled() {
        return dragEnabled;
    }

    /** Indicates that legacy drag-and-drop support is in use.
     *
     * This only matters if dragEnabled is true.
     *
     * @return
     */
    public boolean isLegacyDnDEnabled() {
        return legacyDnDEnabled;
    }

    public void setLegacyDnDEnabled(boolean legacyDnDEnabled) {
        if (this.legacyDnDEnabled != legacyDnDEnabled) {

            if (legacyDnDEnabled == false && legacyDnDSetup == true) {
                throw new IllegalStateException("Legacy DnD support has already been enabled.  Can't unring that bell!  Please use the appropraite constructor.");
            }

            this.legacyDnDEnabled = legacyDnDEnabled;

            updateDnDConfiguration();
        }
    }

    protected void updateDnDConfiguration() {
        if (dragEnabled) {
            if (legacyDnDEnabled) {
                disableNewDnD();
                setupLegacyDnD();
            }
            else {
                disableLegacyDnD();
                setupNewDnD();
            }
        }
        else {
            disableNewDnD();
            disableLegacyDnD();
        }
    }

    protected TreeTableLegacyDnDListener createLegacyDnDListener() {
        return new DefaultTreeTableLegacyDnDListener(this);
    }

    /** Getter for property dndListener.
     * @return Value of property dndListener.
     */
    public TreeTableLegacyDnDListener getLegacyDndListener() {
        return legacyDnDListener;
    }

    public BufferedImage buildDragImage(TreePath path) {
        //  Point ptDragOrigin = e.getDragOrigin();
        //  TreePath path = getPathForLocation(ptDragOrigin.x, ptDragOrigin.y);
        Rectangle raPath = getPathBounds(path);
        //  m_ptOffset.setLocation(ptDragOrigin.x-raPath.x, ptDragOrigin.y-raPath.y);

        Component c = getCellRenderer().getTreeCellRendererComponent(
                this, // tree
                path.getLastPathComponent(), // value
                false, // isSelected
                isExpanded(path), // isExpanded
                getModel().isLeaf(path.getLastPathComponent()), // isLeaf
                0, // row
                false // hasFocus
                );

        // The layout manager normally does this...
        c.setLocation(0, 0);
        c.setSize((int) raPath.getWidth(), (int) raPath.getHeight());


        BufferedImage image = null;
        Graphics2D g2 = null;
        try {
            // Get a buffered image of the selection for dragging a ghost image
            image = new BufferedImage((int) raPath.getWidth(), (int) raPath.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
            // Get a graphics context for this image
            g2 = image.createGraphics();

            // Make the image ghostlike
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.75f));

            // Ask the cell renderer to paint itself into the BufferedImage
            c.paint(g2);

        } finally {
            if (g2 != null) {
                g2.dispose();
            }
        }

        return image;
    }

    public BufferedImage buildDragImage(TreePath[] paths, int maxWidth) {
        //  Point ptDragOrigin = e.getDragOrigin();
        //  TreePath path = getPathForLocation(ptDragOrigin.x, ptDragOrigin.y);

        int left = Integer.MAX_VALUE;
        int right = Integer.MIN_VALUE;
        int top = Integer.MAX_VALUE;
        int bottom = Integer.MIN_VALUE;

        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            Rectangle raPath = getPathBounds(path);

            left = Math.min(left, raPath.x);
            right = Math.max(right, raPath.x + raPath.width);
            top = Math.min(top, raPath.y);
            bottom = Math.max(bottom, raPath.y + raPath.height);
        }

        int width = right - left;
        int height = bottom - top;

        if (maxWidth > 0) {
            width = Math.min(maxWidth, width);
        }

        BufferedImage image = null;
        Graphics2D g2 = null;
        try {
            // Get a buffered image of the selection for dragging a ghost image
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
            // Get a graphics context for this image
            g2 = image.createGraphics();

            // Make the image ghostlike
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.75f));

            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];
                Rectangle raPath = getPathBounds(path);

                Component c = getCellRenderer().getTreeCellRendererComponent(
                        this, // tree
                        path.getLastPathComponent(), // value
                        false, // isSelected
                        isExpanded(path), // isExpanded
                        getModel().isLeaf(path.getLastPathComponent()), // isLeaf
                        0, // row
                        false // hasFocus
                        );

                int x = raPath.x - left;
                int y = raPath.y - top;

                Graphics2D g3 = null;
                try {
                    g3 = (Graphics2D) g2.create(x, y, Math.min(raPath.width, width), raPath.height);

                    // The layout manager normally does this...
                    c.setLocation(x, y);
                    c.setSize((int) raPath.getWidth(), (int) raPath.getHeight());

                    // Ask the cell renderer to paint itself into the BufferedImage
                    c.paint(g3);
                } finally {
                    if (g3 != null) {
                        g3.dispose();
                    }
                }

            }
        } finally {
            if (g2 != null) {
                g2.dispose();
            }
        }

        return image;
    }

    static public void startDrag(BufferedImage image, Point offset) {
        dragging = true;
        dragImage = image;
        dragOffset = offset;
    }

    static public void stopDrag() {
        dragging = false;
    }

    static public boolean isDragging() {
        return dragging;
    }

    static public BufferedImage getDragImage() {
        return dragImage;
    }

    static public Point getDragOffset() {
        return dragOffset;
    }

    @Override
            public String getToolTipText(MouseEvent evt) {
        Point p = evt.getPoint();

        TreePath path = getPathForLocation((int) p.getX(), (int) p.getY());
        if (path != null) {
            Object lpc = path.getLastPathComponent();

            int column = getColumnForLocation(p);

            return getProxyTreeTableModel().getToolTipText(lpc, column, evt);

        }
        return null;
    }

    @Override
            public boolean invokeMenu(JPopupMenu popup, Component inComponent, Point inPoint) {
        TreePath path = getClosestPathForLocation(inPoint.x, inPoint.y);

        if (isPathSelected(path) == false) {
            // If we are clicking on a non-selected path, make sure we update the 
            // selection...
            setSelectionPath(path);
        }

        boolean rv = false;
        Object node;

        TreeTableModel model = getProxyTreeTableModel();

        TreePath paths[] = getSelectionPaths();

        if (model.invokeMenu(this, paths, popup)) {
            rv = true;
        }


        //  if ( rv == true ) popup.addSeparator();

        /*   copyMenuItem.setEnabled(copyAction.isEnabled());
        popup.add(copyMenuItem);
        cutMenuItem.setEnabled(cutAction.isEnabled());
        popup.add(cutMenuItem);
        pasteMenuItem.setEnabled(pasteAction.isEnabled());
        popup.add(pasteMenuItem); */

        return rv;
    }

    public void expandAll(TreePath path) {
        TreeTableModel model = getProxyTreeTableModel();
        Object node = path.getLastPathComponent();
        if (isExpanded(path) == false) {
            expandPath(path);
        }

        int count = model.getChildCount(node);
        for (int index = 0; index < count; index++) {
            TreePath newPath = path.pathByAddingChild(model.getChild(node, index));
            expandAll(newPath);
        }
    }

    @Override
            public void destroy() {
        super.destroy();
        /** Holds value of property dndListener. */
        legacyDnDListener = null;


    }

    protected void focusIfNecessary() {
        if (isFilterable() == false) {
            return;
        }

        Component focusOwner = FocusManager.getCurrentManager().getFocusOwner();




        boolean needFocus = true;

        if (focusOwner == this) {
            needFocus = false;
        }
        else if (filterComponentProvider != null) {
            JComponent filterComponent = filterComponentProvider.getComponent();
            while (focusOwner != null) {
                if (focusOwner == filterComponent) {
                    needFocus = false;
                    break;
                }

                focusOwner = focusOwner.getParent();
            }
        }
        else {
            while (focusOwner != null) {
                if (focusOwner == this) {
                    needFocus = false;
                    break;
                }

                focusOwner = focusOwner.getParent();
            }
        }

        if (needFocus) {
            requestFocusInWindow();
        }
    }

    private MouseListener getMouseListener() {
        return new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (isFilterable()) {
                    focusIfNecessary();
                }

            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }
        };
    }

    private KeyListener getKeyListener() {
        return new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                
                if (isFilterable()) {

                    maybeShowFilterComponent(e);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //System.out.println("");
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        };
    }

    /** This method creates a default filter component provider.
     *
     * This method can be overriden to create a different filter provider,
     * or the setFitlerComponentProvider can be used to change the filter
     * provider on the fly.
     * <p>
     * This method is only called if no filterComponent provider is
     * currently set when one is requested.
     *
     * @return
     */
    protected TreeTableFilterComponentProvider createFilterComponentProvider() {
        return new DefaultTreeTableFilterComponentProvider();
    }

    public TreeTableFilterComponentProvider getFilterComponentProvider() {
        if (filterComponentProvider == null) {
            setFilterComponentProvider(createFilterComponentProvider());
        }

        return filterComponentProvider;
    }

    public void setFilterComponentProvider(TreeTableFilterComponentProvider filterComponentProvider) {
        if (this.filterComponentProvider != filterComponentProvider) {
            if (this.filterComponentProvider != null) {
                this.filterComponentProvider.removeTreeTableFilterListener(this);

                JComponent c = this.filterComponentProvider.getComponent();
                if ( c != null ) {
                    this.remove(c);
                }
            }

            this.filterComponentProvider = filterComponentProvider;

            if (this.filterComponentProvider != null) {
                this.filterComponentProvider.addTreeTableFilterListener(this);
                
                JComponent c = this.filterComponentProvider.getComponent();
                if ( c != null ) {
                    this.add(c);
                    c.setVisible(false);
                }
            }
        }
    }

    public void maybeShowFilterComponent(KeyEvent e) {

        if (e.isConsumed() || e.isAltDown() || e.isControlDown() || e.isMetaDown()) {
            return;
        }
        if (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
            return;
        }

        KeyStroke stroke = KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers());
        InputMap inputMap = null;

        inputMap = getInputMap(JComponent.WHEN_FOCUSED);
        if ( inputMap != null && inputMap.get(stroke) != null ) {
            return;
        }

        inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        if ( inputMap != null && inputMap.get(stroke) != null ) {
            return;
        }

        inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        if ( inputMap != null && inputMap.get(stroke) != null ) {
            return;
        }

        TreeTableFilterComponentProvider fcp = getFilterComponentProvider();
        JComponent c = fcp.getComponent();
        
        e.consume();

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            fcp.clearFilter();
            c.setVisible(false);
        }
        else if (c.isVisible() == false) {
            Dimension fcDim = c.getPreferredSize();
            int x, y;

            if (getParent() instanceof JViewport) {
                Rectangle viewRect = ((JViewport) getParent()).getViewRect();

                x = viewRect.x + viewRect.width - fcDim.width - 30;
                y = viewRect.y + 10;
            }
            else {
                Rectangle viewRect = getBounds();

                x = viewRect.x + viewRect.width - fcDim.width - 30;
                y = viewRect.y + 10;
            }

            char ch = e.getKeyChar();
            if (ch == '\n') {
                fcp.clearFilter();
            }
            else {
                fcp.handleKeyEvent(e);
            }

            c.setBounds(x, y, fcDim.width, fcDim.height);
            c.setVisible(true);
            c.requestFocusInWindow();

        }
    }

    public void hideFilterComponent() {
        if (filterComponentProvider != null && filterComponentProvider.getComponent().isVisible()) {
            filterComponentProvider.getComponent().setVisible(false);
        }
    }

    @Override
    public void filter(TreeTableFilterEvent event) {
        if (getProxyTreeTableModel() instanceof FilterableTreeTableModel) {
            if (event.id == TreeTableFilterEvent.FILTER_CLEARED) {
                ((FilterableTreeTableModel) getProxyTreeTableModel()).setFilterObject(null);

                restoreTreeExpansionState(true);
            }
            else {

                recordTreeExpansionState();

                ((FilterableTreeTableModel) getProxyTreeTableModel()).setFilterObject(event.getFilterObject());

                restoreTreeExpansionState(false);
            }
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
        Component c = e.getOppositeComponent();

        boolean childHasFocus = false;

        while (c != null) {
            if (c == this) {
                childHasFocus = true;
                break;
            }

            c = c.getParent();
        }

        if (e.getOppositeComponent() != null && childHasFocus == false) {
            hideFilterComponent();
        }
    }

    public boolean isFilterable() {
        if (getProxyTreeTableModel() instanceof FilterableTreeTableModel) {
            FilterableTreeTableModel filterableTreeTableModel = (FilterableTreeTableModel) getProxyTreeTableModel();
            return filterableTreeTableModel.isFilterable();
        }

        return false;
    }

    public void setKeyMapHandlingEnabled(boolean keyMapHandlingEnabled) {
        if (isKeyMapHandlingEnabled() != keyMapHandlingEnabled) {

            if ( keyMapHandlingEnabled == true) {
                keyMapHandler = new TreeTableKeyMapHandler(this);
                this.addTreeSelectionListener(keyMapHandler);
            }
            else {
                this.removeTreeSelectionListener(keyMapHandler);
                keyMapHandler = null;
            }
        }
    }

    public boolean isKeyMapHandlingEnabled() {
        return keyMapHandler != null;
    }

}
