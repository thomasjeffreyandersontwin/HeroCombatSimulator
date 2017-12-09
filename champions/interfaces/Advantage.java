/*
 * Advantage.java
 *
 * Created on September 21, 2000, 10:02 AM
 */

package champions.interfaces;

import tjava.Filter;
import champions.Ability;
import champions.AbilityImport;
import champions.BattleEvent;
import champions.Effect;
import champions.Target;
import champions.attackTree.AttackTreeNode;
import champions.exception.BattleEventException;
import champions.parameters.ParameterList;


/**
 *
 * @author  unknown
 * @version 
 */
public interface Advantage extends PAD, Cloneable {

    public void prepower(BattleEvent be, int index, Ability ability, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget) throws BattleEventException;
    public void postpower(BattleEvent be, int index, Ability ability, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget) throws BattleEventException;
    public double calculateMultiplier(Ability ability, int index);
    public AttackTreeNode preactivate(BattleEvent be, int index, Ability ability);
    public boolean affectsDC(Ability a, int index);
    public void setAffectsDC(Ability a, int index, boolean affect);
    /** Returns a Filter that accepts only eligible targets.
     *
     * Returns null if all targets are acceptible.
     */
    public Filter<Target> getTargetFilter(Ability ability, int index);
    
    /** Notifies the Advantage that the Ability has been shutdown.
     *
     * This is called during the Ability deactivation process.
     */
    public void shutdownAbility(BattleEvent be, int index, Ability ability, Target source) throws BattleEventException;
    
    /** Removes Special Configurations from the Ability which the advantage might have added.
     *
     * RemoveAdvantage is called prior to an advantage being removed from an ability.  The
     * removeAdvantage method should remove any value pairs that it specifically added to the
     * ability.  
     *
     * The ability will take care of removing the advantages configuration, object, and parameter
     * lists under the advantage#.* value pairs.
     */
    public void removeAdvantage(Ability ability, int index);
    
    /** Attempt to identify Advantage 
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     * @param ai AbilityImport which is currently being imported.
     * @return Value indicating likelyhood that AbilityImport is this kind of power.
     * 0 indicates there was no recognition. 
     * 5 indicates probable match.
     * 10 indicates almost certain recognition.
     *
    */
    public int identifyAdvantage(AbilityImport ai, int line);

    /** Attempt to Import Advantage setting from AbilityImport information.
     * @param ability Ability into which to import.
     * @param ai AbilityImport from which to grab import information.
     */    
    public void importAdvantage(Ability ability, int index, AbilityImport ai, int line);
    
    /** Indicates only one instance of this advantage can exist for any given ability.
     */
    public boolean isUnique();
    
    /* Returns whether an ability with advantage is enabled.
     * Returns TRUE if the ability is enabled, or FALSE if the ability is disabled
     * specifically because of this advantage.
     *
     * If the ability is not enabled, the ability.setEnableMessage method should 
     * be used to set the message that is displayed in the popup tooltip when the
     * mouse is held over the disabled ability.
     */
    public boolean isEnabled(Ability ability, int index, Target source);
    
    /** Provides hook to add menu items to the Ability right-click menu
     * @param menu
     * @param ability
     * @return true if menus where actually added.
     */
    public boolean invokeMenu(javax.swing.JPopupMenu menu, Ability ability, int index);
    
    /** Get the parameterList necessary to configure the PAD for the ability.
    *
    * This parameterList is stored with the ability from this point forward, so
    * additional calls to getParameters will always return the same parameter list.
    * Note: If the advantage is not defined in the power already, a default parameterList
    * will be generated.
    * @param ability Ability to get parameterList from.
    * @return ParameterList extracted from ability.
    */
    public ParameterList getParameterList(Ability ability);
       
 /** Returns the patterns necessary to import the Power from CW.
     * The Object[][] returned should be in the following format:
     * patterns = Object[][] {
     *  { "PATTERN" , new Object[] { "PARAMETER1", parameter1.class, "PARAMETER2", parameter2.class, ... },
     *  ...
     *  }
     *
     * PATTERN should be a regular expression pattern.  For every PARAMETER* where should be one
     * parathesis group in the expression. The PARAMETERS sub-array can be null, if the line has no parameter
     * and is just informational.
     *
     * By default, the importPower will check each line of the getImportPatterns() array and if a match is
     * found, the specified parameters will be set in the powers parameter list.  It is assumed that each 
     * PATTERN will only occur once.  If the pattern can occur multiple times in a valid import, a custom
     * importPower method will have to be used.
     */
    public Object[][] getImportPatterns();
    
    /* Finishes Importing Ability.
     *
     * This method is called after the character has been completely built, with all abilities
     * that are going to be added already added.  This method can be used to finalize any necessary
     * ability changes, such as translating from Strings to actual Ability objects.
     *
     * This method should return true if it wants the configurePAD to be run once it is done
     * finalizing the method.
     */
    public boolean finalizeImport(Ability ability, int index, AbilityImport ai);
    
    /** Initializes the Advantage when it is first loaded by the Simulator.
     *
     * The <code>initialize</code> method is gauranteed to be called at least once prior to  
     * any use of the Advantage.  However, it is possible that the method would be called
     * multiple times.  If the initialization code can only be run one, the Advantage should
     * track whether it has been initialized already.
     */
    public void initialize();
    
    /** Compares the configuration of advantages from two abilities.
     *
     * In order for the configuration of two advantages to be equal, the
     * advantages must be of the same type and their parameter lists must
     * be configured the same.
     *
     * <code>thisAbility<code> and <code>thatAbility</code> must both 
     * have this type of advantage at the indicated indexes.
     */
    public boolean compareConfiguration(Ability thisAbility, int thisIndex, Ability thatAbility, int thatIndex);

    /** Provides hook to add items to the "Misc Action" panel.
     * After every character change, the actions panel is refreshed.  Powers can add
     * Actions to the panel for convient access.
     * @param v Vector to which Actions can be added for display
     * @param ability Ability being processed.
     */    
    public void addActions(java.util.Vector v, Ability ability, int index);

    Ability getAbility();

    String getDescription();

    double getMultiplier();

    String getName();

    int getPriority();

    void setAbility(Ability ability);

    void setAddedByFramework(boolean addedByFramework);

    void setPriority(int priority);

    void setPrivate(boolean privateLimitation);

    ParameterList getParameterList();

    void setParameterList(ParameterList parameterList);

    boolean isAddedByFramework();

    boolean isPrivate();
    
    public Advantage clone();

    Object getFinalizer();

    void setFinalizer(Object finalizer);

    void setDescription(String description);
    
    boolean isZeroCost();
    
    void setZeroCost(boolean zeroCost);
}
