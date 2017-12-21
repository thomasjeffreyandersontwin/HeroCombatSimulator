/*
 * ATAbilityTree.java
 *
 * Created on November 12, 2007, 10:00 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.abilityTree2;

import champions.Battle;
import champions.BattleChangeEvent;
import champions.BattleChangeType;
import champions.Roster;
import champions.Target;
import champions.event.SegmentAdvancedEvent;
import champions.event.SequenceChangedEvent;
import champions.event.TargetSelectedEvent;
import champions.filters.AndFilter;
import champions.filters.NameAbilityFilter;
import champions.interfaces.BattleListener;
import tjava.Filter;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ToolTipManager;
import javax.swing.UIManager; 
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import VirtualDesktop.Legacy.VirtualDesktopNodeListener;
import tjava.Destroyable;
import treeTable.DefaultTreeTable;
import treeTable.TreeTableColumnModel;
import treeTable.TreeTableFilterEvent;
import treeTable.TreeTableHeader;
import treeTable.TreeTableModel;
import treeTable.TreeTableModelAdapter;

/**
 *
 * @author twalker
 */
public class ATTree extends DefaultTreeTable implements BattleListener, TreeModelListener, TreeWillExpandListener, TreeExpansionListener  {
    
    
    /** Holds the name you want for the title. */
    private String title;
    
    private boolean updateBlockedWhileProcessing = false;
    
    /** Holds the AbilityFilter property.
     */
    private Filter<Object> nodeFilter;

    private Filter<Object> popupFilter;
    
    private boolean popupFilterOverridesNodeFilter = false;
    
    public ATTree() {
        this("Ability Tree");
    }
    /** Creates a new instance of ATAbilityTree */
    public ATTree(String title) {
        super(false);
        
        this.title = title;
        
        setLargeModel(true);
        
                
        addMouseListener( getMouseListener() );
        addTreeWillExpandListener( this );
        addTreeExpansionListener( this );
        
        setupModel();
        
        TreeTableHeader tth = getTreeTableHeader();
        tth.getColumnModel().getColumn(0).setMinWidth(150);
        
        setRootVisible(false);
        
        ToolTipManager.sharedInstance().registerComponent(this);
        
        setShowsRootHandles(false);
        
        Battle.addBattleListener(this);
        
        setCellRenderer( new ATTreeTableRendererAdapter() );
       // setCellEditor( new ATTreeTableEditoradapter() );


        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.addTreeSelectionListener(new VirtualDesktopNodeListener());
        setupUI();
    }

    
    protected void setupUI() {
        Color c;
        c = UIManager.getColor( "AbilityTree.foreground" );
        if ( c != null ) this.setForeground(c);
        c = UIManager.getColor( "AbilityTree.background" );
        if ( c != null ) this.setBackground(c);
        
        Font f = UIManager.getFont("AbilityTree.headerFont");
        if ( treeTableHeader != null && f != null ) {
            treeTableHeader.setFont(f);
        }
        
        Font f2 = UIManager.getFont("AbilityTree.defaultFont");
        if ( f2 != null ) {
            setFont(f2);
        }
        
        setCellRenderer( Integer.class, new ATIntegerTreeTableCellRenderer() );
    }

    
    @Override
    public void updateUI() {
    	super.updateUI();
        
        setupUI();
    }
    
    public void setRoot(ATNode root) {
        TreeTableModel model = new ATModel(root, title);
        this.setTreeTableModel(model);
    }
    public static boolean expandAll=false;
    @Override
    public void expandAll(TreePath path) {
        TreeTableModel model = getProxyTreeTableModel();
        Object node = path.getLastPathComponent();
        boolean expanded = isExpanded(path);
        if ( (node instanceof ATNode && ((ATNode)node).isExpandedByDefault()) ) {
            
            int count = model.getChildCount( node );
            for(int index = 0; index < count; index++) {
                TreePath newPath = path.pathByAddingChild( model.getChild(node, index));
                
                
                expandAll(newPath);
                
            }
            
            if ( expanded == false ) {
            	expandPath(path);
            }
            
        }
        
        //Jeff Anderson Change

        ATNode node2 = (ATNode)node;       
        ATTargetNode root = (ATTargetNode)node2.getRoot();
        
      try {
        for(int i=0;i< root.getChildCount(); i++) {
        	String name="";
        	if ( root.getChildAt(i) instanceof  ATAbilityListNode) {
        		ATAbilityListNode abilityChategory = (ATAbilityListNode) root.getChildAt(i);
        		abilityChategory.setExpandedByDefault(true);
        		if(abilityChategory.getAbilityList().getName()==null) {
        		
        			for(int j=0;j< abilityChategory.getChildCount(); j++) {
        				if(abilityChategory.getChildAt(j) instanceof  ATAbilityListNode)
        				{
        					ATAbilityListNode powerSkillDisad = (ATAbilityListNode) abilityChategory.getChildAt(j);
        					if(powerSkillDisad.getAbilityList().getName().equals("Non Core")|| powerSkillDisad.getAbilityList().getName().equals("Skills") || powerSkillDisad.getAbilityList().getName().equals("Disadvantages"))
        					{
        						powerSkillDisad.setExpandedByDefault(false);
        					}	
        					else {
        						powerSkillDisad.setExpandedByDefault(true);
        					}
        				}
        			}
        		}
        	}
        	
        }
      }
      catch(Exception e) {
    	 e.printStackTrace();
      }
      
        	
       	
    }
    
    /** Expends the children of the specified path.
     *
     * This does not expand the node recursively.  Only the immediate
     * children will be expanded.  However, this can be used with a 
     * tree listener to recursively expand nodes.
     */
    public void expandChildren(TreePath path) {
        TreeTableModel model = getProxyTreeTableModel();
        Object node = path.getLastPathComponent();
        boolean expanded = isExpanded(path);
            
        int count = model.getChildCount( node );
        for(int index = 0; index < count; index++) {
            Object child =  model.getChild(node, index);
            TreePath newPath = path.pathByAddingChild(child);
            if ( (child instanceof ATNode && ((ATNode)child).isExpandedByDefault()) ) {
            	try{
                expandPath(newPath);
            	}
            	catch(Exception e)
            	{}
            }   
        }
    }
    
    public String getToolTipText(MouseEvent evt) {
        Point p = evt.getPoint();
        
        TreePath path = getPathForLocation((int)p.getX(), (int)p.getY());
        if ( path != null ) {
            Object lpc = path.getLastPathComponent();
            if ( lpc instanceof ATNode) {
                ATNode node = (ATNode)lpc;
                int column = getColumnForLocation(p);
                return node.getToolTipText(column);
            }
        }
        return null;
    }
    
    public void updateNodes() {
        Object root = getProxyTreeTableModel().getRoot();
        if ( root instanceof ATNode  ) {
            ((ATNode)root).updateTree();
        }
    }

    public void setVisible(boolean visible) {
        boolean wasVisible = isVisible();
        
        super.setVisible(visible);
        
        if ( visible && ! wasVisible ) {
            TreeTableModel model = getBaseTreeTableModel();
            if ( model instanceof ATModel  ) {
                ((ATModel)model).rebuildTree(false);
            }
        }
    }
    
    @Override
    public void setTreeTableModel(TreeTableModel baseTreeTableModel) {
        TreeTableModel oldBaseModel = getBaseTreeTableModel();
        if ( oldBaseModel != baseTreeTableModel ) {

            if ( this.proxyTreeTableModel != null ) {
                this.proxyTreeTableModel.removeTreeModelListener( this );
            }

            this.baseTreeTableModel = baseTreeTableModel;
            this.proxyTreeTableModel = wrapModelWithProxies(baseTreeTableModel);

            // Setup this listener first, since setTreeTableModel
            // always adds a listener...but listeners are fired backward
            // so we want to be the first in the list so we are the last
            // called for an update...
             if( this.proxyTreeTableModel != null ) {
                this.proxyTreeTableModel.addTreeModelListener( this );
            }

            setModel(new TreeTableModelAdapter(this, proxyTreeTableModel));

            TreeTableColumnModel newColumnModel = proxyTreeTableModel.getColumnModel();

            setColumnModel(newColumnModel);
            
            buildFilter();
            
            if ( oldBaseModel != null ) {
                if ( oldBaseModel instanceof Destroyable ) {
                    ((Destroyable)oldBaseModel).destroy();
                }
            }
        }
    }

    
    

    
    /** Returns whether the tree is currently updatable. 
     *
     *  If the tree is not updatable, nodes of the tree should skip updates
     *  until the tree become updatable.
     *
     */
    public boolean isUpdatable() {
        if ( updateBlockedWhileProcessing && Battle.getCurrentBattle().isProcessing() ) return false;
        return isShowing();
    }

    public boolean isUpdateBlockedWhileProcessing() {
        return updateBlockedWhileProcessing;
    }

    public void setUpdateBlockedWhileProcessing(boolean updateBlockedWhileProcessing) {
        this.updateBlockedWhileProcessing = updateBlockedWhileProcessing;
    }

    public void battleTargetSelected(TargetSelectedEvent event) {
        scrollRowToVisible(0);
    }

    public void battleSegmentAdvanced(SegmentAdvancedEvent event) {
        
    }

    public void battleSequenceChanged(SequenceChangedEvent event) {
    }

    public void stateChanged(BattleChangeEvent e) {
//        if (Battle.getCurrentBattle().isProcessing() == false){
//            updateNodes();
//        }
    }

    public void eventNotification(ChangeEvent event) {
    }

    public void combatStateChange(ChangeEvent event) {
    }

    public void processingChange(BattleChangeEvent event) {

        if ( event.getType() == BattleChangeType.FINISHED_PROCESSING_EVENTS ) {
            if ( isUpdateBlockedWhileProcessing() ) {
                if ( Battle.getCurrentBattle().isProcessing() == false) {
                    TreeTableModel model = getBaseTreeTableModel();
                    if ( model instanceof ATModel  ) {
                        boolean rebuildOccurred = ((ATModel)model).rebuildTree(true);
                        ATTree.expandAll=true;
                        expandAll( new TreePath(model.getRoot()));
                    }
                }

                if ( Battle.getCurrentBattle().isProcessing() ) {
                    setEnabled(false);
                }
                else {
                    setEnabled(true);
                }
            }
            else if (Battle.getCurrentBattle().isProcessing() == false){
                updateNodes();
            }
        }
    }

    
    public void setFont(Font font) {
//        if ( font != null ) {
//            int height = getFontMetrics(font).getHeight();
//            
//            setRowHeight( Math.max(height, 16) );
//        }
        
        super.setFont(font);
    }

    protected void setupModel() {
        ATNode root = new ATNode("ATTree Root", new ATNodeFactory(), null, false);
        setRoot(root);
    }


    
    protected void buildFilter() {
        
        Filter<Object> filter = null;
        
        if (popupFilter != null) {
            if (popupFilterOverridesNodeFilter == false && nodeFilter != null) {
                    filter = new AndFilter<Object>(nodeFilter, popupFilter);
            }
            else {
                filter = popupFilter;
            }
        }
        else if ( nodeFilter != null ) {
            filter = nodeFilter;
        }
        
        Object root = getProxyTreeTableModel().getRoot();
        if (root instanceof ATNode) {
            ((ATNode) root).setNodeFilter( filter );

            //expandAll( new TreePath(root));
            TreePath tp = new TreePath(root);
            expandPath( tp );
            expandChildren( tp );
        }
    }

    
    public void filter(TreeTableFilterEvent event) {
        Object f = event.getFilterObject();
        
        if (f instanceof String) {
            if (((String) f).equals("")  || ((String) f).equals(" ") ) {
                setPopupFilter(null);
            }
            else {
                setPopupFilter( new ATAbilityFilter( new NameAbilityFilter((String) f) ));
            }
        }
        else {
            setPopupFilter( null ) ;
        }
    }

    
    /** Getter for property nodeFilter.
     * @return Value of property nodeFilter.
     *
     */
    public Filter<Object> getNodeFilter() {
        return nodeFilter;
    }
    
    
    //    public void mouseClicked(MouseEvent e) {
    //    }
    //
    //    public void mousePressed(MouseEvent e) {
    //    }
    //
    //    public void mouseReleased(MouseEvent e) {
    //    }
    //
    //    public void mouseEntered(MouseEvent e) {
    //    }
    //
    //    public void mouseExited(MouseEvent e) {
    //    }
    //
    //    public void mouseDragged(MouseEvent e) {
    //    }
    //
    //    public void mouseMoved(MouseEvent e) {
    //        Point p = e.getPoint();
    //        
    //        TreePath path = getPathForLocation((int)p.getX(), (int)p.getY());
    //        if ( path != null ) {
    //            ATNode node = (ATNode)path.getLastPathComponent();
    //            int column = getColumnForLocation(p);
    //            int row = getRowForPath(path);
    //            
    //            showOverlay(node, row, column, p);
    //        }
    //    }
    //    
    //    protected void showOverlay(ATNode node, int row, int column, Point location) {
    //        TreeTableCellRenderer renderer = getProxyTreeTableModel().getCellRenderer(node,column);
    //        
    //        Component c = renderer.getTreeTableCellRendererComponent(this, node, false, false, false, row, column, false);
    //        
    //        Dimension d = c.getPreferredSize();
    //        
    //        BufferedImage bi = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
    //        
    //        Graphics2D g2 = null;
    //        try {
    //            g2 = (Graphics2D)bi.getGraphics();
    //            c.paint(g2);
    //        }
    //        finally {
    //            if ( g2 != null ) {
    //                g2.dispose();
    //            }
    //        }
    //        
    //        ImageIcon icon = new ImageIcon(bi);
    //        
    //        
    //    }
    //    
    //    protected void clearOverlay() {
    //        
    //    }
    
        public Filter<Object> getPopupFilter() {
        return popupFilter;
    }

    
    /** Setter for property nodeFilter.
     * @param nodeFilter New value of property nodeFilter.
     *
     */
    public void setNodeFilter(Filter<Object> nodeFilter) {
        if ( this.nodeFilter != nodeFilter ) { 
            this.nodeFilter = nodeFilter;
            
            buildFilter();
        }
    }


    public void setPopupFilter(Filter<Object> popupFilter) {
        if ( this.popupFilter != popupFilter ) {
            this.popupFilter = popupFilter;
            
            buildFilter();
        }
    }

    
    public void updateFilter() {
        Object root = getProxyTreeTableModel().getRoot();
        if (root instanceof ATTargetNode) {
            ((ATTargetNode) root).buildNode();
            
            expandAll( new TreePath(root));
        }
    }
    
    private MouseListener getMouseListener() {
        return new MouseListener() {
            transient private Object lastNode = null;
            
            public void mouseClicked(MouseEvent e) {
                if ( e.getClickCount() == 1 ) {
                    TreePath tp = getPathForLocation(e.getPoint().x, e.getPoint().y);
                    if ( tp != null  ) {
                        lastNode = tp.getLastPathComponent();
                    }
                }
                else if ( e.getClickCount() == 2 ) {
                    TreePath tp = getPathForLocation(e.getPoint().x, e.getPoint().y);
                    if ( tp != null && tp.getLastPathComponent() == lastNode && tp.getLastPathComponent() instanceof ATNode ) {
                        ((ATNode)tp.getLastPathComponent()).handleDoubleClick(ATTree.this,tp);
                    }
                }
            }
            public void mouseEntered(MouseEvent e) {
                
            }
            public void mouseExited(MouseEvent e) {
                
            }
            public void mousePressed(MouseEvent e) {
            }
            public void mouseReleased(MouseEvent e) {
            }
        };
    }

    public void treeNodesChanged(TreeModelEvent e) {
        //expandAll(e.getTreePath() );
    }

    public void treeNodesInserted(TreeModelEvent e) {
    }

    public void treeNodesRemoved(TreeModelEvent e) {
    }

    public void treeStructureChanged(TreeModelEvent e) {
        TreePath tp = e.getTreePath();
        if ( tp != null && isVisible( tp )) {
            expandAll( tp );
        }
    }

    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        // Tell the tree table model that a node is expanding...
        if ( baseTreeTableModel instanceof ATModel ) {
            ((ATModel)baseTreeTableModel).nodeWillExpand(event.getPath());
        }
      //  ATTargetActionsNode tNode =(ATTargetActionsNode) event.getPath().getLastPathComponent();
    //	Target t = tNode.getTarget();
    //	VirtualDesktop.MessageExporter.exportEvent("CollapseTargetNode", t);
    }

    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
    	
    }

    public void treeExpanded(TreeExpansionEvent event) {
    	ATNode tNode =(ATNode) event.getPath().getLastPathComponent();
    	
    	ATRosterNode rnode;
    	Roster r=null;
    	try{
    		if(event.getPath().getLastPathComponent().getClass()== ATRosterNode.class) {
    			rnode =(ATRosterNode) event.getPath().getLastPathComponent();
    			r= rnode.getRoster();
    		}
    		
    	}catch(Exception e){}
    	
    	if(r!=null){
    		VirtualDesktop.Legacy.MessageExporter.exportEvent("Expand Roster Node", null,r);
    	}
    	else{
    		//if(ATTree.expandAll==false){
    		try {
    			Target t = tNode.getTarget();
    			if(t!=null) {
    				r= t.getRoster();
    				VirtualDesktop.Legacy.MessageExporter.exportEvent("Expand Character Node", t,r);
    			}
    		}
    		catch(Exception e) {}
//    		}
    	}
    	
    }

    public void treeCollapsed(TreeExpansionEvent event) {
    	ATNode tNode =(ATNode) event.getPath().getLastPathComponent();
 
    	ATRosterNode rnode;
    	Roster r=null;
    	try{
    		rnode =(ATRosterNode) event.getPath().getLastPathComponent();
    		r= rnode.getRoster();
    	}
    	catch(Exception e){}
    	
    	if(r!=null){
    		VirtualDesktop.Legacy.MessageExporter.exportEvent("Collapse Roster Node", null,r);
    	}
    	else{
    		Target t = tNode.getTarget();
    		if(t!=null) {
    			r= t.getRoster();
    			VirtualDesktop.Legacy.MessageExporter.exportEvent("Collapse Character Node", t,r);
    		}
    	}
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPopupFilterOverridesNodeFilter() {
        return popupFilterOverridesNodeFilter;
    }

    public void setPopupFilterOverridesNodeFilter(boolean popupFilterOverridesNodeFilter) {
        if ( this.popupFilterOverridesNodeFilter != popupFilterOverridesNodeFilter) {
            this.popupFilterOverridesNodeFilter = popupFilterOverridesNodeFilter;
            buildFilter();
        }
    }

    
    
}
