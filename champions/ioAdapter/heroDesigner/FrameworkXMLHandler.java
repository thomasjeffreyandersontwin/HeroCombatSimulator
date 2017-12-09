/*
 * ListXMLAdapter.java
 *
 * Created on April 10, 2004, 10:09 AM
 */

package champions.ioAdapter.heroDesigner;

import champions.*;
import champions.interfaces.AbilityList;
import champions.ioAdapter.heroDesigner.adapters.ElementalControlAdapter;
import champions.ioAdapter.heroDesigner.adapters.MultipowerAdapter;
import champions.ioAdapter.heroDesigner.adapters.VariablePointPoolAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import xml.DefaultXMLHandler;
import xml.XMLParseErrorList;
import xml.XMLParser;
import xml.XMLParserException;




/**
 *
 * @author  1425
 */
public class FrameworkXMLHandler extends DefaultXMLHandler {
    
    /** Creates a new instance of ListXMLAdapter */
    public FrameworkXMLHandler() {
    }
    
        /** Creates a new instance of ListXMLAdapter */
    public FrameworkXMLHandler(XMLParser parser) {
        super(parser);
    }
    
    public Object parseNode(Document doc, Node node, Object userData, XMLParseErrorList xmlParseErrorList) {
        if ( userData instanceof HDInfo == false ) {
            throw new XMLParserException("userData must be HDInfo object");
        }
        
        HDInfo hdInfo = (HDInfo)userData;
        Target target = hdInfo.target;
        
        String name = findAttribute(node, "NAME");
        if ( name == null || name.equals("") ) {
            name = findAttribute(node, "ALIAS");
        }
        String id = findAttribute(node, "ID");
        
        hdInfo.listToIDMap.put( id, name );
        
        AbilityList sublist = target.getAbilityList().findSublist(hdInfo.baseSublist);
        
        if ( sublist == null ) {
            // Probably will need to create the list eventually, but for now,
            // lets just put it at the root of the abilities.
            sublist = target.getAbilityList();
        }
        
        AbilityList newSublist = new DefaultAbilityList(name);
        sublist.addSublist( newSublist );
        
        // Now Create the relevant Framework Ability...
        String type = node.getNodeName();
        if ( type.equals("ELEMENTAL_CONTROL")){
            ElementalControlAbility ability = new ElementalControlAbility();
            ability.setName(name);
            XMLParseErrorList abilityErrorList = new XMLParseErrorList( name + " import errors");
            AbilityXMLHandler.importAbilityParameters(node, ability, new ElementalControlAdapter(), abilityErrorList);
            if ( abilityErrorList.getXMLParseErrorCount() > 0 ) {
                xmlParseErrorList.addXMLParseError(abilityErrorList);
            }
            newSublist.addAbility(ability);
            newSublist.setFramework( ability.getFramework() );
            ability.reconfigure();
        }
        else if ( type.equals("MULTIPOWER")){
            MultipowerAbility ability = new MultipowerAbility();
            ability.setName(name);
            XMLParseErrorList abilityErrorList = new XMLParseErrorList( name + " import errors");
            AbilityXMLHandler.importAbilityParameters(node, ability, new MultipowerAdapter(), abilityErrorList);
            if ( abilityErrorList.getXMLParseErrorCount() > 0 ) {
                xmlParseErrorList.addXMLParseError(abilityErrorList);
            }
            newSublist.addAbility(ability);
            newSublist.setFramework( ability.getFramework() );
            ability.reconfigure();
        }
        else if ( type.equals("VPP")){
            VariablePointPoolAbility ability = new VariablePointPoolAbility();
            ability.setName(name);
            
            XMLParseErrorList abilityErrorList = new XMLParseErrorList( name + " import errors");
            AbilityXMLHandler.importAbilityParameters(node, ability, new VariablePointPoolAdapter(), abilityErrorList);
            if ( abilityErrorList.getXMLParseErrorCount() > 0 ) {
                xmlParseErrorList.addXMLParseError(abilityErrorList);
            }
            newSublist.addAbility(ability);
            newSublist.setFramework( ability.getFramework() );
            ability.reconfigure();
        }
        else {
            // Other not handled yet...
            System.out.println("Framework Not Imported: Name=\"" + name + "\"");
        }
        
        return null;
    }
    
    
}
