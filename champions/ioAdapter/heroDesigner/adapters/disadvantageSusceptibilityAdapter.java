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
public class disadvantageSusceptibilityAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "SUSCEPTIBILITY";
    private static String[][] translationArray = {
        { "ADDER(XMLID=CONDITION).OPTION", "Condition", "conditionSpecial"},
        { "ADDER(XMLID=DAMAGE).OPTION_ALIAS", "Time", "timeSpecial" },
        { "ADDER(XMLID=DICE).OPTION", "DamageDie", "damageDieSpecial"},
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
    public void timeSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        //    static private String[] timeOptions = { "Effect Is Instant","1 Segment","1 Phase", "1 Turn", "1 Minute", "5 Minutes", "20 Minutes","1 Hour", "6 Hours", "1 Day", "1 Week", "1 Month", "1 Season"};
        
        if ( "Instant".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Effect Is Instant");
        }
        else if ( "per Segment".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "1 Segment");
        }
        else if ( "per Phase".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "1 Phase");
        }
        else if ( "per Turn".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "1 Turn");
        }
        else if ( "per Minute".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "1 Minute");
        }
        else if ( "per 5 Minutes".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "5 Minutes");
        }
        else if ( "per 20 Minutes".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "20 Minutes");
        }
        else if ( "per Hour".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "1 Hour");
        }
    }
    
    /** specialHandler that translates HD Shape option to HCS shape option.
     *
     */
    public void damageDieSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        pl.setParameterValue(parameterName, attrValue.replace('D', 'd'));
        
    }
    /** specialHandler that translates HD Shape option to HCS shape option.
     *
     */
    public void conditionSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "VERYCOMMON".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Very Common");
        }
        else if ( "COMMON".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Common");
        }
        else if ( "UNCOMMON".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Uncommon");
        }
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
