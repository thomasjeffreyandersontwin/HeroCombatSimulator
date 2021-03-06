/*
 * PowerXMLAdapter.java
 *
 * Created on March 29, 2004, 6:16 PM
 */

package champions.ioAdapter.heroDesigner;

import champions.Ability;
import org.w3c.dom.Node;
import xml.XMLParseError;

/**
 *
 * @author  1425
 */
public interface PowerXMLAdapter extends XMLAdapter {
    
    /** Requests the Power/Adder import it's parameter settings from the DOM node.<P>
     *
     * This method is called to initialize the parameter settings of a power/adder
     * from the DOM node provided.  The node will be a node from the indicated
     * XML schema format.<P>
     *
     * This method should not call configurePAD for the power.  That will be 
     * called seperately during the import process.<P>
     *
     * @param format Schema of XML source.
     * @param ability Ability power/adder belongs to.
     * @param node DOM Node containing power/adder information.
     *
     * @return Returns true if import was successful, false if an error occurs.
     */
    public XMLParseError importXML(Ability ability, Node node);
    
    /** Finilizes the Import of a power/adder from an XML source.
     *
     * This method should be called after the Ability has been added to it's
     * source (if one exists).  If called with a null source, then the 
     * ability (and the power and adders) can assume that it will not be
     * attached to a specific source.
     */
    public XMLParseError finalize(Ability ability);
}
