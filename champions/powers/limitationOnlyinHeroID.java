/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.LimitationAdapter;
import champions.parameters.ParameterList;
import champions.Target;
import champions.interfaces.ChampionsConstants;
import champions.parameters.ComboParameter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;



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
public class limitationOnlyinHeroID extends LimitationAdapter implements ChampionsConstants {
    static final long serialVersionUID = -6870520616682472305L;
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    
    static private Object[][] parameterArray = {
        {"HeroIDName","Power.HEROIDNAME", String.class, powerIHID.NO_ID_STRING, "Hero ID", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}
    };
    
    // Limitation Definition Variables
    public static String limitationName = "Only In Heroic Identity"; // The Name of the Limitation
    private static String limitationDescription = "Only In Heroic Identity"; // The Description of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "(Only in Hero ID:).*", null},
        { "Hero ID: (.*)", new Object[] { "HeroIDName", String.class}},
        { "Only In Heroic Identity \\((.*)\\).*", new Object[] { "HeroIDName", String.class}},
        //hd
        { "OIHID.*", null },
        
    };
    
    
    /** Creates new advCombatModifier */
    public limitationOnlyinHeroID() {
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
        
        // Read in any parameters that will be needed to configure the power or
        // Determine the validity of the power configuration.  Read the parameters
        // from the parameterList, instead of directly from the ability, since the
        // Ability isn't configured yet.
        String heroID = (String)parameterList.getParameterValue("HeroIDName");
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        // !!!Limitation has nothing to validate!!!
        
        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        int index = ability.addLimitationInfo(this, limitationName, parameterList);
        
        //This allows normally on to be put false
        //ability.add("Ability.NORMALLYON", "FALSE", true);
        
        // Copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.  Make sure that these
        // Value/Pairs are unique to the PAD.  Any that are not, should be copied
        // manually.
        // Note that if the advantage is not unique, the values should always be
        // accessed via the parameterList and not directly.
        //parameterList.copyValues(ability,index);
        
        // Add Extra Value/Pairs used by the Limitation/BattleEngine
        Target source = ability.getSource();
        if ( source != null ) {
            if ( heroID == null || heroID.equals(powerIHID.NO_ID_STRING)) {
                heroID = powerIHID.getValidHeroID(source);
                parameterList.setParameterValue("HeroIDName", heroID);
            }
            else {
                powerIHID.addHeroIDToSource(heroID, null, source);
            }
        }
        
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
        return "Only in Hero ID";
    }
    
    public boolean isEnabled(Target source) {
        
        ParameterList parameterList = getParameterList();
        String heroidname = (String)parameterList.getParameterValue("HeroIDName");
        int hindex;
        Ability heroIDAbility;
        
        if ( source != null ) {
            hindex = source.findIndexed( "HeroID", "NAME", heroidname);
            if ( hindex != -1 ) {
                heroIDAbility = (Ability)source.getIndexedValue(hindex, "HeroID", "ABILITY");
                if ( heroIDAbility == null ) {
                    ability.setEnableMessage( "Character doesn't have appropiate Hero ID: " + heroidname);
                    return false;
                }
                if ( heroIDAbility.isActivated(source) ) {
                    return true;
                }
                else {
                    ability.setEnableMessage( "Hero ID '" + heroidname + "' isn't activated.");
                    return false;
                }
            }
            else {
                ability.setEnableMessage( "Character doesn't have appropiate Hero ID: " + heroidname);
                return false;
            }
        }
        else {
            ability.setEnableMessage( "Source not set.");
            return false;
        }
        
        
    /*    count = source.getIndexedSize( "ActivationInfo" );
        for ( index2 = 0; index2 < count; index2++ ) {
            ActivationInfo ai2 = (ActivationInfo)source.getIndexedValue(index2, "ActivationInfo","ACTIVATIONINFO" );
            if ( ai2 != null && ai2.isActivated()) {
     
                if ( ai2.getIndexedSize( "Ability" ) > 0 && (ability = (Ability)ai2.getIndexedValue(0,"Ability","ABILITY") ) != null ) {
                   if ( ability.getPower() instanceof powerIHID ) {
     
                        if (ability.getStringValue("Power.HEROIDNAME").equals(heroidname) ) {
     
                          //  System.out.println("found HID");
                            return true;
                        }
                    }
                }
            }
        }
        return false; */
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

        //removed due to limitation in 5e MC getting read as a power when I had both lines below installed
//        if ( possibleLimitation != null && possibleLimitation.indexOf( "Only in Hero ID:" ) != -1 ) {
//            return 10;
//        }
        
        if ( possibleLimitation != null && (possibleLimitation.indexOf( "OIHID" ) != -1 || possibleLimitation.indexOf( "Only In Heroic Identity" ) != -1) ) {
            return 10;
        }
        
        return 0;
    }
    
    // Special Handling is necessary to configure the ability.
    public ParameterList getParameterList() {
        ParameterList pl = super.getParameterList();
        ComboParameter param = (ComboParameter)pl.getParameter("HeroIDName");
        if ( param.getModel() == null) {
            limitationOnlyinHeroID.HeroIDModel hidm = new HeroIDModel();
            //jeff 
            if(ability!=null) {
            	hidm.setSource(ability.getSource());
            }
            param.setModel(hidm);
        }
        return pl;
    }
    
    public void abilitySourceSet(Ability ability, Target oldSource, Target newSource) {
        ParameterList pl = super.getParameterList();
        
        // Make sure we setup the HeroID when the source is setup...
        String heroID = (String)pl.getParameterValue("HeroIDName");
        if ( newSource != null ) {
            if ( heroID == null || heroID.equals(powerIHID.NO_ID_STRING)) {
                heroID = powerIHID.getValidHeroID(newSource);
                pl.setParameterValue("HeroIDName", heroID);
            }
            else {
                powerIHID.addHeroIDToSource(heroID, null, newSource);
            }
        }
        
        // Fix up the Selection Model
        ComboParameter param = (ComboParameter)pl.getParameter("HeroIDName");
        if ( param.getModel() == null) {
            limitationOnlyinHeroID.HeroIDModel hidm = new HeroIDModel();
            hidm.setSource(newSource);
            param.setModel(hidm);
        }
    }
    
    /** Executed for all PADs just prior to an ability being saved.
     * All clean up should be done at this point.
     */
//    public void prepareToSave() {
//        // Make sure you clean up the CanUseCL model, since it will contain pointers to the world and
//        // will cause havoc when reloaded.
//        ParameterList pl = getParameterList();
//        if ( pl.getParameterOption("HeroIDName","MODEL") != null) {
//            pl.setParameterOption("HeroIDName","MODEL", null);
//        }
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
    
    public class HeroIDModel extends AbstractListModel implements ComboBoxModel, PropertyChangeListener {
        
        private Object selectedItem = null;
        
        private Target source = null;
        /** Creates new ENDSourceList */
        public HeroIDModel() {
            
        }
        
        public int getSize() {
            int size = 0;
            if ( source != null ) {
                size = source.getIndexedSize( "HeroID" );
            }
            
            return (size == 0) ? 1 : size;
        }
        
        /** Returns the value at the specified index.
         */
        public Object getElementAt(int index) {
            
            if ( source == null || source.getIndexedSize( "HeroID" ) == 0) return powerIHID.NO_ID_STRING;
            
            return source.getIndexedValue( index, "HeroID", "NAME" );
            
        }
        
        /** This method gets called when a bound property is changed.
         * @param evt A PropertyChangeEvent object describing the event source
         *  	and the property that has changed.
         */
        public void propertyChange(PropertyChangeEvent evt) {
            if ( evt.getPropertyName().startsWith("HeroID") ) {
                //if ( source.findExactIndexed( "HeroID","NAME", selectedItem) == -1 ) {
                //   selectedItem = getElementAt(0);
                //}
                
                fireContentsChanged(this,0,getSize());
            }
        }
        
        public void setSelectedItem(java.lang.Object item) {
            selectedItem = item;
            
        }
        
        public java.lang.Object getSelectedItem() {
            return selectedItem;
        }
        
        public Target getSource() {
            return source;
        }
        
        public void setSource(Target source) {
            if ( this.source != null ) {
                this.source.removePropertyChangeListener(this);
            }
            
            this.source = source;
            fireContentsChanged(this,0,getSize());
            
            if ( source != null ) {
                source.addPropertyChangeListener(this);
            }
        }
        
        public String toString() {
            StringBuffer sb = new StringBuffer();
            
            if ( source != null ) {
                sb.append( "Source: " );
                sb.append( source.toString() );
                
                sb.append( " Size: " );
                sb.append( Integer.toString(getSize()) );
                
                sb.append( " Items: " );
                int index, count;
                count = getSize();
                for(index=0;index<count;index++) {
                    sb.append( getElementAt(index) );
                    if ( index+1 < count ) sb.append( ", ");
                }
            }
            else {
                sb.append( "Source: null" );
            }
            return sb.toString();
        }
    }
}