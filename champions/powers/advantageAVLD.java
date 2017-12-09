/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.AdvantageAdapter;
import champions.BattleEvent;
import champions.Effect;
import champions.Target;
import champions.exception.BattleEventException;
import champions.interfaces.ChampionsConstants;
import champions.parameters.ParameterList;



/**
 *
 * @author  unknown
 * @version
 *
 * To Convert from old format powers, to new format powers:
 *
 * 1) Add implements ChampionsConstants to class definition.<P>
 * 2) Copy and Fill in Advantage Definition Variables. <P>
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
 * 12) Edit getName method to return advantageName variable.
 * 13) Change serialVersionUID by some amount.
 * 14) Add patterns array and define import patterns.<P>
 * 15) Add getImportPatterns() method.<P>
 */
public class advantageAVLD extends AdvantageAdapter implements ChampionsConstants {
    static final long serialVersionUID = -6399128335213532419L;

    static private String[] DefenseOptions = { "Power Defense", "Mental Defense", "Flash Defense" };
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"Defense","Advantage.DEFENSE", String.class, "Power Defense", "Defense vs. AVLD", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", DefenseOptions },
        {"DoesBody","NND.DOESBODY", Boolean.class, new Boolean(false), "Does Body Damage", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}
    };
    
    // Advantage Definition Variables
    public static String advantageName = "Attack Vs. Limited Defense"; // The Name of the Advantage
    private static boolean affectsDC = true; // Indicates the Advantage affects the cost of damage classes
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Attack vs. Limited Defense.*", null}
    };
    
    /** Creates new advCombatModifier */
    public advantageAVLD() {
    }
    
    public String getName() {
        return advantageName;
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
        //String attackType = (String)parameterList.getParameterValue("AttackType");
        String defense = (String)parameterList.getParameterValue("Defense");
        boolean doesbody = (Boolean)parameterList.getParameterValue("DoesBody");
       
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        // AdvantageAreaEffect has nothing to validate.
        
        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        int index = ability.addAdvantageInfo(this, advantageName, parameterList);
        this.setAffectsDC(affectsDC);
        
        // Copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.  Make sure that these
        // Value/Pairs are unique to the PAD.  Any that are not, should be copied
        // manually.
        // Note that if the advantage is not unique, the values should always be
        // accessed via the parameterList and not directly.
        //parameterList.copyValues(ability,index);
               
        // Add Extra Value/Pairs used by the Advantage/BattleEngine
        ability.add("Ability.DOESBODY", doesbody,true);
        ability.add("Ability.DOESKNOCKBACK", "FALSE",false);
        ability.reconfigurePower();

        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
    }
    
    public double calculateMultiplier() {
        return 1.5;
    }
    
    public String getConfigSummary() {
        ParameterList parameterList = getParameterList();
        //String attackType = (String)parameterList.getParameterValue("AttackType");
        String defense = (String)parameterList.getParameterValue("Defense");
        boolean doesbody = (Boolean)parameterList.getParameterValue("DoesBody");
        
        return "Attack Vs. Limited Defense (" + defense + ")";
        
    }

    public int identifyAdvantage(AbilityImport ai, int line) {
        int index,count;
        String possibleAdvantage;
        
        possibleAdvantage = ai.getImportLine(line);
        if ( possibleAdvantage != null && (possibleAdvantage.indexOf("AVLD") != -1 || possibleAdvantage.indexOf("Attack vs. Limited Defense:") != -1)) return 1;
        return 0;
    }
    
    public void prepower(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget) throws BattleEventException {
        int i, count;
        String versus;
        count = effect.getIndexedSize( "Subeffect" );
        
        ParameterList parameterList = getParameterList();
        //String attackType = (String)parameterList.getParameterValue("AttackType");
        String defense = (String)parameterList.getParameterValue("Defense");
        boolean doesbody = (Boolean)parameterList.getParameterValue("DoesBody");
        
       // String doesbody = (String)ability.parseParameter(parameterArray, "DoesBody");
        
        for (i = 0; i < count; i ++ ) {
            if ( (versus = effect.getIndexedStringValue( i, "Subeffect", "VERSUS" )) == null){
                continue;
            }
            
            effect.addIndexed( i,  "Subeffect","DEFSPECIAL","NND", true);
            //effect.add("NND.ATTACKTYPE",  attackType )  ;
            effect.add("NND.DEFENSE",  defense )  ;
            
            // Take care of instances where body is generated (by custom effect) and needs to be removed
            if ( doesbody == false && versus.equals("BODY") ) {
                effect.addIndexed( i,  "Subeffect","VALUE", new Double(0) , true);
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
        return patterns;
    }
}