/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.*;
import champions.interfaces.*;
import champions.event.*;
import champions.exception.*;
import champions.parameters.ComboParameter;
import champions.parameters.ParameterList;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

import java.util.*;

/**
 *
 * @author  unknown
 * @version
 *
 * * To Convert from old format limitation, to new format limitation:
 *
 * 1) Add implements ChampionsConstants to class definition.<P>
 * 2) Copy and Fill in Limitation Definition Variables. <P>
 * 3) Move Parameter Information to parameterArray. <P>
 * 4) Delete getParameters method (unless special parameter handling is necessary.<P>
 * 5) Change configurePAD(Ability,DetailList) method to configurePAD(Ability ability, ParameterList parameterList).<P>
 * 6) Edit configurePAD method to use format specified below.<P>
 * 7) Change checkParameter method to checkParameter(Ability ability, <i>int padIndex</i>,
 * String key, Object value, Object oldValue);
 * 8) Edit getConfigSummary method to use parameterList instead of parseParameter methods.<P>
 * 9) Change all instances of parseParameter to getParameterValue.<P>
 * 10) Add getParameterArray method.<P>
 * 11) Add isUnique method.<P>
 * 12) Edit getName method to return limitationName variable.
 * 13) Add patterns array and define import patterns.<P>
 * 14) Add getImportPatterns() method.<P>
 */
public class limitationOnlyUpToAmountAbsorbed extends LimitationAdapter implements ChampionsConstants {
    static final long serialVersionUID = -6870520616682472305L;
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"AbsorptionAbility","Limitation#.ABSORBABILITY", Ability.class, null, "Absorption Ability", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}
    };
    
    // Limitation Definition Variables
    public static String limitationName = "Only Up To Amount Absorbed"; // The Name of the Limitation
    private static String limitationDescription = "Only Up To Amount Absorbed"; // The Description of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        //  { "(No Knockback).*", new Object[] { "NoKnockback", Boolean.class}}
    };
    
    /** Creates new advCombatModifier */
    public limitationOnlyUpToAmountAbsorbed() {
    }
    
    public String getName() {
        return limitationName;
    }
    
    public Object[][] getParameterArray() {
        return parameterArray;
    }
    
    public boolean isUnique() {
        return unique;
    }
    
    public boolean configurePAD(Ability ability, ParameterList parameterList) {
        // Fail immediately if ability is null
        if ( ability == null ) return false;
        Ability absorptionAbility = (Ability)parameterList.getParameterValue("AbsorptionAbility");
        
        // Read in any parameters that will be needed to configure the power or
        // Determine the validity of the power configuration.  Read the parameters
        // from the parameterList, instead of directly from the ability, since the
        // Ability isn't configured yet.
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        // !!!Limitation has nothing to validate!!!
        
        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        int index = ability.addLimitationInfo(this, limitationName, parameterList);
        
        // Copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.  Make sure that these
        // Value/Pairs are unique to the PAD.  Any that are not, should be copied
        // manually.
        // Note that if the advantage is not unique, the values should always be
        // accessed via the parameterList and not directly.
        //parameterList.copyValues(ability,index);
        
        // Add Extra Value/Pairs used by the Limitation/BattleEngine
        ability.add("Absorption.ONLYUPTOAMOUNTABSORBED", "TRUE", true, false);
        ability.add("Absorption.ABILITY", absorptionAbility, true, false);
        
        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
    }
    
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        return -.25;
    }
    
    public String getConfigSummary() {
        return "Only up to Amount Absorbed";
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
//    public int identifyLimitation(AbilityImport ai,int line) {
//        int index,count;
//        String possibleLimitation;
//        
//        possibleLimitation = ai.getImportLine(line);
//        if ( possibleLimitation != null && possibleLimitation.indexOf( "No Knockback:" ) != -1 ) {
//            return 10;
//        }
//        
//        return 0;
//    }
    
    
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
        return patterns;
    }
    
    /** Removes Special Configurations from the Ability which the limitation might have added.
     *
     * Removelimitation is called prior to an advantage being removed from an ability.  The
     * removelimitation method should remove any value pairs that it specifically added to the
     * ability.
     *
     * The ability will take care of removing the limitation's configuration, object, and parameter
     * lists under the limitation#.* value pairs.
     */
    public void removeLimitation() {
        ability.remove("Absorption.ONLYUPTOAMOUNTABSORBED");
        ability.remove("Absorption.ABILITY");
    }
    
    // Special Handling is necessary to configure the ability.
    public ParameterList getParameterList() {
        ParameterList pl = super.getParameterList();
        ComboParameter param = (ComboParameter)pl.getParameter("AbsorptionAbility");
        if ( param.getModel() == null && ability != null && ability.getSource() != null) {
            param.setModel(new AbsorptionModel(ability.getSource()));
        }
        return pl;
    }
    
    public void abilitySourceSet(Ability ability, Target oldSource, Target newSource) {
        ParameterList pl = super.getParameterList();
        ComboParameter param = (ComboParameter)pl.getParameter("AbsorptionAbility");
        param.setModel( new AbsorptionModel(newSource)); 
    }
    
    /** Executed for all PADs just prior to an ability being saved.
     * All clean up should be done at this point.
     */
//    public void prepareToSave() {
//        // Make sure you clean up the CanUseCL model, since it will contain pointers to the world and
//        // will cause havoc when reloaded.
//        ParameterList pl = getParameterList();
//        AbsorptionModel am = (AbsorptionModel)pl.getParameterOption("AbsorptionAbility","MODEL");
//        if ( am != null) {
//            pl.setParameterOption("AbsorptionAbility","MODEL", null);
//            am.setSource(null);
//        }
//    }
    
    public class AbsorptionModel extends DefaultComboBoxModel
    implements ChangeListener {

        /** Holds value of property source. */
        private Target source;
        private Vector abilityList;
        
        public AbsorptionModel(Target source) {
            abilityList = new Vector();
            setSource(source);
        }
        
        /**
         * Returns the length of the list.
         */
        public int getSize() {
            return abilityList.size();
        }
        
        /**
         * Returns the value at the specified index.
         */
        public Object getElementAt(int index) {
            return abilityList.get(index);
        }
        
        /** Getter for property source.
         * @return Value of property source.
         */
        public Target getSource() {
            return source;
        }
        
        /** Setter for property source.
         * @param source New value of property source.
         */
        public void setSource(Target source) {
            if ( this.source != source ) {
                if ( this.source != null ) {
                    this.source.getAbilityList().removeChangeListener(this);
                }
                this.source = source;
                if ( this.source != null ) {
                    this.source.getAbilityList().addChangeListener(this);
                }
                buildAbilityList();
            }
        }
        
        /**
         * Invoked when the target of the listener has changed its state.
         *
         * @param e  a ChangeEvent object
         */
        public void stateChanged(ChangeEvent e) {
            buildAbilityList();
        }
        
        private void buildAbilityList() {
            abilityList.clear();
            
            if ( source != null ) {
                AbilityIterator ai = source.getAbilities();
                while ( ai.hasNext() ) {
                    Ability a = ai.nextAbility();
                    if ( a.getPower() instanceof powerAbsorption ) {
                        abilityList.add( a );
                    }
                }
            }
            
            fireContentsChanged(this, -1, -1);
        }
   }
}