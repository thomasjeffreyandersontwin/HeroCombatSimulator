/*
 * powerEnergyBlast.java
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
public class disadvantageReputationAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "REPUTATION";
    private static String[][] translationArray = {
        { "ADDER(XMLID=RECOGNIZED).OPTION_ALIAS", "ActivationRoll", "activationRollSpecial"},
        { "ADDER(XMLID=EXTREME)", "ExtremeReputation","trueSpecial" },
        { "ADDER(XMLID=NOTALL)", "KnownToASmallLimitedGroup","trueSpecial" },
    };
    
    /** Returns the XMLID for this Power.
     *
     * Subclass should either override this to return their XMLID or override
     * the identify method to do more complicated identification tasks.
     */
    public String getXMLID() {
        return XMLID;
    }
    
    /** Returns the Translation Array for the Power.
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
    public void extremeReputationSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "EXTREME".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Incompetent");
        }
        else if ( "NORMAL".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Normal");
        }
        else if ( "SLIGHTLY".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Slightly Less Powerful then the PC");
        }
        else if ( "ASPOWERFUL".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "As Powerful as the PC");
        }
    }
    /** specialHandler that translates HD Shape option to HCS shape option.
     *
     */
    public void activationRollSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "11-".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "11");
        }
        else if ( "14-".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "14");
        }
        else if ( "8-".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "8");
        }
    }
    
}
