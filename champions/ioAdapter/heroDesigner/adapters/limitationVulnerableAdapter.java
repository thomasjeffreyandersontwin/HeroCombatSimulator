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
public class limitationVulnerableAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "VULNERABLE";
    private static String[][] translationArray = {
    		 { "OPTION", null, "vulnerabilitySpecial" }, 

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
    public void vulnerabilitySpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData)
    {
	    NamedNodeMap attrs = node.getAttributes();
        
        String common;
        common  = attrs.getNamedItem("OPTION_ALIAS").getNodeValue();
        
        
        pl.setParameterValue("attackType", common);
        String vulnerability = attrs.getNamedItem("COMMENTS").getNodeValue();
        pl.setParameterValue("effect", vulnerability);
        
    }
}
