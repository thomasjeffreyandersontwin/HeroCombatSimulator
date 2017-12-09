/*
 * TargetTree.java
 *
 * Created on February 27, 2002, 11:16 PM
 */

package champions.abilityTree2;

import champions.interfaces.AbilityList;
import champions.interfaces.BattleListener;
import java.awt.Font;
import javax.swing.UIManager;
import treeTable.TreeTableHeader;
import treeTable.TreeTableModel;

/**
 *
 * @author  twalker
 * @version
 */
public class ATAbilityListTree extends ATTree implements BattleListener /*, MouseListener, MouseMotionListener */ {
    
    /** Holds value of property target. */ 
    private AbilityList abilityList;
    
    private boolean includeTargetActions = true;
    
    /** Creates new TargetTree */
    public ATAbilityListTree(String title) {
        super(title);
        setDefaultTreeTableCellRenderer( new ATAbilityCellRenderer() );
        setDefaultTreeTableCellEditor( new ATAbilityCellRenderer() );
        
        
    }
    
    public ATAbilityListTree() {
        this("Abilities");
    }
    
    public void setTreeTableHeader(TreeTableHeader header) {
        super.setTreeTableHeader(header);
        Font f = UIManager.getFont("AbilityTree.headerFont");
        if ( treeTableHeader != null && f != null ) {
            treeTableHeader.setFont(f);
        }
    }
    
    protected void setupModel() {
            ATNode root = new ATAbilityListNodeFactory().createAbilityListNode(abilityList, false, getNodeFilter(), false);
            setPopupFilter(null);
            ATModel model = new ATModel(root, getTitle());
            this.setTreeTableModel(model);
            root.setTree(this);
    }
    
    public void setRoot(ATNode root) {
        ATModel model = new ATModel(root, getTitle());
        setPopupFilter(null);
        model.setIncludeTargetActions(includeTargetActions);
        this.setTreeTableModel(model);
    }

    /** Getter for property target.
     * @return Value of property target.
     */
    public AbilityList getAbilityList() {
        return abilityList;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setAbilityList(AbilityList abilityList) {
        this.abilityList = abilityList;
        
        Object root = getBaseTreeTableModel().getRoot();
        if ( root instanceof ATAbilityListNode ) {
            ((ATAbilityListNode)root).setAbilityList(abilityList);
        }
    }
    
    public boolean getIncludeTargetActions() {
        return includeTargetActions;
    }

    public void setIncludeTargetActions(boolean includeTargetActions) {
        if ( this.includeTargetActions != includeTargetActions ) { 
            this.includeTargetActions = includeTargetActions;
            
            TreeTableModel model = getBaseTreeTableModel();
            if ( model instanceof ATModel ) {
                ((ATModel)model).setIncludeTargetActions(includeTargetActions);
            }
        }
    }



    



}
