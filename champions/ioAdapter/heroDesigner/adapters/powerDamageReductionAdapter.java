/*
 * powerEnergyBlast.java
 *
 * Created on March 29, 2004, 10:11 PM
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
public class powerDamageReductionAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "DAMAGEREDUCTION";
    private static String[][] translationArray = {
        { "OPTIONID", null, "damagereductionSpecial" },
        { "INPUT", "DamageType", "damageTypeSpecial" },
        
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
    
    
    /** CustomHandler for processing parameter.
     *
     */
    public void damagereductionSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        //        LVL50RESISTANT
        //        String test;
        if (attrValue.endsWith("RESISTANT")) {
            pl.setParameterValue("Resistent", "TRUE");
        }
        if (attrValue.startsWith("LVL25")) {
            pl.setParameterValue("Percent", new Integer(25));
        }
        else if (attrValue.startsWith("LVL50")) {
            pl.setParameterValue("Percent", new Integer(50));
        }        // if ( "TURN".(attrValue) ) {
        else if (attrValue.startsWith("LVL75")) {
            pl.setParameterValue("Percent", new Integer(75));
        }
    }
    /** CustomHandler for processing parameter.
     *
     */
    public void damageTypeSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        
        if ( "Physical".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "PD");
        }
        else {
            pl.setParameterValue(parameterName, "ED");
        }
    }
}
