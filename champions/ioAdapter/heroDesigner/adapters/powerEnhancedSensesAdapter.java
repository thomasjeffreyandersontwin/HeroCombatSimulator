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
public class powerEnhancedSensesAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String[][] XMLIDs = new String[][] { 
        {"ACTIVESONAR", "Active Sonar"},
        {"DETECT", "Detect"},
        {"ENHANCEDPERCEPTION", "All Senses"},
        {"HRRP", "High Range Radio Perception"},
        {"INFRAREDPERCEPTION", "Infrared Vision"},
        {"MENTALAWARENESS", "Mental Awareness"},
        {"NIGHTVISION", "Night Vision"}, 
        {"NRAYPERCEPTION", "N-Ray Perception"},
        {"RADAR", "Radar Sense"},
        {"RADIOPERCEIVETRANSMIT", "Radio Listen and Transmit"},
        {"RADIOPERCEPTION", "Radio Hearing"},
        {"SPATIALAWARENESS", "Spatial Awareness"},
        {"ULTRASONICPERCEPTION", "Ultrasonic Hearing"},
        {"ULTRAVIOLETPERCEPTION", "Ultraviolet Vision"},
    };
        
        
        
        
    
    //private static String XMLID = "DETECT"; // Ignore this...
    private static String[][] translationArray = {
//        { "LEVELS", "Level" },
        { "XMLID", "EnhancedSense", "enhancedSenseSpecial" },
        { "ADDER(XMLID=DISCRIMINATORY)", "Discriminatory", "trueSpecial" },
        { "ADDER(XMLID=MICROSCOPIC).LEVELS", "Magnification", "trueSpecial" },
 //       { "ADDER(XMLID=SELFCONTAMICROSCOPICINEDBREATHING).LEVELS", "Magnification" },
  //      { "ADDER(XMLID=TELESCOPIC)", "TelescopicSense", "trueSpecial" },
        { "ADDER(XMLID=TELESCOPIC).LEVELS", "TelescopicLevel" },
        { "ADDER(XMLID=TARGETINGSENSE)", "Targeting", "trueSpecial" },
        { "ADDER(XMLID=RANGEDSENSE)", "Ranged", "trueSpecial" },
 //       { "ADDER(XMLID=INCREASEDARC360)", "Sensing360", "trueSpecial" },
//        { "ADDER(XMLID=HIGHRADIATION)", "SafeEnvironmentHighRadiation", "trueSpecial" },
//        { "ADDER(XMLID=INTENSECOLD)", "SafeEnvironmentIntenseCold", "trueSpecial" },
//        { "ADDER(XMLID=INTENSEHEAT)", "SafeEnvironmentIntenseHeat", "trueSpecial" },
//        { "ADDER(XMLID=LONGEVITY).OPTION_ALIAS", "Longevity" },
//        { "ADDER(XMLID=IMMUNITY).LEVELS", "Immunity" },
//        { "ADDER(XMLID=IMMUNITY).OPTION_ALIAS", "LifeSupportImport" },

    };
    
    public boolean identifyXML(String XMLID, Node node) {
        if ( XMLID == null ) return false;
        
        for(int i = 0; i < XMLIDs.length; i++) {
            if ( XMLIDs[i][0].equals(XMLID) ) return true;
        }
        return false;
    }
    
    /** Returns the XMLID for this Power.
     *
     * Subclass should either override this to return their XMLID or override
     * the identify method to do more complicated identification tasks.
     */
    public String getXMLID() {
        return null;
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
    public void enhancedSenseSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        for(int i = 0; i < XMLIDs.length; i++) {
            if ( XMLIDs[i][0].equals(attrValue) ) {
                // This is the correct one, so set the enhancedSense level from
                // the second parameter of the XMLIDs array from above.
                pl.setParameterValue("EnhancedSense", XMLIDs[i][1]);
                ability.getPower().configurePAD(ability, pl);
                break;
            }
        }  
    }
}
