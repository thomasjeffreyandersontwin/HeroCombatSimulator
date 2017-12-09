/*
 * limitationLimitedMedium.java
 *
 * Created on April 10, 2004, 2:36 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.parameters.ParameterList;
import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;
import champions.powers.limitationConditionalPower;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 *
 * @author  Kevin Richard
 */
public class limitationConditionalPowerAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "CONDITIONALPOWER";
    private static String[][] translationArray = {
        // This will do both the level & reason
        { "OPTION", null, "conditionalPowerSpecial" }, 

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
    
    /** specialHandler that translates HD Shape option to HCS shape option.
     *
     */
    public void conditionalPowerSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        NamedNodeMap attrs = node.getAttributes();
        
        String option = attrs.getNamedItem("OPTION").getNodeValue();

        String reason;
        reason  = attrs.getNamedItem("ALIAS").getNodeValue();
        
        if ( reason.equals("") ) {
            reason  = attrs.getNamedItem("OPTION_ALIAS").getNodeValue();
        }
        
        if ( option.equals("VERYUNCOMMON")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[0]);
        }
        else if ( option.equals("UNCOMMON")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[1]);
        }
        else if ( option.equals("COMMON")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[2]);
        }
        else if ( option.equals("VERYCOMMON")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[3]);
        }
        else if ( option.equals("EXTREMELYCOMMON")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[4]);
        }
        else if ( option.equals("UBIQUITOUS")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[5]);
        }
        else if ( option.equals("DAYLIGHT")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[1]);
            // Switch the name to the daylight comment...
            if ( reason.equals("Conditional Power")) reason  = attrs.getNamedItem("OPTION_ALIAS").getNodeValue();
        }
        else if ( option.equals("ONGROUND")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[1]);
            // Switch the name to the daylight comment...
            if ( reason.equals("Conditional Power")) reason  = attrs.getNamedItem("OPTION_ALIAS").getNodeValue();
        }
        else if ( option.equals("DARKNESS")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[2]);
            // Switch the name to the daylight comment...
            if ( reason.equals("Conditional Power")) reason  = attrs.getNamedItem("OPTION_ALIAS").getNodeValue();
        }
        else if ( option.equals("TWILIGHT")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[3]);
            // Switch the name to the daylight comment...
            if ( reason.equals("Conditional Power")) reason  = attrs.getNamedItem("OPTION_ALIAS").getNodeValue();
        }        
        else if ( option.equals("WATER")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[4]);
            // Switch the name to the daylight comment...
            if ( reason.equals("Conditional Power")) reason  = attrs.getNamedItem("OPTION_ALIAS").getNodeValue();
        }
        else if ( option.equals("RAINSTORM")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[4]);
            // Switch the name to the daylight comment...
            if ( reason.equals("Conditional Power")) reason  = attrs.getNamedItem("OPTION_ALIAS").getNodeValue();
        }        
                else if ( option.equals("TORNADOES")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[5]);
            // Switch the name to the daylight comment...
            if ( reason.equals("Conditional Power")) reason  = attrs.getNamedItem("OPTION_ALIAS").getNodeValue();
        }
                else if ( option.equals("MAGNETIC")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[5]);
            // Switch the name to the daylight comment...
            if ( reason.equals("Conditional Power")) reason  = attrs.getNamedItem("OPTION_ALIAS").getNodeValue();
        }
                else if ( option.equals("FULLMOON")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[5]);
            // Switch the name to the daylight comment...
            if ( reason.equals("Conditional Power")) reason  = attrs.getNamedItem("OPTION_ALIAS").getNodeValue();
        }
                else if ( option.equals("NODARKNESS")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[2]);
            // Switch the name to the daylight comment...
            if ( reason.equals("Conditional Power")) reason  = attrs.getNamedItem("OPTION_ALIAS").getNodeValue();
        }
                else if ( option.equals("NOWATER")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[1]);
            // Switch the name to the daylight comment...
            if ( reason.equals("Conditional Power")) reason  = attrs.getNamedItem("OPTION_ALIAS").getNodeValue();
        }
        //There are two options here because HD has a misspelling.  This should cover both
                else if ( option.equals("NOMAGNETIC") || option.equals("NOMAGETIC")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[1]);
            // Switch the name to the daylight comment...
            if ( reason.equals("Conditional Power")) reason  = attrs.getNamedItem("OPTION_ALIAS").getNodeValue();
        }
                else if ( option.equals("")) {
            pl.setParameterValue("Level", limitationConditionalPower.levelOptions[1]);
            // Switch the name to the daylight comment...
            if ( reason.equals("Conditional Power")) reason  = attrs.getNamedItem("OPTION_ALIAS").getNodeValue();
        }
        pl.setParameterValue("Condition", reason);
    }
}
