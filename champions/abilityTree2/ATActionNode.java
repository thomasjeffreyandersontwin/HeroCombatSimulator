/*
 * PADAbilityNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree2;

import champions.Ability;
import tjava.Filter;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import treeTable.DefaultTreeTableModel;
import treeTable.TreeTable;
import treeTable.TreeTableCellEditor;
import treeTable.TreeTableCellRenderer;
import treeTable.TreeTableColumnModel;

/**
 *
 * @author  twalker
 * @version
 *
 * PADAbilityNode's hold references to Abilities stored in an Ability list.
 * It should not be used for powers/templates which are cached by the PADRoster
 * shared instance mechanism.
 */
public class ATActionNode extends ATNode implements PropertyChangeListener {
    
    /** Hold the Editor for the ability button. */
    protected static ATAbilityCellRenderer editor = null;
    
    /** Hold the Renderer for the ability button. */
    protected static ATAbilityCellRenderer renderer = null;
    
    /** Holds value of property ability. 
     * 
     * Under normal circumstances, this will not be null.  However, in
     * some rare instances it might be, so it should be checked before using.
     */
    private Ability ability;
    
    /** Holds the action for this node.
     */
    protected Action action;
    
    static protected Icon stoppedIcon = null;
    static protected Icon disabledIcon = null;
  
    protected static boolean actionsSetup = false;
    
    /** Creates new PADAbilityNode */
    public ATActionNode(Ability ability, Action action, ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned) {
        super(nodeFactory, nodeFilter, pruned);
        
        setAbility(ability);
        setAction(action);
        
        setupIcons();
        setupActions();
    }
    
//    public ATActionNode(Action action) {
//        setAbility(null);
//        setAction(action);
//        
//        setupIcons();
//        setupActions();
//    }
    
    protected void setupIcons() {
        if ( stoppedIcon == null ) stoppedIcon = UIManager.getIcon("AbilityButton.stoppedIcon");
        if ( disabledIcon == null ) disabledIcon = UIManager.getIcon("AbilityButton.disabledIcon");
   }
    
    protected void setupActions() {
        if ( actionsSetup == false ) {
            actionsSetup = true;
        }
    }
    
    /**
     * Returns the value to be displayed for node <code>node</code>,
     * at column number <code>column</code>.
     * @param column The column of the node for which a value should be returned.
     * @return The Value of the column.
     */
    public Object getValueAt(int column) {
        if (  action == null) return null;
        ATColumn atc = ATColumn.values()[column];
        switch(atc){
            case NAME_COLUMN:
                return action.getValue(Action.NAME);
            default:
                return null;   
        }
    }
    
    /** Returns the Ability associated with node.
     *
     * This should return whatever the node represents.
     *
     * @return null if no Ability is associated, such as in the case of a folder.
     */
    public Ability getAbility() {
        return ability;
    }    
    
    /**
     * Returns the Icon to be used when drawing this node.
     *
     * If the Icon is null, the standard open, closed, leaf icons will be used.
     * @param treeTable The treeTable which is currently displaying the node.
     * @param isSelected Whether this node is currently selected.
     * @param expanded Whether this node is currently expanded.
     * @param leaf Whether this node is currently considered a leaf.
     * @param row The row at which the node is current displayed in the TreeTable.
     * @return An Icon which should be used for this node.  Null if default icons should be used.
     */
    public Icon getIcon(TreeTable treeTable, boolean isSelected, boolean expanded, boolean leaf, int row) {
        //return (ability != null) ? ability.getPower().getIcon() : null;
//        if ( ability == null || ability.isEnabled(null) == false) {
//            return disabledIcon;
//        }
//        else if ( ability.isActivated(null) ) {
//            return runningIcon;
//        }
//        else {
//            return stoppedIcon;
//        }
        
        if ( ability != null ) {
            boolean enabled = ability.isEnabled(null);
            if ( ! enabled ) {
                return disabledIcon;
            }
            else {
                return stoppedIcon;
            }
        }
        else if ( action != null ) {
            Icon icon =  (Icon)action.getValue(Action.SMALL_ICON);
            if ( icon == null ) {
                boolean enabled = action.isEnabled();
                if ( ! enabled ) {
                    return disabledIcon; 
                }
                else {
                    return stoppedIcon;
                }
            }
        }
        
        return null;
    }
    
    /** Allows the node to provide context menu feedback.
     *
     * invokeMenu is called whenever an event occurs which would cause a context menu to be displayed.
     * If the node has context menu items to display, it should add them to the JPopupMenu <code>popup</code>
     * and return true.
     * @param treeTable Indicates the TreeTable which is currently displaying the node.
     * @param path The TreePath which the cursor was over when the context menu
     * event occurred.
     * @param popup The JPopupMenu which is being built.  Always non-null and initialized.
     * @return True if menus where added, False if menus weren't.
     */
//    public boolean invokeMenu(TreeTable treeTable, TreePath path, JPopupMenu popup) {
//        boolean rv = false;
//        
//        return rv;
//    }
    
    /** Gets the nodes preferred CellEditor for the column <code>columnIndex</code>.
     *
     * Returns the Nodes preferred CellEditor for the indicated column.  Null can be returned to
     * indicate that a default editor can be used.
     *
     * @param columnIndex The Index of the column being rendered.
     * @return The TreeTableCellEditor to use editing the indicated column of this node.
     */
    public TreeTableCellEditor getTreeTableCellEditor(int columnIndex) {
        
        if ( columnIndex == ATColumn.NAME_COLUMN.ordinal() ) {
            if ( editor == null ) editor = new ATAbilityCellRenderer();
            
            return editor;
        }
        return null;
    }
    
    /** Gets the nodes preferred CellRenderer for the column <code>columnIndex</code>.
     *
     * Returns the Nodes preferred CellRenderer for the indicated column.  Null can be returned to
     * indicate that a default renderer can be used.
     *
     * @param columnIndex The Index of the column being rendered.
     * @return The TreeTableCellRenderer to use rendering the indicated column of this node.
     */
    public TreeTableCellRenderer getTreeTableCellRenderer(int columnIndex) {
        if (  columnIndex != ATColumn.NAME_COLUMN.ordinal() ) {
            if ( renderer == null ) renderer = new ATAbilityCellRenderer();
            return renderer;
        }
        return null;
    }
    
    public boolean isCellEditable(int columnIndex) {
        // This is editable in order to provide the HTML type button in the tree...
        return columnIndex == ATColumn.NAME_COLUMN.ordinal();
    }
    
    /** Setter for property ability.
     * @param ability New value of property ability.
     *
     */
    public void setAbility(Ability ability) {
        if ( this.ability != ability ) {
            if ( this.ability != null ) {
            }
            
            this.ability = ability;
            
            if ( this.ability != null ) {
            }
        }
        
    }
    
//    protected void buildChildren() {
//        boolean changed = false;
//        
//        if ( getChildCount() > 0 ) {
//            removeAndDestroyAllChildren();
//            changed = true;
//        }
//        
//        if ( addActions() ) {
//            changed = true;
//        }
//        
//        if ( changed && model instanceof DefaultTreeTableModel ) {
//            ((DefaultTreeTableModel)model).nodeStructureChanged(this);
//        }
//    }
    

    
    protected void updateName() {

        if ( model instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)model).nodeChanged(this);
        }
       //Icon icon = p.getIcon();
       // setIcon(icon);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        updateName();
    }
    
 
    /** Tells the Node that it should trigger it's default action. 
     *
     */
    public void triggerDefaultAction(MouseEvent mouseEvent) {
        if ( action != null )  {
           action.actionPerformed( new ActionEvent(this, 0, "TRIGGER_ACTION"));
           if ( getParent() != null ) {
               SwingUtilities.invokeLater( new Runnable() {
                   public void run() {
                       if ( getParent() != null ) {
                        ((ATNode)getParent()).updateTree();
                       }
                   }
               });
           }
        }
    }
    
    public boolean isEnabled() {
        return (action != null && action.isEnabled());
    }
    
    /**
     * Getter for property action.
     * @return Value of property action.
     */
    public Action getAction() {
        return action;
    }
    
    /**
     * Setter for property action.
     * @param action New value of property action.
     */
    public void setAction(Action action) {
        if ( this.action != action ) {
            this.action = action;
            updateName();
        }
    }
    
    public int getColumnSpan(int column, TreeTableColumnModel columnModel) {
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            return ATColumn.MAX_COLUMNS.ordinal();
            
        }
        else {
            return 1;
        }
    }
    
    /** Tells the renderer/editor what color the node thinks is appropriate for the column.
     *
     */
    public Color getColumnColor(int column) {
        Color c = null;
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            c = new Color(0,0,238);
        }
        return c;
    }

    public String getToolTipText(int column) {
        return (String)action.getValue(Action.NAME);
    }
    

    
    /** Returns the Tool text for the node. */
//    public String getToolTipText() {
//        if ( ability == null ) return null;
//        if ( ability.getEnableMessage() != null ) return ability.getEnableMessage();
//        return ability.getHTMLDescription();
//    }
    
    public void destroy() {
        super.destroy();
        
        setAbility(null);
        setAction(null);
    }
    
}
