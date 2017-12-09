/*
 * SpecialParameterNode.java
 *
 * Created on June 11, 2001, 7:43 PM
 */

package champions.abilityTree;

import champions.Ability;
import champions.parameters.ParameterList;
import champions.event.PADValueEvent;
import champions.interfaces.PADValueListener;
import champions.interfaces.SpecialParameter;
import champions.parameters.Parameter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import tjava.SharedPopupAction;

/** A Node which composes the AbilityTreeTable Node Structure.
 *
 * The SpecialParameterNode is used to represent and edit SpecialParameters/Limitations belonging to an ability.
 *
 * One SpecialParameterNode is created for each SpecialParameter/Limitation.
 *
 * @author  twalker
 * @version
 */
public class SpecialParameterNode extends AbilityTreeNode 
implements PADValueListener {
    
    /** Holds value of property specialParameter. */
    private SpecialParameter specialParameter;
    
    /** Holds value of property ability. */
    private Ability ability;
    
    /** Holds value of property index. */
    private int specialParameterIndex = -1;
    
    /** Holds value of property parameterList. */
    private ParameterList parameterList;
    
    
    static private SpecialParameterNode.RemoveSpecialParameterAction removeSpecialParameterAction;
    static private JMenuItem removeMenuItem;
    
    /** Creates new SpecialParameterNode */
    public SpecialParameterNode(AbilityTreeTableModel model, Ability ability, MutableTreeNode parent, SpecialParameter specialParameter, int index) {
        setModel(model);
        setAbility(ability);
        setParent(parent);
        setSpecialParameter(specialParameter);
        setIndex(index);
        
        updateChildren();
        
        setupActions();
    }
    
    
    private void setupActions() {
      if ( removeSpecialParameterAction == null ) removeSpecialParameterAction = new SpecialParameterNode.RemoveSpecialParameterAction();   
      if ( removeMenuItem == null ) removeMenuItem = new JMenuItem();
    }
     
    /** Getter for property specialParameter.
     * @return Value of property specialParameter.
     */
    public SpecialParameter getSpecialParameter() {
        return specialParameter;
    }
    
    /** Setter for property specialParameter.
     * @param specialParameter New value of property specialParameter.
     */
    public void setSpecialParameter(SpecialParameter specialParameter) {
        this.specialParameter = specialParameter;
        
        if ( specialParameter != null ) {
            setUserObject( specialParameter.getName() );
        }
        
        if ( specialParameter != null && ability != null && specialParameterIndex != -1 ) {
            setParameterList( specialParameter.getParameterList(ability, specialParameterIndex) );
        }
    }
    
    /** Getter for property ability.
     * @return Value of property ability.
     */
    public Ability getAbility() {
        return ability;
    }
    
    /** Setter for property ability.
     * @param ability New value of property ability.
     */
    public void setAbility(Ability ability) {
        this.ability = ability;
        
        if ( specialParameter != null && ability != null &&  specialParameterIndex != -1 ) {
            setParameterList( specialParameter.getParameterList(ability, specialParameterIndex) );
        }
    }
    
    /** Getter for property index.
     * @return Value of property index.
     */
    public int getIndex() {
        return specialParameterIndex;
    }
    
    /** Setter for property index.
     * @param index New value of property index.
     */
    public void setIndex(int specialParameterIndex) {
        this.specialParameterIndex = specialParameterIndex;
        
        if ( specialParameter != null && ability != null &&  specialParameterIndex != -1 ) {
            setParameterList( specialParameter.getParameterList(ability, specialParameterIndex) );
        }
    }
    
    public Icon getIcon(JTree tree,boolean selected,boolean expanded,boolean leaf,int row,boolean hasFocus) {
        return (specialParameter != null) ? specialParameter.getIcon() : null;
    }
    
    public String toString() {
        if ( specialParameter != null && ability != null &&  specialParameterIndex != -1 ) {
            return ability.getIndexedStringValue(specialParameterIndex, "SpecialParameter","DESCRIPTION");
        }
        
        return "";
    }
    
    public TreeCellEditor getTreeCellEditor(JTree tree) {
        return null;
    }
    
    public TreeCellRenderer getTreeCellRenderer(JTree tree) {
        return null;
    }
    
    public int getNodeStatus() {
        return OKAY_STATUS;
    }
    
    public void PADValueChanged(PADValueEvent evt) {
        if ( specialParameter != null && ability != null && parameterList != null ) {
            ability.reconfigureSpecialParameter(specialParameter, null, -1);
            //specialParameter.configure(ability, parameterList);
            updateName();
            
            if ( parent instanceof AbilityNode ) {
                ((AbilityNode)parent).updateName();
            }
            
            model.nodeChanged(this);
        }
    }
    
    protected void updateName() {
        if ( specialParameter != null ) {
            setUserObject( specialParameter.getName() );
        }
    }
    
    public boolean PADValueChanging(PADValueEvent evt) {
        if ( specialParameter != null && ability != null ) {
            return specialParameter.checkParameter(ability, specialParameterIndex, evt.getKey(), evt.getValue(), evt.getOldValue());
        }
        return true;
    }
    
    /** Notifies Node of a drop event occuring in or below the node.
     * The Node can use the handleDrop method to perform actions based on the drop of a DetailList on
     * the Node Jtree.  If the node wishes to handles the event, it should call acceptDrop and dropComplete
     * on the event, then return true to indicate the event was handled.
     * @returns
     */
    public boolean handleDrop(JTree tree, TreePath dropPath,DropTargetDropEvent event) {
        return false;
    }
    
    
    /** Called to destroy the object.  All references should be set to null.
     * Once destroy has been called, the object will not be used anymore.
     */
    public void destroy() {
        super.destroy();
        setAbility(null);
        setSpecialParameter(null);
        setParameterList(null);
    }
    
    public void updateChildren() {
       // System.out.println("SpecialParameterNode update Children");
        int acount, aindex, ncount, nindex, total;
        ParameterNode pn;
        String parameterName;
        boolean parameterVisible, parameterEnabled, found, fireChange;
        
        Vector newChildren = new Vector();
        
        ncount = ( children != null ) ? children.size() : 0;
        total = 0;
        fireChange = false;
        
        if ( parameterList == null ) return;
        
        Iterator<Parameter> it = parameterList.getParameters();
            while( it.hasNext() ) {
                Parameter p = it.next();
                parameterName = p.getName();
            parameterVisible = parameterList.isVisible(parameterName);
            parameterEnabled = parameterList.isParameterEnabled(parameterName);
            // Try to find it in the children array.
            if ( parameterVisible ) {
                found = false;
                for(nindex=0;nindex<ncount;nindex++) {
                    if ( children.get(nindex) instanceof ParameterNode ) {
                        pn = (ParameterNode) children.get(nindex);
                        if ( pn.getParameter() == parameterName && pn.getParameterList() == parameterList ) {
                            found = true;
                            // Move the ability node from the childern list to the newChildern list
                            newChildren.add(pn);
                            if ( nindex != total ) fireChange = true;
                            children.set(nindex, null);
                            pn.setEnabled(parameterEnabled);
                            
                            break;
                        }
                    }
                }
                
                if ( found == false ) {
                    pn = new ParameterNode(model, ability, parameterList, this, parameterName);
                    pn.setEnabled(parameterEnabled);
                    pn.getParameterEditor().addPADValueListener(this);
                    newChildren.add(pn);
                    fireChange = true;
                }
                total++;
            }
        }
        
        Vector oldChildren = children;
        children = newChildren;
        
         // Now that everything is done, anything not-null in oldChildren should be destroyed
        // and references to it released.
        if ( oldChildren != null ) {
        for(nindex=0;nindex<oldChildren.size();nindex++) {
            if ( oldChildren.get(nindex) != null ) {
                ((AbilityTreeNode)oldChildren.get(nindex)).destroy();
                oldChildren.set(nindex,null);
                fireChange = true;
            }
        }
        }
        
        if ( fireChange && model != null ) model.nodeStructureChanged(this);
        

    }
    
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
  /*   public void propertyChange(PropertyChangeEvent evt) {
        String property = evt.getPropertyName();
        if ( property.equals( "parameter.INDEXSIZE" ) ) {
            updateChildren();
        }
    } */
    
    /** Getter for property parameterList.
     * @return Value of property parameterList.
     */
    public ParameterList getParameterList() {
        return parameterList;
    }
    
    /** Setter for property parameterList.
     * @param parameterList New value of property parameterList.
     */
    public void setParameterList(ParameterList parameterList) {
        if ( this.parameterList != parameterList ) {
        /*    if ( this.parameterList != null ) {
                this.parameterList.removePropertyChangeListener("parameter.INDEXSIZE",this);
            } */
            this.parameterList = parameterList;
         /*   if ( this.parameterList != null ) {
                this.parameterList.addPropertyChangeListener("parameter.INDEXSIZE",this);
            } */
        }
    }
    
    public boolean invokeMenu(JPopupMenu popup, JTree tree, TreePath path) {
        boolean result = false;
        
            removeSpecialParameterAction.setAbility(ability);
            removeSpecialParameterAction.setSpecialParameter( (SpecialParameter)specialParameter);
            removeMenuItem.setAction(removeSpecialParameterAction);
            popup.add(removeMenuItem);
            result = true;
        
        return result;
    }
    
    public int getColumnSpan(int column) {
        if ( column == AbilityTreeTableModel.ABILITY_TREE_ABILITYCOLUMN ) {
            return AbilityTreeTableModel.ABILITY_TREE_WIDTH;
        }
        else {
            return 1;
        }
    }
   
    public static class RemoveSpecialParameterAction extends SharedPopupAction {
        private Ability ability;
        private SpecialParameter specialParameter;
        
        public RemoveSpecialParameterAction() {
            super("Delete SpecialParameter");
        }
        
        public void actionPerformed(ActionEvent e ) {
            if ( ability != null && specialParameter != null ) {
                    ability.removeSpecialParameter(specialParameter);
            }
        }
        
        public void setAbility(Ability ability) {
            this.ability = ability;
        }
        
        public void setSpecialParameter(SpecialParameter specialParameter) {
            this.specialParameter = specialParameter;
        }
    }
}
