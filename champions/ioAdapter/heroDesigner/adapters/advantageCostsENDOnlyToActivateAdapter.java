/*
 * advantageVariableEffect.java
 *
 * Created on April 10, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.ChampionsMatcher;
import champions.Ability;
import champions.parameters.ParameterList;
import champions.Power;
import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;
import champions.powers.powerAid;
import champions.powers.powerDrain;
import org.w3c.dom.Node;
/**
 *
 * @author  1425
 */
public class advantageCostsENDOnlyToActivateAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "COSTSENDONLYTOACTIVATE";
    private static String[][] translationArray = {
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
}
