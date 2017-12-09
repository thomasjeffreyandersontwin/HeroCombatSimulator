/*
 * powerHKA.java
 *
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;
import champions.Ability;
import champions.PADRoster;
import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;
import champions.parameters.ParameterList;
import champions.powers.SpecialParameterNormallyOn;
import org.w3c.dom.Node;

/**
 *
 * @author  1425
 */
public class SpecialParameterNormallyOnAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "AUTOACTIVATE";
    private static String[][] translationArray = {
        { "XMLID", "NormallyOn", "normallyOnSpecial" },
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
    
    public void normallyOnSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {

        ability.setNormallyOn( attrValue.equals("AUTOACTIVATE") );
    }
    
   public boolean identifyXML(String XMLID, Node node) {
        
        return ( XMLID != null && ( XMLID.equals("AUTOACTIVATE") || XMLID.equals("NOAUTOACTIVATE") ) );
    }
}
