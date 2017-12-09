/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityImport;
import champions.AdvantageAdapter;
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
public class advantageAreaEffect extends AdvantageAdapter
implements ChampionsConstants {
    static final long serialVersionUID = -6399128355213532412L;
    
    static private String[] typeOptions = { "Normal", "Selective", "Nonselective" };
    static private String[] ShapeOptions = { "Radius", "Any Area", "Cone","Line","One-hex" };
    
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"Type","Advantage.TYPE", String.class, "Normal", "Type", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", typeOptions},
        {"Shape","Advantage.SHAPE", String.class, "Radius", "Shape of AE", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", ShapeOptions},
        {"IncreasedAreaImport","Power.INCREASEDAREAIMPORT", Integer.class, new Integer(0) , "IncreasedArea Multiplier import", INTEGER_PARAMETER, HIDDEN, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(0)},
        {"IncreasedAreaLevel","Power.INCREASEDAREALEVEL", Integer.class, new Integer(0) , "IncreasedArea Multiplier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(0)},
        
    };
    
    // Advantage Definition Variables
    public static String advantageName = "Area Effect"; // The Name of the Advantage
    private static boolean affectsDC = true; // Indicates the Advantage affects the cost of damage classes
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    
    // Import Patterns Definitions
    private static Object[][] patterns = {
        { "Area Effect \\((.*)\\): .*", new Object[] {"Shape", String.class}},
        //likely 'normal is not picked up and needs to be seeded below:
        { "Area Of Effect - (.*) Target \\((.*)\\): .*", new Object[] {"Type", String.class,"Shape", String.class}},
        { "Area Of Effect \\((.*)\\): .*", new Object[] {"Shape", String.class}},
        { "Increased Area: ([0-9]*),.*", new Object[] {"IncreasedAreaImport", Integer.class}},
        { "(Nonselective) Target:.*", new Object[] {"Type", String.class}},
        { "(Selective) Target:.*", new Object[] {"Type", String.class}},
        //HD
        //AOE (6" Radius; +1), Selective (+1/4)
        { "Area Of Effect \\([0-9]*\" (.*)\\).*", new Object[] {"Shape", String.class}},
        { "AOE .*\" (.*); .*(Selective) .*", new Object[] {"Shape", String.class,"Type", String.class }},
        { "AOE \\([0-9]*\" (.*)\\).*", new Object[] {"Shape", String.class}},
        { "AOE (Nonselective) \\([0-9]*\" (.*)\\).*", new Object[] {"Type", String.class, "Shape", String.class}},
        { "AOE: (RADIUS) (SELECTIVETARGET)", new Object[] {"Shape", String.class, "Type", String.class}},
    };
    
    
    //Area Of Effect (Cone): 9" Long, +1
    public advantageAreaEffect() {
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
        String type = (String)parameterList.getParameterValue("Type");
        String shape = (String)parameterList.getParameterValue("Shape");
        Integer increasedareaimport = (Integer)parameterList.getParameterValue("IncreasedAreaImport");
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        if (shape.equals("RADIUS")) {
            parameterList.setParameterValue("Shape", "Radius");
        }
        else if (shape.equals("HEX")) {
            parameterList.setParameterValue("Shape", "One-hex");
        }
        else if (shape.equals("CONE")) {
            parameterList.setParameterValue("Shape", "Cone");
        }
        else if (shape.equals("ANY")) {
            parameterList.setParameterValue("Shape", "Any Area");
        }
        else if (shape.equals("LINE")) {
            parameterList.setParameterValue("Shape", "Line");
        }
        
        if (type.equals("SELECTIVETARGET")) {
            parameterList.setParameterValue("Type", "Selective");
        }
 
        // AdvantageAreaEffect has nothing to validate.
        
        if (increasedareaimport.intValue() > 0) {
            int index;
            int mult = 1;
            for (index = 0;mult < increasedareaimport.intValue();index++) {
                mult = mult *2;
            }
            parameterList.setParameterValue("IncreasedAreaLevel", new Integer(index) );
            parameterList.setParameterValue("IncreasedAreaImport", new Integer(0) );
            
        }
        
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
        ability.add("Ability.ISAE", "TRUE" , true );
        if ( type.equals( "Normal" ) ) {
            ability.add("Ability.ISSELECTIVEAE", "FALSE", true);
            ability.add("Ability.ISNONSELECTIVEAE", "FALSE", true);
        }
        else if ( type.equals( "Selective" ) ) {
            ability.add("Ability.ISSELECTIVEAE", "TRUE", true);
            ability.add("Ability.ISNONSELECTIVEAE", "FALSE", true);
        }
        else if ( type.equals( "Nonselective" ) ) {
            ability.add("Ability.ISSELECTIVEAE", "FALSE", true);
            ability.add("Ability.ISNONSELECTIVEAE", "TRUE", true);
        }
        ability.reconfigurePower();
        
        // Update the Stored Description for this Limitation
        setDescription( getConfigSummary() );
        
        // Return True to indicate success in configuringPAD
        return true;
    }
    
    
    public String getConfigSummary() {
        ParameterList pl = getParameterList();
        String type = (String)pl.getParameterValue("Type");
        String shape = (String)pl.getParameterValue("Shape");
        Integer increasedarealevel = (Integer)pl.getParameterValue("IncreasedAreaLevel");
        
        StringBuffer sb = new StringBuffer();
        sb.append( "Area Effect " + "(" + shape + ")" );
        if ( increasedarealevel.intValue() > 1 ) sb.append( ", " + ((int)Math.pow(2,increasedarealevel.intValue()) + " Increased Area" ) );
        
        if ( type.equals("Normal") == false ) sb.append(", " + type + " Target");
        
        return sb.toString();
    }
    
    
 /*   public ParameterList getParameters(Ability ability, int not_used) {
        ParameterList parameters = new ParameterList();
  
        String type = (String)ability.parseParameter(parameterArray, "Type");
  
        parameters.addComboParameter( "Type", "AE.TYPE", "Type", type, typeOptions);
  
        return parameters;
    } */
    
    public double calculateMultiplier() {
        ParameterList pl = getParameterList();
        String type = (String)pl.getParameterValue("Type");
        String shape = (String)pl.getParameterValue("Shape");
        Integer increasedarealevel = (Integer)pl.getParameterValue("IncreasedAreaLevel");
        
        double cost = 1.0;
        
        if (shape.equals("One-hex") ) {
            cost = .5;
        }
        
        cost = cost + (increasedarealevel.intValue() * 0.25);
        
        if ( type.equals( "Selective" ) ) {
            cost += .25;
        }
        else if ( type.equals( "Nonselective" ) ) {
            cost -= .25;
        }
        return cost;
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
    public int identifyAdvantage(AbilityImport ai, int line) {
        int index,count;
        String possibleAdvantage;
        
        possibleAdvantage = ai.getImportLine(line);
        if ( possibleAdvantage != null && possibleAdvantage.indexOf( "AOE"  ) != -1 ) {
            return 10;
        }
        if ( possibleAdvantage != null && possibleAdvantage.indexOf( getName()  ) != -1 ) {
            return 10;
        }
        if ( possibleAdvantage != null && possibleAdvantage.indexOf( "Area Of Effect" ) != -1 ) {
            return 10;
        }
        
        return 0;
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