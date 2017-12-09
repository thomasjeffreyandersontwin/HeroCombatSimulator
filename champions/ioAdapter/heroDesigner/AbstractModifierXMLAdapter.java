/*
 * AbstractPowerXMLAdapter.java
 *
 * Created on April 3, 2004, 10:37 AM
 */

package champions.ioAdapter.heroDesigner;

import champions.Ability;
import champions.parameters.ParameterList;
import champions.interfaces.PAD;
import champions.interfaces.SpecialParameter;
import org.w3c.dom.Node;
import xml.XMLParseError;



/**
 *
 * @author  1425
 */
public abstract class AbstractModifierXMLAdapter extends AbstractXMLAdapter implements ModifierXMLAdapter {
    
    /** Creates a new instance of AbstractPowerXMLAdapter */
    public AbstractModifierXMLAdapter() {
    }
    
    public XMLParseError importXML(Ability ability, Object modifier, int modifierIndex, Node node) {
        if ( modifier instanceof PAD ) {
            PAD pad = (PAD)modifier;
        
            ParameterList pl = pad.getParameterList(ability, modifierIndex);

            return importXML(ability, node, pl);
        }
        else if ( modifier instanceof SpecialParameter ) {
            SpecialParameter sp = (SpecialParameter)modifier;
            ParameterList pl = sp.getParameterList(ability, modifierIndex);
            
            return importXML(ability, node, pl);
        }
        
        return null;
    }
    
    /** Finalizes the Import of a adder from an XML source.
     *
     * This method should be called after the Ability has been added to it's
     * source (if one exists).  If called with a null source, then the 
     * ability (and the power and adders) can assume that it will not be
     * attached to a specific source.
     */
    public XMLParseError finalize(Ability ability, Object pad, int modifierIndex) {
        return null;
    }
   
}
