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
public class disadvantageAccidentalChangeAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "ACCIDENTALCHANGE";
    private static String[][] translationArray = {
        { "ADDER(XMLID=CIRCUMSTANCES).OPTION", "CircumstanceIs", "circumstanceIsSpecial"},
        { "ADDER(XMLID=CHANCETOCHANGE).OPTION_ALIAS", "ActivationRoll", "activationRollSpecial"},
        
    };
    
    /** Returns the XMLID for this Power.
     *
     * Subclass should either override this to return their XMLID or override
     * the identify method to do more complicated identification tasks.
     */
    public String getXMLID() {
        return XMLID;
    }
    
    /** Returns the Translation Array for the Power.
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
    
    /** specialHandler that translates HD Shape option to HCS shape option.
     *
     */
    public void activationRollSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "11-".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "11");
        }
        else if ( "14-".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "14");
        }
        else if ( "8-".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "8");
        }
        else if ( "Always".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Always");
        } 
    }
    /** specialHandler that translates HD Shape option to HCS shape option.
     *
     */
    public void circumstanceIsSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        //{"Uncommon Circumstance","Common Circumstance","Very Common Circumstance"};
        
        if ( "UNCOMMON".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Uncommon Circumstance");
        }
        else if ( "COMMON".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Common Circumstance");
        }
        else if ( "VERYCOMMON".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Very Common Circumstance");
        }
    }
}
