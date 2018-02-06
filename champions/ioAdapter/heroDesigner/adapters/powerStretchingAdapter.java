/*
 * powerEnergyBlast.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.parameters.ParameterList;
import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.HDImportError;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;
import org.w3c.dom.Node;
import xml.XMLParseError;

/**
 *
 * @author  1425
 */
public class powerStretchingAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "STRETCHING";
    private static String[][] translationArray = {
    		 { "LEVELS", "DamageDie" },
    };
    
    private boolean basePower = false;
    
    public powerStretchingAdapter() {
        super();
    }
    
    public powerStretchingAdapter(boolean basePower) {
        super();
        
        this.basePower = basePower;
    }
    
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
        String[][] t = {{ "LEVELS", "Distance" }};
        return t;
    }
    
    public XMLParseError importXML(Ability ability, Node node) {
        XMLParseError result = super.importXML(ability, node);
        
        if ( (result == null || result.getErrorSeverity() < HDImportError.IMPORT_FAILURE)  && basePower ) {
            ParameterList pl = ability.getPower().getParameterList(ability, -1);
            pl.setParameterValue("Base", "TRUE");
            ability.reconfigurePower();
        }
        
        return result;
    }
    
    
}
