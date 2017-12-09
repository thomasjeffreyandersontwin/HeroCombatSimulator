/*
 * DetailListListModel.java
 *
 * Created on December 17, 2000, 4:05 PM
 */

package champions;

import champions.interfaces.AbilityIterator;
import champions.interfaces.AbilityList;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



/**
 *
 * @author  unknown
 * @version
 */
public class AbilityListModel extends AbstractListModel implements ListModel, ChangeListener {
    
    /** Holds value of property detailList. */
    private AbilityList abilityList;
    
    private List<Ability> abilityVector = new ArrayList<Ability>();
    
    /** Creates new DetailListListModel */
    public AbilityListModel(AbilityList dl) {
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
        AbilityIterator ai = abilityList.getAbilities();
        while (ai.hasNext()) {
            ability = ai.nextAbility();
            abilityVector.add(ability);
        }
        
        ai = Battle.getDefaultAbilitiesOld().getAbilities();
        while (ai.hasNext()) {
            ability = ai.nextAbility();
            abilityVector.add(ability);
        }
        
        fireContentsChanged(this, 0, abilityVector.size() );
    }

    
}