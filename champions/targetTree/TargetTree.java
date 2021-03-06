/*
 * TargetTree.java
 *
 * Created on February 27, 2002, 11:16 PM
 */

package champions.targetTree;

import champions.TargetList;
import champions.filters.NameTargetFilter;
import champions.interfaces.ChampionsConstants;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import VirtualDesktop.Legacy.VirtualDesktopNodeListener;
import treeTable.DefaultTreeTable;
import treeTable.DefaultTreeTableModel;
import treeTable.FilterableTreeTableModel;
import treeTable.TreeTableFilterEvent;
import treeTable.TreeTableModel;



/**
 *
 * @author  twalker
 * @version
 */
public class TargetTree extends DefaultTreeTable
        implements ChampionsConstants {
    
    /** Holds value of property target. */
    private TargetList targetList;
    
    /** Holds the name you want for the title. */
    private String title;
    
    /** Creates new TargetTree */
    public TargetTree(String title) {
        this.title = title;
        
        
        setLargeModel(true);
        setupModel();
        setupMouseListener();
        
        setHighlightEnabled(true);
        
        setRootVisible(false);

        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        this.addTreeSelectionListener(new VirtualDesktopNodeListener());
        
        setupUI();
        
    }
    
    public TargetTree(String title, DefaultTreeTableModel treeModel) {
        this.title = title;
        
        
        setLargeModel(true);
        setupMouseListener();
        
        setHighlightEnabled(true);
        
        setRootVisible(false);
        
        setupUI();
        
        setTreeTableModel(treeModel);
    }
    
    public TargetTree() {
        this("Targets");
    }
    
    public void updateUI() {
        super.updateUI();
        
        setupUI();
    }
    
    private final void setupUI() {
//        Color c;
//        c = UIManager.getColor( "AbilityEditor.foreground" );
//        if ( c != null ) this.setForeground(c);
//        c = UIManager.getColor( "AbilityEditor.background" );
//        if ( c != null ) this.setBackground(c);
    }
    
    protected void setupModel() {
        TTNode root = new TTTargetListNode(null, targetList);
        TTModel model = new TTModel(root, title);
        
//        if ( getProxyTreeTableModel() != null ) {
//            getProxyTreeTableModel().removeTreeModelListener(this);
//        }
        
        
        setTreeTableModel(model);
        
//        if ( getProxyTreeTableModel() != null ) {
//            getProxyTreeTableModel().addTreeModelListener(this);
//        }
//        expandAll(new TreePath(root));
    }
    
    @Override
    public void setTreeTableModel(TreeTableModel treeTableModel) {
        TreeTableModel oldModel = getBaseTreeTableModel();
        if ( oldModel != treeTableModel ) {

            super.setTreeTableModel(treeTableModel);

            if ( oldModel != null ) {
                if ( oldModel instanceof TTModel ) {
                    ((TTModel)oldModel).destroy();
                }
            }
            
            if( treeTableModel != null ) {
                
            }
        }
    }
    
    
    
    protected void setupMouseListener() {
  /*      addMouseListener( new MouseAdapter() {
   
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && getAbilityTreeTable() != null && getTarget() != null ) {
                    //int index = PADListListener.this.list.locationToIndex(e.getPoint());
                    TreePath clickPath = TargetTree.this.getSelectionPath();
                    if (clickPath != null && clickPath.getLastPathComponent() instanceof TTNode ) {
                        ((TTNode)clickPath.getLastPathComponent()).handleDoubleClick(abilityPath);
                    }
                }
            }
        }); */
    }
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public TargetList getTargetList() {
        return targetList;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTargetList(TargetList targetList) {
        this.targetList = targetList;
        
    }
    
    /** Getter for property title.
     * @return Value of property title.
     *
     */
    public java.lang.String getTitle() {
        return title;
    }
    
    /** Setter for property title.
     * @param title New value of property title.
     *
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
    }
    
    
    public void filter(TreeTableFilterEvent event) {
         super.filter(event);

         expandAll( new TreePath( getModel().getRoot() ));
    }
    
    public void expandAll(TreePath path) {
        TreeTableModel model = getProxyTreeTableModel();
        Object node = path.getLastPathComponent();
        if ( node instanceof TTNode == false || ((TTNode)node).isExpandedByDefault() ) {
            if ( isExpanded(path) == false ) expandPath(path);
            
            int count = model.getChildCount( node );
            for(int index = 0; index < count; index++) {
                TreePath newPath = path.pathByAddingChild( model.getChild(node, index));
                expandAll(newPath);
            }
        }
    }
    
}
