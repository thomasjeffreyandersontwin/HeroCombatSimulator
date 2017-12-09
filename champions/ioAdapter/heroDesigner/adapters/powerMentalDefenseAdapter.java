/*
 * powerFlash.java
 *
 * Created on April 14, 2004, 12:12 AM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.parameters.ParameterList;
import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;
import org.w3c.dom.Node;
import xml.DefaultXMLHandler;


/**
 *
 * @author  1425
 */
public class powerMentalDefenseAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "MENTALDEFENSE";
    private static String[][] translationArray = {
        { "LEVELS", "Level", "levelSpecial" }, //Need to add a special that will take the base level and add 2 to it
        
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
    
    public void levelSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        int modifier = 0;
                
        String value;
        if ( ( value = DefaultXMLHandler.findAttribute(node, "LEVELS")) != null ) {
            try {
                int levels = Integer.parseInt(value);
                modifier = modifier + levels;
            }
            catch ( NumberFormatException nfe) {
                
            }
        }
        
        pl.setParameterValue(parameterName, new Integer(modifier));
    }
}
