/*
 * powerEnergyBlast.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;
import champions.parameters.ParameterList;
import org.w3c.dom.Node;

/**
 *
 * @author  1425
 */
public class powerAutomatonAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "AUTOMATON";
    private static String[][] translationArray = {
        { "OPTION", "Type", "typeSpecial" },
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
     * or override the importXML method to do more complicated import tasks.
     */
    public String[][] getTranslationArray() {
        return translationArray;
    }
    
    /** CustomHandler for processing parameter.
     *
     */
    public void typeSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
            
            if ( attrValue.equals("CANNOTBESTUNNED") ) {
                pl.setParameterValue("Type","Cannot Be Stunned" );
            }
            else if ( attrValue.equals("NOSTUN1")) {
                pl.setParameterValue("Type","Take No Stun (loses abilities when takes BODY)" );
            }
            else {
                pl.setParameterValue("Type","Take No Stun" );
            }
    }

}
