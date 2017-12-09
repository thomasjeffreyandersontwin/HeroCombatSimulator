/*
 * powerEnergyBlast.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.Battle;
import champions.parameters.ParameterList;
import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.HDImportError;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;
import org.w3c.dom.Node;
import xml.XMLParseError;
import xml.XMLParseErrorList;


/**
 *
 * @author  1425
 */
public class powerExtraDimensionalMovementAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "EXTRADIMENSIONALMOVEMENT";
    private static String[][] translationArray = {
        { "OPTION", null, "dimensionSpecial" },
        { "ADDER(XMLID=INCREASEDWEIGHT).LEVELS", "WeightMultiplier","weightMultiplierSpecial" },
        { "ADDER(XMLID=POSITIONSHIFT)", "PositionShift", "trueSpecial" },
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
    
    /** Imports Parameters.
     *
     * Override this method to provide custom import stuff.  This should be
     * necessary in most cases!!!
     */
   /*  public boolean importXML(Ability ability, Node node) {
         // Make sure we do the normal import first...
         boolean result = super.importXML(ability,node);
    
        // Stupid STUN Only implemented as an advantage in Hero Designer...
         // How the hell is that an Advantage?!?
    
         // Search the children for a node with name of "MODIFIER" and
         // XMLID of STUNONLY
         Node child = node.getFirstChild();
         while( child != null ) {
             if ( "MODIFIER".equals(child.getNodeName()) &&
                  "STUNONLY".equals(child.getAttributes().getNamedItem("XMLID").getNodeValue() ) ) {
                  ParameterList pl = ability.getPower().getParameterList(ability,-1);
                  pl.setParameterValue("StunOnly", "TRUE");
                  break;
             }
    
             child = child.getNextSibling();
         }
    
         return result;
     }*/
    
    public void weightMultiplierSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        int i = Integer.parseInt(attrValue);
        double p;
        p = Math.pow( 2, i );
        int ncValue = (int) p;
        
        pl.setParameterValue("WeightMultiplier", new Integer(ncValue));
    }
    
    /** CustomHandler for processing parameter.
     *
     */
    public void clsAttachedToSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        ability.createIndexed("CanUseCLImport", "ABILITY", attrValue, false);
        
    }
    
    /** CustomHandler for processing parameter.
     *
     */
    public void dimensionSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if (attrValue.equals("SINGLE")) {
            pl.setParameterValue("Dimension", "Single Dimension");
        } else if (attrValue.equals("SINGLEANY")) {
            pl.setParameterValue("Dimension", "Single Dimension, Any Location");
        } else if (attrValue.equals("SINGLECORRESPOND")) {
            pl.setParameterValue("Dimension", "Single Dimension, Any Location corresponding to current physical location");
        } else if (attrValue.equals("RELATEDGROUPSINGLE")) {
            pl.setParameterValue("Dimension", "Related Group of Dimensions, Single Location");
        } else if (attrValue.equals("RELATEDGROUP")) {
            pl.setParameterValue("Dimension", "Related Group of Dimensions, Any Location");
        } else if (attrValue.equals("RELATEDGROUPCORRESPOND")) {
            pl.setParameterValue("Dimension", "Related Group of Dimensions, Any Location corresponding to current physical location");
        } else if (attrValue.equals("ANYSINGLE")) {
            pl.setParameterValue("Dimension", "Any Dimension, Single Location");
        } else if (attrValue.equals("ANY")) {
            pl.setParameterValue("Dimension", "Any Dimension, Any Location");
        } else if (attrValue.equals("ANYCORRESPOND")) {
            pl.setParameterValue("Dimension", "Any Dimension, Any Location corresponding to current physical location");
        } else if (attrValue.equals("SINGLETIME")) {
            pl.setParameterValue("Dimension", "Single Point in Time, Physical Location Same As Starting Location");
        } else if (attrValue.equals("ALLTIME")) {
            pl.setParameterValue("Dimension", "Related Group of Points in Time, Physical Location Same As Starting Location");
        } else if (attrValue.equals("GROUPTIME")) {
            pl.setParameterValue("Dimension", "Any Point in Time, Physical Location Same As Starting Location");
        } else if (attrValue.equals("SINGLETIMESINGLE")) {
            pl.setParameterValue("Dimension", "Single Point in Time, Single Physical Location Other Than Starting Location");
        } else if (attrValue.equals("ALLTIMESINGLE")) {
            pl.setParameterValue("Dimension", "Related Group of Points in Time, Single Physical Location Other Than Starting Location");
        } else if (attrValue.equals("GROUPTIMESINGLE")) {
            pl.setParameterValue("Dimension", "Any Point in Time, Single Physical Location Other Than Starting Location");
        } else if (attrValue.equals("SINGLETIMEGROUP")) {
            pl.setParameterValue("Dimension", "Single Point in Time, Limited Group Of Physical Locations");
        } else if (attrValue.equals("GROUPTIMEGROUP")) {
            pl.setParameterValue("Dimension", "Related Group of Points in Time, Limited Group Of Physical Locations");
        } else if (attrValue.equals("ALLTIMEGROUP")) {
            pl.setParameterValue("Dimension", "Any Point in Time, Limited Group Of Physical Locations");
        } else if (attrValue.equals("SINGLETIMEGROUP")) {
            pl.setParameterValue("Dimension", "Single Point in Time, Any Physical Location");
        } else if (attrValue.equals("GROUPTIMEANY")) {
            pl.setParameterValue("Dimension", "Related Group of Points in Time, Any Physical Location");
        } else if (attrValue.equals("GROUPTIME")) {
            pl.setParameterValue("Dimension", "Any Point in Time, Physical Location Same As Starting Location");
        }
    }
    
}
