/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */
package champions.powers;

import champions.Ability;
import champions.ActivationInfo;
import champions.AdvantageAdapter;
import champions.BattleEvent;
import champions.Dice;
import champions.Effect;
import champions.Target;
import champions.battleMessage.SimpleBattleMessage;
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
public class advantageExplosion extends AdvantageAdapter
        implements ChampionsConstants {
    
    static final long serialVersionUID = -6399121155213532412L;
    static private String[] typeOptions = {"Normal", "Selective", "Nonselective"};
    static private String[] ShapeOptions = {"Radius", "Cone", "Line"};    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first have is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
        {"Type", "Advantage.TYPE", String.class, "Normal", "Type", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", typeOptions},
        {"Shape", "Advantage.SHAPE", String.class, "Radius", "Shape of Explosion", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", ShapeOptions},
        {"FadeRate", "Power.FADERATE", Integer.class, new Integer(1), "Fall Off Rate", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(0)},
    };    // Advantage Definition Variables
    public static String advantageName = "Explosion"; // The Name of the Advantage
    private static boolean affectsDC = true; // Indicates the Advantage affects the cost of damage classes
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    // Import Patterns Definitions
    private static Object[][] patterns = {};
    //Area Of Effect (Cone): 9" Long, +1
    public advantageExplosion() {
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
        if (ability == null) {
            return false;        // Read in any parameters that will be needed to configure the power or
            // Determine the validity of the power configuration.  Read the parameters
            // from the parameterList, instead of directly from the ability, since the
            // Ability isn't configured yet.
        }
        Integer faderate = (Integer) parameterList.getParameterValue("FadeRate");
        String shape = (String) parameterList.getParameterValue("Shape");
        String type = (String) parameterList.getParameterValue("Type");        
        
        while ("Line".equals(shape) && faderate < 3) {
            parameterList.setParameterValue("FadeRate", faderate + 1);
            faderate = (Integer) parameterList.getParameterValue("FadeRate");
            parameterList.copyValues(ability);
        }
        while ("Cone".equals(shape) && faderate < 2) {
            parameterList.setParameterValue("FadeRate", faderate + 1);
            faderate = (Integer) parameterList.getParameterValue("FadeRate");
            parameterList.copyValues(ability);
        }
        while ("Radius".equals(shape) && faderate < 1) {
            parameterList.setParameterValue("FadeRate", faderate +1);
            faderate = (Integer) parameterList.getParameterValue("FadeRate");
            parameterList.copyValues(ability);
        }
        parameterList.copyValues(ability);
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        
        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        int index = ability.addAdvantageInfo(this, advantageName, parameterList);
        this.setAffectsDC(affectsDC);
        
        ability.add("Ability.ISEXPLOSION", "TRUE", true);
        
        // Copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.  Make sure that these
        // Value/Pairs are unique to the PAD.  Any that are not, should be copied
        // manually.
        // Note that if the advantage is not unique, the values should always be
        // accessed via the parameterList and not directly.
        //parameterList.copyValues(ability,index);
        
        // Add Extra Value/Pairs used by the Advantage/BattleEngine
        ability.add("Ability.ISEXPLOSION", "TRUE" , true );
        if ( type.equals( "Normal" ) ) {
            ability.add("Ability.ISSELECTIVEAE", "FALSE", true);
            ability.add("Ability.ISNONSELECTIVEAE", "FALSE", true);
        } else if ( type.equals( "Selective" ) ) {
            ability.add("Ability.ISSELECTIVEAE", "TRUE", true);
            ability.add("Ability.ISNONSELECTIVEAE", "FALSE", true);
        } else if ( type.equals( "Nonselective" ) ) {
            ability.add("Ability.ISSELECTIVEAE", "FALSE", true);
            ability.add("Ability.ISNONSELECTIVEAE", "TRUE", true);
        }
        
        
        ability.reconfigurePower();
        
        // Update the Stored Description for this Limitation
        setDescription(getConfigSummary());
        
        // Return True to indicate success in configuringPAD
        return true;
    }
    
    public String getConfigSummary() {
        return "Explosion";
    }
    
    public boolean checkParameter(String key, Object value, Object oldValue) {
        if ( key.equals("Power.FADERATE") ) {
            ParameterList parameterList = getParameterList();
            String shape = (String)parameterList.getParameterValue("Shape");
            int newfaderate = ((Integer)(value)).intValue();
            if ( "Line".equals(shape) && newfaderate < 3 ) {
                return false;
            } else if ( "Cone".equals(shape) && newfaderate < 2 ) {
                return false;
            } else if ( "Radius".equals(shape) && newfaderate < 1 ) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void prepower(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget) throws BattleEventException {
        
        if (effect.getIndexedSize("Subeffect") == 0 || effect.getIndexedStringValue(0, "Subeffect", "EFFECTTYPE").equals("END")) {
            return;
        }
        
        //String realTargetGroup = targetGroup.substring(0, targetGroup.length() - 3);
        
        int dindex = be.getDiceIndex("DamageDie", targetGroup);
        if (dindex != -1) {
            
            Dice dice = be.getDiceRoll(dindex);
            if (dice != null) {
                double hd = dice.getD6() + (dice.isOnehalf() ? 0.5 : 0);
                
                ActivationInfo ai = be.getActivationInfo();
                int tindex = ai.getTargetIndex(targetReferenceNumber, targetGroup);
                int distance = ai.getTargetDistanceFromExplosion(tindex);
                
                ParameterList pl = getParameterList();
                int fadeRate = (Integer)pl.getParameterValue("FadeRate");
                
                distance = distance / fadeRate;
                
                int count = effect.getSubeffectCount();
                for (int sindex = 0; sindex < count; sindex++) {
                    
                    double value = effect.getSubeffectValue(sindex);
                    double valuePerInch = value / hd;
                    double newValue = Math.max(0, Math.round(value - valuePerInch * distance));
                    
                    effect.setSubeffectValue(sindex, newValue);
                }
            }
        } else {
            be.addBattleMessage( new SimpleBattleMessage(target, "Warning: Explosion attached to non-damage attack not currently supported"));
        }
        
    }
    
    /*   public ParameterList getParameters(Ability ability, int not_used) {
    ParameterList parameters = new ParameterList();
     
    String type = (String)ability.parseParameter(parameterArray, "Type");
     
    parameters.addComboParameter( "Type", "AE.TYPE", "Type", type, typeOptions);
     
    return parameters;
    } */
    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        double cost = 0.5;
        Integer faderate = (Integer) parameterList.getParameterValue("FadeRate");
        String type = (String) parameterList.getParameterValue("Type");
        String shape = (String) parameterList.getParameterValue("Shape");
        if (shape.equals("Line")) {
            cost += (faderate - 3) * 0.25;
        } else if (shape.equals("Cone")) {
            cost += (faderate - 2) * 0.25;
        } else {
            cost += (faderate - 1) * 0.25;
        }
        
        if ( type.equals( "Selective" ) ) {
            cost += .25;
        } else if ( type.equals( "Nonselective" ) ) {
            cost -= .25;
        }
        return cost;
    }
}