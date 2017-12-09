/*
 * TargetTree.java
 *
 * Created on February 27, 2002, 11:16 PM
 */

package champions.ioAdapter.heroDesigner.errorTree;

import champions.interfaces.ChampionsConstants;
import treeTable.*;
import champions.*;
import javax.swing.tree.TreePath;
import tjava.Destroyable;
import xml.XMLParseErrorList;

/**
 *
 * @author  twalker
 * @version
 */
public class ErrorTree extends DefaultTreeTable 
implements ChampionsConstants {
    

    /** Holds the name you want for the title. */
    private String title;
    
    /** Creates new TargetTree */
    public ErrorTree(String title) {
        this.title = title;
        
        setupEmptyModel();
        
        setHighlightEnabled(false);
        
        this.setRootVisible(false);
    }
    
    public ErrorTree(String title, DefaultTreeTableModel treeModel) {
        this.title = title;
        
        
        setHighlightEnabled(true);
        
        this.setRootVisible(false);
        
        setTreeTableModel(treeModel);
    }
    
    public ErrorTree() {
        this("Hero Design Import Errors");
    }
    
    
    
    protected void setupEmptyModel() {
        ETNode root = new ETNode();
        ETModel model = new ETModel(root, title);
        
        setTreeTableModel(model);
    }

    @Override
    public void setTreeTableModel(TreeTableModel treeTableModel) {
        TreeTableModel oldModel = getBaseTreeTableModel();
        if ( oldModel != treeTableModel ) {
            super.setTreeTableModel(treeTableModel);
            
           if ( oldModel != null ) {
                if ( oldModel instanceof Destroyable ) {
                    ((Destroyable)oldModel).destroy();
                }
            }
        }
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
    
    public void addErrorEntry(Target target, XMLParseErrorList errorList, String fileName) {
        ETModel model = (ETModel)getBaseTreeTableModel();
        ETTargetNode node = new ETTargetNode(target, errorList);
        node.setFileName(fileName);
        model.getRoot().add(node);
        model.nodeStructureChanged(model.getRoot());
        expandAll( new TreePath( model.getRoot() ) );
    }
    
   public void clearErrors() {
       ETModel model = (ETModel)getBaseTreeTableModel();
       model.getRoot().removeAndDestroyAllChildren();
       model.nodeStructureChanged(model.getRoot());
   }
   
   
    
    

//    public void treeNodesChanged(javax.swing.event.TreeModelEvent treeModelEvent) {
//    }
//
//    public void treeNodesInserted(javax.swing.event.TreeModelEvent treeModelEvent) {
//    }
//
//    public void treeNodesRemoved(javax.swing.event.TreeModelEvent treeModelEvent) {
//    }
//
//    public void treeStructureChanged(javax.swing.event.TreeModelEvent treeModelEvent) {
//        expandAll(treeModelEvent.getTreePath());
//    }
    
}
