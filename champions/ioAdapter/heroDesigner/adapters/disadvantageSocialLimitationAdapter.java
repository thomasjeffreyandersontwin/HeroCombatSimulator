/*
 * disadvantagePhysicalLimitation.java
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
public class disadvantageSocialLimitationAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "SOCIALLIMITATION";
    private static String[][] translationArray = {
        { "ADDER(XMLID=OCCUR).OPTIONID", "ActivationRoll", "activationRollSpecial"},
        { "ADDER(XMLID=EFFECTS).OPTION_ALIAS", "Restrictions" },
        { "ADDER(XMLID=NOTINSOME)", "NotLimitingInSomeCultures", "trueSpecial" },
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
    public void activationRollSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "OCCASIONALLY".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "8");
        }
        else if ( "FREQUENTLY".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "11");
        }
        else if ( "VERYFREQUENTLY".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "14");
        }
    }
}
