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
public class disadvantagePhysicalLimitationAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "PHYSICALLIMITATION";
    private static String[][] translationArray = {
        { "ADDER(XMLID=OCCURS).OPTIONID", "Occurs", "occursSpecial" },
        { "ADDER(XMLID=IMPAIRS).OPTIONID", "Impairs", "impairsSpecial" },

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
    
    public void occursSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "INFREQUENTLY".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Infrequently");
        }
        else if ( "FREQUENTLY".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Frequently");
        }
        else if ( "ALLTHETIME".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "All the Time");
        }
    }
    public void impairsSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "SLIGHTLY".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Slightly Impairing");
        }
        else if ( "GREATLY".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Greatly Impairing");
        }
        else if ( "FULLY".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Fully Impairing");
        }
    }
}
