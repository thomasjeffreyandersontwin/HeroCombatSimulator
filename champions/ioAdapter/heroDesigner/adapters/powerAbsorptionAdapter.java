/*
 * powerAbsorption.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.Characteristic;
import champions.parameters.ParameterList;
import champions.Target;
import champions.interfaces.AbilityList;
import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.HDImportError;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;
import java.util.ArrayList;
import org.w3c.dom.Node;
import xml.XMLParseError;
import xml.XMLParseErrorList;

/**
 *
 * @author  1425
 */
public class powerAbsorptionAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "ABSORPTION";
    private static String[][] translationArray = {
        { "OPTIONID", "DamageType" },
        { "LEVELS", "AbsorbDie"},
        { "INPUT", "AbsorbTo" },
        { "ADDER(XMLID=INCREASEDMAX).LEVELS", "IncreasedMax"},
        { "ADDER(XMLID=ADDITIONALPOINTSGOINTO).INPUT", "", "additionalPointsSpecial"},
        
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
    
   /* <ADDER XMLID="ADDITIONALPOINTSGOINTO" ID="1124633459337" BASECOST="0.0" 
LEVELS="0" ALIAS="Additional Absorbed Points Go Into:" POSITION="0" 
MULTIPLIER="1.0" GRAPHIC="Burst" COLOR="255 255 255" SFX="Default" 
SHOW_ACTIVE_COST="Yes" INCLUDE_NOTES_IN_PRINTOUT="Yes" NAME="" INPUT="DEX" 
SHOWALIAS="Yes" PRIVATE="No" REQUIRED="No" GROUP="No" SELECTED="YES">*/
    public void additionalPointsSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        pl.addIndexedParameterValue("AbsorbTo", attrValue);
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
        
        // We have to translate the AbsorbTo to be abilities...
        Target source = ability.getSource();
        AbilityList abilities = source.getAbilityList();
        
        ArrayList absorbToAbilities = new ArrayList();
        
        ParameterList pl = ability.getPower().getParameterList(ability, -1);
        for(int index = 0; index < pl.getIndexedParameterSize("AbsorbTo"); index++) {
            String absorbToString = (String)pl.getIndexedParameterValue("AbsorbTo", index);
            
            if ( source.hasStat( absorbToString.toUpperCase() )) {
                absorbToAbilities.add(Characteristic.createCharacteristic(absorbToString.toUpperCase(), false));
            }
            else {
                Ability absorbToAbility = abilities.getAbility(absorbToString,true);

                if ( absorbToAbility != null ) {
                    absorbToAbilities.add(absorbToAbility);
                }
                else {
                    if ( errorList == null ) errorList = new XMLParseErrorList();
                    errorList.addXMLParseError( new HDImportError("Absorption source \"" + absorbToString + "\" not recognized during import", HDImportError.IMPORT_ERROR));
                }
            }
        }
        
        pl.removeAllIndexedParameterValues("AbsorbTo");
        
        for(int index = 0; index < absorbToAbilities.size(); index++) {
            pl.addIndexedParameterValue("AbsorbTo", absorbToAbilities.get(index));
        }
        
        ability.reconfigurePower();
        
        return errorList;
    }
}
