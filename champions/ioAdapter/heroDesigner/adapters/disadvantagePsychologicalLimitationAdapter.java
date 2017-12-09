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
public class disadvantagePsychologicalLimitationAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "PSYCHOLOGICALLIMITATION";
    private static String[][] translationArray = {
        { "ADDER(XMLID=SITUATION).OPTIONID", "Situation", "situationSpecial" },
        { "ADDER(XMLID=INTENSITY).OPTION_ALIAS", "Intensity" },

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
    public void situationSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        //{"Uncommon Circumstance","Common Circumstance","Very Common Circumstance"};
        
        if ( "UNCOMMON".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Uncommon");
        }
        else if ( "COMMON".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Common");
        }
        else if ( "VERYCOMMON".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Very Common");
        }
    }

}
