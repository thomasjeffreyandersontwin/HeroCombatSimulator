/*
 * powerEnergyBlast.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.PADRoster;
import champions.parameters.ParameterList;
import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;
import org.w3c.dom.Node;

/**
 *
 * @author  1425
 */
public class powerFlashAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "FLASH";
    private static String[][] translationArray = {
        { "LEVELS", "DamageDie" },
        { "OPTION_ALIAS", "Senses", "sensesSpecial" },
        { "ADDER(XMLID=SIGHTGROUP).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=MENTALGROUP).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=HEARINGGROUP).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=RADIOGROUP).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=SMELLGROUP).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=TOUCHGROUP).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=SPATIALAWARENESS).ALIAS", "Senses", "sensesSpecial"},
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
    
    /** CustomHandler for processing parameter.
     *
     */
    public void sensesSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "".equals(attrValue) == false ) {
            pl.addIndexedParameterValue("Senses", PADRoster.getNewSense(attrValue));
        }
    }
}
