/*
 * PADFolderNode.java
 *
 * Created on January 14, 2003, 2:49 PM
 */

package champions.abilityTree.PADTree;

import champions.Ability;
import champions.abilityTree.*;
import champions.interfaces.AbilityList;
import treeTable.DefaultTreeTableModel;
import java.util.Iterator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author  Trevor Walker
 */
public class PADAbilityListNode extends PADTreeTableNode
implements ChangeListener {
    
    /** Holds value of property folderName. */
    private String folderName;
    
    /** Holds value of property abilityList */
    private AbilityList abilityList;
    
    /** Indicates the delete option should be shown.
     */
    private boolean deleteEnabled = false; 
    
    /** Creates a new instance of PADFolderNode */
    public PADAbilityListNode(AbilityList abilityList, String folderName) {
        setAbilityList(abilityList);
        setFolderName(folderName);
        buildChildren();
    }
    
    private void buildChildren() {
        removeAllChildren();
        
        Iterator i;
        
        for(int index = 0; index < abilityList.getSublistCount(); index++) {
            AbilityList sublist = abilityList.getSublist(index);
            PADAbilityListNode node = new PADAbilityListNode(sublist, sublist.getName());
            node.setDeleteEnabled(isDeleteEnabled());
            add(node);
        }
        
        i = abilityList.getAbilities(false);
        while ( i.hasNext() ) {
            Ability ability = (Ability)i.next();
            PADAbilityNode node = null;
            
            node = new PADAbilityNode(ability);
            node.setDeleteEnabled(isDeleteEnabled());
            
            add(node);
        }
        
        if ( model instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)model).nodeStructureChanged(this);
        }
    }
    
    /** Getter for property folderName.
     * @return Value of property folderName.
     *
     */
    public String getFolderName() {
        return this.folderName;
    }
    
    /** Setter for property folderName.
     * @param folderName New value of property folderName.
     *
     */
    public void setFolderName(String folderName) {
        setUserObject(folderName);
        this.folderName = folderName;
    }
    
    /** Getter for property abilityList.
     * @return Value of property abilityList.
     *
     */
    public AbilityList getAbilityList() {
        return abilityList;
    }
    
    /** Setter for property abilityList.
     * @param abilityList New value of property abilityList.
     *
     */
    public void setAbilityList(AbilityList abilityList) {
        if ( this.abilityList != abilityList ) {
            if ( this.abilityList != null ) {
                this.abilityList.removeChangeListener(this);
            }
            
            this.abilityList = abilityList;
            
            if ( this.abilityList != null ) {
                this.abilityList.addChangeListener(this);
            }
        }
    }
    
    public boolean getAllowsChildren() {
        return true;
    }
    
    public boolean isLeaf() { 
        return false;
    }
    
    public void stateChanged(ChangeEvent e) {
        buildChildren();
    }
    
    /** Getter for property deleteEnabled.
     * @return Value of property deleteEnabled.
     *
     */
    public boolean isDeleteEnabled() {
        return deleteEnabled;
    }
    
    /** Setter for property deleteEnabled.
     * @param deleteEnabled New value of property deleteEnabled.
     *
     */
    public void setDeleteEnabled(boolean deleteEnabled) {
        this.deleteEnabled = deleteEnabled;
    }
    
}
