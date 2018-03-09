/*
 * AbilityList.java
 *
 * Created on February 8, 2002, 12:04 PM
 */

package champions.interfaces;

import champions.Ability;
import champions.Target;
import java.io.Serializable;
import javax.swing.event.ChangeListener;

/**
 *
 * @author  twalker
 * @version 
 */
public interface AbilityList extends Serializable{
    /** Returns an AbilityIterator which iterates through all the abilities in the list.
     *
     * This version of getAbilities should recursively iterator through all of the abilities
     * contained in this list.  Calling this method is equivalent to calling
     * getAbilities(true);
     */
    public AbilityIterator getAbilities();
    
    /** Returns an AbilityIterator which iterates through the abilities in the list.
     *
     * This version of getAbilities will iterate through the abilities in the list.
     * If the recursive flag is true, it will recursively iterator through it's sublists
     * also.
     */
    public AbilityIterator getAbilities(boolean recursive);
    
    /** Returns an AbilityIterator which iterates through all the skills in the list.
     *
     * This version of getSkills should recursively iterator through all of the skills
     * contained in this list.  Calling this method is equivalent to calling
     * getAbilities(true);
     */
    public AbilityIterator getSkills();
    
    /** Returns an AbilityIterator which iterates through the skills in the list.
     *
     * This version of getSkills will iterate through the skills in the list.
     * If the recursive flag is true, it will recursively iterator through it's sublists
     * also.
     */
    public AbilityIterator getSkills(boolean recursive);
    
    /** Returns the Name of the list.
     *
     * Returns the list's name.  
     */
    public String getName();
    
    /** Set the Name of the list.
     *
     */
    public void setName(String name);
    
    /** Returns an array of the child sublists of this list.
     */
    public AbilityList[] getSublists();
    
    /** Returns the count of the child sublists of this list.
     */
    public int getSublistCount();
    
    /** Returns the child sublist indicated by specified index.
     */
    public AbilityList getSublist(int index);
    
    /** Adds the specified sublist to this list.
     *
     * This method adds the specified sublist as a child of this list.  If the 
     * sublist previously belonged to a different sublist, it will be removed 
     * from that sublist.  If the sublist previously belonged to a different
     * target, the sublists target will be updated.
     */
    public void addSublist(AbilityList sublist);
    
    /** Removed the specified sublist from this list.
     *
     * This method removed the specified sublist from the list.  The sublists
     * target will be set to null and the sublists parent will be set to null.
     *
     * If the sublist is not a direct child of this list, nothing will be done.
     */
    public void removeSublist(AbilityList sublist);
    
    /** Removed the specified sublist from this list.
     *
     * This method removed the specified sublist from the list.  The sublists
     * target will be set to null and the sublists parent will be set to null.
     *
     * If the sublist is not a direct child of this list, nothing will be done.
     */
    public void removeSublist(AbilityList sublist, boolean clearAbilitySource);
    
    /** Returns the ability list this list is contain within.
     */
    public AbilityList getAbilityList();
    
    /** Sets the Parent list of this list.
     *
     * This method will set the parent list of this list.  This method does not
     * check to see that the list/sublist structure is correct and should not be
     * called directly.  Instead addSublist/removeSublist should be called.
     */
    public void setAbilityList(AbilityList parent);
    
    /** Adds the Ability to the List.
     *
     * This method will add the ability to the list.  The abilities target and sublist
     * parameters will be updated appropriately.
     */
    public void addAbility(Ability ability);
    
    /** Adds the Ability to the List.
     *
     * This method will add the ability to the list.  The abilities target and sublist
     * parameters will be updated appropriately.
     */
    public void addAbility(Ability ability, int index);
    
    /** Removes the Ability from this list.
     *
     * This method will remove the ability from this list.  The abilities target and sublist
     * parameters will be set to null.
     */
    public void removeAbility(Ability ability);
    
    /** Removes the Ability from this list.
     *
     * This method will remove the ability from this list.  The abilities target and sublist
     * parameters will be set to null.
     */
    public void removeAbility(Ability ability, boolean clearAbilitySource);
    
    /** Sets the Target which owns this list.
     *
     * This method will set the target which owns this list.  All of the abilities in the
     * list will be added to the target via the Target.addAbility method.  All child sublists
     * will have their setTarget methods called also.
     */
    public void setSource(Target target);
    
    /** Returns the Target which owns this list.
     */
    public Target getSource();
    
    /** Clones the list.
     * 
     * This method will create a recursive clone of the list.  However, the cloned copy will have
     * it's target set to null and will no longer be a child of it's current parent.
     */
    public AbilityList cloneList() throws CloneNotSupportedException;
    
    /** Returns the number of abilities contained in the list.
     *
     * This method returns the number of abilities directly contained in this sublist.  It does
     * not return the count from any sublists.
     */
    public int getAbilityCount();
    
    /** Returns whether the abilityList contains the indicated Ability.
     *
     */
    public boolean hasAbility(Ability ability, boolean recursive);
    
    /** Returns the indicated ability contained in this list.
     *
     * This method returns the ability at indicated index contained in this list.  It does 
     * not return any abilities from sublists.
     */
    public Ability getAbility(int index);
    
    /** Returns the ability of the indicated name, if it exists.
     *
     * Return null if the ability does not exist.
     */
    public Ability getAbility(String name, boolean recurse);
    
    /** Returns the Index of the Given ability.
     *
     * The index indicates the location of the ability in the list, starting with location 0.
     * This can be used to place abilities in specific orders.
     */
    public int getAbilityIndex(Ability ability);
    
    /** Set the Index of the given ability.
     *
     * This method sets the index of the given ability, changing the order of the abilities in 
     * the list.
     *
     */
    public void setAbilityIndex(Ability ability, int index);
    
//    /** Returns whether a given ability is current enabled.
//     *
//     * This method is called by Ability.isEnabled to determine if the ability is currently
//     * enabled.  This can be used by sublists to control abilities for special list types such
//     * as Multipower and Variable Point Pool.
//     *
//     * If the sublist does not care to modify whether an ability is enabled, it should return true.
//     */
//    public boolean isEnabled(Ability ability, Target source);
    
//    /** Returns the CP Cost of the Ability.
//     *
//     * This method is used to determine the cost of the Ability belonging to a list.  In general,
//     * the standard formula of Base * ( 1 + adv ) / (1 + lim ) should be used.  However, in some 
//     * cases, the cost is overriden due to special calculations, such as Multipower and Variable Point pool.
//     *
//     * This method is used to calculate the cost of both the CP Cost and the Adjusted CP Cost, so the ability
//     * cost should be based on only the powerCost, advantage, and limitation parameters provided.
//     *
//     * This method will only be called if the useCustomCost() method returns true.
//     */
//    public int calculateCost(Ability ability, int powerCost, double advantage, double limitation);
//    
//    /** Return whether the calculateCost method should be used to calculate special cost for the ability.
//     */
//    public boolean useCustomCost();
    
    /** Returns the sublist with the indicated name.
     *
     * If this list's name is equals to the indicated one, it returns this.  Otherwise, it searchs
     * recursively through sublists to find the name.  
     *
     * Returns null if the name is not found.
     */
    public AbilityList findSublist(String sublistName);
    
    /** Adds a ChangeListener for the List.
     *
     * stateChange events are fired in the following circumstances:
     * When an ability is added directly to this list.
     * When an ability is remove directly from this list.
     * When a sublist is added directly to this list.
     * When a sublist is removed directly from this list.
     * When this is the top most list and an ability is added to any child sublist.
     */
    public void addChangeListener(ChangeListener listener);
    
    /** Removes a ChangeListener for the List.
     */
    public void removeChangeListener(ChangeListener listener);
    
    /** Causes the List to recursively notify it's parent that an ability has been added.
     */
    public void abilityModificationInSublist(String reason);
    
    /** Returns the Cost of all of the Abilities in the AbilityList, incluing sublists.
     */
    public int getRealCost();
    
    /** Returns the framework that this sublist belongs to.
     *
     */
    public Framework getFramework();
    
    /** Sets the framework that this sublist belongs to.
     *
     * This method sets a sublists framework.  This should only be done if the
     * sublist and all of its children do not already belong to a framework.
     *
     * This method should recursively set the framework for all child sublist.
     */
    public void setFramework(Framework framework);
    
    /** Returns whether this sublist or its children have a framework set.
     *
     * This method recuvsively check to see if a framework is already set.
     */
    public boolean isFrameworkSet();

	public boolean getCollapsed();

	public boolean getExpanded();

	

	

}

