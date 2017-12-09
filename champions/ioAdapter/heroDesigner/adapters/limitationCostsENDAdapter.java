/*
 * limitationCostsEND.java
 *
 * Created on April 10, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;
import champions.Ability;
import champions.parameters.ParameterList;
import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;
import org.w3c.dom.Node;

/**
 *
 * @author  1425
 */
public class limitationCostsENDAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter {
    
    private static String XMLID = "COSTSEND";
    private static String[][] translationArray = {
        { "OPTION", "EndTypes", "endSpecial"},
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

    
      public void endSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "ACTIVATE".equals(attrValue) ) {
            pl.setParameterValue("EndTypes", "Only Costs END to Activate");
        } else if ( "EVERYPHASE".equals(attrValue) ) {
            pl.setParameterValue("EndTypes", "Costs END Every Phase");
        }
    }  
    
    
    
    
    
    
    
    
    
}
