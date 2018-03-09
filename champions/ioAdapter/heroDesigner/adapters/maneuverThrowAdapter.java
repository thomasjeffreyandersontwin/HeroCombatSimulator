/*
 * powerEnergyBlast.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.parameters.ParameterList;
import champions.interfaces.SpecialParameter;
import champions.ioAdapter.heroDesigner.AbstractManeuverXMLAdapter;
import champions.ioAdapter.heroDesigner.HDImportError;
import champions.ioAdapter.heroDesigner.ManeuverXMLAdapter;
import champions.powers.SpecialParameterFixedCost;
import champions.powers.SpecialParameterIsMartialManeuver;
import org.w3c.dom.Node;
import xml.XMLParseError;


/**
 *
 * @author  1425
 */
public class maneuverThrowAdapter extends AbstractManeuverXMLAdapter implements ManeuverXMLAdapter {
    
    private static String[] displayArray = { "Martial Throw", "Sacrifice Throw", "Legsweep"
                                            };
                                            
    private static String[][] translationArray = {
        { "OCV", null, "ocvSpecial" },
        { "DCV", null, "dcvSpecial" },
        { "DC" , null, "dcSpecial" },
        { "BASECOST", null, "costSpecial" },
    };
    
    /** Returns the Translation Array for the PowerAdapter.
     *
     * The Subclass should either override this to return their translationArray
     *or override the importXML method to do more complicated import tasks.
     */
    public String[][] getTranslationArray() {
        return translationArray;
    }
    
    public String[] getDisplayArray() {
        return displayArray;
    }
    
    /** CustomHandler for processing parameter.
     *
     */
    public void ocvSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        try {
            if ( attrValue.startsWith("+") ) {
                attrValue = attrValue.substring(1);
            }
            int ocvMod = Integer.parseInt(attrValue);
            ability.setOCVModifier(ocvMod);
        }
        catch ( NumberFormatException nfe ) {
             
        }
    }
    
    /** CustomHandler for processing parameter.
     *
     */
    public void dcvSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        try {
            if ( attrValue.startsWith("+") ) {
                attrValue = attrValue.substring(1);
            }
            int dcvMod = Integer.parseInt(attrValue);
            ability.setDCVModifier(dcvMod);
        }
        catch ( NumberFormatException nfe ) {
            
        }
        
        
    }
    
    /** CustomHandler for processing parameter.
     *
     */
    public void dcSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
            if (!attrValue.equals("0")) {
            	if(!pl.contains("DamageDie"))
            		pl.addStringParameter("DamageDie", "DamageDie", "DamageDie", attrValue + "d6");
            	else
            		pl.setParameterValue("DamageDie", attrValue + "d6");  
            }
    }
    
        /** CustomHandler for processing parameter.
     *
     */
    public void costSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        try {
            float dcvMod = Float.parseFloat(attrValue);
            SpecialParameter sp = new SpecialParameterFixedCost();
            ability.addSpecialParameter( sp );
            int index = ability.findIndexed("SpecialParameter", "SPECIALPARAMETER", sp);
            ParameterList spl = sp.getParameterList(ability,index);
            spl.setParameterValue("FixedCPCost", new Integer( Math.round(dcvMod)) );
            sp.configure(ability, spl);
        }
        catch ( NumberFormatException nfe ) {
            
        }
        
        
    }
    
    public XMLParseError importXML(Ability ability, Node node) {
        XMLParseError result = super.importXML(ability, node);
        if ( (result == null || result.getErrorSeverity() < HDImportError.IMPORT_FAILURE) ) {
            ability.addSpecialParameter( new SpecialParameterIsMartialManeuver() );
        }
        return result; 
    }
    
}
