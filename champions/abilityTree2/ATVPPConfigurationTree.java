/*
 * ATSingleTargetTree.java
 *
 * Created on June 8, 2004, 10:34 PM
 */

package champions.abilityTree2;

import champions.Target;
import champions.interfaces.Framework;
import treeTable.DefaultTreeTableCellEditor;
import treeTable.DefaultTreeTableCellRenderer;
/**
 *
 * @author  1425
 */
public class ATVPPConfigurationTree extends ATTree {

    /**
     * Creates a new instance of ATSingleTargetTree
     */
    public ATVPPConfigurationTree() {
        setDefaultTreeTableCellRenderer( new DefaultTreeTableCellRenderer());
        setDefaultTreeTableCellEditor(new DefaultTreeTableCellEditor());
    }
    
    protected void setupModel() {
        ATNode root = new ATVPPConfigurationRoot(null, null, null);
        ATModel model = new ATVPPConfigurationModel(root, "Variable Point Pool Configuration");
        
        setTreeTableModel(model);
        root.setTree(this);
    }
    
    public void setup(Framework framework, Target target ) {
        ATVPPConfigurationRoot r = (ATVPPConfigurationRoot)getBaseTreeTableModel().getRoot();
        r.setFramework(framework);
        r.setTarget(target);
        r.buildNode();
    }
    
    public void applyActions() {
        if ( getBaseTreeTableModel() instanceof ATVPPConfigurationModel ) {
            ((ATVPPConfigurationModel)getBaseTreeTableModel()).applyActions();
        }
    }
    
    public void cancelActions() {
        if ( getProxyTreeTableModel() instanceof ATVPPConfigurationModel ) {
            ((ATVPPConfigurationModel)getBaseTreeTableModel()).cancelActions();
        }
    }
}
