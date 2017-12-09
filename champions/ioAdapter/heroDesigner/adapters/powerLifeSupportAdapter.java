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
import champions.powers.powerLifeSupport.Immunity;
import org.w3c.dom.Node;
import xml.DefaultXMLHandler;

/**
 *
 * @author  1425
 */
public class powerLifeSupportAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "LIFESUPPORT";
    private static String[][] translationArray = {
        { "ADDER(XMLID=EXTENDEDBREATHING).OPTION_ALIAS", "ExtendedBreathing" },
        { "ADDER(XMLID=EXPANDEDBREATHING).ALIAS", "ExpandedBreathing" },
        { "ADDER(XMLID=SELFCONTAINEDBREATHING)", "SelfContainedBreathing", "trueSpecial" },
        { "ADDER(XMLID=EATING).OPTION_ALIAS", "DiminishedEating" },
        { "ADDER(XMLID=SLEEPING).OPTION_ALIAS", "DiminishedSleep" },
        { "ADDER(XMLID=LOWPRESSUREVACUUM)", "SafeEnvironmentLowPressure", "trueSpecial" },
        { "ADDER(XMLID=HIGHPRESSURE)", "SafeEnvironmentHighPressure", "trueSpecial" },
        { "ADDER(XMLID=HIGHRADIATION)", "SafeEnvironmentHighRadiation", "trueSpecial" },
        { "ADDER(XMLID=INTENSECOLD)", "SafeEnvironmentIntenseCold", "trueSpecial" },
        { "ADDER(XMLID=INTENSEHEAT)", "SafeEnvironmentIntenseHeat", "trueSpecial" },
        { "ADDER(XMLID=ZEROG)", "SafeEnvironmentZeroGravity", "trueSpecial" },
        { "ADDER(XMLID=LONGEVITY).OPTION_ALIAS", "Longevity" },
        { "ADDER(XMLID=EXPANDEDBREATHING).ALIAS", "ExpandedBreathing" },
        { "ADDER(XMLID=IMMUNITY)", "", "immunitySpecial"}
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
    
    /*
       <ADDER XMLID="IMMUNITY" ID="1163267327181" BASECOST="2.0" LEVELS="0" ALIAS="Immunity" 
            POSITION="-1" MULTIPLIER="1.0" GRAPHIC="Burst" COLOR="255 255 255" SFX="Default" 
            SHOW_ACTIVE_COST="Yes" OPTION="COLD" OPTIONID="COLD" OPTION_ALIAS="Common Cold/Flu" 
            INCLUDE_NOTES_IN_PRINTOUT="Yes" NAME="" SHOWALIAS="Yes" PRIVATE="No" REQUIRED="No" 
            GROUP="No" SELECTED="YES">
      
     **/
    public void immunitySpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        String alias = DefaultXMLHandler.findAttribute(node, "OPTION_ALIAS");
        String costAttr = DefaultXMLHandler.findAttribute(node, "BASECOST");
        
        int cost = 3;
        try {
            double d = Double.parseDouble(costAttr);
            cost = (int)d;            
        }
        catch ( NumberFormatException nfe ) {
            
        }
        
        Immunity i = new Immunity(alias, cost);
        
        pl.addIndexedParameterValue("Immunity", i);
    }
}
