/*
 * limitationActivation.java
 *
 * Created on April 11, 2004, 12:30 AM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;

/**
 *
 * @author  1425
 */
public class limitationActivationAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "ACTIVATIONROLL";
    private static String[][] translationArray = {
        { "OPTIONID", "ActivationRoll" },
        { "ADDER(XMLID=BURNOUT)", "CanBurnout", "trueSpecial" },
        { "ADDER(XMLID=JAMMED)", "CanJam", "trueSpecial" },
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
    //public void levelSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {

        //try {
            // Trim the stupid X at the end...
        //attrValue = attrValue.substring(0, attrValue.length()-1);
            //pl.setParameterValue(parameterName, new Integer(attrValue));
        //}
        //catch ( NumberFormatException nfe ) {
            
        //}
    //}
}
