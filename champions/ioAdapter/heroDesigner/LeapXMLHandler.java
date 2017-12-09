/*
 * AbilityXMLHandler.java
 *
 * Created on March 28, 2004, 11:09 PM
 */

package champions.ioAdapter.heroDesigner;

import champions.*;
import champions.interfaces.*;
import champions.ioAdapter.heroDesigner.adapters.powerLeapingAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import xml.XMLHandler;
import xml.XMLParseErrorList;
import xml.XMLParser;
import xml.XMLParserException;



/** Extracts Ability objects from an HeroDesigner XML Ability node.
 *
 * This XML handler knows how to extract an ability from a HeroDesigner
 * XML Element node.  It uses follows the standard XML ability import
 * procedure, as defined in the {@link Ability} class.<P>
 *
 * @author  1425
 */
public class LeapXMLHandler extends AbilityXMLHandler implements XMLHandler {
    
    /** Creates a new instance of AbilityXMLHandler */
    public LeapXMLHandler() {
    }
    
    public LeapXMLHandler(XMLParser parser) {
        super(parser);
    }
    
    public Object parseNode(Document doc, Node node, Object userData, XMLParseErrorList xmlParseErrorList) throws XMLParserException {
        if ( userData instanceof HDInfo == false ) {
            throw new XMLParserException("userData must be HDInfo object");
        }
        
        HDInfo hdInfo = (HDInfo)userData;
        
        Target target = hdInfo.target;
        
        AbilityList sublist = target.getAbilityList().findSublist(hdInfo.baseSublist);
        
        if ( sublist == null ) {
            // Probably will need to create the list eventually, but for now,
            // lets just put it at the root of the abilities.
            sublist = target.getAbilityList();
        }
        
        // Create an ability and configure it according to the
        // attributes and adders of the ability.
        //NamedNodeMap attrs = node.getAttributes();
        String powerID = findAttribute(node, "XMLID");
        
        
        if ( powerID == null || ! powerID.equals("LEAPING")) {
            throw new XMLParserException("Attempt to import XMLID != LEAPING by LeapingXMLHandler");
        }
        
        PowerXMLAdapter pxa;
        if ( "CHARACTERISTICS".equals(node.getParentNode().getNodeName() )){
            pxa = new powerLeapingAdapter(true);
        }
        else {
            pxa = new powerLeapingAdapter(false);
        }
        Ability ability;
        String abilityName = findAttribute(node, "NAME");
        if ( abilityName == null || abilityName.equals("")) {
            abilityName = findAttribute(node, "ALIAS");
        }
        
        ability = target.getAbilityList().getAbility(abilityName, true);
        
        if ( ability == null ) {
            ability = PADRoster.getNewAbilityInstance("Leaping");
        }
        
        if ( ability == null ) {
           // return "Ability Not Imported: NAME=\"" + powerName + "\", XMLID=\"" + powerID + "\""; 
           System.out.println("Ability Not Imported: NAME=\"" + abilityName + "\", XMLID=\"" + powerID + "\"");
           return null;
        }
        
        ability.setName(abilityName);
        importAbilityParameters(node, ability, pxa, xmlParseErrorList);
        
        sublist.addAbility( ability );
        
        return null;
    }
    
 
    

    
 
}
