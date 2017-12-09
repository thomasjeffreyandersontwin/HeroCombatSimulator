/*
 * powerEnergyBlast.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.PADRoster;
import champions.parameters.ParameterList;
import champions.interfaces.SpecialParameter;
import champions.ioAdapter.heroDesigner.AbstractManeuverXMLAdapter;
import champions.ioAdapter.heroDesigner.HDImportError;
import champions.ioAdapter.heroDesigner.ManeuverXMLAdapter;
import champions.powers.SpecialParameterIsMartialManeuver;
import org.w3c.dom.Node;
import xml.DefaultXMLHandler;
import xml.XMLParseError;


/**
 *
 * @author  1425
 */
public class maneuverStrikeAdapter extends AbstractManeuverXMLAdapter implements ManeuverXMLAdapter {
    
    private static String[] displayArray = { "Basic Strike", 
                                             "Charge",
                                             "Counterstrike",
                                             "Defensive Strike",
                                             "Martial Strike",
                                             "Offensive Strike",
                                             "Sacrifice Strike"
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

    public boolean identifyXML(String XMLID, Node node) {
        if ( XMLID.equals("MANEUVER") ) {
            String e = DefaultXMLHandler.findAttribute(node, "EFFECT");
            if ( e.equals("Strike") || e.equals("[NORMALDC] Strike")) {
                return true;
            }
        }
        
        return false;
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
            pl.setParameterValue("DamageDie", attrValue + "d6");  
    }
    
        /** CustomHandler for processing parameter.
     *
     */
    public void costSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        try {
            int cost = (int)Float.parseFloat(attrValue);
            if ( ability.hasSpecialParameter( "Fixed Cost" ) == false ) {
                // Create a fixed cost special parameter
                SpecialParameter sp = PADRoster.getNewSpecialParameterInstance("Fixed Cost");
                int index = ability.addSpecialParameterInfo(sp, "Fixed Cost");
                ParameterList spl = sp.getParameterList(ability, index);
                spl.setParameterValue( "FixedCPCost", cost);
                sp.configure(ability, spl);
            }
            else {
                int index = ability.getSpecialParameterIndex( PADRoster.getSharedSpecialParameterInstance("Fixed Cost"));
                SpecialParameter sp = ability.getSpecialParameter(index);
                ParameterList spl = sp.getParameterList(ability, index);
                spl.setParameterValue( "FixedCPCost", cost);
                sp.configure(ability, spl);
            }
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
