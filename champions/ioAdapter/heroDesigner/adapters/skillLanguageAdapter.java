/*
 * powerGrowth.java
 *
 * Created on April 14, 2004, 12:12 AM
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
public class skillLanguageAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "LANGUAGES";
    private static String[][] translationArray = {
        { "OPTION_ALIAS", "Level" },
        { "NATIVE_TONGUE", "Native", "nativeSpecial" },
        { "ADDER(XMLID=LITERACY)", "Literate", "trueSpecial" },
        { "INPUT", "Language" }
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
    
    public void nativeSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "Yes".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "TRUE");
        }
        else if ( "No".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "FALSE");
        }
    }
}
