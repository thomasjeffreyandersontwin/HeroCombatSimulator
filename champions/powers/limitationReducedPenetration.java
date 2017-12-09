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
import champions.parameters.ParameterList;

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
 */
public class limitationReducedPenetration extends LimitationAdapter 
implements ChampionsConstants {
    static final long serialVersionUID = -6870320616782472305L;

    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
    };

    // Limitation Definition Variables
    public static String limitationName = "Reduced Penetration"; // The Name of the Limitation
    private static String limitationDescription = "Reduced Penetration"; // The Description of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability

        // Import Patterns Definitions
    private static Object[][] patterns = {
        { "(Reduced Penetration:).*", null },
        //hd
        { "(Reduced Penetration).*", null },
    };
 
    /** Creates new advCombatModifier */
    public limitationReducedPenetration() {
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
        setPriority(3);

        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
    }

    public double calculateMultiplier() {
        return -0.25;
    }

    public String getConfigSummary() {
        return "Reduced Penetration";
    }

    
    public int identifyLimitation(AbilityImport ai,int line) {
        int index,count;
        String possibleLimitation;
        
        possibleLimitation = ai.getImportLine(line);

        //removed due to limitation in 5e MC getting read as a power when I had both lines below installed
//        if ( possibleLimitation != null && possibleLimitation.indexOf( "Only in Hero ID:" ) != -1 ) {
//            return 10;
//        }
        
        if ( possibleLimitation != null && (possibleLimitation.indexOf( "REDUCEDPENETRATION" ) != -1 || possibleLimitation.indexOf( "Reduced Penetration" ) != -1) ) {
            return 10;
        }
        
        return 0;
    }

    /*public ParameterList getParameters(Ability ability, int index) {
        ParameterList parameters = new ParameterList();
        return parameters;
    } */

  /*  public void public void prepower(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget) throws BattleEventException {
        int eindex;
        String defense, type, defspecial;
        Integer defValue;
        Double value;
        int newValue;
        
        eindex = effect.findIndexed( "Subeffect","VERSUS","BODY" );
        if ( eindex != -1 ) {
            type = effect.getIndexedStringValue( eindex, "Subeffect", "VERSUSTYPE" );
            if ( type == null || type.equals("STAT") == false ) throw new BattleEventException( "Limitation Reduced Penetration not compatible with " + ability.getName() );
            defense = effect.getIndexedStringValue( eindex, "Subeffect", "DEFTYPE" );
            if ( (defspecial = effect.getIndexedStringValue(eindex,"Subeffect","DEFSPECIAL")) == null ) {
                defspecial = "NORMAL";
            }
            
            // Munge the defense appropriately
            if ( defspecial.equals("NND" )|| defspecial.equals("NONE") ) {
                // NND && NONE attacks won't be affected by Reduced Penetration
                return;
            }
            if ( defspecial.equals("KILLING") ) {
                defense = "r" + defense;
            }
            
            if ( ( defValue = effect.getIntegerValue( defense + ".DEFENSE" ) ) == null ) {
                defValue = new Integer(0);
            }
            if ( ( value = effect.getIndexedDoubleValue( eindex, "Subeffect", "VALUE" ) ) == null ) {
                value = new Double(0);
            }
            newValue = value.intValue() - defValue.intValue();
            if ( newValue < 0 ) newValue = 0;
            
            effect.addIndexed( eindex,   "Subeffect", "VALUE", new Double(newValue),  true );
        } 
    } */
    public Object[][] getImportPatterns() {
        return patterns;
    }
}