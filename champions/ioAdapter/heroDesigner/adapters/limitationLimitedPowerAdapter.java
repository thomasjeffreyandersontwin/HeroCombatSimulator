/*
 * limitationLimitedMedium.java
 *
 * Created on April 10, 2004, 2:36 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.parameters.ParameterList;
import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;
import champions.powers.limitationLimitedPower;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 *
 * @author  Kevin Richard
 */
public class limitationLimitedPowerAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "LIMITEDPOWER";
    private static String[][] translationArray = {
        // This will do both the level & reason
        { "OPTION", null, "limitedPowerSpecial" }, 

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
    public void limitedPowerSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        NamedNodeMap attrs = node.getAttributes();
        
        String option = attrs.getNamedItem("OPTION").getNodeValue();
        if ( option.equals("ONLYONWOMEN")) {
            pl.setParameterValue("Level", limitationLimitedPower.levelOptions[3]);
        }
        else if ( option.equals("NOSTUN")) {
            pl.setParameterValue("Level", limitationLimitedPower.levelOptions[6]);
        }
        else {
            try {
                int i = Integer.parseInt(option);
                pl.setParameterValue("Level", limitationLimitedPower.levelOptions[i-1]);
            }
            catch ( NumberFormatException nfe ) {
                System.out.println("Error importing Limitation limited power: Option = " + option + ". Expected an integer but didn't get one.");
            }
        }
        
        // Now try to fix up the name...
        String reason;
        reason  = attrs.getNamedItem("ALIAS").getNodeValue();
        
        if ( reason.equals("") ) {
            reason  = attrs.getNamedItem("OPTION_ALIAS").getNodeValue();
        }
        
        pl.setParameterValue("Reason", reason);
    }
}
