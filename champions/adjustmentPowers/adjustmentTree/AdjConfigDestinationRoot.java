/*
 * AdjConfigRoot.java
 *
 * Created on March 3, 2002, 8:17 PM
 */

package champions.adjustmentPowers.adjustmentTree;

import champions.parameters.ParameterList;
import treeTable.*;

import champions.*;
import champions.interfaces.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.beans.*;
import java.io.*;
import java.util.Vector;
/**
 *
 * @author  twalker
 * @version
 */
public class AdjConfigDestinationRoot extends DefaultTreeTableNode implements ChampionsConstants, PropertyChangeListener {
    /** Holds value of property modelType. */
    private int modelType;
    
    /** Holds value of property level. */
    private int level;
    
    static private AdjConfigDestinationRoot.RemoveDestinationAction removeAction;
    
    // Hold Data Flavors
    static private DataFlavor powerFlavor, characteristicFlavor, abilityFlavor, specialEffectFlavor;
    
    /** Holds value of property parameterList. */
    private ParameterList parameterList;
    
    /** Holds value of property parameter. */
    private String parameter;
    
    /** Creates new AdjConfigRoot */
    public AdjConfigDestinationRoot(ParameterList parameterList, String parameter, int modelType, int level) {
        setModelType(modelType);
        setLevel(level);
        setParameterList(parameterList);
        setParameter(parameter);
        
        setupFlavors();
        setupActions();
        
        updateChildren();
    }
    
    protected void setupFlavors() {
        try {
            if (powerFlavor == null ) powerFlavor = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType + "; class=" + Power.class.getName() );
            if (abilityFlavor == null ) abilityFlavor = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType + "; class=" + Ability.class.getName() );
            if (characteristicFlavor == null ) characteristicFlavor = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType + "; class=" + Characteristic.class.getName() );
            if (specialEffectFlavor == null ) specialEffectFlavor = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType + "; class=" + SpecialEffect.class.getName() );
        }
        catch ( Exception e) {
            System.out.println(e);
        }
    }
    
    protected void setupActions() {
        removeAction = new AdjConfigDestinationRoot.RemoveDestinationAction("Remove");
    }
    
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
        if ( parameterList != this.parameterList ) {
            if ( this.parameterList != null ) {
                this.parameterList.removePropertyChangeListener(this);
            }
            this.parameterList = parameterList;
            if ( this.parameterList != null ) {
                this.parameterList.addPropertyChangeListener(this);
            }
        }
    }
    
    /** Getter for property modelType.
     * @return Value of property modelType.
     */
    public int getModelType() {
        return modelType;
    }
    
    /** Setter for property modelType.
     * @param modelType New value of property modelType.
     */
    public void setModelType(int modelType) {
        this.modelType = modelType;
    }
    
    /** Getter for property level.
     * @return Value of property level.
     */
    public int getLevel() {
        return level;
    }
    
    /** Setter for property level.
     * @param level New value of property level.
     */
    public void setLevel(int level) {
        this.level = level;
    }
    
    protected void updateChildren() {
        if ( parameterList == null ) {
            // Children should really be destroyed as they are removed, but we will leave this for now.
            this.removeAllChildren();
            if ( model != null ) ((DefaultTreeTableModel)model).nodeStructureChanged(this);
        }
        else {
            boolean fireChange = false;
            boolean found;
            // Make sure the abilities in the source match the parameterListNodes that are childern
            Vector newChildren = new Vector();
            int aindex, acount;
            int nindex, ncount;
            int total = 0;
            Object o;
            AdjPowerNode pn;
            AdjSpecialEffectNode sen;
            AdjAbilityNode an;
            AdjStatNode sn;
            
            
            ncount = ( children != null ) ? children.size() : 0;
            
            // Check for all the limitations
            acount = parameterList.getIndexedParameterSize(parameter);
            for( aindex = 0; aindex < acount; aindex++) {
                o = parameterList.getIndexedParameterValue(parameter,aindex);
                
                if ( o instanceof Power ) {
                    Power p = (Power)o;
                    // Try to find it in the children array.
                    found = false;
                    for(nindex=0;nindex<ncount;nindex++) {
                        if ( children.get(nindex) instanceof AdjPowerNode ) {
                            pn = (AdjPowerNode) children.get(nindex);
                            if ( pn.getPower().equals(p) ) {
                                found = true;
                                // Move the parameterList node from the childern list to the newChildern list
                                newChildren.add(pn);
                                if ( nindex != total ) fireChange = true;
                                children.set(nindex, null);
                                break;
                            }
                        }
                    }
                    
                    if ( found == false ) {
                        pn = new AdjPowerNode(p);
                        newChildren.add(pn);
                        pn.setParent(this);
                        pn.setModel(model);
                        fireChange = true;
                    }
                }
                else if ( o instanceof Ability ) {
                    Ability a = (Ability)o;
                    // Try to find it in the children array.
                    found = false;
                    for(nindex=0;nindex<ncount;nindex++) {
                        if ( children.get(nindex) instanceof AdjAbilityNode ) {
                            an = (AdjAbilityNode) children.get(nindex);
                            if ( an.getAbility().equals(a) ) {
                                found = true;
                                // Move the parameterList node from the childern list to the newChildern list
                                newChildren.add(an);
                                if ( nindex != total ) fireChange = true;
                                children.set(nindex, null);
                                break;
                            }
                        }
                    }
                    
                    if ( found == false ) {
                        an = new AdjAbilityNode(a);
                        newChildren.add(an);
                        an.setParent(this);
                        an.setModel(model);
                        fireChange = true;
                    }
                }
                else if ( o instanceof Characteristic ) {
                    Characteristic c = (Characteristic)o;
                    // Try to find it in the children array.
                    found = false;
                    for(nindex=0;nindex<ncount;nindex++) {
                        if ( children.get(nindex) instanceof AdjStatNode ) {
                            sn = (AdjStatNode) children.get(nindex);
                            if ( sn.getName().equals( c.getName() ) ) {
                                found = true;
                                // Move the parameterList node from the childern list to the newChildern list
                                newChildren.add(sn);
                                if ( nindex != total ) fireChange = true;
                                children.set(nindex, null);
                                break;
                            }
                        }
                    }
                    
                    if ( found == false ) {
                        sn = new AdjStatNode(c.getName());
                        newChildren.add(sn);
                        sn.setParent(this);
                        sn.setModel(model);
                        fireChange = true;
                    }
                }
                else if ( o instanceof SpecialEffect ) {
                    SpecialEffect se = (SpecialEffect)o;
                    // Try to find it in the children array.
                    found = false;
                    for(nindex=0;nindex<ncount;nindex++) {
                        if ( children.get(nindex) instanceof AdjSpecialEffectNode ) {
                            sen = (AdjSpecialEffectNode) children.get(nindex);
                            if ( sen.getSpecialEffect().equals( se ) ) {
                                found = true;
                                // Move the parameterList node from the childern list to the newChildern list
                                newChildren.add(sen);
                                if ( nindex != total ) fireChange = true;
                                children.set(nindex, null);
                                break;
                            }
                        }
                    }
                    
                    if ( found == false ) {
                        sen = new AdjSpecialEffectNode( se );
                        newChildren.add(sen);
                        sen.setParent(this);
                        sen.setModel(model);
                        fireChange = true;
                    }
                }
                
                total++;
            }
            
            Vector oldChildren = children;
            children = newChildren;
            
            // Now that everything is done, anything level not-null in oldChildren should be destroyed
            // and references to it released.
            if ( oldChildren != null ) {
                for(nindex=0;nindex<oldChildren.size();nindex++) {
                    if ( oldChildren.get(nindex) != null ) {
                        ((DefaultTreeTableNode)oldChildren.get(nindex)).destroy();
                        oldChildren.set(nindex,null);
                        fireChange = true;
                    }
                }
            }
            
            if ( fireChange && model != null ) ((DefaultTreeTableModel)model).nodeStructureChanged(this);
        }
    }
    
    protected String getIndexName() {
        switch ( modelType ) {
            case ADJ_CONFIG_DRAIN_FROM:
                return "DrainFrom";
            case ADJ_CONFIG_AID_TO:
                return "AidTo";
            case ADJ_CONFIG_TRANSFER_FROM:
                return "TransferFrom";
            case ADJ_CONFIG_TRANSFER_TO:
                return "TransferTo";
            case ADJ_CONFIG_ABSORB_TO:
                return "AbsorbTo";
        }
        return null;
    }
    
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        String indexName = getIndexName();
        if ( evt.getPropertyName().startsWith( indexName ) ) {
            updateChildren();
        }
    }
    
    public TreePath willHandleDrop(TreeTable treeTable, TreePath path, DropTargetDragEvent event) {
        TreePath dropPath = null;
        
        if ( isFlavorHandled(event) ) {
            if ( level != ADJ_SINGLE_ADJUSTMENT || children == null || children.size() == 0 ) {
                if ( children != null && children.size() > 0 ) {
                    DefaultTreeTableNode lastChild = (DefaultTreeTableNode)children.get( children.size() - 1 );
                    dropPath = new TreePath( lastChild.getPath() );
                }
                else {
                    dropPath = new TreePath( getPath() );
                }
            }
        }
        
        return dropPath;
    }
    
    public boolean handleDrop(TreeTable treeTable, TreePath path, DropTargetDropEvent event) {
        if ( isFlavorHandled(event) && (level != ADJ_SINGLE_ADJUSTMENT || children == null || children.size() == 0) ) {
            try {
                Transferable tr = event.getTransferable();
                Object o = null;
                if( event.isDataFlavorSupported(powerFlavor)  ) {
                    o = tr.getTransferData(powerFlavor);
                }
                else if( event.isDataFlavorSupported(specialEffectFlavor)  ) {
                    o = tr.getTransferData(specialEffectFlavor);
                }
                else if( event.isDataFlavorSupported(characteristicFlavor)  ) {
                    o = tr.getTransferData(characteristicFlavor);
                }
                else if( event.isDataFlavorSupported(abilityFlavor)  ) {
                    o = tr.getTransferData(abilityFlavor);
                }
                
                if ( o != null ) {
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    
                    addDestinationToParameterList(o);
                    
                    event.dropComplete(true);
                    return true;
                }
                
            }
            catch(IOException ioe) {
                ioe.printStackTrace();
            }
            catch(UnsupportedFlavorException ufe) {
                ufe.printStackTrace();
            }
        }
        
        return false;
    }
    
    protected boolean isFlavorHandled(DropTargetDragEvent event) {
        return  ( event.isDataFlavorSupported(powerFlavor)
        || event.isDataFlavorSupported(abilityFlavor)
        || event.isDataFlavorSupported(characteristicFlavor)
        || event.isDataFlavorSupported(specialEffectFlavor) );
    }
    
    protected boolean isFlavorHandled(DropTargetDropEvent event) {
        return  ( event.isDataFlavorSupported(powerFlavor)
        || event.isDataFlavorSupported(abilityFlavor)
        || event.isDataFlavorSupported(characteristicFlavor)
        || event.isDataFlavorSupported(specialEffectFlavor) );
    }
    
    protected void addDestinationToParameterList(Object o ) {
    /*    String indexName = getIndexName();
        
        int index = parameterList.findIndexed(indexName, "OBJECT", o);
        if ( index == -1 ) {
            parameterList.createIndexed(indexName, "OBJECT", o);
        } */
        parameterList.addIndexedParameterValue(parameter, o );
    }
    
    public boolean invokeMenu(TreeTable treeTable,TreePath path, JPopupMenu popup) {
        boolean result = false;
        DefaultTreeTableNode node = (DefaultTreeTableNode)path.getLastPathComponent();
        if ( node instanceof AdjPowerNode ) {
            removeAction.setParameterList(parameterList);
            removeAction.setParameter( parameter ) ;
            removeAction.setObject( ((AdjPowerNode)node).getPower() );
            result = true;
        }
        else if ( node instanceof AdjStatNode ) {
            removeAction.setParameterList(parameterList);
            removeAction.setParameter( parameter ) ;
            removeAction.setObject( new Characteristic (((AdjStatNode)node).getName() ));
            result = true;
        }
        else if ( node instanceof AdjSpecialEffectNode ) {
            removeAction.setParameterList(parameterList);
            removeAction.setParameter( parameter ) ;
            removeAction.setObject( ((AdjSpecialEffectNode)node).getSpecialEffect() );
            result = true;
        }
        else if (  node instanceof AdjAbilityNode ) {
            removeAction.setParameterList(parameterList);
            removeAction.setParameter( parameter ) ;
            removeAction.setObject( ((AdjAbilityNode)node).getAbility() );
            result = true;
        }
        
        if ( result == true ) {
            popup.add(removeAction);
        }
        
        return result;
    }
   
    /** Getter for property parameter.
     * @return Value of property parameter.
     */
    public String getParameter() {
        return parameter;
    }    
    
    /** Setter for property parameter.
     * @param parameter New value of property parameter.
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
    
    public static class RemoveDestinationAction extends AbstractAction {
        
        /** Holds value of property parameterList. */
        private ParameterList parameterList;
        
        /** Holds value of property object. */
        private Object object;
        
        /** Holds value of property parameter. */
        private String parameter;
        
        public RemoveDestinationAction(String name) {
            super(name);
        }
        
        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            if ( parameterList != null && parameter != null && object != null ) {
                parameterList.removeIndexedParameterValue(parameter, object);
            }
            
            
        }
        
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
            this.parameterList = parameterList;
        }
       
        
        /** Getter for property object.
         * @return Value of property object.
         */
        public Object getObject() {
            return object;
        }
        
        /** Setter for property object.
         * @param object New value of property object.
         */
        public void setObject(Object object) {
            this.object = object;
        }
        
        /** Getter for property parameter.
         * @return Value of property parameter.
         */
        public String getParameter() {
            return parameter;
        }
        
        /** Setter for property parameter.
         * @param parameter New value of property parameter.
         */
        public void setParameter(String parameter) {
            this.parameter = parameter;
        }
        
    }
}
