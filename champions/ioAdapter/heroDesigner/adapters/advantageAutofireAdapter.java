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
import xml.DefaultXMLHandler;


/**
 *
 * @author  1425
 */
public class advantageAutofireAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "AUTOFIRE";
    private static String[][] translationArray = {
        { "OPTIONID", "MaxShots", "maxShotsSpecial" },
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
    public void maxShotsSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        int maxShots = 0;
        if ( "FIVE".equals(attrValue) ) {
            maxShots = 5;
        }
        else if ( "THREE".equals(attrValue) ) {
            maxShots = 3;
        }
        //Added support for 2 shot Autofire
        else if ( "TWO".equals(attrValue) ) {
            maxShots = 2;
        }
        
        String value;
        if ( ( value = DefaultXMLHandler.findAttribute(node, "ADDER(XMLID=DOUBLE).LEVELS")) != null ) {
            try {
                int levels = Integer.parseInt(value);
                maxShots = maxShots * (int)Math.pow(2, levels);
            }
            catch ( NumberFormatException nfe) {
                
            }
        }
        
        pl.setParameterValue(parameterName, new Integer(maxShots));
    }
}
