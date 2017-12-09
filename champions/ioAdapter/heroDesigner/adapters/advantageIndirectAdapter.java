/*
 * advantageAutofireAdapter.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.parameters.ParameterList;
import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;
import org.w3c.dom.Node;


/**
 *
 * @author  1425
 */
public class advantageIndirectAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter {
    
    private static String XMLID = "INDIRECT";
    private static String[][] translationArray = {
        { "OPTIONID", "Direction", "indirectSpecial" },
        //{ "ADDER(XMLID=ODDPOWER).TRUE", "OddPower" } // Not supported by HCS
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
    
    /** CustomHandler for processing parameter.
     *
     */
    public void indirectSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        //need more options here
        if ("SAMEAWAY".equals(attrValue)) {
            pl.setParameterValue("Direction", "Same Location & Fires Away");
        }
            else if ("ANYAWAY".equals(attrValue)) {
                pl.setParameterValue("Direction", "Any Location & Fires Away");
            }
            else if ("ANYANY".equals(attrValue)) {
                pl.setParameterValue("Direction", "Any Location & Any Direction");
            }
    }
}
