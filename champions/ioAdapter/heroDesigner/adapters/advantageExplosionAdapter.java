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
public class advantageExplosionAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "EXPLOSION";
    private static String[][] translationArray = {
        { "LEVELS", "FadeRate" },
        { "ADDER(XMLID=NONSELECTIVETARGET)", "Type", "setSpecial", "Nonselective" },
        { "ADDER(XMLID=SELECTIVETARGET)", "Type", "setSpecial", "Selective" },
        { "OPTION", "Shape", "shapeSpecial" },
        
        
        
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
        if ( "NORMAL".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Radius");
        }
        else if ( "CONE".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Cone");
        }
        else if ( "LINE".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Line");
        }

    }
}
