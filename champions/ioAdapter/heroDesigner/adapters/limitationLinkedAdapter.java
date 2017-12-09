/*
 * limitationLinked.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
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
public class limitationLinkedAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "LINKED";
    private static String[][] translationArray = {
        { "OPTION_ALIAS", null, "abilitySpecial" },
        { "ADDER(XMLID=ANYPHASE)", "AnyPhase", "trueSpecial"},
        { "ADDER(XMLID=NONPROPORTIONAL)", "NonProportional", "trueSpecial" },
        { "ADDER(XMLID=ONLYWHENGREATERATFULL)", "OnlyWhenGreaterAtFull", "trueSpecial" },      
        { "ADDER(XMLID=POWERRARELYOFF)", "ConstantOrUsedMost", "trueSpecial" }        
        
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
    
    /** CustomHandler for processing parameter.
     *
     */
    public void abilitySpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        ability.add("HDImport.LINKEDABILITYNAME", attrValue, true);
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
        String name = ability.getStringValue("HDImport.LINKEDABILITYNAME");
        
        Ability abs = ability.getSource().getAbilityList().getAbility(name, true);
        
        if ( abs != null ) {
            ParameterList pl = pad.getParameterList(ability, index);
            pl.addIndexedParameterValue("LinkedToAbility", abs);
            pad.configurePAD(ability, pl); 
        }
        else {
            parseError = new HDImportError("Unable to locate ability \"" + name + "\" during Linked limitation import", HDImportError.IMPORT_ERROR);
        }
        
        ability.remove("HDImport.LINKEDABILITYNAME");
        
        return parseError;
    }
}
