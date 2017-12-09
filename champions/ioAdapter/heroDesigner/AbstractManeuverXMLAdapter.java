/*
 * AbstractPowerXMLAdapter.java
 *
 * Created on April 3, 2004, 10:37 AM
 */

package champions.ioAdapter.heroDesigner;

import champions.Ability;
import champions.parameters.ParameterList;
import champions.Power;
import org.w3c.dom.Node;
import xml.DefaultXMLParseError;
import xml.XMLParseError;

/**
 *
 * @author  1425
 */
public abstract class AbstractManeuverXMLAdapter extends AbstractXMLAdapter implements ManeuverXMLAdapter {
    
    /** Creates a new instance of AbstractPowerXMLAdapter */
    public AbstractManeuverXMLAdapter() {
    }
    
    public XMLParseError importXML(Ability ability, Node node) {
        Power power = ability.getPower();
        if ( power == null ) return new DefaultXMLParseError("Unknown Error Occurred: Power == null", HDImportError.IMPORT_FAILURE);
        
        ParameterList pl = power.getParameterList(ability, -1);
     
        return importXML(ability, node, pl);
    }
    
    /** Finilizes the Import of a power/adder from an XML source.
     *
     * This method should be called after the Ability has been added to it's
     * source (if one exists).  If called with a null source, then the 
     * ability (and the power and adders) can assume that it will not be
     * attached to a specific source.
     */
    public XMLParseError finalize(Ability ability) {
        return null;
    }
    
    public boolean identifyXML(String XMLID, Node node) {
        if ( XMLID == null || !XMLID.equals("MANEUVER") ) return false;
        
        Node displayNode = node.getAttributes().getNamedItem("DISPLAY");
        if ( displayNode != null ) {
            String display = displayNode.getNodeValue();

            String[] names = getDisplayArray();
            for(int i = 0; i < names.length; i++) {
                if ( display.equals(names[i])) {
                    return true;
                }
            }
        }
        return false;
        
    }
    
    public String[] getDisplayArray() {
        return new String[] {};
    }
    
    public String getXMLID(){
        return "MANEUVER";
    }
    
}
