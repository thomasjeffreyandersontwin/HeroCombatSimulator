/*
 * powerEnergyBlast.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;
import champions.parameters.ParameterList;
import org.w3c.dom.Node;
import xml.XMLParserException;

/**
 *
 * @author  1425
 */
public class powerEBAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "ENERGYBLAST";
    private static String[][] translationArray = {
        { "INPUT", "Defense", "defenseSpecial" },
        { "LEVELS", "DamageDie" },
        { "MODIFIER(XMLID=STUNONLY)", "StunOnly", "trueSpecial"}
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
    
   /** specialHandler that translates HD Shape option to HCS shape option.
     *
     */
    public void defenseSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "PD".equals(attrValue) == false && "ED".equals(attrValue) == false ) {
            pl.setParameterValue("Defense", "ED");
            throw new XMLParserException("Defense Type must be either PD or ED.  Setting to ED.");
        }
        else {
            pl.setParameterValue("Defense", attrValue);
        }
    }
}
