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
public abstract class AbstractPowerXMLAdapter extends AbstractXMLAdapter implements PowerXMLAdapter {
    
    /** Creates a new instance of AbstractPowerXMLAdapter */
    public AbstractPowerXMLAdapter() {
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
}
