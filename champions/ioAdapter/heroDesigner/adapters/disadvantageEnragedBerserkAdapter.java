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
public class disadvantageEnragedBerserkAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "ENRAGED";
    private static String[][] translationArray = {
        { "ADDER(XMLID=CIRCUMSTANCES).OPTION", "CircumstanceIs", "circumstanceSpecial"},
        { "ADDER(XMLID=CHANCETOGO).OPTIONID", "ActivationRoll", "activationRollSpecial"},
        { "ADDER(XMLID=CHANCETORECOVER).OPTIONID", "RecoveryRoll", "activationRollSpecial"},
        { "ADDER(XMLID=BERSERK)", "Berserk", "trueSpecial" },
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
    public void circumstanceSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
//    static public String[] circumstanceIsOptions = {"Uncommon Circumstance","Common Circumstance","Very Common Circumstance"};
        if ( "UNCOMMON".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Uncommon Circumstance");
        }
        else if ( "COMMON".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Common Circumstance");
        }
        else if ( "VERYCOMMON".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Very Common Circumstance");
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
