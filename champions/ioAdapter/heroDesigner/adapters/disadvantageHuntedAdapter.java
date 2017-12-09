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
public class disadvantageHuntedAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "HUNTED";
    private static String[][] translationArray = {
        { "ADDER(XMLID=APPEARANCE).OPTION_ALIAS", "ActivationRoll", "activationRollSpecial"},
        { "ADDER(XMLID=CAPABILITIES).OPTION", "Capabilities", "capabilitiesSpecial"},
        { "ADDER(XMLID=MOTIVATION).OPTION_ALIAS", "Motivation", "motivationSpecial" },
        { "ADDER(XMLID=LIMITED)", "LimitedGeographicalArea", "trueSpecial" },
        { "ADDER(XMLID=NCI)", "ExtensiveNonCombatInfluence", "trueSpecial" },
        { "ADDER(XMLID=PUBLIC)", "PCHasAPublicIDOrIsOtherwiseVeryEasyToFind", "trueSpecial" },
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
    public void capabilitiesSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        //    static public String[] capabilitiesOptions = {"Less Powerful","As Powerful","More Powerful"};
        
        if ( "LESS".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Less Powerful");
        }
        else if ( "AS".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "As Powerful");
        }
        else if ( "MORE".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "More Powerful");
        }
    }
    
    /** specialHandler that translates HD Shape option to HCS shape option.
     *
     */
    public void motivationSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        //    static public String[] capabilitiesOptions = {"Less Powerful","As Powerful","More Powerful"};
        
        if ( "Watching".equals(attrValue) ) {
            attrValue = "Watched";
        }
        pl.setParameterValue(parameterName, attrValue);
    }
    
    /** specialHandler that translates HD Shape option to HCS shape option.
     *
     */
    public void activationRollSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "11-".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "11");
        }
        else if ( "14-".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "14");
        }
        else if ( "8-".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "8");
        }
    }
}
