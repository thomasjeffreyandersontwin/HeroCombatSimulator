/*
 * AbilityNode.java
 *
 * Created on June 11, 2001, 7:32 PM
 */

package champions.abilityTree;

import champions.*;
import champions.SpecialEffect;
import champions.interfaces.*;
import champions.parameterEditor.*;
import champions.event.*;
import tjava.*;

import treeTable.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.util.*;
import java.beans.*;
import java.io.*;

/**
 *
 * @author  twalker
 * @version
 */
public class SpecialEffectsNode extends AbilityTreeNode 
implements PropertyChangeListener, ChampionsConstants {
    
    /** Holds value of property ability. */
    private Ability ability;
    
    private String name;
   
    private Icon icon;

    
    /** Creates new AbilityNode */
    public SpecialEffectsNode(AbilityTreeTableModel model, MutableTreeNode parent, Ability ability) {
        setModel(model);
        setParent(parent);
        setAbility(ability);
        
        setExpandDuringDrag(false);
        
        icon = UIManager.getIcon("SpecialEffect.DefaultIcon");
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
        if ( ability != this.ability ) {
            if ( this.ability != null ) {
                this.ability.removePropertyChangeListener(this);
            }
            
            this.ability = ability;
            updateName();
            updateChildren();
            
            if ( this.ability != null ) {
                this.ability.addPropertyChangeListener(this);
            }
        }
    }
    
 /*   public Icon getIcon(JTree tree,boolean selected,boolean expanded,boolean leaf,int row,boolean hasFocus) {
       // return (power != null) ? power.getIcon() : null;
    } */
    
    public String toString() {
        return name;
    }  
   
    /** Called to destroy the object.  All references should be set to null.
     * Once destroy has been called, the object will not be used anymore.
     */
    public void destroy() {
        super.destroy();
        setAbility(null);
    }
    
    public void updateName() {
        
        if ( ability != null ) {
            name = "Special Effects: ";
            
            int count = 0;
            SpecialEffectIterator sei = ability.getSpecialEffectIterator();
            while ( sei.hasNext() ) {
                SpecialEffect se = sei.nextSpecialEffect();
                
                if ( count > 0 ) {
                    name = name + ", ";
                }
                
                name = name + se.getName();
                count ++;
            }
            
            if ( count == 0 ) {
                name = name + "None";
            }
        }
        
        if ( model != null ) model.nodeChanged(this);
    }
    
    public void updateChildren() {
        // System.out.println("AbilityNode update Children");
        boolean fireChange = false;
        boolean found;
        // Make sure the abilities in the source match the abilityNodes that are childern
        Vector newChildren = new Vector();
        int aindex, acount;
        int nindex, ncount;
        int total = 0;
        PAD pad;
        PADNode pn;
        ParameterNode pan;
        SpecialEffectNode sen;
        String parameterName;
        
        ncount = ( children != null ) ? children.size() : 0;
        
        if ( ability == null ) {
            if ( children != null ) children.clear();
            return;
        }
        
        // Add the name/special effect parameter Nodes
        SpecialEffectIterator sei = ability.getSpecialEffectIterator();
        while( sei.hasNext() ) {
            SpecialEffect se = sei.nextSpecialEffect();
            
            found = false;
            for(nindex=0;nindex<ncount;nindex++) {
                if ( children.get(nindex) instanceof SpecialEffectNode ) {
                    sen = (SpecialEffectNode) children.get(nindex);
                    if ( sen.getSpecialEffect() == se  ) {
                        found = true;
                        // Move the ability node from the childern list to the newChildern list
                        newChildren.add(sen);
                        if ( nindex != total ) fireChange = true;
                        children.set(nindex, null);
                        break;
                    }
                }
            }
            
            
            if ( found == false ) {
                sen = new SpecialEffectNode(model, this, ability, se);
                newChildren.add(sen);
                fireChange = true;
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
    public void propertyChange(PropertyChangeEvent evt) {
        String property = evt.getPropertyName();
        
        if ( evt.getSource() == ability ) {
            if ( property.startsWith("SpecialEffect" ) ) {
                updateName();
                updateChildren();
            }
        }
    }
   
    
    public Object getValue(int columnIndex) {
        Object v = null;
        
        if ( ability != null ) {
            switch (columnIndex) {
                case AbilityTreeTableModel.ABILITY_TREE_ABILITYCOLUMN:
                    v = toString();
                    break;
                case AbilityTreeTableModel.ABILITY_TREE_ENDCOLUMN:
                    break;
                case AbilityTreeTableModel.ABILITY_TREE_PTSCOLUMN:
                    break;
            }
        }
        
        return v;
    }
    
    public int getColumnSpan(int columnIndex) {
        return (columnIndex == AbilityTreeTableModel.ABILITY_TREE_ABILITYCOLUMN) ? 3 : 1;
    }
    
    public boolean isLeaf() {
        return false;
    }
    
    public Icon getIcon(JTree tree,boolean selected,boolean expanded,boolean leaf,int row,boolean hasFocus) {
        return icon;
    }

}
