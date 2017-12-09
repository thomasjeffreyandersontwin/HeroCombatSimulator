/*
 * XMLAdapter.java
 *
 * Created on April 3, 2004, 10:33 AM
 */

package champions.ioAdapter.heroDesigner;

import org.w3c.dom.Node;

/** Iterface containing methods common to all XMLAdapters.
 *
 * @author  1425
 */
public interface XMLAdapter {
    /** Idetifies an XML Element based upon the XMLID and other attributes.<P>
     *
     * This method should return true if the class determines that the provided 
     * node identifies a adder of this type.<P>
     *
     * This method should only return true if it is certain of the identification.<P>
     *
     * The XMLID parameter provides a schema specific idenifier that can be used 
     * to idenfity the element.  In most cases, this data is also contained
     * within the provided node and is there only for conviencence.<P>
     *
     * For more information on the process used to import Abilities (and Characters)
     * from XML, see the {@link champions.Ability Ability} class for more information.<P>
     *
     * @param XMLID Idenifier from XML for element.  
     * @param node Document Object Model element node to be identified.
     */
    public boolean identifyXML(String XMLID, Node node);
}
