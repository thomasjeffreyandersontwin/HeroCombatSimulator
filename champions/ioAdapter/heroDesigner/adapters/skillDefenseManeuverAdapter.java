/*
 * powerCombatLevels.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.parameters.ParameterList;
import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;
import org.w3c.dom.Node;


/**
 *
 * @author  1425
 */
public class skillDefenseManeuverAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "DEFENSE_MANEUVER";
    private static String[][] translationArray = {
        { "OPTIONID", "Level", "levelSpecial" }
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
    
     /** specialHandler that translates HD skill level options to HCS skill level options.
     *
     */
    public void levelSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "ONE".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "I");
        }
        else if ( "TWO".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "II");
        }
        else if ( "THREE".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "III");
        }
        else if ( "FOUR".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "IV");
        }
    }
}
