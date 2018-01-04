/*
 * powerGrowth.java
 *
 * Created on April 14, 2004, 12:12 AM
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
public class powerTunnelingAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "TUNNELING";
    private static String[][] translationArray = {
        { "LEVELS", "DistanceFromCollision" },
        { "ADDER(XMLID=DEFBONUS).LEVELS", "ExtraDEF" },
        { "ADDER(XMLID=FILLIN)", "FillIn", "trueSpecial" },
        { "ADDER(XMLID=IMPROVEDNONCOMBAT).LEVELS", "NoncombatX","NoncombatXSpecial"}
        
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
    
    public void NoncombatXSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        int i = Integer.parseInt(attrValue);
        double p;
        p = Math.pow( 2, i + 1 );
        int ncValue = (int) p;
        
        pl.setParameterValue("NoncombatX", new Integer(ncValue));
    }    
}
