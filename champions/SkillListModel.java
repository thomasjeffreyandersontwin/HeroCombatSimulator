/*
 * DetailListListModel.java
 *
 * Created on December 17, 2000, 4:05 PM
 */

package champions;

import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import champions.interfaces.*;
/**
 *
 * @author  unknown
 * @version
 */
public class SkillListModel extends AbstractListModel implements ListModel, ChangeListener {
    
    /** Holds value of property detailList. */
    private AbilityList abilityList;
    
    private Vector abilityVector = new Vector();
    
    /** Creates new DetailListListModel */
    public SkillListModel(AbilityList dl) {
        setAbilityList(dl);
    }
    
    /** Returns the length of the list.
     */
    public int getSize() {
        return abilityVector.size();
    }
    
    /** Returns the value at the specified index.
     */
    public Object getElementAt(int index) {
        return abilityVector.get(index);
    }
    
    /** Getter for property detailList.
     * @return Value of property detailList.
     */
    public AbilityList getAbilityList() {
        return abilityList;
    }
    /** Setter for property detailList.
     * @param detailList New value of property detailList.
     */
    public void setAbilityList(AbilityList abilityList) {
        if ( abilityList != this.abilityList) {
            if ( this.abilityList != null ) {
                this.abilityList.removeChangeListener(this);
            }
            this.abilityList = abilityList;
            updateModel();
            
            if ( this.abilityList != null ) {
                this.abilityList.addChangeListener(this);
            }
        }
    }
    
    public void stateChanged(ChangeEvent evt) {
        updateModel();
    }
    
    public void updateModel() {
        abilityVector.clear();
        
        Ability ability;
        AbilityIterator ai = abilityList.getSkills();
        while (ai.hasNext()) {
            ability = ai.nextAbility();
            abilityVector.add(ability);
        }
        
        ai = Battle.getDefaultAbilitiesOld().getSkills();
        while (ai.hasNext()) {
            ability = ai.nextAbility();
            abilityVector.add(ability);
        }
        
        fireContentsChanged(this, 0, abilityVector.size() );
    }

    
}