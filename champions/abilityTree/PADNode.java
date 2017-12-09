/*
 * PADNode.java
 *
 * Created on June 11, 2001, 7:43 PM
 */

package champions.abilityTree;

import champions.Ability;
import champions.ChampionsUtilities;
import champions.FrameworkAbility;
import champions.parameters.ParameterList;
import champions.Power;
import champions.event.PADValueEvent;
import champions.interfaces.*;
import champions.parameters.Parameter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
 * The PADNode is used to represent and edit Advantages/Limitations belonging to an ability.
 *
 * One PADNode is created for each Advantage/Limitation.
 *
 * @author  twalker
 * @version
 */
public class PADNode extends AbilityTreeNode
implements PADValueListener, PropertyChangeListener {
    
    /** Holds value of property pad. */
    protected PAD pad;
    
    /** Holds value of property ability. */
    protected Ability ability;
    
    /** Holds value of property index. */
    protected int padIndex = -1;
    
    /** Holds value of property parameterList. */
    protected ParameterList parameterList;
    
    
    static private PADNode.RemoveAdvantageAction removeAdvantageAction;
    static private PADNode.RemoveLimitationAction removeLimitationAction;
    static private JMenuItem removeMenuItem;
    
    /** Creates new PADNode */
    public PADNode(AbilityTreeTableModel model, Ability ability, MutableTreeNode parent, PAD pad, int index) {
        setModel(model);
        setAbility(ability);
        setParent(parent);
        setPad(pad);
        setIndex(index);
        
        updateChildren();
        
        setupActions();
    }
    
    /** Creates new PADNode */
    public PADNode(AbilityTreeTableModel model, Ability ability, MutableTreeNode parent, Power pad) {
        setModel(model);
        setAbility(ability);
        setParent(parent);
        setPad(pad);
        
        updateChildren();
        
        setupActions();
    }
    
    private void setupActions() {
      if ( removeAdvantageAction == null ) removeAdvantageAction = new PADNode.RemoveAdvantageAction();   
      if ( removeLimitationAction == null ) removeLimitationAction = new PADNode.RemoveLimitationAction();
      if ( removeMenuItem == null ) removeMenuItem = new JMenuItem();
    }
    
 /*   protected void buildNode() {
        if ( children == null ) {
            children = new Vector();
        }
        
        ParameterNode mtn = null;
        int index, count;
        
        if ( ability != null && pad != null && parameterList != null) {
            
            String name;
            count = parameterList.getIndexedSize( "parameter");
            for(index=0;index<count;index++) {
                name = parameterList.getIndexedStringValue( index, "parameter", "NAME" );
                mtn = new ParameterNode(model, getAbility(), parameterList, this, name);
                children.add(mtn);
                
                mtn.getParameterEditor().addPADValueListener(this);
            }
        }
    } */
    
    /** Getter for property pad.
     * @return Value of property pad.
     */
    public PAD getPad() {
        return pad;
    }
    
    /** Setter for property pad.
     * @param pad New value of property pad.
     */
    public void setPad(PAD pad) {
        this.pad = pad;
        
        if ( pad != null ) {    
            if ( ability != null && ( pad instanceof Power || padIndex != -1) ) {
                setParameterList( pad.getParameterList(ability, padIndex) );
            }
        }
    }
    
    public boolean isAddedByFramework() {
        if ( pad instanceof Advantage ) {
            return ((Advantage)pad).isAddedByFramework();
        }
        else if ( pad instanceof Limitation ) {
            return ((Limitation)pad).isAddedByFramework();
        }
        return false;
    }
    
    public boolean isPrivateToFramework() {
        if ( pad instanceof Advantage ) {
            return ability instanceof FrameworkAbility && ((Advantage)pad).isPrivate();
        }
        else if ( pad instanceof Limitation ) {
            return ability instanceof FrameworkAbility && ((Limitation)pad).isPrivate();
        }
        return false;
    }
    
    public void setPrivateToFramework(boolean privateToFramework) {
        if ( pad instanceof Advantage ) {
            ((Advantage)pad).setPrivate(privateToFramework);
        }
        else if ( pad instanceof Limitation ) {
            ((Limitation)pad).setPrivate(privateToFramework);
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
        
        if ( pad != null && ability != null && ( pad instanceof Power || padIndex != -1) ) {
            setParameterList( pad.getParameterList(ability, padIndex) );
        }
    }
    
    /** Getter for property index.
     * @return Value of property index.
     */
    public int getIndex() {
        return padIndex;
    }
    
    /** Setter for property index.
     * @param index New value of property index.
     */
    public void setIndex(int padIndex) {
        this.padIndex = padIndex;
        
        if ( pad != null && ability != null && ( pad instanceof Power || padIndex != -1) ) {
            setParameterList( pad.getParameterList(ability, padIndex) );
        }
    }
    
    public Icon getIcon(JTree tree,boolean selected,boolean expanded,boolean leaf,int row,boolean hasFocus) {
        return (pad != null) ? pad.getIcon() : null;
    }
    
    public String toString() {
        if ( pad instanceof Power ) {
            return pad.getName();
        }
        else if ( pad instanceof Advantage) {
            Advantage a = (Advantage)pad;
            if ( a.isAddedByFramework() ) {
                return pad.getName() + " [Framework]";
            }
            else {
                double cost;

                if ( a.isZeroCost() ) {
                    cost = 0;
                }
                else {
                    cost = a.calculateMultiplier(ability, padIndex);
                }

                return ( pad == null ) ? "" : (pad.getName() + " (" + ChampionsUtilities.toSignedStringWithFractions(cost) + ")");
            }
        }
        else if ( pad instanceof Limitation ) {
            Limitation lim = (Limitation)pad;
            if ( lim.isAddedByFramework() ) {
                return pad.getName() + " [Framework]";
            }
            else {
                double cost = lim.calculateMultiplier(ability, padIndex);
                return ( pad == null ) ? "" : (pad.getName() + " (" + ChampionsUtilities.toSignedStringWithFractions(cost) + ")");
            }
        }
        return "PADNode toString() Error";
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
        if ( pad != null && ability != null && parameterList != null ) {
            ability.reconfigure(pad, parameterList);
            ability.calculateMultiplier();
            ability.calculateCPCost();
            
            if ( parent instanceof AbilityNode ) {
                ((AbilityNode)parent).updateName();
            }
        }
    }
    
    public boolean PADValueChanging(PADValueEvent evt) {
        if ( pad != null && ability != null ) {
            return pad.checkParameter(ability, padIndex, evt.getKey(), evt.getValue(), evt.getOldValue());
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
    
    public String getToolTipText(int column) {
        if ( pad instanceof Limitation && ability != null) {
            int index = ability.findExactLimitation((Limitation)pad);
            if ( index != -1 ) {
                return pad.getConfigSummary(ability, index);
            }
        }
        else if ( pad instanceof Advantage && ability != null) {
            int index = ability.findExactAdvantage((Advantage)pad);
            if ( index != -1 ) {
                return pad.getConfigSummary(ability, index);
            }
        }
        return null;
        
    }
    
    
    /** Called to destroy the object.  All references should be set to null.
     * Once destroy has been called, the object will not be used anymore.
     */
    public void destroy() {
        super.destroy();
        setAbility(null);
        setPad(null);
        setParameterList(null);
    }
    
    public void updateChildren() {
       // System.out.println("PADNode update Children");
        int acount, aindex, ncount, nindex, total;
        ParameterNode pn;
        String parameterName;
        boolean parameterVisible, parameterEnabled, found;
        boolean fireChange = true;
        
        
        Vector newChildren = new Vector();
        
        if ( isAddedByFramework() == false ) {
        
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
                        pn.setTree(tree);
                        pn.setEnabled(parameterEnabled);
                        pn.getParameterEditor().addPADValueListener(this);
                        newChildren.add(pn);
                        fireChange = true;
                    }
                    total++;
                }
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
            if ( this.parameterList != null ) {
                this.parameterList.removePropertyChangeListener(this);
            } 
            this.parameterList = parameterList;
            if ( this.parameterList != null ) {
                this.parameterList.addPropertyChangeListener(this);
                updateChildren();
            }
        }
    }
    
    public boolean invokeMenu(JPopupMenu popup, JTree tree, TreePath path) {
        boolean result = false;
        
        if ( pad instanceof Limitation ) {
            removeLimitationAction.setAbility(ability);
            removeLimitationAction.setLimitation( (Limitation)pad);
            removeMenuItem.setAction(removeLimitationAction);
            popup.add(removeMenuItem);
            result = true;
        }
        else if ( pad instanceof Advantage ) {
            removeAdvantageAction.setAbility(ability);
            removeAdvantageAction.setAdvantage( (Advantage)pad);
            removeMenuItem.setAction(removeAdvantageAction);
            popup.add(removeMenuItem);
            result = true;
        }
        
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

    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getPropertyName().endsWith("VISIBLE") ) {
            updateChildren();
        }
    }
   
    public static class RemoveAdvantageAction extends SharedPopupAction {
        private Ability ability;
        private Advantage advantage;
        
        public RemoveAdvantageAction() {
            super("Delete Advantage");
        }
        
        public void actionPerformed(ActionEvent e ) {
            
            if ( ability != null && advantage != null ) {

                    ability.removeAdvantage(advantage);
            }
        }
        
        public void setAbility(Ability ability) {
            this.ability = ability;
        }
        
        public void setAdvantage(Advantage advantage) {
            this.advantage = advantage;
        }
    }
    
    public static  class RemoveLimitationAction extends SharedPopupAction {
        private Ability ability;
        private Limitation limitation;
        
        public RemoveLimitationAction() {
            super("Delete Limitation");
        }
        
        public void actionPerformed(ActionEvent e ) {
            
            if ( ability != null && limitation != null ) {

                    ability.removeLimitation(limitation);
            }
        }
        
        public void setAbility(Ability ability) {
            this.ability = ability;
        }
        
        public void setLimitation(Limitation limitation) {
            this.limitation = limitation;
        }
    }
}
