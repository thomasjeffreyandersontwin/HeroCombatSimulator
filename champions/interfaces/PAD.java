/*
 * PAD.java
 *
 * Created on October 12, 2000, 10:41 PM
 */

package champions.interfaces;

import champions.Ability;
import champions.DetailList;
import champions.Target;
import champions.parameters.ParameterList;
import java.io.Serializable;
import javax.swing.Icon;



/** A PAD is a class which is either a Ability Power, Advantage, or Limitation.
 * All Powers, Advantages, and Limitations should implement PAD.  In general,
 * Powers, Advantages, and Limitations should be derived from the appropriate
 * adapter class in order to get all additional functionality the adapter classes
 * provide.
 *
 * @author unknown
 * @version
 */
public interface PAD extends Serializable {
    /** Configures the ability according to the parameters in parameterList.
     * The parameterList should be stored with the ability for configuration
     * later on. If an existing parameterList alread exists, it should be
     * replaced with this one.
     *
     * All value/pairs should be copied into the ability for direct access.
     * Advantages/Limitations should copy thier value/pairs into the
     * Advantage# and Limitation# indexed lists.
     *
     * This function does not receive an index for advantages and limitations because it
     * is assumed that either an exact match already exists in the ability or the 
     * configurePAD is going to create the advantage entry.
     * @param ability Ability to configure the PAD for.
     * @param pl ParameterList to use for configuration parameters.
     * @return True if sucessful, False if not sucessful.
 */
    public boolean configurePAD(Ability ability, ParameterList pl);
    
     /** Configures the ability according to the value/pairs listed in detailList.
      * A new parameterList should be created via getParameters and stored appropriately.
      *
      * This method should rely on configurePAD(Ability, ParameterList) to do all configure.
      * All this method should do is create a ParameterList from the detailList and call
      * configurePAD(Ability, ParameterList) with the new parameter list.
      * @return True if sucessful, False if failure.
      * @param ability Ability which requires configuration.
      * @param detailList DetailList from which a new ParameterList should be build.
      * @param fromIndex Index of the PAD in the existing DetailList from which the ParameterList is being built.
 */
    public boolean configurePAD(Ability ability, DetailList detailList, int fromIndex);
    
    /** Get the parameterList necessary to configure the PAD for the ability.
     *
     * This parameterList is stored with the ability from this point forward, so
     * additional calls to getParameters will always return the same parameter list.
     * @param ability Ability to get parameterList from.
     * @param index Index of PAD in parameterList.
     * @return ParameterList extracted from ability.
 */
    public ParameterList getParameterList(Ability ability, int index);
    
    /** Creates a new parameterList for PAD based on values in DetailList
     * @param detailList DetailList from which to build the new parameterList.
     * @param fromIndex Index of the PAD in the existing DetailList from which the ParameterList is being built.
     * @return ParameterList build from DetailList.
 */
    public ParameterList createParameterList(DetailList detailList, int fromIndex);
    
/** Returns a complete summary of how the PAD is configured.
 * @param ability Ability which the PAD is attached to.
 * @param index Index of the PAD in the Ability detaillist.
 * @return Complete summary of configuration.
 */
    public String getConfigSummary( Ability ability, int index);
    
/** Check parameter changes before the occur.
 * This method is called just prior to a parameter being changed.  If
 * this method returns false, the changes will be discarded.
 * @param ability Ability which is being modified.
 * @param padIndex Index of PAD in ability.
 * @param key Key which is about to change.
 * @param value New Value for key.
 * @param oldValue Old Value of Key.
 * @return Whether to allow change.
 */
    public boolean checkParameter(Ability ability, int padIndex, String key, Object value, Object oldValue);
    
/** Get the English name of the PAD.
 * @return name of PAD
 */
    public String getName();
    
/** Called when a new source has been set for the ability.
 * Used to notify the power that a new source was set and any appropriate actions should be taken.
 * This is only guaranteed to be called when the source is changed AFTER configurePAD was called.
 * If the PAD needs to perform an Source initializtion, it should do it in the configurePAD function
 * also.
 * @param oldSource Target which used to be the source of the Ability
 * @param newSource Target which is now the source of the Ability
 * @param ability Ability which was moved from oldSource to newSource.
 */
    public void abilitySourceSet(Ability ability, Target oldSource, Target newSource);
    
/** Called when the ability's name is changed.
 * Used to notify the PAD that the ability's name was changed and that appropriate actions should
 * be taken.
 * @param oldName String containing the old name
 * @param newName String containing the new name
 * @param ability Ability which change name from oldName to newName.
 */
    public void abilityNameSet(Ability ability, String oldName, String newName);
    
/** Returns an array containing the information necessary to configure this PAD.
 * @return Array used to create new ParameterList.
 */    
    public Object[][] getParameterArray();
    
    /** Executed for all PADs just prior to an ability being saved.
     * All clean up should be done at this point.
     */
    public void prepareToSave(Ability ability, int index);
    
    /** Returns the Icon representing the Power/Advantage/Limitation 
     */
    public Icon getIcon();
}
