/*
 * advantageAreaEffect.java
 *
 * Created on April 10, 2004, 2:36 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.parameters.ParameterList;
import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;
import org.w3c.dom.Node;


/**
 *
 * @author  Kevin Richard
 */
public class advantageTakesNoDamageFromAttacksAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "TAKESNODAMAGE";
    private static String[][] translationArray = {
        { "OPTION", "Type", "typeSpecial" },
//        { "", "IncreasedAreaImport" },
//        { "", "IncreasedAreaLevel" }
        //{ "ADDER(XMLID=IMPROVEDNONCOMBAT).LEVELS", "NoncombatX"}
        //{ "ADDER(XMLID=ODDPOWER).TRUE", "OddPower" } // Not supported by HCS
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
    
    /** specialHandler that translates HD Shape option to HCS shape option.
     *
     */
    public void typeSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "LIMITED".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Physical");
        }
        else {
            pl.setParameterValue(parameterName, "All Attacks");
        }
    }
}
