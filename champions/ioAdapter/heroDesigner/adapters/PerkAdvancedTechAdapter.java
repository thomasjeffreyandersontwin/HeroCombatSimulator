/*
 * powerArmor.java
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
public class PerkAdvancedTechAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "Advanced Tech";
    private static String[][] translationArray = {
        { "LEVELS", "Levels" },
        { "OPTION", "Options", "optionsSpecial" },
        
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
        /** specialHandler that translates HD Shape option to HCS shape option.
     *
     */
    public void optionsSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "NORMAL".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "10 Points/Level");
        }
        else if ( "GMOPTION".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "15 Points/Level");
        }

    }
    
}
