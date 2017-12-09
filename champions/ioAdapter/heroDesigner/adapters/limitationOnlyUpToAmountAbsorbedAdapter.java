/*
 * limitationSTRMinimum.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.ChampionsMatcher;
import champions.parameters.ParameterList;
import champions.interfaces.PAD;
import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.HDImportError;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;
import org.w3c.dom.Node;
import xml.XMLParseError;

/**
 *
 * @author  1425
 */
public class limitationOnlyUpToAmountAbsorbedAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "MODIFIER";
    private static String[][] translationArray = {
        { "ALIAS", null, "aliasSpecial" },
        //{ "ADDER(XMLID=ODDPOWER).TRUE", "OddPower" } // Not supported by HCS
    };
    
    /** Returns the XMLID for this Power.
     *
     * Subclass should either override this to return their XMLID or override
     * the identify method to do more complicated identification tasks.
     */
    public String getXMLID() {
        return XMLID;
    }
    
    public boolean identifyXML(String XMLID, Node node) {
        if ( super.identifyXML(XMLID, node) ) {
            Node aliasNode = node.getAttributes().getNamedItem("ALIAS");
            if ( aliasNode.getNodeValue().startsWith("Only Up To Amount Rolled By") ) {
                return true;
            }
        }
        return false;
        
    }
    
    /** Returns the Translation Array for the PowerAdapter.
     *
     * The Subclass should either override this to return their translationArray
     *or override the importXML method to do more complicated import tasks.
     */
    public String[][] getTranslationArray() {
        return translationArray;
    }
    
    /** CustomHandler for processing parameter.
     *
     */
    public void aliasSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        
        if ( ChampionsMatcher.matches("Only Up To Amount Rolled By\\s*(.*)\\s*", attrValue) ){
            ability.add("HDImport.ABSORPTIONNAME", ChampionsMatcher.getMatchedGroup(1), true);
        }
        
    }
    
     /** Finalizes the Import of a adder from an XML source.
     *
     * This method should be called after the Ability has been added to it's
     * source (if one exists).  If called with a null source, then the 
     * ability (and the power and adders) can assume that it will not be
     * attached to a specific source.
     */
    public XMLParseError finalize(Ability ability, Object modifier, int index) {
        XMLParseError parseError = null;
        PAD pad = (PAD)modifier;
        String name = ability.getStringValue("HDImport.ABSORPTIONNAME");
        
        Ability abs = ability.getSource().getAbilityList().getAbility(name, true);
        
        if ( abs != null ) {
            ParameterList pl = pad.getParameterList(ability, index);
            pl.setParameterValue("AbsorptionAbility", abs);
            pad.configurePAD(ability, pl);
        }
        else {
            parseError = new HDImportError("Unable to locate ability \"" + name + "\" for Only Up To Amount Absorbed target ability", HDImportError.IMPORT_ERROR);
        }
        
        ability.remove("HDImport.ABSORPTIONNAME");
        
        return parseError;
        
    }
}
