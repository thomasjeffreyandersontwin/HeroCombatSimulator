/*
 * limitationLimitedSpecialEffect.java
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
public class limitationLimitedSpecialEffectAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "LIMITEDSPECIALEFFECT";
    private static String[][] translationArray = {
        { "OPTIONID", "LimitedFX" },
        
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
//    public void limitedfxSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
//
//        try {
//            // Trim the stupid X at the end...
//        attrValue = attrValue.substring(0, attrValue.length()-4);
//            pl.setParameterValue(parameterName, new Integer(attrValue));
//        }
//        catch ( NumberFormatException nfe ) {
//            
//        }
//    }

}