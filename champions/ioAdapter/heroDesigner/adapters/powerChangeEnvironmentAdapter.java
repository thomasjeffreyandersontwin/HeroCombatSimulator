/*
 * powerChangeEnvironment.java
 *
 * Created on April 11, 2004, 10:11 AM
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
public class powerChangeEnvironmentAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "CHANGEENVIRONMENT";
    private static String[][] translationArray = {
        { "LEVELS", "Level" },
        { "ADDER(XMLID=PERROLL).LEVELS", "PERmodifier", "negateSpecial"},
        { "ADDER(XMLID=PERROLL).OPTIONID", "Senses" },
        { "ADDER(XMLID=PERROLL).OPTIONID", "SenseGroup" },
        { "ADDER(XMLID=CHARORSKILLROLL).LEVELS", "STATSkillModifier" },
        { "ADDER(XMLID=MOVEMENT).LEVELS", "MovementModifier" },
        { "ADDER(XMLID=TEMPERATURE).LEVELS", "TemperatureAffected" },
        { "ADDER(XMLID=TEMPERATUREINCREASE).LEVELS", "TemperatureAffected" },
        { "ADDER(XMLID=TEMPERATURE)", "TemperatureNegative", "trueSpecial"},
        { "ADDER(XMLID=CHARANDSKILLROLL).LEVELS", "STATmodifier" },
        { "ADDER(XMLID=OCVDCV).LEVELS", "OCVmodifier" },
        //{ "ADDER(OPTIONID=DCV).LEVELS", "DCVmodifier" },
        { "ADDER(XMLID=DAMAGE).LEVELS", "DamagingModifier" },
        { "ADDER(XMLID=TKSTR).LEVELS", "TelekinesisSTR" },
        { "ADDER(XMLID=LONG).OPTION_ALIAS", "FadeRate" },
//        { "ADDER(XMLID=LONG).LEVELS", "FadeRateLevel" },
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
    
        /** CustomHandler for processing parameter.
     *
     */
    public void negateSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        int i = Integer.parseInt(attrValue);
        
        pl.setParameterValue(parameterName, new Integer( -1 * i ) );
    }
}
