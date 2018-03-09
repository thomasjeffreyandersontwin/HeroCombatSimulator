/*
 * LimitationAdapter.java
 *
 * Created on October 4, 2000, 10:36 AM
 */

package champions;

import champions.attackTree.AttackTreeNode;
import champions.exception.BattleEventException;
import tjava.Filter;
import champions.interfaces.Limitation;
import champions.parameters.BooleanParameter;
import champions.parameters.ParameterList;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;


/**
 *
 * @author  unknown
 * @version
 */
public abstract class LimitationAdapter implements Limitation, Serializable {
    static final long serialVersionUID = -5479433443213725896L;
    
    public static Icon genericLimitationIcon = null;
   
    protected Ability ability = null;
    protected boolean addedByFramework = false;
    protected String description = "";
    protected double multiplier = 0;
    protected int priority = 2;
    //protected boolean privateLimitation = false;
    protected ParameterList parameterList = null;
    
    /** Import Finalizaer. 
     *
     *  Provides an arbitrary object to use during import from other formats.
     */
    private transient Object finalizer = null;
    
    /** Creates new LimitationAdapter */
    public LimitationAdapter() {
        if ( genericLimitationIcon == null ) genericLimitationIcon = UIManager.getIcon("Limitation.DefaultIcon");
    }
    
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
    public boolean configurePAD(Ability ability, ParameterList pl) {
        return false;
    }
    
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
    public boolean configurePAD(Ability ability, DetailList detailList, int fromIndex) {
        
        ParameterList pl = createParameterList(detailList, fromIndex);
        
        return configurePAD(ability, pl);
    }
    
    /** Notifies the limitation that the Ability has been shutdown.
     *
     * This is called during the Ability deactivation process.
     */
    public final void shutdownAbility(BattleEvent be, int index, Ability ability, Target source) throws BattleEventException {
        shutdownAbility(be, source);
    }
    
    public void shutdownAbility(BattleEvent be, Target source) throws BattleEventException {
        
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
//        Object pl = null;
//        if (padIndex != -1 ) {
//            //pl = ability.getIndexedValue(padIndex,"Limitation","PARAMETERLIST");
//            pl = ability.getLimitationParameterList(padIndex);
//        }
        
//        if ( parameterList == null ) {
//            parameterList = createParameterList(ability, padIndex);
//        }
//        
//        if ( padIndex != -1 ) setParameterList(ability, padIndex, parameterList);
        
        return getParameterList();
    }
    
     /** Get the parameterList necessary to configure the PAD for the ability.
     *
     * This parameterList is stored with the ability from this point forward, so
     * additional calls to getParameters will always return the same parameter list.
     * Note: If the advantage is not defined in the power already, a default parameterList
     * will be generated.
     * @param ability Ability to get parameterList from.
     * @return ParameterList extracted from ability.
 */
    public final ParameterList getParameterList(Ability ability) {
        //int padIndex = ability.findExactIndexed("Limitation","LIMITATION",this);
//        int padIndex = ability.findExactLimitation(this);
//        return getParameterList(ability,padIndex);
        return getParameterList();
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
        pl.addParameter( new BooleanParameter("Private", "Limitation.PRIVATE", "Private to framework", false, true, false, false) );
        return pl;
    }
    
     /** Stores the parameterList with the ability for use later.
      */
    public final void setParameterList(Ability ability, int index, ParameterList pl) {
//        if ( index  != -1 ) {
//            //ability.addIndexed(index,"Limitation","PARAMETERLIST", pl, true);
//            ability.setLimitationParameterList(index, pl);
//        }
        setParameterList(pl);
    }
    
/** Returns a complete summary of how the PAD is configured.
 * @param ability Ability which the PAD is attached to.
 * @param index Index of the PAD in the Ability detaillist.
 * @return Complete summary of configuration.
 */
    
    public final String getConfigSummary( Ability ability, int index){
        //return "No Configurable Parameters";
        return getConfigSummary();
    }
    
    public abstract String getConfigSummary();
    
    public final double calculateMultiplier(Ability ability, int index) {
        return calculateMultiplier();
    }
    
    public abstract double calculateMultiplier();
    
    
    /** Removes Special Configurations from the Ability which the limitation might have added.
     *
     * Removelimitation is called prior to an advantage being removed from an ability.  The
     * removelimitation method should remove any value pairs that it specifically added to the
     * ability.  
     *
     * The ability will take care of removing the limitation's configuration, object, and parameter
     * lists under the limitation#.* value pairs.
     */
    public final void removeLimitation(Ability ability, int index) {
        removeLimitation();
    }
    
    public void removeLimitation() {
        
    }
    
    public String getName() {
        return this.getClass().toString();
    }
    
    public final boolean checkParameter(Ability ability, int padIndex, String key, Object value, Object oldValue) {
        return checkParameter(key,value,oldValue);
    }
    
    public boolean checkParameter(String key, Object value, Object oldValue) {
        return true;
    }
    
    public final void postpower(BattleEvent be, int index, Ability ability, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget) throws BattleEventException {
        postpower(be,effect,target,targetReferenceNumber,targetGroup,hitLocationForDamage,finalTarget);
    }
    
    public void postpower(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget) throws BattleEventException {
    
    }
    
    public final AttackTreeNode preactivate(BattleEvent be, int index, Ability ability) {
        return preactivate(be);
    }
    
    public AttackTreeNode preactivate(BattleEvent be) {
        return null;
    }
    
    public final void prepower(BattleEvent be, int index, Ability ability, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget) throws BattleEventException {
        prepower(be,effect,target,targetReferenceNumber,targetGroup,hitLocationForDamage,finalTarget);
    }
    
    public void prepower(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget) throws BattleEventException {
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
    
//    public int getLimitationIndex(DetailList detailList) {
//        return detailList.findExactIndexed("Limitation","LIMITATION",this);
//    }
    
    public Icon getIcon() {
        
        return genericLimitationIcon;
    }
    
     /** Executed for all PADs just prior to an ability being saved.
      * All clean up should be done at this point.
      */
    public final void prepareToSave(Ability ability, int index) {
        prepareToSave();
    }
    
    public void prepareToSave() {
        
    }
    
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
    public int identifyLimitation(AbilityImport ai,int line) {
        int index,count;
        String possibleLimitation;
        
        possibleLimitation = ai.getImportLine(line);
        if ( possibleLimitation != null && possibleLimitation.indexOf( getName() + ":" ) != -1 ) {
            return 10;
        }

        return 0;
    }
    
    /** Attempt to Import Advantage setting from AbilityImport information.
     * @param limitationIndex Index of the new Limitation.
     * @param line Line index which contained the first recognized line for this ability.
     * @param ability Ability into which to import.
     * @param ai AbilityImport from which to grab import information.
     */
    public void importLimitation(Ability ability,int limitationIndex,AbilityImport ai, int line) {
        int index, count;
        Object[][] patterns;
        String pattern;
        Object[] parameters;
        
        ParameterList parameterList = this.getParameterList(ability, limitationIndex);
        
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
     * { "PATTERN" , new Object[] { "PARAMETER1", parameter1.class, "PARAMETER2", parameter2.class, ... },
     * ...
     * }
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
    
    /** Returns whether an object equals an limitation.
     *
     */
    public boolean equals(Object that) {
        if ( that == null ) return false;
        if ( this == that ) return true;
        if ( this.getClass().equals( that.getClass() ) == false ) return false;
        
        ParameterList thisPL = this.getParameterList();
        ParameterList thatPL = ((Limitation)that).getParameterList();
        
        return thisPL != null && thisPL.isRelated(thatPL);
    }
    
    /* Returns whether an ability with limitation is enabled.
     * Returns TRUE if the ability is enabled, or FALSE if the ability is disabled
     * specifically because of this limitation.
     *
     * If the ability is not enabled, the ability.setEnableMessage method should 
     * be used to set the message that is displayed in the popup tooltip when the
     * mouse is held over the disabled ability.
     */
    public final boolean isEnabled(Ability ability, int index, Target source) {
        return isEnabled(source);
    }
    
    public boolean isEnabled(Target source) {
        return true;
    }
    
    /** Provides hook to add menu items to the Ability right-click menu
     * @param menu
     * @param ability
     * @return true if menus where actually added.
     */
    public final boolean invokeMenu(JPopupMenu menu, Ability ability, int index) {
        return invokeMenu(menu);
    }
    
    public boolean invokeMenu(JPopupMenu menu) {
        return false;
    }
    
    /** Initializes the Limitation when it is first loaded by the Simulator.
     *
     * The <code>initialize</code> method is gauranteed to be called at least once prior to  
     * any use of the Limitation.  However, it is possible that the method would be called
     * multiple times.  If the initialization code can only be run one, the limitation should
     * track whether it has been initialized already.
     */
    public void initialize() {
        
    }
    
    public boolean finalizeImport(Ability ability, int index, AbilityImport ai) {
        return false;
    }
    
    /** Compares the configuration of Limitations from two abilities.
     *
     * In order for the configuration of two Limitations to be equal, the
     * Limitations must be of the same type and their parameter lists must
     * be configured the same.
     *
     * <code>thisAbility<code> and <code>thatAbility</code> must both 
     * have this type of Limitation at the indicated indexes.
     */
    public boolean compareConfiguration(Ability thisAbility, int thisIndex, Ability thatAbility, int thatIndex) {
        return compareConfiguration(thatAbility, thatIndex);
    }
    
    public boolean compareConfiguration(Ability thatAbility, int thatIndex) {
        if ( thatAbility == null ) return false;
        
        Limitation that = thatAbility.getLimitation(thatIndex);
        
        return compareConfiguration(that);
    }
    
    public boolean compareConfiguration(Limitation that) {
        if ( this.getClass().equals( that.getClass() ) == false ) return false;
        
        return this.getParameterList().compareConfiguration( that.getParameterList( ) );
        
    }
    
    public final void addActions(java.util.Vector v, Ability ability, int index) {
        addActions(v);
    }
    
    public void addActions(Vector v) {
        
    }
    
    /** Returns a Filter that accepts only eligible targets.
     *
     * Returns null if all targets are acceptible.
     */
    public Filter<Target> getTargetFilter(Ability ability, int index) {
        return getTargetFilter();
    }
    
    public Filter<Target> getTargetFilter() {
        return null;
    }

    public Ability getAbility() {
        return ability;
    }

    public void setAbility(Ability ability) {
        if ( this.ability != ability ) {
            if ( this.ability != null && ability != null) {
                throw new RuntimeException("Limitation already assigned to ability");
            }
            
            this.ability = ability;
        }
    }

    public boolean isAddedByFramework() {
        return addedByFramework;
    }

    public void setAddedByFramework(boolean addedByFramework) {
        this.addedByFramework = addedByFramework;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if ( this.description != description ) {
        
            this.description = description;
            
            if ( ability != null ) {
                ability.updateAbilityDescription();
            }
        }
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isPrivate() {
        return getParameterList().getParameterBooleanValue("Private");
    }

    public void setPrivate(boolean privateLimitation) {
        getParameterList().setParameterValue("Private", privateLimitation);
    }

    public ParameterList getParameterList() {
        if ( parameterList == null ) {
            parameterList = createParameterList(null, -1);
        }
        
        return parameterList;
    }

    public void setParameterList(ParameterList parameterList) {
        this.parameterList = parameterList;
    }
    
    /** Creates a clone of the limitation.
     *
     * The clone will not propogate the ability of parameterList of this limitation.
     */
    public Limitation clone() {
        LimitationAdapter that;
        try {
            Constructor c = getClass().getConstructor();
            that = (LimitationAdapter) c.newInstance();
            
            that.ability = null;

            that.addedByFramework = addedByFramework;
            that.description = description;
            that.multiplier = multiplier;
            that.priority = priority;
            //that.privateLimitation = privateLimitation;
            that.setPrivate( this.isPrivate() );
            
            that.parameterList = parameterList.clone();
            
            return that;
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public Object getFinalizer() {
        return finalizer;
    }

    public void setFinalizer(Object finalizer) {
        this.finalizer = finalizer;
    }
    
}