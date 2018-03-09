/*
 * powerCombatLevels.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.Battle;
import champions.Target;
import champions.interfaces.AbilityList;
import champions.parameters.ParameterList;
import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.HDImportError;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;
import org.w3c.dom.Node;
import xml.XMLParseError;
import xml.XMLParseErrorList;

/**
 *
 * @author  1425
 */
public class powerCombatLevelsAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "COMBAT_LEVELS";
    private static String[][] translationArray = {
        { "LEVELS", "Level" },
        { "OPTIONID", "LevelType", "abilitiesSpecial" },
        { "OPTION_ALIAS",null, "levelTypeSpecial" },
        { "ADDER(XMLID=CLSATTACHEDTO).INPUT", null, "clsAttachedToSpecial" },
        { "ADDER(XMLID=OCVLEVEL).INPUT", "OCVLevel" },
        { "ADDER(XMLID=DCVLEVEL).INPUT", "DCVLevel" },
        { "ADDER(XMLID=DCLEVEL).INPUT", "DCLevel" },
    };
    
    /** Returns the XMLID for this Power.
     *
     * Subclass should either override this to return their XMLID or override
     * the identify method to do more complicated identification tasks.
     */
    public String getXMLID() {
        return XMLID;
    }
    
    /** Returns the Translation Array for the PowerAdapter.
     *
     * The Subclass should either override this to return their translationArray
     *or override the importXML method to do more complicated import tasks.
     */
    public String[][] getTranslationArray() {
        return translationArray;
    }
    
    /** Finilizes the Import of a power/adder from an XML source.
     *
     * This method should be called after the Ability has been added to it's
     * source (if one exists).  If called with a null source, then the
     * ability (and the power and adders) can assume that it will not be
     * attached to a specific source.
     */
    public XMLParseError finalize(Ability ability) {
        XMLParseErrorList errorList = null;
        
        ParameterList pl = ability.getPower().getParameterList(ability, -1);
        int count = ability.getIndexedSize("CanUseCLImport");
        for ( int index = 0; index < count; index++) {
            String name = ability.getIndexedStringValue(index, "CanUseCLImport", "ABILITY");
            if(!name.contains("HTH") && !name.contains("Ranged") &&  !name.contains("Martial Arts") &&  !name.contains("Overall"))
            {
            	Ability a = ability.getSource().getAbility(name);
            
            	if ( a == null ) {
            		AbilityList list = ability.getSource().findSublist(name);
            		
            		if(list!=null)
            		{
            			for (int i=0; i < list.getAbilityCount(); i++)
            			{
            				Ability subAbility = list.getAbility(i);
            				pl.addIndexedParameterValue("CanUseCL", subAbility);
            			}
            			
            		}
            		else
            		{
            	
		            	a = Battle.getDefaultAbilitiesOld().getAbility(name,true);
		            	if ( a != null ) {
		            		pl.addIndexedParameterValue("CanUseCL", a);
		            		//pl.createIndexed("CanUseCL", "ABILITY", a, false);
		            	}
		            	else {
		            		if ( errorList == null ) errorList = new XMLParseErrorList();
		            		//     errorList.addXMLParseError( new HDImportError("Combat Level source \"" + name + "\" not recognized during import", HDImportError.IMPORT_ERROR));
		            	}
		            }
            	}
            }
        }
        ability.removeAll("CanUseCLImport");
        //pl.setParameterSet("CanUseCL", true);
        ability.getPower().configurePAD(ability,pl);
        
        return errorList;
    }
    
    public void abilitiesSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        String[] s = node.getAttributes().getNamedItem("OPTION_ALIAS").getNodeValue().split(",");
        Target source = ability.getSource();
        for (int i = 0; i < s.length; i++) {
        	if(!s[i].equals("with All Combat"))
        		ability.createIndexed("CanUseCLImport", "ABILITY", s[i], false); 
		}
        
        //if(s.equals("All))
        
        
        
    }
    public void clsAttachedToSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        ability.createIndexed("CanUseCLImport", "ABILITY", attrValue, false);
        
    }
    
    /** CustomHandler for processing parameter.
     *
     */
    public void levelTypeSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if (attrValue.equals("SINGLESINGLE")) {
            pl.setParameterValue("LevelType", "Single Attack"); 
        }
        else if (attrValue.equals("SINGLE")) {
            pl.setParameterValue("LevelType", "Single Attack"); 
        }
        else if (attrValue.equals("SINGLESTRIKE")) {
            pl.setParameterValue("LevelType", "Single Attack"); 
        }
        else if (attrValue.equals("STRIKE")) {
            pl.setParameterValue("LevelType", "Tight Group"); 
        }
        else if (attrValue.equals("MARTIAL")) {
            pl.setParameterValue("LevelType", "Martial Arts");
        }
        else if (attrValue.equals("MAGIC")) {
            pl.setParameterValue("LevelType", "Tight Group");
        }
        else if (attrValue.equals("TIGHT")) {
            pl.setParameterValue("LevelType", "Tight Group");
        }
        else if (attrValue.equals("DCV")|| attrValue.equals("With DCV")) {
            pl.setParameterValue("LevelType", "DCV Against All Attacks");
        }
        else if (attrValue.equals("RANGED") || attrValue.equals("With Ranged Combat")) {
            pl.setParameterValue("LevelType", "Ranged Combat");
        }
        else if (attrValue.contains("HTH")) {
            pl.setParameterValue("LevelType", "HTH Combat");
        }
        else if (attrValue.equals("with HTH and Ranged Combat")) {
            pl.setParameterValue("LevelType", "HTH and Ranged Combat");
        }
        else if (attrValue.equals("with Mental and Ranged Combat")) {
            pl.setParameterValue("LevelType", "Mental and Ranged Combat");
        }
        else if (attrValue.equals("with HTH and Mental Combat")) {
            pl.setParameterValue("LevelType", "HTH and Mental Combat");
        }
        else if (attrValue.equals("ALL") || attrValue.equals("with All Combat")) {
            pl.setParameterValue("LevelType", "All Combat");
            pl.setParameterValue("Overall", "Overall (Only when Doing Something)");
        }
    }
}
