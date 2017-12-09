/*
 * powerCombatLevels.java
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
public class powerInvisibilityAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "INVISIBILITY";
    private static String[][] translationArray = {
        { "OPTION_ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=HEARINGGROUP).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=MENTALGROUP).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=RADIOGROUP).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=SIGHTGROUP).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=SMELLGROUP).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=TOUCHGROUP).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=MYSTICGROUP).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=NORMALHEARING).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=NORMALSIGHT).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=NORMALSMELL).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=NORMALTASTE).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=NORMALTOUCH).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=DANGER_SENSE).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=COMBAT_SENSE).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=MINDSCAN).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=ACTIVESONAR).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=DETECT).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=HRRP).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=INFRAREDPERCEPTION).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=MENTALAWARENESS).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=NIGHTVISION).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=NRAYPERCEPTION).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=RADAR).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=RADIOPERCEIVETRANSMIT).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=RADIOPERCEPTION).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=SPATIALAWARENESS).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=ULTRASONICPERCEPTION).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=ULTRAVIOLETPERCEPTION).ALIAS", "Senses", "sensesSpecial"},
        { "ADDER(XMLID=NOFRINGE)", "NoFringe", "trueSpecial" },
//        
//        //{ "MODIFIER(XMLID=BRIGHTFRINGE)", "Recoverable", "trueSpecial" },
//        { "MODIFIER(XMLID=ONLYWHENNOTATTACKING)", "NotAttacking", "trueSpecial" },
//        { "MODIFIER(XMLID=CHAMELEON)", "Chameleon", "trueSpecial" },
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
    public void noFringeSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        pl.setParameterValue("Fringe", "TRUE");
    }
    
    /** CustomHandler for processing parameter.
     *
     */
//    public void brightFringeSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
//        pl.setParameterValue("Fringe", "Bright Fringe");
//    }
    
    /** CustomHandler for processing parameter.
     *
     */
    public void chameleonSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        pl.setParameterValue("Chameleon", "TRUE");
    }
    
//    public void notAttackingSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
//        pl.setParameterValue("NotAttacking", "TRUE");
//    }
    
    public void sensesSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "".equals(attrValue) == false ) {
            pl.addIndexedParameterValue("Senses", PADRoster.getNewSense(attrValue));
        }
    }
}
