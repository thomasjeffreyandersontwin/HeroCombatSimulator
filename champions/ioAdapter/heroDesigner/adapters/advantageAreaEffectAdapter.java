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
public class advantageAreaEffectAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "AOE";
    private static String[][] translationArray = {
        { "ADDER(XMLID=NONSELECTIVETARGET)", "Type", "setSpecial", "Nonselective" },
        { "ADDER(XMLID=SELECTIVETARGET)", "Type", "setSpecial", "Selective" },
        { "OPTION", "Shape", "shapeSpecial" },
        { "ADDER(XMLID=DOUBLEAREA).LEVELS", "IncreasedAreaLevel" },
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
    public void shapeSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "HEX".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "One-hex");
        }
        else if ( "RADIUS".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Radius");
        }
        else if ( "CONE".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Cone");
        }
        else if ( "LINE".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Line");
        }
        else if ( "ANY".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Any Area");
        }
    }
}
