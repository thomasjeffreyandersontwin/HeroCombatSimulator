/*
 * AbilityTree.java
 *
 * Created on June 19, 2001, 3:53 PM
 */

package champions.abilityTree;

import tjava.ContextMenu;
import champions.Preferences;
import champions.Target;
import tjava.ContextMenuListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.ActionMap;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import tjava.Destroyable;
import tjava.GlobalClipboard;
import tjava.SharedPopupAction;
import treeTable.DefaultTreeTable;
import treeTable.DefaultTreeTableColumnModel;
import treeTable.TreeTableColumn;
import treeTable.TreeTableColumnModel;
import treeTable.TreeTableColumnModelEvent;
import treeTable.TreeTableModel;









/**
 *
 * @author  twalker
 * @version
 */
public class AbilityTreeTable extends DefaultTreeTable
implements ContextMenuListener, Destroyable {
    
    /** Holds value of property target. */
    private Target target;
    
    private AbilityTreeTable.CopyAction copyAction;
    private AbilityTreeTable.CutAction cutAction;
    private AbilityTreeTable.PasteAction pasteAction;
    private JMenuItem copyMenuItem, cutMenuItem, pasteMenuItem;
    private InputMap copyPasteInputMap;
    private ActionMap copyPasteActionMap;
    private AbilityTreeDnDListener atdtl;
    
//    static private BufferedImage dragImage;
//    static private Point dragOffset;
//    static private boolean dragging;
    
    /** Creates new AbilityTree */
    public AbilityTreeTable() {
        
        AbilityExpansionListener ael = new AbilityExpansionListener();
        this.addTreeExpansionListener(ael);
        
        atdtl = new AbilityTreeDnDListener(this);
        
        //AbilityTreeTableMouseListener attml = new AbilityTreeTableMouseListener(this);
        ToolTipManager.sharedInstance().registerComponent(this);
        
        getSelectionModel().setSelectionMode( DefaultTreeSelectionModel.SINGLE_TREE_SELECTION );
        
        addTreeSelectionListener( new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath tree = e.getPath();
                if ( tree.getLastPathComponent() instanceof AbilityTreeNode ) {
                    AbilityTreeNode atn = (AbilityTreeNode)tree.getLastPathComponent();
                    copyAction.setNode( atn );
                    cutAction.setNode( atn );
                    pasteAction.setNode( atn );
                    
                    // Also Bind the input maps appropriately.
                    mergeKeyBindings( atn);
                }
            }
        });
        
        
        this.putClientProperty("JTree.lineStyle", "Angled");
        this.setShowsRootHandles(false);
        this.setRootVisible(false);
        this.setEditable(true);
        this.setInvokesStopCellEditing(true);
        this.setLargeModel(true);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        ContextMenu.addContextMenu(this);
        
        setupActions();
        
        setupKeyBindings();
        
        addFocusListener( new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if ( isEditing() ) {
              //      editNextNode();
                }
            }
        });
        
        setupColors();
        
    }
    
    public AbilityTreeTable(Target target) {
        setTarget(target);
    }

    private void setupActions() {
        copyAction = new CopyAction();
        cutAction = new CutAction();
        pasteAction = new PasteAction();
        
        copyMenuItem = new JMenuItem(copyAction);
        copyMenuItem.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK ) );
        
        cutMenuItem = new JMenuItem(cutAction);
        cutMenuItem.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK ) );
        
        pasteMenuItem = new JMenuItem(pasteAction);
        pasteMenuItem.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK ) );
    }
    
    private void setupKeyBindings() {
//        copyPasteInputMap = new InputMap();
//        copyPasteInputMap.put( KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), "copy" );
//        copyPasteInputMap.put( KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK), "cut" );
//        copyPasteInputMap.put( KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), "paste" );
        
        copyPasteActionMap = getActionMap();
        copyPasteActionMap.put( "copy", copyAction );
        copyPasteActionMap.put( "cut", cutAction );
        copyPasteActionMap.put( "paste", pasteAction );
        
    }
    
    private void setupColors() {
                Color c;
        c = UIManager.getColor( "AbilityEditor.foreground" );
        if ( c != null ) this.setForeground(c);
        c = UIManager.getColor( "AbilityEditor.background" );
        if ( c != null ) this.setBackground(c);
    }
    
//    protected TreeTableColumnModel buildColumnModel(TreeTableModel ttm) {
//        boolean overridePrefs = false;
//        // Setup the ColumnModel
//        //Preferences.getPreferenceList().setColumnModel("CharacterEditor.ABILITYCOLUMNMODEL", null);
//        Object o = Preferences.getPreferenceList().getColumnModel("CharacterEditor.ABILITYCOLUMNMODEL");
//        if ( overridePrefs == false && o instanceof TreeTableColumnModel  ) {
//            TreeTableColumnModel tcm = (TreeTableColumnModel)o;
//            if ( tcm != null ) {
//                tcm = copyTreeTableColumnModel(tcm);
//                return tcm;
//            }
//        }
//
//       // Preferences.getPreferenceList().remove("CharacterEditor.ABILITYCOLUMNMODEL");
//
//        return getProxyTreeTableModel().getColumnModel();
//    }
//
    static public TreeTableColumnModel copyTreeTableColumnModel(TreeTableColumnModel tcm) {
        
        TreeTableColumnModel newModel = new DefaultTreeTableColumnModel();
            
            int index,count;
            count = tcm.getColumnCount();
            for(index=0;index<count;index++) {
                TreeTableColumn tc = tcm.getColumn(index);
                TreeTableColumn newTC = new TreeTableColumn(tc.getModelIndex(), tc.getWidth());
                newModel.addColumn(newTC);
             //   newTC.setCellEditor(tc.getCellEditor());
             //   newTC.setCellRenderer(tc.getCellRenderer());
             //   newTC.setHeaderRenderer(tc.getHeaderRenderer());
                newTC.setHeaderValue(tc.getHeaderValue());
                newTC.setIdentifier(tc.getIdentifier());
                newTC.setMaxWidth(tc.getMaxWidth());
                newTC.setMinWidth(tc.getMinWidth());
                newTC.setPreferredWidth(tc.getPreferredWidth());
                newTC.setResizable(tc.getResizable());
                
            }
            
            newModel.setColumnMargin( tcm.getColumnMargin() );
            newModel.setColumnSelectionAllowed( tcm.getColumnSelectionAllowed() );
            newModel.setSelectionModel( new DefaultListSelectionModel() );
            
            return newModel;
    }
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        if ( this.target != target ) {
            TreeTableModel oldModel = getProxyTreeTableModel();
            
            this.target = target;
            AbilityListNode targetRoot;
            
            if ( target != null ) {
                targetRoot = new AbilityListNode(target,target.getAbilityList(),null);
            }
            else {
                targetRoot = new AbilityListNode(target,null,null);
            }
            AbilityTreeTableModel atm = new AbilityTreeTableModel( targetRoot );
            targetRoot.setModel(atm);
            targetRoot.setTree(this);
            setTreeTableModel(atm);
            
            if ( oldModel instanceof Destroyable ) {
                ((Destroyable)oldModel).destroy();
            }
        }
    }
    
    public void setScrollPane(JScrollPane scrollPane) {
         atdtl.setScrollPane(scrollPane);
    }
    
    /**
     * Tells listeners that the selection model of the
     * TreeTableColumnModel changed.
     */
    public void columnSelectionChanged(ListSelectionEvent e) {
        super.columnSelectionChanged(e);
        updateColumnPreferences(getColumnModel());	
    }    
    
    /** Tells listeners that a column was removed from the model.  */
    public void columnRemoved(TreeTableColumnModelEvent e) {
        super.columnRemoved(e);
        updateColumnPreferences(getColumnModel());	
    }
    
    /** Tells listeners that a column was moved due to a margin change.  */
    public void columnMarginChanged(ChangeEvent e) {
        if ( reentrantCall == true ) return;
        super.columnMarginChanged(e);
        updateColumnPreferences(getColumnModel());	
    }
    
    /** Tells listeners that a column was repositioned.  */
    public void columnMoved(TreeTableColumnModelEvent e) {
        super.columnMoved(e);
        updateColumnPreferences(getColumnModel());
    }
    
    /** Tells listeners that a column was added to the model.  */
    public void columnAdded(TreeTableColumnModelEvent e) {
        super.columnAdded(e);
        updateColumnPreferences(getColumnModel());
    }
    
    private void updateColumnPreferences(TreeTableColumnModel tcm) {
        if ( getBaseTreeTableModel() instanceof AbilityTreeTableModel ) {
            // Only update if the AbilityTreeTableModel is in use...
            tcm = copyTreeTableColumnModel( tcm );
            Preferences.getPreferenceList().setColumnModel("CharacterEditor.ABILITYCOLUMNMODEL", tcm);
        }
    }
    
    /**
     * Causes this table to lay out its rows and columns.  Overridden so
     * that columns can be resized to accomodate a change in the size of
     * a containing parent.
     */
    @Override
    public void doLayout() { 
	super.doLayout(); 
        updateColumnPreferences( getColumnModel() );
    }
    
    
    public void selectNode(String name) {
        if ( getModel().getRoot() instanceof AbilityTreeNode ) {
            AbilityTreeNode node = ((AbilityTreeNode)getModel().getRoot()).findNode(name);
            if ( node != null ) {
                setSelectionPath( new TreePath( node.getPath() ) );
            }
        }
    }
    
    @Override
    public boolean invokeMenu(JPopupMenu popup,Component inComponent,Point inPoint) {
        //TreePath path = getClosestPathForLocation(inPoint.x,inPoint.y);
        TreePath path = getClosestPathForLocation(inPoint.x,inPoint.y);
        if(path != null ) {
            if (  getPathBounds(path).contains(inPoint) == false ) {
                path = path.getParentPath();
            }
        
            setSelectionPath(path);
        }
        else {
            path = new TreePath( getModel().getRoot() );
        }
        
        
        boolean rv = false;
        Object node;
        
        int index;
        for(index = path.getPathCount() - 1;index >= 0;index--) {
            node = path.getPathComponent(index);
            if ( node instanceof AbilityTreeNode ) {
                if ( ((AbilityTreeNode)node).invokeMenu(popup, this, path) ) {
                    rv = true;
                }
            }
        }
        
        
        
        if ( target != null ) {
            
            if ( rv == true ) popup.addSeparator();
            
            copyMenuItem.setEnabled(copyAction.isEnabled());
            popup.add(copyMenuItem);
            cutMenuItem.setEnabled(cutAction.isEnabled());
            popup.add(cutMenuItem);
            pasteMenuItem.setEnabled(pasteAction.isEnabled());
            popup.add(pasteMenuItem);
            
            rv = true;
        }
        
        return rv;
    }
    
    public void mergeKeyBindings(AbilityTreeNode atn) {
        InputMap im = null;
        ActionMap am = null;
        
        if (atn != null) {
            im = atn.getInputMap();
            am = atn.getActionMap();
        }
        
        if ( am != null ) {
            am.setParent(copyPasteActionMap);
        }
        else {
            am = copyPasteActionMap;
        }
        
        if ( im != null ) {
            im.setParent(copyPasteInputMap);
        }
        else {
            im = copyPasteInputMap;
        }
        
        setInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, im);
        setActionMap(am);
    }
    
    public boolean isManagingFocus() {
        return true;
    }
    
    public boolean isFocusCycleRoot() {
        return true;
    }
    
    public void editNextNode() {
        TreePath tree = getEditingPath();
        AbilityTreeNode atn = (AbilityTreeNode)tree.getLastPathComponent();
        
        stopEditing();
        
        atn = atn.findNextEditableNode(-1,true);
        
        if ( atn != null ) {
            tree = new TreePath(atn.getPath());
            startEditingAtPath(tree);
        }
    }
    
//    public BufferedImage buildDragImage(TreePath path) {
//        //  Point ptDragOrigin = e.getDragOrigin();
//        //  TreePath path = getPathForLocation(ptDragOrigin.x, ptDragOrigin.y);
//        Rectangle raPath = getPathBounds(path);
//        //  m_ptOffset.setLocation(ptDragOrigin.x-raPath.x, ptDragOrigin.y-raPath.y);
//        
//        Component c =  getCellRenderer().getTreeCellRendererComponent(
//        this,                                           // tree
//        path.getLastPathComponent(),                    // value
//        false,                                          // isSelected
//        isExpanded(path),                               // isExpanded
//        getModel().isLeaf(path.getLastPathComponent()), // isLeaf
//        0,                                              // row
//        false                                           // hasFocus
//        );
//        
//        // The layout manager normally does this...
//        c.setLocation(0,0);
//        c.setSize((int)raPath.getWidth(), (int)raPath.getHeight());
//        
//        
//        // Get a buffered image of the selection for dragging a ghost image
//        BufferedImage image = new BufferedImage((int)raPath.getWidth(),(int)raPath.getHeight(),BufferedImage.TYPE_INT_ARGB_PRE);
//        
//        // Get a graphics context for this image
//        Graphics2D g2 = image.createGraphics();
//        
//        // Make the image ghostlike
//        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.5f));
//        
//        // Ask the cell renderer to paint itself into the BufferedImage
//        c.paint(g2);
//        
//      /*  // Use DST_OVER to cause under-painting to occur
//        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, 0.5f));
//       
//        // Use system colors to match the existing decor
//        g2.setPaint(new GradientPaint(0, 0, SystemColor.controlShadow,
//        getWidth(),   0, new Color(255,255,255,0)));
//       
//        // Paint under the JLabel's text
//        g2.fillRect(0, 0, getWidth(), image.getHeight()); */
//        
//        // Finished with the graphics context now
//        g2.dispose();
//        
//        return image;
//    }
    
//    static public void startDrag(BufferedImage image, Point offset) {
//        dragging = true;
//        dragImage = image;
//        dragOffset = offset;
//    }
//    
//    static public void stopDrag() {
//        dragging = false;
//    }
//    
//    static public boolean isDragging() {
//        return dragging;
//    }
//    
//    static public BufferedImage getDragImage() {
//        return dragImage;
//    }
//    
//    static public Point getDragOffset() {
//        return dragOffset;
//    }
    
    public void destroy() {
        super.destroy();
        
        target = null;
    
        copyAction = null;
        cutAction = null;
        pasteAction = null;
        copyMenuItem = null;
        cutMenuItem = null;
        pasteMenuItem = null;
        copyPasteInputMap = null;
        copyPasteActionMap = null;
        atdtl = null;
    }
    
    public String getToolTipText(MouseEvent mouseEvent) {
        TreePath path = getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
        if ( path != null ) {
            AbilityTreeNode newNode = (AbilityTreeNode) path.getLastPathComponent();
            int column = getColumnForLocation( mouseEvent.getPoint());
            return newNode.getToolTipText(column);
        }
        else {
            return null;
        }
    }
    
    private static  class CopyAction extends SharedPopupAction {
        private AbilityTreeNode node;
        public CopyAction() {
            super("Copy");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK ));
            
        }
        
        public void actionPerformed(ActionEvent e) {
            node.copyOrCutNode(false);
        }
        
        public boolean isEnabled() {
            return node != null ? node.canCopyOrCutNode() : false;
        }
        
        public void setNode(AbilityTreeNode node) {
            this.node = node;
        }
    }
    
    private static class CutAction extends SharedPopupAction {
        private AbilityTreeNode node;
        public CutAction() {
            super("Cut");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK ));
            
        }
        
        public void actionPerformed(ActionEvent e) {
            node.copyOrCutNode(true);
        }
        
        public boolean isEnabled() {
            return node != null ? node.canCopyOrCutNode() : false;
        }
        
        public void setNode(AbilityTreeNode node) {
            this.node = node;
        }
    }
    
    private class PasteAction extends SharedPopupAction {
        private AbilityTreeNode node;
        public PasteAction() {
            super("Paste");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK ));
            
        }
        
        public void actionPerformed(ActionEvent e) {
            Transferable t = GlobalClipboard.getContents(AbilityTreeTable.this);
            node.pasteData(t);
        }
        
        public boolean isEnabled() {
            Transferable t = GlobalClipboard.getContents(AbilityTreeTable.this);
            return node != null ? node.canPasteData(t) : false;
        }
        
        public void setNode(AbilityTreeNode node) {
            this.node = node;
        }
    }
    
}
