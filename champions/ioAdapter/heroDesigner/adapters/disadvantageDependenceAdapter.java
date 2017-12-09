/*
 * powerEnergyBlast.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.parameters.ParameterList;
import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;
import org.w3c.dom.Node;


/**
 *
 * @author  1425
 */
public class disadvantageDependenceAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "DEPENDENCE";
    private static String[][] translationArray = {
        { "ADDER(XMLID=EFFECT).OPTION_ALIAS", "Effect" },
        { "ADDER(XMLID=TIME).OPTION_ALIAS", "Time" },
        { "ADDER(XMLID=SUBSTANCE).OPTION", "Substance", "substanceIsSpecial"}
        //{ "MODIFIER(XMLID=MULTIPLIER).OPTION", "Multiplier", "multiplierSpecial" },
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
    public void substanceIsSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        //{"Uncommon Circumstance","Common Circumstance","Very Common Circumstance"};
        //{"Very Common","Easy To Obtain","Common","Difficult To Obtain","Uncommon","Extremely Difficult To Obtain"};
        if ( "VERYCOMMON".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Very Common");
        }
        else if ( "VERYCOMMON2".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Easy To Obtain");
        }
        else if ( "COMMON".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Common");
        }
        else if ( "COMMON2".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Difficult To Obtain");
        }
        else if ( "UNCOMMON".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Uncommon");
        }
        else if ( "UNCOMMON2".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Extremely Difficult To Obtain");
        }
    }
}
