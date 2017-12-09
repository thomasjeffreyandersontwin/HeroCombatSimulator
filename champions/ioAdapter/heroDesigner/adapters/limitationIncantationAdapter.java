/*
 * limitationIncantation.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;

/**
 *
 * @author  1425
 */
public class limitationIncantationAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "INCANTATIONS";
    private static String[][] translationArray = {
        { "MODIFIER(XMLID=CONSTANT)", "Throughout","trueSpecial" },
        { "ADDER(XMLID=COMPLEX)", "Complex", "trueSpecial" }
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
    
    /** CustomHandler for processing parameter.
     *
     */
//    public void incantationSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
//        pl.setParameterValue(parameterName, "Throughout");
//    }
}
