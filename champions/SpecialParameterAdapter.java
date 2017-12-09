/*
 * SpecialParameterAdapter.java
 *
 * Created on October 2, 2000, 10:03 PM
 */

package champions;

import champions.interfaces.SpecialParameter;
import champions.parameters.ParameterList;
import javax.swing.Icon;
import javax.swing.UIManager;


/**
 *
 * @author  unknown
 * @version
 */
public abstract class SpecialParameterAdapter extends Object implements SpecialParameter, java.io.Serializable, Cloneable {
    static final long serialVersionUID = -7057960825944600560L;
    
    public static Icon genericSpecialParameterIcon = null;
    /** Creates new SpecialParameterAdapter */
    public SpecialParameterAdapter() {
        if ( genericSpecialParameterIcon == null ) genericSpecialParameterIcon = UIManager.getIcon("SpecialParameter.DefaultIcon");
        
    }
    
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
    public boolean configure(Ability ability) {
        ParameterList pl = this.createParameterList(null, -1);
        return configure(ability, pl);
    }
    
    /** Configures the ability according to the parameters in parameterList.
     * The parameterList should be stored with the ability for configuration
     * later on. If an existing parameterList alread exists, it should be
     * replaced with this one.
     *
     * All value/pairs should be copied into the ability for direct access.
     * SpecialParameters/Limitations should copy thier value/pairs into the
     * SpecialParameter# and Limitation# indexed lists.
     *
     * This function does not receive an index for specialParameters and limitations because it
     * is assumed that either an exact match already exists in the ability or the
     * configurePAD is going to create the specialParameter entry.
     * @param ability Ability to configure the PAD for.
     * @param pl ParameterList to use for configuration parameters.
     * @return True if sucessful, False if not sucessful.
     */
    public boolean configurePAD(Ability ability, ParameterList pl) {
        return false;
    }
    
    /** Removes Special Configurations from the Ability which the specialParameter might have added.
     *
     * RemoveSpecialParameter is called prior to an specialParameter being removed from an ability.  The
     * removeSpecialParameter method should remove any value pairs that it specifically added to the
     * ability.  
     *
     * The ability will take care of removing the specialParameters configuration, object, and parameter
     * lists under the specialParameter#.* value pairs.
     */
    public void remove(Ability ability, int index) {
    }
    
    /** Get the parameterList necessary to configure the PAD for the ability.
     *
     * This parameterList is stored with the ability from this point forward, so
     * additional calls to getParameters will always return the same parameter list.
     * @param ability Ability to get parameterList from.
     * @param padIndex Index of PAD in parameterList.
     * @return ParameterList extracted from ability.
     */
    public ParameterList getParameterList(Ability ability, int padIndex) {
        int index;
        Object pl = null;
        if (padIndex != -1 ) {
            pl = ability.getIndexedValue(padIndex,"SpecialParameter","PARAMETERLIST");
        }
        
        if ( pl == null ) {
            pl = createParameterList(ability, padIndex);
        }
        
        if ( padIndex != -1 ) setParameterList(ability, padIndex, (ParameterList)pl);
        
        return (pl != null ) ? (ParameterList)pl : null;
    }
    
    /** Get the parameterList necessary to configure the PAD for the ability.
    *
    * This parameterList is stored with the ability from this point forward, so
    * additional calls to getParameters will always return the same parameter list.
    * Note: If the specialParameter is not defined in the power already, a default parameterList
    * will be generated.
    * @param ability Ability to get parameterList from.
    * @return ParameterList extracted from ability.
    */
    public ParameterList getParameterList(Ability ability) {
        int padIndex = ability.findExactIndexed("SpecialParameter","SPECIALPARAMETER",this);
        return getParameterList(ability,padIndex);
    }
    
    /** Creates a new parameterList for PAD based on values in DetailList
     * @param detailList DetailList from which to build the new parameterList.
     * @param fromIndex Index of the PAD in the existing DetailList from which the ParameterList is being built.
     * @return ParameterList build from DetailList.
     */
    public ParameterList createParameterList(DetailList detailList, int fromIndex) {
        ParameterList pl = null;
        if ( getParameterArray() == null ) {
            pl = new ParameterList();
        }
        else {
            pl = new ParameterList(getParameterArray(), detailList, fromIndex);
        }
        return pl;
    }
    
    /** Stores the parameterList with the ability for use later.
     */
    public void setParameterList(Ability ability, int index, ParameterList pl) {
        if ( index  != -1 ) {
            ability.addIndexed(index,"SpecialParameter","PARAMETERLIST", pl, true);
        }
    }
    
    public String getConfigSummary(Ability ability, int index) {
        return "No Configurable Parameters";
    }
    
    public String getName() {
        return getClass().getName();
    }
    
    public boolean checkParameter(Ability ability, int padIndex, String key, Object value, Object oldValue) {
        return true;
    }
    
    /** Called when a new source has been set for the ability.
     * Used to notify the power that a new source was set and any appropriate actions should be taken.
     * @param source New source of the Ability.
     */
    public void abilitySourceSet(Ability ability, Target oldSource,Target newSource) {
        
    }
    
     /** Called when the ability's name is changed.
      * Used to notify the PAD that the ability's name was changed and that appropriate actions should
      * be taken.
      * @param oldName String containing the old name
      * @param newName String containing the new name
      */
    public void abilityNameSet(Ability ability, String oldName, String newName) {
        
    }

    
    public int getSpecialParameterIndex(DetailList detailList) {
        return detailList.findExactIndexed("SpecialParameter","SPECIALPARAMETER",this);
    }
    
    public Icon getIcon() {
        return genericSpecialParameterIcon;
    }
    
    /** Executed for all PADs just prior to an ability being saved.
     * All clean up should be done at this point.
     */
    public void prepareToSave(Ability ability, int index) {
        
    }
    
    /** Returns whether an object equals an specialParameter.
     * SpecialParameters use weak equals comparators, ie. the only necessity for
     * equality is that the objects are of the same class.
     */
    public boolean equals(Object that) {
        return this == that || ( this.getClass().isInstance(that) );
    }
    
    /** Indicates only one instance of this advantage can exist for any given ability.
     */
    public boolean isUnique() {
        return true;
    }
    
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
    public int identifySpecialParameter(AbilityImport ai, int line) {
        int index,count;
        String possibleSpecialParameter;
        
        possibleSpecialParameter = ai.getImportLine(line);
        if ( possibleSpecialParameter != null && possibleSpecialParameter.indexOf( getName() + ":" ) != -1 ) {
            return 10;
        }

        return 0;
    }

    /** Attempt to Import SpecialParameter setting from AbilityImport information.
     * @param ability Ability into which to import.
     * @param ai AbilityImport from which to grab import information.
     */    
    public void importSpecialParameter(Ability ability, int specialParameterIndex, AbilityImport ai, int line) {
        int index, count;
        Object[][] patterns;
        String pattern;
        Object[] parameters;
        
        ParameterList parameterList = this.getParameterList(ability, specialParameterIndex);
        
        patterns = getImportPatterns();
        if ( patterns != null ) {
            count = patterns.length;
            for (index=0;index<count;index++) {
                pattern = (String)patterns[index][0];
                parameters = (Object[])patterns[index][1];
                
                ai.searchForParameters( pattern, parameters, ability, this, parameterList);
            }
        }
    }
    
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
    public Object[][] getImportPatterns() {
        return null;
    }
    
    /** Initializes the SpecialParameter when it is first loaded by the Simulator.
     *
     * The <code>initialize</code> method is gauranteed to be called at least once prior to
     * any use of the SpecialParameter.  However, it is possible that the method would be called
     * multiple times.  If the initialization code can only be run one, the SpecialParameter should
     * track whether it has been initialized already.
     */
    public void initialize() {
    }
    
    public boolean finalizeImport(Ability ability, int index, AbilityImport ai) {
        return false;
    }
    
    public SpecialParameterAdapter clone() {
        return this;
    }
    
    /** Compares the configuration of SpecialParameters from two abilities.
     *
     * In order for the configuration of two SpecialParameters to be equal, the
     * SpecialParameters must be of the same type and their parameter lists must
     * be configured the same.
     *
     * <code>thisAbility<code> and <code>thatAbility</code> must both 
     * have this type of SpecialParameter at the indicated indexes.
     */
    public boolean compareConfiguration(Ability thisAbility, int thisIndex, Ability thatAbility, int thatIndex) {
        if ( thisAbility == null || thatAbility == null ) return false;
        if ( this.equals( thisAbility.getSpecialParameter(thisIndex) ) == false ) return false;
        if ( this.equals( thatAbility.getSpecialParameter(thatIndex) ) == false ) return false;
        
        return getParameterList(thisAbility, thisIndex).compareConfiguration( getParameterList(thatAbility, thatIndex) );
    }
    
}
