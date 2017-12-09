/*
 * powerCombatLevels.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.Battle;
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
        { "OPTIONID", "LevelType", "levelTypeSpecial" },
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
            Ability a = ability.getSource().getAbility(name);
            
            if ( a == null ) {
                a = Battle.getDefaultAbilitiesOld().getAbility(name,true);
            } 
            
            if ( a != null ) {
                pl.addIndexedParameterValue("CanUseCL", a);
                //pl.createIndexed("CanUseCL", "ABILITY", a, false);
            }
            else {
                if ( errorList == null ) errorList = new XMLParseErrorList();
                errorList.addXMLParseError( new HDImportError("Combat Level source \"" + name + "\" not recognized during import", HDImportError.IMPORT_ERROR));
            }
        }
        ability.removeAll("CanUseCLImport");
        //pl.setParameterSet("CanUseCL", true);
        ability.getPower().configurePAD(ability,pl);
        
        return errorList;
    }
    
    /** CustomHandler for processing parameter.
     *
     */
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
        else if (attrValue.equals("DCV")) {
            pl.setParameterValue("LevelType", "DCV Against All Attacks");
        }
        else if (attrValue.equals("RANGED")) {
            pl.setParameterValue("LevelType", "Ranged Combat");
        }
        else if (attrValue.equals("HTH")) {
            pl.setParameterValue("LevelType", "HTH Combat");
        }
        else if (attrValue.equals("ALL")) {
            pl.setParameterValue("LevelType", "All Combat");
            pl.setParameterValue("Overall", "Overall (Only when Doing Something)");
        }
    }
}
