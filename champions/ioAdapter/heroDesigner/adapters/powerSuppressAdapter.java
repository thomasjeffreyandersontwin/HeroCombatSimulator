/*
 * powerAid.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.Characteristic;
import champions.PADRoster;
import champions.parameters.ParameterList;
import champions.Target;
import champions.interfaces.AbilityList;
import champions.Power;
import champions.SpecialEffect;
import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.HDImportError;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.Node;
import xml.XMLParseError;
import xml.XMLParseErrorList;

/**
 *
 * @author  1425
 */
public class powerSuppressAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "SUPPRESS";
    private static String[][] translationArray = {
        { "LEVELS", "DrainDie" },
        { "INPUT", "DrainFrom" },
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
    
    public void additionalPointsSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        pl.addIndexedParameterValue("DrainFrom", attrValue);
    }
    
    /** CustomHandler for processing parameter.
     *
     */
    //    public void aidToSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
    //        pl.setParameterValue(parameterName, attrValue);
    //        ability.createIndexed("aidToImport", "OBJECT", attrValue, false);
    //
    //    }
    /** Finilizes the Import of a power/adder from an XML source.
     *
     * This method should be called after the Ability has been added to it's
     * source (if one exists).  If called with a null source, then the
     * ability (and the power and adders) can assume that it will not be
     * attached to a specific source.
     */
    public XMLParseError finalize(Ability ability) {
        XMLParseErrorList errorList = null;
        
        Target source = ability.getSource();
        AbilityList abilities = source.getAbilityList();
        
        ArrayList drainFromAbilities = new ArrayList();
        
        ParameterList pl = ability.getPower().getParameterList(ability, -1);
        for(int index = 0; index < pl.getIndexedParameterSize("DrainFrom"); index++) {
            String drainFromString = (String)pl.getIndexedParameterValue("DrainFrom", index);

            
            SpecialEffect s;
            boolean found = false;
            if ( (s = PADRoster.getSharedSpecialEffectInstance(drainFromString)) != null ) {
                if (s != null) {
                    drainFromAbilities.add(s);
                    found = true;
                }
            }
           
            if ( !found && source.hasStat( drainFromString.toUpperCase() )) {
                drainFromAbilities.add(Characteristic.createCharacteristic(drainFromString.toUpperCase(), false));
                found = true;
            }
            
            if ( !found ) {
                Ability drainFromAbility = abilities.getAbility(drainFromString,true);

                if ( drainFromAbility != null ) {
                    drainFromAbilities.add(drainFromAbility);
                    found = true;
                }
            }
            
            if ( ! found ) {
                // Not a stat, so iterate through the Powers Types...
                Iterator i = PADRoster.getAbilityIterator();
                while(i.hasNext()) {
                    String name = (String)i.next();
                    if ( name.equals( drainFromString ) ){
                        Ability a = PADRoster.getSharedAbilityInstance(name);
                        Power p = a.getPower();
                        drainFromAbilities.add(p);
                        found = true;
                    }
                }
            }
            
            if ( !found ) {
                if ( errorList == null ) errorList = new XMLParseErrorList();
                errorList.addXMLParseError( new HDImportError("Drain source \"" + drainFromString + "\" not recognized during import", HDImportError.IMPORT_ERROR));
            }
        }
        
        pl.removeAllIndexedParameterValues("DrainFrom");
        
        for(int index = 0; index < drainFromAbilities.size(); index++) {
            pl.addIndexedParameterValue("DrainFrom", drainFromAbilities.get(index));
        }
        
        ability.reconfigurePower();
        
        return errorList;
    }
}
