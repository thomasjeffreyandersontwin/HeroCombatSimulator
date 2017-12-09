/*
 * Interface.java
 *
 * Created on March 22, 2002, 6:17 PM
 */

package champions.interfaces;



import champions.Ability;
import champions.AbilityImport;
import champions.DetailList;
import champions.Target;
import champions.parameters.ParameterList;
import javax.swing.Icon;


/**
 *
 * @author  Trevor Walker
 * @version
 */
public interface SpecialParameter extends Cloneable {
    /** Configures the ability. The SpecialParameter should create a new
     * ParameterList appropriately. The parameterList should be stored with 
     * the ability for configuration later on. If an existing parameterList 
     * alread exists, it should be replaced with this one.
     *
     * All value/pairs should be copied into the ability for direct access.
     *
     * @param ability Ability to configure the Special Parameter for.
     * @return True if sucessful, False if not sucessful.
     */
    public boolean configure(Ability ability);
    
    /** Configures the ability according to the parameters in parameterList.
     * The parameterList should be stored with the ability for configuration
     * later on. If an existing parameterList alread exists, it should be
     * replaced with this one.
     *
     * All value/pairs should be copied into the ability for direct access.
     * Advantages/Limitations should copy thier value/pairs into the
     * Advantage# and Limitation# indexed lists.
     *
     * This function does not receive an index because it
     * is assumed that either an exact match already exists in the ability or the
     * configureSpecial Parameter is going to create the special parameter entry.
     *
     * @param ability Ability to configure the Special Parameter for.
     * @param pl ParameterList to use for configuration parameters.
     * @return True if sucessful, False if not sucessful.
     */
    public boolean configure(Ability ability, ParameterList pl);
    
    /** Get the parameterList necessary to configure the Special Parameter for the ability.
     *
     * This parameterList is stored with the ability from this point forward, so
     * additional calls to getParameters will always return the same parameter list.
     * @param ability Ability to get parameterList from.
     * @param index Index of Special Parameter in parameterList.
     * @return ParameterList extracted from ability.
     */
    public ParameterList getParameterList(Ability ability, int index);
    
    /** Creates a new parameterList for Special Parameter based on values in DetailList
     *
     * @param detailList DetailList from which to build the new parameterList.
     * @param fromIndex Index of the Special Parameter in the existing DetailList from which the ParameterList is being built.
     * @return ParameterList build from DetailList.
     */
    public ParameterList createParameterList(DetailList detailList, int fromIndex);
    
    /** Returns a complete summary of how the Special Parameter is configured.
     *
     * @param ability Ability which the Special Parameter is attached to.
     * @param index Index of the Special Parameter in the Ability detaillist.
     * @return Complete summary of configuration.
     */
    public String getConfigSummary(Ability ability, int index);
    
    /** Check parameter changes before the occur.
     * This method is called just prior to a parameter being changed.  If
     * this method returns false, the changes will be discarded.
     * @param ability Ability which is being modified.
     * @param Special ParameterIndex Index of Special Parameter in ability.
     * @param key Key which is about to change.
     * @param value New Value for key.
     * @param oldValue Old Value of Key.
     * @return Whether to allow change.
     */
    public boolean checkParameter(Ability ability, int spIndex, String key, Object value, Object oldValue);
    
    /** Get the English name of the Special Parameter.
     * @return name of Special Parameter
     */
    public String getName();
    
    /** Called when a new source has been set for the ability.
     * Used to notify the power that a new source was set and any appropriate actions should be taken.
     * This is only guaranteed to be called when the source is changed AFTER configureSpecial Parameter was called.
     * If the Special Parameter needs to perform an Source initializtion, it should do it in the configureSpecial Parameter function
     * also.
     * @param oldSource Target which used to be the source of the Ability
     * @param newSource Target which is now the source of the Ability
     * @param ability Ability which was moved from oldSource to newSource.
     */
    public void abilitySourceSet(Ability ability, Target oldSource, Target newSource);
    
    /** Called when the ability's name is changed.
     * Used to notify the Special Parameter that the ability's name was changed and that appropriate actions should
     * be taken.
     * @param oldName String containing the old name
     * @param newName String containing the new name
     * @param ability Ability which change name from oldName to newName.
     */
    public void abilityNameSet(Ability ability, String oldName, String newName);
    
    /** Returns an array containing the information necessary to configure this Special Parameter.
     * @return Array used to create new ParameterList.
     */
    public Object[][] getParameterArray();
    
    /** Executed for all Special Parameters just prior to an ability being saved.
     * All clean up should be done at this point.
     */
    public void prepareToSave(Ability ability, int index);
    
    /** Returns the Icon representing the Special Parameter
     */
    public Icon getIcon();
    
    /** Removes Special Configurations from the Ability which the Special Parameter might have added.
     *
     * removeSpecialParameter is called prior to an Special Parameter being removed from an ability.  The
     * removeSpecialParameter method should remove any value pairs that it specifically added to the
     * ability.
     *
     * The ability will take care of removing the Special Parameter configuration, object, and parameter
     * lists under the SpecialParameter#.* value pairs.
     */
    public void remove(Ability ability, int index);
    
    /** Indicates only one instance of this advantage can exist for any given ability.
     */
    public boolean isUnique();
    
    /** Attempt to identify SpecialParameter 
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     * @param ai AbilityImport which is currently being imported.
     * @return Value indicating likelyhood that AbilityImport is this kind of power.
     * 0 indicates there was no recognition. 
     * 5 indicates probable match.
     * 10 indicates almost certain recognition.
     *
     */
    public int identifySpecialParameter(AbilityImport ai, int line);

    /** Attempt to Import SpecialParameter setting from AbilityImport information.
     * @param ability Ability into which to import.
     * @param ai AbilityImport from which to grab import information.
     */    
    public void importSpecialParameter(Ability ability, int index, AbilityImport ai, int line);
    
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
    
    /** Initializes the SpecialParameter when it is first loaded by the Simulator.
     *
     * The <code>initialize</code> method is gauranteed to be called at least once prior to  
     * any use of the SpecialParameter.  However, it is possible that the method would be called
     * multiple times.  If the initialization code can only be run one, the SpecialParameter should
     * track whether it has been initialized already.
     */
    public void initialize();
    
    /** Compares the configuration of SpecialParameters from two abilities.
     *
     * In order for the configuration of two SpecialParameters to be equal, the
     * SpecialParameters must be of the same type and their parameter lists must
     * be configured the same.
     *
     * <code>thisAbility<code> and <code>thatAbility</code> must both 
     * have this type of SpecialParameter at the indicated indexes.
     */
    public boolean compareConfiguration(Ability thisAbility, int thisIndex, Ability thatAbility, int thatIndex);
    
    public SpecialParameter clone();

}

