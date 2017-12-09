/*
 * STTargetTree.java
 *
 * Created on June 8, 2004, 10:34 PM
 */

package champions.senseTree;

import champions.Target;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import javax.swing.tree.TreeSelectionModel;
import treeTable.*;

/**
 *
 * @author  1425
 */
public class STSenseTree extends DefaultTreeTable {
    
    /** Holds the Target of this tree. */
    private Target target;
    
    /** Holds the Sublist name to show. */
    private String sublistName;
    
    /** Creates a new instance of STTargetTree */
    public STSenseTree() {
        setupModel();
        setupMouseListener();
        
        TreeTableHeader tth = getTreeTableHeader();
        tth.getColumnModel().getColumn(0).setMinWidth(200);
        
        this.setRootVisible(false);
        
        ToolTipManager.sharedInstance().registerComponent(this);
        
        setShowsRootHandles(false);
        
        setCellRenderer(ImageIcon.class, new IconTreeTableCellRenderer());

        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
     //   setDefaultTreeTableCellRenderer( new AbilityTreeTableCellEditor() );
     //   setDefaultTreeTableCellEditor( new AbilityTreeTableCellEditor() );
        
        setupUI();
    }
    
    public void updateUI() {
        super.updateUI();
        
        setupUI();
    }
    
    private final void setupUI() {
        Color c;
        c = UIManager.getColor( "SenseTree.foreground" );
        if ( c != null ) this.setForeground(c);
        c = UIManager.getColor( "SenseTree.background" );
        if ( c != null ) this.setBackground(c);
        
        Font f = UIManager.getFont("SenseTree.headerFont");
        if ( treeTableHeader != null && f != null ) {
            treeTableHeader.setFont(f);
        }
    }
    
    protected void setupMouseListener() {
  /*      addMouseListener( new MouseAdapter() {
            
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && getAbilityTreeTable() != null && getTarget() != null ) {
                    //int index = PADListListener.this.list.locationToIndex(e.getPoint());
                    TreePath clickPath = TargetTree.this.getSelectionPath();
                    if (clickPath != null && clickPath.getLastPathComponent() instanceof STNode ) {
                        ((STNode)clickPath.getLastPathComponent()).handleDoubleClick(abilityPath);
                    }
                }
            }
        }); */
    }
    
    protected void setupModel() {
        STNode root = new STTargetNode(target);
        STModel model = new STModel(root);
        this.setTreeTableModel(model);
        expandAll(new TreePath(root));
    }
    
    /**
     * Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    
    /**
     * Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        if ( this.target != target  ) {
            
            this.target = target;
        
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)getProxyTreeTableModel().getRoot();
            if ( root instanceof STTargetNode ) {
                ((STTargetNode)root).setTarget(target);
                expandAll(new TreePath(root));
            }
        }
    }
    
//    protected TreeTableColumnModel buildColumnModel(TreeTableModel baseModel) {
//        TreeTableColumnModel tableColumnModel = new DefaultTreeTableColumnModel();
//
//        int count, index;
//
//        count = baseModel.getColumnCount();
//        for ( index = 0; index < count; index ++ ) {
//            TreeTableColumn tc = new TreeTableColumn(index);
//            tc.setHeaderValue(baseModel.getColumnName(index));
//            if ( index == 0 ) {
//                tc.setMinWidth(180);
//            }
//            tableColumnModel.addColumn( tc );
//        }
//
//        //setTableColumnModel(tableColumnModel);
//        return tableColumnModel;
//    }
    
    public void setRoot(STNode root) {
        STModel model = new STModel(root);
        this.setTreeTableModel(model);
       // root.setTree(this);
    }
    
    public void expandAll(TreePath path) {
        TreeTableModel model = getProxyTreeTableModel();
        STNode node = (STNode)path.getLastPathComponent();
        if ( node.expandByDefault() ) {
            expandPath(path);
        
            int count = model.getChildCount( node );
            for(int index = 0; index < count; index++) {
                TreePath newPath = path.pathByAddingChild( model.getChild(node, index));
                expandAll(newPath);
            }
        }
    }
    
    public String getToolTipText(MouseEvent evt) {
        Point p = evt.getPoint();
        
        TreePath path = getPathForLocation((int)p.getX(), (int)p.getY());
        if ( path != null ) {
            STNode node = (STNode)path.getLastPathComponent();
            return node.getToolTipText();
        }
        return null;
    }

    public void updateNodes() {
        STNode root = (STNode)getProxyTreeTableModel().getRoot();
        if ( root != null  ) {
            root.updateNode();
        }
    }
    
}
