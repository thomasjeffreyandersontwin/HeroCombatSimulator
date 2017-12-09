/*
 * advantageIncreasedStunMultiplier.java
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
public class advantageDoubleKnockbackAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "DOUBLEKB";
    private static String[][] translationArray = {
        { "OPTIONID", "DoubleKB", "doubleKBSpecial" }
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
    public void doubleKBSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        //this needs to be updated when 1.5x kb is added to powerDoubleknockack modified
        if ( "TWOTIMES".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "2X KB");
        }
        else if ( "ONEANDAHALFTIMES".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "1.5X KB");
        }        
    }
}
