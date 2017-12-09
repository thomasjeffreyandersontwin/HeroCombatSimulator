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
public class disadvantageDistinctiveFeaturesAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "DISTINCTIVEFEATURES";
    private static String[][] translationArray = {
        { "ADDER(XMLID=CONCEALABILITY).OPTION", "Concealability", "concealabilitySpecial"},
        { "ADDER(XMLID=REACTION).OPTION_ALIAS", "Reaction" },
        { "ADDER(XMLID=SENSING).OPTION_ALIAS", "Sensing" },
        { "ADDER(XMLID=NOTINSOME)", "NotDistinctiveInSomeCultures", "trueSpecial" },
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
    public void concealabilitySpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "EASILY".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Easily Concealed");
        }
        else if ( "CONCEALABLE".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Concealable");
        }
        else if ( "NOTCONCEALABLE".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Not Concealable");
        }
    }

    
}
