/*
 * powerGrowth.java
 *
 * Created on April 14, 2004, 12:12 AM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;
import org.w3c.dom.Node;
import xml.DefaultXMLHandler;

/**
 *
 * @author  1425
 */
public class skillScienceSkillAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "SCIENCE_SKILL"; // This is not right!!!
    private static String[][] translationArray = {
        { "LEVELS", "Levels" },
        { "CHARACTERISTIC", "BaseStat" },
        { "INPUT", "Science" }
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
    

//    public boolean identifyXML(String XMLID, Node node) {
//       
//        String c = DefaultXMLHandler.findAttribute(node, "CHARACTERISTIC");
//        String i = DefaultXMLHandler.findAttribute(node, "INPUT");
//        if ( node != null && node.getNodeName().equals("SKILL") && c != null && c.equals("GENERAL") == false && i == null) {
//            return true;
//        }
//        return false;
//    }
    
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

   
}
